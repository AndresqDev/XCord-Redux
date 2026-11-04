package org.apache.logging.log4j.core.pattern;

import java.util.Objects;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.MapMessage;

@Plugin(
   name = "MapPatternConverter",
   category = "Converter"
)
@ConverterKeys({"K", "map", "MAP"})
public final class MapPatternConverter extends LogEventPatternConverter {
   private static final String JAVA_UNQUOTED = MapMessage.MapFormat.JAVA_UNQUOTED.name();
   private final String key;
   private final String[] format;

   private MapPatternConverter(String[] var1, String... var2) {
      super(var1 != null && var1.length > 0 ? "MAP{" + var1[0] + '}' : "MAP", "map");
      this.key = var1 != null && var1.length > 0 ? var1[0] : null;
      this.format = var2;
   }

   public static MapPatternConverter newInstance(String[] var0) {
      return new MapPatternConverter(var0, JAVA_UNQUOTED);
   }

   public static MapPatternConverter newInstance(String[] var0, MapMessage.MapFormat var1) {
      return new MapPatternConverter(var0, Objects.toString(var1, JAVA_UNQUOTED));
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      if (var1.getMessage() instanceof MapMessage) {
         MapMessage var3 = (MapMessage)var1.getMessage();
         if (this.key == null) {
            var3.formatTo(this.format, var2);
         } else {
            String var4 = var3.get(this.key);
            if (var4 != null) {
               var2.append(var4);
            }
         }
      }
   }
}
