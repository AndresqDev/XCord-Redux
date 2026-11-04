package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class UnsafeUtil {
   private static final Logger logger = Logger.getLogger(UnsafeUtil.class.getName());
   private static final Unsafe UNSAFE = getUnsafe();
   private static final Class<?> MEMORY_CLASS = Android.getMemoryClass();
   private static final boolean IS_ANDROID_64 = determineAndroidSupportByAddressSize(long.class);
   private static final boolean IS_ANDROID_32 = determineAndroidSupportByAddressSize(int.class);
   private static final UnsafeUtil.MemoryAccessor MEMORY_ACCESSOR = getMemoryAccessor();
   private static final boolean HAS_UNSAFE_BYTEBUFFER_OPERATIONS = supportsUnsafeByteBufferOperations();
   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = supportsUnsafeArrayOperations();
   static final long BYTE_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(byte[].class);
   private static final long BOOLEAN_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(boolean[].class);
   private static final long BOOLEAN_ARRAY_INDEX_SCALE = (long)arrayIndexScale(boolean[].class);
   private static final long INT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(int[].class);
   private static final long INT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(int[].class);
   private static final long LONG_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(long[].class);
   private static final long LONG_ARRAY_INDEX_SCALE = (long)arrayIndexScale(long[].class);
   private static final long FLOAT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(float[].class);
   private static final long FLOAT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(float[].class);
   private static final long DOUBLE_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(double[].class);
   private static final long DOUBLE_ARRAY_INDEX_SCALE = (long)arrayIndexScale(double[].class);
   private static final long OBJECT_ARRAY_BASE_OFFSET = (long)arrayBaseOffset(Object[].class);
   private static final long OBJECT_ARRAY_INDEX_SCALE = (long)arrayIndexScale(Object[].class);
   private static final long BUFFER_ADDRESS_OFFSET = fieldOffset(bufferAddressField());
   private static final int STRIDE = 8;
   private static final int STRIDE_ALIGNMENT_MASK = 7;
   private static final int BYTE_ARRAY_ALIGNMENT = (int)(BYTE_ARRAY_BASE_OFFSET & 7L);
   static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

   private UnsafeUtil() {
   }

   static boolean hasUnsafeArrayOperations() {
      return HAS_UNSAFE_ARRAY_OPERATIONS;
   }

   static boolean hasUnsafeByteBufferOperations() {
      return HAS_UNSAFE_BYTEBUFFER_OPERATIONS;
   }

   static boolean isAndroid64() {
      return IS_ANDROID_64;
   }

   static <T> T allocateInstance(Class<T> var0) {
      try {
         return (T)UNSAFE.allocateInstance(var0);
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      }
   }

   static long objectFieldOffset(java.lang.reflect.Field var0) {
      return MEMORY_ACCESSOR.objectFieldOffset(var0);
   }

   private static int arrayBaseOffset(Class<?> var0) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayBaseOffset(var0) : -1;
   }

   private static int arrayIndexScale(Class<?> var0) {
      return HAS_UNSAFE_ARRAY_OPERATIONS ? MEMORY_ACCESSOR.arrayIndexScale(var0) : -1;
   }

   static byte getByte(Object var0, long var1) {
      return MEMORY_ACCESSOR.getByte(var0, var1);
   }

   static void putByte(Object var0, long var1, byte var3) {
      MEMORY_ACCESSOR.putByte(var0, var1, var3);
   }

   static int getInt(Object var0, long var1) {
      return MEMORY_ACCESSOR.getInt(var0, var1);
   }

   static void putInt(Object var0, long var1, int var3) {
      MEMORY_ACCESSOR.putInt(var0, var1, var3);
   }

   static long getLong(Object var0, long var1) {
      return MEMORY_ACCESSOR.getLong(var0, var1);
   }

   static void putLong(Object var0, long var1, long var3) {
      MEMORY_ACCESSOR.putLong(var0, var1, var3);
   }

   static boolean getBoolean(Object var0, long var1) {
      return MEMORY_ACCESSOR.getBoolean(var0, var1);
   }

   static void putBoolean(Object var0, long var1, boolean var3) {
      MEMORY_ACCESSOR.putBoolean(var0, var1, var3);
   }

   static float getFloat(Object var0, long var1) {
      return MEMORY_ACCESSOR.getFloat(var0, var1);
   }

   static void putFloat(Object var0, long var1, float var3) {
      MEMORY_ACCESSOR.putFloat(var0, var1, var3);
   }

   static double getDouble(Object var0, long var1) {
      return MEMORY_ACCESSOR.getDouble(var0, var1);
   }

   static void putDouble(Object var0, long var1, double var3) {
      MEMORY_ACCESSOR.putDouble(var0, var1, var3);
   }

   static Object getObject(Object var0, long var1) {
      return MEMORY_ACCESSOR.getObject(var0, var1);
   }

   static void putObject(Object var0, long var1, Object var3) {
      MEMORY_ACCESSOR.putObject(var0, var1, var3);
   }

   static byte getByte(byte[] var0, long var1) {
      return MEMORY_ACCESSOR.getByte(var0, BYTE_ARRAY_BASE_OFFSET + var1);
   }

   static void putByte(byte[] var0, long var1, byte var3) {
      MEMORY_ACCESSOR.putByte(var0, BYTE_ARRAY_BASE_OFFSET + var1, var3);
   }

   static int getInt(int[] var0, long var1) {
      return MEMORY_ACCESSOR.getInt(var0, INT_ARRAY_BASE_OFFSET + var1 * INT_ARRAY_INDEX_SCALE);
   }

   static void putInt(int[] var0, long var1, int var3) {
      MEMORY_ACCESSOR.putInt(var0, INT_ARRAY_BASE_OFFSET + var1 * INT_ARRAY_INDEX_SCALE, var3);
   }

   static long getLong(long[] var0, long var1) {
      return MEMORY_ACCESSOR.getLong(var0, LONG_ARRAY_BASE_OFFSET + var1 * LONG_ARRAY_INDEX_SCALE);
   }

   static void putLong(long[] var0, long var1, long var3) {
      MEMORY_ACCESSOR.putLong(var0, LONG_ARRAY_BASE_OFFSET + var1 * LONG_ARRAY_INDEX_SCALE, var3);
   }

   static boolean getBoolean(boolean[] var0, long var1) {
      return MEMORY_ACCESSOR.getBoolean(var0, BOOLEAN_ARRAY_BASE_OFFSET + var1 * BOOLEAN_ARRAY_INDEX_SCALE);
   }

   static void putBoolean(boolean[] var0, long var1, boolean var3) {
      MEMORY_ACCESSOR.putBoolean(var0, BOOLEAN_ARRAY_BASE_OFFSET + var1 * BOOLEAN_ARRAY_INDEX_SCALE, var3);
   }

   static float getFloat(float[] var0, long var1) {
      return MEMORY_ACCESSOR.getFloat(var0, FLOAT_ARRAY_BASE_OFFSET + var1 * FLOAT_ARRAY_INDEX_SCALE);
   }

   static void putFloat(float[] var0, long var1, float var3) {
      MEMORY_ACCESSOR.putFloat(var0, FLOAT_ARRAY_BASE_OFFSET + var1 * FLOAT_ARRAY_INDEX_SCALE, var3);
   }

   static double getDouble(double[] var0, long var1) {
      return MEMORY_ACCESSOR.getDouble(var0, DOUBLE_ARRAY_BASE_OFFSET + var1 * DOUBLE_ARRAY_INDEX_SCALE);
   }

   static void putDouble(double[] var0, long var1, double var3) {
      MEMORY_ACCESSOR.putDouble(var0, DOUBLE_ARRAY_BASE_OFFSET + var1 * DOUBLE_ARRAY_INDEX_SCALE, var3);
   }

   static Object getObject(Object[] var0, long var1) {
      return MEMORY_ACCESSOR.getObject(var0, OBJECT_ARRAY_BASE_OFFSET + var1 * OBJECT_ARRAY_INDEX_SCALE);
   }

   static void putObject(Object[] var0, long var1, Object var3) {
      MEMORY_ACCESSOR.putObject(var0, OBJECT_ARRAY_BASE_OFFSET + var1 * OBJECT_ARRAY_INDEX_SCALE, var3);
   }

   static void copyMemory(byte[] var0, long var1, long var3, long var5) {
      MEMORY_ACCESSOR.copyMemory(var0, var1, var3, var5);
   }

   static void copyMemory(long var0, byte[] var2, long var3, long var5) {
      MEMORY_ACCESSOR.copyMemory(var0, var2, var3, var5);
   }

   static void copyMemory(byte[] var0, long var1, byte[] var3, long var4, long var6) {
      System.arraycopy(var0, (int)var1, var3, (int)var4, (int)var6);
   }

   static byte getByte(long var0) {
      return MEMORY_ACCESSOR.getByte(var0);
   }

   static void putByte(long var0, byte var2) {
      MEMORY_ACCESSOR.putByte(var0, var2);
   }

   static int getInt(long var0) {
      return MEMORY_ACCESSOR.getInt(var0);
   }

   static void putInt(long var0, int var2) {
      MEMORY_ACCESSOR.putInt(var0, var2);
   }

   static long getLong(long var0) {
      return MEMORY_ACCESSOR.getLong(var0);
   }

   static void putLong(long var0, long var2) {
      MEMORY_ACCESSOR.putLong(var0, var2);
   }

   static long addressOffset(ByteBuffer var0) {
      return MEMORY_ACCESSOR.getLong(var0, BUFFER_ADDRESS_OFFSET);
   }

   static Object getStaticObject(java.lang.reflect.Field var0) {
      return MEMORY_ACCESSOR.getStaticObject(var0);
   }

   static Unsafe getUnsafe() {
      Unsafe var0 = null;

      try {
         var0 = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
               Class<Unsafe> var1 = Unsafe.class;

               for (java.lang.reflect.Field var5 : var1.getDeclaredFields()) {
                  var5.setAccessible(true);
                  Object var6 = var5.get(null);
                  if (var1.isInstance(var6)) {
                     return var1.cast(var6);
                  }
               }

               return null;
            }
         });
      } catch (Throwable var2) {
      }

      return var0;
   }

   private static UnsafeUtil.MemoryAccessor getMemoryAccessor() {
      if (UNSAFE == null) {
         return null;
      } else if (Android.isOnAndroidDevice()) {
         if (IS_ANDROID_64) {
            return new UnsafeUtil.Android64MemoryAccessor(UNSAFE);
         } else {
            return IS_ANDROID_32 ? new UnsafeUtil.Android32MemoryAccessor(UNSAFE) : null;
         }
      } else {
         return new UnsafeUtil.JvmMemoryAccessor(UNSAFE);
      }
   }

   private static boolean supportsUnsafeArrayOperations() {
      if (UNSAFE == null) {
         return false;
      } else {
         try {
            Class var0 = UNSAFE.getClass();
            var0.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
            var0.getMethod("arrayBaseOffset", Class.class);
            var0.getMethod("arrayIndexScale", Class.class);
            var0.getMethod("getInt", Object.class, long.class);
            var0.getMethod("putInt", Object.class, long.class, int.class);
            var0.getMethod("getLong", Object.class, long.class);
            var0.getMethod("putLong", Object.class, long.class, long.class);
            var0.getMethod("getObject", Object.class, long.class);
            var0.getMethod("putObject", Object.class, long.class, Object.class);
            if (Android.isOnAndroidDevice()) {
               return true;
            } else {
               var0.getMethod("getByte", Object.class, long.class);
               var0.getMethod("putByte", Object.class, long.class, byte.class);
               var0.getMethod("getBoolean", Object.class, long.class);
               var0.getMethod("putBoolean", Object.class, long.class, boolean.class);
               var0.getMethod("getFloat", Object.class, long.class);
               var0.getMethod("putFloat", Object.class, long.class, float.class);
               var0.getMethod("getDouble", Object.class, long.class);
               var0.getMethod("putDouble", Object.class, long.class, double.class);
               return true;
            }
         } catch (Throwable var1) {
            logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + var1);
            return false;
         }
      }
   }

   private static boolean supportsUnsafeByteBufferOperations() {
      if (UNSAFE == null) {
         return false;
      } else {
         try {
            Class var0 = UNSAFE.getClass();
            var0.getMethod("objectFieldOffset", java.lang.reflect.Field.class);
            var0.getMethod("getLong", Object.class, long.class);
            if (bufferAddressField() == null) {
               return false;
            } else if (Android.isOnAndroidDevice()) {
               return true;
            } else {
               var0.getMethod("getByte", long.class);
               var0.getMethod("putByte", long.class, byte.class);
               var0.getMethod("getInt", long.class);
               var0.getMethod("putInt", long.class, int.class);
               var0.getMethod("getLong", long.class);
               var0.getMethod("putLong", long.class, long.class);
               var0.getMethod("copyMemory", long.class, long.class, long.class);
               var0.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
               return true;
            }
         } catch (Throwable var1) {
            logger.log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + var1);
            return false;
         }
      }
   }

   private static boolean determineAndroidSupportByAddressSize(Class<?> var0) {
      if (!Android.isOnAndroidDevice()) {
         return false;
      } else {
         try {
            Class var1 = MEMORY_CLASS;
            var1.getMethod("peekLong", var0, boolean.class);
            var1.getMethod("pokeLong", var0, long.class, boolean.class);
            var1.getMethod("pokeInt", var0, int.class, boolean.class);
            var1.getMethod("peekInt", var0, boolean.class);
            var1.getMethod("pokeByte", var0, byte.class);
            var1.getMethod("peekByte", var0);
            var1.getMethod("pokeByteArray", var0, byte[].class, int.class, int.class);
            var1.getMethod("peekByteArray", var0, byte[].class, int.class, int.class);
            return true;
         } catch (Throwable var2) {
            return false;
         }
      }
   }

   private static java.lang.reflect.Field bufferAddressField() {
      if (Android.isOnAndroidDevice()) {
         java.lang.reflect.Field var0 = field(Buffer.class, "effectiveDirectAddress");
         if (var0 != null) {
            return var0;
         }
      }

      java.lang.reflect.Field var1 = field(Buffer.class, "address");
      return var1 != null && var1.getType() == long.class ? var1 : null;
   }

   private static int firstDifferingByteIndexNativeEndian(long var0, long var2) {
      int var4 = IS_BIG_ENDIAN ? Long.numberOfLeadingZeros(var0 ^ var2) : Long.numberOfTrailingZeros(var0 ^ var2);
      return var4 >> 3;
   }

   static int mismatch(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      if (var1 >= 0 && var3 >= 0 && var4 >= 0 && var1 + var4 <= var0.length && var3 + var4 <= var2.length) {
         int var5 = 0;
         if (HAS_UNSAFE_ARRAY_OPERATIONS) {
            for (int var6 = BYTE_ARRAY_ALIGNMENT + var1 & 7; var5 < var4 && (var6 & 7) != 0; var6++) {
               if (var0[var1 + var5] != var2[var3 + var5]) {
                  return var5;
               }

               var5++;
            }

            for (int var7 = (var4 - var5 & -8) + var5; var5 < var7; var5 += 8) {
               long var8 = getLong(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1 + (long)var5);
               long var10 = getLong(var2, BYTE_ARRAY_BASE_OFFSET + (long)var3 + (long)var5);
               if (var8 != var10) {
                  return var5 + firstDifferingByteIndexNativeEndian(var8, var10);
               }
            }
         }

         while (var5 < var4) {
            if (var0[var1 + var5] != var2[var3 + var5]) {
               return var5;
            }

            var5++;
         }

         return -1;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private static long fieldOffset(java.lang.reflect.Field var0) {
      return var0 != null && MEMORY_ACCESSOR != null ? MEMORY_ACCESSOR.objectFieldOffset(var0) : -1L;
   }

   private static java.lang.reflect.Field field(Class<?> var0, String var1) {
      java.lang.reflect.Field var2;
      try {
         var2 = var0.getDeclaredField(var1);
      } catch (Throwable var4) {
         var2 = null;
      }

      return var2;
   }

   private static byte getByteBigEndian(Object var0, long var1) {
      return (byte)(getInt(var0, var1 & -4L) >>> (int)((~var1 & 3L) << 3) & 0xFF);
   }

   private static byte getByteLittleEndian(Object var0, long var1) {
      return (byte)(getInt(var0, var1 & -4L) >>> (int)((var1 & 3L) << 3) & 0xFF);
   }

   private static void putByteBigEndian(Object var0, long var1, byte var3) {
      int var4 = getInt(var0, var1 & -4L);
      int var5 = (~((int)var1) & 3) << 3;
      int var6 = var4 & ~(255 << var5) | (255 & var3) << var5;
      putInt(var0, var1 & -4L, var6);
   }

   private static void putByteLittleEndian(Object var0, long var1, byte var3) {
      int var4 = getInt(var0, var1 & -4L);
      int var5 = ((int)var1 & 3) << 3;
      int var6 = var4 & ~(255 << var5) | (255 & var3) << var5;
      putInt(var0, var1 & -4L, var6);
   }

   private static boolean getBooleanBigEndian(Object var0, long var1) {
      return getByteBigEndian(var0, var1) != 0;
   }

   private static boolean getBooleanLittleEndian(Object var0, long var1) {
      return getByteLittleEndian(var0, var1) != 0;
   }

   private static void putBooleanBigEndian(Object var0, long var1, boolean var3) {
      putByteBigEndian(var0, var1, (byte)(var3 ? 1 : 0));
   }

   private static void putBooleanLittleEndian(Object var0, long var1, boolean var3) {
      putByteLittleEndian(var0, var1, (byte)(var3 ? 1 : 0));
   }

   private static final class Android32MemoryAccessor extends UnsafeUtil.MemoryAccessor {
      private static final long SMALL_ADDRESS_MASK = -1L;

      private static int smallAddress(long var0) {
         return (int)(-1L & var0);
      }

      Android32MemoryAccessor(Unsafe var1) {
         super(var1);
      }

      @Override
      public byte getByte(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putByte(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getInt(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putInt(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putLong(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte getByte(Object var1, long var2) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(var1, var2) : UnsafeUtil.getByteLittleEndian(var1, var2);
      }

      @Override
      public void putByte(Object var1, long var2, byte var4) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(var1, var2, var4);
         } else {
            UnsafeUtil.putByteLittleEndian(var1, var2, var4);
         }
      }

      @Override
      public boolean getBoolean(Object var1, long var2) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(var1, var2) : UnsafeUtil.getBooleanLittleEndian(var1, var2);
      }

      @Override
      public void putBoolean(Object var1, long var2, boolean var4) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(var1, var2, var4);
         } else {
            UnsafeUtil.putBooleanLittleEndian(var1, var2, var4);
         }
      }

      @Override
      public float getFloat(Object var1, long var2) {
         return Float.intBitsToFloat(this.getInt(var1, var2));
      }

      @Override
      public void putFloat(Object var1, long var2, float var4) {
         this.putInt(var1, var2, Float.floatToIntBits(var4));
      }

      @Override
      public double getDouble(Object var1, long var2) {
         return Double.longBitsToDouble(this.getLong(var1, var2));
      }

      @Override
      public void putDouble(Object var1, long var2, double var4) {
         this.putLong(var1, var2, Double.doubleToLongBits(var4));
      }

      @Override
      public void copyMemory(long var1, byte[] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(byte[] var1, long var2, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field var1) {
         try {
            return var1.get(null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }
   }

   private static final class Android64MemoryAccessor extends UnsafeUtil.MemoryAccessor {
      Android64MemoryAccessor(Unsafe var1) {
         super(var1);
      }

      @Override
      public byte getByte(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putByte(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getInt(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putInt(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putLong(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte getByte(Object var1, long var2) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getByteBigEndian(var1, var2) : UnsafeUtil.getByteLittleEndian(var1, var2);
      }

      @Override
      public void putByte(Object var1, long var2, byte var4) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putByteBigEndian(var1, var2, var4);
         } else {
            UnsafeUtil.putByteLittleEndian(var1, var2, var4);
         }
      }

      @Override
      public boolean getBoolean(Object var1, long var2) {
         return UnsafeUtil.IS_BIG_ENDIAN ? UnsafeUtil.getBooleanBigEndian(var1, var2) : UnsafeUtil.getBooleanLittleEndian(var1, var2);
      }

      @Override
      public void putBoolean(Object var1, long var2, boolean var4) {
         if (UnsafeUtil.IS_BIG_ENDIAN) {
            UnsafeUtil.putBooleanBigEndian(var1, var2, var4);
         } else {
            UnsafeUtil.putBooleanLittleEndian(var1, var2, var4);
         }
      }

      @Override
      public float getFloat(Object var1, long var2) {
         return Float.intBitsToFloat(this.getInt(var1, var2));
      }

      @Override
      public void putFloat(Object var1, long var2, float var4) {
         this.putInt(var1, var2, Float.floatToIntBits(var4));
      }

      @Override
      public double getDouble(Object var1, long var2) {
         return Double.longBitsToDouble(this.getLong(var1, var2));
      }

      @Override
      public void putDouble(Object var1, long var2, double var4) {
         this.putLong(var1, var2, Double.doubleToLongBits(var4));
      }

      @Override
      public void copyMemory(long var1, byte[] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void copyMemory(byte[] var1, long var2, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field var1) {
         try {
            return var1.get(null);
         } catch (IllegalAccessException var3) {
            return null;
         }
      }
   }

   private static final class JvmMemoryAccessor extends UnsafeUtil.MemoryAccessor {
      JvmMemoryAccessor(Unsafe var1) {
         super(var1);
      }

      @Override
      public byte getByte(long var1) {
         return this.unsafe.getByte(var1);
      }

      @Override
      public void putByte(long var1, byte var3) {
         this.unsafe.putByte(var1, var3);
      }

      @Override
      public int getInt(long var1) {
         return this.unsafe.getInt(var1);
      }

      @Override
      public void putInt(long var1, int var3) {
         this.unsafe.putInt(var1, var3);
      }

      @Override
      public long getLong(long var1) {
         return this.unsafe.getLong(var1);
      }

      @Override
      public void putLong(long var1, long var3) {
         this.unsafe.putLong(var1, var3);
      }

      @Override
      public byte getByte(Object var1, long var2) {
         return this.unsafe.getByte(var1, var2);
      }

      @Override
      public void putByte(Object var1, long var2, byte var4) {
         this.unsafe.putByte(var1, var2, var4);
      }

      @Override
      public boolean getBoolean(Object var1, long var2) {
         return this.unsafe.getBoolean(var1, var2);
      }

      @Override
      public void putBoolean(Object var1, long var2, boolean var4) {
         this.unsafe.putBoolean(var1, var2, var4);
      }

      @Override
      public float getFloat(Object var1, long var2) {
         return this.unsafe.getFloat(var1, var2);
      }

      @Override
      public void putFloat(Object var1, long var2, float var4) {
         this.unsafe.putFloat(var1, var2, var4);
      }

      @Override
      public double getDouble(Object var1, long var2) {
         return this.unsafe.getDouble(var1, var2);
      }

      @Override
      public void putDouble(Object var1, long var2, double var4) {
         this.unsafe.putDouble(var1, var2, var4);
      }

      @Override
      public void copyMemory(long var1, byte[] var3, long var4, long var6) {
         this.unsafe.copyMemory(null, var1, var3, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + var4, var6);
      }

      @Override
      public void copyMemory(byte[] var1, long var2, long var4, long var6) {
         this.unsafe.copyMemory(var1, UnsafeUtil.BYTE_ARRAY_BASE_OFFSET + var2, null, var4, var6);
      }

      @Override
      public Object getStaticObject(java.lang.reflect.Field var1) {
         return this.getObject(this.unsafe.staticFieldBase(var1), this.unsafe.staticFieldOffset(var1));
      }
   }

   private abstract static class MemoryAccessor {
      Unsafe unsafe;

      MemoryAccessor(Unsafe var1) {
         this.unsafe = var1;
      }

      public final long objectFieldOffset(java.lang.reflect.Field var1) {
         return this.unsafe.objectFieldOffset(var1);
      }

      public abstract byte getByte(Object var1, long var2);

      public abstract void putByte(Object var1, long var2, byte var4);

      public final int getInt(Object var1, long var2) {
         return this.unsafe.getInt(var1, var2);
      }

      public final void putInt(Object var1, long var2, int var4) {
         this.unsafe.putInt(var1, var2, var4);
      }

      public final long getLong(Object var1, long var2) {
         return this.unsafe.getLong(var1, var2);
      }

      public final void putLong(Object var1, long var2, long var4) {
         this.unsafe.putLong(var1, var2, var4);
      }

      public abstract boolean getBoolean(Object var1, long var2);

      public abstract void putBoolean(Object var1, long var2, boolean var4);

      public abstract float getFloat(Object var1, long var2);

      public abstract void putFloat(Object var1, long var2, float var4);

      public abstract double getDouble(Object var1, long var2);

      public abstract void putDouble(Object var1, long var2, double var4);

      public final Object getObject(Object var1, long var2) {
         return this.unsafe.getObject(var1, var2);
      }

      public final void putObject(Object var1, long var2, Object var4) {
         this.unsafe.putObject(var1, var2, var4);
      }

      public final int arrayBaseOffset(Class<?> var1) {
         return this.unsafe.arrayBaseOffset(var1);
      }

      public final int arrayIndexScale(Class<?> var1) {
         return this.unsafe.arrayIndexScale(var1);
      }

      public abstract byte getByte(long var1);

      public abstract void putByte(long var1, byte var3);

      public abstract int getInt(long var1);

      public abstract void putInt(long var1, int var3);

      public abstract long getLong(long var1);

      public abstract void putLong(long var1, long var3);

      public abstract Object getStaticObject(java.lang.reflect.Field var1);

      public abstract void copyMemory(long var1, byte[] var3, long var4, long var6);

      public abstract void copyMemory(byte[] var1, long var2, long var4, long var6);
   }
}
