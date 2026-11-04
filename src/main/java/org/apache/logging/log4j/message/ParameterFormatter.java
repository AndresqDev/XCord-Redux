package org.apache.logging.log4j.message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.StringBuilders;

final class ParameterFormatter {
   static final String RECURSION_PREFIX = "[...";
   static final String RECURSION_SUFFIX = "...]";
   static final String ERROR_PREFIX = "[!!!";
   static final String ERROR_SEPARATOR = "=>";
   static final String ERROR_MSG_SEPARATOR = ":";
   static final String ERROR_SUFFIX = "!!!]";
   private static final char DELIM_START = '{';
   private static final char DELIM_STOP = '}';
   private static final char ESCAPE_CHAR = '\\';
   private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_REF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

   private ParameterFormatter() {
   }

   static int countArgumentPlaceholders(String var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length();
         int var2 = 0;
         boolean var3 = false;

         for (int var4 = 0; var4 < var1 - 1; var4++) {
            char var5 = var0.charAt(var4);
            if (var5 == '\\') {
               var3 = !var3;
            } else if (var5 == '{') {
               if (!var3 && var0.charAt(var4 + 1) == '}') {
                  var2++;
                  var4++;
               }

               var3 = false;
            } else {
               var3 = false;
            }
         }

         return var2;
      }
   }

   static int countArgumentPlaceholders2(String var0, int[] var1) {
      if (var0 == null) {
         return 0;
      } else {
         int var2 = var0.length();
         int var3 = 0;
         boolean var4 = false;

         for (int var5 = 0; var5 < var2 - 1; var5++) {
            char var6 = var0.charAt(var5);
            if (var6 == '\\') {
               var4 = !var4;
               var1[0] = -1;
               var3++;
            } else if (var6 == '{') {
               if (!var4 && var0.charAt(var5 + 1) == '}') {
                  var1[var3] = var5;
                  var3++;
                  var5++;
               }

               var4 = false;
            } else {
               var4 = false;
            }
         }

         return var3;
      }
   }

   static int countArgumentPlaceholders3(char[] var0, int var1, int[] var2) {
      int var3 = 0;
      boolean var4 = false;

      for (int var5 = 0; var5 < var1 - 1; var5++) {
         char var6 = var0[var5];
         if (var6 == '\\') {
            var4 = !var4;
         } else if (var6 == '{') {
            if (!var4 && var0[var5 + 1] == '}') {
               var2[var3] = var5;
               var3++;
               var5++;
            }

            var4 = false;
         } else {
            var4 = false;
         }
      }

      return var3;
   }

   static String format(String var0, Object[] var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = var1 == null ? 0 : var1.length;
      formatMessage(var2, var0, var1, var3);
      return var2.toString();
   }

   static void formatMessage2(StringBuilder var0, String var1, Object[] var2, int var3, int[] var4) {
      if (var1 != null && var2 != null && var3 != 0) {
         int var5 = 0;

         for (int var6 = 0; var6 < var3; var6++) {
            var0.append(var1, var5, var4[var6]);
            var5 = var4[var6] + 2;
            recursiveDeepToString(var2[var6], var0);
         }

         var0.append(var1, var5, var1.length());
      } else {
         var0.append(var1);
      }
   }

   static void formatMessage3(StringBuilder var0, char[] var1, int var2, Object[] var3, int var4, int[] var5) {
      if (var1 != null) {
         if (var3 != null && var4 != 0) {
            int var6 = 0;

            for (int var7 = 0; var7 < var4; var7++) {
               var0.append(var1, var6, var5[var7]);
               var6 = var5[var7] + 2;
               recursiveDeepToString(var3[var7], var0);
            }

            var0.append(var1, var6, var2);
         } else {
            var0.append(var1);
         }
      }
   }

   static void formatMessage(StringBuilder var0, String var1, Object[] var2, int var3) {
      if (var1 != null && var2 != null && var3 != 0) {
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;

         int var7;
         for (var7 = var1.length(); var6 < var7 - 1; var6++) {
            char var8 = var1.charAt(var6);
            if (var8 == '\\') {
               var4++;
            } else {
               if (isDelimPair(var8, var1, var6)) {
                  var6++;
                  writeEscapedEscapeChars(var4, var0);
                  if (isOdd(var4)) {
                     writeDelimPair(var0);
                  } else {
                     writeArgOrDelimPair(var2, var3, var5, var0);
                     var5++;
                  }
               } else {
                  handleLiteralChar(var0, var4, var8);
               }

               var4 = 0;
            }
         }

         handleRemainingCharIfAny(var1, var7, var0, var4, var6);
      } else {
         var0.append(var1);
      }
   }

   private static boolean isDelimPair(char var0, String var1, int var2) {
      return var0 == '{' && var1.charAt(var2 + 1) == '}';
   }

   private static void handleRemainingCharIfAny(String var0, int var1, StringBuilder var2, int var3, int var4) {
      if (var4 == var1 - 1) {
         char var5 = var0.charAt(var4);
         handleLastChar(var2, var3, var5);
      }
   }

   private static void handleLastChar(StringBuilder var0, int var1, char var2) {
      if (var2 == '\\') {
         writeUnescapedEscapeChars(var1 + 1, var0);
      } else {
         handleLiteralChar(var0, var1, var2);
      }
   }

   private static void handleLiteralChar(StringBuilder var0, int var1, char var2) {
      writeUnescapedEscapeChars(var1, var0);
      var0.append(var2);
   }

   private static void writeDelimPair(StringBuilder var0) {
      var0.append('{');
      var0.append('}');
   }

   private static boolean isOdd(int var0) {
      return (var0 & 1) == 1;
   }

   private static void writeEscapedEscapeChars(int var0, StringBuilder var1) {
      int var2 = var0 >> 1;
      writeUnescapedEscapeChars(var2, var1);
   }

   private static void writeUnescapedEscapeChars(int var0, StringBuilder var1) {
      while (var0 > 0) {
         var1.append('\\');
         var0--;
      }
   }

   private static void writeArgOrDelimPair(Object[] var0, int var1, int var2, StringBuilder var3) {
      if (var2 < var1) {
         recursiveDeepToString(var0[var2], var3);
      } else {
         writeDelimPair(var3);
      }
   }

   static String deepToString(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof String) {
         return (String)var0;
      } else if (var0 instanceof Integer) {
         return Integer.toString((Integer)var0);
      } else if (var0 instanceof Long) {
         return Long.toString((Long)var0);
      } else if (var0 instanceof Double) {
         return Double.toString((Double)var0);
      } else if (var0 instanceof Boolean) {
         return Boolean.toString((Boolean)var0);
      } else if (var0 instanceof Character) {
         return Character.toString((Character)var0);
      } else if (var0 instanceof Short) {
         return Short.toString((Short)var0);
      } else if (var0 instanceof Float) {
         return Float.toString((Float)var0);
      } else if (var0 instanceof Byte) {
         return Byte.toString((Byte)var0);
      } else {
         StringBuilder var1 = new StringBuilder();
         recursiveDeepToString(var0, var1);
         return var1.toString();
      }
   }

   static void recursiveDeepToString(Object var0, StringBuilder var1) {
      recursiveDeepToString(var0, var1, null);
   }

   private static void recursiveDeepToString(Object var0, StringBuilder var1, Set<Object> var2) {
      if (!appendSpecialTypes(var0, var1)) {
         if (isMaybeRecursive(var0)) {
            appendPotentiallyRecursiveValue(var0, var1, var2);
         } else {
            tryObjectToString(var0, var1);
         }
      }
   }

   private static boolean appendSpecialTypes(Object var0, StringBuilder var1) {
      return StringBuilders.appendSpecificTypes(var1, var0) || appendDate(var0, var1);
   }

   private static boolean appendDate(Object var0, StringBuilder var1) {
      if (!(var0 instanceof Date)) {
         return false;
      } else {
         Date var2 = (Date)var0;
         SimpleDateFormat var3 = SIMPLE_DATE_FORMAT_REF.get();
         var1.append(var3.format(var2));
         return true;
      }
   }

   private static boolean isMaybeRecursive(Object var0) {
      return var0.getClass().isArray() || var0 instanceof Map || var0 instanceof Collection;
   }

   private static void appendPotentiallyRecursiveValue(Object var0, StringBuilder var1, Set<Object> var2) {
      Class var3 = var0.getClass();
      if (var3.isArray()) {
         appendArray(var0, var1, var2, var3);
      } else if (var0 instanceof Map) {
         appendMap(var0, var1, var2);
      } else {
         if (!(var0 instanceof Collection)) {
            throw new IllegalArgumentException("was expecting a container, found " + var3);
         }

         appendCollection(var0, var1, var2);
      }
   }

   private static void appendArray(Object var0, StringBuilder var1, Set<Object> var2, Class<?> var3) {
      if (var3 == byte[].class) {
         var1.append(Arrays.toString((byte[])var0));
      } else if (var3 == short[].class) {
         var1.append(Arrays.toString((short[])var0));
      } else if (var3 == int[].class) {
         var1.append(Arrays.toString((int[])var0));
      } else if (var3 == long[].class) {
         var1.append(Arrays.toString((long[])var0));
      } else if (var3 == float[].class) {
         var1.append(Arrays.toString((float[])var0));
      } else if (var3 == double[].class) {
         var1.append(Arrays.toString((double[])var0));
      } else if (var3 == boolean[].class) {
         var1.append(Arrays.toString((boolean[])var0));
      } else if (var3 == char[].class) {
         var1.append(Arrays.toString((char[])var0));
      } else {
         Set var4 = getOrCreateDejaVu(var2);
         boolean var5 = !var4.add(var0);
         if (var5) {
            String var6 = identityToString(var0);
            var1.append("[...").append(var6).append("...]");
         } else {
            Object[] var12 = (Object[])var0;
            var1.append('[');
            boolean var7 = true;

            for (Object var11 : var12) {
               if (var7) {
                  var7 = false;
               } else {
                  var1.append(", ");
               }

               recursiveDeepToString(var11, var1, cloneDejaVu(var4));
            }

            var1.append(']');
         }
      }
   }

   private static void appendMap(Object var0, StringBuilder var1, Set<Object> var2) {
      Set var3 = getOrCreateDejaVu(var2);
      boolean var4 = !var3.add(var0);
      if (var4) {
         String var5 = identityToString(var0);
         var1.append("[...").append(var5).append("...]");
      } else {
         Map var12 = (Map)var0;
         var1.append('{');
         boolean var6 = true;

         for (Object var8 : var12.entrySet()) {
            Entry var9 = (Entry)var8;
            if (var6) {
               var6 = false;
            } else {
               var1.append(", ");
            }

            Object var10 = var9.getKey();
            Object var11 = var9.getValue();
            recursiveDeepToString(var10, var1, cloneDejaVu(var3));
            var1.append('=');
            recursiveDeepToString(var11, var1, cloneDejaVu(var3));
         }

         var1.append('}');
      }
   }

   private static void appendCollection(Object var0, StringBuilder var1, Set<Object> var2) {
      Set var3 = getOrCreateDejaVu(var2);
      boolean var4 = !var3.add(var0);
      if (var4) {
         String var5 = identityToString(var0);
         var1.append("[...").append(var5).append("...]");
      } else {
         Collection var9 = (Collection)var0;
         var1.append('[');
         boolean var6 = true;

         for (Object var8 : var9) {
            if (var6) {
               var6 = false;
            } else {
               var1.append(", ");
            }

            recursiveDeepToString(var8, var1, cloneDejaVu(var3));
         }

         var1.append(']');
      }
   }

   private static Set<Object> getOrCreateDejaVu(Set<Object> var0) {
      return var0 == null ? createDejaVu() : var0;
   }

   private static Set<Object> createDejaVu() {
      return Collections.newSetFromMap(new IdentityHashMap<>());
   }

   private static Set<Object> cloneDejaVu(Set<Object> var0) {
      Set var1 = createDejaVu();
      var1.addAll(var0);
      return var1;
   }

   private static void tryObjectToString(Object var0, StringBuilder var1) {
      try {
         var1.append(var0.toString());
      } catch (Throwable var3) {
         handleErrorInObjectToString(var0, var1, var3);
      }
   }

   private static void handleErrorInObjectToString(Object var0, StringBuilder var1, Throwable var2) {
      var1.append("[!!!");
      var1.append(identityToString(var0));
      var1.append("=>");
      String var3 = var2.getMessage();
      String var4 = var2.getClass().getName();
      var1.append(var4);
      if (!var4.equals(var3)) {
         var1.append(":");
         var1.append(var3);
      }

      var1.append("!!!]");
   }

   static String identityToString(Object var0) {
      return var0 == null ? null : var0.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(var0));
   }
}
