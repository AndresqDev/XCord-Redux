package io.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {
   private static final Runnable COMPLETED = new PromiseTask.SentinelRunnable("COMPLETED");
   private static final Runnable CANCELLED = new PromiseTask.SentinelRunnable("CANCELLED");
   private static final Runnable FAILED = new PromiseTask.SentinelRunnable("FAILED");
   private Object task;

   PromiseTask(EventExecutor var1, Runnable var2, V var3) {
      super(var1);
      this.task = var3 == null ? var2 : new PromiseTask.RunnableAdapter<>(var2, var3);
   }

   PromiseTask(EventExecutor var1, Runnable var2) {
      super(var1);
      this.task = var2;
   }

   PromiseTask(EventExecutor var1, Callable<V> var2) {
      super(var1);
      this.task = var2;
   }

   @Override
   public final int hashCode() {
      return System.identityHashCode(this);
   }

   @Override
   public final boolean equals(Object var1) {
      return this == var1;
   }

   V runTask() throws Throwable {
      Object var1 = this.task;
      if (var1 instanceof Callable) {
         return (V)((Callable)var1).call();
      } else {
         ((Runnable)var1).run();
         return null;
      }
   }

   @Override
   public void run() {
      try {
         if (this.setUncancellableInternal()) {
            Object var1 = this.runTask();
            this.setSuccessInternal((V)var1);
         }
      } catch (Throwable var2) {
         this.setFailureInternal(var2);
      }
   }

   private boolean clearTaskAfterCompletion(boolean var1, Runnable var2) {
      if (var1) {
         this.task = var2;
      }

      return var1;
   }

   @Override
   public final Promise<V> setFailure(Throwable var1) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setFailureInternal(Throwable var1) {
      super.setFailure(var1);
      this.clearTaskAfterCompletion(true, FAILED);
      return this;
   }

   @Override
   public final boolean tryFailure(Throwable var1) {
      return false;
   }

   protected final boolean tryFailureInternal(Throwable var1) {
      return this.clearTaskAfterCompletion(super.tryFailure(var1), FAILED);
   }

   @Override
   public final Promise<V> setSuccess(V var1) {
      throw new IllegalStateException();
   }

   protected final Promise<V> setSuccessInternal(V var1) {
      super.setSuccess((V)var1);
      this.clearTaskAfterCompletion(true, COMPLETED);
      return this;
   }

   @Override
   public final boolean trySuccess(V var1) {
      return false;
   }

   protected final boolean trySuccessInternal(V var1) {
      return this.clearTaskAfterCompletion(super.trySuccess((V)var1), COMPLETED);
   }

   @Override
   public final boolean setUncancellable() {
      throw new IllegalStateException();
   }

   protected final boolean setUncancellableInternal() {
      return super.setUncancellable();
   }

   @Override
   public boolean cancel(boolean var1) {
      return this.clearTaskAfterCompletion(super.cancel(var1), CANCELLED);
   }

   @Override
   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = super.toStringBuilder();
      var1.setCharAt(var1.length() - 1, ',');
      return var1.append(" task: ").append(this.task).append(')');
   }

   private static final class RunnableAdapter<T> implements Callable<T> {
      final Runnable task;
      final T result;

      RunnableAdapter(Runnable var1, T var2) {
         this.task = var1;
         this.result = (T)var2;
      }

      @Override
      public T call() {
         this.task.run();
         return this.result;
      }

      @Override
      public String toString() {
         return "Callable(task: " + this.task + ", result: " + this.result + ')';
      }
   }

   private static class SentinelRunnable implements Runnable {
      private final String name;

      SentinelRunnable(String var1) {
         this.name = var1;
      }

      @Override
      public void run() {
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
