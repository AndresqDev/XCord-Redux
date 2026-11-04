package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class TopKSelector<T> {
   private final int k;
   private final Comparator<? super T> comparator;
   private final T[] buffer;
   private int bufferSize;
   private T threshold;

   public static <T extends Comparable<? super T>> TopKSelector<T> least(int var0) {
      return least(var0, Ordering.natural());
   }

   public static <T> TopKSelector<T> least(int var0, Comparator<? super T> var1) {
      return new TopKSelector<>(var1, var0);
   }

   public static <T extends Comparable<? super T>> TopKSelector<T> greatest(int var0) {
      return greatest(var0, Ordering.natural());
   }

   public static <T> TopKSelector<T> greatest(int var0, Comparator<? super T> var1) {
      return new TopKSelector<>(Ordering.from(var1).reverse(), var0);
   }

   private TopKSelector(Comparator<? super T> var1, int var2) {
      this.comparator = Preconditions.checkNotNull(var1, "comparator");
      this.k = var2;
      Preconditions.checkArgument(var2 >= 0, "k (%s) must be >= 0", var2);
      Preconditions.checkArgument(var2 <= 1073741823, "k (%s) must be <= Integer.MAX_VALUE / 2", var2);
      this.buffer = (T[])(new Object[IntMath.checkedMultiply(var2, 2)]);
      this.bufferSize = 0;
      this.threshold = null;
   }

   public void offer(@ParametricNullness T var1) {
      if (this.k != 0) {
         if (this.bufferSize == 0) {
            this.buffer[0] = (T)var1;
            this.threshold = (T)var1;
            this.bufferSize = 1;
         } else if (this.bufferSize < this.k) {
            this.buffer[this.bufferSize++] = (T)var1;
            if (this.comparator.compare((T)var1, NullnessCasts.uncheckedCastNullableTToT(this.threshold)) > 0) {
               this.threshold = (T)var1;
            }
         } else if (this.comparator.compare((T)var1, NullnessCasts.uncheckedCastNullableTToT(this.threshold)) < 0) {
            this.buffer[this.bufferSize++] = (T)var1;
            if (this.bufferSize == 2 * this.k) {
               this.trim();
            }
         }
      }
   }

   private void trim() {
      int var1 = 0;
      int var2 = 2 * this.k - 1;
      int var3 = 0;
      int var4 = 0;
      int var5 = IntMath.log2(var2 - var1, RoundingMode.CEILING) * 3;

      while (var1 < var2) {
         int var6 = var1 + var2 + 1 >>> 1;
         int var7 = this.partition(var1, var2, var6);
         if (var7 > this.k) {
            var2 = var7 - 1;
         } else {
            if (var7 >= this.k) {
               break;
            }

            var1 = Math.max(var7, var1 + 1);
            var3 = var7;
         }

         if (++var4 >= var5) {
            Arrays.sort(this.buffer, var1, var2 + 1, this.comparator);
            break;
         }
      }

      this.bufferSize = this.k;
      this.threshold = NullnessCasts.uncheckedCastNullableTToT(this.buffer[var3]);

      for (int var8 = var3 + 1; var8 < this.k; var8++) {
         if (this.comparator.compare(NullnessCasts.uncheckedCastNullableTToT(this.buffer[var8]), NullnessCasts.uncheckedCastNullableTToT(this.threshold)) > 0) {
            this.threshold = this.buffer[var8];
         }
      }
   }

   private int partition(int var1, int var2, int var3) {
      Object var4 = NullnessCasts.uncheckedCastNullableTToT(this.buffer[var3]);
      this.buffer[var3] = this.buffer[var2];
      int var5 = var1;

      for (int var6 = var1; var6 < var2; var6++) {
         if (this.comparator.compare(NullnessCasts.uncheckedCastNullableTToT(this.buffer[var6]), (T)var4) < 0) {
            this.swap(var5, var6);
            var5++;
         }
      }

      this.buffer[var2] = this.buffer[var5];
      this.buffer[var5] = (T)var4;
      return var5;
   }

   private void swap(int var1, int var2) {
      Object var3 = this.buffer[var1];
      this.buffer[var1] = this.buffer[var2];
      this.buffer[var2] = (T)var3;
   }

   TopKSelector<T> combine(TopKSelector<T> var1) {
      for (int var2 = 0; var2 < var1.bufferSize; var2++) {
         this.offer(NullnessCasts.uncheckedCastNullableTToT(var1.buffer[var2]));
      }

      return this;
   }

   public void offerAll(Iterable<? extends T> var1) {
      this.offerAll(var1.iterator());
   }

   public void offerAll(Iterator<? extends T> var1) {
      while (var1.hasNext()) {
         this.offer((T)var1.next());
      }
   }

   public List<T> topK() {
      Arrays.sort(this.buffer, 0, this.bufferSize, this.comparator);
      if (this.bufferSize > this.k) {
         Arrays.fill(this.buffer, this.k, this.buffer.length, null);
         this.bufferSize = this.k;
         this.threshold = this.buffer[this.k - 1];
      }

      return Collections.unmodifiableList(Arrays.asList((T[])Arrays.copyOf(this.buffer, this.bufferSize)));
   }
}
