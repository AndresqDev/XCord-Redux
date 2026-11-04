package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class ReferenceMap<K, V> implements Map<K, V> {
   private final Map<K, Reference<V>> delegate;

   protected ReferenceMap(Map<K, Reference<V>> var1) {
      this.delegate = var1;
   }

   abstract Reference<V> fold(V var1);

   private V unfold(Reference<V> var1) {
      return (V)(var1 == null ? null : var1.get());
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.delegate.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V get(Object var1) {
      return this.unfold(this.delegate.get(var1));
   }

   @Override
   public V put(K var1, V var2) {
      return this.unfold(this.delegate.put((K)var1, this.fold((V)var2)));
   }

   @Override
   public V remove(Object var1) {
      return this.unfold(this.delegate.remove(var1));
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.delegate.put((K)var3.getKey(), this.fold((V)var3.getValue()));
      }
   }

   @Override
   public void clear() {
      this.delegate.clear();
   }

   @Override
   public Set<K> keySet() {
      return this.delegate.keySet();
   }

   @Override
   public Collection<V> values() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      throw new UnsupportedOperationException();
   }
}
