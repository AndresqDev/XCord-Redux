package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "repeat",
   category = "Converter"
)
@ConverterKeys({":|", "repeat"})
@PerformanceSensitive({"allocation"})
public final class RepeatPatternConverter extends LogEventPatternConverter {
   private final String result;

   public static RepeatPatternConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 2) {
         LOGGER.error("Incorrect number of options on repeat. Expected 2 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No string supplied on repeat");
         return null;
      } else if (var1[1] == null) {
         LOGGER.error("No repeat count supplied on repeat");
         return null;
      } else {
         int var2 = 0;
         String var3 = var1[0];

         try {
            var2 = Integer.parseInt(var1[1].trim());
            var3 = Strings.repeat(var1[0], var2);
         } catch (Exception var5) {
            LOGGER.error("The repeat count is not an integer: {}", var1[1].trim());
         }

         return new RepeatPatternConverter(var3);
      }
   }

   private RepeatPatternConverter(String var1) {
      super("repeat", "repeat");
      this.result = var1;
   }

   @Override
   public void format(Object var1, StringBuilder var2) {
      this.format(var2);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      this.format(var2);
   }

   private void format(StringBuilder var1) {
      if (this.result != null) {
         var1.append(this.result);
      }
   }
}
