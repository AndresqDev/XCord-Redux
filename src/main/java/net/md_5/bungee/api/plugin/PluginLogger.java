package net.md_5.bungee.api.plugin;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger extends Logger {
   private final String pluginName;

   protected PluginLogger(Plugin var1) {
      super(var1.getClass().getCanonicalName(), null);
      this.pluginName = "[" + var1.getDescription().getName() + "] ";
      this.setParent(var1.getProxy().getLogger());
   }

   @Override
   public void log(LogRecord var1) {
      var1.setMessage(this.pluginName + var1.getMessage());
      super.log(var1);
   }
}
