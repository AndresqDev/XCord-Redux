package xcord.antibot;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import xcord.XCord;
import xcord.config.XCordConfig;
import xcord.service.ReloadableService;
import xcord.service.TickableService;

public class AntiBotManager implements ReloadableService, TickableService {
    private final Map<String, ConnectionStats> connectionStats = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> whitelistRecords = new ConcurrentHashMap<>();
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
    public void tick(int tick) {
        long now = System.currentTimeMillis();
        XCordConfig config = XCord.getConfig();
        if (++this.purgeCounter % config.getAntiBotWhitelistPurgeInterval() == 0) {
            BungeeCord.getInstance().getLogger().info("Purging old anti-bot whitelist records (Total: " + this.whitelistRecords.size() + ")");
            long cutoff = now - (long)config.getAntiBotWhitelistSurviveTime();
            int purged = 0;
            Iterator<Map.Entry<String, Long>> iterator = this.whitelistRecords.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue() >= cutoff) {
                    continue;
                }
                iterator.remove();
                purged++;
            }
            BungeeCord.getInstance().getLogger().info("Purged " + purged + "  old anti-bot whitelist records!");
        }
        int connections = this.connectionCounter.getAndSet(0);
        if (this.attackActive.get()) {
            XCord.getBlacklistManager().resetTick();
            this.lastConnectionCount = connections;
            if (connections < config.getAntiBotAntiBotActivateConnections()) {
                if (--this.tickCounter <= 0) {
                    this.attackActive.set(false);
                    XCord.setLogFilterEnabled(false);
                }
            } else {
                this.tickCounter = config.getAntiBotAntiBotDeactivateDelay();
            }
            if (XCord.getConfig().getAntiBotPrintConnectionStats()) {
                System.out.println("Anti-bot Stats> Current connections per second=" + connections + " Tracking IPs=" + this.connectionStats.size());
            }
        }
        this.connectionStats.entrySet().removeIf(entry -> now - entry.getValue().getFirstSeen() > (long)config.getAntiBotIpTrackingTimeout());
        for (UserConnection connection : BungeeCord.getInstance().getUserConnections()) {
            if (!connection.isPassedBotChecks()) {
                long loginTime = now - connection.getInitializeTime();
                if (connection.getSentPackets() > 40 && connection.getSettings() != null && loginTime > (long)config.getAntiBotDisableChecksTime()) {
                    connection.setPassedBotChecks(true);
                    continue;
                }
                if (loginTime > (long)config.getAntiBotLoginCheckStrangeConnectionDelay() && connection.getSentPackets() < 10 && connection.isConnected() && this.checkViolation(CheckType.PACKET_TIMEOUT, connection.getCh(), 1, 5, loginTime + " -> Packets: " + connection.getSentPackets())) {
                    try {
                        connection.disconnect(XCord.getMessages().getSlowLogin());
                    } catch (Exception exception) {
                    }
                }
                if (loginTime <= (long)config.getAntiBotLoginCheckTimeoutTime() || connection.getSentPackets() > 0 || !connection.isConnected() || !this.checkViolation(CheckType.LOGIN_TIMEOUT, connection.getCh(), 1, 5, String.valueOf(loginTime))) {
                    continue;
                }
                try {
                    connection.disconnect(XCord.getMessages().getSlowLogin());
                } catch (Exception exception) {
                }
                continue;
            }
            if (connection.isWhitelistedFromAntibot() || connection.getSentPackets() <= 1000 || now - connection.getInitializeTime() <= (long)config.getAntiBotTimeToWhitelist()) {
                continue;
            }
            connection.setWhitelistedFromAntibot(true);
            this.whitelistRecords.put(connection.getName() + connection.getAddress().getAddress().getHostAddress(), now);
        }
    }

    public void whitelist(String name, String address) {
        this.whitelistRecords.put(name + address, System.currentTimeMillis());
    }

    public SimilarityCache getSimilarNameCache() {
        return this.similarNameCache;
    }

    public boolean checkViolation(CheckType type, ChannelWrapper channel, int violationsToAdd, int maxViolations, String details) {
        String address = ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
        ConnectionStats stats = this.getStats(address);
        int violations = stats.addViolations(type, violationsToAdd);
        if (violations >= maxViolations) {
            XCord.getBlacklistManager().isBlacklisted(channel.getRemoteAddress(), "failed " + type + " check> " + details);
            channel.close();
            return false;
        }
        BungeeCord.getInstance().getLogger().info(address + " failed " + type + " check> " + details);
        return true;
    }

    public boolean isWhitelisted(String name) {
        return this.whitelistRecords.get(name) != null || XCord.getConfig().isIgnoredAddress(name);
    }

    public boolean isWhitelisted(String name, String address) {
        String key = name + address;
        return this.whitelistRecords.get(key) != null || XCord.getConfig().isIgnoredAddress(address);
    }

    public ConnectionStats getStats(String address) {
        return this.connectionStats.computeIfAbsent(address, ConnectionStats::new);
    }

    public int incrementConnections() {
        int connections = this.connectionCounter.incrementAndGet();
        if (connections > XCord.getConfig().getAntiBotAntiBotActivateConnections() && !this.attackActive.getAndSet(true)) {
            BungeeCord.getInstance().getLogger().warning("Detected a potential bot attack, enabled extra defensive modules!");
            XCord.setLogFilterEnabled(true);
            this.tickCounter = XCord.getConfig().getAntiBotAntiBotDeactivateDelay();
        }
        return connections;
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
