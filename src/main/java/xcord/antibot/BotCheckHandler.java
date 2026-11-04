package xcord.antibot;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.PlayerPosition;
import net.md_5.bungee.protocol.packet.PlayerPositionAndLook;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.RawDataPacket;
import xcord.XCord;
import xcord.protocol.PacketRegistry;
import xcord.protocol.PacketTransformer;
import xcord.protocol.VarInts;

public abstract class BotCheckHandler extends PacketHandler {
    protected static final TIntObjectMap<Object> LOGIN_DIMENSIONS = new TIntObjectHashMap<>();
    private static final PacketRegistry<RawDataPacket> CBP_REGISTRY = new PacketTransformer<>(RawDataPacket.class, Direction.TO_CLIENT, "CBP_1");
    protected static final PacketRegistry<RawDataPacket> DBP_REGISTRY = new PacketTransformer<RawDataPacket>(
        RawDataPacket.class, Direction.TO_CLIENT, "DBP_1_0_1"
    ) {
        @Override
        public ByteBuf transform(ByteBuf input, Class<RawDataPacket> packetClass, Direction direction, int fromVersion, int toVersion) {
            ByteBuf output = UnpooledByteBufAllocator.DEFAULT.heapBuffer(input.readableBytes());
            Login login = new Login();
            login.read(input, direction, fromVersion);
            input.release();

            for (int version : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                if (version < fromVersion) {
                    continue;
                }
                if (version >= toVersion) {
                    break;
                }
                BotCheckHandler.LOGIN_DIMENSIONS.put(version, login.getDimension());
            }

            login.setEntityId(-100);
            login.setViewDistance(1);
            login.setSimulationDistance(1);
            VarInts.writeVarInt(output, Protocol.GAME.TO_CLIENT.getId(Login.class, fromVersion));
            login.write(output, direction, fromVersion);
            return output;
        }
    };
    protected final int checkId;
    protected final UserConnection user;
    protected final InitialHandler initialHandler;
    protected Runnable callback;
    protected volatile boolean verified = false;
    protected volatile boolean sentPayload = !XCord.getConfig().getAntiBotVerifyV2VerifyBrand();
    protected volatile boolean sentInitials = !XCord.getConfig().getAntiBotVerifyV2VerifyPackets();
    private int ping = -1;
    protected int protocolVersion;
    private boolean sentFakeLobby = false;
    private int verifyAttempts = 0;
    private int fakeLobbyY = 0;
    protected int keepAliveId = -1;
    protected boolean awaitingPosition = false;
    protected boolean receivedKeepAlive = false;
    private double lastY;
    private double lastDeltaY = 5000.0;
    private int fallCount = 0;
    private long lastPacketTime = 0L;
    private boolean advancedCheckEnabled = true;
    private boolean challengeSent = false;
    private boolean challengeAnswered = false;
    private long challengeSentTime = 0L;
    private int expectedKeepAliveId = 0;
    private int challengeAttempts = 0;
    private boolean positionChecked = false;
    private int invalidKeepAlives = 0;

    public BotCheckHandler(InitialHandler initialHandler, UserConnection user, int checkId, int protocolVersion, Runnable callback) {
        this.initialHandler = initialHandler;
        this.user = user;
        this.checkId = checkId;
        this.protocolVersion = protocolVersion;
        this.callback = callback;
    }

    protected void writePacket(Channel channel, ByteBuf packet) {
        if (BungeeCord.getInstance().getConfig().getCompressionThreshold() <= 0 || this.protocolVersion <= 5) {
            channel.writeAndFlush(packet, this.user.getCh().getChannel().voidPromise());
        } else if (XCord.getConfig().getPerformanceOptimizationsUseMergedEncodingPipeline()) {
            channel.pipeline().addBefore("compress", "frame-prepender", PipelineUtils.framePrepender);
            channel.pipeline().context("compress").writeAndFlush(packet, this.user.getCh().getChannel().voidPromise());
            channel.pipeline().remove("frame-prepender");
        } else {
            this.user.getCh().getHandle().pipeline().context("compress").writeAndFlush(packet, this.user.getCh().getChannel().voidPromise());
        }
    }

    public void setAdvancedCheck(boolean enabled) {
        this.advancedCheckEnabled = enabled;
    }

