package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public abstract class Striped<L> {
   private static final int LARGE_LAZY_CUTOFF = 1024;
   private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
      public ReadWriteLock get() {
         return new ReentrantReadWriteLock();
      }
   };
   private static final Supplier<ReadWriteLock> WEAK_SAFE_READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
      public ReadWriteLock get() {
         return new Striped.WeakSafeReadWriteLock();
      }
   };
   private static final int ALL_SET = -1;

   private Striped() {
   }

   public abstract L get(Object var1);

   public abstract L getAt(int var1);

   abstract int indexFor(Object var1);

   public abstract int size();

   public Iterable<L> bulkGet(Iterable<? extends Object> var1) {
      ArrayList var2 = Lists.newArrayList(var1);
      if (var2.isEmpty()) {
         return ImmutableList.of();
      } else {
         int[] var3 = new int[var2.size()];

         for (int var4 = 0; var4 < var2.size(); var4++) {
            var3[var4] = this.indexFor(var2.get(var4));
         }

         Arrays.sort(var3);
         int var7 = var3[0];
         var2.set(0, this.getAt(var7));

         for (int var5 = 1; var5 < var2.size(); var5++) {
            int var6 = var3[var5];
            if (var6 == var7) {
               var2.set(var5, var2.get(var5 - 1));
            } else {
               var2.set(var5, this.getAt(var6));
               var7 = var6;
            }
         }

         return Collections.unmodifiableList(var2);
      }
   }

   static <L> Striped<L> custom(int var0, Supplier<L> var1) {
      return new Striped.CompactStriped<>(var0, var1);
   }

   public static Striped<Lock> lock(int var0) {
      return custom(var0, new Supplier<Lock>() {
         public Lock get() {
            return new Striped.PaddedLock();
         }
      });
   }

   public static Striped<Lock> lazyWeakLock(int var0) {
      return lazy(var0, new Supplier<Lock>() {
         public Lock get() {
            return new ReentrantLock(false);
         }
      });
   }

   private static <L> Striped<L> lazy(int var0, Supplier<L> var1) {
      return (Striped<L>)(var0 < 1024 ? new Striped.SmallLazyStriped<>(var0, var1) : new Striped.LargeLazyStriped<>(var0, var1));
   }

   public static Striped<Semaphore> semaphore(int var0, final int var1) {
      return custom(var0, new Supplier<Semaphore>() {
         public Semaphore get() {
            return new Striped.PaddedSemaphore(var1);
         }
      });
   }

   public static Striped<Semaphore> lazyWeakSemaphore(int var0, final int var1) {
      return lazy(var0, new Supplier<Semaphore>() {
         public Semaphore get() {
            return new Semaphore(var1, false);
         }
      });
   }

   public static Striped<ReadWriteLock> readWriteLock(int var0) {
      return custom(var0, READ_WRITE_LOCK_SUPPLIER);
   }

   public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int var0) {
      return lazy(var0, WEAK_SAFE_READ_WRITE_LOCK_SUPPLIER);
   }

   private static int ceilToPowerOfTwo(int var0) {
      return 1 << IntMath.log2(var0, RoundingMode.CEILING);
   }

   private static int smear(int var0) {
      var0 ^= var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   private static class CompactStriped<L> extends Striped.PowerOfTwoStriped<L> {
      private final Object[] array;

      private CompactStriped(int var1, Supplier<L> var2) {
         super(var1);
         Preconditions.checkArgument(var1 <= 1073741824, "Stripes must be <= 2^30)");
         this.array = new Object[this.mask + 1];

         for (int var3 = 0; var3 < this.array.length; var3++) {
            this.array[var3] = var2.get();
         }
      }

      @Override
      public L getAt(int var1) {
         return (L)this.array[var1];
      }

      @Override
      public int size() {
         return this.array.length;
      }
   }

   @VisibleForTesting
   static class LargeLazyStriped<L> extends Striped.PowerOfTwoStriped<L> {
      final ConcurrentMap<Integer, L> locks;
      final Supplier<L> supplier;
      final int size = this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1;

      LargeLazyStriped(int var1, Supplier<L> var2) {
         super(var1);
         this.supplier = var2;
         this.locks = new MapMaker().weakValues().makeMap();
      }

      @Override
      public L getAt(int var1) {
         if (this.size != Integer.MAX_VALUE) {
            Preconditions.checkElementIndex(var1, this.size());
         }

         Object var2 = this.locks.get(Integer.valueOf(var1));
         if (var2 != null) {
            return (L)var2;
         } else {
            Object var3 = this.supplier.get();
            var2 = this.locks.putIfAbsent(var1, (L)var3);
            return MoreObjects.firstNonNull((L)var2, (L)var3);
         }
      }

      @Override
      public int size() {
         return this.size;
      }
   }

   private static class PaddedLock extends ReentrantLock {
      long unused1;
      long unused2;
      long unused3;

      PaddedLock() {
         super(false);
      }
   }

   private static class PaddedSemaphore extends Semaphore {
      long unused1;
      long unused2;
      long unused3;

      PaddedSemaphore(int var1) {
         super(var1, false);
      }
   }

   private abstract static class PowerOfTwoStriped<L> extends Striped<L> {
      final int mask;

      PowerOfTwoStriped(int var1) {
         Preconditions.checkArgument(var1 > 0, "Stripes must be positive");
         this.mask = var1 > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(var1) - 1;
      }

      @Override
      final int indexFor(Object var1) {
         int var2 = Striped.smear(var1.hashCode());
         return var2 & this.mask;
      }

      @Override
      public final L get(Object var1) {
         return this.getAt(this.indexFor(var1));
      }
   }

   @VisibleForTesting
   static class SmallLazyStriped<L> extends Striped.PowerOfTwoStriped<L> {
      final AtomicReferenceArray<Striped.SmallLazyStriped.ArrayReference<? extends L>> locks;
      final Supplier<L> supplier;
      final int size;
      final ReferenceQueue<L> queue = new ReferenceQueue<>();

      SmallLazyStriped(int var1, Supplier<L> var2) {
         super(var1);
         this.size = this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1;
         this.locks = new AtomicReferenceArray<>(this.size);
         this.supplier = var2;
      }

      @Override
      public L getAt(int var1) {
         if (this.size != Integer.MAX_VALUE) {
            Preconditions.checkElementIndex(var1, this.size());
         }

         Striped.SmallLazyStriped.ArrayReference var2 = this.locks.get(var1);
         Object var3 = var2 == null ? null : var2.get();
         if (var3 != null) {
            return (L)var3;
         } else {
            Object var4 = this.supplier.get();
            Striped.SmallLazyStriped.ArrayReference var5 = new Striped.SmallLazyStriped.ArrayReference<>(var4, var1, this.queue);

            while (!this.locks.compareAndSet(var1, var2, var5)) {
               var2 = this.locks.get(var1);
               var3 = var2 == null ? null : var2.get();
               if (var3 != null) {
                  return (L)var3;
               }
            }

            this.drainQueue();
            return (L)var4;
         }
      }

      private void drainQueue() {
         Reference var1;
         while ((var1 = this.queue.poll()) != null) {
            Striped.SmallLazyStriped.ArrayReference var2 = (Striped.SmallLazyStriped.ArrayReference)var1;
            this.locks.compareAndSet(var2.index, var2, null);
         }
      }

      @Override
      public int size() {
         return this.size;
      }

      private static final class ArrayReference<L> extends WeakReference<L> {
         final int index;

         ArrayReference(L var1, int var2, ReferenceQueue<L> var3) {
            super((L)var1, var3);
            this.index = var2;
         }
      }
   }

   private static final class WeakSafeCondition extends ForwardingCondition {
      private final Condition delegate;
      private final Striped.WeakSafeReadWriteLock strongReference;

      WeakSafeCondition(Condition var1, Striped.WeakSafeReadWriteLock var2) {
         this.delegate = var1;
         this.strongReference = var2;
      }

      @Override
      Condition delegate() {
         return this.delegate;
      }
   }

   private static final class WeakSafeLock extends ForwardingLock {
      private final Lock delegate;
      private final Striped.WeakSafeReadWriteLock strongReference;

      WeakSafeLock(Lock var1, Striped.WeakSafeReadWriteLock var2) {
         this.delegate = var1;
         this.strongReference = var2;
      }

      @Override
      Lock delegate() {
         return this.delegate;
      }

      @Override
      public Condition newCondition() {
         return new Striped.WeakSafeCondition(this.delegate.newCondition(), this.strongReference);
      }
   }

   private static final class WeakSafeReadWriteLock implements ReadWriteLock {
      private final ReadWriteLock delegate = new ReentrantReadWriteLock();

      WeakSafeReadWriteLock() {
      }

      @Override
      public Lock readLock() {
         return new Striped.WeakSafeLock(this.delegate.readLock(), this);
      }

      @Override
      public Lock writeLock() {
         return new Striped.WeakSafeLock(this.delegate.writeLock(), this);
      }
   }
}
