package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xcord.XCord;
import xcord.config.XCordConfig;
import xcord.message.Messages;

public class CommandXCordReload extends Command {
    public CommandXCordReload() {
        super("xcordreload", "bungeecord.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        XCord.setConfig(new XCordConfig());
        XCord.setMessages(new Messages());
        XCord.getInstance().reload();
        sender.sendMessage(ChatColor.BOLD.toString() + ChatColor.RED.toString() + "XCord has been reloaded. Not all modules may be using new configuration values!");
    }
}
