package com.google.common.math;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public abstract class LinearTransformation {
   public static LinearTransformation.LinearTransformationBuilder mapping(double var0, double var2) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0) && DoubleUtils.isFinite(var2));
      return new LinearTransformation.LinearTransformationBuilder(var0, var2);
   }

   public static LinearTransformation vertical(double var0) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0));
      return new LinearTransformation.VerticalLinearTransformation(var0);
   }

   public static LinearTransformation horizontal(double var0) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0));
      double var2 = 0.0;
      return new LinearTransformation.RegularLinearTransformation(var2, var0);
   }

   public static LinearTransformation forNaN() {
      return LinearTransformation.NaNLinearTransformation.INSTANCE;
   }

   public abstract boolean isVertical();

   public abstract boolean isHorizontal();

   public abstract double slope();

   public abstract double transform(double var1);

   public abstract LinearTransformation inverse();

   public static final class LinearTransformationBuilder {
      private final double x1;
      private final double y1;

      private LinearTransformationBuilder(double var1, double var3) {
         this.x1 = var1;
         this.y1 = var3;
      }

      public LinearTransformation and(double var1, double var3) {
         Preconditions.checkArgument(DoubleUtils.isFinite(var1) && DoubleUtils.isFinite(var3));
         if (var1 == this.x1) {
            Preconditions.checkArgument(var3 != this.y1);
            return new LinearTransformation.VerticalLinearTransformation(this.x1);
         } else {
            return this.withSlope((var3 - this.y1) / (var1 - this.x1));
         }
      }

      public LinearTransformation withSlope(double var1) {
         Preconditions.checkArgument(!Double.isNaN(var1));
         if (DoubleUtils.isFinite(var1)) {
            double var3 = this.y1 - this.x1 * var1;
            return new LinearTransformation.RegularLinearTransformation(var1, var3);
         } else {
            return new LinearTransformation.VerticalLinearTransformation(this.x1);
         }
      }
   }

   private static final class NaNLinearTransformation extends LinearTransformation {
      static final LinearTransformation.NaNLinearTransformation INSTANCE = new LinearTransformation.NaNLinearTransformation();

      @Override
      public boolean isVertical() {
         return false;
      }

      @Override
      public boolean isHorizontal() {
         return false;
      }

      @Override
      public double slope() {
         return Double.NaN;
      }

      @Override
      public double transform(double var1) {
         return Double.NaN;
      }

      @Override
      public LinearTransformation inverse() {
         return this;
      }

      @Override
      public String toString() {
         return "NaN";
      }
   }

   private static final class RegularLinearTransformation extends LinearTransformation {
      final double slope;
      final double yIntercept;
      @LazyInit
      LinearTransformation inverse;

      RegularLinearTransformation(double var1, double var3) {
         this.slope = var1;
         this.yIntercept = var3;
         this.inverse = null;
      }

      RegularLinearTransformation(double var1, double var3, LinearTransformation var5) {
         this.slope = var1;
         this.yIntercept = var3;
         this.inverse = var5;
      }

      @Override
      public boolean isVertical() {
         return false;
      }

      @Override
      public boolean isHorizontal() {
         return this.slope == 0.0;
      }

      @Override
      public double slope() {
         return this.slope;
      }

      @Override
      public double transform(double var1) {
         return var1 * this.slope + this.yIntercept;
      }

      @Override
      public LinearTransformation inverse() {
         LinearTransformation var1 = this.inverse;
         return var1 == null ? (this.inverse = this.createInverse()) : var1;
      }

      @Override
      public String toString() {
         return String.format("y = %g * x + %g", this.slope, this.yIntercept);
      }

      private LinearTransformation createInverse() {
         return (LinearTransformation)(this.slope != 0.0
            ? new LinearTransformation.RegularLinearTransformation(1.0 / this.slope, -1.0 * this.yIntercept / this.slope, this)
            : new LinearTransformation.VerticalLinearTransformation(this.yIntercept, this));
      }
   }

   private static final class VerticalLinearTransformation extends LinearTransformation {
      final double x;
      @LazyInit
      LinearTransformation inverse;

      VerticalLinearTransformation(double var1) {
         this.x = var1;
         this.inverse = null;
      }

      VerticalLinearTransformation(double var1, LinearTransformation var3) {
         this.x = var1;
         this.inverse = var3;
      }

      @Override
      public boolean isVertical() {
         return true;
      }

      @Override
      public boolean isHorizontal() {
         return false;
      }

      @Override
      public double slope() {
         throw new IllegalStateException();
      }

      @Override
      public double transform(double var1) {
         throw new IllegalStateException();
      }

      @Override
      public LinearTransformation inverse() {
         LinearTransformation var1 = this.inverse;
         return var1 == null ? (this.inverse = this.createInverse()) : var1;
      }

      @Override
      public String toString() {
         return String.format("x = %g", this.x);
      }

      private LinearTransformation createInverse() {
         return new LinearTransformation.RegularLinearTransformation(0.0, this.x, this);
      }
   }
}
