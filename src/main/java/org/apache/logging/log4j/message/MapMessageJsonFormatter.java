package org.apache.logging.log4j.message;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

enum MapMessageJsonFormatter {
   public static final int MAX_DEPTH = readMaxDepth();
   private static final char DQUOTE = '"';
   private static final char RBRACE = ']';
   private static final char LBRACE = '[';
   private static final char COMMA = ',';
   private static final char RCURLY = '}';
   private static final char LCURLY = '{';
   private static final char COLON = ':';

   private static int readMaxDepth() {
      int var0 = PropertiesUtil.getProperties().getIntegerProperty("log4j2.mapMessage.jsonFormatter.maxDepth", 8);
      if (var0 < 0) {
         throw new IllegalArgumentException("was expecting a positive maxDepth, found: " + var0);
      } else {
         return var0;
      }
   }

   static void format(StringBuilder var0, Object var1) {
      format(var0, var1, 0);
   }

   private static void format(StringBuilder var0, Object var1, int var2) {
      if (var2 >= MAX_DEPTH) {
         throw new IllegalArgumentException("maxDepth has been exceeded");
      } else {
         if (var1 == null) {
            var0.append("null");
         } else if (var1 instanceof IndexedStringMap) {
            IndexedStringMap var3 = (IndexedStringMap)var1;
            formatIndexedStringMap(var0, var3, var2);
         } else if (var1 instanceof Map) {
            Map var4 = (Map)var1;
            formatMap(var0, var4, var2);
         } else if (var1 instanceof List) {
            List var5 = (List)var1;
            formatList(var0, var5, var2);
         } else if (var1 instanceof Collection) {
            Collection var6 = (Collection)var1;
            formatCollection(var0, var6, var2);
         } else if (var1 instanceof Number) {
            Number var7 = (Number)var1;
            formatNumber(var0, var7);
         } else if (var1 instanceof Boolean) {
            boolean var8 = (Boolean)var1;
            formatBoolean(var0, var8);
         } else if (var1 instanceof StringBuilderFormattable) {
            StringBuilderFormattable var9 = (StringBuilderFormattable)var1;
            formatFormattable(var0, var9);
         } else if (var1 instanceof char[]) {
            char[] var10 = (char[])var1;
            formatCharArray(var0, var10);
         } else if (var1 instanceof boolean[]) {
            boolean[] var11 = (boolean[])var1;
            formatBooleanArray(var0, var11);
         } else if (var1 instanceof byte[]) {
            byte[] var12 = (byte[])var1;
            formatByteArray(var0, var12);
         } else if (var1 instanceof short[]) {
            short[] var13 = (short[])var1;
            formatShortArray(var0, var13);
         } else if (var1 instanceof int[]) {
            int[] var14 = (int[])var1;
            formatIntArray(var0, var14);
         } else if (var1 instanceof long[]) {
            long[] var15 = (long[])var1;
            formatLongArray(var0, var15);
         } else if (var1 instanceof float[]) {
            float[] var16 = (float[])var1;
            formatFloatArray(var0, var16);
         } else if (var1 instanceof double[]) {
            double[] var17 = (double[])var1;
            formatDoubleArray(var0, var17);
         } else if (var1 instanceof Object[]) {
            Object[] var18 = (Object[])var1;
            formatObjectArray(var0, var18, var2);
         } else {
            formatString(var0, var1);
         }
      }
   }

   private static void formatIndexedStringMap(StringBuilder var0, IndexedStringMap var1, int var2) {
      var0.append('{');
      int var3 = var2 + 1;

      for (int var4 = 0; var4 < var1.size(); var4++) {
         String var5 = var1.getKeyAt(var4);
         Object var6 = var1.getValueAt(var4);
         if (var4 > 0) {
            var0.append(',');
         }

         var0.append('"');
         int var7 = var0.length();
         var0.append(var5);
         StringBuilders.escapeJson(var0, var7);
         var0.append('"').append(':');
         format(var0, var6, var3);
      }

      var0.append('}');
   }

   private static void formatMap(StringBuilder var0, Map<Object, Object> var1, int var2) {
      var0.append('{');
      int var3 = var2 + 1;
      boolean[] var4 = new boolean[]{true};
      var1.forEach((var3x, var4x) -> {
         if (var3x == null) {
            throw new IllegalArgumentException("null keys are not allowed");
         } else {
            if (var4[0]) {
               var4[0] = false;
            } else {
               var0.append(',');
            }

            var0.append('"');
            String var5 = String.valueOf(var3x);
            int var6 = var0.length();
            var0.append(var5);
            StringBuilders.escapeJson(var0, var6);
            var0.append('"').append(':');
            format(var0, var4x, var3);
         }
      });
      var0.append('}');
   }

