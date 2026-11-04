package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyPluginException extends ProxyException {
   private final Plugin responsiblePlugin;

   public ProxyPluginException(String var1, Throwable var2, Plugin var3) {
      super(var1, var2);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(var3, "responsiblePlugin");
   }

   public ProxyPluginException(Throwable var1, Plugin var2) {
      super(var1);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(var2, "responsiblePlugin");
   }

   protected ProxyPluginException(String var1, Throwable var2, boolean var3, boolean var4, Plugin var5) {
      super(var1, var2, var3, var4);
      this.responsiblePlugin = (Plugin)Preconditions.checkNotNull(var5, "responsiblePlugin");
   }

   public Plugin getResponsiblePlugin() {
      return this.responsiblePlugin;
   }
}
