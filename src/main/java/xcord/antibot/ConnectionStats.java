package xcord.antibot;

import java.net.InetAddress;
import xcord.XCord;

public class ConnectionStats {
    private long firstSeen = System.currentTimeMillis();
    private long lastSeen = System.currentTimeMillis();
    private int loginCounter = 0;
    private int connectionCounter = 0;
    private int actionValue = -1;
    private boolean whitelisted = false;
    private long lastAction = -1L;
    private boolean flagged = false;
    private final int[] violations = new int[CheckType.values().length];

    public int getViolations(CheckType type) {
        return this.violations[type.ordinal()];
    }

    public int addViolations(CheckType type, int amount) {
        int index = type.ordinal();
        int total = this.violations[index] + amount;
        this.violations[index] = total;
        return total;
    }

    public long getLastAction() {
        return this.lastAction;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public int getLastActionInt() {
        return this.actionValue;
    }

    public void setLastAction(int action) {
        this.actionValue = action;
    }

    public ConnectionStats(String address) {
    }

    public long getFirstSeen() {
        return this.firstSeen;
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public boolean checkSpeedyLogin(InetAddress address) {
        long now = System.currentTimeMillis();
        long elapsed = now - this.lastSeen;
        if (elapsed < (long)XCord.getConfig().getAntiBotTimeGroupingTick()) {
            if (++this.loginCounter >= XCord.getConfig().getAntiBotSpeedyLoginMaxLoginsPerTick()) {
                if (XCord.getAntiBotManager().isAttackActive()
                    && this.addViolations(CheckType.SPEEDY_LOGIN, 1) >= (this.whitelisted
                        ? XCord.getConfig().getAntiBotSpeedyLoginBlacklistViolations() * 2
                        : XCord.getConfig().getAntiBotSpeedyLoginBlacklistViolations())) {
                    XCord.getBlacklistManager().isBlacklisted(address, "Suspected-Bot: Speedy-Login(" + this.getViolations(CheckType.SPEEDY_LOGIN) + ")");
                }
                return true;
            }
        } else {
            this.loginCounter = 1;
            this.lastSeen = now;
        }
        return false;
    }

    public boolean checkSpeedyConnection(InetAddress address) {
        long now = System.currentTimeMillis();
        long elapsed = now - this.firstSeen;
        if (elapsed < (long)XCord.getConfig().getAntiBotTimeGroupingTick()) {
            double connections = ++this.connectionCounter;
            double maxConnections = !this.flagged
                ? (double)XCord.getConfig().getAntiBotSpeedyConnectionMaxConnectionsPerTick()
                : Math.ceil((double)XCord.getConfig().getAntiBotSpeedyConnectionMaxConnectionsPerTick() * 0.5);
            if (connections >= maxConnections) {
                if (XCord.getAntiBotManager().isAttackActive()) {
                    double violations = this.addViolations(CheckType.SPEEDY_CONNECTION, 1);
                    double maxViolations = this.whitelisted
                        ? (double)(XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations() * 2)
                        : (!this.flagged
                            ? (double)XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations()
                            : Math.ceil((double)XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations() * 0.5));
                    if (violations >= maxViolations) {
                        XCord.getBlacklistManager().isBlacklisted(address, "Suspected-Bot: Speedy-Connection(" + this.getViolations(CheckType.SPEEDY_CONNECTION) + ")");
                    }
                }
                return true;
            }
        } else {
            this.connectionCounter = 1;
            this.firstSeen = now;
        }
        return false;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
