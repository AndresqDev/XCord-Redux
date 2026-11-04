package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

public final class PromiseCombiner {
   private int expectedCount;
   private int doneCount;
   private Promise<Void> aggregatePromise;
   private Throwable cause;
   private final GenericFutureListener<Future<?>> listener = new GenericFutureListener<Future<?>>() {
      @Override
      public void operationComplete(final Future<?> var1) {
         if (PromiseCombiner.this.executor.inEventLoop()) {
            this.operationComplete0(var1);
         } else {
            PromiseCombiner.this.executor.execute(new Runnable() {
               @Override
               public void run() {
                  operationComplete0(var1);
               }
            });
         }
      }

      private void operationComplete0(Future<?> var1) {
         assert PromiseCombiner.this.executor.inEventLoop();

         ++PromiseCombiner.this.doneCount;
         if (!var1.isSuccess() && PromiseCombiner.this.cause == null) {
            PromiseCombiner.this.cause = var1.cause();
         }

         if (PromiseCombiner.this.doneCount == PromiseCombiner.this.expectedCount && PromiseCombiner.this.aggregatePromise != null) {
            PromiseCombiner.this.tryPromise();
         }
      }
   };
   private final EventExecutor executor;

   @Deprecated
   public PromiseCombiner() {
      this(ImmediateEventExecutor.INSTANCE);
   }

   public PromiseCombiner(EventExecutor var1) {
      this.executor = ObjectUtil.checkNotNull(var1, "executor");
   }

   @Deprecated
   public void add(Promise var1) {
      this.add((Future)var1);
   }

   public void add(Future var1) {
      this.checkAddAllowed();
      this.checkInEventLoop();
      this.expectedCount++;
      var1.addListener(this.listener);
   }

   @Deprecated
   public void addAll(Promise... var1) {
      this.addAll(var1);
   }

   public void addAll(Future... var1) {
      for (Future var5 : var1) {
         this.add(var5);
      }
   }

   public void finish(Promise<Void> var1) {
      ObjectUtil.checkNotNull(var1, "aggregatePromise");
      this.checkInEventLoop();
      if (this.aggregatePromise != null) {
         throw new IllegalStateException("Already finished");
      } else {
         this.aggregatePromise = var1;
         if (this.doneCount == this.expectedCount) {
            this.tryPromise();
         }
      }
   }

   private void checkInEventLoop() {
      if (!this.executor.inEventLoop()) {
         throw new IllegalStateException("Must be called from EventExecutor thread");
      }
   }

   private boolean tryPromise() {
      return this.cause == null ? this.aggregatePromise.trySuccess(null) : this.aggregatePromise.tryFailure(this.cause);
   }

   private void checkAddAllowed() {
      if (this.aggregatePromise != null) {
         throw new IllegalStateException("Adding promises is not allowed after finished adding");
      }
   }
}
