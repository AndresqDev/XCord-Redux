package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandServer
extends Command {
    public CommandServer() {
        super("server", "bungeecord.command.server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /server <name>");
            return;
        }
        ServerInfo info = ProxyServer.getInstance().getServerInfo(args[0]);
        if (info == null) {
            player.sendMessage(ChatColor.RED + "No such server: " + args[0]);
            return;
        }
        player.connect(info);
    }
}
