package xcord;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Stream;
import net.md_5.bungee.BungeeCord;

public class CountryBlocklist extends IPSetBlacklist {
   public CountryBlocklist(String var1) {
      super(var1, "net", "", false);
   }

   public void blockCountries(String... var1) {
      if (!this.failed) {
         this.executor
            .execute(
               () -> {
                  BungeeCord.getInstance().getLogger().info("XCord is downloading your country block zone files... This shouldn't take long");
                  File var2 = new File("tmp-" + System.currentTimeMillis());
                  ArrayList<String> var3 = new ArrayList<String>();

                  for (String var7 : var1) {
                     try {
                        HttpURLConnection var8 = (HttpURLConnection)new URL(
                              "http://" + DnsResolver.resolve("ipdeny.com").getHostAddress() + "/ipblocks/data/countries/" + var7 + ".zone"
                           )
                           .openConnection();
                        var8.setConnectTimeout(15000);
                        var8.setReadTimeout(15000);
                        if (var8.getResponseCode() != 200) {
                           BungeeCord.getInstance()
                              .getLogger()
                              .warning("XCord could not find a zone file for the ISO code '" + var7 + "'. Maybe you misspelled it, or it is not supported?");
                        } else {
                           InputStream var9 = var8.getInputStream();

                           try {
                              BufferedReader var10 = new BufferedReader(new InputStreamReader(var9));

                              try {
                                 Stream<String> var11 = var10.lines();

                                 try {
                                    var11.forEach(var3::add);
                                 } catch (Throwable var18) {
                                    if (var11 != null) {
                                       try {
                                          var11.close();
                                       } catch (Throwable var16) {
                                          var18.addSuppressed(var16);
                                       }
                                    }

                                    throw var18;
                                 }

                                 if (var11 != null) {
                                    var11.close();
                                 }
                              } catch (Throwable var19) {
                                 try {
                                    var10.close();
                                 } catch (Throwable var15) {
                                    var19.addSuppressed(var15);
                                 }

                                 throw var19;
                              }

                              var10.close();
                           } catch (Throwable var20) {
                              if (var9 != null) {
                                 try {
                                    var9.close();
                                 } catch (Throwable var14) {
                                    var20.addSuppressed(var14);
                                 }
                              }

                              throw var20;
                           }

                           if (var9 != null) {
                              var9.close();
                           }
                        }
                     } catch (Exception var21) {
                        var21.printStackTrace();
                     }
                  }

                  if (var3.isEmpty()) {
                     BungeeCord.getInstance().getLogger().info("XCord found no zones for the given ISO codes.");
                     super.destroy();
                  } else {
                     try {
                        String var22 = this.tableName + "-tmp";
                        PrintWriter var23 = new PrintWriter(var2.getName(), "UTF-8");
                        var23.println("create " + var22 + " hash:net family inet hashsize 65536 maxelem 555536");

                        for (String var25 : var3) {
                           var23.println("add " + var22 + " " + var25);
                        }

                        var23.close();
                        super.load(var2, var22);
                        BungeeCord.getInstance().getLogger().info("XCord blocked " + var3.size() + " IP ranges from your specified ISO codes!");
                        BungeeCord.getInstance()
                           .getLogger()
                           .info(
                              "For maximum performance, it is advised to move the "
                                 + this.tableName
                                 + " IPTables rule below your RELATED,ESTABLISHED rule (Assuming your IPTables is setup correctly..)"
                           );
                        Thread.sleep(15000L);
                        var2.delete();
                     } catch (Exception var17) {
                        BungeeCord.getInstance().getLogger().info("XCord FAILED to block your ISO codes. Please check the exception below");
                        var17.printStackTrace();
                     }
                  }
               }
            );
      }
   }
}
