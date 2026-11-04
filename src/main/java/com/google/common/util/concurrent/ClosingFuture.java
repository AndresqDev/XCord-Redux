package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Closeable;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@DoNotMock("Use ClosingFuture.from(Futures.immediate*Future)")
@ElementTypesAreNonnullByDefault
@Beta
public final class ClosingFuture<V> {
   private static final Logger logger = Logger.getLogger(ClosingFuture.class.getName());
   private final AtomicReference<ClosingFuture.State> state = new AtomicReference<>(ClosingFuture.State.OPEN);
   private final ClosingFuture.CloseableList closeables = new ClosingFuture.CloseableList();
   private final FluentFuture<V> future;

   public static <V> ClosingFuture<V> submit(ClosingFuture.ClosingCallable<V> var0, Executor var1) {
      return new ClosingFuture<>(var0, var1);
   }

   public static <V> ClosingFuture<V> submitAsync(ClosingFuture.AsyncClosingCallable<V> var0, Executor var1) {
      return new ClosingFuture<>(var0, var1);
   }

   public static <V> ClosingFuture<V> from(ListenableFuture<V> var0) {
      return new ClosingFuture<>(var0);
   }

   @Deprecated
   public static <C extends Object & AutoCloseable> ClosingFuture<C> eventuallyClosing(ListenableFuture<C> var0, final Executor var1) {
      Preconditions.checkNotNull(var1);
      final ClosingFuture var2 = new ClosingFuture(Futures.nonCancellationPropagating(var0));
      Futures.addCallback(var0, new FutureCallback<AutoCloseable>() {
         public void onSuccess(AutoCloseable var1x) {
            var2.closeables.closer.eventuallyClose(var1x, var1);
         }

         @Override
         public void onFailure(Throwable var1x) {
         }
      }, MoreExecutors.directExecutor());
      return var2;
   }

   public static ClosingFuture.Combiner whenAllComplete(Iterable<? extends ClosingFuture<?>> var0) {
      return new ClosingFuture.Combiner(false, var0);
   }

   public static ClosingFuture.Combiner whenAllComplete(ClosingFuture<?> var0, ClosingFuture<?>... var1) {
      return whenAllComplete(Lists.asList(var0, var1));
   }

   public static ClosingFuture.Combiner whenAllSucceed(Iterable<? extends ClosingFuture<?>> var0) {
      return new ClosingFuture.Combiner(true, var0);
   }

   public static <V1, V2> ClosingFuture.Combiner2<V1, V2> whenAllSucceed(ClosingFuture<V1> var0, ClosingFuture<V2> var1) {
      return new ClosingFuture.Combiner2<>(var0, var1);
   }

   public static <V1, V2, V3> ClosingFuture.Combiner3<V1, V2, V3> whenAllSucceed(ClosingFuture<V1> var0, ClosingFuture<V2> var1, ClosingFuture<V3> var2) {
      return new ClosingFuture.Combiner3<>(var0, var1, var2);
   }

   public static <V1, V2, V3, V4> ClosingFuture.Combiner4<V1, V2, V3, V4> whenAllSucceed(
      ClosingFuture<V1> var0, ClosingFuture<V2> var1, ClosingFuture<V3> var2, ClosingFuture<V4> var3
   ) {
      return new ClosingFuture.Combiner4<>(var0, var1, var2, var3);
   }

   public static <V1, V2, V3, V4, V5> ClosingFuture.Combiner5<V1, V2, V3, V4, V5> whenAllSucceed(
      ClosingFuture<V1> var0, ClosingFuture<V2> var1, ClosingFuture<V3> var2, ClosingFuture<V4> var3, ClosingFuture<V5> var4
   ) {
      return new ClosingFuture.Combiner5<>(var0, var1, var2, var3, var4);
   }

   public static ClosingFuture.Combiner whenAllSucceed(
      ClosingFuture<?> var0,
      ClosingFuture<?> var1,
      ClosingFuture<?> var2,
      ClosingFuture<?> var3,
      ClosingFuture<?> var4,
      ClosingFuture<?> var5,
      ClosingFuture<?>... var6
   ) {
      return whenAllSucceed(FluentIterable.of(var0, var1, var2, var3, var4, var5).append(var6));
   }

