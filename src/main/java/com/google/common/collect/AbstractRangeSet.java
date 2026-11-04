package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class AbstractRangeSet<C extends Comparable> implements RangeSet<C> {
   @Override
   public boolean contains(C var1) {
      return this.rangeContaining((C)var1) != null;
   }

   @Override
   public abstract Range<C> rangeContaining(C var1);

   @Override
   public boolean isEmpty() {
      return this.asRanges().isEmpty();
   }

   @Override
   public void add(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(Range<C> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      this.remove(Range.all());
   }

   @Override
   public boolean enclosesAll(RangeSet<C> var1) {
      return this.enclosesAll(var1.asRanges());
   }

   @Override
   public void addAll(RangeSet<C> var1) {
      this.addAll(var1.asRanges());
   }

   @Override
   public void removeAll(RangeSet<C> var1) {
      this.removeAll(var1.asRanges());
   }

   @Override
   public boolean intersects(Range<C> var1) {
      return !this.subRangeSet(var1).isEmpty();
   }

   @Override
   public abstract boolean encloses(Range<C> var1);

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof RangeSet) {
         RangeSet var2 = (RangeSet)var1;
         return this.asRanges().equals(var2.asRanges());
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return this.asRanges().hashCode();
   }

   @Override
   public final String toString() {
      return this.asRanges().toString();
   }
}
