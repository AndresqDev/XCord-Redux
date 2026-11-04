package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import xcord.XCord;
import xcord.antibot.ConnectionStats;
import xcord.antibot.NameValidator;
import xcord.network.SpoofedServerConnection;

public class UpstreamBridge extends PacketHandler {
    private final ProxyServer bungee;
    private final UserConnection con;
    private long lastTabCompletion = -1L;
    private boolean handleDisconnects = true;
    protected boolean ignoreIncomming = false;

    public UpstreamBridge(ProxyServer bungee, UserConnection con) {
        this.bungee = bungee;
        this.con = con;
    }

    public boolean isHandleDisconnects() {
        return this.handleDisconnects;
    }

    public void setHandleDisconnects(boolean handleDisconnects) {
        this.handleDisconnects = handleDisconnects;
    }

    @Override
    public void exception(Throwable throwable) throws Exception {
        this.con.disconnect(Util.exception(throwable));
    }

    @Override
    public void disconnected(ChannelWrapper channel) throws Exception {
        PlayerDisconnectEvent disconnectEvent = new PlayerDisconnectEvent(this.con);
        this.bungee.getPluginManager().callEvent(disconnectEvent);
        if (this.handleDisconnects) {
            this.con.getTabListHandler().onDisconnect();
            BungeeCord.getInstance().removeConnection(this.con);
            if (this.con.getServer() != null) {
                PlayerListItem playerListItem = new PlayerListItem();
                playerListItem.setAction(PlayerListItem.Action.REMOVE_PLAYER);
                PlayerListItem.Item item = new PlayerListItem.Item();
                item.setUuid(this.con.getUniqueId());
                playerListItem.setItems(new PlayerListItem.Item[]{item});

                for (ProxiedPlayer player : this.con.getServer().getInfo().getPlayers()) {
                    if (ProtocolConstants.isAfterOrEq(player.getPendingConnection().getVersion(), 47)) {
                        player.unsafe().sendPacket(playerListItem);
                    }
                }

                this.con.getServer().disconnect("Quitting");
            }
        }
    }

    @Override
    public void writabilityChanged(ChannelWrapper channel) throws Exception {
        if (this.con.getServer() != null) {
            Channel serverChannel = this.con.getServer().getCh().getHandle();
            if (channel.getHandle().isWritable()) {
                serverChannel.config().setAutoRead(true);
            } else {
                serverChannel.config().setAutoRead(false);
            }
        }
    }

    public void killConnection() {
        this.ignoreIncomming = true;
        this.con.forceClose();
    }

