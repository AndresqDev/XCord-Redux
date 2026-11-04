package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@Beta
public final class ClassPath {
   private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
   private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
   private static final String CLASS_FILE_NAME_EXTENSION = ".class";
   private final ImmutableSet<ClassPath.ResourceInfo> resources;

   private ClassPath(ImmutableSet<ClassPath.ResourceInfo> var1) {
      this.resources = var1;
   }

   public static ClassPath from(ClassLoader var0) throws IOException {
      ImmutableSet var1 = locationsFrom(var0);
      HashSet var2 = new HashSet();

      for (ClassPath.LocationInfo var4 : var1) {
         var2.add(var4.file());
      }

      ImmutableSet.Builder var6 = ImmutableSet.builder();

      for (ClassPath.LocationInfo var5 : var1) {
         var6.addAll(var5.scanResources(var2));
      }

      return new ClassPath(var6.build());
   }

   public ImmutableSet<ClassPath.ResourceInfo> getResources() {
      return this.resources;
   }

   public ImmutableSet<ClassPath.ClassInfo> getAllClasses() {
      return FluentIterable.from(this.resources).filter(ClassPath.ClassInfo.class).toSet();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses() {
      return FluentIterable.from(this.resources).filter(ClassPath.ClassInfo.class).filter(new Predicate<ClassPath.ClassInfo>(this) {
         public boolean apply(ClassPath.ClassInfo var1) {
            return var1.isTopLevel();
         }
      }).toSet();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClasses(String var1) {
      Preconditions.checkNotNull(var1);
      ImmutableSet.Builder var2 = ImmutableSet.builder();

      for (ClassPath.ClassInfo var4 : this.getTopLevelClasses()) {
         if (var4.getPackageName().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2.build();
   }

   public ImmutableSet<ClassPath.ClassInfo> getTopLevelClassesRecursive(String var1) {
      Preconditions.checkNotNull(var1);
      String var2 = new StringBuilder(1 + String.valueOf(var1).length()).append(var1).append('.').toString();
      ImmutableSet.Builder var3 = ImmutableSet.builder();

      for (ClassPath.ClassInfo var5 : this.getTopLevelClasses()) {
         if (var5.getName().startsWith(var2)) {
            var3.add(var5);
         }
      }

      return var3.build();
   }

   static ImmutableSet<ClassPath.LocationInfo> locationsFrom(ClassLoader var0) {
      ImmutableSet.Builder var1 = ImmutableSet.builder();

      for (Entry var3 : getClassPathEntries(var0).entrySet()) {
         var1.add(new ClassPath.LocationInfo((File)var3.getKey(), (ClassLoader)var3.getValue()));
      }

      return var1.build();
   }

   @VisibleForTesting
   static ImmutableSet<File> getClassPathFromManifest(File var0, Manifest var1) {
      if (var1 == null) {
         return ImmutableSet.of();
      } else {
         ImmutableSet.Builder var2 = ImmutableSet.builder();
         String var3 = var1.getMainAttributes().getValue(Name.CLASS_PATH.toString());
         if (var3 != null) {
            Iterator var4 = CLASS_PATH_ATTRIBUTE_SEPARATOR.split(var3).iterator();

            while (true) {
               URL var6;
               while (true) {
                  if (!var4.hasNext()) {
                     return var2.build();
                  }

                  String var5 = (String)var4.next();

                  try {
                     var6 = getClassPathEntry(var0, var5);
                     break;
                  } catch (MalformedURLException var8) {
                     Logger var10000 = logger;
                     String var10002 = String.valueOf(var5);
                     var10000.warning(var10002.length() != 0 ? "Invalid Class-Path entry: ".concat(var10002) : new String("Invalid Class-Path entry: "));
                  }
               }

               if (var6.getProtocol().equals("file")) {
                  var2.add(toFile(var6));
               }
            }
         } else {
            return var2.build();
         }
      }
   }

   @VisibleForTesting
   static ImmutableMap<File, ClassLoader> getClassPathEntries(ClassLoader var0) {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      ClassLoader var2 = var0.getParent();
      if (var2 != null) {
         var1.putAll(getClassPathEntries(var2));
      }

      for (URL var4 : getClassLoaderUrls(var0)) {
         if (var4.getProtocol().equals("file")) {
            File var5 = toFile(var4);
            if (!var1.containsKey(var5)) {
               var1.put(var5, var0);
            }
         }
      }

      return ImmutableMap.copyOf(var1);
   }

   private static ImmutableList<URL> getClassLoaderUrls(ClassLoader var0) {
      if (var0 instanceof URLClassLoader) {
         return ImmutableList.copyOf(((URLClassLoader)var0).getURLs());
      } else {
         return var0.equals(ClassLoader.getSystemClassLoader()) ? parseJavaClassPath() : ImmutableList.of();
      }
   }

   @VisibleForTesting
   static ImmutableList<URL> parseJavaClassPath() {
      ImmutableList.Builder var0 = ImmutableList.builder();

      for (String var2 : Splitter.on(StandardSystemProperty.PATH_SEPARATOR.value()).split(StandardSystemProperty.JAVA_CLASS_PATH.value())) {
         try {
            try {
               var0.add(new File(var2).toURI().toURL());
            } catch (SecurityException var4) {
               var0.add(new URL("file", null, new File(var2).getAbsolutePath()));
            }
         } catch (MalformedURLException var5) {
            Logger var10000 = logger;
            Level var10001 = Level.WARNING;
            String var10003 = String.valueOf(var2);
            var10000.log(
               var10001, var10003.length() != 0 ? "malformed classpath entry: ".concat(var10003) : new String("malformed classpath entry: "), (Throwable)var5
            );
         }
      }

      return var0.build();
   }

   @VisibleForTesting
   static URL getClassPathEntry(File var0, String var1) throws MalformedURLException {
      return new URL(var0.toURI().toURL(), var1);
   }

   @VisibleForTesting
   static String getClassName(String var0) {
      int var1 = var0.length() - ".class".length();
      return var0.substring(0, var1).replace('/', '.');
   }

   @VisibleForTesting
   static File toFile(URL var0) {
      Preconditions.checkArgument(var0.getProtocol().equals("file"));

      try {
         return new File(var0.toURI());
      } catch (URISyntaxException var2) {
         return new File(var0.getPath());
      }
   }

   @Beta
   public static final class ClassInfo extends ClassPath.ResourceInfo {
      private final String className;

      ClassInfo(File var1, String var2, ClassLoader var3) {
         super(var1, var2, var3);
         this.className = ClassPath.getClassName(var2);
      }

      public String getPackageName() {
         return Reflection.getPackageName(this.className);
      }

      public String getSimpleName() {
         int var1 = this.className.lastIndexOf(36);
         if (var1 != -1) {
            String var3 = this.className.substring(var1 + 1);
            return CharMatcher.inRange('0', '9').trimLeadingFrom(var3);
         } else {
            String var2 = this.getPackageName();
            return var2.isEmpty() ? this.className : this.className.substring(var2.length() + 1);
         }
      }

      public String getName() {
         return this.className;
      }

      public boolean isTopLevel() {
         return this.className.indexOf(36) == -1;
      }

      public Class<?> load() {
         try {
            return this.loader.loadClass(this.className);
         } catch (ClassNotFoundException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public String toString() {
         return this.className;
      }
   }

   static final class LocationInfo {
      final File home;
      private final ClassLoader classloader;

      LocationInfo(File var1, ClassLoader var2) {
         this.home = Preconditions.checkNotNull(var1);
         this.classloader = Preconditions.checkNotNull(var2);
      }

      public final File file() {
         return this.home;
      }

      public ImmutableSet<ClassPath.ResourceInfo> scanResources() throws IOException {
         return this.scanResources(new HashSet<>());
      }

      public ImmutableSet<ClassPath.ResourceInfo> scanResources(Set<File> var1) throws IOException {
         ImmutableSet.Builder var2 = ImmutableSet.builder();
         var1.add(this.home);
         this.scan(this.home, var1, var2);
         return var2.build();
      }

      private void scan(File var1, Set<File> var2, ImmutableSet.Builder<ClassPath.ResourceInfo> var3) throws IOException {
         try {
            if (!var1.exists()) {
               return;
            }
         } catch (SecurityException var7) {
            Logger var10000 = ClassPath.logger;
            String var5 = String.valueOf(var1);
            String var6 = String.valueOf(var7);
            var10000.warning(
               new StringBuilder(16 + String.valueOf(var5).length() + String.valueOf(var6).length())
                  .append("Cannot access ")
                  .append(var5)
                  .append(": ")
                  .append(var6)
                  .toString()
            );
            return;
         }

         if (var1.isDirectory()) {
            this.scanDirectory(var1, var3);
         } else {
            this.scanJar(var1, var2, var3);
         }
      }

      private void scanJar(File var1, Set<File> var2, ImmutableSet.Builder<ClassPath.ResourceInfo> var3) throws IOException {
         JarFile var4;
         try {
            var4 = new JarFile(var1);
         } catch (IOException var14) {
            return;
         }

         try {
            for (File var6 : ClassPath.getClassPathFromManifest(var1, var4.getManifest())) {
               if (var2.add(var6.getCanonicalFile())) {
                  this.scan(var6, var2, var3);
               }
            }

            this.scanJarFile(var4, var3);
         } finally {
            try {
               var4.close();
            } catch (IOException var13) {
            }
         }
      }

      private void scanJarFile(JarFile var1, ImmutableSet.Builder<ClassPath.ResourceInfo> var2) {
         Enumeration var3 = var1.entries();

         while (var3.hasMoreElements()) {
            JarEntry var4 = (JarEntry)var3.nextElement();
            if (!var4.isDirectory() && !var4.getName().equals("META-INF/MANIFEST.MF")) {
               var2.add(ClassPath.ResourceInfo.of(new File(var1.getName()), var4.getName(), this.classloader));
            }
         }
      }

      private void scanDirectory(File var1, ImmutableSet.Builder<ClassPath.ResourceInfo> var2) throws IOException {
         HashSet var3 = new HashSet();
         var3.add(var1.getCanonicalFile());
         this.scanDirectory(var1, "", var3, var2);
      }

      private void scanDirectory(File var1, String var2, Set<File> var3, ImmutableSet.Builder<ClassPath.ResourceInfo> var4) throws IOException {
         File[] var5 = var1.listFiles();
         if (var5 == null) {
            Logger var15 = ClassPath.logger;
            String var12 = String.valueOf(var1);
            var15.warning(new StringBuilder(22 + String.valueOf(var12).length()).append("Cannot read directory ").append(var12).toString());
         } else {
            for (File var9 : var5) {
               String var10 = var9.getName();
               if (var9.isDirectory()) {
                  File var11 = var9.getCanonicalFile();
                  if (var3.add(var11)) {
                     this.scanDirectory(
                        var11,
                        new StringBuilder(1 + String.valueOf(var2).length() + String.valueOf(var10).length()).append(var2).append(var10).append("/").toString(),
                        var3,
                        var4
                     );
                     var3.remove(var11);
                  }
               } else {
                  String var10000 = String.valueOf(var2);
                  String var10001 = String.valueOf(var10);
                  String var13 = var10001.length() != 0 ? var10000.concat(var10001) : new String(var10000);
                  if (!var13.equals("META-INF/MANIFEST.MF")) {
                     var4.add(ClassPath.ResourceInfo.of(var9, var13, this.classloader));
                  }
               }
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof ClassPath.LocationInfo)) {
            return false;
         } else {
            ClassPath.LocationInfo var2 = (ClassPath.LocationInfo)var1;
            return this.home.equals(var2.home) && this.classloader.equals(var2.classloader);
         }
      }

      @Override
      public int hashCode() {
         return this.home.hashCode();
      }

      @Override
      public String toString() {
         return this.home.toString();
      }
   }

   @Beta
   public static class ResourceInfo {
      private final File file;
      private final String resourceName;
      final ClassLoader loader;

      static ClassPath.ResourceInfo of(File var0, String var1, ClassLoader var2) {
         return (ClassPath.ResourceInfo)(var1.endsWith(".class") ? new ClassPath.ClassInfo(var0, var1, var2) : new ClassPath.ResourceInfo(var0, var1, var2));
      }

      ResourceInfo(File var1, String var2, ClassLoader var3) {
         this.file = Preconditions.checkNotNull(var1);
         this.resourceName = Preconditions.checkNotNull(var2);
         this.loader = Preconditions.checkNotNull(var3);
      }

      public final URL url() {
         URL var1 = this.loader.getResource(this.resourceName);
         if (var1 == null) {
            throw new NoSuchElementException(this.resourceName);
         } else {
            return var1;
         }
      }

      public final ByteSource asByteSource() {
         return Resources.asByteSource(this.url());
      }

      public final CharSource asCharSource(Charset var1) {
         return Resources.asCharSource(this.url(), var1);
      }

      public final String getResourceName() {
         return this.resourceName;
      }

      final File getFile() {
         return this.file;
      }

      @Override
      public int hashCode() {
         return this.resourceName.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof ClassPath.ResourceInfo)) {
            return false;
         } else {
            ClassPath.ResourceInfo var2 = (ClassPath.ResourceInfo)var1;
            return this.resourceName.equals(var2.resourceName) && this.loader == var2.loader;
         }
      }

      @Override
      public String toString() {
         return this.resourceName;
      }
   }
}
