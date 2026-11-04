package xcord.blacklist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;
import xcord.service.ReloadableService;
import xcord.service.TickableService;
import xcord.util.ProcessUtils;

public class GlobalBlacklist implements ReloadableService, TickableService {
    protected FileBlacklist backend;
    protected int nextUpdate = -1;
    protected String blacklistName = "xcordgb";
    protected long lastLocalModified = -1L;

    protected File getLocalFile() {
        return new File("blacklistv2.txt");
    }

    @Override
    public void tick(int currentTick) {
        if (XCord.getConfig().getSharedGlobalBlacklistEnabled() && currentTick > this.nextUpdate && this.backend != null) {
            this.nextUpdate = currentTick + XCord.getConfig().getSharedGlobalBlacklistUpdateDatabaseIntervalSeconds();
            new Thread(this::loadLocal).start();
        }
    }

    public boolean isBlacklisted(String address) {
        return this.backend.contains(address);
    }

    @Override
    public void reload() throws Exception {
        if (XCord.getConfig().getSharedGlobalBlacklistEnabled()) {
            if (XCord.getConfig().getSharedGlobalBlacklistUseIpset()) {
                boolean hadBackend = this.backend != null;
                this.backend = new IPSetBlacklist(this.blacklistName, "net", "-m state --state NEW", false);
                if (!hadBackend) {
                    this.nextUpdate = XCord.getTick() + 20;
                }
            }
            else {
                BungeeCord.getInstance().getLogger().warning("You're not using IPSet with the global-blacklist. It's highly recommended to use the IPSet option as this will result in FAR superior protection (If you're able to..)");
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

    protected void loadLocal() {
        if (this.backend == null) {
            return;
        }
        File file = this.getLocalFile();
        if (!file.isFile()) {
            this.createTemplate(file);
            XCord.log("[~] Created local blacklist template: %s. Add your entries.", file.getName());
            return;
        }
        long modified = file.lastModified();
        if (modified == this.lastLocalModified) {
            return;
        }
        this.lastLocalModified = modified;
        File stagingFile = null;
        int loadedCount = 0;
        int skippedCount = 0;
        try {
            stagingFile = File.createTempFile("xcord-local-", ".tmp");
            String stagingTableName = this.blacklistName + "-temp";
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(stagingFile), StandardCharsets.UTF_8));
            ) {
                writer.println("create " + stagingTableName + " hash:net family inet hashsize 65536 maxelem 555536");
                String line;
                while ((line = reader.readLine()) != null) {
                    String entry = line.trim();
                    if (entry.isEmpty() || entry.startsWith("#") || entry.startsWith("//")) {
                        continue;
                    }
                    if (entry.startsWith("add ")) {
                        String[] parts = entry.split("\\s+");
                        if (parts.length >= 3) {
                            entry = parts[parts.length - 1];
                        }
                    }
                    if (!isValidEntry(entry)) {
                        skippedCount++;
                        continue;
                    }
                    if (this.backend instanceof IpIntBlacklist && entry.contains("/")) {
                        skippedCount++;
                        continue;
                    }
                    writer.println("add " + stagingTableName + " " + entry);
                    loadedCount++;
                }
            }
            this.backend.load(stagingFile, stagingTableName);
            XCord.log("[~] Loaded local blacklist %s: %d entries (%d skipped).", file.getName(), loadedCount, skippedCount);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            XCord.log("[~] Could not read local blacklist %s.", file.getName());
        }
        finally {
            if (stagingFile != null) {
                stagingFile.delete();
            }
        }
    }

    private void createTemplate(File file) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("# XCord local blacklist");
            writer.println("# One IPv4 address or CIDR range per line.");
            writer.println("# Lines starting with # or // are ignored.");
            writer.println("# Examples:");
            writer.println("# 1.2.3.4");
            writer.println("# 10.0.0.0/8");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean isValidEntry(String entry) {
        if (ProcessUtils.isValidIp(entry)) {
            return true;
        }
        int slash = entry.indexOf('/');
        if (slash <= 0 || slash == entry.length() - 1) {
            return false;
        }
        if (!ProcessUtils.isValidIp(entry.substring(0, slash))) {
            return false;
        }
        try {
            int mask = Integer.parseInt(entry.substring(slash + 1));
            return mask >= 0 && mask <= 32;
        }
        catch (NumberFormatException exception) {
            return false;
        }
    }
}
