package io.netty.channel;

import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventLoop extends SingleThreadEventLoop {
   public DefaultEventLoop() {
      this((EventLoopGroup)null);
   }

   public DefaultEventLoop(ThreadFactory var1) {
      this(null, var1);
   }

   public DefaultEventLoop(Executor var1) {
      this(null, var1);
   }

   public DefaultEventLoop(EventLoopGroup var1) {
      this(var1, new DefaultThreadFactory(DefaultEventLoop.class));
   }

   public DefaultEventLoop(EventLoopGroup var1, ThreadFactory var2) {
      super(var1, var2, true);
   }

   public DefaultEventLoop(EventLoopGroup var1, Executor var2) {
      super(var1, var2, true);
   }

   @Override
   protected void run() {
      do {
         Runnable var1 = this.takeTask();
         if (var1 != null) {
            runTask(var1);
            this.updateLastExecutionTime();
         }
      } while (!this.confirmShutdown());
   }
}
