package me.minebuilders.proxyfork.antiproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Command;
import xcord.BlackBoxApi;
import xcord.IPHubApi;
import xcord.PersistentMap;
import xcord.ProcessUtils;
import xcord.ProxyCheckApi;
import xcord.ProxyCheckProvider;
import xcord.ReloadableService;
import xcord.SubCommandDispatcher;
import xcord.TickableService;
import xcord.WhitelistAddCommand;
import xcord.WhitelistListCommand;
import xcord.WhitelistRemoveCommand;
import xcord.XCord;

public class AntiProxyManager
implements ReloadableService,
TickableService {
    private final List<ProxyCheckProvider> providers = new ArrayList<ProxyCheckProvider>();
    private PersistentMap<Integer, ProxyCheckResult> cache;
    private AntiProxyWhitelistManager whitelist;
    private int purgeInterval = 0;
    private int saveInterval;

    public AntiProxyManager() {
        Object object;
        Object object2;
        File file = new File("IPData.db");
        this.saveInterval = XCord.getTick() + XCord.getConfig().getLoginAntiVpnSaveInterval();
        if (file.isFile()) {
            try {
                object2 = new ObjectInputStream(new FileInputStream(file));
                try {
                    this.cache = (PersistentMap)((ObjectInputStream)object2).readObject();
                    this.cache.setFile(file);
                }
                finally {
                    ((ObjectInputStream)object2).close();
                }
            }
            catch (Exception exception) {
                file.delete();
                this.cache = new PersistentMap();
            }
        } else {
            this.cache = new PersistentMap(file);
        }
        if (((File)(object2 = new File("antivpn-whitelist.db"))).isFile()) {
            try {
                object = new ObjectInputStream(new FileInputStream((File)object2));
                try {
                    this.whitelist = (AntiProxyWhitelistManager)((ObjectInputStream)object).readObject();
                }
                finally {
                    ((ObjectInputStream)object).close();
                }
            }
            catch (Exception exception) {
                ((File)object2).delete();
                this.whitelist = new AntiProxyWhitelistManager((File)object2);
            }
        } else {
            this.whitelist = new AntiProxyWhitelistManager((File)object2);
        }
        object = new SubCommandDispatcher("xvpn", "xcord.command.vpn", new String[0]);
        ((SubCommandDispatcher)object).registerSubCommand("whitelist", new WhitelistAddCommand(this.whitelist));
        ((SubCommandDispatcher)object).registerSubCommand("listwhitelisted", new WhitelistListCommand(this.whitelist));
        ((SubCommandDispatcher)object).registerSubCommand("unwhitelist", new WhitelistRemoveCommand(this.whitelist));
        BungeeCord.getInstance().getPluginManager().registerCommand(null, (Command)object);
    }

    @Override
    public void reload() {
        this.providers.clear();
        if (XCord.getConfig().getLoginAntiVpnBlackboxApiEnabled()) {
            this.providers.add(new BlackBoxApi());
        }
        if (XCord.getConfig().getLoginAntiVpnIphubApiEnabled()) {
            this.providers.add(new IPHubApi());
        }
        if (XCord.getConfig().getLoginAntiVpnProxycheckerioApiEnabled()) {
            this.providers.add(new ProxyCheckApi());
        }
    }

    public ProxyResult check(String string, String string2, boolean bl) {
        int n = ProcessUtils.ipToInt(string2);
        if (this.whitelist.isIPWhitelisted(n) || this.whitelist.isUsernameWhitelisted(string)) {
            return ProxyResult.WHITELISTED;
        }
        ProxyCheckResult proxyCheckResult = this.cache.get(n);
        if (proxyCheckResult == null) {
            ProxyCheckProvider proxyCheckProvider;
            if (bl) {
                return null;
            }
            Iterator<ProxyCheckProvider> iterator = this.providers.iterator();
            while (iterator.hasNext() && (proxyCheckResult = (proxyCheckProvider = iterator.next()).check(string, string2)).getResult() != ProxyResult.DETECTED) {
            }
            if (proxyCheckResult == null) {
                return ProxyResult.UNKNOWN;
            }
            if (XCord.getConfig().getLoginAntiVpnLogNonCacheDetection() && proxyCheckResult.getResult() == ProxyResult.DETECTED) {
                System.out.println("Detected proxy: IP=" + string2 + " USER=" + (string != null ? string : "NA") + " ASN=" + (proxyCheckResult.getAsn() != null ? proxyCheckResult.getAsn() : "NA") + " COUNTRY=" + (proxyCheckResult.getCountryCode() != null ? proxyCheckResult.getCountryCode() : "NA"));
            }
            this.cache.put(n, proxyCheckResult);
        }
        if (proxyCheckResult.getAsn() != null && XCord.getConfig().getBlockedAsns().contains(proxyCheckResult.getAsn())) {
            return ProxyResult.DETECTED;
        }
        if (proxyCheckResult.getCountryCode() != null && XCord.getConfig().getBlockedCountries().contains(proxyCheckResult.getCountryCode())) {
            return ProxyResult.DETECTED;
        }
        return proxyCheckResult.getResult();
    }

    @Override
    public void tick(int n) {
        if (n >= this.purgeInterval) {
            this.purgeInterval = n + XCord.getConfig().getLoginAntiVpnPurgeInterval();
            new Thread(() -> this.cache.getMap().entrySet().removeIf(entry -> ((ProxyCheckResult)entry.getValue()).getTimestamp() < System.currentTimeMillis() - XCord.getConfig().getLoginAntiVpnPurgeAge() * 1000L || ((ProxyCheckResult)entry.getValue()).getResult() == ProxyResult.UNKNOWN)).start();
        } else if (n >= this.saveInterval) {
            this.saveInterval = n + XCord.getConfig().getLoginAntiVpnSaveInterval();
            new Thread(() -> {
                try {
                    this.cache.save();
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }).start();
        }
    }

    public AntiProxyWhitelistManager getWhitelist() {
        return this.whitelist;
    }
}

