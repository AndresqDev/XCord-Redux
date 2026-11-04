package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingCache<K, V> extends ForwardingObject implements Cache<K, V> {
   protected ForwardingCache() {
   }

   protected abstract Cache<K, V> delegate();

   @Override
   public V getIfPresent(Object var1) {
      return this.delegate().getIfPresent(var1);
   }

   @Override
   public V get(K var1, Callable<? extends V> var2) throws ExecutionException {
      return this.delegate().get((K)var1, var2);
   }

   @Override
   public ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> var1) {
      return this.delegate().getAllPresent(var1);
   }

   @Override
   public void put(K var1, V var2) {
      this.delegate().put((K)var1, (V)var2);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      this.delegate().putAll(var1);
   }

   @Override
   public void invalidate(Object var1) {
      this.delegate().invalidate(var1);
   }

   @Override
   public void invalidateAll(Iterable<? extends Object> var1) {
      this.delegate().invalidateAll(var1);
   }

   @Override
   public void invalidateAll() {
      this.delegate().invalidateAll();
   }

   @Override
   public long size() {
      return this.delegate().size();
   }

   @Override
   public CacheStats stats() {
      return this.delegate().stats();
   }

   @Override
   public ConcurrentMap<K, V> asMap() {
      return this.delegate().asMap();
   }

   @Override
   public void cleanUp() {
      this.delegate().cleanUp();
   }

   public abstract static class SimpleForwardingCache<K, V> extends ForwardingCache<K, V> {
      private final Cache<K, V> delegate;

      protected SimpleForwardingCache(Cache<K, V> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      protected final Cache<K, V> delegate() {
         return this.delegate;
      }
   }
}
