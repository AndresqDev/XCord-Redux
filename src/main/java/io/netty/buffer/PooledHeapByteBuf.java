package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

class PooledHeapByteBuf extends PooledByteBuf<byte[]> {
   private static final ObjectPool<PooledHeapByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledHeapByteBuf>() {
      public PooledHeapByteBuf newObject(ObjectPool.Handle<PooledHeapByteBuf> var1) {
         return new PooledHeapByteBuf(var1, 0);
      }
   });

   static PooledHeapByteBuf newInstance(int var0) {
      PooledHeapByteBuf var1 = RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   PooledHeapByteBuf(ObjectPool.Handle<? extends PooledHeapByteBuf> var1, int var2) {
      super(var1, var2);
   }

   @Override
   public final boolean isDirect() {
      return false;
   }

   @Override
   protected byte _getByte(int var1) {
      return HeapByteBufUtil.getByte(this.memory, this.idx(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return HeapByteBufUtil.getShort(this.memory, this.idx(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return HeapByteBufUtil.getShortLE(this.memory, this.idx(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return HeapByteBufUtil.getUnsignedMedium(this.memory, this.idx(var1));
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return HeapByteBufUtil.getUnsignedMediumLE(this.memory, this.idx(var1));
   }

   @Override
   protected int _getInt(int var1) {
      return HeapByteBufUtil.getInt(this.memory, this.idx(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return HeapByteBufUtil.getIntLE(this.memory, this.idx(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return HeapByteBufUtil.getLong(this.memory, this.idx(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return HeapByteBufUtil.getLongLE(this.memory, this.idx(var1));
   }

   @Override
   public final ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(this.memory, this.idx(var1), var2.memoryAddress() + (long)var3, (long)var4);
      } else if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.setBytes(var3, this.memory, this.idx(var1), var4);
      }

      return this;
   }

   @Override
   public final ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      System.arraycopy(this.memory, this.idx(var1), var2, var3, var4);
      return this;
   }

   @Override
   public final ByteBuf getBytes(int var1, ByteBuffer var2) {
      int var3 = var2.remaining();
      this.checkIndex(var1, var3);
      var2.put(this.memory, this.idx(var1), var3);
      return this;
   }

   @Override
   public final ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      var2.write(this.memory, this.idx(var1), var3);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      HeapByteBufUtil.setByte(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setShort(int var1, int var2) {
      HeapByteBufUtil.setShort(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      HeapByteBufUtil.setShortLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      HeapByteBufUtil.setMedium(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      HeapByteBufUtil.setMediumLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setInt(int var1, int var2) {
      HeapByteBufUtil.setInt(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      HeapByteBufUtil.setIntLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setLong(int var1, long var2) {
      HeapByteBufUtil.setLong(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      HeapByteBufUtil.setLongLE(this.memory, this.idx(var1), var2);
   }

   @Override
   public final ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(var2.memoryAddress() + (long)var3, this.memory, this.idx(var1), (long)var4);
      } else if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.getBytes(var3, this.memory, this.idx(var1), var4);
      }

      return this;
   }

   @Override
   public final ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      System.arraycopy(var2, var3, this.memory, this.idx(var1), var4);
      return this;
   }

   @Override
   public final ByteBuf setBytes(int var1, ByteBuffer var2) {
      int var3 = var2.remaining();
      this.checkIndex(var1, var3);
      var2.get(this.memory, this.idx(var1), var3);
      return this;
   }

   @Override
   public final int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      return var2.read(this.memory, this.idx(var1), var3);
   }

   @Override
   public final ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().heapBuffer(var2, this.maxCapacity());
      return var3.writeBytes(this.memory, this.idx(var1), var2);
   }

   @Override
   final ByteBuffer duplicateInternalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return ByteBuffer.wrap(this.memory, this.idx(var1), var2).slice();
   }

   @Override
   public final boolean hasArray() {
      return true;
   }

   @Override
   public final byte[] array() {
      this.ensureAccessible();
      return this.memory;
   }

   @Override
   public final int arrayOffset() {
      return this.offset;
   }

   @Override
   public final boolean hasMemoryAddress() {
      return false;
   }

   @Override
   public final long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   protected final ByteBuffer newInternalNioBuffer(byte[] var1) {
      return ByteBuffer.wrap(var1);
   }
}
