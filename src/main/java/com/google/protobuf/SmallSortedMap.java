package com.google.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class SmallSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> {
   private final int maxArraySize;
   private List<SmallSortedMap<K, V>.Entry> entryList;
   private Map<K, V> overflowEntries;
   private boolean isImmutable;
   private volatile SmallSortedMap<K, V>.EntrySet lazyEntrySet;
   private Map<K, V> overflowEntriesDescending;
   private volatile SmallSortedMap<K, V>.DescendingEntrySet lazyDescendingEntrySet;

   static <FieldDescriptorType extends FieldSet.FieldDescriptorLite<FieldDescriptorType>> SmallSortedMap<FieldDescriptorType, Object> newFieldMap(int var0) {
      return new SmallSortedMap<FieldDescriptorType, Object>(var0) {
         @Override
         public void makeImmutable() {
            if (!this.isImmutable()) {
               for (int var1 = 0; var1 < this.getNumArrayEntries(); var1++) {
                  java.util.Map.Entry var2 = this.getArrayEntryAt(var1);
                  if (((FieldSet.FieldDescriptorLite)var2.getKey()).isRepeated()) {
                     List var3 = (List)var2.getValue();
                     var2.setValue(Collections.unmodifiableList(var3));
                  }
               }

               for (java.util.Map.Entry var5 : this.getOverflowEntries()) {
                  if (((FieldSet.FieldDescriptorLite)var5.getKey()).isRepeated()) {
                     List var6 = (List)var5.getValue();
                     var5.setValue(Collections.unmodifiableList(var6));
                  }
               }
            }

            super.makeImmutable();
         }
      };
   }

   static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest(int var0) {
      return new SmallSortedMap<>(var0);
   }

   private SmallSortedMap(int var1) {
      this.maxArraySize = var1;
      this.entryList = Collections.emptyList();
      this.overflowEntries = Collections.emptyMap();
      this.overflowEntriesDescending = Collections.emptyMap();
   }

   public void makeImmutable() {
      if (!this.isImmutable) {
         this.overflowEntries = this.overflowEntries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntries);
         this.overflowEntriesDescending = this.overflowEntriesDescending.isEmpty()
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(this.overflowEntriesDescending);
         this.isImmutable = true;
      }
   }

   public boolean isImmutable() {
      return this.isImmutable;
   }

   public int getNumArrayEntries() {
      return this.entryList.size();
   }

   public java.util.Map.Entry<K, V> getArrayEntryAt(int var1) {
      return (java.util.Map.Entry<K, V>)this.entryList.get(var1);
   }

   public int getNumOverflowEntries() {
      return this.overflowEntries.size();
   }

   public Iterable<java.util.Map.Entry<K, V>> getOverflowEntries() {
      return (Iterable<java.util.Map.Entry<K, V>>)(this.overflowEntries.isEmpty() ? SmallSortedMap.EmptySet.iterable() : this.overflowEntries.entrySet());
   }

   Iterable<java.util.Map.Entry<K, V>> getOverflowEntriesDescending() {
      return (Iterable<java.util.Map.Entry<K, V>>)(this.overflowEntriesDescending.isEmpty()
         ? SmallSortedMap.EmptySet.iterable()
         : this.overflowEntriesDescending.entrySet());
   }

   @Override
   public int size() {
      return this.entryList.size() + this.overflowEntries.size();
   }

   @Override
   public boolean containsKey(Object var1) {
      Comparable var2 = (Comparable)var1;
      return this.binarySearchInArray((K)var2) >= 0 || this.overflowEntries.containsKey(var2);
   }

   @Override
   public V get(Object var1) {
      Comparable var2 = (Comparable)var1;
      int var3 = this.binarySearchInArray((K)var2);
      return var3 >= 0 ? this.entryList.get(var3).getValue() : this.overflowEntries.get(var2);
   }

   public V put(K var1, V var2) {
      this.checkMutable();
      int var3 = this.binarySearchInArray((K)var1);
      if (var3 >= 0) {
         return this.entryList.get(var3).setValue((V)var2);
      } else {
         this.ensureEntryArrayMutable();
         int var4 = -(var3 + 1);
         if (var4 >= this.maxArraySize) {
            return this.getOverflowEntriesMutable().put((K)var1, (V)var2);
         } else {
            if (this.entryList.size() == this.maxArraySize) {
               SmallSortedMap.Entry var5 = this.entryList.remove(this.maxArraySize - 1);
               this.getOverflowEntriesMutable().put((K)var5.getKey(), (V)var5.getValue());
            }

            this.entryList.add(var4, new SmallSortedMap.Entry((K)var1, var2));
            return null;
         }
      }
   }

   @Override
   public void clear() {
      this.checkMutable();
      if (!this.entryList.isEmpty()) {
         this.entryList.clear();
      }

      if (!this.overflowEntries.isEmpty()) {
         this.overflowEntries.clear();
      }
   }

   @Override
   public V remove(Object var1) {
      this.checkMutable();
      Comparable var2 = (Comparable)var1;
      int var3 = this.binarySearchInArray((K)var2);
      if (var3 >= 0) {
         return this.removeArrayEntryAt(var3);
      } else {
         return this.overflowEntries.isEmpty() ? null : this.overflowEntries.remove(var2);
      }
   }

   private V removeArrayEntryAt(int var1) {
      this.checkMutable();
      Object var2 = this.entryList.remove(var1).getValue();
      if (!this.overflowEntries.isEmpty()) {
         Iterator var3 = this.getOverflowEntriesMutable().entrySet().iterator();
         this.entryList.add(new SmallSortedMap.Entry((java.util.Map.Entry<K, V>)var3.next()));
         var3.remove();
      }

      return (V)var2;
   }

   private int binarySearchInArray(K var1) {
      int var2 = 0;
      int var3 = this.entryList.size() - 1;
      if (var3 >= 0) {
         int var4 = var1.compareTo(this.entryList.get(var3).getKey());
         if (var4 > 0) {
            return -(var3 + 2);
         }

         if (var4 == 0) {
            return var3;
         }
      }

      while (var2 <= var3) {
         int var6 = (var2 + var3) / 2;
         int var5 = var1.compareTo(this.entryList.get(var6).getKey());
         if (var5 < 0) {
            var3 = var6 - 1;
         } else {
            if (var5 <= 0) {
               return var6;
            }

            var2 = var6 + 1;
         }
      }

      return -(var2 + 1);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      if (this.lazyEntrySet == null) {
         this.lazyEntrySet = new SmallSortedMap.EntrySet();
      }

      return this.lazyEntrySet;
   }

   Set<java.util.Map.Entry<K, V>> descendingEntrySet() {
      if (this.lazyDescendingEntrySet == null) {
         this.lazyDescendingEntrySet = new SmallSortedMap.DescendingEntrySet();
      }

      return this.lazyDescendingEntrySet;
   }

   private void checkMutable() {
      if (this.isImmutable) {
         throw new UnsupportedOperationException();
      }
   }

   private SortedMap<K, V> getOverflowEntriesMutable() {
      this.checkMutable();
      if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
         this.overflowEntries = new TreeMap<>();
         this.overflowEntriesDescending = ((TreeMap)this.overflowEntries).descendingMap();
      }

      return (SortedMap<K, V>)this.overflowEntries;
   }

   private void ensureEntryArrayMutable() {
      this.checkMutable();
      if (this.entryList.isEmpty() && !(this.entryList instanceof ArrayList)) {
         this.entryList = new ArrayList<>(this.maxArraySize);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SmallSortedMap)) {
         return super.equals(var1);
      } else {
         SmallSortedMap var2 = (SmallSortedMap)var1;
         int var3 = this.size();
         if (var3 != var2.size()) {
            return false;
         } else {
            int var4 = this.getNumArrayEntries();
            if (var4 != var2.getNumArrayEntries()) {
               return this.entrySet().equals(var2.entrySet());
            } else {
               for (int var5 = 0; var5 < var4; var5++) {
                  if (!this.getArrayEntryAt(var5).equals(var2.getArrayEntryAt(var5))) {
                     return false;
                  }
               }

               return var4 != var3 ? this.overflowEntries.equals(var2.overflowEntries) : true;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      int var2 = this.getNumArrayEntries();

      for (int var3 = 0; var3 < var2; var3++) {
         var1 += this.entryList.get(var3).hashCode();
      }

      if (this.getNumOverflowEntries() > 0) {
         var1 += this.overflowEntries.hashCode();
      }

      return var1;
   }

   private class DescendingEntryIterator implements Iterator<java.util.Map.Entry<K, V>> {
      private int pos = SmallSortedMap.this.entryList.size();
      private Iterator<java.util.Map.Entry<K, V>> lazyOverflowIterator;

      private DescendingEntryIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.pos > 0 && this.pos <= SmallSortedMap.this.entryList.size() || this.getOverflowIterator().hasNext();
      }

      public java.util.Map.Entry<K, V> next() {
         return this.getOverflowIterator().hasNext()
            ? (java.util.Map.Entry)this.getOverflowIterator().next()
            : (java.util.Map.Entry)SmallSortedMap.this.entryList.get(--this.pos);
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      private Iterator<java.util.Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }
   }

   private class DescendingEntrySet extends SmallSortedMap<K, V>.EntrySet {
      private DescendingEntrySet() {
      }

      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new DescendingEntryIterator();
      }
   }

   private static class EmptySet {
      private static final Iterator<Object> ITERATOR = new Iterator<Object>() {
         @Override
         public boolean hasNext() {
            return false;
         }

         @Override
         public Object next() {
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
      private static final Iterable<Object> ITERABLE = new Iterable<Object>() {
         @Override
         public Iterator<Object> iterator() {
            return SmallSortedMap.EmptySet.ITERATOR;
         }
      };

      static <T> Iterable<T> iterable() {
         return (Iterable<T>)ITERABLE;
      }
   }

   private class Entry implements Comparable<SmallSortedMap<K, V>.Entry>, java.util.Map.Entry<K, V> {
      private final K key;
      private V value;

      Entry(java.util.Map.Entry<K, V> var2) {
         this((K)var2.getKey(), var2.getValue());
      }

      Entry(K var2, V var3) {
         this.key = (K)var2;
         this.value = (V)var3;
      }

      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      public int compareTo(SmallSortedMap<K, V>.Entry var1) {
         return this.getKey().compareTo(var1.getKey());
      }

      @Override
      public V setValue(V var1) {
         SmallSortedMap.this.checkMutable();
         Object var2 = this.value;
         this.value = (V)var1;
         return (V)var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return this.equals(this.key, var2.getKey()) && this.equals(this.value, var2.getValue());
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "=" + this.value;
      }

      private boolean equals(Object var1, Object var2) {
         return var1 == null ? var2 == null : var1.equals(var2);
      }
   }

   private class EntryIterator implements Iterator<java.util.Map.Entry<K, V>> {
      private int pos = -1;
      private boolean nextCalledBeforeRemove;
      private Iterator<java.util.Map.Entry<K, V>> lazyOverflowIterator;

      private EntryIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.pos + 1 < SmallSortedMap.this.entryList.size() || !SmallSortedMap.this.overflowEntries.isEmpty() && this.getOverflowIterator().hasNext();
      }

      public java.util.Map.Entry<K, V> next() {
         this.nextCalledBeforeRemove = true;
         return ++this.pos < SmallSortedMap.this.entryList.size()
            ? (java.util.Map.Entry)SmallSortedMap.this.entryList.get(this.pos)
            : (java.util.Map.Entry)this.getOverflowIterator().next();
      }

      @Override
      public void remove() {
         if (!this.nextCalledBeforeRemove) {
            throw new IllegalStateException("remove() was called before next()");
         } else {
            this.nextCalledBeforeRemove = false;
            SmallSortedMap.this.checkMutable();
            if (this.pos < SmallSortedMap.this.entryList.size()) {
               SmallSortedMap.this.removeArrayEntryAt(this.pos--);
            } else {
               this.getOverflowIterator().remove();
            }
         }
      }

      private Iterator<java.util.Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }
   }

   private class EntrySet extends AbstractSet<java.util.Map.Entry<K, V>> {
      private EntrySet() {
      }

      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new EntryIterator();
      }

      @Override
      public int size() {
         return SmallSortedMap.this.size();
      }

      @Override
      public boolean contains(Object var1) {
         java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
         Object var3 = SmallSortedMap.this.get(var2.getKey());
         Object var4 = var2.getValue();
         return var3 == var4 || var3 != null && var3.equals(var4);
      }

      public boolean add(java.util.Map.Entry<K, V> var1) {
         if (!this.contains(var1)) {
            SmallSortedMap.this.put((K)var1.getKey(), (V)var1.getValue());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object var1) {
         java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
         if (this.contains(var2)) {
            SmallSortedMap.this.remove(var2.getKey());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         SmallSortedMap.this.clear();
      }
   }
}
