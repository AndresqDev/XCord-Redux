package com.google.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

final class ByteBufferWriter {
   private static final int MIN_CACHED_BUFFER_SIZE = 1024;
   private static final int MAX_CACHED_BUFFER_SIZE = 16384;
   private static final float BUFFER_REALLOCATION_THRESHOLD = 0.5F;
   private static final ThreadLocal<SoftReference<byte[]>> BUFFER = new ThreadLocal<>();
   private static final Class<?> FILE_OUTPUT_STREAM_CLASS = safeGetClass("java.io.FileOutputStream");
   private static final long CHANNEL_FIELD_OFFSET = getChannelFieldOffset(FILE_OUTPUT_STREAM_CLASS);

   private ByteBufferWriter() {
   }

   static void clearCachedBuffer() {
      BUFFER.set(null);
   }

   static void write(ByteBuffer var0, OutputStream var1) throws IOException {
      int var2 = var0.position();

      try {
         if (var0.hasArray()) {
            var1.write(var0.array(), var0.arrayOffset() + var0.position(), var0.remaining());
         } else if (!writeToChannel(var0, var1)) {
            byte[] var3 = getOrCreateBuffer(var0.remaining());

            while (var0.hasRemaining()) {
               int var4 = Math.min(var0.remaining(), var3.length);
               var0.get(var3, 0, var4);
               var1.write(var3, 0, var4);
            }
         }
      } finally {
         ((Buffer)var0).position(var2);
      }
   }

   private static byte[] getOrCreateBuffer(int var0) {
      var0 = Math.max(var0, 1024);
      byte[] var1 = getBuffer();
      if (var1 == null || needToReallocate(var0, var1.length)) {
         var1 = new byte[var0];
         if (var0 <= 16384) {
            setBuffer(var1);
         }
      }

      return var1;
   }

   private static boolean needToReallocate(int var0, int var1) {
      return var1 < var0 && (float)var1 < (float)var0 * 0.5F;
   }

   private static byte[] getBuffer() {
      SoftReference var0 = BUFFER.get();
      return var0 == null ? null : (byte[])var0.get();
   }

   private static void setBuffer(byte[] var0) {
      BUFFER.set(new SoftReference<>(var0));
   }

   private static boolean writeToChannel(ByteBuffer var0, OutputStream var1) throws IOException {
      if (CHANNEL_FIELD_OFFSET >= 0L && FILE_OUTPUT_STREAM_CLASS.isInstance(var1)) {
         WritableByteChannel var2 = null;

         try {
            var2 = (WritableByteChannel)UnsafeUtil.getObject(var1, CHANNEL_FIELD_OFFSET);
         } catch (ClassCastException var4) {
         }

         if (var2 != null) {
            var2.write(var0);
            return true;
         }
      }

      return false;
   }

   private static Class<?> safeGetClass(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   private static long getChannelFieldOffset(Class<?> var0) {
      try {
         if (var0 != null && UnsafeUtil.hasUnsafeArrayOperations()) {
            java.lang.reflect.Field var1 = var0.getDeclaredField("channel");
            return UnsafeUtil.objectFieldOffset(var1);
         }
      } catch (Throwable var2) {
      }

      return -1L;
   }
}
