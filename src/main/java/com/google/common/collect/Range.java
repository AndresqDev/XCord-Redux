package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Range<C extends Comparable> extends RangeGwtSerializationDependencies implements Predicate<C>, Serializable {
   private static final Range<Comparable> ALL = new Range<>(Cut.belowAll(), Cut.aboveAll());
   final Cut<C> lowerBound;
   final Cut<C> upperBound;
   private static final long serialVersionUID = 0L;

   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn() {
      return Range.LowerBoundFn.INSTANCE;
   }

   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn() {
      return Range.UpperBoundFn.INSTANCE;
   }

   static <C extends Comparable<?>> Ordering<Range<C>> rangeLexOrdering() {
      return (Ordering<Range<C>>)Range.RangeLexOrdering.INSTANCE;
   }

   static <C extends Comparable<?>> Range<C> create(Cut<C> var0, Cut<C> var1) {
      return new Range<>(var0, var1);
   }

   public static <C extends Comparable<?>> Range<C> open(C var0, C var1) {
      return create(Cut.aboveValue((C)var0), Cut.belowValue((C)var1));
   }

   public static <C extends Comparable<?>> Range<C> closed(C var0, C var1) {
      return create(Cut.belowValue((C)var0), Cut.aboveValue((C)var1));
   }

   public static <C extends Comparable<?>> Range<C> closedOpen(C var0, C var1) {
      return create(Cut.belowValue((C)var0), Cut.belowValue((C)var1));
   }

   public static <C extends Comparable<?>> Range<C> openClosed(C var0, C var1) {
      return create(Cut.aboveValue((C)var0), Cut.aboveValue((C)var1));
   }

   public static <C extends Comparable<?>> Range<C> range(C var0, BoundType var1, C var2, BoundType var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Cut var4 = var1 == BoundType.OPEN ? Cut.aboveValue(var0) : Cut.belowValue(var0);
      Cut var5 = var3 == BoundType.OPEN ? Cut.belowValue(var2) : Cut.aboveValue(var2);
      return create(var4, var5);
   }

   public static <C extends Comparable<?>> Range<C> lessThan(C var0) {
      return create(Cut.belowAll(), Cut.belowValue((C)var0));
   }

   public static <C extends Comparable<?>> Range<C> atMost(C var0) {
      return create(Cut.belowAll(), Cut.aboveValue((C)var0));
   }

   public static <C extends Comparable<?>> Range<C> upTo(C var0, BoundType var1) {
      switch (var1) {
         case OPEN:
            return lessThan((C)var0);
         case CLOSED:
            return atMost((C)var0);
         default:
            throw new AssertionError();
      }
   }

   public static <C extends Comparable<?>> Range<C> greaterThan(C var0) {
      return create(Cut.aboveValue((C)var0), Cut.aboveAll());
   }

   public static <C extends Comparable<?>> Range<C> atLeast(C var0) {
      return create(Cut.belowValue((C)var0), Cut.aboveAll());
   }

   public static <C extends Comparable<?>> Range<C> downTo(C var0, BoundType var1) {
      switch (var1) {
         case OPEN:
            return greaterThan((C)var0);
         case CLOSED:
            return atLeast((C)var0);
         default:
            throw new AssertionError();
      }
   }

   public static <C extends Comparable<?>> Range<C> all() {
      return (Range<C>)ALL;
   }

   public static <C extends Comparable<?>> Range<C> singleton(C var0) {
      return closed((C)var0, (C)var0);
   }

   public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof SortedSet) {
         SortedSet var1 = (SortedSet)var0;
         Comparator var2 = var1.comparator();
         if (Ordering.natural().equals(var2) || var2 == null) {
            return closed((C)var1.first(), (C)var1.last());
         }
      }

      Iterator var5 = var0.iterator();
      Comparable var6 = Preconditions.checkNotNull((Comparable)var5.next());
      Comparable var3 = var6;

      while (var5.hasNext()) {
         Comparable var4 = Preconditions.checkNotNull((Comparable)var5.next());
         var6 = Ordering.natural().min(var6, var4);
         var3 = Ordering.natural().max(var3, var4);
      }

      return closed((C)var6, (C)var3);
   }

   private Range(Cut<C> var1, Cut<C> var2) {
      this.lowerBound = Preconditions.checkNotNull(var1);
      this.upperBound = Preconditions.checkNotNull(var2);
      if (var1.compareTo(var2) > 0 || var1 == Cut.aboveAll() || var2 == Cut.belowAll()) {
         String var10003 = String.valueOf(toString(var1, var2));
         throw new IllegalArgumentException(var10003.length() != 0 ? "Invalid range: ".concat(var10003) : new String("Invalid range: "));
      }
   }

   public boolean hasLowerBound() {
      return this.lowerBound != Cut.belowAll();
   }

   public C lowerEndpoint() {
      return this.lowerBound.endpoint();
   }

   public BoundType lowerBoundType() {
      return this.lowerBound.typeAsLowerBound();
   }

   public boolean hasUpperBound() {
      return this.upperBound != Cut.aboveAll();
   }

   public C upperEndpoint() {
      return this.upperBound.endpoint();
   }

   public BoundType upperBoundType() {
      return this.upperBound.typeAsUpperBound();
   }

   public boolean isEmpty() {
      return this.lowerBound.equals(this.upperBound);
   }

   public boolean contains(C var1) {
      Preconditions.checkNotNull(var1);
      return this.lowerBound.isLessThan((C)var1) && !this.upperBound.isLessThan((C)var1);
   }

   @Deprecated
   public boolean apply(C var1) {
      return this.contains((C)var1);
   }

   public boolean containsAll(Iterable<? extends C> var1) {
      if (Iterables.isEmpty(var1)) {
         return true;
      } else {
         if (var1 instanceof SortedSet) {
            SortedSet var2 = (SortedSet)var1;
            Comparator var3 = var2.comparator();
            if (Ordering.natural().equals(var3) || var3 == null) {
               return this.contains((C)var2.first()) && this.contains((C)var2.last());
            }
         }

         for (Comparable var5 : var1) {
            if (!this.contains((C)var5)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean encloses(Range<C> var1) {
      return this.lowerBound.compareTo(var1.lowerBound) <= 0 && this.upperBound.compareTo(var1.upperBound) >= 0;
   }

   public boolean isConnected(Range<C> var1) {
      return this.lowerBound.compareTo(var1.upperBound) <= 0 && var1.lowerBound.compareTo(this.upperBound) <= 0;
   }

   public Range<C> intersection(Range<C> var1) {
      int var2 = this.lowerBound.compareTo(var1.lowerBound);
      int var3 = this.upperBound.compareTo(var1.upperBound);
      if (var2 >= 0 && var3 <= 0) {
         return this;
      } else if (var2 <= 0 && var3 >= 0) {
         return var1;
      } else {
         Cut var4 = var2 >= 0 ? this.lowerBound : var1.lowerBound;
         Cut var5 = var3 <= 0 ? this.upperBound : var1.upperBound;
         return create(var4, var5);
      }
   }

   public Range<C> gap(Range<C> var1) {
      if (this.lowerBound.compareTo(var1.upperBound) < 0 && var1.lowerBound.compareTo(this.upperBound) < 0) {
         String var5 = String.valueOf(this);
         String var6 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(39 + String.valueOf(var5).length() + String.valueOf(var6).length())
               .append("Ranges have a nonempty intersection: ")
               .append(var5)
               .append(", ")
               .append(var6)
               .toString()
         );
      } else {
         boolean var2 = this.lowerBound.compareTo(var1.lowerBound) < 0;
         Range var3 = var2 ? this : var1;
         Range var4 = var2 ? var1 : this;
         return create(var3.upperBound, var4.lowerBound);
      }
   }

   public Range<C> span(Range<C> var1) {
      int var2 = this.lowerBound.compareTo(var1.lowerBound);
      int var3 = this.upperBound.compareTo(var1.upperBound);
      if (var2 <= 0 && var3 >= 0) {
         return this;
      } else if (var2 >= 0 && var3 <= 0) {
         return var1;
      } else {
         Cut var4 = var2 <= 0 ? this.lowerBound : var1.lowerBound;
         Cut var5 = var3 >= 0 ? this.upperBound : var1.upperBound;
         return create(var4, var5);
      }
   }

   public Range<C> canonical(DiscreteDomain<C> var1) {
      Preconditions.checkNotNull(var1);
      Cut var2 = this.lowerBound.canonical(var1);
      Cut var3 = this.upperBound.canonical(var1);
      return var2 == this.lowerBound && var3 == this.upperBound ? this : create(var2, var3);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Range)) {
         return false;
      } else {
         Range var2 = (Range)var1;
         return this.lowerBound.equals(var2.lowerBound) && this.upperBound.equals(var2.upperBound);
      }
   }

   @Override
   public int hashCode() {
      return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
   }

   @Override
   public String toString() {
      return toString(this.lowerBound, this.upperBound);
   }

   private static String toString(Cut<?> var0, Cut<?> var1) {
      StringBuilder var2 = new StringBuilder(16);
      var0.describeAsLowerBound(var2);
      var2.append("..");
      var1.describeAsUpperBound(var2);
      return var2.toString();
   }

   Object readResolve() {
      return this.equals(ALL) ? all() : this;
   }

   static int compareOrThrow(Comparable var0, Comparable var1) {
      return var0.compareTo(var1);
   }

   static class LowerBoundFn implements Function<Range, Cut> {
      static final Range.LowerBoundFn INSTANCE = new Range.LowerBoundFn();

      public Cut apply(Range var1) {
         return var1.lowerBound;
      }
   }

   private static class RangeLexOrdering extends Ordering<Range<?>> implements Serializable {
      static final Ordering<Range<?>> INSTANCE = new Range.RangeLexOrdering();
      private static final long serialVersionUID = 0L;

      public int compare(Range<?> var1, Range<?> var2) {
         return ComparisonChain.start().compare(var1.lowerBound, var2.lowerBound).compare(var1.upperBound, var2.upperBound).result();
      }
   }

   static class UpperBoundFn implements Function<Range, Cut> {
      static final Range.UpperBoundFn INSTANCE = new Range.UpperBoundFn();

      public Cut apply(Range var1) {
         return var1.upperBound;
      }
   }
}
