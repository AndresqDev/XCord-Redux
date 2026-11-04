package org.codehaus.plexus.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class StringUtils {
   public static String clean(String var0) {
      return var0 == null ? "" : var0.trim();
   }

   public static String trim(String var0) {
      return var0 == null ? null : var0.trim();
   }

   public static String deleteWhitespace(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = var0.length();

      for (int var3 = 0; var3 < var2; var3++) {
         if (!Character.isWhitespace(var0.charAt(var3))) {
            var1.append(var0.charAt(var3));
         }
      }

      return var1.toString();
   }

   public static boolean isNotEmpty(String var0) {
      return var0 != null && !var0.isEmpty();
   }

   public static boolean isEmpty(String var0) {
      return var0 == null || var0.trim().isEmpty();
   }

   public static boolean isBlank(String var0) {
      int var1;
      if (var0 != null && (var1 = var0.length()) != 0) {
         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isWhitespace(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isNotBlank(String var0) {
      return !isBlank(var0);
   }

   @Deprecated
   public static boolean equals(String var0, String var1) {
      return Objects.equals(var0, var1);
   }

   public static boolean equalsIgnoreCase(String var0, String var1) {
      return var0 == null ? var1 == null : var0.equalsIgnoreCase(var1);
   }

   public static int indexOfAny(String var0, String[] var1) {
      if (var0 != null && var1 != null) {
         int var2 = var1.length;
         int var3 = Integer.MAX_VALUE;

         for (String var8 : var1) {
            int var4 = var0.indexOf(var8);
            if (var4 != -1 && var4 < var3) {
               var3 = var4;
            }
         }

         return var3 == Integer.MAX_VALUE ? -1 : var3;
      } else {
         return -1;
      }
   }

   public static int lastIndexOfAny(String var0, String[] var1) {
      if (var0 != null && var1 != null) {
         int var2 = -1;

         for (String var7 : var1) {
            int var3 = var0.lastIndexOf(var7);
            if (var3 > var2) {
               var2 = var3;
            }
         }

         return var2;
      } else {
         return -1;
      }
   }

   public static String substring(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 < 0) {
            var1 += var0.length();
         }

         if (var1 < 0) {
            var1 = 0;
         }

         return var1 > var0.length() ? "" : var0.substring(var1);
      }
   }

   public static String substring(String var0, int var1, int var2) {
      if (var0 == null) {
         return null;
      } else {
         if (var2 < 0) {
            var2 += var0.length();
         }

         if (var1 < 0) {
            var1 += var0.length();
         }

         if (var2 > var0.length()) {
            var2 = var0.length();
         }

         if (var1 > var2) {
            return "";
         } else {
            if (var1 < 0) {
               var1 = 0;
            }

            if (var2 < 0) {
               var2 = 0;
            }

            return var0.substring(var1, var2);
         }
      }
   }

   public static String left(String var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Requested String length " + var1 + " is less than zero");
      } else {
         return var0 != null && var0.length() > var1 ? var0.substring(0, var1) : var0;
      }
   }

   public static String right(String var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Requested String length " + var1 + " is less than zero");
      } else {
         return var0 != null && var0.length() > var1 ? var0.substring(var0.length() - var1) : var0;
      }
   }

   public static String mid(String var0, int var1, int var2) {
      if (var1 >= 0 && (var0 == null || var1 <= var0.length())) {
         if (var2 < 0) {
            throw new IllegalArgumentException("Requested String length " + var2 + " is less than zero");
         } else if (var0 == null) {
            return null;
         } else {
            return var0.length() <= var1 + var2 ? var0.substring(var1) : var0.substring(var1, var1 + var2);
         }
      } else {
         throw new StringIndexOutOfBoundsException("String index " + var1 + " is out of bounds");
      }
   }

   public static String[] split(String var0) {
      return split(var0, null, -1);
   }

   public static String[] split(String var0, String var1) {
      return split(var0, var1, -1);
   }

   public static String[] split(String var0, String var1, int var2) {
      StringTokenizer var3;
      if (var1 == null) {
         var3 = new StringTokenizer(var0);
      } else {
         var3 = new StringTokenizer(var0, var1);
      }

      int var4 = var3.countTokens();
      if (var2 > 0 && var4 > var2) {
         var4 = var2;
      }

      String[] var5 = new String[var4];
      int var6 = 0;

      for (int var8 = 0; var3.hasMoreTokens(); var6++) {
         if (var2 > 0 && var6 == var4 - 1) {
            String var9 = var3.nextToken();
            int var10 = var0.indexOf(var9, var8);
            var5[var6] = var0.substring(var10);
            break;
         }

         var5[var6] = var3.nextToken();
         int var7 = var0.indexOf(var5[var6], var8);
         var8 = var7 + var5[var6].length();
      }

      return var5;
   }

   public static String concatenate(Object[] var0) {
      return join(var0, "");
   }

   public static String join(Object[] var0, String var1) {
      if (var1 == null) {
         var1 = "";
      }

      int var2 = var0.length;
      int var3 = var2 == 0 ? 0 : (var0[0].toString().length() + var1.length()) * var2;
      StringBuilder var4 = new StringBuilder(var3);

      for (int var5 = 0; var5 < var2; var5++) {
         if (var5 > 0) {
            var4.append(var1);
         }

         var4.append(var0[var5]);
      }

      return var4.toString();
   }

   public static String join(Iterator<?> var0, String var1) {
      if (var1 == null) {
         var1 = "";
      }

      StringBuilder var2 = new StringBuilder(256);

      while (var0.hasNext()) {
         var2.append(var0.next());
         if (var0.hasNext()) {
            var2.append(var1);
         }
      }

      return var2.toString();
   }

   public static String replaceOnce(String var0, char var1, char var2) {
      return replace(var0, var1, var2, 1);
   }

   public static String replace(String var0, char var1, char var2) {
      return replace(var0, var1, var2, -1);
   }

   public static String replace(String var0, char var1, char var2, int var3) {
      return replace(var0, String.valueOf(var1), String.valueOf(var2), var3);
   }

   public static String replaceOnce(String var0, String var1, String var2) {
      return replace(var0, var1, var2, 1);
   }

   public static String replace(String var0, String var1, String var2) {
      return replace(var0, var1, var2, -1);
   }

   public static String replace(String var0, String var1, String var2, int var3) {
      if (var0 != null && var1 != null && var2 != null && var1.length() != 0) {
         StringBuilder var4 = new StringBuilder(var0.length());
         int var5 = 0;

         int var6;
         while ((var6 = var0.indexOf(var1, var5)) != -1) {
            var4.append(var0, var5, var6).append(var2);
            var5 = var6 + var1.length();
            if (--var3 == 0) {
               break;
            }
         }

         var4.append(var0, var5, var0.length());
         return var4.toString();
      } else {
         return var0;
      }
   }

   public static String overlayString(String var0, String var1, int var2, int var3) {
      return new StringBuilder(var2 + var1.length() + var0.length() - var3 + 1).append(var0, 0, var2).append(var1).append(var0, var3, var0.length()).toString();
   }

   public static String center(String var0, int var1) {
      return center(var0, var1, " ");
   }

   public static String center(String var0, int var1, String var2) {
      int var3 = var0.length();
      int var4 = var1 - var3;
      if (var4 < 1) {
         return var0;
      } else {
         var0 = leftPad(var0, var3 + var4 / 2, var2);
         return rightPad(var0, var1, var2);
      }
   }

   public static String chomp(String var0) {
      return chomp(var0, "\n");
   }

   public static String chomp(String var0, String var1) {
      int var2 = var0.lastIndexOf(var1);
      return var2 != -1 ? var0.substring(0, var2) : var0;
   }

   public static String chompLast(String var0) {
      return chompLast(var0, "\n");
   }

   public static String chompLast(String var0, String var1) {
      if (var0.length() == 0) {
         return var0;
      } else {
         String var2 = var0.substring(var0.length() - var1.length());
         return var1.equals(var2) ? var0.substring(0, var0.length() - var1.length()) : var0;
      }
   }

   public static String getChomp(String var0, String var1) {
      int var2 = var0.lastIndexOf(var1);
      if (var2 == var0.length() - var1.length()) {
         return var1;
      } else {
         return var2 != -1 ? var0.substring(var2) : "";
      }
   }

   public static String prechomp(String var0, String var1) {
      int var2 = var0.indexOf(var1);
      return var2 != -1 ? var0.substring(var2 + var1.length()) : var0;
   }

   public static String getPrechomp(String var0, String var1) {
      int var2 = var0.indexOf(var1);
      return var2 != -1 ? var0.substring(0, var2 + var1.length()) : "";
   }

   public static String chop(String var0) {
      if ("".equals(var0)) {
         return "";
      } else if (var0.length() == 1) {
         return "";
      } else {
         int var1 = var0.length() - 1;
         String var2 = var0.substring(0, var1);
         char var3 = var0.charAt(var1);
         return var3 == '\n' && var2.charAt(var1 - 1) == '\r' ? var2.substring(0, var1 - 1) : var2;
      }
   }

   public static String chopNewline(String var0) {
      int var1 = var0.length() - 1;
      char var2 = var0.charAt(var1);
      if (var2 == '\n') {
         if (var0.charAt(var1 - 1) == '\r') {
            var1--;
         }
      } else {
         var1++;
      }

      return var0.substring(0, var1);
   }

   public static String escape(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = new StringBuilder(2 * var1);

      for (int var3 = 0; var3 < var1; var3++) {
         char var4 = var0.charAt(var3);
         if (var4 > 4095) {
            var2.append("\\u" + Integer.toHexString(var4));
         } else if (var4 > 255) {
            var2.append("\\u0" + Integer.toHexString(var4));
         } else if (var4 > 127) {
            var2.append("\\u00" + Integer.toHexString(var4));
         } else if (var4 < ' ') {
            switch (var4) {
               case '\b':
                  var2.append('\\');
                  var2.append('b');
                  break;
               case '\t':
                  var2.append('\\');
                  var2.append('t');
                  break;
               case '\n':
                  var2.append('\\');
                  var2.append('n');
                  break;
               case '\u000b':
               default:
                  if (var4 > 15) {
                     var2.append("\\u00" + Integer.toHexString(var4));
                  } else {
                     var2.append("\\u000" + Integer.toHexString(var4));
                  }
                  break;
               case '\f':
                  var2.append('\\');
                  var2.append('f');
                  break;
               case '\r':
                  var2.append('\\');
                  var2.append('r');
            }
         } else {
            switch (var4) {
               case '"':
                  var2.append('\\');
                  var2.append('"');
                  break;
               case '\'':
                  var2.append('\\');
                  var2.append('\'');
                  break;
               case '\\':
                  var2.append('\\');
                  var2.append('\\');
                  break;
               default:
                  var2.append(var4);
            }
         }
      }

      return var2.toString();
   }

   public static String repeat(String var0, int var1) {
      StringBuilder var2 = new StringBuilder(var1 * var0.length());

      for (int var3 = 0; var3 < var1; var3++) {
         var2.append(var0);
      }

      return var2.toString();
   }

   public static String rightPad(String var0, int var1) {
      return rightPad(var0, var1, " ");
   }

   public static String rightPad(String var0, int var1, String var2) {
      var1 = (var1 - var0.length()) / var2.length();
      if (var1 > 0) {
         var0 = var0 + repeat(var2, var1);
      }

      return var0;
   }

   public static String leftPad(String var0, int var1) {
      return leftPad(var0, var1, " ");
   }

   public static String leftPad(String var0, int var1, String var2) {
      var1 = (var1 - var0.length()) / var2.length();
      if (var1 > 0) {
         var0 = repeat(var2, var1) + var0;
      }

      return var0;
   }

   public static String strip(String var0) {
      return strip(var0, null);
   }

   public static String strip(String var0, String var1) {
      var0 = stripStart(var0, var1);
      return stripEnd(var0, var1);
   }

   public static String[] stripAll(String[] var0) {
      return stripAll(var0, null);
   }

   public static String[] stripAll(String[] var0, String var1) {
      if (var0 != null && var0.length != 0) {
         int var2 = var0.length;
         String[] var3 = new String[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            var3[var4] = strip(var0[var4], var1);
         }

         return var3;
      } else {
         return var0;
      }
   }

   public static String stripEnd(String var0, String var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = var0.length();
         if (var1 == null) {
            while (var2 != 0 && Character.isWhitespace(var0.charAt(var2 - 1))) {
               var2--;
            }
         } else {
            while (var2 != 0 && var1.indexOf(var0.charAt(var2 - 1)) != -1) {
               var2--;
            }
         }

         return var0.substring(0, var2);
      }
   }

   public static String stripStart(String var0, String var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = 0;
         int var3 = var0.length();
         if (var1 == null) {
            while (var2 != var3 && Character.isWhitespace(var0.charAt(var2))) {
               var2++;
            }
         } else {
            while (var2 != var3 && var1.indexOf(var0.charAt(var2)) != -1) {
               var2++;
            }
         }

         return var0.substring(var2);
      }
   }

   public static String upperCase(String var0) {
      return var0 == null ? null : var0.toUpperCase();
   }

   public static String lowerCase(String var0) {
      return var0 == null ? null : var0.toLowerCase();
   }

   public static String uncapitalise(String var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0.length() == 0
            ? ""
            : new StringBuilder(var0.length()).append(Character.toLowerCase(var0.charAt(0))).append(var0, 1, var0.length()).toString();
      }
   }

   public static String capitalise(String var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0.length() == 0
            ? ""
            : new StringBuilder(var0.length()).append(Character.toTitleCase(var0.charAt(0))).append(var0, 1, var0.length()).toString();
      }
   }

   public static String swapCase(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         StringBuilder var2 = new StringBuilder(var1);
         boolean var3 = false;

         for (int var6 = 0; var6 < var1; var6++) {
            char var4 = var0.charAt(var6);
            char var5;
            if (Character.isUpperCase(var4)) {
               var5 = Character.toLowerCase(var4);
            } else if (Character.isTitleCase(var4)) {
               var5 = Character.toLowerCase(var4);
            } else if (Character.isLowerCase(var4)) {
               if (var3) {
                  var5 = Character.toTitleCase(var4);
               } else {
                  var5 = Character.toUpperCase(var4);
               }
            } else {
               var5 = var4;
            }

            var2.append(var5);
            var3 = Character.isWhitespace(var4);
         }

         return var2.toString();
      }
   }

   public static String capitaliseAllWords(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         StringBuilder var2 = new StringBuilder(var1);
         boolean var3 = true;

         for (int var4 = 0; var4 < var1; var4++) {
            char var5 = var0.charAt(var4);
            if (Character.isWhitespace(var5)) {
               var2.append(var5);
               var3 = true;
            } else if (var3) {
               var2.append(Character.toTitleCase(var5));
               var3 = false;
            } else {
               var2.append(var5);
            }
         }

         return var2.toString();
      }
   }

   public static String uncapitaliseAllWords(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         StringBuilder var2 = new StringBuilder(var1);
         boolean var3 = true;

         for (int var4 = 0; var4 < var1; var4++) {
            char var5 = var0.charAt(var4);
            if (Character.isWhitespace(var5)) {
               var2.append(var5);
               var3 = true;
            } else if (var3) {
               var2.append(Character.toLowerCase(var5));
               var3 = false;
            } else {
               var2.append(var5);
            }
         }

         return var2.toString();
      }
   }

   public static String getNestedString(String var0, String var1) {
      return getNestedString(var0, var1, var1);
   }

   public static String getNestedString(String var0, String var1, String var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var0.indexOf(var1);
         if (var3 != -1) {
            int var4 = var0.indexOf(var2, var3 + var1.length());
            if (var4 != -1) {
               return var0.substring(var3 + var1.length(), var4);
            }
         }

         return null;
      }
   }

   public static int countMatches(String var0, String var1) {
      if (var1.equals("")) {
         return 0;
      } else if (var0 == null) {
         return 0;
      } else {
         int var2 = 0;
         int var3 = 0;

         while ((var3 = var0.indexOf(var1, var3)) != -1) {
            var2++;
            var3 += var1.length();
         }

         return var2;
      }
   }

   public static boolean isAlpha(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isLetter(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isWhitespace(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isWhitespace(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphaSpace(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isLetter(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumeric(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isLetterOrDigit(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumericSpace(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isLetterOrDigit(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumeric(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isDigit(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumericSpace(String var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isDigit(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   @Deprecated
   public static String defaultString(Object var0) {
      return defaultString(var0, "");
   }

   @Deprecated
   public static String defaultString(Object var0, String var1) {
      return Objects.toString(var0, var1);
   }

   public static String reverse(String var0) {
      return var0 == null ? null : new StringBuilder(var0).reverse().toString();
   }

   public static String reverseDelimitedString(String var0, String var1) {
      String[] var2 = split(var0, var1);
      reverseArray(var2);
      return join(var2, var1);
   }

   private static void reverseArray(Object[] var0) {
      int var1 = 0;

      for (int var2 = var0.length - 1; var2 > var1; var1++) {
         Object var3 = var0[var2];
         var0[var2] = var0[var1];
         var0[var1] = var3;
         var2--;
      }
   }

   public static String abbreviate(String var0, int var1) {
      return abbreviate(var0, 0, var1);
   }

   public static String abbreviate(String var0, int var1, int var2) {
      if (var2 < 4) {
         throw new IllegalArgumentException("Minimum abbreviation width is 4");
      } else if (var0.length() <= var2) {
         return var0;
      } else {
         if (var1 > var0.length()) {
            var1 = var0.length();
         }

         if (var0.length() - var1 < var2 - 3) {
            var1 = var0.length() - (var2 - 3);
         }

         if (var1 <= 4) {
            return var0.substring(0, var2 - 3) + "...";
         } else if (var2 < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
         } else {
            return var1 + (var2 - 3) < var0.length() ? "..." + abbreviate(var0.substring(var1), var2 - 3) : "..." + var0.substring(var0.length() - (var2 - 3));
         }
      }
   }

   public static String difference(String var0, String var1) {
      int var2 = differenceAt(var0, var1);
      return var2 == -1 ? "" : var1.substring(var2);
   }

   public static int differenceAt(String var0, String var1) {
      int var2 = 0;

      while (var2 < var0.length() && var2 < var1.length() && var0.charAt(var2) == var1.charAt(var2)) {
         var2++;
      }

      return var2 >= var1.length() && var2 >= var0.length() ? -1 : var2;
   }

   public static String interpolate(String var0, Map<?, ?> var1) {
      Iterator var2 = var1.keySet().iterator();

      while (var2.hasNext()) {
         String var3 = var2.next().toString();
         Object var4 = var1.get(var3);
         if (var4 == null) {
            throw new NullPointerException("The value of the key '" + var3 + "' is null.");
         }

         String var5 = var4.toString();
         var0 = replace(var0, "${" + var3 + "}", var5);
         if (!var3.contains(" ")) {
            var0 = replace(var0, "$" + var3, var5);
         }
      }

      return var0;
   }

   public static String removeAndHump(String var0, String var1) {
      StringBuilder var3 = new StringBuilder();
      StringTokenizer var4 = new StringTokenizer(var0, var1);

      while (var4.hasMoreTokens()) {
         String var5 = (String)var4.nextElement();
         var3.append(capitalizeFirstLetter(var5));
      }

      return var3.toString();
   }

   public static String capitalizeFirstLetter(String var0) {
      char var1 = Character.toTitleCase(var0.substring(0, 1).charAt(0));
      String var2 = var0.substring(1);
      return var1 + var2;
   }

   public static String lowercaseFirstLetter(String var0) {
      char var1 = Character.toLowerCase(var0.substring(0, 1).charAt(0));
      String var2 = var0.substring(1);
      return var1 + var2;
   }

   public static String addAndDeHump(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length(); var2++) {
         if (var2 != 0 && Character.isUpperCase(var0.charAt(var2))) {
            var1.append('-');
         }

         var1.append(var0.charAt(var2));
      }

      return var1.toString().trim().toLowerCase(Locale.ENGLISH);
   }

   public static String quoteAndEscape(String var0, char var1) {
      return quoteAndEscape(var0, var1, new char[]{var1}, new char[]{' '}, '\\', false);
   }

   public static String quoteAndEscape(String var0, char var1, char[] var2) {
      return quoteAndEscape(var0, var1, new char[]{var1}, var2, '\\', false);
   }

   public static String quoteAndEscape(String var0, char var1, char[] var2, char var3, boolean var4) {
      return quoteAndEscape(var0, var1, var2, new char[]{' '}, var3, var4);
   }

   public static String quoteAndEscape(String var0, char var1, char[] var2, char[] var3, char var4, boolean var5) {
      return quoteAndEscape(var0, var1, var2, var3, var4 + "%s", var5);
   }

   public static String quoteAndEscape(String var0, char var1, char[] var2, char[] var3, String var4, boolean var5) {
      if (var0 == null) {
         return null;
      } else if (!var5 && var0.startsWith(Character.toString(var1)) && var0.endsWith(Character.toString(var1))) {
         return var0;
      } else {
         String var6 = escape(var0, var2, var4);
         boolean var7 = false;
         if (var5) {
            var7 = true;
         } else if (!var6.equals(var0)) {
            var7 = true;
         } else {
            for (char var11 : var3) {
               if (var6.indexOf(var11) > -1) {
                  var7 = true;
                  break;
               }
            }
         }

         return var7 ? var1 + var6 + var1 : var6;
      }
   }

   public static String escape(String var0, char[] var1, char var2) {
      return escape(var0, var1, var2 + "%s");
   }

   public static String escape(String var0, char[] var1, String var2) {
      if (var0 == null) {
         return null;
      } else {
         char[] var3 = new char[var1.length];
         System.arraycopy(var1, 0, var3, 0, var1.length);
         Arrays.sort(var3);
         StringBuilder var4 = new StringBuilder(var0.length());

         for (int var5 = 0; var5 < var0.length(); var5++) {
            char var6 = var0.charAt(var5);
            int var7 = Arrays.binarySearch(var3, var6);
            if (var7 > -1) {
               var4.append(String.format(var2, var6));
            } else {
               var4.append(var6);
            }
         }

         return var4.toString();
      }
   }

   public static String removeDuplicateWhitespace(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = var0.length();
      boolean var3 = false;

      for (int var4 = 0; var4 < var2; var4++) {
         char var5 = var0.charAt(var4);
         boolean var6 = Character.isWhitespace(var5);
         if (!var3 || !var6) {
            var1.append(var5);
         }

         var3 = var6;
      }

      return var1.toString();
   }

   public static String unifyLineSeparators(String var0) {
      return unifyLineSeparators(var0, System.getProperty("line.separator"));
   }

   public static String unifyLineSeparators(String var0, String var1) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 == null) {
            var1 = System.getProperty("line.separator");
         }

         if (!var1.equals("\n") && !var1.equals("\r") && !var1.equals("\r\n")) {
            throw new IllegalArgumentException("Requested line separator is invalid.");
         } else {
            int var2 = var0.length();
            StringBuilder var3 = new StringBuilder(var2);

            for (int var4 = 0; var4 < var2; var4++) {
               if (var0.charAt(var4) == '\r') {
                  if (var4 + 1 < var2 && var0.charAt(var4 + 1) == '\n') {
                     var4++;
                  }

                  var3.append(var1);
               } else if (var0.charAt(var4) == '\n') {
                  var3.append(var1);
               } else {
                  var3.append(var0.charAt(var4));
               }
            }

            return var3.toString();
         }
      }
   }

   public static boolean contains(String var0, char var1) {
      return isEmpty(var0) ? false : var0.indexOf(var1) >= 0;
   }

   public static boolean contains(String var0, String var1) {
      return var0 != null && var1 != null ? var0.contains(var1) : false;
   }
}
