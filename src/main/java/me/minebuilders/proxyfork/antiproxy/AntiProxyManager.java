package me.minebuilders.proxyfork.antiproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;
import xcord.antivpn.BlackBoxApi;
import xcord.antivpn.IPHubApi;
import xcord.antivpn.ProxyCheckApi;
import xcord.antivpn.ProxyCheckProvider;
import xcord.command.SubCommandDispatcher;
import xcord.command.WhitelistAddCommand;
import xcord.command.WhitelistListCommand;
import xcord.command.WhitelistRemoveCommand;
import xcord.service.ReloadableService;
import xcord.service.TickableService;
import xcord.storage.PersistentMap;
import xcord.util.ProcessUtils;

public class AntiProxyManager implements ReloadableService, TickableService {
    private final List<ProxyCheckProvider> providers = new ArrayList<ProxyCheckProvider>();
    private PersistentMap<Integer, ProxyCheckResult> cache;
    private AntiProxyWhitelistManager whitelist;
    private int purgeInterval = 0;
    private int saveInterval;

    public AntiProxyManager() {
        File cacheFile = new File("IPData.db");
        this.saveInterval = XCord.getTick() + XCord.getConfig().getLoginAntiVpnSaveInterval();
        if (cacheFile.isFile()) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(cacheFile))) {
                this.cache = (PersistentMap<Integer, ProxyCheckResult>)input.readObject();
                this.cache.setFile(cacheFile);
            } catch (Exception exception) {
                cacheFile.delete();
                this.cache = new PersistentMap<>();
            }
        } else {
            this.cache = new PersistentMap<>(cacheFile);
        }

        File whitelistFile = new File("antivpn-whitelist.db");
        if (whitelistFile.isFile()) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(whitelistFile))) {
                this.whitelist = (AntiProxyWhitelistManager)input.readObject();
            } catch (Exception exception) {
                whitelistFile.delete();
                this.whitelist = new AntiProxyWhitelistManager(whitelistFile);
            }
        } else {
            this.whitelist = new AntiProxyWhitelistManager(whitelistFile);
        }

        SubCommandDispatcher dispatcher = new SubCommandDispatcher("xvpn", "xcord.command.vpn", new String[0]);
        dispatcher.registerSubCommand("whitelist", new WhitelistAddCommand(this.whitelist));
        dispatcher.registerSubCommand("listwhitelisted", new WhitelistListCommand(this.whitelist));
        dispatcher.registerSubCommand("unwhitelist", new WhitelistRemoveCommand(this.whitelist));
        BungeeCord.getInstance().getPluginManager().registerCommand(null, dispatcher);
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

    public ProxyResult check(String name, String ip, boolean cacheOnly) {
        int ipValue = ProcessUtils.ipToInt(ip);
        if (this.whitelist.isIPWhitelisted(ipValue) || this.whitelist.isUsernameWhitelisted(name)) {
            return ProxyResult.WHITELISTED;
        }
        ProxyCheckResult result = this.cache.get(ipValue);
        if (result == null) {
            if (cacheOnly) {
                return null;
            }
            for (ProxyCheckProvider provider : this.providers) {
                result = provider.check(name, ip);
                if (result.getResult() == ProxyResult.DETECTED) {
                    break;
                }
            }
            if (result == null) {
                return ProxyResult.UNKNOWN;
            }
            if (XCord.getConfig().getLoginAntiVpnLogNonCacheDetection() && result.getResult() == ProxyResult.DETECTED) {
                System.out.println("Detected proxy: IP=" + ip + " USER=" + (name != null ? name : "NA") + " ASN=" + (result.getAsn() != null ? result.getAsn() : "NA") + " COUNTRY=" + (result.getCountryCode() != null ? result.getCountryCode() : "NA"));
            }
            this.cache.put(ipValue, result);
        }
        if (result.getAsn() != null && XCord.getConfig().getBlockedAsns().contains(result.getAsn())) {
            return ProxyResult.DETECTED;
        }
        if (result.getCountryCode() != null && XCord.getConfig().getBlockedCountries().contains(result.getCountryCode())) {
            return ProxyResult.DETECTED;
        }
        return result.getResult();
    }

    @Override
    public void tick(int currentTick) {
        if (currentTick >= this.purgeInterval) {
            this.purgeInterval = currentTick + XCord.getConfig().getLoginAntiVpnPurgeInterval();
            new Thread(() -> this.cache.getMap().entrySet().removeIf(entry -> entry.getValue().getTimestamp() < System.currentTimeMillis() - XCord.getConfig().getLoginAntiVpnPurgeAge() * 1000L || entry.getValue().getResult() == ProxyResult.UNKNOWN)).start();
        } else if (currentTick >= this.saveInterval) {
            this.saveInterval = currentTick + XCord.getConfig().getLoginAntiVpnSaveInterval();
            new Thread(() -> {
                try {
                    this.cache.save();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }).start();
        }
    }

    public AntiProxyWhitelistManager getWhitelist() {
        return this.whitelist;
    }
}
