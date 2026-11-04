package net.md_5.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandIP extends PlayerCommand {
   public CommandIP() {
      super("ip", "bungeecord.command.ip");
   }

   @Override
   public void execute(CommandSender var1, String[] var2) {
      if (var2.length < 1) {
         var1.sendMessage(ProxyServer.getInstance().getTranslation("username_needed"));
      } else {
         ProxiedPlayer var3 = ProxyServer.getInstance().getPlayer(var2[0]);
         if (var3 == null) {
            var1.sendMessage(ProxyServer.getInstance().getTranslation("user_not_online"));
         } else {
            var1.sendMessage(ProxyServer.getInstance().getTranslation("command_ip", var2[0], var3.getSocketAddress()));
         }
      }
   }
}
