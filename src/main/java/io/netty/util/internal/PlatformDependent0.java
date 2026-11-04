package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

@SuppressJava6Requirement(
   reason = "Unsafe access is guarded"
)
final class PlatformDependent0 {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
   private static final long ADDRESS_FIELD_OFFSET;
   private static final long BYTE_ARRAY_BASE_OFFSET;
   private static final long INT_ARRAY_BASE_OFFSET;
   private static final long INT_ARRAY_INDEX_SCALE;
   private static final long LONG_ARRAY_BASE_OFFSET;
   private static final long LONG_ARRAY_INDEX_SCALE;
   private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;
   private static final Throwable EXPLICIT_NO_UNSAFE_CAUSE = explicitNoUnsafeCause0();
   private static final Method ALLOCATE_ARRAY_METHOD;
   private static final Method ALIGN_SLICE;
   private static final int JAVA_VERSION = javaVersion0();
   private static final boolean IS_ANDROID = isAndroid0();
   private static final boolean STORE_FENCE_AVAILABLE;
   private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
   private static final Object INTERNAL_UNSAFE;
   private static final boolean RUNNING_IN_NATIVE_IMAGE = SystemPropertyUtil.contains("org.graalvm.nativeimage.imagecode");
   private static final boolean IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE = explicitTryReflectionSetAccessible0();
   static final Unsafe UNSAFE;
   static final int HASH_CODE_ASCII_SEED = -1028477387;
   static final int HASH_CODE_C1 = -862048943;
   static final int HASH_CODE_C2 = 461845907;
   private static final long UNSAFE_COPY_THRESHOLD = 1048576L;
   private static final boolean UNALIGNED;

   private static boolean unsafeStaticFieldOffsetSupported() {
      return !RUNNING_IN_NATIVE_IMAGE;
   }

   static boolean isExplicitNoUnsafe() {
      return EXPLICIT_NO_UNSAFE_CAUSE != null;
   }

