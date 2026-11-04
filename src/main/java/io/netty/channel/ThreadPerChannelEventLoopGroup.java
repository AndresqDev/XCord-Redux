package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {
   private final Object[] childArgs;
   private final int maxChannels;
   final Executor executor;
   final Set<EventLoop> activeChildren = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
   final Queue<EventLoop> idleChildren = new ConcurrentLinkedQueue<>();
   private final ChannelException tooManyChannels;
   private volatile boolean shuttingDown;
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final FutureListener<Object> childTerminationListener = new FutureListener<Object>() {
      @Override
      public void operationComplete(Future<Object> var1) throws Exception {
         if (ThreadPerChannelEventLoopGroup.this.isTerminated()) {
            ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess(null);
         }
      }
   };

   protected ThreadPerChannelEventLoopGroup() {
      this(0);
   }

   protected ThreadPerChannelEventLoopGroup(int var1) {
      this(var1, (ThreadFactory)null);
   }

   protected ThreadPerChannelEventLoopGroup(int var1, ThreadFactory var2, Object... var3) {
      this(var1, var2 == null ? null : new ThreadPerTaskExecutor(var2), var3);
   }

   protected ThreadPerChannelEventLoopGroup(int var1, Executor var2, Object... var3) {
      ObjectUtil.checkPositiveOrZero(var1, "maxChannels");
      if (var2 == null) {
         var2 = new ThreadPerTaskExecutor(new DefaultThreadFactory(this.getClass()));
      }

      if (var3 == null) {
         this.childArgs = EmptyArrays.EMPTY_OBJECTS;
      } else {
         this.childArgs = (Object[])var3.clone();
      }

      this.maxChannels = var1;
      this.executor = (Executor)var2;
      this.tooManyChannels = ChannelException.newStatic("too many channels (max: " + var1 + ')', ThreadPerChannelEventLoopGroup.class, "nextChild()");
   }

   protected EventLoop newChild(Object... var1) throws Exception {
      return new ThreadPerChannelEventLoop(this);
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return new ReadOnlyIterator<>(this.activeChildren.iterator());
   }

   @Override
   public EventLoop next() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      this.shuttingDown = true;

      for (EventLoop var7 : this.activeChildren) {
         var7.shutdownGracefully(var1, var3, var5);
      }

      for (EventLoop var9 : this.idleChildren) {
         var9.shutdownGracefully(var1, var3, var5);
      }

      if (this.isTerminated()) {
         this.terminationFuture.trySuccess(null);
      }

      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      this.shuttingDown = true;

      for (EventLoop var2 : this.activeChildren) {
         var2.shutdown();
      }

      for (EventLoop var4 : this.idleChildren) {
         var4.shutdown();
      }

      if (this.isTerminated()) {
         this.terminationFuture.trySuccess(null);
      }
   }

   @Override
   public boolean isShuttingDown() {
      for (EventLoop var2 : this.activeChildren) {
         if (!var2.isShuttingDown()) {
            return false;
         }
      }

      for (EventLoop var4 : this.idleChildren) {
         if (!var4.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isShutdown() {
      for (EventLoop var2 : this.activeChildren) {
         if (!var2.isShutdown()) {
            return false;
         }
      }

      for (EventLoop var4 : this.idleChildren) {
         if (!var4.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isTerminated() {
      for (EventLoop var2 : this.activeChildren) {
         if (!var2.isTerminated()) {
            return false;
         }
      }

      for (EventLoop var4 : this.idleChildren) {
         if (!var4.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = System.nanoTime() + var3.toNanos(var1);

      long var8;
      for (EventLoop var7 : this.activeChildren) {
         do {
            var8 = var4 - System.nanoTime();
            if (var8 <= 0L) {
               return this.isTerminated();
            }
         } while (var7.awaitTermination(var8, TimeUnit.NANOSECONDS));
      }

      for (EventLoop var11 : this.idleChildren) {
         do {
            var8 = var4 - System.nanoTime();
            if (var8 <= 0L) {
               return this.isTerminated();
            }
         } while (var11.awaitTermination(var8, TimeUnit.NANOSECONDS));
      }

      return this.isTerminated();
   }

   @Override
   public ChannelFuture register(Channel var1) {
      ObjectUtil.checkNotNull(var1, "channel");

      try {
         EventLoop var2 = this.nextChild();
         return var2.register(new DefaultChannelPromise(var1, var2));
      } catch (Throwable var3) {
         return new FailedChannelFuture(var1, GlobalEventExecutor.INSTANCE, var3);
      }
   }

   @Override
   public ChannelFuture register(ChannelPromise var1) {
      try {
         return this.nextChild().register(var1);
      } catch (Throwable var3) {
         var1.setFailure(var3);
         return var1;
      }
   }

   @Deprecated
   @Override
   public ChannelFuture register(Channel var1, ChannelPromise var2) {
      ObjectUtil.checkNotNull(var1, "channel");

      try {
         return this.nextChild().register(var1, var2);
      } catch (Throwable var4) {
         var2.setFailure(var4);
         return var2;
      }
   }

   private EventLoop nextChild() throws Exception {
      if (this.shuttingDown) {
         throw new RejectedExecutionException("shutting down");
      } else {
         EventLoop var1 = this.idleChildren.poll();
         if (var1 == null) {
            if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
               throw this.tooManyChannels;
            }

            var1 = this.newChild(this.childArgs);
            var1.terminationFuture().addListener(this.childTerminationListener);
         }

         this.activeChildren.add(var1);
         return var1;
      }
   }
}
