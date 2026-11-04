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
import net.md_5.bungee.command.CommandServer;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.CommandResetBlacklist;
import net.md_5.bungee.command.CommandXCordReload;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.jni.prepare.DefaultJavaZlibPreparer;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.conf.YamlConfig;
import net.md_5.bungee.module.ModuleManager;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.query.RemoteQuery;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import xcord.MemoryCommand;
import xcord.SocketCheckDispatcher;
import xcord.TimedEventsCommand;
import xcord.XCord;

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
      return (BungeeCord)ProxyServer.getInstance();
   }

   public BungeeCord() throws IOException {
      this.registerChannel("BungeeCord");
      this.geyserLoaded = false;
      ProxyServer.setInstance(this);
      Preconditions.checkState(new File(".").getAbsolutePath().indexOf(33) == -1, "Cannot use Travertine in directory with ! in path.");

      try {
         this.baseBundle = ResourceBundle.getBundle("messages");
      } catch (MissingResourceException var9) {
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

      try {
         DatagramSocket var1 = new DatagramSocket();

         try {
            var1.connect(InetAddress.getByName("8.8.8.8"), 10002);
            serverAddress = var1.getLocalAddress().getHostAddress();
         } catch (Throwable var7) {
            try {
               var1.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var1.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      this.pluginManager.getEventBus().watchForEmpty(ClientConnectEvent.class);
      this.pluginManager.getEventBus().watchForEmpty(ConnectionInitEvent.class);
      this.pluginManager.getEventBus().watchForEmpty(PlayerHandshakeEvent.class);

      try {
         new XCord();
      } catch (Exception var5) {
         System.out.println("Failed to initialize anti-exploit/bot security measures. System will now be force-exited: " + var5.getMessage());
         var5.printStackTrace();
         System.exit(0);
      }

      boolean var10 = true;
      try {
         int var2 = new DefaultJavaZlibPreparer().prepareCompressorAndGetStatus(
            XCord.getConfig().getGeneralCompressionStrength(), 256);
         if (var2 != 0) {
            this.logger.info("An error occured while checking the compressor: " + var2);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (!Boolean.getBoolean("net.md_5.bungee.native.disable")) {
         if (EncryptionUtil.nativeFactory.load()) {
            this.logger.info("Using mbed TLS based native cipher.");
         } else {
            this.logger.info("Using standard Java JCE cipher.");
         }

         if (CompressFactory.zlib.load() && var10) {
            this.logger.info("Using zlib based XCord native compressor.");
         } else {
            this.logger.info("Using standard Java compressor.");
         }
      }
   }

   public void start() throws Exception {
      System.setProperty("io.netty.selectorAutoRebuildThreshold", "0");
      if (System.getProperty("io.netty.leakDetectionLevel") == null && System.getProperty("io.netty.leakDetection.level") == null) {
         ResourceLeakDetector.setLevel(Level.DISABLED);
      }

      this.bossEventLoopGroup = PipelineUtils.newEventLoopGroup(0, null);
      this.workerEventLoopGroup = PipelineUtils.newEventLoopGroup(0, null);
      File var1 = new File("modules");
      this.moduleManager.load(this, var1);
      this.pluginManager.detectPlugins(var1);
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
      for (final ListenerInfo var2 : this.config.getListeners()) {
         if (var2.isProxyProtocol()) {
            this.getLogger()
               .log(
                  java.util.logging.Level.WARNING,
                  "Using PROXY protocol for listener {0}, please ensure this listener is adequately firewalled.",
                  var2.getSocketAddress()
               );
            if (this.connectionThrottle != null) {
               this.connectionThrottle = null;
               this.getLogger().log(java.util.logging.Level.WARNING, "Since PROXY protocol is in use, internal connection throttle has been disabled.");
            }
         }

         ChannelFutureListener var3 = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) throws Exception {
               if (var1.isSuccess()) {
                  BungeeCord.this.listeners.add(var1.channel());
                  BungeeCord.this.getLogger().log(java.util.logging.Level.INFO, "Listening on {0}", var2.getSocketAddress());
               } else {
                  BungeeCord.this.getLogger().log(java.util.logging.Level.WARNING, "Could not bind to host " + var2.getSocketAddress(), var1.cause());
               }
            }
         };
         ServerBootstrap var4 = (ServerBootstrap)((ServerBootstrap)((ServerBootstrap)new ServerBootstrap()
                  .channel(PipelineUtils.getServerChannel(var2.getSocketAddress())))
               .option(ChannelOption.SO_REUSEADDR, true))
            .childAttr(PipelineUtils.LISTENER, var2)
            .childHandler(PipelineUtils.SERVER_CHILD)
            .group(this.bossEventLoopGroup, this.workerEventLoopGroup)
            .localAddress(var2.getSocketAddress());
         if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastConnectOnClients() && PipelineUtils.usingEpoll()) {
            var4.option(ChannelOption.TCP_FASTOPEN, 3);
         }

         var4.bind().addListener(var3);
         if (var2.isQueryEnabled()) {
            this.logger.warning("[!] Critical warning ===========================>");
            this.logger.warning(" > It's HIGHLY recommended to disable 'query-enabled' in your Bungeecord config! (config.yml)");
            this.logger.warning(" > Leaving this option on will open an easily exploitable UDP port, and leave you VERY open to attacks");
            this.logger.warning("[!] Critical warning ===========================>");

            try {
               Thread.sleep(10000L);
            } catch (InterruptedException var6) {
               var6.printStackTrace();
            }

            Preconditions.checkArgument(var2.getSocketAddress() instanceof InetSocketAddress, "Can only create query listener on UDP address");
            ChannelFutureListener var5 = new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  if (var1.isSuccess()) {
                     BungeeCord.this.listeners.add(var1.channel());
                     BungeeCord.this.getLogger().log(java.util.logging.Level.INFO, "Started query on {0}", var1.channel().localAddress());
                  } else {
                     BungeeCord.this.getLogger().log(java.util.logging.Level.WARNING, "Could not bind to host " + var2.getSocketAddress(), var1.cause());
                  }
               }
            };
            new RemoteQuery(this, var2)
               .start(
                  PipelineUtils.getDatagramChannel(), new InetSocketAddress(var2.getHost().getAddress(), var2.getQueryPort()), this.workerEventLoopGroup, var5
               );
         }
      }
   }

   public void stopListeners() {
      for (Channel var2 : this.listeners) {
         this.getLogger().log(java.util.logging.Level.INFO, "Closing listener {0}", var2);

         try {
            var2.close().syncUninterruptibly();
         } catch (ChannelException var4) {
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
   public void stop(final String var1) {
      (new Thread("Shutdown Thread") {
         @Override
         public void run() {
            BungeeCord.this.independentThreadStop(var1, true);
         }
      }).start();
   }

   private void independentThreadStop(String var1, boolean var2) {
      this.shutdownLock.lock();
      if (!this.isRunning) {
         this.shutdownLock.unlock();
      } else {
         this.isRunning = false;
         this.stopListeners();
         this.getLogger().info("Closing pending connections");
         this.connectionLock.readLock().lock();

         try {
            this.getLogger().log(java.util.logging.Level.INFO, "Disconnecting {0} connections", this.connections.size());

            for (UserConnection var4 : this.connections.values()) {
               var4.disconnect(var1);
            }
         } finally {
            this.connectionLock.readLock().unlock();
         }

         try {
            Thread.sleep(500L);
         } catch (InterruptedException var13) {
         }

         if (this.reconnectHandler != null) {
            this.getLogger().info("Saving reconnect locations");
            this.reconnectHandler.save();
            this.reconnectHandler.close();
         }

         this.saveThread.cancel();
         this.getLogger().info("Disabling plugins");

         for (Plugin var19 : Lists.reverse(new ArrayList<>(this.pluginManager.getPlugins()))) {
            try {
               var19.onDisable();

               for (Handler var8 : var19.getLogger().getHandlers()) {
                  var8.close();
               }
            } catch (Throwable var15) {
               String var6 = "Exception disabling plugin " + var19.getDescription().getName();
               this.getLogger().log(java.util.logging.Level.SEVERE, var6, var15);
               this.pluginManager.callEvent(new ProxyExceptionEvent(new ProxyPluginEnableDisableException(var6, var15, var19)));
            }

            this.getScheduler().cancel(var19);
            var19.getExecutorService().shutdownNow();
         }

         this.getLogger().info("Closing IO threads");
         this.bossEventLoopGroup.shutdownGracefully();
         this.workerEventLoopGroup.shutdownGracefully();

         while (true) {
            try {
               this.bossEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
               this.workerEventLoopGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
               break;
            } catch (InterruptedException var14) {
            }
         }

         this.getLogger().info("Thank you and goodbye");

         for (Handler var23 : this.getLogger().getHandlers()) {
            var23.close();
         }

         this.shutdownLock.unlock();
         if (var2) {
            System.exit(0);
         }
      }
   }

   public void broadcast(DefinedPacket var1) {
      this.connectionLock.readLock().lock();

      try {
         for (UserConnection var3 : this.connections.values()) {
            var3.unsafe().sendPacket(var1);
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
      File var1 = new File("messages.properties");
      if (var1.isFile()) {
         try {
            FileReader var2 = new FileReader(var1);

            try {
               this.customBundle = new PropertyResourceBundle(var2);
            } catch (Throwable var6) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            var2.close();
         } catch (IOException var7) {
            this.getLogger().log(java.util.logging.Level.SEVERE, "Could not load custom messages.properties", (Throwable)var7);
         }
      }
   }

   @Override
   public String getTranslation(String var1, Object... var2) {
      String var3 = "<translation '" + var1 + "' missing>";

      try {
         String var4 = this.customBundle != null && this.customBundle.containsKey(var1) ? this.customBundle.getString(var1) : this.baseBundle.getString(var1);
         var3 = var2.length == 0 ? var4 : MessageFormat.format(var4, var2);
      } catch (MissingResourceException var5) {
      }

      return var3;
   }

   @Override
   public Collection<ProxiedPlayer> getPlayers() {
      this.connectionLock.readLock().lock();

      Collection var1;
      try {
         var1 = Collections.unmodifiableCollection(new HashSet<>(this.connections.values()));
      } finally {
         this.connectionLock.readLock().unlock();
      }

      return var1;
   }

   public Collection<UserConnection> getUserConnections() {
      this.connectionLock.readLock().lock();

      ArrayList var1;
      try {
         var1 = new ArrayList<>(this.connections.values());
      } finally {
         this.connectionLock.readLock().unlock();
      }

      return var1;
   }

   @Override
   public int getOnlineCount() {
      return this.connections.size();
   }

   @Override
   public ProxiedPlayer getPlayer(String var1) {
      this.connectionLock.readLock().lock();

      ProxiedPlayer var2;
      try {
         var2 = this.connections.get(var1);
      } finally {
         this.connectionLock.readLock().unlock();
      }

      return var2;
   }

   public UserConnection getPlayerByOfflineUUID(UUID var1) {
      this.connectionLock.readLock().lock();

      UserConnection var2;
      try {
         var2 = this.connectionsByOfflineUUID.get(var1);
      } finally {
         this.connectionLock.readLock().unlock();
      }

      return var2;
   }

   public Map<UUID, UserConnection> retainAndGetOfflineUUIDMap() {
      this.connectionLock.readLock().lock();
      return this.connectionsByOfflineUUID;
   }

   public void releaseOfflineUUIDMap() {
      this.connectionLock.readLock().unlock();
   }

   @Override
   public ProxiedPlayer getPlayer(UUID var1) {
      this.connectionLock.readLock().lock();

      ProxiedPlayer var2;
      try {
         var2 = this.connectionsByUUID.get(var1);
      } finally {
         this.connectionLock.readLock().unlock();
      }

      return var2;
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
   public ServerInfo getServerInfo(String var1) {
      return this.config.getServerInfo(var1);
   }

   @Override
   public void registerChannel(String var1) {
      synchronized (this.pluginChannels) {
         this.pluginChannels.add(var1);
      }
   }

   @Override
   public void unregisterChannel(String var1) {
      synchronized (this.pluginChannels) {
         this.pluginChannels.remove(var1);
      }
   }

   @Override
   public Collection<String> getChannels() {
      synchronized (this.pluginChannels) {
         return Collections.unmodifiableCollection(this.pluginChannels);
      }
   }

   public PluginMessage registerChannels(int var1) {
      return var1 >= 393
         ? new PluginMessage(
            "minecraft:register", Util.format(Iterables.transform(this.pluginChannels, PluginMessage.MODERNISE), "\u0000").getBytes(Charsets.UTF_8), false
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
   public ServerInfo constructServerInfo(String var1, InetSocketAddress var2, String var3, boolean var4) {
      return this.constructServerInfo(var1, (SocketAddress)var2, var3, var4);
   }

   @Override
   public ServerInfo constructServerInfo(String var1, SocketAddress var2, String var3, boolean var4) {
      return new BungeeServerInfo(var1, var2, var3, var4);
   }

   @Override
   public CommandSender getConsole() {
      return ConsoleCommandSender.getInstance();
   }

   @Override
   public void broadcast(String var1) {
      this.broadcast(TextComponent.fromLegacyText(var1));
   }

   @Override
   public void broadcast(BaseComponent... var1) {
      this.getConsole().sendMessage(BaseComponent.toLegacyText(var1));

      for (ProxiedPlayer var3 : this.getPlayers()) {
         var3.sendMessage(var1);
      }
   }

   @Override
   public void broadcast(BaseComponent var1) {
      this.getConsole().sendMessage(var1.toLegacyText());

      for (ProxiedPlayer var3 : this.getPlayers()) {
         var3.sendMessage(var1);
      }
   }

   public void addConnection(UserConnection var1) {
      this.connectionLock.writeLock().lock();

      try {
         this.connections.put(var1.getName(), var1);
         this.connectionsByUUID.put(var1.getUniqueId(), var1);
         this.connectionsByOfflineUUID.put(var1.getPendingConnection().getOfflineId(), var1);
      } finally {
         this.connectionLock.writeLock().unlock();
      }
   }

   public void removeConnection(UserConnection var1) {
      this.connectionLock.writeLock().lock();

      try {
         if (this.connections.get(var1.getName()) == var1) {
            this.connections.remove(var1.getName());
            this.connectionsByUUID.remove(var1.getUniqueId());
            this.connectionsByOfflineUUID.remove(var1.getPendingConnection().getOfflineId());
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
   public Collection<ProxiedPlayer> matchPlayer(final String var1) {
      Preconditions.checkNotNull(var1, "partialName");
      ProxiedPlayer var2 = this.getPlayer(var1);
      return (Collection<ProxiedPlayer>)(var2 != null
         ? Collections.singleton(var2)
         : Sets.newHashSet(Iterables.filter(this.getPlayers(), new Predicate<ProxiedPlayer>() {
            public boolean apply(ProxiedPlayer var1x) {
               return var1x == null ? false : var1x.getName().toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT));
            }
         })));
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
   public void setReconnectHandler(ReconnectHandler var1) {
      this.reconnectHandler = var1;
   }

   @Override
   public ConfigurationAdapter getConfigurationAdapter() {
      return this.configurationAdapter;
   }

   @Override
   public void setConfigurationAdapter(ConfigurationAdapter var1) {
      this.configurationAdapter = var1;
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
