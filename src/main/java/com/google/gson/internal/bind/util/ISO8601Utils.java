package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
   private static final String UTC_ID = "UTC";
   private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

   public static String format(Date var0) {
      return format(var0, false, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1) {
      return format(var0, var1, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1, TimeZone var2) {
      GregorianCalendar var3 = new GregorianCalendar(var2, Locale.US);
      var3.setTime(var0);
      int var4 = "yyyy-MM-ddThh:mm:ss".length();
      var4 += var1 ? ".sss".length() : 0;
      var4 += var2.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length();
      StringBuilder var5 = new StringBuilder(var4);
      padInt(var5, var3.get(1), "yyyy".length());
      var5.append('-');
      padInt(var5, var3.get(2) + 1, "MM".length());
      var5.append('-');
      padInt(var5, var3.get(5), "dd".length());
      var5.append('T');
      padInt(var5, var3.get(11), "hh".length());
      var5.append(':');
      padInt(var5, var3.get(12), "mm".length());
      var5.append(':');
      padInt(var5, var3.get(13), "ss".length());
      if (var1) {
         var5.append('.');
         padInt(var5, var3.get(14), "sss".length());
      }

      int var6 = var2.getOffset(var3.getTimeInMillis());
      if (var6 != 0) {
         int var7 = Math.abs(var6 / 60000 / 60);
         int var8 = Math.abs(var6 / 60000 % 60);
         var5.append((char)(var6 < 0 ? '-' : '+'));
         padInt(var5, var7, "hh".length());
         var5.append(':');
         padInt(var5, var8, "mm".length());
      } else {
         var5.append('Z');
      }

      return var5.toString();
   }

   public static Date parse(String var0, ParsePosition var1) throws ParseException {
      Object var2 = null;

      try {
         int var22 = var1.getIndex();
         int var23 = var22 + 4;
         int var30 = parseInt(var0, var22, var23);
         if (checkOffset(var0, var23, '-')) {
            var23++;
         }

         var22 = var23 + 2;
         int var31 = parseInt(var0, var23, var22);
         if (checkOffset(var0, var22, '-')) {
            var22++;
         }

         int var25 = var22 + 2;
         int var6 = parseInt(var0, var22, var25);
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         boolean var11 = checkOffset(var0, var25, 'T');
         if (!var11 && var0.length() <= var25) {
            GregorianCalendar var34 = new GregorianCalendar(var30, var31 - 1, var6);
            var1.setIndex(var25);
            return var34.getTime();
         }

         if (var11) {
            int var42 = ++var25;
            var22 = var25 + 2;
            var7 = parseInt(var0, var42, var22);
            if (checkOffset(var0, var22, ':')) {
               var22++;
            }

            var25 = var22 + 2;
            var8 = parseInt(var0, var22, var25);
            if (checkOffset(var0, var25, ':')) {
               var25++;
            }

            if (var0.length() > var25) {
               char var12 = var0.charAt(var25);
               if (var12 != 'Z' && var12 != '+' && var12 != '-') {
                  var42 = var25;
                  var25 += 2;
                  var9 = parseInt(var0, var42, var25);
                  if (var9 > 59 && var9 < 63) {
                     var9 = 59;
                  }

                  if (checkOffset(var0, var25, '.')) {
                     int var13 = indexOfNonDigit(var0, ++var25 + 1);
                     int var14 = Math.min(var13, var25 + 3);
                     int var15 = parseInt(var0, var25, var14);
                     switch (var14 - var25) {
                        case 1:
                           var10 = var15 * 100;
                           break;
                        case 2:
                           var10 = var15 * 10;
                           break;
                        default:
                           var10 = var15;
                     }

                     var25 = var13;
                  }
               }
            }
         }

         if (var0.length() <= var25) {
            throw new IllegalArgumentException("No time zone indicator");
         }

         TimeZone var32 = null;
         char var35 = var0.charAt(var25);
         if (var35 == 'Z') {
            var32 = TIMEZONE_UTC;
            var25++;
         } else {
            if (var35 != '+' && var35 != '-') {
               throw new IndexOutOfBoundsException("Invalid time zone indicator '" + var35 + "'");
            }

            String var36 = var0.substring(var25);
            var36 = var36.length() >= 5 ? var36 : var36 + "00";
            var25 += var36.length();
            if (!"+0000".equals(var36) && !"+00:00".equals(var36)) {
               String var39 = "GMT" + var36;
               var32 = TimeZone.getTimeZone(var39);
               String var16 = var32.getID();
               if (!var16.equals(var39)) {
                  String var17 = var16.replace(":", "");
                  if (!var17.equals(var39)) {
                     throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + var39 + " given, resolves to " + var32.getID());
                  }
               }
            } else {
               var32 = TIMEZONE_UTC;
            }
         }

         GregorianCalendar var38 = new GregorianCalendar(var32);
         var38.setLenient(false);
         var38.set(1, var30);
         var38.set(2, var31 - 1);
         var38.set(5, var6);
         var38.set(11, var7);
         var38.set(12, var8);
         var38.set(13, var9);
         var38.set(14, var10);
         var1.setIndex(var25);
         return var38.getTime();
      } catch (IndexOutOfBoundsException var18) {
         var2 = var18;
      } catch (NumberFormatException var19) {
         var2 = var19;
      } catch (IllegalArgumentException var20) {
         var2 = var20;
      }

      String var3 = var0 == null ? null : '"' + var0 + '"';
      String var4 = var2.getMessage();
      if (var4 == null || var4.isEmpty()) {
         var4 = "(" + var2.getClass().getName() + ")";
      }

      ParseException var5 = new ParseException("Failed to parse date [" + var3 + "]: " + var4, var1.getIndex());
      var5.initCause((Throwable)var2);
      throw var5;
   }

   private static boolean checkOffset(String var0, int var1, char var2) {
      return var1 < var0.length() && var0.charAt(var1) == var2;
   }

   private static int parseInt(String var0, int var1, int var2) throws NumberFormatException {
      if (var1 >= 0 && var2 <= var0.length() && var1 <= var2) {
         int var3 = var1;
         int var4 = 0;
         if (var1 < var2) {
            var3 = var1 + 1;
            int var5 = Character.digit(var0.charAt(var1), 10);
            if (var5 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 = -var5;
         }

         while (var3 < var2) {
            int var7 = Character.digit(var0.charAt(var3++), 10);
            if (var7 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 *= 10;
            var4 -= var7;
         }

         return -var4;
      } else {
         throw new NumberFormatException(var0);
      }
   }

   private static void padInt(StringBuilder var0, int var1, int var2) {
      String var3 = Integer.toString(var1);

      for (int var4 = var2 - var3.length(); var4 > 0; var4--) {
         var0.append('0');
      }

      var0.append(var3);
   }

   private static int indexOfNonDigit(String var0, int var1) {
      for (int var2 = var1; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         if (var3 < '0' || var3 > '9') {
            return var2;
         }
      }

      return var0.length();
   }
}
