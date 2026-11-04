package org.apache.logging.log4j.core.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "appenders",
   category = "Core"
)
public final class AppendersPlugin {
   private AppendersPlugin() {
   }

   @PluginFactory
   public static ConcurrentMap<String, Appender> createAppenders(@PluginElement("Appenders") Appender[] var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap(var0.length);

      for (Appender var5 : var0) {
         var1.put(var5.getName(), var5);
      }

      return var1;
   }
}
