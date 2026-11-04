package io.netty.channel.epoll;

import io.netty.channel.unix.Buffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class EpollEventArray {
   private static final int EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
   private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
   private ByteBuffer memory;
   private long memoryAddress;
   private int length;

   EpollEventArray(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("length must be >= 1 but was " + var1);
      } else {
         this.length = var1;
         this.memory = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(var1));
         this.memoryAddress = Buffer.memoryAddress(this.memory);
      }
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   int length() {
      return this.length;
   }

   void increase() {
      this.length <<= 1;
      ByteBuffer var1 = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(this.length));
      Buffer.free(this.memory);
      this.memory = var1;
      this.memoryAddress = Buffer.memoryAddress(var1);
   }

   void free() {
      Buffer.free(this.memory);
      this.memoryAddress = 0L;
   }

   int events(int var1) {
      return this.getInt(var1, 0);
   }

   int fd(int var1) {
      return this.getInt(var1, EPOLL_DATA_OFFSET);
   }

   private int getInt(int var1, int var2) {
      if (PlatformDependent.hasUnsafe()) {
         long var3 = (long)var1 * (long)EPOLL_EVENT_SIZE;
         return PlatformDependent.getInt(this.memoryAddress + var3 + (long)var2);
      } else {
         return this.memory.getInt(var1 * EPOLL_EVENT_SIZE + var2);
      }
   }

   private static int calculateBufferCapacity(int var0) {
      return var0 * EPOLL_EVENT_SIZE;
   }
}
