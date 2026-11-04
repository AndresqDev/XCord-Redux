package net.md_5.bungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import lombok.NonNull;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.entitymap.EntityMap_Dummy;
import net.md_5.bungee.forge.ForgeClientHandler;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.SystemChat;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.tab.ServerUnique;
import net.md_5.bungee.tab.TabList;
import net.md_5.bungee.util.CaseInsensitiveSet;
import net.md_5.bungee.util.ChatComponentTransformer;
import xcord.DnsResolver;
import xcord.MessageTemplate;
import xcord.SpoofedServerConnection;
import xcord.XCord;

public final class UserConnection implements ProxiedPlayer {
   @NonNull
   private final ProxyServer bungee;
   @NonNull
   private final ChannelWrapper ch;
   @NonNull
   private final String name;
   private final InitialHandler pendingConnection;
   private ServerConnection server;
   private Object dimension;
   private boolean dimensionChange = true;
   private final Collection<ServerInfo> pendingConnects = new HashSet<>();
   private int ping = 100;
   private ServerInfo reconnectServer;
   private TabList tabListHandler;
   private int gamemode;
   private int compressionThreshold = -1;
   private Queue<String> serverJoinQueue;
   private final Collection<String> groups = new CaseInsensitiveSet();
   private final Collection<String> permissions = new CaseInsensitiveSet();
   private int clientEntityId;
   private int serverEntityId;
   private ClientSettings settings;
   private final Scoreboard serverSentScoreboard = new Scoreboard();
   private final Collection<UUID> sentBossBars = new HashSet<>();
   private final Multimap<Integer, Integer> potions = HashMultimap.create();
   private String displayName;
   private EntityMap entityRewrite;
   private Locale locale;
   private ForgeClientHandler forgeClientHandler;
   private ForgeServerHandler forgeServerHandler;
   private volatile boolean joinRequiresRewrite = false;
   private boolean passedThroughSpoofedServer = false;
   private long initializeTime = System.currentTimeMillis();
   private int sentPackets = 0;
   private boolean passedBotChecks = false;
   private boolean whitelistedFromAntibot = false;
   private boolean usingForge = false;
   private final Connection.Unsafe unsafe = new Connection.Unsafe() {
      @Override
      public void sendPacket(DefinedPacket var1) {
         UserConnection.this.ch.write(var1);
      }
   };

   public boolean isPassedBotChecks() {
      return this.passedBotChecks;
   }

   public boolean isWhitelistedFromAntibot() {
      return this.whitelistedFromAntibot;
   }

   public void setWhitelistedFromAntibot(boolean var1) {
      this.whitelistedFromAntibot = var1;
   }

   public void setPassedBotChecks(boolean var1) {
      this.passedBotChecks = var1;
   }

   public int getSentPackets() {
      return this.sentPackets;
   }

   public int incrementSentPackets() {
      return ++this.sentPackets;
   }

   public long getInitializeTime() {
      return this.initializeTime;
   }

   public void setServer(ServerConnection var1) {
      this.server = var1;
      if (var1 != null && !(var1 instanceof SpoofedServerConnection)) {
         this.setPassedThroughSpoofedServer(false);
      }
   }

   public void init() {
      this.initializeTime = System.currentTimeMillis();
      if (this.getPendingConnection().getVersion() < 751 || !this.bungee.getConfig().isIpForward()) {
         this.entityRewrite = EntityMap.getEntityMap(this.getPendingConnection().getVersion());
      }

      this.displayName = this.name;
      this.tabListHandler = new ServerUnique(this);
      Collection var1 = this.bungee.getConfigurationAdapter().getGroups(this.name);
      var1.addAll(this.bungee.getConfigurationAdapter().getGroups(this.getUniqueId().toString()));

      for (String var3 : var1) {
         this.addGroups(var3);
      }

      this.forgeClientHandler = new ForgeClientHandler(this);
      if (this.getPendingConnection().getExtraDataInHandshake().contains("\u0000FML\u0000")) {
         this.forgeClientHandler.setFmlTokenInHandshake(true);
      }
   }

   public void sendPacket(PacketWrapper var1) {
      this.ch.write(var1);
   }

   public void appendBufferedPacket(PacketWrapper var1) {
      this.ch.writeBuffered(var1);
   }

   @Deprecated
   public boolean isActive() {
      return !this.ch.isClosed();
   }

