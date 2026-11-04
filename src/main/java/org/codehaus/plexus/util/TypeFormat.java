package org.codehaus.plexus.util;

public final class TypeFormat {
   private static final char[] DIGITS = new char[]{
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z'
   };
   private static final int[] INT_POW_10 = new int[10];
   private static final long[] LONG_POW_10;
   private static final double LOG_10;
   private static final float FLOAT_RELATIVE_ERROR;
   private static final double DOUBLE_RELATIVE_ERROR;
   private static String[] LEADING_ZEROS;
   private static final double[] DOUBLE_POW_10;

   private TypeFormat() {
   }

   public static int indexOf(CharSequence var0, CharSequence var1, int var2) {
      int var3 = var0.length();
      var2 = Math.max(0, var2);
      if (var3 == 0) {
         return Math.min(0, var2);
      } else {
         char var4 = var0.charAt(0);
         int var5 = var1.length() - var3;

         for (int var6 = var2; var6 <= var5; var6++) {
            if (var1.charAt(var6) == var4) {
               boolean var7 = true;

               for (int var8 = 1; var8 < var3; var8++) {
                  if (var1.charAt(var6 + var8) != var0.charAt(var8)) {
                     var7 = false;
                     break;
                  }
               }

               if (var7) {
                  return var6;
               }
            }
         }

         return -1;
      }
   }

   public static boolean parseBoolean(CharSequence var0) {
      return var0.length() == 4
         && (var0.charAt(0) == 't' || var0.charAt(0) == 'T')
         && (var0.charAt(1) == 'r' || var0.charAt(1) == 'R')
         && (var0.charAt(2) == 'u' || var0.charAt(2) == 'U')
         && (var0.charAt(3) == 'e' || var0.charAt(3) == 'E');
   }

   public static short parseShort(CharSequence var0) {
      return parseShort(var0, 10);
   }

   public static short parseShort(CharSequence var0, int var1) {
      try {
         boolean var2 = var0.charAt(0) == '-';
         int var3 = 0;
         int var4 = var2 ? -32768 : -32767;
         int var5 = var4 / var1;
         int var6 = var0.length();
         int var7 = !var2 && var0.charAt(0) != '+' ? 0 : 1;

         do {
            int var8 = Character.digit(var0.charAt(var7), var1);
            int var9 = var3 * var1;
            if (var8 < 0 || var3 < var5 || var9 < var4 + var8) {
               throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
            }

            var3 = var9 - var8;
         } while (++var7 < var6);

         return (short)(var2 ? var3 : -var3);
      } catch (IndexOutOfBoundsException var10) {
         throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
      }
   }

   public static int parseInt(CharSequence var0) {
      return parseInt(var0, 10);
   }

   public static int parseInt(CharSequence var0, int var1) {
      try {
         boolean var2 = var0.charAt(0) == '-';
         int var3 = 0;
         int var4 = var2 ? Integer.MIN_VALUE : -2147483647;
         int var5 = var4 / var1;
         int var6 = var0.length();
         int var7 = !var2 && var0.charAt(0) != '+' ? 0 : 1;

         do {
            int var8 = Character.digit(var0.charAt(var7), var1);
            int var9 = var3 * var1;
            if (var8 < 0 || var3 < var5 || var9 < var4 + var8) {
               throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
            }

            var3 = var9 - var8;
         } while (++var7 < var6);

         return var2 ? var3 : -var3;
      } catch (IndexOutOfBoundsException var10) {
         throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
      }
   }

   public static long parseLong(CharSequence var0) {
      return parseLong(var0, 10);
   }

   public static long parseLong(CharSequence var0, int var1) {
      try {
         boolean var2 = var0.charAt(0) == '-';
         long var3 = 0L;
         long var5 = var2 ? Long.MIN_VALUE : -9223372036854775807L;
         long var7 = var5 / (long)var1;
         int var9 = var0.length();
         int var10 = !var2 && var0.charAt(0) != '+' ? 0 : 1;

         do {
            int var11 = Character.digit(var0.charAt(var10), var1);
            long var12 = var3 * (long)var1;
            if (var11 < 0 || var3 < var7 || var12 < var5 + (long)var11) {
               throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
            }

            var3 = var12 - (long)var11;
         } while (++var10 < var9);

         return var2 ? var3 : -var3;
      } catch (IndexOutOfBoundsException var14) {
         throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
      }
   }

