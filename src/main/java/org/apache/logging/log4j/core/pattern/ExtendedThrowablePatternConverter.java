package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableProxy;

@Plugin(
   name = "ExtendedThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"xEx", "xThrowable", "xException"})
public final class ExtendedThrowablePatternConverter extends ThrowablePatternConverter {
   private ExtendedThrowablePatternConverter(Configuration var1, String[] var2) {
      super("ExtendedThrowable", "throwable", var2, var1);
   }

   public static ExtendedThrowablePatternConverter newInstance(Configuration var0, String[] var1) {
      return new ExtendedThrowablePatternConverter(var0, var1);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      ThrowableProxy var3 = var1.getThrownProxy();
      Throwable var4 = var1.getThrown();
      if ((var4 != null || var3 != null) && this.options.anyLines()) {
         if (var3 == null) {
            super.format(var1, var2);
            return;
         }

         int var5 = var2.length();
         if (var5 > 0 && !Character.isWhitespace(var2.charAt(var5 - 1))) {
            var2.append(' ');
         }

         var3.formatExtendedStackTraceTo(
            var2, this.options.getIgnorePackages(), this.options.getTextRenderer(), this.getSuffix(var1), this.options.getSeparator()
         );
      }
   }
}
