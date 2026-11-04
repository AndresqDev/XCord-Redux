package io.netty.channel.epoll;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class EpollEventLoopGroup extends MultithreadEventLoopGroup {
   public EpollEventLoopGroup() {
      this(0);
   }

   public EpollEventLoopGroup(int var1) {
      this(var1, (ThreadFactory)null);
   }

   public EpollEventLoopGroup(ThreadFactory var1) {
      this(0, var1, 0);
   }

   public EpollEventLoopGroup(int var1, SelectStrategyFactory var2) {
      this(var1, (ThreadFactory)null, var2);
   }

   public EpollEventLoopGroup(int var1, ThreadFactory var2) {
      this(var1, var2, 0);
   }

   public EpollEventLoopGroup(int var1, Executor var2) {
      this(var1, var2, DefaultSelectStrategyFactory.INSTANCE);
   }

   public EpollEventLoopGroup(int var1, ThreadFactory var2, SelectStrategyFactory var3) {
      this(var1, var2, 0, var3);
   }

   @Deprecated
   public EpollEventLoopGroup(int var1, ThreadFactory var2, int var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   @Deprecated
   public EpollEventLoopGroup(int var1, ThreadFactory var2, int var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(int var1, Executor var2, SelectStrategyFactory var3) {
      super(var1, var2, 0, var3, RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, 0, var4, RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectStrategyFactory var4, RejectedExecutionHandler var5) {
      super(var1, var2, var3, 0, var4, var5);
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(
      int var1, Executor var2, EventExecutorChooserFactory var3, SelectStrategyFactory var4, RejectedExecutionHandler var5, EventLoopTaskQueueFactory var6
   ) {
      super(var1, var2, var3, 0, var4, var5, var6);
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(
      int var1,
      Executor var2,
      EventExecutorChooserFactory var3,
      SelectStrategyFactory var4,
      RejectedExecutionHandler var5,
      EventLoopTaskQueueFactory var6,
      EventLoopTaskQueueFactory var7
   ) {
      super(var1, var2, var3, 0, var4, var5, var6, var7);
      Epoll.ensureAvailability();
   }

   public void setIoRatio(int var1) {
      for (EventExecutor var3 : this) {
         ((EpollEventLoop)var3).setIoRatio(var1);
      }
   }

   @Override
   protected EventLoop newChild(Executor var1, Object... var2) throws Exception {
      Integer var3 = (Integer)var2[0];
      SelectStrategyFactory var4 = (SelectStrategyFactory)var2[1];
      RejectedExecutionHandler var5 = (RejectedExecutionHandler)var2[2];
      EventLoopTaskQueueFactory var6 = null;
      EventLoopTaskQueueFactory var7 = null;
      int var8 = var2.length;
      if (var8 > 3) {
         var6 = (EventLoopTaskQueueFactory)var2[3];
      }

      if (var8 > 4) {
         var7 = (EventLoopTaskQueueFactory)var2[4];
      }

      return new EpollEventLoop(this, var1, var3, var4.newSelectStrategy(), var5, var6, var7);
   }
}
