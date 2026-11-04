package xcord.command;

import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class WhitelistRemoveCommand extends SubCommand {
    private final AntiProxyWhitelistManager whitelist;

    public WhitelistRemoveCommand(AntiProxyWhitelistManager whitelist) {
        this.whitelist = whitelist;
        this.usage = "<ip/username>";
        this.permission = "xcord.command.vpn";
        this.minArgs = 1;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws IllegalArgumentException {
        if (this.whitelist.removeWhitelistedEntry(args[0])) {
            sender.sendMessage(ChatColor.RED + args[0] + " has been removed from the anti-vpn/proxy whitelist...");
        } else {
            sender.sendMessage(ChatColor.RED + args[0] + " was not whitelisted?");
        }
    }
}
