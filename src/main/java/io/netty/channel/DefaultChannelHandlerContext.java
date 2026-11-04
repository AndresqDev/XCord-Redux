package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {
   private final ChannelHandler handler;

   DefaultChannelHandlerContext(DefaultChannelPipeline var1, EventExecutor var2, String var3, ChannelHandler var4) {
      super(var1, var2, var3, (Class<? extends ChannelHandler>)var4.getClass());
      this.handler = var4;
   }

   @Override
   public ChannelHandler handler() {
      return this.handler;
   }
}
