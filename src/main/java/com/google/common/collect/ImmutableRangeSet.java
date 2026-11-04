package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class ImmutableRangeSet<C extends Comparable> extends AbstractRangeSet<C> implements Serializable {
   private static final ImmutableRangeSet<Comparable<?>> EMPTY = new ImmutableRangeSet<>(ImmutableList.of());
   private static final ImmutableRangeSet<Comparable<?>> ALL = new ImmutableRangeSet<>(ImmutableList.of(Range.all()));
   private final transient ImmutableList<Range<C>> ranges;
   @LazyInit
   private transient ImmutableRangeSet<C> complement;

   public static <E extends Comparable<? super E>> Collector<Range<E>, ?, ImmutableRangeSet<E>> toImmutableRangeSet() {
      return CollectCollectors.toImmutableRangeSet();
   }

   public static <C extends Comparable> ImmutableRangeSet<C> of() {
      return EMPTY;
   }

   public static <C extends Comparable> ImmutableRangeSet<C> of(Range<C> var0) {
      Preconditions.checkNotNull(var0);
      if (var0.isEmpty()) {
         return of();
      } else {
         return var0.equals(Range.all()) ? all() : new ImmutableRangeSet<>(ImmutableList.of(var0));
      }
   }

   static <C extends Comparable> ImmutableRangeSet<C> all() {
      return ALL;
   }

   public static <C extends Comparable> ImmutableRangeSet<C> copyOf(RangeSet<C> var0) {
      Preconditions.checkNotNull(var0);
      if (var0.isEmpty()) {
         return of();
      } else if (var0.encloses(Range.all())) {
         return all();
      } else {
         if (var0 instanceof ImmutableRangeSet) {
            ImmutableRangeSet var1 = (ImmutableRangeSet)var0;
            if (!var1.isPartialView()) {
               return var1;
            }
         }

         return new ImmutableRangeSet<>(ImmutableList.copyOf(var0.asRanges()));
      }
   }

   public static <C extends Comparable<?>> ImmutableRangeSet<C> copyOf(Iterable<Range<C>> var0) {
      return new ImmutableRangeSet.Builder<C>().addAll(var0).build();
   }

   public static <C extends Comparable<?>> ImmutableRangeSet<C> unionOf(Iterable<Range<C>> var0) {
      return copyOf(TreeRangeSet.create(var0));
   }

   ImmutableRangeSet(ImmutableList<Range<C>> var1) {
      this.ranges = var1;
   }

   private ImmutableRangeSet(ImmutableList<Range<C>> var1, ImmutableRangeSet<C> var2) {
      this.ranges = var1;
      this.complement = var2;
   }

   @Override
   public boolean intersects(Range<C> var1) {
      int var2 = SortedLists.binarySearch(
         this.ranges,
         Range.lowerBoundFn(),
         var1.lowerBound,
         Ordering.natural(),
         SortedLists.KeyPresentBehavior.ANY_PRESENT,
         SortedLists.KeyAbsentBehavior.NEXT_HIGHER
      );
      return var2 < this.ranges.size() && this.ranges.get(var2).isConnected(var1) && !this.ranges.get(var2).intersection(var1).isEmpty()
         ? true
         : var2 > 0 && this.ranges.get(var2 - 1).isConnected(var1) && !this.ranges.get(var2 - 1).intersection(var1).isEmpty();
   }

   @Override
   public boolean encloses(Range<C> var1) {
      int var2 = SortedLists.binarySearch(
         this.ranges,
         Range.lowerBoundFn(),
         var1.lowerBound,
         Ordering.natural(),
         SortedLists.KeyPresentBehavior.ANY_PRESENT,
         SortedLists.KeyAbsentBehavior.NEXT_LOWER
      );
      return var2 != -1 && this.ranges.get(var2).encloses(var1);
   }

   @Override
   public Range<C> rangeContaining(C var1) {
      int var2 = SortedLists.binarySearch(
         this.ranges,
         Range.lowerBoundFn(),
         Cut.belowValue(var1),
         Ordering.natural(),
         SortedLists.KeyPresentBehavior.ANY_PRESENT,
         SortedLists.KeyAbsentBehavior.NEXT_LOWER
      );
      if (var2 != -1) {
         Range var3 = this.ranges.get(var2);
         return var3.contains(var1) ? var3 : null;
      } else {
         return null;
      }
   }

   @Override
   public Range<C> span() {
      if (this.ranges.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return Range.create(this.ranges.get(0).lowerBound, this.ranges.get(this.ranges.size() - 1).upperBound);
      }
   }

   @Override
   public boolean isEmpty() {
      return this.ranges.isEmpty();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void add(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void addAll(RangeSet<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void addAll(Iterable<Range<C>> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void remove(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void removeAll(RangeSet<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void removeAll(Iterable<Range<C>> var1) {
      throw new UnsupportedOperationException();
   }

   public ImmutableSet<Range<C>> asRanges() {
      return (ImmutableSet<Range<C>>)(this.ranges.isEmpty() ? ImmutableSet.of() : new RegularImmutableSortedSet<>(this.ranges, Range.rangeLexOrdering()));
   }

   public ImmutableSet<Range<C>> asDescendingSetOfRanges() {
      return (ImmutableSet<Range<C>>)(this.ranges.isEmpty()
         ? ImmutableSet.of()
         : new RegularImmutableSortedSet<>(this.ranges.reverse(), Range.rangeLexOrdering().reverse()));
   }

   public ImmutableRangeSet<C> complement() {
      ImmutableRangeSet var1 = this.complement;
      if (var1 != null) {
         return var1;
      } else if (this.ranges.isEmpty()) {
         return this.complement = all();
      } else if (this.ranges.size() == 1 && this.ranges.get(0).equals(Range.all())) {
         return this.complement = of();
      } else {
         ImmutableRangeSet.ComplementRanges var2 = new ImmutableRangeSet.ComplementRanges();
         return this.complement = new ImmutableRangeSet<>(var2, this);
      }
   }

   public ImmutableRangeSet<C> union(RangeSet<C> var1) {
      return unionOf(Iterables.concat(this.asRanges(), var1.asRanges()));
   }

   public ImmutableRangeSet<C> intersection(RangeSet<C> var1) {
      TreeRangeSet var2 = TreeRangeSet.create(this);
      var2.removeAll(var1.complement());
      return copyOf(var2);
   }

   public ImmutableRangeSet<C> difference(RangeSet<C> var1) {
      TreeRangeSet var2 = TreeRangeSet.create(this);
      var2.removeAll(var1);
      return copyOf(var2);
   }

   private ImmutableList<Range<C>> intersectRanges(final Range<C> var1) {
      if (this.ranges.isEmpty() || var1.isEmpty()) {
         return ImmutableList.of();
      } else if (var1.encloses(this.span())) {
         return this.ranges;
      } else {
         final int var2;
         if (var1.hasLowerBound()) {
            var2 = SortedLists.binarySearch(
               this.ranges, Range.upperBoundFn(), var1.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER
            );
         } else {
            var2 = 0;
         }

         int var3;
         if (var1.hasUpperBound()) {
            var3 = SortedLists.binarySearch(
               this.ranges, Range.lowerBoundFn(), var1.upperBound, SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER
            );
         } else {
            var3 = this.ranges.size();
         }

         final int var4 = var3 - var2;
         return var4 == 0
            ? ImmutableList.of()
            : new ImmutableList<Range<C>>() {
               @Override
               public int size() {
                  return var4;
               }

               public Range<C> get(int var1x) {
                  Preconditions.checkElementIndex(var1x, var4);
                  return var1x != 0 && var1x != var4 - 1
                     ? ImmutableRangeSet.this.ranges.get(var1x + var2)
                     : ImmutableRangeSet.this.ranges.get(var1x + var2).intersection(var1);
               }

               @Override
               boolean isPartialView() {
                  return true;
               }
            };
      }
   }

   public ImmutableRangeSet<C> subRangeSet(Range<C> var1) {
      if (!this.isEmpty()) {
         Range var2 = this.span();
         if (var1.encloses(var2)) {
            return this;
         }

         if (var1.isConnected(var2)) {
            return new ImmutableRangeSet<>(this.intersectRanges(var1));
         }
      }

      return of();
   }

   public ImmutableSortedSet<C> asSet(DiscreteDomain<C> var1) {
      Preconditions.checkNotNull(var1);
      if (this.isEmpty()) {
         return ImmutableSortedSet.of();
      } else {
         Range var2 = this.span().canonical(var1);
         if (!var2.hasLowerBound()) {
            throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded below");
         } else {
            if (!var2.hasUpperBound()) {
               try {
                  var1.maxValue();
               } catch (NoSuchElementException var4) {
                  throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded above");
               }
            }

            return new ImmutableRangeSet.AsSet(var1);
         }
      }
   }

   boolean isPartialView() {
      return this.ranges.isPartialView();
   }

   public static <C extends Comparable<?>> ImmutableRangeSet.Builder<C> builder() {
      return new ImmutableRangeSet.Builder<>();
   }

   Object writeReplace() {
      return new ImmutableRangeSet.SerializedForm<>(this.ranges);
   }

   private final class AsSet extends ImmutableSortedSet<C> {
      private final DiscreteDomain<C> domain;
      private transient Integer size;

      AsSet(DiscreteDomain<C> var2) {
         super(Ordering.natural());
         this.domain = var2;
      }

      @Override
      public int size() {
         Integer var1 = this.size;
         if (var1 == null) {
            long var2 = 0L;

            for (Range var5 : ImmutableRangeSet.this.ranges) {
               var2 += (long)ContiguousSet.create(var5, this.domain).size();
               if (var2 >= 2147483647L) {
                  break;
               }
            }

            var1 = this.size = Ints.saturatedCast(var2);
         }

         return var1;
      }

      @Override
      public UnmodifiableIterator<C> iterator() {
         return new AbstractIterator<C>() {
            final Iterator<Range<C>> rangeItr = ImmutableRangeSet.this.ranges.iterator();
            Iterator<C> elemItr = Iterators.emptyIterator();

            protected C computeNext() {
               while (!this.elemItr.hasNext()) {
                  if (!this.rangeItr.hasNext()) {
                     return (C)this.endOfData();
                  }

                  this.elemItr = ContiguousSet.create(this.rangeItr.next(), AsSet.this.domain).iterator();
               }

               return this.elemItr.next();
            }
         };
      }

      @GwtIncompatible("NavigableSet")
      @Override
      public UnmodifiableIterator<C> descendingIterator() {
         return new AbstractIterator<C>() {
            final Iterator<Range<C>> rangeItr = ImmutableRangeSet.this.ranges.reverse().iterator();
            Iterator<C> elemItr = Iterators.emptyIterator();

            protected C computeNext() {
               while (!this.elemItr.hasNext()) {
                  if (!this.rangeItr.hasNext()) {
                     return (C)this.endOfData();
                  }

                  this.elemItr = ContiguousSet.create(this.rangeItr.next(), AsSet.this.domain).descendingIterator();
               }

               return this.elemItr.next();
            }
         };
      }

      ImmutableSortedSet<C> subSet(Range<C> var1) {
         return ImmutableRangeSet.this.subRangeSet(var1).asSet(this.domain);
      }

      ImmutableSortedSet<C> headSetImpl(C var1, boolean var2) {
         return this.subSet(Range.upTo((C)var1, BoundType.forBoolean(var2)));
      }

      ImmutableSortedSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4) {
         return !var2 && !var4 && Range.compareOrThrow(var1, var3) == 0
            ? ImmutableSortedSet.of()
            : this.subSet(Range.range((C)var1, BoundType.forBoolean(var2), (C)var3, BoundType.forBoolean(var4)));
      }

      ImmutableSortedSet<C> tailSetImpl(C var1, boolean var2) {
         return this.subSet(Range.downTo((C)var1, BoundType.forBoolean(var2)));
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            try {
               Comparable var2 = (Comparable)var1;
               return ImmutableRangeSet.this.contains(var2);
            } catch (ClassCastException var3) {
               return false;
            }
         }
      }

      @Override
      int indexOf(Object var1) {
         if (this.contains(var1)) {
            Comparable var2 = Objects.requireNonNull((Comparable)var1);
            long var3 = 0L;

            for (Range var6 : ImmutableRangeSet.this.ranges) {
               if (var6.contains(var2)) {
                  return Ints.saturatedCast(var3 + (long)ContiguousSet.create(var6, this.domain).indexOf(var2));
               }

               var3 += (long)ContiguousSet.create(var6, this.domain).size();
            }

            throw new AssertionError("impossible");
         } else {
            return -1;
         }
      }

      @Override
      ImmutableSortedSet<C> createDescendingSet() {
         return new DescendingImmutableSortedSet<>(this);
      }

      @Override
      boolean isPartialView() {
         return ImmutableRangeSet.this.ranges.isPartialView();
      }

      @Override
      public String toString() {
         return ImmutableRangeSet.this.ranges.toString();
      }

      @Override
      Object writeReplace() {
         return new ImmutableRangeSet.AsSetSerializedForm<>(ImmutableRangeSet.this.ranges, this.domain);
      }
   }

   private static class AsSetSerializedForm<C extends Comparable> implements Serializable {
      private final ImmutableList<Range<C>> ranges;
      private final DiscreteDomain<C> domain;

      AsSetSerializedForm(ImmutableList<Range<C>> var1, DiscreteDomain<C> var2) {
         this.ranges = var1;
         this.domain = var2;
      }

      Object readResolve() {
         return new ImmutableRangeSet<>(this.ranges).asSet(this.domain);
      }
   }

   public static class Builder<C extends Comparable<?>> {
      private final List<Range<C>> ranges = Lists.newArrayList();

      @CanIgnoreReturnValue
      public ImmutableRangeSet.Builder<C> add(Range<C> var1) {
         Preconditions.checkArgument(!var1.isEmpty(), "range must not be empty, but was %s", var1);
         this.ranges.add(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableRangeSet.Builder<C> addAll(RangeSet<C> var1) {
         return this.addAll(var1.asRanges());
      }

      @CanIgnoreReturnValue
      public ImmutableRangeSet.Builder<C> addAll(Iterable<Range<C>> var1) {
         for (Range var3 : var1) {
            this.add(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      ImmutableRangeSet.Builder<C> combine(ImmutableRangeSet.Builder<C> var1) {
         this.addAll(var1.ranges);
         return this;
      }

      public ImmutableRangeSet<C> build() {
         ImmutableList.Builder var1 = new ImmutableList.Builder(this.ranges.size());
         Collections.sort(this.ranges, Range.rangeLexOrdering());
         PeekingIterator var2 = Iterators.peekingIterator(this.ranges.iterator());

         while (var2.hasNext()) {
            Range var3;
            for (var3 = (Range)var2.next(); var2.hasNext(); var3 = var3.span((Range<C>)var2.next())) {
               Range var4 = (Range)var2.peek();
               if (!var3.isConnected(var4)) {
                  break;
               }

               Preconditions.checkArgument(var3.intersection(var4).isEmpty(), "Overlapping ranges not permitted but found %s overlapping %s", var3, var4);
            }

            var1.add(var3);
         }

         ImmutableList var5 = var1.build();
         if (var5.isEmpty()) {
            return ImmutableRangeSet.of();
         } else {
            return var5.size() == 1 && Iterables.getOnlyElement(var5).equals(Range.all()) ? ImmutableRangeSet.all() : new ImmutableRangeSet<>(var5);
         }
      }
   }

   private final class ComplementRanges extends ImmutableList<Range<C>> {
      private final boolean positiveBoundedBelow = ImmutableRangeSet.this.ranges.get(0).hasLowerBound();
      private final boolean positiveBoundedAbove = Iterables.getLast(ImmutableRangeSet.this.ranges).hasUpperBound();
      private final int size;

      ComplementRanges() {
         int var2 = ImmutableRangeSet.this.ranges.size() - 1;
         if (this.positiveBoundedBelow) {
            var2++;
         }

         if (this.positiveBoundedAbove) {
            var2++;
         }

         this.size = var2;
      }

      @Override
      public int size() {
         return this.size;
      }

      public Range<C> get(int var1) {
         Preconditions.checkElementIndex(var1, this.size);
         Cut var2;
         if (this.positiveBoundedBelow) {
            var2 = var1 == 0 ? Cut.belowAll() : ImmutableRangeSet.this.ranges.get(var1 - 1).upperBound;
         } else {
            var2 = ImmutableRangeSet.this.ranges.get(var1).upperBound;
         }

         Cut var3;
         if (this.positiveBoundedAbove && var1 == this.size - 1) {
            var3 = Cut.aboveAll();
         } else {
            var3 = ImmutableRangeSet.this.ranges.get(var1 + (this.positiveBoundedBelow ? 0 : 1)).lowerBound;
         }

         return Range.create(var2, var3);
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }

   private static final class SerializedForm<C extends Comparable> implements Serializable {
      private final ImmutableList<Range<C>> ranges;

      SerializedForm(ImmutableList<Range<C>> var1) {
         this.ranges = var1;
      }

      Object readResolve() {
         if (this.ranges.isEmpty()) {
            return ImmutableRangeSet.of();
         } else {
            return this.ranges.equals(ImmutableList.of(Range.all())) ? ImmutableRangeSet.all() : new ImmutableRangeSet<>(this.ranges);
         }
      }
   }
}
