package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@DoNotMock("Use CacheBuilder.newBuilder().build()")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Cache<K, V> {
   V getIfPresent(@CompatibleWith("K") Object var1);

   V get(K var1, Callable<? extends V> var2) throws ExecutionException;

   ImmutableMap<K, V> getAllPresent(Iterable<? extends Object> var1);

   void put(K var1, V var2);

   void putAll(Map<? extends K, ? extends V> var1);

   void invalidate(@CompatibleWith("K") Object var1);

   void invalidateAll(Iterable<? extends Object> var1);

   void invalidateAll();

   @CheckReturnValue
   long size();

   @CheckReturnValue
   CacheStats stats();

   @CheckReturnValue
   ConcurrentMap<K, V> asMap();

   void cleanUp();
}
