package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import java.util.NoSuchElementException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class Cut<C extends Comparable> implements Serializable, Comparable<Cut<C>> {
   final C endpoint;
   private static final long serialVersionUID = 0L;

   Cut(C var1) {
      this.endpoint = (C)var1;
   }

   abstract boolean isLessThan(C var1);

   abstract BoundType typeAsLowerBound();

   abstract BoundType typeAsUpperBound();

   abstract Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2);

   abstract Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2);

   abstract void describeAsLowerBound(StringBuilder var1);

   abstract void describeAsUpperBound(StringBuilder var1);

   abstract C leastValueAbove(DiscreteDomain<C> var1);

   abstract C greatestValueBelow(DiscreteDomain<C> var1);

   Cut<C> canonical(DiscreteDomain<C> var1) {
      return this;
   }

   public int compareTo(Cut<C> var1) {
      if (var1 == belowAll()) {
         return 1;
      } else if (var1 == aboveAll()) {
         return -1;
      } else {
         int var2 = Range.compareOrThrow(this.endpoint, var1.endpoint);
         return var2 != 0 ? var2 : Booleans.compare(this instanceof Cut.AboveValue, var1 instanceof Cut.AboveValue);
      }
   }

   C endpoint() {
      return this.endpoint;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof Cut) {
         Cut var2 = (Cut)var1;

         try {
            int var3 = this.compareTo(var2);
            return var3 == 0;
         } catch (ClassCastException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public abstract int hashCode();

   static <C extends Comparable> Cut<C> belowAll() {
      return Cut.BelowAll.INSTANCE;
   }

   static <C extends Comparable> Cut<C> aboveAll() {
      return Cut.AboveAll.INSTANCE;
   }

   static <C extends Comparable> Cut<C> belowValue(C var0) {
      return new Cut.BelowValue<>((C)var0);
   }

   static <C extends Comparable> Cut<C> aboveValue(C var0) {
      return new Cut.AboveValue<>((C)var0);
   }

   private static final class AboveAll extends Cut<Comparable<?>> {
      private static final Cut.AboveAll INSTANCE = new Cut.AboveAll();
      private static final long serialVersionUID = 0L;

      private AboveAll() {
         super("");
      }

      @Override
      Comparable<?> endpoint() {
         throw new IllegalStateException("range unbounded on this side");
      }

      @Override
      boolean isLessThan(Comparable<?> var1) {
         return false;
      }

      @Override
      BoundType typeAsLowerBound() {
         throw new AssertionError("this statement should be unreachable");
      }

      @Override
      BoundType typeAsUpperBound() {
         throw new IllegalStateException();
      }

      @Override
      Cut<Comparable<?>> withLowerBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new AssertionError("this statement should be unreachable");
      }

      @Override
      Cut<Comparable<?>> withUpperBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new IllegalStateException();
      }

      @Override
      void describeAsLowerBound(StringBuilder var1) {
         throw new AssertionError();
      }

      @Override
      void describeAsUpperBound(StringBuilder var1) {
         var1.append("+∞)");
      }

      @Override
      Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> var1) {
         throw new AssertionError();
      }

      @Override
      Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> var1) {
         return var1.maxValue();
      }

      @Override
      public int compareTo(Cut<Comparable<?>> var1) {
         return var1 == this ? 0 : 1;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this);
      }

      @Override
      public String toString() {
         return "+∞";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class AboveValue<C extends Comparable> extends Cut<C> {
      private static final long serialVersionUID = 0L;

      AboveValue(C var1) {
         super(Preconditions.checkNotNull((C)var1));
      }

      @Override
      boolean isLessThan(C var1) {
         return Range.compareOrThrow(this.endpoint, var1) < 0;
      }

      @Override
      BoundType typeAsLowerBound() {
         return BoundType.OPEN;
      }

      @Override
      BoundType typeAsUpperBound() {
         return BoundType.CLOSED;
      }

      @Override
      Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch (var1) {
            case CLOSED:
               Comparable var3 = var2.next(this.endpoint);
               return var3 == null ? Cut.belowAll() : belowValue((C)var3);
            case OPEN:
               return this;
            default:
               throw new AssertionError();
         }
      }

      @Override
      Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch (var1) {
            case CLOSED:
               return this;
            case OPEN:
               Comparable var3 = var2.next(this.endpoint);
               return var3 == null ? Cut.aboveAll() : belowValue((C)var3);
            default:
               throw new AssertionError();
         }
      }

      @Override
      void describeAsLowerBound(StringBuilder var1) {
         var1.append('(').append(this.endpoint);
      }

      @Override
      void describeAsUpperBound(StringBuilder var1) {
         var1.append(this.endpoint).append(']');
      }

      @Override
      C leastValueAbove(DiscreteDomain<C> var1) {
         return (C)var1.next(this.endpoint);
      }

      @Override
      C greatestValueBelow(DiscreteDomain<C> var1) {
         return this.endpoint;
      }

      @Override
      Cut<C> canonical(DiscreteDomain<C> var1) {
         Comparable var2 = this.leastValueAbove(var1);
         return var2 != null ? belowValue((C)var2) : Cut.aboveAll();
      }

      @Override
      public int hashCode() {
         return ~this.endpoint.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.endpoint);
         return new StringBuilder(2 + String.valueOf(var1).length()).append("/").append(var1).append("\\").toString();
      }
   }

   private static final class BelowAll extends Cut<Comparable<?>> {
      private static final Cut.BelowAll INSTANCE = new Cut.BelowAll();
      private static final long serialVersionUID = 0L;

      private BelowAll() {
         super("");
      }

      @Override
      Comparable<?> endpoint() {
         throw new IllegalStateException("range unbounded on this side");
      }

      @Override
      boolean isLessThan(Comparable<?> var1) {
         return true;
      }

      @Override
      BoundType typeAsLowerBound() {
         throw new IllegalStateException();
      }

      @Override
      BoundType typeAsUpperBound() {
         throw new AssertionError("this statement should be unreachable");
      }

      @Override
      Cut<Comparable<?>> withLowerBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new IllegalStateException();
      }

      @Override
      Cut<Comparable<?>> withUpperBoundType(BoundType var1, DiscreteDomain<Comparable<?>> var2) {
         throw new AssertionError("this statement should be unreachable");
      }

      @Override
      void describeAsLowerBound(StringBuilder var1) {
         var1.append("(-∞");
      }

      @Override
      void describeAsUpperBound(StringBuilder var1) {
         throw new AssertionError();
      }

      @Override
      Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> var1) {
         return var1.minValue();
      }

      @Override
      Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> var1) {
         throw new AssertionError();
      }

      @Override
      Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> var1) {
         try {
            return Cut.belowValue(var1.minValue());
         } catch (NoSuchElementException var3) {
            return this;
         }
      }

      @Override
      public int compareTo(Cut<Comparable<?>> var1) {
         return var1 == this ? 0 : -1;
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(this);
      }

      @Override
      public String toString() {
         return "-∞";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class BelowValue<C extends Comparable> extends Cut<C> {
      private static final long serialVersionUID = 0L;

      BelowValue(C var1) {
         super(Preconditions.checkNotNull((C)var1));
      }

      @Override
      boolean isLessThan(C var1) {
         return Range.compareOrThrow(this.endpoint, var1) <= 0;
      }

      @Override
      BoundType typeAsLowerBound() {
         return BoundType.CLOSED;
      }

      @Override
      BoundType typeAsUpperBound() {
         return BoundType.OPEN;
      }

      @Override
      Cut<C> withLowerBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch (var1) {
            case CLOSED:
               return this;
            case OPEN:
               Comparable var3 = var2.previous(this.endpoint);
               return (Cut<C>)(var3 == null ? Cut.belowAll() : new Cut.AboveValue<>((C)var3));
            default:
               throw new AssertionError();
         }
      }

      @Override
      Cut<C> withUpperBoundType(BoundType var1, DiscreteDomain<C> var2) {
         switch (var1) {
            case CLOSED:
               Comparable var3 = var2.previous(this.endpoint);
               return (Cut<C>)(var3 == null ? Cut.aboveAll() : new Cut.AboveValue<>((C)var3));
            case OPEN:
               return this;
            default:
               throw new AssertionError();
         }
      }

      @Override
      void describeAsLowerBound(StringBuilder var1) {
         var1.append('[').append(this.endpoint);
      }

      @Override
      void describeAsUpperBound(StringBuilder var1) {
         var1.append(this.endpoint).append(')');
      }

      @Override
      C leastValueAbove(DiscreteDomain<C> var1) {
         return this.endpoint;
      }

      @Override
      C greatestValueBelow(DiscreteDomain<C> var1) {
         return (C)var1.previous(this.endpoint);
      }

      @Override
      public int hashCode() {
         return this.endpoint.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.endpoint);
         return new StringBuilder(2 + String.valueOf(var1).length()).append("\\").append(var1).append("/").toString();
      }
   }
}
