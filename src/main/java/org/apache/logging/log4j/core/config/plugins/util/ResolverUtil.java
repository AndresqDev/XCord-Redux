package org.apache.logging.log4j.core.config.plugins.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

public class ResolverUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String VFSZIP = "vfszip";
   private static final String VFS = "vfs";
   private static final String JAR = "jar";
   private static final String BUNDLE_RESOURCE = "bundleresource";
   private final Set<Class<?>> classMatches = new HashSet<>();
   private final Set<URI> resourceMatches = new HashSet<>();
   private ClassLoader classloader;

   public Set<Class<?>> getClasses() {
      return this.classMatches;
   }

   public Set<URI> getResources() {
      return this.resourceMatches;
   }

   public ClassLoader getClassLoader() {
      return this.classloader != null ? this.classloader : (this.classloader = Loader.getClassLoader(ResolverUtil.class, null));
   }

   public void setClassLoader(ClassLoader var1) {
      this.classloader = var1;
   }

   public void find(ResolverUtil.Test var1, String... var2) {
      if (var2 != null) {
         for (String var6 : var2) {
            this.findInPackage(var1, var6);
         }
      }
   }

   public void findInPackage(ResolverUtil.Test var1, String var2) {
      var2 = var2.replace('.', '/');
      ClassLoader var3 = this.getClassLoader();

      Enumeration var4;
      try {
         var4 = var3.getResources(var2);
      } catch (IOException var32) {
         LOGGER.warn("Could not read package: {}", var2, var32);
         return;
      }

      while (var4.hasMoreElements()) {
         try {
            URL var5 = (URL)var4.nextElement();
            String var6 = this.extractPath(var5);
            LOGGER.info("Scanning for classes in '{}' matching criteria {}", var6, var1);
            if ("vfszip".equals(var5.getProtocol())) {
               String var37 = var6.substring(0, var6.length() - var2.length() - 2);
               URL var38 = new URL(var5.getProtocol(), var5.getHost(), var37);
               JarInputStream var39 = new JarInputStream(var38.openStream());

               try {
                  this.loadImplementationsInJar(var1, var2, var37, var39);
               } finally {
                  this.close(var39, var38);
               }
            } else if (!"vfs".equals(var5.getProtocol())) {
               if ("bundleresource".equals(var5.getProtocol())) {
                  this.loadImplementationsInBundle(var1, var2);
               } else if ("jar".equals(var5.getProtocol())) {
                  this.loadImplementationsInJar(var1, var2, var5);
               } else {
                  File var36 = new File(var6);
                  if (var36.isDirectory()) {
                     this.loadImplementationsInDirectory(var1, var2, var36);
                  } else {
                     this.loadImplementationsInJar(var1, var2, var36);
                  }
               }
            } else {
               String var7 = var6.substring(1, var6.length() - var2.length() - 2);
               File var8 = new File(var7);
               if (var8.exists()) {
                  if (var8.isDirectory()) {
                     this.loadImplementationsInDirectory(var1, var2, new File(var8, var2));
                  } else {
                     this.loadImplementationsInJar(var1, var2, var8);
                  }
               } else {
                  String var9 = var6.substring(0, var6.length() - var2.length() - 2);
                  URL var10 = new URL(var5.getProtocol(), var5.getHost(), var9);

                  try (InputStream var11 = var10.openStream()) {
                     JarInputStream var13;
                     if (var11 instanceof JarInputStream) {
                        var13 = (JarInputStream)var11;
                     } else {
                        var13 = new JarInputStream(var11);
                     }

                     this.loadImplementationsInJar(var1, var2, var9, var13);
                  }
               }
            }
         } catch (URISyntaxException | IOException var34) {
            LOGGER.warn("Could not read entries", (Throwable)var34);
         }
      }
   }

   String extractPath(URL var1) throws UnsupportedEncodingException, URISyntaxException {
      String var2 = var1.getPath();
      if (var2.startsWith("jar:")) {
         var2 = var2.substring(4);
      }

      if (var2.startsWith("file:")) {
         var2 = var2.substring(5);
      }

      int var3 = var2.indexOf(33);
      if (var3 > 0) {
         var2 = var2.substring(0, var3);
      }

      String var4 = var1.getProtocol();
      List var5 = Arrays.asList("vfs", "vfszip", "bundleresource");
      if (var5.contains(var4)) {
         return var2;
      } else {
         String var6 = new URI(var2).getPath();
         return new File(var6).exists() ? var6 : URLDecoder.decode(var2, StandardCharsets.UTF_8.name());
      }
   }

   private void loadImplementationsInBundle(ResolverUtil.Test var1, String var2) {
      BundleWiring var3 = (BundleWiring)FrameworkUtil.getBundle(ResolverUtil.class).adapt(BundleWiring.class);

      for (String var6 : var3.listResources(var2, "*.class", 1)) {
         this.addIfMatching(var1, var6);
      }
   }

   private void loadImplementationsInDirectory(ResolverUtil.Test var1, String var2, File var3) {
      File[] var4 = var3.listFiles();
      if (var4 != null) {
         for (File var9 : var4) {
            StringBuilder var5 = new StringBuilder();
            var5.append(var2).append('/').append(var9.getName());
            String var10 = var2 == null ? var9.getName() : var5.toString();
            if (var9.isDirectory()) {
               this.loadImplementationsInDirectory(var1, var10, var9);
            } else if (this.isTestApplicable(var1, var9.getName())) {
               this.addIfMatching(var1, var10);
            }
         }
      }
   }

   private boolean isTestApplicable(ResolverUtil.Test var1, String var2) {
      return var1.doesMatchResource() || var2.endsWith(".class") && var1.doesMatchClass();
   }

   private void loadImplementationsInJar(ResolverUtil.Test var1, String var2, URL var3) {
      Object var4 = null;

      try {
         var4 = (JarURLConnection)var3.openConnection();
         if (var4 != null) {
            try (JarFile var5 = var4.getJarFile()) {
               Enumeration var7 = var5.entries();

               while (var7.hasMoreElements()) {
                  JarEntry var8 = (JarEntry)var7.nextElement();
                  String var9 = var8.getName();
                  if (!var8.isDirectory() && var9.startsWith(var2) && this.isTestApplicable(var1, var9)) {
                     this.addIfMatching(var1, var9);
                  }
               }
            }
         } else {
            LOGGER.error("Could not establish connection to {}", var3.toString());
         }
      } catch (IOException var20) {
         LOGGER.error("Could not search JAR file '{}' for classes matching criteria {}, file not found", var3.toString(), var1, var20);
      }
   }

   private void loadImplementationsInJar(ResolverUtil.Test var1, String var2, File var3) {
      JarInputStream var4 = null;

      try {
         var4 = new JarInputStream(new FileInputStream(var3));
         this.loadImplementationsInJar(var1, var2, var3.getPath(), var4);
      } catch (IOException var9) {
         LOGGER.error("Could not search JAR file '{}' for classes matching criteria {}, file not found", var3, var1, var9);
      } finally {
         this.close(var4, var3);
      }
   }

   private void close(JarInputStream var1, Object var2) {
      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var4) {
            LOGGER.error("Error closing JAR file stream for {}", var2, var4);
         }
      }
   }

   private void loadImplementationsInJar(ResolverUtil.Test var1, String var2, String var3, JarInputStream var4) {
      JarEntry var5;
      try {
         while ((var5 = var4.getNextJarEntry()) != null) {
            String var6 = var5.getName();
            if (!var5.isDirectory() && var6.startsWith(var2) && this.isTestApplicable(var1, var6)) {
               this.addIfMatching(var1, var6);
            }
         }
      } catch (IOException var7) {
         LOGGER.error("Could not search JAR file '{}' for classes matching criteria {} due to an IOException", var3, var1, var7);
      }
   }

   protected void addIfMatching(ResolverUtil.Test var1, String var2) {
      try {
         ClassLoader var3 = this.getClassLoader();
         if (var1.doesMatchClass()) {
            String var4 = var2.substring(0, var2.indexOf(46)).replace('/', '.');
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("Checking to see if class {} matches criteria {}", var4, var1);
            }

            Class var5 = var3.loadClass(var4);
            if (var1.matches(var5)) {
               this.classMatches.add(var5);
            }
         }

         if (var1.doesMatchResource()) {
            URL var7 = var3.getResource(var2);
            if (var7 == null) {
               var7 = var3.getResource(var2.substring(1));
            }

            if (var7 != null && var1.matches(var7.toURI())) {
               this.resourceMatches.add(var7.toURI());
            }
         }
      } catch (Throwable var6) {
         LOGGER.warn("Could not examine class {}", var2, var6);
      }
   }

   public interface Test {
      boolean matches(Class<?> var1);

      boolean matches(URI var1);

      boolean doesMatchClass();

      boolean doesMatchResource();
   }
}
