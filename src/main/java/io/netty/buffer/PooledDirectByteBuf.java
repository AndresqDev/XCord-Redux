package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

final class PooledDirectByteBuf extends PooledByteBuf<ByteBuffer> {
   private static final ObjectPool<PooledDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledDirectByteBuf>() {
      public PooledDirectByteBuf newObject(ObjectPool.Handle<PooledDirectByteBuf> var1) {
         return new PooledDirectByteBuf(var1, 0);
      }
   });

   static PooledDirectByteBuf newInstance(int var0) {
      PooledDirectByteBuf var1 = RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   private PooledDirectByteBuf(ObjectPool.Handle<PooledDirectByteBuf> var1, int var2) {
      super(var1, var2);
   }

   protected ByteBuffer newInternalNioBuffer(ByteBuffer var1) {
      return var1.duplicate();
   }

   @Override
   public boolean isDirect() {
      return true;
   }

   @Override
   protected byte _getByte(int var1) {
      return this.memory.get(this.idx(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return this.memory.getShort(this.idx(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return ByteBufUtil.swapShort(this._getShort(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      var1 = this.idx(var1);
      return (this.memory.get(var1) & 0xFF) << 16 | (this.memory.get(var1 + 1) & 0xFF) << 8 | this.memory.get(var1 + 2) & 0xFF;
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      var1 = this.idx(var1);
      return this.memory.get(var1) & 0xFF | (this.memory.get(var1 + 1) & 0xFF) << 8 | (this.memory.get(var1 + 2) & 0xFF) << 16;
   }

   @Override
   protected int _getInt(int var1) {
      return this.memory.getInt(this.idx(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return ByteBufUtil.swapInt(this._getInt(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return this.memory.getLong(this.idx(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return ByteBufUtil.swapLong(this._getLong(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else if (var2.nioBufferCount() > 0) {
         for (ByteBuffer var8 : var2.nioBuffers(var3, var4)) {
            int var9 = var8.remaining();
            this.getBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.setBytes(var3, this, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      this._internalNioBuffer(var1, var4, true).get(var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.checkDstIndex(var3, var2, var1.length);
      this._internalNioBuffer(this.readerIndex, var3, false).get(var1, var2, var3);
      this.readerIndex += var3;
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      var2.put(this.duplicateInternalNioBuffer(var1, var2.remaining()));
      return this;
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.checkReadableBytes(var2);
      var1.put(this._internalNioBuffer(this.readerIndex, var2, false));
      this.readerIndex += var2;
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.getBytes(var1, var2, var3, false);
      return this;
   }

   private void getBytes(int var1, OutputStream var2, int var3, boolean var4) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 != 0) {
         ByteBufUtil.readBytes(this.alloc(), var4 ? this.internalNioBuffer() : this.memory.duplicate(), this.idx(var1), var3, var2);
      }
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var2;
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      this.memory.put(this.idx(var1), (byte)var2);
   }

   @Override
   protected void _setShort(int var1, int var2) {
      this.memory.putShort(this.idx(var1), (short)var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      this._setShort(var1, ByteBufUtil.swapShort((short)var2));
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      var1 = this.idx(var1);
      this.memory.put(var1, (byte)(var2 >>> 16));
      this.memory.put(var1 + 1, (byte)(var2 >>> 8));
      this.memory.put(var1 + 2, (byte)var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      var1 = this.idx(var1);
      this.memory.put(var1, (byte)var2);
      this.memory.put(var1 + 1, (byte)(var2 >>> 8));
      this.memory.put(var1 + 2, (byte)(var2 >>> 16));
   }

   @Override
   protected void _setInt(int var1, int var2) {
      this.memory.putInt(this.idx(var1), var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      this._setInt(var1, ByteBufUtil.swapInt(var2));
   }

   @Override
   protected void _setLong(int var1, long var2) {
      this.memory.putLong(this.idx(var1), var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      this._setLong(var1, ByteBufUtil.swapLong(var2));
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else if (var2.nioBufferCount() > 0) {
         for (ByteBuffer var8 : var2.nioBuffers(var3, var4)) {
            int var9 = var8.remaining();
            this.setBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.getBytes(var3, this, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      this._internalNioBuffer(var1, var4, false).put(var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      int var3 = var2.remaining();
      this.checkIndex(var1, var3);
      ByteBuffer var4 = this.internalNioBuffer();
      if (var2 == var4) {
         var2 = var2.duplicate();
      }

      var1 = this.idx(var1);
      ((Buffer)var4).limit(var1 + var3).position(var1);
      var4.put(var2);
      return this;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      byte[] var4 = ByteBufUtil.threadLocalTempArray(var3);
      int var5 = var2.read(var4, 0, var3);
      if (var5 <= 0) {
         return var5;
      } else {
         ByteBuffer var6 = this.internalNioBuffer();
         ((Buffer)var6).position(this.idx(var1));
         var6.put(var4, 0, var5);
         return var5;
      }
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().directBuffer(var2, this.maxCapacity());
      return var3.writeBytes(this, var1, var2);
   }

   @Override
   public boolean hasArray() {
      return false;
   }

   @Override
   public byte[] array() {
      throw new UnsupportedOperationException("direct buffer");
   }

   @Override
   public int arrayOffset() {
      throw new UnsupportedOperationException("direct buffer");
   }

   @Override
   public boolean hasMemoryAddress() {
      return false;
   }

   @Override
   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }
}
