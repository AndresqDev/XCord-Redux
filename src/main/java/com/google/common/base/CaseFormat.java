package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum CaseFormat {
   LOWER_HYPHEN(CharMatcher.is('-'), "-") {
      @Override
      String normalizeWord(String var1) {
         return Ascii.toLowerCase(var1);
      }

      @Override
      String convert(CaseFormat var1, String var2) {
         if (var1 == LOWER_UNDERSCORE) {
            return var2.replace('-', '_');
         } else {
            return var1 == UPPER_UNDERSCORE ? Ascii.toUpperCase(var2.replace('-', '_')) : super.convert(var1, var2);
         }
      }
   },
   LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
      @Override
      String normalizeWord(String var1) {
         return Ascii.toLowerCase(var1);
      }

      @Override
      String convert(CaseFormat var1, String var2) {
         if (var1 == LOWER_HYPHEN) {
            return var2.replace('_', '-');
         } else {
            return var1 == UPPER_UNDERSCORE ? Ascii.toUpperCase(var2) : super.convert(var1, var2);
         }
      }
   },
   LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
      @Override
      String normalizeWord(String var1) {
         return CaseFormat.firstCharOnlyToUpper(var1);
      }

      @Override
      String normalizeFirstWord(String var1) {
         return Ascii.toLowerCase(var1);
      }
   },
   UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
      @Override
      String normalizeWord(String var1) {
         return CaseFormat.firstCharOnlyToUpper(var1);
      }
   },
   UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
      @Override
      String normalizeWord(String var1) {
         return Ascii.toUpperCase(var1);
      }

      @Override
      String convert(CaseFormat var1, String var2) {
         if (var1 == LOWER_HYPHEN) {
            return Ascii.toLowerCase(var2.replace('_', '-'));
         } else {
            return var1 == LOWER_UNDERSCORE ? Ascii.toLowerCase(var2) : super.convert(var1, var2);
         }
      }
   };

   private final CharMatcher wordBoundary;
   private final String wordSeparator;

   private CaseFormat(CharMatcher var3, String var4) {
      this.wordBoundary = var3;
      this.wordSeparator = var4;
   }

   public final String to(CaseFormat var1, String var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return var1 == this ? var2 : this.convert(var1, var2);
   }

   String convert(CaseFormat var1, String var2) {
      StringBuilder var3 = null;
      int var4 = 0;
      int var5 = -1;

      while (true) {
         var5++;
         if ((var5 = this.wordBoundary.indexIn(var2, var5)) == -1) {
            return var4 == 0
               ? var1.normalizeFirstWord(var2)
               : java.util.Objects.requireNonNull(var3).append(var1.normalizeWord(var2.substring(var4))).toString();
         }

         if (var4 == 0) {
            var3 = new StringBuilder(var2.length() + 4 * var1.wordSeparator.length());
            var3.append(var1.normalizeFirstWord(var2.substring(var4, var5)));
         } else {
            java.util.Objects.requireNonNull(var3).append(var1.normalizeWord(var2.substring(var4, var5)));
         }

         var3.append(var1.wordSeparator);
         var4 = var5 + this.wordSeparator.length();
      }
   }

   public Converter<String, String> converterTo(CaseFormat var1) {
      return new CaseFormat.StringConverter(this, var1);
   }

   abstract String normalizeWord(String var1);

   String normalizeFirstWord(String var1) {
      return this.normalizeWord(var1);
   }

   private static String firstCharOnlyToUpper(String var0) {
      String var10000;
      if (var0.isEmpty()) {
         var10000 = var0;
      } else {
         char var1 = Ascii.toUpperCase(var0.charAt(0));
         String var2 = Ascii.toLowerCase(var0.substring(1));
         var10000 = new StringBuilder(1 + String.valueOf(var2).length()).append(var1).append(var2).toString();
      }

      return var10000;
   }

   private static final class StringConverter extends Converter<String, String> implements Serializable {
      private final CaseFormat sourceFormat;
      private final CaseFormat targetFormat;
      private static final long serialVersionUID = 0L;

      StringConverter(CaseFormat var1, CaseFormat var2) {
         this.sourceFormat = Preconditions.checkNotNull(var1);
         this.targetFormat = Preconditions.checkNotNull(var2);
      }

      protected String doForward(String var1) {
         return this.sourceFormat.to(this.targetFormat, var1);
      }

      protected String doBackward(String var1) {
         return this.targetFormat.to(this.sourceFormat, var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof CaseFormat.StringConverter)) {
            return false;
         } else {
            CaseFormat.StringConverter var2 = (CaseFormat.StringConverter)var1;
            return this.sourceFormat.equals(var2.sourceFormat) && this.targetFormat.equals(var2.targetFormat);
         }
      }

      @Override
      public int hashCode() {
         return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.sourceFormat);
         String var2 = String.valueOf(this.targetFormat);
         return new StringBuilder(14 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".converterTo(")
            .append(var2)
            .append(")")
            .toString();
      }
   }
}
