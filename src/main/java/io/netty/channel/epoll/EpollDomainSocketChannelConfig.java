package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DuplexChannelConfig;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.util.Map;

public final class EpollDomainSocketChannelConfig extends EpollChannelConfig implements DuplexChannelConfig, DomainSocketChannelConfig {
   private volatile DomainSocketReadMode mode = DomainSocketReadMode.BYTES;
   private volatile boolean allowHalfClosure;

   EpollDomainSocketChannelConfig(AbstractEpollChannel var1) {
      super(var1);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(),
         new ChannelOption[]{UnixChannelOption.DOMAIN_SOCKET_READ_MODE, ChannelOption.ALLOW_HALF_CLOSURE, ChannelOption.SO_SNDBUF, ChannelOption.SO_RCVBUF}
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
         return (T)this.getReadMode();
      } else if (var1 == ChannelOption.ALLOW_HALF_CLOSURE) {
         return (T)this.isAllowHalfClosure();
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         return (T)this.getSendBufferSize();
      } else {
         return (T)(var1 == ChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : super.getOption(var1));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
         this.setReadMode((DomainSocketReadMode)var2);
      } else if (var1 == ChannelOption.ALLOW_HALF_CLOSURE) {
         this.setAllowHalfClosure((Boolean)var2);
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)var2);
      } else {
         if (var1 != ChannelOption.SO_RCVBUF) {
            return super.setOption(var1, var2);
         }

         this.setReceiveBufferSize((Integer)var2);
      }

      return true;
   }

   @Deprecated
   public EpollDomainSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   @Deprecated
   public EpollDomainSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   @Deprecated
   public EpollDomainSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }

   public EpollDomainSocketChannelConfig setReadMode(DomainSocketReadMode var1) {
      this.mode = ObjectUtil.checkNotNull(var1, "mode");
      return this;
   }

   @Override
   public DomainSocketReadMode getReadMode() {
      return this.mode;
   }

   @Override
   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   public EpollDomainSocketChannelConfig setAllowHalfClosure(boolean var1) {
      this.allowHalfClosure = var1;
      return this;
   }

   public int getSendBufferSize() {
      try {
         return ((EpollDomainSocketChannel)this.channel).socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public EpollDomainSocketChannelConfig setSendBufferSize(int var1) {
      try {
         ((EpollDomainSocketChannel)this.channel).socket.setSendBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public int getReceiveBufferSize() {
      try {
         return ((EpollDomainSocketChannel)this.channel).socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public EpollDomainSocketChannelConfig setReceiveBufferSize(int var1) {
      try {
         ((EpollDomainSocketChannel)this.channel).socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }
}
