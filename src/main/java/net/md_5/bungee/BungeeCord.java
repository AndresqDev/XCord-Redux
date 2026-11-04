package net.md_5.bungee;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.waterfallmc.waterfall.conf.WaterfallConfiguration;
import io.github.waterfallmc.waterfall.event.ConnectionInitEvent;
import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxyPluginEnableDisableException;
import io.github.waterfallmc.waterfall.log4j.WaterfallLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;
import java.util.logging.Logger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandList;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.CommandResetBlacklist;
import net.md_5.bungee.command.CommandServer;
import net.md_5.bungee.command.CommandXCordReload;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.jni.prepare.DefaultJavaZlibPreparer;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import xcord.XCord;
import xcord.command.MemoryCommand;
import xcord.command.TimedEventsCommand;
import xcord.network.SocketCheckDispatcher;

public class BungeeCord extends ProxyServer {
    public volatile boolean isRunning;
    public final Configuration config = new WaterfallConfiguration();
    private ResourceBundle baseBundle;
    private ResourceBundle customBundle;
    public EventLoopGroup bossEventLoopGroup;
    public EventLoopGroup workerEventLoopGroup;
    private final Timer saveThread = new Timer("Reconnect Saver");
    private final Collection<Channel> listeners = new HashSet<>();
    private final Map<String, UserConnection> connections = new CaseInsensitiveMap();
    private final Map<UUID, UserConnection> connectionsByOfflineUUID = new HashMap<>();
    private final Map<UUID, UserConnection> connectionsByUUID = new HashMap<>();
    private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    private final ReentrantLock shutdownLock = new ReentrantLock();
    public final PluginManager pluginManager;
    private ReconnectHandler reconnectHandler;
    private ConfigurationAdapter configurationAdapter = new YamlConfig();
    private final Collection<String> pluginChannels = new HashSet<>();
    private final File pluginsFolder = new File("plugins");
    private final BungeeScheduler scheduler = new BungeeScheduler();
    private final Logger logger;
    public final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
            .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
            .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
            .registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer())
            .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter())
            .create();
    public final Gson gsonLegacy = new GsonBuilder()
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(ServerPing.PlayerInfo.class, new PlayerInfoSerializer(4))
            .registerTypeAdapter(Favicon.class, Favicon.getFaviconTypeAdapter())
            .create();
    private ConnectionThrottle connectionThrottle;
    private final ModuleManager moduleManager = new ModuleManager();
    public boolean geyserLoaded;
    public static String serverAddress;

    public static BungeeCord getInstance() {
        return (BungeeCord) ProxyServer.getInstance();
    }

    public BungeeCord() throws IOException {
        this.registerChannel("BungeeCord");
        this.geyserLoaded = false;
        ProxyServer.setInstance(this);
        Preconditions.checkState(
                new File(".").getAbsolutePath().indexOf(33) == -1,
                "Cannot use Travertine in directory with ! in path."
        );

        try {
            this.baseBundle = ResourceBundle.getBundle("messages");
        } catch (MissingResourceException ignored) {
            this.baseBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }

        this.reloadMessages();
        System.setProperty("library.jansi.version", "BungeeCord");
        this.logger = WaterfallLogger.create();
        this.pluginManager = new PluginManager(this);
        this.getPluginManager().registerCommand(null, new CommandReload());
        this.getPluginManager().registerCommand(null, new CommandEnd());
        this.getPluginManager().registerCommand(null, new CommandIP());
        this.getPluginManager().registerCommand(null, new CommandBungee());
        this.getPluginManager().registerCommand(null, new CommandPerms());
        this.getPluginManager().registerCommand(null, new CommandList());
        this.getPluginManager().registerCommand(null, new CommandServer());
        this.getPluginManager().registerCommand(null, new CommandXCordReload());
        this.getPluginManager().registerCommand(null, new CommandResetBlacklist());
        this.getPluginManager().registerCommand(null, new TimedEventsCommand());
        this.getPluginManager().registerCommand(null, new MemoryCommand());

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            serverAddress = socket.getLocalAddress().getHostAddress();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.pluginManager.getEventBus().watchForEmpty(ClientConnectEvent.class);
        this.pluginManager.getEventBus().watchForEmpty(ConnectionInitEvent.class);
        this.pluginManager.getEventBus().watchForEmpty(PlayerHandshakeEvent.class);

        try {
            new XCord();
        } catch (Exception exception) {
            System.out.println("Failed to initialize anti-exploit/bot security measures. System will now be force-exited: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(0);
        }

        try {
            int status = new DefaultJavaZlibPreparer().prepareCompressorAndGetStatus(
                    XCord.getConfig().getGeneralCompressionStrength(), 256
            );
            if (status != 0) {
                this.logger.info("An error occured while checking the compressor: " + status);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (!Boolean.getBoolean("net.md_5.bungee.native.disable")) {
            if (EncryptionUtil.nativeFactory.load()) {
                this.logger.info("Using mbed TLS based native cipher.");
            } else {
                this.logger.info("Using standard Java JCE cipher.");
            }

            if (CompressFactory.zlib.load()) {
                this.logger.info("Using zlib based XCord native compressor.");
            } else {
                this.logger.info("Using standard Java compressor.");
            }
        }
    }

    public void start() throws Exception {
        System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
        if (System.getProperty("io.netty.leakDetectionLevel") == null
                && System.getProperty("io.netty.leakDetection.level") == null) {
            ResourceLeakDetector.setLevel(Level.DISABLED);
        }

        this.bossEventLoopGroup = PipelineUtils.newEventLoopGroup(0, null);
        this.workerEventLoopGroup = PipelineUtils.newEventLoopGroup(0, null);
        File modulesFolder = new File("modules");
        this.moduleManager.load(this, modulesFolder);
        this.pluginManager.detectPlugins(modulesFolder);
        this.pluginsFolder.mkdir();
        this.pluginManager.detectPlugins(this.pluginsFolder);
        this.pluginManager.loadPlugins();
        this.config.load();
        this.geyserLoaded = this.pluginManager.getPlugin("Geyser-BungeeCord") != null;
        if (this.geyserLoaded) {
            this.logger.info("Geyser detected!");
            PipelineUtils.CHANNEL_UTILITY = new SocketCheckDispatcher();
        }

        if (this.config.isForgeSupport()) {
            this.registerChannel("FML");
            this.registerChannel("FML|HS");
            this.registerChannel("FORGE");
        }

        this.isRunning = true;
        this.pluginManager.enablePlugins();
        if (this.config.getThrottle() > 0) {
            this.connectionThrottle = new ConnectionThrottle(this.config.getThrottle(), this.config.getThrottleLimit());
        }

        this.startListeners();
        this.saveThread.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (BungeeCord.this.getReconnectHandler() != null) {
                    BungeeCord.this.getReconnectHandler().save();
                }
            }
        }, 0L, TimeUnit.MINUTES.toMillis(5L));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                BungeeCord.this.independentThreadStop(BungeeCord.this.getTranslation("restart"), false);
            }
        });
    }

    public void startListeners() {
        for (final ListenerInfo listener : this.config.getListeners()) {
            if (listener.isProxyProtocol()) {
                this.getLogger()
                        .log(
                                java.util.logging.Level.WARNING,
                                "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.",
                                listener.getSocketAddress()
                        );
                if (this.connectionThrottle != null) {
                    this.connectionThrottle = null;
                    this.getLogger()
                            .log(
                                    java.util.logging.Level.WARNING,
                                    "Since PROXY protocol is in use, internal connection throttle has been disabled."
                            );
                }
            }

            ChannelFutureListener bindListener = new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        BungeeCord.this.listeners.add(future.channel());
                        BungeeCord.this.getLogger()
                                .log(java.util.logging.Level.INFO, "Listening on {0}", listener.getSocketAddress());
                    } else {
                        BungeeCord.this.getLogger()
                                .log(
                                        java.util.logging.Level.WARNING,
                                        "Could not bind to host " + listener.getSocketAddress(),
                                        future.cause()
                                );
                    }
                }
            };
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .channel(PipelineUtils.getServerChannel(listener.getSocketAddress()))
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childAttr(PipelineUtils.LISTENER, listener)
                    .childHandler(PipelineUtils.SERVER_CHILD)
                    .group(this.bossEventLoopGroup, this.workerEventLoopGroup)
                    .localAddress(listener.getSocketAddress());
            if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastConnectOnClients() && PipelineUtils.usingEpoll()) {
                bootstrap.option(ChannelOption.TCP_FASTOPEN, 3);
            }

            bootstrap.bind().addListener(bindListener);
            if (listener.isQueryEnabled()) {
                this.logger.warning("[!] Critical warning ===========================>");
                this.logger.warning(" > It's HIGHLY recommended to disable 'query-enabled' in your Bungeecord config! (config.yml)");
                this.logger.warning(" > Leaving this option on will open an easily exploitable UDP port, and leave you VERY open to attacks");
                this.logger.warning("[!] Critical warning ===========================>");

                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                Preconditions.checkArgument(
                        listener.getSocketAddress() instanceof InetSocketAddress,
                        "Can only create query listener on UDP address"
                );
                ChannelFutureListener queryListener = new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            BungeeCord.this.listeners.add(future.channel());
                            BungeeCord.this.getLogger()
                                    .log(java.util.logging.Level.INFO, "Started query on {0}", future.channel().localAddress());
                        } else {
                            BungeeCord.this.getLogger()
                                    .log(
                                            java.util.logging.Level.WARNING,
                                            "Could not bind to host " + listener.getSocketAddress(),
                                            future.cause()
                                    );
                        }
                    }
                };
                new RemoteQuery(this, listener)
                        .start(
                                PipelineUtils.getDatagramChannel(),
                                new InetSocketAddress(listener.getHost().getAddress(), listener.getQueryPort()),
                                this.workerEventLoopGroup,
                                queryListener
                        );
            }
        }
    }

    public void stopListeners() {
        for (Channel listener : this.listeners) {
            this.getLogger().log(java.util.logging.Level.INFO, "Closing listener {0}", listener);

            try {
                listener.close().syncUninterruptibly();
            } catch (ChannelException exception) {
                this.getLogger().severe("Could not close listen thread");
            }
        }

        this.listeners.clear();
    }

    @Override
    public void stop() {
        this.stop(this.getTranslation("restart"));
    }

    @Override
    public void stop(final String reason) {
        new Thread("Shutdown Thread") {
            @Override
            public void run() {
                BungeeCord.this.independentThreadStop(reason, true);
            }
        }.start();
    }

    private void independentThreadStop(String reason, boolean exit) {
        this.shutdownLock.lock();
        if (!this.isRunning) {
            this.shutdownLock.unlock();
            return;
        }

        this.isRunning = false;
        this.stopListeners();
        this.getLogger().info("Closing pending connections");
        this.connectionLock.readLock().lock();

        try {
            this.getLogger().log(java.util.logging.Level.INFO, "Disconnecting {0} connections", this.connections.size());

            for (UserConnection connection : this.connections.values()) {
                connection.disconnect(reason);
            }
        } finally {
            this.connectionLock.readLock().unlock();
        }

        try {
            Thread.sleep(500L);
        } catch (InterruptedException ignored) {
        }

        if (this.reconnectHandler != null) {
            this.getLogger().info("Saving reconnect locations");
            this.reconnectHandler.save();
            this.reconnectHandler.close();
        }

        this.saveThread.cancel();
        this.getLogger().info("Disabling plugins");

        for (Plugin plugin : Lists.reverse(new ArrayList<>(this.pluginManager.getPlugins()))) {
            try {
                plugin.onDisable();

                for (Handler handler : plugin.getLogger().getHandlers()) {
                    handler.close();
                }
            } catch (Throwable throwable) {
                String message = "Exception disabling plugin " + plugin.getDescription().getName();
                this.getLogger().log(java.util.logging.Level.SEVERE, message, throwable);
                this.pluginManager.callEvent(
                        new ProxyExceptionEvent(new ProxyPluginEnableDisableException(message, throwable, plugin))
                );
            }

            this.getScheduler().cancel(plugin);
            plugin.getExecutorService().shutdownNow();
        }

        this.getLogger().info("Closing IO threads");
        this.bossEventLoopGroup.shutdownGracefully();
        this.workerEventLoopGroup.shutdownGracefully();

        while (true) {
            try {
                this.bossEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                this.workerEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                break;
            } catch (InterruptedException ignored) {
            }
        }

        this.getLogger().info("Thank you and goodbye");

        for (Handler handler : this.getLogger().getHandlers()) {
            handler.close();
        }

        this.shutdownLock.unlock();
        if (exit) {
            System.exit(0);
        }
    }

    public void broadcast(DefinedPacket packet) {
        this.connectionLock.readLock().lock();

        try {
            for (UserConnection connection : this.connections.values()) {
                connection.unsafe().sendPacket(packet);
            }
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        return XCord.getConfig().getCompatibilityAppearAsWaterfall() ? "Waterfall" : "XCord";
    }

    @Override
    public String getVersion() {
        return "git:Travertine-Bootstrap:1.16-R0.5-SNAPSHOT:a24c106:158";
    }

    public void reloadMessages() {
        File messagesFile = new File("messages.properties");
        if (messagesFile.isFile()) {
            try (FileReader reader = new FileReader(messagesFile)) {
                this.customBundle = new PropertyResourceBundle(reader);
            } catch (IOException exception) {
                this.getLogger().log(java.util.logging.Level.SEVERE, "Could not load custom messages.properties", exception);
            }
        }
    }

    @Override
    public String getTranslation(String key, Object... args) {
        String translation = "<translation '" + key + "' missing>";

        try {
            String format = this.customBundle != null && this.customBundle.containsKey(key)
                    ? this.customBundle.getString(key)
                    : this.baseBundle.getString(key);
            translation = args.length == 0 ? format : MessageFormat.format(format, args);
        } catch (MissingResourceException ignored) {
        }

        return translation;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        this.connectionLock.readLock().lock();

        try {
            return Collections.unmodifiableCollection(new HashSet<>(this.connections.values()));
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    public Collection<UserConnection> getUserConnections() {
        this.connectionLock.readLock().lock();

        try {
            return new ArrayList<>(this.connections.values());
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public int getOnlineCount() {
        return this.connections.size();
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        this.connectionLock.readLock().lock();

        try {
            return this.connections.get(name);
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    public UserConnection getPlayerByOfflineUUID(UUID uuid) {
        this.connectionLock.readLock().lock();

        try {
            return this.connectionsByOfflineUUID.get(uuid);
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    public Map<UUID, UserConnection> retainAndGetOfflineUUIDMap() {
        this.connectionLock.readLock().lock();
        return this.connectionsByOfflineUUID;
    }

    public void releaseOfflineUUIDMap() {
        this.connectionLock.readLock().unlock();
    }

    @Override
    public ProxiedPlayer getPlayer(UUID uuid) {
        this.connectionLock.readLock().lock();

        try {
            return this.connectionsByUUID.get(uuid);
        } finally {
            this.connectionLock.readLock().unlock();
        }
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return this.config.getServers();
    }

    @Override
    public Map<String, ServerInfo> getServersCopy() {
        return this.config.getServersCopy();
    }

    @Override
    public ServerInfo getServerInfo(String name) {
        return this.config.getServerInfo(name);
    }

    @Override
    public void registerChannel(String channel) {
        synchronized (this.pluginChannels) {
            this.pluginChannels.add(channel);
        }
    }

    @Override
    public void unregisterChannel(String channel) {
        synchronized (this.pluginChannels) {
            this.pluginChannels.remove(channel);
        }
    }

    @Override
    public Collection<String> getChannels() {
        synchronized (this.pluginChannels) {
            return Collections.unmodifiableCollection(this.pluginChannels);
        }
    }

    public PluginMessage registerChannels(int protocolVersion) {
        return protocolVersion >= 393
                ? new PluginMessage(
                        "minecraft:register",
                        Util.format(Iterables.transform(this.pluginChannels, PluginMessage.MODERNISE), "\u0000")
                                .getBytes(Charsets.UTF_8),
                        false
                )
                : new PluginMessage("REGISTER", Util.format(this.pluginChannels, "\u0000").getBytes(Charsets.UTF_8), false);
    }

    @Override
    public int getProtocolVersion() {
        return ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1);
    }

    @Override
    public String getGameVersion() {
        return this.getConfig().getGameVersion();
    }

    @Override
    public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
        return this.constructServerInfo(name, (SocketAddress) address, motd, restricted);
    }

    @Override
    public ServerInfo constructServerInfo(String name, SocketAddress address, String motd, boolean restricted) {
        return new BungeeServerInfo(name, address, motd, restricted);
    }

    @Override
    public CommandSender getConsole() {
        return ConsoleCommandSender.getInstance();
    }

    @Override
    public void broadcast(String message) {
        this.broadcast(TextComponent.fromLegacyText(message));
    }

    @Override
    public void broadcast(BaseComponent... message) {
        this.getConsole().sendMessage(BaseComponent.toLegacyText(message));

        for (ProxiedPlayer player : this.getPlayers()) {
            player.sendMessage(message);
        }
    }

    @Override
    public void broadcast(BaseComponent message) {
        this.getConsole().sendMessage(message.toLegacyText());

        for (ProxiedPlayer player : this.getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void addConnection(UserConnection connection) {
        this.connectionLock.writeLock().lock();

        try {
            this.connections.put(connection.getName(), connection);
            this.connectionsByUUID.put(connection.getUniqueId(), connection);
            this.connectionsByOfflineUUID.put(connection.getPendingConnection().getOfflineId(), connection);
        } finally {
            this.connectionLock.writeLock().unlock();
        }
    }

    public void removeConnection(UserConnection connection) {
        this.connectionLock.writeLock().lock();

        try {
            if (this.connections.get(connection.getName()) == connection) {
                this.connections.remove(connection.getName());
                this.connectionsByUUID.remove(connection.getUniqueId());
                this.connectionsByOfflineUUID.remove(connection.getPendingConnection().getOfflineId());
            }
        } finally {
            this.connectionLock.writeLock().unlock();
        }
    }

    @Override
    public Collection<String> getDisabledCommands() {
        return this.config.getDisabledCommands();
    }

    @Override
    public Collection<ProxiedPlayer> matchPlayer(final String partialName) {
        Preconditions.checkNotNull(partialName, "partialName");
        ProxiedPlayer exactMatch = this.getPlayer(partialName);
        return exactMatch != null
                ? Collections.singleton(exactMatch)
                : Sets.newHashSet(Iterables.filter(this.getPlayers(), new Predicate<ProxiedPlayer>() {
                    public boolean apply(ProxiedPlayer player) {
                        return player != null
                                && player.getName().toLowerCase(Locale.ROOT).startsWith(partialName.toLowerCase(Locale.ROOT));
                    }
                }));
    }

    @Override
    public Title createTitle() {
        return new BungeeTitle();
    }

    public Configuration getConfig() {
        return this.config;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public ReconnectHandler getReconnectHandler() {
        return this.reconnectHandler;
    }

    @Override
    public void setReconnectHandler(ReconnectHandler reconnectHandler) {
        this.reconnectHandler = reconnectHandler;
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        return this.configurationAdapter;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public File getPluginsFolder() {
        return this.pluginsFolder;
    }

    public BungeeScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public ConnectionThrottle getConnectionThrottle() {
        return this.connectionThrottle;
    }

    static {
        System.setProperty("java.awt.headless", "true");
        if (System.getProperty("io.netty.allocator.maxOrder") == null) {
            System.setProperty("io.netty.allocator.maxOrder", "9");
        }

        if (System.getProperty("io.netty.leakDetection.level") == null) {
            ResourceLeakDetector.setLevel(Level.DISABLED);
        }

        serverAddress = null;
    }
}
