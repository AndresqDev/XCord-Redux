package xcord.blacklist;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import xcord.XCord;
import xcord.message.MessageTemplate;
import xcord.service.ReloadableService;
import xcord.service.TickableService;

public class BlacklistManager implements ReloadableService, TickableService {
    private volatile int tick = 0;
    private Blacklist blacklist = null;

    @Override
    public void reload() throws Exception {
        if (this.blacklist instanceof IPSetBlacklist) {
            if (!XCord.getConfig().getSecurityManagerIpsetBlacklisting()) {
                this.blacklist.purge();
                this.blacklist = new MemoryBlacklist();
            }
        }
        else if (XCord.getConfig().getSecurityManagerIpsetBlacklisting()) {
            this.blacklist = new IPSetBlacklist("xcord", "ip", "", true);
        }
        else if (this.blacklist == null) {
            this.blacklist = new MemoryBlacklist();
        }
    }

    @Override
    public void tick(int currentTick) {
        if (this.tick < currentTick && this.blacklist != null && !this.blacklist.isEmpty()) {
            BungeeCord.getInstance().getLogger().info("Reset exploit/bot blacklist. (" + this.blacklist.purge() + " IPs)");
        }
    }

    public void resetTick() {
        this.tick = XCord.getTick() + XCord.getConfig().getSecurityManagerBlacklistResetTime();
    }

    public boolean isBlacklisted(String address) {
        return this.blacklist.contains(address);
    }

    public boolean isBlacklisted(InetAddress address, String reason) {
        if (this.isBlacklisted(address)) {
            if (XCord.getConfig().getSecurityManagerLogBlacklist()) {
                BungeeCord.getInstance().getLogger().info("X " + address.getHostAddress() + " for " + reason);
            }
            return true;
        }
        return false;
    }

    public boolean isBlacklisted(SocketAddress address, String reason) {
        return this.isBlacklisted(((InetSocketAddress) address).getAddress(), reason);
    }

    public boolean isBlacklisted(InetAddress address) {
        if (XCord.getConfig().isIgnoredAddress(address)) {
            return false;
        }
        this.resetTick();
        String hostAddress = address.getHostAddress();
        return this.blacklist.add(hostAddress);
    }

    public void disconnect(InetAddress address, MessageTemplate messageTemplate) {
        if (XCord.getConfig().isIgnoredAddress(address)) {
            return;
        }
        String hostAddress = address.getHostAddress();
        for (UserConnection connection : BungeeCord.getInstance().getUserConnections()) {
            if (connection.isPassedBotChecks() || !connection.getAddress().getAddress().getHostAddress().equals(hostAddress)) {
                continue;
            }
            try {
                connection.disconnect(messageTemplate);
            }
            catch (Exception exception) {
            }
        }
    }

    public void forceClose(InetAddress address) {
        if (XCord.getConfig().isIgnoredAddress(address)) {
            return;
        }
        String hostAddress = address.getHostAddress();
        for (UserConnection connection : BungeeCord.getInstance().getUserConnections()) {
            if (connection.isPassedBotChecks() || !connection.getAddress().getAddress().getHostAddress().equals(hostAddress)) {
                continue;
            }
            try {
                connection.forceClose();
            }
            catch (Exception exception) {
            }
        }
    }

    public void clear() {
        this.blacklist.purge();
    }
}
