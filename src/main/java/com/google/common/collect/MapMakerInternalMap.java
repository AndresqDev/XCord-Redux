package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import org.checkerframework.checker.nullness.qual.Nullable;

@GwtIncompatible
class MapMakerInternalMap<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>, S extends MapMakerInternalMap.Segment<K, V, E, S>>
   extends AbstractMap<K, V>
   implements Serializable,
   ConcurrentMap<K, V> {
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final int MAX_SEGMENTS = 65536;
   static final int CONTAINS_VALUE_RETRIES = 3;
   static final int DRAIN_THRESHOLD = 63;
   static final int DRAIN_MAX = 16;
   static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
   final transient int segmentMask;
   final transient int segmentShift;
   final transient MapMakerInternalMap.Segment<K, V, E, S>[] segments;
   final int concurrencyLevel;
   final Equivalence<Object> keyEquivalence;
   final transient MapMakerInternalMap.InternalEntryHelper<K, V, E, S> entryHelper;
   static final MapMakerInternalMap.WeakValueReference<Object, Object, MapMakerInternalMap.DummyInternalEntry> UNSET_WEAK_VALUE_REFERENCE = new MapMakerInternalMap.WeakValueReference<Object, Object, MapMakerInternalMap.DummyInternalEntry>() {
      public MapMakerInternalMap.DummyInternalEntry getEntry() {
         return null;
      }

      @Override
      public void clear() {
      }

      @Override
      public Object get() {
         return null;
      }

      public MapMakerInternalMap.WeakValueReference<Object, Object, MapMakerInternalMap.DummyInternalEntry> copyFor(
         ReferenceQueue<Object> var1, MapMakerInternalMap.DummyInternalEntry var2
      ) {
         return this;
      }
   };
   @Nullable
   transient Set<K> keySet;
   @Nullable
   transient Collection<V> values;
   @Nullable
   transient Set<Entry<K, V>> entrySet;
   private static final long serialVersionUID = 5L;

   private MapMakerInternalMap(MapMaker var1, MapMakerInternalMap.InternalEntryHelper<K, V, E, S> var2) {
      this.concurrencyLevel = Math.min(var1.getConcurrencyLevel(), 65536);
      this.keyEquivalence = var1.getKeyEquivalence();
      this.entryHelper = var2;
      int var3 = Math.min(var1.getInitialCapacity(), 1073741824);
      int var4 = 0;

      byte var5;
      for (var5 = 1; var5 < this.concurrencyLevel; var5 <<= 1) {
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

      for (int var8 = 0; var8 < this.segments.length; var8++) {
         this.segments[var8] = this.createSegment(var7, -1);
      }
   }

   static <K, V> MapMakerInternalMap<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>, ?> create(MapMaker var0) {
      if (var0.getKeyStrength() == MapMakerInternalMap.Strength.STRONG && var0.getValueStrength() == MapMakerInternalMap.Strength.STRONG) {
         return (MapMakerInternalMap<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.StrongKeyStrongValueEntry.Helper.instance()
         ));
      } else if (var0.getKeyStrength() == MapMakerInternalMap.Strength.STRONG && var0.getValueStrength() == MapMakerInternalMap.Strength.WEAK) {
         return (MapMakerInternalMap<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.StrongKeyWeakValueEntry.Helper.instance()
         ));
      } else if (var0.getKeyStrength() == MapMakerInternalMap.Strength.WEAK && var0.getValueStrength() == MapMakerInternalMap.Strength.STRONG) {
         return (MapMakerInternalMap<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.WeakKeyStrongValueEntry.Helper.instance()
         ));
      } else if (var0.getKeyStrength() == MapMakerInternalMap.Strength.WEAK && var0.getValueStrength() == MapMakerInternalMap.Strength.WEAK) {
         return (MapMakerInternalMap<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.WeakKeyWeakValueEntry.Helper.instance()
         ));
      } else {
         throw new AssertionError();
      }
   }

   static <K> MapMakerInternalMap<K, MapMaker.Dummy, ? extends MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?>, ?> createWithDummyValues(MapMaker var0) {
      if (var0.getKeyStrength() == MapMakerInternalMap.Strength.STRONG && var0.getValueStrength() == MapMakerInternalMap.Strength.STRONG) {
         return (MapMakerInternalMap<K, MapMaker.Dummy, ? extends MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.StrongKeyDummyValueEntry.Helper.instance()
         ));
      } else if (var0.getKeyStrength() == MapMakerInternalMap.Strength.WEAK && var0.getValueStrength() == MapMakerInternalMap.Strength.STRONG) {
         return (MapMakerInternalMap<K, MapMaker.Dummy, ? extends MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?>, ?>)(new MapMakerInternalMap<>(
            var0, MapMakerInternalMap.WeakKeyDummyValueEntry.Helper.instance()
         ));
      } else if (var0.getValueStrength() == MapMakerInternalMap.Strength.WEAK) {
         throw new IllegalArgumentException("Map cannot have both weak and dummy values");
      } else {
         throw new AssertionError();
      }
   }

   static <K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> MapMakerInternalMap.WeakValueReference<K, V, E> unsetWeakValueReference() {
      return (MapMakerInternalMap.WeakValueReference<K, V, E>)UNSET_WEAK_VALUE_REFERENCE;
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
   E copyEntry(E var1, E var2) {
      int var3 = var1.getHash();
      return this.segmentFor(var3).copyEntry((E)var1, (E)var2);
   }

   int hash(Object var1) {
      int var2 = this.keyEquivalence.hash(var1);
      return rehash(var2);
   }

   void reclaimValue(MapMakerInternalMap.WeakValueReference<K, V, E> var1) {
      MapMakerInternalMap.InternalEntry var2 = var1.getEntry();
      int var3 = var2.getHash();
      this.segmentFor(var3).reclaimValue((K)var2.getKey(), var3, var1);
   }

   void reclaimKey(E var1) {
      int var2 = var1.getHash();
      this.segmentFor(var2).reclaimKey((E)var1, var2);
   }

   @VisibleForTesting
   boolean isLiveForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
      return this.segmentFor(var1.getHash()).getLiveValueForTesting(var1) != null;
   }

   MapMakerInternalMap.Segment<K, V, E, S> segmentFor(int var1) {
      return this.segments[var1 >>> this.segmentShift & this.segmentMask];
   }

   MapMakerInternalMap.Segment<K, V, E, S> createSegment(int var1, int var2) {
      return this.entryHelper.newSegment(this, var1, var2);
   }

   V getLiveValue(E var1) {
      return (V)(var1.getKey() == null ? null : var1.getValue());
   }

   final MapMakerInternalMap.Segment<K, V, E, S>[] newSegmentArray(int var1) {
      return new MapMakerInternalMap.Segment[var1];
   }

   @VisibleForTesting
   MapMakerInternalMap.Strength keyStrength() {
      return this.entryHelper.keyStrength();
   }

   @VisibleForTesting
   MapMakerInternalMap.Strength valueStrength() {
      return this.entryHelper.valueStrength();
   }

   @VisibleForTesting
   Equivalence<Object> valueEquivalence() {
      return this.entryHelper.valueStrength().defaultEquivalence();
   }

   @Override
   public boolean isEmpty() {
      long var1 = 0L;
      MapMakerInternalMap.Segment[] var3 = this.segments;

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

   @Override
   public int size() {
      MapMakerInternalMap.Segment[] var1 = this.segments;
      long var2 = 0L;

      for (int var4 = 0; var4 < var1.length; var4++) {
         var2 += (long)var1[var4].count;
      }

      return Ints.saturatedCast(var2);
   }

   @Override
   public V get(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).get(var1, var2);
      }
   }

   E getEntry(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).getEntry(var1, var2);
      }
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
         MapMakerInternalMap.Segment[] var2 = this.segments;
         long var3 = -1L;

         for (int var5 = 0; var5 < 3; var5++) {
            long var6 = 0L;

            for (MapMakerInternalMap.Segment var11 : var2) {
               int var12 = var11.count;
               AtomicReferenceArray var13 = var11.table;

               for (int var14 = 0; var14 < var13.length(); var14++) {
                  for (MapMakerInternalMap.InternalEntry var15 = (MapMakerInternalMap.InternalEntry)var13.get(var14); var15 != null; var15 = var15.getNext()) {
                     Object var16 = var11.getLiveValue((E)var15);
                     if (var16 != null && this.valueEquivalence().equivalent(var1, var16)) {
                        return true;
                     }
                  }
               }

               var6 += (long)var11.modCount;
            }

            if (var6 == var3) {
               break;
            }

            var3 = var6;
         }

         return false;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public V put(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put((K)var1, var3, (V)var2, false);
   }

   @CanIgnoreReturnValue
   @Override
   public V putIfAbsent(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).put((K)var1, var3, (V)var2, true);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.put((K)var3.getKey(), (V)var3.getValue());
      }
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = this.hash(var1);
         return this.segmentFor(var2).remove(var1, var2);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public boolean remove(@Nullable Object var1, @Nullable Object var2) {
      if (var1 != null && var2 != null) {
         int var3 = this.hash(var1);
         return this.segmentFor(var3).remove(var1, var3, var2);
      } else {
         return false;
      }
   }

   @CanIgnoreReturnValue
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

   @CanIgnoreReturnValue
   @Override
   public V replace(K var1, V var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      int var3 = this.hash(var1);
      return this.segmentFor(var3).replace((K)var1, var3, (V)var2);
   }

   @Override
   public void clear() {
      for (MapMakerInternalMap.Segment var4 : this.segments) {
         var4.clear();
      }
   }

   @Override
   public Set<K> keySet() {
      Set var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new MapMakerInternalMap.KeySet());
   }

   @Override
   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 != null ? var1 : (this.values = new MapMakerInternalMap.Values());
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      Set var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new MapMakerInternalMap.EntrySet());
   }

   private static <E> ArrayList<E> toArrayList(Collection<E> var0) {
      ArrayList var1 = new ArrayList(var0.size());
      Iterators.addAll(var1, var0.iterator());
      return var1;
   }

   Object writeReplace() {
      return new MapMakerInternalMap.SerializationProxy<>(
         this.entryHelper.keyStrength(),
         this.entryHelper.valueStrength(),
         this.keyEquivalence,
         this.entryHelper.valueStrength().defaultEquivalence(),
         this.concurrencyLevel,
         this
      );
   }

   abstract static class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable {
      private static final long serialVersionUID = 3L;
      final MapMakerInternalMap.Strength keyStrength;
      final MapMakerInternalMap.Strength valueStrength;
      final Equivalence<Object> keyEquivalence;
      final Equivalence<Object> valueEquivalence;
      final int concurrencyLevel;
      transient ConcurrentMap<K, V> delegate;

      AbstractSerializationProxy(
         MapMakerInternalMap.Strength var1,
         MapMakerInternalMap.Strength var2,
         Equivalence<Object> var3,
         Equivalence<Object> var4,
         int var5,
         ConcurrentMap<K, V> var6
      ) {
         this.keyStrength = var1;
         this.valueStrength = var2;
         this.keyEquivalence = var3;
         this.valueEquivalence = var4;
         this.concurrencyLevel = var5;
         this.delegate = var6;
      }

      @Override
      protected ConcurrentMap<K, V> delegate() {
         return this.delegate;
      }

      void writeMapTo(ObjectOutputStream var1) throws IOException {
         var1.writeInt(this.delegate.size());

         for (Entry var3 : this.delegate.entrySet()) {
            var1.writeObject(var3.getKey());
            var1.writeObject(var3.getValue());
         }

         var1.writeObject(null);
      }

      MapMaker readMapMaker(ObjectInputStream var1) throws IOException {
         int var2 = var1.readInt();
         return new MapMaker()
            .initialCapacity(var2)
            .setKeyStrength(this.keyStrength)
            .setValueStrength(this.valueStrength)
            .keyEquivalence(this.keyEquivalence)
            .concurrencyLevel(this.concurrencyLevel);
      }

      void readEntries(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         while (true) {
            Object var2 = var1.readObject();
            if (var2 == null) {
               return;
            }

            Object var3 = var1.readObject();
            this.delegate.put((K)var2, (V)var3);
         }
      }
   }

   abstract static class AbstractStrongKeyEntry<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>>
      implements MapMakerInternalMap.InternalEntry<K, V, E> {
      final K key;
      final int hash;
      @Nullable
      final E next;

      AbstractStrongKeyEntry(K var1, int var2, @Nullable E var3) {
         this.key = (K)var1;
         this.hash = var2;
         this.next = (E)var3;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public int getHash() {
         return this.hash;
      }

      @Override
      public E getNext() {
         return this.next;
      }
   }

   abstract static class AbstractWeakKeyEntry<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>>
      extends WeakReference<K>
      implements MapMakerInternalMap.InternalEntry<K, V, E> {
      final int hash;
      @Nullable
      final E next;

      AbstractWeakKeyEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable E var4) {
         super((K)var2, var1);
         this.hash = var3;
         this.next = (E)var4;
      }

      @Override
      public K getKey() {
         return this.get();
      }

      @Override
      public int getHash() {
         return this.hash;
      }

      @Override
      public E getNext() {
         return this.next;
      }
   }

   static final class CleanupMapTask implements Runnable {
      final WeakReference<MapMakerInternalMap<?, ?, ?, ?>> mapReference;

      public CleanupMapTask(MapMakerInternalMap<?, ?, ?, ?> var1) {
         this.mapReference = new WeakReference<>(var1);
      }

      @Override
      public void run() {
         MapMakerInternalMap var1 = this.mapReference.get();
         if (var1 == null) {
            throw new CancellationException();
         } else {
            for (MapMakerInternalMap.Segment var5 : var1.segments) {
               var5.runCleanup();
            }
         }
      }
   }

   static final class DummyInternalEntry implements MapMakerInternalMap.InternalEntry<Object, Object, MapMakerInternalMap.DummyInternalEntry> {
      private DummyInternalEntry() {
         throw new AssertionError();
      }

      public MapMakerInternalMap.DummyInternalEntry getNext() {
         throw new AssertionError();
      }

      @Override
      public int getHash() {
         throw new AssertionError();
      }

      @Override
      public Object getKey() {
         throw new AssertionError();
      }

      @Override
      public Object getValue() {
         throw new AssertionError();
      }
   }

   final class EntryIterator extends MapMakerInternalMap<K, V, E, S>.HashIterator<Entry<K, V>> {
      EntryIterator(MapMakerInternalMap var1) {
      }

      public Entry<K, V> next() {
         return this.nextEntry();
      }
   }

   final class EntrySet extends MapMakerInternalMap.SafeToArraySet<Entry<K, V>> {
      @Override
      public Iterator<Entry<K, V>> iterator() {
         return MapMakerInternalMap.this.new EntryIterator(MapMakerInternalMap.this);
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
               Object var4 = MapMakerInternalMap.this.get(var3);
               return var4 != null && MapMakerInternalMap.this.valueEquivalence().equivalent(var2.getValue(), var4);
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
            return var3 != null && MapMakerInternalMap.this.remove(var3, var2.getValue());
         }
      }

      @Override
      public int size() {
         return MapMakerInternalMap.this.size();
      }

      @Override
      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      @Override
      public void clear() {
         MapMakerInternalMap.this.clear();
      }
   }

   abstract class HashIterator<T> implements Iterator<T> {
      int nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
      int nextTableIndex = -1;
      @Nullable
      MapMakerInternalMap.Segment<K, V, E, S> currentSegment;
      @Nullable
      AtomicReferenceArray<E> currentTable;
      @Nullable
      E nextEntry;
      MapMakerInternalMap<K, V, E, S>.WriteThroughEntry nextExternal;
      MapMakerInternalMap<K, V, E, S>.WriteThroughEntry lastReturned;

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
                  this.currentSegment = MapMakerInternalMap.this.segments[this.nextSegmentIndex--];
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

      boolean advanceTo(E var1) {
         boolean var4;
         try {
            Object var2 = var1.getKey();
            Object var3 = MapMakerInternalMap.this.getLiveValue((E)var1);
            if (var3 == null) {
               return false;
            }

            this.nextExternal = MapMakerInternalMap.this.new WriteThroughEntry(var2, var3);
            var4 = true;
         } finally {
            this.currentSegment.postReadCleanup();
         }

         return var4;
      }

      @Override
      public boolean hasNext() {
         return this.nextExternal != null;
      }

      MapMakerInternalMap<K, V, E, S>.WriteThroughEntry nextEntry() {
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
         CollectPreconditions.checkRemove(this.lastReturned != null);
         MapMakerInternalMap.this.remove(this.lastReturned.getKey());
         this.lastReturned = null;
      }
   }

   interface InternalEntry<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> {
      E getNext();

      int getHash();

      K getKey();

      V getValue();
   }

   interface InternalEntryHelper<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>, S extends MapMakerInternalMap.Segment<K, V, E, S>> {
      MapMakerInternalMap.Strength keyStrength();

      MapMakerInternalMap.Strength valueStrength();

      S newSegment(MapMakerInternalMap<K, V, E, S> var1, int var2, int var3);

      E newEntry(S var1, K var2, int var3, @Nullable E var4);

      E copy(S var1, E var2, @Nullable E var3);

      void setValue(S var1, E var2, V var3);
   }

   final class KeyIterator extends MapMakerInternalMap<K, V, E, S>.HashIterator<K> {
      KeyIterator(MapMakerInternalMap var1) {
      }

      @Override
      public K next() {
         return this.nextEntry().getKey();
      }
   }

   final class KeySet extends MapMakerInternalMap.SafeToArraySet<K> {
      @Override
      public Iterator<K> iterator() {
         return MapMakerInternalMap.this.new KeyIterator(MapMakerInternalMap.this);
      }

      @Override
      public int size() {
         return MapMakerInternalMap.this.size();
      }

      @Override
      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return MapMakerInternalMap.this.containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         return MapMakerInternalMap.this.remove(var1) != null;
      }

      @Override
      public void clear() {
         MapMakerInternalMap.this.clear();
      }
   }

   private abstract static class SafeToArraySet<E> extends AbstractSet<E> {
      private SafeToArraySet() {
      }

      @Override
      public Object[] toArray() {
         return MapMakerInternalMap.toArrayList(this).toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])MapMakerInternalMap.toArrayList(this).toArray(var1);
      }
   }

   abstract static class Segment<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>, S extends MapMakerInternalMap.Segment<K, V, E, S>>
      extends ReentrantLock {
      @Weak
      final MapMakerInternalMap<K, V, E, S> map;
      volatile int count;
      int modCount;
      int threshold;
      @Nullable
      volatile AtomicReferenceArray<E> table;
      final int maxSegmentSize;
      final AtomicInteger readCount = new AtomicInteger();

      Segment(MapMakerInternalMap<K, V, E, S> var1, int var2, int var3) {
         this.map = var1;
         this.maxSegmentSize = var3;
         this.initTable(this.newEntryArray(var2));
      }

      abstract S self();

      @GuardedBy("this")
      void maybeDrainReferenceQueues() {
      }

      void maybeClearReferenceQueues() {
      }

      void setValue(E var1, V var2) {
         this.map.entryHelper.setValue(this.self(), (E)var1, (V)var2);
      }

      E copyEntry(E var1, E var2) {
         return this.map.entryHelper.copy(this.self(), (E)var1, (E)var2);
      }

      AtomicReferenceArray<E> newEntryArray(int var1) {
         return new AtomicReferenceArray<>(var1);
      }

      void initTable(AtomicReferenceArray<E> var1) {
         this.threshold = var1.length() * 3 / 4;
         if (this.threshold == this.maxSegmentSize) {
            this.threshold++;
         }

         this.table = var1;
      }

      abstract E castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1);

      ReferenceQueue<K> getKeyReferenceQueueForTesting() {
         throw new AssertionError();
      }

      ReferenceQueue<V> getValueReferenceQueueForTesting() {
         throw new AssertionError();
      }

      MapMakerInternalMap.WeakValueReference<K, V, E> getWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         throw new AssertionError();
      }

      MapMakerInternalMap.WeakValueReference<K, V, E> newWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1, V var2) {
         throw new AssertionError();
      }

      void setWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1,
         MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> var2
      ) {
         throw new AssertionError();
      }

      void setTableEntryForTesting(int var1, MapMakerInternalMap.InternalEntry<K, V, ?> var2) {
         this.table.set(var1, this.castForTesting(var2));
      }

      E copyForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1, @Nullable MapMakerInternalMap.InternalEntry<K, V, ?> var2) {
         return this.map.entryHelper.copy(this.self(), this.castForTesting(var1), this.castForTesting(var2));
      }

      void setValueForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1, V var2) {
         this.map.entryHelper.setValue(this.self(), this.castForTesting(var1), (V)var2);
      }

      E newEntryForTesting(K var1, int var2, @Nullable MapMakerInternalMap.InternalEntry<K, V, ?> var3) {
         return this.map.entryHelper.newEntry(this.self(), (K)var1, var2, this.castForTesting(var3));
      }

      @CanIgnoreReturnValue
      boolean removeTableEntryForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return this.removeEntryForTesting(this.castForTesting(var1));
      }

      E removeFromChainForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1, MapMakerInternalMap.InternalEntry<K, V, ?> var2) {
         return this.removeFromChain(this.castForTesting(var1), this.castForTesting(var2));
      }

      @Nullable
      V getLiveValueForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return this.getLiveValue(this.castForTesting(var1));
      }

      void tryDrainReferenceQueues() {
         if (this.tryLock()) {
            try {
               this.maybeDrainReferenceQueues();
            } finally {
               this.unlock();
            }
         }
      }

      @GuardedBy("this")
      void drainKeyReferenceQueue(ReferenceQueue<K> var1) {
         int var3 = 0;

         Reference var2;
         while ((var2 = var1.poll()) != null) {
            MapMakerInternalMap.InternalEntry var4 = (MapMakerInternalMap.InternalEntry)var2;
            this.map.reclaimKey((E)var4);
            if (++var3 == 16) {
               break;
            }
         }
      }

      @GuardedBy("this")
      void drainValueReferenceQueue(ReferenceQueue<V> var1) {
         int var3 = 0;

         Reference var2;
         while ((var2 = var1.poll()) != null) {
            MapMakerInternalMap.WeakValueReference var4 = (MapMakerInternalMap.WeakValueReference)var2;
            this.map.reclaimValue(var4);
            if (++var3 == 16) {
               break;
            }
         }
      }

      <T> void clearReferenceQueue(ReferenceQueue<T> var1) {
         while (var1.poll() != null) {
         }
      }

      E getFirst(int var1) {
         AtomicReferenceArray var2 = this.table;
         return (E)var2.get(var1 & var2.length() - 1);
      }

      E getEntry(Object var1, int var2) {
         if (this.count != 0) {
            for (MapMakerInternalMap.InternalEntry var3 = this.getFirst(var2); var3 != null; var3 = var3.getNext()) {
               if (var3.getHash() == var2) {
                  Object var4 = var3.getKey();
                  if (var4 == null) {
                     this.tryDrainReferenceQueues();
                  } else if (this.map.keyEquivalence.equivalent(var1, var4)) {
                     return (E)var3;
                  }
               }
            }
         }

         return null;
      }

      E getLiveEntry(Object var1, int var2) {
         return this.getEntry(var1, var2);
      }

      V get(Object var1, int var2) {
         Object var4;
         try {
            MapMakerInternalMap.InternalEntry var3 = this.getLiveEntry(var1, var2);
            if (var3 != null) {
               var4 = var3.getValue();
               if (var4 == null) {
                  this.tryDrainReferenceQueues();
               }

               return (V)var4;
            }

            var4 = null;
         } finally {
            this.postReadCleanup();
         }

         return (V)var4;
      }

      boolean containsKey(Object var1, int var2) {
         boolean var4;
         try {
            if (this.count == 0) {
               return false;
            }

            MapMakerInternalMap.InternalEntry var3 = this.getLiveEntry(var1, var2);
            var4 = var3 != null && var3.getValue() != null;
         } finally {
            this.postReadCleanup();
         }

         return var4;
      }

      @VisibleForTesting
      boolean containsValue(Object var1) {
         try {
            if (this.count != 0) {
               AtomicReferenceArray var2 = this.table;
               int var3 = var2.length();

               for (int var4 = 0; var4 < var3; var4++) {
                  for (MapMakerInternalMap.InternalEntry var5 = (MapMakerInternalMap.InternalEntry)var2.get(var4); var5 != null; var5 = var5.getNext()) {
                     Object var6 = this.getLiveValue((E)var5);
                     if (var6 != null && this.map.valueEquivalence().equivalent(var1, var6)) {
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

      V put(K var1, int var2, V var3, boolean var4) {
         this.lock();

         try {
            this.preWriteCleanup();
            int var5 = this.count + 1;
            if (var5 > this.threshold) {
               this.expand();
               var5 = this.count + 1;
            }

            AtomicReferenceArray var6 = this.table;
            int var7 = var2 & var6.length() - 1;
            MapMakerInternalMap.InternalEntry var8 = (MapMakerInternalMap.InternalEntry)var6.get(var7);

            for (MapMakerInternalMap.InternalEntry var9 = var8; var9 != null; var9 = var9.getNext()) {
               Object var10 = var9.getKey();
               if (var9.getHash() == var2 && var10 != null && this.map.keyEquivalence.equivalent(var1, var10)) {
                  Object var11 = var9.getValue();
                  if (var11 == null) {
                     this.modCount++;
                     this.setValue((E)var9, (V)var3);
                     var5 = this.count;
                     this.count = var5;
                     return null;
                  }

                  if (!var4) {
                     this.modCount++;
                     this.setValue((E)var9, (V)var3);
                     return (V)var11;
                  }

                  return (V)var11;
               }
            }

            this.modCount++;
            MapMakerInternalMap.InternalEntry var17 = this.map.entryHelper.newEntry(this.self(), (K)var1, var2, (E)var8);
            this.setValue((E)var17, (V)var3);
            var6.set(var7, var17);
            this.count = var5;
            return null;
         } finally {
            this.unlock();
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
               MapMakerInternalMap.InternalEntry var7 = (MapMakerInternalMap.InternalEntry)var1.get(var6);
               if (var7 != null) {
                  MapMakerInternalMap.InternalEntry var8 = var7.getNext();
                  int var9 = var7.getHash() & var5;
                  if (var8 == null) {
                     var4.set(var9, var7);
                  } else {
                     MapMakerInternalMap.InternalEntry var10 = var7;
                     int var11 = var9;

                     for (MapMakerInternalMap.InternalEntry var12 = var8; var12 != null; var12 = var12.getNext()) {
                        int var13 = var12.getHash() & var5;
                        if (var13 != var11) {
                           var11 = var13;
                           var10 = var12;
                        }
                     }

                     var4.set(var11, var10);

                     for (MapMakerInternalMap.InternalEntry var16 = var7; var16 != var10; var16 = var16.getNext()) {
                        int var17 = var16.getHash() & var5;
                        MapMakerInternalMap.InternalEntry var14 = (MapMakerInternalMap.InternalEntry)var4.get(var17);
                        MapMakerInternalMap.InternalEntry var15 = this.copyEntry((E)var16, (E)var14);
                        if (var15 != null) {
                           var4.set(var17, var15);
                        } else {
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
            this.preWriteCleanup();
            AtomicReferenceArray var5 = this.table;
            int var6 = var2 & var5.length() - 1;
            MapMakerInternalMap.InternalEntry var7 = (MapMakerInternalMap.InternalEntry)var5.get(var6);

            for (MapMakerInternalMap.InternalEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
               Object var9 = var8.getKey();
               if (var8.getHash() == var2 && var9 != null && this.map.keyEquivalence.equivalent(var1, var9)) {
                  Object var10 = var8.getValue();
                  if (var10 != null) {
                     if (!this.map.valueEquivalence().equivalent(var3, var10)) {
                        return false;
                     }

                     this.modCount++;
                     this.setValue((E)var8, (V)var4);
                     return true;
                  }

                  if (isCollected((E)var8)) {
                     int var11 = this.count - 1;
                     this.modCount++;
                     MapMakerInternalMap.InternalEntry var12 = this.removeFromChain((E)var7, (E)var8);
                     var11 = this.count - 1;
                     var5.set(var6, var12);
                     this.count = var11;
                  }

                  return false;
               }
            }

            return false;
         } finally {
            this.unlock();
         }
      }

      V replace(K var1, int var2, V var3) {
         this.lock();

         try {
            this.preWriteCleanup();
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            MapMakerInternalMap.InternalEntry var6 = (MapMakerInternalMap.InternalEntry)var4.get(var5);

            for (MapMakerInternalMap.InternalEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               Object var8 = var7.getKey();
               if (var7.getHash() == var2 && var8 != null && this.map.keyEquivalence.equivalent(var1, var8)) {
                  Object var9 = var7.getValue();
                  if (var9 != null) {
                     this.modCount++;
                     this.setValue((E)var7, (V)var3);
                     return (V)var9;
                  }

                  if (isCollected((E)var7)) {
                     int var10 = this.count - 1;
                     this.modCount++;
                     MapMakerInternalMap.InternalEntry var11 = this.removeFromChain((E)var6, (E)var7);
                     var10 = this.count - 1;
                     var4.set(var5, var11);
                     this.count = var10;
                  }

                  return null;
               }
            }

            return null;
         } finally {
            this.unlock();
         }
      }

      @CanIgnoreReturnValue
      V remove(Object var1, int var2) {
         this.lock();

         try {
            this.preWriteCleanup();
            int var3 = this.count - 1;
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            MapMakerInternalMap.InternalEntry var6 = (MapMakerInternalMap.InternalEntry)var4.get(var5);

            for (MapMakerInternalMap.InternalEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               Object var8 = var7.getKey();
               if (var7.getHash() == var2 && var8 != null && this.map.keyEquivalence.equivalent(var1, var8)) {
                  Object var9 = var7.getValue();
                  if (var9 != null || isCollected((E)var7)) {
                     this.modCount++;
                     MapMakerInternalMap.InternalEntry var17 = this.removeFromChain((E)var6, (E)var7);
                     var3 = this.count - 1;
                     var4.set(var5, var17);
                     this.count = var3;
                     return (V)var9;
                  }

                  return null;
               }
            }

            return null;
         } finally {
            this.unlock();
         }
      }

      boolean remove(Object var1, int var2, Object var3) {
         this.lock();

         try {
            this.preWriteCleanup();
            int var4 = this.count - 1;
            AtomicReferenceArray var5 = this.table;
            int var6 = var2 & var5.length() - 1;
            MapMakerInternalMap.InternalEntry var7 = (MapMakerInternalMap.InternalEntry)var5.get(var6);

            for (MapMakerInternalMap.InternalEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
               Object var9 = var8.getKey();
               if (var8.getHash() == var2 && var9 != null && this.map.keyEquivalence.equivalent(var1, var9)) {
                  Object var10 = var8.getValue();
                  boolean var11 = false;
                  if (this.map.valueEquivalence().equivalent(var3, var10)) {
                     var11 = true;
                  } else if (!isCollected((E)var8)) {
                     return false;
                  }

                  this.modCount++;
                  MapMakerInternalMap.InternalEntry var12 = this.removeFromChain((E)var7, (E)var8);
                  var4 = this.count - 1;
                  var5.set(var6, var12);
                  this.count = var4;
                  return var11;
               }
            }

            return false;
         } finally {
            this.unlock();
         }
      }

      void clear() {
         if (this.count != 0) {
            this.lock();

            try {
               AtomicReferenceArray var1 = this.table;

               for (int var2 = 0; var2 < var1.length(); var2++) {
                  var1.set(var2, null);
               }

               this.maybeClearReferenceQueues();
               this.readCount.set(0);
               this.modCount++;
               this.count = 0;
            } finally {
               this.unlock();
            }
         }
      }

      @GuardedBy("this")
      E removeFromChain(E var1, E var2) {
         int var3 = this.count;
         MapMakerInternalMap.InternalEntry var4 = var2.getNext();

         for (MapMakerInternalMap.InternalEntry var5 = var1; var5 != var2; var5 = var5.getNext()) {
            MapMakerInternalMap.InternalEntry var6 = this.copyEntry((E)var5, (E)var4);
            if (var6 != null) {
               var4 = var6;
            } else {
               var3--;
            }
         }

         this.count = var3;
         return (E)var4;
      }

      @CanIgnoreReturnValue
      boolean reclaimKey(E var1, int var2) {
         this.lock();

         try {
            int var3 = this.count - 1;
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            MapMakerInternalMap.InternalEntry var6 = (MapMakerInternalMap.InternalEntry)var4.get(var5);

            for (MapMakerInternalMap.InternalEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               if (var7 == var1) {
                  this.modCount++;
                  MapMakerInternalMap.InternalEntry var8 = this.removeFromChain((E)var6, (E)var7);
                  var3 = this.count - 1;
                  var4.set(var5, var8);
                  this.count = var3;
                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
         }
      }

      @CanIgnoreReturnValue
      boolean reclaimValue(K var1, int var2, MapMakerInternalMap.WeakValueReference<K, V, E> var3) {
         this.lock();

         try {
            int var4 = this.count - 1;
            AtomicReferenceArray var5 = this.table;
            int var6 = var2 & var5.length() - 1;
            MapMakerInternalMap.InternalEntry var7 = (MapMakerInternalMap.InternalEntry)var5.get(var6);

            for (MapMakerInternalMap.InternalEntry var8 = var7; var8 != null; var8 = var8.getNext()) {
               Object var9 = var8.getKey();
               if (var8.getHash() == var2 && var9 != null && this.map.keyEquivalence.equivalent(var1, var9)) {
                  MapMakerInternalMap.WeakValueReference var10 = ((MapMakerInternalMap.WeakValueEntry)var8).getValueReference();
                  if (var10 != var3) {
                     return false;
                  }

                  this.modCount++;
                  MapMakerInternalMap.InternalEntry var11 = this.removeFromChain((E)var7, (E)var8);
                  var4 = this.count - 1;
                  var5.set(var6, var11);
                  this.count = var4;
                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
         }
      }

      @CanIgnoreReturnValue
      boolean clearValueForTesting(K var1, int var2, MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> var3) {
         this.lock();

         try {
            AtomicReferenceArray var4 = this.table;
            int var5 = var2 & var4.length() - 1;
            MapMakerInternalMap.InternalEntry var6 = (MapMakerInternalMap.InternalEntry)var4.get(var5);

            for (MapMakerInternalMap.InternalEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
               Object var8 = var7.getKey();
               if (var7.getHash() == var2 && var8 != null && this.map.keyEquivalence.equivalent(var1, var8)) {
                  MapMakerInternalMap.WeakValueReference var9 = ((MapMakerInternalMap.WeakValueEntry)var7).getValueReference();
                  if (var9 != var3) {
                     return false;
                  }

                  MapMakerInternalMap.InternalEntry var10 = this.removeFromChain((E)var6, (E)var7);
                  var4.set(var5, var10);
                  return true;
               }
            }

            return false;
         } finally {
            this.unlock();
         }
      }

      @GuardedBy("this")
      boolean removeEntryForTesting(E var1) {
         int var2 = var1.getHash();
         int var3 = this.count - 1;
         AtomicReferenceArray var4 = this.table;
         int var5 = var2 & var4.length() - 1;
         MapMakerInternalMap.InternalEntry var6 = (MapMakerInternalMap.InternalEntry)var4.get(var5);

         for (MapMakerInternalMap.InternalEntry var7 = var6; var7 != null; var7 = var7.getNext()) {
            if (var7 == var1) {
               this.modCount++;
               MapMakerInternalMap.InternalEntry var8 = this.removeFromChain((E)var6, (E)var7);
               var3 = this.count - 1;
               var4.set(var5, var8);
               this.count = var3;
               return true;
            }
         }

         return false;
      }

      static <K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> boolean isCollected(E var0) {
         return var0.getValue() == null;
      }

      @Nullable
      V getLiveValue(E var1) {
         if (var1.getKey() == null) {
            this.tryDrainReferenceQueues();
            return null;
         } else {
            Object var2 = var1.getValue();
            if (var2 == null) {
               this.tryDrainReferenceQueues();
               return null;
            } else {
               return (V)var2;
            }
         }
      }

      void postReadCleanup() {
         if ((this.readCount.incrementAndGet() & 63) == 0) {
            this.runCleanup();
         }
      }

      @GuardedBy("this")
      void preWriteCleanup() {
         this.runLockedCleanup();
      }

      void runCleanup() {
         this.runLockedCleanup();
      }

      void runLockedCleanup() {
         if (this.tryLock()) {
            try {
               this.maybeDrainReferenceQueues();
               this.readCount.set(0);
            } finally {
               this.unlock();
            }
         }
      }
   }

   private static final class SerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V> {
      private static final long serialVersionUID = 3L;

      SerializationProxy(
         MapMakerInternalMap.Strength var1,
         MapMakerInternalMap.Strength var2,
         Equivalence<Object> var3,
         Equivalence<Object> var4,
         int var5,
         ConcurrentMap<K, V> var6
      ) {
         super(var1, var2, var3, var4, var5, var6);
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         this.writeMapTo(var1);
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         MapMaker var2 = this.readMapMaker(var1);
         this.delegate = var2.makeMap();
         this.readEntries(var1);
      }

      private Object readResolve() {
         return this.delegate;
      }
   }

   static enum Strength {
      STRONG {
         @Override
         Equivalence<Object> defaultEquivalence() {
            return Equivalence.equals();
         }
      },
      WEAK {
         @Override
         Equivalence<Object> defaultEquivalence() {
            return Equivalence.identity();
         }
      };

      private Strength() {
      }

      abstract Equivalence<Object> defaultEquivalence();
   }

   static final class StrongKeyDummyValueEntry<K>
      extends MapMakerInternalMap.AbstractStrongKeyEntry<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>>
      implements MapMakerInternalMap.StrongValueEntry<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>> {
      StrongKeyDummyValueEntry(K var1, int var2, @Nullable MapMakerInternalMap.StrongKeyDummyValueEntry<K> var3) {
         super((K)var1, var2, var3);
      }

      public MapMaker.Dummy getValue() {
         return MapMaker.Dummy.VALUE;
      }

      void setValue(MapMaker.Dummy var1) {
      }

      MapMakerInternalMap.StrongKeyDummyValueEntry<K> copy(MapMakerInternalMap.StrongKeyDummyValueEntry<K> var1) {
         return new MapMakerInternalMap.StrongKeyDummyValueEntry<>(this.key, this.hash, var1);
      }

      static final class Helper<K>
         implements MapMakerInternalMap.InternalEntryHelper<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> {
         private static final MapMakerInternalMap.StrongKeyDummyValueEntry.Helper<?> INSTANCE = new MapMakerInternalMap.StrongKeyDummyValueEntry.Helper();

         static <K> MapMakerInternalMap.StrongKeyDummyValueEntry.Helper<K> instance() {
            return (MapMakerInternalMap.StrongKeyDummyValueEntry.Helper<K>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         public MapMakerInternalMap.StrongKeyDummyValueSegment<K> newSegment(
            MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.StrongKeyDummyValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.StrongKeyDummyValueEntry<K> copy(
            MapMakerInternalMap.StrongKeyDummyValueSegment<K> var1,
            MapMakerInternalMap.StrongKeyDummyValueEntry<K> var2,
            @Nullable MapMakerInternalMap.StrongKeyDummyValueEntry<K> var3
         ) {
            return var2.copy(var3);
         }

         public void setValue(MapMakerInternalMap.StrongKeyDummyValueSegment<K> var1, MapMakerInternalMap.StrongKeyDummyValueEntry<K> var2, MapMaker.Dummy var3) {
         }

         public MapMakerInternalMap.StrongKeyDummyValueEntry<K> newEntry(
            MapMakerInternalMap.StrongKeyDummyValueSegment<K> var1, K var2, int var3, @Nullable MapMakerInternalMap.StrongKeyDummyValueEntry<K> var4
         ) {
            return new MapMakerInternalMap.StrongKeyDummyValueEntry<>((K)var2, var3, var4);
         }
      }
   }

   static final class StrongKeyDummyValueSegment<K>
      extends MapMakerInternalMap.Segment<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> {
      StrongKeyDummyValueSegment(
         MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> var1,
         int var2,
         int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.StrongKeyDummyValueSegment<K> self() {
         return this;
      }

      public MapMakerInternalMap.StrongKeyDummyValueEntry<K> castForTesting(MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?> var1) {
         return (MapMakerInternalMap.StrongKeyDummyValueEntry<K>)var1;
      }
   }

   static final class StrongKeyStrongValueEntry<K, V>
      extends MapMakerInternalMap.AbstractStrongKeyEntry<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>>
      implements MapMakerInternalMap.StrongValueEntry<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>> {
      @Nullable
      private volatile V value = (V)null;

      StrongKeyStrongValueEntry(K var1, int var2, @Nullable MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var3) {
         super((K)var1, var2, var3);
      }

      @Nullable
      @Override
      public V getValue() {
         return this.value;
      }

      void setValue(V var1) {
         this.value = (V)var1;
      }

      MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> copy(MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var1) {
         MapMakerInternalMap.StrongKeyStrongValueEntry var2 = new MapMakerInternalMap.StrongKeyStrongValueEntry(this.key, this.hash, var1);
         var2.value = this.value;
         return var2;
      }

      static final class Helper<K, V>
         implements MapMakerInternalMap.InternalEntryHelper<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> {
         private static final MapMakerInternalMap.StrongKeyStrongValueEntry.Helper<?, ?> INSTANCE = new MapMakerInternalMap.StrongKeyStrongValueEntry.Helper();

         static <K, V> MapMakerInternalMap.StrongKeyStrongValueEntry.Helper<K, V> instance() {
            return (MapMakerInternalMap.StrongKeyStrongValueEntry.Helper<K, V>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         public MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> newSegment(
            MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.StrongKeyStrongValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> copy(
            MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> var1,
            MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var2,
            @Nullable MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var3
         ) {
            return var2.copy(var3);
         }

         public void setValue(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> var1, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var2, V var3) {
            var2.setValue(var3);
         }

         public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newEntry(
            MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> var1, K var2, int var3, @Nullable MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> var4
         ) {
            return new MapMakerInternalMap.StrongKeyStrongValueEntry<>((K)var2, var3, var4);
         }
      }
   }

   static final class StrongKeyStrongValueSegment<K, V>
      extends MapMakerInternalMap.Segment<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> {
      StrongKeyStrongValueSegment(
         MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> var1,
         int var2,
         int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> self() {
         return this;
      }

      public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return (MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>)var1;
      }
   }

   static final class StrongKeyWeakValueEntry<K, V>
      extends MapMakerInternalMap.AbstractStrongKeyEntry<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>>
      implements MapMakerInternalMap.WeakValueEntry<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> {
      private volatile MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> valueReference = MapMakerInternalMap.unsetWeakValueReference();

      StrongKeyWeakValueEntry(K var1, int var2, @Nullable MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var3) {
         super((K)var1, var2, var3);
      }

      @Override
      public V getValue() {
         return this.valueReference.get();
      }

      @Override
      public void clearValue() {
         this.valueReference.clear();
      }

      void setValue(V var1, ReferenceQueue<V> var2) {
         MapMakerInternalMap.WeakValueReference var3 = this.valueReference;
         this.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(var2, (V)var1, this);
         var3.clear();
      }

      MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> copy(ReferenceQueue<V> var1, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var2) {
         MapMakerInternalMap.StrongKeyWeakValueEntry var3 = new MapMakerInternalMap.StrongKeyWeakValueEntry(this.key, this.hash, var2);
         var3.valueReference = this.valueReference.copyFor(var1, var3);
         return var3;
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> getValueReference() {
         return this.valueReference;
      }

      static final class Helper<K, V>
         implements MapMakerInternalMap.InternalEntryHelper<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> {
         private static final MapMakerInternalMap.StrongKeyWeakValueEntry.Helper<?, ?> INSTANCE = new MapMakerInternalMap.StrongKeyWeakValueEntry.Helper();

         static <K, V> MapMakerInternalMap.StrongKeyWeakValueEntry.Helper<K, V> instance() {
            return (MapMakerInternalMap.StrongKeyWeakValueEntry.Helper<K, V>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.WEAK;
         }

         public MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> newSegment(
            MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.StrongKeyWeakValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> copy(
            MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> var1,
            MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var2,
            @Nullable MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var3
         ) {
            return MapMakerInternalMap.Segment.isCollected(var2) ? null : var2.copy(var1.queueForValues, var3);
         }

         public void setValue(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> var1, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var2, V var3) {
            var2.setValue(var3, var1.queueForValues);
         }

         public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newEntry(
            MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> var1, K var2, int var3, @Nullable MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> var4
         ) {
            return new MapMakerInternalMap.StrongKeyWeakValueEntry<>((K)var2, var3, var4);
         }
      }
   }

   static final class StrongKeyWeakValueSegment<K, V>
      extends MapMakerInternalMap.Segment<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> {
      private final ReferenceQueue<V> queueForValues = new ReferenceQueue<>();

      StrongKeyWeakValueSegment(
         MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> var1,
         int var2,
         int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> self() {
         return this;
      }

      @Override
      ReferenceQueue<V> getValueReferenceQueueForTesting() {
         return this.queueForValues;
      }

      public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return (MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>)var1;
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> getWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1
      ) {
         return this.castForTesting(var1).getValueReference();
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> newWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1, V var2
      ) {
         return new MapMakerInternalMap.WeakValueReferenceImpl<>(this.queueForValues, (V)var2, this.castForTesting(var1));
      }

      @Override
      public void setWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1,
         MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> var2
      ) {
         MapMakerInternalMap.StrongKeyWeakValueEntry var3 = this.castForTesting(var1);
         MapMakerInternalMap.WeakValueReference var5 = var3.valueReference;
         var3.valueReference = var2;
         var5.clear();
      }

      @Override
      void maybeDrainReferenceQueues() {
         this.drainValueReferenceQueue(this.queueForValues);
      }

      @Override
      void maybeClearReferenceQueues() {
         this.clearReferenceQueue(this.queueForValues);
      }
   }

   interface StrongValueEntry<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> extends MapMakerInternalMap.InternalEntry<K, V, E> {
   }

   final class ValueIterator extends MapMakerInternalMap<K, V, E, S>.HashIterator<V> {
      ValueIterator(MapMakerInternalMap var1) {
      }

      @Override
      public V next() {
         return this.nextEntry().getValue();
      }
   }

   final class Values extends AbstractCollection<V> {
      @Override
      public Iterator<V> iterator() {
         return MapMakerInternalMap.this.new ValueIterator(MapMakerInternalMap.this);
      }

      @Override
      public int size() {
         return MapMakerInternalMap.this.size();
      }

      @Override
      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return MapMakerInternalMap.this.containsValue(var1);
      }

      @Override
      public void clear() {
         MapMakerInternalMap.this.clear();
      }

      @Override
      public Object[] toArray() {
         return MapMakerInternalMap.toArrayList(this).toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])MapMakerInternalMap.toArrayList(this).toArray(var1);
      }
   }

   static final class WeakKeyDummyValueEntry<K>
      extends MapMakerInternalMap.AbstractWeakKeyEntry<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>>
      implements MapMakerInternalMap.StrongValueEntry<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>> {
      WeakKeyDummyValueEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyDummyValueEntry<K> var4) {
         super(var1, (K)var2, var3, var4);
      }

      public MapMaker.Dummy getValue() {
         return MapMaker.Dummy.VALUE;
      }

      void setValue(MapMaker.Dummy var1) {
      }

      MapMakerInternalMap.WeakKeyDummyValueEntry<K> copy(ReferenceQueue<K> var1, MapMakerInternalMap.WeakKeyDummyValueEntry<K> var2) {
         return new MapMakerInternalMap.WeakKeyDummyValueEntry<>(var1, this.getKey(), this.hash, var2);
      }

      static final class Helper<K>
         implements MapMakerInternalMap.InternalEntryHelper<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> {
         private static final MapMakerInternalMap.WeakKeyDummyValueEntry.Helper<?> INSTANCE = new MapMakerInternalMap.WeakKeyDummyValueEntry.Helper();

         static <K> MapMakerInternalMap.WeakKeyDummyValueEntry.Helper<K> instance() {
            return (MapMakerInternalMap.WeakKeyDummyValueEntry.Helper<K>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.WEAK;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         public MapMakerInternalMap.WeakKeyDummyValueSegment<K> newSegment(
            MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.WeakKeyDummyValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.WeakKeyDummyValueEntry<K> copy(
            MapMakerInternalMap.WeakKeyDummyValueSegment<K> var1,
            MapMakerInternalMap.WeakKeyDummyValueEntry<K> var2,
            @Nullable MapMakerInternalMap.WeakKeyDummyValueEntry<K> var3
         ) {
            return var2.getKey() == null ? null : var2.copy(var1.queueForKeys, var3);
         }

         public void setValue(MapMakerInternalMap.WeakKeyDummyValueSegment<K> var1, MapMakerInternalMap.WeakKeyDummyValueEntry<K> var2, MapMaker.Dummy var3) {
         }

         public MapMakerInternalMap.WeakKeyDummyValueEntry<K> newEntry(
            MapMakerInternalMap.WeakKeyDummyValueSegment<K> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyDummyValueEntry<K> var4
         ) {
            return new MapMakerInternalMap.WeakKeyDummyValueEntry<>(var1.queueForKeys, (K)var2, var3, var4);
         }
      }
   }

   static final class WeakKeyDummyValueSegment<K>
      extends MapMakerInternalMap.Segment<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> {
      private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();

      WeakKeyDummyValueSegment(
         MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> var1,
         int var2,
         int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.WeakKeyDummyValueSegment<K> self() {
         return this;
      }

      @Override
      ReferenceQueue<K> getKeyReferenceQueueForTesting() {
         return this.queueForKeys;
      }

      public MapMakerInternalMap.WeakKeyDummyValueEntry<K> castForTesting(MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?> var1) {
         return (MapMakerInternalMap.WeakKeyDummyValueEntry<K>)var1;
      }

      @Override
      void maybeDrainReferenceQueues() {
         this.drainKeyReferenceQueue(this.queueForKeys);
      }

      @Override
      void maybeClearReferenceQueues() {
         this.clearReferenceQueue(this.queueForKeys);
      }
   }

   static final class WeakKeyStrongValueEntry<K, V>
      extends MapMakerInternalMap.AbstractWeakKeyEntry<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>>
      implements MapMakerInternalMap.StrongValueEntry<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>> {
      @Nullable
      private volatile V value = (V)null;

      WeakKeyStrongValueEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var4) {
         super(var1, (K)var2, var3, var4);
      }

      @Nullable
      @Override
      public V getValue() {
         return this.value;
      }

      void setValue(V var1) {
         this.value = (V)var1;
      }

      MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> copy(ReferenceQueue<K> var1, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var2) {
         MapMakerInternalMap.WeakKeyStrongValueEntry var3 = new MapMakerInternalMap.WeakKeyStrongValueEntry(var1, this.getKey(), this.hash, var2);
         var3.setValue(this.value);
         return var3;
      }

      static final class Helper<K, V>
         implements MapMakerInternalMap.InternalEntryHelper<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> {
         private static final MapMakerInternalMap.WeakKeyStrongValueEntry.Helper<?, ?> INSTANCE = new MapMakerInternalMap.WeakKeyStrongValueEntry.Helper();

         static <K, V> MapMakerInternalMap.WeakKeyStrongValueEntry.Helper<K, V> instance() {
            return (MapMakerInternalMap.WeakKeyStrongValueEntry.Helper<K, V>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.WEAK;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.STRONG;
         }

         public MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> newSegment(
            MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.WeakKeyStrongValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> copy(
            MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> var1,
            MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var2,
            @Nullable MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var3
         ) {
            return var2.getKey() == null ? null : var2.copy(var1.queueForKeys, var3);
         }

         public void setValue(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> var1, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var2, V var3) {
            var2.setValue(var3);
         }

         public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newEntry(
            MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> var4
         ) {
            return new MapMakerInternalMap.WeakKeyStrongValueEntry<>(var1.queueForKeys, (K)var2, var3, var4);
         }
      }
   }

   static final class WeakKeyStrongValueSegment<K, V>
      extends MapMakerInternalMap.Segment<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> {
      private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();

      WeakKeyStrongValueSegment(
         MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> var1,
         int var2,
         int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> self() {
         return this;
      }

      @Override
      ReferenceQueue<K> getKeyReferenceQueueForTesting() {
         return this.queueForKeys;
      }

      public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return (MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>)var1;
      }

      @Override
      void maybeDrainReferenceQueues() {
         this.drainKeyReferenceQueue(this.queueForKeys);
      }

      @Override
      void maybeClearReferenceQueues() {
         this.clearReferenceQueue(this.queueForKeys);
      }
   }

   static final class WeakKeyWeakValueEntry<K, V>
      extends MapMakerInternalMap.AbstractWeakKeyEntry<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>>
      implements MapMakerInternalMap.WeakValueEntry<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> {
      private volatile MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> valueReference = MapMakerInternalMap.unsetWeakValueReference();

      WeakKeyWeakValueEntry(ReferenceQueue<K> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var4) {
         super(var1, (K)var2, var3, var4);
      }

      @Override
      public V getValue() {
         return this.valueReference.get();
      }

      MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> copy(ReferenceQueue<K> var1, ReferenceQueue<V> var2, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var3) {
         MapMakerInternalMap.WeakKeyWeakValueEntry var4 = new MapMakerInternalMap.WeakKeyWeakValueEntry(var1, this.getKey(), this.hash, var3);
         var4.valueReference = this.valueReference.copyFor(var2, var4);
         return var4;
      }

      @Override
      public void clearValue() {
         this.valueReference.clear();
      }

      void setValue(V var1, ReferenceQueue<V> var2) {
         MapMakerInternalMap.WeakValueReference var3 = this.valueReference;
         this.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(var2, (V)var1, this);
         var3.clear();
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> getValueReference() {
         return this.valueReference;
      }

      static final class Helper<K, V>
         implements MapMakerInternalMap.InternalEntryHelper<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> {
         private static final MapMakerInternalMap.WeakKeyWeakValueEntry.Helper<?, ?> INSTANCE = new MapMakerInternalMap.WeakKeyWeakValueEntry.Helper();

         static <K, V> MapMakerInternalMap.WeakKeyWeakValueEntry.Helper<K, V> instance() {
            return (MapMakerInternalMap.WeakKeyWeakValueEntry.Helper<K, V>)INSTANCE;
         }

         @Override
         public MapMakerInternalMap.Strength keyStrength() {
            return MapMakerInternalMap.Strength.WEAK;
         }

         @Override
         public MapMakerInternalMap.Strength valueStrength() {
            return MapMakerInternalMap.Strength.WEAK;
         }

         public MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> newSegment(
            MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> var1,
            int var2,
            int var3
         ) {
            return new MapMakerInternalMap.WeakKeyWeakValueSegment<>(var1, var2, var3);
         }

         public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> copy(
            MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> var1,
            MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var2,
            @Nullable MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var3
         ) {
            if (var2.getKey() == null) {
               return null;
            } else {
               return MapMakerInternalMap.Segment.isCollected(var2) ? null : var2.copy(var1.queueForKeys, var1.queueForValues, var3);
            }
         }

         public void setValue(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> var1, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var2, V var3) {
            var2.setValue(var3, var1.queueForValues);
         }

         public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newEntry(
            MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> var1, K var2, int var3, @Nullable MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> var4
         ) {
            return new MapMakerInternalMap.WeakKeyWeakValueEntry<>(var1.queueForKeys, (K)var2, var3, var4);
         }
      }
   }

   static final class WeakKeyWeakValueSegment<K, V>
      extends MapMakerInternalMap.Segment<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> {
      private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();
      private final ReferenceQueue<V> queueForValues = new ReferenceQueue<>();

      WeakKeyWeakValueSegment(
         MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> var1, int var2, int var3
      ) {
         super(var1, var2, var3);
      }

      MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> self() {
         return this;
      }

      @Override
      ReferenceQueue<K> getKeyReferenceQueueForTesting() {
         return this.queueForKeys;
      }

      @Override
      ReferenceQueue<V> getValueReferenceQueueForTesting() {
         return this.queueForValues;
      }

      public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> var1) {
         return (MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>)var1;
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> getWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1
      ) {
         return this.castForTesting(var1).getValueReference();
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> newWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1, V var2
      ) {
         return new MapMakerInternalMap.WeakValueReferenceImpl<>(this.queueForValues, (V)var2, this.castForTesting(var1));
      }

      @Override
      public void setWeakValueReferenceForTesting(
         MapMakerInternalMap.InternalEntry<K, V, ?> var1,
         MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> var2
      ) {
         MapMakerInternalMap.WeakKeyWeakValueEntry var3 = this.castForTesting(var1);
         MapMakerInternalMap.WeakValueReference var5 = var3.valueReference;
         var3.valueReference = var2;
         var5.clear();
      }

      @Override
      void maybeDrainReferenceQueues() {
         this.drainKeyReferenceQueue(this.queueForKeys);
         this.drainValueReferenceQueue(this.queueForValues);
      }

      @Override
      void maybeClearReferenceQueues() {
         this.clearReferenceQueue(this.queueForKeys);
      }
   }

   interface WeakValueEntry<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> extends MapMakerInternalMap.InternalEntry<K, V, E> {
      MapMakerInternalMap.WeakValueReference<K, V, E> getValueReference();

      void clearValue();
   }

   interface WeakValueReference<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> {
      @Nullable
      V get();

      E getEntry();

      void clear();

      MapMakerInternalMap.WeakValueReference<K, V, E> copyFor(ReferenceQueue<V> var1, E var2);
   }

   static final class WeakValueReferenceImpl<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>>
      extends WeakReference<V>
      implements MapMakerInternalMap.WeakValueReference<K, V, E> {
      @Weak
      final E entry;

      WeakValueReferenceImpl(ReferenceQueue<V> var1, V var2, E var3) {
         super((V)var2, var1);
         this.entry = (E)var3;
      }

      @Override
      public E getEntry() {
         return this.entry;
      }

      @Override
      public MapMakerInternalMap.WeakValueReference<K, V, E> copyFor(ReferenceQueue<V> var1, E var2) {
         return new MapMakerInternalMap.WeakValueReferenceImpl<>(var1, this.get(), (E)var2);
      }
   }

   final class WriteThroughEntry extends AbstractMapEntry<K, V> {
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
         Object var2 = MapMakerInternalMap.this.put(this.key, (V)var1);
         this.value = (V)var1;
         return (V)var2;
      }
   }
}
