package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

final class NioByteString extends ByteString.LeafByteString {
   private final ByteBuffer buffer;

   NioByteString(ByteBuffer var1) {
      Internal.checkNotNull(var1, "buffer");
      this.buffer = var1.slice().order(ByteOrder.nativeOrder());
   }

   private Object writeReplace() {
      return ByteString.copyFrom(this.buffer.slice());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      throw new InvalidObjectException("NioByteString instances are not to be serialized directly");
   }

   @Override
   public byte byteAt(int var1) {
      try {
         return this.buffer.get(var1);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw var3;
      } catch (IndexOutOfBoundsException var4) {
         throw new ArrayIndexOutOfBoundsException(var4.getMessage());
      }
   }

   @Override
   public byte internalByteAt(int var1) {
      return this.byteAt(var1);
   }

   @Override
   public int size() {
      return this.buffer.remaining();
   }

   @Override
   public ByteString substring(int var1, int var2) {
      try {
         ByteBuffer var3 = this.slice(var1, var2);
         return new NioByteString(var3);
      } catch (ArrayIndexOutOfBoundsException var4) {
         throw var4;
      } catch (IndexOutOfBoundsException var5) {
         throw new ArrayIndexOutOfBoundsException(var5.getMessage());
      }
   }

   @Override
   protected void copyToInternal(byte[] var1, int var2, int var3, int var4) {
      ByteBuffer var5 = this.buffer.slice();
      ((Buffer)var5).position(var2);
      var5.get(var1, var3, var4);
   }

   @Override
   public void copyTo(ByteBuffer var1) {
      var1.put(this.buffer.slice());
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      var1.write(this.toByteArray());
   }

   @Override
   boolean equalsRange(ByteString var1, int var2, int var3) {
      return this.substring(0, var3).equals(var1.substring(var2, var2 + var3));
   }

   @Override
   void writeToInternal(OutputStream var1, int var2, int var3) throws IOException {
      if (this.buffer.hasArray()) {
         int var4 = this.buffer.arrayOffset() + this.buffer.position() + var2;
         var1.write(this.buffer.array(), var4, var3);
      } else {
         ByteBufferWriter.write(this.slice(var2, var2 + var3), var1);
      }
   }

   @Override
   void writeTo(ByteOutput var1) throws IOException {
      var1.writeLazy(this.buffer.slice());
   }

   @Override
   public ByteBuffer asReadOnlyByteBuffer() {
      return this.buffer.asReadOnlyBuffer();
   }

   @Override
   public List<ByteBuffer> asReadOnlyByteBufferList() {
      return Collections.singletonList(this.asReadOnlyByteBuffer());
   }

   @Override
   protected String toStringInternal(Charset var1) {
      byte[] var2;
      int var3;
      int var4;
      if (this.buffer.hasArray()) {
         var2 = this.buffer.array();
         var3 = this.buffer.arrayOffset() + this.buffer.position();
         var4 = this.buffer.remaining();
      } else {
         var2 = this.toByteArray();
         var3 = 0;
         var4 = var2.length;
      }

      return new String(var2, var3, var4, var1);
   }

   @Override
   public boolean isValidUtf8() {
      return Utf8.isValidUtf8(this.buffer);
   }

   @Override
   protected int partialIsValidUtf8(int var1, int var2, int var3) {
      return Utf8.partialIsValidUtf8(var1, this.buffer, var2, var2 + var3);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ByteString)) {
         return false;
      } else {
         ByteString var2 = (ByteString)var1;
         if (this.size() != var2.size()) {
            return false;
         } else if (this.size() == 0) {
            return true;
         } else if (var1 instanceof NioByteString) {
            return this.buffer.equals(((NioByteString)var1).buffer);
         } else {
            return var1 instanceof RopeByteString ? var1.equals(this) : this.buffer.equals(var2.asReadOnlyByteBuffer());
         }
      }
   }

   @Override
   protected int partialHash(int var1, int var2, int var3) {
      for (int var4 = var2; var4 < var2 + var3; var4++) {
         var1 = var1 * 31 + this.buffer.get(var4);
      }

      return var1;
   }

   @Override
   public InputStream newInput() {
      return new InputStream() {
         private final ByteBuffer buf = NioByteString.this.buffer.slice();

         @Override
         public void mark(int var1) {
            ((Buffer)this.buf).mark();
         }

         @Override
         public boolean markSupported() {
            return true;
         }

         @Override
         public void reset() throws IOException {
            try {
               ((Buffer)this.buf).reset();
            } catch (InvalidMarkException var2) {
               throw new IOException(var2);
            }
         }

         @Override
         public int available() throws IOException {
            return this.buf.remaining();
         }

         @Override
         public int read() throws IOException {
            return !this.buf.hasRemaining() ? -1 : this.buf.get() & 0xFF;
         }

         @Override
         public int read(byte[] var1, int var2, int var3) throws IOException {
            if (!this.buf.hasRemaining()) {
               return -1;
            } else {
               var3 = Math.min(var3, this.buf.remaining());
               this.buf.get(var1, var2, var3);
               return var3;
            }
         }
      };
   }

   @Override
   public CodedInputStream newCodedInput() {
      return CodedInputStream.newInstance(this.buffer, true);
   }

   private ByteBuffer slice(int var1, int var2) {
      if (var1 >= this.buffer.position() && var2 <= this.buffer.limit() && var1 <= var2) {
         ByteBuffer var3 = this.buffer.slice();
         ((Buffer)var3).position(var1 - this.buffer.position());
         ((Buffer)var3).limit(var2 - this.buffer.position());
         return var3;
      } else {
         throw new IllegalArgumentException(String.format("Invalid indices [%d, %d]", var1, var2));
      }
   }
}
