package io.github.waterfallmc.waterfall.log4j;

import com.google.common.base.Strings;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.jul.LevelTranslator;
import org.apache.logging.log4j.message.MessageFormatMessage;

class Log4JLogHandler extends Handler {
   private final Map<String, Logger> cache = new ConcurrentHashMap<>();

   @Override
   public void publish(LogRecord var1) {
      if (this.isLoggable(var1)) {
         Logger var2 = this.cache.computeIfAbsent(Strings.nullToEmpty(var1.getLoggerName()), LogManager::getLogger);
         String var3 = var1.getMessage();
         if (var1.getResourceBundle() != null) {
            try {
               var3 = var1.getResourceBundle().getString(var3);
            } catch (MissingResourceException var5) {
            }
         }

         Level var4 = LevelTranslator.toLevel(var1.getLevel());
         if (var1.getParameters() != null && var1.getParameters().length > 0) {
            var2.log(var4, new MessageFormatMessage(var3, var1.getParameters()), var1.getThrown());
         } else {
            var2.log(var4, var3, var1.getThrown());
         }
      }
   }

   @Override
   public void flush() {
   }

   @Override
   public void close() {
   }
}
