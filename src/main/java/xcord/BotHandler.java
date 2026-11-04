package xcord;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.RawDataPacket;
import xcord.BotCheckHandler;
import xcord.SpoofedServerConnection;

public class BotHandler
extends BotCheckHandler {
    private UserConnection user;
    private boolean sentRawData = false;
    private long challengeTime;

    public BotHandler(InitialHandler initialHandler, Runnable runnable, UserConnection userConnection, int n, int n2) {
        super(initialHandler, userConnection, n, n2, runnable);
        this.user = userConnection;
        RawDataPacket rawDataPacket = (RawDataPacket)DBP_REGISTRY.get(n2);
        if (rawDataPacket != null) {
            this.writePacket(userConnection.getCh().getHandle(), rawDataPacket.getRawData().copy());
            userConnection.setClientEntityId(-100);
            userConnection.setServerEntityId(-100);
            userConnection.setDimension(LOGIN_DIMENSIONS.get(n2));
            userConnection.setDimensionChange(true);
            this.sentRawData = true;
        }
        this.user.getCh().getHandle().writeAndFlush(new KeepAlive(n));
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
        return super.isVerified() || !this.sentRawData && this.verified;
    }

    @Override
    public String toString() {
        return "[" + this.user.getAddress() + "|" + this.user.getName() + "] -> BotHandler";
    }
}

