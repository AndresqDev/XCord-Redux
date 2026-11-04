package org.apache.logging.log4j.util;

public final class Chars {
   public static final char CR = '\r';
   public static final char DQUOTE = '"';
   public static final char EQ = '=';
   public static final char LF = '\n';
   public static final char NUL = '\u0000';
   public static final char QUOTE = '\'';
   public static final char SPACE = ' ';
   public static final char TAB = '\t';

   public static char getUpperCaseHex(int var0) {
      if (var0 >= 0 && var0 < 16) {
         return var0 < 10 ? getNumericalDigit(var0) : getUpperCaseAlphaDigit(var0);
      } else {
         return '\u0000';
      }
   }

   public static char getLowerCaseHex(int var0) {
      if (var0 >= 0 && var0 < 16) {
         return var0 < 10 ? getNumericalDigit(var0) : getLowerCaseAlphaDigit(var0);
      } else {
         return '\u0000';
      }
   }

   private static char getNumericalDigit(int var0) {
      return (char)(48 + var0);
   }

   private static char getUpperCaseAlphaDigit(int var0) {
      return (char)(65 + var0 - 10);
   }

   private static char getLowerCaseAlphaDigit(int var0) {
      return (char)(97 + var0 - 10);
   }

   private Chars() {
   }
}
