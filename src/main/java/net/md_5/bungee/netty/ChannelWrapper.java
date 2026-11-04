package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Kick;
import xcord.XCord;
import xcord.network.CompressionDecoder;
import xcord.network.CompressionEncoder;
import xcord.network.MergedCompressionEncoder;

public class ChannelWrapper {
    private final Channel channel;
    private SocketAddress remoteAddress;
    private volatile boolean closed;
    private volatile boolean closing;
    private boolean beingForward;

    public ChannelWrapper(ChannelHandlerContext context) {
        this.channel = context.channel();
        this.remoteAddress = this.channel.remoteAddress() == null ? this.channel.parent().localAddress() : this.channel.remoteAddress();
    }

    public ChannelWrapper() {
        this.channel = null;
        this.remoteAddress = null;
    }

    public void setProtocol(Protocol protocol) {
        this.channel.pipeline().get(MinecraftDecoder.class).setProtocol(protocol);
        this.channel.pipeline().get(MinecraftEncoder.class).setProtocol(protocol);
    }

    public void setVersion(int protocolVersion) {
        this.channel.pipeline().get(MinecraftDecoder.class).setProtocolVersion(protocolVersion);
        this.channel.pipeline().get(MinecraftEncoder.class).setProtocolVersion(protocolVersion);
    }

    public void write(Object packet) {
        if (this.closed) {
            return;
        }
        if (packet instanceof PacketWrapper) {
            PacketWrapper wrapper = (PacketWrapper) packet;
            wrapper.setReleased(true);
            this.channel.writeAndFlush(wrapper.buf, this.channel.voidPromise());
        } else {
            this.channel.writeAndFlush(packet, this.channel.voidPromise());
        }
    }

    public void writeBuffered(Object packet) {
        if (this.closed) {
            return;
        }
        if (packet instanceof PacketWrapper) {
            PacketWrapper wrapper = (PacketWrapper) packet;
            wrapper.setReleased(true);
            this.channel.write(wrapper.buf, this.channel.voidPromise());
        } else {
            this.channel.write(packet, this.channel.voidPromise());
        }
    }

    public void markClosed() {
        this.closed = this.closing = true;
    }

    public void close() {
        this.close(null);
    }

    public void close(Object packet) {
        if (this.closed) {
            return;
        }
        this.closed = this.closing = true;
        if (packet != null && this.channel.isActive()) {
            this.channel.writeAndFlush(packet).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE);
        } else {
            this.channel.flush();
            this.channel.close();
        }
    }

    public void flush() {
        if (!this.closed) {
            this.channel.flush();
        }
    }

    public void forceClose() {
        if (!this.closed) {
            this.closed = this.closing = true;
            this.channel.close();
        }
    }

    public void delayedClose(final Kick kick) {
        if (!this.closing) {
            this.closing = true;
            this.channel.eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    ChannelWrapper.this.close(kick);
                }
            }, 250L, TimeUnit.MILLISECONDS);
        }
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {
        Preconditions.checkState(this.channel.eventLoop().inEventLoop(), "cannot add handler outside of event loop");
        this.channel.pipeline().flush();
        this.channel.pipeline().addBefore(baseName, name, handler);
    }

    public Channel getHandle() {
        return this.channel;
    }

    public void setCompressionThreshold(int threshold) {
        if (threshold == -1) {
            this.channel.pipeline().remove("compress");
            this.channel.pipeline().remove("decompress");
            return;
        }

        BungeeZlib zlib = CompressFactory.zlib.newInstance();
        ChannelHandler encoder;
        if (XCord.getConfig().getPerformanceOptimizationsUseMergedEncodingPipeline()) {
            encoder = new MergedCompressionEncoder(threshold, zlib);
            this.channel.pipeline().remove("frame-prepender");
        } else {
            encoder = new CompressionEncoder(threshold, zlib);
        }

        CompressionDecoder decoder = new CompressionDecoder(threshold, zlib);
        boolean initialized = false;
        if (this.channel.pipeline().get("compress") == null) {
            this.addBefore("packet-encoder", "compress", encoder);
            initialized = true;
        }

        if (this.channel.pipeline().get("decompress") == null) {
            this.addBefore("packet-decoder", "decompress", decoder);
            initialized = true;
        }

        if (initialized) {
            int compressionStrength = XCord.getConfig().getGeneralCompressionStrength();
            zlib.init(false, compressionStrength == -1 ? 6 : Math.min(Math.max(compressionStrength, 1), 12));
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isClosing() {
        return this.closing;
    }

    public boolean isBeingForward() {
        return this.beingForward;
    }

    public void setBeingForward(boolean beingForward) {
        this.beingForward = beingForward;
    }
}
