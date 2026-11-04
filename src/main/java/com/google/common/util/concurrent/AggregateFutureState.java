package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import com.google.j2objc.annotations.ReflectionSupport;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class AggregateFutureState<OutputT> extends AbstractFuture.TrustedFuture<OutputT> {
   private volatile Set<Throwable> seenExceptions = null;
   private volatile int remaining;
   private static final AggregateFutureState.AtomicHelper ATOMIC_HELPER;
   private static final Logger log = Logger.getLogger(AggregateFutureState.class.getName());

   AggregateFutureState(int var1) {
      this.remaining = var1;
   }

   final Set<Throwable> getOrInitSeenExceptions() {
      Set var1 = this.seenExceptions;
      if (var1 == null) {
         var1 = Sets.newConcurrentHashSet();
         this.addInitialException(var1);
         ATOMIC_HELPER.compareAndSetSeenExceptions(this, null, var1);
         var1 = Objects.requireNonNull(this.seenExceptions);
      }

      return var1;
   }

   abstract void addInitialException(Set<Throwable> var1);

   final int decrementRemainingAndGet() {
      return ATOMIC_HELPER.decrementAndGetRemainingCount(this);
   }

   final void clearSeenExceptions() {
      this.seenExceptions = null;
   }

   static {
      Throwable var1 = null;

      Object var0;
      try {
         var0 = new AggregateFutureState.SafeAtomicHelper(
            AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions"),
            AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining")
         );
      } catch (Throwable var3) {
         var1 = var3;
         var0 = new AggregateFutureState.SynchronizedAtomicHelper();
      }

      ATOMIC_HELPER = (AggregateFutureState.AtomicHelper)var0;
      if (var1 != null) {
         log.log(Level.SEVERE, "SafeAtomicHelper is broken!", var1);
      }
   }

   private abstract static class AtomicHelper {
      private AtomicHelper() {
      }

      abstract void compareAndSetSeenExceptions(AggregateFutureState<?> var1, Set<Throwable> var2, Set<Throwable> var3);

      abstract int decrementAndGetRemainingCount(AggregateFutureState<?> var1);
   }

   private static final class SafeAtomicHelper extends AggregateFutureState.AtomicHelper {
      final AtomicReferenceFieldUpdater<AggregateFutureState<?>, Set<Throwable>> seenExceptionsUpdater;
      final AtomicIntegerFieldUpdater<AggregateFutureState<?>> remainingCountUpdater;

      SafeAtomicHelper(AtomicReferenceFieldUpdater var1, AtomicIntegerFieldUpdater var2) {
         this.seenExceptionsUpdater = var1;
         this.remainingCountUpdater = var2;
      }

      @Override
      void compareAndSetSeenExceptions(AggregateFutureState<?> var1, Set<Throwable> var2, Set<Throwable> var3) {
         this.seenExceptionsUpdater.compareAndSet(var1, var2, var3);
      }

      @Override
      int decrementAndGetRemainingCount(AggregateFutureState<?> var1) {
         return this.remainingCountUpdater.decrementAndGet(var1);
      }
   }

   private static final class SynchronizedAtomicHelper extends AggregateFutureState.AtomicHelper {
      private SynchronizedAtomicHelper() {
      }

      @Override
      void compareAndSetSeenExceptions(AggregateFutureState<?> var1, Set<Throwable> var2, Set<Throwable> var3) {
         synchronized (var1) {
            if (var1.seenExceptions == var2) {
               var1.seenExceptions = var3;
            }
         }
      }

      @Override
      int decrementAndGetRemainingCount(AggregateFutureState<?> var1) {
         synchronized (var1) {
            return --var1.remaining;
         }
      }
   }
}
