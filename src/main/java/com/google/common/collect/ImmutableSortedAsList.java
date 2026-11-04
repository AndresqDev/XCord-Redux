package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import java.util.Spliterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E> implements SortedIterable<E> {
   ImmutableSortedAsList(ImmutableSortedSet<E> var1, ImmutableList<E> var2) {
      super(var1, var2);
   }

   ImmutableSortedSet<E> delegateCollection() {
      return (ImmutableSortedSet<E>)super.delegateCollection();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.delegateCollection().comparator();
   }

   @GwtIncompatible
   @Override
   public int indexOf(Object var1) {
      int var2 = this.delegateCollection().indexOf(var1);
      return var2 >= 0 && this.get(var2).equals(var1) ? var2 : -1;
   }

   @GwtIncompatible
   @Override
   public int lastIndexOf(Object var1) {
      return this.indexOf(var1);
   }

   @Override
   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0;
   }

   @GwtIncompatible
   @Override
   ImmutableList<E> subListUnchecked(int var1, int var2) {
      ImmutableList var3 = super.subListUnchecked(var1, var2);
      return new RegularImmutableSortedSet<>(var3, this.comparator()).asList();
   }

   @Override
   public Spliterator<E> spliterator() {
      return CollectSpliterators.indexed(this.size(), 1301, this.delegateList()::get, this.comparator());
   }
}
