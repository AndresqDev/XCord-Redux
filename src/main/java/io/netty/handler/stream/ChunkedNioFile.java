package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

public class ChunkedNioFile implements ChunkedInput<ByteBuf> {
   private final FileChannel in;
   private final long startOffset;
   private final long endOffset;
   private final int chunkSize;
   private long offset;

   public ChunkedNioFile(File var1) throws IOException {
      this(new RandomAccessFile(var1, "r").getChannel());
   }

   public ChunkedNioFile(File var1, int var2) throws IOException {
      this(new RandomAccessFile(var1, "r").getChannel(), var2);
   }

   public ChunkedNioFile(FileChannel var1) throws IOException {
      this(var1, 8192);
   }

   public ChunkedNioFile(FileChannel var1, int var2) throws IOException {
      this(var1, 0L, var1.size(), var2);
   }

   public ChunkedNioFile(FileChannel var1, long var2, long var4, int var6) throws IOException {
      ObjectUtil.checkNotNull(var1, "in");
      ObjectUtil.checkPositiveOrZero(var2, "offset");
      ObjectUtil.checkPositiveOrZero(var4, "length");
      ObjectUtil.checkPositive(var6, "chunkSize");
      if (!var1.isOpen()) {
         throw new ClosedChannelException();
      } else {
         this.in = var1;
         this.chunkSize = var6;
         this.offset = this.startOffset = var2;
         this.endOffset = var2 + var4;
      }
   }

   public long startOffset() {
      return this.startOffset;
   }

   public long endOffset() {
      return this.endOffset;
   }

   public long currentOffset() {
      return this.offset;
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      return this.offset >= this.endOffset || !this.in.isOpen();
   }

   @Override
   public void close() throws Exception {
      this.in.close();
   }

   @Deprecated
   public ByteBuf readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public ByteBuf readChunk(ByteBufAllocator var1) throws Exception {
      long var2 = this.offset;
      if (var2 >= this.endOffset) {
         return null;
      } else {
         int var4 = (int)Math.min((long)this.chunkSize, this.endOffset - var2);
         ByteBuf var5 = var1.buffer(var4);
         boolean var6 = true;

         ByteBuf var12;
         try {
            int var7 = 0;

            do {
               int var8 = var5.writeBytes(this.in, var2 + (long)var7, var4 - var7);
               if (var8 < 0) {
                  break;
               }

               var7 += var8;
            } while (var7 != var4);

            this.offset += (long)var7;
            var6 = false;
            var12 = var5;
         } finally {
            if (var6) {
               var5.release();
            }
         }

         return var12;
      }
   }

   @Override
   public long length() {
      return this.endOffset - this.startOffset;
   }

   @Override
   public long progress() {
      return this.offset - this.startOffset;
   }
}
