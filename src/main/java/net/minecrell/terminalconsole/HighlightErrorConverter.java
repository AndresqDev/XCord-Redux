package net.minecrell.terminalconsole;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.checkerframework.checker.nullness.qual.Nullable;

@Plugin(
   name = "highlightError",
   category = "Converter"
)
@ConverterKeys({"highlightError"})
@PerformanceSensitive({"allocation"})
public final class HighlightErrorConverter extends LogEventPatternConverter {
   private static final String ANSI_RESET = "\u001b[m";
   private static final String ANSI_ERROR = "\u001b[31;1m";
   private static final String ANSI_WARN = "\u001b[33;1m";
   private final List<PatternFormatter> formatters;

   protected HighlightErrorConverter(List<PatternFormatter> var1) {
      super("highlightError", null);
      this.formatters = var1;
   }

   public void format(LogEvent var1, StringBuilder var2) {
      if (TerminalConsoleAppender.isAnsiSupported()) {
         Level var3 = var1.getLevel();
         if (var3.isMoreSpecificThan(Level.ERROR)) {
            this.format("\u001b[31;1m", var1, var2);
            return;
         }

         if (var3.isMoreSpecificThan(Level.WARN)) {
            this.format("\u001b[33;1m", var1, var2);
            return;
         }
      }

      int var5 = 0;

      for (int var4 = this.formatters.size(); var5 < var4; var5++) {
         this.formatters.get(var5).format(var1, var2);
      }
   }

   private void format(String var1, LogEvent var2, StringBuilder var3) {
      int var4 = var3.length();
      var3.append(var1);
      int var5 = var3.length();
      int var6 = 0;

      for (int var7 = this.formatters.size(); var6 < var7; var6++) {
         this.formatters.get(var6).format(var2, var3);
      }

      if (var3.length() == var5) {
         var3.setLength(var4);
      } else {
         var3.append("\u001b[m");
      }
   }

   public boolean handlesThrowable() {
      for (PatternFormatter var2 : this.formatters) {
         if (var2.handlesThrowable()) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public static HighlightErrorConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 1) {
         LOGGER.error("Incorrect number of options on highlightError. Expected 1 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on highlightError");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         return new HighlightErrorConverter(var3);
      }
   }
}
