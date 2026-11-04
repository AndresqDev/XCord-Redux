package io.github.waterfallmc.waterfall.exception;

import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import net.md_5.bungee.api.ProxyServer;

public class ProxyInternalException extends ProxyException {
   public ProxyInternalException(String var1) {
      super(var1);
   }

   public ProxyInternalException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ProxyInternalException(Throwable var1) {
      super(var1);
   }

   protected ProxyInternalException(String var1, Throwable var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public static void reportInternalException(Throwable var0) {
      try {
         ProxyServer.getInstance().getPluginManager().callEvent(new ProxyExceptionEvent(new ProxyInternalException(var0)));
      } catch (Throwable var2) {
         var2.printStackTrace();
      }
   }
}
