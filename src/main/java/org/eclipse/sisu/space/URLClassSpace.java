package org.eclipse.sisu.space;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.eclipse.sisu.inject.DeferredClass;

public class URLClassSpace implements ClassSpace {
   private static final String MANIFEST_ENTRY = "META-INF/MANIFEST.MF";
   private static final URL[] NO_URLS;
   private static final Enumeration<URL> NO_ENTRIES;
   private static final String[] EMPTY_CLASSPATH;
   private static final ClassLoader SYSTEM_LOADER;
   private static final String SYSTEM_CLASSPATH;
   private final ClassLoader loader;
   private final String pathDetails;
   private URL[] classPath;

   static {
      ClassLoader var0;
      String var1;
      try {
         var0 = ClassLoader.getSystemClassLoader();
         var1 = System.getProperty("java.class.path", ".");
      } catch (RuntimeException var2) {
         var0 = null;
         var1 = null;
      } catch (LinkageError var3) {
         var0 = null;
         var1 = null;
      }

      SYSTEM_LOADER = var0;
      SYSTEM_CLASSPATH = var1;
      NO_URLS = new URL[0];
      NO_ENTRIES = Collections.enumeration(Collections.emptySet());
      EMPTY_CLASSPATH = new String[0];
   }

   public URLClassSpace(ClassLoader var1) {
      this.loader = var1;
      this.pathDetails = null;
   }

   public URLClassSpace(ClassLoader var1, URL[] var2) {
      this.loader = var1;
      this.pathDetails = Arrays.toString((Object[])var2);
      if (var2 != null && var2.length > 0) {
         this.classPath = expandClassPath(var2);
      } else {
         this.classPath = NO_URLS;
      }
   }

   @Override
   public final Class<?> loadClass(String var1) {
      try {
         return this.loader.loadClass(var1);
      } catch (Exception var3) {
         throw new TypeNotPresentException(var1, var3);
      } catch (LinkageError var4) {
         throw new TypeNotPresentException(var1, var4);
      }
   }

   @Override
   public final DeferredClass<?> deferLoadClass(String var1) {
      return new NamedClass(this, var1);
   }

   @Override
   public final URL getResource(String var1) {
      return this.loader.getResource(var1);
   }

   @Override
   public final Enumeration<URL> getResources(String var1) {
      try {
         Enumeration var2 = this.loader.getResources(var1);
         return var2 != null ? var2 : NO_ENTRIES;
      } catch (IOException var3) {
         return NO_ENTRIES;
      }
   }

   @Override
   public final Enumeration<URL> findEntries(String var1, String var2, boolean var3) {
      return (Enumeration<URL>)(SYSTEM_LOADER != null
            && this.loader == SYSTEM_LOADER
            && this.pathDetails == null
            && !var3
            && var2 != null
            && var2.indexOf(42) < 0
         ? this.getResources(ResourceEnumeration.normalizeSearchPath(var1) + var2)
         : new ResourceEnumeration(var1, var2, var3, this.getClassPath()));
   }

   public final URL[] getURLs() {
      return (URL[])this.getClassPath().clone();
   }

   @Override
   public final int hashCode() {
      return this.loader.hashCode();
   }

   @Override
   public final boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof URLClassSpace ? this.loader.equals(((URLClassSpace)var1).loader) : false;
      }
   }

   @Override
   public final String toString() {
      return this.pathDetails == null ? this.loader.toString() : this.loader + "(" + this.pathDetails + ")";
   }

   private synchronized URL[] getClassPath() {
      if (this.classPath == null) {
         for (ClassLoader var1 = this.loader; var1 != null; var1 = var1.getParent()) {
            if (var1 instanceof URLClassLoader) {
               URL[] var2 = ((URLClassLoader)var1).getURLs();
               if (var2 != null && var2.length > 0) {
                  this.classPath = expandClassPath(var2);
                  break;
               }
            } else if (SYSTEM_LOADER != null && var1 == SYSTEM_LOADER) {
               this.classPath = expandClassPath(getSystemClassPath());
               break;
            }
         }

         if (this.classPath == null) {
            this.classPath = NO_URLS;
         }
      }

      return this.classPath;
   }

   private static URL[] getSystemClassPath() {
      String[] var0 = SYSTEM_CLASSPATH.split(File.pathSeparator);
      URL[] var1 = new URL[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         try {
            var1[var2] = new File(var0[var2]).toURI().toURL();
         } catch (MalformedURLException var3) {
            var1[var2] = null;
         }
      }

      return var1;
   }

   private static URL[] expandClassPath(URL[] var0) {
      ArrayList var1 = new ArrayList();
      Collections.addAll(var1, var0);
      ArrayList var2 = new ArrayList();
      HashSet var3 = new HashSet();

      for (int var4 = 0; var4 < var1.size(); var4++) {
         URL var5 = normalizeEntry((URL)var1.get(var4));
         if (var5 != null && var3.add(var5.toString())) {
            var2.add(var5);

            String[] var6;
            try {
               var6 = getClassPathEntries(var5);
            } catch (IOException var12) {
               continue;
            }

            for (String var7 : var6) {
               try {
                  var1.add(new URL(var5, var7));
               } catch (MalformedURLException var11) {
               }
            }
         }
      }

      return var2.toArray(new URL[var2.size()]);
   }

   private static URL normalizeEntry(URL var0) {
      if (var0 != null && "jar".equals(var0.getProtocol())) {
         String var1 = var0.getPath();
         if (var1.endsWith("!/")) {
            try {
               return new URL(var1.substring(0, var1.length() - 2));
            } catch (MalformedURLException var3) {
               throw new IllegalStateException(var3.toString());
            }
         }
      }

      return var0;
   }

   private static String[] getClassPathEntries(URL var0) throws IOException {
      Manifest var1;
      if (var0.getPath().endsWith("/")) {
         InputStream var2 = Streams.open(new URL(var0, "META-INF/MANIFEST.MF"));

         try {
            var1 = new Manifest(var2);
         } finally {
            var2.close();
         }
      } else if ("file".equals(var0.getProtocol())) {
         JarFile var16 = new JarFile(FileEntryIterator.toFile(var0));

         try {
            var1 = var16.getManifest();
         } finally {
            var16.close();
         }
      } else {
         JarInputStream var17 = new JarInputStream(Streams.open(var0));

         try {
            var1 = var17.getManifest();
         } finally {
            var17.close();
         }
      }

      if (var1 != null) {
         String var18 = var1.getMainAttributes().getValue("Class-Path");
         if (var18 != null) {
            return var18.split(" ");
         }
      }

      return EMPTY_CLASSPATH;
   }
}