    public void handlePing(int latency) {
        this.ping = latency;
        this.challengeAttempts++;
        if (this.advancedCheckEnabled && !this.positionChecked && latency > 80 && this.user.getCh().getHandle().isActive()) {
            int channelLatency = PipelineUtils.CHANNEL_UTILITY.getPing(this.user.getCh().getHandle());
            if (latency - channelLatency > XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesProxyCheck2Leniency()) {
                if (this.challengeAttempts >= XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesProxyCheck2MaxAttempts()) {
                    this.user.getCh().setProtocol(Protocol.GAME);
                    System.out
                        .println(
                            "Disconnected "
                                + this.user.getName()
                                + ", failed verification (VerifyV2): 'block-potential-proxies-2' [Disable in XCord.yml if incorrect] --> "
                                + (latency - channelLatency)
                        );
                    ConnectionStats stats = XCord.getAntiBotManager().getStats(this.user.getAddress().getHostString());
                    if (stats.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                        XCord.getBlacklistManager().isBlacklisted(this.user.getAddress(), "Verify-Proxy");
                    }
                    this.user.disconnect(XCord.getMessages().getProxyLogicVerifyV2());
                } else {
                    this.user.getCh().getHandle().eventLoop().schedule(() -> {
                        if (this.user.getCh().getHandle().isActive()) {
                            this.sendChallenge();
                        }
                    }, 2L, TimeUnit.SECONDS);
                }
            } else {
                this.positionChecked = true;
            }
        } else {
            this.positionChecked = true;
        }

        if (this.positionChecked && this.challengeSent) {
            this.finishVerification();
        }
    }