   private ClosingFuture(ListenableFuture<V> var1) {
      this.future = FluentFuture.from(var1);
   }

   private ClosingFuture(final ClosingFuture.ClosingCallable<V> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      TrustedListenableFutureTask var3 = TrustedListenableFutureTask.create(new Callable<V>() {
         @ParametricNullness
         @Override
         public V call() throws Exception {
            return (V)var1.call(ClosingFuture.this.closeables.closer);
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      });
      var2.execute(var3);
      this.future = var3;
   }

   private ClosingFuture(final ClosingFuture.AsyncClosingCallable<V> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      TrustedListenableFutureTask var3 = TrustedListenableFutureTask.create(new AsyncCallable<V>() {
         @Override
         public ListenableFuture<V> call() throws Exception {
            ClosingFuture.CloseableList var1x = new ClosingFuture.CloseableList();

            FluentFuture var3;
            try {
               ClosingFuture var2 = var1.call(var1x.closer);
               var2.becomeSubsumedInto(ClosingFuture.this.closeables);
               var3 = var2.future;
            } finally {
               ClosingFuture.this.closeables.add(var1x, MoreExecutors.directExecutor());
            }

            return var3;
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      });
      var2.execute(var3);
      this.future = var3;
   }

   public ListenableFuture<?> statusFuture() {
      return Futures.nonCancellationPropagating(this.future.transform(Functions.constant(null), MoreExecutors.directExecutor()));
   }

   public <U> ClosingFuture<U> transform(final ClosingFuture.ClosingFunction<? super V, U> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      AsyncFunction var3 = new AsyncFunction<V, U>() {
         @Override
         public ListenableFuture<U> apply(V var1x) throws Exception {
            return ClosingFuture.this.closeables.applyClosingFunction(var1, var1x);
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      };
      return this.derive(this.future.transformAsync(var3, var2));
   }

   public <U> ClosingFuture<U> transformAsync(final ClosingFuture.AsyncClosingFunction<? super V, U> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      AsyncFunction var3 = new AsyncFunction<V, U>() {
         @Override
         public ListenableFuture<U> apply(V var1x) throws Exception {
            return ClosingFuture.this.closeables.applyAsyncClosingFunction(var1, var1x);
         }

         @Override
         public String toString() {
            return var1.toString();
         }
      };
      return this.derive(this.future.transformAsync(var3, var2));
   }

   public static <V, U> ClosingFuture.AsyncClosingFunction<V, U> withoutCloser(final AsyncFunction<V, U> var0) {
      Preconditions.checkNotNull(var0);
      return new ClosingFuture.AsyncClosingFunction<V, U>() {
         @Override
         public ClosingFuture<U> apply(ClosingFuture.DeferredCloser var1, V var2) throws Exception {
            return ClosingFuture.from(var0.apply(var2));
         }
      };
   }

   public <X extends Throwable> ClosingFuture<V> catching(Class<X> var1, ClosingFuture.ClosingFunction<? super X, ? extends V> var2, Executor var3) {
      return this.catchingMoreGeneric(var1, var2, var3);
   }

   private <X extends Throwable, W extends V> ClosingFuture<V> catchingMoreGeneric(
      Class<X> var1, final ClosingFuture.ClosingFunction<? super X, W> var2, Executor var3
   ) {
      Preconditions.checkNotNull(var2);
      AsyncFunction var4 = new AsyncFunction<X, W>() {
         public ListenableFuture<W> apply(X var1) throws Exception {
            return ClosingFuture.this.closeables.applyClosingFunction(var2, var1);
         }

         @Override
         public String toString() {
            return var2.toString();
         }
      };
      return this.derive(this.future.catchingAsync(var1, var4, var3));
   }

   public <X extends Throwable> ClosingFuture<V> catchingAsync(Class<X> var1, ClosingFuture.AsyncClosingFunction<? super X, ? extends V> var2, Executor var3) {
      return this.catchingAsyncMoreGeneric(var1, var2, var3);
   }

   private <X extends Throwable, W extends V> ClosingFuture<V> catchingAsyncMoreGeneric(
      Class<X> var1, final ClosingFuture.AsyncClosingFunction<? super X, W> var2, Executor var3
   ) {
      Preconditions.checkNotNull(var2);
      AsyncFunction var4 = new AsyncFunction<X, W>() {
         public ListenableFuture<W> apply(X var1) throws Exception {
            return ClosingFuture.this.closeables.applyAsyncClosingFunction(var2, var1);
         }

         @Override
         public String toString() {
            return var2.toString();
         }
      };
      return this.derive(this.future.catchingAsync(var1, var4, var3));
   }

   public FluentFuture<V> finishToFuture() {
      if (this.compareAndUpdateState(ClosingFuture.State.OPEN, ClosingFuture.State.WILL_CLOSE)) {
         logger.log(Level.FINER, "will close {0}", this);
         this.future.addListener(new Runnable() {
            @Override
            public void run() {
               ClosingFuture.this.checkAndUpdateState(ClosingFuture.State.WILL_CLOSE, ClosingFuture.State.CLOSING);
               ClosingFuture.this.close();
               ClosingFuture.this.checkAndUpdateState(ClosingFuture.State.CLOSING, ClosingFuture.State.CLOSED);
            }
         }, MoreExecutors.directExecutor());
      } else {
         switch ((ClosingFuture.State)this.state.get()) {
            case SUBSUMED:
               throw new IllegalStateException("Cannot call finishToFuture() after deriving another step");
            case WILL_CREATE_VALUE_AND_CLOSER:
               throw new IllegalStateException("Cannot call finishToFuture() after calling finishToValueAndCloser()");
            case WILL_CLOSE:
            case CLOSING:
            case CLOSED:
               throw new IllegalStateException("Cannot call finishToFuture() twice");
            case OPEN:
               throw new AssertionError();
         }
      }

      return this.future;
   }

   public void finishToValueAndCloser(final ClosingFuture.ValueAndCloserConsumer<? super V> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      if (!this.compareAndUpdateState(ClosingFuture.State.OPEN, ClosingFuture.State.WILL_CREATE_VALUE_AND_CLOSER)) {
         switch ((ClosingFuture.State)this.state.get()) {
            case SUBSUMED:
               throw new IllegalStateException("Cannot call finishToValueAndCloser() after deriving another step");
            case WILL_CREATE_VALUE_AND_CLOSER:
               throw new IllegalStateException("Cannot call finishToValueAndCloser() twice");
            case WILL_CLOSE:
            case CLOSING:
            case CLOSED:
               throw new IllegalStateException("Cannot call finishToValueAndCloser() after calling finishToFuture()");
            case OPEN:
            default:
               throw new AssertionError(this.state);
         }
      } else {
         this.future.addListener(new Runnable() {
            @Override
            public void run() {
               ClosingFuture.provideValueAndCloser(var1, ClosingFuture.this);
            }
         }, var2);
      }
   }

   private static <C, V extends C> void provideValueAndCloser(ClosingFuture.ValueAndCloserConsumer<C> var0, ClosingFuture<V> var1) {
      var0.accept(new ClosingFuture.ValueAndCloser<>(var1));
   }

   @CanIgnoreReturnValue
   public boolean cancel(boolean var1) {
      logger.log(Level.FINER, "cancelling {0}", this);
      boolean var2 = this.future.cancel(var1);
      if (var2) {
         this.close();
      }

      return var2;
   }

   private void close() {
      logger.log(Level.FINER, "closing {0}", this);
      this.closeables.close();
   }

   private <U> ClosingFuture<U> derive(FluentFuture<U> var1) {
      ClosingFuture var2 = new ClosingFuture<>(var1);
      this.becomeSubsumedInto(var2.closeables);
      return var2;
   }

   private void becomeSubsumedInto(ClosingFuture.CloseableList var1) {
      this.checkAndUpdateState(ClosingFuture.State.OPEN, ClosingFuture.State.SUBSUMED);
      var1.add(this.closeables, MoreExecutors.directExecutor());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("state", this.state.get()).addValue(this.future).toString();
   }

   @Override
   protected void finalize() {
      if (this.state.get().equals(ClosingFuture.State.OPEN)) {
         logger.log(Level.SEVERE, "Uh oh! An open ClosingFuture has leaked and will close: {0}", this);
         FluentFuture var1 = this.finishToFuture();
      }
   }

   private static void closeQuietly(final AutoCloseable var0, Executor var1) {
      if (var0 != null) {
         try {
            var1.execute(new Runnable() {
               @Override
               public void run() {
                  try {
                     var0.close();
                  } catch (Exception var2) {
                     ClosingFuture.logger.log(Level.WARNING, "thrown by close()", (Throwable)var2);
                  }
               }
            });
         } catch (RejectedExecutionException var3) {
            if (logger.isLoggable(Level.WARNING)) {
               logger.log(Level.WARNING, String.format("while submitting close to %s; will close inline", var1), (Throwable)var3);
            }

            closeQuietly(var0, MoreExecutors.directExecutor());
         }
      }
   }

   private void checkAndUpdateState(ClosingFuture.State var1, ClosingFuture.State var2) {
      Preconditions.checkState(this.compareAndUpdateState(var1, var2), "Expected state to be %s, but it was %s", var1, var2);
   }

   private boolean compareAndUpdateState(ClosingFuture.State var1, ClosingFuture.State var2) {
      return this.state.compareAndSet(var1, var2);
   }

   @VisibleForTesting
   CountDownLatch whenClosedCountDown() {
      return this.closeables.whenClosedCountDown();
   }

   @FunctionalInterface
   public interface AsyncClosingCallable<V> {
      ClosingFuture<V> call(ClosingFuture.DeferredCloser var1) throws Exception;
   }

   @FunctionalInterface
   public interface AsyncClosingFunction<T, U> {
      ClosingFuture<U> apply(ClosingFuture.DeferredCloser var1, @ParametricNullness T var2) throws Exception;
   }

   private static final class CloseableList extends IdentityHashMap<AutoCloseable, Executor> implements Closeable {
      private final ClosingFuture.DeferredCloser closer = new ClosingFuture.DeferredCloser(this);
      private volatile boolean closed;
      private volatile CountDownLatch whenClosed;

      private CloseableList() {
      }

      <V, U> ListenableFuture<U> applyClosingFunction(ClosingFuture.ClosingFunction<? super V, U> var1, @ParametricNullness V var2) throws Exception {
         ClosingFuture.CloseableList var3 = new ClosingFuture.CloseableList();

         ListenableFuture var4;
         try {
            var4 = Futures.immediateFuture(var1.apply(var3.closer, var2));
         } finally {
            this.add(var3, MoreExecutors.directExecutor());
         }

         return var4;
      }

      <V, U> FluentFuture<U> applyAsyncClosingFunction(ClosingFuture.AsyncClosingFunction<V, U> var1, @ParametricNullness V var2) throws Exception {
         ClosingFuture.CloseableList var3 = new ClosingFuture.CloseableList();

         FluentFuture var5;
         try {
            ClosingFuture var4 = var1.apply(var3.closer, var2);
            var4.becomeSubsumedInto(var3);
            var5 = var4.future;
         } finally {
            this.add(var3, MoreExecutors.directExecutor());
         }

         return var5;
      }

      @Override
      public void close() {
         if (!this.closed) {
            synchronized (this) {
               if (this.closed) {
                  return;
               }

               this.closed = true;
            }

            for (Entry var2 : this.entrySet()) {
               ClosingFuture.closeQuietly((AutoCloseable)var2.getKey(), (Executor)var2.getValue());
            }

            this.clear();
            if (this.whenClosed != null) {
               this.whenClosed.countDown();
            }
         }
      }

      void add(AutoCloseable var1, Executor var2) {
         Preconditions.checkNotNull(var2);
         if (var1 != null) {
            synchronized (this) {
               if (!this.closed) {
                  this.put(var1, var2);
                  return;
               }
            }

            ClosingFuture.closeQuietly(var1, var2);
         }
      }

      CountDownLatch whenClosedCountDown() {
         if (this.closed) {
            return new CountDownLatch(0);
         } else {
            synchronized (this) {
               if (this.closed) {
                  return new CountDownLatch(0);
               } else {
                  Preconditions.checkState(this.whenClosed == null);
                  return this.whenClosed = new CountDownLatch(1);
               }
            }
         }
      }
   }

   @FunctionalInterface
   public interface ClosingCallable<V> {
      @ParametricNullness
      V call(ClosingFuture.DeferredCloser var1) throws Exception;
   }

   @FunctionalInterface
   public interface ClosingFunction<T, U> {
      @ParametricNullness
      U apply(ClosingFuture.DeferredCloser var1, @ParametricNullness T var2) throws Exception;
   }

   @DoNotMock("Use ClosingFuture.whenAllSucceed() or .whenAllComplete() instead.")
   public static class Combiner {
      private final ClosingFuture.CloseableList closeables = new ClosingFuture.CloseableList();
      private final boolean allMustSucceed;
      protected final ImmutableList<ClosingFuture<?>> inputs;
      private static final Function<ClosingFuture<?>, FluentFuture<?>> INNER_FUTURE = new Function<ClosingFuture<?>, FluentFuture<?>>() {
         public FluentFuture<?> apply(ClosingFuture<?> var1) {
            return var1.future;
         }
      };

      private Combiner(boolean var1, Iterable<? extends ClosingFuture<?>> var2) {
         this.allMustSucceed = var1;
         this.inputs = ImmutableList.copyOf(var2);

         for (ClosingFuture var4 : var2) {
            var4.becomeSubsumedInto(this.closeables);
         }
      }

      public <V> ClosingFuture<V> call(final ClosingFuture.Combiner.CombiningCallable<V> var1, Executor var2) {
         Callable var3 = new Callable<V>() {
            @ParametricNullness
            @Override
            public V call() throws Exception {
               return new ClosingFuture.Peeker(Combiner.this.inputs).call(var1, Combiner.this.closeables);
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         };
         ClosingFuture var4 = new ClosingFuture(this.futureCombiner().call(var3, var2));
         var4.closeables.add(this.closeables, MoreExecutors.directExecutor());
         return var4;
      }

      public <V> ClosingFuture<V> callAsync(final ClosingFuture.Combiner.AsyncCombiningCallable<V> var1, Executor var2) {
         AsyncCallable var3 = new AsyncCallable<V>() {
            @Override
            public ListenableFuture<V> call() throws Exception {
               return new ClosingFuture.Peeker(Combiner.this.inputs).callAsync(var1, Combiner.this.closeables);
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         };
         ClosingFuture var4 = new ClosingFuture(this.futureCombiner().callAsync(var3, var2));
         var4.closeables.add(this.closeables, MoreExecutors.directExecutor());
         return var4;
      }

      private Futures.FutureCombiner<Object> futureCombiner() {
         return this.allMustSucceed ? Futures.whenAllSucceed(this.inputFutures()) : Futures.whenAllComplete(this.inputFutures());
      }

      private ImmutableList<FluentFuture<?>> inputFutures() {
         return FluentIterable.from(this.inputs).transform(INNER_FUTURE).toList();
      }

      @FunctionalInterface
      public interface AsyncCombiningCallable<V> {
         ClosingFuture<V> call(ClosingFuture.DeferredCloser var1, ClosingFuture.Peeker var2) throws Exception;
      }

      @FunctionalInterface
      public interface CombiningCallable<V> {
         @ParametricNullness
         V call(ClosingFuture.DeferredCloser var1, ClosingFuture.Peeker var2) throws Exception;
      }
   }

   public static final class Combiner2<V1, V2> extends ClosingFuture.Combiner {
      private final ClosingFuture<V1> future1;
      private final ClosingFuture<V2> future2;

      private Combiner2(ClosingFuture<V1> var1, ClosingFuture<V2> var2) {
         super(true, ImmutableList.of(var1, var2));
         this.future1 = var1;
         this.future2 = var2;
      }

      public <U> ClosingFuture<U> call(final ClosingFuture.Combiner2.ClosingFunction2<V1, V2, U> var1, Executor var2) {
         return this.call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            @Override
            public U call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
               return (U)var1.apply(var1x, var2.getDone(Combiner2.this.future1), var2.getDone(Combiner2.this.future2));
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         }, var2);
      }

      public <U> ClosingFuture<U> callAsync(final ClosingFuture.Combiner2.AsyncClosingFunction2<V1, V2, U> var1, Executor var2) {
         return this.callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            @Override
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
               return var1.apply(var1x, var2.getDone(Combiner2.this.future1), var2.getDone(Combiner2.this.future2));
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         }, var2);
      }

      @FunctionalInterface
      public interface AsyncClosingFunction2<V1, V2, U> {
         ClosingFuture<U> apply(ClosingFuture.DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3) throws Exception;
      }

      @FunctionalInterface
      public interface ClosingFunction2<V1, V2, U> {
         @ParametricNullness
         U apply(ClosingFuture.DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3) throws Exception;
      }
   }

   public static final class Combiner3<V1, V2, V3> extends ClosingFuture.Combiner {
      private final ClosingFuture<V1> future1;
      private final ClosingFuture<V2> future2;
      private final ClosingFuture<V3> future3;

      private Combiner3(ClosingFuture<V1> var1, ClosingFuture<V2> var2, ClosingFuture<V3> var3) {
         super(true, ImmutableList.of(var1, var2, var3));
         this.future1 = var1;
         this.future2 = var2;
         this.future3 = var3;
      }

      public <U> ClosingFuture<U> call(final ClosingFuture.Combiner3.ClosingFunction3<V1, V2, V3, U> var1, Executor var2) {
         return this.call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            @Override
            public U call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
               return (U)var1.apply(var1x, var2.getDone(Combiner3.this.future1), var2.getDone(Combiner3.this.future2), var2.getDone(Combiner3.this.future3));
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         }, var2);
      }

