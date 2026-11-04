package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf implements Iterable<ByteBuf> {
   private static final ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
   private static final Iterator<ByteBuf> EMPTY_ITERATOR = Collections.<ByteBuf>emptyList().iterator();
   private final ByteBufAllocator alloc;
   private final boolean direct;
   private final int maxNumComponents;
   private int componentCount;
   private CompositeByteBuf.Component[] components;
   private boolean freed;
   static final CompositeByteBuf.ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER = new CompositeByteBuf.ByteWrapper<byte[]>() {
      public ByteBuf wrap(byte[] var1) {
         return Unpooled.wrappedBuffer(var1);
      }

      public boolean isEmpty(byte[] var1) {
         return var1.length == 0;
      }
   };
   static final CompositeByteBuf.ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER = new CompositeByteBuf.ByteWrapper<ByteBuffer>() {
      public ByteBuf wrap(ByteBuffer var1) {
         return Unpooled.wrappedBuffer(var1);
      }

      public boolean isEmpty(ByteBuffer var1) {
         return !var1.hasRemaining();
      }
   };
   private CompositeByteBuf.Component lastAccessed;

   private CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, int var4) {
      super(Integer.MAX_VALUE);
      this.alloc = ObjectUtil.checkNotNull(var1, "alloc");
      if (var3 < 1) {
         throw new IllegalArgumentException("maxNumComponents: " + var3 + " (expected: >= 1)");
      } else {
         this.direct = var2;
         this.maxNumComponents = var3;
         this.components = newCompArray(var4, var3);
      }
   }

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3) {
      this(var1, var2, var3, 0);
   }

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, ByteBuf... var4) {
      this(var1, var2, var3, var4, 0);
   }

   CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, ByteBuf[] var4, int var5) {
      this(var1, var2, var3, var4.length - var5);
      this.addComponents0(false, 0, var4, var5);
      this.consolidateIfNeeded();
      this.setIndex0(0, this.capacity());
   }

   public CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, Iterable<ByteBuf> var4) {
      this(var1, var2, var3, var4 instanceof Collection ? ((Collection)var4).size() : 0);
      this.addComponents(false, 0, var4);
      this.setIndex(0, this.capacity());
   }

   <T> CompositeByteBuf(ByteBufAllocator var1, boolean var2, int var3, CompositeByteBuf.ByteWrapper<T> var4, T[] var5, int var6) {
      this(var1, var2, var3, var5.length - var6);
      this.addComponents0(false, 0, var4, var5, var6);
      this.consolidateIfNeeded();
      this.setIndex(0, this.capacity());
   }

   private static CompositeByteBuf.Component[] newCompArray(int var0, int var1) {
      int var2 = Math.min(16, var1);
      return new CompositeByteBuf.Component[Math.max(var0, var2)];
   }

   CompositeByteBuf(ByteBufAllocator var1) {
      super(Integer.MAX_VALUE);
      this.alloc = var1;
      this.direct = false;
      this.maxNumComponents = 0;
      this.components = null;
   }

   public CompositeByteBuf addComponent(ByteBuf var1) {
      return this.addComponent(false, var1);
   }

   public CompositeByteBuf addComponents(ByteBuf... var1) {
      return this.addComponents(false, var1);
   }

   public CompositeByteBuf addComponents(Iterable<ByteBuf> var1) {
      return this.addComponents(false, var1);
   }

   public CompositeByteBuf addComponent(int var1, ByteBuf var2) {
      return this.addComponent(false, var1, var2);
   }

   public CompositeByteBuf addComponent(boolean var1, ByteBuf var2) {
      return this.addComponent(var1, this.componentCount, var2);
   }

   public CompositeByteBuf addComponents(boolean var1, ByteBuf... var2) {
      ObjectUtil.checkNotNull(var2, "buffers");
      this.addComponents0(var1, this.componentCount, var2, 0);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(boolean var1, Iterable<ByteBuf> var2) {
      return this.addComponents(var1, this.componentCount, var2);
   }

   public CompositeByteBuf addComponent(boolean var1, int var2, ByteBuf var3) {
      ObjectUtil.checkNotNull(var3, "buffer");
      this.addComponent0(var1, var2, var3);
      this.consolidateIfNeeded();
      return this;
   }

   private static void checkForOverflow(int var0, int var1) {
      if (var0 + var1 < 0) {
         throw new IllegalArgumentException("Can't increase by " + var1 + " as capacity(" + var0 + ") would overflow " + Integer.MAX_VALUE);
      }
   }

   private int addComponent0(boolean var1, int var2, ByteBuf var3) {
      assert var3 != null;

      boolean var4 = false;

      int var7;
      try {
         this.checkComponentIndex(var2);
         CompositeByteBuf.Component var5 = this.newComponent(ensureAccessible(var3), 0);
         int var6 = var5.length();
         checkForOverflow(this.capacity(), var6);
         this.addComp(var2, var5);
         var4 = true;
         if (var6 > 0 && var2 < this.componentCount - 1) {
            this.updateComponentOffsets(var2);
         } else if (var2 > 0) {
            var5.reposition(this.components[var2 - 1].endOffset);
         }

         if (var1) {
            this.writerIndex += var6;
         }

         var7 = var2;
      } finally {
         if (!var4) {
            var3.release();
         }
      }

      return var7;
   }

   private static ByteBuf ensureAccessible(ByteBuf var0) {
      if (checkAccessible && !var0.isAccessible()) {
         throw new IllegalReferenceCountException(0);
      } else {
         return var0;
      }
   }

   private CompositeByteBuf.Component newComponent(ByteBuf var1, int var2) {
      int var3 = var1.readerIndex();
      int var4 = var1.readableBytes();
      ByteBuf var5 = var1;
      int var6 = var3;

      while (var5 instanceof WrappedByteBuf || var5 instanceof SwappedByteBuf) {
         var5 = var5.unwrap();
      }

      if (var5 instanceof AbstractUnpooledSlicedByteBuf) {
         var6 = var3 + ((AbstractUnpooledSlicedByteBuf)var5).idx(0);
         var5 = var5.unwrap();
      } else if (var5 instanceof PooledSlicedByteBuf) {
         var6 = var3 + ((PooledSlicedByteBuf)var5).adjustment;
         var5 = var5.unwrap();
      } else if (var5 instanceof DuplicatedByteBuf || var5 instanceof PooledDuplicatedByteBuf) {
         var5 = var5.unwrap();
      }

      ByteBuf var7 = var1.capacity() == var4 ? var1 : null;
      return new CompositeByteBuf.Component(var1.order(ByteOrder.BIG_ENDIAN), var3, var5.order(ByteOrder.BIG_ENDIAN), var6, var2, var4, var7);
   }

   public CompositeByteBuf addComponents(int var1, ByteBuf... var2) {
      ObjectUtil.checkNotNull(var2, "buffers");
      this.addComponents0(false, var1, var2, 0);
      this.consolidateIfNeeded();
      return this;
   }

   private CompositeByteBuf addComponents0(boolean var1, int var2, ByteBuf[] var3, int var4) {
      int var5 = var3.length;
      int var6 = var5 - var4;
      int var7 = 0;
      int var8 = this.capacity();

      for (int var9 = var4; var9 < var3.length; var9++) {
         ByteBuf var10 = var3[var9];
         if (var10 == null) {
            break;
         }

         var7 += var10.readableBytes();
         checkForOverflow(var8, var7);
      }

      int var16 = Integer.MAX_VALUE;

      CompositeByteBuf var18;
      try {
         this.checkComponentIndex(var2);
         this.shiftComps(var2, var6);
         int var17 = var2 > 0 ? this.components[var2 - 1].endOffset : 0;

         for (var16 = var2; var4 < var5; var16++) {
            ByteBuf var11 = var3[var4];
            if (var11 == null) {
               break;
            }

            CompositeByteBuf.Component var12 = this.newComponent(ensureAccessible(var11), var17);
            this.components[var16] = var12;
            var17 = var12.endOffset;
            var4++;
         }

         var18 = this;
      } finally {
         if (var16 < this.componentCount) {
            if (var16 < var2 + var6) {
               this.removeCompRange(var16, var2 + var6);

               while (var4 < var5) {
                  ReferenceCountUtil.safeRelease(var3[var4]);
                  var4++;
               }
            }

            this.updateComponentOffsets(var16);
         }

         if (var1 && var16 > var2 && var16 <= this.componentCount) {
            this.writerIndex = this.writerIndex + (this.components[var16 - 1].endOffset - this.components[var2].offset);
         }
      }

      return var18;
   }

   private <T> int addComponents0(boolean var1, int var2, CompositeByteBuf.ByteWrapper<T> var3, T[] var4, int var5) {
      this.checkComponentIndex(var2);
      int var6 = var5;

      for (int var7 = var4.length; var6 < var7; var6++) {
         Object var8 = var4[var6];
         if (var8 == null) {
            break;
         }

         if (!var3.isEmpty(var8)) {
            var2 = this.addComponent0(var1, var2, var3.wrap(var8)) + 1;
            int var9 = this.componentCount;
            if (var2 > var9) {
               var2 = var9;
            }
         }
      }

      return var2;
   }

   public CompositeByteBuf addComponents(int var1, Iterable<ByteBuf> var2) {
      return this.addComponents(false, var1, var2);
   }

   public CompositeByteBuf addFlattenedComponents(boolean var1, ByteBuf var2) {
      ObjectUtil.checkNotNull(var2, "buffer");
      int var3 = var2.readerIndex();
      int var4 = var2.writerIndex();
      if (var3 == var4) {
         var2.release();
         return this;
      } else if (!(var2 instanceof CompositeByteBuf)) {
         this.addComponent0(var1, this.componentCount, var2);
         this.consolidateIfNeeded();
         return this;
      } else {
         CompositeByteBuf var5;
         if (var2 instanceof WrappedCompositeByteBuf) {
            var5 = (CompositeByteBuf)var2.unwrap();
         } else {
            var5 = (CompositeByteBuf)var2;
         }

         var5.checkIndex(var3, var4 - var3);
         CompositeByteBuf.Component[] var6 = var5.components;
         int var7 = this.componentCount;
         int var8 = this.writerIndex;
         boolean var19 = false /* VF: Semaphore variable */;

         CompositeByteBuf var21;
         try {
            var19 = true;
            int var9 = var5.toComponentIndex0(var3);
            int var10 = this.capacity();

            while (true) {
               CompositeByteBuf.Component var11 = var6[var9];
               int var12 = var11.offset;
               int var13 = Math.max(var3, var12);
               int var14 = Math.min(var4, var11.endOffset);
               int var15 = var14 - var13;
               if (var15 > 0) {
                  this.addComp(
                     this.componentCount,
                     new CompositeByteBuf.Component(var11.srcBuf.retain(), var11.srcIdx(var13), var11.buf, var11.idx(var13), var10, var15, null)
                  );
               }

               if (var4 == var14) {
                  if (var1) {
                     this.writerIndex = var8 + (var4 - var3);
                  }

                  this.consolidateIfNeeded();
                  var2.release();
                  var2 = null;
                  var21 = this;
                  var19 = false;
                  break;
               }

               var10 += var15;
               var9++;
            }
         } finally {
            if (var19) {
               if (var2 != null) {
                  if (var1) {
                     this.writerIndex = var8;
                  }

                  for (int var17 = this.componentCount - 1; var17 >= var7; var17--) {
                     this.components[var17].free();
                     this.removeComp(var17);
                  }
               }
            }
         }

         if (var2 != null) {
            if (var1) {
               this.writerIndex = var8;
            }

            for (int var22 = this.componentCount - 1; var22 >= var7; var22--) {
               this.components[var22].free();
               this.removeComp(var22);
            }
         }

         return var21;
      }
   }

   private CompositeByteBuf addComponents(boolean var1, int var2, Iterable<ByteBuf> var3) {
      if (var3 instanceof ByteBuf) {
         return this.addComponent(var1, var2, (ByteBuf)var3);
      } else {
         ObjectUtil.checkNotNull(var3, "buffers");
         Iterator var4 = var3.iterator();

         try {
            this.checkComponentIndex(var2);

            while (var4.hasNext()) {
               ByteBuf var5 = (ByteBuf)var4.next();
               if (var5 == null) {
                  break;
               }

               var2 = this.addComponent0(var1, var2, var5) + 1;
               var2 = Math.min(var2, this.componentCount);
            }
         } finally {
            while (var4.hasNext()) {
               ReferenceCountUtil.safeRelease(var4.next());
            }
         }

         this.consolidateIfNeeded();
         return this;
      }
   }

   private void consolidateIfNeeded() {
      int var1 = this.componentCount;
      if (var1 > this.maxNumComponents) {
         this.consolidate0(0, var1);
      }
   }

   private void checkComponentIndex(int var1) {
      this.ensureAccessible();
      if (var1 < 0 || var1 > this.componentCount) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", var1, this.componentCount));
      }
   }

   private void checkComponentIndex(int var1, int var2) {
      this.ensureAccessible();
      if (var1 < 0 || var1 + var2 > this.componentCount) {
         throw new IndexOutOfBoundsException(
            String.format(
               "cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", var1, var2, this.componentCount
            )
         );
      }
   }

   private void updateComponentOffsets(int var1) {
      int var2 = this.componentCount;
      if (var2 > var1) {
         for (int var3 = var1 > 0 ? this.components[var1 - 1].endOffset : 0; var1 < var2; var1++) {
            CompositeByteBuf.Component var4 = this.components[var1];
            var4.reposition(var3);
            var3 = var4.endOffset;
         }
      }
   }

   public CompositeByteBuf removeComponent(int var1) {
      this.checkComponentIndex(var1);
      CompositeByteBuf.Component var2 = this.components[var1];
      if (this.lastAccessed == var2) {
         this.lastAccessed = null;
      }

      var2.free();
      this.removeComp(var1);
      if (var2.length() > 0) {
         this.updateComponentOffsets(var1);
      }

      return this;
   }

   public CompositeByteBuf removeComponents(int var1, int var2) {
      this.checkComponentIndex(var1, var2);
      if (var2 == 0) {
         return this;
      } else {
         int var3 = var1 + var2;
         boolean var4 = false;

         for (int var5 = var1; var5 < var3; var5++) {
            CompositeByteBuf.Component var6 = this.components[var5];
            if (var6.length() > 0) {
               var4 = true;
            }

            if (this.lastAccessed == var6) {
               this.lastAccessed = null;
            }

            var6.free();
         }

         this.removeCompRange(var1, var3);
         if (var4) {
            this.updateComponentOffsets(var1);
         }

         return this;
      }
   }

   @Override
   public Iterator<ByteBuf> iterator() {
      this.ensureAccessible();
      return (Iterator<ByteBuf>)(this.componentCount == 0 ? EMPTY_ITERATOR : new CompositeByteBuf.CompositeByteBufIterator());
   }

   @Override
   protected int forEachByteAsc0(int var1, int var2, ByteProcessor var3) throws Exception {
      if (var2 <= var1) {
         return -1;
      } else {
         int var4 = this.toComponentIndex0(var1);

         for (int var5 = var2 - var1; var5 > 0; var4++) {
            CompositeByteBuf.Component var6 = this.components[var4];
            if (var6.offset != var6.endOffset) {
               ByteBuf var7 = var6.buf;
               int var8 = var6.idx(var1);
               int var9 = Math.min(var5, var6.endOffset - var1);
               int var10 = var7 instanceof AbstractByteBuf
                  ? ((AbstractByteBuf)var7).forEachByteAsc0(var8, var8 + var9, var3)
                  : var7.forEachByte(var8, var9, var3);
               if (var10 != -1) {
                  return var10 - var6.adjustment;
               }

               var1 += var9;
               var5 -= var9;
            }
         }

         return -1;
      }
   }

   @Override
   protected int forEachByteDesc0(int var1, int var2, ByteProcessor var3) throws Exception {
      if (var2 > var1) {
         return -1;
      } else {
         int var4 = this.toComponentIndex0(var1);

         for (int var5 = 1 + var1 - var2; var5 > 0; var4--) {
            CompositeByteBuf.Component var6 = this.components[var4];
            if (var6.offset != var6.endOffset) {
               ByteBuf var7 = var6.buf;
               int var8 = var6.idx(var5 + var2);
               int var9 = Math.min(var5, var8);
               int var10 = var8 - var9;
               int var11 = var7 instanceof AbstractByteBuf
                  ? ((AbstractByteBuf)var7).forEachByteDesc0(var8 - 1, var10, var3)
                  : var7.forEachByteDesc(var10, var9, var3);
               if (var11 != -1) {
                  return var11 - var6.adjustment;
               }

               var5 -= var9;
            }
         }

         return -1;
      }
   }

   public List<ByteBuf> decompose(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (var2 == 0) {
         return Collections.emptyList();
      } else {
         int var3 = this.toComponentIndex0(var1);
         CompositeByteBuf.Component var5 = this.components[var3];
         ByteBuf var6 = var5.srcBuf.slice(var5.srcIdx(var1), Math.min(var5.endOffset - var1, var2));
         int var4 = var2 - var6.readableBytes();
         if (var4 == 0) {
            return Collections.singletonList(var6);
         } else {
            ArrayList var7 = new ArrayList(this.componentCount - var3);
            var7.add(var6);

            do {
               CompositeByteBuf.Component var8 = this.components[++var3];
               var6 = var8.srcBuf.slice(var8.srcIdx(var8.offset), Math.min(var8.length(), var4));
               var4 -= var6.readableBytes();
               var7.add(var6);
            } while (var4 > 0);

            return var7;
         }
      }
   }

   @Override
   public boolean isDirect() {
      int var1 = this.componentCount;
      if (var1 == 0) {
         return false;
      } else {
         for (int var2 = 0; var2 < var1; var2++) {
            if (!this.components[var2].buf.isDirect()) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean hasArray() {
      switch (this.componentCount) {
         case 0:
            return true;
         case 1:
            return this.components[0].buf.hasArray();
         default:
            return false;
      }
   }

   @Override
   public byte[] array() {
      switch (this.componentCount) {
         case 0:
            return EmptyArrays.EMPTY_BYTES;
         case 1:
            return this.components[0].buf.array();
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public int arrayOffset() {
      switch (this.componentCount) {
         case 0:
            return 0;
         case 1:
            CompositeByteBuf.Component var1 = this.components[0];
            return var1.idx(var1.buf.arrayOffset());
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean hasMemoryAddress() {
      switch (this.componentCount) {
         case 0:
            return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
         case 1:
            return this.components[0].buf.hasMemoryAddress();
         default:
            return false;
      }
   }

   @Override
   public long memoryAddress() {
      switch (this.componentCount) {
         case 0:
            return Unpooled.EMPTY_BUFFER.memoryAddress();
         case 1:
            CompositeByteBuf.Component var1 = this.components[0];
            return var1.buf.memoryAddress() + (long)var1.adjustment;
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public int capacity() {
      int var1 = this.componentCount;
      return var1 > 0 ? this.components[var1 - 1].endOffset : 0;
   }

   public CompositeByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.componentCount;
      int var3 = this.capacity();
      if (var1 > var3) {
         int var4 = var1 - var3;
         ByteBuf var5 = this.allocBuffer(var4).setIndex(0, var4);
         this.addComponent0(false, var2, var5);
         if (this.componentCount >= this.maxNumComponents) {
            this.consolidateIfNeeded();
         }
      } else if (var1 < var3) {
         this.lastAccessed = null;
         int var9 = var2 - 1;

         for (int var10 = var3 - var1; var9 >= 0; var9--) {
            CompositeByteBuf.Component var6 = this.components[var9];
            int var7 = var6.length();
            if (var10 < var7) {
               var6.endOffset -= var10;
               ByteBuf var8 = var6.slice;
               if (var8 != null) {
                  var6.slice = var8.slice(0, var6.length());
               }
               break;
            }

            var6.free();
            var10 -= var7;
         }

         this.removeCompRange(var9 + 1, var2);
         if (this.readerIndex() > var1) {
            this.setIndex0(var1, var1);
         } else if (this.writerIndex > var1) {
            this.writerIndex = var1;
         }
      }

      return this;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   @Override
   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public int numComponents() {
      return this.componentCount;
   }

   public int maxNumComponents() {
      return this.maxNumComponents;
   }

   public int toComponentIndex(int var1) {
      this.checkIndex(var1);
      return this.toComponentIndex0(var1);
   }

   private int toComponentIndex0(int var1) {
      int var2 = this.componentCount;
      if (var1 == 0) {
         for (int var3 = 0; var3 < var2; var3++) {
            if (this.components[var3].endOffset > 0) {
               return var3;
            }
         }
      }

      if (var2 > 2) {
         int var7 = 0;
         int var4 = var2;

         while (var7 <= var4) {
            int var5 = var7 + var4 >>> 1;
            CompositeByteBuf.Component var6 = this.components[var5];
            if (var1 >= var6.endOffset) {
               var7 = var5 + 1;
            } else {
               if (var1 >= var6.offset) {
                  return var5;
               }

               var4 = var5 - 1;
            }
         }

         throw new Error("should not reach here");
      } else {
         return var2 != 1 && var1 >= this.components[0].endOffset ? 1 : 0;
      }
   }

   public int toByteIndex(int var1) {
      this.checkComponentIndex(var1);
      return this.components[var1].offset;
   }

   @Override
   public byte getByte(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent(var1);
      return var2.buf.getByte(var2.idx(var1));
   }

   @Override
   protected byte _getByte(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      return var2.buf.getByte(var2.idx(var1));
   }

   @Override
   protected short _getShort(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShort(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255)
            : (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8);
      }
   }

   @Override
   protected short _getShortLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 2 <= var2.endOffset) {
         return var2.buf.getShortLE(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (short)(this._getByte(var1) & 255 | (this._getByte(var1 + 1) & 255) << 8)
            : (short)((this._getByte(var1) & 255) << 8 | this._getByte(var1 + 1) & 255);
      }
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMedium(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(var1) & 65535) << 8 | this._getByte(var1 + 2) & 0xFF
            : this._getShort(var1) & 65535 | (this._getByte(var1 + 2) & 0xFF) << 16;
      }
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 3 <= var2.endOffset) {
         return var2.buf.getUnsignedMediumLE(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(var1) & 65535 | (this._getByte(var1 + 2) & 0xFF) << 16
            : (this._getShortLE(var1) & 65535) << 8 | this._getByte(var1 + 2) & 0xFF;
      }
   }

   @Override
   protected int _getInt(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getInt(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (this._getShort(var1) & 65535) << 16 | this._getShort(var1 + 2) & 65535
            : this._getShort(var1) & 65535 | (this._getShort(var1 + 2) & 65535) << 16;
      }
   }

   @Override
   protected int _getIntLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 4 <= var2.endOffset) {
         return var2.buf.getIntLE(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? this._getShortLE(var1) & 65535 | (this._getShortLE(var1 + 2) & 65535) << 16
            : (this._getShortLE(var1) & 65535) << 16 | this._getShortLE(var1 + 2) & 65535;
      }
   }

   @Override
   protected long _getLong(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLong(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? ((long)this._getInt(var1) & 4294967295L) << 32 | (long)this._getInt(var1 + 4) & 4294967295L
            : (long)this._getInt(var1) & 4294967295L | ((long)this._getInt(var1 + 4) & 4294967295L) << 32;
      }
   }

   @Override
   protected long _getLongLE(int var1) {
      CompositeByteBuf.Component var2 = this.findComponent0(var1);
      if (var1 + 8 <= var2.endOffset) {
         return var2.buf.getLongLE(var2.idx(var1));
      } else {
         return this.order() == ByteOrder.BIG_ENDIAN
            ? (long)this._getIntLE(var1) & 4294967295L | ((long)this._getIntLE(var1 + 4) & 4294967295L) << 32
            : ((long)this._getIntLE(var1) & 4294967295L) << 32 | (long)this._getIntLE(var1 + 4) & 4294967295L;
      }
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      if (var4 == 0) {
         return this;
      } else {
         for (int var5 = this.toComponentIndex0(var1); var4 > 0; var5++) {
            CompositeByteBuf.Component var6 = this.components[var5];
            int var7 = Math.min(var4, var6.endOffset - var1);
            var6.buf.getBytes(var6.idx(var1), var2, var3, var7);
            var1 += var7;
            var3 += var7;
            var4 -= var7;
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int var1, ByteBuffer var2) {
      int var3 = var2.limit();
      int var4 = var2.remaining();
      this.checkIndex(var1, var4);
      if (var4 == 0) {
         return this;
      } else {
         int var5 = this.toComponentIndex0(var1);

         try {
            while (var4 > 0) {
               CompositeByteBuf.Component var6 = this.components[var5];
               int var7 = Math.min(var4, var6.endOffset - var1);
               ((Buffer)var2).limit(var2.position() + var7);
               var6.buf.getBytes(var6.idx(var1), var2);
               var1 += var7;
               var4 -= var7;
               var5++;
            }
         } finally {
            ((Buffer)var2).limit(var3);
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var4 == 0) {
         return this;
      } else {
         for (int var5 = this.toComponentIndex0(var1); var4 > 0; var5++) {
            CompositeByteBuf.Component var6 = this.components[var5];
            int var7 = Math.min(var4, var6.endOffset - var1);
            var6.buf.getBytes(var6.idx(var1), var2, var3, var7);
            var1 += var7;
            var3 += var7;
            var4 -= var7;
         }

         return this;
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

   public CompositeByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return this;
      } else {
         for (int var4 = this.toComponentIndex0(var1); var3 > 0; var4++) {
            CompositeByteBuf.Component var5 = this.components[var4];
            int var6 = Math.min(var3, var5.endOffset - var1);
            var5.buf.getBytes(var5.idx(var1), var2, var6);
            var1 += var6;
            var3 -= var6;
         }

         return this;
      }
   }

   public CompositeByteBuf setByte(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent(var1);
      var3.buf.setByte(var3.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      var3.buf.setByte(var3.idx(var1), var2);
   }

   public CompositeByteBuf setShort(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShort(var1, var2);
      return this;
   }

   @Override
   protected void _setShort(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 2 <= var3.endOffset) {
         var3.buf.setShort(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(var1, (byte)(var2 >>> 8));
         this._setByte(var1 + 1, (byte)var2);
      } else {
         this._setByte(var1, (byte)var2);
         this._setByte(var1 + 1, (byte)(var2 >>> 8));
      }
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 2 <= var3.endOffset) {
         var3.buf.setShortLE(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(var1, (byte)var2);
         this._setByte(var1 + 1, (byte)(var2 >>> 8));
      } else {
         this._setByte(var1, (byte)(var2 >>> 8));
         this._setByte(var1 + 1, (byte)var2);
      }
   }

   public CompositeByteBuf setMedium(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMedium(var1, var2);
      return this;
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 3 <= var3.endOffset) {
         var3.buf.setMedium(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(var1, (short)(var2 >> 8));
         this._setByte(var1 + 2, (byte)var2);
      } else {
         this._setShort(var1, (short)var2);
         this._setByte(var1 + 2, (byte)(var2 >>> 16));
      }
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 3 <= var3.endOffset) {
         var3.buf.setMediumLE(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(var1, (short)var2);
         this._setByte(var1 + 2, (byte)(var2 >>> 16));
      } else {
         this._setShortLE(var1, (short)(var2 >> 8));
         this._setByte(var1 + 2, (byte)var2);
      }
   }

   public CompositeByteBuf setInt(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setInt(var1, var2);
      return this;
   }

   @Override
   protected void _setInt(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 4 <= var3.endOffset) {
         var3.buf.setInt(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(var1, (short)(var2 >>> 16));
         this._setShort(var1 + 2, (short)var2);
      } else {
         this._setShort(var1, (short)var2);
         this._setShort(var1 + 2, (short)(var2 >>> 16));
      }
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      CompositeByteBuf.Component var3 = this.findComponent0(var1);
      if (var1 + 4 <= var3.endOffset) {
         var3.buf.setIntLE(var3.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShortLE(var1, (short)var2);
         this._setShortLE(var1 + 2, (short)(var2 >>> 16));
      } else {
         this._setShortLE(var1, (short)(var2 >>> 16));
         this._setShortLE(var1 + 2, (short)var2);
      }
   }

   public CompositeByteBuf setLong(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLong(var1, var2);
      return this;
   }

   @Override
   protected void _setLong(int var1, long var2) {
      CompositeByteBuf.Component var4 = this.findComponent0(var1);
      if (var1 + 8 <= var4.endOffset) {
         var4.buf.setLong(var4.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setInt(var1, (int)(var2 >>> 32));
         this._setInt(var1 + 4, (int)var2);
      } else {
         this._setInt(var1, (int)var2);
         this._setInt(var1 + 4, (int)(var2 >>> 32));
      }
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      CompositeByteBuf.Component var4 = this.findComponent0(var1);
      if (var1 + 8 <= var4.endOffset) {
         var4.buf.setLongLE(var4.idx(var1), var2);
      } else if (this.order() == ByteOrder.BIG_ENDIAN) {
         this._setIntLE(var1, (int)var2);
         this._setIntLE(var1 + 4, (int)(var2 >>> 32));
      } else {
         this._setIntLE(var1, (int)(var2 >>> 32));
         this._setIntLE(var1 + 4, (int)var2);
      }
   }

   public CompositeByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      if (var4 == 0) {
         return this;
      } else {
         for (int var5 = this.toComponentIndex0(var1); var4 > 0; var5++) {
            CompositeByteBuf.Component var6 = this.components[var5];
            int var7 = Math.min(var4, var6.endOffset - var1);
            var6.buf.setBytes(var6.idx(var1), var2, var3, var7);
            var1 += var7;
            var3 += var7;
            var4 -= var7;
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int var1, ByteBuffer var2) {
      int var3 = var2.limit();
      int var4 = var2.remaining();
      this.checkIndex(var1, var4);
      if (var4 == 0) {
         return this;
      } else {
         int var5 = this.toComponentIndex0(var1);

         try {
            while (var4 > 0) {
               CompositeByteBuf.Component var6 = this.components[var5];
               int var7 = Math.min(var4, var6.endOffset - var1);
               ((Buffer)var2).limit(var2.position() + var7);
               var6.buf.setBytes(var6.idx(var1), var2);
               var1 += var7;
               var4 -= var7;
               var5++;
            }
         } finally {
            ((Buffer)var2).limit(var3);
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var4 == 0) {
         return this;
      } else {
         for (int var5 = this.toComponentIndex0(var1); var4 > 0; var5++) {
            CompositeByteBuf.Component var6 = this.components[var5];
            int var7 = Math.min(var4, var6.endOffset - var1);
            var6.buf.setBytes(var6.idx(var1), var2, var3, var7);
            var1 += var7;
            var3 += var7;
            var4 -= var7;
         }

         return this;
      }
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return var2.read(EmptyArrays.EMPTY_BYTES);
      } else {
         int var4 = this.toComponentIndex0(var1);
         int var5 = 0;

         do {
            CompositeByteBuf.Component var6 = this.components[var4];
            int var7 = Math.min(var3, var6.endOffset - var1);
            if (var7 == 0) {
               var4++;
            } else {
               int var8 = var6.buf.setBytes(var6.idx(var1), var2, var7);
               if (var8 < 0) {
                  if (var5 == 0) {
                     return -1;
                  }
                  break;
               }

               var1 += var8;
               var3 -= var8;
               var5 += var8;
               if (var8 == var7) {
                  var4++;
               }
            }
         } while (var3 > 0);

         return var5;
      }
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      if (var3 == 0) {
         return var2.read(EMPTY_NIO_BUFFER);
      } else {
         int var4 = this.toComponentIndex0(var1);
         int var5 = 0;

         do {
            CompositeByteBuf.Component var6 = this.components[var4];
            int var7 = Math.min(var3, var6.endOffset - var1);
            if (var7 == 0) {
               var4++;
            } else {
               int var8 = var6.buf.setBytes(var6.idx(var1), var2, var7);
               if (var8 == 0) {
                  break;
               }

               if (var8 < 0) {
                  if (var5 == 0) {
                     return -1;
                  }
                  break;
               }

               var1 += var8;
               var3 -= var8;
               var5 += var8;
               if (var8 == var7) {
                  var4++;
               }
            }
         } while (var3 > 0);

         return var5;
      }
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex(var1, var5);
      if (var5 == 0) {
         return var2.read(EMPTY_NIO_BUFFER, var3);
      } else {
         int var6 = this.toComponentIndex0(var1);
         int var7 = 0;

         do {
            CompositeByteBuf.Component var8 = this.components[var6];
            int var9 = Math.min(var5, var8.endOffset - var1);
            if (var9 == 0) {
               var6++;
            } else {
               int var10 = var8.buf.setBytes(var8.idx(var1), var2, var3 + (long)var7, var9);
               if (var10 == 0) {
                  break;
               }

               if (var10 < 0) {
                  if (var7 == 0) {
                     return -1;
                  }
                  break;
               }

               var1 += var10;
               var5 -= var10;
               var7 += var10;
               if (var10 == var9) {
                  var6++;
               }
            }
         } while (var5 > 0);

         return var7;
      }
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.allocBuffer(var2);
      if (var2 != 0) {
         this.copyTo(var1, var2, this.toComponentIndex0(var1), var3);
      }

      return var3;
   }

   private void copyTo(int var1, int var2, int var3, ByteBuf var4) {
      int var5 = 0;

      for (int var6 = var3; var2 > 0; var6++) {
         CompositeByteBuf.Component var7 = this.components[var6];
         int var8 = Math.min(var2, var7.endOffset - var1);
         var7.buf.getBytes(var7.idx(var1), var4, var5, var8);
         var1 += var8;
         var5 += var8;
         var2 -= var8;
      }

      var4.writerIndex(var4.capacity());
   }

   public ByteBuf component(int var1) {
      this.checkComponentIndex(var1);
      return this.components[var1].duplicate();
   }

   public ByteBuf componentAtOffset(int var1) {
      return this.findComponent(var1).duplicate();
   }

   public ByteBuf internalComponent(int var1) {
      this.checkComponentIndex(var1);
      return this.components[var1].slice();
   }

   public ByteBuf internalComponentAtOffset(int var1) {
      return this.findComponent(var1).slice();
   }

   private CompositeByteBuf.Component findComponent(int var1) {
      CompositeByteBuf.Component var2 = this.lastAccessed;
      if (var2 != null && var1 >= var2.offset && var1 < var2.endOffset) {
         this.ensureAccessible();
         return var2;
      } else {
         this.checkIndex(var1);
         return this.findIt(var1);
      }
   }

   private CompositeByteBuf.Component findComponent0(int var1) {
      CompositeByteBuf.Component var2 = this.lastAccessed;
      return var2 != null && var1 >= var2.offset && var1 < var2.endOffset ? var2 : this.findIt(var1);
   }

   private CompositeByteBuf.Component findIt(int var1) {
      int var2 = 0;
      int var3 = this.componentCount;

      while (var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         CompositeByteBuf.Component var5 = this.components[var4];
         if (var5 == null) {
            throw new IllegalStateException("No component found for offset. Composite buffer layout might be outdated, e.g. from a discardReadBytes call.");
         }

         if (var1 >= var5.endOffset) {
            var2 = var4 + 1;
         } else {
            if (var1 >= var5.offset) {
               this.lastAccessed = var5;
               return var5;
            }

            var3 = var4 - 1;
         }
      }

      throw new Error("should not reach here");
   }

   @Override
   public int nioBufferCount() {
      int var1 = this.componentCount;
      switch (var1) {
         case 0:
            return 1;
         case 1:
            return this.components[0].buf.nioBufferCount();
         default:
            int var2 = 0;

            for (int var3 = 0; var3 < var1; var3++) {
               var2 += this.components[var3].buf.nioBufferCount();
            }

            return var2;
      }
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      switch (this.componentCount) {
         case 0:
            return EMPTY_NIO_BUFFER;
         case 1:
            return this.components[0].internalNioBuffer(var1, var2);
         default:
            throw new UnsupportedOperationException();
      }
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      switch (this.componentCount) {
         case 0:
            return EMPTY_NIO_BUFFER;
         case 1:
            CompositeByteBuf.Component var3 = this.components[0];
            ByteBuf var4 = var3.buf;
            if (var4.nioBufferCount() == 1) {
               return var4.nioBuffer(var3.idx(var1), var2);
            }
         default:
            ByteBuffer[] var9 = this.nioBuffers(var1, var2);
            if (var9.length == 1) {
               return var9[0];
            } else {
               ByteBuffer var10 = ByteBuffer.allocate(var2).order(this.order());

               for (ByteBuffer var8 : var9) {
                  var10.put(var8);
               }

               ((Buffer)var10).flip();
               return var10;
            }
      }
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex(var1, var2);
      if (var2 == 0) {
         return new ByteBuffer[]{EMPTY_NIO_BUFFER};
      } else {
         RecyclableArrayList var3 = RecyclableArrayList.newInstance(this.componentCount);

         ByteBuffer[] var11;
         try {
            for (int var4 = this.toComponentIndex0(var1); var2 > 0; var4++) {
               CompositeByteBuf.Component var5 = this.components[var4];
               ByteBuf var6 = var5.buf;
               int var7 = Math.min(var2, var5.endOffset - var1);
               switch (var6.nioBufferCount()) {
                  case 0:
                     throw new UnsupportedOperationException();
                  case 1:
                     var3.add(var6.nioBuffer(var5.idx(var1), var7));
                     break;
                  default:
                     Collections.addAll(var3, var6.nioBuffers(var5.idx(var1), var7));
               }

               var1 += var7;
               var2 -= var7;
            }

            var11 = var3.toArray(new ByteBuffer[0]);
         } finally {
            var3.recycle();
         }

         return var11;
      }
   }

   public CompositeByteBuf consolidate() {
      this.ensureAccessible();
      this.consolidate0(0, this.componentCount);
      return this;
   }

   public CompositeByteBuf consolidate(int var1, int var2) {
      this.checkComponentIndex(var1, var2);
      this.consolidate0(var1, var2);
      return this;
   }

   private void consolidate0(int var1, int var2) {
      if (var2 > 1) {
         int var3 = var1 + var2;
         int var4 = var1 != 0 ? this.components[var1].offset : 0;
         int var5 = this.components[var3 - 1].endOffset - var4;
         ByteBuf var6 = this.allocBuffer(var5);

         for (int var7 = var1; var7 < var3; var7++) {
            this.components[var7].transferTo(var6);
         }

         this.lastAccessed = null;
         this.removeCompRange(var1 + 1, var3);
         this.components[var1] = this.newComponent(var6, 0);
         if (var1 != 0 || var2 != this.componentCount) {
            this.updateComponentOffsets(var1);
         }
      }
   }

   public CompositeByteBuf discardReadComponents() {
      this.ensureAccessible();
      int var1 = this.readerIndex();
      if (var1 == 0) {
         return this;
      } else {
         int var2 = this.writerIndex();
         if (var1 == var2 && var2 == this.capacity()) {
            int var7 = 0;

            for (int var8 = this.componentCount; var7 < var8; var7++) {
               this.components[var7].free();
            }

            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(var1);
            return this;
         } else {
            int var3 = 0;
            CompositeByteBuf.Component var4 = null;

            for (int var5 = this.componentCount; var3 < var5; var3++) {
               var4 = this.components[var3];
               if (var4.endOffset > var1) {
                  break;
               }

               var4.free();
            }

            if (var3 == 0) {
               return this;
            } else {
               CompositeByteBuf.Component var9 = this.lastAccessed;
               if (var9 != null && var9.endOffset <= var1) {
                  this.lastAccessed = null;
               }

               this.removeCompRange(0, var3);
               int var6 = var4.offset;
               this.updateComponentOffsets(0);
               this.setIndex(var1 - var6, var2 - var6);
               this.adjustMarkers(var6);
               return this;
            }
         }
      }
   }

   public CompositeByteBuf discardReadBytes() {
      this.ensureAccessible();
      int var1 = this.readerIndex();
      if (var1 == 0) {
         return this;
      } else {
         int var2 = this.writerIndex();
         if (var1 == var2 && var2 == this.capacity()) {
            int var8 = 0;

            for (int var9 = this.componentCount; var8 < var9; var8++) {
               this.components[var8].free();
            }

            this.lastAccessed = null;
            this.clearComps();
            this.setIndex(0, 0);
            this.adjustMarkers(var1);
            return this;
         } else {
            int var3 = 0;
            CompositeByteBuf.Component var4 = null;

            for (int var5 = this.componentCount; var3 < var5; var3++) {
               var4 = this.components[var3];
               if (var4.endOffset > var1) {
                  break;
               }

               var4.free();
            }

            int var10 = var1 - var4.offset;
            var4.offset = 0;
            var4.endOffset -= var1;
            var4.srcAdjustment += var1;
            var4.adjustment += var1;
            ByteBuf var6 = var4.slice;
            if (var6 != null) {
               var4.slice = var6.slice(var10, var4.length());
            }

            CompositeByteBuf.Component var7 = this.lastAccessed;
            if (var7 != null && var7.endOffset <= var1) {
               this.lastAccessed = null;
            }

            this.removeCompRange(0, var3);
            this.updateComponentOffsets(0);
            this.setIndex(0, var2 - var1);
            this.adjustMarkers(var1);
            return this;
         }
      }
   }

   private ByteBuf allocBuffer(int var1) {
      return this.direct ? this.alloc().directBuffer(var1) : this.alloc().heapBuffer(var1);
   }

   @Override
   public String toString() {
      String var1 = super.toString();
      var1 = var1.substring(0, var1.length() - 1);
      return var1 + ", components=" + this.componentCount + ')';
   }

   public CompositeByteBuf readerIndex(int var1) {
      super.readerIndex(var1);
      return this;
   }

   public CompositeByteBuf writerIndex(int var1) {
      super.writerIndex(var1);
      return this;
   }

   public CompositeByteBuf setIndex(int var1, int var2) {
      super.setIndex(var1, var2);
      return this;
   }

   public CompositeByteBuf clear() {
      super.clear();
      return this;
   }

   public CompositeByteBuf markReaderIndex() {
      super.markReaderIndex();
      return this;
   }

   public CompositeByteBuf resetReaderIndex() {
      super.resetReaderIndex();
      return this;
   }

   public CompositeByteBuf markWriterIndex() {
      super.markWriterIndex();
      return this;
   }

   public CompositeByteBuf resetWriterIndex() {
      super.resetWriterIndex();
      return this;
   }

   public CompositeByteBuf ensureWritable(int var1) {
      super.ensureWritable(var1);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2) {
      return this.getBytes(var1, var2, var2.writableBytes());
   }

   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.getBytes(var1, var2, var2.writerIndex(), var3);
      var2.writerIndex(var2.writerIndex() + var3);
      return this;
   }

   public CompositeByteBuf getBytes(int var1, byte[] var2) {
      return this.getBytes(var1, var2, 0, var2.length);
   }

   public CompositeByteBuf setBoolean(int var1, boolean var2) {
      return this.setByte(var1, var2 ? 1 : 0);
   }

   public CompositeByteBuf setChar(int var1, int var2) {
      return this.setShort(var1, var2);
   }

   public CompositeByteBuf setFloat(int var1, float var2) {
      return this.setInt(var1, Float.floatToRawIntBits(var2));
   }

   public CompositeByteBuf setDouble(int var1, double var2) {
      return this.setLong(var1, Double.doubleToRawLongBits(var2));
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2) {
      super.setBytes(var1, var2, var2.readableBytes());
      return this;
   }

   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      super.setBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf setBytes(int var1, byte[] var2) {
      return this.setBytes(var1, var2, 0, var2.length);
   }

   public CompositeByteBuf setZero(int var1, int var2) {
      super.setZero(var1, var2);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1) {
      super.readBytes(var1, var1.writableBytes());
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2) {
      super.readBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      super.readBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] var1) {
      super.readBytes(var1, 0, var1.length);
      return this;
   }

   public CompositeByteBuf readBytes(byte[] var1, int var2, int var3) {
      super.readBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf readBytes(ByteBuffer var1) {
      super.readBytes(var1);
      return this;
   }

   public CompositeByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      super.readBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf skipBytes(int var1) {
      super.skipBytes(var1);
      return this;
   }

   public CompositeByteBuf writeBoolean(boolean var1) {
      this.writeByte(var1 ? 1 : 0);
      return this;
   }

   public CompositeByteBuf writeByte(int var1) {
      this.ensureWritable0(1);
      this._setByte(this.writerIndex++, var1);
      return this;
   }

   public CompositeByteBuf writeShort(int var1) {
      super.writeShort(var1);
      return this;
   }

   public CompositeByteBuf writeMedium(int var1) {
      super.writeMedium(var1);
      return this;
   }

   public CompositeByteBuf writeInt(int var1) {
      super.writeInt(var1);
      return this;
   }

   public CompositeByteBuf writeLong(long var1) {
      super.writeLong(var1);
      return this;
   }

   public CompositeByteBuf writeChar(int var1) {
      super.writeShort(var1);
      return this;
   }

   public CompositeByteBuf writeFloat(float var1) {
      super.writeInt(Float.floatToRawIntBits(var1));
      return this;
   }

   public CompositeByteBuf writeDouble(double var1) {
      super.writeLong(Double.doubleToRawLongBits(var1));
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1) {
      super.writeBytes(var1, var1.readableBytes());
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2) {
      super.writeBytes(var1, var2);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      super.writeBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] var1) {
      super.writeBytes(var1, 0, var1.length);
      return this;
   }

   public CompositeByteBuf writeBytes(byte[] var1, int var2, int var3) {
      super.writeBytes(var1, var2, var3);
      return this;
   }

   public CompositeByteBuf writeBytes(ByteBuffer var1) {
      super.writeBytes(var1);
      return this;
   }

   public CompositeByteBuf writeZero(int var1) {
      super.writeZero(var1);
      return this;
   }

   public CompositeByteBuf retain(int var1) {
      super.retain(var1);
      return this;
   }

   public CompositeByteBuf retain() {
      super.retain();
      return this;
   }

   public CompositeByteBuf touch() {
      return this;
   }

   public CompositeByteBuf touch(Object var1) {
      return this;
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex(), this.readableBytes());
   }

   public CompositeByteBuf discardSomeReadBytes() {
      return this.discardReadComponents();
   }

   @Override
   protected void deallocate() {
      if (!this.freed) {
         this.freed = true;
         int var1 = 0;

         for (int var2 = this.componentCount; var1 < var2; var1++) {
            this.components[var1].free();
         }
      }
   }

   @Override
   boolean isAccessible() {
      return !this.freed;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }

   private void clearComps() {
      this.removeCompRange(0, this.componentCount);
   }

   private void removeComp(int var1) {
      this.removeCompRange(var1, var1 + 1);
   }

   private void removeCompRange(int var1, int var2) {
      if (var1 < var2) {
         int var3 = this.componentCount;

         assert var1 >= 0 && var2 <= var3;

         if (var2 < var3) {
            System.arraycopy(this.components, var2, this.components, var1, var3 - var2);
         }

         int var4 = var3 - var2 + var1;

         for (int var5 = var4; var5 < var3; var5++) {
            this.components[var5] = null;
         }

         this.componentCount = var4;
      }
   }

   private void addComp(int var1, CompositeByteBuf.Component var2) {
      this.shiftComps(var1, 1);
      this.components[var1] = var2;
   }

   private void shiftComps(int var1, int var2) {
      int var3 = this.componentCount;
      int var4 = var3 + var2;

      assert var1 >= 0 && var1 <= var3 && var2 > 0;

      if (var4 > this.components.length) {
         int var5 = Math.max(var3 + (var3 >> 1), var4);
         CompositeByteBuf.Component[] var6;
         if (var1 == var3) {
            var6 = Arrays.copyOf(this.components, var5, CompositeByteBuf.Component[].class);
         } else {
            var6 = new CompositeByteBuf.Component[var5];
            if (var1 > 0) {
               System.arraycopy(this.components, 0, var6, 0, var1);
            }

            if (var1 < var3) {
               System.arraycopy(this.components, var1, var6, var1 + var2, var3 - var1);
            }
         }

         this.components = var6;
      } else if (var1 < var3) {
         System.arraycopy(this.components, var1, this.components, var1 + var2, var3 - var1);
      }

      this.componentCount = var4;
   }

   interface ByteWrapper<T> {
      ByteBuf wrap(T var1);

      boolean isEmpty(T var1);
   }

   private static final class Component {
      final ByteBuf srcBuf;
      final ByteBuf buf;
      int srcAdjustment;
      int adjustment;
      int offset;
      int endOffset;
      private ByteBuf slice;

      Component(ByteBuf var1, int var2, ByteBuf var3, int var4, int var5, int var6, ByteBuf var7) {
         this.srcBuf = var1;
         this.srcAdjustment = var2 - var5;
         this.buf = var3;
         this.adjustment = var4 - var5;
         this.offset = var5;
         this.endOffset = var5 + var6;
         this.slice = var7;
      }

      int srcIdx(int var1) {
         return var1 + this.srcAdjustment;
      }

      int idx(int var1) {
         return var1 + this.adjustment;
      }

      int length() {
         return this.endOffset - this.offset;
      }

      void reposition(int var1) {
         int var2 = var1 - this.offset;
         this.endOffset += var2;
         this.srcAdjustment -= var2;
         this.adjustment -= var2;
         this.offset = var1;
      }

      void transferTo(ByteBuf var1) {
         var1.writeBytes(this.buf, this.idx(this.offset), this.length());
         this.free();
      }

      ByteBuf slice() {
         ByteBuf var1 = this.slice;
         if (var1 == null) {
            this.slice = var1 = this.srcBuf.slice(this.srcIdx(this.offset), this.length());
         }

         return var1;
      }

      ByteBuf duplicate() {
         return this.srcBuf.duplicate();
      }

      ByteBuffer internalNioBuffer(int var1, int var2) {
         return this.srcBuf.internalNioBuffer(this.srcIdx(var1), var2);
      }

      void free() {
         this.slice = null;
         this.srcBuf.release();
      }
   }

   private final class CompositeByteBufIterator implements Iterator<ByteBuf> {
      private final int size = CompositeByteBuf.this.numComponents();
      private int index;

      private CompositeByteBufIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.size > this.index;
      }

      public ByteBuf next() {
         if (this.size != CompositeByteBuf.this.numComponents()) {
            throw new ConcurrentModificationException();
         } else if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            try {
               return CompositeByteBuf.this.components[this.index++].slice();
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Read-Only");
      }
   }
}
