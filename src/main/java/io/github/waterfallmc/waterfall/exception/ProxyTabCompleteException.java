package io.github.waterfallmc.waterfall.exception;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ProxyTabCompleteException extends ProxyCommandException {
   public ProxyTabCompleteException(String var1, Throwable var2, Command var3, CommandSender var4, String[] var5) {
      super(var1, var2, var3, var4, var5);
   }

   public ProxyTabCompleteException(Throwable var1, Command var2, CommandSender var3, String[] var4) {
      super(var1, var2, var3, var4);
   }

   protected ProxyTabCompleteException(String var1, Throwable var2, boolean var3, boolean var4, Command var5, CommandSender var6, String[] var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }
}
