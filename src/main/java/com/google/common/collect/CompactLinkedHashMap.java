package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactLinkedHashMap<K, V> extends CompactHashMap<K, V> {
   private static final int ENDPOINT = -2;
   @VisibleForTesting
   transient long[] links;
   private transient int firstEntry;
   private transient int lastEntry;
   private final boolean accessOrder;

   public static <K, V> CompactLinkedHashMap<K, V> create() {
      return new CompactLinkedHashMap<>();
   }

   public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(int var0) {
      return new CompactLinkedHashMap<>(var0);
   }

   CompactLinkedHashMap() {
      this(3);
   }

   CompactLinkedHashMap(int var1) {
      this(var1, false);
   }

   CompactLinkedHashMap(int var1, boolean var2) {
      super(var1);
      this.accessOrder = var2;
   }

   @Override
   void init(int var1) {
      super.init(var1);
      this.firstEntry = -2;
      this.lastEntry = -2;
   }

   @Override
   int allocArrays() {
      int var1 = super.allocArrays();
      this.links = new long[var1];
      return var1;
   }

   @Override
   Map<K, V> createHashFloodingResistantDelegate(int var1) {
      return new LinkedHashMap<>(var1, 1.0F, this.accessOrder);
   }

   @CanIgnoreReturnValue
   @Override
   Map<K, V> convertToHashFloodingResistantImplementation() {
      Map var1 = super.convertToHashFloodingResistantImplementation();
      this.links = null;
      return var1;
   }

   private int getPredecessor(int var1) {
      return (int)(this.link(var1) >>> 32) - 1;
   }

   @Override
   int getSuccessor(int var1) {
      return (int)this.link(var1) - 1;
   }

   private void setSuccessor(int var1, int var2) {
      long var3 = 4294967295L;
      this.setLink(var1, this.link(var1) & ~var3 | (long)(var2 + 1) & var3);
   }

   private void setPredecessor(int var1, int var2) {
      long var3 = -4294967296L;
      this.setLink(var1, this.link(var1) & ~var3 | (long)(var2 + 1) << 32);
   }

   private void setSucceeds(int var1, int var2) {
      if (var1 == -2) {
         this.firstEntry = var2;
      } else {
         this.setSuccessor(var1, var2);
      }

      if (var2 == -2) {
         this.lastEntry = var1;
      } else {
         this.setPredecessor(var2, var1);
      }
   }

   @Override
   void insertEntry(int var1, @ParametricNullness K var2, @ParametricNullness V var3, int var4, int var5) {
      super.insertEntry(var1, (K)var2, (V)var3, var4, var5);
      this.setSucceeds(this.lastEntry, var1);
      this.setSucceeds(var1, -2);
   }

   @Override
   void accessEntry(int var1) {
      if (this.accessOrder) {
         this.setSucceeds(this.getPredecessor(var1), this.getSuccessor(var1));
         this.setSucceeds(this.lastEntry, var1);
         this.setSucceeds(var1, -2);
         this.incrementModCount();
      }
   }

   @Override
   void moveLastEntry(int var1, int var2) {
      int var3 = this.size() - 1;
      super.moveLastEntry(var1, var2);
      this.setSucceeds(this.getPredecessor(var1), this.getSuccessor(var1));
      if (var1 < var3) {
         this.setSucceeds(this.getPredecessor(var3), var1);
         this.setSucceeds(var1, this.getSuccessor(var3));
      }

      this.setLink(var3, 0L);
   }

   @Override
   void resizeEntries(int var1) {
      super.resizeEntries(var1);
      this.links = Arrays.copyOf(this.requireLinks(), var1);
   }

   @Override
   int firstEntryIndex() {
      return this.firstEntry;
   }

   @Override
   int adjustAfterRemove(int var1, int var2) {
      return var1 >= this.size() ? var2 : var1;
   }

   @Override
   Set<Entry<K, V>> createEntrySet() {
      class 1EntrySetImpl extends CompactHashMap<K, V>.EntrySetView {
         _EntrySetImpl(CompactLinkedHashMap var1) {
         }

         @Override
         public Spliterator<Entry<K, V>> spliterator() {
            return Spliterators.spliterator(this, 17);
         }
      }

      return new 1EntrySetImpl(this);
   }

   @Override
   Set<K> createKeySet() {
      class 1KeySetImpl extends CompactHashMap<K, V>.KeySetView {
         _KeySetImpl(CompactLinkedHashMap var1) {
         }

         @Override
         public Object[] toArray() {
            return ObjectArrays.toArrayImpl(this);
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])ObjectArrays.toArrayImpl(this, var1);
         }

         @Override
         public Spliterator<K> spliterator() {
            return Spliterators.spliterator(this, 17);
         }
      }

      return new 1KeySetImpl(this);
   }

   @Override
   Collection<V> createValues() {
      class 1ValuesImpl extends CompactHashMap<K, V>.ValuesView {
         _ValuesImpl(CompactLinkedHashMap var1) {
         }

         @Override
         public Object[] toArray() {
            return ObjectArrays.toArrayImpl(this);
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])ObjectArrays.toArrayImpl(this, var1);
         }

         @Override
         public Spliterator<V> spliterator() {
            return Spliterators.spliterator(this, 16);
         }
      }

      return new 1ValuesImpl(this);
   }

   @Override
   public void clear() {
      if (!this.needsAllocArrays()) {
         this.firstEntry = -2;
         this.lastEntry = -2;
         if (this.links != null) {
            Arrays.fill(this.links, 0, this.size(), 0L);
         }

         super.clear();
      }
   }

   private long[] requireLinks() {
      return Objects.requireNonNull(this.links);
   }

   private long link(int var1) {
      return this.requireLinks()[var1];
   }

   private void setLink(int var1, long var2) {
      this.requireLinks()[var1] = var2;
   }
}
