package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xcord.Messages;
import xcord.XCord;
import xcord.XCordConfig;

public class CommandXCordReload extends Command {
   public CommandXCordReload() {
      super("xcordreload", "bungeecord.command.reload");
   }

   @Override
   public void execute(CommandSender var1, String[] var2) {
      XCord.setConfig(new XCordConfig());
      XCord.setMessages(new Messages());
      XCord.getInstance().reload();
      var1.sendMessage(ChatColor.BOLD.toString() + ChatColor.RED.toString() + "XCord has been reloaded. Not all modules may be using new configuration values!");
   }
}
