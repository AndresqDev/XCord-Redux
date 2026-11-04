package io.github.waterfallmc.waterfall.utils;

import java.util.Arrays;
import java.util.Objects;

public class Hex {
   private static final char[] ENCODE_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private static final byte[] DECODE_TABLE = new byte[128];

   public static byte[] decode(CharSequence var0) {
      byte[] var1 = new byte[var0.length() >> 1];
      decode(var0, 0, var1, 0, var1.length);
      return var1;
   }

   public static void decode(final char[] var0, final int var1, byte[] var2, int var3, int var4) {
      decode(new CharSequence() {
         @Override
         public int length() {
            return var0.length;
         }

         @Override
         public char charAt(int var1x) {
            return var0[var1x];
         }

         @Override
         public CharSequence subSequence(int var1x, int var2) {
            return this.toString().substring(var1x, var2);
         }

         @Override
         public String toString() {
            return new String(var0, var1, var0.length);
         }
      });
   }

   public static void decode(CharSequence var0, int var1, byte[] var2, int var3, int var4) {
      Objects.requireNonNull(var0, "Null chars");
      Objects.requireNonNull(var2, "Null destination");
      int var5 = var0.length();
      if ((var5 & 1) != 0) {
         throw new IllegalArgumentException("Odd number of characters: " + var5);
      } else if (var4 < var5 - var1 >> 1) {
         throw new IllegalArgumentException("Too many bytes to fill with " + var5 + " characters: " + var4);
      } else if (var3 >= 0 && var1 >= 0 && var4 >= 0 && var4 * 2 <= var5 - var1 && var4 <= var2.length - var3) {
         int var6 = 0;

         for (int var7 = var1; var6 < var4; var6++) {
            char var8 = var0.charAt(var7++);
            char var9 = var0.charAt(var7++);
            var2[var6 + var3] = (byte)(toDigit(var8) << 4 | toDigit(var9));
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public static String encodeString(byte[] var0) {
      return new String(encode(var0));
   }

   public static char[] encode(byte[] var0) {
      char[] var1 = new char[var0.length << 1];
      encode(var1, 0, var0, 0, var0.length);
      return var1;
   }

   public static void encode(char[] var0, int var1, byte[] var2, int var3, int var4) {
      Objects.requireNonNull(var0, "Null chars");
      Objects.requireNonNull(var2, "Null bytes");
      if (var3 < 0 || var1 < 0 || var4 < 0 || var4 * 2 > var0.length - var1 || var4 > var2.length - var3) {
         throw new IndexOutOfBoundsException();
      } else if (var4 != 0) {
         int var5 = 0;

         for (int var6 = var1; var5 < var4; var5++) {
            byte var7 = var2[var5 + var3];
            var0[var6++] = fromDigit((byte)(var7 >> 4 & 15));
            var0[var6++] = fromDigit((byte)(var7 & 15));
         }
      }
   }

   public static byte toDigit(char var0) {
      byte var1;
      if (var0 < DECODE_TABLE.length) {
         var1 = DECODE_TABLE[var0];
      } else {
         var1 = -1;
      }

      if (var1 < 0) {
         throw new IllegalArgumentException("Invalid character " + var0);
      } else {
         return var1;
      }
   }

   private static char fromDigit(byte var0) {
      assert (var0 & 15) == var0 : "Out of range " + var0;

      return ENCODE_TABLE[var0];
   }

   static {
      Arrays.fill(DECODE_TABLE, (byte)-1);

      for (int var0 = 0; var0 < ENCODE_TABLE.length; var0++) {
         char var1 = ENCODE_TABLE[var0];
         DECODE_TABLE[var1] = (byte)var0;
         char var2;
         if ((var2 = Character.toUpperCase(var1)) != var1) {
            DECODE_TABLE[var2] = (byte)var0;
         }
      }
   }
}
