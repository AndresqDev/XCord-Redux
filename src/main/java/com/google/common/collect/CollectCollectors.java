package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Collector.Characteristics;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectCollectors {
   private static final Collector<Object, ?, ImmutableList<Object>> TO_IMMUTABLE_LIST = Collector.of(
      ImmutableList::builder, ImmutableList.Builder::add, ImmutableList.Builder::combine, ImmutableList.Builder::build
   );
   private static final Collector<Object, ?, ImmutableSet<Object>> TO_IMMUTABLE_SET = Collector.of(
      ImmutableSet::builder, ImmutableSet.Builder::add, ImmutableSet.Builder::combine, ImmutableSet.Builder::build
   );
   @GwtIncompatible
   private static final Collector<Range<Comparable<?>>, ?, ImmutableRangeSet<Comparable<?>>> TO_IMMUTABLE_RANGE_SET = Collector.of(
      ImmutableRangeSet::builder, ImmutableRangeSet.Builder::add, ImmutableRangeSet.Builder::combine, ImmutableRangeSet.Builder::build
   );

   static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
      return (Collector<E, ?, ImmutableList<E>>)TO_IMMUTABLE_LIST;
   }

   static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
      return (Collector<E, ?, ImmutableSet<E>>)TO_IMMUTABLE_SET;
   }

   static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> var0) {
      Preconditions.checkNotNull(var0);
      return Collector.of(
         () -> new ImmutableSortedSet.Builder(var0), ImmutableSortedSet.Builder::add, ImmutableSortedSet.Builder::combine, ImmutableSortedSet.Builder::build
      );
   }

   static <E extends Enum<E>> Collector<E, ?, ImmutableSet<E>> toImmutableEnumSet() {
      return (Collector<E, ?, ImmutableSet<E>>)CollectCollectors.EnumSetAccumulator.TO_IMMUTABLE_ENUM_SET;
   }

   @GwtIncompatible
   static <E extends Comparable<? super E>> Collector<Range<E>, ?, ImmutableRangeSet<E>> toImmutableRangeSet() {
      return TO_IMMUTABLE_RANGE_SET;
   }

   static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> var0, ToIntFunction<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(
         LinkedHashMultiset::create, (var2, var3) -> var2.add(Preconditions.checkNotNull((E)var0.apply(var3)), var1.applyAsInt(var3)), (var0x, var1x) -> {
            var0x.addAll(var1x);
            return var0x;
         }, var0x -> ImmutableMultiset.copyFromEntries(var0x.entrySet())
      );
   }

   static <T, E, M extends Multiset<E>> Collector<T, ?, M> toMultiset(Function<? super T, E> var0, ToIntFunction<? super T> var1, Supplier<M> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return (Collector<T, ?, M>)Collector.of(var2, (var2x, var3) -> var2x.add(var0.apply(var3), var1.applyAsInt(var3)), (var0x, var1x) -> {
         var0x.addAll(var1x);
         return var0x;
      });
   }

   static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(
         ImmutableMap.Builder::new,
         (var2, var3) -> var2.put((K)var0.apply(var3), (V)var1.apply(var3)),
         ImmutableMap.Builder::combine,
         ImmutableMap.Builder::build
      );
   }

   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collectors.collectingAndThen(Collectors.toMap(var0, var1, var2, LinkedHashMap::new), ImmutableMap::copyOf);
   }

   static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
      Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(
         () -> new ImmutableSortedMap.Builder(var0),
         (var2x, var3) -> var2x.put(var1.apply(var3), var2.apply(var3)),
         ImmutableSortedMap.Builder::combine,
         ImmutableSortedMap.Builder::build,
         Characteristics.UNORDERED
      );
   }

   static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
      Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2, BinaryOperator<V> var3
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return Collectors.collectingAndThen(Collectors.toMap(var1, var2, var3, () -> new TreeMap(var0)), ImmutableSortedMap::copyOfSorted);
   }

   static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(
         ImmutableBiMap.Builder::new,
         (var2, var3) -> var2.put((K)var0.apply(var3), (V)var1.apply(var3)),
         ImmutableBiMap.Builder::combine,
         ImmutableBiMap.Builder::build
      );
   }

   static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(
         () -> new CollectCollectors.EnumMapAccumulator(
               (var0x, var1x) -> {
                  String var2 = String.valueOf(var0x);
                  String var3 = String.valueOf(var1x);
                  throw new IllegalArgumentException(
                     new StringBuilder(27 + String.valueOf(var2).length() + String.valueOf(var3).length())
                        .append("Multiple values for key: ")
                        .append(var2)
                        .append(", ")
                        .append(var3)
                        .toString()
                  );
               }
            ),
         (var2, var3) -> {
            Enum var4 = (Enum)var0.apply(var3);
            Object var5 = var1.apply(var3);
            var2.put(Preconditions.checkNotNull((K)var4, "Null key for input %s", var3), Preconditions.checkNotNull(var5, "Null value for input %s", var3));
         },
         CollectCollectors.EnumMapAccumulator::combine,
         CollectCollectors.EnumMapAccumulator::toImmutableMap,
         Characteristics.UNORDERED
      );
   }

   static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(() -> new CollectCollectors.EnumMapAccumulator(var2), (var2x, var3) -> {
         Enum var4 = (Enum)var0.apply(var3);
         Object var5 = var1.apply(var3);
         var2x.put(Preconditions.checkNotNull((K)var4, "Null key for input %s", var3), Preconditions.checkNotNull(var5, "Null value for input %s", var3));
      }, CollectCollectors.EnumMapAccumulator::combine, CollectCollectors.EnumMapAccumulator::toImmutableMap);
   }

   @GwtIncompatible
   static <T, K extends Comparable<? super K>, V> Collector<T, ?, ImmutableRangeMap<K, V>> toImmutableRangeMap(
      Function<? super T, Range<K>> var0, Function<? super T, ? extends V> var1
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collector.of(
         ImmutableRangeMap::builder,
         (var2, var3) -> var2.put((Range<K>)var0.apply(var3), (V)var1.apply(var3)),
         ImmutableRangeMap.Builder::combine,
         ImmutableRangeMap.Builder::build
      );
   }

   static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1
   ) {
      Preconditions.checkNotNull(var0, "keyFunction");
      Preconditions.checkNotNull(var1, "valueFunction");
      return Collector.of(
         ImmutableListMultimap::builder,
         (var2, var3) -> var2.put((K)var0.apply(var3), (V)var1.apply(var3)),
         ImmutableListMultimap.Builder::combine,
         ImmutableListMultimap.Builder::build
      );
   }

   static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collectors.collectingAndThen(
         flatteningToMultimap(
            var1x -> Preconditions.checkNotNull((K)var0.apply((T)var1x)),
            var1x -> ((Stream)var1.apply((T)var1x)).peek(Preconditions::checkNotNull),
            MultimapBuilder.linkedHashKeys().arrayListValues()::build
         ),
         ImmutableListMultimap::copyOf
      );
   }

   static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1
   ) {
      Preconditions.checkNotNull(var0, "keyFunction");
      Preconditions.checkNotNull(var1, "valueFunction");
      return Collector.of(
         ImmutableSetMultimap::builder,
         (var2, var3) -> var2.put((K)var0.apply(var3), (V)var1.apply(var3)),
         ImmutableSetMultimap.Builder::combine,
         ImmutableSetMultimap.Builder::build
      );
   }

   static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return Collectors.collectingAndThen(
         flatteningToMultimap(
            var1x -> Preconditions.checkNotNull((K)var0.apply((T)var1x)),
            var1x -> ((Stream)var1.apply((T)var1x)).peek(Preconditions::checkNotNull),
            MultimapBuilder.linkedHashKeys().linkedHashSetValues()::build
         ),
         ImmutableSetMultimap::copyOf
      );
   }

   static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> toMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, Supplier<M> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return (Collector<T, ?, M>)Collector.of(var2, (var2x, var3) -> var2x.put(var0.apply(var3), var1.apply(var3)), (var0x, var1x) -> {
         var0x.putAll(var1x);
         return var0x;
      });
   }

   static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> flatteningToMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1, Supplier<M> var2
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return (Collector<T, ?, M>)Collector.of(var2, (var2x, var3) -> {
         Object var4 = var0.apply(var3);
         Collection var5 = var2x.get(var4);
         ((Stream)var1.apply(var3)).forEachOrdered(var5::add);
      }, (var0x, var1x) -> {
         var0x.putAll(var1x);
         return var0x;
      });
   }

   private static class EnumMapAccumulator<K extends Enum<K>, V> {
      private final BinaryOperator<V> mergeFunction;
      private EnumMap<K, V> map = null;

      EnumMapAccumulator(BinaryOperator<V> var1) {
         this.mergeFunction = var1;
      }

      void put(K var1, V var2) {
         if (this.map == null) {
            this.map = new EnumMap<>(var1.getDeclaringClass());
         }

         this.map.merge((K)var1, (V)var2, this.mergeFunction);
      }

      CollectCollectors.EnumMapAccumulator<K, V> combine(CollectCollectors.EnumMapAccumulator<K, V> var1) {
         if (this.map == null) {
            return var1;
         } else if (var1.map == null) {
            return this;
         } else {
            var1.map.forEach(this::put);
            return this;
         }
      }

      ImmutableMap<K, V> toImmutableMap() {
         return this.map == null ? ImmutableMap.of() : ImmutableEnumMap.asImmutable(this.map);
      }
   }

   private static final class EnumSetAccumulator<E extends Enum<E>> {
      static final Collector<Enum<?>, ?, ImmutableSet<? extends Enum<?>>> TO_IMMUTABLE_ENUM_SET = Collector.of(
         CollectCollectors.EnumSetAccumulator::new,
         CollectCollectors.EnumSetAccumulator::add,
         CollectCollectors.EnumSetAccumulator::combine,
         CollectCollectors.EnumSetAccumulator::toImmutableSet,
         Characteristics.UNORDERED
      );
      private EnumSet<E> set;

      void add(E var1) {
         if (this.set == null) {
            this.set = EnumSet.of((E)var1);
         } else {
            this.set.add((E)var1);
         }
      }

      CollectCollectors.EnumSetAccumulator<E> combine(CollectCollectors.EnumSetAccumulator<E> var1) {
         if (this.set == null) {
            return var1;
         } else if (var1.set == null) {
            return this;
         } else {
            this.set.addAll(var1.set);
            return this;
         }
      }

      ImmutableSet<E> toImmutableSet() {
         return this.set == null ? ImmutableSet.of() : ImmutableEnumSet.asImmutable(this.set);
      }
   }
}
