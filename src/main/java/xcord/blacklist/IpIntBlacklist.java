package xcord.blacklist;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import xcord.XCord;
import xcord.util.ProcessUtils;

public class IpIntBlacklist implements Blacklist, FileBlacklist {
    private TIntSet addresses = new TIntHashSet(120000);

    @Override
    public boolean add(String address) {
        throw new UnsupportedOperationException("This blacklist type cannot support dynamic writing!");
    }

    @Override
    public boolean contains(String address) {
        return this.addresses.contains(ProcessUtils.ipToInt(address));
    }

    @Override
    public void load(File file, String tableName) {
        String prefix = tableName.trim() + " ";
        TIntHashSet loadedAddresses = new TIntHashSet(120000);
        int failedCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int prefixIndex = line.indexOf(prefix);
                if (prefixIndex > 1) {
                    String entry = line.substring(prefixIndex + prefix.length()).trim();
                    if (entry.length() < 20) {
                        try {
                            loadedAddresses.add(ProcessUtils.ipToInt(entry));
                        }
                        catch (Exception exception) {
                            failedCount++;
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        this.addresses = loadedAddresses;
        if (XCord.getConfig().getDebugDebugMode()) {
            System.out.println("Loaded " + loadedAddresses.size() + " compatible IPV4 addresses to the global blacklist (" + failedCount + " IP ranges could not be loaded)");
        }
    }

    @Override
    public int purge() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return this.addresses.isEmpty();
    }
}
