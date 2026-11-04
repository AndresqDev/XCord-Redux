package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractCache<K, V> implements Cache<K, V> {
   protected AbstractCache() {
   }

   @Override
   public V get(K var1, Callable<? extends V> var2) throws ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   public ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> var1) {
      LinkedHashMap var2 = Maps.newLinkedHashMap();

      for (Object var4 : var1) {
         if (!var2.containsKey(var4)) {
            Object var6 = this.getIfPresent(var4);
            if (var6 != null) {
               var2.put(var4, var6);
            }
         }
      }

      return ImmutableMap.copyOf(var2);
   }

   @Override
   public void put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public void cleanUp() {
   }

   @Override
   public long size() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void invalidate(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void invalidateAll(Iterable<? extends Object> var1) {
      for (Object var3 : var1) {
         this.invalidate(var3);
      }
   }

   @Override
   public void invalidateAll() {
      throw new UnsupportedOperationException();
   }

   @Override
   public CacheStats stats() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ConcurrentMap<K, V> asMap() {
      throw new UnsupportedOperationException();
   }

   public static final class SimpleStatsCounter implements AbstractCache.StatsCounter {
      private final LongAddable hitCount = LongAddables.create();
      private final LongAddable missCount = LongAddables.create();
      private final LongAddable loadSuccessCount = LongAddables.create();
      private final LongAddable loadExceptionCount = LongAddables.create();
      private final LongAddable totalLoadTime = LongAddables.create();
      private final LongAddable evictionCount = LongAddables.create();

      @Override
      public void recordHits(int var1) {
         this.hitCount.add((long)var1);
      }

      @Override
      public void recordMisses(int var1) {
         this.missCount.add((long)var1);
      }

      @Override
      public void recordLoadSuccess(long var1) {
         this.loadSuccessCount.increment();
         this.totalLoadTime.add(var1);
      }

      @Override
      public void recordLoadException(long var1) {
         this.loadExceptionCount.increment();
         this.totalLoadTime.add(var1);
      }

      @Override
      public void recordEviction() {
         this.evictionCount.increment();
      }

      @Override
      public CacheStats snapshot() {
         return new CacheStats(
            negativeToMaxValue(this.hitCount.sum()),
            negativeToMaxValue(this.missCount.sum()),
            negativeToMaxValue(this.loadSuccessCount.sum()),
            negativeToMaxValue(this.loadExceptionCount.sum()),
            negativeToMaxValue(this.totalLoadTime.sum()),
            negativeToMaxValue(this.evictionCount.sum())
         );
      }

      private static long negativeToMaxValue(long var0) {
         return var0 >= 0L ? var0 : Long.MAX_VALUE;
      }

      public void incrementBy(AbstractCache.StatsCounter var1) {
         CacheStats var2 = var1.snapshot();
         this.hitCount.add(var2.hitCount());
         this.missCount.add(var2.missCount());
         this.loadSuccessCount.add(var2.loadSuccessCount());
         this.loadExceptionCount.add(var2.loadExceptionCount());
         this.totalLoadTime.add(var2.totalLoadTime());
         this.evictionCount.add(var2.evictionCount());
      }
   }

   public interface StatsCounter {
      void recordHits(int var1);

      void recordMisses(int var1);

      void recordLoadSuccess(long var1);

      void recordLoadException(long var1);

      void recordEviction();

      CacheStats snapshot();
   }
}
