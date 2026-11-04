package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf {
   private final ObjectPool.Handle<PooledByteBuf<T>> recyclerHandle;
   protected PoolChunk<T> chunk;
   protected long handle;
   protected T memory;
   protected int offset;
   protected int length;
   int maxLength;
   PoolThreadCache cache;
   ByteBuffer tmpNioBuf;
   private ByteBufAllocator allocator;

   protected PooledByteBuf(ObjectPool.Handle<? extends PooledByteBuf<T>> var1, int var2) {
      super(var2);
      this.recyclerHandle = var1;
   }

   void init(PoolChunk<T> var1, ByteBuffer var2, long var3, int var5, int var6, int var7, PoolThreadCache var8) {
      this.init0(var1, var2, var3, var5, var6, var7, var8);
   }

   void initUnpooled(PoolChunk<T> var1, int var2) {
      this.init0(var1, null, 0L, 0, var2, var2, null);
   }

   private void init0(PoolChunk<T> var1, ByteBuffer var2, long var3, int var5, int var6, int var7, PoolThreadCache var8) {
      assert var3 >= 0L;

      assert var1 != null;

      assert !PoolChunk.isSubpage(var3) || var1.arena.size2SizeIdx(var7) <= var1.arena.smallMaxSizeIdx : "Allocated small sub-page handle for a buffer size that isn't \"small.\"";

      var1.incrementPinnedMemory(var7);
      this.chunk = var1;
      this.memory = var1.memory;
      this.tmpNioBuf = var2;
      this.allocator = var1.arena.parent;
      this.cache = var8;
      this.handle = var3;
      this.offset = var5;
      this.length = var6;
      this.maxLength = var7;
   }

   final void reuse(int var1) {
      this.maxCapacity(var1);
      this.resetRefCnt();
      this.setIndex0(0, 0);
      this.discardMarks();
   }

   @Override
   public final int capacity() {
      return this.length;
   }

   @Override
   public int maxFastWritableBytes() {
      return Math.min(this.maxLength, this.maxCapacity()) - this.writerIndex;
   }

   @Override
   public final ByteBuf capacity(int var1) {
      if (var1 == this.length) {
         this.ensureAccessible();
         return this;
      } else {
         this.checkNewCapacity(var1);
         if (!this.chunk.unpooled) {
            if (var1 > this.length) {
               if (var1 <= this.maxLength) {
                  this.length = var1;
                  return this;
               }
            } else if (var1 > this.maxLength >>> 1 && (this.maxLength > 512 || var1 > this.maxLength - 16)) {
               this.length = var1;
               this.trimIndicesToCapacity(var1);
               return this;
            }
         }

         this.chunk.decrementPinnedMemory(this.maxLength);
         this.chunk.arena.reallocate(this, var1, true);
         return this;
      }
   }

   @Override
   public final ByteBufAllocator alloc() {
      return this.allocator;
   }

   @Override
   public final ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   @Override
   public final ByteBuf unwrap() {
      return null;
   }

   @Override
   public final ByteBuf retainedDuplicate() {
      return PooledDuplicatedByteBuf.newInstance(this, this, this.readerIndex(), this.writerIndex());
   }

   @Override
   public final ByteBuf retainedSlice() {
      int var1 = this.readerIndex();
      return this.retainedSlice(var1, this.writerIndex() - var1);
   }

   @Override
   public final ByteBuf retainedSlice(int var1, int var2) {
      return PooledSlicedByteBuf.newInstance(this, this, var1, var2);
   }

   protected final ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = this.newInternalNioBuffer(this.memory);
      } else {
         ((Buffer)var1).clear();
      }

      return var1;
   }

   protected abstract ByteBuffer newInternalNioBuffer(T var1);

   @Override
   protected final void deallocate() {
      if (this.handle >= 0L) {
         long var1 = this.handle;
         this.handle = -1L;
         this.memory = null;
         this.chunk.decrementPinnedMemory(this.maxLength);
         this.chunk.arena.free(this.chunk, this.tmpNioBuf, var1, this.maxLength, this.cache);
         this.tmpNioBuf = null;
         this.chunk = null;
         this.recycle();
      }
   }

   private void recycle() {
      this.recyclerHandle.recycle(this);
   }

   protected final int idx(int var1) {
      return this.offset + var1;
   }

   final ByteBuffer _internalNioBuffer(int var1, int var2, boolean var3) {
      var1 = this.idx(var1);
      ByteBuffer var4 = var3 ? this.newInternalNioBuffer(this.memory) : this.internalNioBuffer();
      ((Buffer)var4).limit(var1 + var2).position(var1);
      return var4;
   }

   ByteBuffer duplicateInternalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this._internalNioBuffer(var1, var2, true);
   }

   @Override
   public final ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this._internalNioBuffer(var1, var2, false);
   }

   @Override
   public final int nioBufferCount() {
      return 1;
   }

   @Override
   public final ByteBuffer nioBuffer(int var1, int var2) {
      return this.duplicateInternalNioBuffer(var1, var2).slice();
   }

   @Override
   public final ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   @Override
   public final boolean isContiguous() {
      return true;
   }

   @Override
   public final int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return var2.write(this.duplicateInternalNioBuffer(var1, var3));
   }

   @Override
   public final int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = var1.write(this._internalNioBuffer(this.readerIndex, var2, false));
      this.readerIndex += var3;
      return var3;
   }

   @Override
   public final int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return var2.write(this.duplicateInternalNioBuffer(var1, var5), var3);
   }

   @Override
   public final int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = var1.write(this._internalNioBuffer(this.readerIndex, var4, false), var2);
      this.readerIndex += var5;
      return var5;
   }

   @Override
   public final int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      try {
         return var2.read(this.internalNioBuffer(var1, var3));
      } catch (ClosedChannelException var5) {
         return -1;
      }
   }

   @Override
   public final int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      try {
         return var2.read(this.internalNioBuffer(var1, var5), var3);
      } catch (ClosedChannelException var7) {
         return -1;
      }
   }
}
