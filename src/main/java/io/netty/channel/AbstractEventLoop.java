package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutor;

public abstract class AbstractEventLoop extends AbstractEventExecutor implements EventLoop {
   protected AbstractEventLoop() {
   }

   protected AbstractEventLoop(EventLoopGroup var1) {
      super(var1);
   }

   @Override
   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   @Override
   public EventLoop next() {
      return (EventLoop)super.next();
   }
}
