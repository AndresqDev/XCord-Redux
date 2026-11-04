package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

public abstract class DefaultMaxMessagesRecvByteBufAllocator implements MaxMessagesRecvByteBufAllocator {
   private final boolean ignoreBytesRead;
   private volatile int maxMessagesPerRead;
   private volatile boolean respectMaybeMoreData = true;

   public DefaultMaxMessagesRecvByteBufAllocator() {
      this(1);
   }

   public DefaultMaxMessagesRecvByteBufAllocator(int var1) {
      this(var1, false);
   }

   DefaultMaxMessagesRecvByteBufAllocator(int var1, boolean var2) {
      this.ignoreBytesRead = var2;
      this.maxMessagesPerRead(var1);
   }

   @Override
   public int maxMessagesPerRead() {
      return this.maxMessagesPerRead;
   }

   @Override
   public MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int var1) {
      ObjectUtil.checkPositive(var1, "maxMessagesPerRead");
      this.maxMessagesPerRead = var1;
      return this;
   }

   public DefaultMaxMessagesRecvByteBufAllocator respectMaybeMoreData(boolean var1) {
      this.respectMaybeMoreData = var1;
      return this;
   }

   public final boolean respectMaybeMoreData() {
      return this.respectMaybeMoreData;
   }

   public abstract class MaxMessageHandle implements RecvByteBufAllocator.ExtendedHandle {
      private ChannelConfig config;
      private int maxMessagePerRead;
      private int totalMessages;
      private int totalBytesRead;
      private int attemptedBytesRead;
      private int lastBytesRead;
      private final boolean respectMaybeMoreData;
      private final UncheckedBooleanSupplier defaultMaybeMoreSupplier;

      public MaxMessageHandle() {
         this.respectMaybeMoreData = DefaultMaxMessagesRecvByteBufAllocator.this.respectMaybeMoreData;
         this.defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
            @Override
            public boolean get() {
               return MaxMessageHandle.this.attemptedBytesRead == MaxMessageHandle.this.lastBytesRead;
            }
         };
      }

      @Override
      public void reset(ChannelConfig var1) {
         this.config = var1;
         this.maxMessagePerRead = DefaultMaxMessagesRecvByteBufAllocator.this.maxMessagesPerRead();
         this.totalMessages = this.totalBytesRead = 0;
      }

      @Override
      public ByteBuf allocate(ByteBufAllocator var1) {
         return var1.ioBuffer(this.guess());
      }

      @Override
      public final void incMessagesRead(int var1) {
         this.totalMessages += var1;
      }

      @Override
      public void lastBytesRead(int var1) {
         this.lastBytesRead = var1;
         if (var1 > 0) {
            this.totalBytesRead += var1;
         }
      }

      @Override
      public final int lastBytesRead() {
         return this.lastBytesRead;
      }

      @Override
      public boolean continueReading() {
         return this.continueReading(this.defaultMaybeMoreSupplier);
      }

      @Override
      public boolean continueReading(UncheckedBooleanSupplier var1) {
         return this.config.isAutoRead()
            && (!this.respectMaybeMoreData || var1.get())
            && this.totalMessages < this.maxMessagePerRead
            && (DefaultMaxMessagesRecvByteBufAllocator.this.ignoreBytesRead || this.totalBytesRead > 0);
      }

      @Override
      public void readComplete() {
      }

      @Override
      public int attemptedBytesRead() {
         return this.attemptedBytesRead;
      }

      @Override
      public void attemptedBytesRead(int var1) {
         this.attemptedBytesRead = var1;
      }

      protected final int totalBytesRead() {
         return this.totalBytesRead < 0 ? Integer.MAX_VALUE : this.totalBytesRead;
      }
   }
}
