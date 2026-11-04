package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginEnableDisableException extends ProxyPluginException {
   public ProxyPluginEnableDisableException(String var1, Throwable var2, Plugin var3) {
      super(var1, var2, var3);
   }

   public ProxyPluginEnableDisableException(Throwable var1, Plugin var2) {
      super(var1, var2);
   }

   protected ProxyPluginEnableDisableException(String var1, Throwable var2, boolean var3, boolean var4, Plugin var5) {
      super(var1, var2, var3, var4, var5);
   }
}
