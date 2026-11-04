package xcord.blacklist;

import java.io.File;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class DosFilterBlacklist extends GlobalBlacklist {
    private boolean active = false;

    public DosFilterBlacklist() {
        this.blacklistName = "xdosfilter";
    }

    @Override
    public void tick(int currentTick) {
        if (XCord.getConfig().getAntiDdosDosIpFilterEnabled() && this.backend != null) {
            if (currentTick > this.nextUpdate) {
                this.nextUpdate = currentTick + XCord.getConfig().getAntiDdosDosIpFilterUpdateDatabaseIntervalSeconds();
                new Thread(this::loadLocal).start();
            }
            if (XCord.getAntiBotManager().isAttackActive() && !this.active && XCord.getAntiBotManager().getConnectionCount() >= XCord.getConfig().getAntiDdosDosIpFilterEnableAtThisConnectionsPerSecond()) {
                this.active = true;
                XCord.log("DoS-Filter has been activated!");
                if (this.backend instanceof DosFilterIPSet) {
                    ((DosFilterIPSet) this.backend).enable();
                }
            }
            else if (!XCord.getAntiBotManager().isAttackActive() && this.active) {
                this.active = false;
                XCord.log("DoS-Filter has been deactivated.");
                if (this.backend instanceof DosFilterIPSet) {
                    ((DosFilterIPSet) this.backend).disable();
                }
            }
        }
    }

    @Override
    protected File getLocalFile() {
        return new File("dosblacklist.txt");
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void reload() throws Exception {
        if (XCord.getConfig().getAntiDdosDosIpFilterEnabled()) {
            if (XCord.getConfig().getAntiDdosDosIpFilterUseIpset()) {
                boolean hadBackend = this.backend != null;
                this.backend = new DosFilterIPSet(this.blacklistName, "net");
                if (!hadBackend) {
                    this.nextUpdate = XCord.getTick() + 20;
                }
            }
            else {
                BungeeCord.getInstance().getLogger().warning("You're not using IPSet with the DoS filter. It's highly recommended to use the IPSet option as this will result in FAR superior protection (If you're able to..)");
                this.backend = new IpIntBlacklist();
                this.nextUpdate = XCord.getTick() + 1;
            }
            new Thread(this::loadLocal).start();
        }
        else {
            if (this.backend instanceof IPSetBlacklist) {
                ((IPSetBlacklist) this.backend).destroy();
            }
            this.backend = null;
        }
    }
}