   @Override
   public void setDisplayName(String var1) {
      Preconditions.checkNotNull(var1, "displayName");
      Preconditions.checkArgument(var1.length() <= 16, "Display name cannot be longer than 16 characters");
      this.displayName = var1;
   }

   @Override
   public void connect(ServerInfo var1) {
      this.connect(var1, null, ServerConnectEvent.Reason.PLUGIN);
   }

   @Override
   public void connect(ServerInfo var1, ServerConnectEvent.Reason var2) {
      this.connect(var1, null, false, var2);
   }

   @Override
   public void connect(ServerInfo var1, Callback<Boolean> var2) {
      this.connect(var1, var2, false, ServerConnectEvent.Reason.PLUGIN);
   }

   @Override
   public void connect(ServerInfo var1, Callback<Boolean> var2, ServerConnectEvent.Reason var3) {
      this.connect(var1, var2, false, var3);
   }

   @Deprecated
   public void connectNow(ServerInfo var1) {
      this.connectNow(var1, ServerConnectEvent.Reason.UNKNOWN);
   }

   public void connectNow(ServerInfo var1, ServerConnectEvent.Reason var2) {
      this.dimensionChange = true;
      this.connect(var1, var2);
   }

   public ServerInfo updateAndGetNextServer(ServerInfo var1) {
      if (this.serverJoinQueue == null) {
         this.serverJoinQueue = new LinkedList<>(this.getPendingConnection().getListener().getServerPriority());
      }

      ServerInfo var2 = null;

      while (!this.serverJoinQueue.isEmpty()) {
         ServerInfo var3 = ProxyServer.getInstance().getServerInfo(this.serverJoinQueue.remove());
         if (!Objects.equals(var1, var3)) {
            var2 = var3;
            break;
         }
      }

      return var2;
   }

