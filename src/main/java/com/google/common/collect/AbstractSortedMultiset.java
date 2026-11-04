package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class AbstractSortedMultiset<E> extends AbstractMultiset<E> implements SortedMultiset<E> {
   @GwtTransient
   final Comparator<? super E> comparator;
   private transient SortedMultiset<E> descendingMultiset;

   AbstractSortedMultiset() {
      this(Ordering.natural());
   }

   AbstractSortedMultiset(Comparator<? super E> var1) {
      this.comparator = Preconditions.checkNotNull(var1);
   }

   @Override
   public NavigableSet<E> elementSet() {
      return (NavigableSet<E>)super.elementSet();
   }

   NavigableSet<E> createElementSet() {
      return new SortedMultisets.NavigableElementSet<>(this);
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.comparator;
   }

   @Override
   public Multiset.Entry<E> firstEntry() {
      Iterator var1 = this.entryIterator();
      return var1.hasNext() ? (Multiset.Entry)var1.next() : null;
   }

   @Override
   public Multiset.Entry<E> lastEntry() {
      Iterator var1 = this.descendingEntryIterator();
      return var1.hasNext() ? (Multiset.Entry)var1.next() : null;
   }

   @Override
   public Multiset.Entry<E> pollFirstEntry() {
      Iterator var1 = this.entryIterator();
      if (var1.hasNext()) {
         Multiset.Entry var2 = (Multiset.Entry)var1.next();
         var2 = Multisets.immutableEntry(var2.getElement(), var2.getCount());
         var1.remove();
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public Multiset.Entry<E> pollLastEntry() {
      Iterator var1 = this.descendingEntryIterator();
      if (var1.hasNext()) {
         Multiset.Entry var2 = (Multiset.Entry)var1.next();
         var2 = Multisets.immutableEntry(var2.getElement(), var2.getCount());
         var1.remove();
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public SortedMultiset<E> subMultiset(@ParametricNullness E var1, BoundType var2, @ParametricNullness E var3, BoundType var4) {
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var4);
      return this.tailMultiset((E)var1, var2).headMultiset((E)var3, var4);
   }

   abstract Iterator<Multiset.Entry<E>> descendingEntryIterator();

   Iterator<E> descendingIterator() {
      return Multisets.iteratorImpl(this.descendingMultiset());
   }

   @Override
   public SortedMultiset<E> descendingMultiset() {
      SortedMultiset var1 = this.descendingMultiset;
      return var1 == null ? (this.descendingMultiset = this.createDescendingMultiset()) : var1;
   }

   SortedMultiset<E> createDescendingMultiset() {
      class 1DescendingMultisetImpl extends DescendingMultiset<E> {
         @Override
         SortedMultiset<E> forwardMultiset() {
            return AbstractSortedMultiset.this;
         }

         @Override
         Iterator<Multiset.Entry<E>> entryIterator() {
            return AbstractSortedMultiset.this.descendingEntryIterator();
         }

         @Override
         public Iterator<E> iterator() {
            return AbstractSortedMultiset.this.descendingIterator();
         }
      }

      return new 1DescendingMultisetImpl();
   }
}
