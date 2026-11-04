package xcord;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import me.minebuilders.proxyfork.antiproxy.AntiProxyManager;
import net.md_5.bungee.BungeeCord;
import xcord.AntiBotManager;
import xcord.AsyncJobService;
import xcord.BlacklistManager;
import xcord.CountryBlockService;
import xcord.DosFilterBlacklist;
import xcord.GlobalBlacklist;
import xcord.LogFilter;
import xcord.Messages;
import xcord.ReloadableService;
import xcord.TickableService;
import xcord.XCordConfig;

public class XCord
extends TimerTask {
    private static XCord instance;
    private static XCordConfig config;
    private static BlacklistManager blacklistManager;
    private static AntiBotManager antiBotManager;
    private static Messages messages;
    private static AsyncJobService asyncJobService;
    private static AntiProxyManager antiProxyManager;
    private static GlobalBlacklist globalBlacklist;
    private static DosFilterBlacklist dosFilterBlacklist;
    private static boolean logFilterEnabled;
    private static volatile int tick;
    private final List<TickableService> tickableServices = new ArrayList<TickableService>(3);
    private final List<ReloadableService> reloadableServices = new ArrayList<ReloadableService>(2);

    public XCord() {
        instance = this;
        config = new XCordConfig();
        blacklistManager = new BlacklistManager();
        antiBotManager = new AntiBotManager();
        messages = new Messages();
        asyncJobService = new AsyncJobService();
        antiProxyManager = new AntiProxyManager();
        globalBlacklist = new GlobalBlacklist();
        dosFilterBlacklist = new DosFilterBlacklist();
        this.tickableServices.add(dosFilterBlacklist);
        this.tickableServices.add(antiBotManager);
        this.tickableServices.add(blacklistManager);
        this.tickableServices.add(globalBlacklist);
        this.reloadableServices.add(antiProxyManager);
        this.reloadableServices.add(new LogFilter());
        this.reloadableServices.add(blacklistManager);
        this.reloadableServices.add(antiBotManager);
        this.reloadableServices.add(globalBlacklist);
        this.reloadableServices.add(dosFilterBlacklist);
        this.reloadableServices.add(new CountryBlockService());
        this.reload();
        new Timer("bobproxy-base", true).scheduleAtFixedRate((TimerTask)this, XCord.getConfig().getGeneralBaseTick(), (long)XCord.getConfig().getGeneralBaseTick());
        asyncJobService.start();
        BungeeCord.getInstance().getLogger().info("[~] XCord is waking up and checking the morning breeze...");
        System.out.println("XCord has started.");
    }

    public static XCord getInstance() {
        return instance;
    }

    public void reload() {
        for (ReloadableService reloadableService : this.reloadableServices) {
            try {
                reloadableService.reload();
            }
            catch (Exception exception) {
                BungeeCord.getInstance().getLogger().warning("Failed to reload " + reloadableService.getClass().getSimpleName());
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        ++tick;
        for (TickableService tickableService : this.tickableServices) {
            tickableService.tick(tick);
        }
    }

    public static GlobalBlacklist getGlobalBlacklist() {
        return globalBlacklist;
    }

    public static int getTick() {
        return tick;
    }

    public static BlacklistManager getBlacklistManager() {
        return blacklistManager;
    }

    public static AntiBotManager getAntiBotManager() {
        return antiBotManager;
    }

    public static XCordConfig getConfig() {
        return config;
    }

    public static void setConfig(XCordConfig xCordConfig) {
        config = xCordConfig;
    }

    public static void setMessages(Messages messages) {
        XCord.messages = messages;
    }

    public static boolean isLogFilterEnabled() {
        return logFilterEnabled;
    }

    public static Messages getMessages() {
        return messages;
    }

    public static AsyncJobService getAsyncJobService() {
        return asyncJobService;
    }

    public static AntiProxyManager getAntiProxyManager() {
        return antiProxyManager;
    }

    public static DosFilterBlacklist getDosFilterBlacklist() {
        return dosFilterBlacklist;
    }

    public static void setLogFilterEnabled(boolean bl) {
        logFilterEnabled = bl;
        if (bl) {
            BungeeCord.getInstance().getLogger().info("Now filtering logs/console...");
        } else {
            BungeeCord.getInstance().getLogger().info("No longer filtering logs/console...");
        }
    }

    public static void log(String string, Object ... objectArray) {
        BungeeCord.getInstance().getLogger().info(String.format(string, objectArray));
    }

    public static void log(String string) {
        BungeeCord.getInstance().getLogger().info(string);
    }

    static {
        logFilterEnabled = false;
        tick = 0;
    }
}

