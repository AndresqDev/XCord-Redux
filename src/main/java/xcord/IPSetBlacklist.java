package xcord;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.md_5.bungee.BungeeCord;
import xcord.FileBlacklist;
import xcord.IPSetWriter;
import xcord.MemoryBlacklist;
import xcord.ProcessUtils;

public class IPSetBlacklist
extends MemoryBlacklist
implements FileBlacklist {
    protected final ExecutorService executor = Executors.newFixedThreadPool(1);
    protected final String tableName;
    protected final String hashType;
    protected boolean failed = false;
    private final String iptablesMatch;
    private final IPSetWriter writer;

    public IPSetBlacklist(String string) {
        this(string, "ip", "", false);
    }

    public IPSetBlacklist(String string, String string2) {
        this(string, "ip", string2, false);
    }

    public IPSetBlacklist(String string, String string2, String string3, boolean bl) {
        this.tableName = string;
        this.hashType = string2;
        this.iptablesMatch = string3.length() > 0 ? string3.trim() + " " : string3;
        this.setup();
        this.writer = bl ? new IPSetWriter(200, string, "create " + string + " hash:" + string2 + " hashsize 4096") : null;
    }

    @Override
    public boolean add(String string) {
        if (super.add(string)) {
            this.writer.a(string);
            return true;
        }
        return false;
    }

    @Override
    public int purge() {
        this.exec("sudo ipset flush " + this.tableName);
        return super.purge();
    }

    public void destroy() {
        this.exec("sudo iptables -D INPUT " + this.iptablesMatch + "-m set --match-set " + this.tableName + " src -j DROP", 10000);
        this.exec("sudo iptables -D INPUT -m set --match-set " + this.tableName + " src -j DROP", 10000);
        this.exec("sudo ipset destroy " + this.tableName);
    }

    public void restore(File file) {
        this.exec("sudo ipset flush " + this.tableName);
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "ipset", "restore", "-!").redirectErrorStream(true);
        processBuilder.redirectInput(file);
        this.exec(processBuilder, 30000);
    }

    @Override
    public void load(File file, String string) {
        this.exec("sudo ipset destroy " + string);
        this.restore(file);
        if (this.exec("sudo ipset flush " + this.tableName).contains("not exist")) {
            this.exec("sudo ipset rename " + string + " " + this.tableName);
        } else {
            this.exec("sudo ipset swap " + this.tableName + " " + string);
            this.exec("sudo ipset destroy " + string);
        }
    }

    protected void setup() {
        try {
            BungeeCord.getInstance().getLogger().info("XCord is preparing IPSet blacklisting for table: " + this.tableName + "...");
            if (!this.exec("sudo ipset version", 10000).contains("ipset")) {
                BungeeCord.getInstance().getLogger().info("IPSet is not enabled on this system or XCord does not have sufficient permission. Either install IPSet or disable all IPSet options.");
            }
            this.exec("sudo ipset create " + this.tableName + " hash:" + this.hashType + " hashsize 4096", 10000);
            if (!this.exec("sudo iptables --list INPUT -n", 10000).contains(" " + this.tableName + " ")) {
                this.exec("sudo iptables -I INPUT 1 " + this.iptablesMatch + "-m set --match-set " + this.tableName + " src -j DROP", 10000);
                BungeeCord.getInstance().getLogger().info("IPSet iptables rules have been created.");
            } else {
                BungeeCord.getInstance().getLogger().info("IPSet iptables check already set.");
            }
            if (!this.exec("sudo iptables --list INPUT -n", 10000).contains(" " + this.tableName + " ")) {
                BungeeCord.getInstance().getLogger().info("XCord FAILED to register IPTables with IPSet. This feature REQUIRES you to run the server on ROOT. IPSet filtering may not be enabled.. Make sure you have IPSet installed on your system!");
                this.failed = true;
            } else {
                BungeeCord.getInstance().getLogger().info("IPSet blacklisting is now enabled (Insure the server is running on ROOT!)");
            }
        }
        catch (Exception exception) {
            BungeeCord.getInstance().getLogger().info("An error occured while attempting to register IPTables/IPSet");
            exception.printStackTrace();
            this.failed = true;
        }
    }

    private String exec(String string) {
        return this.exec(string, -1);
    }

    protected String exec(String string, int n) {
        return this.exec(string.split(" "), n);
    }

    private String exec(String[] stringArray, int n) {
        return this.exec(new ProcessBuilder(stringArray).redirectErrorStream(true), n);
    }

    private String exec(ProcessBuilder processBuilder, int n) {
        return ProcessUtils.execute(processBuilder, n);
    }
}

