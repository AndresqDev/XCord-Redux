package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V> {
   private static final long serialVersionUID = 430848587173315748L;

   protected AbstractSortedSetMultimap(Map<K, Collection<V>> var1) {
      super(var1);
   }

   abstract SortedSet<V> createCollection();

   SortedSet<V> createUnmodifiableEmptyCollection() {
      return this.unmodifiableCollectionSubclass(this.createCollection());
   }

   <E> SortedSet<E> unmodifiableCollectionSubclass(Collection<E> var1) {
      return (SortedSet<E>)(var1 instanceof NavigableSet
         ? Sets.unmodifiableNavigableSet((NavigableSet<E>)var1)
         : Collections.unmodifiableSortedSet((SortedSet<E>)var1));
   }

   @Override
   Collection<V> wrapCollection(@ParametricNullness K var1, Collection<V> var2) {
      return (Collection<V>)(var2 instanceof NavigableSet
         ? new AbstractMapBasedMultimap.WrappedNavigableSet(var1, (NavigableSet<V>)var2, null)
         : new AbstractMapBasedMultimap.WrappedSortedSet(var1, (SortedSet<V>)var2, null));
   }

   @Override
   public SortedSet<V> get(@ParametricNullness K var1) {
      return (SortedSet<V>)super.get((K)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public SortedSet<V> removeAll(Object var1) {
      return (SortedSet<V>)super.removeAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public SortedSet<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return (SortedSet<V>)super.replaceValues((K)var1, var2);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   @Override
   public Collection<V> values() {
      return super.values();
   }
}
