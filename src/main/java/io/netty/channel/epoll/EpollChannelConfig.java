package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;

public class EpollChannelConfig extends DefaultChannelConfig {
   private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

   EpollChannelConfig(AbstractEpollChannel var1) {
      super(var1);
   }

   EpollChannelConfig(AbstractEpollChannel var1, RecvByteBufAllocator var2) {
      super(var1, var2);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{EpollChannelOption.EPOLL_MODE});
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == EpollChannelOption.EPOLL_MODE) {
         return (T)this.getEpollMode();
      } else {
         try {
            if (var1 instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption var5 = (IntegerUnixChannelOption)var1;
               return (T)((AbstractEpollChannel)this.channel).socket.getIntOpt(var5.level(), var5.optname());
            }

            if (var1 instanceof RawUnixChannelOption) {
               RawUnixChannelOption var2 = (RawUnixChannelOption)var1;
               ByteBuffer var3 = ByteBuffer.allocate(var2.length());
               ((AbstractEpollChannel)this.channel).socket.getRawOpt(var2.level(), var2.optname(), var3);
               return (T)((Buffer)var3).flip();
            }
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }

         return super.getOption(var1);
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == EpollChannelOption.EPOLL_MODE) {
         this.setEpollMode((EpollMode)var2);
         return true;
      } else {
         try {
            if (var1 instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption var5 = (IntegerUnixChannelOption)var1;
               ((AbstractEpollChannel)this.channel).socket.setIntOpt(var5.level(), var5.optname(), (Integer)var2);
               return true;
            }

            if (var1 instanceof RawUnixChannelOption) {
               RawUnixChannelOption var3 = (RawUnixChannelOption)var1;
               ((AbstractEpollChannel)this.channel).socket.setRawOpt(var3.level(), var3.optname(), (ByteBuffer)var2);
               return true;
            }
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }

         return super.setOption(var1, var2);
      }
   }

   public EpollChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      if (!(var1.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
         throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
      } else {
         super.setRecvByteBufAllocator(var1);
         return this;
      }
   }

   public EpollChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollMode getEpollMode() {
      return ((AbstractEpollChannel)this.channel).isFlagSet(Native.EPOLLET) ? EpollMode.EDGE_TRIGGERED : EpollMode.LEVEL_TRIGGERED;
   }

   public EpollChannelConfig setEpollMode(EpollMode var1) {
      ObjectUtil.checkNotNull(var1, "mode");

      try {
         switch (var1) {
            case EDGE_TRIGGERED:
               this.checkChannelNotRegistered();
               ((AbstractEpollChannel)this.channel).setFlag(Native.EPOLLET);
               break;
            case LEVEL_TRIGGERED:
               this.checkChannelNotRegistered();
               ((AbstractEpollChannel)this.channel).clearFlag(Native.EPOLLET);
               break;
            default:
               throw new Error();
         }

         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   private void checkChannelNotRegistered() {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("EpollMode can only be changed before channel is registered");
      }
   }

   @Override
   protected final void autoReadCleared() {
      ((AbstractEpollChannel)this.channel).clearEpollIn();
   }

   final void setMaxBytesPerGatheringWrite(long var1) {
      this.maxBytesPerGatheringWrite = var1;
   }

   final long getMaxBytesPerGatheringWrite() {
      return this.maxBytesPerGatheringWrite;
   }
}
