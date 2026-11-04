package xcord;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import xcord.CheckType;
import xcord.ConnectionStats;
import xcord.ReloadableService;
import xcord.SimilarityCache;
import xcord.TickableService;
import xcord.XCord;
import xcord.XCordConfig;

public class AntiBotManager
implements ReloadableService,
TickableService {
    private final Map<String, ConnectionStats> connectionStats = new ConcurrentHashMap<String, ConnectionStats>();
    private final ConcurrentHashMap<String, Long> whitelistRecords = new ConcurrentHashMap();
    private final AtomicInteger connectionCounter = new AtomicInteger();
    private final AtomicBoolean attackActive = new AtomicBoolean(false);
    private int tickCounter = 0;
    private int purgeCounter = 0;
    private volatile int lastConnectionCount = 0;
    private SimilarityCache similarNameCache;

    @Override
    public void reload() throws Exception {
        this.similarNameCache = new SimilarityCache(XCord.getConfig().getAntiBotNamePatternSameLengthOnly(), XCord.getConfig().getAntiBotNamePatternNameHistoryLength());
    }

    @Override
    public void tick(int n) {
        long l = System.currentTimeMillis();
        XCordConfig xCordConfig = XCord.getConfig();
        if (++this.purgeCounter % xCordConfig.getAntiBotWhitelistPurgeInterval() == 0) {
            BungeeCord.getInstance().getLogger().info("Purging old anti-bot whitelist records (Total: " + this.whitelistRecords.size() + ")");
            Iterator<Map.Entry<String, Long>> iterator = this.whitelistRecords.entrySet().iterator();
            long l2 = l - (long)xCordConfig.getAntiBotWhitelistSurviveTime();
            int n2 = 0;
            while (iterator.hasNext()) {
                if (iterator.next().getValue() >= l2) continue;
                iterator.remove();
                ++n2;
            }
            BungeeCord.getInstance().getLogger().info("Purged " + n2 + "  old anti-bot whitelist records!");
        }
        int n3 = this.connectionCounter.getAndSet(0);
        if (this.attackActive.get()) {
            XCord.getBlacklistManager().resetTick();
            this.lastConnectionCount = n3;
            if (n3 < xCordConfig.getAntiBotAntiBotActivateConnections()) {
                if (--this.tickCounter <= 0) {
                    this.attackActive.set(false);
                    XCord.setLogFilterEnabled(false);
                }
            } else {
                this.tickCounter = xCordConfig.getAntiBotAntiBotDeactivateDelay();
            }
            if (XCord.getConfig().getAntiBotPrintConnectionStats()) {
                System.out.println("Anti-bot Stats> Current connections per second=" + n3 + " Tracking IPs=" + this.connectionStats.size());
            }
        }
        this.connectionStats.entrySet().removeIf(entry -> l - ((ConnectionStats)entry.getValue()).getFirstSeen() > (long)xCordConfig.getAntiBotIpTrackingTimeout());
        Collection<UserConnection> collection = BungeeCord.getInstance().getUserConnections();
        for (UserConnection userConnection : collection) {
            if (!userConnection.isPassedBotChecks()) {
                long l3 = l - userConnection.getInitializeTime();
                if (userConnection.getSentPackets() > 40 && userConnection.getSettings() != null && l3 > (long)xCordConfig.getAntiBotDisableChecksTime()) {
                    userConnection.setPassedBotChecks(true);
                    continue;
                }
                if (l3 > (long)xCordConfig.getAntiBotLoginCheckStrangeConnectionDelay() && userConnection.getSentPackets() < 10 && userConnection.isConnected() && this.checkViolation(CheckType.PACKET_TIMEOUT, userConnection.getCh(), 1, 5, l3 + " -> Packets: " + userConnection.getSentPackets())) {
                    try {
                        userConnection.disconnect(XCord.getMessages().getSlowLogin());
                    }
                    catch (Exception exception) {
                    }
                }
                if (l3 <= (long)xCordConfig.getAntiBotLoginCheckTimeoutTime() || userConnection.getSentPackets() > 0 || !userConnection.isConnected() || !this.checkViolation(CheckType.LOGIN_TIMEOUT, userConnection.getCh(), 1, 5, String.valueOf(l3))) continue;
                try {
                    userConnection.disconnect(XCord.getMessages().getSlowLogin());
                }
                catch (Exception exception) {}
                continue;
            }
            if (userConnection.isWhitelistedFromAntibot() || userConnection.getSentPackets() <= 1000 || l - userConnection.getInitializeTime() <= (long)xCordConfig.getAntiBotTimeToWhitelist()) continue;
            userConnection.setWhitelistedFromAntibot(true);
            this.whitelistRecords.put(userConnection.getName() + userConnection.getAddress().getAddress().getHostAddress(), l);
        }
    }

    public void whitelist(String string, String string2) {
        this.whitelistRecords.put(string + string2, System.currentTimeMillis());
    }

    public SimilarityCache getSimilarNameCache() {
        return this.similarNameCache;
    }

    public boolean checkViolation(CheckType checkType, ChannelWrapper channelWrapper, int n, int n2, String string) {
        String string2 = ((InetSocketAddress)channelWrapper.getRemoteAddress()).getAddress().getHostAddress();
        ConnectionStats connectionStats = this.getStats(string2);
        int n3 = connectionStats.addViolations(checkType, n);
        if (n3 >= n2) {
            XCord.getBlacklistManager().isBlacklisted(channelWrapper.getRemoteAddress(), "failed " + (Object)((Object)checkType) + " check> " + string);
            channelWrapper.close();
            return false;
        }
        BungeeCord.getInstance().getLogger().info(string2 + " failed " + (Object)((Object)checkType) + " check> " + string);
        return true;
    }

    public boolean isWhitelisted(String string) {
        return this.whitelistRecords.get(string) != null || XCord.getConfig().isIgnoredAddress(string);
    }

    public boolean isWhitelisted(String string, String string2) {
        String string3 = string + string2;
        return this.whitelistRecords.get(string3) != null || XCord.getConfig().isIgnoredAddress(string2);
    }

    public ConnectionStats getStats(String string) {
        return this.connectionStats.computeIfAbsent(string, ConnectionStats::new);
    }

    public int incrementConnections() {
        int n = this.connectionCounter.incrementAndGet();
        if (n > XCord.getConfig().getAntiBotAntiBotActivateConnections() && !this.attackActive.getAndSet(true)) {
            BungeeCord.getInstance().getLogger().warning("Detected a potential bot attack, enabled extra defensive modules!");
            XCord.setLogFilterEnabled(true);
            this.tickCounter = XCord.getConfig().getAntiBotAntiBotDeactivateDelay();
        }
        return n;
    }

    public boolean isAttackActive() {
        return this.attackActive.get();
    }

    public int getConnectionCount() {
        return this.connectionCounter.get();
    }

    public int getLastConnectionCount() {
        return this.lastConnectionCount;
    }
}

