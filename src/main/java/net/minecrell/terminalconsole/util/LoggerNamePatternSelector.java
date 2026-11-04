package net.minecrell.terminalconsole.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.PatternMatch;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "LoggerNamePatternSelector",
   category = "Core",
   elementType = "patternSelector"
)
@PerformanceSensitive({"allocation"})
public final class LoggerNamePatternSelector implements PatternSelector {
   private final PatternFormatter[] defaultFormatters;
   private final List<LoggerNamePatternSelector.LoggerNameSelector> formatters = new ArrayList<>();

   protected LoggerNamePatternSelector(String var1, PatternMatch[] var2, boolean var3, boolean var4, boolean var5, Configuration var6) {
      PatternParser var7 = PatternLayout.createPatternParser(var6);
      PatternFormatter[] var8 = new PatternFormatter[0];
      this.defaultFormatters = var7.parse(var1, var3, var4, var5).toArray(var8);

      for (PatternMatch var12 : var2) {
         PatternFormatter[] var13 = var7.parse(var12.getPattern(), var3, var4, var5).toArray(var8);

         for (String var17 : var12.getKey().split(",")) {
            this.formatters.add(new LoggerNamePatternSelector.LoggerNameSelector(var17, var13));
         }
      }
   }

   public PatternFormatter[] getFormatters(LogEvent var1) {
      String var2 = var1.getLoggerName();
      if (var2 != null) {
         for (int var3 = 0; var3 < this.formatters.size(); var3++) {
            LoggerNamePatternSelector.LoggerNameSelector var4 = this.formatters.get(var3);
            if (var4.test(var2)) {
               return var4.get();
            }
         }
      }

      return this.defaultFormatters;
   }

   @PluginFactory
   public static LoggerNamePatternSelector createSelector(
      @Required(message = "Default pattern is required") @PluginAttribute("defaultPattern") String var0,
      @PluginElement("PatternMatch") PatternMatch[] var1,
      @PluginAttribute(value = "alwaysWriteExceptions",defaultBoolean = true) boolean var2,
      @PluginAttribute("disableAnsi") boolean var3,
      @PluginAttribute("noConsoleNoAnsi") boolean var4,
      @PluginConfiguration Configuration var5
   ) {
      return new LoggerNamePatternSelector(var0, var1, var2, var3, var4, var5);
   }

   private static class LoggerNameSelector {
      private final String name;
      private final boolean isPackage;
      private final PatternFormatter[] formatters;

      LoggerNameSelector(String var1, PatternFormatter[] var2) {
         this.name = var1;
         this.isPackage = var1.endsWith(".");
         this.formatters = var2;
      }

      PatternFormatter[] get() {
         return this.formatters;
      }

      boolean test(String var1) {
         return this.isPackage ? var1.startsWith(this.name) : var1.equals(this.name);
      }
   }
}
