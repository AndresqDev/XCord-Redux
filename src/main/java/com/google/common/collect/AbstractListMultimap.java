package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {
   private static final long serialVersionUID = 6588350623831699109L;

   protected AbstractListMultimap(Map<K, Collection<V>> var1) {
      super(var1);
   }

   abstract List<V> createCollection();

   List<V> createUnmodifiableEmptyCollection() {
      return Collections.emptyList();
   }

   @Override
   <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> var1) {
      return Collections.unmodifiableList((List<? extends E>)var1);
   }

   @Override
   Collection<V> wrapCollection(@ParametricNullness K var1, Collection<V> var2) {
      return this.wrapList((K)var1, (List<V>)var2, null);
   }

   @Override
   public List<V> get(@ParametricNullness K var1) {
      return (List<V>)super.get((K)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> removeAll(Object var1) {
      return (List<V>)super.removeAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return (List<V>)super.replaceValues((K)var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
      return super.put((K)var1, (V)var2);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   @Override
   public boolean equals(Object var1) {
      return super.equals(var1);
   }
}
