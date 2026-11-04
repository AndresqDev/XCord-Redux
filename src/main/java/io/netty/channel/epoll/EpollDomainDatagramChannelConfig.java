package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import java.io.IOException;
import java.util.Map;

public final class EpollDomainDatagramChannelConfig extends EpollChannelConfig implements DomainDatagramChannelConfig {
   private boolean activeOnOpen;

   EpollDomainDatagramChannelConfig(EpollDomainDatagramChannel var1) {
      super(var1);
      this.setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048));
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, ChannelOption.SO_SNDBUF});
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         return (T)this.activeOnOpen;
      } else {
         return (T)(var1 == ChannelOption.SO_SNDBUF ? this.getSendBufferSize() : super.getOption(var1));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen((Boolean)var2);
      } else {
         if (var1 != ChannelOption.SO_SNDBUF) {
            return super.setOption(var1, var2);
         }

         this.setSendBufferSize((Integer)var2);
      }

      return true;
   }

   private void setActiveOnOpen(boolean var1) {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("Can only changed before channel was registered");
      } else {
         this.activeOnOpen = var1;
      }
   }

   boolean getActiveOnOpen() {
      return this.activeOnOpen;
   }

   public EpollDomainDatagramChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }

   @Deprecated
   public EpollDomainDatagramChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setMaxMessagesPerWrite(int var1) {
      super.setMaxMessagesPerWrite(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setSendBufferSize(int var1) {
      try {
         ((EpollDomainDatagramChannel)this.channel).socket.setSendBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getSendBufferSize() {
      try {
         return ((EpollDomainDatagramChannel)this.channel).socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDomainDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollDomainDatagramChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }
}
