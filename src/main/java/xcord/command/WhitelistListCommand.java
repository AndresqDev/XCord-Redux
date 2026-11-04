package xcord.command;

import java.math.BigInteger;
import java.net.InetAddress;
import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.CommandSender;

public class WhitelistListCommand extends SubCommand {
    private final AntiProxyWhitelistManager whitelist;

    public WhitelistListCommand(AntiProxyWhitelistManager whitelist) {
        this.whitelist = whitelist;
        this.permission = "xcord.command.vpn";
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws IllegalArgumentException {
        sender.sendMessage("Whitelisted IP addresses:");
        for (int ip : this.whitelist.getWhitelistedIps().toArray()) {
            byte[] addressBytes = BigInteger.valueOf(ip).toByteArray();
            try {
                InetAddress address = InetAddress.getByAddress(addressBytes);
                sender.sendMessage(" > " + address.getHostAddress());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        sender.sendMessage("Whitelisted Usernames:");
        for (String username : this.whitelist.getWhitelistedUsernames()) {
            sender.sendMessage(" > " + username);
        }
    }
}
