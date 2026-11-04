package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class ByteStreams {
   private static final int BUFFER_SIZE = 8192;
   private static final int ZERO_COPY_CHUNK_SIZE = 524288;
   private static final int MAX_ARRAY_LEN = 2147483639;
   private static final int TO_BYTE_ARRAY_DEQUE_SIZE = 20;
   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
      @Override
      public void write(int var1) {
      }

      @Override
      public void write(byte[] var1) {
         Preconditions.checkNotNull(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         Preconditions.checkNotNull(var1);
      }

      @Override
      public String toString() {
         return "ByteStreams.nullOutputStream()";
      }
   };

   static byte[] createBuffer() {
      return new byte[8192];
   }

   private ByteStreams() {
   }

   @CanIgnoreReturnValue
   public static long copy(InputStream var0, OutputStream var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      byte[] var2 = createBuffer();
      long var3 = 0L;

      while (true) {
         int var5 = var0.read(var2);
         if (var5 == -1) {
            return var3;
         }

         var1.write(var2, 0, var5);
         var3 += (long)var5;
      }
   }

   @CanIgnoreReturnValue
   public static long copy(ReadableByteChannel var0, WritableByteChannel var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (var0 instanceof FileChannel) {
         FileChannel var9 = (FileChannel)var0;
         long var10 = var9.position();
         long var5 = var10;

         long var7;
         do {
            var7 = var9.transferTo(var5, 524288L, var1);
            var5 += var7;
            var9.position(var5);
         } while (var7 > 0L || var5 < var9.size());

         return var5 - var10;
      } else {
         ByteBuffer var2 = ByteBuffer.wrap(createBuffer());
         long var3 = 0L;

         while (var0.read(var2) != -1) {
            Java8Compatibility.flip(var2);

            while (var2.hasRemaining()) {
               var3 += (long)var1.write(var2);
            }

            Java8Compatibility.clear(var2);
         }

         return var3;
      }
   }

   private static byte[] toByteArrayInternal(InputStream var0, Queue<byte[]> var1, int var2) throws IOException {
      for (int var3 = 8192; var2 < 2147483639; var3 = IntMath.saturatedMultiply(var3, 2)) {
         byte[] var4 = new byte[Math.min(var3, 2147483639 - var2)];
         var1.add(var4);
         int var5 = 0;

         while (var5 < var4.length) {
            int var6 = var0.read(var4, var5, var4.length - var5);
            if (var6 == -1) {
               return combineBuffers(var1, var2);
            }

            var5 += var6;
            var2 += var6;
         }
      }

      if (var0.read() == -1) {
         return combineBuffers(var1, 2147483639);
      } else {
         throw new OutOfMemoryError("input is too large to fit in a byte array");
      }
   }

   private static byte[] combineBuffers(Queue<byte[]> var0, int var1) {
      byte[] var2 = new byte[var1];
      int var3 = var1;

      while (var3 > 0) {
         byte[] var4 = (byte[])var0.remove();
         int var5 = Math.min(var3, var4.length);
         int var6 = var1 - var3;
         System.arraycopy(var4, 0, var2, var6, var5);
         var3 -= var5;
      }

      return var2;
   }

   public static byte[] toByteArray(InputStream var0) throws IOException {
      Preconditions.checkNotNull(var0);
      return toByteArrayInternal(var0, new ArrayDeque<>(20), 0);
   }

   static byte[] toByteArray(InputStream var0, long var1) throws IOException {
      Preconditions.checkArgument(var1 >= 0L, "expectedSize (%s) must be non-negative", var1);
      if (var1 > 2147483639L) {
         throw new OutOfMemoryError(new StringBuilder(62).append(var1).append(" bytes is too large to fit in a byte array").toString());
      } else {
         byte[] var3 = new byte[(int)var1];
         int var4 = (int)var1;

         while (var4 > 0) {
            int var5 = (int)var1 - var4;
            int var6 = var0.read(var3, var5, var4);
            if (var6 == -1) {
               return Arrays.copyOf(var3, var5);
            }

            var4 -= var6;
         }

         int var7 = var0.read();
         if (var7 == -1) {
            return var3;
         } else {
            ArrayDeque var8 = new ArrayDeque(22);
            var8.add(var3);
            var8.add(new byte[]{(byte)var7});
            return toByteArrayInternal(var0, var8, var3.length + 1);
         }
      }
   }

   @CanIgnoreReturnValue
   @Beta
   public static long exhaust(InputStream var0) throws IOException {
      long var1 = 0L;
      byte[] var5 = createBuffer();

      long var3;
      while ((var3 = (long)var0.read(var5)) != -1L) {
         var1 += var3;
      }

      return var1;
   }

   @Beta
   public static ByteArrayDataInput newDataInput(byte[] var0) {
      return newDataInput(new ByteArrayInputStream(var0));
   }

   @Beta
   public static ByteArrayDataInput newDataInput(byte[] var0, int var1) {
      Preconditions.checkPositionIndex(var1, var0.length);
      return newDataInput(new ByteArrayInputStream(var0, var1, var0.length - var1));
   }

   @Beta
   public static ByteArrayDataInput newDataInput(ByteArrayInputStream var0) {
      return new ByteStreams.ByteArrayDataInputStream(Preconditions.checkNotNull(var0));
   }

   @Beta
   public static ByteArrayDataOutput newDataOutput() {
      return newDataOutput(new ByteArrayOutputStream());
   }

   @Beta
   public static ByteArrayDataOutput newDataOutput(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException(String.format("Invalid size: %s", var0));
      } else {
         return newDataOutput(new ByteArrayOutputStream(var0));
      }
   }

   @Beta
   public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream var0) {
      return new ByteStreams.ByteArrayDataOutputStream(Preconditions.checkNotNull(var0));
   }

   @Beta
   public static OutputStream nullOutputStream() {
      return NULL_OUTPUT_STREAM;
   }

   @Beta
   public static InputStream limit(InputStream var0, long var1) {
      return new ByteStreams.LimitedInputStream(var0, var1);
   }

   @Beta
   public static void readFully(InputStream var0, byte[] var1) throws IOException {
      readFully(var0, var1, 0, var1.length);
   }

   @Beta
   public static void readFully(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      int var4 = read(var0, var1, var2, var3);
      if (var4 != var3) {
         throw new EOFException(
            new StringBuilder(81)
               .append("reached end of stream after reading ")
               .append(var4)
               .append(" bytes; ")
               .append(var3)
               .append(" bytes expected")
               .toString()
         );
      }
   }

   @Beta
   public static void skipFully(InputStream var0, long var1) throws IOException {
      long var3 = skipUpTo(var0, var1);
      if (var3 < var1) {
         throw new EOFException(
            new StringBuilder(100)
               .append("reached end of stream after skipping ")
               .append(var3)
               .append(" bytes; ")
               .append(var1)
               .append(" bytes expected")
               .toString()
         );
      }
   }

   static long skipUpTo(InputStream var0, long var1) throws IOException {
      long var3 = 0L;
      byte[] var5 = null;

      while (var3 < var1) {
         long var6 = var1 - var3;
         long var8 = skipSafely(var0, var6);
         if (var8 == 0L) {
            int var10 = (int)Math.min(var6, 8192L);
            if (var5 == null) {
               var5 = new byte[var10];
            }

            if ((var8 = (long)var0.read(var5, 0, var10)) == -1L) {
               break;
            }
         }

         var3 += var8;
      }

      return var3;
   }

   private static long skipSafely(InputStream var0, long var1) throws IOException {
      int var3 = var0.available();
      return var3 == 0 ? 0L : var0.skip(Math.min((long)var3, var1));
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   public static <T> T readBytes(InputStream var0, ByteProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      byte[] var2 = createBuffer();

      int var3;
      do {
         var3 = var0.read(var2);
      } while (var3 != -1 && var1.processBytes(var2, 0, var3));

      return (T)var1.getResult();
   }

   @Beta
   @CanIgnoreReturnValue
   public static int read(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (var3 < 0) {
         throw new IndexOutOfBoundsException(String.format("len (%s) cannot be negative", var3));
      } else {
         Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
         int var4 = 0;

         while (var4 < var3) {
            int var5 = var0.read(var1, var2 + var4, var3 - var4);
            if (var5 == -1) {
               break;
            }

            var4 += var5;
         }

         return var4;
      }
   }

   private static class ByteArrayDataInputStream implements ByteArrayDataInput {
      final DataInput input;

      ByteArrayDataInputStream(ByteArrayInputStream var1) {
         this.input = new DataInputStream(var1);
      }

      @Override
      public void readFully(byte[] var1) {
         try {
            this.input.readFully(var1);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      @Override
      public void readFully(byte[] var1, int var2, int var3) {
         try {
            this.input.readFully(var1, var2, var3);
         } catch (IOException var5) {
            throw new IllegalStateException(var5);
         }
      }

      @Override
      public int skipBytes(int var1) {
         try {
            return this.input.skipBytes(var1);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      @Override
      public boolean readBoolean() {
         try {
            return this.input.readBoolean();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public byte readByte() {
         try {
            return this.input.readByte();
         } catch (EOFException var2) {
            throw new IllegalStateException(var2);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public int readUnsignedByte() {
         try {
            return this.input.readUnsignedByte();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public short readShort() {
         try {
            return this.input.readShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public int readUnsignedShort() {
         try {
            return this.input.readUnsignedShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public char readChar() {
         try {
            return this.input.readChar();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public int readInt() {
         try {
            return this.input.readInt();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public long readLong() {
         try {
            return this.input.readLong();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public float readFloat() {
         try {
            return this.input.readFloat();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public double readDouble() {
         try {
            return this.input.readDouble();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public String readLine() {
         try {
            return this.input.readLine();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      @Override
      public String readUTF() {
         try {
            return this.input.readUTF();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }
   }

   private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {
      final DataOutput output;
      final ByteArrayOutputStream byteArrayOutputStream;

      ByteArrayDataOutputStream(ByteArrayOutputStream var1) {
         this.byteArrayOutputStream = var1;
         this.output = new DataOutputStream(var1);
      }

      @Override
      public void write(int var1) {
         try {
            this.output.write(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void write(byte[] var1) {
         try {
            this.output.write(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         try {
            this.output.write(var1, var2, var3);
         } catch (IOException var5) {
            throw new AssertionError(var5);
         }
      }

      @Override
      public void writeBoolean(boolean var1) {
         try {
            this.output.writeBoolean(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeByte(int var1) {
         try {
            this.output.writeByte(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeBytes(String var1) {
         try {
            this.output.writeBytes(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeChar(int var1) {
         try {
            this.output.writeChar(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeChars(String var1) {
         try {
            this.output.writeChars(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeDouble(double var1) {
         try {
            this.output.writeDouble(var1);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      @Override
      public void writeFloat(float var1) {
         try {
            this.output.writeFloat(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeInt(int var1) {
         try {
            this.output.writeInt(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeLong(long var1) {
         try {
            this.output.writeLong(var1);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      @Override
      public void writeShort(int var1) {
         try {
            this.output.writeShort(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public void writeUTF(String var1) {
         try {
            this.output.writeUTF(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      @Override
      public byte[] toByteArray() {
         return this.byteArrayOutputStream.toByteArray();
      }
   }

   private static final class LimitedInputStream extends FilterInputStream {
      private long left;
      private long mark = -1L;

      LimitedInputStream(InputStream var1, long var2) {
         super(var1);
         Preconditions.checkNotNull(var1);
         Preconditions.checkArgument(var2 >= 0L, "limit must be non-negative");
         this.left = var2;
      }

      @Override
      public int available() throws IOException {
         return (int)Math.min((long)this.in.available(), this.left);
      }

      @Override
      public synchronized void mark(int var1) {
         this.in.mark(var1);
         this.mark = this.left;
      }

      @Override
      public int read() throws IOException {
         if (this.left == 0L) {
            return -1;
         } else {
            int var1 = this.in.read();
            if (var1 != -1) {
               this.left--;
            }

            return var1;
         }
      }

      @Override
      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.left == 0L) {
            return -1;
         } else {
            var3 = (int)Math.min((long)var3, this.left);
            int var4 = this.in.read(var1, var2, var3);
            if (var4 != -1) {
               this.left -= (long)var4;
            }

            return var4;
         }
      }

      @Override
      public synchronized void reset() throws IOException {
         if (!this.in.markSupported()) {
            throw new IOException("Mark not supported");
         } else if (this.mark == -1L) {
            throw new IOException("Mark not set");
         } else {
            this.in.reset();
            this.left = this.mark;
         }
      }

      @Override
      public long skip(long var1) throws IOException {
         var1 = Math.min(var1, this.left);
         long var3 = this.in.skip(var1);
         this.left -= var3;
         return var3;
      }
   }
}
