package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;

public class CommandReload extends Command {
   public CommandReload() {
      super("greload", "bungeecord.command.reload");
   }

   @Override
   public void execute(CommandSender var1, String[] var2) {
      BungeeCord.getInstance().config.load();
      BungeeCord.getInstance().reloadMessages();
      BungeeCord.getInstance().stopListeners();
      BungeeCord.getInstance().startListeners();
      BungeeCord.getInstance().getPluginManager().callEvent(new ProxyReloadEvent(var1));
      var1.sendMessage(
         ChatColor.BOLD.toString()
            + ChatColor.RED.toString()
            + "Travertine has been reloaded. This is NOT advisable and you will not be supported with any issues that arise! Please restart Travertine ASAP."
      );
   }
}
