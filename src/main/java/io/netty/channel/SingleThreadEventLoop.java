package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
   protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventLoop.maxPendingTasks", Integer.MAX_VALUE));
   private final Queue<Runnable> tailTasks;

   protected SingleThreadEventLoop(EventLoopGroup var1, ThreadFactory var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, Executor var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, ThreadFactory var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1, var2, var3, var4, var5);
      this.tailTasks = this.newTaskQueue(var4);
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, Executor var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1, var2, var3, var4, var5);
      this.tailTasks = this.newTaskQueue(var4);
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, Executor var2, boolean var3, Queue<Runnable> var4, Queue<Runnable> var5, RejectedExecutionHandler var6) {
      super(var1, var2, var3, var4, var6);
      this.tailTasks = ObjectUtil.checkNotNull(var5, "tailTaskQueue");
   }

   @Override
   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   @Override
   public EventLoop next() {
      return (EventLoop)super.next();
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
      ObjectUtil.checkNotNull(var2, "promise");
      ObjectUtil.checkNotNull(var1, "channel");
      var1.unsafe().register(this, var2);
      return var2;
   }

   public final void executeAfterEventLoopIteration(Runnable var1) {
      ObjectUtil.checkNotNull(var1, "task");
      if (this.isShutdown()) {
         reject();
      }

      if (!this.tailTasks.offer(var1)) {
         this.reject(var1);
      }

      if (!(var1 instanceof AbstractEventExecutor.LazyRunnable) && this.wakesUpForTask(var1)) {
         this.wakeup(this.inEventLoop());
      }
   }

   final boolean removeAfterEventLoopIterationTask(Runnable var1) {
      return this.tailTasks.remove(ObjectUtil.checkNotNull(var1, "task"));
   }

   @Override
   protected void afterRunningAllTasks() {
      this.runAllTasksFrom(this.tailTasks);
   }

   @Override
   protected boolean hasTasks() {
      return super.hasTasks() || !this.tailTasks.isEmpty();
   }

   @Override
   public int pendingTasks() {
      return super.pendingTasks() + this.tailTasks.size();
   }

   public int registeredChannels() {
      return -1;
   }

   public Iterator<Channel> registeredChannelsIterator() {
      throw new UnsupportedOperationException("registeredChannelsIterator");
   }

   protected static final class ChannelsReadOnlyIterator<T extends Channel> implements Iterator<Channel> {
      private final Iterator<T> channelIterator;
      private static final Iterator<Object> EMPTY = new Iterator<Object>() {
         @Override
         public boolean hasNext() {
            return false;
         }

         @Override
         public Object next() {
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      };

      public ChannelsReadOnlyIterator(Iterable<T> var1) {
         this.channelIterator = ObjectUtil.checkNotNull(var1, "channelIterable").iterator();
      }

      @Override
      public boolean hasNext() {
         return this.channelIterator.hasNext();
      }

      public Channel next() {
         return this.channelIterator.next();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }

      public static <T> Iterator<T> empty() {
         return (Iterator<T>)EMPTY;
      }
   }
}
