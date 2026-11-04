package net.md_5.bungee.command;

import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandList
extends Command {
    public CommandList() {
        super("list", "bungeecord.command.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String playerNames = ProxyServer.getInstance().getPlayers().stream()
            .map(ProxiedPlayer::getName)
            .collect(Collectors.joining(", "));
        sender.sendMessage(ChatColor.GREEN + "Players (" + ProxyServer.getInstance().getPlayers().size() + "): "
            + ChatColor.WHITE + (playerNames.isEmpty() ? "(none)" : playerNames));
    }
}
