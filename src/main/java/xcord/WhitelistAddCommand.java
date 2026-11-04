package xcord;

import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import xcord.SubCommand;

public class WhitelistAddCommand
extends SubCommand {
    private final AntiProxyWhitelistManager whitelist;

    public WhitelistAddCommand(AntiProxyWhitelistManager antiProxyWhitelistManager) {
        this.whitelist = antiProxyWhitelistManager;
        this.usage = "<ip/username>";
        this.permission = "xcord.command.vpn";
        this.minArgs = 1;
    }

    @Override
    public void execute(CommandSender commandSender, String[] stringArray) throws IllegalArgumentException {
        this.whitelist.whitelistEntry(stringArray[0]);
        commandSender.sendMessage(ChatColor.GRAY + stringArray[0] + " has been added to the anti-vpn/proxy whitelist...");
    }
}