      public <U> ClosingFuture<U> callAsync(final ClosingFuture.Combiner3.AsyncClosingFunction3<V1, V2, V3, U> var1, Executor var2) {
         return this.callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            @Override
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
               return var1.apply(var1x, var2.getDone(Combiner3.this.future1), var2.getDone(Combiner3.this.future2), var2.getDone(Combiner3.this.future3));
            }

            @Override
            public String toString() {
               return var1.toString();
            }
         }, var2);
      }

      @FunctionalInterface
      public interface AsyncClosingFunction3<V1, V2, V3, U> {
         ClosingFuture<U> apply(ClosingFuture.DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4) throws Exception;
      }

      @FunctionalInterface
      public interface ClosingFunction3<V1, V2, V3, U> {
         @ParametricNullness
         U apply(ClosingFuture.DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4) throws Exception;
      }
   }

   public static final class Combiner4<V1, V2, V3, V4> extends ClosingFuture.Combiner {
      private final ClosingFuture<V1> future1;
      private final ClosingFuture<V2> future2;
      private final ClosingFuture<V3> future3;
      private final ClosingFuture<V4> future4;

      private Combiner4(ClosingFuture<V1> var1, ClosingFuture<V2> var2, ClosingFuture<V3> var3, ClosingFuture<V4> var4) {
         super(true, ImmutableList.of(var1, var2, var3, var4));
         this.future1 = var1;
         this.future2 = var2;
         this.future3 = var3;
         this.future4 = var4;
      }

      public <U> ClosingFuture<U> call(final ClosingFuture.Combiner4.ClosingFunction4<V1, V2, V3, V4, U> var1, Executor var2) {
         return this.call(
            new ClosingFuture.Combiner.CombiningCallable<U>() {
               @ParametricNullness
               @Override
               public U call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
                  return (U)var1.apply(
                     var1x,
                     var2.getDone(Combiner4.this.future1),
                     var2.getDone(Combiner4.this.future2),
                     var2.getDone(Combiner4.this.future3),
                     var2.getDone(Combiner4.this.future4)
                  );
               }

               @Override
               public String toString() {
                  return var1.toString();
               }
            },
            var2
         );
      }

      public <U> ClosingFuture<U> callAsync(final ClosingFuture.Combiner4.AsyncClosingFunction4<V1, V2, V3, V4, U> var1, Executor var2) {
         return this.callAsync(
            new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
               @Override
               public ClosingFuture<U> call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
                  return var1.apply(
                     var1x,
                     var2.getDone(Combiner4.this.future1),
                     var2.getDone(Combiner4.this.future2),
                     var2.getDone(Combiner4.this.future3),
                     var2.getDone(Combiner4.this.future4)
                  );
               }

               @Override
               public String toString() {
                  return var1.toString();
               }
            },
            var2
         );
      }

      @FunctionalInterface
      public interface AsyncClosingFunction4<V1, V2, V3, V4, U> {
         ClosingFuture<U> apply(
            ClosingFuture.DeferredCloser var1,
            @ParametricNullness V1 var2,
            @ParametricNullness V2 var3,
            @ParametricNullness V3 var4,
            @ParametricNullness V4 var5
         ) throws Exception;
      }

      @FunctionalInterface
      public interface ClosingFunction4<V1, V2, V3, V4, U> {
         @ParametricNullness
         U apply(
            ClosingFuture.DeferredCloser var1,
            @ParametricNullness V1 var2,
            @ParametricNullness V2 var3,
            @ParametricNullness V3 var4,
            @ParametricNullness V4 var5
         ) throws Exception;
      }
   }

   public static final class Combiner5<V1, V2, V3, V4, V5> extends ClosingFuture.Combiner {
      private final ClosingFuture<V1> future1;
      private final ClosingFuture<V2> future2;
      private final ClosingFuture<V3> future3;
      private final ClosingFuture<V4> future4;
      private final ClosingFuture<V5> future5;

      private Combiner5(ClosingFuture<V1> var1, ClosingFuture<V2> var2, ClosingFuture<V3> var3, ClosingFuture<V4> var4, ClosingFuture<V5> var5) {
         super(true, ImmutableList.of(var1, var2, var3, var4, var5));
         this.future1 = var1;
         this.future2 = var2;
         this.future3 = var3;
         this.future4 = var4;
         this.future5 = var5;
      }

      public <U> ClosingFuture<U> call(final ClosingFuture.Combiner5.ClosingFunction5<V1, V2, V3, V4, V5, U> var1, Executor var2) {
         return this.call(
            new ClosingFuture.Combiner.CombiningCallable<U>() {
               @ParametricNullness
               @Override
               public U call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
                  return (U)var1.apply(
                     var1x,
                     var2.getDone(Combiner5.this.future1),
                     var2.getDone(Combiner5.this.future2),
                     var2.getDone(Combiner5.this.future3),
                     var2.getDone(Combiner5.this.future4),
                     var2.getDone(Combiner5.this.future5)
                  );
               }

               @Override
               public String toString() {
                  return var1.toString();
               }
            },
            var2
         );
      }

      public <U> ClosingFuture<U> callAsync(final ClosingFuture.Combiner5.AsyncClosingFunction5<V1, V2, V3, V4, V5, U> var1, Executor var2) {
         return this.callAsync(
            new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
               @Override
               public ClosingFuture<U> call(ClosingFuture.DeferredCloser var1x, ClosingFuture.Peeker var2) throws Exception {
                  return var1.apply(
                     var1x,
                     var2.getDone(Combiner5.this.future1),
                     var2.getDone(Combiner5.this.future2),
                     var2.getDone(Combiner5.this.future3),
                     var2.getDone(Combiner5.this.future4),
                     var2.getDone(Combiner5.this.future5)
                  );
               }

               @Override
               public String toString() {
                  return var1.toString();
               }
            },
            var2
         );
      }

      @FunctionalInterface
      public interface AsyncClosingFunction5<V1, V2, V3, V4, V5, U> {
         ClosingFuture<U> apply(
            ClosingFuture.DeferredCloser var1,
            @ParametricNullness V1 var2,
            @ParametricNullness V2 var3,
            @ParametricNullness V3 var4,
            @ParametricNullness V4 var5,
            @ParametricNullness V5 var6
         ) throws Exception;
      }

      @FunctionalInterface
      public interface ClosingFunction5<V1, V2, V3, V4, V5, U> {
         @ParametricNullness
         U apply(
            ClosingFuture.DeferredCloser var1,
            @ParametricNullness V1 var2,
            @ParametricNullness V2 var3,
            @ParametricNullness V3 var4,
            @ParametricNullness V4 var5,
            @ParametricNullness V5 var6
         ) throws Exception;
      }
   }

   public static final class DeferredCloser {
      @RetainedWith
      private final ClosingFuture.CloseableList list;

      DeferredCloser(ClosingFuture.CloseableList var1) {
         this.list = var1;
      }

      @ParametricNullness
      @CanIgnoreReturnValue
      public <C extends Object & AutoCloseable> C eventuallyClose(@ParametricNullness C var1, Executor var2) {
         Preconditions.checkNotNull(var2);
         if (var1 != null) {
            this.list.add((AutoCloseable)var1, var2);
         }

         return (C)var1;
      }
   }

   public static final class Peeker {
      private final ImmutableList<ClosingFuture<?>> futures;
      private volatile boolean beingCalled;

      private Peeker(ImmutableList<ClosingFuture<?>> var1) {
         this.futures = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      public final <D> D getDone(ClosingFuture<D> var1) throws ExecutionException {
         Preconditions.checkState(this.beingCalled);
         Preconditions.checkArgument(this.futures.contains(var1));
         return Futures.getDone((Future<D>)var1.future);
      }

      @ParametricNullness
      private <V> V call(ClosingFuture.Combiner.CombiningCallable<V> var1, ClosingFuture.CloseableList var2) throws Exception {
         this.beingCalled = true;
         ClosingFuture.CloseableList var3 = new ClosingFuture.CloseableList();

         Object var4;
         try {
            var4 = var1.call(var3.closer, this);
         } finally {
            var2.add(var3, MoreExecutors.directExecutor());
            this.beingCalled = false;
         }

         return (V)var4;
      }

      private <V> FluentFuture<V> callAsync(ClosingFuture.Combiner.AsyncCombiningCallable<V> var1, ClosingFuture.CloseableList var2) throws Exception {
         this.beingCalled = true;
         ClosingFuture.CloseableList var3 = new ClosingFuture.CloseableList();

         FluentFuture var5;
         try {
            ClosingFuture var4 = var1.call(var3.closer, this);
            var4.becomeSubsumedInto(var2);
            var5 = var4.future;
         } finally {
            var2.add(var3, MoreExecutors.directExecutor());
            this.beingCalled = false;
         }

         return var5;
      }
   }

   static enum State {
      OPEN,
      SUBSUMED,
      WILL_CLOSE,
      CLOSING,
      CLOSED,
      WILL_CREATE_VALUE_AND_CLOSER;
   }

   public static final class ValueAndCloser<V> {
      private final ClosingFuture<? extends V> closingFuture;

      ValueAndCloser(ClosingFuture<? extends V> var1) {
         this.closingFuture = Preconditions.checkNotNull(var1);
      }

      @ParametricNullness
      public V get() throws ExecutionException {
         return Futures.getDone((Future<V>)this.closingFuture.future);
      }

      public void closeAsync() {
         this.closingFuture.close();
      }
   }

   @FunctionalInterface
   public interface ValueAndCloserConsumer<V> {
      void accept(ClosingFuture.ValueAndCloser<V> var1);
   }
}