   private static Throwable explicitNoUnsafeCause0() {
      boolean var0 = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
      logger.debug("-Dio.netty.noUnsafe: {}", var0);
      if (var0) {
         logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
         return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
      } else {
         String var1;
         if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            var1 = "io.netty.tryUnsafe";
         } else {
            var1 = "org.jboss.netty.tryUnsafe";
         }

         if (!SystemPropertyUtil.getBoolean(var1, true)) {
            String var2 = "sun.misc.Unsafe: unavailable (" + var1 + ")";
            logger.debug(var2);
            return new UnsupportedOperationException(var2);
         } else {
            return null;
         }
      }
   }

   static boolean isUnaligned() {
      return UNALIGNED;
   }

   static boolean hasUnsafe() {
      return UNSAFE != null;
   }

   static Throwable getUnsafeUnavailabilityCause() {
      return UNSAFE_UNAVAILABILITY_CAUSE;
   }

   static boolean unalignedAccess() {
      return UNALIGNED;
   }

   static void throwException(Throwable var0) {
      UNSAFE.throwException(ObjectUtil.checkNotNull(var0, "cause"));
   }

   static boolean hasDirectBufferNoCleanerConstructor() {
      return DIRECT_BUFFER_CONSTRUCTOR != null;
   }

   static ByteBuffer reallocateDirectNoCleaner(ByteBuffer var0, int var1) {
      return newDirectBuffer(UNSAFE.reallocateMemory(directBufferAddress(var0), (long)var1), var1);
   }

   static ByteBuffer allocateDirectNoCleaner(int var0) {
      return newDirectBuffer(UNSAFE.allocateMemory((long)Math.max(1, var0)), var0);
   }

   static boolean hasAlignSliceMethod() {
      return ALIGN_SLICE != null;
   }

   static ByteBuffer alignSlice(ByteBuffer var0, int var1) {
      try {
         return (ByteBuffer)ALIGN_SLICE.invoke(var0, var1);
      } catch (IllegalAccessException var3) {
         throw new Error(var3);
      } catch (InvocationTargetException var4) {
         throw new Error(var4);
      }
   }

   static boolean hasAllocateArrayMethod() {
      return ALLOCATE_ARRAY_METHOD != null;
   }

   static byte[] allocateUninitializedArray(int var0) {
      try {
         return (byte[])ALLOCATE_ARRAY_METHOD.invoke(INTERNAL_UNSAFE, byte.class, var0);
      } catch (IllegalAccessException var2) {
         throw new Error(var2);
      } catch (InvocationTargetException var3) {
         throw new Error(var3);
      }
   }

   static ByteBuffer newDirectBuffer(long var0, int var2) {
      ObjectUtil.checkPositiveOrZero(var2, "capacity");

      try {
         return (ByteBuffer)DIRECT_BUFFER_CONSTRUCTOR.newInstance(var0, var2);
      } catch (Throwable var4) {
         if (var4 instanceof Error) {
            throw (Error)var4;
         } else {
            throw new Error(var4);
         }
      }
   }

   static long directBufferAddress(ByteBuffer var0) {
      return getLong(var0, ADDRESS_FIELD_OFFSET);
   }

   static long byteArrayBaseOffset() {
      return BYTE_ARRAY_BASE_OFFSET;
   }

   static Object getObject(Object var0, long var1) {
      return UNSAFE.getObject(var0, var1);
   }

   static int getInt(Object var0, long var1) {
      return UNSAFE.getInt(var0, var1);
   }

   static void safeConstructPutInt(Object var0, long var1, int var3) {
      if (STORE_FENCE_AVAILABLE) {
         UNSAFE.putInt(var0, var1, var3);
         UNSAFE.storeFence();
      } else {
         UNSAFE.putIntVolatile(var0, var1, var3);
      }
   }

   private static long getLong(Object var0, long var1) {
      return UNSAFE.getLong(var0, var1);
   }

   static long objectFieldOffset(Field var0) {
      return UNSAFE.objectFieldOffset(var0);
   }

   static byte getByte(long var0) {
      return UNSAFE.getByte(var0);
   }

   static short getShort(long var0) {
      return UNSAFE.getShort(var0);
   }

   static int getInt(long var0) {
      return UNSAFE.getInt(var0);
   }

   static long getLong(long var0) {
      return UNSAFE.getLong(var0);
   }

   static byte getByte(byte[] var0, int var1) {
      return UNSAFE.getByte(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static byte getByte(byte[] var0, long var1) {
      return UNSAFE.getByte(var0, BYTE_ARRAY_BASE_OFFSET + var1);
   }

   static short getShort(byte[] var0, int var1) {
      return UNSAFE.getShort(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static int getInt(byte[] var0, int var1) {
      return UNSAFE.getInt(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static int getInt(int[] var0, long var1) {
      return UNSAFE.getInt(var0, INT_ARRAY_BASE_OFFSET + INT_ARRAY_INDEX_SCALE * var1);
   }

   static int getIntVolatile(long var0) {
      return UNSAFE.getIntVolatile(null, var0);
   }

   static void putIntOrdered(long var0, int var2) {
      UNSAFE.putOrderedInt(null, var0, var2);
   }

   static long getLong(byte[] var0, int var1) {
      return UNSAFE.getLong(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static long getLong(long[] var0, long var1) {
      return UNSAFE.getLong(var0, LONG_ARRAY_BASE_OFFSET + LONG_ARRAY_INDEX_SCALE * var1);
   }

   static void putByte(long var0, byte var2) {
      UNSAFE.putByte(var0, var2);
   }

   static void putShort(long var0, short var2) {
      UNSAFE.putShort(var0, var2);
   }

   static void putInt(long var0, int var2) {
      UNSAFE.putInt(var0, var2);
   }

   static void putLong(long var0, long var2) {
      UNSAFE.putLong(var0, var2);
   }

   static void putByte(byte[] var0, int var1, byte var2) {
      UNSAFE.putByte(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putByte(Object var0, long var1, byte var3) {
      UNSAFE.putByte(var0, var1, var3);
   }

   static void putShort(byte[] var0, int var1, short var2) {
      UNSAFE.putShort(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putInt(byte[] var0, int var1, int var2) {
      UNSAFE.putInt(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putLong(byte[] var0, int var1, long var2) {
      UNSAFE.putLong(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putObject(Object var0, long var1, Object var3) {
      UNSAFE.putObject(var0, var1, var3);
   }

   static void copyMemory(long var0, long var2, long var4) {
      if (javaVersion() <= 8) {
         copyMemoryWithSafePointPolling(var0, var2, var4);
      } else {
         UNSAFE.copyMemory(var0, var2, var4);
      }
   }

   private static void copyMemoryWithSafePointPolling(long var0, long var2, long var4) {
      while (var4 > 0L) {
         long var6 = Math.min(var4, 1048576L);
         UNSAFE.copyMemory(var0, var2, var6);
         var4 -= var6;
         var0 += var6;
         var2 += var6;
      }
   }

   static void copyMemory(Object var0, long var1, Object var3, long var4, long var6) {
      if (javaVersion() <= 8) {
         copyMemoryWithSafePointPolling(var0, var1, var3, var4, var6);
      } else {
         UNSAFE.copyMemory(var0, var1, var3, var4, var6);
      }
   }

   private static void copyMemoryWithSafePointPolling(Object var0, long var1, Object var3, long var4, long var6) {
      while (var6 > 0L) {
         long var8 = Math.min(var6, 1048576L);
         UNSAFE.copyMemory(var0, var1, var3, var4, var8);
         var6 -= var8;
         var1 += var8;
         var4 += var8;
      }
   }

   static void setMemory(long var0, long var2, byte var4) {
      UNSAFE.setMemory(var0, var2, var4);
   }

   static void setMemory(Object var0, long var1, long var3, byte var5) {
      UNSAFE.setMemory(var0, var1, var3, var5);
   }

   static boolean equals(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = var4 & 7;
      long var6 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
      long var8 = (long)(var3 - var1);
      if (var4 >= 8) {
         long var10 = var6 + (long)var5;

         for (long var12 = var6 - 8L + (long)var4; var12 >= var10; var12 -= 8L) {
            if (UNSAFE.getLong(var0, var12) != UNSAFE.getLong(var2, var12 + var8)) {
               return false;
            }
         }
      }

      if (var5 >= 4) {
         var5 -= 4;
         long var14 = var6 + (long)var5;
         if (UNSAFE.getInt(var0, var14) != UNSAFE.getInt(var2, var14 + var8)) {
            return false;
         }
      }

      long var15 = var6 + var8;
      return var5 >= 2
         ? UNSAFE.getChar(var0, var6) == UNSAFE.getChar(var2, var15) && (var5 == 2 || UNSAFE.getByte(var0, var6 + 2L) == UNSAFE.getByte(var2, var15 + 2L))
         : var5 == 0 || UNSAFE.getByte(var0, var6) == UNSAFE.getByte(var2, var15);
   }

   static int equalsConstantTime(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      long var5 = 0L;
      long var7 = (long)(var4 & 7);
      long var9 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
      long var11 = var9 + var7;
      long var13 = (long)(var3 - var1);

      for (long var15 = var9 - 8L + (long)var4; var15 >= var11; var15 -= 8L) {
         var5 |= UNSAFE.getLong(var0, var15) ^ UNSAFE.getLong(var2, var15 + var13);
      }

      if (var7 >= 4L) {
         var5 |= (long)(UNSAFE.getInt(var0, var9) ^ UNSAFE.getInt(var2, var9 + var13));
         var7 -= 4L;
      }

      if (var7 >= 2L) {
         long var17 = var11 - var7;
         var5 |= (long)(UNSAFE.getChar(var0, var17) ^ UNSAFE.getChar(var2, var17 + var13));
         var7 -= 2L;
      }

      if (var7 == 1L) {
         long var18 = var11 - 1L;
         var5 |= (long)(UNSAFE.getByte(var0, var18) ^ UNSAFE.getByte(var2, var18 + var13));
      }

      return ConstantTimeUtils.equalsConstantTime(var5, 0L);
   }

   static boolean isZero(byte[] var0, int var1, int var2) {
      if (var2 <= 0) {
         return true;
      } else {
         long var3 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
         int var5 = var2 & 7;
         long var6 = var3 + (long)var5;

         for (long var8 = var3 - 8L + (long)var2; var8 >= var6; var8 -= 8L) {
            if (UNSAFE.getLong(var0, var8) != 0L) {
               return false;
            }
         }

         if (var5 >= 4) {
            var5 -= 4;
            if (UNSAFE.getInt(var0, var3 + (long)var5) != 0) {
               return false;
            }
         }

         return var5 < 2 ? var0[var1] == 0 : UNSAFE.getChar(var0, var3) == 0 && (var5 == 2 || var0[var1 + 2] == 0);
      }
   }

   static int hashCodeAscii(byte[] var0, int var1, int var2) {
      int var3 = -1028477387;
      long var4 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
      int var6 = var2 & 7;
      long var7 = var4 + (long)var6;

      for (long var9 = var4 - 8L + (long)var2; var9 >= var7; var9 -= 8L) {
         var3 = hashCodeAsciiCompute(UNSAFE.getLong(var0, var9), var3);
      }

      if (var6 == 0) {
         return var3;
      } else {
         int var11 = -862048943;
         if (var6 != 2 & var6 != 4 & var6 != 6) {
            var3 = var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(var0, var4));
            var11 = 461845907;
            var4++;
         }

         if (var6 != 1 & var6 != 4 & var6 != 5) {
            var3 = var3 * var11 + hashCodeAsciiSanitize(UNSAFE.getShort(var0, var4));
            var11 = var11 == -862048943 ? 461845907 : -862048943;
            var4 += 2L;
         }

         return var6 >= 4 ? var3 * var11 + hashCodeAsciiSanitize(UNSAFE.getInt(var0, var4)) : var3;
      }
   }

   static int hashCodeAsciiCompute(long var0, int var2) {
      return var2 * -862048943 + hashCodeAsciiSanitize((int)var0) * 461845907 + (int)((var0 & 2242545357458243584L) >>> 32);
   }

   static int hashCodeAsciiSanitize(int var0) {
      return var0 & 522133279;
   }

   static int hashCodeAsciiSanitize(short var0) {
      return var0 & 7967;
   }

   static int hashCodeAsciiSanitize(byte var0) {
      return var0 & 31;
   }

   static ClassLoader getClassLoader(final Class<?> var0) {
      return System.getSecurityManager() == null ? var0.getClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return var0.getClassLoader();
         }
      });
   }

   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null
         ? Thread.currentThread().getContextClassLoader()
         : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return Thread.currentThread().getContextClassLoader();
            }
         });
   }

   static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static int addressSize() {
      return UNSAFE.addressSize();
   }

   static long allocateMemory(long var0) {
      return UNSAFE.allocateMemory(var0);
   }

   static void freeMemory(long var0) {
      UNSAFE.freeMemory(var0);
   }

   static long reallocateMemory(long var0, long var2) {
      return UNSAFE.reallocateMemory(var0, var2);
   }

   static boolean isAndroid() {
      return IS_ANDROID;
   }

   private static boolean isAndroid0() {
      String var0 = SystemPropertyUtil.get("java.vm.name");
      boolean var1 = "Dalvik".equals(var0);
      if (var1) {
         logger.debug("Platform: Android");
      }

      return var1;
   }

   private static boolean explicitTryReflectionSetAccessible0() {
      return SystemPropertyUtil.getBoolean("io.netty.tryReflectionSetAccessible", javaVersion() < 9 || RUNNING_IN_NATIVE_IMAGE);
   }

   static boolean isExplicitTryReflectionSetAccessible() {
      return IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
   }

   static int javaVersion() {
      return JAVA_VERSION;
   }

   private static int javaVersion0() {
      int var0;
      if (isAndroid0()) {
         var0 = 6;
      } else {
         var0 = majorVersionFromJavaSpecificationVersion();
      }

      logger.debug("Java version: {}", var0);
      return var0;
   }

   static int majorVersionFromJavaSpecificationVersion() {
      return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.6"));
   }

   static int majorVersion(String var0) {
      String[] var1 = var0.split("\\.");
      int[] var2 = new int[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = Integer.parseInt(var1[var3]);
      }

      if (var2[0] == 1) {
         assert var2[1] >= 6;

         return var2[1];
      } else {
         return var2[0];
      }
   }

   private PlatformDependent0() {
   }

   static {
      Field var1 = null;
      Method var2 = null;
      Object var3 = null;
      Object var5 = null;
      boolean var6 = false;
      var3 = EXPLICIT_NO_UNSAFE_CAUSE;
      final ByteBuffer var0;
      final Unsafe var4;
      if (EXPLICIT_NO_UNSAFE_CAUSE != null) {
         var0 = null;
         var1 = null;
         var4 = null;
         var5 = null;
      } else {
         var0 = ByteBuffer.allocateDirect(1);
         Object var7 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Field var1 = Unsafe.class.getDeclaredField("theUnsafe");
                  Throwable var2 = ReflectionUtil.trySetAccessible(var1, false);
                  return var2 != null ? var2 : var1.get(null);
               } catch (NoSuchFieldException var3) {
                  return var3;
               } catch (SecurityException var4) {
                  return var4;
               } catch (IllegalAccessException var5) {
                  return var5;
               } catch (NoClassDefFoundError var6) {
                  return var6;
               }
            }
         });
         if (var7 instanceof Throwable) {
            var4 = null;
            var3 = (Throwable)var7;
            if (logger.isTraceEnabled()) {
               logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Throwable)var7);
            } else {
               logger.debug("sun.misc.Unsafe.theUnsafe: unavailable: {}", ((Throwable)var7).getMessage());
            }
         } else {
            var4 = (Unsafe)var7;
            logger.debug("sun.misc.Unsafe.theUnsafe: available");
         }

         if (var4 != null) {
            Object var9 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     var4.getClass().getDeclaredMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
                     return null;
                  } catch (NoSuchMethodException var2) {
                     return var2;
                  } catch (SecurityException var3) {
                     return var3;
                  }
               }
            });
            if (var9 == null) {
               logger.debug("sun.misc.Unsafe.copyMemory: available");
            } else {
               var4 = null;
               var3 = (Throwable)var9;
               if (logger.isTraceEnabled()) {
                  logger.debug("sun.misc.Unsafe.copyMemory: unavailable", (Throwable)var9);
               } else {
                  logger.debug("sun.misc.Unsafe.copyMemory: unavailable: {}", ((Throwable)var9).getMessage());
               }
            }
         }

         if (var4 != null) {
            Object var31 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     var4.getClass().getDeclaredMethod("storeFence");
                     return null;
                  } catch (NoSuchMethodException var2) {
                     return var2;
                  } catch (SecurityException var3) {
                     return var3;
                  }
               }
            });
            if (var31 == null) {
               logger.debug("sun.misc.Unsafe.storeFence: available");
               var6 = true;
            } else {
               var6 = false;
               if (logger.isTraceEnabled()) {
                  logger.debug("sun.misc.Unsafe.storeFence: unavailable", (Throwable)var31);
               } else {
                  logger.debug("sun.misc.Unsafe.storeFence: unavailable: {}", ((Throwable)var31).getMessage());
               }
            }
         }

         if (var4 != null) {
            Object var32 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field var1 = Buffer.class.getDeclaredField("address");
                     long var2 = var4.objectFieldOffset(var1);
                     long var4x = var4.getLong(var0, var2);
                     return var4x == 0L ? null : var1;
                  } catch (NoSuchFieldException var6) {
                     return var6;
                  } catch (SecurityException var7) {
                     return var7;
                  }
               }
            });
            if (var32 instanceof Field) {
               var1 = (Field)var32;
               logger.debug("java.nio.Buffer.address: available");
            } else {
               var3 = (Throwable)var32;
               if (logger.isTraceEnabled()) {
                  logger.debug("java.nio.Buffer.address: unavailable", (Throwable)var32);
               } else {
                  logger.debug("java.nio.Buffer.address: unavailable: {}", ((Throwable)var32).getMessage());
               }

               var4 = null;
            }
         }

         if (var4 != null) {
            long var8 = (long)var4.arrayIndexScale(byte[].class);
            if (var8 != 1L) {
               logger.debug("unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", var8);
               var3 = new UnsupportedOperationException("Unexpected unsafe.arrayIndexScale");
               var4 = null;
            }
         }
      }

      UNSAFE_UNAVAILABILITY_CAUSE = (Throwable)var3;
      UNSAFE = var4;
      if (var4 == null) {
         ADDRESS_FIELD_OFFSET = -1L;
         BYTE_ARRAY_BASE_OFFSET = -1L;
         LONG_ARRAY_BASE_OFFSET = -1L;
         LONG_ARRAY_INDEX_SCALE = -1L;
         INT_ARRAY_BASE_OFFSET = -1L;
         INT_ARRAY_INDEX_SCALE = -1L;
         UNALIGNED = false;
         DIRECT_BUFFER_CONSTRUCTOR = null;
         ALLOCATE_ARRAY_METHOD = null;
         STORE_FENCE_AVAILABLE = false;
      } else {
         long var30 = -1L;

         Constructor var29;
         try {
            Object var10 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Constructor var1 = var0.getClass().getDeclaredConstructor(long.class, int.class);
                     Throwable var2 = ReflectionUtil.trySetAccessible(var1, true);
                     return var2 != null ? var2 : var1;
                  } catch (NoSuchMethodException var3) {
                     return var3;
                  } catch (SecurityException var4) {
                     return var4;
                  }
               }
            });
            if (var10 instanceof Constructor) {
               var30 = UNSAFE.allocateMemory(1L);

               try {
                  ((Constructor)var10).newInstance(var30, 1);
                  var29 = (Constructor)var10;
                  logger.debug("direct buffer constructor: available");
               } catch (InstantiationException var24) {
                  var29 = null;
               } catch (IllegalAccessException var25) {
                  var29 = null;
               } catch (InvocationTargetException var26) {
                  var29 = null;
               }
            } else {
               if (logger.isTraceEnabled()) {
                  logger.debug("direct buffer constructor: unavailable", (Throwable)var10);
               } else {
                  logger.debug("direct buffer constructor: unavailable: {}", ((Throwable)var10).getMessage());
               }

               var29 = null;
            }
         } finally {
            if (var30 != -1L) {
               UNSAFE.freeMemory(var30);
            }
         }

         DIRECT_BUFFER_CONSTRUCTOR = var29;
         ADDRESS_FIELD_OFFSET = objectFieldOffset(var1);
         BYTE_ARRAY_BASE_OFFSET = (long)UNSAFE.arrayBaseOffset(byte[].class);
         INT_ARRAY_BASE_OFFSET = (long)UNSAFE.arrayBaseOffset(int[].class);
         INT_ARRAY_INDEX_SCALE = (long)UNSAFE.arrayIndexScale(int[].class);
         LONG_ARRAY_BASE_OFFSET = (long)UNSAFE.arrayBaseOffset(long[].class);
         LONG_ARRAY_INDEX_SCALE = (long)UNSAFE.arrayIndexScale(long[].class);
         Object var11 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Class var1 = Class.forName("java.nio.Bits", false, PlatformDependent0.getSystemClassLoader());
                  int var2 = PlatformDependent0.javaVersion();
                  if (PlatformDependent0.unsafeStaticFieldOffsetSupported() && var2 >= 9) {
                     String var3 = var2 >= 11 ? "UNALIGNED" : "unaligned";

                     try {
                        Field var4 = var1.getDeclaredField(var3);
                        if (var4.getType() == boolean.class) {
                           long var5 = PlatformDependent0.UNSAFE.staticFieldOffset(var4);
                           Object var7 = PlatformDependent0.UNSAFE.staticFieldBase(var4);
                           return PlatformDependent0.UNSAFE.getBoolean(var7, var5);
                        }
                     } catch (NoSuchFieldException var8) {
                     }
                  }

                  Method var14 = var1.getDeclaredMethod("unaligned");
                  Throwable var15 = ReflectionUtil.trySetAccessible(var14, true);
                  return var15 != null ? var15 : var14.invoke(null);
               } catch (NoSuchMethodException var9) {
                  return var9;
               } catch (SecurityException var10) {
                  return var10;
               } catch (IllegalAccessException var11) {
                  return var11;
               } catch (ClassNotFoundException var12) {
                  return var12;
               } catch (InvocationTargetException var13) {
                  return var13;
               }
            }
         });
         boolean var33;
         if (var11 instanceof Boolean) {
            var33 = (Boolean)var11;
            logger.debug("java.nio.Bits.unaligned: available, {}", var33);
         } else {
            String var12 = SystemPropertyUtil.get("os.arch", "");
            var33 = var12.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            Throwable var13 = (Throwable)var11;
            if (logger.isTraceEnabled()) {
               logger.debug("java.nio.Bits.unaligned: unavailable, {}", var33, var13);
            } else {
               logger.debug("java.nio.Bits.unaligned: unavailable, {}, {}", var33, var13.getMessage());
            }
         }

         UNALIGNED = var33;
         if (javaVersion() >= 9) {
            final Object var34 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Class var1 = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass("jdk.internal.misc.Unsafe");
                     Method var2 = var1.getDeclaredMethod("getUnsafe");
                     return var2.invoke(null);
                  } catch (Throwable var3) {
                     return var3;
                  }
               }
            });
            if (!(var34 instanceof Throwable)) {
               var5 = var34;
               Object var35 = var34;
               var34 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  @Override
                  public Object run() {
                     try {
                        return var34.getClass().getDeclaredMethod("allocateUninitializedArray", Class.class, int.class);
                     } catch (NoSuchMethodException var2) {
                        return var2;
                     } catch (SecurityException var3) {
                        return var3;
                     }
                  }
               });
               if (var34 instanceof Method) {
                  try {
                     Method var14 = (Method)var34;
                     byte[] var15 = (byte[])var14.invoke(var35, byte.class, 8);

                     assert var15.length == 8;

                     var2 = var14;
                  } catch (IllegalAccessException var22) {
                     var34 = var22;
                  } catch (InvocationTargetException var23) {
                     var34 = var23;
                  }
               }
            }

            if (var34 instanceof Throwable) {
               if (logger.isTraceEnabled()) {
                  logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)var34);
               } else {
                  logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable: {}", ((Throwable)var34).getMessage());
               }
            } else {
               logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
            }
         } else {
            logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
         }

         ALLOCATE_ARRAY_METHOD = var2;
         STORE_FENCE_AVAILABLE = var6;
      }

      if (javaVersion() > 9) {
         ALIGN_SLICE = (Method)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  return ByteBuffer.class.getDeclaredMethod("alignedSlice", int.class);
               } catch (Exception var2) {
                  return null;
               }
            }
         });
      } else {
         ALIGN_SLICE = null;
      }

      INTERNAL_UNSAFE = var5;
      logger.debug("java.nio.DirectByteBuffer.<init>(long, int): {}", DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable");
   }
}
