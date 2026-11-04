package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class NonStickyEventExecutorGroup implements EventExecutorGroup {
   private final EventExecutorGroup group;
   private final int maxTaskExecutePerRun;

   public NonStickyEventExecutorGroup(EventExecutorGroup var1) {
      this(var1, 1024);
   }

   public NonStickyEventExecutorGroup(EventExecutorGroup var1, int var2) {
      this.group = verify(var1);
      this.maxTaskExecutePerRun = ObjectUtil.checkPositive(var2, "maxTaskExecutePerRun");
   }

   private static EventExecutorGroup verify(EventExecutorGroup var0) {
      for (EventExecutor var2 : ObjectUtil.checkNotNull(var0, "group")) {
         if (var2 instanceof OrderedEventExecutor) {
            throw new IllegalArgumentException("EventExecutorGroup " + var0 + " contains OrderedEventExecutors: " + var2);
         }
      }

      return var0;
   }

   private NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor newExecutor(EventExecutor var1) {
      return new NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor(var1, this.maxTaskExecutePerRun);
   }

   @Override
   public boolean isShuttingDown() {
      return this.group.isShuttingDown();
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.group.shutdownGracefully();
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      return this.group.shutdownGracefully(var1, var3, var5);
   }

   @Override
   public Future<?> terminationFuture() {
      return this.group.terminationFuture();
   }

   @Override
   public void shutdown() {
      this.group.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return this.group.shutdownNow();
   }

   @Override
   public EventExecutor next() {
      return this.newExecutor(this.group.next());
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      final Iterator var1 = this.group.iterator();
      return new Iterator<EventExecutor>() {
         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         public EventExecutor next() {
            return NonStickyEventExecutorGroup.this.newExecutor((EventExecutor)var1.next());
         }

         @Override
         public void remove() {
            var1.remove();
         }
      };
   }

   @Override
   public Future<?> submit(Runnable var1) {
      return this.group.submit(var1);
   }

   @Override
   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.group.submit(var1, (T)var2);
   }

   @Override
   public <T> Future<T> submit(Callable<T> var1) {
      return this.group.submit(var1);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return this.group.schedule(var1, var2, var4);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return this.group.schedule(var1, var2, var4);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.group.scheduleAtFixedRate(var1, var2, var4, var6);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.group.scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   @Override
   public boolean isShutdown() {
      return this.group.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.group.isTerminated();
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      return this.group.awaitTermination(var1, var3);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      return this.group.invokeAll(var1);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.group.invokeAll(var1, var2, var4);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      return this.group.invokeAny(var1);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      return this.group.invokeAny(var1, var2, var4);
   }

   @Override
   public void execute(Runnable var1) {
      this.group.execute(var1);
   }

   private static final class NonStickyOrderedEventExecutor extends AbstractEventExecutor implements OrderedEventExecutor, Runnable {
      private final EventExecutor executor;
      private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
      private static final int NONE = 0;
      private static final int SUBMITTED = 1;
      private static final int RUNNING = 2;
      private final AtomicInteger state = new AtomicInteger();
      private final int maxTaskExecutePerRun;

      NonStickyOrderedEventExecutor(EventExecutor var1, int var2) {
         super(var1);
         this.executor = var1;
         this.maxTaskExecutePerRun = var2;
      }

      @Override
      public void run() {
      }

      @Override
      public boolean inEventLoop(Thread var1) {
         return false;
      }

      @Override
      public boolean inEventLoop() {
         return false;
      }

      @Override
      public boolean isShuttingDown() {
         return this.executor.isShutdown();
      }

      @Override
      public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
         return this.executor.shutdownGracefully(var1, var3, var5);
      }

      @Override
      public Future<?> terminationFuture() {
         return this.executor.terminationFuture();
      }

      @Override
      public void shutdown() {
         this.executor.shutdown();
      }

      @Override
      public boolean isShutdown() {
         return this.executor.isShutdown();
      }

      @Override
      public boolean isTerminated() {
         return this.executor.isTerminated();
      }

      @Override
      public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         return this.executor.awaitTermination(var1, var3);
      }

      @Override
      public void execute(Runnable var1) {
         if (!this.tasks.offer(var1)) {
            throw new RejectedExecutionException();
         } else {
            if (this.state.compareAndSet(0, 1)) {
               this.executor.execute(this);
            }
         }
      }
   }
}
