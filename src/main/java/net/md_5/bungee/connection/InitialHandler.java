package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.waterfallmc.waterfall.utils.UUIDUtils;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.EpollTcpInfo;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.cipher.CipherDecoder;
import net.md_5.bungee.cipher.CipherEncoder;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;
import xcord.XCord;
import xcord.antibot.BotCheckHandler;
import xcord.antibot.BotHandler;
import xcord.antibot.CheckType;
import xcord.antibot.ConnectionStats;
import xcord.antibot.LegacyBotHandler;
import xcord.antibot.NameValidator;
import xcord.antibot.SimilarityCache.Match;
import xcord.antivpn.ProxyCheckJob;
import xcord.config.XCordConfig;
import xcord.message.MessageTemplate;
import xcord.network.AntiDdosFrameDecoder;
import xcord.network.ExploitMonitor;
import xcord.network.SpoofedServerConnection;
import xcord.protocol.VarIntFrameDecoder;
import xcord.service.DelayedJob;
import xcord.util.SilentException;

public class InitialHandler extends PacketHandler implements PendingConnection {
    private static final Random RANDOM = new Random();
    private final BungeeCord bungee;
    private ChannelWrapper ch;
    private final ListenerInfo listener;
    private Handshake handshake;
    private LoginRequest loginRequest;
    private EncryptionRequest request;
    private final Set<String> registeredChannels = new HashSet<>();
    private PluginMessage brandMessage;
    private InitialHandler.State thisState = InitialHandler.State.HANDSHAKE;
    private final Connection.Unsafe unsafe = new Connection.Unsafe() {
        @Override
        public void sendPacket(DefinedPacket packet) {
            InitialHandler.this.ch.write(packet);
        }
    };
    private boolean onlineMode = BungeeCord.getInstance().config.isOnlineMode();
    private InetSocketAddress virtualHost;
    private String name;
    private UUID uniqueId;
    private UUID offlineId;
    private LoginResult loginProfile;
    private boolean legacy;
    private String extraDataInHandshake = "";
    private BaseComponent[] delayedKickReason = null;
    private final long connectionTimestamp = System.currentTimeMillis();
    private int latency = -1;
    private boolean forge = false;
    private boolean passthroughThenKick = false;
    private transient long lastPreLegacyPingTime = -1L;
    private transient String preLegacyPingCachedMessage = null;
    private transient String prePreLegacyPingCachedMessage = null;
    private static volatile long cachedPingAge = -1L;
    private static volatile long cachedLegacyPingAge = -1L;
    private static volatile StatusResponse cachedStatusResponse = null;
    private static volatile StatusResponse cachedLegacyStatusResponse = null;
    private static final boolean ACCEPT_INVALID_PACKETS = Boolean.parseBoolean(System.getProperty("waterfall.acceptInvalidPackets", "false"));
    private static final String MOJANG_AUTH_URL = System.getProperty(
            "waterfall.auth.url", "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s%s"
    );

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return !this.ch.isClosing();
    }

    public boolean isPassthroughThenKick() {
        return this.passthroughThenKick;
    }

    private boolean canSendKickMessage() {
        return this.thisState == InitialHandler.State.USERNAME
                || this.thisState == InitialHandler.State.ENCRYPT
                || this.thisState == InitialHandler.State.FINISHED;
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
    }

    public void relayMessage(PluginMessage pluginMessage) throws Exception {
        if (!pluginMessage.getTag().equals("REGISTER") && !pluginMessage.getTag().equals("minecraft:register")) {
            if (pluginMessage.getTag().equals("UNREGISTER") || pluginMessage.getTag().equals("minecraft:unregister")) {
                String channels = new String(pluginMessage.getData(), StandardCharsets.UTF_8);

                for (String channel : channels.split("\u0000")) {
                    this.registeredChannels.remove(channel);
                }
            } else if (pluginMessage.getTag().equals("MC|Brand") || pluginMessage.getTag().equals("minecraft:brand")) {
                this.brandMessage = pluginMessage;
            }
        } else {
            String channels = new String(pluginMessage.getData(), StandardCharsets.UTF_8);

            for (String channel : channels.split("\u0000")) {
                Preconditions.checkState(
                        this.registeredChannels.size() < XCord.getConfig().getCompatibilityPayloadChannelLimit(),
                        "Too many registered channels"
                );
                Preconditions.checkArgument(channel.length() < 128, "Channel name too long");
                this.registeredChannels.add(channel);
            }
        }
    }

    @Override
    public void exception(Throwable throwable) throws Exception {
        if (this.canSendKickMessage()) {
            this.disconnect(ChatColor.RED + Util.exception(throwable));
        } else {
            this.ch.close();
        }
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (packet.packet == null) {
            throw new QuietException("Unexpected packet received during login process! " + BufUtil.dump(packet.buf, 16));
        }
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        this.relayMessage(pluginMessage);
    }

    @Override
    public void handle(LegacyHandshake legacyHandshake) throws Exception {
        this.legacy = true;
        this.ch.close(this.bungee.getTranslation("outdated_client", this.bungee.getGameVersion()));
    }

    @Override
    public void handle(LegacyPing legacyPing) throws Exception {
        this.legacy = true;
        final boolean v1_5 = legacyPing.isV1_5();
        if (this.lastPreLegacyPingTime + XCord.getConfig().getAntiBotNullPingAndPingSpamCacheMilliseconds() > System.currentTimeMillis()) {
            this.ch.close(v1_5 ? this.preLegacyPingCachedMessage : this.prePreLegacyPingCachedMessage);
        } else {
            ServerPing ping = new ServerPing(
                    new ServerPing.Protocol(this.bungee.getName() + " " + this.bungee.getGameVersion(), this.bungee.getProtocolVersion()),
                    new ServerPing.Players(this.listener.getMaxPlayers(), this.bungee.getOnlineCount(), null),
                    new TextComponent(TextComponent.fromLegacyText(this.listener.getMotd())),
                    (Favicon) null
            );
            if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
                PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) XCord.getConfig().getGeneralInitialTimeout());
            }

            Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
                public void done(ProxyPingEvent event, Throwable error) {
                    if (!InitialHandler.this.ch.isClosed() && !InitialHandler.this.ch.isClosing()) {
                        ServerPing response = event.getResponse();
                        InitialHandler.this.preLegacyPingCachedMessage = ChatColor.DARK_BLUE
                                + "\u0000"
                                + 127
                                + '\u0000'
                                + response.getVersion().getName()
                                + '\u0000'
                                + InitialHandler.getFirstLine(response.getDescription())
                                + '\u0000'
                                + (response.getPlayers() != null ? response.getPlayers().getOnline() : "-1")
                                + '\u0000'
                                + (response.getPlayers() != null ? response.getPlayers().getMax() : "-1");
                        InitialHandler.this.prePreLegacyPingCachedMessage = ChatColor.stripColor(InitialHandler.getFirstLine(response.getDescription()))
                                + '§'
                                + (response.getPlayers() != null ? response.getPlayers().getOnline() : "-1")
                                + '§'
                                + (response.getPlayers() != null ? response.getPlayers().getMax() : "-1");
                        InitialHandler.this.lastPreLegacyPingTime = System.currentTimeMillis();
                        InitialHandler.this.ch.close(
                                v1_5 ? InitialHandler.this.preLegacyPingCachedMessage : InitialHandler.this.prePreLegacyPingCachedMessage
                        );
                    }
                }
            };
            this.bungee.getPluginManager().callEvent(new ProxyPingEvent(this, ping, callback));
        }
    }

    private static String getFirstLine(String text) {
        int lineBreak = text.indexOf(10);
        return lineBreak == -1 ? text : text.substring(0, lineBreak);
    }

    private ServerPing getPingInfo(String motd, int protocolVersion) {
        return new ServerPing(
                new ServerPing.Protocol(this.bungee.getName() + " " + this.bungee.getGameVersion(), protocolVersion),
                new ServerPing.Players(this.listener.getMaxPlayers(), this.bungee.getOnlineCount(), null),
                motd,
                BungeeCord.getInstance().config.getFaviconObject()
        );
    }

    @Override
    public void handle(StatusRequest statusRequest) throws Exception {
        Preconditions.checkState(this.thisState == InitialHandler.State.STATUS, "Not expecting STATUS");
        if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
            PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) XCord.getConfig().getGeneralInitialTimeout());
        }

        ServerInfo forcedHost = AbstractReconnectHandler.getForcedHost(this);
        String motd = forcedHost != null ? forcedHost.getMotd() : this.listener.getMotd();
        final int protocolVersion = ProtocolConstants.SUPPORTED_VERSION_IDS.contains(this.handshake.getProtocolVersion())
                ? this.handshake.getProtocolVersion()
                : this.bungee.getProtocolVersion();
        boolean legacyProtocol = ProtocolConstants.isBeforeOrEq(this.handshake.getProtocolVersion(), 47);
        boolean cacheEnabled = XCord.getConfig().getAntiBotNullPingAndPingSpamCachePingMotd()
                && (!XCord.getConfig().getAntiBotNullPingAndPingSpamOnlyCacheDuringBotAttack() || XCord.getAntiBotManager().isAttackActive());
        final boolean updateCache;
        final boolean useCachedPing;
        if (cacheEnabled
                && (legacyProtocol ? cachedLegacyPingAge : cachedPingAge)
                        + XCord.getConfig().getAntiBotNullPingAndPingSpamCacheMilliseconds() < System.currentTimeMillis()) {
            updateCache = true;
            useCachedPing = false;
        } else {
            updateCache = false;
            useCachedPing = cacheEnabled;
        }

        Callback<ServerPing> pingCallback = new Callback<ServerPing>() {
            public void done(ServerPing ping, Throwable error) {
                if (error != null) {
                    ping = InitialHandler.this.getPingInfo(InitialHandler.this.bungee.getTranslation("ping_cannot_connect"), protocolVersion);
                    InitialHandler.this.bungee.getLogger().log(Level.WARNING, "Error pinging remote server", error);
                }

                Callback<ProxyPingEvent> proxyPingCallback = new Callback<ProxyPingEvent>() {
                    public void done(ProxyPingEvent event, Throwable eventError) {
                        Gson gson = InitialHandler.this.handshake.getProtocolVersion() == 4
                                ? BungeeCord.getInstance().gsonLegacy
                                : BungeeCord.getInstance().gson;
                        if (InitialHandler.this.bungee.getConnectionThrottle() != null) {
                            InitialHandler.this.bungee.getConnectionThrottle().unthrottle(InitialHandler.this.getSocketAddress());
                        }

                        StatusResponse response;
                        if (ProtocolConstants.isBeforeOrEq(InitialHandler.this.handshake.getProtocolVersion(), 47)) {
                            if (useCachedPing) {
                                response = new StatusResponse(
                                        InitialHandler.this.swapJSONProtocol(InitialHandler.cachedLegacyStatusResponse.getResponse(), protocolVersion)
                                );
                            } else {
                                JsonElement tree = gson.toJsonTree(event.getResponse());
                                Preconditions.checkArgument(tree.isJsonObject(), "Response is not a JSON object");
                                JsonObject jsonObject = tree.getAsJsonObject();
                                jsonObject.addProperty("description", event.getResponse().getDescription());
                                response = new StatusResponse(gson.toJson(tree));
                                if (updateCache) {
                                    InitialHandler.cachedLegacyPingAge = System.currentTimeMillis();
                                    InitialHandler.cachedLegacyStatusResponse = response;
                                }
                            }
                        } else if (useCachedPing) {
                            response = new StatusResponse(
                                    InitialHandler.this.swapJSONProtocol(InitialHandler.cachedStatusResponse.getResponse(), protocolVersion)
                            );
                        } else {
                            response = new StatusResponse(gson.toJson(event.getResponse()));
                            if (updateCache) {
                                InitialHandler.cachedPingAge = System.currentTimeMillis();
                                InitialHandler.cachedStatusResponse = response;
                            }
                        }

                        InitialHandler.this.unsafe.sendPacket(response);
                    }
                };
                if (!useCachedPing) {
                    InitialHandler.this.bungee
                            .getPluginManager()
                            .callEvent(new ProxyPingEvent(InitialHandler.this, ping, proxyPingCallback));
                } else {
                    proxyPingCallback.done(new ProxyPingEvent(InitialHandler.this, ping, proxyPingCallback), null);
                }
            }
        };
        if (forcedHost != null && this.listener.isPingPassthrough() && !useCachedPing) {
            ((BungeeServerInfo) forcedHost).ping(pingCallback, this.handshake.getProtocolVersion());
        } else {
            pingCallback.done(this.getPingInfo(motd, protocolVersion), null);
        }

        this.thisState = InitialHandler.State.PING;
    }

    private String swapJSONProtocol(String json, int protocolVersion) {
        int protocolIndex = json.indexOf("\"protocol\":");
        if (protocolIndex <= 0) {
            return "none";
        }

        protocolIndex += "\"protocol\":".length();
        String prefix = json.substring(0, protocolIndex);
        int valueEnd = prefix.length() + 1;

        while (Character.isDigit(json.charAt(valueEnd))) {
            valueEnd++;
        }

        return prefix + protocolVersion + json.substring(valueEnd);
    }

    @Override
    public void handle(PingPacket pingPacket) throws Exception {
        if (!ACCEPT_INVALID_PACKETS) {
            Preconditions.checkState(this.thisState == InitialHandler.State.PING, "Not expecting PING");
        }

        this.unsafe.sendPacket(pingPacket);
        this.disconnect("");
    }

    @Override
    public void handle(Handshake handshake) throws Exception {
        Preconditions.checkState(this.thisState == InitialHandler.State.HANDSHAKE, "Not expecting HANDSHAKE");
        String remoteAddress = ((InetSocketAddress) this.ch.getRemoteAddress()).getAddress().getHostAddress();
        if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
            PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) XCord.getConfig().getGeneralInitialTimeout());
        }

        this.latency = (int) (System.currentTimeMillis() - this.connectionTimestamp);
        this.handshake = handshake;
        this.ch.setVersion(handshake.getProtocolVersion());
        XCordConfig config = XCord.getConfig();
        if (config.getAntiBotDnsCheck()
                && (
                        handshake.getHost().contains(BungeeCord.serverAddress)
                                || XCord.getConfig().getAntiBotDnsCheckIsStrict()
                                        && (handshake.getHost().contains("localhost") || handshake.getHost().contains("127.0.0.1"))
                )) {
            if (XCord.getAntiBotManager().isAttackActive()) {
                if (this.ch.getRemoteAddress() != null) {
                    XCord.getBlacklistManager().isBlacklisted(
                            this.ch.getRemoteAddress(),
                            "failed DNS check (Strict: "
                                    + (handshake.getHost().contains("localhost") || handshake.getHost().contains("127.0.0.1"))
                                    + ")"
                    );
                    throw new SilentException("DNS check");
                }
            } else if (!XCord.getConfig().isIgnoredAddress(((InetSocketAddress) this.ch.getRemoteAddress()).getAddress())) {
                BungeeCord.getInstance()
                        .getLogger()
                        .info(this.ch.getRemoteAddress().toString() + " connected through your IP (Not domain, Abnormal behavior)");
            }
        }

        if (config.getAntiBotUnstableCheckEnabled() && this.latency >= config.getAntiBotUnstableCheckLatencyThreshold()) {
            if (XCord.getAntiBotManager().isAttackActive()) {
                if (XCord.getAntiBotManager()
                        .checkViolation(
                                CheckType.UNSTABLE_CONNECTION,
                                this.ch,
                                1,
                                XCord.getConfig().getAntiBotUnstableCheckBlacklistViolations(),
                                "Latency: " + this.latency
                        )) {
                    if (!config.getAntiBotUnstableCheckDelayKickForReason() || this.canSendKickMessage()) {
                        this.ch.close();
                        return;
                    }

                    this.delayedKickReason = XCord.getMessages().getFailedConnection().getComponents();
                }
            } else if (!XCord.isLogFilterEnabled()) {
                BungeeCord.getInstance().getLogger().info(remoteAddress + " connected abnormally> block-latency: " + this.latency);
            }
        }

        if (handshake.getHost().contains("\u0000")) {
            if (handshake.getHost().contains("FML")) {
                this.forge = true;
            }

            String[] hostParts = handshake.getHost().split("\u0000", 2);
            handshake.setHost(hostParts[0]);
            this.extraDataInHandshake = "\u0000" + hostParts[1];
        }

        if (handshake.getHost().endsWith(".")) {
            handshake.setHost(handshake.getHost().substring(0, handshake.getHost().length() - 1));
        }

        this.virtualHost = InetSocketAddress.createUnresolved(handshake.getHost(), handshake.getPort());
        if (this.bungee.getPluginManager().getEventBus().isWatchedListenerActive(2)) {
            this.bungee.getPluginManager().callEvent(new PlayerHandshakeEvent(this, handshake));
            if (!remoteAddress.equals(((InetSocketAddress) this.ch.getRemoteAddress()).getAddress().getHostAddress())) {
                this.ch.setBeingForward(true);
            }
        }

        switch (handshake.getRequestedProtocol()) {
            case 1:
                if (this.bungee.getConfig().isLogPings() && !XCord.isLogFilterEnabled()) {
                    this.bungee.getLogger().log(Level.INFO, "{0} has pinged", this);
                }

                this.thisState = InitialHandler.State.STATUS;
                this.ch.setProtocol(Protocol.STATUS);
                break;
            case 2:
                if (BungeeCord.getInstance().getConfig().isLogInitialHandlerConnections() && !XCord.isLogFilterEnabled()) {
                    this.bungee.getLogger().log(Level.INFO, "{0} has connected", this);
                }

                this.thisState = InitialHandler.State.USERNAME;
                this.ch.setProtocol(Protocol.LOGIN);
                if (this.delayedKickReason != null) {
                    this.disconnect(this.delayedKickReason);
                    return;
                }

                SocketAddress socketAddress = this.ch.getRemoteAddress();
                if (socketAddress != null && !this.ch.isBeingForward()) {
                    InetSocketAddress clientAddress = (InetSocketAddress) socketAddress;
                    InetAddress clientInetAddress = clientAddress.getAddress();
                    ConnectionStats stats = XCord.getAntiBotManager().getStats(clientInetAddress.getHostAddress());
                    if (stats.getLastActionInt() > 0 && XCord.getAntiBotManager().isAttackActive()) {
                        int portDelta = Math.abs(clientAddress.getPort() - stats.getLastActionInt());
                        if (portDelta > 4000
                                && portDelta < 10000
                                && !XCord.getAntiBotManager().isWhitelisted(clientInetAddress.getHostAddress())
                                && System.currentTimeMillis() - stats.getLastSeen() < 10000L
                                && XCord.getConfig().getAntiBotLinuxCheck()) {
                            InetAddress linuxCheckAddress = clientAddress.getAddress();
                            this.disconnect(XCord.getMessages().getLinuxCheckFail(), XCord.getConfig().getGeneralKickTimeout());
                            XCord.getBlacklistManager().disconnect(linuxCheckAddress, XCord.getMessages().getLinuxCheckFail());
                            BungeeCord.getInstance()
                                    .getLogger()
                                    .info(clientAddress.getAddress().getHostAddress() + " Suspected-Bot: Linux-OS(" + portDelta + ")");
                            return;
                        }
                    }

                    if (stats.checkSpeedyLogin(clientInetAddress)) {
                        this.disconnect(XCord.getMessages().getSpeedJoinFail(), XCord.getConfig().getGeneralKickTimeout());
                        return;
                    }

                    stats.setLastAction(clientAddress.getPort());
                }

                if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) {
                    if (handshake.getProtocolVersion() > this.bungee.getProtocolVersion()) {
                        this.disconnect(this.bungee.getTranslation("outdated_server", this.bungee.getGameVersion()));
                    } else {
                        this.disconnect(this.bungee.getTranslation("outdated_client", this.bungee.getGameVersion()));
                    }

                    return;
                }
                break;
            default:
                throw new QuietException("Cannot request protocol " + handshake.getRequestedProtocol());
        }
    }

    @Override
    public void handle(LoginRequest loginRequest) throws Exception {
        Preconditions.checkState(this.thisState == InitialHandler.State.USERNAME, "Not expecting USERNAME");
        this.loginRequest = loginRequest;
        if (this.getName().length() > 16) {
            this.disconnect(this.bungee.getTranslation("name_too_long"), XCord.getConfig().getGeneralKickTimeout());
        } else if (!NameValidator.isValidName(
                loginRequest.getData(),
                XCord.getConfig().getGeneralAlwaysCheckAllowedNameCharacters() || this.onlineMode
        )) {
            this.disconnect(this.bungee.getTranslation("name_invalid"), XCord.getConfig().getGeneralKickTimeout());
        } else if (this.getName().length() < XCord.getConfig().getGeneralMinimumNameLength()) {
            this.disconnect(XCord.getMessages().getShortName(), XCord.getConfig().getGeneralKickTimeout());
        } else {
            if (XCord.getConfig().getAntiBotStrangeNameEnabled() && XCord.getAntiBotManager().isAttackActive()) {
                int randomNumbers = 0;
                int randomCases = 0;
                char[] characters = loginRequest.getData().toCharArray();

                for (int index = 1; index < characters.length; index++) {
                    char previous = characters[index - 1];
                    char current = characters[index];
                    boolean previousDigit = Character.isDigit(previous);
                    boolean currentDigit = Character.isDigit(current);
                    if (previousDigit && !currentDigit) {
                        randomNumbers++;
                    }

                    if (!previousDigit && !currentDigit && Character.isLowerCase(previous) != Character.isLowerCase(current)) {
                        randomCases++;
                    }
                }

                if (XCord.getConfig().getDebugDebugMode()) {
                    BungeeCord.getInstance()
                            .getLogger()
                            .info("Strange-Name Data> randomCases: " + randomCases + " randomNumbers: " + randomNumbers);
                }

                if (randomCases >= XCord.getConfig().getAntiBotStrangeNameRandomCapitalCount()
                        && randomNumbers >= XCord.getConfig().getAntiBotStrangeNameRandomNumberCount()) {
                    if (XCord.getAntiBotManager()
                            .checkViolation(
                                    CheckType.STRANGE_NAME,
                                    this.ch,
                                    1,
                                    XCord.getConfig().getAntiBotStrangeNameBlacklistViolations(),
                                    randomCases + "/" + randomNumbers
                            )) {
                        this.disconnect(XCord.getMessages().getFailedConnection(), XCord.getConfig().getGeneralKickTimeout());
                    }

                    return;
                }
            }

            if (XCord.getConfig().getAntiBotNamePatternEnabled()
                    && XCord.getAntiBotManager().isAttackActive()
                    && XCord.getConfig().getAntiBotNamePatternMinimumNameLength() <= loginRequest.getData().length()) {
                Match match = XCord.getAntiBotManager().getSimilarNameCache().findClosest(loginRequest.getData());
                if (match != null) {
                    if (XCord.getConfig().getDebugDebugMode()) {
                        System.out.println(
                                "-> NamePatternCheck result: Tested="
                                        + loginRequest.getData()
                                        + " Matched="
                                        + match.getValue()
                                        + " %"
                                        + match.getScore()
                        );
                    }

                    if (match.getScore() >= XCord.getConfig().getAntiBotNamePatternMinimumMatchPercent()
                            && !XCord.getAntiBotManager()
                                    .isWhitelisted(
                                            this.getName(),
                                            ((InetSocketAddress) this.ch.getRemoteAddress()).getAddress().getHostAddress()
                                    )) {
                        System.out.println(
                                "Name-Pattern detection> user=" + loginRequest.getData() + " Matched=" + match.getValue() + " %=" + match.getScore()
                        );
                        if (XCord.getAntiBotManager()
                                .checkViolation(
                                        CheckType.NAME_PATTERN,
                                        this.ch,
                                        1,
                                        XCord.getConfig().getAntiBotNamePatternBlacklistViolations(),
                                        match.getValue() + ":%" + match.getScore()
                                )) {
                            this.disconnect(XCord.getMessages().getFailedConnection(), XCord.getConfig().getGeneralKickTimeout());
                        }

                        return;
                    }
                }
            }

            if (XCord.getConfig().getIpSpooferSpoofListedUserIps() && XCord.getConfig().getSpoofedUsers().contains(this.getName())) {
                try {
                    InetSocketAddress clientAddress = (InetSocketAddress) this.ch.getRemoteAddress();
                    String spoofedAddress = XCord.getConfig().getIpSpooferAddress();
                    this.ch.getHandle().pipeline().get(HandlerBoss.class).replaceIPAddress(spoofedAddress);
                    if (XCord.getConfig().getIpSpooferLogOnSpoof()) {
                        BungeeCord.getInstance()
                                .getLogger()
                                .info("XCord spoofed " + clientAddress.getHostName() + " with " + spoofedAddress + " for " + this.getName());
                    }
                } catch (Exception exception) {
                    System.out.println("Failed to replace IP for " + this.getName());
                    exception.printStackTrace();
                }
            }

            if (BungeeCord.getInstance().config.isEnforceSecureProfile()) {
                PlayerPublicKey publicKey = loginRequest.getPublicKey();
                if (publicKey == null) {
                    this.disconnect(this.bungee.getTranslation("secure_profile_required"));
                    return;
                }

                if (Instant.ofEpochMilli(publicKey.getExpiry()).isBefore(Instant.now())) {
                    this.disconnect(this.bungee.getTranslation("secure_profile_expired"));
                    return;
                }

                if (this.getVersion() < 760 && !EncryptionUtil.check(publicKey, null)) {
                    this.disconnect(this.bungee.getTranslation("secure_profile_invalid"));
                    return;
                }
            }

            int playerLimit = BungeeCord.getInstance().config.getPlayerLimit();
            if (playerLimit > 0 && this.bungee.getOnlineCount() > playerLimit) {
                this.disconnect(this.bungee.getTranslation("proxy_full"));
            } else if (!this.isOnlineMode() && this.bungee.getPlayer(this.getUniqueId()) != null) {
                this.disconnect(this.bungee.getTranslation("already_connected_proxy"));
            } else {
                Callback<PreLoginEvent> preLoginCallback = new Callback<PreLoginEvent>() {
                    public void done(PreLoginEvent event, Throwable error) {
                        if (event.isCancelled()) {
                            BaseComponent[] reason = event.getCancelReasonComponents();
                            InitialHandler.this.disconnect(
                                    reason != null ? reason : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message"))
                            );
                        } else if (!InitialHandler.this.ch.isClosed()) {
                            if (InitialHandler.this.onlineMode) {
                                InitialHandler.this.thisState = InitialHandler.State.ENCRYPT;
                                InitialHandler.this.request = EncryptionUtil.encryptRequest();
                                InitialHandler.this.unsafe().sendPacket(InitialHandler.this.request);
                            } else {
                                InitialHandler.this.thisState = InitialHandler.State.FINISHED;
                                InitialHandler.this.finish();
                            }
                        }
                    }
                };
                if (XCord.getConfig().getAntiBotPassthroughVerifyThenReconnectEnabled()
                        && XCord.getAntiBotManager().isAttackActive()
                        && !XCord.getAntiBotManager()
                                .isWhitelisted(this.getName(), this.getAddress().getAddress().getHostAddress())) {
                    this.passthroughThenKick = true;
                }

                if (!this.passthroughThenKick) {
                    this.bungee.getPluginManager().callEvent(new PreLoginEvent(this, preLoginCallback));
                } else {
                    this.onlineMode = false;
                    this.thisState = InitialHandler.State.FINISHED;
                    this.finish();
                }
            }
        }
    }

    @Override
    public void handle(EncryptionResponse encryptionResponse) throws Exception {
        Preconditions.checkState(this.thisState == InitialHandler.State.ENCRYPT, "Not expecting ENCRYPT");
        Preconditions.checkState(EncryptionUtil.check(this.loginRequest.getPublicKey(), encryptionResponse, this.request), "Invalid verification");
        this.thisState = InitialHandler.State.FINISHED;
        SecretKey secretKey = EncryptionUtil.getSecret(encryptionResponse, this.request);
        if (secretKey instanceof SecretKeySpec && secretKey.getEncoded().length != 16) {
            this.ch.close();
        } else {
            BungeeCipher decryptCipher = EncryptionUtil.getCipher(false, secretKey);
            this.ch.addBefore("frame-decoder", "decrypt", new CipherDecoder(decryptCipher));
            BungeeCipher encryptCipher = EncryptionUtil.getCipher(true, secretKey);
            this.ch.addBefore("frame-prepender", "encrypt", new CipherEncoder(encryptCipher));
            String encodedName = URLEncoder.encode(this.getName(), "UTF-8");
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            for (byte[] bytes : new byte[][]{
                    this.request.getServerId().getBytes("ISO_8859_1"),
                    secretKey.getEncoded(),
                    EncryptionUtil.keys.getPublic().getEncoded()
            }) {
                digest.update(bytes);
            }

            String serverHash = URLEncoder.encode(new BigInteger(digest.digest()).toString(16), "UTF-8");
            String ipParameter = BungeeCord.getInstance().config.isPreventProxyConnections()
                    && this.getSocketAddress() instanceof InetSocketAddress
                    ? "&ip=" + URLEncoder.encode(this.getAddress().getAddress().getHostAddress(), "UTF-8")
                    : "";
            String authUrl = String.format(MOJANG_AUTH_URL, encodedName, serverHash, ipParameter);
            Callback<String> authCallback = new Callback<String>() {
                public void done(String responseBody, Throwable error) {
                    if (error == null) {
                        LoginResult profile = BungeeCord.getInstance().gson.fromJson(responseBody, LoginResult.class);
                        if (profile != null && profile.getId() != null) {
                            InitialHandler.this.loginProfile = profile;
                            InitialHandler.this.name = profile.getName();
                            InitialHandler.this.uniqueId = Util.getUUID(profile.getId());
                            InitialHandler.this.finish();
                            return;
                        }

                        InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("offline_mode_player"));
                        if (XCord.getConfig().getDebugDebugMode()) {
                            BungeeCord.getInstance()
                                    .getLogger()
                                    .info("Failed online-mode validation on " + InitialHandler.this.getName() + ", response: " + responseBody);
                        }
                    } else {
                        InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("mojang_fail"));
                        InitialHandler.this.bungee
                                .getLogger()
                                .log(Level.SEVERE, "Error authenticating " + InitialHandler.this.getName() + " with minecraft.net", error);
                    }
                }
            };
            HttpClient.get(authUrl, this.ch.getHandle().eventLoop(), authCallback);
        }
    }

    private void finish() {
        if (this.isOnlineMode()) {
            ProxiedPlayer existingPlayer = this.bungee.getPlayer(this.getName());
            if (existingPlayer != null) {
                existingPlayer.disconnect(this.bungee.getTranslation("already_connected_proxy"));
            }

            ProxiedPlayer existingPlayerById = this.bungee.getPlayer(this.getUniqueId());
            if (existingPlayerById != null) {
                existingPlayerById.disconnect(this.bungee.getTranslation("already_connected_proxy"));
            }
        } else {
            ProxiedPlayer existingPlayer = this.bungee.getPlayer(this.getName());
            if (existingPlayer != null) {
                this.disconnect(this.bungee.getTranslation("already_connected_proxy"));
                return;
            }
        }

        this.offlineId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.getName()).getBytes(Charsets.UTF_8));
        if (this.uniqueId == null) {
            this.uniqueId = this.offlineId;
        }

        if (BungeeCord.getInstance().config.isEnforceSecureProfile() && this.getVersion() >= 760) {
            boolean validProfile = false;

            try {
                validProfile = EncryptionUtil.check(this.loginRequest.getPublicKey(), this.uniqueId);
            } catch (GeneralSecurityException ignored) {
            }

            if (!validProfile) {
                if (XCord.getAntiBotManager().isAttackActive()) {
                    XCord.getBlacklistManager().isBlacklisted(this.getAddress(), "Invalid secure profile");
                } else {
                    this.disconnect(this.bungee.getTranslation("secure_profile_invalid"));
                }

                return;
            }
        }

        PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) BungeeCord.getInstance().config.getTimeout());
        String remoteAddress = ((InetSocketAddress) this.ch.getRemoteAddress()).getAddress().getHostAddress();
        final boolean whitelisted = XCord.getAntiBotManager().isWhitelisted(this.getName(), remoteAddress);
        Callback<LoginEvent> loginCallback = new Callback<LoginEvent>() {
            public void done(LoginEvent event, Throwable error) {
                if (event.isCancelled()) {
                    BaseComponent[] reason = event.getCancelReasonComponents();
                    InitialHandler.this.disconnect(
                            reason != null ? reason : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message"))
                    );
                } else if (!InitialHandler.this.ch.isClosed() && !InitialHandler.this.ch.isClosing()) {
                    InitialHandler.this.ch.getHandle().eventLoop().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!InitialHandler.this.ch.isClosing()) {
                                UserConnection connection = new UserConnection(
                                        InitialHandler.this.bungee,
                                        InitialHandler.this.ch,
                                        InitialHandler.this.getName(),
                                        InitialHandler.this
                                );
                                connection.setCompressionThreshold(BungeeCord.getInstance().config.getCompressionThreshold());
                                connection.setWhitelistedFromAntibot(whitelisted);
                                connection.setUsingForge(InitialHandler.this.forge);
                                connection.init();
                                InitialHandler.this.unsafe.sendPacket(
                                        new LoginSuccess(
                                                InitialHandler.this.getUniqueId(),
                                                InitialHandler.this.getName(),
                                                InitialHandler.this.loginProfile == null
                                                        ? null
                                                        : InitialHandler.this.loginProfile.getProperties()
                                        )
                                );
                                InitialHandler.this.ch.setProtocol(Protocol.GAME);
                                UpstreamBridge bridge = XCord.getConfig().getExploitMonitorEnabled()
                                        ? new ExploitMonitor(InitialHandler.this.bungee, connection)
                                        : new UpstreamBridge(InitialHandler.this.bungee, connection);
                                bridge.setHandleDisconnects(false);
                                InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class).setHandler(bridge);
                                AtomicBoolean verificationStarted = new AtomicBoolean(false);
                                Runnable completeLogin = () -> {
                                    if (!InitialHandler.this.ch.isClosed() && !verificationStarted.getAndSet(true)) {
                                        if (InitialHandler.this.passthroughThenKick) {
                                            XCord.getAntiBotManager()
                                                    .whitelist(
                                                            InitialHandler.this.getName(),
                                                            InitialHandler.this.getAddress().getAddress().getHostAddress()
                                                    );
                                            InitialHandler.this.disconnect(XCord.getConfig().getSuccessfulVerifyKickMessage());
                                        } else {
                                            if (connection.getServer() instanceof SpoofedServerConnection) {
                                                connection.setPassedThroughSpoofedServer(true);
                                                connection.setServer(null);
                                            }

                                            if (XCord.getConfig().getAntiBotVerifyV2Enabled() && connection.isDisableEntityMetadataRewrite()) {
                                                connection.setJoinRequiresRewrite(true);
                                            }

                                            bridge.setHandleDisconnects(true);
                                            if (XCord.getConfig().getAntiDdosDuringLoginEnabled()) {
                                                if (XCord.getConfig().getAntiDdosDuringGameplayEnabled()) {
                                                    InitialHandler.this.ch.getHandle()
                                                            .pipeline()
                                                            .replace(
                                                                    "frame-decoder",
                                                                    "frame-decoder",
                                                                    new AntiDdosFrameDecoder(
                                                                            XCord.getConfig().getAntiDdosDuringGameplayMaxBytesPerSecond(),
                                                                            XCord.getConfig().getAntiDdosDuringGameplayMaxPacketsPerSecond()
                                                                    ).setFirstPacketCheck(false)
                                                            );
                                                } else {
                                                    InitialHandler.this.ch.getHandle()
                                                            .pipeline()
                                                            .replace("frame-decoder", "frame-decoder", new VarIntFrameDecoder());
                                                }
                                            } else if (XCord.getConfig().getAntiDdosDuringGameplayEnabled()) {
                                                InitialHandler.this.ch.getHandle()
                                                        .pipeline()
                                                        .replace(
                                                                "frame-decoder",
                                                                "frame-decoder",
                                                                new AntiDdosFrameDecoder(
                                                                        XCord.getConfig().getAntiDdosDuringGameplayMaxBytesPerSecond(),
                                                                        XCord.getConfig().getAntiDdosDuringGameplayMaxPacketsPerSecond()
                                                                ).setFirstPacketCheck(false)
                                                        );
                                            }

                                            BungeeCord.getInstance().addConnection(connection);
                                            connection.getTabListHandler().onConnect();
                                            connection.unsafe()
                                                    .sendPacket(BungeeCord.getInstance().registerChannels(InitialHandler.this.getVersion()));
                                            InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)
                                                    .setHandler(bridge);
                                            InitialHandler.this.bungee.getPluginManager().callEvent(new PostLoginEvent(connection));
                                            ServerInfo serverInfo;
                                            if (InitialHandler.this.bungee.getReconnectHandler() != null) {
                                                serverInfo = InitialHandler.this.bungee.getReconnectHandler().getServer(connection);
                                            } else {
                                                serverInfo = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
                                            }

                                            if (serverInfo == null) {
                                                serverInfo = InitialHandler.this.bungee
                                                        .getServerInfo(InitialHandler.this.listener.getDefaultServer());
                                            }

                                            connection.connect(serverInfo, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
                                            InitialHandler.this.thisState = InitialHandler.State.FINISHED;
                                        }
                                    }
                                };
                                Runnable antiVpnCheck = !XCord.getConfig().getLoginAntiVpnEnabled()
                                        ? null
                                        : () -> {
                                            InetAddress clientInetAddress =
                                                    ((InetSocketAddress) InitialHandler.this.ch.getRemoteAddress()).getAddress();
                                            Callback<ProxyResult> proxyCallback = (result, proxyError) -> {
                                                if (!InitialHandler.this.ch.isClosed() && result == ProxyResult.DETECTED) {
                                                    InitialHandler.this.fireQuitEvent(connection);
                                                    InitialHandler.this.ch.getHandle()
                                                            .eventLoop()
                                                            .execute(() -> connection.disconnect(XCord.getMessages().getVpn()));
                                                    if (XCord.getConfig().getLoginAntiVpnBlacklistOnDetection()
                                                            || XCord.getConfig().getLoginAntiVpnOnlyBlacklistOnDetectionDuringAttack()
                                                                    && XCord.getAntiBotManager().isAttackActive()) {
                                                        XCord.getBlacklistManager().isBlacklisted(clientInetAddress);
                                                        XCord.getBlacklistManager()
                                                                .disconnect(clientInetAddress, XCord.getMessages().getVpn());
                                                    }
                                                } else {
                                                    InitialHandler.this.ch.getHandle().eventLoop().execute(completeLogin);
                                                }
                                            };
                                            ProxyResult result = XCord.getAntiProxyManager()
                                                    .check(InitialHandler.this.getName(), clientInetAddress.getHostAddress(), true);
                                            if (result == null) {
                                                XCord.getAsyncJobService()
                                                        .submit(
                                                                new ProxyCheckJob(
                                                                        InitialHandler.this.getName(),
                                                                        clientInetAddress.getHostAddress(),
                                                                        proxyCallback
                                                                )
                                                        );
                                            } else {
                                                proxyCallback.done(result, null);
                                            }
                                        };
                                if (XCord.getConfig().getAntiBotVerifyV2Enabled()
                                        && !whitelisted
                                        && (!XCord.getConfig().getAntiBotVerifyV2Ignore116PlusVersions()
                                                || InitialHandler.this.getVersion() < 735)
                                        && (!XCord.getConfig().getAntiBotVerifyV2IgnoreGeyser()
                                                || InitialHandler.this.getUniqueId().getMostSignificantBits() != 0L)) {
                                    int verifyAttempt = InitialHandler.RANDOM.nextInt(5000) + 1;
                                    Runnable verificationCallback = antiVpnCheck != null ? antiVpnCheck : completeLogin;
                                    boolean advancedCheck = InitialHandler.this.ch.getHandle() instanceof EpollSocketChannel
                                            && !InitialHandler.this.ch.isBeingForward()
                                            && InitialHandler.this.getUniqueId().getMostSignificantBits() != 0L
                                            && ((InetSocketAddress) InitialHandler.this.ch.getRemoteAddress())
                                                    .getAddress()
                                                    .getHostAddress()
                                                    .contains(
                                                            ((InetSocketAddress) InitialHandler.this.ch.getHandle().remoteAddress())
                                                                    .getAddress()
                                                                    .getHostAddress()
                                                    )
                                            && !XCord.getAntiProxyManager()
                                                    .getWhitelist()
                                                    .isUsernameWhitelisted(InitialHandler.this.getName())
                                            && !XCord.getAntiProxyManager()
                                                    .getWhitelist()
                                                    .isEntryWhitelisted(
                                                            ((InetSocketAddress) InitialHandler.this.ch.getRemoteAddress())
                                                                    .getAddress()
                                                                    .getHostAddress()
                                                    );
                                    if (advancedCheck
                                            && XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesProxyCheck1Enabled()
                                            && InitialHandler.this.ch.getHandle().isActive()
                                            && PipelineUtils.CHANNEL_UTILITY
                                                    .isSuspiciousMss(InitialHandler.this.ch.getHandle())) {
                                        EpollTcpInfo tcpInfo = ((EpollSocketChannel) InitialHandler.this.ch.getHandle()).tcpInfo();
                                        ConnectionStats stats = XCord.getAntiBotManager()
                                                .getStats(
                                                        ((InetSocketAddress) InitialHandler.this.ch.getRemoteAddress())
                                                                .getAddress()
                                                                .getHostAddress()
                                                );
                                        InitialHandler.this.fireQuitEvent(connection);
                                        if (stats.addViolations(CheckType.VERIFY, 1)
                                                > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                                            XCord.getBlacklistManager()
                                                    .isBlacklisted(connection.getAddress(), "VerifyV2-Block-Proxies");
                                            XCord.getBlacklistManager().forceClose(connection.getAddress().getAddress());
                                        } else {
                                            InitialHandler.this.disconnect(
                                                    XCord.getMessages().getVpn(), XCord.getConfig().getGeneralKickTimeout()
                                            );
                                            System.out.println(
                                                    "Disconnected "
                                                            + InitialHandler.this.getName()
                                                            + ", failed verification (VerifyV2): 'block-potential-proxies-1' [Disable in XCord.yml if incorrect] -> "
                                                            + (tcpInfo.advmss() - tcpInfo.sndMss())
                                            );
                                        }

                                        return;
                                    }

                                    BotCheckHandler botCheckHandler = InitialHandler.this.getVersion() > 5
                                            ? new BotHandler(
                                                    InitialHandler.this,
                                                    verificationCallback,
                                                    connection,
                                                    verifyAttempt,
                                                    InitialHandler.this.getVersion()
                                            )
                                            : new LegacyBotHandler(
                                                    InitialHandler.this,
                                                    verificationCallback,
                                                    connection,
                                                    verifyAttempt,
                                                    InitialHandler.this.getVersion()
                                            );
                                    botCheckHandler.setAdvancedCheck(
                                            advancedCheck
                                                    && XCord.getConfig()
                                                            .getAntiBotVerifyV2BlockPotentialProxiesProxyCheck2Enabled()
                                    );
                                    DelayedJob delayedJob = new DelayedJob(
                                            System.currentTimeMillis()
                                                    + (long) XCord.getConfig().getAntiBotVerifyV2MaxCheckTime(),
                                            () -> {
                                                if (!InitialHandler.this.ch.isClosed()
                                                        && !InitialHandler.this.ch.isClosing()
                                                        && (!botCheckHandler.isVerified() || botCheckHandler.getCallback() != null)) {
                                                    ConnectionStats stats = XCord.getAntiBotManager()
                                                            .getStats(connection.getAddress().getHostString());
                                                    if (stats.addViolations(CheckType.VERIFY, 1)
                                                            > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                                                        XCord.getBlacklistManager()
                                                                .isBlacklisted(connection.getAddress(), "Verify-timeout");
                                                        XCord.getBlacklistManager()
                                                                .forceClose(connection.getAddress().getAddress());
                                                    } else {
                                                        InitialHandler.this.ch.getHandle().eventLoop().execute(() -> {
                                                            InitialHandler.this.fireQuitEvent(connection);
                                                            connection.disconnect(XCord.getMessages().getFailedConnection());
                                                        });
                                                        System.out.println(
                                                                "Disconnected "
                                                                        + InitialHandler.this.getName()
                                                                        + ", failed verification: "
                                                                        + botCheckHandler.getDebugInfo()
                                                        );
                                                    }
                                                }
                                            }
                                    );
                                    InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)
                                            .setHandler(botCheckHandler);
                                    XCord.getAsyncJobService().submit(delayedJob);
                                } else if (antiVpnCheck != null) {
                                    antiVpnCheck.run();
                                } else {
                                    completeLogin.run();
                                }
                            }
                        }
                    });
                }
            }
        };
        LoginEvent loginEvent = new LoginEvent(this, loginCallback, this.getLoginProfile());
        if (!this.passthroughThenKick) {
            this.bungee.getPluginManager().callEvent(loginEvent);
        } else {
            loginCallback.done(loginEvent, null);
        }
    }

    public void fireQuitEvent(UserConnection connection) {
        if (!this.passthroughThenKick && XCord.getConfig().getCompatibilityAlwaysFireQuitEvent()) {
            try {
                PlayerDisconnectEvent event = new PlayerDisconnectEvent(connection);
                BungeeCord.getInstance().getPluginManager().callEvent(event);
            } catch (Exception exception) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void handle(LoginPayloadResponse loginPayloadResponse) throws Exception {
        if (XCord.getAntiBotManager().isAttackActive()) {
            PipelineUtils.CHANNEL_UTILITY.close(this.ch.getChannel());
        } else {
            this.disconnect("Unexpected custom LoginPayloadResponse");
        }
    }

    @Override
    public void disconnect(String reason) {
        if (this.canSendKickMessage()) {
            this.disconnect(TextComponent.fromLegacyText(reason));
        } else {
            this.ch.close();
        }
    }

    @Override
    public void disconnect(BaseComponent... reason) {
        if (this.canSendKickMessage()) {
            this.ch.delayedClose(new Kick(ComponentSerializer.toString(reason)));
        } else {
            this.ch.close();
        }
    }

    public void disconnect(String reason, int timeout) {
        if (this.canSendKickMessage()) {
            if (!this.ch.isClosed()) {
                PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) timeout);
            }

            this.ch.delayedClose(new Kick(ComponentSerializer.toString(TextComponent.fromLegacyText(reason))));
        } else {
            this.ch.close();
        }
    }

    public void disconnect(MessageTemplate reason, int timeout) {
        if (this.canSendKickMessage()) {
            if (!this.ch.isClosed()) {
                PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long) timeout);
            }

            this.ch.delayedClose(new Kick(reason.getJson()));
        } else {
            this.ch.close();
        }
    }

    @Override
    public void disconnect(BaseComponent reason) {
        this.disconnect(new BaseComponent[]{reason});
    }

    @Override
    public String getName() {
        return this.name != null ? this.name : (this.loginRequest == null ? null : this.loginRequest.getData());
    }

    @Override
    public int getVersion() {
        return this.handshake == null ? -1 : this.handshake.getProtocolVersion();
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
    public Connection.Unsafe unsafe() {
        return this.unsafe;
    }

    @Override
    public void setOnlineMode(boolean onlineMode) {
        Preconditions.checkState(this.thisState == InitialHandler.State.USERNAME, "Can only set online mode status whilst state is username");
        this.onlineMode = onlineMode;
    }

    @Override
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String getUUID() {
        return UUIDUtils.undash(this.uniqueId.toString());
    }

    @Override
    public String toString() {
        return "[" + this.getSocketAddress() + (this.getName() != null ? "|" + this.getName() : "") + "] <-> InitialHandler";
    }

    @Override
    public boolean isConnected() {
        return !this.ch.isClosed();
    }

    public ChannelWrapper getChannel() {
        return this.ch;
    }

    public InitialHandler(BungeeCord bungee, ListenerInfo listener) {
        this.bungee = bungee;
        this.listener = listener;
    }

    @Override
    public ListenerInfo getListener() {
        return this.listener;
    }

    public Handshake getHandshake() {
        return this.handshake;
    }

    public LoginRequest getLoginRequest() {
        return this.loginRequest;
    }

    public Set<String> getRegisteredChannels() {
        return this.registeredChannels;
    }

    public PluginMessage getBrandMessage() {
        return this.brandMessage;
    }

    @Override
    public boolean isOnlineMode() {
        return this.onlineMode;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public UUID getOfflineId() {
        return this.offlineId;
    }

    public LoginResult getLoginProfile() {
        return this.loginProfile;
    }

    @Override
    public boolean isLegacy() {
        return this.legacy;
    }

    public String getExtraDataInHandshake() {
        return this.extraDataInHandshake;
    }

    private static enum State {
        HANDSHAKE,
        STATUS,
        PING,
        USERNAME,
        ENCRYPT,
        FINISHED;
    }
}
