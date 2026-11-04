package xcord.command;

import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class WhitelistAddCommand extends SubCommand {
    private final AntiProxyWhitelistManager whitelist;

    public WhitelistAddCommand(AntiProxyWhitelistManager whitelist) {
        this.whitelist = whitelist;
        this.usage = "<ip/username>";
        this.permission = "xcord.command.vpn";
        this.minArgs = 1;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws IllegalArgumentException {
        this.whitelist.whitelistEntry(args[0]);
        sender.sendMessage(ChatColor.GRAY + args[0] + " has been added to the anti-vpn/proxy whitelist...");
    }
}
