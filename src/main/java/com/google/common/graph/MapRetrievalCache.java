package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Map;

@ElementTypesAreNonnullByDefault
final class MapRetrievalCache<K, V> extends MapIteratorCache<K, V> {
   private transient volatile MapRetrievalCache.CacheEntry<K, V> cacheEntry1;
   private transient volatile MapRetrievalCache.CacheEntry<K, V> cacheEntry2;

   MapRetrievalCache(Map<K, V> var1) {
      super(var1);
   }

   @Override
   V get(Object var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.getIfCached(var1);
      if (var2 != null) {
         return (V)var2;
      } else {
         var2 = this.getWithoutCaching(var1);
         if (var2 != null) {
            this.addToCache((K)var1, (V)var2);
         }

         return (V)var2;
      }
   }

   @Override
   V getIfCached(Object var1) {
      Object var2 = super.getIfCached(var1);
      if (var2 != null) {
         return (V)var2;
      } else {
         MapRetrievalCache.CacheEntry var3 = this.cacheEntry1;
         if (var3 != null && var3.key == var1) {
            return var3.value;
         } else {
            var3 = this.cacheEntry2;
            if (var3 != null && var3.key == var1) {
               this.addToCache(var3);
               return var3.value;
            } else {
               return null;
            }
         }
      }
   }

   @Override
   void clearCache() {
      super.clearCache();
      this.cacheEntry1 = null;
      this.cacheEntry2 = null;
   }

   private void addToCache(K var1, V var2) {
      this.addToCache(new MapRetrievalCache.CacheEntry<>((K)var1, (V)var2));
   }

   private void addToCache(MapRetrievalCache.CacheEntry<K, V> var1) {
      this.cacheEntry2 = this.cacheEntry1;
      this.cacheEntry1 = var1;
   }

   private static final class CacheEntry<K, V> {
      final K key;
      final V value;

      CacheEntry(K var1, V var2) {
         this.key = (K)var1;
         this.value = (V)var2;
      }
   }
}
