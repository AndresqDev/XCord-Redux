package joptsimple;

import java.util.List;

final class ParserRules {
   static final char HYPHEN_CHAR = '-';
   static final String HYPHEN = String.valueOf('-');
   static final String DOUBLE_HYPHEN = "--";
   static final String OPTION_TERMINATOR = "--";
   static final String RESERVED_FOR_EXTENSIONS = "W";

   private ParserRules() {
      throw new UnsupportedOperationException();
   }

   static boolean isShortOptionToken(String var0) {
      return var0.startsWith(HYPHEN) && !HYPHEN.equals(var0) && !isLongOptionToken(var0);
   }

   static boolean isLongOptionToken(String var0) {
      return var0.startsWith("--") && !isOptionTerminator(var0);
   }

   static boolean isOptionTerminator(String var0) {
      return "--".equals(var0);
   }

   static void ensureLegalOption(String var0) {
      if (var0.startsWith(HYPHEN)) {
         throw new IllegalOptionSpecificationException(String.valueOf(var0));
      } else {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            ensureLegalOptionCharacter(var0.charAt(var1));
         }
      }
   }

   static void ensureLegalOptions(List<String> var0) {
      for (String var2 : var0) {
         ensureLegalOption(var2);
      }
   }

   private static void ensureLegalOptionCharacter(char var0) {
      if (!Character.isLetterOrDigit(var0) && !isAllowedPunctuation(var0)) {
         throw new IllegalOptionSpecificationException(String.valueOf(var0));
      }
   }

   private static boolean isAllowedPunctuation(char var0) {
      String var1 = "?._-";
      return var1.indexOf(var0) != -1;
   }
}
