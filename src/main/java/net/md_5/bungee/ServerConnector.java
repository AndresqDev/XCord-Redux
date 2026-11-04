package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.entitymap.EntityMap_Dummy;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.forge.ForgeUtils;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.GameState;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginPayloadRequest;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.ViewDistance;
import net.md_5.bungee.util.AddressUtil;
import net.md_5.bungee.util.QuietException;
import xcord.XCord;
import xcord.logging.Log4jFilter;
import xcord.network.SpoofedServerConnection;

public class ServerConnector extends PacketHandler {
    private final ProxyServer bungee;
    private ChannelWrapper ch;
    private final UserConnection user;
    private final BungeeServerInfo target;
    private ServerConnector.State thisState = ServerConnector.State.LOGIN_SUCCESS;
    private ForgeServerHandler handshakeHandler;
    private boolean obsolete;

    @Override
    public void exception(Throwable throwable) throws Exception {
        if (!this.obsolete) {
            String message = "Exception Connecting:" + Util.exception(throwable);
            if (this.user.getServer() == null) {
                this.user.disconnect(message);
            } else {
                this.user.sendMessage(ChatColor.RED + message);
            }
        }
    }

    @Override
    public void connected(ChannelWrapper channel) throws Exception {
        this.ch = channel;
        this.handshakeHandler = new ForgeServerHandler(this.user, this.ch, this.target);
        Handshake handshake = this.user.getPendingConnection().getHandshake();
        Handshake newHandshake = new Handshake(handshake.getProtocolVersion(), handshake.getHost(), handshake.getPort(), 2);
        if (BungeeCord.getInstance().config.isIpForward() && this.user.getSocketAddress() instanceof InetSocketAddress) {
            String host = newHandshake.getHost() + "\u0000" + AddressUtil.sanitizeAddress(this.user.getAddress()) + "\u0000" + this.user.getUUID();
            LoginResult loginProfile = this.user.getPendingConnection().getLoginProfile();
            Property[] properties = new Property[0];
            if (loginProfile != null && loginProfile.getProperties() != null && loginProfile.getProperties().length > 0) {
                properties = loginProfile.getProperties();
            }

            if ((!XCord.getConfig().getFixesFix17KickWhen18Joins() || this.user.getPendingConnection().getVersion() > 47)
                    && this.user.getForgeClientHandler().isFmlTokenInHandshake()) {
                Property[] extendedProperties = Arrays.copyOf(properties, properties.length + 2);
                extendedProperties[extendedProperties.length - 2] = new Property("forgeClient", "true", null);
                extendedProperties[extendedProperties.length - 1] =
                        new Property("extraData", this.user.getExtraDataInHandshake().replaceAll("\u0000", "\u0001"), "");
                properties = extendedProperties;
            }

            if (properties.length > 0) {
                host = host + "\u0000" + BungeeCord.getInstance().gson.toJson(properties);
            }

            Log4jFilter.filter(host);
            newHandshake.setHost(host);
        } else if (!this.user.getExtraDataInHandshake().isEmpty()) {
            newHandshake.setHost(newHandshake.getHost() + this.user.getExtraDataInHandshake());
        }

        channel.write(newHandshake);
        channel.setProtocol(Protocol.LOGIN);
        channel.write(new LoginRequest(this.user.getName(), null, this.user.getUniqueId()));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        this.user.getPendingConnects().remove(this.target);
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
    }

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception {
        Preconditions.checkState(this.thisState == ServerConnector.State.LOGIN_SUCCESS, "Not expecting LOGIN_SUCCESS");
        this.ch.setProtocol(Protocol.GAME);
        this.thisState = ServerConnector.State.LOGIN;
        if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer())
                && this.user.getForgeClientHandler().isHandshakeComplete()
                && this.user.getServer().isForgeServer()) {
            this.user.getForgeClientHandler().resetHandshake();
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
        this.ch.setCompressionThreshold(setCompression.getThreshold());
    }

    @Override
    public void handle(Login login) throws Exception {
        Preconditions.checkState(this.thisState == ServerConnector.State.LOGIN, "Not expecting LOGIN");
        if (this.user.isJoinRequiresRewrite()) {
            this.user.setEntityRewrite(EntityMap.getEntityMap(this.user.getPendingConnection().getVersion(), true));
        }

        ServerConnection serverConnection = new ServerConnection(this.ch, this.target);
        ServerConnectedEvent serverConnectedEvent = new ServerConnectedEvent(this.user, serverConnection);
        if (serverConnection.isForgeServer() && this.user.isForgeUser()) {
            ((MinecraftDecoder) serverConnection.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
            ((MinecraftDecoder) this.user.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
        }

        this.bungee.getPluginManager().callEvent(serverConnectedEvent);
        this.ch.write(BungeeCord.getInstance().registerChannels(this.user.getPendingConnection().getVersion()));
        Queue<DefinedPacket> packetQueue = this.target.getPacketQueue();
        synchronized (packetQueue) {
            while (!packetQueue.isEmpty()) {
                this.ch.write(packetQueue.poll());
            }
        }

        PluginMessage brandMessage = this.user.getPendingConnection().getBrandMessage();
        if (brandMessage != null) {
            this.ch.write(brandMessage);
        }

        Set<String> registeredChannels = this.user.getPendingConnection().getRegisteredChannels();
        if (!registeredChannels.isEmpty()) {
            this.ch.write(
                    new PluginMessage(
                            this.user.getPendingConnection().getVersion() >= 393 ? "minecraft:register" : "REGISTER",
                            Joiner.on("\u0000").join(registeredChannels).getBytes(StandardCharsets.UTF_8),
                            false
                    )
            );
        }

        if (!this.user.isDisableEntityMetadataRewrite() && this.user.getSettings() != null) {
            this.ch.write(this.user.getSettings());
        }

        if (this.user.getForgeClientHandler().getClientModList() == null && !this.user.getForgeClientHandler().isHandshakeComplete()) {
            this.user.getForgeClientHandler().setHandshakeComplete();
        }

        if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer()) && login.getDimension() instanceof Integer) {
            if (this.user.getServer() != null) {
                this.user.getServer().setObsolete(true);
            }

            this.user.getTabListHandler().onServerChange();
            Scoreboard scoreboard = this.user.getServerSentScoreboard();
            if (!this.user.isDisableEntityMetadataRewrite()) {
                for (Objective objective : scoreboard.getObjectives()) {
                    this.user.unsafe().sendPacket(
                            new ScoreboardObjective(
                                    objective.getName(),
                                    objective.getValue(),
                                    objective.getType() == null ? null : ScoreboardObjective.HealthDisplay.fromString(objective.getType()),
                                    (byte) 1
                            )
                    );
                }

                for (Score score : scoreboard.getScores()) {
                    this.user.unsafe().sendPacket(new ScoreboardScore(score.getItemName(), (byte) 1, score.getScoreName(), score.getValue()));
                }

                for (Team team : scoreboard.getTeams()) {
                    this.user.unsafe().sendPacket(new net.md_5.bungee.protocol.packet.Team(team.getName()));
                }
            }

            scoreboard.clear();

            for (UUID bossBar : this.user.getSentBossBars()) {
                this.user.unsafe().sendPacket(new BossBar(bossBar, 1));
            }

            this.user.getSentBossBars().clear();
            this.user.unsafe().sendPacket(new EntityStatus(this.user.getClientEntityId(), (byte) (login.isReducedDebugInfo() ? 22 : 23)));
            if (this.user.getPendingConnection().getVersion() >= 573) {
                this.user.unsafe().sendPacket(new GameState((short) 11, login.isNormalRespawn() ? 0.0F : 1.0F));
            }

            this.user.setDimensionChange(true);
            if (!this.user.isDisableEntityMetadataRewrite() && login.getDimension() == this.user.getDimension()) {
                this.user.unsafe().sendPacket(
                        new Respawn(
                                ((Number) login.getDimension()).intValue() >= 0 ? -1 : 0,
                                login.getWorldName(),
                                login.getSeed(),
                                login.getDifficulty(),
                                login.getGameMode(),
                                login.getPreviousGameMode(),
                                login.getLevelType(),
                                login.isDebug(),
                                login.isFlat(),
                                false,
                                login.getDeathLocation()
                        )
                );
            }

            this.user.setServerEntityId(login.getEntityId());
            if (this.user.isDisableEntityMetadataRewrite() || this.user.isPassedThroughSpoofedServer()) {
                this.user.setClientEntityId(login.getEntityId());
                if (login.getDimension() != this.user.getDimension()) {
                    this.user.unsafe().sendPacket(
                            new Respawn(
                                    ((Number) login.getDimension()).intValue() >= 0 ? -1 : 0,
                                    login.getWorldName(),
                                    login.getSeed(),
                                    login.getDifficulty(),
                                    login.getGameMode(),
                                    login.getPreviousGameMode(),
                                    login.getLevelType(),
                                    login.isDebug(),
                                    login.isFlat(),
                                    false,
                                    login.getDeathLocation()
                            )
                    );
                }

                Login loginPacket = new Login(
                        login.getEntityId(),
                        login.isHardcore(),
                        login.getGameMode(),
                        login.getPreviousGameMode(),
                        login.getWorldNames(),
                        login.getDimensions(),
                        login.getDimension(),
                        login.getWorldName(),
                        login.getSeed(),
                        login.getDifficulty(),
                        (byte) this.user.getPendingConnection().getListener().getTabListSize(),
                        login.getLevelType(),
                        login.getViewDistance(),
                        login.getSimulationDistance(),
                        login.isReducedDebugInfo(),
                        login.isNormalRespawn(),
                        login.isDebug(),
                        login.isFlat(),
                        login.getDeathLocation()
                );
                this.user.unsafe().sendPacket(loginPacket);
                if (login.getDimension() == this.user.getDimension()) {
                    this.user.unsafe().sendPacket(
                            new Respawn(
                                    ((Number) login.getDimension()).intValue() >= 0 ? -1 : 0,
                                    login.getWorldName(),
                                    login.getSeed(),
                                    login.getDifficulty(),
                                    login.getGameMode(),
                                    login.getPreviousGameMode(),
                                    login.getLevelType(),
                                    login.isDebug(),
                                    login.isFlat(),
                                    false,
                                    login.getDeathLocation()
                            )
                    );
                }
            }

            this.user.unsafe().sendPacket(
                    new Respawn(
                            login.getDimension(),
                            login.getWorldName(),
                            login.getSeed(),
                            login.getDifficulty(),
                            login.getGameMode(),
                            login.getPreviousGameMode(),
                            login.getLevelType(),
                            login.isDebug(),
                            login.isFlat(),
                            false,
                            login.getDeathLocation()
                    )
            );
            if (this.user.getPendingConnection().getVersion() >= 477) {
                this.user.unsafe().sendPacket(new ViewDistance(login.getViewDistance()));
            }

            this.user.setDimension(login.getDimension());
            if (this.user.getServer() != null) {
                this.user.getServer().disconnect("Quitting");
            }
        } else {
            this.user.setClientEntityId(login.getEntityId());
            this.user.setServerEntityId(login.getEntityId());
            Login loginPacket = new Login(
                    login.getEntityId(),
                    login.isHardcore(),
                    login.getGameMode(),
                    login.getPreviousGameMode(),
                    login.getWorldNames(),
                    login.getDimensions(),
                    login.getDimension(),
                    login.getWorldName(),
                    login.getSeed(),
                    login.getDifficulty(),
                    (byte) this.user.getPendingConnection().getListener().getTabListSize(),
                    login.getLevelType(),
                    login.getViewDistance(),
                    login.getSimulationDistance(),
                    login.isReducedDebugInfo(),
                    login.isNormalRespawn(),
                    login.isDebug(),
                    login.isFlat(),
                    login.getDeathLocation()
            );
            this.user.unsafe().sendPacket(loginPacket);
            if (this.user.getServer() == null && !this.user.isPassedThroughSpoofedServer()) {
                String brand = this.bungee.getName() + " (" + this.bungee.getVersion() + ")";
                if (ProtocolConstants.isBeforeOrEq(this.user.getPendingConnection().getVersion(), 5)) {
                    this.user.unsafe().sendPacket(new PluginMessage("MC|Brand", brand.getBytes(StandardCharsets.UTF_8), this.handshakeHandler.isServerForge()));
                } else {
                    ByteBuf brandBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
                    DefinedPacket.writeString(brand, brandBuffer);
                    this.user.unsafe().sendPacket(
                            new PluginMessage(
                                    this.user.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand",
                                    brandBuffer,
                                    this.handshakeHandler.isServerForge()
                            )
                    );
                    brandBuffer.release();
                }
            } else {
                if (this.user.getServer() != null) {
                    this.user.getServer().setObsolete(true);
                    this.user.getTabListHandler().onServerChange();
                    this.user.getServerSentScoreboard().clear();

                    for (UUID bossBar : this.user.getSentBossBars()) {
                        this.user.unsafe().sendPacket(new BossBar(bossBar, 1));
                    }

                    this.user.getSentBossBars().clear();
                }

                this.user.unsafe().sendPacket(
                        new Respawn(
                                login.getDimension(),
                                login.getWorldName(),
                                login.getSeed(),
                                login.getDifficulty(),
                                login.getGameMode(),
                                login.getPreviousGameMode(),
                                login.getLevelType(),
                                login.isDebug(),
                                login.isFlat(),
                                false,
                                login.getDeathLocation()
                        )
                );
                if (this.user.getServer() != null) {
                    this.user.getServer().disconnect("Quitting");
                }
            }

            this.user.setDimension(login.getDimension());
        }

        if (this.user.isJoinRequiresRewrite()) {
            this.user.setJoinRequiresRewrite(false);
            this.user.setEntityRewrite(EntityMap_Dummy.INSTANCE);
        }

        if (!this.user.isActive()) {
            serverConnection.disconnect("Quitting");
            if (!XCord.isLogFilterEnabled()) {
                this.bungee.getLogger().warning("No client connected for pending server!");
            }
        } else {
            this.target.addPlayer(this.user);
            this.user.getPendingConnects().remove(this.target);
            this.user.setServerJoinQueue(null);
            this.user.setDimensionChange(false);
            BungeeServerInfo previousServer = this.user.getServer() == null ? null : this.user.getServer().getInfo();
            this.user.setServer(serverConnection);
            this.ch.getHandle().pipeline().get(HandlerBoss.class)
                    .setHandler(new DownstreamBridge(this.bungee, this.user, serverConnection));
            this.bungee.getPluginManager().callEvent(new ServerSwitchEvent(this.user, previousServer));
            this.thisState = ServerConnector.State.FINISHED;
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(EncryptionRequest encryptionRequest) throws Exception {
        throw new QuietException("Server is online mode!");
    }

    @Override
    public void handle(Kick kick) throws Exception {
        ServerInfo nextServer = this.user.updateAndGetNextServer(this.target);
        ServerKickEvent kickEvent = new ServerKickEvent(
                this.user,
                this.target,
                ComponentSerializer.parse(kick.getMessage()),
                nextServer,
                ServerKickEvent.State.CONNECTING,
                ServerKickEvent.Cause.SERVER
        );
        if (kickEvent.getKickReason().toLowerCase(Locale.ROOT).contains("outdated") && nextServer != null) {
            kickEvent.setCancelled(true);
        }

        if (!this.user.isPassedThroughSpoofedServer()) {
            this.bungee.getPluginManager().callEvent(kickEvent);
        }

        if (kickEvent.isCancelled() && kickEvent.getCancelServer() != null) {
            this.obsolete = true;
            this.user.connect(kickEvent.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
            throw CancelSendSignal.INSTANCE;
        }

        String kickReason = this.bungee.getTranslation("connect_kick", this.target.getName(), kickEvent.getKickReason());
        if (this.user.isDimensionChange()) {
            this.user.disconnect(kickReason);
        } else if (!this.user.isPassedThroughSpoofedServer()
                && this.user.getServer() != null
                && !(this.user.getServer() instanceof SpoofedServerConnection)) {
            this.user.sendMessage(kickReason);
        } else {
            this.user.disconnect(kickReason);
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (BungeeCord.getInstance().config.isForgeSupport()) {
            if (pluginMessage.getTag().equals("REGISTER")) {
                Set<String> registeredChannels = ForgeUtils.readRegisteredChannels(pluginMessage);
                boolean forgeRegistered = false;
                for (String channel : registeredChannels) {
                    if (!channel.equals("FML|HS")) {
                        continue;
                    }

                    if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer())
                            && this.user.getForgeClientHandler().isHandshakeComplete()) {
                        this.user.getForgeClientHandler().resetHandshake();
                    }

                    forgeRegistered = true;
                    break;
                }

                if (forgeRegistered && !this.handshakeHandler.isServerForge()) {
                    this.handshakeHandler.setServerAsForgeServer();
                    this.user.setForgeServerHandler(this.handshakeHandler);
                }
            }

            if (pluginMessage.getTag().equals("FML|HS") || pluginMessage.getTag().equals("FORGE")) {
                this.handshakeHandler.handle(pluginMessage);
                if (this.user.getForgeClientHandler().checkUserOutdated()) {
                    this.ch.close();
                    this.user.getPendingConnects().remove(this.target);
                }

                throw CancelSendSignal.INSTANCE;
            }
        }

        this.user.unsafe().sendPacket(pluginMessage);
    }

    @Override
    public String toString() {
        return "[" + this.user.getName() + "|" + this.user.getAddress() + "] <-> ServerConnector [" + this.target.getName() + "]";
    }

    @Override
    public void handle(LoginPayloadRequest loginPayloadRequest) {
        this.ch.write(new LoginPayloadResponse(loginPayloadRequest.getId(), null));
    }

    public ServerConnector(ProxyServer bungee, UserConnection user, BungeeServerInfo target) {
        this.bungee = bungee;
        this.user = user;
        this.target = target;
    }

    public ForgeServerHandler getHandshakeHandler() {
        return this.handshakeHandler;
    }

    private static enum State {
        LOGIN_SUCCESS,
        ENCRYPT_RESPONSE,
        LOGIN,
        FINISHED;
    }
}
