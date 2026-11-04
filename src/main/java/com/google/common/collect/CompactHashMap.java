package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactHashMap<K, V> extends AbstractMap<K, V> implements Serializable {
   private static final Object NOT_FOUND = new Object();
   @VisibleForTesting
   static final double HASH_FLOODING_FPP = 0.001;
   private static final int MAX_HASH_BUCKET_LENGTH = 9;
   private transient Object table;
   @VisibleForTesting
   transient int[] entries;
   @VisibleForTesting
   transient Object[] keys;
   @VisibleForTesting
   transient Object[] values;
   private transient int metadata;
   private transient int size;
   private transient Set<K> keySetView;
   private transient Set<Entry<K, V>> entrySetView;
   private transient Collection<V> valuesView;

   public static <K, V> CompactHashMap<K, V> create() {
      return new CompactHashMap<>();
   }

   public static <K, V> CompactHashMap<K, V> createWithExpectedSize(int var0) {
      return new CompactHashMap<>(var0);
   }

   CompactHashMap() {
      this.init(3);
   }

   CompactHashMap(int var1) {
      this.init(var1);
   }

   void init(int var1) {
      Preconditions.checkArgument(var1 >= 0, "Expected size must be >= 0");
      this.metadata = Ints.constrainToRange(var1, 1, 1073741823);
   }

   @VisibleForTesting
   boolean needsAllocArrays() {
      return this.table == null;
   }

   @CanIgnoreReturnValue
   int allocArrays() {
      Preconditions.checkState(this.needsAllocArrays(), "Arrays already allocated");
      int var1 = this.metadata;
      int var2 = CompactHashing.tableSize(var1);
      this.table = CompactHashing.createTable(var2);
      this.setHashTableMask(var2 - 1);
      this.entries = new int[var1];
      this.keys = new Object[var1];
      this.values = new Object[var1];
      return var1;
   }

   @VisibleForTesting
   Map<K, V> delegateOrNull() {
      return this.table instanceof Map ? (Map)this.table : null;
   }

   Map<K, V> createHashFloodingResistantDelegate(int var1) {
      return new LinkedHashMap<>(var1, 1.0F);
   }

   @VisibleForTesting
   @CanIgnoreReturnValue
   Map<K, V> convertToHashFloodingResistantImplementation() {
      Map var1 = this.createHashFloodingResistantDelegate(this.hashTableMask() + 1);

      for (int var2 = this.firstEntryIndex(); var2 >= 0; var2 = this.getSuccessor(var2)) {
         var1.put(this.key(var2), this.value(var2));
      }

      this.table = var1;
      this.entries = null;
      this.keys = null;
      this.values = null;
      this.incrementModCount();
      return var1;
   }

   private void setHashTableMask(int var1) {
      int var2 = 32 - Integer.numberOfLeadingZeros(var1);
      this.metadata = CompactHashing.maskCombine(this.metadata, var2, 31);
   }

   private int hashTableMask() {
      return (1 << (this.metadata & 31)) - 1;
   }

   void incrementModCount() {
      this.metadata += 32;
   }

   void accessEntry(int var1) {
   }

   @CanIgnoreReturnValue
   @Override
   public V put(@ParametricNullness K var1, @ParametricNullness V var2) {
      if (this.needsAllocArrays()) {
         this.allocArrays();
      }

      Map var3 = this.delegateOrNull();
      if (var3 != null) {
         return (V)var3.put(var1, var2);
      } else {
         int[] var4 = this.requireEntries();
         Object[] var5 = this.requireKeys();
         Object[] var6 = this.requireValues();
         int var7 = this.size;
         int var8 = var7 + 1;
         int var9 = Hashing.smearedHash(var1);
         int var10 = this.hashTableMask();
         int var11 = var9 & var10;
         int var12 = CompactHashing.tableGet(this.requireTable(), var11);
         if (var12 == 0) {
            if (var8 > var10) {
               var10 = this.resizeTable(var10, CompactHashing.newCapacity(var10), var9, var7);
            } else {
               CompactHashing.tableSet(this.requireTable(), var11, var7 + 1);
            }
         } else {
            int var15 = CompactHashing.getHashPrefix(var9, var10);
            int var16 = 0;

            int var13;
            int var14;
            do {
               var13 = var12 - 1;
               var14 = var4[var13];
               if (CompactHashing.getHashPrefix(var14, var10) == var15 && Objects.equal(var1, var5[var13])) {
                  Object var17 = var6[var13];
                  var6[var13] = var2;
                  this.accessEntry(var13);
                  return (V)var17;
               }

               var12 = CompactHashing.getNext(var14, var10);
               var16++;
            } while (var12 != 0);

            if (var16 >= 9) {
               return this.convertToHashFloodingResistantImplementation().put((K)var1, (V)var2);
            }

            if (var8 > var10) {
               var10 = this.resizeTable(var10, CompactHashing.newCapacity(var10), var9, var7);
            } else {
               var4[var13] = CompactHashing.maskCombine(var14, var7 + 1, var10);
            }
         }

         this.resizeMeMaybe(var8);
         this.insertEntry(var7, (K)var1, (V)var2, var9, var10);
         this.size = var8;
         this.incrementModCount();
         return null;
      }
   }

   void insertEntry(int var1, @ParametricNullness K var2, @ParametricNullness V var3, int var4, int var5) {
      this.setEntry(var1, CompactHashing.maskCombine(var4, 0, var5));
      this.setKey(var1, (K)var2);
      this.setValue(var1, (V)var3);
   }

   private void resizeMeMaybe(int var1) {
      int var2 = this.requireEntries().length;
      if (var1 > var2) {
         int var3 = Math.min(1073741823, var2 + Math.max(1, var2 >>> 1) | 1);
         if (var3 != var2) {
            this.resizeEntries(var3);
         }
      }
   }

   void resizeEntries(int var1) {
      this.entries = Arrays.copyOf(this.requireEntries(), var1);
      this.keys = Arrays.copyOf(this.requireKeys(), var1);
      this.values = Arrays.copyOf(this.requireValues(), var1);
   }

   @CanIgnoreReturnValue
   private int resizeTable(int var1, int var2, int var3, int var4) {
      Object var5 = CompactHashing.createTable(var2);
      int var6 = var2 - 1;
      if (var4 != 0) {
         CompactHashing.tableSet(var5, var3 & var6, var4 + 1);
      }

      Object var7 = this.requireTable();
      int[] var8 = this.requireEntries();

      for (int var9 = 0; var9 <= var1; var9++) {
         int var10 = CompactHashing.tableGet(var7, var9);

         while (var10 != 0) {
            int var11 = var10 - 1;
            int var12 = var8[var11];
            int var13 = CompactHashing.getHashPrefix(var12, var1) | var9;
            int var14 = var13 & var6;
            int var15 = CompactHashing.tableGet(var5, var14);
            CompactHashing.tableSet(var5, var14, var10);
            var8[var11] = CompactHashing.maskCombine(var13, var15, var6);
            var10 = CompactHashing.getNext(var12, var1);
         }
      }

      this.table = var5;
      this.setHashTableMask(var6);
      return var6;
   }

   private int indexOf(Object var1) {
      if (this.needsAllocArrays()) {
         return -1;
      } else {
         int var2 = Hashing.smearedHash(var1);
         int var3 = this.hashTableMask();
         int var4 = CompactHashing.tableGet(this.requireTable(), var2 & var3);
         if (var4 == 0) {
            return -1;
         } else {
            int var5 = CompactHashing.getHashPrefix(var2, var3);

            do {
               int var6 = var4 - 1;
               int var7 = this.entry(var6);
               if (CompactHashing.getHashPrefix(var7, var3) == var5 && Objects.equal(var1, this.key(var6))) {
                  return var6;
               }

               var4 = CompactHashing.getNext(var7, var3);
            } while (var4 != 0);

            return -1;
         }
      }
   }

   @Override
   public boolean containsKey(Object var1) {
      Map var2 = this.delegateOrNull();
      return var2 != null ? var2.containsKey(var1) : this.indexOf(var1) != -1;
   }

   @Override
   public V get(Object var1) {
      Map var2 = this.delegateOrNull();
      if (var2 != null) {
         return (V)var2.get(var1);
      } else {
         int var3 = this.indexOf(var1);
         if (var3 == -1) {
            return null;
         } else {
            this.accessEntry(var3);
            return this.value(var3);
         }
      }
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1) {
      Map var2 = this.delegateOrNull();
      if (var2 != null) {
         return (V)var2.remove(var1);
      } else {
         Object var3 = this.removeHelper(var1);
         return (V)(var3 == NOT_FOUND ? null : var3);
      }
   }

   @Nullable
   private Object removeHelper(Object var1) {
      if (this.needsAllocArrays()) {
         return NOT_FOUND;
      } else {
         int var2 = this.hashTableMask();
         int var3 = CompactHashing.remove(var1, null, var2, this.requireTable(), this.requireEntries(), this.requireKeys(), null);
         if (var3 == -1) {
            return NOT_FOUND;
         } else {
            Object var4 = this.value(var3);
            this.moveLastEntry(var3, var2);
            this.size--;
            this.incrementModCount();
            return var4;
         }
      }
   }

   void moveLastEntry(int var1, int var2) {
      Object var3 = this.requireTable();
      int[] var4 = this.requireEntries();
      Object[] var5 = this.requireKeys();
      Object[] var6 = this.requireValues();
      int var7 = this.size() - 1;
      if (var1 < var7) {
         Object var8 = var5[var7];
         var5[var1] = var8;
         var6[var1] = var6[var7];
         var5[var7] = null;
         var6[var7] = null;
         var4[var1] = var4[var7];
         var4[var7] = 0;
         int var9 = Hashing.smearedHash(var8) & var2;
         int var10 = CompactHashing.tableGet(var3, var9);
         int var11 = var7 + 1;
         if (var10 == var11) {
            CompactHashing.tableSet(var3, var9, var1 + 1);
         } else {
            int var12;
            int var13;
            do {
               var12 = var10 - 1;
               var13 = var4[var12];
               var10 = CompactHashing.getNext(var13, var2);
            } while (var10 != var11);

            var4[var12] = CompactHashing.maskCombine(var13, var1 + 1, var2);
         }
      } else {
         var5[var1] = null;
         var6[var1] = null;
         var4[var1] = 0;
      }
   }

   int firstEntryIndex() {
      return this.isEmpty() ? -1 : 0;
   }

   int getSuccessor(int var1) {
      return var1 + 1 < this.size ? var1 + 1 : -1;
   }

   int adjustAfterRemove(int var1, int var2) {
      return var1 - 1;
   }

   @Override
   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Preconditions.checkNotNull(var1);
      Map var2 = this.delegateOrNull();
      if (var2 != null) {
         var2.replaceAll(var1);
      } else {
         for (int var3 = 0; var3 < this.size; var3++) {
            this.setValue(var3, (V)var1.apply(this.key(var3), this.value(var3)));
         }
      }
   }

   @Override
   public Set<K> keySet() {
      return this.keySetView == null ? (this.keySetView = this.createKeySet()) : this.keySetView;
   }

   Set<K> createKeySet() {
      return new CompactHashMap.KeySetView();
   }

   Iterator<K> keySetIterator() {
      Map var1 = this.delegateOrNull();
      return (Iterator<K>)(var1 != null ? var1.keySet().iterator() : new CompactHashMap<K, V>.Itr<K>() {
         @ParametricNullness
         @Override
         K getOutput(int var1) {
            return (K)CompactHashMap.this.key(var1);
         }
      });
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      Map var2 = this.delegateOrNull();
      if (var2 != null) {
         var2.forEach(var1);
      } else {
         for (int var3 = this.firstEntryIndex(); var3 >= 0; var3 = this.getSuccessor(var3)) {
            var1.accept(this.key(var3), this.value(var3));
         }
      }
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.entrySetView == null ? (this.entrySetView = this.createEntrySet()) : this.entrySetView;
   }

   Set<Entry<K, V>> createEntrySet() {
      return new CompactHashMap.EntrySetView();
   }

   Iterator<Entry<K, V>> entrySetIterator() {
      Map var1 = this.delegateOrNull();
      return (Iterator<Entry<K, V>>)(var1 != null ? var1.entrySet().iterator() : new CompactHashMap<K, V>.Itr<Entry<K, V>>() {
         Entry<K, V> getOutput(int var1) {
            return CompactHashMap.this.new MapEntry(var1);
         }
      });
   }

   @Override
   public int size() {
      Map var1 = this.delegateOrNull();
      return var1 != null ? var1.size() : this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsValue(Object var1) {
      Map var2 = this.delegateOrNull();
      if (var2 != null) {
         return var2.containsValue(var1);
      } else {
         for (int var3 = 0; var3 < this.size; var3++) {
            if (Objects.equal(var1, this.value(var3))) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public Collection<V> values() {
      return this.valuesView == null ? (this.valuesView = this.createValues()) : this.valuesView;
   }

   Collection<V> createValues() {
      return new CompactHashMap.ValuesView();
   }

   Iterator<V> valuesIterator() {
      Map var1 = this.delegateOrNull();
      return (Iterator<V>)(var1 != null ? var1.values().iterator() : new CompactHashMap<K, V>.Itr<V>() {
         @ParametricNullness
         @Override
         V getOutput(int var1) {
            return (V)CompactHashMap.this.value(var1);
         }
      });
   }

   public void trimToSize() {
      if (!this.needsAllocArrays()) {
         Map var1 = this.delegateOrNull();
         if (var1 != null) {
            Map var5 = this.createHashFloodingResistantDelegate(this.size());
            var5.putAll(var1);
            this.table = var5;
         } else {
            int var2 = this.size;
            if (var2 < this.requireEntries().length) {
               this.resizeEntries(var2);
            }

            int var3 = CompactHashing.tableSize(var2);
            int var4 = this.hashTableMask();
            if (var3 < var4) {
               this.resizeTable(var4, var3, 0, 0);
            }
         }
      }
   }

   @Override
   public void clear() {
      if (!this.needsAllocArrays()) {
         this.incrementModCount();
         Map var1 = this.delegateOrNull();
         if (var1 != null) {
            this.metadata = Ints.constrainToRange(this.size(), 3, 1073741823);
            var1.clear();
            this.table = null;
            this.size = 0;
         } else {
            Arrays.fill(this.requireKeys(), 0, this.size, null);
            Arrays.fill(this.requireValues(), 0, this.size, null);
            CompactHashing.tableClear(this.requireTable());
            Arrays.fill(this.requireEntries(), 0, this.size, 0);
            this.size = 0;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size());
      Iterator var2 = this.entrySetIterator();

      while (var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new InvalidObjectException(new StringBuilder(25).append("Invalid size: ").append(var2).toString());
      } else {
         this.init(var2);

         for (int var3 = 0; var3 < var2; var3++) {
            Object var4 = var1.readObject();
            Object var5 = var1.readObject();
            this.put((K)var4, (V)var5);
         }
      }
   }

   private Object requireTable() {
      return java.util.Objects.requireNonNull(this.table);
   }

   private int[] requireEntries() {
      return java.util.Objects.requireNonNull(this.entries);
   }

   private Object[] requireKeys() {
      return java.util.Objects.requireNonNull(this.keys);
   }

   private Object[] requireValues() {
      return java.util.Objects.requireNonNull(this.values);
   }

   private K key(int var1) {
      return (K)this.requireKeys()[var1];
   }

   private V value(int var1) {
      return (V)this.requireValues()[var1];
   }

   private int entry(int var1) {
      return this.requireEntries()[var1];
   }

   private void setKey(int var1, K var2) {
      this.requireKeys()[var1] = var2;
   }

   private void setValue(int var1, V var2) {
      this.requireValues()[var1] = var2;
   }

   private void setEntry(int var1, int var2) {
      this.requireEntries()[var1] = var2;
   }

   class EntrySetView extends Maps.EntrySet<K, V> {
      @Override
      Map<K, V> map() {
         return CompactHashMap.this;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return CompactHashMap.this.entrySetIterator();
      }

      @Override
      public Spliterator<Entry<K, V>> spliterator() {
         Map var1 = CompactHashMap.this.delegateOrNull();
         return var1 != null
            ? var1.entrySet().spliterator()
            : CollectSpliterators.indexed(CompactHashMap.this.size, 17, var1x -> CompactHashMap.this.new MapEntry(var1x));
      }

      @Override
      public boolean contains(Object var1) {
         Map var2 = CompactHashMap.this.delegateOrNull();
         if (var2 != null) {
            return var2.entrySet().contains(var1);
         } else if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var3 = (Entry)var1;
            int var4 = CompactHashMap.this.indexOf(var3.getKey());
            return var4 != -1 && Objects.equal(CompactHashMap.this.value(var4), var3.getValue());
         }
      }

      @Override
      public boolean remove(Object var1) {
         Map var2 = CompactHashMap.this.delegateOrNull();
         if (var2 != null) {
            return var2.entrySet().remove(var1);
         } else if (var1 instanceof Entry) {
            Entry var3 = (Entry)var1;
            if (CompactHashMap.this.needsAllocArrays()) {
               return false;
            } else {
               int var4 = CompactHashMap.this.hashTableMask();
               int var5 = CompactHashing.remove(
                  var3.getKey(),
                  var3.getValue(),
                  var4,
                  CompactHashMap.this.requireTable(),
                  CompactHashMap.this.requireEntries(),
                  CompactHashMap.this.requireKeys(),
                  CompactHashMap.this.requireValues()
               );
               if (var5 == -1) {
                  return false;
               } else {
                  CompactHashMap.this.moveLastEntry(var5, var4);
                  CompactHashMap.this.size--;
                  CompactHashMap.this.incrementModCount();
                  return true;
               }
            }
         } else {
            return false;
         }
      }
   }

   private abstract class Itr<T> implements Iterator<T> {
      int expectedMetadata = CompactHashMap.this.metadata;
      int currentIndex = CompactHashMap.this.firstEntryIndex();
      int indexToRemove = -1;

      private Itr() {
      }

      @Override
      public boolean hasNext() {
         return this.currentIndex >= 0;
      }

      @ParametricNullness
      abstract T getOutput(int var1);

      @ParametricNullness
      @Override
      public T next() {
         this.checkForConcurrentModification();
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.indexToRemove = this.currentIndex;
            Object var1 = this.getOutput(this.currentIndex);
            this.currentIndex = CompactHashMap.this.getSuccessor(this.currentIndex);
            return (T)var1;
         }
      }

      @Override
      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.indexToRemove >= 0);
         this.incrementExpectedModCount();
         CompactHashMap.this.remove(CompactHashMap.this.key(this.indexToRemove));
         this.currentIndex = CompactHashMap.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
         this.indexToRemove = -1;
      }

      void incrementExpectedModCount() {
         this.expectedMetadata += 32;
      }

      private void checkForConcurrentModification() {
         if (CompactHashMap.this.metadata != this.expectedMetadata) {
            throw new ConcurrentModificationException();
         }
      }
   }

   class KeySetView extends Maps.KeySet<K, V> {
      KeySetView() {
         super(CompactHashMap.this);
      }

      @Override
      public Object[] toArray() {
         if (CompactHashMap.this.needsAllocArrays()) {
            return new Object[0];
         } else {
            Map var1 = CompactHashMap.this.delegateOrNull();
            return var1 != null ? var1.keySet().toArray() : ObjectArrays.copyAsObjectArray(CompactHashMap.this.requireKeys(), 0, CompactHashMap.this.size);
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         if (CompactHashMap.this.needsAllocArrays()) {
            if (var1.length > 0) {
               var1[0] = null;
            }

            return (T[])var1;
         } else {
            Map var2 = CompactHashMap.this.delegateOrNull();
            return (T[])(var2 != null
               ? var2.keySet().toArray(var1)
               : ObjectArrays.toArrayImpl(CompactHashMap.this.requireKeys(), 0, CompactHashMap.this.size, var1));
         }
      }

      @Override
      public boolean remove(Object var1) {
         Map var2 = CompactHashMap.this.delegateOrNull();
         return var2 != null ? var2.keySet().remove(var1) : CompactHashMap.this.removeHelper(var1) != CompactHashMap.NOT_FOUND;
      }

      @Override
      public Iterator<K> iterator() {
         return CompactHashMap.this.keySetIterator();
      }

      @Override
      public Spliterator<K> spliterator() {
         if (CompactHashMap.this.needsAllocArrays()) {
            return Spliterators.spliterator(new Object[0], 17);
         } else {
            Map var1 = CompactHashMap.this.delegateOrNull();
            return var1 != null ? var1.keySet().spliterator() : Spliterators.spliterator(CompactHashMap.this.requireKeys(), 0, CompactHashMap.this.size, 17);
         }
      }

      @Override
      public void forEach(Consumer<? super K> var1) {
         Preconditions.checkNotNull(var1);
         Map var2 = CompactHashMap.this.delegateOrNull();
         if (var2 != null) {
            var2.keySet().forEach(var1);
         } else {
            for (int var3 = CompactHashMap.this.firstEntryIndex(); var3 >= 0; var3 = CompactHashMap.this.getSuccessor(var3)) {
               var1.accept(CompactHashMap.this.key(var3));
            }
         }
      }
   }

   final class MapEntry extends AbstractMapEntry<K, V> {
      @ParametricNullness
      private final K key;
      private int lastKnownIndex;

      MapEntry(int var2) {
         this.key = CompactHashMap.this.key(var2);
         this.lastKnownIndex = var2;
      }

      @ParametricNullness
      @Override
      public K getKey() {
         return this.key;
      }

      private void updateLastKnownIndex() {
         if (this.lastKnownIndex == -1
            || this.lastKnownIndex >= CompactHashMap.this.size()
            || !Objects.equal(this.key, CompactHashMap.this.key(this.lastKnownIndex))) {
            this.lastKnownIndex = CompactHashMap.this.indexOf(this.key);
         }
      }

      @ParametricNullness
      @Override
      public V getValue() {
         Map var1 = CompactHashMap.this.delegateOrNull();
         if (var1 != null) {
            return NullnessCasts.uncheckedCastNullableTToT((V)var1.get(this.key));
         } else {
            this.updateLastKnownIndex();
            return this.lastKnownIndex == -1 ? NullnessCasts.unsafeNull() : CompactHashMap.this.value(this.lastKnownIndex);
         }
      }

      @ParametricNullness
      @Override
      public V setValue(@ParametricNullness V var1) {
         Map var2 = CompactHashMap.this.delegateOrNull();
         if (var2 != null) {
            return NullnessCasts.uncheckedCastNullableTToT((V)var2.put(this.key, var1));
         } else {
            this.updateLastKnownIndex();
            if (this.lastKnownIndex == -1) {
               CompactHashMap.this.put(this.key, (V)var1);
               return NullnessCasts.unsafeNull();
            } else {
               Object var3 = CompactHashMap.this.value(this.lastKnownIndex);
               CompactHashMap.this.setValue(this.lastKnownIndex, (V)var1);
               return (V)var3;
            }
         }
      }
   }

   class ValuesView extends Maps.Values<K, V> {
      ValuesView() {
         super(CompactHashMap.this);
      }

      @Override
      public Iterator<V> iterator() {
         return CompactHashMap.this.valuesIterator();
      }

      @Override
      public void forEach(Consumer<? super V> var1) {
         Preconditions.checkNotNull(var1);
         Map var2 = CompactHashMap.this.delegateOrNull();
         if (var2 != null) {
            var2.values().forEach(var1);
         } else {
            for (int var3 = CompactHashMap.this.firstEntryIndex(); var3 >= 0; var3 = CompactHashMap.this.getSuccessor(var3)) {
               var1.accept(CompactHashMap.this.value(var3));
            }
         }
      }

      @Override
      public Spliterator<V> spliterator() {
         if (CompactHashMap.this.needsAllocArrays()) {
            return Spliterators.spliterator(new Object[0], 16);
         } else {
            Map var1 = CompactHashMap.this.delegateOrNull();
            return var1 != null ? var1.values().spliterator() : Spliterators.spliterator(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size, 16);
         }
      }

      @Override
      public Object[] toArray() {
         if (CompactHashMap.this.needsAllocArrays()) {
            return new Object[0];
         } else {
            Map var1 = CompactHashMap.this.delegateOrNull();
            return var1 != null ? var1.values().toArray() : ObjectArrays.copyAsObjectArray(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size);
         }
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         if (CompactHashMap.this.needsAllocArrays()) {
            if (var1.length > 0) {
               var1[0] = null;
            }

            return (T[])var1;
         } else {
            Map var2 = CompactHashMap.this.delegateOrNull();
            return (T[])(var2 != null
               ? var2.values().toArray(var1)
               : ObjectArrays.toArrayImpl(CompactHashMap.this.requireValues(), 0, CompactHashMap.this.size, var1));
         }
      }
   }
}
