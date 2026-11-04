package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {
   private static final Comparator<ScheduledFutureTask<?>> SCHEDULED_FUTURE_TASK_COMPARATOR = new Comparator<ScheduledFutureTask<?>>() {
      public int compare(ScheduledFutureTask<?> var1, ScheduledFutureTask<?> var2) {
         return var1.compareTo((Delayed)var2);
      }
   };
   private static final long START_TIME = System.nanoTime();
   static final Runnable WAKEUP_TASK = new Runnable() {
      @Override
      public void run() {
      }
   };
   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
   long nextTaskId;

   protected AbstractScheduledEventExecutor() {
   }

   protected AbstractScheduledEventExecutor(EventExecutorGroup var1) {
      super(var1);
   }

   protected long getCurrentTimeNanos() {
      return defaultCurrentTimeNanos();
   }

   @Deprecated
   protected static long nanoTime() {
      return defaultCurrentTimeNanos();
   }

   static long defaultCurrentTimeNanos() {
      return System.nanoTime() - START_TIME;
   }

   static long deadlineNanos(long var0, long var2) {
      long var4 = var0 + var2;
      return var4 < 0L ? Long.MAX_VALUE : var4;
   }

   protected static long deadlineToDelayNanos(long var0) {
      return ScheduledFutureTask.deadlineToDelayNanos(defaultCurrentTimeNanos(), var0);
   }

   protected static long initialNanoTime() {
      return START_TIME;
   }

   PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue() {
      if (this.scheduledTaskQueue == null) {
         this.scheduledTaskQueue = new DefaultPriorityQueue<>(SCHEDULED_FUTURE_TASK_COMPARATOR, 11);
      }

      return this.scheduledTaskQueue;
   }

   private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> var0) {
      return var0 == null || var0.isEmpty();
   }

   protected void cancelScheduledTasks() {
      assert this.inEventLoop();

      PriorityQueue var1 = this.scheduledTaskQueue;
      if (!isNullOrEmpty(var1)) {
         ScheduledFutureTask[] var2 = var1.toArray(new ScheduledFutureTask[0]);

         for (ScheduledFutureTask var6 : var2) {
            var6.cancelWithoutRemove(false);
         }

         var1.clearIgnoringIndexes();
      }
   }

   protected final Runnable pollScheduledTask() {
      return this.pollScheduledTask(this.getCurrentTimeNanos());
   }

   protected final Runnable pollScheduledTask(long var1) {
      assert this.inEventLoop();

      ScheduledFutureTask var3 = this.peekScheduledTask();
      if (var3 != null && var3.deadlineNanos() - var1 <= 0L) {
         this.scheduledTaskQueue.remove();
         var3.setConsumed();
         return var3;
      } else {
         return null;
      }
   }

   protected final long nextScheduledTaskNano() {
      ScheduledFutureTask var1 = this.peekScheduledTask();
      return var1 != null ? var1.delayNanos() : -1L;
   }

   protected final long nextScheduledTaskDeadlineNanos() {
      ScheduledFutureTask var1 = this.peekScheduledTask();
      return var1 != null ? var1.deadlineNanos() : -1L;
   }

   final ScheduledFutureTask<?> peekScheduledTask() {
      PriorityQueue var1 = this.scheduledTaskQueue;
      return var1 != null ? (ScheduledFutureTask)var1.peek() : null;
   }

   protected final boolean hasScheduledTasks() {
      ScheduledFutureTask var1 = this.peekScheduledTask();
      return var1 != null && var1.deadlineNanos() <= this.getCurrentTimeNanos();
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var4, "unit");
      if (var2 < 0L) {
         var2 = 0L;
      }

      this.validateScheduled0(var2, var4);
      return this.schedule(new ScheduledFutureTask(this, var1, deadlineNanos(this.getCurrentTimeNanos(), var4.toNanos(var2))));
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      ObjectUtil.checkNotNull(var1, "callable");
      ObjectUtil.checkNotNull(var4, "unit");
      if (var2 < 0L) {
         var2 = 0L;
      }

      this.validateScheduled0(var2, var4);
      return this.schedule(new ScheduledFutureTask<>(this, var1, deadlineNanos(this.getCurrentTimeNanos(), var4.toNanos(var2))));
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var6, "unit");
      if (var2 < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", var2));
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", var4));
      } else {
         this.validateScheduled0(var2, var6);
         this.validateScheduled0(var4, var6);
         return this.schedule(new ScheduledFutureTask(this, var1, deadlineNanos(this.getCurrentTimeNanos(), var6.toNanos(var2)), var6.toNanos(var4)));
      }
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      ObjectUtil.checkNotNull(var1, "command");
      ObjectUtil.checkNotNull(var6, "unit");
      if (var2 < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", var2));
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", var4));
      } else {
         this.validateScheduled0(var2, var6);
         this.validateScheduled0(var4, var6);
         return this.schedule(new ScheduledFutureTask(this, var1, deadlineNanos(this.getCurrentTimeNanos(), var6.toNanos(var2)), -var6.toNanos(var4)));
      }
   }

   private void validateScheduled0(long var1, TimeUnit var3) {
      this.validateScheduled(var1, var3);
   }

   @Deprecated
   protected void validateScheduled(long var1, TimeUnit var3) {
   }

   final void scheduleFromEventLoop(ScheduledFutureTask<?> var1) {
      this.scheduledTaskQueue().add(var1.setId(++this.nextTaskId));
   }

   private <V> ScheduledFuture<V> schedule(ScheduledFutureTask<V> var1) {
      if (this.inEventLoop()) {
         this.scheduleFromEventLoop(var1);
      } else {
         long var2 = var1.deadlineNanos();
         if (this.beforeScheduledTaskSubmitted(var2)) {
            this.execute(var1);
         } else {
            this.lazyExecute(var1);
            if (this.afterScheduledTaskSubmitted(var2)) {
               this.execute(WAKEUP_TASK);
            }
         }
      }

      return var1;
   }

   final void removeScheduled(ScheduledFutureTask<?> var1) {
      assert var1.isCancelled();

      if (this.inEventLoop()) {
         this.scheduledTaskQueue().removeTyped(var1);
      } else {
         this.lazyExecute(var1);
      }
   }

   protected boolean beforeScheduledTaskSubmitted(long var1) {
      return true;
   }

   protected boolean afterScheduledTaskSubmitted(long var1) {
      return true;
   }
}
