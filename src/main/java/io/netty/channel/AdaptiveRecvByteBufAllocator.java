package io.netty.channel;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;

public class AdaptiveRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {
   static final int DEFAULT_MINIMUM = 64;
   static final int DEFAULT_INITIAL = 2048;
   static final int DEFAULT_MAXIMUM = 65536;
   private static final int INDEX_INCREMENT = 4;
   private static final int INDEX_DECREMENT = 1;
   private static final int[] SIZE_TABLE;
   @Deprecated
   public static final AdaptiveRecvByteBufAllocator DEFAULT;
   private final int minIndex;
   private final int maxIndex;
   private final int initial;

   private static int getSizeTableIndex(int var0) {
      int var1 = 0;
      int var2 = SIZE_TABLE.length - 1;

      while (var2 >= var1) {
         if (var2 == var1) {
            return var2;
         }

         int var3 = var1 + var2 >>> 1;
         int var4 = SIZE_TABLE[var3];
         int var5 = SIZE_TABLE[var3 + 1];
         if (var0 > var5) {
            var1 = var3 + 1;
         } else {
            if (var0 >= var4) {
               if (var0 == var4) {
                  return var3;
               }

               return var3 + 1;
            }

            var2 = var3 - 1;
         }
      }

      return var1;
   }

   public AdaptiveRecvByteBufAllocator() {
      this(64, 2048, 65536);
   }

   public AdaptiveRecvByteBufAllocator(int var1, int var2, int var3) {
      ObjectUtil.checkPositive(var1, "minimum");
      if (var2 < var1) {
         throw new IllegalArgumentException("initial: " + var2);
      } else if (var3 < var2) {
         throw new IllegalArgumentException("maximum: " + var3);
      } else {
         int var4 = getSizeTableIndex(var1);
         if (SIZE_TABLE[var4] < var1) {
            this.minIndex = var4 + 1;
         } else {
            this.minIndex = var4;
         }

         int var5 = getSizeTableIndex(var3);
         if (SIZE_TABLE[var5] > var3) {
            this.maxIndex = var5 - 1;
         } else {
            this.maxIndex = var5;
         }

         this.initial = var2;
      }
   }

   @Override
   public RecvByteBufAllocator.Handle newHandle() {
      return new AdaptiveRecvByteBufAllocator.HandleImpl(this.minIndex, this.maxIndex, this.initial);
   }

   public AdaptiveRecvByteBufAllocator respectMaybeMoreData(boolean var1) {
      super.respectMaybeMoreData(var1);
      return this;
   }

   static {
      ArrayList var0 = new ArrayList();

      for (byte var1 = 16; var1 < 512; var1 += 16) {
         var0.add(Integer.valueOf(var1));
      }

      for (short var2 = 512; var2 > 0; var2 <<= 1) {
         var0.add(Integer.valueOf(var2));
      }

      SIZE_TABLE = new int[var0.size()];

      for (int var3 = 0; var3 < SIZE_TABLE.length; var3++) {
         SIZE_TABLE[var3] = (Integer)var0.get(var3);
      }

      DEFAULT = new AdaptiveRecvByteBufAllocator();
   }

   private final class HandleImpl extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
      private final int minIndex;
      private final int maxIndex;
      private int index;
      private int nextReceiveBufferSize;
      private boolean decreaseNow;

      HandleImpl(int var2, int var3, int var4) {
         this.minIndex = var2;
         this.maxIndex = var3;
         this.index = AdaptiveRecvByteBufAllocator.getSizeTableIndex(var4);
         this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
      }

      @Override
      public void lastBytesRead(int var1) {
         if (var1 == this.attemptedBytesRead()) {
            this.record(var1);
         }

         super.lastBytesRead(var1);
      }

      @Override
      public int guess() {
         return this.nextReceiveBufferSize;
      }

      private void record(int var1) {
         if (var1 <= AdaptiveRecvByteBufAllocator.SIZE_TABLE[Math.max(0, this.index - 1)]) {
            if (this.decreaseNow) {
               this.index = Math.max(this.index - 1, this.minIndex);
               this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
               this.decreaseNow = false;
            } else {
               this.decreaseNow = true;
            }
         } else if (var1 >= this.nextReceiveBufferSize) {
            this.index = Math.min(this.index + 4, this.maxIndex);
            this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
            this.decreaseNow = false;
         }
      }

      @Override
      public void readComplete() {
         this.record(this.totalBytesRead());
      }
   }
}
