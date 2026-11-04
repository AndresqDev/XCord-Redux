package org.apache.logging.log4j.core.config;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "loggers",
   category = "Core"
)
public final class LoggersPlugin {
   private LoggersPlugin() {
   }

   @PluginFactory
   public static Loggers createLoggers(@PluginElement("Loggers") LoggerConfig[] var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap();
      LoggerConfig var2 = null;

      for (LoggerConfig var6 : var0) {
         if (var6 != null) {
            if (var6.getName().isEmpty()) {
               if (var2 != null) {
                  throw new IllegalStateException("Configuration has multiple root loggers. There can be only one.");
               }

               var2 = var6;
            }

            var1.put(var6.getName(), var6);
         }
      }

      return new Loggers(var1, var2);
   }
}
