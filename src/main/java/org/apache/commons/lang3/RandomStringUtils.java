package org.apache.commons.lang3;

import java.util.Random;

public class RandomStringUtils {
   private static final Random RANDOM = new Random();

   public static String random(int var0) {
      return random(var0, false, false);
   }

   public static String randomAscii(int var0) {
      return random(var0, 32, 127, false, false);
   }

   public static String randomAscii(int var0, int var1) {
      return randomAscii(RandomUtils.nextInt(var0, var1));
   }

   public static String randomAlphabetic(int var0) {
      return random(var0, true, false);
   }

   public static String randomAlphabetic(int var0, int var1) {
      return randomAlphabetic(RandomUtils.nextInt(var0, var1));
   }

   public static String randomAlphanumeric(int var0) {
      return random(var0, true, true);
   }

   public static String randomAlphanumeric(int var0, int var1) {
      return randomAlphanumeric(RandomUtils.nextInt(var0, var1));
   }

   public static String randomGraph(int var0) {
      return random(var0, 33, 126, false, false);
   }

   public static String randomGraph(int var0, int var1) {
      return randomGraph(RandomUtils.nextInt(var0, var1));
   }

   public static String randomNumeric(int var0) {
      return random(var0, false, true);
   }

   public static String randomNumeric(int var0, int var1) {
      return randomNumeric(RandomUtils.nextInt(var0, var1));
   }

   public static String randomPrint(int var0) {
      return random(var0, 32, 126, false, false);
   }

   public static String randomPrint(int var0, int var1) {
      return randomPrint(RandomUtils.nextInt(var0, var1));
   }

   public static String random(int var0, boolean var1, boolean var2) {
      return random(var0, 0, 0, var1, var2);
   }

   public static String random(int var0, int var1, int var2, boolean var3, boolean var4) {
      return random(var0, var1, var2, var3, var4, null, RANDOM);
   }

   public static String random(int var0, int var1, int var2, boolean var3, boolean var4, char... var5) {
      return random(var0, var1, var2, var3, var4, var5, RANDOM);
   }

   public static String random(int var0, int var1, int var2, boolean var3, boolean var4, char[] var5, Random var6) {
      if (var0 == 0) {
         return "";
      } else if (var0 < 0) {
         throw new IllegalArgumentException("Requested random string length " + var0 + " is less than 0.");
      } else if (var5 != null && var5.length == 0) {
         throw new IllegalArgumentException("The chars array must not be empty");
      } else {
         if (var1 == 0 && var2 == 0) {
            if (var5 != null) {
               var2 = var5.length;
            } else if (!var3 && !var4) {
               var2 = 1114111;
            } else {
               var2 = 123;
               var1 = 32;
            }
         } else if (var2 <= var1) {
            throw new IllegalArgumentException("Parameter end (" + var2 + ") must be greater than start (" + var1 + ")");
         }

         byte var7 = 48;
         byte var8 = 65;
         if (var5 != null || (!var4 || var2 > 48) && (!var3 || var2 > 65)) {
            StringBuilder var9 = new StringBuilder(var0);
            int var10 = var2 - var1;

            while (var0-- != 0) {
               int var11;
               if (var5 == null) {
                  var11 = var6.nextInt(var10) + var1;
                  switch (Character.getType(var11)) {
                     case 0:
                     case 18:
                     case 19:
                        var0++;
                        continue;
                  }
               } else {
                  var11 = var5[var6.nextInt(var10) + var1];
               }

               int var12 = Character.charCount(var11);
               if (var0 == 0 && var12 > 1) {
                  var0++;
               } else if (var3 && Character.isLetter(var11) || var4 && Character.isDigit(var11) || !var3 && !var4) {
                  var9.appendCodePoint(var11);
                  if (var12 == 2) {
                     var0--;
                  }
               } else {
                  var0++;
               }
            }

            return var9.toString();
         } else {
            throw new IllegalArgumentException(
               "Parameter end (" + var2 + ") must be greater then (" + 48 + ") for generating digits or greater then (" + 65 + ") for generating letters."
            );
         }
      }
   }

   public static String random(int var0, String var1) {
      return var1 == null ? random(var0, 0, 0, false, false, null, RANDOM) : random(var0, var1.toCharArray());
   }

   public static String random(int var0, char... var1) {
      return var1 == null ? random(var0, 0, 0, false, false, null, RANDOM) : random(var0, 0, var1.length, false, false, var1, RANDOM);
   }
}
