package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class LinkedHashMultimap<K, V> extends LinkedHashMultimapGwtSerializationDependencies<K, V> {
   private static final int DEFAULT_KEY_CAPACITY = 16;
   private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
   @VisibleForTesting
   static final double VALUE_SET_LOAD_FACTOR = 1.0;
   @VisibleForTesting
   transient int valueSetCapacity = 2;
   private transient LinkedHashMultimap.ValueEntry<K, V> multimapHeaderEntry;
   @GwtIncompatible
   private static final long serialVersionUID = 1L;

   public static <K, V> LinkedHashMultimap<K, V> create() {
      return new LinkedHashMultimap<>(16, 2);
   }

   public static <K, V> LinkedHashMultimap<K, V> create(int var0, int var1) {
      return new LinkedHashMultimap<>(Maps.capacity(var0), Maps.capacity(var1));
   }

   public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      LinkedHashMultimap var1 = create(var0.keySet().size(), 2);
      var1.putAll(var0);
      return var1;
   }

   private static <K, V> void succeedsInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var0, LinkedHashMultimap.ValueSetLink<K, V> var1) {
      var0.setSuccessorInValueSet(var1);
      var1.setPredecessorInValueSet(var0);
   }

   private static <K, V> void succeedsInMultimap(LinkedHashMultimap.ValueEntry<K, V> var0, LinkedHashMultimap.ValueEntry<K, V> var1) {
      var0.setSuccessorInMultimap(var1);
      var1.setPredecessorInMultimap(var0);
   }

   private static <K, V> void deleteFromValueSet(LinkedHashMultimap.ValueSetLink<K, V> var0) {
      succeedsInValueSet(var0.getPredecessorInValueSet(), var0.getSuccessorInValueSet());
   }

   private static <K, V> void deleteFromMultimap(LinkedHashMultimap.ValueEntry<K, V> var0) {
      succeedsInMultimap(var0.getPredecessorInMultimap(), var0.getSuccessorInMultimap());
   }

   private LinkedHashMultimap(int var1, int var2) {
      super(Platform.newLinkedHashMapWithExpectedSize(var1));
      CollectPreconditions.checkNonnegative(var2, "expectedValuesPerKey");
      this.valueSetCapacity = var2;
      this.multimapHeaderEntry = LinkedHashMultimap.ValueEntry.newHeader();
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   @Override
   Set<V> createCollection() {
      return Platform.newLinkedHashSetWithExpectedSize(this.valueSetCapacity);
   }

   @Override
   Collection<V> createCollection(@ParametricNullness K var1) {
      return new LinkedHashMultimap.ValueSet(var1, this.valueSetCapacity);
   }

   @CanIgnoreReturnValue
   @Override
   public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return super.replaceValues((K)var1, var2);
   }

   @Override
   public Set<Entry<K, V>> entries() {
      return super.entries();
   }

   @Override
   public Set<K> keySet() {
      return super.keySet();
   }

   @Override
   public Collection<V> values() {
      return super.values();
   }

   @Override
   Iterator<Entry<K, V>> entryIterator() {
      return new Iterator<Entry<K, V>>() {
         LinkedHashMultimap.ValueEntry<K, V> nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.getSuccessorInMultimap();
         LinkedHashMultimap.ValueEntry<K, V> toRemove;

         @Override
         public boolean hasNext() {
            return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
         }

         public Entry<K, V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               LinkedHashMultimap.ValueEntry var1 = this.nextEntry;
               this.toRemove = var1;
               this.nextEntry = this.nextEntry.getSuccessorInMultimap();
               return var1;
            }
         }

         @Override
         public void remove() {
            Preconditions.checkState(this.toRemove != null, "no calls to next() since the last call to remove()");
            LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
            this.toRemove = null;
         }
      };
   }

   @Override
   Spliterator<Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(this.entries(), 17);
   }

   @Override
   Iterator<V> valueIterator() {
      return Maps.valueIterator(this.entryIterator());
   }

   @Override
   Spliterator<V> valueSpliterator() {
      return CollectSpliterators.map(this.entrySpliterator(), Entry::getValue);
   }

   @Override
   public void clear() {
      super.clear();
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.keySet().size());

      for (Object var3 : this.keySet()) {
         var1.writeObject(var3);
      }

      var1.writeInt(this.size());

      for (Entry var5 : this.entries()) {
         var1.writeObject(var5.getKey());
         var1.writeObject(var5.getValue());
      }
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.multimapHeaderEntry = LinkedHashMultimap.ValueEntry.newHeader();
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
      this.valueSetCapacity = 2;
      int var2 = var1.readInt();
      Map var3 = Platform.newLinkedHashMapWithExpectedSize(12);

      for (int var4 = 0; var4 < var2; var4++) {
         Object var5 = var1.readObject();
         var3.put(var5, this.createCollection((K)var5));
      }

      int var8 = var1.readInt();

      for (int var9 = 0; var9 < var8; var9++) {
         Object var6 = var1.readObject();
         Object var7 = var1.readObject();
         ((Collection)Objects.requireNonNull((Collection)var3.get(var6))).add(var7);
      }

      this.setMap(var3);
   }

   @VisibleForTesting
   static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements LinkedHashMultimap.ValueSetLink<K, V> {
      final int smearedValueHash;
      LinkedHashMultimap.ValueEntry<K, V> nextInValueBucket;
      LinkedHashMultimap.ValueSetLink<K, V> predecessorInValueSet;
      LinkedHashMultimap.ValueSetLink<K, V> successorInValueSet;
      LinkedHashMultimap.ValueEntry<K, V> predecessorInMultimap;
      LinkedHashMultimap.ValueEntry<K, V> successorInMultimap;

      ValueEntry(@ParametricNullness K var1, @ParametricNullness V var2, int var3, LinkedHashMultimap.ValueEntry<K, V> var4) {
         super((K)var1, (V)var2);
         this.smearedValueHash = var3;
         this.nextInValueBucket = var4;
      }

      static <K, V> LinkedHashMultimap.ValueEntry<K, V> newHeader() {
         return new LinkedHashMultimap.ValueEntry<>(null, null, 0, null);
      }

      boolean matchesValue(Object var1, int var2) {
         return this.smearedValueHash == var2 && com.google.common.base.Objects.equal(this.getValue(), var1);
      }

      @Override
      public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
         return Objects.requireNonNull(this.predecessorInValueSet);
      }

      @Override
      public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
         return Objects.requireNonNull(this.successorInValueSet);
      }

      @Override
      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.predecessorInValueSet = var1;
      }

      @Override
      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.successorInValueSet = var1;
      }

      public LinkedHashMultimap.ValueEntry<K, V> getPredecessorInMultimap() {
         return Objects.requireNonNull(this.predecessorInMultimap);
      }

      public LinkedHashMultimap.ValueEntry<K, V> getSuccessorInMultimap() {
         return Objects.requireNonNull(this.successorInMultimap);
      }

      public void setSuccessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> var1) {
         this.successorInMultimap = var1;
      }

      public void setPredecessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> var1) {
         this.predecessorInMultimap = var1;
      }
   }

   @VisibleForTesting
   final class ValueSet extends Sets.ImprovedAbstractSet<V> implements LinkedHashMultimap.ValueSetLink<K, V> {
      @ParametricNullness
      private final K key;
      @VisibleForTesting
      LinkedHashMultimap.ValueEntry<K, V>[] hashTable;
      private int size = 0;
      private int modCount = 0;
      private LinkedHashMultimap.ValueSetLink<K, V> firstEntry;
      private LinkedHashMultimap.ValueSetLink<K, V> lastEntry;

      ValueSet(@ParametricNullness K var2, int var3) {
         this.key = (K)var2;
         this.firstEntry = this;
         this.lastEntry = this;
         int var4 = Hashing.closedTableSize(var3, 1.0);
         LinkedHashMultimap.ValueEntry[] var5 = new LinkedHashMultimap.ValueEntry[var4];
         this.hashTable = var5;
      }

      private int mask() {
         return this.hashTable.length - 1;
      }

      @Override
      public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
         return this.lastEntry;
      }

      @Override
      public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
         return this.firstEntry;
      }

      @Override
      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.lastEntry = var1;
      }

      @Override
      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.firstEntry = var1;
      }

      @Override
      public Iterator<V> iterator() {
         return new Iterator<V>() {
            LinkedHashMultimap.ValueSetLink<K, V> nextEntry = ValueSet.this.firstEntry;
            LinkedHashMultimap.ValueEntry<K, V> toRemove;
            int expectedModCount = ValueSet.this.modCount;

            private void checkForComodification() {
               if (ValueSet.this.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

            @Override
            public boolean hasNext() {
               this.checkForComodification();
               return this.nextEntry != ValueSet.this;
            }

            @ParametricNullness
            @Override
            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  LinkedHashMultimap.ValueEntry var1 = (LinkedHashMultimap.ValueEntry)this.nextEntry;
                  Object var2 = var1.getValue();
                  this.toRemove = var1;
                  this.nextEntry = var1.getSuccessorInValueSet();
                  return (V)var2;
               }
            }

            @Override
            public void remove() {
               this.checkForComodification();
               Preconditions.checkState(this.toRemove != null, "no calls to next() since the last call to remove()");
               ValueSet.this.remove(this.toRemove.getValue());
               this.expectedModCount = ValueSet.this.modCount;
               this.toRemove = null;
            }
         };
      }

      @Override
      public void forEach(Consumer<? super V> var1) {
         Preconditions.checkNotNull(var1);

         for (LinkedHashMultimap.ValueSetLink var2 = this.firstEntry; var2 != this; var2 = var2.getSuccessorInValueSet()) {
            var1.accept(((LinkedHashMultimap.ValueEntry)var2).getValue());
         }
      }

      @Override
      public int size() {
         return this.size;
      }

      @Override
      public boolean contains(Object var1) {
         int var2 = Hashing.smearedHash(var1);

         for (LinkedHashMultimap.ValueEntry var3 = this.hashTable[var2 & this.mask()]; var3 != null; var3 = var3.nextInValueBucket) {
            if (var3.matchesValue(var1, var2)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean add(@ParametricNullness V var1) {
         int var2 = Hashing.smearedHash(var1);
         int var3 = var2 & this.mask();
         LinkedHashMultimap.ValueEntry var4 = this.hashTable[var3];

         for (LinkedHashMultimap.ValueEntry var5 = var4; var5 != null; var5 = var5.nextInValueBucket) {
            if (var5.matchesValue(var1, var2)) {
               return false;
            }
         }

         LinkedHashMultimap.ValueEntry var6 = new LinkedHashMultimap.ValueEntry<>(this.key, var1, var2, var4);
         LinkedHashMultimap.succeedsInValueSet(this.lastEntry, var6);
         LinkedHashMultimap.succeedsInValueSet(var6, this);
         LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), var6);
         LinkedHashMultimap.succeedsInMultimap(var6, LinkedHashMultimap.this.multimapHeaderEntry);
         this.hashTable[var3] = var6;
         this.size++;
         this.modCount++;
         this.rehashIfNecessary();
         return true;
      }

      private void rehashIfNecessary() {
         if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0)) {
            LinkedHashMultimap.ValueEntry[] var1 = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];
            this.hashTable = var1;
            int var2 = var1.length - 1;

            for (LinkedHashMultimap.ValueSetLink var3 = this.firstEntry; var3 != this; var3 = var3.getSuccessorInValueSet()) {
               LinkedHashMultimap.ValueEntry var4 = (LinkedHashMultimap.ValueEntry)var3;
               int var5 = var4.smearedValueHash & var2;
               var4.nextInValueBucket = var1[var5];
               var1[var5] = var4;
            }
         }
      }

      @CanIgnoreReturnValue
      @Override
      public boolean remove(Object var1) {
         int var2 = Hashing.smearedHash(var1);
         int var3 = var2 & this.mask();
         LinkedHashMultimap.ValueEntry var4 = null;

         for (LinkedHashMultimap.ValueEntry var5 = this.hashTable[var3]; var5 != null; var5 = var5.nextInValueBucket) {
            if (var5.matchesValue(var1, var2)) {
               if (var4 == null) {
                  this.hashTable[var3] = var5.nextInValueBucket;
               } else {
                  var4.nextInValueBucket = var5.nextInValueBucket;
               }

               LinkedHashMultimap.deleteFromValueSet(var5);
               LinkedHashMultimap.deleteFromMultimap(var5);
               this.size--;
               this.modCount++;
               return true;
            }

            var4 = var5;
         }

         return false;
      }

      @Override
      public void clear() {
         Arrays.fill(this.hashTable, null);
         this.size = 0;

         for (LinkedHashMultimap.ValueSetLink var1 = this.firstEntry; var1 != this; var1 = var1.getSuccessorInValueSet()) {
            LinkedHashMultimap.ValueEntry var2 = (LinkedHashMultimap.ValueEntry)var1;
            LinkedHashMultimap.deleteFromMultimap(var2);
         }

         LinkedHashMultimap.succeedsInValueSet(this, this);
         this.modCount++;
      }
   }

   private interface ValueSetLink<K, V> {
      LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet();

      LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet();

      void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1);

      void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1);
   }
}
