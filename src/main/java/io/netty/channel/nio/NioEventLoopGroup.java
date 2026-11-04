package io.netty.channel.nio;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class NioEventLoopGroup extends MultithreadEventLoopGroup {
   public NioEventLoopGroup() {
      this(0);
   }

   public NioEventLoopGroup(int var1) {
      this(var1, (Executor)null);
   }

   public NioEventLoopGroup(ThreadFactory var1) {
      this(0, var1, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2) {
      this(var1, var2, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int var1, Executor var2) {
      this(var1, var2, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2, SelectorProvider var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2, SelectorProvider var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int var1, Executor var2, SelectorProvider var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int var1, Executor var2, SelectorProvider var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectorProvider var4, SelectStrategyFactory var5) {
      super(var1, var2, var3, var4, var5, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(
      int var1, Executor var2, EventExecutorChooserFactory var3, SelectorProvider var4, SelectStrategyFactory var5, RejectedExecutionHandler var6
   ) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public NioEventLoopGroup(
      int var1,
      Executor var2,
      EventExecutorChooserFactory var3,
      SelectorProvider var4,
      SelectStrategyFactory var5,
      RejectedExecutionHandler var6,
      EventLoopTaskQueueFactory var7
   ) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public NioEventLoopGroup(
      int var1,
      Executor var2,
      EventExecutorChooserFactory var3,
      SelectorProvider var4,
      SelectStrategyFactory var5,
      RejectedExecutionHandler var6,
      EventLoopTaskQueueFactory var7,
      EventLoopTaskQueueFactory var8
   ) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void setIoRatio(int var1) {
      for (EventExecutor var3 : this) {
         ((NioEventLoop)var3).setIoRatio(var1);
      }
   }

   public void rebuildSelectors() {
      for (EventExecutor var2 : this) {
         ((NioEventLoop)var2).rebuildSelector();
      }
   }

   @Override
   protected EventLoop newChild(Executor var1, Object... var2) throws Exception {
      SelectorProvider var3 = (SelectorProvider)var2[0];
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

      return new NioEventLoop(this, var1, var3, var4.newSelectStrategy(), var5, var6, var7);
   }
}
