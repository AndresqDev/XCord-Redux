package xcord.country;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.BungeeCord;
import xcord.blacklist.IPSetBlacklist;
import xcord.network.DnsResolver;

public class CountryBlocklist extends IPSetBlacklist {
    public CountryBlocklist(String tableName) {
        super(tableName, "net", "", false);
    }

    public void blockCountries(String... isoCodes) {
        if (!this.failed) {
            this.executor.execute(() -> {
                BungeeCord.getInstance().getLogger().info("XCord is downloading your country block zone files... This shouldn't take long");
                File tempFile = new File("tmp-" + System.currentTimeMillis());
                List<String> ranges = new ArrayList<String>();

                for (String isoCode : isoCodes) {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL("http://" + DnsResolver.resolve("ipdeny.com").getHostAddress() + "/ipblocks/data/countries/" + isoCode + ".zone").openConnection();
                        connection.setConnectTimeout(15000);
                        connection.setReadTimeout(15000);
                        if (connection.getResponseCode() != 200) {
                            BungeeCord.getInstance().getLogger().warning("XCord could not find a zone file for the ISO code '" + isoCode + "'. Maybe you misspelled it, or it is not supported?");
                        }
                        else {
                            try (InputStream input = connection.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    ranges.add(line);
                                }
                            }
                        }
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                if (ranges.isEmpty()) {
                    BungeeCord.getInstance().getLogger().info("XCord found no zones for the given ISO codes.");
                    super.destroy();
                }
                else {
                    try {
                        String stagingTableName = this.tableName + "-tmp";
                        try (PrintWriter writer = new PrintWriter(tempFile.getName(), "UTF-8")) {
                            writer.println("create " + stagingTableName + " hash:net family inet hashsize 65536 maxelem 555536");
                            for (String range : ranges) {
                                writer.println("add " + stagingTableName + " " + range);
                            }
                        }
                        super.load(tempFile, stagingTableName);
                        BungeeCord.getInstance().getLogger().info("XCord blocked " + ranges.size() + " IP ranges from your specified ISO codes!");
                        BungeeCord.getInstance().getLogger().info("For maximum performance, it is advised to move the " + this.tableName + " IPTables rule below your RELATED,ESTABLISHED rule (Assuming your IPTables is setup correctly..)");
                        Thread.sleep(15000L);
                        tempFile.delete();
                    }
                    catch (Exception exception) {
                        BungeeCord.getInstance().getLogger().info("XCord FAILED to block your ISO codes. Please check the exception below");
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}
