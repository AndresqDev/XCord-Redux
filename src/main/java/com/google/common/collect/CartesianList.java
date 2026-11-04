package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess {
   private final transient ImmutableList<List<E>> axes;
   private final transient int[] axesSizeProduct;

   static <E> List<List<E>> create(List<? extends List<? extends E>> var0) {
      ImmutableList.Builder var1 = new ImmutableList.Builder(var0.size());

      for (List var3 : var0) {
         ImmutableList var4 = ImmutableList.copyOf(var3);
         if (var4.isEmpty()) {
            return ImmutableList.of();
         }

         var1.add(var4);
      }

      return new CartesianList<>(var1.build());
   }

   CartesianList(ImmutableList<List<E>> var1) {
      this.axes = var1;
      int[] var2 = new int[var1.size() + 1];
      var2[var1.size()] = 1;

      try {
         for (int var3 = var1.size() - 1; var3 >= 0; var3--) {
            var2[var3] = IntMath.checkedMultiply(var2[var3 + 1], ((List)var1.get(var3)).size());
         }
      } catch (ArithmeticException var4) {
         throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
      }

      this.axesSizeProduct = var2;
   }

   private int getAxisIndexForProductIndex(int var1, int var2) {
      return var1 / this.axesSizeProduct[var2 + 1] % this.axes.get(var2).size();
   }

   @Override
   public int indexOf(Object var1) {
      if (!(var1 instanceof List)) {
         return -1;
      } else {
         List var2 = (List)var1;
         if (var2.size() != this.axes.size()) {
            return -1;
         } else {
            ListIterator var3 = var2.listIterator();
            int var4 = 0;

            while (var3.hasNext()) {
               int var5 = var3.nextIndex();
               int var6 = this.axes.get(var5).indexOf(var3.next());
               if (var6 == -1) {
                  return -1;
               }

               var4 += var6 * this.axesSizeProduct[var5 + 1];
            }

            return var4;
         }
      }
   }

   @Override
   public int lastIndexOf(Object var1) {
      if (!(var1 instanceof List)) {
         return -1;
      } else {
         List var2 = (List)var1;
         if (var2.size() != this.axes.size()) {
            return -1;
         } else {
            ListIterator var3 = var2.listIterator();
            int var4 = 0;

            while (var3.hasNext()) {
               int var5 = var3.nextIndex();
               int var6 = this.axes.get(var5).lastIndexOf(var3.next());
               if (var6 == -1) {
                  return -1;
               }

               var4 += var6 * this.axesSizeProduct[var5 + 1];
            }

            return var4;
         }
      }
   }

   public ImmutableList<E> get(final int var1) {
      Preconditions.checkElementIndex(var1, this.size());
      return new ImmutableList<E>() {
         @Override
         public int size() {
            return CartesianList.this.axes.size();
         }

         @Override
         public E get(int var1x) {
            Preconditions.checkElementIndex(var1x, this.size());
            int var2 = CartesianList.this.getAxisIndexForProductIndex(var1, var1x);
            return CartesianList.this.axes.get(var1x).get(var2);
         }

         @Override
         boolean isPartialView() {
            return true;
         }
      };
   }

   @Override
   public int size() {
      return this.axesSizeProduct[0];
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof List)) {
         return false;
      } else {
         List var2 = (List)var1;
         if (var2.size() != this.axes.size()) {
            return false;
         } else {
            int var3 = 0;

            for (Object var5 : var2) {
               if (!this.axes.get(var3).contains(var5)) {
                  return false;
               }

               var3++;
            }

            return true;
         }
      }
   }
}
