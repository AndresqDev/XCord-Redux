package com.google.protobuf;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

abstract class AbstractProtobufList<E> extends AbstractList<E> implements Internal.ProtobufList<E> {
   protected static final int DEFAULT_CAPACITY = 10;
   private boolean isMutable = true;

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else if (!(var1 instanceof RandomAccess)) {
         return super.equals(var1);
      } else {
         List var2 = (List)var1;
         int var3 = this.size();
         if (var3 != var2.size()) {
            return false;
         } else {
            for (int var4 = 0; var4 < var3; var4++) {
               if (!this.get(var4).equals(var2.get(var4))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.size();
      int var2 = 1;

      for (int var3 = 0; var3 < var1; var3++) {
         var2 = 31 * var2 + this.get(var3).hashCode();
      }

      return var2;
   }

   @Override
   public boolean add(E var1) {
      this.ensureIsMutable();
      return super.add((E)var1);
   }

   @Override
   public void add(int var1, E var2) {
      this.ensureIsMutable();
      super.add(var1, (E)var2);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      this.ensureIsMutable();
      return super.addAll(var1);
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.ensureIsMutable();
      return super.addAll(var1, var2);
   }

   @Override
   public void clear() {
      this.ensureIsMutable();
      super.clear();
   }

   @Override
   public boolean isModifiable() {
      return this.isMutable;
   }

   @Override
   public final void makeImmutable() {
      this.isMutable = false;
   }

   @Override
   public E remove(int var1) {
      this.ensureIsMutable();
      return super.remove(var1);
   }

   @Override
   public boolean remove(Object var1) {
      this.ensureIsMutable();
      return super.remove(var1);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      this.ensureIsMutable();
      return super.removeAll(var1);
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      this.ensureIsMutable();
      return super.retainAll(var1);
   }

   @Override
   public E set(int var1, E var2) {
      this.ensureIsMutable();
      return super.set(var1, (E)var2);
   }

   protected void ensureIsMutable() {
      if (!this.isMutable) {
         throw new UnsupportedOperationException();
      }
   }
}
