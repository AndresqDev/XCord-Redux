package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class EpollServerDomainSocketChannel extends AbstractEpollServerChannel implements ServerDomainSocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollServerDomainSocketChannel.class);
   private final EpollServerChannelConfig config = new EpollServerChannelConfig(this);
   private volatile DomainSocketAddress local;

   public EpollServerDomainSocketChannel() {
      super(LinuxSocket.newSocketDomain(), false);
   }

   public EpollServerDomainSocketChannel(int var1) {
      super(var1);
   }

   EpollServerDomainSocketChannel(LinuxSocket var1) {
      super(var1);
   }

   EpollServerDomainSocketChannel(LinuxSocket var1, boolean var2) {
      super(var1, var2);
   }

   @Override
   protected Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception {
      return new EpollDomainSocketChannel(this, new Socket(var1));
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      this.socket.bind(var1);
      this.socket.listen(this.config.getBacklog());
      this.local = (DomainSocketAddress)var1;
      this.active = true;
   }

   @Override
   protected void doClose() throws Exception {
      try {
         super.doClose();
      } finally {
         DomainSocketAddress var5 = this.local;
         if (var5 != null) {
            File var6 = new File(var5.path());
            boolean var7 = var6.delete();
            if (!var7 && logger.isDebugEnabled()) {
               logger.debug("Failed to delete a domain socket file: {}", var5.path());
            }
         }
      }
   }

   public EpollServerChannelConfig config() {
      return this.config;
   }

   @Override
   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   @Override
   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }
}
