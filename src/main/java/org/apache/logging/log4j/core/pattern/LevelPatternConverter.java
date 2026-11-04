package org.apache.logging.log4j.core.pattern;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "LevelPatternConverter",
   category = "Converter"
)
@ConverterKeys({"p", "level"})
@PerformanceSensitive({"allocation"})
public class LevelPatternConverter extends LogEventPatternConverter {
   private static final String OPTION_LENGTH = "length";
   private static final String OPTION_LOWER = "lowerCase";
   private static final LevelPatternConverter INSTANCE = new LevelPatternConverter.SimpleLevelPatternConverter();

   private LevelPatternConverter() {
      super("Level", "level");
   }

   public static LevelPatternConverter newInstance(String[] var0) {
      if (var0 != null && var0.length != 0) {
         HashMap var1 = new HashMap();
         int var2 = Integer.MAX_VALUE;
         boolean var3 = false;
         String[] var4 = var0[0].split(Patterns.COMMA_SEPARATOR);

         for (String var8 : var4) {
            String[] var9 = var8.split("=");
            if (var9 != null && var9.length == 2) {
               String var10 = var9[0].trim();
               String var11 = var9[1].trim();
               if ("length".equalsIgnoreCase(var10)) {
                  var2 = Integer.parseInt(var11);
               } else if ("lowerCase".equalsIgnoreCase(var10)) {
                  var3 = Boolean.parseBoolean(var11);
               } else {
                  Level var12 = Level.toLevel(var10, null);
                  if (var12 == null) {
                     LOGGER.error("Invalid Level {}", var10);
                  } else {
                     var1.put(var12, var11);
                  }
               }
            } else {
               LOGGER.error("Invalid option {}", var8);
            }
         }

         if (var1.isEmpty() && var2 == Integer.MAX_VALUE && !var3) {
            return INSTANCE;
         } else {
            for (Level var16 : Level.values()) {
               if (!var1.containsKey(var16)) {
                  String var17 = left(var16, var2);
                  var1.put(var16, var3 ? var17.toLowerCase(Locale.US) : var17);
               }
            }

            return new LevelPatternConverter.LevelMapLevelPatternConverter(var1);
         }
      } else {
         return INSTANCE;
      }
   }

   private static String left(Level var0, int var1) {
      String var2 = var0.toString();
      return var1 >= var2.length() ? var2 : var2.substring(0, var1);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      throw new UnsupportedOperationException("Overridden by subclasses");
   }

   @Override
   public String getStyleClass(Object var1) {
      return var1 instanceof LogEvent ? "level " + ((LogEvent)var1).getLevel().name().toLowerCase(Locale.ENGLISH) : "level";
   }

   private static final class LevelMapLevelPatternConverter extends LevelPatternConverter {
      private final Map<Level, String> levelMap;

      private LevelMapLevelPatternConverter(Map<Level, String> var1) {
         this.levelMap = var1;
      }

      @Override
      public void format(LogEvent var1, StringBuilder var2) {
         var2.append(this.levelMap.get(var1.getLevel()));
      }
   }

   private static final class SimpleLevelPatternConverter extends LevelPatternConverter {
      private SimpleLevelPatternConverter() {
      }

      @Override
      public void format(LogEvent var1, StringBuilder var2) {
         var2.append(var1.getLevel());
      }
   }
}
