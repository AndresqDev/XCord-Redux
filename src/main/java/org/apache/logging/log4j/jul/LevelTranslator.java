package org.apache.logging.log4j.jul;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class LevelTranslator {
   public static final Level FINEST = Level.forName("FINEST", Level.TRACE.intLevel() + 100);
   public static final Level CONFIG = Level.forName("CONFIG", Level.INFO.intLevel() + 50);
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final LevelConverter LEVEL_CONVERTER;

   public static Level toLevel(java.util.logging.Level var0) {
      return LEVEL_CONVERTER.toLevel(var0);
   }

   public static java.util.logging.Level toJavaLevel(Level var0) {
      return LEVEL_CONVERTER.toJavaLevel(var0);
   }

   private LevelTranslator() {
   }

   static {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j.jul.levelConverter");
      if (var0 != null) {
         Object var1;
         try {
            var1 = LoaderUtil.newCheckedInstanceOf(var0, LevelConverter.class);
         } catch (Exception var3) {
            LOGGER.error("Could not create custom LevelConverter [{}].", var0, var3);
            var1 = new DefaultLevelConverter();
         }

         LEVEL_CONVERTER = (LevelConverter)var1;
      } else {
         LEVEL_CONVERTER = new DefaultLevelConverter();
      }
   }
}
