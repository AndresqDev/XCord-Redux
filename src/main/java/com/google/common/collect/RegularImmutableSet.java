package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.Spliterator;
import java.util.Spliterators;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableSet<E> extends ImmutableSet.CachingAsList<E> {
   private static final Object[] EMPTY_ARRAY = new Object[0];
   static final RegularImmutableSet<Object> EMPTY = new RegularImmutableSet<>(EMPTY_ARRAY, 0, EMPTY_ARRAY, 0);
   private final transient Object[] elements;
   private final transient int hashCode;
   @VisibleForTesting
   final transient Object[] table;
   private final transient int mask;

   RegularImmutableSet(Object[] var1, int var2, Object[] var3, int var4) {
      this.elements = var1;
      this.hashCode = var2;
      this.table = var3;
      this.mask = var4;
   }

   @Override
   public boolean contains(Object var1) {
      Object[] var2 = this.table;
      if (var1 != null && var2.length != 0) {
         int var5 = Hashing.smearedHash(var1);

         while (true) {
            var5 &= this.mask;
            Object var4 = var2[var5];
            if (var4 == null) {
               return false;
            }

            if (var4.equals(var1)) {
               return true;
            }

            var5++;
         }
      } else {
         return false;
      }
   }

   @Override
   public int size() {
      return this.elements.length;
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      return Iterators.forArray((E[])this.elements);
   }

   @Override
   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this.elements, 1297);
   }

   @Override
   Object[] internalArray() {
      return this.elements;
   }

   @Override
   int internalArrayStart() {
      return 0;
   }

   @Override
   int internalArrayEnd() {
      return this.elements.length;
   }

   @Override
   int copyIntoArray(Object[] var1, int var2) {
      System.arraycopy(this.elements, 0, var1, var2, this.elements.length);
      return var2 + this.elements.length;
   }

   @Override
   ImmutableList<E> createAsList() {
      return (ImmutableList<E>)(this.table.length == 0 ? ImmutableList.of() : new RegularImmutableAsList<>(this, this.elements));
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   boolean isHashCodeFast() {
      return true;
   }
}
