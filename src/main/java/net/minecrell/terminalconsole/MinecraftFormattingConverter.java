package net.minecrell.terminalconsole;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

@Plugin(
   name = "minecraftFormatting",
   category = "Converter"
)
@ConverterKeys({"minecraftFormatting"})
@PerformanceSensitive({"allocation"})
public final class MinecraftFormattingConverter extends LogEventPatternConverter {
   public static final String KEEP_FORMATTING_PROPERTY = "terminal.keepMinecraftFormatting";
   private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty("terminal.keepMinecraftFormatting");
   static final String ANSI_RESET = "\u001b[m";
   private static final char COLOR_CHAR = '§';
   private static final String LOOKUP = "0123456789abcdefklmnor";
   private static final String[] ansiCodes = new String[]{
      "\u001b[0;30m",
      "\u001b[0;34m",
      "\u001b[0;32m",
      "\u001b[0;36m",
      "\u001b[0;31m",
      "\u001b[0;35m",
      "\u001b[0;33m",
      "\u001b[0;37m",
      "\u001b[0;30;1m",
      "\u001b[0;34;1m",
      "\u001b[0;32;1m",
      "\u001b[0;36;1m",
      "\u001b[0;31;1m",
      "\u001b[0;35;1m",
      "\u001b[0;33;1m",
      "\u001b[0;37;1m",
      "\u001b[5m",
      "\u001b[21m",
      "\u001b[9m",
      "\u001b[4m",
      "\u001b[3m",
      "\u001b[m"
   };
   private final boolean ansi;
   private final List<PatternFormatter> formatters;

   protected MinecraftFormattingConverter(List<PatternFormatter> var1, boolean var2) {
      super("minecraftFormatting", null);
      this.formatters = var1;
      this.ansi = !var2;
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();
      int var4 = 0;

      for (int var5 = this.formatters.size(); var4 < var5; var4++) {
         this.formatters.get(var4).format(var1, var2);
      }

      if (!KEEP_FORMATTING && var2.length() != var3) {
         String var6 = var2.substring(var3);
         format(var6, var2, var3, this.ansi && TerminalConsoleAppender.isAnsiSupported());
      }
   }

   static void format(String var0, StringBuilder var1, int var2, boolean var3) {
      int var4 = var0.indexOf(167);
      int var5 = var0.length() - 1;
      if (var4 != -1 && var4 != var5) {
         var1.setLength(var2 + var4);
         int var6 = var4;

         do {
            int var7 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(var0.charAt(var4 + 1)));
            if (var7 != -1) {
               if (var6 != var4) {
                  var1.append(var0, var6, var4);
               }

               if (var3) {
                  var1.append(ansiCodes[var7]);
               }

               var4 += 2;
               var6 = var4;
            } else {
               var4++;
            }

            var4 = var0.indexOf(167, var4);
         } while (var4 != -1 && var4 < var5);

         var1.append(var0, var6, var0.length());
         if (var3) {
            var1.append("\u001b[m");
         }
      }
   }

   @Nullable
   public static MinecraftFormattingConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length < 1 || var1.length > 2) {
         LOGGER.error("Incorrect number of options on minecraftFormatting. Expected at least 1, max 2 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on minecraftFormatting");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         boolean var4 = var1.length > 1 && "strip".equals(var1[1]);
         return new MinecraftFormattingConverter(var3, var4);
      }
   }
}
