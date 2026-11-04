package io.github.waterfallmc.waterfall.console;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
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

@Plugin(
   name = "paperMinecraftFormatting",
   category = "Converter"
)
@ConverterKeys({"paperMinecraftFormatting"})
@PerformanceSensitive({"allocation"})
public final class HexFormattingConverter extends LogEventPatternConverter {
   private static final boolean KEEP_FORMATTING = PropertiesUtil.getProperties().getBooleanProperty("terminal.keepMinecraftFormatting");
   private static final String ANSI_RESET = "\u001b[m";
   private static final char COLOR_CHAR = '§';
   private static final String LOOKUP = "0123456789abcdefklmnor";
   private static final String RGB_ANSI = "\u001b[38;2;%d;%d;%dm";
   private static final Pattern NAMED_PATTERN = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
   private static final Pattern RGB_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");
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

   protected HexFormattingConverter(List<PatternFormatter> var1, boolean var2) {
      super("paperMinecraftFormatting", null);
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
         boolean var6 = this.ansi && TerminalConsoleAppender.isAnsiSupported();
         String var7 = var6 ? convertRGBColors(var2.substring(var3)) : stripRGBColors(var2.substring(var3));
         format(var7, var2, var3, var6);
      }
   }

   private static String convertRGBColors(String var0) {
      Matcher var1 = RGB_PATTERN.matcher(var0);
      StringBuffer var2 = new StringBuffer();

      while (var1.find()) {
         String var3 = var1.group().replace(String.valueOf('§'), "").replace('x', '#');
         int var4 = Integer.decode(var3);
         int var5 = var4 >> 16 & 0xFF;
         int var6 = var4 >> 8 & 0xFF;
         int var7 = var4 & 0xFF;
         String var8 = String.format("\u001b[38;2;%d;%d;%dm", var5, var6, var7);
         var1.appendReplacement(var2, var8);
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   private static String stripRGBColors(String var0) {
      Matcher var1 = RGB_PATTERN.matcher(var0);
      StringBuffer var2 = new StringBuffer();

      while (var1.find()) {
         var1.appendReplacement(var2, "");
      }

      var1.appendTail(var2);
      return var2.toString();
   }

   static void format(String var0, StringBuilder var1, int var2, boolean var3) {
      int var4 = var0.indexOf(167);
      int var5 = var0.length() - 1;
      if (var4 != -1 && var4 != var5) {
         Matcher var6 = NAMED_PATTERN.matcher(var0);
         StringBuffer var7 = new StringBuffer();

         while (var6.find()) {
            int var8 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(var6.group().charAt(1)));
            if (var8 != -1) {
               var6.appendReplacement(var7, var3 ? ansiCodes[var8] : "");
            }
         }

         var6.appendTail(var7);
         var1.setLength(var2);
         var1.append(var7.toString());
         if (var3) {
            var1.append("\u001b[m");
         }
      } else {
         var1.setLength(var2);
         var1.append(var0);
         if (var3) {
            var1.append("\u001b[m");
         }
      }
   }

   public static HexFormattingConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length < 1 || var1.length > 2) {
         LOGGER.error("Incorrect number of options on paperMinecraftFormatting. Expected at least 1, max 2 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on paperMinecraftFormatting");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         boolean var4 = var1.length > 1 && "strip".equals(var1[1]);
         return new HexFormattingConverter(var3, var4);
      }
   }
}
