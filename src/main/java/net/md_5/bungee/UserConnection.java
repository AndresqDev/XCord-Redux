package net.md_5.bungee;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import io.netty.bootstrap.Bootstrap;
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
import xcord.XCord;
import xcord.message.MessageTemplate;
import xcord.network.DnsResolver;
import xcord.network.SpoofedServerConnection;

public final class UserConnection implements ProxiedPlayer {
    private final ProxyServer bungee;
    private final ChannelWrapper ch;
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
        public void sendPacket(DefinedPacket packet) {
            UserConnection.this.ch.write(packet);
        }
    };

    public boolean isPassedBotChecks() {
        return this.passedBotChecks;
    }

    public boolean isWhitelistedFromAntibot() {
        return this.whitelistedFromAntibot;
    }

    public void setWhitelistedFromAntibot(boolean whitelistedFromAntibot) {
        this.whitelistedFromAntibot = whitelistedFromAntibot;
    }

    public void setPassedBotChecks(boolean passedBotChecks) {
        this.passedBotChecks = passedBotChecks;
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

    public void setServer(ServerConnection server) {
        this.server = server;
        if (server != null && !(server instanceof SpoofedServerConnection)) {
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
        Collection<String> configuredGroups = this.bungee.getConfigurationAdapter().getGroups(this.name);
        configuredGroups.addAll(this.bungee.getConfigurationAdapter().getGroups(this.getUniqueId().toString()));

        for (String group : configuredGroups) {
            this.addGroups(group);
        }

        this.forgeClientHandler = new ForgeClientHandler(this);
        if (this.getPendingConnection().getExtraDataInHandshake().contains("\u0000FML\u0000")) {
            this.forgeClientHandler.setFmlTokenInHandshake(true);
        }
    }

    public void sendPacket(PacketWrapper packet) {
        this.ch.write(packet);
    }

    public void appendBufferedPacket(PacketWrapper packet) {
        this.ch.writeBuffered(packet);
    }

    @Deprecated
    public boolean isActive() {
        return !this.ch.isClosed();
    }

    @Override
    public void setDisplayName(String displayName) {
        Preconditions.checkNotNull(displayName, "displayName");
        Preconditions.checkArgument(displayName.length() <= 16, "Display name cannot be longer than 16 characters");
        this.displayName = displayName;
    }

    @Override
    public void connect(ServerInfo info) {
        this.connect(info, null, ServerConnectEvent.Reason.PLUGIN);
    }

    @Override
    public void connect(ServerInfo info, ServerConnectEvent.Reason reason) {
        this.connect(info, null, false, reason);
    }

    @Override
    public void connect(ServerInfo info, Callback<Boolean> callback) {
        this.connect(info, callback, false, ServerConnectEvent.Reason.PLUGIN);
    }

    @Override
    public void connect(ServerInfo info, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        this.connect(info, callback, false, reason);
    }

    @Deprecated
    public void connectNow(ServerInfo info) {
        this.connectNow(info, ServerConnectEvent.Reason.UNKNOWN);
    }

    public void connectNow(ServerInfo info, ServerConnectEvent.Reason reason) {
        this.dimensionChange = true;
        this.connect(info, reason);
    }

    public ServerInfo updateAndGetNextServer(ServerInfo excludedServer) {
        if (this.serverJoinQueue == null) {
            this.serverJoinQueue = new LinkedList<>(this.getPendingConnection().getListener().getServerPriority());
        }

        ServerInfo nextServer = null;

        while (!this.serverJoinQueue.isEmpty()) {
            ServerInfo candidate = ProxyServer.getInstance().getServerInfo(this.serverJoinQueue.remove());
            if (!Objects.equals(excludedServer, candidate)) {
                nextServer = candidate;
                break;
            }
        }

        return nextServer;
    }

    @Override
    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry) {
        this.connect(info, callback, retry, ServerConnectEvent.Reason.PLUGIN);
    }

    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, ServerConnectEvent.Reason reason) {
        this.connect(info, callback, retry, reason, this.bungee.getConfig().getServerConnectTimeout());
    }

    @Override
    public void connect(ServerInfo info, Callback<Boolean> callback, boolean retry, int connectTimeout) {
        this.connect(info, callback, retry, ServerConnectEvent.Reason.PLUGIN, connectTimeout);
    }

    @Override
    public void connect(
            ServerInfo info,
            final Callback<Boolean> callback,
            boolean retry,
            ServerConnectEvent.Reason reason,
            int connectTimeout
    ) {
        Preconditions.checkNotNull(info, "info");
        ServerConnectRequest.Builder builder = ServerConnectRequest.builder().retry(retry).reason(reason).target(info);
        builder.connectTimeout(connectTimeout);
        if (callback != null) {
            builder.callback(new Callback<ServerConnectRequest.Result>() {
                public void done(ServerConnectRequest.Result result, Throwable error) {
                    callback.done(result == ServerConnectRequest.Result.SUCCESS ? Boolean.TRUE : Boolean.FALSE, error);
                }
            });
        }

        this.connect(builder.build());
    }

    @Override
    public void connect(final ServerConnectRequest request) {
        Preconditions.checkNotNull(request, "request");
        final Callback<ServerConnectRequest.Result> callback = request.getCallback();
        ServerConnectEvent connectEvent = new ServerConnectEvent(this, request.getTarget(), request.getReason(), request);
        if (this.bungee.getPluginManager().callEvent(connectEvent).isCancelled()) {
            if (callback != null) {
                callback.done(ServerConnectRequest.Result.EVENT_CANCEL, null);
            }

            if (this.getServer() == null && !this.ch.isClosing()) {
                throw new IllegalStateException("Cancelled ServerConnectEvent with no server or disconnect.");
            }

            return;
        }

        final BungeeServerInfo target = (BungeeServerInfo) connectEvent.getTarget();
        if (this.getServer() != null && Objects.equals(this.getServer().getInfo(), target)) {
            if (callback != null) {
                callback.done(ServerConnectRequest.Result.ALREADY_CONNECTED, null);
            }

            this.sendMessage(this.bungee.getTranslation("already_connected"));
            return;
        }

        if (this.pendingConnects.contains(target)) {
            if (callback != null) {
                callback.done(ServerConnectRequest.Result.ALREADY_CONNECTING, null);
            }

            this.sendMessage(this.bungee.getTranslation("already_connecting"));
            return;
        }

        this.pendingConnects.add(target);
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) throws Exception {
                PipelineUtils.BASE.initChannel(channel);
                channel.pipeline()
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
                channel.pipeline()
                        .addAfter(
                                "frame-prepender",
                                "packet-encoder",
                                new MinecraftEncoder(Protocol.HANDSHAKE, false, UserConnection.this.getPendingConnection().getVersion())
                        );
                channel.pipeline().get(HandlerBoss.class)
                        .setHandler(new ServerConnector(UserConnection.this.bungee, UserConnection.this, target));
            }
        };
        ChannelFutureListener connectListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (callback != null) {
                    callback.done(future.isSuccess() ? ServerConnectRequest.Result.SUCCESS : ServerConnectRequest.Result.FAIL, future.cause());
                }

                if (!future.isSuccess()) {
                    future.channel().close();
                    UserConnection.this.pendingConnects.remove(target);
                    ServerInfo nextServer = UserConnection.this.updateAndGetNextServer(target);
                    if (!request.isRetry()
                            || nextServer == null
                            || UserConnection.this.getServer() != null && nextServer == UserConnection.this.getServer().getInfo()) {
                        if (UserConnection.this.dimensionChange) {
                            UserConnection.this.disconnect(
                                    UserConnection.this.bungee.getTranslation("fallback_kick", future.cause().getClass().getName())
                            );
                        } else if (UserConnection.this.getServer() != null && !UserConnection.this.isPassedThroughSpoofedServer()) {
                            UserConnection.this.sendMessage(
                                    UserConnection.this.bungee.getTranslation("fallback_kick", future.cause().getClass().getName())
                            );
                        } else {
                            UserConnection.this.disconnect(
                                    UserConnection.this.bungee.getTranslation("fallback_kick", future.cause().getClass().getName())
                            );
                        }
                    } else {
                        UserConnection.this.sendMessage(UserConnection.this.bungee.getTranslation("fallback_lobby"));
                        UserConnection.this.connect(
                                nextServer, null, true, ServerConnectEvent.Reason.LOBBY_FALLBACK, request.getConnectTimeout()
                        );
                    }
                }
            }
        };
        Bootstrap bootstrap = new Bootstrap()
                .channel(PipelineUtils.getChannel(target.getAddress()))
                .group(this.ch.getHandle().eventLoop())
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, request.getConnectTimeout())
                .remoteAddress(target.getAddress());
        if (target.getAddress().isUnresolved()) {
            bootstrap.resolver(DnsResolver.getResolverGroup());
        }

        if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastOpenOnServers() && PipelineUtils.usingEpoll()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        if (this.getPendingConnection().getListener().isSetLocalAddress()
                && !PlatformDependent.isWindows()
                && this.getPendingConnection().getListener().getSocketAddress() instanceof InetSocketAddress) {
            bootstrap.localAddress(this.getPendingConnection().getListener().getHost().getHostString(), 0);
        }

        bootstrap.connect().addListener(connectListener);
    }

    @Override
    public void disconnect(String reason) {
        this.disconnect0(TextComponent.fromLegacyText(reason));
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        this.disconnect0(reason);
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.disconnect0(reason);
    }

    public void disconnect0(BaseComponent... reason) {
        if (!this.ch.isClosing()) {
            this.bungee.getLogger().log(Level.INFO, "[{0}] disconnected with: {1}", new Object[]{this.getName(), BaseComponent.toLegacyText(reason)});
            this.ch.close(new Kick(ComponentSerializer.toString(reason)));
            if (this.server != null) {
                this.server.setObsolete(true);
                this.server.disconnect("Quitting");
            }
        }
    }

    public void disconnect(MessageTemplate reason) {
        if (!this.ch.isClosing()) {
            this.bungee.getLogger().log(Level.INFO, "[{0}] disconnected with: {1}", new Object[]{this.getName(), reason.getLegacyText()});
            if (this.getPendingConnection() != null && this.getPendingConnection().getVersion() < 47) {
                this.ch.close(new Kick(reason.getLegacyText()));
            } else {
                this.ch.close(new Kick(reason.getJson()));
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
    public void chat(String message) {
        Preconditions.checkState(this.server != null, "Not connected to server");
        if (this.getPendingConnection().getVersion() >= 759) {
            throw new UnsupportedOperationException("Cannot spoof chat on this client version!");
        } else {
            this.server.getCh().write(new Chat(message));
        }
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        this.sendMessage(ChatMessageType.SYSTEM, message);
    }

    @Override
    public void sendMessage(BaseComponent message) {
        this.sendMessage(ChatMessageType.SYSTEM, message);
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent... message) {
        this.sendMessage(position, null, message);
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent message) {
        this.sendMessage(position, (UUID) null, message);
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent... message) {
        this.sendMessage(ChatMessageType.CHAT, sender, message);
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent message) {
        this.sendMessage(ChatMessageType.CHAT, sender, message);
    }

    private void sendMessage(ChatMessageType position, UUID sender, String message) {
        if (this.getPendingConnection().getVersion() >= 759) {
            if (position == ChatMessageType.CHAT) {
                position = ChatMessageType.SYSTEM;
            }

            this.unsafe().sendPacket(new SystemChat(message, position.ordinal()));
        } else {
            this.unsafe().sendPacket(new Chat(message, (byte) position.ordinal(), sender));
        }
    }

    private void sendMessage(ChatMessageType position, UUID sender, BaseComponent... message) {
        message = ChatComponentTransformer.getInstance().transform(this, true, message);
        if (position != ChatMessageType.ACTION_BAR
                || this.getPendingConnection().getVersion() < 47
                || this.getPendingConnection().getVersion() >= 755) {
            this.sendMessage(position, sender, ComponentSerializer.toString(message));
        } else if (this.getPendingConnection().getVersion() <= 210) {
            this.sendMessage(
                    position, sender, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message)))
            );
        } else {
            Title title = new Title();
            title.setAction(Title.Action.ACTIONBAR);
            title.setText(ComponentSerializer.toString(message));
            this.unsafe.sendPacket(title);
        }
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.unsafe().sendPacket(new PluginMessage(channel, data, this.forgeClientHandler.isForgeUser()));
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) this.getSocketAddress();
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
    public void addGroups(String... groups) {
        for (String group : groups) {
            this.groups.add(group);

            for (String permission : this.bungee.getConfigurationAdapter().getPermissions(group)) {
                this.setPermission(permission, true);
            }
        }
    }

    @Override
    public void removeGroups(String... groups) {
        for (String group : groups) {
            this.groups.remove(group);

            for (String permission : this.bungee.getConfigurationAdapter().getPermissions(group)) {
                this.setPermission(permission, false);
            }
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.bungee.getPluginManager()
                .callEvent(new PermissionCheckEvent(this, permission, this.permissions.contains(permission)))
                .hasPermission();
    }

    @Override
    public void setPermission(String permission, boolean value) {
        if (value) {
            this.permissions.add(permission);
        } else {
            this.permissions.remove(permission);
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

    public void setSettings(ClientSettings settings) {
        this.settings = settings;
        this.locale = null;
    }

    @Override
    public Locale getLocale() {
        return this.locale == null && this.settings != null
                ? this.locale = Locale.forLanguageTag(this.settings.getLocale().replace('_', '-'))
                : this.locale;
    }

    @Override
    public byte getViewDistance() {
        return this.settings != null ? this.settings.getViewDistance() : 10;
    }

    @Override
    public ProxiedPlayer.ChatMode getChatMode() {
        if (this.settings == null) {
            return ProxiedPlayer.ChatMode.SHOWN;
        }

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

    @Override
    public boolean hasChatColors() {
        return this.settings == null || this.settings.isChatColours();
    }

    @Override
    public SkinConfiguration getSkinParts() {
        return this.settings != null ? new PlayerSkinConfiguration(this.settings.getSkinParts()) : PlayerSkinConfiguration.SKIN_SHOW_ALL;
    }

    @Override
    public ProxiedPlayer.MainHand getMainHand() {
        return this.settings != null && this.settings.getMainHand() != 1
                ? ProxiedPlayer.MainHand.LEFT
                : ProxiedPlayer.MainHand.RIGHT;
    }

    @Override
    public boolean isForgeUser() {
        return this.forgeClientHandler.isForgeUser();
    }

    @Override
    public Map<String, String> getModList() {
        return this.forgeClientHandler.getClientModList() == null
                ? ImmutableMap.of()
                : ImmutableMap.copyOf(this.forgeClientHandler.getClientModList());
    }

    @Override
    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
            header = ChatComponentTransformer.getInstance().transform(this, true, header)[0];
            footer = ChatComponentTransformer.getInstance().transform(this, true, footer)[0];
            this.unsafe().sendPacket(new PlayerListHeaderFooter(ComponentSerializer.toString(header), ComponentSerializer.toString(footer)));
        }
    }

    @Override
    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
            header = ChatComponentTransformer.getInstance().transform(this, true, header);
            footer = ChatComponentTransformer.getInstance().transform(this, true, footer);
            this.unsafe().sendPacket(new PlayerListHeaderFooter(ComponentSerializer.toString(header), ComponentSerializer.toString(footer)));
        }
    }

    @Override
    public void resetTabHeader() {
        this.setTabHeader((BaseComponent) null, null);
    }

    @Override
    public void sendTitle(net.md_5.bungee.api.Title title) {
        title.send(this);
    }

    public String getExtraDataInHandshake() {
        return this.getPendingConnection().getExtraDataInHandshake();
    }

    public void setCompressionThreshold(int compressionThreshold) {
        if (!ProtocolConstants.isBeforeOrEq(this.pendingConnection.getVersion(), 5)) {
            if (!this.ch.isClosing() && this.compressionThreshold == -1 && compressionThreshold >= 0) {
                this.compressionThreshold = compressionThreshold;
                this.unsafe.sendPacket(new SetCompression(compressionThreshold));
                this.ch.setCompressionThreshold(compressionThreshold);
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

    public void setUsingForge(boolean usingForge) {
        this.usingForge = usingForge;
    }

    public void flush() {
        if (!this.ch.isClosed()) {
            this.ch.flush();
        }
    }

    public UserConnection(ProxyServer bungee, ChannelWrapper ch, String name, InitialHandler pendingConnection) {
        if (bungee == null) {
            throw new NullPointerException("bungee is marked non-null but is null");
        } else if (ch == null) {
            throw new NullPointerException("ch is marked non-null but is null");
        } else if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            this.bungee = bungee;
            this.ch = ch;
            this.name = name;
            this.pendingConnection = pendingConnection;
        }
    }

    public ChannelWrapper getCh() {
        return this.ch;
    }

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

    public void setDimension(Object dimension) {
        this.dimension = dimension;
    }

    public boolean isDimensionChange() {
        return this.dimensionChange;
    }

    public void setDimensionChange(boolean dimensionChange) {
        this.dimensionChange = dimensionChange;
    }

    public Collection<ServerInfo> getPendingConnects() {
        return this.pendingConnects;
    }

    @Override
    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    @Override
    public ServerInfo getReconnectServer() {
        return this.reconnectServer;
    }

    @Override
    public void setReconnectServer(ServerInfo reconnectServer) {
        this.reconnectServer = reconnectServer;
    }

    public TabList getTabListHandler() {
        return this.tabListHandler;
    }

    public int getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setServerJoinQueue(Queue<String> serverJoinQueue) {
        this.serverJoinQueue = serverJoinQueue;
    }

    public int getClientEntityId() {
        return this.clientEntityId;
    }

    public void setClientEntityId(int clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public int getServerEntityId() {
        return this.serverEntityId;
    }

    public void setServerEntityId(int serverEntityId) {
        this.serverEntityId = serverEntityId;
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

    public void setEntityRewrite(EntityMap entityRewrite) {
        this.entityRewrite = entityRewrite;
    }

    public ForgeClientHandler getForgeClientHandler() {
        return this.forgeClientHandler;
    }

    public void setForgeClientHandler(ForgeClientHandler forgeClientHandler) {
        this.forgeClientHandler = forgeClientHandler;
    }

    public ForgeServerHandler getForgeServerHandler() {
        return this.forgeServerHandler;
    }

    public void setForgeServerHandler(ForgeServerHandler forgeServerHandler) {
        this.forgeServerHandler = forgeServerHandler;
    }

    public boolean isJoinRequiresRewrite() {
        return this.joinRequiresRewrite;
    }

    public void setJoinRequiresRewrite(boolean joinRequiresRewrite) {
        this.joinRequiresRewrite = joinRequiresRewrite;
    }

    public boolean isPassedThroughSpoofedServer() {
        return this.passedThroughSpoofedServer;
    }

    public void setPassedThroughSpoofedServer(boolean passedThroughSpoofedServer) {
        this.passedThroughSpoofedServer = passedThroughSpoofedServer;
    }
}
