package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.Collections;

final class FixedCompositeByteBuf extends AbstractReferenceCountedByteBuf {
   private static final ByteBuf[] EMPTY = new ByteBuf[]{Unpooled.EMPTY_BUFFER};
   private final int nioBufferCount;
   private final int capacity;
   private final ByteBufAllocator allocator;
   private final ByteOrder order;
   private final ByteBuf[] buffers;
   private final boolean direct;

   FixedCompositeByteBuf(ByteBufAllocator var1, ByteBuf... var2) {
      super(Integer.MAX_VALUE);
      if (var2.length == 0) {
         this.buffers = EMPTY;
         this.order = ByteOrder.BIG_ENDIAN;
         this.nioBufferCount = 1;
         this.capacity = 0;
         this.direct = Unpooled.EMPTY_BUFFER.isDirect();
      } else {
         ByteBuf var3 = var2[0];
         this.buffers = var2;
         boolean var4 = true;
         int var5 = var3.nioBufferCount();
         int var6 = var3.readableBytes();
         this.order = var3.order();

         for (int var7 = 1; var7 < var2.length; var7++) {
            var3 = var2[var7];
            if (var2[var7].order() != this.order) {
               throw new IllegalArgumentException("All ByteBufs need to have same ByteOrder");
            }

            var5 += var3.nioBufferCount();
            var6 += var3.readableBytes();
            if (!var3.isDirect()) {
               var4 = false;
            }
         }

         this.nioBufferCount = var5;
         this.capacity = var6;
         this.direct = var4;
      }

      this.setIndex(0, this.capacity());
      this.allocator = var1;
   }

   @Override
   public boolean isWritable() {
      return false;
   }

   @Override
   public boolean isWritable(int var1) {
      return false;
   }

   @Override
   public ByteBuf discardReadBytes() {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int capacity() {
      return this.capacity;
   }

   @Override
   public int maxCapacity() {
      return this.capacity;
   }

   @Override
   public ByteBuf capacity(int var1) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.allocator;
   }

   @Override
   public ByteOrder order() {
      return this.order;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }

   @Override
   public boolean isDirect() {
      return this.direct;
   }

   private FixedCompositeByteBuf.Component findComponent(int var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < this.buffers.length; var3++) {
         FixedCompositeByteBuf.Component var4 = null;
         ByteBuf var5 = this.buffers[var3];
         if (var5 instanceof FixedCompositeByteBuf.Component) {
            var4 = (FixedCompositeByteBuf.Component)var5;
            var5 = var4.buf;
         }

         var2 += var5.readableBytes();
         if (var1 < var2) {
            if (var4 == null) {
               var4 = new FixedCompositeByteBuf.Component(var3, var2 - var5.readableBytes(), var5);
               this.buffers[var3] = var4;
            }

            return var4;
         }
      }

