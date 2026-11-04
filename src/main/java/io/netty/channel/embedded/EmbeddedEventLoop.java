package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop extends AbstractScheduledEventExecutor implements EventLoop {
   private long startTime = initialNanoTime();
   private long frozenTimestamp;
   private boolean timeFrozen;
   private final Queue<Runnable> tasks = new ArrayDeque<>(2);

   @Override
   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   @Override
   public EventLoop next() {
      return (EventLoop)super.next();
   }

   @Override
   public void execute(Runnable var1) {
      this.tasks.add(ObjectUtil.checkNotNull(var1, "command"));
   }

   void runTasks() {
      while (true) {
         Runnable var1 = this.tasks.poll();
         if (var1 == null) {
            return;
         }

         var1.run();
      }
   }

   boolean hasPendingNormalTasks() {
      return !this.tasks.isEmpty();
   }

   long runScheduledTasks() {
      long var1 = this.getCurrentTimeNanos();

      while (true) {
         Runnable var3 = this.pollScheduledTask(var1);
         if (var3 == null) {
            return this.nextScheduledTaskNano();
         }

         var3.run();
      }
   }

   long nextScheduledTask() {
      return this.nextScheduledTaskNano();
   }

   @Override
   protected long getCurrentTimeNanos() {
      return this.timeFrozen ? this.frozenTimestamp : System.nanoTime() - this.startTime;
   }

   void advanceTimeBy(long var1) {
      if (this.timeFrozen) {
         this.frozenTimestamp += var1;
      } else {
         this.startTime -= var1;
      }
   }

   void freezeTime() {
      if (!this.timeFrozen) {
         this.frozenTimestamp = this.getCurrentTimeNanos();
         this.timeFrozen = true;
      }
   }

   void unfreezeTime() {
      if (this.timeFrozen) {
         this.startTime = System.nanoTime() - this.frozenTimestamp;
         this.timeFrozen = false;
      }
   }

   @Override
   protected void cancelScheduledTasks() {
      super.cancelScheduledTasks();
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Future<?> terminationFuture() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void shutdown() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isShuttingDown() {
      return false;
   }

   @Override
   public boolean isShutdown() {
      return false;
   }

   @Override
   public boolean isTerminated() {
      return false;
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) {
      return false;
   }

   @Override
   public ChannelFuture register(Channel var1) {
      return this.register(new DefaultChannelPromise(var1, this));
   }

   @Override
   public ChannelFuture register(ChannelPromise var1) {
      ObjectUtil.checkNotNull(var1, "promise");
      var1.channel().unsafe().register(this, var1);
      return var1;
   }

   @Deprecated
   @Override
   public ChannelFuture register(Channel var1, ChannelPromise var2) {
      var1.unsafe().register(this, var2);
      return var2;
   }

   @Override
   public boolean inEventLoop() {
      return true;
   }

   @Override
   public boolean inEventLoop(Thread var1) {
      return true;
   }
}
