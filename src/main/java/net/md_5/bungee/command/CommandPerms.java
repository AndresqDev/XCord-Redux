package net.md_5.bungee.command;

import java.util.HashSet;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class CommandPerms extends Command {
   public CommandPerms() {
      super("perms");
   }

   @Override
   public void execute(CommandSender var1, String[] var2) {
      HashSet var3 = new HashSet();

      for (String var5 : var1.getGroups()) {
         var3.addAll(ProxyServer.getInstance().getConfigurationAdapter().getPermissions(var5));
      }

      var1.sendMessage(ProxyServer.getInstance().getTranslation("command_perms_groups", Util.csv(var1.getGroups())));

      for (String var7 : var3) {
         var1.sendMessage(ProxyServer.getInstance().getTranslation("command_perms_permission", var7));
      }
   }
}
