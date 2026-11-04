package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

@Immutable
@ElementTypesAreNonnullByDefault
abstract class AbstractNonStreamingHashFunction extends AbstractHashFunction {
   @Override
   public Hasher newHasher() {
      return this.newHasher(32);
   }

   @Override
   public Hasher newHasher(int var1) {
      Preconditions.checkArgument(var1 >= 0);
      return new AbstractNonStreamingHashFunction.BufferingHasher(var1);
   }

   @Override
   public HashCode hashInt(int var1) {
      return this.hashBytes(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(var1).array());
   }

   @Override
   public HashCode hashLong(long var1) {
      return this.hashBytes(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(var1).array());
   }

   @Override
   public HashCode hashUnencodedChars(CharSequence var1) {
      int var2 = var1.length();
      ByteBuffer var3 = ByteBuffer.allocate(var2 * 2).order(ByteOrder.LITTLE_ENDIAN);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.putChar(var1.charAt(var4));
      }

      return this.hashBytes(var3.array());
   }

   @Override
   public HashCode hashString(CharSequence var1, Charset var2) {
      return this.hashBytes(var1.toString().getBytes(var2));
   }

   @Override
   public abstract HashCode hashBytes(byte[] var1, int var2, int var3);

   @Override
   public HashCode hashBytes(ByteBuffer var1) {
      return this.newHasher(var1.remaining()).putBytes(var1).hash();
   }

   private final class BufferingHasher extends AbstractHasher {
      final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;

      BufferingHasher(int var2) {
         this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(var2);
      }

      @Override
      public Hasher putByte(byte var1) {
         this.stream.write(var1);
         return this;
      }

      @Override
      public Hasher putBytes(byte[] var1, int var2, int var3) {
         this.stream.write(var1, var2, var3);
         return this;
      }

      @Override
      public Hasher putBytes(ByteBuffer var1) {
         this.stream.write(var1);
         return this;
      }

      @Override
      public HashCode hash() {
         return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
      }
   }

   private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
      ExposedByteArrayOutputStream(int var1) {
         super(var1);
      }

      void write(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.count + var2 > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, this.count + var2);
         }

         var1.get(this.buf, this.count, var2);
         this.count += var2;
      }

      byte[] byteArray() {
         return this.buf;
      }

      int length() {
         return this.count;
      }
   }
}
