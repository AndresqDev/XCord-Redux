package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.Comparator;
import java.util.function.ObjIntConsumer;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
   private static final long[] ZERO_CUMULATIVE_COUNTS = new long[]{0L};
   static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET = new RegularImmutableSortedMultiset<>(Ordering.natural());
   @VisibleForTesting
   final transient RegularImmutableSortedSet<E> elementSet;
   private final transient long[] cumulativeCounts;
   private final transient int offset;
   private final transient int length;

   RegularImmutableSortedMultiset(Comparator<? super E> var1) {
      this.elementSet = ImmutableSortedSet.emptySet(var1);
      this.cumulativeCounts = ZERO_CUMULATIVE_COUNTS;
      this.offset = 0;
      this.length = 0;
   }

   RegularImmutableSortedMultiset(RegularImmutableSortedSet<E> var1, long[] var2, int var3, int var4) {
      this.elementSet = var1;
      this.cumulativeCounts = var2;
      this.offset = var3;
      this.length = var4;
   }

   private int getCount(int var1) {
      return (int)(this.cumulativeCounts[this.offset + var1 + 1] - this.cumulativeCounts[this.offset + var1]);
   }

   @Override
   Multiset.Entry<E> getEntry(int var1) {
      return Multisets.immutableEntry((E)this.elementSet.asList().get(var1), this.getCount(var1));
   }

   @Override
   public void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);

      for (int var2 = 0; var2 < this.length; var2++) {
         var1.accept(this.elementSet.asList().get(var2), this.getCount(var2));
      }
   }

   @Override
   public Multiset.Entry<E> firstEntry() {
      return this.isEmpty() ? null : this.getEntry(0);
   }

   @Override
   public Multiset.Entry<E> lastEntry() {
      return this.isEmpty() ? null : this.getEntry(this.length - 1);
   }

   @Override
   public int count(Object var1) {
      int var2 = this.elementSet.indexOf(var1);
      return var2 >= 0 ? this.getCount(var2) : 0;
   }

   @Override
   public int size() {
      long var1 = this.cumulativeCounts[this.offset + this.length] - this.cumulativeCounts[this.offset];
      return Ints.saturatedCast(var1);
   }

   @Override
   public ImmutableSortedSet<E> elementSet() {
      return this.elementSet;
   }

   @Override
   public ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2) {
      return this.getSubMultiset(0, this.elementSet.headIndex((E)var1, Preconditions.checkNotNull(var2) == BoundType.CLOSED));
   }

   @Override
   public ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2) {
      return this.getSubMultiset(this.elementSet.tailIndex((E)var1, Preconditions.checkNotNull(var2) == BoundType.CLOSED), this.length);
   }

   ImmutableSortedMultiset<E> getSubMultiset(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, this.length);
      if (var1 == var2) {
         return emptyMultiset(this.comparator());
      } else if (var1 == 0 && var2 == this.length) {
         return this;
      } else {
         RegularImmutableSortedSet var3 = this.elementSet.getSubSet(var1, var2);
         return new RegularImmutableSortedMultiset<>(var3, this.cumulativeCounts, this.offset + var1, var2 - var1);
      }
   }

   @Override
   boolean isPartialView() {
      return this.offset > 0 || this.length < this.cumulativeCounts.length - 1;
   }
}
