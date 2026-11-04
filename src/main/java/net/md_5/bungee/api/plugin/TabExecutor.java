package net.md_5.bungee.api.plugin;

import net.md_5.bungee.api.CommandSender;

public interface TabExecutor {
   Iterable<String> onTabComplete(CommandSender var1, String[] var2);
}
