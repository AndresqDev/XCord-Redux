package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class SimpleUserEventChannelHandler<I> extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;
   private final boolean autoRelease;

   protected SimpleUserEventChannelHandler() {
      this(true);
   }

   protected SimpleUserEventChannelHandler(boolean var1) {
      this.matcher = TypeParameterMatcher.find(this, SimpleUserEventChannelHandler.class, "I");
      this.autoRelease = var1;
   }

   protected SimpleUserEventChannelHandler(Class<? extends I> var1) {
      this(var1, true);
   }

   protected SimpleUserEventChannelHandler(Class<? extends I> var1, boolean var2) {
      this.matcher = TypeParameterMatcher.get(var1);
      this.autoRelease = var2;
   }

   protected boolean acceptEvent(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   @Override
   public final void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      boolean var3 = true;

      try {
         if (this.acceptEvent(var2)) {
            this.eventReceived(var1, (I)var2);
         } else {
            var3 = false;
            var1.fireUserEventTriggered(var2);
         }
      } finally {
         if (this.autoRelease && var3) {
            ReferenceCountUtil.release(var2);
         }
      }
   }

   protected abstract void eventReceived(ChannelHandlerContext var1, I var2) throws Exception;
}
