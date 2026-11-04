package org.codehaus.plexus.util;

public class Base64 {
   static final int CHUNK_SIZE = 76;
   static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
   static final int BASELENGTH = 255;
   static final int LOOKUPLENGTH = 64;
   static final int EIGHTBIT = 8;
   static final int SIXTEENBIT = 16;
   static final int TWENTYFOURBITGROUP = 24;
   static final int FOURBYTE = 4;
   static final int SIGN = -128;
   static final byte PAD = 61;
   private static byte[] base64Alphabet = new byte[255];
   private static byte[] lookUpBase64Alphabet = new byte[64];

   private static boolean isBase64(byte var0) {
      return var0 == 61 ? true : var0 >= 0 && base64Alphabet[var0] != -1;
   }

   public static boolean isArrayByteBase64(byte[] var0) {
      var0 = discardWhitespace(var0);
      int var1 = var0.length;
      if (var1 == 0) {
         return true;
      } else {
         for (byte var5 : var0) {
            if (!isBase64(var5)) {
               return false;
            }
         }

         return true;
      }
   }

   public static byte[] encodeBase64(byte[] var0) {
      return encodeBase64(var0, false);
   }

   public static byte[] encodeBase64Chunked(byte[] var0) {
      return encodeBase64(var0, true);
   }

   public byte[] decode(byte[] var1) {
      return decodeBase64(var1);
   }

   public static byte[] encodeBase64(byte[] var0, boolean var1) {
      int var2 = var0.length * 8;
      int var3 = var2 % 24;
      int var4 = var2 / 24;
      B var5 = null;
      int var6 = 0;
      int var7 = 0;
      if (var3 != 0) {
         var6 = (var4 + 1) * 4;
      } else {
         var6 = var4 * 4;
      }

      if (var1) {
         var7 = CHUNK_SEPARATOR.length == 0 ? 0 : (int)Math.ceil((double)((float)var6 / 76.0F));
         var6 += var7 * CHUNK_SEPARATOR.length;
      }

      var5 = new byte[var6];
      byte var8 = 0;
      byte var9 = 0;
      byte var10 = 0;
      byte var11 = 0;
      byte var12 = 0;
      int var13 = 0;
      int var14 = 0;
      int var15 = 0;
      int var16 = 76;
      int var17 = 0;

      for (var15 = 0; var15 < var4; var15++) {
         var14 = var15 * 3;
         var10 = var0[var14];
         var11 = var0[var14 + 1];
         var12 = var0[var14 + 2];
         var9 = (byte)(var11 & 15);
         var8 = (byte)(var10 & 3);
         byte var18 = (var10 & -128) == 0 ? (byte)(var10 >> 2) : (byte)(var10 >> 2 ^ 192);
         byte var19 = (var11 & -128) == 0 ? (byte)(var11 >> 4) : (byte)(var11 >> 4 ^ 240);
         byte var20 = (var12 & -128) == 0 ? (byte)(var12 >> 6) : (byte)(var12 >> 6 ^ 252);
         ((Object[])var5)[var13] = lookUpBase64Alphabet[var18];
         ((Object[])var5)[var13 + 1] = lookUpBase64Alphabet[var19 | var8 << 4];
         ((Object[])var5)[var13 + 2] = lookUpBase64Alphabet[var9 << 2 | var20];
         ((Object[])var5)[var13 + 3] = lookUpBase64Alphabet[var12 & 63];
         var13 += 4;
         if (var1 && var13 == var16) {
            System.arraycopy(CHUNK_SEPARATOR, 0, var5, var13, CHUNK_SEPARATOR.length);
            var16 = 76 * (++var17 + 1) + var17 * CHUNK_SEPARATOR.length;
            var13 += CHUNK_SEPARATOR.length;
         }
      }

      var14 = var15 * 3;
      if (var3 == 8) {
         var10 = var0[var14];
         var8 = (byte)(var10 & 3);
         byte var37 = (var10 & -128) == 0 ? (byte)(var10 >> 2) : (byte)(var10 >> 2 ^ 192);
         ((Object[])var5)[var13] = lookUpBase64Alphabet[var37];
         ((Object[])var5)[var13 + 1] = lookUpBase64Alphabet[var8 << 4];
         ((Object[])var5)[var13 + 2] = 61;
         ((Object[])var5)[var13 + 3] = 61;
      } else if (var3 == 16) {
         var10 = var0[var14];
         var11 = var0[var14 + 1];
         var9 = (byte)(var11 & 15);
         var8 = (byte)(var10 & 3);
         byte var38 = (var10 & -128) == 0 ? (byte)(var10 >> 2) : (byte)(var10 >> 2 ^ 192);
         byte var39 = (var11 & -128) == 0 ? (byte)(var11 >> 4) : (byte)(var11 >> 4 ^ 240);
         ((Object[])var5)[var13] = lookUpBase64Alphabet[var38];
         ((Object[])var5)[var13 + 1] = lookUpBase64Alphabet[var39 | var8 << 4];
         ((Object[])var5)[var13 + 2] = lookUpBase64Alphabet[var9 << 2];
         ((Object[])var5)[var13 + 3] = 61;
      }

      if (var1 && var17 < var7) {
         System.arraycopy(CHUNK_SEPARATOR, 0, var5, var6 - CHUNK_SEPARATOR.length, CHUNK_SEPARATOR.length);
      }

      return (byte[])var5;
   }

