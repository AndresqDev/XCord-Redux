package xcord.antibot;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.RawDataPacket;
import xcord.network.SpoofedServerConnection;

public class BotHandler extends BotCheckHandler {
    private boolean sentRawData = false;
    private long challengeTime;

    public BotHandler(InitialHandler initialHandler, Runnable callback, UserConnection userConnection, int checkId, int protocolVersion) {
        super(initialHandler, userConnection, checkId, protocolVersion, callback);
        RawDataPacket rawDataPacket = DBP_REGISTRY.get(protocolVersion);
        if (rawDataPacket != null) {
            this.writePacket(userConnection.getCh().getHandle(), rawDataPacket.getRawData().copy());
            userConnection.setClientEntityId(-100);
            userConnection.setServerEntityId(-100);
            userConnection.setDimension(LOGIN_DIMENSIONS.get(protocolVersion));
            userConnection.setDimensionChange(true);
            this.sentRawData = true;
        }
        this.user.getCh().getHandle().writeAndFlush(new KeepAlive(checkId));
        this.challengeTime = System.currentTimeMillis();
    }

    @Override
    public void handle(KeepAlive keepAlive) throws Exception {
        if (keepAlive.getRandomId() == (long)this.checkId && !this.verified) {
            this.handlePing((int)Math.max(1L, System.currentTimeMillis() - this.challengeTime));
            this.verified = true;
        } else {
            super.handle(keepAlive);
        }
    }

    @Override
    public void completeVerification() {
        if (this.sentRawData) {
            this.user.setServer(new SpoofedServerConnection(null));
        }
        this.user.setDimensionChange(true);
        this.runCallback();
    }

    @Override
    public boolean isVerified() {
        return super.isVerified() || (!this.sentRawData && this.verified);
    }

    @Override
    public String toString() {
        return "[" + this.user.getAddress() + "|" + this.user.getName() + "] -> BotHandler";
    }
}
