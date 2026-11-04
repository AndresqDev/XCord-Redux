package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Maps {
   private Maps() {
   }

   static <K> Function<Entry<K, ?>, K> keyFunction() {
      return Maps.EntryFunction.KEY;
   }

   static <V> Function<Entry<?, V>, V> valueFunction() {
      return Maps.EntryFunction.VALUE;
   }

   static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> var0) {
      return new TransformedIterator<Entry<K, V>, K>(var0) {
         @ParametricNullness
         K transform(Entry<K, V> var1) {
            return (K)var1.getKey();
         }
      };
   }

   static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> var0) {
      return new TransformedIterator<Entry<K, V>, V>(var0) {
         @ParametricNullness
         V transform(Entry<K, V> var1) {
            return (V)var1.getValue();
         }
      };
   }

   @GwtCompatible(
      serializable = true
   )
   public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> var0) {
      if (var0 instanceof ImmutableEnumMap) {
         return (ImmutableEnumMap)var0;
      } else {
         Iterator var1 = var0.entrySet().iterator();
         if (!var1.hasNext()) {
            return ImmutableMap.of();
         } else {
            Entry var2 = (Entry)var1.next();
            Enum var3 = (Enum)var2.getKey();
            Object var4 = var2.getValue();
            CollectPreconditions.checkEntryNotNull(var3, var4);
            Class var5 = var3.getDeclaringClass();
            EnumMap var6 = new EnumMap(var5);
            var6.put((K)var3, var4);

            while (var1.hasNext()) {
               Entry var7 = (Entry)var1.next();
               Enum var8 = (Enum)var7.getKey();
               Object var9 = var7.getValue();
               CollectPreconditions.checkEntryNotNull(var8, var9);
               var6.put((K)var8, var9);
            }

            return ImmutableEnumMap.asImmutable(var6);
         }
      }
   }

   public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
      java.util.function.Function<? super T, ? extends K> var0, java.util.function.Function<? super T, ? extends V> var1
   ) {
      return CollectCollectors.toImmutableEnumMap(var0, var1);
   }

   public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
      java.util.function.Function<? super T, ? extends K> var0, java.util.function.Function<? super T, ? extends V> var1, BinaryOperator<V> var2
   ) {
      return CollectCollectors.toImmutableEnumMap(var0, var1, var2);
   }

   public static <K, V> HashMap<K, V> newHashMap() {
      return new HashMap<>();
   }

   public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> var0) {
      return new HashMap<>(var0);
   }

   public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int var0) {
      return new HashMap<>(capacity(var0));
   }

   static int capacity(int var0) {
      if (var0 < 3) {
         CollectPreconditions.checkNonnegative(var0, "expectedSize");
         return var0 + 1;
      } else {
         return var0 < 1073741824 ? (int)((float)var0 / 0.75F + 1.0F) : Integer.MAX_VALUE;
      }
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
      return new LinkedHashMap<>();
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> var0) {
      return new LinkedHashMap<>(var0);
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int var0) {
      return new LinkedHashMap<>(capacity(var0));
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
      return new ConcurrentHashMap<>();
   }

   public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
      return new TreeMap<>();
   }

   public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> var0) {
      return new TreeMap<>(var0);
   }

   public static <C, K extends C, V> TreeMap<K, V> newTreeMap(Comparator<C> var0) {
      return new TreeMap<>(var0);
   }

   public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> var0) {
      return new EnumMap<>(Preconditions.checkNotNull(var0));
   }

   public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> var0) {
      return new EnumMap<>(var0);
   }

   public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
      return new IdentityHashMap<>();
   }

   public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> var0, Map<? extends K, ? extends V> var1) {
      if (var0 instanceof SortedMap) {
         SortedMap var2 = (SortedMap)var0;
         return difference(var2, var1);
      } else {
         return difference(var0, var1, Equivalence.equals());
      }
   }

   public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> var0, Map<? extends K, ? extends V> var1, Equivalence<? super V> var2) {
      Preconditions.checkNotNull(var2);
      LinkedHashMap var3 = newLinkedHashMap();
      LinkedHashMap var4 = new LinkedHashMap(var1);
      LinkedHashMap var5 = newLinkedHashMap();
      LinkedHashMap var6 = newLinkedHashMap();
      doDifference(var0, var1, var2, var3, var4, var5, var6);
      return new Maps.MapDifferenceImpl<>(var3, var4, var5, var6);
   }

   public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> var0, Map<? extends K, ? extends V> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Comparator var2 = orNaturalOrder(var0.comparator());
      TreeMap var3 = newTreeMap(var2);
      TreeMap var4 = newTreeMap(var2);
      var4.putAll(var1);
      TreeMap var5 = newTreeMap(var2);
      TreeMap var6 = newTreeMap(var2);
      doDifference(var0, var1, Equivalence.equals(), var3, var4, var5, var6);
      return new Maps.SortedMapDifferenceImpl<>(var3, var4, var5, var6);
   }

   private static <K, V> void doDifference(
      Map<? extends K, ? extends V> var0,
      Map<? extends K, ? extends V> var1,
      Equivalence<? super V> var2,
      Map<K, V> var3,
      Map<K, V> var4,
      Map<K, V> var5,
      Map<K, MapDifference.ValueDifference<V>> var6
   ) {
      for (Entry var8 : var0.entrySet()) {
         Object var9 = var8.getKey();
         Object var10 = var8.getValue();
         if (var1.containsKey(var9)) {
            Object var11 = NullnessCasts.uncheckedCastNullableTToT(var4.remove(var9));
            if (var2.equivalent(var10, var11)) {
               var5.put(var9, var10);
            } else {
               var6.put(var9, Maps.ValueDifferenceImpl.create(var10, var11));
            }
         } else {
            var3.put(var9, var10);
         }
      }
   }

   private static <K, V> Map<K, V> unmodifiableMap(Map<K, ? extends V> var0) {
      return (Map<K, V>)(var0 instanceof SortedMap ? Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>)var0) : Collections.unmodifiableMap(var0));
   }

   static <E> Comparator<? super E> orNaturalOrder(Comparator<? super E> var0) {
      return (Comparator<? super E>)(var0 != null ? var0 : Ordering.natural());
   }

   public static <K, V> Map<K, V> asMap(Set<K> var0, Function<? super K, V> var1) {
      return new Maps.AsMapView<>(var0, var1);
   }

   public static <K, V> SortedMap<K, V> asMap(SortedSet<K> var0, Function<? super K, V> var1) {
      return new Maps.SortedAsMapView<>(var0, var1);
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> var0, Function<? super K, V> var1) {
      return new Maps.NavigableAsMapView<>(var0, var1);
   }

   static <K, V> Iterator<Entry<K, V>> asMapEntryIterator(Set<K> var0, final Function<? super K, V> var1) {
      return new TransformedIterator<K, Entry<K, V>>(var0.iterator()) {
         Entry<K, V> transform(@ParametricNullness K var1x) {
            return Maps.immutableEntry((K)var1x, (V)var1.apply(var1x));
         }
      };
   }

   private static <E> Set<E> removeOnlySet(final Set<E> var0) {
      return new ForwardingSet<E>() {
         @Override
         protected Set<E> delegate() {
            return var0;
         }

         @Override
         public boolean add(@ParametricNullness E var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends E> var1) {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> var0) {
      return new ForwardingSortedSet<E>() {
         @Override
         protected SortedSet<E> delegate() {
            return var0;
         }

         @Override
         public boolean add(@ParametricNullness E var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends E> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public SortedSet<E> headSet(@ParametricNullness E var1) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.headSet(var1));
         }

         @Override
         public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.subSet(var1, var2));
         }

         @Override
         public SortedSet<E> tailSet(@ParametricNullness E var1) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.tailSet(var1));
         }
      };
   }

   @GwtIncompatible
   private static <E> NavigableSet<E> removeOnlyNavigableSet(final NavigableSet<E> var0) {
      return new ForwardingNavigableSet<E>() {
         @Override
         protected NavigableSet<E> delegate() {
            return var0;
         }

         @Override
         public boolean add(@ParametricNullness E var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean addAll(Collection<? extends E> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public SortedSet<E> headSet(@ParametricNullness E var1) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.headSet(var1));
         }

         @Override
         public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
            return Maps.removeOnlyNavigableSet((NavigableSet<E>)super.headSet(var1, var2));
         }

         @Override
         public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.subSet(var1, var2));
         }

         @Override
         public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
            return Maps.removeOnlyNavigableSet((NavigableSet<E>)super.subSet(var1, var2, var3, var4));
         }

         @Override
         public SortedSet<E> tailSet(@ParametricNullness E var1) {
            return Maps.removeOnlySortedSet((SortedSet<E>)super.tailSet(var1));
         }

         @Override
         public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
            return Maps.removeOnlyNavigableSet((NavigableSet<E>)super.tailSet(var1, var2));
         }

         @Override
         public NavigableSet<E> descendingSet() {
            return Maps.removeOnlyNavigableSet(super.descendingSet());
         }
      };
   }

   public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> var0, Function<? super K, V> var1) {
      return toMap(var0.iterator(), var1);
   }

   public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> var0, Function<? super K, V> var1) {
      Preconditions.checkNotNull(var1);
      LinkedHashMap var2 = newLinkedHashMap();

      while (var0.hasNext()) {
         Object var3 = var0.next();
         var2.put(var3, var1.apply(var3));
      }

      return ImmutableMap.copyOf(var2);
   }

   @CanIgnoreReturnValue
   public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> var0, Function<? super V, K> var1) {
      return uniqueIndex(var0.iterator(), var1);
   }

   @CanIgnoreReturnValue
   public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> var0, Function<? super V, K> var1) {
      Preconditions.checkNotNull(var1);
      ImmutableMap.Builder var2 = ImmutableMap.builder();

      while (var0.hasNext()) {
         Object var3 = var0.next();
         var2.put(var1.apply(var3), var3);
      }

      try {
         return var2.build();
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException(String.valueOf(var4.getMessage()).concat(". To index multiple values under a key, use Multimaps.index."));
      }
   }

   @GwtIncompatible
   public static ImmutableMap<String, String> fromProperties(Properties var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      Enumeration var2 = var0.propertyNames();

      while (var2.hasMoreElements()) {
         String var3 = Objects.requireNonNull((String)var2.nextElement());
         var1.put(var3, Objects.requireNonNull(var0.getProperty(var3)));
      }

      return var1.build();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <K, V> Entry<K, V> immutableEntry(@ParametricNullness K var0, @ParametricNullness V var1) {
      return new ImmutableEntry<>((K)var0, (V)var1);
   }

   static <K, V> Set<Entry<K, V>> unmodifiableEntrySet(Set<Entry<K, V>> var0) {
      return new Maps.UnmodifiableEntrySet<>(Collections.unmodifiableSet(var0));
   }

   static <K, V> Entry<K, V> unmodifiableEntry(final Entry<? extends K, ? extends V> var0) {
      Preconditions.checkNotNull(var0);
      return new AbstractMapEntry<K, V>() {
         @ParametricNullness
         @Override
         public K getKey() {
            return (K)var0.getKey();
         }

         @ParametricNullness
         @Override
         public V getValue() {
            return (V)var0.getValue();
         }
      };
   }

   static <K, V> UnmodifiableIterator<Entry<K, V>> unmodifiableEntryIterator(final Iterator<Entry<K, V>> var0) {
      return new UnmodifiableIterator<Entry<K, V>>() {
         @Override
         public boolean hasNext() {
            return var0.hasNext();
         }

         public Entry<K, V> next() {
            return Maps.unmodifiableEntry((Entry<? extends K, ? extends V>)var0.next());
         }
      };
   }

   public static <A, B> Converter<A, B> asConverter(BiMap<A, B> var0) {
      return new Maps.BiMapConverter<>(var0);
   }

   public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> var0) {
      return Synchronized.biMap(var0, null);
   }

   public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> var0) {
      return new Maps.UnmodifiableBiMap<>(var0, null);
   }

   public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> var0, Function<? super V1, V2> var1) {
      return transformEntries(var0, asEntryTransformer(var1));
   }

   public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> var0, Function<? super V1, V2> var1) {
      return transformEntries(var0, asEntryTransformer(var1));
   }

   @GwtIncompatible
   public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> var0, Function<? super V1, V2> var1) {
      return transformEntries(var0, asEntryTransformer(var1));
   }

   public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> var0, Maps.EntryTransformer<? super K, ? super V1, V2> var1) {
      return new Maps.TransformedEntriesMap<>(var0, var1);
   }

   public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> var0, Maps.EntryTransformer<? super K, ? super V1, V2> var1) {
      return new Maps.TransformedEntriesSortedMap<>(var0, var1);
   }

   @GwtIncompatible
   public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> var0, Maps.EntryTransformer<? super K, ? super V1, V2> var1) {
      return new Maps.TransformedEntriesNavigableMap<>(var0, var1);
   }

   static <K, V1, V2> Maps.EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> var0) {
      Preconditions.checkNotNull(var0);
      return new Maps.EntryTransformer<K, V1, V2>() {
         @ParametricNullness
         @Override
         public V2 transformEntry(@ParametricNullness K var1, @ParametricNullness V1 var2) {
            return (V2)var0.apply(var2);
         }
      };
   }

   static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final Maps.EntryTransformer<? super K, V1, V2> var0, @ParametricNullness final K var1) {
      Preconditions.checkNotNull(var0);
      return new Function<V1, V2>() {
         @ParametricNullness
         @Override
         public V2 apply(@ParametricNullness V1 var1x) {
            return (V2)var0.transformEntry(var1, var1x);
         }
      };
   }

   static <K, V1, V2> Function<Entry<K, V1>, V2> asEntryToValueFunction(final Maps.EntryTransformer<? super K, ? super V1, V2> var0) {
      Preconditions.checkNotNull(var0);
      return new Function<Entry<K, V1>, V2>() {
         @ParametricNullness
         public V2 apply(Entry<K, V1> var1) {
            return (V2)var0.transformEntry(var1.getKey(), var1.getValue());
         }
      };
   }

   static <V2, K, V1> Entry<K, V2> transformEntry(final Maps.EntryTransformer<? super K, ? super V1, V2> var0, final Entry<K, V1> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractMapEntry<K, V2>() {
         @ParametricNullness
         @Override
         public K getKey() {
            return (K)var1.getKey();
         }

         @ParametricNullness
         @Override
         public V2 getValue() {
            return (V2)var0.transformEntry(var1.getKey(), var1.getValue());
         }
      };
   }

   static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> asEntryToEntryFunction(final Maps.EntryTransformer<? super K, ? super V1, V2> var0) {
      Preconditions.checkNotNull(var0);
      return new Function<Entry<K, V1>, Entry<K, V2>>() {
         public Entry<K, V2> apply(Entry<K, V1> var1) {
            return Maps.transformEntry(var0, var1);
         }
      };
   }

   static <K> Predicate<Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> var0) {
      return Predicates.compose(var0, keyFunction());
   }

   static <V> Predicate<Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> var0) {
      return Predicates.compose(var0, valueFunction());
   }

   public static <K, V> Map<K, V> filterKeys(Map<K, V> var0, Predicate<? super K> var1) {
      Preconditions.checkNotNull(var1);
      Predicate var2 = keyPredicateOnEntries(var1);
      return (Map<K, V>)(var0 instanceof Maps.AbstractFilteredMap
         ? filterFiltered((Maps.AbstractFilteredMap<K, V>)var0, var2)
         : new Maps.FilteredKeyMap<>(Preconditions.checkNotNull(var0), var1, var2));
   }

   public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> var0, Predicate<? super K> var1) {
      return filterEntries(var0, keyPredicateOnEntries(var1));
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> var0, Predicate<? super K> var1) {
      return filterEntries(var0, keyPredicateOnEntries(var1));
   }

   public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> var0, Predicate<? super K> var1) {
      Preconditions.checkNotNull(var1);
      return filterEntries(var0, keyPredicateOnEntries(var1));
   }

   public static <K, V> Map<K, V> filterValues(Map<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, valuePredicateOnEntries(var1));
   }

   public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, valuePredicateOnEntries(var1));
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, valuePredicateOnEntries(var1));
   }

   public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> var0, Predicate<? super V> var1) {
      return filterEntries(var0, valuePredicateOnEntries(var1));
   }

   public static <K, V> Map<K, V> filterEntries(Map<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var1);
      return (Map<K, V>)(var0 instanceof Maps.AbstractFilteredMap
         ? filterFiltered((Maps.AbstractFilteredMap<K, V>)var0, var1)
         : new Maps.FilteredEntryMap<>(Preconditions.checkNotNull(var0), var1));
   }

   public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var1);
      return (SortedMap<K, V>)(var0 instanceof Maps.FilteredEntrySortedMap
         ? filterFiltered((Maps.FilteredEntrySortedMap<K, V>)var0, var1)
         : new Maps.FilteredEntrySortedMap<>(Preconditions.checkNotNull(var0), var1));
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var1);
      return (NavigableMap<K, V>)(var0 instanceof Maps.FilteredEntryNavigableMap
         ? filterFiltered((Maps.FilteredEntryNavigableMap<K, V>)var0, var1)
         : new Maps.FilteredEntryNavigableMap<>(Preconditions.checkNotNull(var0), var1));
   }

   public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return (BiMap<K, V>)(var0 instanceof Maps.FilteredEntryBiMap
         ? filterFiltered((Maps.FilteredEntryBiMap<K, V>)var0, var1)
         : new Maps.FilteredEntryBiMap<>(var0, var1));
   }

   private static <K, V> Map<K, V> filterFiltered(Maps.AbstractFilteredMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      return new Maps.FilteredEntryMap<>(var0.unfiltered, Predicates.and(var0.predicate, var1));
   }

   private static <K, V> SortedMap<K, V> filterFiltered(Maps.FilteredEntrySortedMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Predicate var2 = Predicates.and(var0.predicate, var1);
      return new Maps.FilteredEntrySortedMap<>(var0.sortedMap(), var2);
   }

   @GwtIncompatible
   private static <K, V> NavigableMap<K, V> filterFiltered(Maps.FilteredEntryNavigableMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Predicate var2 = Predicates.and(var0.entryPredicate, var1);
      return new Maps.FilteredEntryNavigableMap<>(var0.unfiltered, var2);
   }

   private static <K, V> BiMap<K, V> filterFiltered(Maps.FilteredEntryBiMap<K, V> var0, Predicate<? super Entry<K, V>> var1) {
      Predicate var2 = Predicates.and(var0.predicate, var1);
      return new Maps.FilteredEntryBiMap<>(var0.unfiltered(), var2);
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> var0) {
      Preconditions.checkNotNull(var0);
      return (NavigableMap<K, V>)(var0 instanceof Maps.UnmodifiableNavigableMap ? var0 : new Maps.UnmodifiableNavigableMap<>(var0));
   }

   private static <K, V> Entry<K, V> unmodifiableOrNull(Entry<K, ? extends V> var0) {
      return var0 == null ? null : unmodifiableEntry(var0);
   }

   @GwtIncompatible
   public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> var0) {
      return Synchronized.navigableMap(var0);
   }

   static <V> V safeGet(Map<?, V> var0, Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return (V)var0.get(var1);
      } catch (NullPointerException | ClassCastException var3) {
         return null;
      }
   }

   static boolean safeContainsKey(Map<?, ?> var0, Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return var0.containsKey(var1);
      } catch (NullPointerException | ClassCastException var3) {
         return false;
      }
   }

   static <V> V safeRemove(Map<?, V> var0, Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return (V)var0.remove(var1);
      } catch (NullPointerException | ClassCastException var3) {
         return null;
      }
   }

   static boolean containsKeyImpl(Map<?, ?> var0, Object var1) {
      return Iterators.contains(keyIterator(var0.entrySet().iterator()), var1);
   }

   static boolean containsValueImpl(Map<?, ?> var0, Object var1) {
      return Iterators.contains(valueIterator(var0.entrySet().iterator()), var1);
   }

   static <K, V> boolean containsEntryImpl(Collection<Entry<K, V>> var0, Object var1) {
      return !(var1 instanceof Entry) ? false : var0.contains(unmodifiableEntry((Entry<? extends K, ? extends V>)var1));
   }

   static <K, V> boolean removeEntryImpl(Collection<Entry<K, V>> var0, Object var1) {
      return !(var1 instanceof Entry) ? false : var0.remove(unmodifiableEntry((Entry<? extends K, ? extends V>)var1));
   }

   static boolean equalsImpl(Map<?, ?> var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var1 instanceof Map) {
         Map var2 = (Map)var1;
         return var0.entrySet().equals(var2.entrySet());
      } else {
         return false;
      }
   }

   static String toStringImpl(Map<?, ?> var0) {
      StringBuilder var1 = Collections2.newStringBuilderForCollection(var0.size()).append('{');
      boolean var2 = true;

      for (Entry var4 : var0.entrySet()) {
         if (!var2) {
            var1.append(", ");
         }

         var2 = false;
         var1.append(var4.getKey()).append('=').append(var4.getValue());
      }

      return var1.append('}').toString();
   }

   static <K, V> void putAllImpl(Map<K, V> var0, Map<? extends K, ? extends V> var1) {
      for (Entry var3 : var1.entrySet()) {
         var0.put(var3.getKey(), var3.getValue());
      }
   }

   static <K> K keyOrNull(Entry<K, ?> var0) {
      return (K)(var0 == null ? null : var0.getKey());
   }

   static <V> V valueOrNull(Entry<?, V> var0) {
      return (V)(var0 == null ? null : var0.getValue());
   }

   static <E> ImmutableMap<E, Integer> indexMap(Collection<E> var0) {
      ImmutableMap.Builder var1 = new ImmutableMap.Builder(var0.size());
      int var2 = 0;

      for (Object var4 : var0) {
         var1.put(var4, var2++);
      }

      return var1.build();
   }

   @Beta
   @GwtIncompatible
   public static <K extends Comparable<? super K>, V> NavigableMap<K, V> subMap(NavigableMap<K, V> var0, Range<K> var1) {
      if (var0.comparator() != null && var0.comparator() != Ordering.natural() && var1.hasLowerBound() && var1.hasUpperBound()) {
         Preconditions.checkArgument(
            var0.comparator().compare(var1.lowerEndpoint(), var1.upperEndpoint()) <= 0,
            "map is using a custom comparator which is inconsistent with the natural ordering."
         );
      }

      if (var1.hasLowerBound() && var1.hasUpperBound()) {
         return (NavigableMap<K, V>)var0.subMap(
            var1.lowerEndpoint(), var1.lowerBoundType() == BoundType.CLOSED, var1.upperEndpoint(), var1.upperBoundType() == BoundType.CLOSED
         );
      } else if (var1.hasLowerBound()) {
         return (NavigableMap<K, V>)var0.tailMap(var1.lowerEndpoint(), var1.lowerBoundType() == BoundType.CLOSED);
      } else {
         return (NavigableMap<K, V>)(var1.hasUpperBound()
            ? var0.headMap(var1.upperEndpoint(), var1.upperBoundType() == BoundType.CLOSED)
            : Preconditions.checkNotNull(var0));
      }
   }

   private abstract static class AbstractFilteredMap<K, V> extends Maps.ViewCachingAbstractMap<K, V> {
      final Map<K, V> unfiltered;
      final Predicate<? super Entry<K, V>> predicate;

      AbstractFilteredMap(Map<K, V> var1, Predicate<? super Entry<K, V>> var2) {
         this.unfiltered = var1;
         this.predicate = var2;
      }

      boolean apply(Object var1, @ParametricNullness V var2) {
         return this.predicate.apply(Maps.immutableEntry((K)var1, (V)var2));
      }

      @Override
      public V put(@ParametricNullness K var1, @ParametricNullness V var2) {
         Preconditions.checkArgument(this.apply(var1, (V)var2));
         return this.unfiltered.put((K)var1, (V)var2);
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         for (Entry var3 : var1.entrySet()) {
            Preconditions.checkArgument(this.apply(var3.getKey(), (V)var3.getValue()));
         }

         this.unfiltered.putAll(var1);
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.unfiltered.containsKey(var1) && this.apply(var1, this.unfiltered.get(var1));
      }

      @Override
      public V get(Object var1) {
         Object var2 = this.unfiltered.get(var1);
         return (V)(var2 != null && this.apply(var1, (V)var2) ? var2 : null);
      }

      @Override
      public boolean isEmpty() {
         return this.entrySet().isEmpty();
      }

      @Override
      public V remove(Object var1) {
         return this.containsKey(var1) ? this.unfiltered.remove(var1) : null;
      }

      @Override
      Collection<V> createValues() {
         return new Maps.FilteredMapValues<>(this, this.unfiltered, this.predicate);
      }
   }

   private static class AsMapView<K, V> extends Maps.ViewCachingAbstractMap<K, V> {
      private final Set<K> set;
      final Function<? super K, V> function;

      Set<K> backingSet() {
         return this.set;
      }

      AsMapView(Set<K> var1, Function<? super K, V> var2) {
         this.set = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public Set<K> createKeySet() {
         return Maps.removeOnlySet(this.backingSet());
      }

      @Override
      Collection<V> createValues() {
         return Collections2.transform(this.set, this.function);
      }

      @Override
      public int size() {
         return this.backingSet().size();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.backingSet().contains(var1);
      }

      @Override
      public V get(Object var1) {
         return this.getOrDefault(var1, null);
      }

      @Override
      public V getOrDefault(Object var1, V var2) {
         return (V)(Collections2.safeContains(this.backingSet(), var1) ? this.function.apply((K)var1) : var2);
      }

      @Override
      public V remove(Object var1) {
         return this.backingSet().remove(var1) ? this.function.apply((K)var1) : null;
      }

      @Override
      public void clear() {
         this.backingSet().clear();
      }

      @Override
      protected Set<Entry<K, V>> createEntrySet() {
         class 1EntrySetImpl extends Maps.EntrySet<K, V> {
            @Override
            Map<K, V> map() {
               return AsMapView.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
               return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
            }
         }

         return new 1EntrySetImpl();
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V> var1) {
         Preconditions.checkNotNull(var1);
         this.backingSet().forEach(var2 -> var1.accept(var2, this.function.apply(var2)));
      }
   }

   private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable {
      private final BiMap<A, B> bimap;
      private static final long serialVersionUID = 0L;

      BiMapConverter(BiMap<A, B> var1) {
         this.bimap = Preconditions.checkNotNull(var1);
      }

      @Override
      protected B doForward(A var1) {
         return convert(this.bimap, (A)var1);
      }

      @Override
      protected A doBackward(B var1) {
         return convert(this.bimap.inverse(), (B)var1);
      }

      private static <X, Y> Y convert(BiMap<X, Y> var0, X var1) {
         Object var2 = var0.get(var1);
         Preconditions.checkArgument(var2 != null, "No non-null mapping present for input: %s", var1);
         return (Y)var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Maps.BiMapConverter) {
            Maps.BiMapConverter var2 = (Maps.BiMapConverter)var1;
            return this.bimap.equals(var2.bimap);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.bimap.hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.bimap);
         return new StringBuilder(18 + String.valueOf(var1).length()).append("Maps.asConverter(").append(var1).append(")").toString();
      }
   }

   @GwtIncompatible
   abstract static class DescendingMap<K, V> extends ForwardingMap<K, V> implements NavigableMap<K, V> {
      private transient Comparator<? super K> comparator;
      private transient Set<Entry<K, V>> entrySet;
      private transient NavigableSet<K> navigableKeySet;

      abstract NavigableMap<K, V> forward();

      @Override
      protected final Map<K, V> delegate() {
         return this.forward();
      }

      @Override
      public Comparator<? super K> comparator() {
         Object var1 = this.comparator;
         if (var1 == null) {
            Object var2 = this.forward().comparator();
            if (var2 == null) {
               var2 = Ordering.natural();
            }

            var1 = this.comparator = reverse((Comparator<? super K>)var2);
         }

         return (Comparator<? super K>)var1;
      }

      private static <T> Ordering<T> reverse(Comparator<T> var0) {
         return Ordering.from(var0).reverse();
      }

      @ParametricNullness
      @Override
      public K firstKey() {
         return this.forward().lastKey();
      }

      @ParametricNullness
      @Override
      public K lastKey() {
         return this.forward().firstKey();
      }

      @Override
      public Entry<K, V> lowerEntry(@ParametricNullness K var1) {
         return this.forward().higherEntry((K)var1);
      }

      @Override
      public K lowerKey(@ParametricNullness K var1) {
         return this.forward().higherKey((K)var1);
      }

      @Override
      public Entry<K, V> floorEntry(@ParametricNullness K var1) {
         return this.forward().ceilingEntry((K)var1);
      }

      @Override
      public K floorKey(@ParametricNullness K var1) {
         return this.forward().ceilingKey((K)var1);
      }

      @Override
      public Entry<K, V> ceilingEntry(@ParametricNullness K var1) {
         return this.forward().floorEntry((K)var1);
      }

      @Override
      public K ceilingKey(@ParametricNullness K var1) {
         return this.forward().floorKey((K)var1);
      }

      @Override
      public Entry<K, V> higherEntry(@ParametricNullness K var1) {
         return this.forward().lowerEntry((K)var1);
      }

      @Override
      public K higherKey(@ParametricNullness K var1) {
         return this.forward().lowerKey((K)var1);
      }

      @Override
      public Entry<K, V> firstEntry() {
         return this.forward().lastEntry();
      }

      @Override
      public Entry<K, V> lastEntry() {
         return this.forward().firstEntry();
      }

      @Override
      public Entry<K, V> pollFirstEntry() {
         return this.forward().pollLastEntry();
      }

      @Override
      public Entry<K, V> pollLastEntry() {
         return this.forward().pollFirstEntry();
      }

      @Override
      public NavigableMap<K, V> descendingMap() {
         return this.forward();
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         Set var1 = this.entrySet;
         return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
      }

      abstract Iterator<Entry<K, V>> entryIterator();

      Set<Entry<K, V>> createEntrySet() {
         class 1EntrySetImpl extends Maps.EntrySet<K, V> {
            @Override
            Map<K, V> map() {
               return DescendingMap.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
               return DescendingMap.this.entryIterator();
            }
         }

         return new 1EntrySetImpl();
      }

      @Override
      public Set<K> keySet() {
         return this.navigableKeySet();
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         NavigableSet var1 = this.navigableKeySet;
         return var1 == null ? (this.navigableKeySet = new Maps.NavigableKeySet<>(this)) : var1;
      }

      @Override
      public NavigableSet<K> descendingKeySet() {
         return this.forward().navigableKeySet();
      }

      @Override
      public NavigableMap<K, V> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return this.forward().subMap((K)var3, var4, (K)var1, var2).descendingMap();
      }

      @Override
      public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return this.subMap((K)var1, true, (K)var2, false);
      }

      @Override
      public NavigableMap<K, V> headMap(@ParametricNullness K var1, boolean var2) {
         return this.forward().tailMap((K)var1, var2).descendingMap();
      }

      @Override
      public SortedMap<K, V> headMap(@ParametricNullness K var1) {
         return this.headMap((K)var1, false);
      }

      @Override
      public NavigableMap<K, V> tailMap(@ParametricNullness K var1, boolean var2) {
         return this.forward().headMap((K)var1, var2).descendingMap();
      }

      @Override
      public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
         return this.tailMap((K)var1, true);
      }

      @Override
      public Collection<V> values() {
         return new Maps.Values<>(this);
      }

      @Override
      public String toString() {
         return this.standardToString();
      }
   }

   private static enum EntryFunction implements Function<Entry<?, ?>, Object> {
      KEY {
         public Object apply(Entry<?, ?> var1) {
            return var1.getKey();
         }
      },
      VALUE {
         public Object apply(Entry<?, ?> var1) {
            return var1.getValue();
         }
      };

      private EntryFunction() {
      }
   }

   abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Entry<K, V>> {
      abstract Map<K, V> map();

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public void clear() {
         this.map().clear();
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            Entry var2 = (Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = Maps.safeGet(this.map(), var3);
            return com.google.common.base.Objects.equal(var4, var2.getValue()) && (var4 != null || this.map().containsKey(var3));
         }
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean remove(Object var1) {
         if (this.contains(var1) && var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.map().keySet().remove(var2.getKey());
         } else {
            return false;
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         try {
            return super.removeAll(Preconditions.checkNotNull(var1));
         } catch (UnsupportedOperationException var3) {
            return Sets.removeAllImpl(this, var1.iterator());
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         try {
            return super.retainAll(Preconditions.checkNotNull(var1));
         } catch (UnsupportedOperationException var7) {
            HashSet var3 = Sets.newHashSetWithExpectedSize(var1.size());

            for (Object var5 : var1) {
               if (this.contains(var5) && var5 instanceof Entry) {
                  Entry var6 = (Entry)var5;
                  var3.add(var6.getKey());
               }
            }

            return this.map().keySet().retainAll(var3);
         }
      }
   }

   @FunctionalInterface
   public interface EntryTransformer<K, V1, V2> {
      V2 transformEntry(@ParametricNullness K var1, @ParametricNullness V1 var2);
   }

   static final class FilteredEntryBiMap<K, V> extends Maps.FilteredEntryMap<K, V> implements BiMap<K, V> {
      @RetainedWith
      private final BiMap<V, K> inverse;

      private static <K, V> Predicate<Entry<V, K>> inversePredicate(final Predicate<? super Entry<K, V>> var0) {
         return new Predicate<Entry<V, K>>() {
            public boolean apply(Entry<V, K> var1) {
               return var0.apply(Maps.immutableEntry(var1.getValue(), var1.getKey()));
            }
         };
      }

      FilteredEntryBiMap(BiMap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
         super(var1, var2);
         this.inverse = new Maps.FilteredEntryBiMap<>(var1.inverse(), inversePredicate(var2), this);
      }

      private FilteredEntryBiMap(BiMap<K, V> var1, Predicate<? super Entry<K, V>> var2, BiMap<V, K> var3) {
         super(var1, var2);
         this.inverse = var3;
      }

      BiMap<K, V> unfiltered() {
         return (BiMap<K, V>)this.unfiltered;
      }

      @Override
      public V forcePut(@ParametricNullness K var1, @ParametricNullness V var2) {
         Preconditions.checkArgument(this.apply(var1, (V)var2));
         return this.unfiltered().forcePut((K)var1, (V)var2);
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         this.unfiltered().replaceAll((var2, var3) -> this.apply(var2, (V)var3) ? (V)var1.apply(var2, var3) : var3);
      }

      @Override
      public BiMap<V, K> inverse() {
         return this.inverse;
      }

      @Override
      public Set<V> values() {
         return this.inverse.keySet();
      }
   }

   static class FilteredEntryMap<K, V> extends Maps.AbstractFilteredMap<K, V> {
      final Set<Entry<K, V>> filteredEntrySet;

      FilteredEntryMap(Map<K, V> var1, Predicate<? super Entry<K, V>> var2) {
         super(var1, var2);
         this.filteredEntrySet = Sets.filter(var1.entrySet(), this.predicate);
      }

      @Override
      protected Set<Entry<K, V>> createEntrySet() {
         return new Maps.FilteredEntryMap.EntrySet();
      }

      @Override
      Set<K> createKeySet() {
         return new Maps.FilteredEntryMap.KeySet();
      }

      static <K, V> boolean removeAllKeys(Map<K, V> var0, Predicate<? super Entry<K, V>> var1, Collection<?> var2) {
         Iterator var3 = var0.entrySet().iterator();
         boolean var4 = false;

         while (var3.hasNext()) {
            Entry var5 = (Entry)var3.next();
            if (var1.apply(var5) && var2.contains(var5.getKey())) {
               var3.remove();
               var4 = true;
            }
         }

         return var4;
      }

      static <K, V> boolean retainAllKeys(Map<K, V> var0, Predicate<? super Entry<K, V>> var1, Collection<?> var2) {
         Iterator var3 = var0.entrySet().iterator();
         boolean var4 = false;

         while (var3.hasNext()) {
            Entry var5 = (Entry)var3.next();
            if (var1.apply(var5) && !var2.contains(var5.getKey())) {
               var3.remove();
               var4 = true;
            }
         }

         return var4;
      }

      private class EntrySet extends ForwardingSet<Entry<K, V>> {
         private EntrySet() {
         }

         @Override
         protected Set<Entry<K, V>> delegate() {
            return FilteredEntryMap.this.filteredEntrySet;
         }

         @Override
         public Iterator<Entry<K, V>> iterator() {
            return new TransformedIterator<Entry<K, V>, Entry<K, V>>(FilteredEntryMap.this.filteredEntrySet.iterator()) {
               Entry<K, V> transform(final Entry<K, V> var1) {
                  return new ForwardingMapEntry<K, V>() {
                     @Override
                     protected Entry<K, V> delegate() {
                        return var1;
                     }

                     @ParametricNullness
                     @Override
                     public V setValue(@ParametricNullness V var1x) {
                        Preconditions.checkArgument(FilteredEntryMap.this.apply(this.getKey(), (V)var1x));
                        return (V)super.setValue(var1x);
                     }
                  };
               }
            };
         }
      }

      class KeySet extends Maps.KeySet<K, V> {
         KeySet() {
            super(FilteredEntryMap.this);
         }

         @Override
         public boolean remove(Object var1) {
            if (FilteredEntryMap.this.containsKey(var1)) {
               FilteredEntryMap.this.unfiltered.remove(var1);
               return true;
            } else {
               return false;
            }
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            return Maps.FilteredEntryMap.removeAllKeys(FilteredEntryMap.this.unfiltered, FilteredEntryMap.this.predicate, var1);
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            return Maps.FilteredEntryMap.retainAllKeys(FilteredEntryMap.this.unfiltered, FilteredEntryMap.this.predicate, var1);
         }

         @Override
         public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])Lists.newArrayList(this.iterator()).toArray(var1);
         }
      }
   }

   @GwtIncompatible
   private static class FilteredEntryNavigableMap<K, V> extends AbstractNavigableMap<K, V> {
      private final NavigableMap<K, V> unfiltered;
      private final Predicate<? super Entry<K, V>> entryPredicate;
      private final Map<K, V> filteredDelegate;

      FilteredEntryNavigableMap(NavigableMap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
         this.unfiltered = Preconditions.checkNotNull(var1);
         this.entryPredicate = var2;
         this.filteredDelegate = new Maps.FilteredEntryMap<>(var1, var2);
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.unfiltered.comparator();
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         return new Maps.NavigableKeySet<K, V>(this) {
            @Override
            public boolean removeAll(Collection<?> var1) {
               return Maps.FilteredEntryMap.removeAllKeys(FilteredEntryNavigableMap.this.unfiltered, FilteredEntryNavigableMap.this.entryPredicate, var1);
            }

            @Override
            public boolean retainAll(Collection<?> var1) {
               return Maps.FilteredEntryMap.retainAllKeys(FilteredEntryNavigableMap.this.unfiltered, FilteredEntryNavigableMap.this.entryPredicate, var1);
            }
         };
      }

      @Override
      public Collection<V> values() {
         return new Maps.FilteredMapValues<>(this, this.unfiltered, this.entryPredicate);
      }

      @Override
      Iterator<Entry<K, V>> entryIterator() {
         return Iterators.filter(this.unfiltered.entrySet().iterator(), this.entryPredicate);
      }

      @Override
      Iterator<Entry<K, V>> descendingEntryIterator() {
         return Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), this.entryPredicate);
      }

      @Override
      public int size() {
         return this.filteredDelegate.size();
      }

      @Override
      public boolean isEmpty() {
         return !Iterables.any(this.unfiltered.entrySet(), this.entryPredicate);
      }

      @Override
      public V get(Object var1) {
         return this.filteredDelegate.get(var1);
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.filteredDelegate.containsKey(var1);
      }

      @Override
      public V put(@ParametricNullness K var1, @ParametricNullness V var2) {
         return this.filteredDelegate.put((K)var1, (V)var2);
      }

      @Override
      public V remove(Object var1) {
         return this.filteredDelegate.remove(var1);
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         this.filteredDelegate.putAll(var1);
      }

      @Override
      public void clear() {
         this.filteredDelegate.clear();
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return this.filteredDelegate.entrySet();
      }

      @Override
      public Entry<K, V> pollFirstEntry() {
         return Iterables.removeFirstMatching(this.unfiltered.entrySet(), this.entryPredicate);
      }

      @Override
      public Entry<K, V> pollLastEntry() {
         return Iterables.removeFirstMatching(this.unfiltered.descendingMap().entrySet(), this.entryPredicate);
      }

      @Override
      public NavigableMap<K, V> descendingMap() {
         return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
      }

      @Override
      public NavigableMap<K, V> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return Maps.filterEntries(this.unfiltered.subMap((K)var1, var2, (K)var3, var4), this.entryPredicate);
      }

      @Override
      public NavigableMap<K, V> headMap(@ParametricNullness K var1, boolean var2) {
         return Maps.filterEntries(this.unfiltered.headMap((K)var1, var2), this.entryPredicate);
      }

      @Override
      public NavigableMap<K, V> tailMap(@ParametricNullness K var1, boolean var2) {
         return Maps.filterEntries(this.unfiltered.tailMap((K)var1, var2), this.entryPredicate);
      }
   }

   private static class FilteredEntrySortedMap<K, V> extends Maps.FilteredEntryMap<K, V> implements SortedMap<K, V> {
      FilteredEntrySortedMap(SortedMap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
         super(var1, var2);
      }

      SortedMap<K, V> sortedMap() {
         return (SortedMap<K, V>)this.unfiltered;
      }

      public SortedSet<K> keySet() {
         return (SortedSet<K>)super.keySet();
      }

      SortedSet<K> createKeySet() {
         return new Maps.FilteredEntrySortedMap.SortedKeySet();
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap().comparator();
      }

      @ParametricNullness
      @Override
      public K firstKey() {
         return this.keySet().iterator().next();
      }

      @ParametricNullness
      @Override
      public K lastKey() {
         SortedMap var1 = this.sortedMap();

         while (true) {
            Object var2 = var1.lastKey();
            if (this.apply(var2, NullnessCasts.uncheckedCastNullableTToT(this.unfiltered.get(var2)))) {
               return (K)var2;
            }

            var1 = this.sortedMap().headMap((K)var2);
         }
      }

      @Override
      public SortedMap<K, V> headMap(@ParametricNullness K var1) {
         return new Maps.FilteredEntrySortedMap<>(this.sortedMap().headMap((K)var1), this.predicate);
      }

      @Override
      public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return new Maps.FilteredEntrySortedMap<>(this.sortedMap().subMap((K)var1, (K)var2), this.predicate);
      }

      @Override
      public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
         return new Maps.FilteredEntrySortedMap<>(this.sortedMap().tailMap((K)var1), this.predicate);
      }

      class SortedKeySet extends Maps.FilteredEntryMap<K, V>.KeySet implements SortedSet<K> {
         @Override
         public Comparator<? super K> comparator() {
            return FilteredEntrySortedMap.this.sortedMap().comparator();
         }

         @Override
         public SortedSet<K> subSet(@ParametricNullness K var1, @ParametricNullness K var2) {
            return (SortedSet<K>)FilteredEntrySortedMap.this.subMap((K)var1, (K)var2).keySet();
         }

         @Override
         public SortedSet<K> headSet(@ParametricNullness K var1) {
            return (SortedSet<K>)FilteredEntrySortedMap.this.headMap((K)var1).keySet();
         }

         @Override
         public SortedSet<K> tailSet(@ParametricNullness K var1) {
            return (SortedSet<K>)FilteredEntrySortedMap.this.tailMap((K)var1).keySet();
         }

         @ParametricNullness
         @Override
         public K first() {
            return FilteredEntrySortedMap.this.firstKey();
         }

         @ParametricNullness
         @Override
         public K last() {
            return FilteredEntrySortedMap.this.lastKey();
         }
      }
   }

   private static class FilteredKeyMap<K, V> extends Maps.AbstractFilteredMap<K, V> {
      final Predicate<? super K> keyPredicate;

      FilteredKeyMap(Map<K, V> var1, Predicate<? super K> var2, Predicate<? super Entry<K, V>> var3) {
         super(var1, var3);
         this.keyPredicate = var2;
      }

      @Override
      protected Set<Entry<K, V>> createEntrySet() {
         return Sets.filter(this.unfiltered.entrySet(), this.predicate);
      }

      @Override
      Set<K> createKeySet() {
         return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.unfiltered.containsKey(var1) && this.keyPredicate.apply((K)var1);
      }
   }

   private static final class FilteredMapValues<K, V> extends Maps.Values<K, V> {
      final Map<K, V> unfiltered;
      final Predicate<? super Entry<K, V>> predicate;

      FilteredMapValues(Map<K, V> var1, Map<K, V> var2, Predicate<? super Entry<K, V>> var3) {
         super(var1);
         this.unfiltered = var2;
         this.predicate = var3;
      }

      @Override
      public boolean remove(Object var1) {
         Iterator var2 = this.unfiltered.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            if (this.predicate.apply(var3) && com.google.common.base.Objects.equal(var3.getValue(), var1)) {
               var2.remove();
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         Iterator var2 = this.unfiltered.entrySet().iterator();
         boolean var3 = false;

         while (var2.hasNext()) {
            Entry var4 = (Entry)var2.next();
            if (this.predicate.apply(var4) && var1.contains(var4.getValue())) {
               var2.remove();
               var3 = true;
            }
         }

         return var3;
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         Iterator var2 = this.unfiltered.entrySet().iterator();
         boolean var3 = false;

         while (var2.hasNext()) {
            Entry var4 = (Entry)var2.next();
            if (this.predicate.apply(var4) && !var1.contains(var4.getValue())) {
               var2.remove();
               var3 = true;
            }
         }

         return var3;
      }

      @Override
      public Object[] toArray() {
         return Lists.newArrayList(this.iterator()).toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])Lists.newArrayList(this.iterator()).toArray(var1);
      }
   }

   abstract static class IteratorBasedAbstractMap<K, V> extends AbstractMap<K, V> {
      @Override
      public abstract int size();

      abstract Iterator<Entry<K, V>> entryIterator();

      Spliterator<Entry<K, V>> entrySpliterator() {
         return Spliterators.spliterator(this.entryIterator(), (long)this.size(), 65);
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return new Maps.EntrySet<K, V>() {
            @Override
            Map<K, V> map() {
               return IteratorBasedAbstractMap.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
               return IteratorBasedAbstractMap.this.entryIterator();
            }

            @Override
            public Spliterator<Entry<K, V>> spliterator() {
               return IteratorBasedAbstractMap.this.entrySpliterator();
            }

            @Override
            public void forEach(Consumer<? super Entry<K, V>> var1) {
               IteratorBasedAbstractMap.this.forEachEntry(var1);
            }
         };
      }

      void forEachEntry(Consumer<? super Entry<K, V>> var1) {
         this.entryIterator().forEachRemaining(var1);
      }

      @Override
      public void clear() {
         Iterators.clear(this.entryIterator());
      }
   }

   static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
      @Weak
      final Map<K, V> map;

      KeySet(Map<K, V> var1) {
         this.map = Preconditions.checkNotNull(var1);
      }

      Map<K, V> map() {
         return this.map;
      }

      @Override
      public Iterator<K> iterator() {
         return Maps.keyIterator(this.map().entrySet().iterator());
      }

      @Override
      public void forEach(Consumer<? super K> var1) {
         Preconditions.checkNotNull(var1);
         this.map.forEach((var1x, var2) -> var1.accept(var1x));
      }

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.map().containsKey(var1);
      }

      @Override
      public boolean remove(Object var1) {
         if (this.contains(var1)) {
            this.map().remove(var1);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         this.map().clear();
      }
   }

   static class MapDifferenceImpl<K, V> implements MapDifference<K, V> {
      final Map<K, V> onlyOnLeft;
      final Map<K, V> onlyOnRight;
      final Map<K, V> onBoth;
      final Map<K, MapDifference.ValueDifference<V>> differences;

      MapDifferenceImpl(Map<K, V> var1, Map<K, V> var2, Map<K, V> var3, Map<K, MapDifference.ValueDifference<V>> var4) {
         this.onlyOnLeft = Maps.unmodifiableMap(var1);
         this.onlyOnRight = Maps.unmodifiableMap(var2);
         this.onBoth = Maps.unmodifiableMap(var3);
         this.differences = Maps.unmodifiableMap(var4);
      }

      @Override
      public boolean areEqual() {
         return this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty();
      }

      @Override
      public Map<K, V> entriesOnlyOnLeft() {
         return this.onlyOnLeft;
      }

      @Override
      public Map<K, V> entriesOnlyOnRight() {
         return this.onlyOnRight;
      }

      @Override
      public Map<K, V> entriesInCommon() {
         return this.onBoth;
      }

      @Override
      public Map<K, MapDifference.ValueDifference<V>> entriesDiffering() {
         return this.differences;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof MapDifference)) {
            return false;
         } else {
            MapDifference var2 = (MapDifference)var1;
            return this.entriesOnlyOnLeft().equals(var2.entriesOnlyOnLeft())
               && this.entriesOnlyOnRight().equals(var2.entriesOnlyOnRight())
               && this.entriesInCommon().equals(var2.entriesInCommon())
               && this.entriesDiffering().equals(var2.entriesDiffering());
         }
      }

      @Override
      public int hashCode() {
         return com.google.common.base.Objects.hashCode(this.entriesOnlyOnLeft(), this.entriesOnlyOnRight(), this.entriesInCommon(), this.entriesDiffering());
      }

      @Override
      public String toString() {
         if (this.areEqual()) {
            return "equal";
         } else {
            StringBuilder var1 = new StringBuilder("not equal");
            if (!this.onlyOnLeft.isEmpty()) {
               var1.append(": only on left=").append(this.onlyOnLeft);
            }

            if (!this.onlyOnRight.isEmpty()) {
               var1.append(": only on right=").append(this.onlyOnRight);
            }

            if (!this.differences.isEmpty()) {
               var1.append(": value differences=").append(this.differences);
            }

            return var1.toString();
         }
      }
   }

   @GwtIncompatible
   private static final class NavigableAsMapView<K, V> extends AbstractNavigableMap<K, V> {
      private final NavigableSet<K> set;
      private final Function<? super K, V> function;

      NavigableAsMapView(NavigableSet<K> var1, Function<? super K, V> var2) {
         this.set = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public NavigableMap<K, V> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return Maps.asMap(this.set.subSet((K)var1, var2, (K)var3, var4), this.function);
      }

      @Override
      public NavigableMap<K, V> headMap(@ParametricNullness K var1, boolean var2) {
         return Maps.asMap(this.set.headSet((K)var1, var2), this.function);
      }

      @Override
      public NavigableMap<K, V> tailMap(@ParametricNullness K var1, boolean var2) {
         return Maps.asMap(this.set.tailSet((K)var1, var2), this.function);
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.set.comparator();
      }

      @Override
      public V get(Object var1) {
         return this.getOrDefault(var1, null);
      }

      @Override
      public V getOrDefault(Object var1, V var2) {
         return (V)(Collections2.safeContains(this.set, var1) ? this.function.apply((K)var1) : var2);
      }

      @Override
      public void clear() {
         this.set.clear();
      }

      @Override
      Iterator<Entry<K, V>> entryIterator() {
         return Maps.asMapEntryIterator(this.set, this.function);
      }

      @Override
      Spliterator<Entry<K, V>> entrySpliterator() {
         return CollectSpliterators.map(this.set.spliterator(), var1 -> Maps.immutableEntry((K)var1, this.function.apply(var1)));
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V> var1) {
         this.set.forEach(var2 -> var1.accept(var2, this.function.apply(var2)));
      }

      @Override
      Iterator<Entry<K, V>> descendingEntryIterator() {
         return this.descendingMap().entrySet().iterator();
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         return Maps.removeOnlyNavigableSet(this.set);
      }

      @Override
      public int size() {
         return this.set.size();
      }

      @Override
      public NavigableMap<K, V> descendingMap() {
         return Maps.asMap(this.set.descendingSet(), this.function);
      }
   }

   @GwtIncompatible
   static class NavigableKeySet<K, V> extends Maps.SortedKeySet<K, V> implements NavigableSet<K> {
      NavigableKeySet(NavigableMap<K, V> var1) {
         super(var1);
      }

      NavigableMap<K, V> map() {
         return (NavigableMap<K, V>)this.map;
      }

      @Override
      public K lower(@ParametricNullness K var1) {
         return this.map().lowerKey((K)var1);
      }

      @Override
      public K floor(@ParametricNullness K var1) {
         return this.map().floorKey((K)var1);
      }

      @Override
      public K ceiling(@ParametricNullness K var1) {
         return this.map().ceilingKey((K)var1);
      }

      @Override
      public K higher(@ParametricNullness K var1) {
         return this.map().higherKey((K)var1);
      }

      @Override
      public K pollFirst() {
         return Maps.keyOrNull(this.map().pollFirstEntry());
      }

      @Override
      public K pollLast() {
         return Maps.keyOrNull(this.map().pollLastEntry());
      }

      @Override
      public NavigableSet<K> descendingSet() {
         return this.map().descendingKeySet();
      }

      @Override
      public Iterator<K> descendingIterator() {
         return this.descendingSet().iterator();
      }

      @Override
      public NavigableSet<K> subSet(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return this.map().subMap((K)var1, var2, (K)var3, var4).navigableKeySet();
      }

      @Override
      public SortedSet<K> subSet(@ParametricNullness K var1, @ParametricNullness K var2) {
         return this.subSet((K)var1, true, (K)var2, false);
      }

      @Override
      public NavigableSet<K> headSet(@ParametricNullness K var1, boolean var2) {
         return this.map().headMap((K)var1, var2).navigableKeySet();
      }

      @Override
      public SortedSet<K> headSet(@ParametricNullness K var1) {
         return this.headSet((K)var1, false);
      }

      @Override
      public NavigableSet<K> tailSet(@ParametricNullness K var1, boolean var2) {
         return this.map().tailMap((K)var1, var2).navigableKeySet();
      }

      @Override
      public SortedSet<K> tailSet(@ParametricNullness K var1) {
         return this.tailSet((K)var1, true);
      }
   }

   private static class SortedAsMapView<K, V> extends Maps.AsMapView<K, V> implements SortedMap<K, V> {
      SortedAsMapView(SortedSet<K> var1, Function<? super K, V> var2) {
         super(var1, var2);
      }

      SortedSet<K> backingSet() {
         return (SortedSet<K>)super.backingSet();
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.backingSet().comparator();
      }

      @Override
      public Set<K> keySet() {
         return Maps.removeOnlySortedSet(this.backingSet());
      }

      @Override
      public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return Maps.asMap(this.backingSet().subSet((K)var1, (K)var2), this.function);
      }

      @Override
      public SortedMap<K, V> headMap(@ParametricNullness K var1) {
         return Maps.asMap(this.backingSet().headSet((K)var1), this.function);
      }

      @Override
      public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
         return Maps.asMap(this.backingSet().tailSet((K)var1), this.function);
      }

      @ParametricNullness
      @Override
      public K firstKey() {
         return this.backingSet().first();
      }

      @ParametricNullness
      @Override
      public K lastKey() {
         return this.backingSet().last();
      }
   }

   static class SortedKeySet<K, V> extends Maps.KeySet<K, V> implements SortedSet<K> {
      SortedKeySet(SortedMap<K, V> var1) {
         super(var1);
      }

      SortedMap<K, V> map() {
         return (SortedMap<K, V>)super.map();
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.map().comparator();
      }

      @Override
      public SortedSet<K> subSet(@ParametricNullness K var1, @ParametricNullness K var2) {
         return new Maps.SortedKeySet<>(this.map().subMap((K)var1, (K)var2));
      }

      @Override
      public SortedSet<K> headSet(@ParametricNullness K var1) {
         return new Maps.SortedKeySet<>(this.map().headMap((K)var1));
      }

      @Override
      public SortedSet<K> tailSet(@ParametricNullness K var1) {
         return new Maps.SortedKeySet<>(this.map().tailMap((K)var1));
      }

      @ParametricNullness
      @Override
      public K first() {
         return this.map().firstKey();
      }

      @ParametricNullness
      @Override
      public K last() {
         return this.map().lastKey();
      }
   }

   static class SortedMapDifferenceImpl<K, V> extends Maps.MapDifferenceImpl<K, V> implements SortedMapDifference<K, V> {
      SortedMapDifferenceImpl(SortedMap<K, V> var1, SortedMap<K, V> var2, SortedMap<K, V> var3, SortedMap<K, MapDifference.ValueDifference<V>> var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      public SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering() {
         return (SortedMap<K, MapDifference.ValueDifference<V>>)super.entriesDiffering();
      }

      @Override
      public SortedMap<K, V> entriesInCommon() {
         return (SortedMap<K, V>)super.entriesInCommon();
      }

      @Override
      public SortedMap<K, V> entriesOnlyOnLeft() {
         return (SortedMap<K, V>)super.entriesOnlyOnLeft();
      }

      @Override
      public SortedMap<K, V> entriesOnlyOnRight() {
         return (SortedMap<K, V>)super.entriesOnlyOnRight();
      }
   }

   static class TransformedEntriesMap<K, V1, V2> extends Maps.IteratorBasedAbstractMap<K, V2> {
      final Map<K, V1> fromMap;
      final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;

      TransformedEntriesMap(Map<K, V1> var1, Maps.EntryTransformer<? super K, ? super V1, V2> var2) {
         this.fromMap = Preconditions.checkNotNull(var1);
         this.transformer = Preconditions.checkNotNull(var2);
      }

      @Override
      public int size() {
         return this.fromMap.size();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.fromMap.containsKey(var1);
      }

      @Override
      public V2 get(Object var1) {
         return this.getOrDefault(var1, null);
      }

      @Override
      public V2 getOrDefault(Object var1, V2 var2) {
         Object var3 = this.fromMap.get(var1);
         return (V2)(var3 == null && !this.fromMap.containsKey(var1)
            ? var2
            : this.transformer.transformEntry((K)var1, NullnessCasts.uncheckedCastNullableTToT((V1)var3)));
      }

      @Override
      public V2 remove(Object var1) {
         return this.fromMap.containsKey(var1)
            ? this.transformer.transformEntry((K)var1, NullnessCasts.uncheckedCastNullableTToT(this.fromMap.remove(var1)))
            : null;
      }

      @Override
      public void clear() {
         this.fromMap.clear();
      }

      @Override
      public Set<K> keySet() {
         return this.fromMap.keySet();
      }

      @Override
      Iterator<Entry<K, V2>> entryIterator() {
         return Iterators.transform(this.fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(this.transformer));
      }

      @Override
      Spliterator<Entry<K, V2>> entrySpliterator() {
         return CollectSpliterators.map(this.fromMap.entrySet().spliterator(), Maps.asEntryToEntryFunction(this.transformer));
      }

      @Override
      public void forEach(BiConsumer<? super K, ? super V2> var1) {
         Preconditions.checkNotNull(var1);
         this.fromMap.forEach((var2, var3) -> var1.accept(var2, this.transformer.transformEntry(var2, var3)));
      }

      @Override
      public Collection<V2> values() {
         return new Maps.Values<>(this);
      }
   }

   @GwtIncompatible
   private static class TransformedEntriesNavigableMap<K, V1, V2> extends Maps.TransformedEntriesSortedMap<K, V1, V2> implements NavigableMap<K, V2> {
      TransformedEntriesNavigableMap(NavigableMap<K, V1> var1, Maps.EntryTransformer<? super K, ? super V1, V2> var2) {
         super(var1, var2);
      }

      @Override
      public Entry<K, V2> ceilingEntry(@ParametricNullness K var1) {
         return this.transformEntry(this.fromMap().ceilingEntry((K)var1));
      }

      @Override
      public K ceilingKey(@ParametricNullness K var1) {
         return this.fromMap().ceilingKey((K)var1);
      }

      @Override
      public NavigableSet<K> descendingKeySet() {
         return this.fromMap().descendingKeySet();
      }

      @Override
      public NavigableMap<K, V2> descendingMap() {
         return Maps.transformEntries(this.fromMap().descendingMap(), this.transformer);
      }

      @Override
      public Entry<K, V2> firstEntry() {
         return this.transformEntry(this.fromMap().firstEntry());
      }

      @Override
      public Entry<K, V2> floorEntry(@ParametricNullness K var1) {
         return this.transformEntry(this.fromMap().floorEntry((K)var1));
      }

      @Override
      public K floorKey(@ParametricNullness K var1) {
         return this.fromMap().floorKey((K)var1);
      }

      public NavigableMap<K, V2> headMap(@ParametricNullness K var1) {
         return this.headMap((K)var1, false);
      }

      @Override
      public NavigableMap<K, V2> headMap(@ParametricNullness K var1, boolean var2) {
         return Maps.transformEntries(this.fromMap().headMap((K)var1, var2), this.transformer);
      }

      @Override
      public Entry<K, V2> higherEntry(@ParametricNullness K var1) {
         return this.transformEntry(this.fromMap().higherEntry((K)var1));
      }

      @Override
      public K higherKey(@ParametricNullness K var1) {
         return this.fromMap().higherKey((K)var1);
      }

      @Override
      public Entry<K, V2> lastEntry() {
         return this.transformEntry(this.fromMap().lastEntry());
      }

      @Override
      public Entry<K, V2> lowerEntry(@ParametricNullness K var1) {
         return this.transformEntry(this.fromMap().lowerEntry((K)var1));
      }

      @Override
      public K lowerKey(@ParametricNullness K var1) {
         return this.fromMap().lowerKey((K)var1);
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         return this.fromMap().navigableKeySet();
      }

      @Override
      public Entry<K, V2> pollFirstEntry() {
         return this.transformEntry(this.fromMap().pollFirstEntry());
      }

      @Override
      public Entry<K, V2> pollLastEntry() {
         return this.transformEntry(this.fromMap().pollLastEntry());
      }

      @Override
      public NavigableMap<K, V2> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return Maps.transformEntries(this.fromMap().subMap((K)var1, var2, (K)var3, var4), this.transformer);
      }

      public NavigableMap<K, V2> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return this.subMap((K)var1, true, (K)var2, false);
      }

      public NavigableMap<K, V2> tailMap(@ParametricNullness K var1) {
         return this.tailMap((K)var1, true);
      }

      @Override
      public NavigableMap<K, V2> tailMap(@ParametricNullness K var1, boolean var2) {
         return Maps.transformEntries(this.fromMap().tailMap((K)var1, var2), this.transformer);
      }

      private Entry<K, V2> transformEntry(Entry<K, V1> var1) {
         return var1 == null ? null : Maps.transformEntry(this.transformer, var1);
      }

      protected NavigableMap<K, V1> fromMap() {
         return (NavigableMap<K, V1>)super.fromMap();
      }
   }

   static class TransformedEntriesSortedMap<K, V1, V2> extends Maps.TransformedEntriesMap<K, V1, V2> implements SortedMap<K, V2> {
      protected SortedMap<K, V1> fromMap() {
         return (SortedMap<K, V1>)this.fromMap;
      }

      TransformedEntriesSortedMap(SortedMap<K, V1> var1, Maps.EntryTransformer<? super K, ? super V1, V2> var2) {
         super(var1, var2);
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.fromMap().comparator();
      }

      @ParametricNullness
      @Override
      public K firstKey() {
         return this.fromMap().firstKey();
      }

      @Override
      public SortedMap<K, V2> headMap(@ParametricNullness K var1) {
         return Maps.transformEntries(this.fromMap().headMap((K)var1), this.transformer);
      }

      @ParametricNullness
      @Override
      public K lastKey() {
         return this.fromMap().lastKey();
      }

      @Override
      public SortedMap<K, V2> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return Maps.transformEntries(this.fromMap().subMap((K)var1, (K)var2), this.transformer);
      }

      @Override
      public SortedMap<K, V2> tailMap(@ParametricNullness K var1) {
         return Maps.transformEntries(this.fromMap().tailMap((K)var1), this.transformer);
      }
   }

   private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {
      final Map<K, V> unmodifiableMap;
      final BiMap<? extends K, ? extends V> delegate;
      @RetainedWith
      BiMap<V, K> inverse;
      transient Set<V> values;
      private static final long serialVersionUID = 0L;

      UnmodifiableBiMap(BiMap<? extends K, ? extends V> var1, BiMap<V, K> var2) {
         this.unmodifiableMap = Collections.unmodifiableMap(var1);
         this.delegate = var1;
         this.inverse = var2;
      }

      @Override
      protected Map<K, V> delegate() {
         return this.unmodifiableMap;
      }

      @Override
      public V forcePut(@ParametricNullness K var1, @ParametricNullness V var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public BiMap<V, K> inverse() {
         BiMap var1 = this.inverse;
         return var1 == null ? (this.inverse = new Maps.UnmodifiableBiMap<>(this.delegate.inverse(), this)) : var1;
      }

      @Override
      public Set<V> values() {
         Set var1 = this.values;
         return var1 == null ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : var1;
      }
   }

   static class UnmodifiableEntries<K, V> extends ForwardingCollection<Entry<K, V>> {
      private final Collection<Entry<K, V>> entries;

      UnmodifiableEntries(Collection<Entry<K, V>> var1) {
         this.entries = var1;
      }

      @Override
      protected Collection<Entry<K, V>> delegate() {
         return this.entries;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return Maps.unmodifiableEntryIterator(this.entries.iterator());
      }

      @Override
      public Object[] toArray() {
         return this.standardToArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.standardToArray(var1);
      }
   }

   static class UnmodifiableEntrySet<K, V> extends Maps.UnmodifiableEntries<K, V> implements Set<Entry<K, V>> {
      UnmodifiableEntrySet(Set<Entry<K, V>> var1) {
         super(var1);
      }

      @Override
      public boolean equals(Object var1) {
         return Sets.equalsImpl(this, var1);
      }

      @Override
      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }
   }

   @GwtIncompatible
   static class UnmodifiableNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements Serializable, NavigableMap<K, V> {
      private final NavigableMap<K, ? extends V> delegate;
      private transient Maps.UnmodifiableNavigableMap<K, V> descendingMap;

      UnmodifiableNavigableMap(NavigableMap<K, ? extends V> var1) {
         this.delegate = var1;
      }

      UnmodifiableNavigableMap(NavigableMap<K, ? extends V> var1, Maps.UnmodifiableNavigableMap<K, V> var2) {
         this.delegate = var1;
         this.descendingMap = var2;
      }

      @Override
      protected SortedMap<K, V> delegate() {
         return Collections.unmodifiableSortedMap(this.delegate);
      }

      @Override
      public Entry<K, V> lowerEntry(@ParametricNullness K var1) {
         return Maps.unmodifiableOrNull(this.delegate.lowerEntry((K)var1));
      }

      @Override
      public K lowerKey(@ParametricNullness K var1) {
         return this.delegate.lowerKey((K)var1);
      }

      @Override
      public Entry<K, V> floorEntry(@ParametricNullness K var1) {
         return Maps.unmodifiableOrNull(this.delegate.floorEntry((K)var1));
      }

      @Override
      public K floorKey(@ParametricNullness K var1) {
         return this.delegate.floorKey((K)var1);
      }

      @Override
      public Entry<K, V> ceilingEntry(@ParametricNullness K var1) {
         return Maps.unmodifiableOrNull(this.delegate.ceilingEntry((K)var1));
      }

      @Override
      public K ceilingKey(@ParametricNullness K var1) {
         return this.delegate.ceilingKey((K)var1);
      }

      @Override
      public Entry<K, V> higherEntry(@ParametricNullness K var1) {
         return Maps.unmodifiableOrNull(this.delegate.higherEntry((K)var1));
      }

      @Override
      public K higherKey(@ParametricNullness K var1) {
         return this.delegate.higherKey((K)var1);
      }

      @Override
      public Entry<K, V> firstEntry() {
         return Maps.unmodifiableOrNull(this.delegate.firstEntry());
      }

      @Override
      public Entry<K, V> lastEntry() {
         return Maps.unmodifiableOrNull(this.delegate.lastEntry());
      }

      @Override
      public final Entry<K, V> pollFirstEntry() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Entry<K, V> pollLastEntry() {
         throw new UnsupportedOperationException();
      }

      @Override
      public NavigableMap<K, V> descendingMap() {
         Maps.UnmodifiableNavigableMap var1 = this.descendingMap;
         return var1 == null ? (this.descendingMap = new Maps.UnmodifiableNavigableMap<>(this.delegate.descendingMap(), this)) : var1;
      }

      @Override
      public Set<K> keySet() {
         return this.navigableKeySet();
      }

      @Override
      public NavigableSet<K> navigableKeySet() {
         return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
      }

      @Override
      public NavigableSet<K> descendingKeySet() {
         return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
      }

      @Override
      public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
         return this.subMap((K)var1, true, (K)var2, false);
      }

      @Override
      public NavigableMap<K, V> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
         return Maps.unmodifiableNavigableMap(this.delegate.subMap((K)var1, var2, (K)var3, var4));
      }

      @Override
      public SortedMap<K, V> headMap(@ParametricNullness K var1) {
         return this.headMap((K)var1, false);
      }

      @Override
      public NavigableMap<K, V> headMap(@ParametricNullness K var1, boolean var2) {
         return Maps.unmodifiableNavigableMap(this.delegate.headMap((K)var1, var2));
      }

      @Override
      public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
         return this.tailMap((K)var1, true);
      }

      @Override
      public NavigableMap<K, V> tailMap(@ParametricNullness K var1, boolean var2) {
         return Maps.unmodifiableNavigableMap(this.delegate.tailMap((K)var1, var2));
      }
   }

   static class ValueDifferenceImpl<V> implements MapDifference.ValueDifference<V> {
      @ParametricNullness
      private final V left;
      @ParametricNullness
      private final V right;

      static <V> MapDifference.ValueDifference<V> create(@ParametricNullness V var0, @ParametricNullness V var1) {
         return new Maps.ValueDifferenceImpl<>((V)var0, (V)var1);
      }

      private ValueDifferenceImpl(@ParametricNullness V var1, @ParametricNullness V var2) {
         this.left = (V)var1;
         this.right = (V)var2;
      }

      @ParametricNullness
      @Override
      public V leftValue() {
         return this.left;
      }

      @ParametricNullness
      @Override
      public V rightValue() {
         return this.right;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof MapDifference.ValueDifference)) {
            return false;
         } else {
            MapDifference.ValueDifference var2 = (MapDifference.ValueDifference)var1;
            return com.google.common.base.Objects.equal(this.left, var2.leftValue()) && com.google.common.base.Objects.equal(this.right, var2.rightValue());
         }
      }

      @Override
      public int hashCode() {
         return com.google.common.base.Objects.hashCode(this.left, this.right);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.left);
         String var2 = String.valueOf(this.right);
         return new StringBuilder(4 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   static class Values<K, V> extends AbstractCollection<V> {
      @Weak
      final Map<K, V> map;

      Values(Map<K, V> var1) {
         this.map = Preconditions.checkNotNull(var1);
      }

      final Map<K, V> map() {
         return this.map;
      }

      @Override
      public Iterator<V> iterator() {
         return Maps.valueIterator(this.map().entrySet().iterator());
      }

      @Override
      public void forEach(Consumer<? super V> var1) {
         Preconditions.checkNotNull(var1);
         this.map.forEach((var1x, var2) -> var1.accept(var2));
      }

      @Override
      public boolean remove(Object var1) {
         try {
            return super.remove(var1);
         } catch (UnsupportedOperationException var5) {
            for (Entry var4 : this.map().entrySet()) {
               if (com.google.common.base.Objects.equal(var1, var4.getValue())) {
                  this.map().remove(var4.getKey());
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         try {
            return super.removeAll(Preconditions.checkNotNull(var1));
         } catch (UnsupportedOperationException var6) {
            HashSet var3 = Sets.newHashSet();

            for (Entry var5 : this.map().entrySet()) {
               if (var1.contains(var5.getValue())) {
                  var3.add(var5.getKey());
               }
            }

            return this.map().keySet().removeAll(var3);
         }
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         try {
            return super.retainAll(Preconditions.checkNotNull(var1));
         } catch (UnsupportedOperationException var6) {
            HashSet var3 = Sets.newHashSet();

            for (Entry var5 : this.map().entrySet()) {
               if (var1.contains(var5.getValue())) {
                  var3.add(var5.getKey());
               }
            }

            return this.map().keySet().retainAll(var3);
         }
      }

      @Override
      public int size() {
         return this.map().size();
      }

      @Override
      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      @Override
      public boolean contains(Object var1) {
         return this.map().containsValue(var1);
      }

      @Override
      public void clear() {
         this.map().clear();
      }
   }

   @GwtCompatible
   abstract static class ViewCachingAbstractMap<K, V> extends AbstractMap<K, V> {
      private transient Set<Entry<K, V>> entrySet;
      private transient Set<K> keySet;
      private transient Collection<V> values;

      abstract Set<Entry<K, V>> createEntrySet();

      @Override
      public Set<Entry<K, V>> entrySet() {
         Set var1 = this.entrySet;
         return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
      }

      @Override
      public Set<K> keySet() {
         Set var1 = this.keySet;
         return var1 == null ? (this.keySet = this.createKeySet()) : var1;
      }

      Set<K> createKeySet() {
         return new Maps.KeySet<>(this);
      }

      @Override
      public Collection<V> values() {
         Collection var1 = this.values;
         return var1 == null ? (this.values = this.createValues()) : var1;
      }

      Collection<V> createValues() {
         return new Maps.Values<>(this);
      }
   }
}