    @Override
    public void handle(PluginMessage message) throws Exception {
        this.sentPayload = true;
        this.initialHandler.relayMessage(message);
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
        if (this.awaitingPosition && (long)this.keepAliveId == keepAlive.getRandomId()) {
            this.receivedKeepAlive = true;
            this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.lastPacketTime));
        } else if (this.challengeAnswered && (long)this.expectedKeepAliveId == keepAlive.getRandomId()) {
            this.challengeAnswered = false;
            this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.challengeSentTime));
        } else if (keepAlive.getRandomId() > 1L && ++this.invalidKeepAlives > 5) {
            this.failVerification(5);
        }
    }

    protected void finishVerification() {
        if (!this.user.getCh().isClosing()) {
            if (XCord.getConfig().getAntiBotVerifyV2MaxPing() < this.ping) {
                this.user.disconnect(XCord.getMessages().getHighPing());
            } else {
                if (XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckEnabled()) {
                    XCord.getAntiBotManager().whitelist(this.user.getName(), this.user.getAddress().getAddress().getHostAddress());
                    if (this.protocolVersion < 759) {
                        this.user.unsafe().sendPacket(XCord.getConfig().getVerifiedPacket());
                    }
                }
                this.user.getCh().setProtocol(Protocol.GAME);
                this.completeVerification();
            }
        }
    }

    @Override
    public boolean shouldHandle(PacketWrapper packetWrapper) throws Exception {
        return true;
    }

    @Override
    public void handle(ClientSettings settings) throws Exception {
        this.sentInitials = true;
        this.user.setSettings(settings);
        SettingsChangedEvent event = new SettingsChangedEvent(this.user);
        BungeeCord.getInstance().getPluginManager().callEvent(event);
    }

    protected void runCallback() {
        this.verified = true;
        if (this.callback != null) {
            this.callback.run();
            this.callback = null;
        }
    }

    private void sendChallenge() {
        this.challengeSent = true;
        this.challengeAnswered = true;
        this.expectedKeepAliveId = ThreadLocalRandom.current().nextInt(1000);
        this.user
            .getCh()
            .getHandle()
            .writeAndFlush(new KeepAlive((long)this.expectedKeepAliveId))
            .addListener(future -> this.challengeSentTime = System.currentTimeMillis());
    }

    @Override
    public void handle(PacketWrapper wrapper) throws Exception {
        super.handle(wrapper);
        if (this.isVerified() && this.callback != null && !this.challengeSent) {
            if (!XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckEnabled()) {
                if (this.advancedCheckEnabled && !this.positionChecked) {
                    this.sendChallenge();
                } else {
                    this.finishVerification();
                }
            } else if (!this.sentFakeLobby) {
                Channel channel = this.user.getCh().getHandle();
                this.user.getCh().setProtocol(Protocol.BOT_CHECK);
                this.sentFakeLobby = true;
                RawDataPacket packet = CBP_REGISTRY.get(this.protocolVersion);
                this.writePacket(channel, packet.getRawData().copy());
                this.fakeLobbyY = 8 + ThreadLocalRandom.current().nextInt(6);
                channel.write(new PlayerPositionAndLook(8.0, (double)this.fakeLobbyY, 8.0, 0.0F, 0.0F, 5, false));
                this.lastY = (double)this.fakeLobbyY;
                if (this.protocolVersion < 759) {
                    channel.write(XCord.getConfig().getVerifyingPacket());
                }
                channel.flush();
            } else if (wrapper.packet instanceof PlayerPosition) {
                PlayerPosition position = (PlayerPosition)wrapper.packet;
                double y = position.getY();
                if (wrapper.packet instanceof PlayerPositionAndLook && this.awaitingPosition && y == (double)this.fakeLobbyY) {
                    this.awaitingPosition = false;
                    if (this.receivedKeepAlive) {
                        if (this.fallCount < 6) {
                            this.failVerification(2);
                        } else if (!this.advancedCheckEnabled) {
                            this.finishVerification();
                        } else {
                            this.sendChallenge();
                        }
                        return;
                    }
                }

                if (!this.awaitingPosition && position.isOnGround() && y == 4.25) {
                    if (this.verifyAttempts > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                        this.failVerification(4);
                        return;
                    }
                    this.receivedKeepAlive = false;
                    this.awaitingPosition = true;
                    this.keepAliveId = ThreadLocalRandom.current().nextInt(1000);
                    this.fakeLobbyY = (int)(this.lastY + 5.0);
                    this.verifyAttempts++;
                    this.user.getCh().getHandle().write(new PlayerPositionAndLook(8.0, (double)this.fakeLobbyY, 8.0, 0.0F, 0.0F, 6 + this.verifyAttempts, false));
                    this.user.getCh().getHandle().write(new KeepAlive((long)this.keepAliveId));
                    this.user.getCh().getHandle().flush();
                    this.lastPacketTime = System.currentTimeMillis();
                } else if (!XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckTestCollision()
                    || ((!position.isOnGround() || !(y < 5.0) || y == 4.25) && !(y < 4.25))) {
                    if (this.verifyAttempts == 0 && y > 5.0 && y != (double)this.fakeLobbyY) {
                        double deltaY = y - this.lastY;
                        if (deltaY < this.lastDeltaY && deltaY < -0.1) {
                            this.fallCount++;
                        }
                        if (deltaY <= -5.0) {
                            this.failVerification(5);
                            return;
                        }
                        this.lastDeltaY = deltaY;
                    }
                } else {
                    this.failVerification(1);
                }

                this.lastY = y;
            }
        }
    }

    private void failVerification(int reason) {
        this.user.getCh().setProtocol(Protocol.GAME);
        System.out.println("Disconnected " + this.user.getName() + ", failed advanced-verify bot-check ID:" + reason);
        ConnectionStats stats = XCord.getAntiBotManager().getStats(this.user.getAddress().getHostString());
        if (stats.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
            XCord.getBlacklistManager().isBlacklisted(this.user.getAddress(), "Verify-" + reason);
        }
        this.user.disconnect(XCord.getMessages().getFailedConnection());
    }

    public Runnable getCallback() {
        return this.callback;
    }

    public abstract void completeVerification();

    public boolean isVerified() {
        return this.verified && this.sentInitials && this.sentPayload;
    }

    public int getPing() {
        return this.ping;
    }

    @Override
    public void disconnected(ChannelWrapper wrapper) throws Exception {
        if ((this.user.getPendingConnection() == null || !this.user.getPendingConnection().isPassthroughThenKick())
            && XCord.getConfig().getCompatibilityAlwaysFireQuitEvent()) {
            try {
                PlayerDisconnectEvent event = new PlayerDisconnectEvent(this.user);
                BungeeCord.getInstance().getPluginManager().callEvent(event);
            } catch (Exception exception) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    exception.printStackTrace();
                }
            }
        }
        this.user.getCh().close();
    }

    public String getDebugInfo() {
        return "Protocol: "
            + this.user.getPendingConnection().getVersion()
            + " > Sent-Initials: "
            + this.sentInitials
            + " > Sent-Payload: "
            + this.sentPayload
            + " > Verified: "
            + this.verified
            + " Ping: "
            + this.ping;
    }
}