   public static byte[] decodeBase64(byte[] var0) {
      var0 = discardNonBase64(var0);
      if (var0.length == 0) {
         return new byte[0];
      } else {
         int var1 = var0.length / 4;
         B var2 = null;
         byte var3 = 0;
         byte var4 = 0;
         byte var5 = 0;
         byte var6 = 0;
         byte var7 = 0;
         byte var8 = 0;
         byte var9 = 0;
         int var10 = 0;
         int var11 = var0.length;

         while (var0[var11 - 1] == 61) {
            if (--var11 == 0) {
               return new byte[0];
            }
         }

         var2 = new byte[var11 - var1];

         for (int var22 = 0; var22 < var1; var22++) {
            var10 = var22 * 4;
            var7 = var0[var10 + 2];
            var8 = var0[var10 + 3];
            var3 = base64Alphabet[var0[var10]];
            var4 = base64Alphabet[var0[var10 + 1]];
            if (var7 != 61 && var8 != 61) {
               var5 = base64Alphabet[var7];
               var6 = base64Alphabet[var8];
               ((Object[])var2)[var9] = (byte)(var3 << 2 | var4 >> 4);
               ((Object[])var2)[var9 + 1] = (byte)((var4 & 15) << 4 | var5 >> 2 & 15);
               ((Object[])var2)[var9 + 2] = (byte)(var5 << 6 | var6);
            } else if (var7 == 61) {
               ((Object[])var2)[var9] = (byte)(var3 << 2 | var4 >> 4);
            } else if (var8 == 61) {
               var5 = base64Alphabet[var7];
               ((Object[])var2)[var9] = (byte)(var3 << 2 | var4 >> 4);
               ((Object[])var2)[var9 + 1] = (byte)((var4 & 15) << 4 | var5 >> 2 & 15);
            }

            var9 += 3;
         }

         return (byte[])var2;
      }
   }

   static byte[] discardWhitespace(byte[] var0) {
      byte[] var1 = new byte[var0.length];
      int var2 = 0;

      for (byte var6 : var0) {
         switch (var6) {
            default:
               var1[var2++] = var6;
            case 9:
            case 10:
            case 13:
            case 32:
         }
      }

      byte[] var7 = new byte[var2];
      System.arraycopy(var1, 0, var7, 0, var2);
      return var7;
   }

   static byte[] discardNonBase64(byte[] var0) {
      byte[] var1 = new byte[var0.length];
      int var2 = 0;

      for (byte var6 : var0) {
         if (isBase64(var6)) {
            var1[var2++] = var6;
         }
      }

      byte[] var7 = new byte[var2];
      System.arraycopy(var1, 0, var7, 0, var2);
      return var7;
   }

   public byte[] encode(byte[] var1) {
      return encodeBase64(var1, false);
   }

   static {
      for (int var0 = 0; var0 < 255; var0++) {
         base64Alphabet[var0] = -1;
      }

      for (int var2 = 90; var2 >= 65; var2--) {
         base64Alphabet[var2] = (byte)(var2 - 65);
      }

      for (int var3 = 122; var3 >= 97; var3--) {
         base64Alphabet[var3] = (byte)(var3 - 97 + 26);
      }

      for (int var4 = 57; var4 >= 48; var4--) {
         base64Alphabet[var4] = (byte)(var4 - 48 + 52);
      }

      base64Alphabet[43] = 62;
      base64Alphabet[47] = 63;

      for (int var5 = 0; var5 <= 25; var5++) {
         lookUpBase64Alphabet[var5] = (byte)(65 + var5);
      }

      int var6 = 26;

      for (int var1 = 0; var6 <= 51; var1++) {
         lookUpBase64Alphabet[var6] = (byte)(97 + var1);
         var6++;
      }

      var6 = 52;

      for (int var8 = 0; var6 <= 61; var8++) {
         lookUpBase64Alphabet[var6] = (byte)(48 + var8);
         var6++;
      }

      lookUpBase64Alphabet[62] = 43;
      lookUpBase64Alphabet[63] = 47;
   }
}
