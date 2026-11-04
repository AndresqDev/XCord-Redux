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
public interface SortedMultiset<E> extends SortedIterable<E>, SortedMultisetBridge<E> {
   @Override
   Comparator<? super E> comparator();

   Multiset.Entry<E> firstEntry();

   Multiset.Entry<E> lastEntry();

   Multiset.Entry<E> pollFirstEntry();

   Multiset.Entry<E> pollLastEntry();

   NavigableSet<E> elementSet();

   @Override
   Set<Multiset.Entry<E>> entrySet();

   @Override
   Iterator<E> iterator();

   SortedMultiset<E> descendingMultiset();

   SortedMultiset<E> headMultiset(@ParametricNullness E var1, BoundType var2);

   SortedMultiset<E> subMultiset(@ParametricNullness E var1, BoundType var2, @ParametricNullness E var3, BoundType var4);

   SortedMultiset<E> tailMultiset(@ParametricNullness E var1, BoundType var2);
}
