package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;

final class PooledUnsafeHeapByteBuf extends PooledHeapByteBuf {
   private static final ObjectPool<PooledUnsafeHeapByteBuf> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PooledUnsafeHeapByteBuf>() {
      public PooledUnsafeHeapByteBuf newObject(ObjectPool.Handle<PooledUnsafeHeapByteBuf> var1) {
         return new PooledUnsafeHeapByteBuf(var1, 0);
      }
   });

   static PooledUnsafeHeapByteBuf newUnsafeInstance(int var0) {
      PooledUnsafeHeapByteBuf var1 = RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   private PooledUnsafeHeapByteBuf(ObjectPool.Handle<PooledUnsafeHeapByteBuf> var1, int var2) {
      super(var1, var2);
   }

   @Override
   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.memory, this.idx(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.memory, this.idx(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE(this.memory, this.idx(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.memory, this.idx(var1));
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.memory, this.idx(var1));
   }

   @Override
   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.memory, this.idx(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE(this.memory, this.idx(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.memory, this.idx(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE(this.memory, this.idx(var1));
   }

   @Override
   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong(this.memory, this.idx(var1), var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE(this.memory, this.idx(var1), var2);
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.checkIndex(var1, var2);
         UnsafeByteBufUtil.setZero(this.memory, this.idx(var1), var2);
         return this;
      } else {
         return super.setZero(var1, var2);
      }
   }

   @Override
   public ByteBuf writeZero(int var1) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.ensureWritable(var1);
         int var2 = this.writerIndex;
         UnsafeByteBufUtil.setZero(this.memory, this.idx(var2), var1);
         this.writerIndex = var2 + var1;
         return this;
      } else {
         return super.writeZero(var1);
      }
   }

   @Deprecated
   @Override
   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeHeapSwappedByteBuf(this) : super.newSwappedByteBuf());
   }
}
