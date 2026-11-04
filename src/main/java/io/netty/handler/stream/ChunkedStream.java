package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream implements ChunkedInput<ByteBuf> {
   static final int DEFAULT_CHUNK_SIZE = 8192;
   private final PushbackInputStream in;
   private final int chunkSize;
   private long offset;
   private boolean closed;

   public ChunkedStream(InputStream var1) {
      this(var1, 8192);
   }

   public ChunkedStream(InputStream var1, int var2) {
      ObjectUtil.checkNotNull(var1, "in");
      ObjectUtil.checkPositive(var2, "chunkSize");
      if (var1 instanceof PushbackInputStream) {
         this.in = (PushbackInputStream)var1;
      } else {
         this.in = new PushbackInputStream(var1);
      }

      this.chunkSize = var2;
   }

   public long transferredBytes() {
      return this.offset;
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      if (this.closed) {
         return true;
      } else if (this.in.available() > 0) {
         return false;
      } else {
         int var1 = this.in.read();
         if (var1 < 0) {
            return true;
         } else {
            this.in.unread(var1);
            return false;
         }
      }
   }

   @Override
   public void close() throws Exception {
      this.closed = true;
      this.in.close();
   }

   @Deprecated
   public ByteBuf readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public ByteBuf readChunk(ByteBufAllocator var1) throws Exception {
      if (this.isEndOfInput()) {
         return null;
      } else {
         int var2 = this.in.available();
         int var3;
         if (var2 <= 0) {
            var3 = this.chunkSize;
         } else {
            var3 = Math.min(this.chunkSize, this.in.available());
         }

         boolean var4 = true;
         ByteBuf var5 = var1.buffer(var3);

         Object var7;
         try {
            int var6 = var5.writeBytes(this.in, var3);
            if (var6 >= 0) {
               this.offset += (long)var6;
               var4 = false;
               return var5;
            }

            var7 = null;
         } finally {
            if (var4) {
               var5.release();
            }
         }

         return (ByteBuf)var7;
      }
   }

   @Override
   public long length() {
      return -1L;
   }

   @Override
   public long progress() {
      return this.offset;
   }
}
