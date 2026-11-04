package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMultimap<K, V> extends ForwardingObject implements Multimap<K, V> {
   protected ForwardingMultimap() {
   }

   protected abstract Multimap<K, V> delegate();

   @Override
   public Map<K, Collection<V>> asMap() {
      return this.delegate().asMap();
   }

   @Override
   public void clear() {
      this.delegate().clear();
   }

   @Override
   public boolean containsEntry(Object var1, Object var2) {
      return this.delegate().containsEntry(var1, var2);
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.delegate().containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.delegate().containsValue(var1);
   }

   @Override
   public Collection<Entry<K, V>> entries() {
      return this.delegate().entries();
   }

   @Override
   public Collection<V> get(@ParametricNullness K var1) {
      return this.delegate().get((K)var1);
   }

   @Override
   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   @Override
   public Multiset<K> keys() {
      return this.delegate().keys();
   }

   @Override
   public Set<K> keySet() {
      return this.delegate().keySet();
   }

   @CanIgnoreReturnValue
   @Override
   public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.delegate().put((K)var1, (V)var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return this.delegate().putAll((K)var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean putAll(Multimap<? extends K, ? extends V> var1) {
      return this.delegate().putAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean remove(Object var1, Object var2) {
      return this.delegate().remove(var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public Collection<V> removeAll(Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public Collection<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues((K)var1, var2);
   }

   @Override
   public int size() {
      return this.delegate().size();
   }

   @Override
   public Collection<V> values() {
      return this.delegate().values();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }
}
