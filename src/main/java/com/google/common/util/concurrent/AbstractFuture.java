package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.j2objc.annotations.ReflectionSupport;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
@ReflectionSupport(ReflectionSupport.Level.FULL)
public abstract class AbstractFuture<V> extends InternalFutureFailureAccess implements ListenableFuture<V> {
   private static final boolean GENERATE_CANCELLATION_CAUSES;
   private static final Logger log;
   private static final long SPIN_THRESHOLD_NANOS = 1000L;
   private static final AbstractFuture.AtomicHelper ATOMIC_HELPER;
   private static final Object NULL;
   private volatile Object value;
   private volatile AbstractFuture.Listener listeners;
   private volatile AbstractFuture.Waiter waiters;

   private void removeWaiter(AbstractFuture.Waiter var1) {
      var1.thread = null;

      label28:
      while (true) {
         AbstractFuture.Waiter var2 = null;
         AbstractFuture.Waiter var3 = this.waiters;
         if (var3 == AbstractFuture.Waiter.TOMBSTONE) {
            return;
         }

         while (var3 != null) {
            AbstractFuture.Waiter var4 = var3.next;
            if (var3.thread != null) {
               var2 = var3;
            } else if (var2 != null) {
               var2.next = var4;
               if (var2.thread == null) {
                  continue label28;
               }
            } else if (!ATOMIC_HELPER.casWaiters(this, var3, var4)) {
               continue label28;
            }

            var3 = var4;
         }

         return;
      }
   }

