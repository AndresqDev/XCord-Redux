package me.minebuilders.proxyfork.antiproxy;

import com.google.common.collect.Sets;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import xcord.util.ProcessUtils;

public class AntiProxyWhitelistManager implements Serializable {
    private static final long serialVersionUID = 5518619159099964693L;
    private final Set<String> whitelistedUsernames = Sets.newConcurrentHashSet();
    private TIntSet whitelistedIps = new TIntHashSet();
    private File backendFile;

    public AntiProxyWhitelistManager(File backendFile) {
        this.backendFile = backendFile;
    }

    public AntiProxyWhitelistManager() {
    }

    public void setBackendFile(File backendFile) {
        this.backendFile = backendFile;
    }

    private void saveToFile() {
        new Thread(() -> {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(this.backendFile))) {
                output.writeObject(this);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public void whitelistEntry(String entry) {
        if (ProcessUtils.isValidIp(entry)) {
            int ip = ProcessUtils.ipToInt(entry);
            TIntHashSet updatedIps = new TIntHashSet(this.whitelistedIps);
            updatedIps.add(ip);
            this.whitelistedIps = updatedIps;
        } else {
            this.whitelistedUsernames.add(entry);
        }
        this.saveToFile();
    }

    public boolean removeWhitelistedEntry(String entry) {
        boolean removed;
        try {
            if (!ProcessUtils.isValidIp(entry)) {
                return this.whitelistedUsernames.remove(entry);
            }
            int ip = ProcessUtils.ipToInt(entry);
            TIntHashSet updatedIps = new TIntHashSet(this.whitelistedIps);
            removed = updatedIps.remove(ip);
            this.whitelistedIps = updatedIps;
        } finally {
            this.saveToFile();
        }
        return removed;
    }

    public boolean isEntryWhitelisted(String entry) {
        return ProcessUtils.isValidIp(entry) ? this.whitelistedIps.contains(ProcessUtils.ipToInt(entry)) : this.whitelistedUsernames.contains(entry);
    }

    public boolean isIPWhitelisted(int ip) {
        return this.whitelistedIps.contains(ip);
    }

    public boolean isUsernameWhitelisted(String username) {
        return this.whitelistedUsernames.contains(username);
    }

    public Set<String> getWhitelistedUsernames() {
        return this.whitelistedUsernames;
    }

    public TIntSet getWhitelistedIps() {
        return this.whitelistedIps;
    }
}
