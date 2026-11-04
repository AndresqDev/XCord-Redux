package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class FilteredKeyMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {
   final Multimap<K, V> unfiltered;
   final Predicate<? super K> keyPredicate;

   FilteredKeyMultimap(Multimap<K, V> var1, Predicate<? super K> var2) {
      this.unfiltered = Preconditions.checkNotNull(var1);
      this.keyPredicate = Preconditions.checkNotNull(var2);
   }

   @Override
   public Multimap<K, V> unfiltered() {
      return this.unfiltered;
   }

   @Override
   public Predicate<? super Entry<K, V>> entryPredicate() {
      return Maps.keyPredicateOnEntries(this.keyPredicate);
   }

   @Override
   public int size() {
      int var1 = 0;

      for (Collection var3 : this.asMap().values()) {
         var1 += var3.size();
      }

      return var1;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.unfiltered.containsKey(var1) ? this.keyPredicate.apply((K)var1) : false;
   }

   @Override
   public Collection<V> removeAll(Object var1) {
      return this.containsKey(var1) ? this.unfiltered.removeAll(var1) : this.unmodifiableEmptyCollection();
   }

   Collection<V> unmodifiableEmptyCollection() {
      return (Collection<V>)(this.unfiltered instanceof SetMultimap ? Collections.emptySet() : Collections.emptyList());
   }

   @Override
   public void clear() {
      this.keySet().clear();
   }

   @Override
   Set<K> createKeySet() {
      return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
   }

   @Override
   public Collection<V> get(@ParametricNullness K var1) {
      if (this.keyPredicate.apply((K)var1)) {
         return this.unfiltered.get((K)var1);
      } else {
         return (Collection<V>)(this.unfiltered instanceof SetMultimap
            ? new FilteredKeyMultimap.AddRejectingSet<>(var1)
            : new FilteredKeyMultimap.AddRejectingList<>(var1));
      }
   }

   @Override
   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   @Override
   Collection<Entry<K, V>> createEntries() {
      return new FilteredKeyMultimap.Entries();
   }

   @Override
   Collection<V> createValues() {
      return new FilteredMultimapValues<>(this);
   }

   @Override
   Map<K, Collection<V>> createAsMap() {
      return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
   }

   @Override
   Multiset<K> createKeys() {
      return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
   }

   static class AddRejectingList<K, V> extends ForwardingList<V> {
      @ParametricNullness
      final K key;

      AddRejectingList(@ParametricNullness K var1) {
         this.key = (K)var1;
      }

      @Override
      public boolean add(@ParametricNullness V var1) {
         this.add(0, (V)var1);
         return true;
      }

      @Override
      public void add(int var1, @ParametricNullness V var2) {
         Preconditions.checkPositionIndex(var1, 0);
         String var3 = String.valueOf(this.key);
         throw new IllegalArgumentException(
            new StringBuilder(32 + String.valueOf(var3).length()).append("Key does not satisfy predicate: ").append(var3).toString()
         );
      }

      @Override
      public boolean addAll(Collection<? extends V> var1) {
         this.addAll(0, var1);
         return true;
      }

      @CanIgnoreReturnValue
      @Override
      public boolean addAll(int var1, Collection<? extends V> var2) {
         Preconditions.checkNotNull(var2);
         Preconditions.checkPositionIndex(var1, 0);
         String var3 = String.valueOf(this.key);
         throw new IllegalArgumentException(
            new StringBuilder(32 + String.valueOf(var3).length()).append("Key does not satisfy predicate: ").append(var3).toString()
         );
      }

      @Override
      protected List<V> delegate() {
         return Collections.emptyList();
      }
   }

   static class AddRejectingSet<K, V> extends ForwardingSet<V> {
      @ParametricNullness
      final K key;

      AddRejectingSet(@ParametricNullness K var1) {
         this.key = (K)var1;
      }

      @Override
      public boolean add(@ParametricNullness V var1) {
         String var2 = String.valueOf(this.key);
         throw new IllegalArgumentException(
            new StringBuilder(32 + String.valueOf(var2).length()).append("Key does not satisfy predicate: ").append(var2).toString()
         );
      }

      @Override
      public boolean addAll(Collection<? extends V> var1) {
         Preconditions.checkNotNull(var1);
         String var2 = String.valueOf(this.key);
         throw new IllegalArgumentException(
            new StringBuilder(32 + String.valueOf(var2).length()).append("Key does not satisfy predicate: ").append(var2).toString()
         );
      }

      @Override
      protected Set<V> delegate() {
         return Collections.emptySet();
      }
   }

   class Entries extends ForwardingCollection<Entry<K, V>> {
      @Override
      protected Collection<Entry<K, V>> delegate() {
         return Collections2.filter(FilteredKeyMultimap.this.unfiltered.entries(), FilteredKeyMultimap.this.entryPredicate());
      }

      @Override
      public boolean remove(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            if (FilteredKeyMultimap.this.unfiltered.containsKey(var2.getKey()) && FilteredKeyMultimap.this.keyPredicate.apply((K)var2.getKey())) {
               return FilteredKeyMultimap.this.unfiltered.remove(var2.getKey(), var2.getValue());
            }
         }

         return false;
      }
   }
}
