package org.fusesource.hawtjni.runtime;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Library {
   public static final String STRATEGY_PROPERTY = "hawtjni.strategy";
   public static final String STRATEGY_SHA1 = "sha1";
   public static final String STRATEGY_TEMP = "temp";
   static final String SLASH = System.getProperty("file.separator");
   static final String STRATEGY = System.getProperty("hawtjni.strategy", "windows".equals(getOperatingSystem()) ? "sha1" : "temp");
   private final String name;
   private final String version;
   private final ClassLoader classLoader;
   private boolean loaded;
   private String nativeLibraryPath;
   private URL nativeLibrarySourceUrl;

   public Library(String var1) {
      this(var1, null, null);
   }

   public Library(String var1, Class<?> var2) {
      this(var1, version(var2), var2.getClassLoader());
   }

   public Library(String var1, String var2) {
      this(var1, var2, null);
   }

   public Library(String var1, String var2, ClassLoader var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("name cannot be null");
      } else {
         this.name = var1;
         this.version = var2;
         this.classLoader = var3;
      }
   }

   private static String version(Class<?> var0) {
      try {
         return var0.getPackage().getImplementationVersion();
      } catch (Throwable var2) {
         return null;
      }
   }

   public String getNativeLibraryPath() {
      return this.nativeLibraryPath;
   }

   public URL getNativeLibrarySourceUrl() {
      return this.nativeLibrarySourceUrl;
   }

   public static String getOperatingSystem() {
      String var0 = System.getProperty("os.name").toLowerCase().trim();
      if (var0.startsWith("linux")) {
         return "linux";
      } else if (var0.startsWith("mac os x")) {
         return "osx";
      } else {
         return var0.startsWith("win") ? "windows" : var0.replaceAll("\\W+", "_");
      }
   }

   public static String getPlatform() {
      return getOperatingSystem() + getBitModel();
   }

   public static int getBitModel() {
      String var0 = System.getProperty("sun.arch.data.model");
      if (var0 == null) {
         var0 = System.getProperty("com.ibm.vm.bitmode");
      }

      return var0 != null ? Integer.parseInt(var0) : -1;
   }

   public synchronized void load() {
      if (!this.loaded) {
         this.doLoad();
         this.loaded = true;
      }
   }

   private void doLoad() {
      String var1 = System.getProperty("library." + this.name + ".version");
      if (var1 == null) {
         var1 = this.version;
      }

      ArrayList var2 = new ArrayList();
      String[] var3 = this.getSpecificSearchDirs();
      String var4 = this.map(this.name);
      String var5 = var1 == null ? null : this.map(this.name + "-" + var1);
      String var6 = System.getProperty("library." + this.name + ".path");
      if (var6 != null) {
         for (String var10 : var3) {
            if (var1 != null && this.load(var2, this.file(var6, var10, var5))) {
               return;
            }

            if (this.load(var2, this.file(var6, var10, var4))) {
               return;
            }
         }
      }

      if (var1 == null || !this.loadLibrary(var2, this.name + getBitModel() + "-" + var1)) {
         if (var1 == null || !this.loadLibrary(var2, this.name + "-" + var1)) {
            if (!this.loadLibrary(var2, this.name)) {
               if (this.classLoader != null) {
                  String var13 = var1 != null ? var5 : var4;

                  for (String var11 : var3) {
                     if (var1 != null && this.extractAndLoad(var2, var6, var11, var5, var13)) {
                        return;
                     }

                     if (this.extractAndLoad(var2, var6, var11, var4, var13)) {
                        return;
                     }
                  }
               }

               UnsatisfiedLinkError var14 = new UnsatisfiedLinkError("Could not load library. Reasons: " + var2.toString());

               try {
                  Method var16 = Throwable.class.getMethod("addSuppressed", Throwable.class);

                  for (Throwable var20 : var2) {
                     var16.invoke(var14, var20);
                  }
               } catch (Throwable var12) {
               }

               throw var14;
            }
         }
      }
   }

   @Deprecated
   public final String getArchSpecifcResourcePath() {
      return this.getArchSpecificResourcePath();
   }

   public final String getArchSpecificResourcePath() {
      return "META-INF/native/" + getPlatform() + "/" + System.getProperty("os.arch") + "/" + this.map(this.name);
   }

   @Deprecated
   public final String getOperatingSystemSpecifcResourcePath() {
      return this.getOperatingSystemSpecificResourcePath();
   }

   public final String getOperatingSystemSpecificResourcePath() {
      return this.getPlatformSpecificResourcePath(getOperatingSystem());
   }

   @Deprecated
   public final String getPlatformSpecifcResourcePath() {
      return this.getPlatformSpecificResourcePath();
   }

   public final String getPlatformSpecificResourcePath() {
      return this.getPlatformSpecificResourcePath(getPlatform());
   }

   @Deprecated
   public final String getPlatformSpecifcResourcePath(String var1) {
      return this.getPlatformSpecificResourcePath(var1);
   }

   public final String getPlatformSpecificResourcePath(String var1) {
      return "META-INF/native/" + var1 + "/" + this.map(this.name);
   }

   @Deprecated
   public final String getResorucePath() {
      return this.getResourcePath();
   }

   public final String getResourcePath() {
      return "META-INF/native/" + this.map(this.name);
   }

   public final String getLibraryFileName() {
      return this.map(this.name);
   }

   public final String[] getSpecificSearchDirs() {
      return new String[]{getPlatform() + "/" + System.getProperty("os.arch"), getPlatform(), getOperatingSystem(), "."};
   }

   private boolean extractAndLoad(ArrayList<Throwable> var1, String var2, String var3, String var4, String var5) {
      String var6 = "META-INF/native/" + (var3 == null ? "" : var3 + '/') + var4;
      URL var7 = this.classLoader.getResource(var6);
      if (var7 != null) {
         int var8 = var5.lastIndexOf(46);
         String var9 = var5.substring(0, var8) + "-";
         String var10 = var5.substring(var8);

         for (File var12 : Arrays.asList(
            var2 != null ? this.file(var2) : null,
            this.file(System.getProperty("java.io.tmpdir")),
            this.file(System.getProperty("user.home"), ".hawtjni", this.name)
         )) {
            if (var12 != null) {
               File var13;
               if ("sha1".equals(STRATEGY)) {
                  var13 = this.extractSha1(var1, var7, var9, var10, var12);
               } else {
                  var13 = this.extractTemp(var1, var7, var9, var10, var12);
               }

               if (var13 != null && this.load(var1, var13)) {
                  this.nativeLibrarySourceUrl = var7;
                  return true;
               }
            }
         }
      }

      return false;
   }

   private File file(String... var1) {
      File var2 = null;

      for (String var6 : var1) {
         if (var2 == null) {
            var2 = new File(var6);
         } else if (var6 != null) {
            var2 = new File(var2, var6);
         }
      }

      return var2;
   }

   private String map(String var1) {
      var1 = System.mapLibraryName(var1);
      String var2 = ".dylib";
      if (var1.endsWith(var2)) {
         var1 = var1.substring(0, var1.length() - var2.length()) + ".jnilib";
      }

      return var1;
   }

   private File extractSha1(ArrayList<Throwable> var1, URL var2, String var3, String var4, File var5) {
      File var6 = null;
      var5 = var5.getAbsoluteFile();
      if (!var5.exists() && !var5.mkdirs()) {
         var1.add(new IOException("Unable to create directory: " + var5));
         return null;
      } else {
         try {
            String var7 = this.computeSha1(var2.openStream());
            String var19 = "";
            var6 = new File(var5, var3 + var7 + var4);
            if (var6.isFile() && var6.canRead()) {
               var19 = this.computeSha1(new FileInputStream(var6));
            }

            if (var19.equals(var7)) {
               return var6;
            } else {
               FileOutputStream var9 = null;
               InputStream var10 = null;

               File var20;
               try {
                  var10 = var2.openStream();
                  if (var10 != null) {
                     byte[] var11 = new byte[4096];
                     var9 = new FileOutputStream(var6);

                     int var12;
                     while ((var12 = var10.read(var11)) != -1) {
                        var9.write(var11, 0, var12);
                     }

                     this.chmod755(var6);
                  }

                  var20 = var6;
               } finally {
                  close(var9);
                  close(var10);
               }

               return var20;
            }
         } catch (Throwable var17) {
            IOException var8;
            if (var6 != null) {
               var6.delete();
               var8 = new IOException("Unable to extract library from " + var2 + " to " + var6);
            } else {
               var8 = new IOException("Unable to create temporary file in " + var5);
            }

            var8.initCause(var17);
            var1.add(var8);
            return null;
         }
      }
   }

   private String computeSha1(InputStream var1) throws NoSuchAlgorithmException, IOException {
      String var2;
      try {
         MessageDigest var3 = MessageDigest.getInstance("SHA1");
         byte[] var5 = new byte[4096];

         int var4;
         while ((var4 = var1.read(var5)) != -1) {
            var3.update(var5, 0, var4);
         }

         byte[] var6 = var3.digest();
         StringBuilder var7 = new StringBuilder();

         for (byte var11 : var6) {
            var7.append(Integer.toString((var11 & 255) + 256, 16).substring(1));
         }

         var2 = var7.toString();
      } finally {
         close(var1);
      }

      return var2;
   }

   private File extractTemp(ArrayList<Throwable> var1, URL var2, String var3, String var4, File var5) {
      File var6 = null;
      var5 = var5.getAbsoluteFile();
      if (!var5.exists() && !var5.mkdirs()) {
         var1.add(new IOException("Unable to create directory: " + var5));
         return null;
      } else {
         try {
            FileOutputStream var7 = null;
            InputStream var17 = null;

            File var18;
            try {
               var6 = File.createTempFile(var3, var4, var5);
               var17 = var2.openStream();
               if (var17 != null) {
                  byte[] var9 = new byte[4096];
                  var7 = new FileOutputStream(var6);

                  int var10;
                  while ((var10 = var17.read(var9)) != -1) {
                     var7.write(var9, 0, var10);
                  }

                  this.chmod755(var6);
               }

               var6.deleteOnExit();
               var18 = var6;
            } finally {
               close(var7);
               close(var17);
            }

            return var18;
         } catch (Throwable var15) {
            IOException var8;
            if (var6 != null) {
               var6.delete();
               var8 = new IOException("Unable to extract library from " + var2 + " to " + var6);
            } else {
               var8 = new IOException("Unable to create temporary file in " + var5);
            }

            var8.initCause(var15);
            var1.add(var8);
            return null;
         }
      }
   }

   private static void close(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (Exception var2) {
         }
      }
   }

   private void chmod755(File var1) {
      if (!getPlatform().startsWith("windows")) {
         try {
            ClassLoader var2 = this.getClass().getClassLoader();
            Class var3 = var2.loadClass("java.nio.file.attribute.PosixFilePermissions");
            Method var4 = var3.getMethod("fromString", String.class);
            Object var5 = var4.invoke(null, "rwxr-xr-x");
            Object var6 = var1.getClass().getMethod("toPath").invoke(var1);
            Class var7 = var2.loadClass("java.nio.file.Path");
            Class var8 = var2.loadClass("java.nio.file.Files");
            Method var9 = var8.getMethod("setPosixFilePermissions", var7, Set.class);
            var9.invoke(null, var6, var5);
         } catch (Throwable var11) {
            try {
               Runtime.getRuntime().exec(new String[]{"chmod", "755", var1.getCanonicalPath()}).waitFor();
            } catch (Throwable var10) {
            }
         }
      }
   }

   private boolean load(ArrayList<Throwable> var1, File var2) {
      try {
         System.load(var2.getPath());
         this.nativeLibraryPath = var2.getPath();
         return true;
      } catch (UnsatisfiedLinkError var5) {
         LinkageError var4 = new LinkageError("Unable to load library from " + var2);
         var4.initCause(var5);
         var1.add(var4);
         return false;
      }
   }

   private boolean loadLibrary(ArrayList<Throwable> var1, String var2) {
      try {
         System.loadLibrary(var2);
         this.nativeLibraryPath = "java.library.path,sun.boot.library.pathlib:" + var2;
         return true;
      } catch (UnsatisfiedLinkError var5) {
         LinkageError var4 = new LinkageError("Unable to load library " + var2);
         var4.initCause(var5);
         var1.add(var4);
         return false;
      }
   }
}
