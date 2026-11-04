package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.List;
import net.md_5.bungee.protocol.OverflowPacketException;
import xcord.VarIntFrameDecoder;
import xcord.XCord;

public class AntiDdosFrameDecoder
extends VarIntFrameDecoder {
    private int packetCount = 0;
    private int bytesThisSecond = 0;
    private int lastTick = 0;
    private final int maxBytesPerSecond;
    private final int maxPacketsPerSecond;
    private boolean firstPacket = true;
    private static final OverflowPacketException TOO_MUCH_DATA = new OverflowPacketException("Too much data");

    public AntiDdosFrameDecoder(int n, int n2) {
        this.maxBytesPerSecond = n;
        this.maxPacketsPerSecond = n2;
    }

    public AntiDdosFrameDecoder setFirstPacketCheck(boolean bl) {
        this.firstPacket = bl;
        return this;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (!channelHandlerContext.channel().isActive()) {
            byteBuf.clear();
            return;
        }
        if (this.firstPacket && byteBuf.readableBytes() > 10) {
            this.firstPacket = false;
            if (byteBuf.readableBytes() > XCord.getConfig().getAntiDdosDuringLoginInitialBufferLimitBytes()) {
                String string = ((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
                System.out.println("Anti-DDOS> " + string + " sent an initial " + byteBuf.readableBytes() + " payload (Terminated)");
                if (XCord.getConfig().getAntiDdosBlacklist()) {
                    XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress());
                }
                byteBuf.clear();
                throw TOO_MUCH_DATA;
            }
        }
        if (XCord.getTick() != this.lastTick) {
            this.packetCount = 0;
            this.bytesThisSecond = 0;
            this.lastTick = XCord.getTick();
        }
        if ((this.bytesThisSecond += byteBuf.readableBytes()) > this.maxBytesPerSecond) {
            String string = ((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println("Anti-DDOS> " + string + " sent " + this.bytesThisSecond + " bytes in a second (Terminated)");
            if (XCord.getConfig().getAntiDdosBlacklist()) {
                XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress());
            }
            byteBuf.clear();
            throw TOO_MUCH_DATA;
        }
        if (++this.packetCount > this.maxPacketsPerSecond) {
            String string = ((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println("Anti-DDOS: " + string + " sent over " + this.packetCount + "+ packets in a second (Terminated)");
            if (XCord.getConfig().getAntiDdosBlacklist()) {
                XCord.getBlacklistManager().isBlacklisted(((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress());
            }
            byteBuf.clear();
            throw TOO_MUCH_DATA;
        }
        super.decode(channelHandlerContext, byteBuf, list);
    }
}