   protected AbstractFuture() {
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public V get(long var1, TimeUnit var3) throws InterruptedException, TimeoutException, ExecutionException {
      long var4 = var3.toNanos(var1);
      long var6 = var4;
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         Object var8 = this.value;
         if (var8 != null & !(var8 instanceof AbstractFuture.SetFuture)) {
            return this.getDoneValue(var8);
         } else {
            long var9 = var4 > 0L ? System.nanoTime() + var4 : 0L;
            if (var4 >= 1000L) {
               AbstractFuture.Waiter var11 = this.waiters;
               if (var11 == AbstractFuture.Waiter.TOMBSTONE) {
                  return this.getDoneValue(Objects.requireNonNull(this.value));
               }

               AbstractFuture.Waiter var12 = new AbstractFuture.Waiter();

               while (true) {
                  var12.setNext(var11);
                  if (ATOMIC_HELPER.casWaiters(this, var11, var12)) {
                     do {
                        OverflowAvoidingLockSupport.parkNanos(this, var6);
                        if (Thread.interrupted()) {
                           this.removeWaiter(var12);
                           throw new InterruptedException();
                        }

                        var8 = this.value;
                        if (var8 != null & !(var8 instanceof AbstractFuture.SetFuture)) {
                           return this.getDoneValue(var8);
                        }

                        var6 = var9 - System.nanoTime();
                     } while (var6 >= 1000L);

                     this.removeWaiter(var12);
                     break;
                  }

                  var11 = this.waiters;
                  if (var11 == AbstractFuture.Waiter.TOMBSTONE) {
                     return this.getDoneValue(Objects.requireNonNull(this.value));
                  }
               }
            }

            while (var6 > 0L) {
               var8 = this.value;
               if (var8 != null & !(var8 instanceof AbstractFuture.SetFuture)) {
                  return this.getDoneValue(var8);
               }

               if (Thread.interrupted()) {
                  throw new InterruptedException();
               }

               var6 = var9 - System.nanoTime();
            }

            String var24 = this.toString();
            String var25 = var3.toString().toLowerCase(Locale.ROOT);
            String var14 = var3.toString().toLowerCase(Locale.ROOT);
            String var13 = new StringBuilder(28 + String.valueOf(var14).length()).append("Waited ").append(var1).append(" ").append(var14).toString();
            if (var6 + 1000L < 0L) {
               var13 = String.valueOf(var13).concat(" (plus ");
               long var28 = -var6;
               long var16 = var3.convert(var28, TimeUnit.NANOSECONDS);
               long var18 = var28 - var3.toNanos(var16);
               boolean var20 = var16 == 0L || var18 > 1000L;
               if (var16 > 0L) {
                  String var21 = String.valueOf(var13);
                  var13 = new StringBuilder(21 + String.valueOf(var21).length() + String.valueOf(var25).length())
                     .append(var21)
                     .append(var16)
                     .append(" ")
                     .append(var25)
                     .toString();
                  if (var20) {
                     var13 = String.valueOf(var13).concat(",");
                  }

                  var13 = String.valueOf(var13).concat(" ");
               }

               if (var20) {
                  String var29 = String.valueOf(var13);
                  var13 = new StringBuilder(33 + String.valueOf(var29).length()).append(var29).append(var18).append(" nanoseconds ").toString();
               }

               var13 = String.valueOf(var13).concat("delay)");
            }

            if (this.isDone()) {
               throw new TimeoutException(String.valueOf(var13).concat(" but future completed as timeout expired"));
            } else {
               throw new TimeoutException(
                  new StringBuilder(5 + String.valueOf(var13).length() + String.valueOf(var24).length()).append(var13).append(" for ").append(var24).toString()
               );
            }
         }
      }
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public V get() throws InterruptedException, ExecutionException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         Object var1 = this.value;
         if (var1 != null & !(var1 instanceof AbstractFuture.SetFuture)) {
            return this.getDoneValue(var1);
         } else {
            AbstractFuture.Waiter var2 = this.waiters;
            if (var2 != AbstractFuture.Waiter.TOMBSTONE) {
               AbstractFuture.Waiter var3 = new AbstractFuture.Waiter();

               do {
                  var3.setNext(var2);
                  if (ATOMIC_HELPER.casWaiters(this, var2, var3)) {
                     do {
                        LockSupport.park(this);
                        if (Thread.interrupted()) {
                           this.removeWaiter(var3);
                           throw new InterruptedException();
                        }

                        var1 = this.value;
                     } while (!(var1 != null & !(var1 instanceof AbstractFuture.SetFuture)));

                     return this.getDoneValue(var1);
                  }

                  var2 = this.waiters;
               } while (var2 != AbstractFuture.Waiter.TOMBSTONE);
            }

            return this.getDoneValue(Objects.requireNonNull(this.value));
         }
      }
   }

   @ParametricNullness
   private V getDoneValue(Object var1) throws ExecutionException {
      if (var1 instanceof AbstractFuture.Cancellation) {
         throw cancellationExceptionWithCause("Task was cancelled.", ((AbstractFuture.Cancellation)var1).cause);
      } else if (var1 instanceof AbstractFuture.Failure) {
         throw new ExecutionException(((AbstractFuture.Failure)var1).exception);
      } else {
         return (V)(var1 == NULL ? NullnessCasts.uncheckedNull() : var1);
      }
   }

   @Override
   public boolean isDone() {
      Object var1 = this.value;
      return var1 != null & !(var1 instanceof AbstractFuture.SetFuture);
   }

   @Override
   public boolean isCancelled() {
      Object var1 = this.value;
      return var1 instanceof AbstractFuture.Cancellation;
   }

   @CanIgnoreReturnValue
   @Override
   public boolean cancel(boolean var1) {
      Object var2 = this.value;
      boolean var3 = false;
      if (var2 == null | var2 instanceof AbstractFuture.SetFuture) {
         Object var4 = GENERATE_CANCELLATION_CAUSES
            ? new AbstractFuture.Cancellation(var1, new CancellationException("Future.cancel() was called."))
            : Objects.requireNonNull(var1 ? AbstractFuture.Cancellation.CAUSELESS_INTERRUPTED : AbstractFuture.Cancellation.CAUSELESS_CANCELLED);
         AbstractFuture var5 = this;

         while (true) {
            while (!ATOMIC_HELPER.casValue(var5, var2, var4)) {
               var2 = var5.value;
               if (!(var2 instanceof AbstractFuture.SetFuture)) {
                  return var3;
               }
            }

            var3 = true;
            if (var1) {
               var5.interruptTask();
            }

            complete(var5);
            if (!(var2 instanceof AbstractFuture.SetFuture)) {
               break;
            }

            ListenableFuture var6 = ((AbstractFuture.SetFuture)var2).future;
            if (!(var6 instanceof AbstractFuture.Trusted)) {
               var6.cancel(var1);
               break;
            }

            AbstractFuture var7 = (AbstractFuture)var6;
            var2 = var7.value;
            if (!(var2 == null | var2 instanceof AbstractFuture.SetFuture)) {
               break;
            }

            var5 = var7;
         }
      }

      return var3;
   }

   protected void interruptTask() {
   }

   protected final boolean wasInterrupted() {
      Object var1 = this.value;
      return var1 instanceof AbstractFuture.Cancellation && ((AbstractFuture.Cancellation)var1).wasInterrupted;
   }

   @Override
   public void addListener(Runnable var1, Executor var2) {
      Preconditions.checkNotNull(var1, "Runnable was null.");
      Preconditions.checkNotNull(var2, "Executor was null.");
      if (!this.isDone()) {
         AbstractFuture.Listener var3 = this.listeners;
         if (var3 != AbstractFuture.Listener.TOMBSTONE) {
            AbstractFuture.Listener var4 = new AbstractFuture.Listener(var1, var2);

            do {
               var4.next = var3;
               if (ATOMIC_HELPER.casListeners(this, var3, var4)) {
                  return;
               }

               var3 = this.listeners;
            } while (var3 != AbstractFuture.Listener.TOMBSTONE);
         }
      }

      executeListener(var1, var2);
   }

   @CanIgnoreReturnValue
   protected boolean set(@ParametricNullness V var1) {
      Object var2 = var1 == null ? NULL : var1;
      if (ATOMIC_HELPER.casValue(this, null, var2)) {
         complete(this);
         return true;
      } else {
         return false;
      }
   }

   @CanIgnoreReturnValue
   protected boolean setException(Throwable var1) {
      AbstractFuture.Failure var2 = new AbstractFuture.Failure(Preconditions.checkNotNull(var1));
      if (ATOMIC_HELPER.casValue(this, null, var2)) {
         complete(this);
         return true;
      } else {
         return false;
      }
   }

   @CanIgnoreReturnValue
   protected boolean setFuture(ListenableFuture<? extends V> var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.value;
      if (var2 == null) {
         if (var1.isDone()) {
            Object var9 = getFutureValue(var1);
            if (ATOMIC_HELPER.casValue(this, null, var9)) {
               complete(this);
               return true;
            }

            return false;
         }

         AbstractFuture.SetFuture var3 = new AbstractFuture.SetFuture<>(this, var1);
         if (ATOMIC_HELPER.casValue(this, null, var3)) {
            try {
               var1.addListener(var3, DirectExecutor.INSTANCE);
            } catch (Throwable var8) {
               Throwable var4 = var8;

               AbstractFuture.Failure var5;
               try {
                  var5 = new AbstractFuture.Failure(var4);
               } catch (Throwable var7) {
                  var5 = AbstractFuture.Failure.FALLBACK_INSTANCE;
               }

               boolean var6 = ATOMIC_HELPER.casValue(this, var3, var5);
            }

            return true;
         }

         var2 = this.value;
      }

      if (var2 instanceof AbstractFuture.Cancellation) {
         var1.cancel(((AbstractFuture.Cancellation)var2).wasInterrupted);
      }

      return false;
   }

   private static Object getFutureValue(ListenableFuture<?> var0) {
      if (var0 instanceof AbstractFuture.Trusted) {
         Object var8 = ((AbstractFuture)var0).value;
         if (var8 instanceof AbstractFuture.Cancellation) {
            AbstractFuture.Cancellation var9 = (AbstractFuture.Cancellation)var8;
            if (var9.wasInterrupted) {
               var8 = var9.cause != null ? new AbstractFuture.Cancellation(false, var9.cause) : AbstractFuture.Cancellation.CAUSELESS_CANCELLED;
            }
         }

         return Objects.requireNonNull(var8);
      } else {
         if (var0 instanceof InternalFutureFailureAccess) {
            Throwable var1 = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)var0);
            if (var1 != null) {
               return new AbstractFuture.Failure(var1);
            }
         }

         boolean var7 = var0.isCancelled();
         if (!GENERATE_CANCELLATION_CAUSES & var7) {
            return Objects.requireNonNull(AbstractFuture.Cancellation.CAUSELESS_CANCELLED);
         } else {
            try {
               Object var2 = getUninterruptibly(var0);
               if (var7) {
                  String var11 = String.valueOf(var0);
                  return new AbstractFuture.Cancellation(
                     false,
                     new IllegalArgumentException(
                        new StringBuilder(84 + String.valueOf(var11).length())
                           .append("get() did not throw CancellationException, despite reporting isCancelled() == true: ")
                           .append(var11)
                           .toString()
                     )
                  );
               } else {
                  return var2 == null ? NULL : var2;
               }
            } catch (ExecutionException var4) {
               if (var7) {
                  String var10 = String.valueOf(var0);
                  return new AbstractFuture.Cancellation(
                     false,
                     new IllegalArgumentException(
                        new StringBuilder(84 + String.valueOf(var10).length())
                           .append("get() did not throw CancellationException, despite reporting isCancelled() == true: ")
                           .append(var10)
                           .toString(),
                        var4
                     )
                  );
               } else {
                  return new AbstractFuture.Failure(var4.getCause());
               }
            } catch (CancellationException var5) {
               if (!var7) {
                  String var3 = String.valueOf(var0);
                  return new AbstractFuture.Failure(
                     new IllegalArgumentException(
                        new StringBuilder(77 + String.valueOf(var3).length())
                           .append("get() threw CancellationException, despite reporting isCancelled() == false: ")
                           .append(var3)
                           .toString(),
                        var5
                     )
                  );
               } else {
                  return new AbstractFuture.Cancellation(false, var5);
               }
            } catch (Throwable var6) {
               return new AbstractFuture.Failure(var6);
            }
         }
      }
   }

   @ParametricNullness
   private static <V> V getUninterruptibly(Future<V> var0) throws ExecutionException {
      boolean var1 = false;

      try {
         while (true) {
            try {
               return (V)var0.get();
            } catch (InterruptedException var6) {
               var1 = true;
            }
         }
      } finally {
         if (var1) {
            Thread.currentThread().interrupt();
         }
      }
   }

   private static void complete(AbstractFuture<?> var0) {
      AbstractFuture var1 = var0;
      AbstractFuture.Listener var2 = null;

      label23:
      while (true) {
         var1.releaseWaiters();
         var1.afterDone();
         var2 = var1.clearListeners(var2);
         Object var7 = null;

         while (var2 != null) {
            AbstractFuture.Listener var3 = var2;
            var2 = var2.next;
            Runnable var4 = Objects.requireNonNull(var3.task);
            if (var4 instanceof AbstractFuture.SetFuture) {
               AbstractFuture.SetFuture var5 = (AbstractFuture.SetFuture)var4;
               var1 = var5.owner;
               if (var1.value == var5) {
                  Object var6 = getFutureValue(var5.future);
                  if (ATOMIC_HELPER.casValue(var1, var5, var6)) {
                     continue label23;
                  }
               }
            } else {
               executeListener(var4, Objects.requireNonNull(var3.executor));
            }
         }

         return;
      }
   }

   @Beta
   @ForOverride
   protected void afterDone() {
   }

   @Override
   protected final Throwable tryInternalFastPathGetFailure() {
      if (this instanceof AbstractFuture.Trusted) {
         Object var1 = this.value;
         if (var1 instanceof AbstractFuture.Failure) {
            return ((AbstractFuture.Failure)var1).exception;
         }
      }

      return null;
   }

   final void maybePropagateCancellationTo(Future<?> var1) {
      if (var1 != null & this.isCancelled()) {
         var1.cancel(this.wasInterrupted());
      }
   }

   private void releaseWaiters() {
      AbstractFuture.Waiter var1;
      do {
         var1 = this.waiters;
      } while (!ATOMIC_HELPER.casWaiters(this, var1, AbstractFuture.Waiter.TOMBSTONE));

      for (AbstractFuture.Waiter var2 = var1; var2 != null; var2 = var2.next) {
         var2.unpark();
      }
   }

   private AbstractFuture.Listener clearListeners(AbstractFuture.Listener var1) {
      AbstractFuture.Listener var2;
      do {
         var2 = this.listeners;
      } while (!ATOMIC_HELPER.casListeners(this, var2, AbstractFuture.Listener.TOMBSTONE));

      AbstractFuture.Listener var3 = var1;

      while (var2 != null) {
         AbstractFuture.Listener var4 = var2;
         var2 = var2.next;
         var4.next = var3;
         var3 = var4;
      }

      return var3;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.getClass().getName().startsWith("com.google.common.util.concurrent.")) {
         var1.append(this.getClass().getSimpleName());
      } else {
         var1.append(this.getClass().getName());
      }

      var1.append('@').append(Integer.toHexString(System.identityHashCode(this))).append("[status=");
      if (this.isCancelled()) {
         var1.append("CANCELLED");
      } else if (this.isDone()) {
         this.addDoneString(var1);
      } else {
         this.addPendingString(var1);
      }

      return var1.append("]").toString();
   }

   protected String pendingToString() {
      if (this instanceof ScheduledFuture) {
         long var1 = ((ScheduledFuture)this).getDelay(TimeUnit.MILLISECONDS);
         return new StringBuilder(41).append("remaining delay=[").append(var1).append(" ms]").toString();
      } else {
         return null;
      }
   }

   private void addPendingString(StringBuilder var1) {
      int var2 = var1.length();
      var1.append("PENDING");
      Object var3 = this.value;
      if (var3 instanceof AbstractFuture.SetFuture) {
         var1.append(", setFuture=[");
         this.appendUserObject(var1, ((AbstractFuture.SetFuture)var3).future);
         var1.append("]");
      } else {
         String var4;
         try {
            var4 = Strings.emptyToNull(this.pendingToString());
         } catch (StackOverflowError | RuntimeException var7) {
            String var6 = String.valueOf(var7.getClass());
            var4 = new StringBuilder(38 + String.valueOf(var6).length()).append("Exception thrown from implementation: ").append(var6).toString();
         }

         if (var4 != null) {
            var1.append(", info=[").append(var4).append("]");
         }
      }

      if (this.isDone()) {
         var1.delete(var2, var1.length());
         this.addDoneString(var1);
      }
   }

   private void addDoneString(StringBuilder var1) {
      try {
         Object var2 = getUninterruptibly(this);
         var1.append("SUCCESS, result=[");
         this.appendResultObject(var1, var2);
         var1.append("]");
      } catch (ExecutionException var3) {
         var1.append("FAILURE, cause=[").append(var3.getCause()).append("]");
      } catch (CancellationException var4) {
         var1.append("CANCELLED");
      } catch (RuntimeException var5) {
         var1.append("UNKNOWN, cause=[").append(var5.getClass()).append(" thrown from get()]");
      }
   }

   private void appendResultObject(StringBuilder var1, Object var2) {
      if (var2 == null) {
         var1.append("null");
      } else if (var2 == this) {
         var1.append("this future");
      } else {
         var1.append(var2.getClass().getName()).append("@").append(Integer.toHexString(System.identityHashCode(var2)));
      }
   }

   private void appendUserObject(StringBuilder var1, Object var2) {
      try {
         if (var2 == this) {
            var1.append("this future");
         } else {
            var1.append(var2);
         }
      } catch (StackOverflowError | RuntimeException var4) {
         var1.append("Exception thrown from implementation: ").append(var4.getClass());
      }
   }

   private static void executeListener(Runnable var0, Executor var1) {
      try {
         var1.execute(var0);
      } catch (RuntimeException var5) {
         Logger var10000 = log;
         Level var10001 = Level.SEVERE;
         String var3 = String.valueOf(var0);
         String var4 = String.valueOf(var1);
         var10000.log(
            var10001,
            new StringBuilder(57 + String.valueOf(var3).length() + String.valueOf(var4).length())
               .append("RuntimeException while executing runnable ")
               .append(var3)
               .append(" with executor ")
               .append(var4)
               .toString(),
            (Throwable)var5
         );
      }
   }

   private static CancellationException cancellationExceptionWithCause(String var0, Throwable var1) {
      CancellationException var2 = new CancellationException(var0);
      var2.initCause(var1);
      return var2;
   }

   static {
      boolean var0;
      try {
         var0 = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
      } catch (SecurityException var7) {
         var0 = false;
      }

      GENERATE_CANCELLATION_CAUSES = var0;
      log = Logger.getLogger(AbstractFuture.class.getName());
      Throwable var1 = null;
      Throwable var2 = null;

      try {
         var8 = new AbstractFuture.UnsafeAtomicHelper();
      } catch (Throwable var6) {
         var1 = var6;

         try {
            var8 = new AbstractFuture.SafeAtomicHelper(
               AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.Waiter.class, Thread.class, "thread"),
               AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.Waiter.class, AbstractFuture.Waiter.class, "next"),
               AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, AbstractFuture.Waiter.class, "waiters"),
               AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, AbstractFuture.Listener.class, "listeners"),
               AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value")
            );
         } catch (Throwable var5) {
            var2 = var5;
            var8 = new AbstractFuture.SynchronizedHelper();
         }
      }

      ATOMIC_HELPER = (AbstractFuture.AtomicHelper)var8;
      Class<LockSupport> var3 = LockSupport.class;
      if (var2 != null) {
         log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", var1);
         log.log(Level.SEVERE, "SafeAtomicHelper is broken!", var2);
      }

      NULL = new Object();
   }

   private abstract static class AtomicHelper {
      private AtomicHelper() {
      }

      abstract void putThread(AbstractFuture.Waiter var1, Thread var2);

      abstract void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2);

      abstract boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3);

      abstract boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3);

      abstract boolean casValue(AbstractFuture<?> var1, Object var2, Object var3);
   }

   private static final class Cancellation {
      static final AbstractFuture.Cancellation CAUSELESS_INTERRUPTED;
      static final AbstractFuture.Cancellation CAUSELESS_CANCELLED;
      final boolean wasInterrupted;
      final Throwable cause;

      Cancellation(boolean var1, Throwable var2) {
         this.wasInterrupted = var1;
         this.cause = var2;
      }

      static {
         if (AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
            CAUSELESS_CANCELLED = null;
            CAUSELESS_INTERRUPTED = null;
         } else {
            CAUSELESS_CANCELLED = new AbstractFuture.Cancellation(false, null);
            CAUSELESS_INTERRUPTED = new AbstractFuture.Cancellation(true, null);
         }
      }
   }

   private static final class Failure {
      static final AbstractFuture.Failure FALLBACK_INSTANCE = new AbstractFuture.Failure(new Throwable("Failure occurred while trying to finish a future.") {
         @Override
         public synchronized Throwable fillInStackTrace() {
            return this;
         }
      });
      final Throwable exception;

      Failure(Throwable var1) {
         this.exception = Preconditions.checkNotNull(var1);
      }
   }

   private static final class Listener {
      static final AbstractFuture.Listener TOMBSTONE = new AbstractFuture.Listener();
      final Runnable task;
      final Executor executor;
      AbstractFuture.Listener next;

      Listener(Runnable var1, Executor var2) {
         this.task = var1;
         this.executor = var2;
      }

      Listener() {
         this.task = null;
         this.executor = null;
      }
   }

   private static final class SafeAtomicHelper extends AbstractFuture.AtomicHelper {
      final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;

      SafeAtomicHelper(
         AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> var1,
         AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> var2,
         AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> var3,
         AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> var4,
         AtomicReferenceFieldUpdater<AbstractFuture, Object> var5
      ) {
         this.waiterThreadUpdater = var1;
         this.waiterNextUpdater = var2;
         this.waitersUpdater = var3;
         this.listenersUpdater = var4;
         this.valueUpdater = var5;
      }

      @Override
      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         this.waiterThreadUpdater.lazySet(var1, var2);
      }

      @Override
      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         this.waiterNextUpdater.lazySet(var1, var2);
      }

      @Override
      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         return this.waitersUpdater.compareAndSet(var1, var2, var3);
      }

      @Override
      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         return this.listenersUpdater.compareAndSet(var1, var2, var3);
      }

      @Override
      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         return this.valueUpdater.compareAndSet(var1, var2, var3);
      }
   }

   private static final class SetFuture<V> implements Runnable {
      final AbstractFuture<V> owner;
      final ListenableFuture<? extends V> future;

      SetFuture(AbstractFuture<V> var1, ListenableFuture<? extends V> var2) {
         this.owner = var1;
         this.future = var2;
      }

      @Override
      public void run() {
         if (this.owner.value == this) {
            Object var1 = AbstractFuture.getFutureValue(this.future);
            if (AbstractFuture.ATOMIC_HELPER.casValue(this.owner, this, var1)) {
               AbstractFuture.complete(this.owner);
            }
         }
      }
   }

   private static final class SynchronizedHelper extends AbstractFuture.AtomicHelper {
      private SynchronizedHelper() {
      }

      @Override
      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         var1.thread = var2;
      }

      @Override
      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         var1.next = var2;
      }

      @Override
      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         synchronized (var1) {
            if (var1.waiters == var2) {
               var1.waiters = var3;
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         synchronized (var1) {
            if (var1.listeners == var2) {
               var1.listeners = var3;
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         synchronized (var1) {
            if (var1.value == var2) {
               var1.value = var3;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   interface Trusted<V> extends ListenableFuture<V> {
   }

   abstract static class TrustedFuture<V> extends AbstractFuture<V> implements AbstractFuture.Trusted<V> {
      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public final V get() throws InterruptedException, ExecutionException {
         return super.get();
      }

      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public final V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
         return super.get(var1, var3);
      }

      @Override
      public final boolean isDone() {
         return super.isDone();
      }

      @Override
      public final boolean isCancelled() {
         return super.isCancelled();
      }

      @Override
      public final void addListener(Runnable var1, Executor var2) {
         super.addListener(var1, var2);
      }

      @CanIgnoreReturnValue
      @Override
      public final boolean cancel(boolean var1) {
         return super.cancel(var1);
      }
   }

   private static final class UnsafeAtomicHelper extends AbstractFuture.AtomicHelper {
      static final Unsafe UNSAFE;
      static final long LISTENERS_OFFSET;
      static final long WAITERS_OFFSET;
      static final long VALUE_OFFSET;
      static final long WAITER_THREAD_OFFSET;
      static final long WAITER_NEXT_OFFSET;

      private UnsafeAtomicHelper() {
      }

      @Override
      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         UNSAFE.putObject(var1, WAITER_THREAD_OFFSET, var2);
      }

      @Override
      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         UNSAFE.putObject(var1, WAITER_NEXT_OFFSET, var2);
      }

      @Override
      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         return UNSAFE.compareAndSwapObject(var1, WAITERS_OFFSET, var2, var3);
      }

      @Override
      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         return UNSAFE.compareAndSwapObject(var1, LISTENERS_OFFSET, var2, var3);
      }

      @Override
      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         return UNSAFE.compareAndSwapObject(var1, VALUE_OFFSET, var2, var3);
      }

      static {
         Unsafe var0 = null;

         try {
            var0 = Unsafe.getUnsafe();
         } catch (SecurityException var5) {
            try {
               var0 = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                  public Unsafe run() throws Exception {
                     Class<Unsafe> var1 = Unsafe.class;

                     for (Field var5 : var1.getDeclaredFields()) {
                        var5.setAccessible(true);
                        Object var6 = var5.get(null);
                        if (var1.isInstance(var6)) {
                           return var1.cast(var6);
                        }
                     }

                     throw new NoSuchFieldError("the Unsafe");
                  }
               });
            } catch (PrivilegedActionException var4) {
               throw new RuntimeException("Could not initialize intrinsics", var4.getCause());
            }
         }

         try {
            Class<AbstractFuture> var1 = AbstractFuture.class;
            WAITERS_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("waiters"));
            LISTENERS_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("listeners"));
            VALUE_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("value"));
            WAITER_THREAD_OFFSET = var0.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("thread"));
            WAITER_NEXT_OFFSET = var0.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("next"));
            UNSAFE = var0;
         } catch (Exception var3) {
            Throwables.throwIfUnchecked(var3);
            throw new RuntimeException(var3);
         }
      }
   }

   private static final class Waiter {
      static final AbstractFuture.Waiter TOMBSTONE = new AbstractFuture.Waiter(false);
      volatile Thread thread;
      volatile AbstractFuture.Waiter next;

      Waiter(boolean var1) {
      }

      Waiter() {
         AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
      }

      void setNext(AbstractFuture.Waiter var1) {
         AbstractFuture.ATOMIC_HELPER.putNext(this, var1);
      }

      void unpark() {
         Thread var1 = this.thread;
         if (var1 != null) {
            this.thread = null;
            LockSupport.unpark(var1);
         }
      }
   }
}
