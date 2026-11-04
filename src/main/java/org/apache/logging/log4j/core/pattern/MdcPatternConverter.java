package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.TriConsumer;

@Plugin(
   name = "MdcPatternConverter",
   category = "Converter"
)
@ConverterKeys({"X", "mdc", "MDC"})
@PerformanceSensitive({"allocation"})
public final class MdcPatternConverter extends LogEventPatternConverter {
   private final String key;
   private final String[] keys;
   private final boolean full;
   private static final TriConsumer<String, Object, StringBuilder> WRITE_KEY_VALUES_INTO = (var0, var1, var2) -> {
      var2.append(var0).append('=');
      StringBuilders.appendValue(var2, var1);
      var2.append(", ");
   };

   private MdcPatternConverter(String[] var1) {
      super(var1 != null && var1.length > 0 ? "MDC{" + var1[0] + '}' : "MDC", "mdc");
      if (var1 != null && var1.length > 0) {
         this.full = false;
         if (var1[0].indexOf(44) > 0) {
            this.keys = var1[0].split(",");

            for (int var2 = 0; var2 < this.keys.length; var2++) {
               this.keys[var2] = this.keys[var2].trim();
            }

            this.key = null;
         } else {
            this.keys = null;
            this.key = var1[0];
         }
      } else {
         this.full = true;
         this.key = null;
         this.keys = null;
      }
   }

   public static MdcPatternConverter newInstance(String[] var0) {
      return new MdcPatternConverter(var0);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      ReadOnlyStringMap var3 = var1.getContextData();
      if (this.full) {
         if (var3 == null || var3.isEmpty()) {
            var2.append("{}");
            return;
         }

         appendFully(var3, var2);
      } else if (this.keys != null) {
         if (var3 == null || var3.isEmpty()) {
            var2.append("{}");
            return;
         }

         appendSelectedKeys(this.keys, var3, var2);
      } else if (var3 != null) {
         Object var4 = var3.getValue(this.key);
         if (var4 != null) {
            StringBuilders.appendValue(var2, var4);
         }
      }
   }

   private static void appendFully(ReadOnlyStringMap var0, StringBuilder var1) {
      var1.append("{");
      int var2 = var1.length();
      var0.forEach(WRITE_KEY_VALUES_INTO, var1);
      int var3 = var1.length();
      if (var3 > var2) {
         var1.setCharAt(var3 - 2, '}');
         var1.deleteCharAt(var3 - 1);
      } else {
         var1.append('}');
      }
   }

   private static void appendSelectedKeys(String[] var0, ReadOnlyStringMap var1, StringBuilder var2) {
      int var3 = var2.length();
      var2.append('{');

      for (int var4 = 0; var4 < var0.length; var4++) {
         String var5 = var0[var4];
         Object var6 = var1.getValue(var5);
         if (var6 != null) {
            if (var2.length() - var3 > 1) {
               var2.append(", ");
            }

            var2.append(var5).append('=');
            StringBuilders.appendValue(var2, var6);
         }
      }

      var2.append('}');
   }
}