      throw new IllegalStateException();
   }

   private ByteBuf buffer(int var1) {
      ByteBuf var2 = this.buffers[var1];
      return var2 instanceof FixedCompositeByteBuf.Component ? ((FixedCompositeByteBuf.Component)var2).buf : var2;
   }

   @Override
   public byte getByte(int var1) {
      return this._getByte(var1);
   }

   @Override
   protected byte _getByte(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      return var2.buf.getByte(var1 - var2.offset);
   }

   @Override
   protected short _getShort(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShort(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255)
            : (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8);
      }
   }

   @Override
   protected short _getShortLE(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShortLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8)
            : (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255);
      }
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMedium(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(var1) & 65535) << 8 | this._getByte(var1 + 2) & 0xFF
            : this._getShort(var1) & 65535 | (this._getByte(var1 + 2) & 0xFF) << 16;
      }
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMediumLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(var1) & 65535 | (this._getByte(var1 + 2) & 0xFF) << 16
            : (this._getShortLE(var1) & 65535) << 8 | this._getByte(var1 + 2) & 0xFF;
      }
   }

   @Override
   protected int _getInt(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getInt(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(var1) & 65535) << 16 | this._getShort(var1 + 2) & 65535
            : this._getShort(var1) & 65535 | (this._getShort(var1 + 2) & 65535) << 16;
      }
   }

   @Override
   protected int _getIntLE(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getIntLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(var1) & 65535 | (this._getShortLE(var1 + 2) & 65535) << 16
            : (this._getShortLE(var1) & 65535) << 16 | this._getShortLE(var1 + 2) & 65535;
      }
   }

   @Override
   protected long _getLong(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLong(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? ((long)this._getInt(var1) & 4294967295L) << 32 | (long)this._getInt(var1 + 4) & 4294967295L
            : (long)this._getInt(var1) & 4294967295L | ((long)this._getInt(var1 + 4) & 4294967295L) << 32;
      }
   }

   @Override
   protected long _getLongLE(int var1) {
      FixedCompositeByteBuf.Component var2 = this.findComponent(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLongLE(var1 - var2.offset);
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (long)this._getIntLE(var1) & 4294967295L | ((long)this._getIntLE(var1 + 4) & 4294967295L) << 32
            : ((long)this._getIntLE(var1) & 4294967295L) << 32 | (long)this._getIntLE(var1 + 4) & 4294967295L;
      }
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      if (var4 == 0) {
         return this;
      } else {
         FixedCompositeByteBuf.Component var5 = this.findComponent(var1);
         int var6 = var5.index;
         int var7 = var5.offset;
         ByteBuf var8 = var5.buf;

         while (true) {
            int var9 = Math.min(var4, var8.readableBytes() - (var1 - var7));
            var8.getBytes(var1 - var7, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
            var7 += var8.readableBytes();
            if (var4 <= 0) {
               return this;
            }

            var8 = this.buffer(++var6);
         }
      }
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      int var3 = var2.limit();
      int var4 = var2.remaining();
      this.checkIndex(var1, var4);
      if (var4 == 0) {
         return this;
      } else {
         try {
            FixedCompositeByteBuf.Component var5 = this.findComponent(var1);
            int var6 = var5.index;
            int var7 = var5.offset;
            ByteBuf var8 = var5.buf;

            while (true) {
               int var9 = Math.min(var4, var8.readableBytes() - (var1 - var7));
               ((Buffer)var2).limit(var2.position() + var9);
               var8.getBytes(var1 - var7, var2);
               var1 += var9;
               var4 -= var9;
               var7 += var8.readableBytes();
               if (var4 <= 0) {
                  return this;
               }

               var8 = this.buffer(++var6);
            }
         } finally {
            ((Buffer)var2).limit(var3);
         }
      }
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var4 == 0) {
         return this;
      } else {
         FixedCompositeByteBuf.Component var5 = this.findComponent(var1);
         int var6 = var5.index;
         int var7 = var5.offset;
         ByteBuf var8 = var5.buf;

         while (true) {
            int var9 = Math.min(var4, var8.readableBytes() - (var1 - var7));
            var8.getBytes(var1 - var7, var2, var3, var9);
            var1 += var9;
            var3 += var9;
            var4 -= var9;
            var7 += var8.readableBytes();
            if (var4 <= 0) {
               return this;
            }

            var8 = this.buffer(++var6);
         }
      }
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      int var4 = this.nioBufferCount();
      if (var4 == 1) {
         return var2.write(this.internalNioBuffer(var1, var3));
      } else {
         long var5 = var2.write(this.nioBuffers(var1, var3));
         return var5 > 2147483647L ? Integer.MAX_VALUE : (int)var5;
      }
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      int var6 = this.nioBufferCount();
      if (var6 == 1) {
         return var2.write(this.internalNioBuffer(var1, var5), var3);
      } else {
         long var7 = 0L;

         for (ByteBuffer var12 : this.nioBuffers(var1, var5)) {
            var7 += (long)var2.write(var12, var3 + var7);
         }

         return var7 > 2147483647L ? Integer.MAX_VALUE : (int)var7;
      }
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return this;
      } else {
         FixedCompositeByteBuf.Component var4 = this.findComponent(var1);
         int var5 = var4.index;
         int var6 = var4.offset;
         ByteBuf var7 = var4.buf;

         while (true) {
            int var8 = Math.min(var3, var7.readableBytes() - (var1 - var6));
            var7.getBytes(var1 - var6, var2, var8);
            var1 += var8;
            var3 -= var8;
            var6 += var7.readableBytes();
            if (var3 <= 0) {
               return this;
            }

            var7 = this.buffer(++var5);
         }
      }
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      boolean var3 = true;
      ByteBuf var4 = this.alloc().buffer(var2);

      ByteBuf var5;
      try {
         var4.writeBytes(this, var1, var2);
         var3 = false;
         var5 = var4;
      } finally {
         if (var3) {
            var4.release();
         }
      }

      return var5;
   }

   @Override
   public int nioBufferCount() {
      return this.nioBufferCount;
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (this.buffers.length == 1) {
         ByteBuf var3 = this.buffer(0);
         if (var3.nioBufferCount() == 1) {
            return var3.nioBuffer(var1, var2);
         }
      }

      ByteBuffer var6 = ByteBuffer.allocate(var2).order(this.order());
      ByteBuffer[] var4 = this.nioBuffers(var1, var2);

      for (int var5 = 0; var5 < var4.length; var5++) {
         var6.put(var4[var5]);
      }

      ((Buffer)var6).flip();
      return var6;
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      if (this.buffers.length == 1) {
         return this.buffer(0).internalNioBuffer(var1, var2);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (var2 == 0) {
         return EmptyArrays.EMPTY_BYTE_BUFFERS;
      } else {
         RecyclableArrayList var3 = RecyclableArrayList.newInstance(this.buffers.length);

         try {
            FixedCompositeByteBuf.Component var4 = this.findComponent(var1);
            int var5 = var4.index;
            int var6 = var4.offset;
            ByteBuf var7 = var4.buf;

            while (true) {
               int var8 = Math.min(var2, var7.readableBytes() - (var1 - var6));
               switch (var7.nioBufferCount()) {
                  case 0:
                     throw new UnsupportedOperationException();
                  case 1:
                     var3.add(var7.nioBuffer(var1 - var6, var8));
                     break;
                  default:
                     Collections.addAll(var3, var7.nioBuffers(var1 - var6, var8));
               }

               var1 += var8;
               var2 -= var8;
               var6 += var7.readableBytes();
               if (var2 <= 0) {
                  return var3.toArray(new ByteBuffer[0]);
               }

               var7 = this.buffer(++var5);
            }
         } finally {
            var3.recycle();
         }
      }
   }

   @Override
   public boolean hasArray() {
      switch (this.buffers.length) {
         case 0:
            return true;
         case 1:
            return this.buffer(0).hasArray();
         default:
            return false;
      }
   }

   @Override
   public byte[] array() {
      switch (this.buffers.length) {
         case 0:
            return EmptyArrays.EMPTY_BYTES;
         case 1:
            return this.buffer(0).array();
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public int arrayOffset() {
      switch (this.buffers.length) {
         case 0:
            return 0;
         case 1:
            return this.buffer(0).arrayOffset();
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean hasMemoryAddress() {
      switch (this.buffers.length) {
         case 0:
            return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
         case 1:
            return this.buffer(0).hasMemoryAddress();
         default:
            return false;
      }
   }

   @Override
   public long memoryAddress() {
      switch (this.buffers.length) {
         case 0:
            return Unpooled.EMPTY_BUFFER.memoryAddress();
         case 1:
            return this.buffer(0).memoryAddress();
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   protected void deallocate() {
      for (int var1 = 0; var1 < this.buffers.length; var1++) {
         this.buffer(var1).release();
      }
   }

   @Override
   public String toString() {
      String var1 = super.toString();
      var1 = var1.substring(0, var1.length() - 1);
      return var1 + ", components=" + this.buffers.length + ')';
   }

   private static final class Component extends WrappedByteBuf {
      private final int index;
      private final int offset;
      private final int endOffset;

      Component(int var1, int var2, ByteBuf var3) {
         super(var3);
         this.index = var1;
         this.offset = var2;
         this.endOffset = var2 + var3.readableBytes();
      }
   }
}
