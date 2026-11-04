package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@ElementTypesAreNonnullByDefault
@Beta
public final class ExecutionSequencer {
   private final AtomicReference<ListenableFuture<Void>> ref = new AtomicReference<>(Futures.immediateVoidFuture());
   private ExecutionSequencer.ThreadConfinedTaskQueue latestTaskQueue = new ExecutionSequencer.ThreadConfinedTaskQueue();

   private ExecutionSequencer() {
   }

   public static ExecutionSequencer create() {
      return new ExecutionSequencer();
   }

   public <T> ListenableFuture<T> submit(final Callable<T> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.submitAsync(new AsyncCallable<T>(this) {
         @Override
         public ListenableFuture<T> call() throws Exception {
            return Futures.immediateFuture((T)var1.call());
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      }, var2);
   }

   public <T> ListenableFuture<T> submitAsync(final AsyncCallable<T> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      final ExecutionSequencer.TaskNonReentrantExecutor var3 = new ExecutionSequencer.TaskNonReentrantExecutor(var2, this);
      AsyncCallable var4 = new AsyncCallable<T>(this) {
         @Override
         public ListenableFuture<T> call() throws Exception {
            return !var3.trySetStarted() ? Futures.immediateCancelledFuture() : var1.call();
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      };
      final SettableFuture var5 = SettableFuture.create();
      final ListenableFuture var6 = this.ref.getAndSet(var5);
      final TrustedListenableFutureTask var7 = TrustedListenableFutureTask.create(var4);
      var6.addListener(var7, var3);
      final ListenableFuture var8 = Futures.nonCancellationPropagating(var7);
      Runnable var9 = new Runnable(this) {
         @Override
         public void run() {
            if (var7.isDone()) {
               var5.setFuture(var6);
            } else if (var8.isCancelled() && var3.trySetCancelled()) {
               var7.cancel(false);
            }
         }
      };
      var8.addListener(var9, MoreExecutors.directExecutor());
      var7.addListener(var9, MoreExecutors.directExecutor());
      return var8;
   }

   static enum RunningState {
      NOT_RUN,
      CANCELLED,
      STARTED;
   }

   private static final class TaskNonReentrantExecutor extends AtomicReference<ExecutionSequencer.RunningState> implements Runnable, Executor {
      ExecutionSequencer sequencer;
      Executor delegate;
      Runnable task;
      Thread submitting;

      private TaskNonReentrantExecutor(Executor var1, ExecutionSequencer var2) {
         super(ExecutionSequencer.RunningState.NOT_RUN);
         this.delegate = var1;
         this.sequencer = var2;
      }

      @Override
      public void execute(Runnable var1) {
         if (this.get() == ExecutionSequencer.RunningState.CANCELLED) {
            this.delegate = null;
            this.sequencer = null;
         } else {
            this.submitting = Thread.currentThread();

            try {
               ExecutionSequencer.ThreadConfinedTaskQueue var2 = Objects.requireNonNull(this.sequencer).latestTaskQueue;
               if (var2.thread == this.submitting) {
                  this.sequencer = null;
                  Preconditions.checkState(var2.nextTask == null);
                  var2.nextTask = var1;
                  var2.nextExecutor = Objects.requireNonNull(this.delegate);
                  this.delegate = null;
               } else {
                  Executor var3 = Objects.requireNonNull(this.delegate);
                  this.delegate = null;
                  this.task = var1;
                  var3.execute(this);
               }
            } finally {
               this.submitting = null;
            }
         }
      }

      @Override
      public void run() {
         Thread var1 = Thread.currentThread();
         if (var1 != this.submitting) {
            Runnable var9 = Objects.requireNonNull(this.task);
            this.task = null;
            var9.run();
         } else {
            ExecutionSequencer.ThreadConfinedTaskQueue var2 = new ExecutionSequencer.ThreadConfinedTaskQueue();
            var2.thread = var1;
            Objects.requireNonNull(this.sequencer).latestTaskQueue = var2;
            this.sequencer = null;

            try {
               Runnable var3 = Objects.requireNonNull(this.task);
               this.task = null;
               var3.run();

               while (true) {
                  Runnable var4 = var2.nextTask;
                  boolean var10000 = var2.nextTask != null;
                  Executor var5 = var2.nextExecutor;
                  if (!(var10000 & var2.nextExecutor != null)) {
                     return;
                  }

                  var2.nextTask = null;
                  var2.nextExecutor = null;
                  var5.execute(var4);
               }
            } finally {
               var2.thread = null;
            }
         }
      }

      private boolean trySetStarted() {
         return this.compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.STARTED);
      }

      private boolean trySetCancelled() {
         return this.compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.CANCELLED);
      }
   }

   private static final class ThreadConfinedTaskQueue {
      Thread thread;
      Runnable nextTask;
      Executor nextExecutor;

      private ThreadConfinedTaskQueue() {
      }
   }
}
