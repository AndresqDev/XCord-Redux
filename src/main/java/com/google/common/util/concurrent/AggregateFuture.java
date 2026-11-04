package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AggregateFuture<InputT, OutputT> extends AggregateFutureState<OutputT> {
   private static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());
   private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
   private final boolean allMustSucceed;
   private final boolean collectsValues;

   AggregateFuture(ImmutableCollection<? extends ListenableFuture<? extends InputT>> var1, boolean var2, boolean var3) {
      super(var1.size());
      this.futures = Preconditions.checkNotNull(var1);
      this.allMustSucceed = var2;
      this.collectsValues = var3;
   }

   @Override
   protected final void afterDone() {
      super.afterDone();
      ImmutableCollection var1 = this.futures;
      this.releaseResources(AggregateFuture.ReleaseResourcesReason.OUTPUT_FUTURE_DONE);
      if (this.isCancelled() & var1 != null) {
         boolean var2 = this.wasInterrupted();

         for (Future var4 : var1) {
            var4.cancel(var2);
         }
      }
   }

   @Override
   protected final String pendingToString() {
      ImmutableCollection var1 = this.futures;
      if (var1 != null) {
         String var2 = String.valueOf(var1);
         return new StringBuilder(8 + String.valueOf(var2).length()).append("futures=").append(var2).toString();
      } else {
         return super.pendingToString();
      }
   }

   final void init() {
      Objects.requireNonNull(this.futures);
      if (this.futures.isEmpty()) {
         this.handleAllCompleted();
      } else {
         if (this.allMustSucceed) {
            int var1 = 0;

            for (final ListenableFuture var3 : this.futures) {
               final int var4 = var1++;
               var3.addListener(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        if (var3.isCancelled()) {
                           AggregateFuture.this.futures = null;
                           AggregateFuture.this.cancel(false);
                        } else {
                           AggregateFuture.this.collectValueFromNonCancelledFuture(var4, var3);
                        }
                     } finally {
                        AggregateFuture.this.decrementCountAndMaybeComplete(null);
                     }
                  }
               }, MoreExecutors.directExecutor());
            }
         } else {
            final ImmutableCollection var5 = this.collectsValues ? this.futures : null;
            Runnable var6 = new Runnable() {
               @Override
               public void run() {
                  AggregateFuture.this.decrementCountAndMaybeComplete(var5);
               }
            };

            for (ListenableFuture var8 : this.futures) {
               var8.addListener(var6, MoreExecutors.directExecutor());
            }
         }
      }
   }

   private void handleException(Throwable var1) {
      Preconditions.checkNotNull(var1);
      if (this.allMustSucceed) {
         boolean var2 = this.setException(var1);
         if (!var2) {
            boolean var3 = addCausalChain(this.getOrInitSeenExceptions(), var1);
            if (var3) {
               log(var1);
               return;
            }
         }
      }

      if (var1 instanceof Error) {
         log(var1);
      }
   }

   private static void log(Throwable var0) {
      String var1 = var0 instanceof Error ? "Input Future failed with Error" : "Got more than one input Future failure. Logging failures after the first";
      logger.log(Level.SEVERE, var1, var0);
   }

   @Override
   final void addInitialException(Set<Throwable> var1) {
      Preconditions.checkNotNull(var1);
      if (!this.isCancelled()) {
         boolean var2 = addCausalChain(var1, Objects.requireNonNull(this.tryInternalFastPathGetFailure()));
      }
   }

   private void collectValueFromNonCancelledFuture(int var1, Future<? extends InputT> var2) {
      try {
         this.collectOneValue(var1, Futures.getDone(var2));
      } catch (ExecutionException var4) {
         this.handleException(var4.getCause());
      } catch (Throwable var5) {
         this.handleException(var5);
      }
   }

   private void decrementCountAndMaybeComplete(ImmutableCollection<? extends Future<? extends InputT>> var1) {
      int var2 = this.decrementRemainingAndGet();
      Preconditions.checkState(var2 >= 0, "Less than 0 remaining futures");
      if (var2 == 0) {
         this.processCompleted(var1);
      }
   }

   private void processCompleted(ImmutableCollection<? extends Future<? extends InputT>> var1) {
      if (var1 != null) {
         int var2 = 0;

         for (Future var4 : var1) {
            if (!var4.isCancelled()) {
               this.collectValueFromNonCancelledFuture(var2, var4);
            }

            var2++;
         }
      }

      this.clearSeenExceptions();
      this.handleAllCompleted();
      this.releaseResources(AggregateFuture.ReleaseResourcesReason.ALL_INPUT_FUTURES_PROCESSED);
   }

   @ForOverride
   @OverridingMethodsMustInvokeSuper
   void releaseResources(AggregateFuture.ReleaseResourcesReason var1) {
      Preconditions.checkNotNull(var1);
      this.futures = null;
   }

   abstract void collectOneValue(int var1, @ParametricNullness InputT var2);

   abstract void handleAllCompleted();

   private static boolean addCausalChain(Set<Throwable> var0, Throwable var1) {
      for (Throwable var2 = var1; var2 != null; var2 = var2.getCause()) {
         boolean var3 = var0.add(var2);
         if (!var3) {
            return false;
         }
      }

      return true;
   }

   static enum ReleaseResourcesReason {
      OUTPUT_FUTURE_DONE,
      ALL_INPUT_FUTURES_PROCESSED;
   }
}
