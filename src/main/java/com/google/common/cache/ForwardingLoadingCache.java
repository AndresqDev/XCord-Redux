package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingLoadingCache<K, V> extends ForwardingCache<K, V> implements LoadingCache<K, V> {
   protected ForwardingLoadingCache() {
   }

   protected abstract LoadingCache<K, V> delegate();

   @Override
   public V get(K var1) throws ExecutionException {
      return this.delegate().get((K)var1);
   }

   @Override
   public V getUnchecked(K var1) {
      return this.delegate().getUnchecked((K)var1);
   }

   @Override
   public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      return this.delegate().getAll(var1);
   }

   @Override
   public V apply(K var1) {
      return this.delegate().apply((K)var1);
   }

   @Override
   public void refresh(K var1) {
      this.delegate().refresh((K)var1);
   }

   public abstract static class SimpleForwardingLoadingCache<K, V> extends ForwardingLoadingCache<K, V> {
      private final LoadingCache<K, V> delegate;

      protected SimpleForwardingLoadingCache(LoadingCache<K, V> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      protected final LoadingCache<K, V> delegate() {
         return this.delegate;
      }
   }
}
