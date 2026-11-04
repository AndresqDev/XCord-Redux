package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Set;

@DoNotMock("Use ImmutableRangeSet or TreeRangeSet")
@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public interface RangeSet<C extends Comparable> {
   boolean contains(C var1);

   Range<C> rangeContaining(C var1);

   boolean intersects(Range<C> var1);

   boolean encloses(Range<C> var1);

   boolean enclosesAll(RangeSet<C> var1);

   default boolean enclosesAll(Iterable<Range<C>> var1) {
      for (Range var3 : var1) {
         if (!this.encloses(var3)) {
            return false;
         }
      }

      return true;
   }

   boolean isEmpty();

   Range<C> span();

   Set<Range<C>> asRanges();

   Set<Range<C>> asDescendingSetOfRanges();

   RangeSet<C> complement();

   RangeSet<C> subRangeSet(Range<C> var1);

   void add(Range<C> var1);

   void remove(Range<C> var1);

   void clear();

   void addAll(RangeSet<C> var1);

   default void addAll(Iterable<Range<C>> var1) {
      for (Range var3 : var1) {
         this.add(var3);
      }
   }

   void removeAll(RangeSet<C> var1);

   default void removeAll(Iterable<Range<C>> var1) {
      for (Range var3 : var1) {
         this.remove(var3);
      }
   }

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   @Override
   String toString();
}
