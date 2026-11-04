package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Async.Execute;
import org.jetbrains.annotations.Async.Schedule;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEventExecutor.class);
   static final long DEFAULT_SHUTDOWN_QUIET_PERIOD = 2L;
   static final long DEFAULT_SHUTDOWN_TIMEOUT = 15L;
   private final EventExecutorGroup parent;
   private final Collection<EventExecutor> selfCollection = Collections.singleton(this);

   protected AbstractEventExecutor() {
      this(null);
   }

   protected AbstractEventExecutor(EventExecutorGroup var1) {
      this.parent = var1;
   }

   @Override
   public EventExecutorGroup parent() {
      return this.parent;
   }

   @Override
   public EventExecutor next() {
      return this;
   }

   @Override
   public boolean inEventLoop() {
      return this.inEventLoop(Thread.currentThread());
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.selfCollection.iterator();
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   @Deprecated
   @Override
   public abstract void shutdown();

   @Deprecated
   @Override
   public List<Runnable> shutdownNow() {
      this.shutdown();
      return Collections.emptyList();
   }

   @Override
   public <V> Promise<V> newPromise() {
      return new DefaultPromise<>(this);
   }

   @Override
   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new DefaultProgressivePromise<>(this);
   }

   @Override
   public <V> Future<V> newSucceededFuture(V var1) {
      return new SucceededFuture<>(this, (V)var1);
   }

   @Override
   public <V> Future<V> newFailedFuture(Throwable var1) {
      return new FailedFuture<>(this, var1);
   }

   @Override
   public Future<?> submit(Runnable var1) {
      return (Future<?>)super.submit(var1);
   }

   @Override
   public <T> Future<T> submit(Runnable var1, T var2) {
      return (Future<T>)super.<T>submit(var1, (T)var2);
   }

   @Override
   public <T> Future<T> submit(Callable<T> var1) {
      return (Future<T>)super.<T>submit(var1);
   }

   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return new PromiseTask<>(this, var1, (T)var2);
   }

   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return new PromiseTask<>(this, var1);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      throw new UnsupportedOperationException();
   }

   protected static void safeExecute(Runnable var0) {
      try {
         runTask(var0);
      } catch (Throwable var2) {
         logger.warn("A task raised an exception. Task: {}", var0, var2);
      }
   }

   protected static void runTask(@Execute Runnable var0) {
      var0.run();
   }

   public void lazyExecute(Runnable var1) {
      this.lazyExecute0(var1);
   }

   private void lazyExecute0(@Schedule Runnable var1) {
      this.execute(var1);
   }

   public interface LazyRunnable extends Runnable {
   }
}
