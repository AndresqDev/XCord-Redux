package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

public class FixedRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {
   private final int bufferSize;

   public FixedRecvByteBufAllocator(int var1) {
      ObjectUtil.checkPositive(var1, "bufferSize");
      this.bufferSize = var1;
   }

   @Override
   public RecvByteBufAllocator.Handle newHandle() {
      return new FixedRecvByteBufAllocator.HandleImpl(this.bufferSize);
   }

   public FixedRecvByteBufAllocator respectMaybeMoreData(boolean var1) {
      super.respectMaybeMoreData(var1);
      return this;
   }

   private final class HandleImpl extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
      private final int bufferSize;

      HandleImpl(int var2) {
         this.bufferSize = var2;
      }

      @Override
      public int guess() {
         return this.bufferSize;
      }
   }
}
