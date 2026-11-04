package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class FilteredEntryMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {
   final Multimap<K, V> unfiltered;
   final Predicate<? super Entry<K, V>> predicate;

   FilteredEntryMultimap(Multimap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
      this.unfiltered = Preconditions.checkNotNull(var1);
      this.predicate = Preconditions.checkNotNull(var2);
   }

   @Override
   public Multimap<K, V> unfiltered() {
      return this.unfiltered;
   }

   @Override
   public Predicate<? super Entry<K, V>> entryPredicate() {
      return this.predicate;
   }

   @Override
   public int size() {
      return this.entries().size();
   }

   private boolean satisfies(@ParametricNullness K var1, @ParametricNullness V var2) {
      return this.predicate.apply(Maps.immutableEntry((K)var1, (V)var2));
   }

   static <E> Collection<E> filterCollection(Collection<E> var0, Predicate<? super E> var1) {
      return (Collection<E>)(var0 instanceof Set ? Sets.filter((Set<E>)var0, var1) : Collections2.filter(var0, var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.asMap().get(var1) != null;
   }

   @Override
   public Collection<V> removeAll(Object var1) {
      return MoreObjects.firstNonNull(this.asMap().remove(var1), this.unmodifiableEmptyCollection());
   }

   Collection<V> unmodifiableEmptyCollection() {
      return (Collection<V>)(this.unfiltered instanceof SetMultimap ? Collections.emptySet() : Collections.emptyList());
   }

   @Override
   public void clear() {
      this.entries().clear();
   }

   @Override
   public Collection<V> get(@ParametricNullness K var1) {
      return filterCollection(this.unfiltered.get((K)var1), new FilteredEntryMultimap.ValuePredicate(var1));
   }

   @Override
   Collection<Entry<K, V>> createEntries() {
      return filterCollection(this.unfiltered.entries(), this.predicate);
   }

   @Override
   Collection<V> createValues() {
      return new FilteredMultimapValues<>(this);
   }

   @Override
   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   @Override
   Map<K, Collection<V>> createAsMap() {
      return new FilteredEntryMultimap.AsMap();
   }

   @Override
   Set<K> createKeySet() {
      return this.asMap().keySet();
   }

   boolean removeEntriesIf(Predicate<? super Entry<K, Collection<V>>> var1) {
      Iterator var2 = this.unfiltered.asMap().entrySet().iterator();
      boolean var3 = false;

      while (var2.hasNext()) {
         Entry var4 = (Entry)var2.next();
         Object var5 = var4.getKey();
         Collection var6 = filterCollection((Collection<V>)var4.getValue(), new FilteredEntryMultimap.ValuePredicate(var5));
         if (!var6.isEmpty() && var1.apply(Maps.immutableEntry(var5, var6))) {
            if (var6.size() == ((Collection)var4.getValue()).size()) {
               var2.remove();
            } else {
               var6.clear();
            }

            var3 = true;
         }
      }

      return var3;
   }

   @Override
   Multiset<K> createKeys() {
      return new FilteredEntryMultimap.Keys();
   }

   class AsMap extends Maps.ViewCachingAbstractMap<K, Collection<V>> {
      @Override
      public boolean containsKey(Object var1) {
         return this.get(var1) != null;
      }

      @Override
      public void clear() {
         FilteredEntryMultimap.this.clear();
      }

      public Collection<V> get(Object var1) {
         Collection var2 = FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
         if (var2 == null) {
            return null;
         } else {
            var2 = FilteredEntryMultimap.filterCollection(var2, FilteredEntryMultimap.this.new ValuePredicate(var1));
            return var2.isEmpty() ? null : var2;
         }
      }

      public Collection<V> remove(Object var1) {
         Collection var2 = FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
         if (var2 == null) {
            return null;
         } else {
            Object var3 = var1;
            ArrayList var4 = Lists.newArrayList();
            Iterator var5 = var2.iterator();

            while (var5.hasNext()) {
               Object var6 = var5.next();
               if (FilteredEntryMultimap.this.satisfies((K)var3, (V)var6)) {
                  var5.remove();
                  var4.add(var6);
               }
            }

            if (var4.isEmpty()) {
               return null;
            } else {
               return (Collection<V>)(FilteredEntryMultimap.this.unfiltered instanceof SetMultimap
                  ? Collections.unmodifiableSet(Sets.newLinkedHashSet(var4))
                  : Collections.unmodifiableList(var4));
            }
         }
      }

      @Override
      Set<K> createKeySet() {
         class 1KeySetImpl extends Maps.KeySet<K, Collection<V>> {
            _KeySetImpl() {
               super(AsMap.this);
            }

            @Override
            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(var1)));
            }

            @Override
            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(var1))));
            }

            @Override
            public boolean remove(Object var1) {
               return AsMap.this.remove(var1) != null;
            }
         }

         return new 1KeySetImpl();
      }

      @Override
      Set<Entry<K, Collection<V>>> createEntrySet() {
         class 1EntrySetImpl extends Maps.EntrySet<K, Collection<V>> {
            @Override
            Map<K, Collection<V>> map() {
               return AsMap.this;
            }

            @Override
            public Iterator<Entry<K, Collection<V>>> iterator() {
               return new AbstractIterator<Entry<K, Collection<V>>>() {
                  final Iterator<Entry<K, Collection<V>>> backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();

                  protected Entry<K, Collection<V>> computeNext() {
                     while (this.backingIterator.hasNext()) {
                        Entry var1 = this.backingIterator.next();
                        Object var2 = var1.getKey();
                        Collection var3 = FilteredEntryMultimap.filterCollection(
                           (Collection<V>)var1.getValue(), FilteredEntryMultimap.this.new ValuePredicate(var2)
                        );
                        if (!var3.isEmpty()) {
                           return Maps.immutableEntry((K)var2, var3);
                        }
                     }

                     return this.endOfData();
                  }
               };
            }

            @Override
            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(var1));
            }

            @Override
            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(var1)));
            }

            @Override
            public int size() {
               return Iterators.size(this.iterator());
            }
         }

         return new 1EntrySetImpl();
      }

      @Override
      Collection<Collection<V>> createValues() {
         class 1ValuesImpl extends Maps.Values<K, Collection<V>> {
            _ValuesImpl() {
               super(AsMap.this);
            }

            @Override
            public boolean remove(Object var1) {
               if (var1 instanceof Collection) {
                  Collection var2 = (Collection)var1;
                  Iterator var3 = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();

                  while (var3.hasNext()) {
                     Entry var4 = (Entry)var3.next();
                     Object var5 = var4.getKey();
                     Collection var6 = FilteredEntryMultimap.filterCollection(
                        (Collection<V>)var4.getValue(), FilteredEntryMultimap.this.new ValuePredicate(var5)
                     );
                     if (!var6.isEmpty() && var2.equals(var6)) {
                        if (var6.size() == ((Collection)var4.getValue()).size()) {
                           var3.remove();
                        } else {
                           var6.clear();
                        }

                        return true;
                     }
                  }
               }

               return false;
            }

            @Override
            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(var1)));
            }

            @Override
            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(var1))));
            }
         }

         return new 1ValuesImpl();
      }
   }

   class Keys extends Multimaps.Keys<K, V> {
      Keys() {
         super(FilteredEntryMultimap.this);
      }

      @Override
      public int remove(Object var1, int var2) {
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            Collection var3 = FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
            if (var3 == null) {
               return 0;
            } else {
               Object var4 = var1;
               int var5 = 0;
               Iterator var6 = var3.iterator();

               while (var6.hasNext()) {
                  Object var7 = var6.next();
                  if (FilteredEntryMultimap.this.satisfies((K)var4, (V)var7)) {
                     if (++var5 <= var2) {
                        var6.remove();
                     }
                  }
               }

               return var5;
            }
         }
      }

      @Override
      public Set<Multiset.Entry<K>> entrySet() {
         return new Multisets.EntrySet<K>() {
            @Override
            Multiset<K> multiset() {
               return Keys.this;
            }

            @Override
            public Iterator<Multiset.Entry<K>> iterator() {
               return Keys.this.entryIterator();
            }

            @Override
            public int size() {
               return FilteredEntryMultimap.this.keySet().size();
            }

            private boolean removeEntriesIf(final Predicate<? super Multiset.Entry<K>> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(new Predicate<Map.Entry<K, Collection<V>>>(this) {
                  public boolean apply(Map.Entry<K, Collection<V>> var1x) {
                     return var1.apply(Multisets.immutableEntry(var1x.getKey(), ((Collection)var1x.getValue()).size()));
                  }
               });
            }

            @Override
            public boolean removeAll(Collection<?> var1) {
               return this.removeEntriesIf(Predicates.in(var1));
            }

            @Override
            public boolean retainAll(Collection<?> var1) {
               return this.removeEntriesIf(Predicates.not(Predicates.in(var1)));
            }
         };
      }
   }

   final class ValuePredicate implements Predicate<V> {
      @ParametricNullness
      private final K key;

      ValuePredicate(@ParametricNullness K var2) {
         this.key = (K)var2;
      }

      @Override
      public boolean apply(@ParametricNullness V var1) {
         return FilteredEntryMultimap.this.satisfies(this.key, (V)var1);
      }
   }
}
