package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class GeneralRange<T> implements Serializable {
   private final Comparator<? super T> comparator;
   private final boolean hasLowerBound;
   private final T lowerEndpoint;
   private final BoundType lowerBoundType;
   private final boolean hasUpperBound;
   private final T upperEndpoint;
   private final BoundType upperBoundType;
   private transient GeneralRange<T> reverse;

   static <T extends Comparable> GeneralRange<T> from(Range<T> var0) {
      Comparable var1 = var0.hasLowerBound() ? var0.lowerEndpoint() : null;
      BoundType var2 = var0.hasLowerBound() ? var0.lowerBoundType() : BoundType.OPEN;
      Comparable var3 = var0.hasUpperBound() ? var0.upperEndpoint() : null;
      BoundType var4 = var0.hasUpperBound() ? var0.upperBoundType() : BoundType.OPEN;
      return new GeneralRange<>(Ordering.natural(), var0.hasLowerBound(), (T)var1, var2, var0.hasUpperBound(), (T)var3, var4);
   }

   static <T> GeneralRange<T> all(Comparator<? super T> var0) {
      return new GeneralRange<>(var0, false, null, BoundType.OPEN, false, null, BoundType.OPEN);
   }

   static <T> GeneralRange<T> downTo(Comparator<? super T> var0, @ParametricNullness T var1, BoundType var2) {
      return new GeneralRange<>(var0, true, (T)var1, var2, false, null, BoundType.OPEN);
   }

   static <T> GeneralRange<T> upTo(Comparator<? super T> var0, @ParametricNullness T var1, BoundType var2) {
      return new GeneralRange<>(var0, false, null, BoundType.OPEN, true, (T)var1, var2);
   }

   static <T> GeneralRange<T> range(Comparator<? super T> var0, @ParametricNullness T var1, BoundType var2, @ParametricNullness T var3, BoundType var4) {
      return new GeneralRange<>(var0, true, (T)var1, var2, true, (T)var3, var4);
   }

   private GeneralRange(Comparator<? super T> var1, boolean var2, T var3, BoundType var4, boolean var5, T var6, BoundType var7) {
      this.comparator = Preconditions.checkNotNull(var1);
      this.hasLowerBound = var2;
      this.hasUpperBound = var5;
      this.lowerEndpoint = (T)var3;
      this.lowerBoundType = Preconditions.checkNotNull(var4);
      this.upperEndpoint = (T)var6;
      this.upperBoundType = Preconditions.checkNotNull(var7);
      if (var2) {
         var1.compare(NullnessCasts.uncheckedCastNullableTToT(var3), NullnessCasts.uncheckedCastNullableTToT(var3));
      }

      if (var5) {
         var1.compare(NullnessCasts.uncheckedCastNullableTToT(var6), NullnessCasts.uncheckedCastNullableTToT(var6));
      }

      if (var2 && var5) {
         int var8 = var1.compare(NullnessCasts.uncheckedCastNullableTToT(var3), NullnessCasts.uncheckedCastNullableTToT(var6));
         Preconditions.checkArgument(var8 <= 0, "lowerEndpoint (%s) > upperEndpoint (%s)", var3, var6);
         if (var8 == 0) {
            Preconditions.checkArgument(var4 != BoundType.OPEN | var7 != BoundType.OPEN);
         }
      }
   }

   Comparator<? super T> comparator() {
      return this.comparator;
   }

   boolean hasLowerBound() {
      return this.hasLowerBound;
   }

   boolean hasUpperBound() {
      return this.hasUpperBound;
   }

   boolean isEmpty() {
      return this.hasUpperBound() && this.tooLow(NullnessCasts.uncheckedCastNullableTToT(this.getUpperEndpoint()))
         || this.hasLowerBound() && this.tooHigh(NullnessCasts.uncheckedCastNullableTToT(this.getLowerEndpoint()));
   }

   boolean tooLow(@ParametricNullness T var1) {
      if (!this.hasLowerBound()) {
         return false;
      } else {
         Object var2 = NullnessCasts.uncheckedCastNullableTToT(this.getLowerEndpoint());
         int var3 = this.comparator.compare((T)var1, (T)var2);
         return var3 < 0 | var3 == 0 & this.getLowerBoundType() == BoundType.OPEN;
      }
   }

   boolean tooHigh(@ParametricNullness T var1) {
      if (!this.hasUpperBound()) {
         return false;
      } else {
         Object var2 = NullnessCasts.uncheckedCastNullableTToT(this.getUpperEndpoint());
         int var3 = this.comparator.compare((T)var1, (T)var2);
         return var3 > 0 | var3 == 0 & this.getUpperBoundType() == BoundType.OPEN;
      }
   }

   boolean contains(@ParametricNullness T var1) {
      return !this.tooLow((T)var1) && !this.tooHigh((T)var1);
   }

   GeneralRange<T> intersect(GeneralRange<T> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this.comparator.equals(var1.comparator));
      boolean var2 = this.hasLowerBound;
      Object var3 = this.getLowerEndpoint();
      BoundType var4 = this.getLowerBoundType();
      if (!this.hasLowerBound()) {
         var2 = var1.hasLowerBound;
         var3 = var1.getLowerEndpoint();
         var4 = var1.getLowerBoundType();
      } else if (var1.hasLowerBound()) {
         int var5 = this.comparator.compare(this.getLowerEndpoint(), (T)var1.getLowerEndpoint());
         if (var5 < 0 || var5 == 0 && var1.getLowerBoundType() == BoundType.OPEN) {
            var3 = var1.getLowerEndpoint();
            var4 = var1.getLowerBoundType();
         }
      }

      boolean var9 = this.hasUpperBound;
      Object var6 = this.getUpperEndpoint();
      BoundType var7 = this.getUpperBoundType();
      if (!this.hasUpperBound()) {
         var9 = var1.hasUpperBound;
         var6 = var1.getUpperEndpoint();
         var7 = var1.getUpperBoundType();
      } else if (var1.hasUpperBound()) {
         int var8 = this.comparator.compare(this.getUpperEndpoint(), (T)var1.getUpperEndpoint());
         if (var8 > 0 || var8 == 0 && var1.getUpperBoundType() == BoundType.OPEN) {
            var6 = var1.getUpperEndpoint();
            var7 = var1.getUpperBoundType();
         }
      }

      if (var2 && var9) {
         int var10 = this.comparator.compare((T)var3, (T)var6);
         if (var10 > 0 || var10 == 0 && var4 == BoundType.OPEN && var7 == BoundType.OPEN) {
            var3 = var6;
            var4 = BoundType.OPEN;
            var7 = BoundType.CLOSED;
         }
      }

      return new GeneralRange<>(this.comparator, var2, (T)var3, var4, var9, (T)var6, var7);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GeneralRange)) {
         return false;
      } else {
         GeneralRange var2 = (GeneralRange)var1;
         return this.comparator.equals(var2.comparator)
            && this.hasLowerBound == var2.hasLowerBound
            && this.hasUpperBound == var2.hasUpperBound
            && this.getLowerBoundType().equals(var2.getLowerBoundType())
            && this.getUpperBoundType().equals(var2.getUpperBoundType())
            && Objects.equal(this.getLowerEndpoint(), var2.getLowerEndpoint())
            && Objects.equal(this.getUpperEndpoint(), var2.getUpperEndpoint());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.comparator, this.getLowerEndpoint(), this.getLowerBoundType(), this.getUpperEndpoint(), this.getUpperBoundType());
   }

   GeneralRange<T> reverse() {
      GeneralRange var1 = this.reverse;
      if (var1 == null) {
         var1 = new GeneralRange<>(
            Ordering.from(this.comparator).reverse(),
            this.hasUpperBound,
            this.getUpperEndpoint(),
            this.getUpperBoundType(),
            this.hasLowerBound,
            this.getLowerEndpoint(),
            this.getLowerBoundType()
         );
         var1.reverse = this;
         return this.reverse = var1;
      } else {
         return var1;
      }
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.comparator);
      int var2 = this.lowerBoundType == BoundType.CLOSED ? 91 : 40;
      String var3 = String.valueOf(this.hasLowerBound ? this.lowerEndpoint : "-∞");
      String var4 = String.valueOf(this.hasUpperBound ? this.upperEndpoint : "∞");
      int var5 = this.upperBoundType == BoundType.CLOSED ? 93 : 41;
      return new StringBuilder(4 + String.valueOf(var1).length() + String.valueOf(var3).length() + String.valueOf(var4).length())
         .append(var1)
         .append(":")
         .append((char)var2)
         .append(var3)
         .append(',')
         .append(var4)
         .append((char)var5)
         .toString();
   }

   T getLowerEndpoint() {
      return this.lowerEndpoint;
   }

   BoundType getLowerBoundType() {
      return this.lowerBoundType;
   }

   T getUpperEndpoint() {
      return this.upperEndpoint;
   }

   BoundType getUpperBoundType() {
      return this.upperBoundType;
   }
}
