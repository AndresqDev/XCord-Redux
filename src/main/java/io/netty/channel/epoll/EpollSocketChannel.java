package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

public final class EpollSocketChannel extends AbstractEpollStreamChannel implements SocketChannel {
   private final EpollSocketChannelConfig config;
   private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

   public EpollSocketChannel() {
      super(LinuxSocket.newSocketStream(), false);
      this.config = new EpollSocketChannelConfig(this);
   }

   public EpollSocketChannel(InternetProtocolFamily var1) {
      super(LinuxSocket.newSocketStream(var1), false);
      this.config = new EpollSocketChannelConfig(this);
   }

   public EpollSocketChannel(int var1) {
      super(var1);
      this.config = new EpollSocketChannelConfig(this);
   }

   EpollSocketChannel(LinuxSocket var1, boolean var2) {
      super(var1, var2);
      this.config = new EpollSocketChannelConfig(this);
   }

   EpollSocketChannel(Channel var1, LinuxSocket var2, InetSocketAddress var3) {
      super(var1, var2, var3);
      this.config = new EpollSocketChannelConfig(this);
      if (var1 instanceof EpollServerSocketChannel) {
         this.tcpMd5SigAddresses = ((EpollServerSocketChannel)var1).tcpMd5SigAddresses();
      }
   }

   public EpollTcpInfo tcpInfo() {
      return this.tcpInfo(new EpollTcpInfo());
   }

   public EpollTcpInfo tcpInfo(EpollTcpInfo var1) {
      try {
         this.socket.getTcpInfo(var1);
         return var1;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public EpollSocketChannelConfig config() {
      return this.config;
   }

   @Override
   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   @Override
   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollSocketChannel.EpollSocketChannelUnsafe();
   }

   @Override
   boolean doConnect0(SocketAddress var1) throws Exception {
      if (Native.IS_SUPPORTING_TCP_FASTOPEN_CLIENT && this.config.isTcpFastOpenConnect()) {
         ChannelOutboundBuffer var2 = this.unsafe().outboundBuffer();
         var2.addFlush();
         Object var3;
         if ((var3 = var2.current()) instanceof ByteBuf) {
            ByteBuf var4 = (ByteBuf)var3;
            long var5 = this.doWriteOrSendBytes(var4, (InetSocketAddress)var1, true);
            if (var5 > 0L) {
               var2.removeBytes(var5);
               return true;
            }
         }
      }

      return super.doConnect0(var1);
   }

   void setTcpMd5Sig(Map<InetAddress, byte[]> var1) throws IOException {
      synchronized (this) {
         this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, var1);
      }
   }

   private final class EpollSocketChannelUnsafe extends AbstractEpollStreamChannel.EpollStreamUnsafe {
      private EpollSocketChannelUnsafe() {
      }

      @Override
      protected Executor prepareToClose() {
         try {
            if (EpollSocketChannel.this.isOpen() && EpollSocketChannel.this.config().getSoLinger() > 0) {
               ((EpollEventLoop)EpollSocketChannel.this.eventLoop()).remove(EpollSocketChannel.this);
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }
   }
}
