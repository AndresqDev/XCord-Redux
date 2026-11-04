package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelConfig implements ChannelConfig {
   private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
   private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
   private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      DefaultChannelConfig.class, "autoRead"
   );
   private static final AtomicReferenceFieldUpdater<DefaultChannelConfig, WriteBufferWaterMark> WATERMARK_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      DefaultChannelConfig.class, WriteBufferWaterMark.class, "writeBufferWaterMark"
   );
   protected final Channel channel;
   private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
   private volatile RecvByteBufAllocator rcvBufAllocator;
   private volatile MessageSizeEstimator msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
   private volatile int connectTimeoutMillis = 30000;
   private volatile int writeSpinCount = 16;
   private volatile int maxMessagesPerWrite = Integer.MAX_VALUE;
   private volatile int autoRead = 1;
   private volatile boolean autoClose = true;
   private volatile WriteBufferWaterMark writeBufferWaterMark = WriteBufferWaterMark.DEFAULT;
   private volatile boolean pinEventExecutor = true;

   public DefaultChannelConfig(Channel var1) {
      this(var1, new AdaptiveRecvByteBufAllocator());
   }

   protected DefaultChannelConfig(Channel var1, RecvByteBufAllocator var2) {
      this.setRecvByteBufAllocator(var2, var1.metadata());
      this.channel = var1;
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         null,
         ChannelOption.CONNECT_TIMEOUT_MILLIS,
         ChannelOption.MAX_MESSAGES_PER_READ,
         ChannelOption.WRITE_SPIN_COUNT,
         ChannelOption.ALLOCATOR,
         ChannelOption.AUTO_READ,
         ChannelOption.AUTO_CLOSE,
         ChannelOption.RCVBUF_ALLOCATOR,
         ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,
         ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,
         ChannelOption.WRITE_BUFFER_WATER_MARK,
         ChannelOption.MESSAGE_SIZE_ESTIMATOR,
         ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP,
         ChannelOption.MAX_MESSAGES_PER_WRITE
      );
   }

   protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> var1, ChannelOption<?>... var2) {
      if (var1 == null) {
         var1 = new IdentityHashMap();
      }

      for (ChannelOption var6 : var2) {
         var1.put(var6, this.getOption(var6));
      }

      return (Map<ChannelOption<?>, Object>)var1;
   }

   @Override
   public boolean setOptions(Map<ChannelOption<?>, ?> var1) {
      ObjectUtil.checkNotNull(var1, "options");
      boolean var2 = true;

      for (Entry var4 : var1.entrySet()) {
         if (!this.setOption((ChannelOption<Object>)var4.getKey(), var4.getValue())) {
            var2 = false;
         }
      }

      return var2;
   }

   @Override
   public <T> T getOption(ChannelOption<T> var1) {
      ObjectUtil.checkNotNull(var1, "option");
      if (var1 == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
         return (T)this.getConnectTimeoutMillis();
      } else if (var1 == ChannelOption.MAX_MESSAGES_PER_READ) {
         return (T)this.getMaxMessagesPerRead();
      } else if (var1 == ChannelOption.WRITE_SPIN_COUNT) {
         return (T)this.getWriteSpinCount();
      } else if (var1 == ChannelOption.ALLOCATOR) {
         return (T)this.getAllocator();
      } else if (var1 == ChannelOption.RCVBUF_ALLOCATOR) {
         return this.getRecvByteBufAllocator();
      } else if (var1 == ChannelOption.AUTO_READ) {
         return (T)this.isAutoRead();
      } else if (var1 == ChannelOption.AUTO_CLOSE) {
         return (T)this.isAutoClose();
      } else if (var1 == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
         return (T)this.getWriteBufferHighWaterMark();
      } else if (var1 == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
         return (T)this.getWriteBufferLowWaterMark();
      } else if (var1 == ChannelOption.WRITE_BUFFER_WATER_MARK) {
         return (T)this.getWriteBufferWaterMark();
      } else if (var1 == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
         return (T)this.getMessageSizeEstimator();
      } else if (var1 == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
         return (T)this.getPinEventExecutorPerGroup();
      } else {
         return (T)(var1 == ChannelOption.MAX_MESSAGES_PER_WRITE ? this.getMaxMessagesPerWrite() : null);
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
         this.setConnectTimeoutMillis((Integer)var2);
      } else if (var1 == ChannelOption.MAX_MESSAGES_PER_READ) {
         this.setMaxMessagesPerRead((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_SPIN_COUNT) {
         this.setWriteSpinCount((Integer)var2);
      } else if (var1 == ChannelOption.ALLOCATOR) {
         this.setAllocator((ByteBufAllocator)var2);
      } else if (var1 == ChannelOption.RCVBUF_ALLOCATOR) {
         this.setRecvByteBufAllocator((RecvByteBufAllocator)var2);
      } else if (var1 == ChannelOption.AUTO_READ) {
         this.setAutoRead((Boolean)var2);
      } else if (var1 == ChannelOption.AUTO_CLOSE) {
         this.setAutoClose((Boolean)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
         this.setWriteBufferHighWaterMark((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
         this.setWriteBufferLowWaterMark((Integer)var2);
      } else if (var1 == ChannelOption.WRITE_BUFFER_WATER_MARK) {
         this.setWriteBufferWaterMark((WriteBufferWaterMark)var2);
      } else if (var1 == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
         this.setMessageSizeEstimator((MessageSizeEstimator)var2);
      } else if (var1 == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
         this.setPinEventExecutorPerGroup((Boolean)var2);
      } else {
         if (var1 != ChannelOption.MAX_MESSAGES_PER_WRITE) {
            return false;
         }

         this.setMaxMessagesPerWrite((Integer)var2);
      }

      return true;
   }

   protected <T> void validate(ChannelOption<T> var1, T var2) {
      ObjectUtil.checkNotNull(var1, "option").validate(var2);
   }

   @Override
   public int getConnectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   @Override
   public ChannelConfig setConnectTimeoutMillis(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "connectTimeoutMillis");
      this.connectTimeoutMillis = var1;
      return this;
   }

   @Deprecated
   @Override
   public int getMaxMessagesPerRead() {
      try {
         MaxMessagesRecvByteBufAllocator var1 = this.getRecvByteBufAllocator();
         return var1.maxMessagesPerRead();
      } catch (ClassCastException var2) {
         throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", var2);
      }
   }

   @Deprecated
   @Override
   public ChannelConfig setMaxMessagesPerRead(int var1) {
      try {
         MaxMessagesRecvByteBufAllocator var2 = this.getRecvByteBufAllocator();
         var2.maxMessagesPerRead(var1);
         return this;
      } catch (ClassCastException var3) {
         throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", var3);
      }
   }

   public int getMaxMessagesPerWrite() {
      return this.maxMessagesPerWrite;
   }

   public ChannelConfig setMaxMessagesPerWrite(int var1) {
      this.maxMessagesPerWrite = ObjectUtil.checkPositive(var1, "maxMessagesPerWrite");
      return this;
   }

   @Override
   public int getWriteSpinCount() {
      return this.writeSpinCount;
   }

   @Override
   public ChannelConfig setWriteSpinCount(int var1) {
      ObjectUtil.checkPositive(var1, "writeSpinCount");
      if (var1 == Integer.MAX_VALUE) {
         var1--;
      }

      this.writeSpinCount = var1;
      return this;
   }

   @Override
   public ByteBufAllocator getAllocator() {
      return this.allocator;
   }

   @Override
   public ChannelConfig setAllocator(ByteBufAllocator var1) {
      this.allocator = ObjectUtil.checkNotNull(var1, "allocator");
      return this;
   }

   @Override
   public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {
      return (T)this.rcvBufAllocator;
   }

   @Override
   public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      this.rcvBufAllocator = ObjectUtil.checkNotNull(var1, "allocator");
      return this;
   }

   private void setRecvByteBufAllocator(RecvByteBufAllocator var1, ChannelMetadata var2) {
      ObjectUtil.checkNotNull(var1, "allocator");
      ObjectUtil.checkNotNull(var2, "metadata");
      if (var1 instanceof MaxMessagesRecvByteBufAllocator) {
         ((MaxMessagesRecvByteBufAllocator)var1).maxMessagesPerRead(var2.defaultMaxMessagesPerRead());
      }

      this.setRecvByteBufAllocator(var1);
   }

   @Override
   public boolean isAutoRead() {
      return this.autoRead == 1;
   }

   @Override
   public ChannelConfig setAutoRead(boolean var1) {
      boolean var2 = AUTOREAD_UPDATER.getAndSet(this, var1 ? 1 : 0) == 1;
      if (var1 && !var2) {
         this.channel.read();
      } else if (!var1 && var2) {
         this.autoReadCleared();
      }

      return this;
   }

   protected void autoReadCleared() {
   }

   @Override
   public boolean isAutoClose() {
      return this.autoClose;
   }

   @Override
   public ChannelConfig setAutoClose(boolean var1) {
      this.autoClose = var1;
      return this;
   }

   @Override
   public int getWriteBufferHighWaterMark() {
      return this.writeBufferWaterMark.high();
   }

   @Override
   public ChannelConfig setWriteBufferHighWaterMark(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "writeBufferHighWaterMark");

      WriteBufferWaterMark var2;
      do {
         var2 = this.writeBufferWaterMark;
         if (var1 < var2.low()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + var2.low() + "): " + var1);
         }
      } while (!WATERMARK_UPDATER.compareAndSet(this, var2, new WriteBufferWaterMark(var2.low(), var1, false)));

      return this;
   }

   @Override
   public int getWriteBufferLowWaterMark() {
      return this.writeBufferWaterMark.low();
   }

   @Override
   public ChannelConfig setWriteBufferLowWaterMark(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "writeBufferLowWaterMark");

      WriteBufferWaterMark var2;
      do {
         var2 = this.writeBufferWaterMark;
         if (var1 > var2.high()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + var2.high() + "): " + var1);
         }
      } while (!WATERMARK_UPDATER.compareAndSet(this, var2, new WriteBufferWaterMark(var1, var2.high(), false)));

      return this;
   }

   @Override
   public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      this.writeBufferWaterMark = ObjectUtil.checkNotNull(var1, "writeBufferWaterMark");
      return this;
   }

   @Override
   public WriteBufferWaterMark getWriteBufferWaterMark() {
      return this.writeBufferWaterMark;
   }

   @Override
   public MessageSizeEstimator getMessageSizeEstimator() {
      return this.msgSizeEstimator;
   }

   @Override
   public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      this.msgSizeEstimator = ObjectUtil.checkNotNull(var1, "estimator");
      return this;
   }

   private ChannelConfig setPinEventExecutorPerGroup(boolean var1) {
      this.pinEventExecutor = var1;
      return this;
   }

   private boolean getPinEventExecutorPerGroup() {
      return this.pinEventExecutor;
   }
}
