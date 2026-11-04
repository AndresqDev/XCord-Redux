package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterator.OfDouble;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class ImmutableDoubleArray implements Serializable {
   private static final ImmutableDoubleArray EMPTY = new ImmutableDoubleArray(new double[0]);
   private final double[] array;
   private final transient int start;
   private final int end;

   public static ImmutableDoubleArray of() {
      return EMPTY;
   }

   public static ImmutableDoubleArray of(double var0) {
      return new ImmutableDoubleArray(new double[]{var0});
   }

   public static ImmutableDoubleArray of(double var0, double var2) {
      return new ImmutableDoubleArray(new double[]{var0, var2});
   }

   public static ImmutableDoubleArray of(double var0, double var2, double var4) {
      return new ImmutableDoubleArray(new double[]{var0, var2, var4});
   }

   public static ImmutableDoubleArray of(double var0, double var2, double var4, double var6) {
      return new ImmutableDoubleArray(new double[]{var0, var2, var4, var6});
   }

   public static ImmutableDoubleArray of(double var0, double var2, double var4, double var6, double var8) {
      return new ImmutableDoubleArray(new double[]{var0, var2, var4, var6, var8});
   }

   public static ImmutableDoubleArray of(double var0, double var2, double var4, double var6, double var8, double var10) {
      return new ImmutableDoubleArray(new double[]{var0, var2, var4, var6, var8, var10});
   }

   public static ImmutableDoubleArray of(double var0, double... var2) {
      Preconditions.checkArgument(var2.length <= 2147483646, "the total number of elements must fit in an int");
      double[] var3 = new double[var2.length + 1];
      var3[0] = var0;
      System.arraycopy(var2, 0, var3, 1, var2.length);
      return new ImmutableDoubleArray(var3);
   }

   public static ImmutableDoubleArray copyOf(double[] var0) {
      return var0.length == 0 ? EMPTY : new ImmutableDoubleArray(Arrays.copyOf(var0, var0.length));
   }

   public static ImmutableDoubleArray copyOf(Collection<Double> var0) {
      return var0.isEmpty() ? EMPTY : new ImmutableDoubleArray(Doubles.toArray(var0));
   }

   public static ImmutableDoubleArray copyOf(Iterable<Double> var0) {
      return var0 instanceof Collection ? copyOf((Collection<Double>)var0) : builder().addAll(var0).build();
   }

   public static ImmutableDoubleArray copyOf(DoubleStream var0) {
      double[] var1 = var0.toArray();
      return var1.length == 0 ? EMPTY : new ImmutableDoubleArray(var1);
   }

   public static ImmutableDoubleArray.Builder builder(int var0) {
      Preconditions.checkArgument(var0 >= 0, "Invalid initialCapacity: %s", var0);
      return new ImmutableDoubleArray.Builder(var0);
   }

   public static ImmutableDoubleArray.Builder builder() {
      return new ImmutableDoubleArray.Builder(10);
   }

   private ImmutableDoubleArray(double[] var1) {
      this(var1, 0, var1.length);
   }

   private ImmutableDoubleArray(double[] var1, int var2, int var3) {
      this.array = var1;
      this.start = var2;
      this.end = var3;
   }

   public int length() {
      return this.end - this.start;
   }

   public boolean isEmpty() {
      return this.end == this.start;
   }

   public double get(int var1) {
      Preconditions.checkElementIndex(var1, this.length());
      return this.array[this.start + var1];
   }

   public int indexOf(double var1) {
      for (int var3 = this.start; var3 < this.end; var3++) {
         if (areEqual(this.array[var3], var1)) {
            return var3 - this.start;
         }
      }

      return -1;
   }

   public int lastIndexOf(double var1) {
      for (int var3 = this.end - 1; var3 >= this.start; var3--) {
         if (areEqual(this.array[var3], var1)) {
            return var3 - this.start;
         }
      }

      return -1;
   }

   public boolean contains(double var1) {
      return this.indexOf(var1) >= 0;
   }

   public void forEach(DoubleConsumer var1) {
      Preconditions.checkNotNull(var1);

      for (int var2 = this.start; var2 < this.end; var2++) {
         var1.accept(this.array[var2]);
      }
   }

   public DoubleStream stream() {
      return Arrays.stream(this.array, this.start, this.end);
   }

   public double[] toArray() {
      return Arrays.copyOfRange(this.array, this.start, this.end);
   }

   public ImmutableDoubleArray subArray(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, this.length());
      return var1 == var2 ? EMPTY : new ImmutableDoubleArray(this.array, this.start + var1, this.start + var2);
   }

   private OfDouble spliterator() {
      return Spliterators.spliterator(this.array, this.start, this.end, 1040);
   }

   public List<Double> asList() {
      return new ImmutableDoubleArray.AsList(this);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ImmutableDoubleArray)) {
         return false;
      } else {
         ImmutableDoubleArray var2 = (ImmutableDoubleArray)var1;
         if (this.length() != var2.length()) {
            return false;
         } else {
            for (int var3 = 0; var3 < this.length(); var3++) {
               if (!areEqual(this.get(var3), var2.get(var3))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean areEqual(double var0, double var2) {
      return Double.doubleToLongBits(var0) == Double.doubleToLongBits(var2);
   }

   @Override
   public int hashCode() {
      int var1 = 1;

      for (int var2 = this.start; var2 < this.end; var2++) {
         var1 *= 31;
         var1 += Doubles.hashCode(this.array[var2]);
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.isEmpty()) {
         return "[]";
      } else {
         StringBuilder var1 = new StringBuilder(this.length() * 5);
         var1.append('[').append(this.array[this.start]);

         for (int var2 = this.start + 1; var2 < this.end; var2++) {
            var1.append(", ").append(this.array[var2]);
         }

         var1.append(']');
         return var1.toString();
      }
   }

   public ImmutableDoubleArray trimmed() {
      return this.isPartialView() ? new ImmutableDoubleArray(this.toArray()) : this;
   }

   private boolean isPartialView() {
      return this.start > 0 || this.end < this.array.length;
   }

   Object writeReplace() {
      return this.trimmed();
   }

   Object readResolve() {
      return this.isEmpty() ? EMPTY : this;
   }

   static class AsList extends AbstractList<Double> implements Serializable, RandomAccess {
      private final ImmutableDoubleArray parent;

      private AsList(ImmutableDoubleArray var1) {
         this.parent = var1;
      }

      @Override
      public int size() {
         return this.parent.length();
      }

      public Double get(int var1) {
         return this.parent.get(var1);
      }

      @Override
      public boolean contains(Object var1) {
         return this.indexOf(var1) >= 0;
      }

      @Override
      public int indexOf(Object var1) {
         return var1 instanceof Double ? this.parent.indexOf((Double)var1) : -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         return var1 instanceof Double ? this.parent.lastIndexOf((Double)var1) : -1;
      }

      @Override
      public List<Double> subList(int var1, int var2) {
         return this.parent.subArray(var1, var2).asList();
      }

      @Override
      public Spliterator<Double> spliterator() {
         return this.parent.spliterator();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof ImmutableDoubleArray.AsList) {
            ImmutableDoubleArray.AsList var6 = (ImmutableDoubleArray.AsList)var1;
            return this.parent.equals(var6.parent);
         } else if (!(var1 instanceof List)) {
            return false;
         } else {
            List var2 = (List)var1;
            if (this.size() != var2.size()) {
               return false;
            } else {
               int var3 = this.parent.start;

               for (Object var5 : var2) {
                  if (!(var5 instanceof Double) || !ImmutableDoubleArray.areEqual(this.parent.array[var3++], (Double)var5)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.parent.hashCode();
      }

      @Override
      public String toString() {
         return this.parent.toString();
      }
   }

   @CanIgnoreReturnValue
   public static final class Builder {
      private double[] array;
      private int count = 0;

      Builder(int var1) {
         this.array = new double[var1];
      }

      public ImmutableDoubleArray.Builder add(double var1) {
         this.ensureRoomFor(1);
         this.array[this.count] = var1;
         this.count++;
         return this;
      }

      public ImmutableDoubleArray.Builder addAll(double[] var1) {
         this.ensureRoomFor(var1.length);
         System.arraycopy(var1, 0, this.array, this.count, var1.length);
         this.count += var1.length;
         return this;
      }

      public ImmutableDoubleArray.Builder addAll(Iterable<Double> var1) {
         if (var1 instanceof Collection) {
            return this.addAll((Collection<Double>)var1);
         } else {
            for (Double var3 : var1) {
               this.add(var3);
            }

            return this;
         }
      }

      public ImmutableDoubleArray.Builder addAll(Collection<Double> var1) {
         this.ensureRoomFor(var1.size());

         for (Double var3 : var1) {
            this.array[this.count++] = var3;
         }

         return this;
      }

      public ImmutableDoubleArray.Builder addAll(DoubleStream var1) {
         OfDouble var2 = var1.spliterator();
         long var3 = var2.getExactSizeIfKnown();
         if (var3 > 0L) {
            this.ensureRoomFor(Ints.saturatedCast(var3));
         }

         var2.forEachRemaining(this::add);
         return this;
      }

      public ImmutableDoubleArray.Builder addAll(ImmutableDoubleArray var1) {
         this.ensureRoomFor(var1.length());
         System.arraycopy(var1.array, var1.start, this.array, this.count, var1.length());
         this.count = this.count + var1.length();
         return this;
      }

      private void ensureRoomFor(int var1) {
         int var2 = this.count + var1;
         if (var2 > this.array.length) {
            this.array = Arrays.copyOf(this.array, expandedCapacity(this.array.length, var2));
         }
      }

      private static int expandedCapacity(int var0, int var1) {
         if (var1 < 0) {
            throw new AssertionError("cannot store more than MAX_VALUE elements");
         } else {
            int var2 = var0 + (var0 >> 1) + 1;
            if (var2 < var1) {
               var2 = Integer.highestOneBit(var1 - 1) << 1;
            }

            if (var2 < 0) {
               var2 = Integer.MAX_VALUE;
            }

            return var2;
         }
      }

      @CheckReturnValue
      public ImmutableDoubleArray build() {
         return this.count == 0 ? ImmutableDoubleArray.EMPTY : new ImmutableDoubleArray(this.array, 0, this.count);
      }
   }
}