   @Override
   public void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3) {
      this.connect(var1, var2, var3, ServerConnectEvent.Reason.PLUGIN);
   }

   public void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3, ServerConnectEvent.Reason var4) {
      this.connect(var1, var2, var3, var4, this.bungee.getConfig().getServerConnectTimeout());
   }

   @Override
   public void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3, int var4) {
      this.connect(var1, var2, var3, ServerConnectEvent.Reason.PLUGIN, var4);
   }

   @Override
   public void connect(ServerInfo var1, final Callback<Boolean> var2, boolean var3, ServerConnectEvent.Reason var4, int var5) {
      Preconditions.checkNotNull(var1, "info");
      ServerConnectRequest.Builder var6 = ServerConnectRequest.builder().retry(var3).reason(var4).target(var1);
      var6.connectTimeout(var5);
      if (var2 != null) {
         var6.callback(new Callback<ServerConnectRequest.Result>() {
            public void done(ServerConnectRequest.Result var1, Throwable var2x) {
               var2.done(var1 == ServerConnectRequest.Result.SUCCESS ? Boolean.TRUE : Boolean.FALSE, var2x);
            }
         });
      }

      this.connect(var6.build());
   }

   @Override
   public void connect(final ServerConnectRequest var1) {
      Preconditions.checkNotNull(var1, "request");
      final Callback var2 = var1.getCallback();
      ServerConnectEvent var3 = new ServerConnectEvent(this, var1.getTarget(), var1.getReason(), var1);
      if (this.bungee.getPluginManager().callEvent(var3).isCancelled()) {
         if (var2 != null) {
            var2.done(ServerConnectRequest.Result.EVENT_CANCEL, null);
         }

         if (this.getServer() == null && !this.ch.isClosing()) {
            throw new IllegalStateException("Cancelled ServerConnectEvent with no server or disconnect.");
         }
      } else {
         final BungeeServerInfo var4 = (BungeeServerInfo)var3.getTarget();
         if (this.getServer() != null && Objects.equals(this.getServer().getInfo(), var4)) {
            if (var2 != null) {
               var2.done(ServerConnectRequest.Result.ALREADY_CONNECTED, null);
            }

            this.sendMessage(this.bungee.getTranslation("already_connected"));
         } else if (this.pendingConnects.contains(var4)) {
            if (var2 != null) {
               var2.done(ServerConnectRequest.Result.ALREADY_CONNECTING, null);
            }

            this.sendMessage(this.bungee.getTranslation("already_connecting"));
         } else {
            this.pendingConnects.add(var4);
            ChannelInitializer var5 = new ChannelInitializer() {
               protected void initChannel(Channel var1) throws Exception {
                  PipelineUtils.BASE.initChannel(var1);
                  var1.pipeline()
                     .addAfter(
                        "frame-decoder",
                        "packet-decoder",
                        new MinecraftDecoder(
                           Protocol.HANDSHAKE,
                           false,
                           UserConnection.this.getPendingConnection().getVersion(),
                           XCord.getConfig().getPerformanceOptimizationsDontCopyPackets()
                        )
                     );
                  var1.pipeline()
                     .addAfter(
                        "frame-prepender",
                        "packet-encoder",
                        new MinecraftEncoder(Protocol.HANDSHAKE, false, UserConnection.this.getPendingConnection().getVersion())
                     );
                  ((HandlerBoss)var1.pipeline().get(HandlerBoss.class)).setHandler(new ServerConnector(UserConnection.this.bungee, UserConnection.this, var4));
               }
            };
            ChannelFutureListener var6 = new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (var2 != null) {
                     var2.done(var1x.isSuccess() ? ServerConnectRequest.Result.SUCCESS : ServerConnectRequest.Result.FAIL, var1x.cause());
                  }

                  if (!var1x.isSuccess()) {
                     var1x.channel().close();
                     UserConnection.this.pendingConnects.remove(var4);
                     ServerInfo var2x = UserConnection.this.updateAndGetNextServer(var4);
                     if (!var1.isRetry() || var2x == null || UserConnection.this.getServer() != null && var2x == UserConnection.this.getServer().getInfo()) {
                        if (UserConnection.this.dimensionChange) {
                           UserConnection.this.disconnect(UserConnection.this.bungee.getTranslation("fallback_kick", var1x.cause().getClass().getName()));
                        } else if (UserConnection.this.getServer() != null && !UserConnection.this.isPassedThroughSpoofedServer()) {
                           UserConnection.this.sendMessage(UserConnection.this.bungee.getTranslation("fallback_kick", var1x.cause().getClass().getName()));
                        } else {
                           UserConnection.this.disconnect(UserConnection.this.bungee.getTranslation("fallback_kick", var1x.cause().getClass().getName()));
                        }
                     } else {
                        UserConnection.this.sendMessage(UserConnection.this.bungee.getTranslation("fallback_lobby"));
                        UserConnection.this.connect(var2x, null, true, ServerConnectEvent.Reason.LOBBY_FALLBACK, var1.getConnectTimeout());
                     }
                  }
               }
            };
            io.netty.bootstrap.Bootstrap var7 = ((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)new io.netty.bootstrap.Bootstrap()
                           .channel(PipelineUtils.getChannel(var4.getAddress())))
                        .group(this.ch.getHandle().eventLoop()))
                     .handler(var5))
                  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, var1.getConnectTimeout()))
               .remoteAddress(var4.getAddress());
            if (var4.getAddress().isUnresolved()) {
               var7.resolver(DnsResolver.getResolverGroup());
            }

            if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastOpenOnServers() && PipelineUtils.usingEpoll()) {
               var7.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
            }

            if (this.getPendingConnection().getListener().isSetLocalAddress()
               && !PlatformDependent.isWindows()
               && this.getPendingConnection().getListener().getSocketAddress() instanceof InetSocketAddress) {
               var7.localAddress(this.getPendingConnection().getListener().getHost().getHostString(), 0);
            }

            var7.connect().addListener(var6);
         }
      }
   }

   @Override
   public void disconnect(String var1) {
      this.disconnect0(TextComponent.fromLegacyText(var1));
   }

   @Override
   public void disconnect(BaseComponent... var1) {
      this.disconnect0(var1);
   }

   @Override
   public void disconnect(BaseComponent var1) {
      this.disconnect0(var1);
   }

   public void disconnect0(BaseComponent... var1) {
      if (!this.ch.isClosing()) {
         this.bungee.getLogger().log(Level.INFO, "[{0}] disconnected with: {1}", new Object[]{this.getName(), BaseComponent.toLegacyText(var1)});
         this.ch.close(new Kick(ComponentSerializer.toString(var1)));
         if (this.server != null) {
            this.server.setObsolete(true);
            this.server.disconnect("Quitting");
         }
      }
   }

   public void disconnect(MessageTemplate var1) {
      if (!this.ch.isClosing()) {
         this.bungee.getLogger().log(Level.INFO, "[{0}] disconnected with: {1}", new Object[]{this.getName(), var1.getLegacyText()});
         if (this.getPendingConnection() != null && this.getPendingConnection().getVersion() < 47) {
            this.ch.close(new Kick(var1.getLegacyText()));
         } else {
            this.ch.close(new Kick(var1.getJson()));
         }

         if (this.server != null) {
            this.server.setObsolete(true);
            this.server.disconnect("Quitting");
         }
      }
   }

   public void forceClose() {
      if (!this.ch.isClosed()) {
         if (this.server != null) {
            this.server.setObsolete(true);
            this.server.forceDisconnect();
         }

         this.ch.forceClose();
      }
   }

   @Override
   public void chat(String var1) {
      Preconditions.checkState(this.server != null, "Not connected to server");
      if (this.getPendingConnection().getVersion() >= 759) {
         throw new UnsupportedOperationException("Cannot spoof chat on this client version!");
      } else {
         this.server.getCh().write(new Chat(var1));
      }
   }

   @Override
   public void sendMessage(String var1) {
      this.sendMessage(TextComponent.fromLegacyText(var1));
   }

   @Override
   public void sendMessages(String... var1) {
      for (String var5 : var1) {
         this.sendMessage(var5);
      }
   }

   @Override
   public void sendMessage(BaseComponent... var1) {
      this.sendMessage(ChatMessageType.SYSTEM, var1);
   }

   @Override
   public void sendMessage(BaseComponent var1) {
      this.sendMessage(ChatMessageType.SYSTEM, var1);
   }

   @Override
   public void sendMessage(ChatMessageType var1, BaseComponent... var2) {
      this.sendMessage(var1, null, var2);
   }

   @Override
   public void sendMessage(ChatMessageType var1, BaseComponent var2) {
      this.sendMessage(var1, (UUID)null, var2);
   }

   @Override
   public void sendMessage(UUID var1, BaseComponent... var2) {
      this.sendMessage(ChatMessageType.CHAT, var1, var2);
   }

   @Override
   public void sendMessage(UUID var1, BaseComponent var2) {
      this.sendMessage(ChatMessageType.CHAT, var1, var2);
   }

   private void sendMessage(ChatMessageType var1, UUID var2, String var3) {
      if (this.getPendingConnection().getVersion() >= 759) {
         if (var1 == ChatMessageType.CHAT) {
            var1 = ChatMessageType.SYSTEM;
         }

         this.unsafe().sendPacket(new SystemChat(var3, var1.ordinal()));
      } else {
         this.unsafe().sendPacket(new Chat(var3, (byte)var1.ordinal(), var2));
      }
   }

   private void sendMessage(ChatMessageType var1, UUID var2, BaseComponent... var3) {
      var3 = ChatComponentTransformer.getInstance().transform(this, true, var3);
      if (var1 != ChatMessageType.ACTION_BAR || this.getPendingConnection().getVersion() < 47 || this.getPendingConnection().getVersion() >= 755) {
         this.sendMessage(var1, var2, ComponentSerializer.toString(var3));
      } else if (this.getPendingConnection().getVersion() <= 210) {
         this.sendMessage(var1, var2, ComponentSerializer.toString((BaseComponent)(new TextComponent(BaseComponent.toLegacyText(var3)))));
      } else {
         Title var4 = new Title();
         var4.setAction(Title.Action.ACTIONBAR);
         var4.setText(ComponentSerializer.toString(var3));
         this.unsafe.sendPacket(var4);
      }
   }

   @Override
   public void sendData(String var1, byte[] var2) {
      this.unsafe().sendPacket(new PluginMessage(var1, var2, this.forgeClientHandler.isForgeUser()));
   }

   @Override
   public InetSocketAddress getAddress() {
      return (InetSocketAddress)this.getSocketAddress();
   }

   @Override
   public SocketAddress getSocketAddress() {
      return this.ch.getRemoteAddress();
   }

   @Override
   public Collection<String> getGroups() {
      return Collections.unmodifiableCollection(this.groups);
   }

   @Override
   public void addGroups(String... var1) {
      for (String var5 : var1) {
         this.groups.add(var5);

         for (String var7 : this.bungee.getConfigurationAdapter().getPermissions(var5)) {
            this.setPermission(var7, true);
         }
      }
   }

   @Override
   public void removeGroups(String... var1) {
      for (String var5 : var1) {
         this.groups.remove(var5);

         for (String var7 : this.bungee.getConfigurationAdapter().getPermissions(var5)) {
            this.setPermission(var7, false);
         }
      }
   }

   @Override
   public boolean hasPermission(String var1) {
      return this.bungee.getPluginManager().callEvent(new PermissionCheckEvent(this, var1, this.permissions.contains(var1))).hasPermission();
   }

   @Override
   public void setPermission(String var1, boolean var2) {
      if (var2) {
         this.permissions.add(var1);
      } else {
         this.permissions.remove(var1);
      }
   }

   @Override
   public Collection<String> getPermissions() {
      return Collections.unmodifiableCollection(this.permissions);
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public Connection.Unsafe unsafe() {
      return this.unsafe;
   }

   @Override
   public String getUUID() {
      return this.getPendingConnection().getUUID();
   }

   @Override
   public UUID getUniqueId() {
      return this.getPendingConnection().getUniqueId();
   }

   public void setSettings(ClientSettings var1) {
      this.settings = var1;
      this.locale = null;
   }

   @Override
   public Locale getLocale() {
      return this.locale == null && this.settings != null ? (this.locale = Locale.forLanguageTag(this.settings.getLocale().replace('_', '-'))) : this.locale;
   }

   @Override
   public byte getViewDistance() {
      return this.settings != null ? this.settings.getViewDistance() : 10;
   }

   @Override
   public ProxiedPlayer.ChatMode getChatMode() {
      if (this.settings == null) {
         return ProxiedPlayer.ChatMode.SHOWN;
      } else {
         switch (this.settings.getChatFlags()) {
            case 0:
            default:
               return ProxiedPlayer.ChatMode.SHOWN;
            case 1:
               return ProxiedPlayer.ChatMode.COMMANDS_ONLY;
            case 2:
               return ProxiedPlayer.ChatMode.HIDDEN;
         }
      }
   }

   @Override
   public boolean hasChatColors() {
      return this.settings == null || this.settings.isChatColours();
   }

   @Override
   public SkinConfiguration getSkinParts() {
      return (SkinConfiguration)(this.settings != null ? new PlayerSkinConfiguration(this.settings.getSkinParts()) : PlayerSkinConfiguration.SKIN_SHOW_ALL);
   }

   @Override
   public ProxiedPlayer.MainHand getMainHand() {
      return this.settings != null && this.settings.getMainHand() != 1 ? ProxiedPlayer.MainHand.LEFT : ProxiedPlayer.MainHand.RIGHT;
   }

   @Override
   public boolean isForgeUser() {
      return this.forgeClientHandler.isForgeUser();
   }

   @Override
   public Map<String, String> getModList() {
      return this.forgeClientHandler.getClientModList() == null ? ImmutableMap.of() : ImmutableMap.copyOf(this.forgeClientHandler.getClientModList());
   }

   @Override
   public void setTabHeader(BaseComponent var1, BaseComponent var2) {
      if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
         var1 = ChatComponentTransformer.getInstance().transform(this, true, var1)[0];
         var2 = ChatComponentTransformer.getInstance().transform(this, true, var2)[0];
         this.unsafe().sendPacket(new PlayerListHeaderFooter(ComponentSerializer.toString(var1), ComponentSerializer.toString(var2)));
      }
   }

   @Override
   public void setTabHeader(BaseComponent[] var1, BaseComponent[] var2) {
      if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
         var1 = ChatComponentTransformer.getInstance().transform(this, true, var1);
         var2 = ChatComponentTransformer.getInstance().transform(this, true, var2);
         this.unsafe().sendPacket(new PlayerListHeaderFooter(ComponentSerializer.toString(var1), ComponentSerializer.toString(var2)));
      }
   }

   @Override
   public void resetTabHeader() {
      this.setTabHeader((BaseComponent)null, null);
   }

   @Override
   public void sendTitle(net.md_5.bungee.api.Title var1) {
      var1.send(this);
   }

   public String getExtraDataInHandshake() {
      return this.getPendingConnection().getExtraDataInHandshake();
   }

   public void setCompressionThreshold(int var1) {
      if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
         if (!this.ch.isClosing() && this.compressionThreshold == -1 && var1 >= 0) {
            this.compressionThreshold = var1;
            this.unsafe.sendPacket(new SetCompression(var1));
            this.ch.setCompressionThreshold(var1);
         }
      }
   }

   @Override
   public boolean isConnected() {
      return !this.ch.isClosed();
   }

   @Override
   public Scoreboard getScoreboard() {
      return this.serverSentScoreboard;
   }

   public boolean isDisableEntityMetadataRewrite() {
      return this.entityRewrite == EntityMap_Dummy.INSTANCE;
   }

   public boolean isUsingForge() {
      return this.usingForge;
   }

   public void setUsingForge(boolean var1) {
      this.usingForge = var1;
   }

   public void flush() {
      if (!this.ch.isClosed()) {
         this.ch.flush();
      }
   }

   public UserConnection(@NonNull ProxyServer var1, @NonNull ChannelWrapper var2, @NonNull String var3, InitialHandler var4) {
      if (var1 == null) {
         throw new NullPointerException("bungee is marked non-null but is null");
      } else if (var2 == null) {
         throw new NullPointerException("ch is marked non-null but is null");
      } else if (var3 == null) {
         throw new NullPointerException("name is marked non-null but is null");
      } else {
         this.bungee = var1;
         this.ch = var2;
         this.name = var3;
         this.pendingConnection = var4;
      }
   }

   @NonNull
   public ChannelWrapper getCh() {
      return this.ch;
   }

   @NonNull
   @Override
   public String getName() {
      return this.name;
   }

   public InitialHandler getPendingConnection() {
      return this.pendingConnection;
   }

   public ServerConnection getServer() {
      return this.server;
   }

   public Object getDimension() {
      return this.dimension;
   }

   public void setDimension(Object var1) {
      this.dimension = var1;
   }

   public boolean isDimensionChange() {
      return this.dimensionChange;
   }

   public void setDimensionChange(boolean var1) {
      this.dimensionChange = var1;
   }

   public Collection<ServerInfo> getPendingConnects() {
      return this.pendingConnects;
   }

   @Override
   public int getPing() {
      return this.ping;
   }

   public void setPing(int var1) {
      this.ping = var1;
   }

   @Override
   public ServerInfo getReconnectServer() {
      return this.reconnectServer;
   }

   @Override
   public void setReconnectServer(ServerInfo var1) {
      this.reconnectServer = var1;
   }

   public TabList getTabListHandler() {
      return this.tabListHandler;
   }

   public int getGamemode() {
      return this.gamemode;
   }

   public void setGamemode(int var1) {
      this.gamemode = var1;
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }

   public void setServerJoinQueue(Queue<String> var1) {
      this.serverJoinQueue = var1;
   }

   public int getClientEntityId() {
      return this.clientEntityId;
   }

   public void setClientEntityId(int var1) {
      this.clientEntityId = var1;
   }

   public int getServerEntityId() {
      return this.serverEntityId;
   }

   public void setServerEntityId(int var1) {
      this.serverEntityId = var1;
   }

   public ClientSettings getSettings() {
      return this.settings;
   }

   public Scoreboard getServerSentScoreboard() {
      return this.serverSentScoreboard;
   }

   public Collection<UUID> getSentBossBars() {
      return this.sentBossBars;
   }

   public Multimap<Integer, Integer> getPotions() {
      return this.potions;
   }

   @Override
   public String getDisplayName() {
      return this.displayName;
   }

   public EntityMap getEntityRewrite() {
      return this.entityRewrite;
   }

   public void setEntityRewrite(EntityMap var1) {
      this.entityRewrite = var1;
   }

   public ForgeClientHandler getForgeClientHandler() {
      return this.forgeClientHandler;
   }

   public void setForgeClientHandler(ForgeClientHandler var1) {
      this.forgeClientHandler = var1;
   }

   public ForgeServerHandler getForgeServerHandler() {
      return this.forgeServerHandler;
   }

   public void setForgeServerHandler(ForgeServerHandler var1) {
      this.forgeServerHandler = var1;
   }

   public boolean isJoinRequiresRewrite() {
      return this.joinRequiresRewrite;
   }

   public void setJoinRequiresRewrite(boolean var1) {
      this.joinRequiresRewrite = var1;
   }

   public boolean isPassedThroughSpoofedServer() {
      return this.passedThroughSpoofedServer;
   }

   public void setPassedThroughSpoofedServer(boolean var1) {
      this.passedThroughSpoofedServer = var1;
   }
}
