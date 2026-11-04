package xcord.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.List;
import net.md_5.bungee.protocol.OverflowPacketException;
import xcord.XCord;
import xcord.protocol.VarIntFrameDecoder;

public class AntiDdosFrameDecoder extends VarIntFrameDecoder {
    private static final OverflowPacketException TOO_MUCH_DATA = new OverflowPacketException("Too much data");

    private int packetCount = 0;
    private int bytesThisSecond = 0;
    private int lastTick = 0;
    private final int maxBytesPerSecond;
    private final int maxPacketsPerSecond;
    private boolean firstPacket = true;

    public AntiDdosFrameDecoder(int maxBytesPerSecond, int maxPacketsPerSecond) {
        this.maxBytesPerSecond = maxBytesPerSecond;
        this.maxPacketsPerSecond = maxPacketsPerSecond;
    }

    public AntiDdosFrameDecoder setFirstPacketCheck(boolean enabled) {
        this.firstPacket = enabled;
        return this;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> out) {
        if (!context.channel().isActive()) {
            buffer.clear();
            return;
        }
        if (this.firstPacket && buffer.readableBytes() > 10) {
            this.firstPacket = false;
            if (buffer.readableBytes() > XCord.getConfig().getAntiDdosDuringLoginInitialBufferLimitBytes()) {
                String remoteHost = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
                System.out.println("Anti-DDOS> " + remoteHost + " sent an initial " + buffer.readableBytes() + " payload (Terminated)");
                if (XCord.getConfig().getAntiDdosBlacklist()) {
                    XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress) context.channel().remoteAddress()).getAddress());
                }
                buffer.clear();
                throw TOO_MUCH_DATA;
            }
        }
        if (XCord.getTick() != this.lastTick) {
            this.packetCount = 0;
            this.bytesThisSecond = 0;
            this.lastTick = XCord.getTick();
        }
        if ((this.bytesThisSecond += buffer.readableBytes()) > this.maxBytesPerSecond) {
            String remoteHost = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println("Anti-DDOS> " + remoteHost + " sent " + this.bytesThisSecond + " bytes in a second (Terminated)");
            if (XCord.getConfig().getAntiDdosBlacklist()) {
                XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress) context.channel().remoteAddress()).getAddress());
            }
            buffer.clear();
            throw TOO_MUCH_DATA;
        }
        if (++this.packetCount > this.maxPacketsPerSecond) {
            String remoteHost = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println("Anti-DDOS: " + remoteHost + " sent over " + this.packetCount + "+ packets in a second (Terminated)");
            if (XCord.getConfig().getAntiDdosBlacklist()) {
                XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress) context.channel().remoteAddress()).getAddress());
            }
            buffer.clear();
            throw TOO_MUCH_DATA;
        }
        super.decode(context, buffer, out);
    }
}
