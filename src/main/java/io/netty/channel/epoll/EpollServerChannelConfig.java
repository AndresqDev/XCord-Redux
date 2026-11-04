package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannelRecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.util.Map;

public class EpollServerChannelConfig extends EpollChannelConfig implements ServerSocketChannelConfig {
   private volatile int backlog = NetUtil.SOMAXCONN;
   private volatile int pendingFastOpenRequestsThreshold;

   EpollServerChannelConfig(AbstractEpollChannel var1) {
      super(var1, new ServerChannelRecvByteBufAllocator());
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, ChannelOption.TCP_FASTOPEN}
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.SO_RCVBUF) {
         return (T)this.getReceiveBufferSize();
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         return (T)this.isReuseAddress();
      } else if (var1 == ChannelOption.SO_BACKLOG) {
         return (T)this.getBacklog();
      } else {
         return (T)(var1 == ChannelOption.TCP_FASTOPEN ? this.getTcpFastopen() : super.getOption(var1));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)var2);
      } else if (var1 == ChannelOption.SO_BACKLOG) {
         this.setBacklog((Integer)var2);
      } else {
         if (var1 != ChannelOption.TCP_FASTOPEN) {
            return super.setOption(var1, var2);
         }

         this.setTcpFastopen((Integer)var2);
      }

      return true;
   }

   @Override
   public boolean isReuseAddress() {
      try {
         return ((AbstractEpollChannel)this.channel).socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerChannelConfig setReuseAddress(boolean var1) {
      try {
         ((AbstractEpollChannel)this.channel).socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getReceiveBufferSize() {
      try {
         return ((AbstractEpollChannel)this.channel).socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerChannelConfig setReceiveBufferSize(int var1) {
      try {
         ((AbstractEpollChannel)this.channel).socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getBacklog() {
      return this.backlog;
   }

   public EpollServerChannelConfig setBacklog(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "backlog");
      this.backlog = var1;
      return this;
   }

   public int getTcpFastopen() {
      return this.pendingFastOpenRequestsThreshold;
   }

   public EpollServerChannelConfig setTcpFastopen(int var1) {
      this.pendingFastOpenRequestsThreshold = ObjectUtil.checkPositiveOrZero(var1, "pendingFastOpenRequestsThreshold");
      return this;
   }

   public EpollServerChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public EpollServerChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   @Deprecated
   public EpollServerChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollServerChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollServerChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollServerChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   @Deprecated
   public EpollServerChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   @Deprecated
   public EpollServerChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollServerChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }
}
