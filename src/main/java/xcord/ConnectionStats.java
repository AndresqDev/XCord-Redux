package xcord;

import java.net.InetAddress;
import xcord.CheckType;
import xcord.XCord;

public class ConnectionStats {
    private final String ip;
    private long firstSeen = System.currentTimeMillis();
    private long lastSeen = System.currentTimeMillis();
    private int loginCounter = 0;
    private int connectionCounter = 0;
    private int actionValue = -1;
    private boolean whitelisted = false;
    private long lastAction = -1L;
    private boolean flagged = false;
    private final int[] violations = new int[CheckType.values().length];

    public int getViolations(CheckType checkType) {
        return this.violations[checkType.ordinal()];
    }

    public int addViolations(CheckType checkType, int n) {
        int n2 = checkType.ordinal();
        int n3 = this.violations[n2] + n;
        this.violations[n2] = n3;
        return n3;
    }

    public long getLastAction() {
        return this.lastAction;
    }

    public void setWhitelisted(boolean bl) {
        this.whitelisted = bl;
    }

    public int getLastActionInt() {
        return this.actionValue;
    }

    public void setLastAction(int n) {
        this.actionValue = n;
    }

    public ConnectionStats(String string) {
        this.ip = string;
    }

    public long getFirstSeen() {
        return this.firstSeen;
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public boolean checkSpeedyLogin(InetAddress inetAddress) {
        long l = System.currentTimeMillis();
        long l2 = l - this.lastSeen;
        if (l2 < (long)XCord.getConfig().getAntiBotTimeGroupingTick()) {
            if (++this.loginCounter >= XCord.getConfig().getAntiBotSpeedyLoginMaxLoginsPerTick()) {
                if (XCord.getAntiBotManager().isAttackActive() && this.addViolations(CheckType.SPEEDY_LOGIN, 1) >= (this.whitelisted ? XCord.getConfig().getAntiBotSpeedyLoginBlacklistViolations() * 2 : XCord.getConfig().getAntiBotSpeedyLoginBlacklistViolations())) {
                    XCord.getBlacklistManager().isBlacklisted(inetAddress, "Suspected-Bot: Speedy-Login(" + this.getViolations(CheckType.SPEEDY_LOGIN) + ")");
                }
                return true;
            }
        } else {
            this.loginCounter = 1;
            this.lastSeen = l;
        }
        return false;
    }

    public boolean checkSpeedyConnection(InetAddress inetAddress) {
        long l = System.currentTimeMillis();
        long l2 = l - this.firstSeen;
        if (l2 < (long)XCord.getConfig().getAntiBotTimeGroupingTick()) {
            double d = ++this.connectionCounter;
            double d2 = !this.flagged ? (double)XCord.getConfig().getAntiBotSpeedyConnectionMaxConnectionsPerTick() : Math.ceil((double)XCord.getConfig().getAntiBotSpeedyConnectionMaxConnectionsPerTick() * 0.5);
            if (d >= d2) {
                if (XCord.getAntiBotManager().isAttackActive()) {
                    double d3 = this.addViolations(CheckType.SPEEDY_CONNECTION, 1);
                    double d4 = this.whitelisted ? (double)(XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations() * 2) : (!this.flagged ? (double)XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations() : Math.ceil((double)XCord.getConfig().getAntiBotSpeedyConnectionBlacklistViolations() * 0.5));
                    if (d3 >= d4) {
                        XCord.getBlacklistManager().isBlacklisted(inetAddress, "Suspected-Bot: Speedy-Connection(" + this.getViolations(CheckType.SPEEDY_CONNECTION) + ")");
                    }
                }
                return true;
            }
        } else {
            this.connectionCounter = 1;
            this.firstSeen = l;
        }
        return false;
    }

    public void setFlagged(boolean bl) {
        this.flagged = bl;
    }
}

