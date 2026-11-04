package org.yaml.snakeyaml.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class ArrayUtils {
   private ArrayUtils() {
   }

   public static <E> List<E> toUnmodifiableList(E[] var0) {
      return (List<E>)(var0.length == 0 ? Collections.emptyList() : new ArrayUtils.UnmodifiableArrayList<>((E[])var0));
   }

   public static <E> List<E> toUnmodifiableCompositeList(E[] var0, E[] var1) {
      Object var2;
      if (var0.length == 0) {
         var2 = toUnmodifiableList(var1);
      } else if (var1.length == 0) {
         var2 = toUnmodifiableList(var0);
      } else {
         var2 = new ArrayUtils.CompositeUnmodifiableArrayList<>(var0, var1);
      }

      return (List<E>)var2;
   }

   private static class CompositeUnmodifiableArrayList<E> extends AbstractList<E> {
      private final E[] array1;
      private final E[] array2;

      CompositeUnmodifiableArrayList(E[] var1, E[] var2) {
         this.array1 = (E[])var1;
         this.array2 = (E[])var2;
      }

      @Override
      public E get(int var1) {
         Object var2;
         if (var1 < this.array1.length) {
            var2 = this.array1[var1];
         } else {
            if (var1 - this.array1.length >= this.array2.length) {
               throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size());
            }

            var2 = this.array2[var1 - this.array1.length];
         }

         return (E)var2;
      }

      @Override
      public int size() {
         return this.array1.length + this.array2.length;
      }
   }

   private static class UnmodifiableArrayList<E> extends AbstractList<E> {
      private final E[] array;

      UnmodifiableArrayList(E[] var1) {
         this.array = (E[])var1;
      }

      @Override
      public E get(int var1) {
         if (var1 >= this.array.length) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size());
         } else {
            return this.array[var1];
         }
      }

      @Override
      public int size() {
         return this.array.length;
      }
   }
}
