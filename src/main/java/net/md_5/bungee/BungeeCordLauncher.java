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
    public static void main(String[] args) throws Exception {
        Security.setProperty("networkaddress.cache.ttl", "30");
        Security.setProperty("networkaddress.cache.negative.ttl", "10");
        if (System.getProperty("io.netty.allocator.maxOrder") == null) {
            System.setProperty("io.netty.allocator.maxOrder", "8");
        }

        if (System.getProperty("jdk.util.jar.enableMultiRelease") == null) {
            System.setProperty("jdk.util.jar.enableMultiRelease", "force");
        }

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.acceptsAll(Arrays.asList("help"), "Show the help");
        parser.acceptsAll(Arrays.asList("v", "version"), "Print version and exit");
        parser.acceptsAll(Arrays.asList("noconsole"), "Disable console input");
        OptionSet options = parser.parse(args);
        if (options.has("help")) {
            parser.printHelpOn(System.out);
        } else if (options.has("version")) {
            System.out.println(BungeeCord.class.getPackage().getImplementationVersion());
        } else {
            if (BungeeCord.class.getPackage().getSpecificationVersion() != null
                    && System.getProperty("IReallyKnowWhatIAmDoingISwear") == null) {
                Date buildDate = new SimpleDateFormat("yyyyMMdd")
                        .parse(BungeeCord.class.getPackage().getSpecificationVersion());
                Calendar outdatedCutoff = Calendar.getInstance();
                outdatedCutoff.add(Calendar.WEEK_OF_YEAR, -8);
                if (buildDate.before(outdatedCutoff.getTime())) {
                    System.err.println("*** Hey! This build is potentially outdated :( ***");
                    System.err.println("*** Please check for a new build from https://papermc.io/ci/job/Travertine/ ***");
                    System.err.println("*** Should this build be outdated, you will get NO support for it. ***");
                    System.err.println("*** Server will start in 10 seconds ***");
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10L));
                }
            }

            BungeeCord bungee = new BungeeCord();
            ProxyServer.setInstance(bungee);
            bungee.getLogger().info("Enabled Travertine (XCORD) version " + bungee.getVersion());
            bungee.start();
            if (!options.has("noconsole")) {
                new WaterfallConsole().start();
            }
        }
    }
}
