package io.netty.util.concurrent;

import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

final class ScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V>, PriorityQueueNode {
   private long id;
   private long deadlineNanos;
   private final long periodNanos;
   private int queueIndex = -1;

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Runnable var2, long var3) {
      super(var1, var2);
      this.deadlineNanos = var3;
      this.periodNanos = 0L;
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Runnable var2, long var3, long var5) {
      super(var1, var2);
      this.deadlineNanos = var3;
      this.periodNanos = validatePeriod(var5);
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Callable<V> var2, long var3, long var5) {
      super(var1, var2);
      this.deadlineNanos = var3;
      this.periodNanos = validatePeriod(var5);
   }

   ScheduledFutureTask(AbstractScheduledEventExecutor var1, Callable<V> var2, long var3) {
      super(var1, var2);
      this.deadlineNanos = var3;
      this.periodNanos = 0L;
   }

   private static long validatePeriod(long var0) {
      if (var0 == 0L) {
         throw new IllegalArgumentException("period: 0 (expected: != 0)");
      } else {
         return var0;
      }
   }

   ScheduledFutureTask<V> setId(long var1) {
      if (this.id == 0L) {
         this.id = var1;
      }

      return this;
   }

   @Override
   protected EventExecutor executor() {
      return super.executor();
   }

   public long deadlineNanos() {
      return this.deadlineNanos;
   }

   void setConsumed() {
      if (this.periodNanos == 0L) {
         assert this.scheduledExecutor().getCurrentTimeNanos() >= this.deadlineNanos;

         this.deadlineNanos = 0L;
      }
   }

   public long delayNanos() {
      return this.delayNanos(this.scheduledExecutor().getCurrentTimeNanos());
   }

   static long deadlineToDelayNanos(long var0, long var2) {
      return var2 == 0L ? 0L : Math.max(0L, var2 - var0);
   }

   public long delayNanos(long var1) {
      return deadlineToDelayNanos(var1, this.deadlineNanos);
   }

   @Override
   public long getDelay(TimeUnit var1) {
      return var1.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
   }

   public int compareTo(Delayed var1) {
      if (this == var1) {
         return 0;
      } else {
         ScheduledFutureTask var2 = (ScheduledFutureTask)var1;
         long var3 = this.deadlineNanos() - var2.deadlineNanos();
         if (var3 < 0L) {
            return -1;
         } else if (var3 > 0L) {
            return 1;
         } else if (this.id < var2.id) {
            return -1;
         } else {
            assert this.id != var2.id;

            return 1;
         }
      }
   }

   @Override
   public void run() {
      assert this.executor().inEventLoop();

      try {
         if (this.delayNanos() > 0L) {
            if (this.isCancelled()) {
               this.scheduledExecutor().scheduledTaskQueue().removeTyped(this);
            } else {
               this.scheduledExecutor().scheduleFromEventLoop(this);
            }

            return;
         }

         if (this.periodNanos == 0L) {
            if (this.setUncancellableInternal()) {
               Object var1 = this.runTask();
               this.setSuccessInternal((V)var1);
            }
         } else if (!this.isCancelled()) {
            this.runTask();
            if (!this.executor().isShutdown()) {
               if (this.periodNanos > 0L) {
                  this.deadlineNanos = this.deadlineNanos + this.periodNanos;
               } else {
                  this.deadlineNanos = this.scheduledExecutor().getCurrentTimeNanos() - this.periodNanos;
               }

               if (!this.isCancelled()) {
                  this.scheduledExecutor().scheduledTaskQueue().add(this);
               }
            }
         }
      } catch (Throwable var2) {
         this.setFailureInternal(var2);
      }
   }

   private AbstractScheduledEventExecutor scheduledExecutor() {
      return (AbstractScheduledEventExecutor)this.executor();
   }

   @Override
   public boolean cancel(boolean var1) {
      boolean var2 = super.cancel(var1);
      if (var2) {
         this.scheduledExecutor().removeScheduled(this);
      }

      return var2;
   }

   boolean cancelWithoutRemove(boolean var1) {
      return super.cancel(var1);
   }

   @Override
   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = super.toStringBuilder();
      var1.setCharAt(var1.length() - 1, ',');
      return var1.append(" deadline: ").append(this.deadlineNanos).append(", period: ").append(this.periodNanos).append(')');
   }

   @Override
   public int priorityQueueIndex(DefaultPriorityQueue<?> var1) {
      return this.queueIndex;
   }

   @Override
   public void priorityQueueIndex(DefaultPriorityQueue<?> var1, int var2) {
      this.queueIndex = var2;
   }
}
