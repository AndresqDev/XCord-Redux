package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class DescendingMultiset<E> extends ForwardingMultiset<E> implements SortedMultiset<E> {
   private transient Comparator<? super E> comparator;
   private transient NavigableSet<E> elementSet;
   private transient Set<Multiset.Entry<E>> entrySet;

   abstract SortedMultiset<E> forwardMultiset();

   @Override
   public Comparator<? super E> comparator() {
      Comparator var1 = this.comparator;
      return var1 == null ? (this.comparator = Ordering.from(this.forwardMultiset().comparator()).reverse()) : var1;
   }

   @Override
   public NavigableSet<E> elementSet() {
      NavigableSet var1 = this.elementSet;
      return var1 == null ? (this.elementSet = new SortedMultisets.NavigableElementSet<>(this)) : var1;
   }

   @Override
   public Multiset.Entry<E> pollFirstEntry() {
      return this.forwardMultiset().pollLastEntry();
   }

   @Override
   public Multiset.Entry<E> pollLastEntry() {
      return this.forwardMultiset().pollFirstEntry();
   }

   @Override
   public SortedMultiset<E> headMultiset(@ParametricNullness E var1, BoundType var2) {
      return this.forwardMultiset().tailMultiset((E)var1, var2).descendingMultiset();
   }

   @Override
   public SortedMultiset<E> subMultiset(@ParametricNullness E var1, BoundType var2, @ParametricNullness E var3, BoundType var4) {
      return this.forwardMultiset().subMultiset((E)var3, var4, (E)var1, var2).descendingMultiset();
   }

   @Override
   public SortedMultiset<E> tailMultiset(@ParametricNullness E var1, BoundType var2) {
      return this.forwardMultiset().headMultiset((E)var1, var2).descendingMultiset();
   }

   @Override
   protected Multiset<E> delegate() {
      return this.forwardMultiset();
   }

   @Override
   public SortedMultiset<E> descendingMultiset() {
      return this.forwardMultiset();
   }

   @Override
   public Multiset.Entry<E> firstEntry() {
      return this.forwardMultiset().lastEntry();
   }

   @Override
   public Multiset.Entry<E> lastEntry() {
      return this.forwardMultiset().firstEntry();
   }

   abstract Iterator<Multiset.Entry<E>> entryIterator();

   @Override
   public Set<Multiset.Entry<E>> entrySet() {
      Set var1 = this.entrySet;
      return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
   }

   Set<Multiset.Entry<E>> createEntrySet() {
      class 1EntrySetImpl extends Multisets.EntrySet<E> {
         @Override
         Multiset<E> multiset() {
            return DescendingMultiset.this;
         }

         @Override
         public Iterator<Multiset.Entry<E>> iterator() {
            return DescendingMultiset.this.entryIterator();
         }

         @Override
         public int size() {
            return DescendingMultiset.this.forwardMultiset().entrySet().size();
         }
      }

      return new 1EntrySetImpl();
   }

   @Override
   public Iterator<E> iterator() {
      return Multisets.iteratorImpl(this);
   }

   @Override
   public Object[] toArray() {
      return this.standardToArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])this.standardToArray(var1);
   }

   @Override
   public String toString() {
      return this.entrySet().toString();
   }
}
