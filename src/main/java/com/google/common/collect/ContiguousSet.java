package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.DoNotCall;
import java.util.NoSuchElementException;
import java.util.Objects;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C> {
   final DiscreteDomain<C> domain;

   public static <C extends Comparable> ContiguousSet<C> create(Range<C> var0, DiscreteDomain<C> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Range var2 = var0;

      try {
         if (!var0.hasLowerBound()) {
            var2 = var2.intersection(Range.atLeast((C)var1.minValue()));
         }

         if (!var0.hasUpperBound()) {
            var2 = var2.intersection(Range.atMost((C)var1.maxValue()));
         }
      } catch (NoSuchElementException var6) {
         throw new IllegalArgumentException(var6);
      }

      boolean var3;
      if (var2.isEmpty()) {
         var3 = true;
      } else {
         Comparable var4 = Objects.requireNonNull(var0.lowerBound.leastValueAbove(var1));
         Comparable var5 = Objects.requireNonNull(var0.upperBound.greatestValueBelow(var1));
         var3 = Range.compareOrThrow(var4, var5) > 0;
      }

      return (ContiguousSet<C>)(var3 ? new EmptyContiguousSet<>(var1) : new RegularContiguousSet<>(var2, var1));
   }

   @Beta
   public static ContiguousSet<Integer> closed(int var0, int var1) {
      return create(Range.closed(var0, var1), DiscreteDomain.integers());
   }

   @Beta
   public static ContiguousSet<Long> closed(long var0, long var2) {
      return create(Range.closed(var0, var2), DiscreteDomain.longs());
   }

   @Beta
   public static ContiguousSet<Integer> closedOpen(int var0, int var1) {
      return create(Range.closedOpen(var0, var1), DiscreteDomain.integers());
   }

   @Beta
   public static ContiguousSet<Long> closedOpen(long var0, long var2) {
      return create(Range.closedOpen(var0, var2), DiscreteDomain.longs());
   }

   ContiguousSet(DiscreteDomain<C> var1) {
      super(Ordering.natural());
      this.domain = var1;
   }

   public ContiguousSet<C> headSet(C var1) {
      return this.headSetImpl(Preconditions.checkNotNull((C)var1), false);
   }

   @GwtIncompatible
   public ContiguousSet<C> headSet(C var1, boolean var2) {
      return this.headSetImpl(Preconditions.checkNotNull((C)var1), var2);
   }

   public ContiguousSet<C> subSet(C var1, C var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkArgument(this.comparator().compare((C)var1, (C)var2) <= 0);
      return this.subSetImpl((C)var1, true, (C)var2, false);
   }

   @GwtIncompatible
   public ContiguousSet<C> subSet(C var1, boolean var2, C var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator().compare((C)var1, (C)var3) <= 0);
      return this.subSetImpl((C)var1, var2, (C)var3, var4);
   }

   public ContiguousSet<C> tailSet(C var1) {
      return this.tailSetImpl(Preconditions.checkNotNull((C)var1), true);
   }

   @GwtIncompatible
   public ContiguousSet<C> tailSet(C var1, boolean var2) {
      return this.tailSetImpl(Preconditions.checkNotNull((C)var1), var2);
   }

   abstract ContiguousSet<C> headSetImpl(C var1, boolean var2);

   abstract ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4);

   abstract ContiguousSet<C> tailSetImpl(C var1, boolean var2);

   public abstract ContiguousSet<C> intersection(ContiguousSet<C> var1);

   public abstract Range<C> range();

   public abstract Range<C> range(BoundType var1, BoundType var2);

   @GwtIncompatible
   @Override
   ImmutableSortedSet<C> createDescendingSet() {
      return new DescendingImmutableSortedSet<>(this);
   }

   @Override
   public String toString() {
      return this.range().toString();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   public static <E> ImmutableSortedSet.Builder<E> builder() {
      throw new UnsupportedOperationException();
   }
}
