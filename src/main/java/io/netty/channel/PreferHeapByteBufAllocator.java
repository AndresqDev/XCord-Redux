package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class PreferHeapByteBufAllocator implements ByteBufAllocator {
   private final ByteBufAllocator allocator;

   public PreferHeapByteBufAllocator(ByteBufAllocator var1) {
      this.allocator = ObjectUtil.checkNotNull(var1, "allocator");
   }

   @Override
   public ByteBuf buffer() {
      return this.allocator.heapBuffer();
   }

   @Override
   public ByteBuf buffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   @Override
   public ByteBuf buffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   @Override
   public ByteBuf ioBuffer() {
      return this.allocator.heapBuffer();
   }

   @Override
   public ByteBuf ioBuffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   @Override
   public ByteBuf ioBuffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   @Override
   public ByteBuf heapBuffer() {
      return this.allocator.heapBuffer();
   }

   @Override
   public ByteBuf heapBuffer(int var1) {
      return this.allocator.heapBuffer(var1);
   }

   @Override
   public ByteBuf heapBuffer(int var1, int var2) {
      return this.allocator.heapBuffer(var1, var2);
   }

   @Override
   public ByteBuf directBuffer() {
      return this.allocator.directBuffer();
   }

   @Override
   public ByteBuf directBuffer(int var1) {
      return this.allocator.directBuffer(var1);
   }

   @Override
   public ByteBuf directBuffer(int var1, int var2) {
      return this.allocator.directBuffer(var1, var2);
   }

   @Override
   public CompositeByteBuf compositeBuffer() {
      return this.allocator.compositeHeapBuffer();
   }

   @Override
   public CompositeByteBuf compositeBuffer(int var1) {
      return this.allocator.compositeHeapBuffer(var1);
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer() {
      return this.allocator.compositeHeapBuffer();
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer(int var1) {
      return this.allocator.compositeHeapBuffer(var1);
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer() {
      return this.allocator.compositeDirectBuffer();
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer(int var1) {
      return this.allocator.compositeDirectBuffer(var1);
   }

   @Override
   public boolean isDirectBufferPooled() {
      return this.allocator.isDirectBufferPooled();
   }

   @Override
   public int calculateNewCapacity(int var1, int var2) {
      return this.allocator.calculateNewCapacity(var1, var2);
   }
}