   private static void formatList(StringBuilder var0, List<Object> var1, int var2) {
      var0.append('[');
      int var3 = var2 + 1;

      for (int var4 = 0; var4 < var1.size(); var4++) {
         if (var4 > 0) {
            var0.append(',');
         }

         Object var5 = var1.get(var4);
         format(var0, var5, var3);
      }

      var0.append(']');
   }

   private static void formatCollection(StringBuilder var0, Collection<Object> var1, int var2) {
      var0.append('[');
      int var3 = var2 + 1;
      boolean[] var4 = new boolean[]{true};
      var1.forEach(var3x -> {
         if (var4[0]) {
            var4[0] = false;
         } else {
            var0.append(',');
         }

         format(var0, var3x, var3);
      });
      var0.append(']');
   }

   private static void formatNumber(StringBuilder var0, Number var1) {
      if (var1 instanceof BigDecimal) {
         BigDecimal var2 = (BigDecimal)var1;
         var0.append(var2.toString());
      } else if (var1 instanceof Double) {
         double var6 = (Double)var1;
         var0.append(var6);
      } else if (var1 instanceof Float) {
         float var7 = (Float)var1;
         var0.append(var7);
      } else if (!(var1 instanceof Byte) && !(var1 instanceof Short) && !(var1 instanceof Integer) && !(var1 instanceof Long)) {
         long var9 = var1.longValue();
         double var4 = var1.doubleValue();
         if (Double.compare((double)var9, var4) == 0) {
            var0.append(var9);
         } else {
            var0.append(var4);
         }
      } else {
         long var8 = var1.longValue();
         var0.append(var8);
      }
   }

   private static void formatBoolean(StringBuilder var0, boolean var1) {
      var0.append(var1);
   }

   private static void formatFormattable(StringBuilder var0, StringBuilderFormattable var1) {
      var0.append('"');
      int var2 = var0.length();
      var1.formatTo(var0);
      StringBuilders.escapeJson(var0, var2);
      var0.append('"');
   }

   private static void formatCharArray(StringBuilder var0, char[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         char var3 = var1[var2];
         var0.append('"');
         int var4 = var0.length();
         var0.append(var3);
         StringBuilders.escapeJson(var0, var4);
         var0.append('"');
      }

      var0.append(']');
   }

   private static void formatBooleanArray(StringBuilder var0, boolean[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         boolean var3 = var1[var2];
         var0.append(var3);
      }

      var0.append(']');
   }

   private static void formatByteArray(StringBuilder var0, byte[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         byte var3 = var1[var2];
         var0.append((int)var3);
      }

      var0.append(']');
   }

   private static void formatShortArray(StringBuilder var0, short[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         short var3 = var1[var2];
         var0.append((int)var3);
      }

      var0.append(']');
   }

   private static void formatIntArray(StringBuilder var0, int[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         int var3 = var1[var2];
         var0.append(var3);
      }

      var0.append(']');
   }

   private static void formatLongArray(StringBuilder var0, long[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         long var3 = var1[var2];
         var0.append(var3);
      }

      var0.append(']');
   }

   private static void formatFloatArray(StringBuilder var0, float[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         float var3 = var1[var2];
         var0.append(var3);
      }

      var0.append(']');
   }

   private static void formatDoubleArray(StringBuilder var0, double[] var1) {
      var0.append('[');

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var2 > 0) {
            var0.append(',');
         }

         double var3 = var1[var2];
         var0.append(var3);
      }

      var0.append(']');
   }

   private static void formatObjectArray(StringBuilder var0, Object[] var1, int var2) {
      var0.append('[');
      int var3 = var2 + 1;

      for (int var4 = 0; var4 < var1.length; var4++) {
         if (var4 > 0) {
            var0.append(',');
         }

         Object var5 = var1[var4];
         format(var0, var5, var3);
      }

      var0.append(']');
   }

   private static void formatString(StringBuilder var0, Object var1) {
      var0.append('"');
      int var2 = var0.length();
      String var3 = String.valueOf(var1);
      var0.append(var3);
      StringBuilders.escapeJson(var0, var2);
      var0.append('"');
   }
}
