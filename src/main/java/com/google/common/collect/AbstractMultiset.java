package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
   @LazyInit
   private transient Set<E> elementSet;
   @LazyInit
   private transient Set<Multiset.Entry<E>> entrySet;

   @Override
   public boolean isEmpty() {
      return this.entrySet().isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      return this.count(var1) > 0;
   }

   @CanIgnoreReturnValue
   @Override
   public final boolean add(@ParametricNullness E var1) {
      this.add((E)var1, 1);
      return true;
   }

   @CanIgnoreReturnValue
   @Override
   public int add(@ParametricNullness E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @CanIgnoreReturnValue
   @Override
   public final boolean remove(Object var1) {
      return this.remove(var1, 1) > 0;
   }

   @CanIgnoreReturnValue
   @Override
   public int remove(Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @CanIgnoreReturnValue
   @Override
   public int setCount(@ParametricNullness E var1, int var2) {
      return Multisets.setCountImpl(this, (E)var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean setCount(@ParametricNullness E var1, int var2, int var3) {
      return Multisets.setCountImpl(this, (E)var1, var2, var3);
   }

   @CanIgnoreReturnValue
   @Override
   public final boolean addAll(Collection<? extends E> var1) {
      return Multisets.addAllImpl(this, var1);
   }

   @CanIgnoreReturnValue
   @Override
   public final boolean removeAll(Collection<?> var1) {
      return Multisets.removeAllImpl(this, var1);
   }

   @CanIgnoreReturnValue
   @Override
   public final boolean retainAll(Collection<?> var1) {
      return Multisets.retainAllImpl(this, var1);
   }

   @Override
   public abstract void clear();

   @Override
   public Set<E> elementSet() {
      Set var1 = this.elementSet;
      if (var1 == null) {
         this.elementSet = var1 = this.createElementSet();
      }

      return var1;
   }

   Set<E> createElementSet() {
      return new AbstractMultiset.ElementSet();
   }

   abstract Iterator<E> elementIterator();

   @Override
   public Set<Multiset.Entry<E>> entrySet() {
      Set var1 = this.entrySet;
      if (var1 == null) {
         this.entrySet = var1 = this.createEntrySet();
      }

      return var1;
   }

   Set<Multiset.Entry<E>> createEntrySet() {
      return new AbstractMultiset.EntrySet();
   }

   abstract Iterator<Multiset.Entry<E>> entryIterator();

   abstract int distinctElements();

   @Override
   public final boolean equals(Object var1) {
      return Multisets.equalsImpl(this, var1);
   }

   @Override
   public final int hashCode() {
      return this.entrySet().hashCode();
   }

   @Override
   public final String toString() {
      return this.entrySet().toString();
   }

   class ElementSet extends Multisets.ElementSet<E> {
      @Override
      Multiset<E> multiset() {
         return AbstractMultiset.this;
      }

      @Override
      public Iterator<E> iterator() {
         return AbstractMultiset.this.elementIterator();
      }
   }

   class EntrySet extends Multisets.EntrySet<E> {
      @Override
      Multiset<E> multiset() {
         return AbstractMultiset.this;
      }

      @Override
      public Iterator<Multiset.Entry<E>> iterator() {
         return AbstractMultiset.this.entryIterator();
      }

      @Override
      public int size() {
         return AbstractMultiset.this.distinctElements();
      }
   }
}
