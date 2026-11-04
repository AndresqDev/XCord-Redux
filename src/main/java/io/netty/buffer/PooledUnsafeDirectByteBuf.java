package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class PooledUnsafeDirectByteBuf extends PooledByteBuf<ByteBuffer> {
   private static final ObjectPool<PooledUnsafeDirectByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledUnsafeDirectByteBuf>() {
      public PooledUnsafeDirectByteBuf newObject(ObjectPool.Handle<PooledUnsafeDirectByteBuf> var1) {
         return new PooledUnsafeDirectByteBuf(var1, 0);
      }
   });
   private long memoryAddress;

   static PooledUnsafeDirectByteBuf newInstance(int var0) {
      PooledUnsafeDirectByteBuf var1 = RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   private PooledUnsafeDirectByteBuf(ObjectPool.Handle<PooledUnsafeDirectByteBuf> var1, int var2) {
      super(var1, var2);
   }

   @Override
   void init(PoolChunk<ByteBuffer> var1, ByteBuffer var2, long var3, int var5, int var6, int var7, PoolThreadCache var8) {
      super.init(var1, var2, var3, var5, var6, var7, var8);
      this.initMemoryAddress();
   }

   @Override
   void initUnpooled(PoolChunk<ByteBuffer> var1, int var2) {
      super.initUnpooled(var1, var2);
      this.initMemoryAddress();
   }

   private void initMemoryAddress() {
      this.memoryAddress = PlatformDependent.directBufferAddress(this.memory) + (long)this.offset;
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
      return UnsafeByteBufUtil.getByte(this.addr(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.addr(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE(this.addr(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.addr(var1));
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.addr(var1));
   }

   @Override
   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.addr(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE(this.addr(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.addr(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE(this.addr(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte(this.addr(var1), (byte)var2);
   }

   @Override
   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort(this.addr(var1), var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE(this.addr(var1), var2);
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium(this.addr(var1), var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE(this.addr(var1), var2);
   }

   @Override
   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt(this.addr(var1), var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE(this.addr(var1), var2);
   }

   @Override
   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong(this.addr(var1), var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2);
      return this;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return UnsafeByteBufUtil.copy(this, this.addr(var1), var1, var2);
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
      return true;
   }

   @Override
   public long memoryAddress() {
      this.ensureAccessible();
      return this.memoryAddress;
   }

   private long addr(int var1) {
      return this.memoryAddress + (long)var1;
   }

   @Override
   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeDirectSwappedByteBuf(this) : super.newSwappedByteBuf());
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      this.checkIndex(var1, var2);
      UnsafeByteBufUtil.setZero(this.addr(var1), var2);
      return this;
   }

   @Override
   public ByteBuf writeZero(int var1) {
      this.ensureWritable(var1);
      int var2 = this.writerIndex;
      UnsafeByteBufUtil.setZero(this.addr(var2), var1);
      this.writerIndex = var2 + var1;
      return this;
   }
}
