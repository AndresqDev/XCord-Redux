package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungee extends Command {
   public CommandBungee() {
      super("bungee");
   }

   @Override
   public void execute(CommandSender var1, String[] var2) {
      var1.sendMessage(ChatColor.BLUE + "This server is running Travertine version " + ProxyServer.getInstance().getVersion() + " by md_5");
   }
}
