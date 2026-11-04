package xcord;

import net.md_5.bungee.BungeeCord;
import xcord.IPSetBlacklist;

public class DosFilterIPSet
extends IPSetBlacklist {
    public DosFilterIPSet(String string) {
        super(string, "net", "", false);
    }

    public DosFilterIPSet(String string, String string2) {
        super(string, string2, "", false);
    }

    public void enable() {
        this.executor.execute(() -> this.exec("sudo iptables -I INPUT 1 -m state --state NEW -m set --match-set " + this.tableName + " src -j DROP", 10000));
    }

    public void disable() {
        this.executor.execute(() -> this.exec("sudo iptables -D INPUT -m state --state NEW -m set --match-set " + this.tableName + " src -j DROP", 10000));
    }

    @Override
    protected void setup() {
        try {
            BungeeCord.getInstance().getLogger().info("XCord is preparing IPSet blacklisting for table: " + this.tableName + "...");
            if (!this.exec("sudo ipset version", 10000).contains("ipset")) {
                BungeeCord.getInstance().getLogger().info("IPSet is not enabled on this system. Install it manually or disable the IPSet options.");
            }
            this.exec("sudo ipset create " + this.tableName + " hash:" + this.hashType + " hashsize 4096", 10000);
            if (this.exec("sudo iptables --list INPUT -n", 10000).contains(" " + this.tableName + " ")) {
                this.disable();
            }
        }
        catch (Exception exception) {
            BungeeCord.getInstance().getLogger().info("An error occured while attempting to register IPTables/IPSet");
            exception.printStackTrace();
            this.failed = true;
        }
    }
}

