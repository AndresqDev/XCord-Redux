package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.j2objc.annotations.RetainedWith;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class SequentialExecutor implements Executor {
   private static final Logger log = Logger.getLogger(SequentialExecutor.class.getName());
   private final Executor executor;
   @GuardedBy("queue")
   private final Deque<Runnable> queue = new ArrayDeque<>();
   @GuardedBy("queue")
   private SequentialExecutor.WorkerRunningState workerRunningState = SequentialExecutor.WorkerRunningState.IDLE;
   @GuardedBy("queue")
   private long workerRunCount = 0L;
   @RetainedWith
   private final SequentialExecutor.QueueWorker worker = new SequentialExecutor.QueueWorker();

   SequentialExecutor(Executor var1) {
      this.executor = Preconditions.checkNotNull(var1);
   }

   @Override
   public void execute(final Runnable var1) {
      Preconditions.checkNotNull(var1);
      Runnable var2;
      long var3;
      synchronized (this.queue) {
         if (this.workerRunningState == SequentialExecutor.WorkerRunningState.RUNNING
            || this.workerRunningState == SequentialExecutor.WorkerRunningState.QUEUED) {
            this.queue.add(var1);
            return;
         }

         var3 = this.workerRunCount;
         var2 = new Runnable(this) {
            @Override
            public void run() {
               var1.run();
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         };
         this.queue.add(var2);
         this.workerRunningState = SequentialExecutor.WorkerRunningState.QUEUING;
      }

      try {
         this.executor.execute(this.worker);
      } catch (Error | RuntimeException var12) {
         RuntimeException var14 = var12;
         synchronized (this.queue) {
            boolean var7 = (
                  this.workerRunningState == SequentialExecutor.WorkerRunningState.IDLE
                     || this.workerRunningState == SequentialExecutor.WorkerRunningState.QUEUING
               )
               && this.queue.removeLastOccurrence(var2);
            if (var14 instanceof RejectedExecutionException && !var7) {
               return;
            }

            throw var14;
         }
      }

      boolean var15 = this.workerRunningState != SequentialExecutor.WorkerRunningState.QUEUING;
      if (!var15) {
         synchronized (this.queue) {
            if (this.workerRunCount == var3 && this.workerRunningState == SequentialExecutor.WorkerRunningState.QUEUING) {
               this.workerRunningState = SequentialExecutor.WorkerRunningState.QUEUED;
            }
         }
      }
   }

   @Override
   public String toString() {
      int var1 = System.identityHashCode(this);
      String var2 = String.valueOf(this.executor);
      return new StringBuilder(32 + String.valueOf(var2).length()).append("SequentialExecutor@").append(var1).append("{").append(var2).append("}").toString();
   }

   private final class QueueWorker implements Runnable {
      Runnable task;

      private QueueWorker() {
      }

      @Override
      public void run() {
         try {
            this.workOnQueue();
         } catch (Error var5) {
            synchronized (SequentialExecutor.this.queue) {
               SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.IDLE;
            }

            throw var5;
         }
      }

      private void workOnQueue() {
         boolean var1 = false;
         boolean var2 = false;

         while (true) {
            try {
               synchronized (SequentialExecutor.this.queue) {
                  if (!var2) {
                     if (SequentialExecutor.this.workerRunningState == SequentialExecutor.WorkerRunningState.RUNNING) {
                        return;
                     }

                     SequentialExecutor.this.workerRunCount++;
                     SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.RUNNING;
                     var2 = true;
                  }

                  this.task = SequentialExecutor.this.queue.poll();
                  if (this.task == null) {
                     SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.IDLE;
                     return;
                  }
               }

               var1 |= Thread.interrupted();

               try {
                  this.task.run();
               } catch (RuntimeException var15) {
                  Logger var10000 = SequentialExecutor.log;
                  Level var10001 = Level.SEVERE;
                  String var4 = String.valueOf(this.task);
                  var10000.log(
                     var10001,
                     new StringBuilder(35 + String.valueOf(var4).length()).append("Exception while executing runnable ").append(var4).toString(),
                     (Throwable)var15
                  );
               } finally {
                  this.task = null;
               }
            } finally {
               if (var1) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }

      @Override
      public String toString() {
         Runnable var1 = this.task;
         if (var1 != null) {
            String var3 = String.valueOf(var1);
            return new StringBuilder(34 + String.valueOf(var3).length()).append("SequentialExecutorWorker{running=").append(var3).append("}").toString();
         } else {
            String var2 = String.valueOf(SequentialExecutor.this.workerRunningState);
            return new StringBuilder(32 + String.valueOf(var2).length()).append("SequentialExecutorWorker{state=").append(var2).append("}").toString();
         }
      }
   }

   static enum WorkerRunningState {
      IDLE,
      QUEUING,
      QUEUED,
      RUNNING;
   }
}
