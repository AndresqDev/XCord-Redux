package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "EndOfBatchPatternConverter",
   category = "Converter"
)
@ConverterKeys({"endOfBatch"})
@PerformanceSensitive({"allocation"})
public final class EndOfBatchPatternConverter extends LogEventPatternConverter {
   private static final EndOfBatchPatternConverter INSTANCE = new EndOfBatchPatternConverter();

   private EndOfBatchPatternConverter() {
      super("LoggerFqcn", "loggerFqcn");
   }

   public static EndOfBatchPatternConverter newInstance(String[] var0) {
      return INSTANCE;
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(var1.isEndOfBatch());
   }
}
