package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JavaZlib implements BungeeZlib {
   private final byte[] buffer = new byte[8192];
   private Deflater deflater;
   private Inflater inflater;

   @Override
   public void init(boolean var1, int var2) {
      this.free();
      this.deflater = new Deflater(var2);
      this.inflater = new Inflater();
   }

   @Override
   public void free() {
      if (this.deflater != null) {
         this.deflater.end();
      }

      if (this.inflater != null) {
         this.inflater.end();
      }
   }

   @Override
   public boolean requiresDirectMemory() {
      return false;
   }

   @Override
   public void inflate(ByteBuf var1, ByteBuf var2, int var3) throws DataFormatException {
      byte[] var4 = new byte[var1.readableBytes()];
      var1.readBytes(var4);
      this.inflater.setInput(var4);

      while (!this.inflater.finished() && this.inflater.getTotalIn() < var4.length) {
         int var5 = this.inflater.inflate(this.buffer);
         var2.writeBytes(this.buffer, 0, var5);
      }

      this.inflater.reset();
   }

   @Override
   public void deflate(ByteBuf var1, ByteBuf var2) throws DataFormatException {
      byte[] var3 = new byte[var1.readableBytes()];
      var1.readBytes(var3);
      this.deflater.setInput(var3);
      this.deflater.finish();

      while (!this.deflater.finished()) {
         int var4 = this.deflater.deflate(this.buffer);
         var2.writeBytes(this.buffer, 0, var4);
      }

      this.deflater.reset();
   }
}
