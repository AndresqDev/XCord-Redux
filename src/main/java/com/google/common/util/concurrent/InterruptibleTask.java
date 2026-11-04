package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.j2objc.annotations.ReflectionSupport;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.LockSupport;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class InterruptibleTask<T> extends AtomicReference<Runnable> implements Runnable {
   private static final Runnable DONE = new InterruptibleTask.DoNothingRunnable();
   private static final Runnable PARKED = new InterruptibleTask.DoNothingRunnable();
   private static final int MAX_BUSY_WAIT_SPINS = 1000;

   @Override
   public final void run() {
      Thread var1 = Thread.currentThread();
      if (this.compareAndSet(null, var1)) {
         boolean var2 = !this.isDone();
         Object var3 = null;
         Throwable var4 = null;

         try {
            if (var2) {
               var3 = this.runInterruptibly();
            }
         } catch (Throwable var9) {
            var4 = var9;
         } finally {
            if (!this.compareAndSet(var1, DONE)) {
               this.waitForInterrupt(var1);
            }

            if (var2) {
               if (var4 == null) {
                  this.afterRanInterruptiblySuccess(NullnessCasts.uncheckedCastNullableTToT((T)var3));
               } else {
                  this.afterRanInterruptiblyFailure(var4);
               }
            }
         }
      }
   }

   private void waitForInterrupt(Thread var1) {
      boolean var2 = false;
      int var3 = 0;
      Runnable var4 = this.get();

      for (InterruptibleTask.Blocker var5 = null; var4 instanceof InterruptibleTask.Blocker || var4 == PARKED; var4 = this.get()) {
         if (var4 instanceof InterruptibleTask.Blocker) {
            var5 = (InterruptibleTask.Blocker)var4;
         }

         if (++var3 > 1000) {
            if (var4 == PARKED || this.compareAndSet(var4, PARKED)) {
               var2 = Thread.interrupted() || var2;
               LockSupport.park(var5);
            }
         } else {
            Thread.yield();
         }
      }

      if (var2) {
         var1.interrupt();
      }
   }

   abstract boolean isDone();

   @ParametricNullness
   abstract T runInterruptibly() throws Exception;

   abstract void afterRanInterruptiblySuccess(@ParametricNullness T var1);

   abstract void afterRanInterruptiblyFailure(Throwable var1);

   final void interruptTask() {
      Runnable var1 = this.get();
      if (var1 instanceof Thread) {
         InterruptibleTask.Blocker var2 = new InterruptibleTask.Blocker(this);
         var2.setOwner(Thread.currentThread());
         if (this.compareAndSet(var1, var2)) {
            try {
               ((Thread)var1).interrupt();
            } finally {
               Runnable var5 = this.getAndSet(DONE);
               if (var5 == PARKED) {
                  LockSupport.unpark((Thread)var1);
               }
            }
         }
      }
   }

   @Override
   public final String toString() {
      Runnable var1 = this.get();
      String var2;
      if (var1 == DONE) {
         var2 = "running=[DONE]";
      } else if (var1 instanceof InterruptibleTask.Blocker) {
         var2 = "running=[INTERRUPTED]";
      } else if (var1 instanceof Thread) {
         String var3 = ((Thread)var1).getName();
         var2 = new StringBuilder(21 + String.valueOf(var3).length()).append("running=[RUNNING ON ").append(var3).append("]").toString();
      } else {
         var2 = "running=[NOT STARTED YET]";
      }

      String var4 = this.toPendingString();
      return new StringBuilder(2 + String.valueOf(var2).length() + String.valueOf(var4).length()).append(var2).append(", ").append(var4).toString();
   }

   abstract String toPendingString();

   static {
      Class<LockSupport> var0 = LockSupport.class;
   }

   @VisibleForTesting
   static final class Blocker extends AbstractOwnableSynchronizer implements Runnable {
      private final InterruptibleTask<?> task;

      private Blocker(InterruptibleTask<?> var1) {
         this.task = var1;
      }

      @Override
      public void run() {
      }

      private void setOwner(Thread var1) {
         super.setExclusiveOwnerThread(var1);
      }

      @Override
      public String toString() {
         return this.task.toString();
      }
   }

   private static final class DoNothingRunnable implements Runnable {
      private DoNothingRunnable() {
      }

      @Override
      public void run() {
      }
   }
}
