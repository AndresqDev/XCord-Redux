package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E> implements SortedMultiset<E> {
   @LazyInit
   transient ImmutableSortedMultiset<E> descendingMultiset;

   public static <E> Collector<E, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(Comparator<? super E> var0) {
      return toImmutableSortedMultiset(var0, Function.identity(), var0x -> 1);
   }

   public static <T, E> Collector<T, ?, ImmutableSortedMultiset<E>> toImmutableSortedMultiset(
      Comparator<? super E> var0, Function<? super T, ? extends E> var1, ToIntFunction<? super T> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(
         () -> TreeMultiset.create(var0), (var2x, var3) -> var2x.add(Preconditions.checkNotNull(var1.apply(var3)), var2.applyAsInt(var3)), (var0x, var1x) -> {
            var0x.addAll(var1x);
            return var0x;
         }, var1x -> copyOfSortedEntries(var0, var1x.entrySet())
      );
   }

   public static <E> ImmutableSortedMultiset<E> of() {
      return (ImmutableSortedMultiset<E>)RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0) {
      RegularImmutableSortedSet var1 = (RegularImmutableSortedSet)ImmutableSortedSet.<E>of((E)var0);
      long[] var2 = new long[]{0L, 1L};
      return new RegularImmutableSortedMultiset<>(var1, var2, 0, 1);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1) {
      return copyOf(Ordering.natural(), Arrays.asList((E[])(new Comparable[]{var0, var1})));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2) {
      return copyOf(Ordering.natural(), Arrays.asList((E[])(new Comparable[]{var0, var1, var2})));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3) {
      return copyOf(Ordering.natural(), Arrays.asList((E[])(new Comparable[]{var0, var1, var2, var3})));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4) {
      return copyOf(Ordering.natural(), Arrays.asList((E[])(new Comparable[]{var0, var1, var2, var3, var4})));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      int var7 = var6.length + 6;
      ArrayList var8 = Lists.newArrayListWithCapacity(var7);
      Collections.addAll(var8, (E[])(new Comparable[]{var0, var1, var2, var3, var4, var5}));
      Collections.addAll(var8, (E[])var6);
      return copyOf(Ordering.natural(), var8);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] var0) {
      return copyOf(Ordering.natural(), Arrays.asList((E[])var0));
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, var0);
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, var0);
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> var0, Iterator<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      return new ImmutableSortedMultiset.Builder<E>(var0).addAll(var1).build();
   }

   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> var0, Iterable<? extends E> var1) {
      if (var1 instanceof ImmutableSortedMultiset) {
         ImmutableSortedMultiset var2 = (ImmutableSortedMultiset)var1;
         if (var0.equals(var2.comparator())) {
            if (var2.isPartialView()) {
               return copyOfSortedEntries(var0, var2.entrySet().asList());
            }

            return var2;
         }
      }

      ArrayList var3 = Lists.newArrayList(var1);
      TreeMultiset var4 = TreeMultiset.create(Preconditions.checkNotNull(var0));
      Iterables.addAll(var4, var3);
      return copyOfSortedEntries(var0, var4.entrySet());
   }

   public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> var0) {
      return copyOfSortedEntries(var0.comparator(), Lists.newArrayList(var0.entrySet()));
   }

   private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> var0, Collection<Multiset.Entry<E>> var1) {
      if (var1.isEmpty()) {
         return emptyMultiset(var0);
      } else {
         ImmutableList.Builder var2 = new ImmutableList.Builder(var1.size());
         long[] var3 = new long[var1.size() + 1];
         int var4 = 0;

         for (Multiset.Entry var6 : var1) {
            var2.add(var6.getElement());
            var3[var4 + 1] = var3[var4] + (long)var6.getCount();
            var4++;
         }

         return new RegularImmutableSortedMultiset<>(new RegularImmutableSortedSet<>(var2.build(), var0), var3, 0, var1.size());
      }
   }

   static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> var0) {
      return (ImmutableSortedMultiset<E>)(Ordering.natural().equals(var0)
         ? RegularImmutableSortedMultiset.NATURAL_EMPTY_MULTISET
         : new RegularImmutableSortedMultiset<>(var0));
   }

   ImmutableSortedMultiset() {
   }

   @Override
   public final Comparator<? super E> comparator() {
      return this.elementSet().comparator();
   }

   public abstract ImmutableSortedSet<E> elementSet();

   public ImmutableSortedMultiset<E> descendingMultiset() {
      ImmutableSortedMultiset var1 = this.descendingMultiset;
      return var1 == null
         ? (
            this.descendingMultiset = (ImmutableSortedMultiset<E>)(this.isEmpty()
               ? emptyMultiset(Ordering.from(this.comparator()).reverse())
               : new DescendingImmutableSortedMultiset<>(this))
         )
         : var1;
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final Multiset.Entry<E> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final Multiset.Entry<E> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2);

   public ImmutableSortedMultiset<E> subMultiset(E var1, BoundType var2, E var3, BoundType var4) {
      Preconditions.checkArgument(this.comparator().compare((E)var1, (E)var3) <= 0, "Expected lowerBound <= upperBound but %s > %s", var1, var3);
      return this.tailMultiset((E)var1, var2).headMultiset((E)var3, var4);
   }

   public abstract ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2);

   public static <E> ImmutableSortedMultiset.Builder<E> orderedBy(Comparator<E> var0) {
      return new ImmutableSortedMultiset.Builder<>(var0);
   }

   public static <E extends Comparable<?>> ImmutableSortedMultiset.Builder<E> reverseOrder() {
      return new ImmutableSortedMultiset.Builder<>(Ordering.natural().reverse());
   }

   public static <E extends Comparable<?>> ImmutableSortedMultiset.Builder<E> naturalOrder() {
      return new ImmutableSortedMultiset.Builder<>(Ordering.natural());
   }

   @Override
   Object writeReplace() {
      return new ImmutableSortedMultiset.SerializedForm<>(this);
   }

   public static class Builder<E> extends ImmutableMultiset.Builder<E> {
      public Builder(Comparator<? super E> var1) {
         super(TreeMultiset.create(Preconditions.checkNotNull(var1)));
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> add(E var1) {
         super.add((E)var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> add(E... var1) {
         super.add((E[])var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addCopies(E var1, int var2) {
         super.addCopies((E)var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> setCount(E var1, int var2) {
         super.setCount((E)var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMultiset.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      public ImmutableSortedMultiset<E> build() {
         return ImmutableSortedMultiset.copyOfSorted((SortedMultiset<E>)this.contents);
      }
   }

   private static final class SerializedForm<E> implements Serializable {
      final Comparator<? super E> comparator;
      final E[] elements;
      final int[] counts;

      SerializedForm(SortedMultiset<E> var1) {
         this.comparator = var1.comparator();
         int var2 = var1.entrySet().size();
         this.elements = (E[])(new Object[var2]);
         this.counts = new int[var2];
         int var3 = 0;

         for (Multiset.Entry var5 : var1.entrySet()) {
            this.elements[var3] = (E)var5.getElement();
            this.counts[var3] = var5.getCount();
            var3++;
         }
      }

      Object readResolve() {
         int var1 = this.elements.length;
         ImmutableSortedMultiset.Builder var2 = new ImmutableSortedMultiset.Builder<>(this.comparator);

         for (int var3 = 0; var3 < var1; var3++) {
            var2.addCopies(this.elements[var3], this.counts[var3]);
         }

         return var2.build();
      }
   }
}
