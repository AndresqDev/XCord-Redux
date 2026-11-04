package io.netty.channel.socket.oio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;

@Deprecated
public class OioServerSocketChannel extends AbstractOioMessageChannel implements ServerSocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioServerSocketChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 1);
   final ServerSocket socket;
   private final OioServerSocketChannelConfig config;

   private static ServerSocket newServerSocket() {
      try {
         return new ServerSocket();
      } catch (IOException var1) {
         throw new ChannelException("failed to create a server socket", var1);
      }
   }

   public OioServerSocketChannel() {
      this(newServerSocket());
   }

   public OioServerSocketChannel(ServerSocket var1) {
      super(null);
      ObjectUtil.checkNotNull(var1, "socket");
      boolean var2 = false;

      try {
         var1.setSoTimeout(1000);
         var2 = true;
      } catch (IOException var10) {
         throw new ChannelException("Failed to set the server socket timeout.", var10);
      } finally {
         if (!var2) {
            try {
               var1.close();
            } catch (IOException var11) {
               if (logger.isWarnEnabled()) {
                  logger.warn("Failed to close a partially initialized socket.", (Throwable)var11);
               }
            }
         }
      }

      this.socket = var1;
      this.config = new DefaultOioServerSocketChannelConfig(this, var1);
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   public OioServerSocketChannelConfig config() {
      return this.config;
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return null;
   }

   @Override
   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   @Override
   public boolean isActive() {
      return this.isOpen() && this.socket.isBound();
   }

   @Override
   protected SocketAddress localAddress0() {
      return SocketUtils.localSocketAddress(this.socket);
   }

   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      this.socket.bind(var1, this.config.getBacklog());
   }

   @Override
   protected void doClose() throws Exception {
      this.socket.close();
   }

   @Override
   protected int doReadMessages(List<Object> var1) throws Exception {
      if (this.socket.isClosed()) {
         return -1;
      } else {
         try {
            Socket var2 = this.socket.accept();

            try {
               var1.add(new OioSocketChannel(this, var2));
               return 1;
            } catch (Throwable var6) {
               logger.warn("Failed to create a new channel from an accepted socket.", var6);

               try {
                  var2.close();
               } catch (Throwable var5) {
                  logger.warn("Failed to close a socket.", var5);
               }
            }
         } catch (SocketTimeoutException var7) {
         }

         return 0;
      }
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return null;
   }

   @Override
   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   protected void setReadPending(boolean var1) {
      super.setReadPending(var1);
   }

   final void clearReadPending0() {
      super.clearReadPending();
   }
}
