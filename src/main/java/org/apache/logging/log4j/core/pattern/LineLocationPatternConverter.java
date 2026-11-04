package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.LocationAware;

@Plugin(
   name = "LineLocationPatternConverter",
   category = "Converter"
)
@ConverterKeys({"L", "line"})
public final class LineLocationPatternConverter extends LogEventPatternConverter implements LocationAware {
   private static final LineLocationPatternConverter INSTANCE = new LineLocationPatternConverter();

   private LineLocationPatternConverter() {
      super("Line", "line");
   }

   public static LineLocationPatternConverter newInstance(String[] var0) {
      return INSTANCE;
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      StackTraceElement var3 = var1.getSource();
      if (var3 != null) {
         var2.append(var3.getLineNumber());
      }
   }

   @Override
   public boolean requiresLocation() {
      return true;
   }
}
