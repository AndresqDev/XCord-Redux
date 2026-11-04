package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xcord.XCord;

public class CommandResetBlacklist extends Command {
    public CommandResetBlacklist() {
        super("clearblacklist", "bungeecord.command.resetblacklist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        XCord.getBlacklistManager().clear();
        sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.RED.toString() + " X Blacklist reset");
    }
}
