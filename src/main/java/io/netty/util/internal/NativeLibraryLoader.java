package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;

public final class NativeLibraryLoader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
   private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
   private static final File WORKDIR;
   private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
   private static final boolean TRY_TO_PATCH_SHADED_ID;
   private static final boolean DETECT_NATIVE_LIBRARY_DUPLICATES;
   private static final byte[] UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(CharsetUtil.US_ASCII);

   public static void loadFirstAvailable(ClassLoader var0, String... var1) {
      ArrayList var2 = new ArrayList();

      for (String var6 : var1) {
         try {
            load(var6, var0);
            logger.debug("Loaded library with name '{}'", var6);
            return;
         } catch (Throwable var8) {
            var2.add(var8);
         }
      }

      IllegalArgumentException var9 = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString((Object[])var1));
      ThrowableUtil.addSuppressedAndClear(var9, var2);
      throw var9;
   }

   private static String calculateMangledPackagePrefix() {
      String var0 = NativeLibraryLoader.class.getName();
      String var1 = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.');
      if (!var0.endsWith(var1)) {
         throw new UnsatisfiedLinkError(
            String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", var1, var0)
         );
      } else {
         return var0.substring(0, var0.length() - var1.length()).replace("_", "_1").replace('.', '_');
      }
   }

   public static void load(String var0, ClassLoader var1) {
      String var2 = calculateMangledPackagePrefix();
      String var3 = var2 + var0;
      ArrayList var4 = new ArrayList();

      try {
         loadLibrary(var1, var3, false);
      } catch (Throwable var26) {
         var4.add(var26);
         String var5 = System.mapLibraryName(var3);
         String var6 = "META-INF/native/" + var5;
         InputStream var7 = null;
         FileOutputStream var8 = null;
         File var9 = null;
         URL var10 = getResource(var6, var1);

         try {
            if (var10 == null) {
               if (!PlatformDependent.isOsx()) {
                  FileNotFoundException var28 = new FileNotFoundException(var6);
                  ThrowableUtil.addSuppressedAndClear(var28, var4);
                  throw var28;
               }

               String var11 = var6.endsWith(".jnilib") ? "META-INF/native/lib" + var3 + ".dynlib" : "META-INF/native/lib" + var3 + ".jnilib";
               var10 = getResource(var11, var1);
               if (var10 == null) {
                  FileNotFoundException var30 = new FileNotFoundException(var11);
                  ThrowableUtil.addSuppressedAndClear(var30, var4);
                  throw var30;
               }
            }

            int var27 = var5.lastIndexOf(46);
            String var29 = var5.substring(0, var27);
            String var13 = var5.substring(var27);
            var9 = PlatformDependent.createTempFile(var29, var13, WORKDIR);
            var7 = var10.openStream();
            var8 = new FileOutputStream(var9);
            byte[] var14 = new byte[8192];

            int var15;
            while ((var15 = var7.read(var14)) > 0) {
               var8.write(var14, 0, var15);
            }

            var8.flush();
            if (shouldShadedLibraryIdBePatched(var2)) {
               tryPatchShadedLibraryIdAndSign(var9, var0);
            }

            closeQuietly(var8);
            var8 = null;
            loadLibrary(var1, var9.getPath(), true);
         } catch (UnsatisfiedLinkError var23) {
            try {
               if (var9 != null && var9.isFile() && var9.canRead() && !NativeLibraryLoader.NoexecVolumeDetector.canExecuteExecutable(var9)) {
                  logger.info(
                     "{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D{}=[path] to set native working directory separately.",
                     var9.getPath(),
                     "io.netty.native.workdir"
                  );
               }
            } catch (Throwable var22) {
               var4.add(var22);
               logger.debug("Error checking if {} is on a file store mounted with noexec", var9, var22);
            }

            ThrowableUtil.addSuppressedAndClear(var23, var4);
            throw var23;
         } catch (Exception var24) {
            UnsatisfiedLinkError var12 = new UnsatisfiedLinkError("could not load a native library: " + var3);
            var12.initCause(var24);
            ThrowableUtil.addSuppressedAndClear(var12, var4);
            throw var12;
         } finally {
            closeQuietly(var7);
            closeQuietly(var8);
            if (var9 != null && (!DELETE_NATIVE_LIB_AFTER_LOADING || !var9.delete())) {
               var9.deleteOnExit();
            }
         }
      }
   }

   private static URL getResource(String var0, ClassLoader var1) {
      Enumeration var2;
      try {
         if (var1 == null) {
            var2 = ClassLoader.getSystemResources(var0);
         } else {
            var2 = var1.getResources(var0);
         }
      } catch (IOException var11) {
         throw new RuntimeException("An error occurred while getting the resources for " + var0, var11);
      }

      ArrayList var3 = Collections.list(var2);
      int var4 = var3.size();
      switch (var4) {
         case 0:
            return null;
         case 1:
            return (URL)var3.get(0);
         default:
            if (!DETECT_NATIVE_LIBRARY_DUPLICATES) {
               logger.warn("Multiple resources found for '" + var0 + "' with different content: " + var3 + ". Please fix your dependency graph.");
               return (URL)var3.get(0);
            } else {
               try {
                  MessageDigest var5 = MessageDigest.getInstance("SHA-256");
                  URL var6 = (URL)var3.get(0);
                  byte[] var7 = digest(var5, var6);
                  boolean var8 = true;
                  if (var7 == null) {
                     var8 = false;
                  } else {
                     for (int var9 = 1; var9 < var4; var9++) {
                        byte[] var10 = digest(var5, (URL)var3.get(var9));
                        if (var10 == null || !Arrays.equals(var7, var10)) {
                           var8 = false;
                           break;
                        }
                     }
                  }

                  if (var8) {
                     return var6;
                  }
               } catch (NoSuchAlgorithmException var12) {
                  logger.debug("Don't support SHA-256, can't check if resources have same content.", (Throwable)var12);
               }

               throw new IllegalStateException("Multiple resources found for '" + var0 + "' with different content: " + var3);
            }
      }
   }

   private static byte[] digest(MessageDigest var0, URL var1) {
      InputStream var2 = null;

      Object var4;
      try {
         var2 = var1.openStream();
         byte[] var3 = new byte[8192];

         while ((var11 = var2.read(var3)) != -1) {
            var0.update(var3, 0, var11);
         }

         return var0.digest();
      } catch (IOException var9) {
         logger.debug("Can't read resource.", (Throwable)var9);
         var4 = null;
      } finally {
         closeQuietly(var2);
      }

      return (byte[])var4;
   }

   static void tryPatchShadedLibraryIdAndSign(File var0, String var1) {
      String var2 = new String(generateUniqueId(var1.length()), CharsetUtil.UTF_8);
      if (tryExec("install_name_tool -id " + var2 + " " + var0.getAbsolutePath())) {
         tryExec("codesign -s - " + var0.getAbsolutePath());
      }
   }

   private static boolean tryExec(String var0) {
      try {
         int var1 = Runtime.getRuntime().exec(var0).waitFor();
         if (var1 != 0) {
            logger.debug("Execution of '{}' failed: {}", var0, var1);
            return false;
         }

         logger.debug("Execution of '{}' succeed: {}", var0, var1);
         return true;
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      } catch (IOException var3) {
         logger.info("Execution of '{}' failed.", var0, var3);
      } catch (SecurityException var4) {
         logger.error("Execution of '{}' failed.", var0, var4);
      }

      return false;
   }

   private static boolean shouldShadedLibraryIdBePatched(String var0) {
      return TRY_TO_PATCH_SHADED_ID && PlatformDependent.isOsx() && !var0.isEmpty();
   }

   private static byte[] generateUniqueId(int var0) {
      byte[] var1 = new byte[var0];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = UNIQUE_ID_BYTES[PlatformDependent.threadLocalRandom().nextInt(UNIQUE_ID_BYTES.length)];
      }

      return var1;
   }

   private static void loadLibrary(ClassLoader var0, String var1, boolean var2) {
      Object var3 = null;

      try {
         try {
            Class var4 = tryToLoadClass(var0, NativeLibraryUtil.class);
            loadLibraryByHelper(var4, var1, var2);
            logger.debug("Successfully loaded the library {}", var1);
            return;
         } catch (UnsatisfiedLinkError var5) {
            var3 = var5;
         } catch (Exception var6) {
            var3 = var6;
         }

         NativeLibraryUtil.loadLibrary(var1, var2);
         logger.debug("Successfully loaded the library {}", var1);
      } catch (NoSuchMethodError var7) {
         if (var3 != null) {
            ThrowableUtil.addSuppressed(var7, (Throwable)var3);
         }

         rethrowWithMoreDetailsIfPossible(var1, var7);
      } catch (UnsatisfiedLinkError var8) {
         if (var3 != null) {
            ThrowableUtil.addSuppressed(var8, (Throwable)var3);
         }

         throw var8;
      }
   }

   @SuppressJava6Requirement(
      reason = "Guarded by version check"
   )
   private static void rethrowWithMoreDetailsIfPossible(String var0, NoSuchMethodError var1) {
      if (PlatformDependent.javaVersion() >= 7) {
         throw new LinkageError("Possible multiple incompatible native libraries on the classpath for '" + var0 + "'?", var1);
      } else {
         throw var1;
      }
   }

   private static void loadLibraryByHelper(final Class<?> var0, final String var1, final boolean var2) throws UnsatisfiedLinkError {
      Object var3 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            try {
               Method var1x = var0.getMethod("loadLibrary", String.class, boolean.class);
               var1x.setAccessible(true);
               return var1x.invoke(null, var1, var2);
            } catch (Exception var2x) {
               return var2x;
            }
         }
      });
      if (var3 instanceof Throwable) {
         Throwable var4 = (Throwable)var3;

         assert !(var4 instanceof UnsatisfiedLinkError) : var4 + " should be a wrapper throwable";

         Throwable var5 = var4.getCause();
         if (var5 instanceof UnsatisfiedLinkError) {
            throw (UnsatisfiedLinkError)var5;
         } else {
            UnsatisfiedLinkError var6 = new UnsatisfiedLinkError(var4.getMessage());
            var6.initCause(var4);
            throw var6;
         }
      }
   }

   private static Class<?> tryToLoadClass(final ClassLoader var0, final Class<?> var1) throws ClassNotFoundException {
      try {
         return Class.forName(var1.getName(), false, var0);
      } catch (ClassNotFoundException var7) {
         if (var0 == null) {
            throw var7;
         } else {
            try {
               final byte[] var3 = classToByteArray(var1);
               return AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
                  public Class<?> run() {
                     try {
                        Method var1x = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                        var1x.setAccessible(true);
                        return (Class<?>)var1x.invoke(var0, var1.getName(), var3, 0, var3.length);
                     } catch (Exception var2) {
                        throw new IllegalStateException("Define class failed!", var2);
                     }
                  }
               });
            } catch (ClassNotFoundException var4) {
               ThrowableUtil.addSuppressed(var4, var7);
               throw var4;
            } catch (RuntimeException var5) {
               ThrowableUtil.addSuppressed(var5, var7);
               throw var5;
            } catch (Error var6) {
               ThrowableUtil.addSuppressed(var6, var7);
               throw var6;
            }
         }
      }
   }

   private static byte[] classToByteArray(Class<?> var0) throws ClassNotFoundException {
      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(46);
      if (var2 > 0) {
         var1 = var1.substring(var2 + 1);
      }

      URL var3 = var0.getResource(var1 + ".class");
      if (var3 == null) {
         throw new ClassNotFoundException(var0.getName());
      } else {
         byte[] var4 = new byte[1024];
         ByteArrayOutputStream var5 = new ByteArrayOutputStream(4096);
         InputStream var6 = null;

         byte[] var13;
         try {
            var6 = var3.openStream();

            while ((var7 = var6.read(var4)) != -1) {
               var5.write(var4, 0, var7);
            }

            var13 = var5.toByteArray();
         } catch (IOException var11) {
            throw new ClassNotFoundException(var0.getName(), var11);
         } finally {
            closeQuietly(var6);
            closeQuietly(var5);
         }

         return var13;
      }
   }

   private static void closeQuietly(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }
   }

   private NativeLibraryLoader() {
   }

   static {
      String var0 = SystemPropertyUtil.get("io.netty.native.workdir");
      if (var0 != null) {
         File var1 = new File(var0);
         var1.mkdirs();

         try {
            var1 = var1.getAbsoluteFile();
         } catch (Exception var3) {
         }

         WORKDIR = var1;
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
      } else {
         WORKDIR = PlatformDependent.tmpdir();
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
      }

      DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
      logger.debug("-Dio.netty.native.deleteLibAfterLoading: {}", DELETE_NATIVE_LIB_AFTER_LOADING);
      TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean("io.netty.native.tryPatchShadedId", true);
      logger.debug("-Dio.netty.native.tryPatchShadedId: {}", TRY_TO_PATCH_SHADED_ID);
      DETECT_NATIVE_LIBRARY_DUPLICATES = SystemPropertyUtil.getBoolean("io.netty.native.detectNativeLibraryDuplicates", true);
      logger.debug("-Dio.netty.native.detectNativeLibraryDuplicates: {}", DETECT_NATIVE_LIBRARY_DUPLICATES);
   }

   private static final class NoexecVolumeDetector {
      @SuppressJava6Requirement(
         reason = "Usage guarded by java version check"
      )
      private static boolean canExecuteExecutable(File var0) throws IOException {
         if (PlatformDependent.javaVersion() < 7) {
            return true;
         } else if (var0.canExecute()) {
            return true;
         } else {
            Set var1 = Files.getPosixFilePermissions(var0.toPath());
            EnumSet var2 = EnumSet.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE);
            if (var1.containsAll(var2)) {
               return false;
            } else {
               EnumSet var3 = EnumSet.copyOf(var1);
               var3.addAll(var2);
               Files.setPosixFilePermissions(var0.toPath(), var3);
               return var0.canExecute();
            }
         }
      }
   }
}
