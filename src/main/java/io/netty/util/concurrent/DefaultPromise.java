package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
   private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance(DefaultPromise.class.getName() + ".rejectedExecution");
   private static final int MAX_LISTENER_STACK_DEPTH = Math.min(8, SystemPropertyUtil.getInt("io.netty.defaultPromise.maxListenerStackDepth", 8));
   private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      DefaultPromise.class, Object.class, "result"
   );
   private static final Object SUCCESS = new Object();
   private static final Object UNCANCELLABLE = new Object();
   private static final DefaultPromise.CauseHolder CANCELLATION_CAUSE_HOLDER = new DefaultPromise.CauseHolder(
      DefaultPromise.StacklessCancellationException.newInstance(DefaultPromise.class, "cancel(...)")
   );
   private static final StackTraceElement[] CANCELLATION_STACK = CANCELLATION_CAUSE_HOLDER.cause.getStackTrace();
   private volatile Object result;
   private final EventExecutor executor;
   private Object listeners;
   private short waiters;
   private boolean notifyingListeners;

   public DefaultPromise(EventExecutor var1) {
      this.executor = ObjectUtil.checkNotNull(var1, "executor");
   }

   protected DefaultPromise() {
      this.executor = null;
   }

   @Override
   public Promise<V> setSuccess(V var1) {
      if (this.setSuccess0((V)var1)) {
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this);
      }
   }

   @Override
   public boolean trySuccess(V var1) {
      return this.setSuccess0((V)var1);
   }

   @Override
   public Promise<V> setFailure(Throwable var1) {
      if (this.setFailure0(var1)) {
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this, var1);
      }
   }

   @Override
   public boolean tryFailure(Throwable var1) {
      return this.setFailure0(var1);
   }

   @Override
   public boolean setUncancellable() {
      if (RESULT_UPDATER.compareAndSet(this, null, UNCANCELLABLE)) {
         return true;
      } else {
         Object var1 = this.result;
         return !isDone0(var1) || !isCancelled0(var1);
      }
   }

   @Override
   public boolean isSuccess() {
      Object var1 = this.result;
      return var1 != null && var1 != UNCANCELLABLE && !(var1 instanceof DefaultPromise.CauseHolder);
   }

   @Override
   public boolean isCancellable() {
      return this.result == null;
   }

   @Override
   public Throwable cause() {
      return this.cause0(this.result);
   }

   private Throwable cause0(Object var1) {
      if (!(var1 instanceof DefaultPromise.CauseHolder)) {
         return null;
      } else {
         if (var1 == CANCELLATION_CAUSE_HOLDER) {
            DefaultPromise.LeanCancellationException var2 = new DefaultPromise.LeanCancellationException();
            if (RESULT_UPDATER.compareAndSet(this, CANCELLATION_CAUSE_HOLDER, new DefaultPromise.CauseHolder(var2))) {
               return var2;
            }

            var1 = this.result;
         }

         return ((DefaultPromise.CauseHolder)var1).cause;
      }
   }

   @Override
   public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> var1) {
      ObjectUtil.checkNotNull(var1, "listener");
      synchronized (this) {
         this.addListener0(var1);
      }

      if (this.isDone()) {
         this.notifyListeners();
      }

      return this;
   }

   @Override
   public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      ObjectUtil.checkNotNull(var1, "listeners");
      synchronized (this) {
         for (GenericFutureListener var6 : var1) {
            if (var6 == null) {
               break;
            }

            this.addListener0(var6);
         }
      }

      if (this.isDone()) {
         this.notifyListeners();
      }

      return this;
   }

   @Override
   public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1) {
      ObjectUtil.checkNotNull(var1, "listener");
      synchronized (this) {
         this.removeListener0(var1);
         return this;
      }
   }

   @Override
   public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      ObjectUtil.checkNotNull(var1, "listeners");
      synchronized (this) {
         for (GenericFutureListener var6 : var1) {
            if (var6 == null) {
               break;
            }

            this.removeListener0(var6);
         }

         return this;
      }
   }

   @Override
   public Promise<V> await() throws InterruptedException {
      if (this.isDone()) {
         return this;
      } else if (Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         this.checkDeadLock();
         synchronized (this) {
            while (!this.isDone()) {
               this.incWaiters();

               try {
                  this.wait();
               } finally {
                  this.decWaiters();
               }
            }

            return this;
         }
      }
   }

   @Override
   public Promise<V> awaitUninterruptibly() {
      if (this.isDone()) {
         return this;
      } else {
         this.checkDeadLock();
         boolean var1 = false;
         synchronized (this) {
            while (!this.isDone()) {
               this.incWaiters();

               try {
                  this.wait();
               } catch (InterruptedException var9) {
                  var1 = true;
               } finally {
                  this.decWaiters();
               }
            }
         }

         if (var1) {
            Thread.currentThread().interrupt();
         }

         return this;
      }
   }

   @Override
   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      return this.await0(var3.toNanos(var1), true);
   }

   @Override
   public boolean await(long var1) throws InterruptedException {
      return this.await0(TimeUnit.MILLISECONDS.toNanos(var1), true);
   }

   @Override
   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      try {
         return this.await0(var3.toNanos(var1), false);
      } catch (InterruptedException var5) {
         throw new InternalError();
      }
   }

   @Override
   public boolean awaitUninterruptibly(long var1) {
      try {
         return this.await0(TimeUnit.MILLISECONDS.toNanos(var1), false);
      } catch (InterruptedException var4) {
         throw new InternalError();
      }
   }

   @Override
   public V getNow() {
      Object var1 = this.result;
      return (V)(!(var1 instanceof DefaultPromise.CauseHolder) && var1 != SUCCESS && var1 != UNCANCELLABLE ? var1 : null);
   }

   @Override
   public V get() throws InterruptedException, ExecutionException {
      Object var1 = this.result;
      if (!isDone0(var1)) {
         this.await();
         var1 = this.result;
      }

      if (var1 != SUCCESS && var1 != UNCANCELLABLE) {
         Throwable var2 = this.cause0(var1);
         if (var2 == null) {
            return (V)var1;
         } else if (var2 instanceof CancellationException) {
            throw (CancellationException)var2;
         } else {
            throw new ExecutionException(var2);
         }
      } else {
         return null;
      }
   }

   @Override
   public V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      Object var4 = this.result;
      if (!isDone0(var4)) {
         if (!this.await(var1, var3)) {
            throw new TimeoutException();
         }

         var4 = this.result;
      }

      if (var4 != SUCCESS && var4 != UNCANCELLABLE) {
         Throwable var5 = this.cause0(var4);
         if (var5 == null) {
            return (V)var4;
         } else if (var5 instanceof CancellationException) {
            throw (CancellationException)var5;
         } else {
            throw new ExecutionException(var5);
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean cancel(boolean var1) {
      if (RESULT_UPDATER.compareAndSet(this, null, CANCELLATION_CAUSE_HOLDER)) {
         if (this.checkNotifyWaiters()) {
            this.notifyListeners();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isCancelled() {
      return isCancelled0(this.result);
   }

   @Override
   public boolean isDone() {
      return isDone0(this.result);
   }

   @Override
   public Promise<V> sync() throws InterruptedException {
      this.await();
      this.rethrowIfFailed();
      return this;
   }

   @Override
   public Promise<V> syncUninterruptibly() {
      this.awaitUninterruptibly();
      this.rethrowIfFailed();
      return this;
   }

   @Override
   public String toString() {
      return this.toStringBuilder().toString();
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = new StringBuilder(64).append(StringUtil.simpleClassName(this)).append('@').append(Integer.toHexString(this.hashCode()));
      Object var2 = this.result;
      if (var2 == SUCCESS) {
         var1.append("(success)");
      } else if (var2 == UNCANCELLABLE) {
         var1.append("(uncancellable)");
      } else if (var2 instanceof DefaultPromise.CauseHolder) {
         var1.append("(failure: ").append(((DefaultPromise.CauseHolder)var2).cause).append(')');
      } else if (var2 != null) {
         var1.append("(success: ").append(var2).append(')');
      } else {
         var1.append("(incomplete)");
      }

      return var1;
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   protected void checkDeadLock() {
      EventExecutor var1 = this.executor();
      if (var1 != null && var1.inEventLoop()) {
         throw new BlockingOperationException(this.toString());
      }
   }

   protected static void notifyListener(EventExecutor var0, Future<?> var1, GenericFutureListener<?> var2) {
      notifyListenerWithStackOverFlowProtection(
         ObjectUtil.checkNotNull(var0, "eventExecutor"), ObjectUtil.checkNotNull(var1, "future"), ObjectUtil.checkNotNull(var2, "listener")
      );
   }

   private void notifyListeners() {
      EventExecutor var1 = this.executor();
      if (var1.inEventLoop()) {
         InternalThreadLocalMap var2 = InternalThreadLocalMap.get();
         int var3 = var2.futureListenerStackDepth();
         if (var3 < MAX_LISTENER_STACK_DEPTH) {
            var2.setFutureListenerStackDepth(var3 + 1);

            try {
               this.notifyListenersNow();
            } finally {
               var2.setFutureListenerStackDepth(var3);
            }

            return;
         }
      }

      safeExecute(var1, new Runnable() {
         @Override
         public void run() {
            DefaultPromise.this.notifyListenersNow();
         }
      });
   }

   private static void notifyListenerWithStackOverFlowProtection(EventExecutor var0, final Future<?> var1, final GenericFutureListener<?> var2) {
      if (var0.inEventLoop()) {
         InternalThreadLocalMap var3 = InternalThreadLocalMap.get();
         int var4 = var3.futureListenerStackDepth();
         if (var4 < MAX_LISTENER_STACK_DEPTH) {
            var3.setFutureListenerStackDepth(var4 + 1);

            try {
               notifyListener0(var1, var2);
            } finally {
               var3.setFutureListenerStackDepth(var4);
            }

            return;
         }
      }

      safeExecute(var0, new Runnable() {
         @Override
         public void run() {
            DefaultPromise.notifyListener0(var1, var2);
         }
      });
   }

   private void notifyListenersNow() {
      Object var1;
      synchronized (this) {
         if (this.notifyingListeners || this.listeners == null) {
            return;
         }

         this.notifyingListeners = true;
         var1 = this.listeners;
         this.listeners = null;
      }

      while (true) {
         if (var1 instanceof DefaultFutureListeners) {
            this.notifyListeners0((DefaultFutureListeners)var1);
         } else {
            notifyListener0(this, (GenericFutureListener)var1);
         }

         synchronized (this) {
            if (this.listeners == null) {
               this.notifyingListeners = false;
               return;
            }

            var1 = this.listeners;
            this.listeners = null;
         }
      }
   }

   private void notifyListeners0(DefaultFutureListeners var1) {
      GenericFutureListener[] var2 = var1.listeners();
      int var3 = var1.size();

      for (int var4 = 0; var4 < var3; var4++) {
         notifyListener0(this, var2[var4]);
      }
   }

   private static void notifyListener0(Future var0, GenericFutureListener var1) {
      try {
         var1.operationComplete(var0);
      } catch (Throwable var3) {
         if (logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by " + var1.getClass().getName() + ".operationComplete()", var3);
         }
      }
   }

   private void addListener0(GenericFutureListener<? extends Future<? super V>> var1) {
      if (this.listeners == null) {
         this.listeners = var1;
      } else if (this.listeners instanceof DefaultFutureListeners) {
         ((DefaultFutureListeners)this.listeners).add(var1);
      } else {
         this.listeners = new DefaultFutureListeners((GenericFutureListener<? extends Future<?>>)this.listeners, var1);
      }
   }

   private void removeListener0(GenericFutureListener<? extends Future<? super V>> var1) {
      if (this.listeners instanceof DefaultFutureListeners) {
         ((DefaultFutureListeners)this.listeners).remove(var1);
      } else if (this.listeners == var1) {
         this.listeners = null;
      }
   }

   private boolean setSuccess0(V var1) {
      return this.setValue0(var1 == null ? SUCCESS : var1);
   }

   private boolean setFailure0(Throwable var1) {
      return this.setValue0(new DefaultPromise.CauseHolder(ObjectUtil.checkNotNull(var1, "cause")));
   }

   private boolean setValue0(Object var1) {
      if (!RESULT_UPDATER.compareAndSet(this, null, var1) && !RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, var1)) {
         return false;
      } else {
         if (this.checkNotifyWaiters()) {
            this.notifyListeners();
         }

         return true;
      }
   }

   private synchronized boolean checkNotifyWaiters() {
      if (this.waiters > 0) {
         this.notifyAll();
      }

      return this.listeners != null;
   }

   private void incWaiters() {
      if (this.waiters == 32767) {
         throw new IllegalStateException("too many waiters: " + this);
      } else {
         this.waiters++;
      }
   }

   private void decWaiters() {
      this.waiters--;
   }

   private void rethrowIfFailed() {
      Throwable var1 = this.cause();
      if (var1 != null) {
         PlatformDependent.throwException(var1);
      }
   }

   private boolean await0(long var1, boolean var3) throws InterruptedException {
      if (this.isDone()) {
         return true;
      } else if (var1 <= 0L) {
         return this.isDone();
      } else if (var3 && Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         this.checkDeadLock();
         long var4 = System.nanoTime();
         synchronized (this) {
            boolean var7 = false;

            try {
               for (long var8 = var1; !this.isDone() && var8 > 0L; var8 = var1 - (System.nanoTime() - var4)) {
                  this.incWaiters();

                  try {
                     this.wait(var8 / 1000000L, (int)(var8 % 1000000L));
                  } catch (InterruptedException var22) {
                     if (var3) {
                        throw var22;
                     }

                     var7 = true;
                  } finally {
                     this.decWaiters();
                  }

                  if (this.isDone()) {
                     return true;
                  }
               }

               return this.isDone();
            } finally {
               if (var7) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }
   }

   void notifyProgressiveListeners(final long var1, final long var3) {
      Object var5 = this.progressiveListeners();
      if (var5 != null) {
         final ProgressiveFuture var6 = (ProgressiveFuture)this;
         EventExecutor var7 = this.executor();
         if (var7.inEventLoop()) {
            if (var5 instanceof GenericProgressiveFutureListener[]) {
               notifyProgressiveListeners0(var6, (GenericProgressiveFutureListener<?>[])var5, var1, var3);
            } else {
               notifyProgressiveListener0(var6, (GenericProgressiveFutureListener)var5, var1, var3);
            }
         } else if (var5 instanceof GenericProgressiveFutureListener[]) {
            final GenericProgressiveFutureListener[] var8 = (GenericProgressiveFutureListener[])var5;
            safeExecute(var7, new Runnable() {
               @Override
               public void run() {
                  DefaultPromise.notifyProgressiveListeners0(var6, var8, var1, var3);
               }
            });
         } else {
            final GenericProgressiveFutureListener var9 = (GenericProgressiveFutureListener)var5;
            safeExecute(var7, new Runnable() {
               @Override
               public void run() {
                  DefaultPromise.notifyProgressiveListener0(var6, var9, var1, var3);
               }
            });
         }
      }
   }

   private synchronized Object progressiveListeners() {
      Object var1 = this.listeners;
      if (var1 == null) {
         return null;
      } else if (var1 instanceof DefaultFutureListeners) {
         DefaultFutureListeners var2 = (DefaultFutureListeners)var1;
         int var3 = var2.progressiveSize();
         switch (var3) {
            case 0:
               return null;
            case 1:
               for (GenericFutureListener var12 : var2.listeners()) {
                  if (var12 instanceof GenericProgressiveFutureListener) {
                     return var12;
                  }
               }

               return null;
            default:
               GenericFutureListener[] var4 = var2.listeners();
               GenericProgressiveFutureListener[] var5 = new GenericProgressiveFutureListener[var3];
               int var6 = 0;

               for (int var7 = 0; var7 < var3; var6++) {
                  GenericFutureListener var8 = var4[var6];
                  if (var8 instanceof GenericProgressiveFutureListener) {
                     var5[var7++] = (GenericProgressiveFutureListener)var8;
                  }
               }

               return var5;
         }
      } else {
         return var1 instanceof GenericProgressiveFutureListener ? var1 : null;
      }
   }

   private static void notifyProgressiveListeners0(ProgressiveFuture<?> var0, GenericProgressiveFutureListener<?>[] var1, long var2, long var4) {
      for (GenericProgressiveFutureListener var9 : var1) {
         if (var9 == null) {
            break;
         }

         notifyProgressiveListener0(var0, var9, var2, var4);
      }
   }

   private static void notifyProgressiveListener0(ProgressiveFuture var0, GenericProgressiveFutureListener var1, long var2, long var4) {
      try {
         var1.operationProgressed(var0, var2, var4);
      } catch (Throwable var7) {
         if (logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by " + var1.getClass().getName() + ".operationProgressed()", var7);
         }
      }
   }

   private static boolean isCancelled0(Object var0) {
      return var0 instanceof DefaultPromise.CauseHolder && ((DefaultPromise.CauseHolder)var0).cause instanceof CancellationException;
   }

   private static boolean isDone0(Object var0) {
      return var0 != null && var0 != UNCANCELLABLE;
   }

   private static void safeExecute(EventExecutor var0, Runnable var1) {
      try {
         var0.execute(var1);
      } catch (Throwable var3) {
         rejectedExecutionLogger.error("Failed to submit a listener notification task. Event loop shut down?", var3);
      }
   }

   private static final class CauseHolder {
      final Throwable cause;

      CauseHolder(Throwable var1) {
         this.cause = var1;
      }
   }

   private static final class LeanCancellationException extends CancellationException {
      private static final long serialVersionUID = 2794674970981187807L;

      private LeanCancellationException() {
      }

      @Override
      public Throwable fillInStackTrace() {
         this.setStackTrace(DefaultPromise.CANCELLATION_STACK);
         return this;
      }

      @Override
      public String toString() {
         return CancellationException.class.getName();
      }
   }

   private static final class StacklessCancellationException extends CancellationException {
      private static final long serialVersionUID = -2974906711413716191L;

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }

      static DefaultPromise.StacklessCancellationException newInstance(Class<?> var0, String var1) {
         return ThrowableUtil.unknownStackTrace(new DefaultPromise.StacklessCancellationException(), var0, var1);
      }
   }
}
