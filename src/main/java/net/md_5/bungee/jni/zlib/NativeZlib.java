package net.md_5.bungee.jni.zlib;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public class NativeZlib implements BungeeZlib {
   private final NativeCompressImpl nativeCompress = new NativeCompressImpl();
   private final NativeCompressImpl nativeDecompress = new NativeCompressImpl();
   private long compressCtx;
   private long dempressCtx;

   @Override
   public void init(boolean var1, int var2) {
      this.free();
      this.compressCtx = this.nativeCompress.init(true, var2);
      this.dempressCtx = this.nativeCompress.init(false, var2);
   }

   @Override
   public void free() {
      if (this.compressCtx != 0L) {
         this.nativeCompress.end(this.compressCtx, true);
         this.compressCtx = 0L;
      }

      if (this.dempressCtx != 0L) {
         this.nativeDecompress.end(this.dempressCtx, false);
         this.dempressCtx = 0L;
      }

      this.nativeCompress.consumed = 0;
      this.nativeCompress.finished = false;
      this.nativeDecompress.consumed = 0;
      this.nativeDecompress.finished = false;
   }

   @Override
   public boolean requiresDirectMemory() {
      return true;
   }

   @Override
   public void inflate(ByteBuf var1, ByteBuf var2, int var3) throws DataFormatException {
      var1.memoryAddress();
      var2.memoryAddress();
      Preconditions.checkState(this.dempressCtx != 0L, "Invalid pointer to compress!");

      while (!this.nativeDecompress.finished && var1.isReadable()) {
         var2.ensureWritable(8192);
         int var4 = this.nativeDecompress
            .process(
               this.dempressCtx,
               var1.memoryAddress() + (long)var1.readerIndex(),
               var1.readableBytes(),
               var2.memoryAddress() + (long)var2.writerIndex(),
               var2.writableBytes(),
               false
            );
         var1.readerIndex(var1.readerIndex() + this.nativeDecompress.consumed);
         var2.writerIndex(var2.writerIndex() + var4);
      }

      this.nativeDecompress.reset(this.dempressCtx, false);
      this.nativeDecompress.consumed = 0;
      this.nativeDecompress.finished = false;
   }

   @Override
   public void deflate(ByteBuf var1, ByteBuf var2) throws DataFormatException {
      var1.memoryAddress();
      var2.memoryAddress();
      Preconditions.checkState(this.compressCtx != 0L, "Invalid pointer to compress!");

      while (!this.nativeCompress.finished) {
         var2.ensureWritable(8192);
         int var3 = this.nativeCompress
            .process(
               this.compressCtx,
               var1.memoryAddress() + (long)var1.readerIndex(),
               var1.readableBytes(),
               var2.memoryAddress() + (long)var2.writerIndex(),
               var2.writableBytes(),
               true
            );
         var1.readerIndex(var1.readerIndex() + this.nativeCompress.consumed);
         var2.writerIndex(var2.writerIndex() + var3);
      }

      this.nativeCompress.reset(this.compressCtx, true);
      this.nativeCompress.consumed = 0;
      this.nativeCompress.finished = false;
   }
}
