package org.apache.logging.log4j.util;

import java.util.Map.Entry;

public final class StringBuilders {
   private StringBuilders() {
   }

   public static StringBuilder appendDqValue(StringBuilder var0, Object var1) {
      return var0.append('"').append(var1).append('"');
   }

   public static StringBuilder appendKeyDqValue(StringBuilder var0, Entry<String, String> var1) {
      return appendKeyDqValue(var0, (String)var1.getKey(), var1.getValue());
   }

   public static StringBuilder appendKeyDqValue(StringBuilder var0, String var1, Object var2) {
      return var0.append(var1).append('=').append('"').append(var2).append('"');
   }

   public static void appendValue(StringBuilder var0, Object var1) {
      if (!appendSpecificTypes(var0, var1)) {
         var0.append(var1);
      }
   }

   public static boolean appendSpecificTypes(StringBuilder var0, Object var1) {
      if (var1 == null || var1 instanceof String) {
         var0.append((String)var1);
      } else if (var1 instanceof StringBuilderFormattable) {
         ((StringBuilderFormattable)var1).formatTo(var0);
      } else if (var1 instanceof CharSequence) {
         var0.append((CharSequence)var1);
      } else if (var1 instanceof Integer) {
         var0.append(((Integer)var1).intValue());
      } else if (var1 instanceof Long) {
         var0.append(((Long)var1).longValue());
      } else if (var1 instanceof Double) {
         var0.append(((Double)var1).doubleValue());
      } else if (var1 instanceof Boolean) {
         var0.append(((Boolean)var1).booleanValue());
      } else if (var1 instanceof Character) {
         var0.append(((Character)var1).charValue());
      } else if (var1 instanceof Short) {
         var0.append(((Short)var1).shortValue());
      } else if (var1 instanceof Float) {
         var0.append(((Float)var1).floatValue());
      } else {
         if (!(var1 instanceof Byte)) {
            return false;
         }

         var0.append(((Byte)var1).byteValue());
      }

      return true;
   }

   public static boolean equals(CharSequence var0, int var1, int var2, CharSequence var3, int var4, int var5) {
      if (var2 == var5) {
         for (int var6 = 0; var6 < var5; var6++) {
            if (var0.charAt(var6 + var1) != var3.charAt(var6 + var4)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean equalsIgnoreCase(CharSequence var0, int var1, int var2, CharSequence var3, int var4, int var5) {
      if (var2 == var5) {
         for (int var6 = 0; var6 < var5; var6++) {
            if (Character.toLowerCase(var0.charAt(var6 + var1)) != Character.toLowerCase(var3.charAt(var6 + var4))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void trimToMaxSize(StringBuilder var0, int var1) {
      if (var0 != null && var0.capacity() > var1) {
         var0.setLength(var1);
         var0.trimToSize();
      }
   }

   public static void escapeJson(StringBuilder var0, int var1) {
      int var2 = 0;

      for (int var3 = var1; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         switch (var4) {
            case '\b':
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case '"':
            case '\\':
               var2++;
               break;
            default:
               if (Character.isISOControl(var4)) {
                  var2 += 5;
               }
         }
      }

      int var7 = var0.length() - 1;
      var0.setLength(var0.length() + var2);
      int var8 = var0.length() - 1;

      for (int var5 = var7; var8 > var5; var5--) {
         char var6 = var0.charAt(var5);
         switch (var6) {
            case '\b':
               var8 = escapeAndDecrement(var0, var8, 'b');
               break;
            case '\t':
               var8 = escapeAndDecrement(var0, var8, 't');
               break;
            case '\n':
               var8 = escapeAndDecrement(var0, var8, 'n');
               break;
            case '\f':
               var8 = escapeAndDecrement(var0, var8, 'f');
               break;
            case '\r':
               var8 = escapeAndDecrement(var0, var8, 'r');
               break;
            case '"':
            case '\\':
               var8 = escapeAndDecrement(var0, var8, var6);
               break;
            default:
               if (Character.isISOControl(var6)) {
                  var0.setCharAt(var8--, Chars.getUpperCaseHex(var6 & 15));
                  var0.setCharAt(var8--, Chars.getUpperCaseHex((var6 & 240) >> 4));
                  var0.setCharAt(var8--, '0');
                  var0.setCharAt(var8--, '0');
                  var0.setCharAt(var8--, 'u');
                  var0.setCharAt(var8--, '\\');
               } else {
                  var0.setCharAt(var8, var6);
                  var8--;
               }
         }
      }
   }

   private static int escapeAndDecrement(StringBuilder var0, int var1, char var2) {
      var0.setCharAt(var1--, var2);
      var0.setCharAt(var1--, '\\');
      return var1;
   }

   public static void escapeXml(StringBuilder var0, int var1) {
      byte var2 = 0;

      for (int var3 = var1; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         switch (var4) {
            case '"':
            case '\'':
               var2 += 5;
               break;
            case '&':
               var2 += 4;
               break;
            case '<':
            case '>':
               var2 += 3;
         }
      }

      int var7 = var0.length() - 1;
      var0.setLength(var0.length() + var2);
      int var8 = var0.length() - 1;

      for (int var5 = var7; var8 > var5; var5--) {
         char var6 = var0.charAt(var5);
         switch (var6) {
            case '"':
               var0.setCharAt(var8--, ';');
               var0.setCharAt(var8--, 't');
               var0.setCharAt(var8--, 'o');
               var0.setCharAt(var8--, 'u');
               var0.setCharAt(var8--, 'q');
               var0.setCharAt(var8--, '&');
               break;
            case '&':
               var0.setCharAt(var8--, ';');
               var0.setCharAt(var8--, 'p');
               var0.setCharAt(var8--, 'm');
               var0.setCharAt(var8--, 'a');
               var0.setCharAt(var8--, '&');
               break;
            case '\'':
               var0.setCharAt(var8--, ';');
               var0.setCharAt(var8--, 's');
               var0.setCharAt(var8--, 'o');
               var0.setCharAt(var8--, 'p');
               var0.setCharAt(var8--, 'a');
               var0.setCharAt(var8--, '&');
               break;
            case '<':
               var0.setCharAt(var8--, ';');
               var0.setCharAt(var8--, 't');
               var0.setCharAt(var8--, 'l');
               var0.setCharAt(var8--, '&');
               break;
            case '>':
               var0.setCharAt(var8--, ';');
               var0.setCharAt(var8--, 't');
               var0.setCharAt(var8--, 'g');
               var0.setCharAt(var8--, '&');
               break;
            default:
               var0.setCharAt(var8--, var6);
         }
      }
   }
}