   public static float parseFloat(CharSequence var0) {
      double var1 = parseDouble(var0);
      if (var1 >= Float.MIN_VALUE && var1 <= Float.MAX_VALUE) {
         return (float)var1;
      } else {
         throw new NumberFormatException("Float overflow for input characters: \"" + var0.toString() + "\"");
      }
   }

   public static double parseDouble(CharSequence var0) throws NumberFormatException {
      try {
         int var1 = var0.length();
         double var2 = 0.0;
         int var4 = 0;
         boolean var5 = var0.charAt(0) == '-';
         int var6 = !var5 && var0.charAt(0) != '+' ? 0 : 1;
         if (var0.charAt(var6) == 'N' || var0.charAt(var6) == 'I') {
            if (var0.toString().equals("NaN")) {
               return Double.NaN;
            }

            if (var0.subSequence(var6, var1).toString().equals("Infinity")) {
               return var5 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
         }

         boolean var7 = false;

         do {
            char var8 = var0.charAt(var6);
            if (var8 == '.' && !var7) {
               var7 = true;
            } else {
               if (var8 == 'e' || var8 == 'E') {
                  break;
               }

               if (var8 < '0' || var8 > '9') {
                  throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
               }

               var2 = var2 * 10.0 + (double)(var8 - '0');
               if (var7) {
                  var4--;
               }
            }
         } while (++var6 < var1);

         var2 = var5 ? -var2 : var2;
         if (var6 < var1) {
            var6++;
            boolean var15 = var0.charAt(var6) == '-';
            var6 = !var15 && var0.charAt(var6) != '+' ? var6 : var6 + 1;
            int var9 = 0;

            do {
               char var10 = var0.charAt(var6);
               if (var10 < '0' || var10 > '9') {
                  throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
               }

               var9 = var9 * 10 + (var10 - '0');
               if (var9 > 10000000) {
                  var9 = 10000000;
               }
            } while (++var6 < var1);

            var4 += var15 ? -var9 : var9;
         }

         return multE(var2, var4);
      } catch (IndexOutOfBoundsException var11) {
         throw new NumberFormatException("For input characters: \"" + var0.toString() + "\"");
      }
   }

   public static StringBuffer format(boolean var0, StringBuffer var1) {
      return var0 ? var1.append("true") : var1.append("false");
   }

   public static StringBuffer format(short var0, StringBuffer var1) {
      return format((int)var0, var1);
   }

   public static StringBuffer format(short var0, int var1, StringBuffer var2) {
      return format((int)var0, var1, var2);
   }

   public static StringBuffer format(int var0, StringBuffer var1) {
      if (var0 <= 0) {
         if (var0 == Integer.MIN_VALUE) {
            return var1.append("-2147483648");
         }

         if (var0 == 0) {
            return var1.append('0');
         }

         var0 = -var0;
         var1.append('-');
      }

      int var2 = 1;

      while (var2 < 10 && var0 >= INT_POW_10[var2]) {
         var2++;
      }

      var2--;

      while (var2 >= 0) {
         int var3 = INT_POW_10[var2];
         int var4 = var0 / var3;
         var0 -= var4 * var3;
         var1.append(DIGITS[var4]);
         var2--;
      }

      return var1;
   }

   public static StringBuffer format(int var0, int var1, StringBuffer var2) {
      if (var1 == 10) {
         return format(var0, var2);
      } else if (var1 >= 2 && var1 <= 36) {
         if (var0 < 0) {
            var2.append('-');
         } else {
            var0 = -var0;
         }

         format2(var0, var1, var2);
         return var2;
      } else {
         throw new IllegalArgumentException("radix: " + var1);
      }
   }

   private static void format2(int var0, int var1, StringBuffer var2) {
      if (var0 <= -var1) {
         format2(var0 / var1, var1, var2);
         var2.append(DIGITS[-(var0 % var1)]);
      } else {
         var2.append(DIGITS[-var0]);
      }
   }

   public static StringBuffer format(long var0, StringBuffer var2) {
      if (var0 <= 0L) {
         if (var0 == Long.MIN_VALUE) {
            return var2.append("-9223372036854775808");
         }

         if (var0 == 0L) {
            return var2.append('0');
         }

         var0 = -var0;
         var2.append('-');
      }

      int var3 = 1;

      while (var3 < 19 && var0 >= LONG_POW_10[var3]) {
         var3++;
      }

      var3--;

      while (var3 >= 0) {
         long var4 = LONG_POW_10[var3];
         int var6 = (int)(var0 / var4);
         var0 -= (long)var6 * var4;
         var2.append(DIGITS[var6]);
         var3--;
      }

      return var2;
   }

   public static StringBuffer format(long var0, int var2, StringBuffer var3) {
      if (var2 == 10) {
         return format(var0, var3);
      } else if (var2 >= 2 && var2 <= 36) {
         if (var0 < 0L) {
            var3.append('-');
         } else {
            var0 = -var0;
         }

         format2(var0, var2, var3);
         return var3;
      } else {
         throw new IllegalArgumentException("radix: " + var2);
      }
   }

   private static void format2(long var0, int var2, StringBuffer var3) {
      if (var0 <= (long)(-var2)) {
         format2(var0 / (long)var2, var2, var3);
         var3.append(DIGITS[(int)(-(var0 % (long)var2))]);
      } else {
         var3.append(DIGITS[(int)(-var0)]);
      }
   }

   public static StringBuffer format(float var0, StringBuffer var1) {
      return format(var0, 0.0F, var1);
   }

   public static StringBuffer format(float var0, float var1, StringBuffer var2) {
      boolean var3;
      if (var1 > 0.0F) {
         var3 = true;
      } else {
         if (var1 != 0.0F) {
            throw new IllegalArgumentException("precision: Negative values not allowed");
         }

         if (var0 == 0.0F) {
            return var2.append("0.0");
         }

         var3 = false;
         var1 = Math.max(Math.abs(var0 * FLOAT_RELATIVE_ERROR), Float.MIN_VALUE);
      }

      return format((double)var0, (double)var1, var3, var2);
   }

   public static StringBuffer format(double var0, StringBuffer var2) {
      return format(var0, 0.0, var2);
   }

   public static StringBuffer format(double var0, int var2, StringBuffer var3) {
      if (var2 >= 1 && var2 <= 19) {
         double var4 = Math.abs(var0 / DOUBLE_POW_10[var2 - 1]);
         return format(var0, var4, var3);
      } else {
         throw new IllegalArgumentException("digits: " + var2 + " is not in range [1 .. 19]");
      }
   }

   public static StringBuffer format(double var0, double var2, StringBuffer var4) {
      boolean var5 = false;
      if (var2 > 0.0) {
         var5 = true;
      } else if (var2 == 0.0) {
         if (var0 == 0.0) {
            return var4.append("0.0");
         }

         var2 = Math.max(Math.abs(var0 * DOUBLE_RELATIVE_ERROR), Double.MIN_VALUE);
      } else if (var2 < 0.0) {
         throw new IllegalArgumentException("precision: Negative values not allowed");
      }

      return format(var0, var2, var5, var4);
   }

   private static StringBuffer format(double var0, double var2, boolean var4, StringBuffer var5) {
      if (Double.isNaN(var0)) {
         return var5.append("NaN");
      } else if (Double.isInfinite(var0)) {
         return var0 >= 0.0 ? var5.append("Infinity") : var5.append("-Infinity");
      } else {
         if (var0 < 0.0) {
            var0 = -var0;
            var5.append('-');
         }

         int var6 = (int)Math.floor(Math.log(var2) / LOG_10);
         double var7 = multE(var0, -var6);
         if (var7 >= 9.223372E18F) {
            throw new IllegalArgumentException("Specified precision would result in too many digits");
         } else {
            int var9 = var5.length();
            format(Math.round(var7), var5);
            int var10 = var5.length() - var9;
            int var11 = var10 + var6;
            boolean var12 = false;
            if (var11 <= -LEADING_ZEROS.length || var11 > var10) {
               var5.insert(var9 + 1, '.');
               var12 = true;
            } else if (var11 > 0) {
               var5.insert(var9 + var11, '.');
            } else {
               var5.insert(var9, LEADING_ZEROS[-var11]);
            }

            if (!var4) {
               int var13 = var5.length();

               while (var5.charAt(--var13) == '0') {
               }

               var5.setLength(var13 + 1);
            }

            if (var5.charAt(var5.length() - 1) == '.') {
               if (var4) {
                  var5.setLength(var5.length() - 1);
               } else {
                  var5.append('0');
               }
            }

            if (var12) {
               var5.append('E');
               format(var11 - 1, var5);
            }

            return var5;
         }
      }
   }

   private static final double multE(double var0, int var2) {
      if (var2 >= 0) {
         if (var2 <= 308) {
            return var0 * DOUBLE_POW_10[var2];
         } else {
            var0 *= 1.0E21;
            var2 = Math.min(308, var2 - 21);
            return var0 * DOUBLE_POW_10[var2];
         }
      } else if (var2 >= -308) {
         return var0 / DOUBLE_POW_10[-var2];
      } else {
         var0 /= 1.0E21;
         var2 = Math.max(-308, var2 + 21);
         return var0 / DOUBLE_POW_10[-var2];
      }
   }

   static {
      byte var0 = 1;

      for (int var1 = 0; var1 < 10; var1++) {
         INT_POW_10[var1] = var0;
         var0 *= 10;
      }

      LONG_POW_10 = new long[19];
      long var3 = 1L;

      for (int var2 = 0; var2 < 19; var2++) {
         LONG_POW_10[var2] = var3;
         var3 *= 10L;
      }

      LOG_10 = Math.log(10.0);
      FLOAT_RELATIVE_ERROR = (float)Math.pow(2.0, -24.0);
      DOUBLE_RELATIVE_ERROR = Math.pow(2.0, -53.0);
      LEADING_ZEROS = new String[]{"0.", "0.0", "0.00"};
      DOUBLE_POW_10 = new double[]{
         1.0,
         10.0,
         100.0,
         1000.0,
         10000.0,
         100000.0,
         1000000.0,
         1.0E7,
         1.0E8,
         1.0E9,
         1.0E10,
         1.0E11,
         1.0E12,
         1.0E13,
         1.0E14,
         1.0E15,
         1.0E16,
         1.0E17,
         1.0E18,
         1.0E19,
         1.0E20,
         1.0E21,
         1.0E22,
         1.0E23,
         1.0E24,
         1.0E25,
         1.0E26,
         1.0E27,
         1.0E28,
         1.0E29,
         1.0E30,
         1.0E31,
         1.0E32,
         1.0E33,
         1.0E34,
         1.0E35,
         1.0E36,
         1.0E37,
         1.0E38,
         1.0E39,
         1.0E40,
         1.0E41,
         1.0E42,
         1.0E43,
         1.0E44,
         1.0E45,
         1.0E46,
         1.0E47,
         1.0E48,
         1.0E49,
         1.0E50,
         1.0E51,
         1.0E52,
         1.0E53,
         1.0E54,
         1.0E55,
         1.0E56,
         1.0E57,
         1.0E58,
         1.0E59,
         1.0E60,
         1.0E61,
         1.0E62,
         1.0E63,
         1.0E64,
         1.0E65,
         1.0E66,
         1.0E67,
         1.0E68,
         1.0E69,
         1.0E70,
         1.0E71,
         1.0E72,
         1.0E73,
         1.0E74,
         1.0E75,
         1.0E76,
         1.0E77,
         1.0E78,
         1.0E79,
         1.0E80,
         1.0E81,
         1.0E82,
         1.0E83,
         1.0E84,
         1.0E85,
         1.0E86,
         1.0E87,
         1.0E88,
         1.0E89,
         1.0E90,
         1.0E91,
         1.0E92,
         1.0E93,
         1.0E94,
         1.0E95,
         1.0E96,
         1.0E97,
         1.0E98,
         1.0E99,
         1.0E100,
         1.0E101,
         1.0E102,
         1.0E103,
         1.0E104,
         1.0E105,
         1.0E106,
         1.0E107,
         1.0E108,
         1.0E109,
         1.0E110,
         1.0E111,
         1.0E112,
         1.0E113,
         1.0E114,
         1.0E115,
         1.0E116,
         1.0E117,
         1.0E118,
         1.0E119,
         1.0E120,
         1.0E121,
         1.0E122,
         1.0E123,
         1.0E124,
         1.0E125,
         1.0E126,
         1.0E127,
         1.0E128,
         1.0E129,
         1.0E130,
         1.0E131,
         1.0E132,
         1.0E133,
         1.0E134,
         1.0E135,
         1.0E136,
         1.0E137,
         1.0E138,
         1.0E139,
         1.0E140,
         1.0E141,
         1.0E142,
         1.0E143,
         1.0E144,
         1.0E145,
         1.0E146,
         1.0E147,
         1.0E148,
         1.0E149,
         1.0E150,
         1.0E151,
         1.0E152,
         1.0E153,
         1.0E154,
         1.0E155,
         1.0E156,
         1.0E157,
         1.0E158,
         1.0E159,
         1.0E160,
         1.0E161,
         1.0E162,
         1.0E163,
         1.0E164,
         1.0E165,
         1.0E166,
         1.0E167,
         1.0E168,
         1.0E169,
         1.0E170,
         1.0E171,
         1.0E172,
         1.0E173,
         1.0E174,
         1.0E175,
         1.0E176,
         1.0E177,
         1.0E178,
         1.0E179,
         1.0E180,
         1.0E181,
         1.0E182,
         1.0E183,
         1.0E184,
         1.0E185,
         1.0E186,
         1.0E187,
         1.0E188,
         1.0E189,
         1.0E190,
         1.0E191,
         1.0E192,
         1.0E193,
         1.0E194,
         1.0E195,
         1.0E196,
         1.0E197,
         1.0E198,
         1.0E199,
         1.0E200,
         1.0E201,
         1.0E202,
         1.0E203,
         1.0E204,
         1.0E205,
         1.0E206,
         1.0E207,
         1.0E208,
         1.0E209,
         1.0E210,
         1.0E211,
         1.0E212,
         1.0E213,
         1.0E214,
         1.0E215,
         1.0E216,
         1.0E217,
         1.0E218,
         1.0E219,
         1.0E220,
         1.0E221,
         1.0E222,
         1.0E223,
         1.0E224,
         1.0E225,
         1.0E226,
         1.0E227,
         1.0E228,
         1.0E229,
         1.0E230,
         1.0E231,
         1.0E232,
         1.0E233,
         1.0E234,
         1.0E235,
         1.0E236,
         1.0E237,
         1.0E238,
         1.0E239,
         1.0E240,
         1.0E241,
         1.0E242,
         1.0E243,
         1.0E244,
         1.0E245,
         1.0E246,
         1.0E247,
         1.0E248,
         1.0E249,
         1.0E250,
         1.0E251,
         1.0E252,
         1.0E253,
         1.0E254,
         1.0E255,
         1.0E256,
         1.0E257,
         1.0E258,
         1.0E259,
         1.0E260,
         1.0E261,
         1.0E262,
         1.0E263,
         1.0E264,
         1.0E265,
         1.0E266,
         1.0E267,
         1.0E268,
         1.0E269,
         1.0E270,
         1.0E271,
         1.0E272,
         1.0E273,
         1.0E274,
         1.0E275,
         1.0E276,
         1.0E277,
         1.0E278,
         1.0E279,
         1.0E280,
         1.0E281,
         1.0E282,
         1.0E283,
         1.0E284,
         1.0E285,
         1.0E286,
         1.0E287,
         1.0E288,
         1.0E289,
         1.0E290,
         1.0E291,
         1.0E292,
         1.0E293,
         1.0E294,
         1.0E295,
         1.0E296,
         1.0E297,
         1.0E298,
         1.0E299,
         1.0E300,
         1.0E301,
         1.0E302,
         1.0E303,
         1.0E304,
         1.0E305,
         1.0E306,
         1.0E307,
         1.0E308
      };
   }
}
