package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
class MapIteratorCache<K, V> {
   private final Map<K, V> backingMap;
   private transient volatile Entry<K, V> cacheEntry;

   MapIteratorCache(Map<K, V> var1) {
      this.backingMap = Preconditions.checkNotNull(var1);
   }

   @CanIgnoreReturnValue
   final V put(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      this.clearCache();
      return this.backingMap.put((K)var1, (V)var2);
   }

   @CanIgnoreReturnValue
   final V remove(Object var1) {
      Preconditions.checkNotNull(var1);
      this.clearCache();
      return this.backingMap.remove(var1);
   }

   final void clear() {
      this.clearCache();
      this.backingMap.clear();
   }

   V get(Object var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.getIfCached(var1);
      return (V)(var2 == null ? this.getWithoutCaching(var1) : var2);
   }

   final V getWithoutCaching(Object var1) {
      Preconditions.checkNotNull(var1);
      return this.backingMap.get(var1);
   }

   final boolean containsKey(Object var1) {
      return this.getIfCached(var1) != null || this.backingMap.containsKey(var1);
   }

   final Set<K> unmodifiableKeySet() {
      return new AbstractSet<K>() {
         public UnmodifiableIterator<K> iterator() {
            final Iterator var1 = MapIteratorCache.this.backingMap.entrySet().iterator();
            return new UnmodifiableIterator<K>() {
               @Override
               public boolean hasNext() {
                  return var1.hasNext();
               }

               @Override
               public K next() {
                  Entry var1x = (Entry)var1.next();
                  MapIteratorCache.this.cacheEntry = var1x;
                  return (K)var1x.getKey();
               }
            };
         }

         @Override
         public int size() {
            return MapIteratorCache.this.backingMap.size();
         }

         @Override
         public boolean contains(Object var1) {
            return MapIteratorCache.this.containsKey(var1);
         }
      };
   }

   V getIfCached(Object var1) {
      Entry var2 = this.cacheEntry;
      return (V)(var2 != null && var2.getKey() == var1 ? var2.getValue() : null);
   }

   void clearCache() {
      this.cacheEntry = null;
   }
}