    @Override
    public void handle(Handshake handshake) throws Exception {
        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public boolean shouldHandle(PacketWrapper packet) throws Exception {
        if (this.ignoreIncomming) {
            return false;
        }

        if (!this.con.isPassedBotChecks()) {
            this.con.incrementSentPackets();
        }

        return this.con.getServer() != null || packet.packet instanceof PluginMessage;
    }

    @Override
    public void handle(PacketWrapper packet) throws Exception {
        if (this.con.getServer() != null) {
            EntityMap entityMap = this.con.getEntityRewrite();
            if (entityMap != null) {
                entityMap.rewriteServerbound(
                        packet.buf,
                        this.con.getClientEntityId(),
                        this.con.getServerEntityId(),
                        this.con.getPendingConnection().getVersion()
                );
            }

            this.con.getServer().getCh().write(packet);
        }
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
        ServerConnection.KeepAliveData keepAliveData = this.con.getServer().getKeepAlives().peek();
        if (keepAliveData != null && keepAlive.getRandomId() == keepAliveData.getId()) {
            Preconditions.checkState(
                    keepAliveData == this.con.getServer().getKeepAlives().poll(), "keepalive queue mismatch"
            );
            int ping = (int) (System.currentTimeMillis() - keepAliveData.getTime());
            this.con.getTabListHandler().onPingChange(ping);
            this.con.setPing(ping);
        } else {
            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(ClientChat clientChat) throws Exception {
        this.handleChat(clientChat.getMessage());
    }

    @Override
    public void handle(ClientCommand clientCommand) throws Exception {
        this.handleChat("/" + clientCommand.getCommand());
    }

    @Override
    public void handle(Chat chat) throws Exception {
        String message = this.handleChat(chat.getMessage());
        if (message != null) {
            chat.setMessage(message);
            this.con.getServer().unsafe().sendPacket(chat);
        }

        throw CancelSendSignal.INSTANCE;
    }

    private String handleChat(String message) {
        if (this.handleDisconnects && this.con.getServer() != null && !(this.con.getServer() instanceof SpoofedServerConnection)) {
            if ((System.currentTimeMillis() - this.con.getInitializeTime() < (long) XCord.getConfig().getAntiBotChatForceDelay()
                            || this.con.getSettings() == null)
                    && XCord.getConfig().getAntiBotChatCheck()) {
                if (!XCord.getConfig().getAntiBotDnsCheckIsStrict1()
                        || message.length() >= 100
                        || !message.contains("PickaxeChat")) {
                    throw CancelSendSignal.INSTANCE;
                }

                BungeeCord.getInstance().getLogger().info("PickaxeChat client joined from " + this.con.getName());
                this.con.setWhitelistedFromAntibot(true);
                this.con.setPassedBotChecks(true);
            }

            for (int index = 0; index < message.length(); index++) {
                char character = message.charAt(index);
                if (!NameValidator.isAllowedChar(character)) {
                    this.con.disconnect(this.bungee.getTranslation("illegal_chat_characters", Util.unicode(character)));
                    throw CancelSendSignal.INSTANCE;
                }
            }

            ChatEvent chatEvent = new ChatEvent(this.con, this.con.getServer(), message);
            if (!this.bungee.getPluginManager().callEvent(chatEvent).isCancelled()) {
                message = chatEvent.getMessage();
                if (!chatEvent.isCommand() || !this.bungee.getPluginManager().dispatchCommand(this.con, message.substring(1))) {
                    return message;
                }
            }

            throw CancelSendSignal.INSTANCE;
        }

        throw CancelSendSignal.INSTANCE;
    }

    @Override
    public void handle(TabCompleteRequest tabCompleteRequest) throws Exception {
        if (this.bungee.getConfig().getTabThrottle() > 0
                && this.con.getPendingConnection().getVersion() >= 393
                && !this.bungee.getConfig().isDisableModernTabLimiter()) {
            long now = System.currentTimeMillis();
            if (this.lastTabCompletion > 0L && now - this.lastTabCompletion <= (long) this.bungee.getConfig().getTabThrottle()) {
                throw CancelSendSignal.INSTANCE;
            }

            this.lastTabCompletion = now;
        }

        ArrayList<String> suggestions = new ArrayList<>();
        boolean commandHandled = false;
        if (tabCompleteRequest.getCursor().startsWith("/")) {
            commandHandled = this.bungee.getPluginManager()
                    .dispatchCommand(this.con, tabCompleteRequest.getCursor().substring(1), suggestions);
        }

        TabCompleteEvent tabCompleteEvent = new TabCompleteEvent(
                this.con, this.con.getServer(), tabCompleteRequest.getCursor(), suggestions
        );
        this.bungee.getPluginManager().callEvent(tabCompleteEvent);
        if (tabCompleteEvent.isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        List<String> eventSuggestions = tabCompleteEvent.getSuggestions();
        if (eventSuggestions.isEmpty()) {
            if (commandHandled) {
                throw CancelSendSignal.INSTANCE;
            }
        } else {
            if (this.con.getPendingConnection().getVersion() < 393) {
                this.con.unsafe().sendPacket(new TabCompleteResponse(eventSuggestions));
            } else {
                int start = tabCompleteRequest.getCursor().lastIndexOf(32) + 1;
                int end = tabCompleteRequest.getCursor().length();
                StringRange range = StringRange.between(start, end);
                LinkedList<Suggestion> suggestionList = new LinkedList<>();

                for (String suggestion : eventSuggestions) {
                    suggestionList.add(new Suggestion(range, suggestion));
                }

                this.con.unsafe()
                        .sendPacket(new TabCompleteResponse(tabCompleteRequest.getTransactionId(), new Suggestions(range, suggestionList)));
            }

            throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(ClientSettings clientSettings) throws Exception {
        this.con.setSettings(clientSettings);
        SettingsChangedEvent settingsChangedEvent = new SettingsChangedEvent(this.con);
        this.bungee.getPluginManager().callEvent(settingsChangedEvent);
    }

    @Override
    public void handle(PluginMessage pluginMessage) throws Exception {
        if (pluginMessage.getTag().equals("BungeeCord")) {
            throw CancelSendSignal.INSTANCE;
        }

        if (pluginMessage.getTag().equals("FML|HS")
                || pluginMessage.getTag().equals("FML")
                || pluginMessage.getTag().equals("LMC")) {
            ConnectionStats stats = XCord.getAntiBotManager().getStats(this.con.getAddress().getAddress().getHostAddress());
            stats.setWhitelisted(true);
        }

        if (BungeeCord.getInstance().config.isForgeSupport()) {
            if (pluginMessage.getTag().equals("FML") && pluginMessage.getStream().readUnsignedByte() == 1) {
                throw CancelSendSignal.INSTANCE;
            }

            if (pluginMessage.getTag().equals("FML|HS")) {
                this.con.getForgeClientHandler().handle(pluginMessage);
                throw CancelSendSignal.INSTANCE;
            }

            if (this.con.getServer() != null && !this.con.getServer().isForgeServer() && pluginMessage.getData().length > 32767) {
                throw CancelSendSignal.INSTANCE;
            }
        }

        PluginMessageEvent pluginMessageEvent = new PluginMessageEvent(
                this.con, this.con.getServer(), pluginMessage.getTag(), pluginMessage.getData().clone()
        );
        if (this.bungee.getPluginManager().callEvent(pluginMessageEvent).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
        }

        this.con.getPendingConnection().relayMessage(pluginMessage);
    }

    @Override
    public String toString() {
        return "[" + this.con.getAddress() + "|" + this.con.getName() + "] -> UpstreamBridge";
    }
}
