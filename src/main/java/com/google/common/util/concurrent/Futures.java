package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Futures extends GwtFuturesCatchingSpecialization {
   private Futures() {
   }

   public static <V> ListenableFuture<V> immediateFuture(@ParametricNullness V var0) {
      return (ListenableFuture<V>)(var0 == null ? ImmediateFuture.NULL : new ImmediateFuture<>((V)var0));
   }

   public static ListenableFuture<Void> immediateVoidFuture() {
      return (ListenableFuture<Void>)ImmediateFuture.NULL;
   }

   public static <V> ListenableFuture<V> immediateFailedFuture(Throwable var0) {
      Preconditions.checkNotNull(var0);
      return new ImmediateFuture.ImmediateFailedFuture<>(var0);
   }

   public static <V> ListenableFuture<V> immediateCancelledFuture() {
      return new ImmediateFuture.ImmediateCancelledFuture<>();
   }

   @Beta
   public static <O> ListenableFuture<O> submit(Callable<O> var0, Executor var1) {
      TrustedListenableFutureTask var2 = TrustedListenableFutureTask.create(var0);
      var1.execute(var2);
      return var2;
   }

   @Beta
   public static ListenableFuture<Void> submit(Runnable var0, Executor var1) {
      TrustedListenableFutureTask var2 = TrustedListenableFutureTask.create(var0, null);
      var1.execute(var2);
      return var2;
   }

   @Beta
   public static <O> ListenableFuture<O> submitAsync(AsyncCallable<O> var0, Executor var1) {
      TrustedListenableFutureTask var2 = TrustedListenableFutureTask.create(var0);
      var1.execute(var2);
      return var2;
   }

   @Beta
   @GwtIncompatible
   public static <O> ListenableFuture<O> scheduleAsync(AsyncCallable<O> var0, Duration var1, ScheduledExecutorService var2) {
      return scheduleAsync(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS, var2);
   }

   @Beta
   @GwtIncompatible
   public static <O> ListenableFuture<O> scheduleAsync(AsyncCallable<O> var0, long var1, TimeUnit var3, ScheduledExecutorService var4) {
      TrustedListenableFutureTask var5 = TrustedListenableFutureTask.create(var0);
      final ScheduledFuture var6 = var4.schedule(var5, var1, var3);
      var5.addListener(new Runnable() {
         @Override
         public void run() {
            var6.cancel(false);
         }
      }, MoreExecutors.directExecutor());
      return var5;
   }

   @Beta
   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catching(
      ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2, Executor var3
   ) {
      return AbstractCatchingFuture.create(var0, var1, var2, var3);
   }

   @Beta
   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(
      ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2, Executor var3
   ) {
      return AbstractCatchingFuture.create(var0, var1, var2, var3);
   }

   @Beta
   @GwtIncompatible
   public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> var0, Duration var1, ScheduledExecutorService var2) {
      return withTimeout(var0, Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS, var2);
   }

   @Beta
   @GwtIncompatible
   public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> var0, long var1, TimeUnit var3, ScheduledExecutorService var4) {
      return var0.isDone() ? var0 : TimeoutFuture.create(var0, var1, var3, var4);
   }

   @Beta
   public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1, Executor var2) {
      return AbstractTransformFuture.create(var0, var1, var2);
   }

   @Beta
   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> var0, Function<? super I, ? extends O> var1, Executor var2) {
      return AbstractTransformFuture.create(var0, var1, var2);
   }

   @Beta
   @GwtIncompatible
   public static <I, O> Future<O> lazyTransform(final Future<I> var0, final Function<? super I, ? extends O> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Future<O>() {
         @Override
         public boolean cancel(boolean var1x) {
            return var0.cancel(var1x);
         }

         @Override
         public boolean isCancelled() {
            return var0.isCancelled();
         }

         @Override
         public boolean isDone() {
            return var0.isDone();
         }

         @Override
         public O get() throws InterruptedException, ExecutionException {
            return (O)this.applyTransformation((I)var0.get());
         }

         @Override
         public O get(long var1x, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
            return (O)this.applyTransformation((I)var0.get(var1x, var3));
         }

         private O applyTransformation(I var1x) throws ExecutionException {
            try {
               return (O)var1.apply(var1x);
            } catch (Throwable var3) {
               throw new ExecutionException(var3);
            }
         }
      };
   }

   @SafeVarargs
   @Beta
   public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... var0) {
      return new CollectionFuture.ListFuture<>(ImmutableList.copyOf(var0), true);
   }

   @Beta
   public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new CollectionFuture.ListFuture<>(ImmutableList.copyOf(var0), true);
   }

   @SafeVarargs
   @Beta
   public static <V> Futures.FutureCombiner<V> whenAllComplete(ListenableFuture<? extends V>... var0) {
      return new Futures.FutureCombiner<>(false, ImmutableList.copyOf(var0));
   }

   @Beta
   public static <V> Futures.FutureCombiner<V> whenAllComplete(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new Futures.FutureCombiner<>(false, ImmutableList.copyOf(var0));
   }

   @SafeVarargs
   @Beta
   public static <V> Futures.FutureCombiner<V> whenAllSucceed(ListenableFuture<? extends V>... var0) {
      return new Futures.FutureCombiner<>(true, ImmutableList.copyOf(var0));
   }

   @Beta
   public static <V> Futures.FutureCombiner<V> whenAllSucceed(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new Futures.FutureCombiner<>(true, ImmutableList.copyOf(var0));
   }

   @Beta
   public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> var0) {
      if (var0.isDone()) {
         return var0;
      } else {
         Futures.NonCancellationPropagatingFuture var1 = new Futures.NonCancellationPropagatingFuture(var0);
         var0.addListener(var1, MoreExecutors.directExecutor());
         return var1;
      }
   }

   @SafeVarargs
   @Beta
   public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... var0) {
      return new CollectionFuture.ListFuture<>(ImmutableList.copyOf(var0), false);
   }

   @Beta
   public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new CollectionFuture.ListFuture<>(ImmutableList.copyOf(var0), false);
   }

   @Beta
   public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> var0) {
      ListenableFuture[] var1 = gwtCompatibleToArray(var0);
      final Futures.InCompletionOrderState var2 = new Futures.InCompletionOrderState(var1);
      ImmutableList.Builder var3 = ImmutableList.builderWithExpectedSize(var1.length);

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3.add(new Futures.InCompletionOrderFuture(var2));
      }

      final ImmutableList var7 = var3.build();

      for (final int var5 = 0; var5 < var1.length; var5++) {
         var1[var5].addListener(new Runnable() {
            @Override
            public void run() {
               var2.recordInputCompletion(var7, var5);
            }
         }, MoreExecutors.directExecutor());
      }

      return var7;
   }

   private static <T> ListenableFuture<? extends T>[] gwtCompatibleToArray(Iterable<? extends ListenableFuture<? extends T>> var0) {
      Object var1;
      if (var0 instanceof Collection) {
         var1 = (Collection)var0;
      } else {
         var1 = ImmutableList.copyOf(var0);
      }

      return var1.toArray(new ListenableFuture[0]);
   }

   public static <V> void addCallback(ListenableFuture<V> var0, FutureCallback<? super V> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      var0.addListener(new Futures.CallbackListener<>(var0, var1), var2);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   public static <V> V getDone(Future<V> var0) throws ExecutionException {
      Preconditions.checkState(var0.isDone(), "Future was expected to be done: %s", var0);
      return Uninterruptibles.getUninterruptibly(var0);
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1) throws X {
      return FuturesGetChecked.getChecked(var0, var1);
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1, Duration var2) throws X {
      return getChecked(var0, var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1, long var2, TimeUnit var4) throws X {
      return FuturesGetChecked.getChecked(var0, var1, var2, var4);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   public static <V> V getUnchecked(Future<V> var0) {
      Preconditions.checkNotNull(var0);

      try {
         return Uninterruptibles.getUninterruptibly(var0);
      } catch (ExecutionException var2) {
         wrapAndThrowUnchecked(var2.getCause());
         throw new AssertionError();
      }
   }

   private static void wrapAndThrowUnchecked(Throwable var0) {
      if (var0 instanceof Error) {
         throw new ExecutionError((Error)var0);
      } else {
         throw new UncheckedExecutionException(var0);
      }
   }

   private static final class CallbackListener<V> implements Runnable {
      final Future<V> future;
      final FutureCallback<? super V> callback;

      CallbackListener(Future<V> var1, FutureCallback<? super V> var2) {
         this.future = var1;
         this.callback = var2;
      }

      @Override
      public void run() {
         if (this.future instanceof InternalFutureFailureAccess) {
            Throwable var1 = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)this.future);
            if (var1 != null) {
               this.callback.onFailure(var1);
               return;
            }
         }

         Object var5;
         try {
            var5 = Futures.getDone(this.future);
         } catch (ExecutionException var3) {
            this.callback.onFailure(var3.getCause());
            return;
         } catch (Error | RuntimeException var4) {
            this.callback.onFailure(var4);
            return;
         }

         this.callback.onSuccess((V)var5);
      }

      @Override
      public String toString() {
         return MoreObjects.toStringHelper(this).addValue(this.callback).toString();
      }
   }

   @Beta
   @CanIgnoreReturnValue
   @GwtCompatible
   public static final class FutureCombiner<V> {
      private final boolean allMustSucceed;
      private final ImmutableList<ListenableFuture<? extends V>> futures;

      private FutureCombiner(boolean var1, ImmutableList<ListenableFuture<? extends V>> var2) {
         this.allMustSucceed = var1;
         this.futures = var2;
      }

      public <C> ListenableFuture<C> callAsync(AsyncCallable<C> var1, Executor var2) {
         return new CombinedFuture(this.futures, this.allMustSucceed, var2, var1);
      }

      @CanIgnoreReturnValue
      public <C> ListenableFuture<C> call(Callable<C> var1, Executor var2) {
         return new CombinedFuture(this.futures, this.allMustSucceed, var2, var1);
      }

      public ListenableFuture<?> run(final Runnable var1, Executor var2) {
         return this.call(new Callable<Void>(this) {
            public Void call() throws Exception {
               var1.run();
               return null;
            }
         }, var2);
      }
   }

   private static final class InCompletionOrderFuture<T> extends AbstractFuture<T> {
      private Futures.InCompletionOrderState<T> state;

      private InCompletionOrderFuture(Futures.InCompletionOrderState<T> var1) {
         this.state = var1;
      }

      @Override
      public boolean cancel(boolean var1) {
         Futures.InCompletionOrderState var2 = this.state;
         if (super.cancel(var1)) {
            Objects.requireNonNull(var2).recordOutputCancellation(var1);
            return true;
         } else {
            return false;
         }
      }

      @Override
      protected void afterDone() {
         this.state = null;
      }

      @Override
      protected String pendingToString() {
         Futures.InCompletionOrderState var1 = this.state;
         if (var1 != null) {
            int var2 = var1.inputFutures.length;
            int var3 = var1.incompleteOutputCount.get();
            return new StringBuilder(49).append("inputCount=[").append(var2).append("], remaining=[").append(var3).append("]").toString();
         } else {
            return null;
         }
      }
   }

   private static final class InCompletionOrderState<T> {
      private boolean wasCancelled = false;
      private boolean shouldInterrupt = true;
      private final AtomicInteger incompleteOutputCount;
      private final ListenableFuture<? extends T>[] inputFutures;
      private volatile int delegateIndex = 0;

      private InCompletionOrderState(ListenableFuture<? extends T>[] var1) {
         this.inputFutures = var1;
         this.incompleteOutputCount = new AtomicInteger(var1.length);
      }

      private void recordOutputCancellation(boolean var1) {
         this.wasCancelled = true;
         if (!var1) {
            this.shouldInterrupt = false;
         }

         this.recordCompletion();
      }

      private void recordInputCompletion(ImmutableList<AbstractFuture<T>> var1, int var2) {
         ListenableFuture var3 = Objects.requireNonNull(this.inputFutures[var2]);
         this.inputFutures[var2] = null;

         for (int var4 = this.delegateIndex; var4 < var1.size(); var4++) {
            if (((AbstractFuture)var1.get(var4)).setFuture(var3)) {
               this.recordCompletion();
               this.delegateIndex = var4 + 1;
               return;
            }
         }

         this.delegateIndex = var1.size();
      }

      private void recordCompletion() {
         if (this.incompleteOutputCount.decrementAndGet() == 0 && this.wasCancelled) {
            for (ListenableFuture var4 : this.inputFutures) {
               if (var4 != null) {
                  var4.cancel(this.shouldInterrupt);
               }
            }
         }
      }
   }

   private static final class NonCancellationPropagatingFuture<V> extends AbstractFuture.TrustedFuture<V> implements Runnable {
      private ListenableFuture<V> delegate;

      NonCancellationPropagatingFuture(ListenableFuture<V> var1) {
         this.delegate = var1;
      }

      @Override
      public void run() {
         ListenableFuture var1 = this.delegate;
         if (var1 != null) {
            this.setFuture(var1);
         }
      }

      @Override
      protected String pendingToString() {
         ListenableFuture var1 = this.delegate;
         if (var1 != null) {
            String var2 = String.valueOf(var1);
            return new StringBuilder(11 + String.valueOf(var2).length()).append("delegate=[").append(var2).append("]").toString();
         } else {
            return null;
         }
      }

      @Override
      protected void afterDone() {
         this.delegate = null;
      }
   }
}
