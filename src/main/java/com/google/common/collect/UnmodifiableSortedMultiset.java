package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.NavigableSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class UnmodifiableSortedMultiset<E> extends Multisets.UnmodifiableMultiset<E> implements SortedMultiset<E> {
   private transient UnmodifiableSortedMultiset<E> descendingMultiset;
   private static final long serialVersionUID = 0L;

   UnmodifiableSortedMultiset(SortedMultiset<E> var1) {
      super(var1);
   }

   protected SortedMultiset<E> delegate() {
      return (SortedMultiset<E>)super.delegate();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.delegate().comparator();
   }

   NavigableSet<E> createElementSet() {
      return Sets.unmodifiableNavigableSet(this.delegate().elementSet());
   }

   @Override
   public NavigableSet<E> elementSet() {
      return (NavigableSet<E>)super.elementSet();
   }

   @Override
   public SortedMultiset<E> descendingMultiset() {
      UnmodifiableSortedMultiset var1 = this.descendingMultiset;
      if (var1 == null) {
         var1 = new UnmodifiableSortedMultiset<>(this.delegate().descendingMultiset());
         var1.descendingMultiset = this;
         return this.descendingMultiset = var1;
      } else {
         return var1;
      }
   }

   @Override
   public Multiset.Entry<E> firstEntry() {
      return this.delegate().firstEntry();
   }

   @Override
   public Multiset.Entry<E> lastEntry() {
      return this.delegate().lastEntry();
   }

   @Override
   public Multiset.Entry<E> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Multiset.Entry<E> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   @Override
   public SortedMultiset<E> headMultiset(@ParametricNullness E var1, BoundType var2) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().headMultiset((E)var1, var2));
   }

   @Override
   public SortedMultiset<E> subMultiset(@ParametricNullness E var1, BoundType var2, @ParametricNullness E var3, BoundType var4) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().subMultiset((E)var1, var2, (E)var3, var4));
   }

   @Override
   public SortedMultiset<E> tailMultiset(@ParametricNullness E var1, BoundType var2) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().tailMultiset((E)var1, var2));
   }
}
