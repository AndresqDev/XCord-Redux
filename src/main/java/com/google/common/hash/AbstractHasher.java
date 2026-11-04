package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@ElementTypesAreNonnullByDefault
@CanIgnoreReturnValue
abstract class AbstractHasher implements Hasher {
   @Override
   public final Hasher putBoolean(boolean var1) {
      return this.putByte((byte)(var1 ? 1 : 0));
   }

   @Override
   public final Hasher putDouble(double var1) {
      return this.putLong(Double.doubleToRawLongBits(var1));
   }

   @Override
   public final Hasher putFloat(float var1) {
      return this.putInt(Float.floatToRawIntBits(var1));
   }

   @Override
   public Hasher putUnencodedChars(CharSequence var1) {
      int var2 = 0;

      for (int var3 = var1.length(); var2 < var3; var2++) {
         this.putChar(var1.charAt(var2));
      }

      return this;
   }

   @Override
   public Hasher putString(CharSequence var1, Charset var2) {
      return this.putBytes(var1.toString().getBytes(var2));
   }

   @Override
   public Hasher putBytes(byte[] var1) {
      return this.putBytes(var1, 0, var1.length);
   }

   @Override
   public Hasher putBytes(byte[] var1, int var2, int var3) {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);

      for (int var4 = 0; var4 < var3; var4++) {
         this.putByte(var1[var2 + var4]);
      }

      return this;
   }

   @Override
   public Hasher putBytes(ByteBuffer var1) {
      if (var1.hasArray()) {
         this.putBytes(var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
         Java8Compatibility.position(var1, var1.limit());
      } else {
         for (int var2 = var1.remaining(); var2 > 0; var2--) {
            this.putByte(var1.get());
         }
      }

      return this;
   }

   @Override
   public Hasher putShort(short var1) {
      this.putByte((byte)var1);
      this.putByte((byte)(var1 >>> 8));
      return this;
   }

   @Override
   public Hasher putInt(int var1) {
      this.putByte((byte)var1);
      this.putByte((byte)(var1 >>> 8));
      this.putByte((byte)(var1 >>> 16));
      this.putByte((byte)(var1 >>> 24));
      return this;
   }

   @Override
   public Hasher putLong(long var1) {
      for (byte var3 = 0; var3 < 64; var3 += 8) {
         this.putByte((byte)((int)(var1 >>> var3)));
      }

      return this;
   }

   @Override
   public Hasher putChar(char var1) {
      this.putByte((byte)var1);
      this.putByte((byte)(var1 >>> '\b'));
      return this;
   }

   @Override
   public <T> Hasher putObject(@ParametricNullness T var1, Funnel<? super T> var2) {
      var2.funnel(var1, this);
      return this;
   }
}
