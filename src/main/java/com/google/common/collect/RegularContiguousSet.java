package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class RegularContiguousSet<C extends Comparable> extends ContiguousSet<C> {
   private final Range<C> range;
   private static final long serialVersionUID = 0L;

   RegularContiguousSet(Range<C> var1, DiscreteDomain<C> var2) {
      super(var2);
      this.range = var1;
   }

   private ContiguousSet<C> intersectionInCurrentDomain(Range<C> var1) {
      return (ContiguousSet<C>)(this.range.isConnected(var1)
         ? ContiguousSet.create(this.range.intersection(var1), this.domain)
         : new EmptyContiguousSet<>(this.domain));
   }

   @Override
   ContiguousSet<C> headSetImpl(C var1, boolean var2) {
      return this.intersectionInCurrentDomain(Range.upTo((C)var1, BoundType.forBoolean(var2)));
   }

   @Override
   ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4) {
      return (ContiguousSet<C>)(var1.compareTo(var3) == 0 && !var2 && !var4
         ? new EmptyContiguousSet<>(this.domain)
         : this.intersectionInCurrentDomain(Range.range((C)var1, BoundType.forBoolean(var2), (C)var3, BoundType.forBoolean(var4))));
   }

   @Override
   ContiguousSet<C> tailSetImpl(C var1, boolean var2) {
      return this.intersectionInCurrentDomain(Range.downTo((C)var1, BoundType.forBoolean(var2)));
   }

   @GwtIncompatible
   @Override
   int indexOf(Object var1) {
      return this.contains(var1) ? (int)this.domain.distance(this.first(), Objects.requireNonNull((C)var1)) : -1;
   }

   @Override
   public UnmodifiableIterator<C> iterator() {
      return new AbstractSequentialIterator<C>(this.first()) {
         final C last = (C)RegularContiguousSet.this.last();

         protected C computeNext(C var1) {
            return RegularContiguousSet.equalsOrThrow(var1, this.last) ? null : RegularContiguousSet.this.domain.next((C)var1);
         }
      };
   }

   @GwtIncompatible
   @Override
   public UnmodifiableIterator<C> descendingIterator() {
      return new AbstractSequentialIterator<C>(this.last()) {
         final C first = (C)RegularContiguousSet.this.first();

         protected C computeNext(C var1) {
            return RegularContiguousSet.equalsOrThrow(var1, this.first) ? null : RegularContiguousSet.this.domain.previous((C)var1);
         }
      };
   }

   private static boolean equalsOrThrow(Comparable<?> var0, Comparable<?> var1) {
      return var1 != null && Range.compareOrThrow(var0, var1) == 0;
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   public C first() {
      return Objects.requireNonNull(this.range.lowerBound.leastValueAbove(this.domain));
   }

   public C last() {
      return Objects.requireNonNull(this.range.upperBound.greatestValueBelow(this.domain));
   }

   @Override
   ImmutableList<C> createAsList() {
      return (ImmutableList<C>)(this.domain.supportsFastOffset ? new ImmutableAsList<C>() {
         ImmutableSortedSet<C> delegateCollection() {
            return RegularContiguousSet.this;
         }

         public C get(int var1) {
            Preconditions.checkElementIndex(var1, this.size());
            return RegularContiguousSet.this.domain.offset((C)RegularContiguousSet.this.first(), (long)var1);
         }
      } : super.createAsList());
   }

   @Override
   public int size() {
      long var1 = this.domain.distance(this.first(), this.last());
      return var1 >= 2147483647L ? Integer.MAX_VALUE : (int)var1 + 1;
   }

   @Override
   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         try {
            return this.range.contains((C)var1);
         } catch (ClassCastException var3) {
            return false;
         }
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return Collections2.containsAllImpl(this, var1);
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public ContiguousSet<C> intersection(ContiguousSet<C> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this.domain.equals(var1.domain));
      if (var1.isEmpty()) {
         return var1;
      } else {
         Comparable var2 = Ordering.natural().max(this.first(), (Comparable)var1.first());
         Comparable var3 = Ordering.natural().min(this.last(), (Comparable)var1.last());
         return (ContiguousSet<C>)(var2.compareTo(var3) <= 0
            ? ContiguousSet.create(Range.closed((C)var2, (C)var3), this.domain)
            : new EmptyContiguousSet<>(this.domain));
      }
   }

   @Override
   public Range<C> range() {
      return this.range(BoundType.CLOSED, BoundType.CLOSED);
   }

   @Override
   public Range<C> range(BoundType var1, BoundType var2) {
      return Range.create(this.range.lowerBound.withLowerBoundType(var1, this.domain), this.range.upperBound.withUpperBoundType(var2, this.domain));
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 instanceof RegularContiguousSet) {
            RegularContiguousSet var2 = (RegularContiguousSet)var1;
            if (this.domain.equals(var2.domain)) {
               return this.first().equals(var2.first()) && this.last().equals(var2.last());
            }
         }

         return super.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      return Sets.hashCodeImpl(this);
   }

   @GwtIncompatible
   @Override
   Object writeReplace() {
      return new RegularContiguousSet.SerializedForm(this.range, this.domain);
   }

   @GwtIncompatible
   private static final class SerializedForm<C extends Comparable> implements Serializable {
      final Range<C> range;
      final DiscreteDomain<C> domain;

      private SerializedForm(Range<C> var1, DiscreteDomain<C> var2) {
         this.range = var1;
         this.domain = var2;
      }

      private Object readResolve() {
         return new RegularContiguousSet<>(this.range, this.domain);
      }
   }
}
