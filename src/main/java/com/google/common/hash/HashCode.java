package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedInts;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
public abstract class HashCode {
   private static final char[] hexDigits = "0123456789abcdef".toCharArray();

   HashCode() {
   }

   public abstract int bits();

   public abstract int asInt();

   public abstract long asLong();

   public abstract long padToLong();

   public abstract byte[] asBytes();

   @CanIgnoreReturnValue
   public int writeBytesTo(byte[] var1, int var2, int var3) {
      var3 = Ints.min(var3, this.bits() / 8);
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      this.writeBytesToImpl(var1, var2, var3);
      return var3;
   }

   abstract void writeBytesToImpl(byte[] var1, int var2, int var3);

   byte[] getBytesInternal() {
      return this.asBytes();
   }

   abstract boolean equalsSameBits(HashCode var1);

   public static HashCode fromInt(int var0) {
      return new HashCode.IntHashCode(var0);
   }

   public static HashCode fromLong(long var0) {
      return new HashCode.LongHashCode(var0);
   }

   public static HashCode fromBytes(byte[] var0) {
      Preconditions.checkArgument(var0.length >= 1, "A HashCode must contain at least 1 byte.");
      return fromBytesNoCopy((byte[])var0.clone());
   }

   static HashCode fromBytesNoCopy(byte[] var0) {
      return new HashCode.BytesHashCode(var0);
   }

   public static HashCode fromString(String var0) {
      Preconditions.checkArgument(var0.length() >= 2, "input string (%s) must have at least 2 characters", var0);
      Preconditions.checkArgument(var0.length() % 2 == 0, "input string (%s) must have an even number of characters", var0);
      byte[] var1 = new byte[var0.length() / 2];

      for (byte var2 = 0; var2 < var0.length(); var2 += 2) {
         int var3 = decode(var0.charAt(var2)) << 4;
         int var4 = decode(var0.charAt(var2 + 1));
         var1[var2 / 2] = (byte)(var3 + var4);
      }

      return fromBytesNoCopy(var1);
   }

   private static int decode(char var0) {
      if (var0 >= '0' && var0 <= '9') {
         return var0 - 48;
      } else if (var0 >= 'a' && var0 <= 'f') {
         return var0 - 97 + 10;
      } else {
         throw new IllegalArgumentException(new StringBuilder(32).append("Illegal hexadecimal character: ").append(var0).toString());
      }
   }

   @Override
   public final boolean equals(Object var1) {
      if (!(var1 instanceof HashCode)) {
         return false;
      } else {
         HashCode var2 = (HashCode)var1;
         return this.bits() == var2.bits() && this.equalsSameBits(var2);
      }
   }

   @Override
   public final int hashCode() {
      if (this.bits() >= 32) {
         return this.asInt();
      } else {
         byte[] var1 = this.getBytesInternal();
         int var2 = var1[0] & 255;

         for (int var3 = 1; var3 < var1.length; var3++) {
            var2 |= (var1[var3] & 255) << var3 * 8;
         }

         return var2;
      }
   }

   @Override
   public final String toString() {
      byte[] var1 = this.getBytesInternal();
      StringBuilder var2 = new StringBuilder(2 * var1.length);

      for (byte var6 : var1) {
         var2.append(hexDigits[var6 >> 4 & 15]).append(hexDigits[var6 & 15]);
      }

      return var2.toString();
   }

   private static final class BytesHashCode extends HashCode implements Serializable {
      final byte[] bytes;
      private static final long serialVersionUID = 0L;

      BytesHashCode(byte[] var1) {
         this.bytes = Preconditions.checkNotNull(var1);
      }

      @Override
      public int bits() {
         return this.bytes.length * 8;
      }

      @Override
      public byte[] asBytes() {
         return (byte[])this.bytes.clone();
      }

      @Override
      public int asInt() {
         Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", this.bytes.length);
         return this.bytes[0] & 0xFF | (this.bytes[1] & 0xFF) << 8 | (this.bytes[2] & 0xFF) << 16 | (this.bytes[3] & 0xFF) << 24;
      }

      @Override
      public long asLong() {
         Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", this.bytes.length);
         return this.padToLong();
      }

      @Override
      public long padToLong() {
         long var1 = (long)(this.bytes[0] & 255);

         for (int var3 = 1; var3 < Math.min(this.bytes.length, 8); var3++) {
            var1 |= ((long)this.bytes[var3] & 255L) << var3 * 8;
         }

         return var1;
      }

      @Override
      void writeBytesToImpl(byte[] var1, int var2, int var3) {
         System.arraycopy(this.bytes, 0, var1, var2, var3);
      }

      @Override
      byte[] getBytesInternal() {
         return this.bytes;
      }

      @Override
      boolean equalsSameBits(HashCode var1) {
         if (this.bytes.length != var1.getBytesInternal().length) {
            return false;
         } else {
            boolean var2 = true;

            for (int var3 = 0; var3 < this.bytes.length; var3++) {
               var2 &= this.bytes[var3] == var1.getBytesInternal()[var3];
            }

            return var2;
         }
      }
   }

   private static final class IntHashCode extends HashCode implements Serializable {
      final int hash;
      private static final long serialVersionUID = 0L;

      IntHashCode(int var1) {
         this.hash = var1;
      }

      @Override
      public int bits() {
         return 32;
      }

      @Override
      public byte[] asBytes() {
         return new byte[]{(byte)this.hash, (byte)(this.hash >> 8), (byte)(this.hash >> 16), (byte)(this.hash >> 24)};
      }

      @Override
      public int asInt() {
         return this.hash;
      }

      @Override
      public long asLong() {
         throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
      }

      @Override
      public long padToLong() {
         return UnsignedInts.toLong(this.hash);
      }

      @Override
      void writeBytesToImpl(byte[] var1, int var2, int var3) {
         for (int var4 = 0; var4 < var3; var4++) {
            var1[var2 + var4] = (byte)(this.hash >> var4 * 8);
         }
      }

      @Override
      boolean equalsSameBits(HashCode var1) {
         return this.hash == var1.asInt();
      }
   }

   private static final class LongHashCode extends HashCode implements Serializable {
      final long hash;
      private static final long serialVersionUID = 0L;

      LongHashCode(long var1) {
         this.hash = var1;
      }

      @Override
      public int bits() {
         return 64;
      }

      @Override
      public byte[] asBytes() {
         return new byte[]{
            (byte)((int)this.hash),
            (byte)((int)(this.hash >> 8)),
            (byte)((int)(this.hash >> 16)),
            (byte)((int)(this.hash >> 24)),
            (byte)((int)(this.hash >> 32)),
            (byte)((int)(this.hash >> 40)),
            (byte)((int)(this.hash >> 48)),
            (byte)((int)(this.hash >> 56))
         };
      }

      @Override
      public int asInt() {
         return (int)this.hash;
      }

      @Override
      public long asLong() {
         return this.hash;
      }

      @Override
      public long padToLong() {
         return this.hash;
      }

      @Override
      void writeBytesToImpl(byte[] var1, int var2, int var3) {
         for (int var4 = 0; var4 < var3; var4++) {
            var1[var2 + var4] = (byte)((int)(this.hash >> var4 * 8));
         }
      }

      @Override
      boolean equalsSameBits(HashCode var1) {
         return this.hash == var1.asLong();
      }
   }
}
