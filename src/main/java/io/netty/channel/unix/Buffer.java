package io.netty.channel.unix;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Buffer {
   private Buffer() {
   }

   public static void free(ByteBuffer var0) {
      PlatformDependent.freeDirectBuffer(var0);
   }

   public static ByteBuffer allocateDirectWithNativeOrder(int var0) {
      return ByteBuffer.allocateDirect(var0).order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
   }

   public static long memoryAddress(ByteBuffer var0) {
      assert var0.isDirect();

      return PlatformDependent.hasUnsafe() ? PlatformDependent.directBufferAddress(var0) : memoryAddress0(var0);
   }

   public static int addressSize() {
      return PlatformDependent.hasUnsafe() ? PlatformDependent.addressSize() : addressSize0();
   }

   private static native int addressSize0();

   private static native long memoryAddress0(ByteBuffer var0);
}
