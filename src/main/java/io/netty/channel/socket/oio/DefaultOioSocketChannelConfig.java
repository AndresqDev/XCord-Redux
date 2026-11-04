package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.SocketChannel;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

@Deprecated
public class DefaultOioSocketChannelConfig extends DefaultSocketChannelConfig implements OioSocketChannelConfig {
   @Deprecated
   public DefaultOioSocketChannelConfig(SocketChannel var1, Socket var2) {
      super(var1, var2);
      this.setAllocator(new PreferHeapByteBufAllocator(this.getAllocator()));
   }

   DefaultOioSocketChannelConfig(OioSocketChannel var1, Socket var2) {
      super(var1, var2);
      this.setAllocator(new PreferHeapByteBufAllocator(this.getAllocator()));
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_TIMEOUT});
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      return (T)(var1 == ChannelOption.SO_TIMEOUT ? this.getSoTimeout() : super.getOption(var1));
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_TIMEOUT) {
         this.setSoTimeout((Integer)var2);
         return true;
      } else {
         return super.setOption(var1, var2);
      }
   }

   @Override
   public OioSocketChannelConfig setSoTimeout(int var1) {
      try {
         this.javaSocket.setSoTimeout(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getSoTimeout() {
      try {
         return this.javaSocket.getSoTimeout();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public OioSocketChannelConfig setTcpNoDelay(boolean var1) {
      super.setTcpNoDelay(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setSoLinger(int var1) {
      super.setSoLinger(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setSendBufferSize(int var1) {
      super.setSendBufferSize(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setReceiveBufferSize(int var1) {
      super.setReceiveBufferSize(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setKeepAlive(boolean var1) {
      super.setKeepAlive(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setTrafficClass(int var1) {
      super.setTrafficClass(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setReuseAddress(boolean var1) {
      super.setReuseAddress(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      super.setPerformancePreferences(var1, var2, var3);
      return this;
   }

   @Override
   public OioSocketChannelConfig setAllowHalfClosure(boolean var1) {
      super.setAllowHalfClosure(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   @Deprecated
   @Override
   public OioSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   @Override
   protected void autoReadCleared() {
      if (this.channel instanceof OioSocketChannel) {
         ((OioSocketChannel)this.channel).clearReadPending0();
      }
   }

   @Override
   public OioSocketChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   @Override
   public OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }
}
