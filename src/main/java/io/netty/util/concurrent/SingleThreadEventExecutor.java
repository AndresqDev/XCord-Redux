package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.jetbrains.annotations.Async.Schedule;

public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor {
   static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
   private static final int ST_NOT_STARTED = 1;
   private static final int ST_STARTED = 2;
   private static final int ST_SHUTTING_DOWN = 3;
   private static final int ST_SHUTDOWN = 4;
   private static final int ST_TERMINATED = 5;
   private static final Runnable NOOP_TASK = new Runnable() {
      @Override
      public void run() {
      }
   };
   private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, "state"
   );
   private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties"
   );
   private final Queue<Runnable> taskQueue;
   private volatile Thread thread;
   private volatile ThreadProperties threadProperties;
   private final Executor executor;
   private volatile boolean interrupted;
   private final CountDownLatch threadLock = new CountDownLatch(1);
   private final Set<Runnable> shutdownHooks = new LinkedHashSet<>();
   private final boolean addTaskWakesUp;
   private final int maxPendingTasks;
   private final RejectedExecutionHandler rejectedExecutionHandler;
   private long lastExecutionTime;
   private volatile int state = 1;
   private volatile long gracefulShutdownQuietPeriod;
   private volatile long gracefulShutdownTimeout;
   private long gracefulShutdownStartTime;
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);

   protected SingleThreadEventExecutor(EventExecutorGroup var1, ThreadFactory var2, boolean var3) {
      this(var1, new ThreadPerTaskExecutor(var2), var3);
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, ThreadFactory var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      this(var1, new ThreadPerTaskExecutor(var2), var3, var4, var5);
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, Executor var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, Executor var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1);
      this.addTaskWakesUp = var3;
      this.maxPendingTasks = Math.max(16, var4);
      this.executor = ThreadExecutorMap.apply(var2, this);
      this.taskQueue = this.newTaskQueue(this.maxPendingTasks);
      this.rejectedExecutionHandler = ObjectUtil.checkNotNull(var5, "rejectedHandler");
   }

   protected SingleThreadEventExecutor(EventExecutorGroup var1, Executor var2, boolean var3, Queue<Runnable> var4, RejectedExecutionHandler var5) {
      super(var1);
      this.addTaskWakesUp = var3;
      this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
      this.executor = ThreadExecutorMap.apply(var2, this);
      this.taskQueue = ObjectUtil.checkNotNull(var4, "taskQueue");
      this.rejectedExecutionHandler = ObjectUtil.checkNotNull(var5, "rejectedHandler");
   }

   @Deprecated
   protected Queue<Runnable> newTaskQueue() {
      return this.newTaskQueue(this.maxPendingTasks);
   }

   protected Queue<Runnable> newTaskQueue(int var1) {
      return new LinkedBlockingQueue<>(var1);
   }

   protected void interruptThread() {
      Thread var1 = this.thread;
      if (var1 == null) {
         this.interrupted = true;
      } else {
         var1.interrupt();
      }
   }

   protected Runnable pollTask() {
      assert this.inEventLoop();

      return pollTaskFrom(this.taskQueue);
   }

   protected static Runnable pollTaskFrom(Queue<Runnable> var0) {
      Runnable var1;
      do {
         var1 = (Runnable)var0.poll();
      } while (var1 == WAKEUP_TASK);

      return var1;
   }

   protected Runnable takeTask() {
      assert this.inEventLoop();

      if (!(this.taskQueue instanceof BlockingQueue)) {
         throw new UnsupportedOperationException();
      } else {
         BlockingQueue var1 = (BlockingQueue)this.taskQueue;

         Runnable var5;
         do {
            ScheduledFutureTask var2 = this.peekScheduledTask();
            if (var2 == null) {
               Runnable var9 = null;

               try {
                  var9 = (Runnable)var1.take();
                  if (var9 == WAKEUP_TASK) {
                     var9 = null;
                  }
               } catch (InterruptedException var7) {
               }

               return var9;
            }

            long var3 = var2.delayNanos();
            var5 = null;
            if (var3 > 0L) {
               try {
                  var5 = (Runnable)var1.poll(var3, TimeUnit.NANOSECONDS);
               } catch (InterruptedException var8) {
                  return null;
               }
            }

            if (var5 == null) {
               this.fetchFromScheduledTaskQueue();
               var5 = (Runnable)var1.poll();
            }
         } while (var5 == null);

         return var5;
      }
   }

   private boolean fetchFromScheduledTaskQueue() {
      if (this.scheduledTaskQueue != null && !this.scheduledTaskQueue.isEmpty()) {
         long var1 = this.getCurrentTimeNanos();

         Runnable var3;
         do {
            var3 = this.pollScheduledTask(var1);
            if (var3 == null) {
               return true;
            }
         } while (this.taskQueue.offer(var3));

         this.scheduledTaskQueue.add((ScheduledFutureTask<?>)var3);
         return false;
      } else {
         return true;
      }
   }

   private boolean executeExpiredScheduledTasks() {
      if (this.scheduledTaskQueue != null && !this.scheduledTaskQueue.isEmpty()) {
         long var1 = this.getCurrentTimeNanos();
         Runnable var3 = this.pollScheduledTask(var1);
         if (var3 == null) {
            return false;
         } else {
            do {
               safeExecute(var3);
            } while ((var3 = this.pollScheduledTask(var1)) != null);

            return true;
         }
      } else {
         return false;
      }
   }

   protected Runnable peekTask() {
      assert this.inEventLoop();

      return this.taskQueue.peek();
   }

   protected boolean hasTasks() {
      assert this.inEventLoop();

      return !this.taskQueue.isEmpty();
   }

   public int pendingTasks() {
      return this.taskQueue.size();
   }

   protected void addTask(Runnable var1) {
      ObjectUtil.checkNotNull(var1, "task");
      if (!this.offerTask(var1)) {
         this.reject(var1);
      }
   }

   final boolean offerTask(Runnable var1) {
      if (this.isShutdown()) {
         reject();
      }

      return this.taskQueue.offer(var1);
   }

   protected boolean removeTask(Runnable var1) {
      return this.taskQueue.remove(ObjectUtil.checkNotNull(var1, "task"));
   }

   protected boolean runAllTasks() {
      assert this.inEventLoop();

      boolean var2 = false;

      boolean var1;
      do {
         var1 = this.fetchFromScheduledTaskQueue();
         if (this.runAllTasksFrom(this.taskQueue)) {
            var2 = true;
         }
      } while (!var1);

      if (var2) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      this.afterRunningAllTasks();
      return var2;
   }

   protected final boolean runScheduledAndExecutorTasks(int var1) {
      assert this.inEventLoop();

      int var3 = 0;

      boolean var2;
      do {
         var2 = this.runExistingTasksFrom(this.taskQueue) | this.executeExpiredScheduledTasks();
      } while (var2 && ++var3 < var1);

      if (var3 > 0) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      this.afterRunningAllTasks();
      return var3 > 0;
   }

   protected final boolean runAllTasksFrom(Queue<Runnable> var1) {
      Runnable var2 = pollTaskFrom(var1);
      if (var2 == null) {
         return false;
      } else {
         do {
            safeExecute(var2);
            var2 = pollTaskFrom(var1);
         } while (var2 != null);

         return true;
      }
   }

   private boolean runExistingTasksFrom(Queue<Runnable> var1) {
      Runnable var2 = pollTaskFrom(var1);
      if (var2 == null) {
         return false;
      } else {
         int var3 = Math.min(this.maxPendingTasks, var1.size());
         safeExecute(var2);

         while (var3-- > 0 && (var2 = (Runnable)var1.poll()) != null) {
            safeExecute(var2);
         }

         return true;
      }
   }

   protected boolean runAllTasks(long var1) {
      this.fetchFromScheduledTaskQueue();
      Runnable var3 = this.pollTask();
      if (var3 == null) {
         this.afterRunningAllTasks();
         return false;
      } else {
         long var4 = var1 > 0L ? this.getCurrentTimeNanos() + var1 : 0L;
         long var6 = 0L;

         long var8;
         while (true) {
            safeExecute(var3);
            if ((++var6 & 63L) == 0L) {
               var8 = this.getCurrentTimeNanos();
               if (var8 >= var4) {
                  break;
               }
            }

            var3 = this.pollTask();
            if (var3 == null) {
               var8 = this.getCurrentTimeNanos();
               break;
            }
         }

         this.afterRunningAllTasks();
         this.lastExecutionTime = var8;
         return true;
      }
   }

   protected void afterRunningAllTasks() {
   }

   protected long delayNanos(long var1) {
      var1 -= initialNanoTime();
      ScheduledFutureTask var3 = this.peekScheduledTask();
      return var3 == null ? SCHEDULE_PURGE_INTERVAL : var3.delayNanos(var1);
   }

   protected long deadlineNanos() {
      ScheduledFutureTask var1 = this.peekScheduledTask();
      return var1 == null ? this.getCurrentTimeNanos() + SCHEDULE_PURGE_INTERVAL : var1.deadlineNanos();
   }

   protected void updateLastExecutionTime() {
      this.lastExecutionTime = this.getCurrentTimeNanos();
   }

   protected abstract void run();

   protected void cleanup() {
   }

   protected void wakeup(boolean var1) {
      if (!var1) {
         this.taskQueue.offer(WAKEUP_TASK);
      }
   }

   @Override
   public boolean inEventLoop(Thread var1) {
      return var1 == this.thread;
   }

   public void addShutdownHook(final Runnable var1) {
      if (this.inEventLoop()) {
         this.shutdownHooks.add(var1);
      } else {
         this.execute(new Runnable() {
            @Override
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.add(var1);
            }
         });
      }
   }

   public void removeShutdownHook(final Runnable var1) {
      if (this.inEventLoop()) {
         this.shutdownHooks.remove(var1);
      } else {
         this.execute(new Runnable() {
            @Override
            public void run() {
               SingleThreadEventExecutor.this.shutdownHooks.remove(var1);
            }
         });
      }
   }

   private boolean runShutdownHooks() {
      boolean var1 = false;

      while (!this.shutdownHooks.isEmpty()) {
         ArrayList var2 = new ArrayList<>(this.shutdownHooks);
         this.shutdownHooks.clear();

         for (Runnable var4 : var2) {
            try {
               runTask(var4);
            } catch (Throwable var9) {
               logger.warn("Shutdown hook raised an exception.", var9);
            } finally {
               var1 = true;
            }
         }
      }

      if (var1) {
         this.lastExecutionTime = this.getCurrentTimeNanos();
      }

      return var1;
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      ObjectUtil.checkPositiveOrZero(var1, "quietPeriod");
      if (var3 < var1) {
         throw new IllegalArgumentException("timeout: " + var3 + " (expected >= quietPeriod (" + var1 + "))");
      } else {
         ObjectUtil.checkNotNull(var5, "unit");
         if (this.isShuttingDown()) {
            return this.terminationFuture();
         } else {
            boolean var6 = this.inEventLoop();

            while (!this.isShuttingDown()) {
               boolean var7 = true;
               int var8 = this.state;
               int var9;
               if (var6) {
                  var9 = 3;
               } else {
                  switch (var8) {
                     case 1:
                     case 2:
                        var9 = 3;
                        break;
                     default:
                        var9 = var8;
                        var7 = false;
                  }
               }

               if (STATE_UPDATER.compareAndSet(this, var8, var9)) {
                  this.gracefulShutdownQuietPeriod = var5.toNanos(var1);
                  this.gracefulShutdownTimeout = var5.toNanos(var3);
                  if (this.ensureThreadStarted(var8)) {
                     return this.terminationFuture;
                  }

                  if (var7) {
                     this.taskQueue.offer(WAKEUP_TASK);
                     if (!this.addTaskWakesUp) {
                        this.wakeup(var6);
                     }
                  }

                  return this.terminationFuture();
               }
            }

            return this.terminationFuture();
         }
      }
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      if (!this.isShutdown()) {
         boolean var1 = this.inEventLoop();

         while (!this.isShuttingDown()) {
            boolean var2 = true;
            int var3 = this.state;
            int var4;
            if (var1) {
               var4 = 4;
            } else {
               switch (var3) {
                  case 1:
                  case 2:
                  case 3:
                     var4 = 4;
                     break;
                  default:
                     var4 = var3;
                     var2 = false;
               }
            }

            if (STATE_UPDATER.compareAndSet(this, var3, var4)) {
               if (this.ensureThreadStarted(var3)) {
                  return;
               }

               if (var2) {
                  this.taskQueue.offer(WAKEUP_TASK);
                  if (!this.addTaskWakesUp) {
                     this.wakeup(var1);
                  }
               }

               return;
            }
         }
      }
   }

   @Override
   public boolean isShuttingDown() {
      return this.state >= 3;
   }

   @Override
   public boolean isShutdown() {
      return this.state >= 4;
   }

   @Override
   public boolean isTerminated() {
      return this.state == 5;
   }

   protected boolean confirmShutdown() {
      if (!this.isShuttingDown()) {
         return false;
      } else if (!this.inEventLoop()) {
         throw new IllegalStateException("must be invoked from an event loop");
      } else {
         this.cancelScheduledTasks();
         if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = this.getCurrentTimeNanos();
         }

         if (!this.runAllTasks() && !this.runShutdownHooks()) {
            long var1 = this.getCurrentTimeNanos();
            if (this.isShutdown() || var1 - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
               return true;
            } else if (var1 - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
               this.taskQueue.offer(WAKEUP_TASK);

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var4) {
               }

               return false;
            } else {
               return true;
            }
         } else if (this.isShutdown()) {
            return true;
         } else if (this.gracefulShutdownQuietPeriod == 0L) {
            return true;
         } else {
            this.taskQueue.offer(WAKEUP_TASK);
            return false;
         }
      }
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      ObjectUtil.checkNotNull(var3, "unit");
      if (this.inEventLoop()) {
         throw new IllegalStateException("cannot await termination of the current thread");
      } else {
         this.threadLock.await(var1, var3);
         return this.isTerminated();
      }
   }

   @Override
   public void execute(Runnable var1) {
      this.execute0(var1);
   }

   @Override
   public void lazyExecute(Runnable var1) {
      this.lazyExecute0(var1);
   }

   private void execute0(@Schedule Runnable var1) {
      ObjectUtil.checkNotNull(var1, "task");
      this.execute(var1, !(var1 instanceof AbstractEventExecutor.LazyRunnable) && this.wakesUpForTask(var1));
   }

   private void lazyExecute0(@Schedule Runnable var1) {
      this.execute(ObjectUtil.checkNotNull(var1, "task"), false);
   }

   private void execute(Runnable var1, boolean var2) {
      boolean var3 = this.inEventLoop();
      this.addTask(var1);
      if (!var3) {
         this.startThread();
         if (this.isShutdown()) {
            boolean var4 = false;

            try {
               if (this.removeTask(var1)) {
                  var4 = true;
               }
            } catch (UnsupportedOperationException var6) {
            }

            if (var4) {
               reject();
            }
         }
      }

      if (!this.addTaskWakesUp && var2) {
         this.wakeup(var3);
      }
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(var1);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
      this.throwIfInEventLoop("invokeAny");
      return super.invokeAny(var1, var2, var4);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(var1);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
      this.throwIfInEventLoop("invokeAll");
      return super.invokeAll(var1, var2, var4);
   }

   private void throwIfInEventLoop(String var1) {
      if (this.inEventLoop()) {
         throw new RejectedExecutionException("Calling " + var1 + " from within the EventLoop is not allowed");
      }
   }

   public final ThreadProperties threadProperties() {
      Object var1 = this.threadProperties;
      if (var1 == null) {
         Thread var2 = this.thread;
         if (var2 == null) {
            assert !this.inEventLoop();

            this.submit(NOOP_TASK).syncUninterruptibly();
            var2 = this.thread;

            assert var2 != null;
         }

         var1 = new SingleThreadEventExecutor.DefaultThreadProperties(var2);
         if (!PROPERTIES_UPDATER.compareAndSet(this, null, (ThreadProperties)var1)) {
            var1 = this.threadProperties;
         }
      }

      return (ThreadProperties)var1;
   }

   protected boolean wakesUpForTask(Runnable var1) {
      return true;
   }

   protected static void reject() {
      throw new RejectedExecutionException("event executor terminated");
   }

   protected final void reject(Runnable var1) {
      this.rejectedExecutionHandler.rejected(var1, this);
   }

   private void startThread() {
      if (this.state == 1 && STATE_UPDATER.compareAndSet(this, 1, 2)) {
         boolean var1 = false;

         try {
            this.doStartThread();
            var1 = true;
         } finally {
            if (!var1) {
               STATE_UPDATER.compareAndSet(this, 2, 1);
            }
         }
      }
   }

   private boolean ensureThreadStarted(int var1) {
      if (var1 == 1) {
         try {
            this.doStartThread();
         } catch (Throwable var3) {
            STATE_UPDATER.set(this, 5);
            this.terminationFuture.tryFailure(var3);
            if (!(var3 instanceof Exception)) {
               PlatformDependent.throwException(var3);
            }

            return true;
         }
      }

      return false;
   }

   private void doStartThread() {
      assert this.thread == null;

      this.executor
         .execute(
            new Runnable() {
               @Override
               public void run() {
                  SingleThreadEventExecutor.this.thread = Thread.currentThread();
                  if (SingleThreadEventExecutor.this.interrupted) {
                     SingleThreadEventExecutor.this.thread.interrupt();
                  }

                  boolean var1 = false;
                  SingleThreadEventExecutor.this.updateLastExecutionTime();

                  try {
                     SingleThreadEventExecutor.this.run();
                     var1 = true;
                  } catch (Throwable var121) {
                     SingleThreadEventExecutor.logger.warn("Unexpected exception from an event executor: ", var121);
                  } finally {
                     int var16;
                     do {
                        var16 = SingleThreadEventExecutor.this.state;
                     } while (var16 < 3 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, var16, 3));

                     if (var1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && SingleThreadEventExecutor.logger.isErrorEnabled()) {
                        SingleThreadEventExecutor.logger
                           .error(
                              "Buggy "
                                 + EventExecutor.class.getSimpleName()
                                 + " implementation; "
                                 + SingleThreadEventExecutor.class.getSimpleName()
                                 + ".confirmShutdown() must be called before run() implementation terminates."
                           );
                     }

                     try {
                        while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                        }

                        do {
                           var16 = SingleThreadEventExecutor.this.state;
                        } while (var16 < 4 && !SingleThreadEventExecutor.STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, var16, 4));

                        SingleThreadEventExecutor.this.confirmShutdown();
                     } finally {
                        try {
                           SingleThreadEventExecutor.this.cleanup();
                        } finally {
                           FastThreadLocal.removeAll();
                           SingleThreadEventExecutor.STATE_UPDATER.set(SingleThreadEventExecutor.this, 5);
                           SingleThreadEventExecutor.this.threadLock.countDown();
                           int var22 = SingleThreadEventExecutor.this.drainTasks();
                           if (var22 > 0 && SingleThreadEventExecutor.logger.isWarnEnabled()) {
                              SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + var22 + ')');
                           }

                           SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                        }
                     }
                  }
               }
            }
         );
   }

   final int drainTasks() {
      int var1 = 0;

      while (true) {
         Runnable var2 = this.taskQueue.poll();
         if (var2 == null) {
            return var1;
         }

         if (WAKEUP_TASK != var2) {
            var1++;
         }
      }
   }

   private static final class DefaultThreadProperties implements ThreadProperties {
      private final Thread t;

      DefaultThreadProperties(Thread var1) {
         this.t = var1;
      }

      @Override
      public State state() {
         return this.t.getState();
      }

      @Override
      public int priority() {
         return this.t.getPriority();
      }

      @Override
      public boolean isInterrupted() {
         return this.t.isInterrupted();
      }

      @Override
      public boolean isDaemon() {
         return this.t.isDaemon();
      }

      @Override
      public String name() {
         return this.t.getName();
      }

      @Override
      public long id() {
         return this.t.getId();
      }

      @Override
      public StackTraceElement[] stackTrace() {
         return this.t.getStackTrace();
      }

      @Override
      public boolean isAlive() {
         return this.t.isAlive();
      }
   }

   @Deprecated
   protected interface NonWakeupRunnable extends AbstractEventExecutor.LazyRunnable {
   }
}
