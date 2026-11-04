package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.internal.ObjectUtil;

public final class DefaultMessageSizeEstimator implements MessageSizeEstimator {
   public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator(8);
   private final MessageSizeEstimator.Handle handle;

   public DefaultMessageSizeEstimator(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "unknownSize");
      this.handle = new DefaultMessageSizeEstimator.HandleImpl(var1);
   }

   @Override
   public MessageSizeEstimator.Handle newHandle() {
      return this.handle;
   }

   private static final class HandleImpl implements MessageSizeEstimator.Handle {
      private final int unknownSize;

      private HandleImpl(int var1) {
         this.unknownSize = var1;
      }

      @Override
      public int size(Object var1) {
         if (var1 instanceof ByteBuf) {
            return ((ByteBuf)var1).readableBytes();
         } else if (var1 instanceof ByteBufHolder) {
            return ((ByteBufHolder)var1).content().readableBytes();
         } else {
            return var1 instanceof FileRegion ? 0 : this.unknownSize;
         }
      }
   }
}
