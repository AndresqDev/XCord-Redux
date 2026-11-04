package io.netty.buffer;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream extends OutputStream implements DataOutput {
   private final ByteBuf buffer;
   private final int startIndex;
   private DataOutputStream utf8out;
   private boolean closed;

   public ByteBufOutputStream(ByteBuf var1) {
      this.buffer = ObjectUtil.checkNotNull(var1, "buffer");
      this.startIndex = var1.writerIndex();
   }

   public int writtenBytes() {
      return this.buffer.writerIndex() - this.startIndex;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 != 0) {
         this.buffer.writeBytes(var1, var2, var3);
      }
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.buffer.writeBytes(var1);
   }

   @Override
   public void write(int var1) throws IOException {
      this.buffer.writeByte(var1);
   }

   @Override
   public void writeBoolean(boolean var1) throws IOException {
      this.buffer.writeBoolean(var1);
   }

   @Override
   public void writeByte(int var1) throws IOException {
      this.buffer.writeByte(var1);
   }

   @Override
   public void writeBytes(String var1) throws IOException {
      this.buffer.writeCharSequence(var1, CharsetUtil.US_ASCII);
   }

   @Override
   public void writeChar(int var1) throws IOException {
      this.buffer.writeChar(var1);
   }

   @Override
   public void writeChars(String var1) throws IOException {
      int var2 = var1.length();

      for (int var3 = 0; var3 < var2; var3++) {
         this.buffer.writeChar(var1.charAt(var3));
      }
   }

   @Override
   public void writeDouble(double var1) throws IOException {
      this.buffer.writeDouble(var1);
   }

   @Override
   public void writeFloat(float var1) throws IOException {
      this.buffer.writeFloat(var1);
   }

   @Override
   public void writeInt(int var1) throws IOException {
      this.buffer.writeInt(var1);
   }

   @Override
   public void writeLong(long var1) throws IOException {
      this.buffer.writeLong(var1);
   }

   @Override
   public void writeShort(int var1) throws IOException {
      this.buffer.writeShort((short)var1);
   }

   @Override
   public void writeUTF(String var1) throws IOException {
      DataOutputStream var2 = this.utf8out;
      if (var2 == null) {
         if (this.closed) {
            throw new IOException("The stream is closed");
         }

         this.utf8out = var2 = new DataOutputStream(this);
      }

      var2.writeUTF(var1);
   }

   public ByteBuf buffer() {
      return this.buffer;
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;

         try {
            super.close();
         } finally {
            if (this.utf8out != null) {
               this.utf8out.close();
            }
         }
      }
   }
}
