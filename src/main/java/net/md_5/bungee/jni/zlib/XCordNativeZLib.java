package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import me.minebuilders.natives.compression.LibNativeDeflate;
import me.minebuilders.natives.compression.LibNativeInflate;

public class XCordNativeZLib implements BungeeZlib {
   private final LibNativeInflate inflate = new LibNativeInflate();
   private final LibNativeDeflate deflate = new LibNativeDeflate();
   private long inflateCtx;
   private long deflateCtx;

   @Override
   public boolean requiresDirectMemory() {
      return true;
   }

   @Override
   public void init(boolean var1, int var2) {
      this.inflateCtx = this.inflate.init();
      this.deflateCtx = this.deflate.init(var2);
   }

   @Override
   public void free() {
      if (this.inflateCtx != 0L) {
         this.inflate.free(this.inflateCtx);
         this.deflate.free(this.deflateCtx);
         this.inflateCtx = 0L;
      }
   }

   @Override
   public void inflate(ByteBuf var1, ByteBuf var2, int var3) throws DataFormatException {
      var1.memoryAddress();
      var2.memoryAddress();
      var2.ensureWritable(var3);
      this.inflate
         .run(this.inflateCtx, var1.memoryAddress() + (long)var1.readerIndex(), var1.readableBytes(), var2.memoryAddress() + (long)var2.writerIndex(), var3);
      var2.writerIndex(var2.writerIndex() + var3);
   }

   @Override
   public void deflate(ByteBuf var1, ByteBuf var2) throws DataFormatException {
      while (true) {
         int var3 = this.deflate
            .run(
               this.deflateCtx,
               var1.memoryAddress() + (long)var1.readerIndex(),
               var1.readableBytes(),
               var2.memoryAddress() + (long)var2.writerIndex(),
               var2.writableBytes()
            );
         if (var3 > 0) {
            var2.writerIndex(var2.writerIndex() + var3);
            return;
         }

         var2.capacity(var2.capacity() * 2);
      }
   }
}
