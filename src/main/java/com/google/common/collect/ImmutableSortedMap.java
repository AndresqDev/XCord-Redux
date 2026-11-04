package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements NavigableMap<K, V> {
   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
   private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap<>(
      ImmutableSortedSet.emptySet(Ordering.natural()), ImmutableList.of()
   );
   private final transient RegularImmutableSortedSet<K> keySet;
   private final transient ImmutableList<V> valueList;
   private transient ImmutableSortedMap<K, V> descendingMap;
   private static final long serialVersionUID = 0L;

   public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
      Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2
   ) {
      return CollectCollectors.toImmutableSortedMap(var0, var1, var2);
   }

   public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
      Comparator<? super K> var0, Function<? super T, ? extends K> var1, Function<? super T, ? extends V> var2, BinaryOperator<V> var3
   ) {
      return CollectCollectors.toImmutableSortedMap(var0, var1, var2, var3);
   }

   static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> var0) {
      return Ordering.natural().equals(var0) ? of() : new ImmutableSortedMap<>(ImmutableSortedSet.emptySet(var0), ImmutableList.of());
   }

   public static <K, V> ImmutableSortedMap<K, V> of() {
      return (ImmutableSortedMap<K, V>)NATURAL_EMPTY_MAP;
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1) {
      return of(Ordering.natural(), (K)var0, (V)var1);
   }

   private static <K, V> ImmutableSortedMap<K, V> of(Comparator<? super K> var0, K var1, V var2) {
      return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(ImmutableList.of((K)var1), Preconditions.checkNotNull(var0)), ImmutableList.of((V)var2));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3) {
      return fromEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9
   ) {
      return fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11
   ) {
      return fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11));
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13
   ) {
      return fromEntries(
         entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11), entryOf(var12, var13)
      );
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15
   ) {
      return fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15)
      );
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15, K var16, V var17
   ) {
      return fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15),
         entryOf(var16, var17)
      );
   }

   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(
      K var0,
      V var1,
      K var2,
      V var3,
      K var4,
      V var5,
      K var6,
      V var7,
      K var8,
      V var9,
      K var10,
      V var11,
      K var12,
      V var13,
      K var14,
      V var15,
      K var16,
      V var17,
      K var18,
      V var19
   ) {
      return fromEntries(
         entryOf(var0, var1),
         entryOf(var2, var3),
         entryOf(var4, var5),
         entryOf(var6, var7),
         entryOf(var8, var9),
         entryOf(var10, var11),
         entryOf(var12, var13),
         entryOf(var14, var15),
         entryOf(var16, var17),
         entryOf(var18, var19)
      );
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      Ordering var1 = (Ordering)NATURAL_ORDER;
      return copyOfInternal(var0, var1);
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> var0, Comparator<? super K> var1) {
      return copyOfInternal(var0, Preconditions.checkNotNull(var1));
   }

   @Beta
   public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Ordering var1 = (Ordering)NATURAL_ORDER;
      return copyOf(var0, var1);
   }

   @Beta
   public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0, Comparator<? super K> var1) {
      return fromEntries(Preconditions.checkNotNull(var1), false, var0);
   }

   public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> var0) {
      Comparator var1 = var0.comparator();
      if (var1 == null) {
         var1 = NATURAL_ORDER;
      }

      if (var0 instanceof ImmutableSortedMap) {
         ImmutableSortedMap var2 = (ImmutableSortedMap)var0;
         if (!var2.isPartialView()) {
            return var2;
         }
      }

      return fromEntries(var1, true, var0.entrySet());
   }

   private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> var0, Comparator<? super K> var1) {
      boolean var2 = false;
      if (var0 instanceof SortedMap) {
         SortedMap var3 = (SortedMap)var0;
         Comparator var4 = var3.comparator();
         var2 = var4 == null ? var1 == NATURAL_ORDER : var1.equals(var4);
      }

      if (var2 && var0 instanceof ImmutableSortedMap) {
         ImmutableSortedMap var5 = (ImmutableSortedMap)var0;
         if (!var5.isPartialView()) {
            return var5;
         }
      }

      return fromEntries(var1, var2, var0.entrySet());
   }

   private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> fromEntries(Entry<K, V>... var0) {
      return fromEntries(Ordering.natural(), false, var0, var0.length);
   }

   private static <K, V> ImmutableSortedMap<K, V> fromEntries(
      Comparator<? super K> var0, boolean var1, Iterable<? extends Entry<? extends K, ? extends V>> var2
   ) {
      Entry[] var3 = Iterables.toArray(var2, EMPTY_ENTRY_ARRAY);
      return fromEntries(var0, var1, var3, var3.length);
   }

   private static <K, V> ImmutableSortedMap<K, V> fromEntries(final Comparator<? super K> var0, boolean var1, Entry<K, V>[] var2, int var3) {
      switch (var3) {
         case 0:
            return emptyMap(var0);
         case 1:
            Entry var4 = Objects.requireNonNull(var2[0]);
            return of(var0, (K)var4.getKey(), (V)var4.getValue());
         default:
            Object[] var5 = new Object[var3];
            Object[] var6 = new Object[var3];
            if (var1) {
               for (int var7 = 0; var7 < var3; var7++) {
                  Entry var8 = Objects.requireNonNull(var2[var7]);
                  Object var9 = var8.getKey();
                  Object var10 = var8.getValue();
                  CollectPreconditions.checkEntryNotNull(var9, var10);
                  var5[var7] = var9;
                  var6[var7] = var10;
               }
            } else {
               Arrays.sort(var2, 0, var3, new Comparator<Entry<K, V>>() {
                  public int compare(Entry<K, V> var1, Entry<K, V> var2) {
                     Objects.requireNonNull(var1);
                     Objects.requireNonNull(var2);
                     return var0.compare(var1.getKey(), var2.getKey());
                  }
               });
               Entry var14 = Objects.requireNonNull(var2[0]);
               Object var15 = var14.getKey();
               var5[0] = var15;
               var6[0] = var14.getValue();
               CollectPreconditions.checkEntryNotNull(var5[0], var6[0]);

               for (int var16 = 1; var16 < var3; var16++) {
                  Entry var17 = Objects.requireNonNull(var2[var16 - 1]);
                  Entry var11 = Objects.requireNonNull(var2[var16]);
                  Object var12 = var11.getKey();
                  Object var13 = var11.getValue();
                  CollectPreconditions.checkEntryNotNull(var12, var13);
                  var5[var16] = var12;
                  var6[var16] = var13;
                  checkNoConflict(var0.compare(var15, var12) != 0, "key", var17, var11);
                  var15 = var12;
               }
            }

            return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(new RegularImmutableList<>(var5), var0), new RegularImmutableList<>(var6));
      }
   }

   public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> naturalOrder() {
      return new ImmutableSortedMap.Builder<>(Ordering.natural());
   }

   public static <K, V> ImmutableSortedMap.Builder<K, V> orderedBy(Comparator<K> var0) {
      return new ImmutableSortedMap.Builder<>(var0);
   }

   public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> reverseOrder() {
      return new ImmutableSortedMap.Builder<>(Ordering.natural().reverse());
   }

   ImmutableSortedMap(RegularImmutableSortedSet<K> var1, ImmutableList<V> var2) {
      this(var1, var2, null);
   }

   ImmutableSortedMap(RegularImmutableSortedSet<K> var1, ImmutableList<V> var2, ImmutableSortedMap<K, V> var3) {
      this.keySet = var1;
      this.valueList = var2;
      this.descendingMap = var3;
   }

   @Override
   public int size() {
      return this.valueList.size();
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      ImmutableList var2 = this.keySet.asList();

      for (int var3 = 0; var3 < this.size(); var3++) {
         var1.accept(var2.get(var3), this.valueList.get(var3));
      }
   }

   @Override
   public V get(Object var1) {
      int var2 = this.keySet.indexOf(var1);
      return var2 == -1 ? null : this.valueList.get(var2);
   }

   @Override
   boolean isPartialView() {
      return this.keySet.isPartialView() || this.valueList.isPartialView();
   }

   @Override
   public ImmutableSet<Entry<K, V>> entrySet() {
      return super.entrySet();
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      class 1EntrySet extends ImmutableMapEntrySet<K, V> {
         @Override
         public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.asList().iterator();
         }

         @Override
         public Spliterator<Entry<K, V>> spliterator() {
            return this.asList().spliterator();
         }

         @Override
         public void forEach(Consumer<? super Entry<K, V>> var1) {
            this.asList().forEach(var1);
         }

         @Override
         ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableAsList<Entry<K, V>>() {
               public Entry<K, V> get(int var1) {
                  return new SimpleImmutableEntry<>((K)ImmutableSortedMap.this.keySet.asList().get(var1), ImmutableSortedMap.this.valueList.get(var1));
               }

               @Override
               public Spliterator<Entry<K, V>> spliterator() {
                  return CollectSpliterators.indexed(this.size(), 1297, this::get);
               }

               @Override
               ImmutableCollection<Entry<K, V>> delegateCollection() {
                  return 1EntrySet.this;
               }
            };
         }

         @Override
         ImmutableMap<K, V> map() {
            return ImmutableSortedMap.this;
         }
      }

      return (ImmutableSet<Entry<K, V>>)(this.isEmpty() ? ImmutableSet.of() : new 1EntrySet());
   }

   public ImmutableSortedSet<K> keySet() {
      return this.keySet;
   }

   @Override
   ImmutableSet<K> createKeySet() {
      throw new AssertionError("should never be called");
   }

   @Override
   public ImmutableCollection<V> values() {
      return this.valueList;
   }

   @Override
   ImmutableCollection<V> createValues() {
      throw new AssertionError("should never be called");
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.keySet().comparator();
   }

   @Override
   public K firstKey() {
      return this.keySet().first();
   }

   @Override
   public K lastKey() {
      return this.keySet().last();
   }

   private ImmutableSortedMap<K, V> getSubMap(int var1, int var2) {
      if (var1 == 0 && var2 == this.size()) {
         return this;
      } else {
         return var1 == var2 ? emptyMap(this.comparator()) : new ImmutableSortedMap<>(this.keySet.getSubSet(var1, var2), this.valueList.subList(var1, var2));
      }
   }

   public ImmutableSortedMap<K, V> headMap(K var1) {
      return this.headMap((K)var1, false);
   }

   public ImmutableSortedMap<K, V> headMap(K var1, boolean var2) {
      return this.getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull((K)var1), var2));
   }

   public ImmutableSortedMap<K, V> subMap(K var1, K var2) {
      return this.subMap((K)var1, true, (K)var2, false);
   }

   public ImmutableSortedMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator().compare((K)var1, (K)var3) <= 0, "expected fromKey <= toKey but %s > %s", var1, var3);
      return this.headMap((K)var3, var4).tailMap((K)var1, var2);
   }

   public ImmutableSortedMap<K, V> tailMap(K var1) {
      return this.tailMap((K)var1, true);
   }

   public ImmutableSortedMap<K, V> tailMap(K var1, boolean var2) {
      return this.getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull((K)var1), var2), this.size());
   }

   @Override
   public Entry<K, V> lowerEntry(K var1) {
      return this.headMap((K)var1, false).lastEntry();
   }

   @Override
   public K lowerKey(K var1) {
      return Maps.keyOrNull(this.lowerEntry((K)var1));
   }

   @Override
   public Entry<K, V> floorEntry(K var1) {
      return this.headMap((K)var1, true).lastEntry();
   }

   @Override
   public K floorKey(K var1) {
      return Maps.keyOrNull(this.floorEntry((K)var1));
   }

   @Override
   public Entry<K, V> ceilingEntry(K var1) {
      return this.tailMap((K)var1, true).firstEntry();
   }

   @Override
   public K ceilingKey(K var1) {
      return Maps.keyOrNull(this.ceilingEntry((K)var1));
   }

   @Override
   public Entry<K, V> higherEntry(K var1) {
      return this.tailMap((K)var1, false).firstEntry();
   }

   @Override
   public K higherKey(K var1) {
      return Maps.keyOrNull(this.higherEntry((K)var1));
   }

   @Override
   public Entry<K, V> firstEntry() {
      return this.isEmpty() ? null : this.entrySet().asList().get(0);
   }

   @Override
   public Entry<K, V> lastEntry() {
      return this.isEmpty() ? null : this.entrySet().asList().get(this.size() - 1);
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final Entry<K, V> pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final Entry<K, V> pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public ImmutableSortedMap<K, V> descendingMap() {
      ImmutableSortedMap var1 = this.descendingMap;
      if (var1 == null) {
         return this.isEmpty()
            ? emptyMap(Ordering.from(this.comparator()).reverse())
            : new ImmutableSortedMap<>((RegularImmutableSortedSet<K>)this.keySet.descendingSet(), this.valueList.reverse(), this);
      } else {
         return var1;
      }
   }

   public ImmutableSortedSet<K> navigableKeySet() {
      return this.keySet;
   }

   public ImmutableSortedSet<K> descendingKeySet() {
      return this.keySet.descendingSet();
   }

   @Override
   Object writeReplace() {
      return new ImmutableSortedMap.SerializedForm<>(this);
   }

   public static class Builder<K, V> extends ImmutableMap.Builder<K, V> {
      private final Comparator<? super K> comparator;

      public Builder(Comparator<? super K> var1) {
         this.comparator = Preconditions.checkNotNull(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> put(K var1, V var2) {
         super.put((K)var1, (V)var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableSortedMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @Deprecated
      @CanIgnoreReturnValue
      @Beta
      @DoNotCall("Always throws UnsupportedOperationException")
      public final ImmutableSortedMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         throw new UnsupportedOperationException("Not available on ImmutableSortedMap.Builder");
      }

      ImmutableSortedMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableSortedMap<K, V> build() {
         return this.buildOrThrow();
      }

      public ImmutableSortedMap<K, V> buildOrThrow() {
         switch (this.size) {
            case 0:
               return ImmutableSortedMap.emptyMap(this.comparator);
            case 1:
               Entry var1 = Objects.requireNonNull(this.entries[0]);
               return ImmutableSortedMap.of(this.comparator, (K)var1.getKey(), (V)var1.getValue());
            default:
               return ImmutableSortedMap.fromEntries(this.comparator, false, this.entries, this.size);
         }
      }
   }

   private static class SerializedForm<K, V> extends ImmutableMap.SerializedForm<K, V> {
      private final Comparator<? super K> comparator;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableSortedMap<K, V> var1) {
         super(var1);
         this.comparator = var1.comparator();
      }

      ImmutableSortedMap.Builder<K, V> makeBuilder(int var1) {
         return new ImmutableSortedMap.Builder<>(this.comparator);
      }
   }
}
