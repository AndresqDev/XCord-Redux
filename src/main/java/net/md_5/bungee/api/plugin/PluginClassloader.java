package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.md_5.bungee.api.ProxyServer;

final class PluginClassloader extends URLClassLoader {
   private static final Set<PluginClassloader> allLoaders = new CopyOnWriteArraySet<>();
   private final ProxyServer proxy;
   private final PluginDescription desc;
   private final JarFile jar;
   private final Manifest manifest;
   private final URL url;
   private final ClassLoader libraryLoader;
   private Plugin plugin;

   public PluginClassloader(ProxyServer var1, PluginDescription var2, File var3, ClassLoader var4) throws IOException {
      super(new URL[]{var3.toURI().toURL()});
      this.proxy = var1;
      this.desc = var2;
      this.jar = new JarFile(var3);
      this.manifest = this.jar.getManifest();
      this.url = var3.toURI().toURL();
      this.libraryLoader = var4;
      allLoaders.add(this);
   }

   @Override
   protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      return this.loadClass0(var1, var2, true, true);
   }

   private Class<?> loadClass0(String var1, boolean var2, boolean var3, boolean var4) throws ClassNotFoundException {
      try {
         Class var5 = super.loadClass(var1, var2);
         if (var3 || var5.getClassLoader() == this) {
            return var5;
         }
      } catch (ClassNotFoundException var10) {
      }

      if (var4 && this.libraryLoader != null) {
         try {
            return this.libraryLoader.loadClass(var1);
         } catch (ClassNotFoundException var9) {
         }
      }

      if (var3) {
         for (PluginClassloader var6 : allLoaders) {
            if (var6 != this) {
               try {
                  return var6.loadClass0(var1, var2, false, this.proxy.getPluginManager().isTransitiveDepend(this.desc, var6.desc));
               } catch (ClassNotFoundException var8) {
               }
            }
         }
      }

      throw new ClassNotFoundException(var1);
   }

   @Override
   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      String var2 = var1.replace('.', '/').concat(".class");
      JarEntry var3 = this.jar.getJarEntry(var2);
      if (var3 != null) {
         byte[] var4;
         try {
            InputStream var5 = this.jar.getInputStream(var3);

            try {
               var4 = ByteStreams.toByteArray(var5);
            } catch (Throwable var9) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (IOException var10) {
            throw new ClassNotFoundException(var1, var10);
         }

         int var12 = var1.lastIndexOf(46);
         if (var12 != -1) {
            String var6 = var1.substring(0, var12);
            if (this.getPackage(var6) == null) {
               try {
                  if (this.manifest != null) {
                     this.definePackage(var6, this.manifest, this.url);
                  } else {
                     this.definePackage(var6, null, null, null, null, null, null, null);
                  }
               } catch (IllegalArgumentException var11) {
                  if (this.getPackage(var6) == null) {
                     throw new IllegalStateException("Cannot find package " + var6);
                  }
               }
            }
         }

         CodeSigner[] var13 = var3.getCodeSigners();
         CodeSource var7 = new CodeSource(this.url, var13);
         return this.defineClass(var1, var4, 0, var4.length, var7);
      } else {
         return super.findClass(var1);
      }
   }

   @Override
   public void close() throws IOException {
      try {
         super.close();
      } finally {
         this.jar.close();
      }
   }

   void init(Plugin var1) {
      Preconditions.checkArgument(var1 != null, "plugin");
      Preconditions.checkArgument(var1.getClass().getClassLoader() == this, "Plugin has incorrect ClassLoader");
      if (this.plugin != null) {
         throw new IllegalArgumentException("Plugin already initialized!");
      } else {
         this.plugin = var1;
         var1.init(this.proxy, this.desc);
      }
   }

   @Override
   public String toString() {
      return "PluginClassloader(desc=" + this.desc + ")";
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
