package net.md_5.bungee;

import io.github.waterfallmc.waterfall.console.WaterfallConsole;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.md_5.bungee.api.ProxyServer;

public class BungeeCordLauncher {
   public static void main(String[] var0) throws Exception {
      Security.setProperty("networkaddress.cache.ttl", "30");
      Security.setProperty("networkaddress.cache.negative.ttl", "10");
      if (System.getProperty("io.netty.allocator.maxOrder") == null) {
         System.setProperty("io.netty.allocator.maxOrder", "8");
      }

      if (System.getProperty("jdk.util.jar.enableMultiRelease") == null) {
         System.setProperty("jdk.util.jar.enableMultiRelease", "force");
      }

      OptionParser var1 = new OptionParser();
      var1.allowsUnrecognizedOptions();
      var1.acceptsAll(Arrays.asList("help"), "Show the help");
      var1.acceptsAll(Arrays.asList("v", "version"), "Print version and exit");
      var1.acceptsAll(Arrays.asList("noconsole"), "Disable console input");
      OptionSet var2 = var1.parse(var0);
      if (var2.has("help")) {
         var1.printHelpOn(System.out);
      } else if (var2.has("version")) {
         System.out.println(BungeeCord.class.getPackage().getImplementationVersion());
      } else {
         if (BungeeCord.class.getPackage().getSpecificationVersion() != null && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
            Date var3 = new SimpleDateFormat("yyyyMMdd").parse(BungeeCord.class.getPackage().getSpecificationVersion());
            Calendar var4 = Calendar.getInstance();
            var4.add(3, -8);
            if (var3.before(var4.getTime())) {
               System.err.println("*** Hey! This build is potentially outdated :( ***");
               System.err.println("*** Please check for a new build from https://papermc.io/ci/job/Travertine/ ***");
               System.err.println("*** Should this build be outdated, you will get NO support for it. ***");
               System.err.println("*** Server will start in 10 seconds ***");
               Thread.sleep(TimeUnit.SECONDS.toMillis(10L));
            }
         }

         BungeeCord var5 = new BungeeCord();
         ProxyServer.setInstance(var5);
         var5.getLogger().info("Enabled Travertine (XCORD) version " + var5.getVersion());
         var5.start();
         if (!var2.has("noconsole")) {
            new WaterfallConsole().start();
         }
      }
   }
}
