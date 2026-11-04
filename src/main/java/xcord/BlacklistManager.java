package xcord;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import xcord.Blacklist;
import xcord.IPSetBlacklist;
import xcord.MemoryBlacklist;
import xcord.MessageTemplate;
import xcord.ReloadableService;
import xcord.TickableService;
import xcord.XCord;

public class BlacklistManager
implements ReloadableService,
TickableService {
    private volatile int tick = 0;
    private Blacklist blacklist = null;

    @Override
    public void reload() throws Exception {
        if (this.blacklist instanceof IPSetBlacklist) {
            if (!XCord.getConfig().getSecurityManagerIpsetBlacklisting()) {
                this.blacklist.purge();
                this.blacklist = new MemoryBlacklist();
            }
        } else if (XCord.getConfig().getSecurityManagerIpsetBlacklisting()) {
            this.blacklist = new IPSetBlacklist("xcord", "ip", "", true);
        } else if (this.blacklist == null) {
            this.blacklist = new MemoryBlacklist();
        }
    }

    @Override
    public void tick(int n) {
        if (this.tick < n && this.blacklist != null && !this.blacklist.isEmpty()) {
            BungeeCord.getInstance().getLogger().info("Reset exploit/bot blacklist. (" + this.blacklist.purge() + " IPs)");
        }
    }

    public void resetTick() {
        this.tick = XCord.getTick() + XCord.getConfig().getSecurityManagerBlacklistResetTime();
    }

    public boolean isBlacklisted(String string) {
        return this.blacklist.contains(string);
    }

    public boolean isBlacklisted(InetAddress inetAddress, String string) {
        if (this.isBlacklisted(inetAddress)) {
            if (XCord.getConfig().getSecurityManagerLogBlacklist()) {
                BungeeCord.getInstance().getLogger().info("X " + inetAddress.getHostAddress() + " for " + string);
            }
            return true;
        }
        return false;
    }

    public boolean isBlacklisted(SocketAddress socketAddress, String string) {
        return this.isBlacklisted(((InetSocketAddress)socketAddress).getAddress(), string);
    }

    public boolean isBlacklisted(InetAddress inetAddress) {
        if (XCord.getConfig().isIgnoredAddress(inetAddress)) {
            return false;
        }
        this.resetTick();
        String string = inetAddress.getHostAddress();
        return this.blacklist.add(string);
    }

    public void disconnect(InetAddress inetAddress, MessageTemplate messageTemplate) {
        if (XCord.getConfig().isIgnoredAddress(inetAddress)) {
            return;
        }
        String string = inetAddress.getHostAddress();
        for (UserConnection userConnection : BungeeCord.getInstance().getUserConnections()) {
            if (userConnection.isPassedBotChecks() || !userConnection.getAddress().getAddress().getHostAddress().equals(string)) continue;
            try {
                userConnection.disconnect(messageTemplate);
            }
            catch (Exception exception) {}
        }
    }

    public void forceClose(InetAddress inetAddress) {
        if (XCord.getConfig().isIgnoredAddress(inetAddress)) {
            return;
        }
        String string = inetAddress.getHostAddress();
        for (UserConnection userConnection : BungeeCord.getInstance().getUserConnections()) {
            if (userConnection.isPassedBotChecks() || !userConnection.getAddress().getAddress().getHostAddress().equals(string)) continue;
            try {
                userConnection.forceClose();
            }
            catch (Exception exception) {}
        }
    }

    public void clear() {
        this.blacklist.purge();
    }
}

