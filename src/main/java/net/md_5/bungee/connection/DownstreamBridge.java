package net.md_5.bungee.connection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import java.io.DataInput;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Position;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.EntityEffect;
import net.md_5.bungee.protocol.packet.EntityRemoveEffect;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ServerData;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.tab.TabList;
import xcord.XCord;

public class DownstreamBridge extends PacketHandler {
    private static final Command DUMMY_COMMAND = context -> 0;
    private final ProxyServer bungee;
    private final UserConnection con;
    private final ServerConnection server;

    @Override
    public void exception(Throwable throwable) throws Exception {
        if (!this.server.isObsolete()) {
            if (XCord.getConfig().getDebugOutputDownstreamErrors()) {
                throwable.printStackTrace();
            }

            ServerInfo nextServer = this.con.updateAndGetNextServer(this.server.getInfo());
            ServerKickEvent kickEvent = this.bungee.getPluginManager().callEvent(
                    new ServerKickEvent(
                            this.con,
                            this.server.getInfo(),
                            TextComponent.fromLegacyText(this.bungee.getTranslation("server_went_down")),
                            nextServer,
                            ServerKickEvent.State.CONNECTED,
                            ServerKickEvent.Cause.EXCEPTION
                    )
            );
            if (kickEvent.isCancelled() && kickEvent.getCancelServer() != null) {
                this.server.setObsolete(true);
                this.con.connectNow(kickEvent.getCancelServer(), ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
            } else {
                this.con.disconnect0(kickEvent.getKickReasonComponent());
            }
        }
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        this.server.getInfo().removePlayer(this.con);
        if (this.bungee.getReconnectHandler() != null) {
            this.bungee.getReconnectHandler().setServer(this.con);
        }

        if (!this.server.isObsolete()) {
            ServerInfo nextServer = this.con.updateAndGetNextServer(this.server.getInfo());
            ServerKickEvent kickEvent = this.bungee.getPluginManager().callEvent(
                    new ServerKickEvent(
                            this.con,
                            this.server.getInfo(),
                            TextComponent.fromLegacyText(this.bungee.getTranslation("lost_connection")),
                            nextServer,
                            ServerKickEvent.State.CONNECTED,
                            ServerKickEvent.Cause.LOST_CONNECTION
                    )
            );
            if (kickEvent.isCancelled() && kickEvent.getCancelServer() != null) {
                this.server.setObsolete(true);
                this.con.connectNow(kickEvent.getCancelServer());
            } else {
                this.con.disconnect0(kickEvent.getKickReasonComponent());
            }
        }

        ServerDisconnectEvent disconnectEvent = new ServerDisconnectEvent(this.con, this.server.getInfo());
        this.bungee.getPluginManager().callEvent(disconnectEvent);
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        return !this.server.isObsolete();
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        EntityMap entityMap = this.con.getEntityRewrite();
        if (entityMap != null) {
            entityMap.rewriteClientbound(
                    packet.buf,
                    this.con.getServerEntityId(),
                    this.con.getClientEntityId(),
                    this.con.getPendingConnection().getVersion()
            );
        }

        this.con.appendBufferedPacket(packet);
    }

    @Override
    public void readCompleted() throws Exception {
        this.con.flush();
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
        int timeout = this.bungee.getConfig().getTimeout();
        if (timeout <= 0 || this.server.getKeepAlives().size() < timeout / 50) {
            this.server.getKeepAlives()
                    .add(new ServerConnection.KeepAliveData(keepAlive.getRandomId(), System.currentTimeMillis()));
        }
    }

    @Override
    public void handle(PlayerListItem playerListItem) throws Exception {
        boolean disableTabListRewrite = this.bungee.getConfig().isDisableTabListRewrite();
        this.con.getTabListHandler()
                .onUpdate(disableTabListRewrite ? playerListItem : TabList.rewrite(playerListItem));
        if (!disableTabListRewrite) {
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(ScoreboardObjective scoreboardObjective) throws Exception {
        if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
            Scoreboard scoreboard = this.con.getServerSentScoreboard();
            switch (scoreboardObjective.getAction()) {
                case 0:
                    scoreboard.addObjective(
                            new Objective(
                                    scoreboardObjective.getName(),
                                    scoreboardObjective.getValue(),
                                    scoreboardObjective.getType() != null ? scoreboardObjective.getType().toString() : null
                            )
                    );
                    break;
                case 1:
                    scoreboard.removeObjective(scoreboardObjective.getName());
                    break;
                case 2:
                    Objective objective = scoreboard.getObjective(scoreboardObjective.getName());
                    if (objective != null) {
                        objective.setValue(scoreboardObjective.getValue());
                        objective.setType(
                                scoreboardObjective.getType() != null ? scoreboardObjective.getType().toString() : null
                        );
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown objective action: " + scoreboardObjective.getAction());
            }
        }
    }

    @Override
    public void handle(ScoreboardScore scoreboardScore) throws Exception {
        if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
            Scoreboard scoreboard = this.con.getServerSentScoreboard();
            switch (scoreboardScore.getAction()) {
                case 0:
                    Score score = new Score(scoreboardScore.getItemName(), scoreboardScore.getScoreName(), scoreboardScore.getValue());
                    scoreboard.removeScore(scoreboardScore.getItemName());
                    scoreboard.addScore(score);
                    break;
                case 1:
                    scoreboard.removeScore(scoreboardScore.getItemName());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown scoreboard action: " + scoreboardScore.getAction());
            }
        }
    }

    @Override
    public void handle(ScoreboardDisplay scoreboardDisplay) throws Exception {
        if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
            Scoreboard scoreboard = this.con.getServerSentScoreboard();
            scoreboard.setName(scoreboardDisplay.getName());
            scoreboard.setPosition(Position.values()[scoreboardDisplay.getPosition()]);
        }
    }

    @Override
    public void handle(Team team) throws Exception {
        if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
            Scoreboard scoreboard = this.con.getServerSentScoreboard();
            if (team.getMode() == 1) {
                scoreboard.removeTeam(team.getName());
                return;
            }

            net.md_5.bungee.api.score.Team scoreboardTeam;
            if (team.getMode() == 0) {
                scoreboardTeam = new net.md_5.bungee.api.score.Team(team.getName());
                scoreboard.addTeam(scoreboardTeam);
            } else {
                scoreboardTeam = scoreboard.getTeam(team.getName());
            }

            if (scoreboardTeam != null) {
                if (team.getMode() == 0 || team.getMode() == 2) {
                    scoreboardTeam.setDisplayName(team.getDisplayName());
                    scoreboardTeam.setPrefix(team.getPrefix());
                    scoreboardTeam.setSuffix(team.getSuffix());
                    scoreboardTeam.setFriendlyFire(team.getFriendlyFire());
                    scoreboardTeam.setNameTagVisibility(team.getNameTagVisibility());
                    scoreboardTeam.setCollisionRule(team.getCollisionRule());
                    scoreboardTeam.setColor(team.getColor());
                }

                if (team.getPlayers() != null) {
                    for (String player : team.getPlayers()) {
                        if (team.getMode() == 0 || team.getMode() == 3) {
                            scoreboardTeam.addPlayer(player);
                        } else if (team.getMode() == 4) {
                            scoreboardTeam.removePlayer(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        PluginMessageEvent pluginMessageEvent = new PluginMessageEvent(
                this.server, this.con, pluginMessage.getTag(), pluginMessage.getData().clone()
        );
        if (this.bungee.getPluginManager().callEvent(pluginMessageEvent).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        if (pluginMessage.getTag()
                .equals(this.con.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand")) {
            if (ProtocolConstants.isAfterOrEq(this.con.getPendingConnection().getVersion(), 47)) {
                try {
                    ByteBuf brandBuffer = Unpooled.wrappedBuffer(pluginMessage.getData());
                    DefinedPacket.readString(brandBuffer);
                    brandBuffer.release();
                    brandBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
                    DefinedPacket.writeString(XCord.getConfig().getGeneralXCord(), brandBuffer);
                    pluginMessage.setData(brandBuffer);
                    brandBuffer.release();
                } catch (Exception exception) {
                    return;
                }
            } else {
                pluginMessage.setData(XCord.getConfig().getGeneralXCord().getBytes(StandardCharsets.UTF_8));
            }

            this.con.unsafe().sendPacket(pluginMessage);
            throw CancelSendSignal.INSTANCE;
        }

        if (!pluginMessage.getTag().equals("BungeeCord")) {
            return;
        }

        DataInput input = pluginMessage.getStream();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        String subChannel = input.readUTF();
        if (subChannel.equals("ForwardToPlayer")) {
            ProxiedPlayer target = this.bungee.getPlayer(input.readUTF());
            if (target != null) {
                String channel = input.readUTF();
                short length = input.readShort();
                byte[] data = new byte[length];
                input.readFully(data);
                output.writeUTF(channel);
                output.writeShort(data.length);
                output.write(data);
                byte[] forwardedData = output.toByteArray();
                target.getServer().sendData("BungeeCord", forwardedData);
            }

            output = null;
        }

        if (subChannel.equals("Forward")) {
            String target = input.readUTF();
            String channel = input.readUTF();
            short length = input.readShort();
            byte[] data = new byte[length];
            input.readFully(data);
            output.writeUTF(channel);
            output.writeShort(data.length);
            output.write(data);
            byte[] forwardedData = output.toByteArray();
            output = null;
            if (target.equals("ALL")) {
                for (ServerInfo serverInfo : this.bungee.getServers().values()) {
                    if (serverInfo != this.server.getInfo()) {
                        serverInfo.sendData("BungeeCord", forwardedData);
                    }
                }
            } else if (target.equals("ONLINE")) {
                for (ServerInfo serverInfo : this.bungee.getServers().values()) {
                    if (serverInfo != this.server.getInfo()) {
                        serverInfo.sendData("BungeeCord", forwardedData, false);
                    }
                }
            } else {
                ServerInfo serverInfo = this.bungee.getServerInfo(target);
                if (serverInfo != null) {
                    serverInfo.sendData("BungeeCord", forwardedData);
                }
            }
        }

        if (subChannel.equals("Connect")) {
            ServerInfo serverInfo = this.bungee.getServerInfo(input.readUTF());
            if (serverInfo != null) {
                this.con.connect(serverInfo, ServerConnectEvent.Reason.PLUGIN_MESSAGE);
            }
        }

        if (subChannel.equals("ConnectOther")) {
            ProxiedPlayer player = this.bungee.getPlayer(input.readUTF());
            if (player != null) {
                ServerInfo serverInfo = this.bungee.getServerInfo(input.readUTF());
                if (serverInfo != null) {
                    player.connect(serverInfo);
                }
            }
        }

        if (subChannel.equals("IP")) {
            output.writeUTF("IP");
            if (this.con.getSocketAddress() instanceof InetSocketAddress) {
                output.writeUTF(this.con.getAddress().getHostString());
                output.writeInt(this.con.getAddress().getPort());
            } else {
                output.writeUTF("unix://" + ((DomainSocketAddress) this.con.getSocketAddress()).path());
                output.writeInt(0);
            }
        }

        if (subChannel.equals("IPOther")) {
            ProxiedPlayer player = this.bungee.getPlayer(input.readUTF());
            if (player != null) {
                output.writeUTF("IPOther");
                output.writeUTF(player.getName());
                if (player.getSocketAddress() instanceof InetSocketAddress) {
                    InetSocketAddress address = (InetSocketAddress) player.getSocketAddress();
                    output.writeUTF(address.getHostString());
                    output.writeInt(address.getPort());
                } else {
                    output.writeUTF("unix://" + ((DomainSocketAddress) player.getSocketAddress()).path());
                    output.writeInt(0);
                }
            }
        }

        if (subChannel.equals("PlayerCount")) {
            String target = input.readUTF();
            output.writeUTF("PlayerCount");
            if (target.equals("ALL")) {
                output.writeUTF("ALL");
                output.writeInt(this.bungee.getOnlineCount());
            } else {
                ServerInfo serverInfo = this.bungee.getServerInfo(target);
                if (serverInfo != null) {
                    output.writeUTF(serverInfo.getName());
                    output.writeInt(serverInfo.getPlayers().size());
                }
            }
        }

        if (subChannel.equals("PlayerList")) {
            String target = input.readUTF();
            output.writeUTF("PlayerList");
            if (target.equals("ALL")) {
                output.writeUTF("ALL");
                output.writeUTF(Util.csv(this.bungee.getPlayers()));
            } else {
                ServerInfo serverInfo = this.bungee.getServerInfo(target);
                if (serverInfo != null) {
                    output.writeUTF(serverInfo.getName());
                    output.writeUTF(Util.csv(serverInfo.getPlayers()));
                }
            }
        }

        if (subChannel.equals("GetServers")) {
            output.writeUTF("GetServers");
            output.writeUTF(Util.csv(this.bungee.getServers().keySet()));
        }

        if (subChannel.equals("Message")) {
            String target = input.readUTF();
            String message = input.readUTF();
            if (target.equals("ALL")) {
                for (ProxiedPlayer player : this.bungee.getPlayers()) {
                    player.sendMessage(message);
                }
            } else {
                ProxiedPlayer player = this.bungee.getPlayer(target);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        }

        if (subChannel.equals("MessageRaw")) {
            String target = input.readUTF();
            BaseComponent[] message = ComponentSerializer.parse(input.readUTF());
            if (target.equals("ALL")) {
                for (ProxiedPlayer player : this.bungee.getPlayers()) {
                    player.sendMessage(message);
                }
            } else {
                ProxiedPlayer player = this.bungee.getPlayer(target);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        }

        if (subChannel.equals("GetServer")) {
            output.writeUTF("GetServer");
            output.writeUTF(this.server.getInfo().getName());
        }

        if (subChannel.equals("UUID")) {
            output.writeUTF("UUID");
            output.writeUTF(this.con.getUUID());
        }

        if (subChannel.equals("UUIDOther")) {
            ProxiedPlayer player = this.bungee.getPlayer(input.readUTF());
            if (player != null) {
                output.writeUTF("UUIDOther");
                output.writeUTF(player.getName());
                output.writeUTF(player.getUUID());
            }
        }

        if (subChannel.equals("ServerIP")) {
            ServerInfo serverInfo = this.bungee.getServerInfo(input.readUTF());
            if (serverInfo != null && !serverInfo.getAddress().isUnresolved()) {
                output.writeUTF("ServerIP");
                output.writeUTF(serverInfo.getName());
                output.writeUTF(serverInfo.getAddress().getAddress().getHostAddress());
                output.writeShort(serverInfo.getAddress().getPort());
            }
        }

        if (subChannel.equals("KickPlayer")) {
            ProxiedPlayer player = this.bungee.getPlayer(input.readUTF());
            if (player != null) {
                String reason = input.readUTF();
                player.disconnect(new TextComponent(reason));
            }
        }

        if (output != null) {
            byte[] response = output.toByteArray();
            if (response.length != 0) {
                this.server.sendData("BungeeCord", response);
            }
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(Kick kick) throws Exception {
        ServerInfo nextServer = this.con.updateAndGetNextServer(this.server.getInfo());
        if (Objects.equals(this.server.getInfo(), nextServer)) {
            nextServer = null;
        }

        ServerKickEvent kickEvent = this.bungee.getPluginManager().callEvent(
                new ServerKickEvent(
                        this.con,
                        this.server.getInfo(),
                        ComponentSerializer.parse(kick.getMessage()),
                        nextServer,
                        ServerKickEvent.State.CONNECTED,
                        ServerKickEvent.Cause.SERVER
                )
        );
        if (kickEvent.isCancelled() && kickEvent.getCancelServer() != null) {
            this.con.connectNow(kickEvent.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
        } else {
            this.con.disconnect0(kickEvent.getKickReasonComponent());
        }

        this.server.setObsolete(true);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(SetCompression setCompression) throws Exception {
        this.server.getCh().setCompressionThreshold(setCompression.getThreshold());
    }

    @Override
    public void handle(TabCompleteResponse tabCompleteResponse) throws Exception {
        List<String> commands = tabCompleteResponse.getCommands();
        if (commands == null) {
            commands = Lists.transform(
                    tabCompleteResponse.getSuggestions().getList(), new Function<Suggestion, String>() {
                        public String apply(Suggestion suggestion) {
                            return suggestion.getText();
                        }
                    }
            );
        }

        TabCompleteResponseEvent responseEvent = new TabCompleteResponseEvent(
                this.server, this.con, new ArrayList<>(commands)
        );
        if (!this.bungee.getPluginManager().callEvent(responseEvent).isCancelled()) {
            if (!commands.equals(responseEvent.getSuggestions())) {
                if (tabCompleteResponse.getCommands() != null) {
                    tabCompleteResponse.setCommands(responseEvent.getSuggestions());
                } else {
                    final StringRange range = tabCompleteResponse.getSuggestions().getRange();
                    tabCompleteResponse.setSuggestions(
                            new Suggestions(
                                    range,
                                    Lists.transform(responseEvent.getSuggestions(), new Function<String, Suggestion>() {
                                        public Suggestion apply(String suggestion) {
                                            return new Suggestion(range, suggestion);
                                        }
                                    })
                            )
                    );
                }
            }

            this.con.unsafe().sendPacket(tabCompleteResponse);
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(BossBar bossBar) {
        switch (bossBar.getAction()) {
            case 0:
                this.con.getSentBossBars().add(bossBar.getUuid());
                break;
            case 1:
                this.con.getSentBossBars().remove(bossBar.getUuid());
        }
    }

    @Override
    public void handle(EntityEffect entityEffect) throws Exception {
        if (!this.con.isDisableEntityMetadataRewrite()) {
            if (this.con.getForgeClientHandler().isForgeUser() && !this.con.getForgeClientHandler().isHandshakeComplete()) {
                throw CancelSendSignal.INSTANCE;
            }

            this.con.getPotions().put(this.rewriteEntityId(entityEffect.getEntityId()), entityEffect.getEffectId());
        }
    }

    @Override
    public void handle(EntityRemoveEffect entityRemoveEffect) throws Exception {
        if (!this.con.isDisableEntityMetadataRewrite()) {
            this.con.getPotions().remove(this.rewriteEntityId(entityRemoveEffect.getEntityId()), entityRemoveEffect.getEffectId());
        }
    }

    private int rewriteEntityId(int entityId) {
        return entityId == this.con.getServerEntityId() ? this.con.getClientEntityId() : entityId;
    }

    @Override
    public void handle(Respawn respawn) {
        this.con.setDimension(respawn.getDimension());
    }

    @Override
    public void handle(Commands commandsPacket) throws Exception {
        boolean hasNewCommands = false;
        Map<String, net.md_5.bungee.api.plugin.Command> commands = new HashMap<>();

        for (Entry<String, net.md_5.bungee.api.plugin.Command> entry : this.bungee.getPluginManager().getCommands()) {
            if (!this.bungee.getDisabledCommands().contains(entry.getKey())
                    && commandsPacket.getRoot().getChild(entry.getKey()) == null
                    && entry.getValue().hasPermission(this.con)) {
                commands.put(entry.getKey(), entry.getValue());
            }
        }

        ProxyDefineCommandsEvent defineCommandsEvent = new ProxyDefineCommandsEvent(this.server, this.con, commands);
        this.bungee.getPluginManager().callEvent(defineCommandsEvent);

        for (Entry<String, net.md_5.bungee.api.plugin.Command> entry : defineCommandsEvent.getCommands().entrySet()) {
            CommandNode node = LiteralArgumentBuilder.literal(entry.getKey()).executes(DUMMY_COMMAND)
                    .then(
                            RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString())
                                    .suggests(Commands.SuggestionRegistry.ASK_SERVER)
                                    .executes(DUMMY_COMMAND)
                    )
                    .build();
            commandsPacket.getRoot().addChild(node);
            hasNewCommands = true;
        }

        if (hasNewCommands) {
            this.con.unsafe().sendPacket(commandsPacket);
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(ServerData serverData) throws Exception {
        serverData.setMotd(null);
        serverData.setIcon(null);
        this.con.unsafe().sendPacket(serverData);
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public String toString() {
        return "[" + this.con.getAddress() + "|" + this.con.getName() + "] <-> DownstreamBridge <-> [" + this.server.getInfo().getName() + "]";
    }

    public DownstreamBridge(ProxyServer bungee, UserConnection con, ServerConnection server) {
        this.bungee = bungee;
        this.con = con;
        this.server = server;
    }
}
