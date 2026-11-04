package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufInputStream extends InputStream implements DataInput {
   private final ByteBuf buffer;
   private final int startIndex;
   private final int endIndex;
   private boolean closed;
   private final boolean releaseOnClose;
   private StringBuilder lineBuf;

   public ByteBufInputStream(ByteBuf var1) {
      this(var1, var1.readableBytes());
   }

   public ByteBufInputStream(ByteBuf var1, int var2) {
      this(var1, var2, false);
   }

   public ByteBufInputStream(ByteBuf var1, boolean var2) {
      this(var1, var1.readableBytes(), var2);
   }

   public ByteBufInputStream(ByteBuf var1, int var2, boolean var3) {
      ObjectUtil.checkNotNull(var1, "buffer");
      if (var2 < 0) {
         if (var3) {
            var1.release();
         }

         ObjectUtil.checkPositiveOrZero(var2, "length");
      }

      if (var2 > var1.readableBytes()) {
         if (var3) {
            var1.release();
         }

         throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + var2 + ", maximum is " + var1.readableBytes());
      } else {
         this.releaseOnClose = var3;
         this.buffer = var1;
         this.startIndex = var1.readerIndex();
         this.endIndex = this.startIndex + var2;
         var1.markReaderIndex();
      }
   }

   public int readBytes() {
      return this.buffer.readerIndex() - this.startIndex;
   }

   @Override
   public void close() throws IOException {
      try {
         super.close();
      } finally {
         if (this.releaseOnClose && !this.closed) {
            this.closed = true;
            this.buffer.release();
         }
      }
   }

   @Override
   public int available() throws IOException {
      return this.endIndex - this.buffer.readerIndex();
   }

   @Override
   public void mark(int var1) {
      this.buffer.markReaderIndex();
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.available();
      return var1 == 0 ? -1 : this.buffer.readByte() & 0xFF;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.available();
      if (var4 == 0) {
         return -1;
      } else {
         var3 = Math.min(var4, var3);
         this.buffer.readBytes(var1, var2, var3);
         return var3;
      }
   }

   @Override
   public void reset() throws IOException {
      this.buffer.resetReaderIndex();
   }

   @Override
   public long skip(long var1) throws IOException {
      return var1 > 2147483647L ? (long)this.skipBytes(Integer.MAX_VALUE) : (long)this.skipBytes((int)var1);
   }

   @Override
   public boolean readBoolean() throws IOException {
      this.checkAvailable(1);
      return this.read() != 0;
   }

   @Override
   public byte readByte() throws IOException {
      int var1 = this.available();
      if (var1 == 0) {
         throw new EOFException();
      } else {
         return this.buffer.readByte();
      }
   }

   @Override
   public char readChar() throws IOException {
      return (char)this.readShort();
   }

   @Override
   public double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   @Override
   public float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   @Override
   public void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   @Override
   public void readFully(byte[] var1, int var2, int var3) throws IOException {
      this.checkAvailable(var3);
      this.buffer.readBytes(var1, var2, var3);
   }

   @Override
   public int readInt() throws IOException {
      this.checkAvailable(4);
      return this.buffer.readInt();
   }

   @Override
   public String readLine() throws IOException {
      int var1 = this.available();
      if (var1 == 0) {
         return null;
      } else {
         if (this.lineBuf != null) {
            this.lineBuf.setLength(0);
         }

         while (true) {
            short var2 = this.buffer.readUnsignedByte();
            var1--;
            switch (var2) {
               case 10:
                  return this.lineBuf != null && this.lineBuf.length() > 0 ? this.lineBuf.toString() : "";
               case 13:
                  if (var1 > 0 && (char)this.buffer.getUnsignedByte(this.buffer.readerIndex()) == '\n') {
                     this.buffer.skipBytes(1);
                     var1--;
                  }

                  return this.lineBuf != null && this.lineBuf.length() > 0 ? this.lineBuf.toString() : "";
               default:
                  if (this.lineBuf == null) {
                     this.lineBuf = new StringBuilder();
                  }

                  this.lineBuf.append((char)var2);
                  if (var1 <= 0) {
                     return this.lineBuf != null && this.lineBuf.length() > 0 ? this.lineBuf.toString() : "";
                  }
            }
         }
      }
   }

   @Override
   public long readLong() throws IOException {
      this.checkAvailable(8);
      return this.buffer.readLong();
   }

   @Override
   public short readShort() throws IOException {
      this.checkAvailable(2);
      return this.buffer.readShort();
   }

   @Override
   public String readUTF() throws IOException {
      return DataInputStream.readUTF(this);
   }

   @Override
   public int readUnsignedByte() throws IOException {
      return this.readByte() & 0xFF;
   }

   @Override
   public int readUnsignedShort() throws IOException {
      return this.readShort() & 65535;
   }

   @Override
   public int skipBytes(int var1) throws IOException {
      int var2 = Math.min(this.available(), var1);
      this.buffer.skipBytes(var2);
      return var2;
   }

   private void checkAvailable(int var1) throws IOException {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
      } else if (var1 > this.available()) {
         throw new EOFException("fieldSize is too long! Length is " + var1 + ", but maximum is " + this.available());
      }
   }
}
