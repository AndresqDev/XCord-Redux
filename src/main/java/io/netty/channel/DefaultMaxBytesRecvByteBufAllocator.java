package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class DefaultMaxBytesRecvByteBufAllocator implements MaxBytesRecvByteBufAllocator {
   private volatile int maxBytesPerRead;
   private volatile int maxBytesPerIndividualRead;

   public DefaultMaxBytesRecvByteBufAllocator() {
      this(65536, 65536);
   }

   public DefaultMaxBytesRecvByteBufAllocator(int var1, int var2) {
      checkMaxBytesPerReadPair(var1, var2);
      this.maxBytesPerRead = var1;
      this.maxBytesPerIndividualRead = var2;
   }

   @Override
   public RecvByteBufAllocator.Handle newHandle() {
      return new DefaultMaxBytesRecvByteBufAllocator.HandleImpl();
   }

   @Override
   public int maxBytesPerRead() {
      return this.maxBytesPerRead;
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int var1) {
      ObjectUtil.checkPositive(var1, "maxBytesPerRead");
      synchronized (this) {
         int var3 = this.maxBytesPerIndividualRead();
         if (var1 < var3) {
            throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + var3 + "): " + var1);
         } else {
            this.maxBytesPerRead = var1;
            return this;
         }
      }
   }

   @Override
   public int maxBytesPerIndividualRead() {
      return this.maxBytesPerIndividualRead;
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int var1) {
      ObjectUtil.checkPositive(var1, "maxBytesPerIndividualRead");
      synchronized (this) {
         int var3 = this.maxBytesPerRead();
         if (var1 > var3) {
            throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + var3 + "): " + var1);
         } else {
            this.maxBytesPerIndividualRead = var1;
            return this;
         }
      }
   }

   @Override
   public synchronized Entry<Integer, Integer> maxBytesPerReadPair() {
      return new SimpleEntry<>(this.maxBytesPerRead, this.maxBytesPerIndividualRead);
   }

   private static void checkMaxBytesPerReadPair(int var0, int var1) {
      ObjectUtil.checkPositive(var0, "maxBytesPerRead");
      ObjectUtil.checkPositive(var1, "maxBytesPerIndividualRead");
      if (var0 < var1) {
         throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + var1 + "): " + var0);
      }
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int var1, int var2) {
      checkMaxBytesPerReadPair(var1, var2);
      synchronized (this) {
         this.maxBytesPerRead = var1;
         this.maxBytesPerIndividualRead = var2;
         return this;
      }
   }

   private final class HandleImpl implements RecvByteBufAllocator.ExtendedHandle {
      private int individualReadMax;
      private int bytesToRead;
      private int lastBytesRead;
      private int attemptBytesRead;
      private final UncheckedBooleanSupplier defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
         @Override
         public boolean get() {
            return HandleImpl.this.attemptBytesRead == HandleImpl.this.lastBytesRead;
         }
      };

      private HandleImpl() {
      }

      @Override
      public ByteBuf allocate(ByteBufAllocator var1) {
         return var1.ioBuffer(this.guess());
      }

      @Override
      public int guess() {
         return Math.min(this.individualReadMax, this.bytesToRead);
      }

      @Override
      public void reset(ChannelConfig var1) {
         this.bytesToRead = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerRead();
         this.individualReadMax = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerIndividualRead();
      }

      @Override
      public void incMessagesRead(int var1) {
      }

      @Override
      public void lastBytesRead(int var1) {
         this.lastBytesRead = var1;
         this.bytesToRead -= var1;
      }

      @Override
      public int lastBytesRead() {
         return this.lastBytesRead;
      }

      @Override
      public boolean continueReading() {
         return this.continueReading(this.defaultMaybeMoreSupplier);
      }

      @Override
      public boolean continueReading(UncheckedBooleanSupplier var1) {
         return this.bytesToRead > 0 && var1.get();
      }

      @Override
      public void readComplete() {
      }

      @Override
      public void attemptedBytesRead(int var1) {
         this.attemptBytesRead = var1;
      }

      @Override
      public int attemptedBytesRead() {
         return this.attemptBytesRead;
      }
   }
}
