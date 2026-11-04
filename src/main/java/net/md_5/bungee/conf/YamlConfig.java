package net.md_5.bungee.conf;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import xcord.XCord;
import xcord.util.ProcessUtils;

public class YamlConfig implements ConfigurationAdapter {
    private final Yaml yaml;
    private Map<String, Object> config;
    private final File file;

    public YamlConfig() {
        this(new File("config.yml"));
    }

    public YamlConfig(File file) {
        this.file = file;
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Override
    public void load() {
        this.load(true);
    }

    public void load(boolean loadDefaults) {
        try {
            this.file.createNewFile();
            try (FileInputStream input = new FileInputStream(this.file)) {
                try {
                    this.config = this.yaml.load(input);
                } catch (YAMLException ex) {
                    throw new RuntimeException(
                        "Invalid configuration encountered - this is a configuration error and NOT a bug! Please attempt to fix the error or see https://www.spigotmc.org/ for help.",
                        ex
                    );
                }
            }

            if (this.config == null) {
                this.config = new CaseInsensitiveMap<>();
            } else {
                this.config = new CaseInsensitiveMap<>(this.config);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load configuration!", ex);
        }

        if (loadDefaults) {
            Map<String, Object> permissions = this.get("permissions", null);
            if (permissions == null) {
                this.set("permissions.default", Arrays.asList("bungeecord.command.server", "bungeecord.command.list"));
                this.set(
                    "permissions.admin", Arrays.asList("bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload")
                );
            }

            Map<String, Object> groups = this.get("groups", null);
            if (groups == null) {
                this.set("groups.md_5", Collections.singletonList("admin"));
            }
        }
    }

    private <T> T get(String path, T def) {
        return this.get(path, def, this.config);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String path, T def, Map<String, Object> map) {
        int index = path.indexOf('.');
        if (index == -1) {
            Object value = map.get(path);
            if (value == null && def != null) {
                value = def;
                map.put(path, def);
                this.save();
            }
            return (T) value;
        }

        String first = path.substring(0, index);
        String second = path.substring(index + 1);
        Map<String, Object> child = (Map<String, Object>) map.get(first);
        if (child == null) {
            child = new LinkedHashMap<>();
            map.put(first, child);
        }
        return this.get(second, def, child);
    }

    private void set(String path, Object value) {
        this.set(path, value, this.config);
    }

    @SuppressWarnings("unchecked")
    private void set(String path, Object value, Map<String, Object> map) {
        int index = path.indexOf('.');
        if (index == -1) {
            if (value == null) {
                map.remove(path);
            } else {
                map.put(path, value);
            }
            this.save();
            return;
        }

        String first = path.substring(0, index);
        String second = path.substring(index + 1);
        Map<String, Object> child = (Map<String, Object>) map.get(first);
        if (child == null) {
            child = new LinkedHashMap<>();
            map.put(first, child);
        }
        this.set(second, value, child);
    }

    private void save() {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.file), Charsets.UTF_8)) {
            this.yaml.dump(this.config, writer);
        } catch (IOException ex) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save config", ex);
        }
    }

    @Override
    public int getInt(String path, int def) {
        return this.get(path, def);
    }

    public double getDouble(String path, double def) {
        return this.get(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return this.get(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.get(path, def);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        Map<String, Map<String, Object>> configuredServers = this.get(
            "servers", Collections.<String, Map<String, Object>>singletonMap("lobby", new HashMap<>())
        );
        Map<String, ServerInfo> servers = new HashMap<>();

        for (Entry<String, Map<String, Object>> entry : configuredServers.entrySet()) {
            Map<String, Object> server = entry.getValue();
            String name = entry.getKey();
            String address = this.get("address", "localhost:25565", server);
            String motd = ChatColor.translateAlternateColorCodes('&', this.get("motd", "&1Just another XCord - Forced Host", server));
            boolean restricted = this.get("restricted", false, server);
            SocketAddress socketAddress = Util.getAddr(
                address,
                XCord.getConfig().getGeneralDynamicallyResolveServerAddresses()
                    && !address.contains("localhost")
                    && !ProcessUtils.isValidIp(address.split(":")[0])
            );
            ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(name, socketAddress, motd, restricted);
            servers.put(name, serverInfo);
        }

        return servers;
    }

    @Override
    public Collection<ListenerInfo> getListeners() {
        Collection<Map<String, Object>> configuredListeners = this.get(
            "listeners", Arrays.<Map<String, Object>>asList(new HashMap<>())
        );
        Map<String, String> defaultForcedHosts = new HashMap<>();
        defaultForcedHosts.put("pvp.md-5.net", "pvp");
        Collection<ListenerInfo> listeners = new HashSet<>();

        for (Map<String, Object> listener : configuredListeners) {
            String motd = this.get("motd", "&1Another XCord server", listener);
            motd = ChatColor.translateAlternateColorCodes('&', motd);
            int maxPlayers = this.get("max_players", 1, listener);
            boolean forceDefault = this.get("force_default_server", false, listener);
            String host = this.get("host", "0.0.0.0:25577", listener);
            int tabListSize = this.get("tab_size", 60, listener);
            SocketAddress socketAddress = Util.getAddr(host);
            Map<String, String> forcedHosts = new CaseInsensitiveMap<>(this.get("forced_hosts", defaultForcedHosts, listener));
            String tabList = this.get("tab_list", "GLOBAL_PING", listener);
            DefaultTabList tabListType = DefaultTabList.valueOf(tabList.toUpperCase(Locale.ROOT));
            boolean setLocalAddress = this.get("bind_local_address", true, listener);
            boolean pingPassthrough = this.get("ping_passthrough", false, listener);
            boolean queryEnabled = this.get("query_enabled", false, listener);
            int queryPort = this.get("query_port", 25577, listener);
            boolean proxyProtocol = this.get("proxy_protocol", false, listener);
            List<String> serverPriority = new ArrayList<>(this.get("priorities", Collections.<String>emptyList(), listener));
            String defaultServer = this.get("default_server", null, listener);
            String fallbackServer = this.get("fallback_server", null, listener);
            if (defaultServer != null) {
                serverPriority.add(defaultServer);
                this.set("default_server", null, listener);
            }

            if (fallbackServer != null) {
                serverPriority.add(fallbackServer);
                this.set("fallback_server", null, listener);
            }

            if (serverPriority.isEmpty()) {
                serverPriority.add("lobby");
            }

            this.set("priorities", serverPriority, listener);
            ListenerInfo listenerInfo = new ListenerInfo(
                socketAddress, motd, maxPlayers, tabListSize, serverPriority, forceDefault, forcedHosts,
                tabListType.toString(), setLocalAddress, pingPassthrough, queryPort, queryEnabled, proxyProtocol
            );
            listeners.add(listenerInfo);
        }

        return listeners;
    }

    @Override
    public Collection<String> getGroups(String player) {
        Map<String, Collection<String>> groups = this.get("groups", Collections.emptyMap());
        Collection<String> playerGroups = groups.get(player);
        Collection<String> result = playerGroups == null ? new HashSet<>() : new HashSet<>(playerGroups);
        result.add("default");
        return result;
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return this.get(path, def);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        Collection<String> permissions = this.get("permissions." + group, null);
        return permissions == null ? Collections.emptySet() : permissions;
    }

    private enum DefaultTabList {
        GLOBAL,
        GLOBAL_PING,
        SERVER
    }
}
