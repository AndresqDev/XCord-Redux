package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.compress.CompressFactory;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Kick;
import xcord.CompressionDecoder;
import xcord.CompressionEncoder;
import xcord.MergedCompressionEncoder;
import xcord.XCord;

public class ChannelWrapper {
   private final Channel ch;
   private SocketAddress remoteAddress;
   private volatile boolean closed;
   private volatile boolean closing;
   private boolean beingForward = false;

   public ChannelWrapper(ChannelHandlerContext var1) {
      this.ch = var1.channel();
      this.remoteAddress = this.ch.remoteAddress() == null ? this.ch.parent().localAddress() : this.ch.remoteAddress();
   }

   public ChannelWrapper() {
      this.ch = null;
      this.remoteAddress = null;
   }

   public void setProtocol(Protocol var1) {
      ((MinecraftDecoder)this.ch.pipeline().get(MinecraftDecoder.class)).setProtocol(var1);
      ((MinecraftEncoder)this.ch.pipeline().get(MinecraftEncoder.class)).setProtocol(var1);
   }

   public void setVersion(int var1) {
      ((MinecraftDecoder)this.ch.pipeline().get(MinecraftDecoder.class)).setProtocolVersion(var1);
      ((MinecraftEncoder)this.ch.pipeline().get(MinecraftEncoder.class)).setProtocolVersion(var1);
   }

   public void write(Object var1) {
      if (!this.closed) {
         if (var1 instanceof PacketWrapper) {
            ((PacketWrapper)var1).setReleased(true);
            this.ch.writeAndFlush(((PacketWrapper)var1).buf, this.ch.voidPromise());
         } else {
            this.ch.writeAndFlush(var1, this.ch.voidPromise());
         }
      }
   }

   public void writeBuffered(Object var1) {
      if (!this.closed) {
         if (var1 instanceof PacketWrapper) {
            ((PacketWrapper)var1).setReleased(true);
            this.ch.write(((PacketWrapper)var1).buf, this.ch.voidPromise());
         } else {
            this.ch.write(var1, this.ch.voidPromise());
         }
      }
   }

   public void markClosed() {
      this.closed = this.closing = true;
   }

   public void close() {
      this.close(null);
   }

   public void close(Object var1) {
      if (!this.closed) {
         this.closed = this.closing = true;
         if (var1 != null && this.ch.isActive()) {
            this.ch.writeAndFlush(var1).addListeners(new GenericFutureListener[]{ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE});
         } else {
            this.ch.flush();
            this.ch.close();
         }
      }
   }

   public void flush() {
      if (!this.closed) {
         this.ch.flush();
      }
   }

   public void forceClose() {
      if (!this.closed) {
         this.closed = this.closing = true;
         this.ch.close();
      }
   }

   public void delayedClose(final Kick var1) {
      if (!this.closing) {
         this.closing = true;
         this.ch.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
               ChannelWrapper.this.close(var1);
            }
         }, 250L, TimeUnit.MILLISECONDS);
      }
   }

   public void addBefore(String var1, String var2, ChannelHandler var3) {
      Preconditions.checkState(this.ch.eventLoop().inEventLoop(), "cannot add handler outside of event loop");
      this.ch.pipeline().flush();
      this.ch.pipeline().addBefore(var1, var2, var3);
   }

   public Channel getHandle() {
      return this.ch;
   }

   public void setCompressionThreshold(int var1) {
      if (var1 == -1) {
         this.ch.pipeline().remove("compress");
         this.ch.pipeline().remove("decompress");
      } else {
         BungeeZlib var2 = CompressFactory.zlib.newInstance();
         Object var3;
         if (XCord.getConfig().getPerformanceOptimizationsUseMergedEncodingPipeline()) {
            var3 = new MergedCompressionEncoder(var1, var2);
            this.ch.pipeline().remove("frame-prepender");
         } else {
            var3 = new CompressionEncoder(var1, var2);
         }

         CompressionDecoder var4 = new CompressionDecoder(var1, var2);
         boolean var5 = false;
         if (this.ch.pipeline().get("compress") == null) {
            this.addBefore("packet-encoder", "compress", (ChannelHandler)var3);
            var5 = true;
         }

         if (this.ch.pipeline().get("decompress") == null) {
            this.addBefore("packet-decoder", "decompress", var4);
            var5 = true;
         }

         if (var5) {
            var2.init(
               false,
               XCord.getConfig().getGeneralCompressionStrength() == -1 ? 6 : Math.min(Math.max(XCord.getConfig().getGeneralCompressionStrength(), 1), 12)
            );
         }
      }
   }

   public Channel getChannel() {
      return this.ch;
   }

   public SocketAddress getRemoteAddress() {
      return this.remoteAddress;
   }

   public void setRemoteAddress(SocketAddress var1) {
      this.remoteAddress = var1;
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

   public void setBeingForward(boolean var1) {
      this.beingForward = var1;
   }
}
