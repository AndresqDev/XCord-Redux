package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

@GwtCompatible(
   emulated = true
)
class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final int MAX_SEGMENTS = 65536;
   static final int CONTAINS_VALUE_RETRIES = 3;
   static final int DRAIN_THRESHOLD = 63;
   static final int DRAIN_MAX = 16;
   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
   final int segmentMask;
   final int segmentShift;
   final LocalCache.Segment<K, V>[] segments;
   final int concurrencyLevel;
   final Equivalence<Object> keyEquivalence;
   final Equivalence<Object> valueEquivalence;
   final LocalCache.Strength keyStrength;
   final LocalCache.Strength valueStrength;
   final long maxWeight;
   final Weigher<K, V> weigher;
   final long expireAfterAccessNanos;
   final long expireAfterWriteNanos;
   final long refreshNanos;
   final Queue<RemovalNotification<K, V>> removalNotificationQueue;
   final RemovalListener<K, V> removalListener;
   final Ticker ticker;
   final LocalCache.EntryFactory entryFactory;
   final AbstractCache.StatsCounter globalStatsCounter;
   @Nullable
   final CacheLoader<? super K, V> defaultLoader;
   static final LocalCache.ValueReference<Object, Object> UNSET = new LocalCache.ValueReference<Object, Object>() {
      @Override
      public Object get() {
         return null;
      }

      @Override
      public int getWeight() {
         return 0;
      }

      @Override
      public ReferenceEntry<Object, Object> getEntry() {
         return null;
      }

      @Override
      public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> var1, @Nullable Object var2, ReferenceEntry<Object, Object> var3) {
         return this;
      }

      @Override
      public boolean isLoading() {
         return false;
      }

      @Override
      public boolean isActive() {
         return false;
      }

      @Override
      public Object waitForValue() {
         return null;
      }

      @Override
      public void notifyNewValue(Object var1) {
      }
   };
   static final Queue<?> DISCARDING_QUEUE = new AbstractQueue<Object>() {
      @Override
      public boolean offer(Object var1) {
         return true;
      }

      @Override
      public Object peek() {
         return null;
      }

      @Override
      public Object poll() {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public Iterator<Object> iterator() {
         return ImmutableSet.<Object>of().iterator();
      }
   };
   @RetainedWith
   @Nullable
   Set<K> keySet;
   @RetainedWith
   @Nullable
   Collection<V> values;
   @RetainedWith
   @Nullable
   Set<Entry<K, V>> entrySet;

   LocalCache(CacheBuilder<? super K, ? super V> var1, @Nullable CacheLoader<? super K, V> var2) {
      this.concurrencyLevel = Math.min(var1.getConcurrencyLevel(), 65536);
      this.keyStrength = var1.getKeyStrength();
      this.valueStrength = var1.getValueStrength();
      this.keyEquivalence = var1.getKeyEquivalence();
      this.valueEquivalence = var1.getValueEquivalence();
      this.maxWeight = var1.getMaximumWeight();
      this.weigher = var1.getWeigher();
      this.expireAfterAccessNanos = var1.getExpireAfterAccessNanos();
      this.expireAfterWriteNanos = var1.getExpireAfterWriteNanos();
      this.refreshNanos = var1.getRefreshNanos();
      this.removalListener = var1.getRemovalListener();
      this.removalNotificationQueue = (Queue<RemovalNotification<K, V>>)(this.removalListener == CacheBuilder.NullListener.INSTANCE
         ? discardingQueue()
         : new ConcurrentLinkedQueue<>());
      this.ticker = var1.getTicker(this.recordsTime());
      this.entryFactory = LocalCache.EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
      this.globalStatsCounter = var1.getStatsCounterSupplier().get();
      this.defaultLoader = var2;
      int var3 = Math.min(var1.getInitialCapacity(), 1073741824);
      if (this.evictsBySize() && !this.customWeigher()) {
         var3 = (int)Math.min((long)var3, this.maxWeight);
      }

      int var4 = 0;

      byte var5;
      for (var5 = 1; var5 < this.concurrencyLevel && (!this.evictsBySize() || (long)(var5 * 20) <= this.maxWeight); var5 <<= 1) {
         var4++;
      }

      this.segmentShift = 32 - var4;
      this.segmentMask = var5 - 1;
      this.segments = this.newSegmentArray(var5);
      int var6 = var3 / var5;
      if (var6 * var5 < var3) {
         var6++;
      }

      byte var7 = 1;

      while (var7 < var6) {
         var7 <<= 1;
      }

      if (this.evictsBySize()) {
         long var8 = this.maxWeight / (long)var5 + 1L;
         long var10 = this.maxWeight % (long)var5;

         for (int var12 = 0; var12 < this.segments.length; var12++) {
            if ((long)var12 == var10) {
               var8--;
            }

            this.segments[var12] = this.createSegment(var7, var8, var1.getStatsCounterSupplier().get());
         }
      } else {
         for (int var13 = 0; var13 < this.segments.length; var13++) {
            this.segments[var13] = this.createSegment(var7, -1L, var1.getStatsCounterSupplier().get());
         }
      }
   }

   boolean evictsBySize() {
      return this.maxWeight >= 0L;
   }

   boolean customWeigher() {
      return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
   }

   boolean expires() {
      return this.expiresAfterWrite() || this.expiresAfterAccess();
   }

   boolean expiresAfterWrite() {
      return this.expireAfterWriteNanos > 0L;
   }

   boolean expiresAfterAccess() {
      return this.expireAfterAccessNanos > 0L;
   }

   boolean refreshes() {
      return this.refreshNanos > 0L;
   }

   boolean usesAccessQueue() {
      return this.expiresAfterAccess() || this.evictsBySize();
   }

   boolean usesWriteQueue() {
      return this.expiresAfterWrite();
   }

   boolean recordsWrite() {
      return this.expiresAfterWrite() || this.refreshes();
   }

   boolean recordsAccess() {
      return this.expiresAfterAccess();
   }

   boolean recordsTime() {
      return this.recordsWrite() || this.recordsAccess();
   }

   boolean usesWriteEntries() {
      return this.usesWriteQueue() || this.recordsWrite();
   }

   boolean usesAccessEntries() {
      return this.usesAccessQueue() || this.recordsAccess();
   }

   boolean usesKeyReferences() {
      return this.keyStrength != LocalCache.Strength.STRONG;
   }

   boolean usesValueReferences() {
      return this.valueStrength != LocalCache.Strength.STRONG;
   }

   static <K, V> LocalCache.ValueReference<K, V> unset() {
      return (LocalCache.ValueReference<K, V>)UNSET;
   }

   static <K, V> ReferenceEntry<K, V> nullEntry() {
      return LocalCache.NullEntry.INSTANCE;
   }

   static <E> Queue<E> discardingQueue() {
      return (Queue<E>)DISCARDING_QUEUE;
   }

   static int rehash(int var0) {
      var0 += var0 << 15 ^ -12931;
      var0 ^= var0 >>> 10;
      var0 += var0 << 3;
      var0 ^= var0 >>> 6;
      var0 += (var0 << 2) + (var0 << 14);
      return var0 ^ var0 >>> 16;
   }

   @VisibleForTesting
   ReferenceEntry<K, V> newEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
      LocalCache.Segment var4 = this.segmentFor(var2);
      var4.lock();

      ReferenceEntry var5;
      try {
         var5 = var4.newEntry(var1, var2, var3);
      } finally {
         var4.unlock();
      }

      return var5;
   }

   @VisibleForTesting
   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2) {
      int var3 = var1.getHash();
      return this.segmentFor(var3).copyEntry(var1, var2);
   }

   @VisibleForTesting
   LocalCache.ValueReference<K, V> newValueReference(ReferenceEntry<K, V> var1, V var2, int var3) {
      int var4 = var1.getHash();
      return this.valueStrength.referenceValue(this.segmentFor(var4), var1, Preconditions.checkNotNull((V)var2), var3);
   }

   int hash(@Nullable Object var1) {
      int var2 = this.keyEquivalence.hash(var1);
      return rehash(var2);
   }

   void reclaimValue(LocalCache.ValueReference<K, V> var1) {
      ReferenceEntry var2 = var1.getEntry();
      int var3 = var2.getHash();
      this.segmentFor(var3).reclaimValue((K)var2.getKey(), var3, var1);
   }

   void reclaimKey(ReferenceEntry<K, V> var1) {
      int var2 = var1.getHash();
      this.segmentFor(var2).reclaimKey(var1, var2);
   }

   @VisibleForTesting
   boolean isLive(ReferenceEntry<K, V> var1, long var2) {
      return this.segmentFor(var1.getHash()).getLiveValue(var1, var2) != null;
   }

   LocalCache.Segment<K, V> segmentFor(int var1) {
      return this.segments[var1 >>> this.segmentShift & this.segmentMask];
   }

   LocalCache.Segment<K, V> createSegment(int var1, long var2, AbstractCache.StatsCounter var4) {
      return new LocalCache.Segment<>(this, var1, var2, var4);
   }

   @Nullable
   V getLiveValue(ReferenceEntry<K, V> var1, long var2) {
      if (var1.getKey() == null) {
         return null;
      } else {
         Object var4 = var1.getValueReference().get();
         if (var4 == null) {
            return null;
         } else {
            return (V)(this.isExpired(var1, var2) ? null : var4);
         }
      }
   }

   boolean isExpired(ReferenceEntry<K, V> var1, long var2) {
      Preconditions.checkNotNull(var1);
      return this.expiresAfterAccess() && var2 - var1.getAccessTime() >= this.expireAfterAccessNanos
         ? true
         : this.expiresAfterWrite() && var2 - var1.getWriteTime() >= this.expireAfterWriteNanos;
   }

   static <K, V> void connectAccessOrder(ReferenceEntry<K, V> var0, ReferenceEntry<K, V> var1) {
      var0.setNextInAccessQueue(var1);
      var1.setPreviousInAccessQueue(var0);
   }

   static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> var0) {
      ReferenceEntry var1 = nullEntry();
      var0.setNextInAccessQueue(var1);
      var0.setPreviousInAccessQueue(var1);
   }

   static <K, V> void connectWriteOrder(ReferenceEntry<K, V> var0, ReferenceEntry<K, V> var1) {
      var0.setNextInWriteQueue(var1);
      var1.setPreviousInWriteQueue(var0);
   }

   static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> var0) {
      ReferenceEntry var1 = nullEntry();
      var0.setNextInWriteQueue(var1);
      var0.setPreviousInWriteQueue(var1);
   }

   void processPendingNotifications() {
      RemovalNotification var1;
      while ((var1 = this.removalNotificationQueue.poll()) != null) {
         try {
            this.removalListener.onRemoval(var1);
         } catch (Throwable var3) {
            logger.log(Level.WARNING, "Exception thrown by removal listener", var3);
         }
      }
   }

   final LocalCache.Segment<K, V>[] newSegmentArray(int var1) {
      return new LocalCache.Segment[var1];
   }

   public void cleanUp() {
      for (LocalCache.Segment var4 : this.segments) {
         var4.cleanUp();
      }
   }

   @Override
   public boolean isEmpty() {
      long var1 = 0L;
      LocalCache.Segment[] var3 = this.segments;

      for (int var4 = 0; var4 < var3.length; var4++) {
         if (var3[var4].count != 0) {
            return false;
         }

         var1 += (long)var3[var4].modCount;
      }

      if (var1 != 0L) {
         for (int var5 = 0; var5 < var3.length; var5++) {
            if (var3[var5].count != 0) {
               return false;
            }

            var1 -= (long)var3[var5].modCount;
         }

         return var1 == 0L;
      } else {
         return true;
      }
   }

   long longSize() {
      LocalCache.Segment[] var1 = this.segments;
      long var2 = 0L;

      for (int var4 = 0; var4 < var1.length; var4++) {
         var2 += (long)var1[var4].count;
      }

      return var2;
   }

   @Override
   public int size() {
      return Ints.saturatedCast(this.longSize());
   }

   @Nullable
   @Override
   public V get(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).get(var1, var2);
      }
   }

   V get(K var1, CacheLoader<? super K, V> var2) throws ExecutionException {
      int var3 = this.hash(Preconditions.checkNotNull(var1));
      return this.segmentFor(var3).get((K)var1, var3, var2);
   }

   @Nullable
   public V getIfPresent(Object var1) {
      int var2 = this.hash(Preconditions.checkNotNull(var1));
      Object var3 = this.segmentFor(var2).get(var1, var2);
      if (var3 == null) {
         this.globalStatsCounter.recordMisses(1);
      } else {
         this.globalStatsCounter.recordHits(1);
      }

      return (V)var3;
   }

   @Nullable
   @Override
   public V getOrDefault(@Nullable Object var1, @Nullable V var2) {
      Object var3 = this.get(var1);
      return (V)(var3 != null ? var3 : var2);
   }

   V getOrLoad(K var1) throws ExecutionException {
      return this.get((K)var1, this.defaultLoader);
   }

   ImmutableMap<K, V> getAllPresent(Iterable<?> var1) {
      int var2 = 0;
      int var3 = 0;
      LinkedHashMap var4 = Maps.newLinkedHashMap();

      for (Object var6 : var1) {
         Object var7 = this.get(var6);
         if (var7 == null) {
            var3++;
         } else {
            var4.put(var6, var7);
            var2++;
         }
      }

      this.globalStatsCounter.recordHits(var2);
      this.globalStatsCounter.recordMisses(var3);
      return ImmutableMap.copyOf(var4);
   }

   ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      int var2 = 0;
      int var3 = 0;
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      LinkedHashSet var5 = Sets.newLinkedHashSet();

      for (Object var7 : var1) {
         Object var8 = this.get(var7);
         if (!var4.containsKey(var7)) {
            var4.put(var7, var8);
            if (var8 == null) {
               var3++;
               var5.add(var7);
            } else {
               var2++;
            }
         }
      }

      ImmutableMap var17;
      try {
         if (!var5.isEmpty()) {
            try {
               Map var16 = this.loadAll(var5, this.defaultLoader);

               for (Object var21 : var5) {
                  Object var9 = var16.get(var21);
                  if (var9 == null) {
                     String var10 = String.valueOf(var21);
                     throw new CacheLoader.InvalidCacheLoadException(
                        new StringBuilder(37 + String.valueOf(var10).length()).append("loadAll failed to return a value for ").append(var10).toString()
                     );
                  }

                  var4.put(var21, var9);
               }
            } catch (CacheLoader.UnsupportedLoadingOperationException var14) {
               for (Object var20 : var5) {
                  var3--;
                  var4.put(var20, this.get((K)var20, this.defaultLoader));
               }
            }
         }

         var17 = ImmutableMap.copyOf(var4);
      } finally {
         this.globalStatsCounter.recordHits(var2);
         this.globalStatsCounter.recordMisses(var3);
      }

      return var17;
   }

   @Nullable
   Map<K, V> loadAll(Set<? extends K> var1, CacheLoader<? super K, V> var2) throws ExecutionException {
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var1);
      Stopwatch var3 = Stopwatch.createStarted();
      boolean var5 = false;

      Map var4;
      try {
         Map var6 = var2.loadAll(var1);
         var4 = var6;
         var5 = true;
      } catch (CacheLoader.UnsupportedLoadingOperationException var17) {
         var5 = true;
         throw var17;
      } catch (InterruptedException var18) {
         Thread.currentThread().interrupt();
         throw new ExecutionException(var18);
      } catch (RuntimeException var19) {
         throw new UncheckedExecutionException(var19);
      } catch (Exception var20) {
         throw new ExecutionException(var20);
      } catch (Error var21) {
         throw new ExecutionError(var21);
      } finally {
         if (!var5) {
            this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
         }
      }

      if (var4 == null) {
         this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
         String var24 = String.valueOf(var2);
         throw new CacheLoader.InvalidCacheLoadException(
            new StringBuilder(31 + String.valueOf(var24).length()).append(var24).append(" returned null map from loadAll").toString()
         );
      } else {
         var3.stop();
         boolean var23 = false;

         for (Entry var8 : var4.entrySet()) {
            Object var9 = var8.getKey();
            Object var10 = var8.getValue();
            if (var9 != null && var10 != null) {
               this.put((K)var9, (V)var10);
            } else {
               var23 = true;
            }
         }

         if (var23) {
            this.globalStatsCounter.recordLoadException(var3.elapsed(TimeUnit.NANOSECONDS));
            String var25 = String.valueOf(var2);
            throw new CacheLoader.InvalidCacheLoadException(
               new StringBuilder(42 + String.valueOf(var25).length()).append(var25).append(" returned null keys or values from loadAll").toString()
            );
         } else {
            this.globalStatsCounter.recordLoadSuccess(var3.elapsed(TimeUnit.NANOSECONDS));
            return var4;
         }
      }
   }

   ReferenceEntry<K, V> getEntry(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).getEntry(var1, var2);
      }
   }

   void refresh(K var1) {
      int var2 = this.hash(Preconditions.checkNotNull(var1));
      this.segmentFor(var2).refresh((K)var1, var2, this.defaultLoader, false);
   }

   @Override
   public boolean containsKey(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).containsKey(var1, var2);
      }
   }

   @Override
   public boolean containsValue(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else {
         long var2 = this.ticker.read();
         LocalCache.Segment[] var4 = this.segments;
         long var5 = -1L;

         for (int var7 = 0; var7 < 3; var7++) {
            long var8 = 0L;

            for (LocalCache.Segment var13 : var4) {
               int var14 = var13.count;
               AtomicReferenceArray var15 = var13.table;

               for (int var16 = 0; var16 < var15.length(); var16++) {
                  for (ReferenceEntry var17 = (ReferenceEntry)var15.get(var16); var17 != null; var17 = var17.getNext()) {
                     Object var18 = var13.getLiveValue(var17, var2);
                     if (var18 != null && this.valueEquivalence.equivalent(var1, var18)) {
                        return true;
                     }
                  }
               }

               var8 += (long)var13.modCount;
            }

            if (var8 == var5) {
               break;
            }

            var5 = var8;
         }

         return false;
      }
   }

   @Override
   public V put(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put((K)var1, var3, (V)var2, false);
   }

   @Override
   public V putIfAbsent(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put((K)var1, var3, (V)var2, true);
   }

   @Override
   public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).compute((K)var1, var3, var2);
   }

   @Override
   public V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.compute(var1, (var2x, var3) -> var3 == null ? (V)var2.apply(var1) : var3);
   }

   @Override
   public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.compute((K)var1, (var1x, var2x) -> (V)(var2x == null ? null : var2.apply(var1x, var2x)));
   }

   @Override
   public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return this.compute((K)var1, (var2x, var3x) -> (V)(var3x == null ? var2 : var3.apply(var3x, var2)));
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public V remove(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).remove(var1, var2);
      }
   }

   @Override
   public boolean remove(@Nullable Object var1, @Nullable Object var2) {
      if (var1 != null && var2 != null) {
         int var3 = this.hash(var1);
         return this.segmentFor(var3).remove(var1, var3, var2);
      } else {
         return false;
      }
   }

   @Override
   public boolean replace(K var1, @Nullable V var2, V var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      if (var2 == null) {
         return false;
      } else {
         int var4 = this.hash(var1);
         return this.segmentFor(var4).replace((K)var1, var4, (V)var2, (V)var3);
      }
   }

   @Override
   public V replace(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).replace((K)var1, var3, (V)var2);
   }

   @Override
   public void clear() {
      for (LocalCache.Segment var4 : this.segments) {
         var4.clear();
      }
   }

   void invalidateAll(Iterable<?> var1) {
      for (Object var3 : var1) {
         this.remove(var3);
      }
   }

   @Override
   public Set<K> keySet() {
      Set var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new LocalCache.KeySet());
   }

   @Override
   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 != null ? var1 : (this.values = new LocalCache.Values());
   }

   @GwtIncompatible
   @Override
   public Set<Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new LocalCache.EntrySet());
   }

   private static <E> ArrayList<E> toArrayList(Collection<E> var0) {
      ArrayList var1 = new ArrayList(var0.size());
      Iterators.addAll(var1, var0.iterator());
      return var1;
   }

   boolean removeIf(BiPredicate<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      label27:
      for (Object var4 : this.keySet()) {
         Object var5;
         do {
            var5 = this.get(var4);
            if (var5 == null || !var1.test(var4, var5)) {
               continue label27;
            }
         } while (!this.remove(var4, var5));

         var2 = true;
      }

      return var2;
   }

   abstract class AbstractCacheSet<T> extends AbstractSet<T> {
      @Override
      public int size() {
         return LocalCache.this.size();
      }

      @Override
      public boolean isEmpty() {
         return LocalCache.this.isEmpty();
      }

      @Override
      public void clear() {
         LocalCache.this.clear();
      }

      @Override
      public Object[] toArray() {
         return LocalCache.toArrayList(this).toArray();
      }

      @Override
      public <E> E[] toArray(E[] var1) {
         return (E[])LocalCache.toArrayList(this).toArray(var1);
      }
   }

   abstract static class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V> {
      @Override
      public LocalCache.ValueReference<K, V> getValueReference() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getNext() {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getHash() {
         throw new UnsupportedOperationException();
      }

      @Override
      public K getKey() {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setAccessTime(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setWriteTime(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }
   }

   static final class AccessQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
      final ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>(this) {
         @Weak
         ReferenceEntry<K, V> nextAccess = this;
         @Weak
         ReferenceEntry<K, V> previousAccess = this;

         @Override
         public long getAccessTime() {
            return Long.MAX_VALUE;
         }

         @Override
         public void setAccessTime(long var1) {
         }

         @Override
         public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
         }

         @Override
         public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
            this.nextAccess = var1;
         }

         @Override
         public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
         }

         @Override
         public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
            this.previousAccess = var1;
         }
      };

      public boolean offer(ReferenceEntry<K, V> var1) {
         LocalCache.connectAccessOrder(var1.getPreviousInAccessQueue(), var1.getNextInAccessQueue());
         LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), var1);
         LocalCache.connectAccessOrder(var1, this.head);
         return true;
      }

      public ReferenceEntry<K, V> peek() {
         ReferenceEntry var1 = this.head.getNextInAccessQueue();
         return var1 == this.head ? null : var1;
      }

      public ReferenceEntry<K, V> poll() {
         ReferenceEntry var1 = this.head.getNextInAccessQueue();
         if (var1 == this.head) {
            return null;
         } else {
            this.remove(var1);
            return var1;
         }
      }

      @Override
      public boolean remove(Object var1) {
         ReferenceEntry var2 = (ReferenceEntry)var1;
         ReferenceEntry var3 = var2.getPreviousInAccessQueue();
         ReferenceEntry var4 = var2.getNextInAccessQueue();
         LocalCache.connectAccessOrder(var3, var4);
         LocalCache.nullifyAccessOrder(var2);
         return var4 != LocalCache.NullEntry.INSTANCE;
      }

      @Override
      public boolean contains(Object var1) {
         ReferenceEntry var2 = (ReferenceEntry)var1;
         return var2.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
      }

      @Override
      public boolean isEmpty() {
         return this.head.getNextInAccessQueue() == this.head;
      }

      @Override
      public int size() {
         int var1 = 0;

         for (ReferenceEntry var2 = this.head.getNextInAccessQueue(); var2 != this.head; var2 = var2.getNextInAccessQueue()) {
            var1++;
         }

         return var1;
      }

      @Override
      public void clear() {
         ReferenceEntry var1 = this.head.getNextInAccessQueue();

         while (var1 != this.head) {
            ReferenceEntry var2 = var1.getNextInAccessQueue();
            LocalCache.nullifyAccessOrder(var1);
            var1 = var2;
         }

         this.head.setNextInAccessQueue(this.head);
         this.head.setPreviousInAccessQueue(this.head);
      }

      @Override
      public Iterator<ReferenceEntry<K, V>> iterator() {
         return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
            protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> var1) {
               ReferenceEntry var2 = var1.getNextInAccessQueue();
               return var2 == AccessQueue.this.head ? null : var2;
            }
         };
      }
   }

   static class ComputingValueReference<K, V> extends LocalCache.LoadingValueReference<K, V> {
      ComputingValueReference(LocalCache.ValueReference<K, V> var1) {
         super(var1);
      }

      @Override
      public boolean isLoading() {
         return false;
      }
   }

   static enum EntryFactory {
      STRONG {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongEntry<>((K)var2, var3, var4);
         }
      },
      STRONG_ACCESS {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongAccessEntry<>((K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            return var4;
         }
      },
      STRONG_WRITE {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongWriteEntry<>((K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      STRONG_ACCESS_WRITE {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.StrongAccessWriteEntry<>((K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      WEAK {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakEntry<>(var1.keyReferenceQueue, (K)var2, var3, var4);
         }
      },
      WEAK_ACCESS {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakAccessEntry<>(var1.keyReferenceQueue, (K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            return var4;
         }
      },
      WEAK_WRITE {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakWriteEntry<>(var1.keyReferenceQueue, (K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      },
      WEAK_ACCESS_WRITE {
         @Override
         <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
            return new LocalCache.WeakAccessWriteEntry<>(var1.keyReferenceQueue, (K)var2, var3, var4);
         }

         @Override
         <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
            ReferenceEntry var4 = super.copyEntry(var1, var2, var3);
            this.copyAccessEntry(var2, var4);
            this.copyWriteEntry(var2, var4);
            return var4;
         }
      };

      static final int ACCESS_MASK = 1;
      static final int WRITE_MASK = 2;
      static final int WEAK_MASK = 4;
      static final LocalCache.EntryFactory[] factories = new LocalCache.EntryFactory[]{
         STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE
      };

      private EntryFactory() {
      }

      static LocalCache.EntryFactory getFactory(LocalCache.Strength var0, boolean var1, boolean var2) {
         int var3 = (var0 == LocalCache.Strength.WEAK ? 4 : 0) | (var1 ? 1 : 0) | (var2 ? 2 : 0);
         return factories[var3];
      }

      abstract <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4);

      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, ReferenceEntry<K, V> var3) {
         return this.newEntry(var1, (K)var2.getKey(), var2.getHash(), var3);
      }

      <K, V> void copyAccessEntry(ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2) {
         var2.setAccessTime(var1.getAccessTime());
         LocalCache.connectAccessOrder(var1.getPreviousInAccessQueue(), var2);
         LocalCache.connectAccessOrder(var2, var1.getNextInAccessQueue());
         LocalCache.nullifyAccessOrder(var1);
      }

      <K, V> void copyWriteEntry(ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2) {
         var2.setWriteTime(var1.getWriteTime());
         LocalCache.connectWriteOrder(var1.getPreviousInWriteQueue(), var2);
         LocalCache.connectWriteOrder(var2, var1.getNextInWriteQueue());
         LocalCache.nullifyWriteOrder(var1);
      }
   }

   final class EntryIterator extends LocalCache<K, V>.HashIterator<Entry<K, V>> {
      EntryIterator(LocalCache var1) {
      }

      public Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   final class EntrySet extends LocalCache<K, V>.AbstractCacheSet<Entry<K, V>> {
      @Override
      public Iterator<Entry<K, V>> iterator() {
         return LocalCache.this.new EntryIterator(LocalCache.this);
      }

      @Override
      public boolean removeIf(Predicate<? super Entry<K, V>> var1) {
         Preconditions.checkNotNull(var1);
         return LocalCache.this.removeIf((var1x, var2) -> var1.test(Maps.immutableEntry((K)var1x, (V)var2)));
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = var2.getKey();
            if (var3 == null) {
               return false;
            } else {
               Object var4 = LocalCache.this.get(var3);
               return var4 != null && LocalCache.this.valueEquivalence.equivalent(var2.getValue(), var4);
            }
         }
      }

      @Override
      public boolean remove(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = var2.getKey();
            return var3 != null && LocalCache.this.remove(var3, var2.getValue());
         }
      }
   }

   abstract class HashIterator<T> implements Iterator<T> {
      int nextSegmentIndex = LocalCache.this.segments.length - 1;
      int nextTableIndex = -1;
      @Nullable
      LocalCache.Segment<K, V> currentSegment;
      @Nullable
      AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
      @Nullable
      ReferenceEntry<K, V> nextEntry;
      LocalCache<K, V>.WriteThroughEntry nextExternal;
      LocalCache<K, V>.WriteThroughEntry lastReturned;

      HashIterator() {
         this.advance();
      }

      @Override
      public abstract T next();

      final void advance() {
         this.nextExternal = null;
         if (!this.nextInChain()) {
            if (!this.nextInTable()) {
               while (this.nextSegmentIndex >= 0) {
                  this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
                  if (this.currentSegment.count != 0) {
                     this.currentTable = this.currentSegment.table;
                     this.nextTableIndex = this.currentTable.length() - 1;
                     if (this.nextInTable()) {
                        return;
                     }
                  }
               }
            }
         }
      }

      boolean nextInChain() {
         if (this.nextEntry != null) {
            for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
               if (this.advanceTo(this.nextEntry)) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean nextInTable() {
         while (this.nextTableIndex >= 0) {
            if ((this.nextEntry = this.currentTable.get(this.nextTableIndex--)) != null && (this.advanceTo(this.nextEntry) || this.nextInChain())) {
               return true;
            }
         }

         return false;
      }

      boolean advanceTo(ReferenceEntry<K, V> var1) {
         boolean var6;
         try {
            long var2 = LocalCache.this.ticker.read();
            Object var4 = var1.getKey();
            Object var5 = LocalCache.this.getLiveValue(var1, var2);
            if (var5 == null) {
               return false;
            }

            this.nextExternal = LocalCache.this.new WriteThroughEntry(var4, var5);
            var6 = true;
         } finally {
            this.currentSegment.postReadCleanup();
         }

         return var6;
      }

      @Override
      public boolean hasNext() {
         return this.nextExternal != null;
      }

      LocalCache<K, V>.WriteThroughEntry nextEntry() {
         if (this.nextExternal == null) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.nextExternal;
            this.advance();
            return this.lastReturned;
         }
      }

      @Override
      public void remove() {
         Preconditions.checkState(this.lastReturned != null);
         LocalCache.this.remove(this.lastReturned.getKey());
         this.lastReturned = null;
      }
   }

   final class KeyIterator extends LocalCache<K, V>.HashIterator<K> {
      KeyIterator(LocalCache var1) {
      }

      @Override
      public K next() {
         return this.nextEntry().getKey();
      }
   }

   final class KeySet extends LocalCache<K, V>.AbstractCacheSet<K> {
      @Override
      public Iterator<K> iterator() {
         return LocalCache.this.new KeyIterator(LocalCache.this);
      }

      @Override
      public boolean contains(Object var1) {
         return LocalCache.this.containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         return LocalCache.this.remove(var1) != null;
      }
   }

   static final class LoadingSerializationProxy<K, V> extends LocalCache.ManualSerializationProxy<K, V> implements LoadingCache<K, V>, Serializable {
      private static final long serialVersionUID = 1L;
      @Nullable
      transient LoadingCache<K, V> autoDelegate;

      LoadingSerializationProxy(LocalCache<K, V> var1) {
         super(var1);
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         CacheBuilder var2 = this.recreateCacheBuilder();
         this.autoDelegate = var2.build(this.loader);
      }

      @Override
      public V get(K var1) throws ExecutionException {
         return this.autoDelegate.get((K)var1);
      }

      @Override
      public V getUnchecked(K var1) {
         return this.autoDelegate.getUnchecked((K)var1);
      }

      @Override
      public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
         return this.autoDelegate.getAll(var1);
      }

      @Override
      public final V apply(K var1) {
         return this.autoDelegate.apply((K)var1);
      }

      @Override
      public void refresh(K var1) {
         this.autoDelegate.refresh((K)var1);
      }

      private Object readResolve() {
         return this.autoDelegate;
      }
   }

   static class LoadingValueReference<K, V> implements LocalCache.ValueReference<K, V> {
      volatile LocalCache.ValueReference<K, V> oldValue;
      final SettableFuture<V> futureValue = SettableFuture.create();
      final Stopwatch stopwatch = Stopwatch.createUnstarted();

      public LoadingValueReference() {
         this(null);
      }

      public LoadingValueReference(LocalCache.ValueReference<K, V> var1) {
         this.oldValue = var1 == null ? LocalCache.unset() : var1;
      }

      @Override
      public boolean isLoading() {
         return true;
      }

      @Override
      public boolean isActive() {
         return this.oldValue.isActive();
      }

      @Override
      public int getWeight() {
         return this.oldValue.getWeight();
      }

      public boolean set(@Nullable V var1) {
         return this.futureValue.set((V)var1);
      }

      public boolean setException(Throwable var1) {
         return this.futureValue.setException(var1);
      }

      private ListenableFuture<V> fullyFailedFuture(Throwable var1) {
         return Futures.immediateFailedFuture(var1);
      }

      @Override
      public void notifyNewValue(@Nullable V var1) {
         if (var1 != null) {
            this.set((V)var1);
         } else {
            this.oldValue = LocalCache.unset();
         }
      }

      public ListenableFuture<V> loadFuture(K var1, CacheLoader<? super K, V> var2) {
         try {
            this.stopwatch.start();
            Object var3 = this.oldValue.get();
            if (var3 == null) {
               Object var7 = var2.load(var1);
               return (ListenableFuture<V>)(this.set((V)var7) ? this.futureValue : Futures.immediateFuture((V)var7));
            } else {
               ListenableFuture var6 = var2.reload(var1, var3);
               return var6 == null ? Futures.immediateFuture(null) : Futures.transform(var6, new com.google.common.base.Function<V, V>() {
                  @Override
                  public V apply(V var1) {
                     LoadingValueReference.this.set(var1);
                     return (V)var1;
                  }
               }, MoreExecutors.directExecutor());
            }
         } catch (Throwable var5) {
            Object var4 = this.setException(var5) ? this.futureValue : this.fullyFailedFuture(var5);
            if (var5 instanceof InterruptedException) {
               Thread.currentThread().interrupt();
            }

            return (ListenableFuture<V>)var4;
         }
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         this.stopwatch.start();

         Object var3;
         try {
            var3 = this.oldValue.waitForValue();
         } catch (ExecutionException var7) {
            var3 = null;
         }

         Object var4;
         try {
            var4 = var2.apply(var1, var3);
         } catch (Throwable var6) {
            this.setException(var6);
            throw var6;
         }

         this.set((V)var4);
         return (V)var4;
      }

      public long elapsedNanos() {
         return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
      }

      @Override
      public V waitForValue() throws ExecutionException {
         return Uninterruptibles.getUninterruptibly(this.futureValue);
      }

      @Override
      public V get() {
         return this.oldValue.get();
      }

      public LocalCache.ValueReference<K, V> getOldValue() {
         return this.oldValue;
      }

      @Override
      public ReferenceEntry<K, V> getEntry() {
         return null;
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, @Nullable V var2, ReferenceEntry<K, V> var3) {
         return this;
      }
   }

   static class LocalLoadingCache<K, V> extends LocalCache.LocalManualCache<K, V> implements LoadingCache<K, V> {
      private static final long serialVersionUID = 1L;

      LocalLoadingCache(CacheBuilder<? super K, ? super V> var1, CacheLoader<? super K, V> var2) {
         super(new LocalCache(var1, Preconditions.checkNotNull(var2)));
      }

      @Override
      public V get(K var1) throws ExecutionException {
         return this.localCache.getOrLoad((K)var1);
      }

      @Override
      public V getUnchecked(K var1) {
         try {
            return this.get((K)var1);
         } catch (ExecutionException var3) {
            throw new UncheckedExecutionException(var3.getCause());
         }
      }

      @Override
      public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
         return this.localCache.getAll(var1);
      }

      @Override
      public void refresh(K var1) {
         this.localCache.refresh((K)var1);
      }

      @Override
      public final V apply(K var1) {
         return this.getUnchecked((K)var1);
      }

      @Override
      Object writeReplace() {
         return new LocalCache.LoadingSerializationProxy<>(this.localCache);
      }
   }

   static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {
      final LocalCache<K, V> localCache;
      private static final long serialVersionUID = 1L;

      LocalManualCache(CacheBuilder<? super K, ? super V> var1) {
         this(new LocalCache<>(var1, null));
      }

      private LocalManualCache(LocalCache<K, V> var1) {
         this.localCache = var1;
      }

      @Nullable
      @Override
      public V getIfPresent(Object var1) {
         return this.localCache.getIfPresent(var1);
      }

      @Override
      public V get(K var1, final Callable<? extends V> var2) throws ExecutionException {
         Preconditions.checkNotNull(var2);
         return this.localCache.get((K)var1, new CacheLoader<Object, V>(this) {
            @Override
            public V load(Object var1) throws Exception {
               return (V)var2.call();
            }
         });
      }

      @Override
      public ImmutableMap<K, V> getAllPresent(Iterable<?> var1) {
         return this.localCache.getAllPresent(var1);
      }

      @Override
      public void put(K var1, V var2) {
         this.localCache.put((K)var1, (V)var2);
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         this.localCache.putAll(var1);
      }

      @Override
      public void invalidate(Object var1) {
         Preconditions.checkNotNull(var1);
         this.localCache.remove(var1);
      }

      @Override
      public void invalidateAll(Iterable<?> var1) {
         this.localCache.invalidateAll(var1);
      }

      @Override
      public void invalidateAll() {
         this.localCache.clear();
      }

      @Override
      public long size() {
         return this.localCache.longSize();
      }

      @Override
      public ConcurrentMap<K, V> asMap() {
         return this.localCache;
      }

      @Override
      public CacheStats stats() {
         AbstractCache.SimpleStatsCounter var1 = new AbstractCache.SimpleStatsCounter();
         var1.incrementBy(this.localCache.globalStatsCounter);

         for (LocalCache.Segment var5 : this.localCache.segments) {
            var1.incrementBy(var5.statsCounter);
         }

         return var1.snapshot();
      }

      @Override
      public void cleanUp() {
         this.localCache.cleanUp();
      }

      Object writeReplace() {
         return new LocalCache.ManualSerializationProxy<>(this.localCache);
      }
   }

   static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V> implements Serializable {
      private static final long serialVersionUID = 1L;
      final LocalCache.Strength keyStrength;
      final LocalCache.Strength valueStrength;
      final Equivalence<Object> keyEquivalence;
      final Equivalence<Object> valueEquivalence;
      final long expireAfterWriteNanos;
      final long expireAfterAccessNanos;
      final long maxWeight;
      final Weigher<K, V> weigher;
      final int concurrencyLevel;
      final RemovalListener<? super K, ? super V> removalListener;
      @Nullable
      final Ticker ticker;
      final CacheLoader<? super K, V> loader;
      @Nullable
      transient Cache<K, V> delegate;

      ManualSerializationProxy(LocalCache<K, V> var1) {
         this(
            var1.keyStrength,
            var1.valueStrength,
            var1.keyEquivalence,
            var1.valueEquivalence,
            var1.expireAfterWriteNanos,
            var1.expireAfterAccessNanos,
            var1.maxWeight,
            var1.weigher,
            var1.concurrencyLevel,
            var1.removalListener,
            var1.ticker,
            var1.defaultLoader
         );
      }

      private ManualSerializationProxy(
         LocalCache.Strength var1,
         LocalCache.Strength var2,
         Equivalence<Object> var3,
         Equivalence<Object> var4,
         long var5,
         long var7,
         long var9,
         Weigher<K, V> var11,
         int var12,
         RemovalListener<? super K, ? super V> var13,
         Ticker var14,
         CacheLoader<? super K, V> var15
      ) {
         this.keyStrength = var1;
         this.valueStrength = var2;
         this.keyEquivalence = var3;
         this.valueEquivalence = var4;
         this.expireAfterWriteNanos = var5;
         this.expireAfterAccessNanos = var7;
         this.maxWeight = var9;
         this.weigher = var11;
         this.concurrencyLevel = var12;
         this.removalListener = var13;
         this.ticker = var14 != Ticker.systemTicker() && var14 != CacheBuilder.NULL_TICKER ? var14 : null;
         this.loader = var15;
      }

      CacheBuilder<K, V> recreateCacheBuilder() {
         CacheBuilder var1 = CacheBuilder.newBuilder()
            .setKeyStrength(this.keyStrength)
            .setValueStrength(this.valueStrength)
            .keyEquivalence(this.keyEquivalence)
            .valueEquivalence(this.valueEquivalence)
            .concurrencyLevel(this.concurrencyLevel)
            .removalListener(this.removalListener);
         var1.strictParsing = false;
         if (this.expireAfterWriteNanos > 0L) {
            var1.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
         }

         if (this.expireAfterAccessNanos > 0L) {
            var1.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
         }

         if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
            var1.weigher(this.weigher);
            if (this.maxWeight != -1L) {
               var1.maximumWeight(this.maxWeight);
            }
         } else if (this.maxWeight != -1L) {
            var1.maximumSize(this.maxWeight);
         }

         if (this.ticker != null) {
            var1.ticker(this.ticker);
         }

         return var1;
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         CacheBuilder var2 = this.recreateCacheBuilder();
         this.delegate = var2.build();
      }

      private Object readResolve() {
         return this.delegate;
      }

      @Override
      protected Cache<K, V> delegate() {
         return this.delegate;
      }
   }

   private static enum NullEntry implements ReferenceEntry<Object, Object> {
      INSTANCE;

      @Override
      public LocalCache.ValueReference<Object, Object> getValueReference() {
         return null;
      }

      @Override
      public void setValueReference(LocalCache.ValueReference<Object, Object> var1) {
      }

      @Override
      public ReferenceEntry<Object, Object> getNext() {
         return null;
      }

      @Override
      public int getHash() {
         return 0;
      }

      @Override
      public Object getKey() {
         return null;
      }

      @Override
      public long getAccessTime() {
         return 0L;
      }

      @Override
      public void setAccessTime(long var1) {
      }

      @Override
      public ReferenceEntry<Object, Object> getNextInAccessQueue() {
         return this;
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<Object, Object> var1) {
      }

      @Override
      public ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
         return this;
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<Object, Object> var1) {
      }

      @Override
      public long getWriteTime() {
         return 0L;
      }

      @Override
      public void setWriteTime(long var1) {
      }

      @Override
      public ReferenceEntry<Object, Object> getNextInWriteQueue() {
         return this;
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<Object, Object> var1) {
      }

      @Override
      public ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
         return this;
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<Object, Object> var1) {
      }
   }

   static class Segment<K, V> extends ReentrantLock {
      @Weak
      final LocalCache<K, V> map;
      volatile int count;
      @GuardedBy("this")
      long totalWeight;
      int modCount;
      int threshold;
      @Nullable
      volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
      final long maxSegmentWeight;
      @Nullable
      final ReferenceQueue<K> keyReferenceQueue;
      @Nullable
      final ReferenceQueue<V> valueReferenceQueue;
      final Queue<ReferenceEntry<K, V>> recencyQueue;
      final AtomicInteger readCount = new AtomicInteger();
      @GuardedBy("this")
      final Queue<ReferenceEntry<K, V>> writeQueue;
      @GuardedBy("this")
      final Queue<ReferenceEntry<K, V>> accessQueue;
      final AbstractCache.StatsCounter statsCounter;

      Segment(LocalCache<K, V> var1, int var2, long var3, AbstractCache.StatsCounter var5) {
         this.map = var1;
         this.maxSegmentWeight = var3;
         this.statsCounter = Preconditions.checkNotNull(var5);
         this.initTable(this.newEntryArray(var2));
         this.keyReferenceQueue = var1.usesKeyReferences() ? new ReferenceQueue<>() : null;
         this.valueReferenceQueue = var1.usesValueReferences() ? new ReferenceQueue<>() : null;
         this.recencyQueue = (Queue<ReferenceEntry<K, V>>)(var1.usesAccessQueue() ? new ConcurrentLinkedQueue<>() : LocalCache.discardingQueue());
         this.writeQueue = (Queue<ReferenceEntry<K, V>>)(var1.usesWriteQueue() ? new LocalCache.WriteQueue<>() : LocalCache.discardingQueue());
         this.accessQueue = (Queue<ReferenceEntry<K, V>>)(var1.usesAccessQueue() ? new LocalCache.AccessQueue<>() : LocalCache.discardingQueue());
      }

      AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(int var1) {
         return new AtomicReferenceArray<>(var1);
      }

      void initTable(AtomicReferenceArray<ReferenceEntry<K, V>> var1) {
         this.threshold = var1.length() * 3 / 4;
         if (!this.map.customWeigher() && (long)this.threshold == this.maxSegmentWeight) {
            this.threshold++;
         }

         this.table = var1;
      }

      @GuardedBy("this")
      ReferenceEntry<K, V> newEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
         return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull((K)var1), var2, var3);
      }

      @GuardedBy("this")
      ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2) {
         if (var1.getKey() == null) {
            return null;
         } else {
            LocalCache.ValueReference var3 = var1.getValueReference();
            Object var4 = var3.get();
            if (var4 == null && var3.isActive()) {
               return null;
            } else {
               ReferenceEntry var5 = this.map.entryFactory.copyEntry(this, var1, var2);
               var5.setValueReference(var3.copyFor(this.valueReferenceQueue, (V)var4, var5));
               return var5;
            }
         }
      }

      @GuardedBy("this")
      void setValue(ReferenceEntry<K, V> var1, K var2, V var3, long var4) {
         LocalCache.ValueReference var6 = var1.getValueReference();
         int var7 = this.map.weigher.weigh((K)var2, (V)var3);
         Preconditions.checkState(var7 >= 0, "Weights must be non-negative");
         LocalCache.ValueReference var8 = this.map.valueStrength.referenceValue(this, var1, (V)var3, var7);
         var1.setValueReference(var8);
         this.recordWrite(var1, var7, var4);
         var6.notifyNewValue(var3);
      }

      V get(K var1, int var2, CacheLoader<? super K, V> var3) throws ExecutionException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var3);

         try {
            if (this.count != 0) {
               ReferenceEntry var4 = this.getEntry(var1, var2);
               if (var4 != null) {
                  long var16 = this.map.ticker.read();
                  Object var7 = this.getLiveValue(var4, var16);
                  if (var7 != null) {
                     this.recordRead(var4, var16);
                     this.statsCounter.recordHits(1);
                     return this.scheduleRefresh(var4, (K)var1, var2, (V)var7, var16, var3);
                  }

                  LocalCache.ValueReference var8 = var4.getValueReference();
                  if (var8.isLoading()) {
                     return this.waitForLoadingValue(var4, (K)var1, var8);
                  }
               }
            }

            return this.lockedGetOrLoad((K)var1, var2, var3);
         } catch (ExecutionException var13) {
            Throwable var5 = var13.getCause();
            if (var5 instanceof Error) {
               throw new ExecutionError((Error)var5);
            } else if (var5 instanceof RuntimeException) {
               throw new UncheckedExecutionException(var5);
            } else {
               throw var13;
            }
         } finally {
            this.postReadCleanup();
         }
      }

      @Nullable
      V get(Object var1, int var2) {
         try {
            if (this.count != 0) {
               long var3 = this.map.ticker.read();
               ReferenceEntry var5 = this.getLiveEntry(var1, var2, var3);
               if (var5 == null) {
                  return null;
               }

               Object var6 = var5.getValueReference().get();
               if (var6 != null) {
                  this.recordRead(var5, var3);
                  return this.scheduleRefresh(var5, (K)var5.getKey(), var2, (V)var6, var3, this.map.defaultLoader);
               }

               this.tryDrainReferenceQueues();
            }

            return null;
         } finally {
            this.postReadCleanup();
         }
      }

      V lockedGetOrLoad(K var1, int var2, CacheLoader<? super K, V> var3) throws ExecutionException {
         LocalCache.ValueReference var5 = null;
         LocalCache.LoadingValueReference var6 = null;
         boolean var7 = true;
         this.lock();

         ReferenceEntry var4;
         try {
            long var8 = this.map.ticker.read();
            this.preWriteCleanup(var8);
            int var10 = this.count - 1;
            AtomicReferenceArray var11 = this.table;
            int var12 = var2 & var11.length() - 1;
            ReferenceEntry var13 = (ReferenceEntry)var11.get(var12);

            for (var4 = var13; var4 != null; var4 = var4.getNext()) {
               Object var14 = var4.getKey();
               if (var4.getHash() == var2 && var14 != null && this.map.keyEquivalence.equivalent(var1, var14)) {
                  var5 = var4.getValueReference();
                  if (var5.isLoading()) {
                     var7 = false;
                  } else {
                     Object var15 = var5.get();
                     if (var15 == null) {
                        this.enqueueNotification((K)var14, var2, (V)var15, var5.getWeight(), RemovalCause.COLLECTED);
                     } else {
                        if (!this.map.isExpired(var4, var8)) {
                           this.recordLockedRead(var4, var8);
                           this.statsCounter.recordHits(1);
                           return (V)var15;
                        }

                        this.enqueueNotification((K)var14, var2, (V)var15, var5.getWeight(), RemovalCause.EXPIRED);
                     }

                     this.writeQueue.remove(var4);
                     this.accessQueue.remove(var4);
                     this.count = var10;
                  }
                  break;
               }
            }

            if (var7) {
               var6 = new LocalCache.LoadingValueReference();
               if (var4 == null) {
                  var4 = this.newEntry((K)var1, var2, var13);
                  var4.setValueReference(var6);
                  var11.set(var12, var4);
               } else {
                  var4.setValueReference(var6);
               }
            }
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         if (var7) {
            Object var9;
            try {
               synchronized (var4) {
                  var9 = this.loadSync((K)var1, var2, var6, var3);
               }
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return (V)var9;
         } else {
            return this.waitForLoadingValue(var4, (K)var1, var5);
         }
      }

      V waitForLoadingValue(ReferenceEntry<K, V> var1, K var2, LocalCache.ValueReference<K, V> var3) throws ExecutionException {
         if (!var3.isLoading()) {
            throw new AssertionError();
         } else {
            Preconditions.checkState(!Thread.holdsLock(var1), "Recursive load of: %s", var2);

            Object var7;
            try {
               Object var4 = var3.waitForValue();
               if (var4 == null) {
                  String var11 = String.valueOf(var2);
                  throw new CacheLoader.InvalidCacheLoadException(
                     new StringBuilder(35 + String.valueOf(var11).length()).append("CacheLoader returned null for key ").append(var11).append(".").toString()
                  );
               }

               long var5 = this.map.ticker.read();
               this.recordRead(var1, var5);
               var7 = var4;
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return (V)var7;
         }
      }

      V compute(K var1, int var2, BiFunction<? super K, ? super V, ? extends V> var3) {
         LocalCache.ValueReference var5 = null;
         LocalCache.ComputingValueReference var6 = null;
         boolean var7 = true;
         this.lock();

         Object var21;
         try {
            long var9 = this.map.ticker.read();
            this.preWriteCleanup(var9);
            AtomicReferenceArray var11 = this.table;
            int var12 = var2 & var11.length() - 1;
            ReferenceEntry var13 = (ReferenceEntry)var11.get(var12);

            ReferenceEntry var4;
            for (var4 = var13; var4 != null; var4 = var4.getNext()) {
               var21 = var4.getKey();
               if (var4.getHash() == var2 && var21 != null && this.map.keyEquivalence.equivalent(var1, var21)) {
                  var5 = var4.getValueReference();
                  if (this.map.isExpired(var4, var9)) {
                     this.enqueueNotification((K)var21, var2, (V)var5.get(), var5.getWeight(), RemovalCause.EXPIRED);
                  }

                  this.writeQueue.remove(var4);
                  this.accessQueue.remove(var4);
                  var7 = false;
                  break;
               }
            }

            var6 = new LocalCache.ComputingValueReference(var5);
            if (var4 == null) {
               var7 = true;
               var4 = this.newEntry((K)var1, var2, var13);
               var4.setValueReference(var6);
               var11.set(var12, var4);
            } else {
               var4.setValueReference(var6);
            }

            Object var8 = var6.compute(var1, var3);
            if (var8 == null) {
               if (!var7 && !var5.isLoading()) {
                  this.removeEntry(var4, var2, RemovalCause.EXPLICIT);
                  return null;
               }

               this.removeLoadingValue((K)var1, var2, var6);
               return null;
            }

            if (var5 == null || var8 != var5.get()) {
               try {
                  return this.getAndRecordStats((K)var1, var2, var6, Futures.immediateFuture((V)var8));
               } catch (ExecutionException var18) {
                  throw new AssertionError("impossible; Futures.immediateFuture can't throw");
               }
            }

            var6.set(var8);
            var4.setValueReference(var5);
            this.recordWrite(var4, 0, var9);
            var21 = var8;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         return (V)var21;
      }

      V loadSync(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, CacheLoader<? super K, V> var4) throws ExecutionException {
         ListenableFuture var5 = var3.loadFuture(var1, var4);
         return this.getAndRecordStats((K)var1, var2, var3, var5);
      }

      ListenableFuture<V> loadAsync(final K var1, final int var2, final LocalCache.LoadingValueReference<K, V> var3, CacheLoader<? super K, V> var4) {
         final ListenableFuture var5 = var3.loadFuture(var1, var4);
         var5.addListener(new Runnable() {
            @Override
            public void run() {
               try {
                  Segment.this.getAndRecordStats(var1, var2, var3, var5);
               } catch (Throwable var2x) {
                  LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", var2x);
                  var3.setException(var2x);
               }
            }
         }, MoreExecutors.directExecutor());
         return var5;
      }

      V getAndRecordStats(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, ListenableFuture<V> var4) throws ExecutionException {
         Object var5 = null;

         Object var6;
         try {
            var5 = Uninterruptibles.getUninterruptibly(var4);
            if (var5 == null) {
               var6 = String.valueOf(var1);
               throw new CacheLoader.InvalidCacheLoadException(
                  new StringBuilder(35 + String.valueOf(var6).length())
                     .append("CacheLoader returned null for key ")
                     .append((String)var6)
                     .append(".")
                     .toString()
               );
            }

            this.statsCounter.recordLoadSuccess(var3.elapsedNanos());
            this.storeLoadedValue((K)var1, var2, var3, (V)var5);
            var6 = var5;
         } finally {
            if (var5 == null) {
               this.statsCounter.recordLoadException(var3.elapsedNanos());
               this.removeLoadingValue((K)var1, var2, var3);
            }
         }

         return (V)var6;
      }

      V scheduleRefresh(ReferenceEntry<K, V> var1, K var2, int var3, V var4, long var5, CacheLoader<? super K, V> var7) {
         if (this.map.refreshes() && var5 - var1.getWriteTime() > this.map.refreshNanos && !var1.getValueReference().isLoading()) {
            Object var8 = this.refresh((K)var2, var3, var7, true);
            if (var8 != null) {
               return (V)var8;
            }
         }

         return (V)var4;
      }

      @Nullable
      V refresh(K var1, int var2, CacheLoader<? super K, V> var3, boolean var4) {
         LocalCache.LoadingValueReference var5 = this.insertLoadingValueReference((K)var1, var2, var4);
         if (var5 == null) {
            return null;
         } else {
            ListenableFuture var6 = this.loadAsync((K)var1, var2, var5, var3);
            if (var6.isDone()) {
               try {
                  return Uninterruptibles.getUninterruptibly(var6);
               } catch (Throwable var8) {
               }
            }

            return null;
         }
      }

      @Nullable
      LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K var1, int var2, boolean var3) {
         ReferenceEntry var4 = null;
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            ReferenceEntry var9 = (ReferenceEntry)var7.get(var8);

            for (ReferenceEntry var17 = var9; var17 != null; var17 = var17.getNext()) {
               Object var10 = var17.getKey();
               if (var17.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var17.getValueReference();
                  if (!var11.isLoading() && (!var3 || var5 - var17.getWriteTime() >= this.map.refreshNanos)) {
                     this.modCount++;
                     LocalCache.LoadingValueReference var21 = new LocalCache.LoadingValueReference(var11);
                     var17.setValueReference(var21);
                     return var21;
                  }

                  return null;
               }
            }

            this.modCount++;
            LocalCache.LoadingValueReference var19 = new LocalCache.LoadingValueReference();
            var4 = this.newEntry((K)var1, var2, var9);
            var4.setValueReference(var19);
            var7.set(var8, var4);
            return var19;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void tryDrainReferenceQueues() {
         if (this.tryLock()) {
            try {
               this.drainReferenceQueues();
            } finally {
               this.unlock();
            }
         }
      }

      @GuardedBy("this")
      void drainReferenceQueues() {
         if (this.map.usesKeyReferences()) {
            this.drainKeyReferenceQueue();
         }

         if (this.map.usesValueReferences()) {
            this.drainValueReferenceQueue();
         }
      }

      @GuardedBy("this")
      void drainKeyReferenceQueue() {
         int var2 = 0;

         Reference var1;
         while ((var1 = this.keyReferenceQueue.poll()) != null) {
            ReferenceEntry var3 = (ReferenceEntry)var1;
            this.map.reclaimKey(var3);
            if (++var2 == 16) {
               break;
            }
         }
      }

      @GuardedBy("this")
      void drainValueReferenceQueue() {
         int var2 = 0;

         Reference var1;
         while ((var1 = this.valueReferenceQueue.poll()) != null) {
            LocalCache.ValueReference var3 = (LocalCache.ValueReference)var1;
            this.map.reclaimValue(var3);
            if (++var2 == 16) {
               break;
            }
         }
      }

      void clearReferenceQueues() {
         if (this.map.usesKeyReferences()) {
            this.clearKeyReferenceQueue();
         }

         if (this.map.usesValueReferences()) {
            this.clearValueReferenceQueue();
         }
      }

      void clearKeyReferenceQueue() {
         while (this.keyReferenceQueue.poll() != null) {
         }
      }

      void clearValueReferenceQueue() {
         while (this.valueReferenceQueue.poll() != null) {
         }
      }

      void recordRead(ReferenceEntry<K, V> var1, long var2) {
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var2);
         }

         this.recencyQueue.add(var1);
      }

      @GuardedBy("this")
      void recordLockedRead(ReferenceEntry<K, V> var1, long var2) {
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var2);
         }

         this.accessQueue.add(var1);
      }

      @GuardedBy("this")
      void recordWrite(ReferenceEntry<K, V> var1, int var2, long var3) {
         this.drainRecencyQueue();
         this.totalWeight += (long)var2;
         if (this.map.recordsAccess()) {
            var1.setAccessTime(var3);
         }

         if (this.map.recordsWrite()) {
            var1.setWriteTime(var3);
         }

         this.accessQueue.add(var1);
         this.writeQueue.add(var1);
      }

      @GuardedBy("this")
      void drainRecencyQueue() {
         ReferenceEntry var1;
         while ((var1 = this.recencyQueue.poll()) != null) {
            if (this.accessQueue.contains(var1)) {
               this.accessQueue.add(var1);
            }
         }
      }

      void tryExpireEntries(long var1) {
         if (this.tryLock()) {
            try {
               this.expireEntries(var1);
            } finally {
               this.unlock();
            }
         }
      }

      @GuardedBy("this")
      void expireEntries(long var1) {
         this.drainRecencyQueue();

         ReferenceEntry var3;
         while ((var3 = this.writeQueue.peek()) != null && this.map.isExpired(var3, var1)) {
            if (!this.removeEntry(var3, var3.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }

         while ((var3 = this.accessQueue.peek()) != null && this.map.isExpired(var3, var1)) {
            if (!this.removeEntry(var3, var3.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }
      }

      @GuardedBy("this")
      void enqueueNotification(@Nullable K var1, int var2, @Nullable V var3, int var4, RemovalCause var5) {
         this.totalWeight -= (long)var4;
         if (var5.wasEvicted()) {
            this.statsCounter.recordEviction();
         }

         if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
            RemovalNotification var6 = RemovalNotification.create(var1, var3, var5);
            this.map.removalNotificationQueue.offer(var6);
         }
      }

      @GuardedBy("this")
      void evictEntries(ReferenceEntry<K, V> var1) {
         if (this.map.evictsBySize()) {
            this.drainRecencyQueue();
            if ((long)var1.getValueReference().getWeight() > this.maxSegmentWeight && !this.removeEntry(var1, var1.getHash(), RemovalCause.SIZE)) {
               throw new AssertionError();
            } else {
               while (this.totalWeight > this.maxSegmentWeight) {
                  ReferenceEntry var2 = this.getNextEvictable();
                  if (!this.removeEntry(var2, var2.getHash(), RemovalCause.SIZE)) {
                     throw new AssertionError();
                  }
               }
            }
         }
      }

      @GuardedBy("this")
      ReferenceEntry<K, V> getNextEvictable() {
         for (ReferenceEntry var2 : this.accessQueue) {
            int var3 = var2.getValueReference().getWeight();
            if (var3 > 0) {
               return var2;
            }
         }

         throw new AssertionError();
      }

      ReferenceEntry<K, V> getFirst(int var1) {
         AtomicReferenceArray var2 = this.table;
         return (ReferenceEntry<K, V>)var2.get(var1 & var2.length() - 1);
      }

      @Nullable
      ReferenceEntry<K, V> getEntry(Object var1, int var2) {
         for (ReferenceEntry var3 = this.getFirst(var2); var3 != null; var3 = var3.getNext()) {
            if (var3.getHash() == var2) {
               Object var4 = var3.getKey();
               if (var4 == null) {
                  this.tryDrainReferenceQueues();
               } else if (this.map.keyEquivalence.equivalent(var1, var4)) {
                  return var3;
               }
            }
         }

         return null;
      }

      @Nullable
      ReferenceEntry<K, V> getLiveEntry(Object var1, int var2, long var3) {
         ReferenceEntry var5 = this.getEntry(var1, var2);
         if (var5 == null) {
            return null;
         } else if (this.map.isExpired(var5, var3)) {
            this.tryExpireEntries(var3);
            return null;
         } else {
            return var5;
         }
      }

      V getLiveValue(ReferenceEntry<K, V> var1, long var2) {
         if (var1.getKey() == null) {
            this.tryDrainReferenceQueues();
            return null;
         } else {
            Object var4 = var1.getValueReference().get();
            if (var4 == null) {
               this.tryDrainReferenceQueues();
               return null;
            } else if (this.map.isExpired(var1, var2)) {
               this.tryExpireEntries(var2);
               return null;
            } else {
               return (V)var4;
            }
         }
      }

      boolean containsKey(Object var1, int var2) {
         boolean var6;
         try {
            if (this.count == 0) {
               return false;
            }

            long var3 = this.map.ticker.read();
            ReferenceEntry var5 = this.getLiveEntry(var1, var2, var3);
            if (var5 != null) {
               return var5.getValueReference().get() != null;
            }

            var6 = false;
         } finally {
            this.postReadCleanup();
         }

         return var6;
      }

      @VisibleForTesting
      boolean containsValue(Object var1) {
         try {
            if (this.count != 0) {
               long var2 = this.map.ticker.read();
               AtomicReferenceArray var4 = this.table;
               int var5 = var4.length();

               for (int var6 = 0; var6 < var5; var6++) {
                  for (ReferenceEntry var7 = (ReferenceEntry)var4.get(var6); var7 != null; var7 = var7.getNext()) {
                     Object var8 = this.getLiveValue(var7, var2);
                     if (var8 != null && this.map.valueEquivalence.equivalent(var1, var8)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         } finally {
            this.postReadCleanup();
         }
      }

      @Nullable
      V put(K var1, int var2, V var3, boolean var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            int var7 = this.count + 1;
            if (var7 > this.threshold) {
               this.expand();
               var7 = this.count + 1;
            }

            AtomicReferenceArray var8 = this.table;
            int var9 = var2 & var8.length() - 1;
            ReferenceEntry var10 = (ReferenceEntry)var8.get(var9);

            for (ReferenceEntry var11 = var10; var11 != null; var11 = var11.getNext()) {
               Object var12 = var11.getKey();
               if (var11.getHash() == var2 && var12 != null && this.map.keyEquivalence.equivalent(var1, var12)) {
                  LocalCache.ValueReference var13 = var11.getValueReference();
                  Object var14 = var13.get();
                  if (var14 != null) {
                     if (!var4) {
                        this.modCount++;
                        this.enqueueNotification((K)var1, var2, (V)var14, var13.getWeight(), RemovalCause.REPLACED);
                        this.setValue(var11, (K)var1, (V)var3, var5);
                        this.evictEntries(var11);
                        return (V)var14;
                     }

                     this.recordLockedRead(var11, var5);
                     return (V)var14;
                  }

                  this.modCount++;
                  if (var13.isActive()) {
                     this.enqueueNotification((K)var1, var2, (V)var14, var13.getWeight(), RemovalCause.COLLECTED);
                     this.setValue(var11, (K)var1, (V)var3, var5);
                     var7 = this.count;
                  } else {
                     this.setValue(var11, (K)var1, (V)var3, var5);
                     var7 = this.count + 1;
                  }

                  this.count = var7;
                  this.evictEntries(var11);
                  return null;
               }
            }

            this.modCount++;
            ReferenceEntry var22 = this.newEntry((K)var1, var2, var10);
            this.setValue(var22, (K)var1, (V)var3, var5);
            var8.set(var9, var22);
            var7 = this.count + 1;
            this.count = var7;
            this.evictEntries(var22);
            return null;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("this")
      void expand() {
         AtomicReferenceArray var1 = this.table;
         int var2 = var1.length();
         if (var2 < 1073741824) {
            int var3 = this.count;
            AtomicReferenceArray var4 = this.newEntryArray(var2 << 1);
            this.threshold = var4.length() * 3 / 4;
            int var5 = var4.length() - 1;

            for (int var6 = 0; var6 < var2; var6++) {
               ReferenceEntry var7 = (ReferenceEntry)var1.get(var6);
               if (var7 != null) {
                  ReferenceEntry var8 = var7.getNext();
                  int var9 = var7.getHash() & var5;
                  if (var8 == null) {
                     var4.set(var9, var7);
                  } else {
                     ReferenceEntry var10 = var7;
                     int var11 = var9;

                     for (ReferenceEntry var12 = var8; var12 != null; var12 = var12.getNext()) {
                        int var13 = var12.getHash() & var5;
                        if (var13 != var11) {
                           var11 = var13;
                           var10 = var12;
                        }
                     }

                     var4.set(var11, var10);

                     for (ReferenceEntry var16 = var7; var16 != var10; var16 = var16.getNext()) {
                        int var17 = var16.getHash() & var5;
                        ReferenceEntry var14 = (ReferenceEntry)var4.get(var17);
                        ReferenceEntry var15 = this.copyEntry(var16, var14);
                        if (var15 != null) {
                           var4.set(var17, var15);
                        } else {
                           this.removeCollectedEntry(var16);
                           var3--;
                        }
                     }
                  }
               }
            }

            this.table = var4;
            this.count = var3;
         }
      }

      boolean replace(K var1, int var2, V var3, V var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            ReferenceEntry var9 = (ReferenceEntry)var7.get(var8);

            for (ReferenceEntry var10 = var9; var10 != null; var10 = var10.getNext()) {
               Object var11 = var10.getKey();
               if (var10.getHash() == var2 && var11 != null && this.map.keyEquivalence.equivalent(var1, var11)) {
                  LocalCache.ValueReference var12 = var10.getValueReference();
                  Object var13 = var12.get();
                  if (var13 != null) {
                     if (!this.map.valueEquivalence.equivalent(var3, var13)) {
                        this.recordLockedRead(var10, var5);
                        return false;
                     }

                     this.modCount++;
                     this.enqueueNotification((K)var1, var2, (V)var13, var12.getWeight(), RemovalCause.REPLACED);
                     this.setValue(var10, (K)var1, (V)var4, var5);
                     this.evictEntries(var10);
                     return true;
                  }

                  if (var12.isActive()) {
                     int var14 = this.count - 1;
                     this.modCount++;
                     ReferenceEntry var15 = this.removeValueFromChain(var9, var10, (K)var11, var2, (V)var13, var12, RemovalCause.COLLECTED);
                     var14 = this.count - 1;
                     var7.set(var8, var15);
                     this.count = var14;
                  }

                  return false;
               }
            }

            return false;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      V replace(K var1, int var2, V var3) {
         this.lock();

         try {
            long var4 = this.map.ticker.read();
            this.preWriteCleanup(var4);
            AtomicReferenceArray var6 = this.table;
            int var7 = var2 & var6.length() - 1;
            ReferenceEntry var8 = (ReferenceEntry)var6.get(var7);

            for (ReferenceEntry var9 = var8; var9 != null; var9 = var9.getNext()) {
               Object var10 = var9.getKey();
               if (var9.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var9.getValueReference();
                  Object var12 = var11.get();
                  if (var12 != null) {
                     this.modCount++;
                     this.enqueueNotification((K)var1, var2, (V)var12, var11.getWeight(), RemovalCause.REPLACED);
                     this.setValue(var9, (K)var1, (V)var3, var4);
                     this.evictEntries(var9);
                     return (V)var12;
                  }

                  if (var11.isActive()) {
                     int var13 = this.count - 1;
                     this.modCount++;
                     ReferenceEntry var14 = this.removeValueFromChain(var8, var9, (K)var10, var2, (V)var12, var11, RemovalCause.COLLECTED);
                     var13 = this.count - 1;
                     var6.set(var7, var14);
                     this.count = var13;
                  }

                  return null;
               }
            }

            return null;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      V remove(Object var1, int var2) {
         this.lock();

         try {
            long var3 = this.map.ticker.read();
            this.preWriteCleanup(var3);
            int var5 = this.count - 1;
            AtomicReferenceArray var6 = this.table;
            int var7 = var2 & var6.length() - 1;
            ReferenceEntry var8 = (ReferenceEntry)var6.get(var7);

            for (ReferenceEntry var9 = var8; var9 != null; var9 = var9.getNext()) {
               Object var10 = var9.getKey();
               if (var9.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  LocalCache.ValueReference var11 = var9.getValueReference();
                  Object var12 = var11.get();
                  RemovalCause var13;
                  if (var12 != null) {
                     var13 = RemovalCause.EXPLICIT;
                  } else {
                     if (!var11.isActive()) {
                        return null;
                     }

                     var13 = RemovalCause.COLLECTED;
                  }

                  this.modCount++;
                  ReferenceEntry var14 = this.removeValueFromChain(var8, var9, (K)var10, var2, (V)var12, var11, var13);
                  var5 = this.count - 1;
                  var6.set(var7, var14);
                  this.count = var5;
                  return (V)var12;
               }
            }

            return null;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean remove(Object var1, int var2, Object var3) {
         this.lock();

         try {
            long var4 = this.map.ticker.read();
            this.preWriteCleanup(var4);
            int var6 = this.count - 1;
            AtomicReferenceArray var7 = this.table;
            int var8 = var2 & var7.length() - 1;
            ReferenceEntry var9 = (ReferenceEntry)var7.get(var8);

            for (ReferenceEntry var10 = var9; var10 != null; var10 = var10.getNext()) {
               Object var11 = var10.getKey();
               if (var10.getHash() == var2 && var11 != null && this.map.keyEquivalence.equivalent(var1, var11)) {
                  LocalCache.ValueReference var12 = var10.getValueReference();
                  Object var13 = var12.get();
                  RemovalCause var14;
                  if (this.map.valueEquivalence.equivalent(var3, var13)) {
                     var14 = RemovalCause.EXPLICIT;
                  } else {
                     if (var13 != null || !var12.isActive()) {
                        return false;
                     }

                     var14 = RemovalCause.COLLECTED;
                  }

                  this.modCount++;
                  ReferenceEntry var15 = this.removeValueFromChain(var9, var10, (K)var11, var2, (V)var13, var12, var14);
                  var6 = this.count - 1;
                  var7.set(var8, var15);
                  this.count = var6;
                  return var14 == RemovalCause.EXPLICIT;
               }
            }

            return false;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean storeLoadedValue(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3, V var4) {
         this.lock();

         try {
            long var5 = this.map.ticker.read();
            this.preWriteCleanup(var5);
            int var7 = this.count + 1;
            if (var7 > this.threshold) {
               this.expand();
               var7 = this.count + 1;
            }

            AtomicReferenceArray var8 = this.table;
            int var9 = var2 & var8.length() - 1;
            ReferenceEntry var10 = (ReferenceEntry)var8.get(var9);

            for (ReferenceEntry var11 = var10; var11 != null; var11 = var11.getNext()) {
               Object var12 = var11.getKey();
               if (var11.getHash() == var2 && var12 != null && this.map.keyEquivalence.equivalent(var1, var12)) {
                  LocalCache.ValueReference var13 = var11.getValueReference();
                  Object var14 = var13.get();
                  if (var3 != var13 && (var14 != null || var13 == LocalCache.UNSET)) {
                     this.enqueueNotification((K)var1, var2, (V)var4, 0, RemovalCause.REPLACED);
                     return false;
                  }

                  this.modCount++;
                  if (var3.isActive()) {
                     RemovalCause var15 = var14 == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
                     this.enqueueNotification((K)var1, var2, (V)var14, var3.getWeight(), var15);
                     var7--;
                  }

                  this.setValue(var11, (K)var1, (V)var4, var5);
                  this.count = var7;
                  this.evictEntries(var11);
                  return true;
               }
            }

            this.modCount++;
            ReferenceEntry var19 = this.newEntry((K)var1, var2, var10);
            this.setValue(var19, (K)var1, (V)var4, var5);
            var8.set(var9, var19);
            this.count = var7;
            this.evictEntries(var19);
            return true;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void clear() {
         if (this.count != 0) {
            this.lock();

            try {
               long var1 = this.map.ticker.read();
               this.preWriteCleanup(var1);
               AtomicReferenceArray var3 = this.table;

               for (int var4 = 0; var4 < var3.length(); var4++) {
                  for (ReferenceEntry var5 = (ReferenceEntry)var3.get(var4); var5 != null; var5 = var5.getNext()) {
                     if (var5.getValueReference().isActive()) {
                        Object var6 = var5.getKey();
                        Object var7 = var5.getValueReference().get();
                        RemovalCause var8 = var6 != null && var7 != null ? RemovalCause.EXPLICIT : RemovalCause.COLLECTED;
                        this.enqueueNotification((K)var6, var5.getHash(), (V)var7, var5.getValueReference().getWeight(), var8);
                     }
                  }
               }

               for (int var12 = 0; var12 < var3.length(); var12++) {
                  var3.set(var12, null);
               }

               this.clearReferenceQueues();
               this.writeQueue.clear();
               this.accessQueue.clear();
               this.readCount.set(0);
               this.modCount++;
               this.count = 0;
            } finally {
               this.unlock();
               this.postWriteCleanup();
            }
         }
      }

      @GuardedBy("this")
      @Nullable
      ReferenceEntry<K, V> removeValueFromChain(
         ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2, @Nullable K var3, int var4, V var5, LocalCache.ValueReference<K, V> var6, RemovalCause var7
      ) {
         this.enqueueNotification((K)var3, var4, (V)var5, var6.getWeight(), var7);
         this.writeQueue.remove(var2);
         this.accessQueue.remove(var2);
         if (var6.isLoading()) {
            var6.notifyNewValue(null);
            return var1;
         } else {
            return this.removeEntryFromChain(var1, var2);
         }
      }

      @GuardedBy("this")
      @Nullable
      ReferenceEntry<K, V> removeEntryFromChain(ReferenceEntry<K, V> var1, ReferenceEntry<K, V> var2) {
         int var3 = this.count;
         ReferenceEntry var4 = var2.getNext();

         for (ReferenceEntry var5 = var1; var5 != var2; var5 = var5.getNext()) {
            ReferenceEntry var6 = this.copyEntry(var5, var4);
            if (var6 != null) {
               var4 = var6;
            } else {
               this.removeCollectedEntry(var5);
               var3--;
            }
         }

         this.count = var3;
         return var4;
      }

      @GuardedBy("this")
      void removeCollectedEntry(ReferenceEntry<K, V> var1) {
         this.enqueueNotification(
            (K)var1.getKey(), var1.getHash(), (V)var1.getValueReference().get(), var1.getValueReference().getWeight(), RemovalCause.COLLECTED
         );
         this.writeQueue.remove(var1);
         this.accessQueue.remove(var1);
      }

      boolean reclaimKey(ReferenceEntry<K, V> var1, int var2) {
         this.lock();

         try {
            int var3 = this.count - 1;
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            ReferenceEntry var6 = (ReferenceEntry)var4.get(var5);

            for (ReferenceEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               if (var7 == var1) {
                  this.modCount++;
                  ReferenceEntry var8 = this.removeValueFromChain(
                     var6, var7, (K)var7.getKey(), var2, (V)var7.getValueReference().get(), var7.getValueReference(), RemovalCause.COLLECTED
                  );
                  var3 = this.count - 1;
                  var4.set(var5, var8);
                  this.count = var3;
                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean reclaimValue(K var1, int var2, LocalCache.ValueReference<K, V> var3) {
         this.lock();

         try {
            int var4 = this.count - 1;
            AtomicReferenceArray var5 = this.table;
            int var6 = var2 & var5.length() - 1;
            ReferenceEntry var7 = (ReferenceEntry)var5.get(var6);

            for (ReferenceEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
               Object var9 = var8.getKey();
               if (var8.getHash() == var2 && var9 != null && this.map.keyEquivalence.equivalent(var1, var9)) {
                  LocalCache.ValueReference var10 = var8.getValueReference();
                  if (var10 != var3) {
                     return false;
                  }

                  this.modCount++;
                  ReferenceEntry var11 = this.removeValueFromChain(var7, var8, (K)var9, var2, (V)var3.get(), var3, RemovalCause.COLLECTED);
                  var4 = this.count - 1;
                  var5.set(var6, var11);
                  this.count = var4;
                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
            if (!this.isHeldByCurrentThread()) {
               this.postWriteCleanup();
            }
         }
      }

      boolean removeLoadingValue(K var1, int var2, LocalCache.LoadingValueReference<K, V> var3) {
         this.lock();

         try {
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            ReferenceEntry var6 = (ReferenceEntry)var4.get(var5);

            for (ReferenceEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               Object var8 = var7.getKey();
               if (var7.getHash() == var2 && var8 != null && this.map.keyEquivalence.equivalent(var1, var8)) {
                  LocalCache.ValueReference var9 = var7.getValueReference();
                  if (var9 != var3) {
                     return false;
                  }

                  if (var3.isActive()) {
                     var7.setValueReference(var3.getOldValue());
                  } else {
                     ReferenceEntry var10 = this.removeEntryFromChain(var6, var7);
                     var4.set(var5, var10);
                  }

                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @VisibleForTesting
      @GuardedBy("this")
      boolean removeEntry(ReferenceEntry<K, V> var1, int var2, RemovalCause var3) {
         int var4 = this.count - 1;
         AtomicReferenceArray var5 = this.table;
         int var6 = var2 & var5.length() - 1;
         ReferenceEntry var7 = (ReferenceEntry)var5.get(var6);

         for (ReferenceEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
            if (var8 == var1) {
               this.modCount++;
               ReferenceEntry var9 = this.removeValueFromChain(
                  var7, var8, (K)var8.getKey(), var2, (V)var8.getValueReference().get(), var8.getValueReference(), var3
               );
               var4 = this.count - 1;
               var5.set(var6, var9);
               this.count = var4;
               return true;
            }
         }

         return false;
      }

      void postReadCleanup() {
         if ((this.readCount.incrementAndGet() & 63) == 0) {
            this.cleanUp();
         }
      }

      @GuardedBy("this")
      void preWriteCleanup(long var1) {
         this.runLockedCleanup(var1);
      }

      void postWriteCleanup() {
         this.runUnlockedCleanup();
      }

      void cleanUp() {
         long var1 = this.map.ticker.read();
         this.runLockedCleanup(var1);
         this.runUnlockedCleanup();
      }

      void runLockedCleanup(long var1) {
         if (this.tryLock()) {
            try {
               this.drainReferenceQueues();
               this.expireEntries(var1);
               this.readCount.set(0);
            } finally {
               this.unlock();
            }
         }
      }

      void runUnlockedCleanup() {
         if (!this.isHeldByCurrentThread()) {
            this.map.processPendingNotifications();
         }
      }
   }

   static class SoftValueReference<K, V> extends SoftReference<V> implements LocalCache.ValueReference<K, V> {
      final ReferenceEntry<K, V> entry;

      SoftValueReference(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         super((V)var2, var1);
         this.entry = var3;
      }

      @Override
      public int getWeight() {
         return 1;
      }

      @Override
      public ReferenceEntry<K, V> getEntry() {
         return this.entry;
      }

      @Override
      public void notifyNewValue(V var1) {
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         return new LocalCache.SoftValueReference<>(var1, (V)var2, var3);
      }

      @Override
      public boolean isLoading() {
         return false;
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public V waitForValue() {
         return this.get();
      }
   }

   static enum Strength {
      STRONG {
         @Override
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference<K, V>)(var4 == 1
               ? new LocalCache.StrongValueReference<>((V)var3)
               : new LocalCache.WeightedStrongValueReference<>((V)var3, var4));
         }

         @Override
         Equivalence<Object> defaultEquivalence() {
            return Equivalence.equals();
         }
      },
      SOFT {
         @Override
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference<K, V>)(var4 == 1
               ? new LocalCache.SoftValueReference<>(var1.valueReferenceQueue, (V)var3, var2)
               : new LocalCache.WeightedSoftValueReference<>(var1.valueReferenceQueue, (V)var3, var2, var4));
         }

         @Override
         Equivalence<Object> defaultEquivalence() {
            return Equivalence.identity();
         }
      },
      WEAK {
         @Override
         <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, V var3, int var4) {
            return (LocalCache.ValueReference<K, V>)(var4 == 1
               ? new LocalCache.WeakValueReference<>(var1.valueReferenceQueue, (V)var3, var2)
               : new LocalCache.WeightedWeakValueReference<>(var1.valueReferenceQueue, (V)var3, var2, var4));
         }

         @Override
         Equivalence<Object> defaultEquivalence() {
            return Equivalence.identity();
         }
      };

      private Strength() {
      }

      abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> var1, ReferenceEntry<K, V> var2, V var3, int var4);

      abstract Equivalence<Object> defaultEquivalence();
   }

   static final class StrongAccessEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long accessTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

      StrongAccessEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
         super((K)var1, var2, var3);
      }

      @Override
      public long getAccessTime() {
         return this.accessTime;
      }

      @Override
      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }
   }

   static final class StrongAccessWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long accessTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
      volatile long writeTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      StrongAccessWriteEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
         super((K)var1, var2, var3);
      }

      @Override
      public long getAccessTime() {
         return this.accessTime;
      }

      @Override
      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }

      @Override
      public long getWriteTime() {
         return this.writeTime;
      }

      @Override
      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static class StrongEntry<K, V> extends LocalCache.AbstractReferenceEntry<K, V> {
      final K key;
      final int hash;
      @Nullable
      final ReferenceEntry<K, V> next;
      volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

      StrongEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
         this.key = (K)var1;
         this.hash = var2;
         this.next = var3;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public LocalCache.ValueReference<K, V> getValueReference() {
         return this.valueReference;
      }

      @Override
      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         this.valueReference = var1;
      }

      @Override
      public int getHash() {
         return this.hash;
      }

      @Override
      public ReferenceEntry<K, V> getNext() {
         return this.next;
      }
   }

   static class StrongValueReference<K, V> implements LocalCache.ValueReference<K, V> {
      final V referent;

      StrongValueReference(V var1) {
         this.referent = (V)var1;
      }

      @Override
      public V get() {
         return this.referent;
      }

      @Override
      public int getWeight() {
         return 1;
      }

      @Override
      public ReferenceEntry<K, V> getEntry() {
         return null;
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         return this;
      }

      @Override
      public boolean isLoading() {
         return false;
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public V waitForValue() {
         return this.get();
      }

      @Override
      public void notifyNewValue(V var1) {
      }
   }

   static final class StrongWriteEntry<K, V> extends LocalCache.StrongEntry<K, V> {
      volatile long writeTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      StrongWriteEntry(K var1, int var2, @Nullable ReferenceEntry<K, V> var3) {
         super((K)var1, var2, var3);
      }

      @Override
      public long getWriteTime() {
         return this.writeTime;
      }

      @Override
      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   final class ValueIterator extends LocalCache<K, V>.HashIterator<V> {
      ValueIterator(LocalCache var1) {
      }

      @Override
      public V next() {
         return this.nextEntry().getValue();
      }
   }

   interface ValueReference<K, V> {
      @Nullable
      V get();

      V waitForValue() throws ExecutionException;

      int getWeight();

      @Nullable
      ReferenceEntry<K, V> getEntry();

      LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, @Nullable V var2, ReferenceEntry<K, V> var3);

      void notifyNewValue(@Nullable V var1);

      boolean isLoading();

      boolean isActive();
   }

   final class Values extends AbstractCollection<V> {
      @Override
      public int size() {
         return LocalCache.this.size();
      }

      @Override
      public boolean isEmpty() {
         return LocalCache.this.isEmpty();
      }

      @Override
      public void clear() {
         LocalCache.this.clear();
      }

      @Override
      public Iterator<V> iterator() {
         return LocalCache.this.new ValueIterator(LocalCache.this);
      }

      @Override
      public boolean removeIf(Predicate<? super V> var1) {
         Preconditions.checkNotNull(var1);
         return LocalCache.this.removeIf((var1x, var2) -> var1.test(var2));
      }

      @Override
      public boolean contains(Object var1) {
         return LocalCache.this.containsValue(var1);
      }

      @Override
      public Object[] toArray() {
         return LocalCache.toArrayList(this).toArray();
      }

      @Override
      public <E> E[] toArray(E[] var1) {
         return (E[])LocalCache.toArrayList(this).toArray(var1);
      }
   }

   static final class WeakAccessEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long accessTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();

      WeakAccessEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
         super(var1, (K)var2, var3, var4);
      }

      @Override
      public long getAccessTime() {
         return this.accessTime;
      }

      @Override
      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }
   }

   static final class WeakAccessWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long accessTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
      volatile long writeTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      WeakAccessWriteEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
         super(var1, (K)var2, var3, var4);
      }

      @Override
      public long getAccessTime() {
         return this.accessTime;
      }

      @Override
      public void setAccessTime(long var1) {
         this.accessTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         return this.nextAccess;
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         this.nextAccess = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         this.previousAccess = var1;
      }

      @Override
      public long getWriteTime() {
         return this.writeTime;
      }

      @Override
      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
      final int hash;
      @Nullable
      final ReferenceEntry<K, V> next;
      volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();

      WeakEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
         super((K)var2, var1);
         this.hash = var3;
         this.next = var4;
      }

      @Override
      public K getKey() {
         return this.get();
      }

      @Override
      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setAccessTime(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setNextInAccessQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setPreviousInAccessQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setWriteTime(long var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public LocalCache.ValueReference<K, V> getValueReference() {
         return this.valueReference;
      }

      @Override
      public void setValueReference(LocalCache.ValueReference<K, V> var1) {
         this.valueReference = var1;
      }

      @Override
      public int getHash() {
         return this.hash;
      }

      @Override
      public ReferenceEntry<K, V> getNext() {
         return this.next;
      }
   }

   static class WeakValueReference<K, V> extends WeakReference<V> implements LocalCache.ValueReference<K, V> {
      final ReferenceEntry<K, V> entry;

      WeakValueReference(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         super((V)var2, var1);
         this.entry = var3;
      }

      @Override
      public int getWeight() {
         return 1;
      }

      @Override
      public ReferenceEntry<K, V> getEntry() {
         return this.entry;
      }

      @Override
      public void notifyNewValue(V var1) {
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         return new LocalCache.WeakValueReference<>(var1, (V)var2, var3);
      }

      @Override
      public boolean isLoading() {
         return false;
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public V waitForValue() {
         return this.get();
      }
   }

   static final class WeakWriteEntry<K, V> extends LocalCache.WeakEntry<K, V> {
      volatile long writeTime = Long.MAX_VALUE;
      @Weak
      ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
      @Weak
      ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();

      WeakWriteEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable ReferenceEntry<K, V> var4) {
         super(var1, (K)var2, var3, var4);
      }

      @Override
      public long getWriteTime() {
         return this.writeTime;
      }

      @Override
      public void setWriteTime(long var1) {
         this.writeTime = var1;
      }

      @Override
      public ReferenceEntry<K, V> getNextInWriteQueue() {
         return this.nextWrite;
      }

      @Override
      public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
         this.nextWrite = var1;
      }

      @Override
      public ReferenceEntry<K, V> getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      @Override
      public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
         this.previousWrite = var1;
      }
   }

   static final class WeightedSoftValueReference<K, V> extends LocalCache.SoftValueReference<K, V> {
      final int weight;

      WeightedSoftValueReference(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3, int var4) {
         super(var1, (V)var2, var3);
         this.weight = var4;
      }

      @Override
      public int getWeight() {
         return this.weight;
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         return new LocalCache.WeightedSoftValueReference<>(var1, (V)var2, var3, this.weight);
      }
   }

   static final class WeightedStrongValueReference<K, V> extends LocalCache.StrongValueReference<K, V> {
      final int weight;

      WeightedStrongValueReference(V var1, int var2) {
         super((V)var1);
         this.weight = var2;
      }

      @Override
      public int getWeight() {
         return this.weight;
      }
   }

   static final class WeightedWeakValueReference<K, V> extends LocalCache.WeakValueReference<K, V> {
      final int weight;

      WeightedWeakValueReference(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3, int var4) {
         super(var1, (V)var2, var3);
         this.weight = var4;
      }

      @Override
      public int getWeight() {
         return this.weight;
      }

      @Override
      public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> var1, V var2, ReferenceEntry<K, V> var3) {
         return new LocalCache.WeightedWeakValueReference<>(var1, (V)var2, var3, this.weight);
      }
   }

   static final class WriteQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
      final ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>(this) {
         @Weak
         ReferenceEntry<K, V> nextWrite = this;
         @Weak
         ReferenceEntry<K, V> previousWrite = this;

         @Override
         public long getWriteTime() {
            return Long.MAX_VALUE;
         }

         @Override
         public void setWriteTime(long var1) {
         }

         @Override
         public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
         }

         @Override
         public void setNextInWriteQueue(ReferenceEntry<K, V> var1) {
            this.nextWrite = var1;
         }

         @Override
         public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
         }

         @Override
         public void setPreviousInWriteQueue(ReferenceEntry<K, V> var1) {
            this.previousWrite = var1;
         }
      };

      public boolean offer(ReferenceEntry<K, V> var1) {
         LocalCache.connectWriteOrder(var1.getPreviousInWriteQueue(), var1.getNextInWriteQueue());
         LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), var1);
         LocalCache.connectWriteOrder(var1, this.head);
         return true;
      }

      public ReferenceEntry<K, V> peek() {
         ReferenceEntry var1 = this.head.getNextInWriteQueue();
         return var1 == this.head ? null : var1;
      }

      public ReferenceEntry<K, V> poll() {
         ReferenceEntry var1 = this.head.getNextInWriteQueue();
         if (var1 == this.head) {
            return null;
         } else {
            this.remove(var1);
            return var1;
         }
      }

      @Override
      public boolean remove(Object var1) {
         ReferenceEntry var2 = (ReferenceEntry)var1;
         ReferenceEntry var3 = var2.getPreviousInWriteQueue();
         ReferenceEntry var4 = var2.getNextInWriteQueue();
         LocalCache.connectWriteOrder(var3, var4);
         LocalCache.nullifyWriteOrder(var2);
         return var4 != LocalCache.NullEntry.INSTANCE;
      }

      @Override
      public boolean contains(Object var1) {
         ReferenceEntry var2 = (ReferenceEntry)var1;
         return var2.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
      }

      @Override
      public boolean isEmpty() {
         return this.head.getNextInWriteQueue() == this.head;
      }

      @Override
      public int size() {
         int var1 = 0;

         for (ReferenceEntry var2 = this.head.getNextInWriteQueue(); var2 != this.head; var2 = var2.getNextInWriteQueue()) {
            var1++;
         }

         return var1;
      }

      @Override
      public void clear() {
         ReferenceEntry var1 = this.head.getNextInWriteQueue();

         while (var1 != this.head) {
            ReferenceEntry var2 = var1.getNextInWriteQueue();
            LocalCache.nullifyWriteOrder(var1);
            var1 = var2;
         }

         this.head.setNextInWriteQueue(this.head);
         this.head.setPreviousInWriteQueue(this.head);
      }

      @Override
      public Iterator<ReferenceEntry<K, V>> iterator() {
         return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
            protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> var1) {
               ReferenceEntry var2 = var1.getNextInWriteQueue();
               return var2 == WriteQueue.this.head ? null : var2;
            }
         };
      }
   }

   final class WriteThroughEntry implements Entry<K, V> {
      final K key;
      V value;

      WriteThroughEntry(K var2, V var3) {
         this.key = (K)var2;
         this.value = (V)var3;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            return this.key.equals(var2.getKey()) && this.value.equals(var2.getValue());
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode() ^ this.value.hashCode();
      }

      @Override
      public V setValue(V var1) {
         Object var2 = LocalCache.this.put(this.key, (V)var1);
         this.value = (V)var1;
         return (V)var2;
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.getKey());
         String var2 = String.valueOf(this.getValue());
         return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append("=").append(var2).toString();
      }
   }
}
