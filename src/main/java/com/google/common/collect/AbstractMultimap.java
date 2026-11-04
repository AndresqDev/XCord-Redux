package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMultimap<K, V> implements Multimap<K, V> {
   @LazyInit
   private transient Collection<Entry<K, V>> entries;
   @LazyInit
   private transient Set<K> keySet;
   @LazyInit
   private transient Multiset<K> keys;
   @LazyInit
   private transient Collection<V> values;
   @LazyInit
   private transient Map<K, Collection<V>> asMap;

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsValue(Object var1) {
      for (Collection var3 : this.asMap().values()) {
         if (var3.contains(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsEntry(Object var1, Object var2) {
      Collection var3 = this.asMap().get(var1);
      return var3 != null && var3.contains(var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean remove(Object var1, Object var2) {
      Collection var3 = this.asMap().get(var1);
      return var3 != null && var3.remove(var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.get((K)var1).add((V)var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2) {
      Preconditions.checkNotNull(var2);
      if (var2 instanceof Collection) {
         Collection var4 = (Collection)var2;
         return !var4.isEmpty() && this.get((K)var1).addAll(var4);
      } else {
         Iterator var3 = var2.iterator();
         return var3.hasNext() && Iterators.addAll(this.get((K)var1), var3);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public boolean putAll(Multimap<? extends K, ? extends V> var1) {
      boolean var2 = false;

      for (Entry var4 : var1.entries()) {
         var2 |= this.put((K)var4.getKey(), (V)var4.getValue());
      }

      return var2;
   }

   @CanIgnoreReturnValue
   @Override
   public Collection<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      Preconditions.checkNotNull(var2);
      Collection var3 = this.removeAll(var1);
      this.putAll((K)var1, var2);
      return var3;
   }

   @Override
   public Collection<Entry<K, V>> entries() {
      Collection var1 = this.entries;
      return var1 == null ? (this.entries = this.createEntries()) : var1;
   }

   abstract Collection<Entry<K, V>> createEntries();

   abstract Iterator<Entry<K, V>> entryIterator();

   Spliterator<Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(this.entryIterator(), (long)this.size(), this instanceof SetMultimap ? 1 : 0);
   }

   @Override
   public Set<K> keySet() {
      Set var1 = this.keySet;
      return var1 == null ? (this.keySet = this.createKeySet()) : var1;
   }

   abstract Set<K> createKeySet();

   @Override
   public Multiset<K> keys() {
      Multiset var1 = this.keys;
      return var1 == null ? (this.keys = this.createKeys()) : var1;
   }

   abstract Multiset<K> createKeys();

   @Override
   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 == null ? (this.values = this.createValues()) : var1;
   }

   abstract Collection<V> createValues();

   Iterator<V> valueIterator() {
      return Maps.valueIterator(this.entries().iterator());
   }

   Spliterator<V> valueSpliterator() {
      return Spliterators.spliterator(this.valueIterator(), (long)this.size(), 0);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      Map var1 = this.asMap;
      return var1 == null ? (this.asMap = this.createAsMap()) : var1;
   }

   abstract Map<K, Collection<V>> createAsMap();

   @Override
   public boolean equals(Object var1) {
      return Multimaps.equalsImpl(this, var1);
   }

   @Override
   public int hashCode() {
      return this.asMap().hashCode();
   }

   @Override
   public String toString() {
      return this.asMap().toString();
   }

   class Entries extends Multimaps.Entries<K, V> {
      @Override
      Multimap<K, V> multimap() {
         return AbstractMultimap.this;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return AbstractMultimap.this.entryIterator();
      }

      @Override
      public Spliterator<Entry<K, V>> spliterator() {
         return AbstractMultimap.this.entrySpliterator();
      }
   }

   class EntrySet extends AbstractMultimap<K, V>.Entries implements Set<Entry<K, V>> {
      EntrySet(AbstractMultimap var1) {
      }

      @Override
      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }

      @Override
      public boolean equals(Object var1) {
         return Sets.equalsImpl(this, var1);
      }
   }

   class Values extends AbstractCollection<V> {
      @Override
      public Iterator<V> iterator() {
         return AbstractMultimap.this.valueIterator();
      }

      @Override
      public Spliterator<V> spliterator() {
         return AbstractMultimap.this.valueSpliterator();
      }

      @Override
      public int size() {
         return AbstractMultimap.this.size();
      }

      @Override
      public boolean contains(Object var1) {
         return AbstractMultimap.this.containsValue(var1);
      }

      @Override
      public void clear() {
         AbstractMultimap.this.clear();
      }
   }
}
