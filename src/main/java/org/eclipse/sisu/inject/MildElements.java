package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class MildElements<T> extends AbstractCollection<T> {
   private final ReferenceQueue<T> queue = new ReferenceQueue<>();
   final List<Reference<T>> list;
   private final boolean soft;

   MildElements(List<Reference<T>> var1, boolean var2) {
      this.list = var1;
      this.soft = var2;
   }

   @Override
   public boolean add(T var1) {
      this.compact();
      return this.list
         .add(
            (Reference<T>)(this.soft
               ? new MildElements.Soft<>((T)var1, this.queue, this.list.size())
               : new MildElements.Weak<>((T)var1, this.queue, this.list.size()))
         );
   }

   @Override
   public int size() {
      this.compact();
      return this.list.size();
   }

   @Override
   public Iterator<T> iterator() {
      this.compact();
      return new MildElements.Itr();
   }

   private void compact() {
      Reference var1;
      while ((var1 = this.queue.poll()) != null) {
         this.evict(var1);
      }
   }

   void evict(Reference<? extends T> var1) {
      int var2 = ((MildElements.Indexable)var1).index(-1);
      if (var2 >= 0) {
         Reference var3 = this.list.remove(this.list.size() - 1);
         if (var1 != var3) {
            ((MildElements.Indexable)var3).index(var2);
            this.list.set(var2, var3);
         }
      }
   }

   private interface Indexable {
      int index(int var1);
   }

   final class Itr implements Iterator<T> {
      private int index;
      private T nextElement;
      private boolean haveElement;

      @Override
      public boolean hasNext() {
         while (this.nextElement == null && this.index < MildElements.this.list.size()) {
            this.nextElement = MildElements.this.list.get(this.index++).get();
         }

         return this.nextElement != null;
      }

      @Override
      public T next() {
         this.haveElement = this.hasNext();
         if (this.haveElement) {
            Object var1 = this.nextElement;
            this.nextElement = null;
            return (T)var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         if (this.haveElement) {
            MildElements.this.evict(MildElements.this.list.get(--this.index));
            this.haveElement = false;
         } else {
            throw new IllegalStateException();
         }
      }
   }

   private static final class Soft<T> extends SoftReference<T> implements MildElements.Indexable {
      private int index;

      Soft(T var1, ReferenceQueue<T> var2, int var3) {
         super((T)var1, var2);
         this.index = var3;
      }

      @Override
      public int index(int var1) {
         int var2 = this.index;
         this.index = var1;
         return var2;
      }
   }

   private static final class Weak<T> extends WeakReference<T> implements MildElements.Indexable {
      private int index;

      Weak(T var1, ReferenceQueue<T> var2, int var3) {
         super((T)var1, var2);
         this.index = var3;
      }

      @Override
      public int index(int var1) {
         int var2 = this.index;
         this.index = var1;
         return var2;
      }
   }
}
