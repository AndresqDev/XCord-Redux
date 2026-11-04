package xcord.antibot;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.RawDataPacket;
import xcord.network.SpoofedServerConnection;

public class LegacyBotHandler extends BotCheckHandler {
    private boolean sentKeepAlive = false;
    private long challengeTime;

    public LegacyBotHandler(InitialHandler initialHandler, Runnable callback, UserConnection userConnection, int checkId, int protocolVersion) {
        super(initialHandler, userConnection, checkId, protocolVersion, callback);
        RawDataPacket rawDataPacket = DBP_REGISTRY.get(protocolVersion);
        if (rawDataPacket != null) {
            this.writePacket(userConnection.getCh().getHandle(), rawDataPacket.getRawData().copy());
            userConnection.setClientEntityId(-100);
            userConnection.setServerEntityId(-100);
            userConnection.setDimension(LOGIN_DIMENSIONS.get(protocolVersion));
            userConnection.setDimensionChange(true);
        } else {
            this.verified = true;
            this.sentPayload = true;
            this.sentInitials = true;
            callback.run();
        }
    }

    @Override
    public void completeVerification() {
        this.user.setServer(new SpoofedServerConnection(null));
        this.runCallback();
    }

    @Override
    public boolean shouldHandle(PacketWrapper packetWrapper) throws Exception {
        if (!this.sentKeepAlive) {
            this.sentKeepAlive = true;
            this.user.getCh().write(new KeepAlive(this.checkId));
            this.challengeTime = System.currentTimeMillis();
        }
        return super.shouldHandle(packetWrapper);
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
        if (keepAlive.getRandomId() == (long)this.checkId && !this.verified && this.sentKeepAlive && this.getPing() <= 0) {
            this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.challengeTime));
            this.verified = true;
        } else {
            super.handle(keepAlive);
        }
    }

    @Override
    public String toString() {
        return "[" + this.user.getAddress() + "|" + this.user.getName() + "] -> LegacyBotHandler";
    }
}
