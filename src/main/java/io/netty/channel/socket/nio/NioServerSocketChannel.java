package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;
import java.util.Map;

public class NioServerSocketChannel extends AbstractNioMessageChannel implements ServerSocketChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
   private static final Method OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY = SelectorProviderUtil.findOpenMethod("openServerSocketChannel");
   private final ServerSocketChannelConfig config = new NioServerSocketChannel.NioServerSocketChannelConfig(this, this.javaChannel().socket());

   private static java.nio.channels.ServerSocketChannel newChannel(SelectorProvider var0, InternetProtocolFamily var1) {
      try {
         java.nio.channels.ServerSocketChannel var2 = SelectorProviderUtil.newChannel(OPEN_SERVER_SOCKET_CHANNEL_WITH_FAMILY, var0, var1);
         return var2 == null ? var0.openServerSocketChannel() : var2;
      } catch (IOException var3) {
         throw new ChannelException("Failed to open a socket.", var3);
      }
   }

   public NioServerSocketChannel() {
      this(DEFAULT_SELECTOR_PROVIDER);
   }

   public NioServerSocketChannel(SelectorProvider var1) {
      this(var1, null);
   }

   public NioServerSocketChannel(SelectorProvider var1, InternetProtocolFamily var2) {
      this(newChannel(var1, var2));
   }

   public NioServerSocketChannel(java.nio.channels.ServerSocketChannel var1) {
      super(null, var1, 16);
   }

   @Override
   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public ServerSocketChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isActive() {
      return this.isOpen() && this.javaChannel().socket().isBound();
   }

   @Override
   public InetSocketAddress remoteAddress() {
      return null;
   }

   protected java.nio.channels.ServerSocketChannel javaChannel() {
      return (java.nio.channels.ServerSocketChannel)super.javaChannel();
   }

   @Override
   protected SocketAddress localAddress0() {
      return SocketUtils.localSocketAddress(this.javaChannel().socket());
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   @Override
   protected void doBind(SocketAddress var1) throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         this.javaChannel().bind(var1, this.config.getBacklog());
      } else {
         this.javaChannel().socket().bind(var1, this.config.getBacklog());
      }
   }

   @Override
   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   @Override
   protected int doReadMessages(List<Object> var1) throws Exception {
      SocketChannel var2 = SocketUtils.accept(this.javaChannel());

      try {
         if (var2 != null) {
            var1.add(new NioSocketChannel(this, var2));
            return 1;
         }
      } catch (Throwable var6) {
         logger.warn("Failed to create a new channel from an accepted socket.", var6);

         try {
            var2.close();
         } catch (Throwable var5) {
            logger.warn("Failed to close a socket.", var5);
         }
      }

      return 0;
   }

   @Override
   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void doFinishConnect() throws Exception {
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

   @Override
   protected boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected final Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   protected boolean closeOnReadError(Throwable var1) {
      return super.closeOnReadError(var1);
   }

   private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig {
      private NioServerSocketChannelConfig(NioServerSocketChannel var2, ServerSocket var3) {
         super(var2, var3);
      }

      @Override
      protected void autoReadCleared() {
         NioServerSocketChannel.this.clearReadPending();
      }

      @Override
      public <T> boolean setOption(ChannelOption<T> var1, T var2) {
         return PlatformDependent.javaVersion() >= 7 && var1 instanceof NioChannelOption
            ? NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption<Object>)var1, var2)
            : super.setOption(var1, var2);
      }

      @Override
      public <T> T getOption(ChannelOption<T> var1) {
         return PlatformDependent.javaVersion() >= 7 && var1 instanceof NioChannelOption
            ? NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption<T>)var1)
            : super.getOption(var1);
      }

      @Override
      public Map<ChannelOption<?>, Object> getOptions() {
         return PlatformDependent.javaVersion() >= 7 ? this.getOptions(super.getOptions(), NioChannelOption.getOptions(this.jdkChannel())) : super.getOptions();
      }

      private java.nio.channels.ServerSocketChannel jdkChannel() {
         return ((NioServerSocketChannel)this.channel).javaChannel();
      }
   }
}
