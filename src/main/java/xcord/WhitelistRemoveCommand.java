package xcord;

import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import xcord.SubCommand;

public class WhitelistRemoveCommand
extends SubCommand {
    private final AntiProxyWhitelistManager whitelist;

    public WhitelistRemoveCommand(AntiProxyWhitelistManager antiProxyWhitelistManager) {
        this.whitelist = antiProxyWhitelistManager;
        this.usage = "<ip/username>";
        this.permission = "xcord.command.vpn";
        this.minArgs = 1;
    }

    @Override
    public void execute(CommandSender commandSender, String[] stringArray) throws IllegalArgumentException {
        if (this.whitelist.removeWhitelistedEntry(stringArray[0])) {
            commandSender.sendMessage(ChatColor.RED + stringArray[0] + " has been removed from the anti-vpn/proxy whitelist...");
        } else {
            commandSender.sendMessage(ChatColor.RED + stringArray[0] + " was not whitelisted?");
        }
    }
}

