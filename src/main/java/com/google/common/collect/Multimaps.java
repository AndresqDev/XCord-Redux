package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Multimaps {
   private Multimaps() {
   }

   public static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> toMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, Supplier<M> var2
   ) {
      return CollectCollectors.toMultimap(var0, var1, var2);
   }

   @Beta
   public static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> flatteningToMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1, Supplier<M> var2
   ) {
      return CollectCollectors.flatteningToMultimap(var0, var1, var2);
   }

   public static <K, V> Multimap<K, V> newMultimap(Map<K, Collection<V>> var0, com.google.common.base.Supplier<? extends Collection<V>> var1) {
      return new Multimaps.CustomMultimap<>(var0, var1);
   }

   public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> var0, com.google.common.base.Supplier<? extends List<V>> var1) {
      return new Multimaps.CustomListMultimap<>(var0, var1);
   }

   public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> var0, com.google.common.base.Supplier<? extends Set<V>> var1) {
      return new Multimaps.CustomSetMultimap<>(var0, var1);
   }

   public static <K, V> SortedSetMultimap<K, V> newSortedSetMultimap(Map<K, Collection<V>> var0, com.google.common.base.Supplier<? extends SortedSet<V>> var1) {
      return new Multimaps.CustomSortedSetMultimap<>(var0, var1);
   }

   @CanIgnoreReturnValue
   public static <K, V, M extends Multimap<K, V>> M invertFrom(Multimap<? extends V, ? extends K> var0, M var1) {
      Preconditions.checkNotNull(var1);

      for (Entry var3 : var0.entries()) {
         var1.put(var3.getValue(), var3.getKey());
      }

      return (M)var1;
   }

   public static <K, V> Multimap<K, V> synchronizedMultimap(Multimap<K, V> var0) {
      return Synchronized.multimap(var0, null);
   }

   public static <K, V> Multimap<K, V> unmodifiableMultimap(Multimap<K, V> var0) {
      return (Multimap<K, V>)(!(var0 instanceof Multimaps.UnmodifiableMultimap) && !(var0 instanceof ImmutableMultimap)
         ? new Multimaps.UnmodifiableMultimap<>(var0)
         : var0);
   }

   @Deprecated
   public static <K, V> Multimap<K, V> unmodifiableMultimap(ImmutableMultimap<K, V> var0) {
      return Preconditions.checkNotNull(var0);
   }

   public static <K, V> SetMultimap<K, V> synchronizedSetMultimap(SetMultimap<K, V> var0) {
      return Synchronized.setMultimap(var0, null);
   }

   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(SetMultimap<K, V> var0) {
      return (SetMultimap<K, V>)(!(var0 instanceof Multimaps.UnmodifiableSetMultimap) && !(var0 instanceof ImmutableSetMultimap)
         ? new Multimaps.UnmodifiableSetMultimap<>(var0)
         : var0);
   }

   @Deprecated
   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(ImmutableSetMultimap<K, V> var0) {
      return Preconditions.checkNotNull(var0);
   }

   public static <K, V> SortedSetMultimap<K, V> synchronizedSortedSetMultimap(SortedSetMultimap<K, V> var0) {
      return Synchronized.sortedSetMultimap(var0, null);
   }

   public static <K, V> SortedSetMultimap<K, V> unmodifiableSortedSetMultimap(SortedSetMultimap<K, V> var0) {
      return (SortedSetMultimap<K, V>)(var0 instanceof Multimaps.UnmodifiableSortedSetMultimap ? var0 : new Multimaps.UnmodifiableSortedSetMultimap<>(var0));
   }

   public static <K, V> ListMultimap<K, V> synchronizedListMultimap(ListMultimap<K, V> var0) {
      return Synchronized.listMultimap(var0, null);
   }

   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ListMultimap<K, V> var0) {
      return (ListMultimap<K, V>)(!(var0 instanceof Multimaps.UnmodifiableListMultimap) && !(var0 instanceof ImmutableListMultimap)
         ? new Multimaps.UnmodifiableListMultimap<>(var0)
         : var0);
   }

   @Deprecated
   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ImmutableListMultimap<K, V> var0) {
      return Preconditions.checkNotNull(var0);
   }

   private static <V> Collection<V> unmodifiableValueCollection(Collection<V> var0) {
      if (var0 instanceof SortedSet) {
         return Collections.unmodifiableSortedSet((SortedSet<V>)var0);
      } else if (var0 instanceof Set) {
         return Collections.unmodifiableSet((Set<? extends V>)var0);
      } else {
         return (Collection<V>)(var0 instanceof List ? Collections.unmodifiableList((List<? extends V>)var0) : Collections.unmodifiableCollection(var0));
      }
   }

   private static <K, V> Collection<Entry<K, V>> unmodifiableEntries(Collection<Entry<K, V>> var0) {
      return (Collection<Entry<K, V>>)(var0 instanceof Set
         ? Maps.unmodifiableEntrySet((Set<Entry<K, V>>)var0)
         : new Maps.UnmodifiableEntries<>(Collections.unmodifiableCollection(var0)));
   }

   @Beta
   public static <K, V> Map<K, List<V>> asMap(ListMultimap<K, V> var0) {
      return var0.asMap();
   }

   @Beta
   public static <K, V> Map<K, Set<V>> asMap(SetMultimap<K, V> var0) {
      return var0.asMap();
   }

   @Beta
   public static <K, V> Map<K, SortedSet<V>> asMap(SortedSetMultimap<K, V> var0) {
      return var0.asMap();
   }

   @Beta
   public static <K, V> Map<K, Collection<V>> asMap(Multimap<K, V> var0) {
      return var0.asMap();
   }

   public static <K, V> SetMultimap<K, V> forMap(Map<K, V> var0) {
      return new Multimaps.MapMultimap<>(var0);
   }

   public static <K, V1, V2> Multimap<K, V2> transformValues(Multimap<K, V1> var0, com.google.common.base.Function<? super V1, V2> var1) {
      Preconditions.checkNotNull(var1);
      Maps.EntryTransformer var2 = Maps.asEntryTransformer(var1);
      return transformEntries(var0, var2);
   }

   public static <K, V1, V2> ListMultimap<K, V2> transformValues(ListMultimap<K, V1> var0, com.google.common.base.Function<? super V1, V2> var1) {
      Preconditions.checkNotNull(var1);
      Maps.EntryTransformer var2 = Maps.asEntryTransformer(var1);
      return transformEntries(var0, var2);
   }

   public static <K, V1, V2> Multimap<K, V2> transformEntries(Multimap<K, V1> var0, Maps.EntryTransformer<? super K, ? super V1, V2> var1) {
      return new Multimaps.TransformedEntriesMultimap<>(var0, var1);
   }

   public static <K, V1, V2> ListMultimap<K, V2> transformEntries(ListMultimap<K, V1> var0, Maps.EntryTransformer<? super K, ? super V1, V2> var1) {
      return new Multimaps.TransformedEntriesListMultimap<>(var0, var1);
   }

   public static <K, V> ImmutableListMultimap<K, V> index(Iterable<V> var0, com.google.common.base.Function<? super V, K> var1) {
      return index(var0.iterator(), var1);
   }

   public static <K, V> ImmutableListMultimap<K, V> index(Iterator<V> var0, com.google.common.base.Function<? super V, K> var1) {
      Preconditions.checkNotNull(var1);
      ImmutableListMultimap.Builder var2 = ImmutableListMultimap.builder();

      while (var0.hasNext()) {
         Object var3 = var0.next();
         Preconditions.checkNotNull(var3, var0);
         var2.put(var1.apply(var3), var3);
      }

      return var2.build();
   }

   public static <K, V> Multimap<K, V> filterKeys(Multimap<K, V> var0, Predicate<? super K> var1) {
      if (var0 instanceof SetMultimap) {
         return filterKeys((SetMultimap<K, V>)var0, var1);
      } else if (var0 instanceof ListMultimap) {
         return filterKeys((ListMultimap<K, V>)var0, var1);
      } else if (var0 instanceof FilteredKeyMultimap) {
         FilteredKeyMultimap var3 = (FilteredKeyMultimap)var0;
         return new FilteredKeyMultimap<>(var3.unfiltered, Predicates.and(var3.keyPredicate, var1));
      } else if (var0 instanceof FilteredMultimap) {
         FilteredMultimap var2 = (FilteredMultimap)var0;
         return filterFiltered(var2, Maps.keyPredicateOnEntries(var1));
      } else {
         return new FilteredKeyMultimap<>(var0, var1);
      }
   }

   public static <K, V> SetMultimap<K, V> filterKeys(SetMultimap<K, V> var0, Predicate<? super K> var1) {
      if (var0 instanceof FilteredKeySetMultimap) {
         FilteredKeySetMultimap var3 = (FilteredKeySetMultimap)var0;
         return new FilteredKeySetMultimap<>(var3.unfiltered(), Predicates.and(var3.keyPredicate, var1));
      } else if (var0 instanceof FilteredSetMultimap) {
         FilteredSetMultimap var2 = (FilteredSetMultimap)var0;
         return filterFiltered(var2, Maps.keyPredicateOnEntries(var1));
      } else {
         return new FilteredKeySetMultimap<>(var0, var1);
      }
   }

   public static <K, V> ListMultimap<K, V> filterKeys(ListMultimap<K, V> var0, Predicate<? super K> var1) {
      if (var0 instanceof FilteredKeyListMultimap) {
         FilteredKeyListMultimap var2 = (FilteredKeyListMultimap)var0;
         return new FilteredKeyListMultimap<>(var2.unfiltered(), Predicates.and(var2.keyPredicate, var1));
      } else {
         return new FilteredKeyListMultimap<>(var0, var1);
      }
   }

   public static <K, V> Multimap<K, V> filterValues(Multimap<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, Maps.valuePredicateOnEntries(var1));
   }

   public static <K, V> SetMultimap<K, V> filterValues(SetMultimap<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, Maps.valuePredicateOnEntries(var1));
   }

   public static <K, V> Multimap<K, V> filterEntries(Multimap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var1);
      if (var0 instanceof SetMultimap) {
         return filterEntries((SetMultimap<K, V>)var0, var1);
      } else {
         return (Multimap<K, V>)(var0 instanceof FilteredMultimap
            ? filterFiltered((FilteredMultimap<K, V>)var0, var1)
            : new FilteredEntryMultimap<>(Preconditions.checkNotNull(var0), var1));
      }
   }

   public static <K, V> SetMultimap<K, V> filterEntries(SetMultimap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var1);
      return (SetMultimap<K, V>)(var0 instanceof FilteredSetMultimap
         ? filterFiltered((FilteredSetMultimap<K, V>)var0, var1)
         : new FilteredEntrySetMultimap<>(Preconditions.checkNotNull(var0), var1));
   }

   private static <K, V> Multimap<K, V> filterFiltered(FilteredMultimap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Predicate var2 = Predicates.and(var0.entryPredicate(), var1);
      return new FilteredEntryMultimap<>(var0.unfiltered(), var2);
   }

   private static <K, V> SetMultimap<K, V> filterFiltered(FilteredSetMultimap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Predicate var2 = Predicates.and(var0.entryPredicate(), var1);
      return new FilteredEntrySetMultimap<>(var0.unfiltered(), var2);
   }

   static boolean equalsImpl(Multimap<?, ?> var0, Object var1) {
      if (var1 == var0) {
         return true;
      } else if (var1 instanceof Multimap) {
         Multimap var2 = (Multimap)var1;
         return var0.asMap().equals(var2.asMap());
      } else {
         return false;
      }
   }

   static final class AsMap<K, V> extends Maps.ViewCachingAbstractMap<K, Collection<V>> {
      @Weak
      private final Multimap<K, V> multimap;

      AsMap(Multimap<K, V> var1) {
         this.multimap = Preconditions.checkNotNull(var1);
      }

      @Override
      public int size() {
         return this.multimap.keySet().size();
      }

      @Override
      protected Set<Entry<K, Collection<V>>> createEntrySet() {
         return new Multimaps.AsMap.EntrySet();
      }

      void removeValuesForKey(Object var1) {
         this.multimap.keySet().remove(var1);
      }

      public Collection<V> get(Object var1) {
         return this.containsKey(var1) ? this.multimap.get((K)var1) : null;
      }

      public Collection<V> remove(Object var1) {
         return this.containsKey(var1) ? this.multimap.removeAll(var1) : null;
      }

      @Override
      public Set<K> keySet() {
         return this.multimap.keySet();
      }

      @Override
      public boolean isEmpty() {
         return this.multimap.isEmpty();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.multimap.containsKey(var1);
      }

      @Override
      public void clear() {
         this.multimap.clear();
      }

      class EntrySet extends Maps.EntrySet<K, Collection<V>> {
         @Override
         Map<K, Collection<V>> map() {
            return AsMap.this;
         }

         @Override
         public Iterator<Entry<K, Collection<V>>> iterator() {
            return Maps.asMapEntryIterator(AsMap.this.multimap.keySet(), new com.google.common.base.Function<K, Collection<V>>() {
               public Collection<V> apply(@ParametricNullness K var1) {
                  return AsMap.this.multimap.get((K)var1);
               }
            });
         }

         @Override
         public boolean remove(Object var1) {
            if (!this.contains(var1)) {
               return false;
            } else {
               Entry var2 = Objects.requireNonNull((Entry)var1);
               AsMap.this.removeValuesForKey(var2.getKey());
               return true;
            }
         }
      }
   }

   private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V> {
      transient com.google.common.base.Supplier<? extends List<V>> factory;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      CustomListMultimap(Map<K, Collection<V>> var1, com.google.common.base.Supplier<? extends List<V>> var2) {
         super(var1);
         this.factory = Preconditions.checkNotNull(var2);
      }

      @Override
      Set<K> createKeySet() {
         return this.createMaybeNavigableKeySet();
      }

      @Override
      Map<K, Collection<V>> createAsMap() {
         return this.createMaybeNavigableAsMap();
      }

      @Override
      protected List<V> createCollection() {
         return (List<V>)this.factory.get();
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeObject(this.factory);
         var1.writeObject(this.backingMap());
      }

      @GwtIncompatible
      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.factory = (com.google.common.base.Supplier<? extends List<V>>)var1.readObject();
         Map var2 = (Map)var1.readObject();
         this.setMap(var2);
      }
   }

   private static class CustomMultimap<K, V> extends AbstractMapBasedMultimap<K, V> {
      transient com.google.common.base.Supplier<? extends Collection<V>> factory;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      CustomMultimap(Map<K, Collection<V>> var1, com.google.common.base.Supplier<? extends Collection<V>> var2) {
         super(var1);
         this.factory = Preconditions.checkNotNull(var2);
      }

      @Override
      Set<K> createKeySet() {
         return this.createMaybeNavigableKeySet();
      }

      @Override
      Map<K, Collection<V>> createAsMap() {
         return this.createMaybeNavigableAsMap();
      }

      @Override
      protected Collection<V> createCollection() {
         return (Collection<V>)this.factory.get();
      }

      @Override
      <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> var1) {
         if (var1 instanceof NavigableSet) {
            return Sets.unmodifiableNavigableSet((NavigableSet<E>)var1);
         } else if (var1 instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet<E>)var1);
         } else if (var1 instanceof Set) {
            return Collections.unmodifiableSet((Set<? extends E>)var1);
         } else {
            return (Collection<E>)(var1 instanceof List ? Collections.unmodifiableList((List<? extends E>)var1) : Collections.unmodifiableCollection(var1));
         }
      }

      @Override
      Collection<V> wrapCollection(@ParametricNullness K var1, Collection<V> var2) {
         if (var2 instanceof List) {
            return this.wrapList((K)var1, (List<V>)var2, null);
         } else if (var2 instanceof NavigableSet) {
            return new AbstractMapBasedMultimap.WrappedNavigableSet(var1, (NavigableSet<V>)var2, null);
         } else if (var2 instanceof SortedSet) {
            return new AbstractMapBasedMultimap.WrappedSortedSet(var1, (SortedSet<V>)var2, null);
         } else {
            return (Collection<V>)(var2 instanceof Set
               ? new AbstractMapBasedMultimap.WrappedSet(var1, (Set<V>)var2)
               : new AbstractMapBasedMultimap.WrappedCollection(var1, var2, null));
         }
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeObject(this.factory);
         var1.writeObject(this.backingMap());
      }

      @GwtIncompatible
      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.factory = (com.google.common.base.Supplier<? extends Collection<V>>)var1.readObject();
         Map var2 = (Map)var1.readObject();
         this.setMap(var2);
      }
   }

   private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V> {
      transient com.google.common.base.Supplier<? extends Set<V>> factory;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      CustomSetMultimap(Map<K, Collection<V>> var1, com.google.common.base.Supplier<? extends Set<V>> var2) {
         super(var1);
         this.factory = Preconditions.checkNotNull(var2);
      }

      @Override
      Set<K> createKeySet() {
         return this.createMaybeNavigableKeySet();
      }

      @Override
      Map<K, Collection<V>> createAsMap() {
         return this.createMaybeNavigableAsMap();
      }

      @Override
      protected Set<V> createCollection() {
         return (Set<V>)this.factory.get();
      }

      @Override
      <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> var1) {
         if (var1 instanceof NavigableSet) {
            return Sets.unmodifiableNavigableSet((NavigableSet<E>)var1);
         } else {
            return (Collection<E>)(var1 instanceof SortedSet
               ? Collections.unmodifiableSortedSet((SortedSet<E>)var1)
               : Collections.unmodifiableSet((Set<? extends E>)var1));
         }
      }

      @Override
      Collection<V> wrapCollection(@ParametricNullness K var1, Collection<V> var2) {
         if (var2 instanceof NavigableSet) {
            return new AbstractMapBasedMultimap.WrappedNavigableSet(var1, (NavigableSet<V>)var2, null);
         } else {
            return (Collection<V>)(var2 instanceof SortedSet
               ? new AbstractMapBasedMultimap.WrappedSortedSet(var1, (SortedSet<V>)var2, null)
               : new AbstractMapBasedMultimap.WrappedSet(var1, (Set<V>)var2));
         }
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeObject(this.factory);
         var1.writeObject(this.backingMap());
      }

      @GwtIncompatible
      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.factory = (com.google.common.base.Supplier<? extends Set<V>>)var1.readObject();
         Map var2 = (Map)var1.readObject();
         this.setMap(var2);
      }
   }

   private static class CustomSortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {
      transient com.google.common.base.Supplier<? extends SortedSet<V>> factory;
      transient Comparator<? super V> valueComparator;
      @GwtIncompatible
      private static final long serialVersionUID = 0L;

      CustomSortedSetMultimap(Map<K, Collection<V>> var1, com.google.common.base.Supplier<? extends SortedSet<V>> var2) {
         super(var1);
         this.factory = Preconditions.checkNotNull(var2);
         this.valueComparator = ((SortedSet)var2.get()).comparator();
      }

      @Override
      Set<K> createKeySet() {
         return this.createMaybeNavigableKeySet();
      }

      @Override
      Map<K, Collection<V>> createAsMap() {
         return this.createMaybeNavigableAsMap();
      }

      @Override
      protected SortedSet<V> createCollection() {
         return (SortedSet<V>)this.factory.get();
      }

      @Override
      public Comparator<? super V> valueComparator() {
         return this.valueComparator;
      }

      @GwtIncompatible
      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         var1.writeObject(this.factory);
         var1.writeObject(this.backingMap());
      }

      @GwtIncompatible
      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.factory = (com.google.common.base.Supplier<? extends SortedSet<V>>)var1.readObject();
         this.valueComparator = this.factory.get().comparator();
         Map var2 = (Map)var1.readObject();
         this.setMap(var2);
      }
   }

   abstract static class Entries<K, V> extends AbstractCollection<Entry<K, V>> {
      abstract Multimap<K, V> multimap();

      @Override
      public int size() {
         return this.multimap().size();
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.multimap().containsEntry(var2.getKey(), var2.getValue());
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.multimap().remove(var2.getKey(), var2.getValue());
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         this.multimap().clear();
      }
   }

   static class Keys<K, V> extends AbstractMultiset<K> {
      @Weak
      final Multimap<K, V> multimap;

      Keys(Multimap<K, V> var1) {
         this.multimap = var1;
      }

      @Override
      Iterator<Multiset.Entry<K>> entryIterator() {
         return new TransformedIterator<Map.Entry<K, Collection<V>>, Multiset.Entry<K>>(this, this.multimap.asMap().entrySet().iterator()) {
            Multiset.Entry<K> transform(final Map.Entry<K, Collection<V>> var1) {
               return new Multisets.AbstractEntry<K>(this) {
                  @ParametricNullness
                  @Override
                  public K getElement() {
                     return (K)var1.getKey();
                  }

                  @Override
                  public int getCount() {
                     return ((Collection)var1.getValue()).size();
                  }
               };
            }
         };
      }

      @Override
      public Spliterator<K> spliterator() {
         return CollectSpliterators.map(this.multimap.entries().spliterator(), Map.Entry::getKey);
      }

      @Override
      public void forEach(Consumer<? super K> var1) {
         Preconditions.checkNotNull(var1);
         this.multimap.entries().forEach(var1x -> var1.accept(var1x.getKey()));
      }

      @Override
      int distinctElements() {
         return this.multimap.asMap().size();
      }

      @Override
      public int size() {
         return this.multimap.size();
      }

      @Override
      public boolean contains(Object var1) {
         return this.multimap.containsKey(var1);
      }

      @Override
      public Iterator<K> iterator() {
         return Maps.keyIterator(this.multimap.entries().iterator());
      }

      @Override
      public int count(Object var1) {
         Collection var2 = Maps.safeGet(this.multimap.asMap(), var1);
         return var2 == null ? 0 : var2.size();
      }

      @Override
      public int remove(Object var1, int var2) {
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            Collection var3 = Maps.safeGet(this.multimap.asMap(), var1);
            if (var3 == null) {
               return 0;
            } else {
               int var4 = var3.size();
               if (var2 >= var4) {
                  var3.clear();
               } else {
                  Iterator var5 = var3.iterator();

                  for (int var6 = 0; var6 < var2; var6++) {
                     var5.next();
                     var5.remove();
                  }
               }

               return var4;
            }
         }
      }

      @Override
      public void clear() {
         this.multimap.clear();
      }

      @Override
      public Set<K> elementSet() {
         return this.multimap.keySet();
      }

      @Override
      Iterator<K> elementIterator() {
         throw new AssertionError("should never be called");
      }
   }

   private static class MapMultimap<K, V> extends AbstractMultimap<K, V> implements SetMultimap<K, V>, Serializable {
      final Map<K, V> map;
      private static final long serialVersionUID = 7845222491160860175L;

      MapMultimap(Map<K, V> var1) {
         this.map = Preconditions.checkNotNull(var1);
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.map.containsKey(var1);
      }

      @Override
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      @Override
      public boolean containsEntry(Object var1, Object var2) {
         return this.map.entrySet().contains(Maps.immutableEntry(var1, var2));
      }

      @Override
      public Set<V> get(@ParametricNullness final K var1) {
         return new Sets.ImprovedAbstractSet<V>() {
            @Override
            public Iterator<V> iterator() {
               return new Iterator<V>() {
                  int i;

                  @Override
                  public boolean hasNext() {
                     return this.i == 0 && MapMultimap.this.map.containsKey(var1);
                  }

                  @ParametricNullness
                  @Override
                  public V next() {
                     if (!this.hasNext()) {
                        throw new NoSuchElementException();
                     } else {
                        this.i++;
                        return NullnessCasts.uncheckedCastNullableTToT(MapMultimap.this.map.get(var1));
                     }
                  }

                  @Override
                  public void remove() {
                     CollectPreconditions.checkRemove(this.i == 1);
                     this.i = -1;
                     MapMultimap.this.map.remove(var1);
                  }
               };
            }

            @Override
            public int size() {
               return MapMultimap.this.map.containsKey(var1) ? 1 : 0;
            }
         };
      }

      @Override
      public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(Multimap<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1, Object var2) {
         return this.map.entrySet().remove(Maps.immutableEntry(var1, var2));
      }

      @Override
      public Set<V> removeAll(Object var1) {
         HashSet var2 = new HashSet(2);
         if (!this.map.containsKey(var1)) {
            return var2;
         } else {
            var2.add(this.map.remove(var1));
            return var2;
         }
      }

      @Override
      public void clear() {
         this.map.clear();
      }

      @Override
      Set<K> createKeySet() {
         return this.map.keySet();
      }

      @Override
      Collection<V> createValues() {
         return this.map.values();
      }

      @Override
      public Set<Entry<K, V>> entries() {
         return this.map.entrySet();
      }

      @Override
      Collection<Entry<K, V>> createEntries() {
         throw new AssertionError("unreachable");
      }

      @Override
      Multiset<K> createKeys() {
         return new Multimaps.Keys<>(this);
      }

      @Override
      Iterator<Entry<K, V>> entryIterator() {
         return this.map.entrySet().iterator();
      }

      @Override
      Map<K, Collection<V>> createAsMap() {
         return new Multimaps.AsMap<>(this);
      }

      @Override
      public int hashCode() {
         return this.map.hashCode();
      }
   }

   private static final class TransformedEntriesListMultimap<K, V1, V2> extends Multimaps.TransformedEntriesMultimap<K, V1, V2> implements ListMultimap<K, V2> {
      TransformedEntriesListMultimap(ListMultimap<K, V1> var1, Maps.EntryTransformer<? super K, ? super V1, V2> var2) {
         super(var1, var2);
      }

      List<V2> transform(@ParametricNullness K var1, Collection<V1> var2) {
         return Lists.transform((List<V1>)var2, Maps.asValueToValueFunction(this.transformer, (K)var1));
      }

      @Override
      public List<V2> get(@ParametricNullness K var1) {
         return this.transform((K)var1, this.fromMultimap.get((K)var1));
      }

      @Override
      public List<V2> removeAll(Object var1) {
         return this.transform((K)var1, this.fromMultimap.removeAll(var1));
      }

      @Override
      public List<V2> replaceValues(@ParametricNullness K var1, Iterable<? extends V2> var2) {
         throw new UnsupportedOperationException();
      }
   }

   private static class TransformedEntriesMultimap<K, V1, V2> extends AbstractMultimap<K, V2> {
      final Multimap<K, V1> fromMultimap;
      final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;

      TransformedEntriesMultimap(Multimap<K, V1> var1, Maps.EntryTransformer<? super K, ? super V1, V2> var2) {
         this.fromMultimap = Preconditions.checkNotNull(var1);
         this.transformer = Preconditions.checkNotNull(var2);
      }

      Collection<V2> transform(@ParametricNullness K var1, Collection<V1> var2) {
         com.google.common.base.Function var3 = Maps.asValueToValueFunction(this.transformer, (K)var1);
         return (Collection<V2>)(var2 instanceof List ? Lists.transform((List)var2, var3) : Collections2.transform(var2, var3));
      }

      @Override
      Map<K, Collection<V2>> createAsMap() {
         return Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer<K, Collection<V1>, Collection<V2>>() {
            public Collection<V2> transformEntry(@ParametricNullness K var1, Collection<V1> var2) {
               return TransformedEntriesMultimap.this.transform(var1, var2);
            }
         });
      }

      @Override
      public void clear() {
         this.fromMultimap.clear();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.fromMultimap.containsKey(var1);
      }

      @Override
      Collection<Entry<K, V2>> createEntries() {
         return new AbstractMultimap.Entries();
      }

      @Override
      Iterator<Entry<K, V2>> entryIterator() {
         return Iterators.transform(this.fromMultimap.entries().iterator(), Maps.asEntryToEntryFunction(this.transformer));
      }

      @Override
      public Collection<V2> get(@ParametricNullness K var1) {
         return this.transform((K)var1, this.fromMultimap.get((K)var1));
      }

      @Override
      public boolean isEmpty() {
         return this.fromMultimap.isEmpty();
      }

      @Override
      Set<K> createKeySet() {
         return this.fromMultimap.keySet();
      }

      @Override
      Multiset<K> createKeys() {
         return this.fromMultimap.keys();
      }

      @Override
      public boolean put(@ParametricNullness K var1, @ParametricNullness V2 var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(@ParametricNullness K var1, Iterable<? extends V2> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(Multimap<? extends K, ? extends V2> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1, Object var2) {
         return this.get((K)var1).remove(var2);
      }

      @Override
      public Collection<V2> removeAll(Object var1) {
         return this.transform((K)var1, this.fromMultimap.removeAll(var1));
      }

      @Override
      public Collection<V2> replaceValues(@ParametricNullness K var1, Iterable<? extends V2> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return this.fromMultimap.size();
      }

      @Override
      Collection<V2> createValues() {
         return Collections2.transform(this.fromMultimap.entries(), Maps.asEntryToValueFunction(this.transformer));
      }
   }

   private static class UnmodifiableListMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V> implements ListMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      UnmodifiableListMultimap(ListMultimap<K, V> var1) {
         super(var1);
      }

      public ListMultimap<K, V> delegate() {
         return (ListMultimap<K, V>)super.delegate();
      }

      @Override
      public List<V> get(@ParametricNullness K var1) {
         return Collections.unmodifiableList(this.delegate().get((K)var1));
      }

      @Override
      public List<V> removeAll(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }
   }

   private static class UnmodifiableMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {
      final Multimap<K, V> delegate;
      @LazyInit
      transient Collection<Entry<K, V>> entries;
      @LazyInit
      transient Multiset<K> keys;
      @LazyInit
      transient Set<K> keySet;
      @LazyInit
      transient Collection<V> values;
      @LazyInit
      transient Map<K, Collection<V>> map;
      private static final long serialVersionUID = 0L;

      UnmodifiableMultimap(Multimap<K, V> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      protected Multimap<K, V> delegate() {
         return this.delegate;
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Map<K, Collection<V>> asMap() {
         Map var1 = this.map;
         if (var1 == null) {
            var1 = this.map = Collections.unmodifiableMap(
               Maps.transformValues(this.delegate.asMap(), new com.google.common.base.Function<Collection<V>, Collection<V>>(this) {
                  public Collection<V> apply(Collection<V> var1) {
                     return Multimaps.unmodifiableValueCollection(var1);
                  }
               })
            );
         }

         return var1;
      }

      @Override
      public Collection<Entry<K, V>> entries() {
         Collection var1 = this.entries;
         if (var1 == null) {
            this.entries = var1 = Multimaps.unmodifiableEntries(this.delegate.entries());
         }

         return var1;
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V> var1) {
         this.delegate.forEach(Preconditions.checkNotNull(var1));
      }

      @Override
      public Collection<V> get(@ParametricNullness K var1) {
         return Multimaps.unmodifiableValueCollection(this.delegate.get((K)var1));
      }

      @Override
      public Multiset<K> keys() {
         Multiset var1 = this.keys;
         if (var1 == null) {
            this.keys = var1 = Multisets.unmodifiableMultiset(this.delegate.keys());
         }

         return var1;
      }

      @Override
      public Set<K> keySet() {
         Set var1 = this.keySet;
         if (var1 == null) {
            this.keySet = var1 = Collections.unmodifiableSet(this.delegate.keySet());
         }

         return var1;
      }

      @Override
      public boolean put(@ParametricNullness K var1, @ParametricNullness V var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean putAll(Multimap<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<V> removeAll(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<V> values() {
         Collection var1 = this.values;
         if (var1 == null) {
            this.values = var1 = Collections.unmodifiableCollection(this.delegate.values());
         }

         return var1;
      }
   }

   private static class UnmodifiableSetMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V> implements SetMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      UnmodifiableSetMultimap(SetMultimap<K, V> var1) {
         super(var1);
      }

      public SetMultimap<K, V> delegate() {
         return (SetMultimap<K, V>)super.delegate();
      }

      @Override
      public Set<V> get(@ParametricNullness K var1) {
         return Collections.unmodifiableSet(this.delegate().get((K)var1));
      }

      @Override
      public Set<Entry<K, V>> entries() {
         return Maps.unmodifiableEntrySet(this.delegate().entries());
      }

      @Override
      public Set<V> removeAll(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }
   }

   private static class UnmodifiableSortedSetMultimap<K, V> extends Multimaps.UnmodifiableSetMultimap<K, V> implements SortedSetMultimap<K, V> {
      private static final long serialVersionUID = 0L;

      UnmodifiableSortedSetMultimap(SortedSetMultimap<K, V> var1) {
         super(var1);
      }

      public SortedSetMultimap<K, V> delegate() {
         return (SortedSetMultimap<K, V>)super.delegate();
      }

      @Override
      public SortedSet<V> get(@ParametricNullness K var1) {
         return Collections.unmodifiableSortedSet(this.delegate().get((K)var1));
      }

      @Override
      public SortedSet<V> removeAll(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public SortedSet<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Comparator<? super V> valueComparator() {
         return this.delegate().valueComparator();
      }
   }
}
