package org.apache.http.protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

final class ChainBuilder<E> {
   private final LinkedList<E> list = new LinkedList<>();
   private final Map<Class<?>, E> uniqueClasses = new HashMap<>();

   public ChainBuilder() {
   }

   private void ensureUnique(E var1) {
      Object var2 = this.uniqueClasses.remove(var1.getClass());
      if (var2 != null) {
         this.list.remove(var2);
      }

      this.uniqueClasses.put(var1.getClass(), (E)var1);
   }

   public ChainBuilder<E> addFirst(E var1) {
      if (var1 == null) {
         return this;
      } else {
         this.ensureUnique((E)var1);
         this.list.addFirst((E)var1);
         return this;
      }
   }

   public ChainBuilder<E> addLast(E var1) {
      if (var1 == null) {
         return this;
      } else {
         this.ensureUnique((E)var1);
         this.list.addLast((E)var1);
         return this;
      }
   }

   public ChainBuilder<E> addAllFirst(Collection<E> var1) {
      if (var1 == null) {
         return this;
      } else {
         for (Object var3 : var1) {
            this.addFirst((E)var3);
         }

         return this;
      }
   }

   public ChainBuilder<E> addAllFirst(E... var1) {
      if (var1 == null) {
         return this;
      } else {
         for (Object var5 : var1) {
            this.addFirst((E)var5);
         }

         return this;
      }
   }

   public ChainBuilder<E> addAllLast(Collection<E> var1) {
      if (var1 == null) {
         return this;
      } else {
         for (Object var3 : var1) {
            this.addLast((E)var3);
         }

         return this;
      }
   }

   public ChainBuilder<E> addAllLast(E... var1) {
      if (var1 == null) {
         return this;
      } else {
         for (Object var5 : var1) {
            this.addLast((E)var5);
         }

         return this;
      }
   }

   public LinkedList<E> build() {
      return new LinkedList<>(this.list);
   }
}
