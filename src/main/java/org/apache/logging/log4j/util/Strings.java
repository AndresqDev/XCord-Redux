package org.apache.logging.log4j.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public final class Strings {
   private static final ThreadLocal<StringBuilder> tempStr = ThreadLocal.withInitial(StringBuilder::new);
   public static final String EMPTY = "";
   public static final String[] EMPTY_ARRAY = new String[0];
   public static final String LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator", "\n");

   public static String dquote(String var0) {
      return '"' + var0 + '"';
   }

   public static boolean isBlank(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            char var2 = var0.charAt(var1);
            if (!Character.isWhitespace(var2)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isEmpty(CharSequence var0) {
      return var0 == null || var0.length() == 0;
   }

   public static boolean isNotBlank(String var0) {
      return !isBlank(var0);
   }

   public static boolean isNotEmpty(CharSequence var0) {
      return !isEmpty(var0);
   }

   public static String join(Iterable<?> var0, char var1) {
      return var0 == null ? null : join(var0.iterator(), var1);
   }

   public static String join(Iterator<?> var0, char var1) {
      if (var0 == null) {
         return null;
      } else if (!var0.hasNext()) {
         return "";
      } else {
         Object var2 = var0.next();
         if (!var0.hasNext()) {
            return Objects.toString(var2, "");
         } else {
            StringBuilder var3 = new StringBuilder(256);
            if (var2 != null) {
               var3.append(var2);
            }

            while (var0.hasNext()) {
               var3.append(var1);
               Object var4 = var0.next();
               if (var4 != null) {
                  var3.append(var4);
               }
            }

            return var3.toString();
         }
      }
   }

   public static String left(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else if (var1 < 0) {
         return "";
      } else {
         return var0.length() <= var1 ? var0 : var0.substring(0, var1);
      }
   }

   public static String quote(String var0) {
      return '\'' + var0 + '\'';
   }

   public static String trimToNull(String var0) {
      String var1 = var0 == null ? null : var0.trim();
      return isEmpty(var1) ? null : var1;
   }

   private Strings() {
   }

   public static String toRootUpperCase(String var0) {
      return var0.toUpperCase(Locale.ROOT);
   }

   public static String concat(String var0, String var1) {
      if (isEmpty(var0)) {
         return var1;
      } else if (isEmpty(var1)) {
         return var0;
      } else {
         StringBuilder var2 = tempStr.get();

         String var3;
         try {
            var3 = var2.append(var0).append(var1).toString();
         } finally {
            var2.setLength(0);
         }

         return var3;
      }
   }

   public static String repeat(String var0, int var1) {
      Objects.requireNonNull(var0, "str");
      if (var1 < 0) {
         throw new IllegalArgumentException("count");
      } else {
         StringBuilder var2 = tempStr.get();

         String var7;
         try {
            for (int var3 = 0; var3 < var1; var3++) {
               var2.append(var0);
            }

            var7 = var2.toString();
         } finally {
            var2.setLength(0);
         }

         return var7;
      }
   }
}
