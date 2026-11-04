package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.DoNotMock;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@DoNotMock("Use ImmutableMap.of or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableMap<K, V> implements Serializable, Map<K, V> {
   static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Entry[0];
   @LazyInit
   @RetainedWith
   private transient ImmutableSet<Entry<K, V>> entrySet;
   @LazyInit
   @RetainedWith
   private transient ImmutableSet<K> keySet;
   @LazyInit
   @RetainedWith
   private transient ImmutableCollection<V> values;
   @LazyInit
   private transient ImmutableSetMultimap<K, V> multimapView;

   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      return CollectCollectors.toImmutableMap(var0, var1);
   }

   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2
   ) {
      return CollectCollectors.toImmutableMap(var0, var1, var2);
   }

   public static <K, V> ImmutableMap<K, V> of() {
      return (ImmutableMap<K, V>)RegularImmutableMap.EMPTY;
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1) {
      return ImmutableBiMap.of((K)var0, (V)var1);
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return RegularImmutableMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   public static <K, V> ImmutableMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11) {
      return RegularImmutableMap.fromEntries(
         entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11)
      );
   }

   public static <K, V> ImmutableMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13
   ) {
      return RegularImmutableMap.fromEntries(
         entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9), entryOf(var10, var11), entryOf(var12, var13)
      );
   }

   public static <K, V> ImmutableMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15
   ) {
      return RegularImmutableMap.fromEntries(
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

   public static <K, V> ImmutableMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15, K var16, V var17
   ) {
      return RegularImmutableMap.fromEntries(
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

   public static <K, V> ImmutableMap<K, V> of(
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
      return RegularImmutableMap.fromEntries(
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

   @SafeVarargs
   public static <K, V> ImmutableMap<K, V> ofEntries(Entry<? extends K, ? extends V>... var0) {
      return RegularImmutableMap.fromEntries(var0);
   }

   static <K, V> Entry<K, V> entryOf(K var0, V var1) {
      CollectPreconditions.checkEntryNotNull(var0, var1);
      return new SimpleImmutableEntry<>((K)var0, (V)var1);
   }

   public static <K, V> ImmutableMap.Builder<K, V> builder() {
      return new ImmutableMap.Builder<>();
   }

   @Beta
   public static <K, V> ImmutableMap.Builder<K, V> builderWithExpectedSize(int var0) {
      CollectPreconditions.checkNonnegative(var0, "expectedSize");
      return new ImmutableMap.Builder<>(var0);
   }

   static void checkNoConflict(boolean var0, String var1, Entry<?, ?> var2, Entry<?, ?> var3) {
      if (!var0) {
         throw conflictException(var1, var2, var3);
      }
   }

   static IllegalArgumentException conflictException(String var0, Object var1, Object var2) {
      String var3 = String.valueOf(var1);
      String var4 = String.valueOf(var2);
      return new IllegalArgumentException(
         new StringBuilder(34 + String.valueOf(var0).length() + String.valueOf(var3).length() + String.valueOf(var4).length())
            .append("Multiple entries with same ")
            .append(var0)
            .append(": ")
            .append(var3)
            .append(" and ")
            .append(var4)
            .toString()
      );
   }

   public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      if (var0 instanceof ImmutableMap && !(var0 instanceof SortedMap)) {
         ImmutableMap var1 = (ImmutableMap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      } else if (var0 instanceof EnumMap) {
         return copyOfEnumMap((EnumMap<K, ? extends V>)var0);
      }

      return copyOf(var0.entrySet());
   }

   @Beta
   public static <K, V> ImmutableMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Entry[] var1 = Iterables.toArray(var0, EMPTY_ENTRY_ARRAY);
      switch (var1.length) {
         case 0:
            return of();
         case 1:
            Entry var2 = Objects.requireNonNull(var1[0]);
            return of((K)var2.getKey(), (V)var2.getValue());
         default:
            return RegularImmutableMap.fromEntries(var1);
      }
   }

   private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(EnumMap<K, ? extends V> var0) {
      EnumMap var1 = new EnumMap(var0);

      for (Entry var3 : var1.entrySet()) {
         CollectPreconditions.checkEntryNotNull(var3.getKey(), var3.getValue());
      }

      return ImmutableEnumMap.asImmutable(var1);
   }

   ImmutableMap() {
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V putIfAbsent(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean replace(K var1, V var2, V var3) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V replace(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V computeIfAbsent(K var1, Function<? super K, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void putAll(Map<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final V remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.get(var1) != null;
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   @Override
   public abstract V get(Object var1);

   @Override
   public final V getOrDefault(Object var1, V var2) {
      Object var3 = this.get(var1);
      return (V)(var3 != null ? var3 : var2);
   }

   public ImmutableSet<Entry<K, V>> entrySet() {
      ImmutableSet var1 = this.entrySet;
      return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
   }

   abstract ImmutableSet<Entry<K, V>> createEntrySet();

   public ImmutableSet<K> keySet() {
      ImmutableSet var1 = this.keySet;
      return var1 == null ? (this.keySet = this.createKeySet()) : var1;
   }

   abstract ImmutableSet<K> createKeySet();

   UnmodifiableIterator<K> keyIterator() {
      final UnmodifiableIterator var1 = this.entrySet().iterator();
      return new UnmodifiableIterator<K>(this) {
         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         @Override
         public K next() {
            return (K)((Entry)var1.next()).getKey();
         }
      };
   }

   Spliterator<K> keySpliterator() {
      return CollectSpliterators.map(this.entrySet().spliterator(), Entry::getKey);
   }

   public ImmutableCollection<V> values() {
      ImmutableCollection var1 = this.values;
      return var1 == null ? (this.values = this.createValues()) : var1;
   }

   abstract ImmutableCollection<V> createValues();

   public ImmutableSetMultimap<K, V> asMultimap() {
      if (this.isEmpty()) {
         return ImmutableSetMultimap.of();
      } else {
         ImmutableSetMultimap var1 = this.multimapView;
         return var1 == null ? (this.multimapView = new ImmutableSetMultimap<>(new ImmutableMap.MapViewOfValuesAsSingletonSets(), this.size(), null)) : var1;
      }
   }

   @Override
   public boolean equals(Object var1) {
      return Maps.equalsImpl(this, var1);
   }

   abstract boolean isPartialView();

   @Override
   public int hashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   boolean isHashCodeFast() {
      return false;
   }

   @Override
   public String toString() {
      return Maps.toStringImpl(this);
   }

   Object writeReplace() {
      return new ImmutableMap.SerializedForm<>(this);
   }

   @DoNotMock
   public static class Builder<K, V> {
      Comparator<? super V> valueComparator;
      Entry<K, V>[] entries;
      int size;
      boolean entriesUsed;

      public Builder() {
         this(4);
      }

      Builder(int var1) {
         this.entries = new Entry[var1];
         this.size = 0;
         this.entriesUsed = false;
      }

      private void ensureCapacity(int var1) {
         if (var1 > this.entries.length) {
            this.entries = Arrays.copyOf(this.entries, ImmutableCollection.Builder.expandedCapacity(this.entries.length, var1));
            this.entriesUsed = false;
         }
      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> put(K var1, V var2) {
         this.ensureCapacity(this.size + 1);
         Entry var3 = ImmutableMap.entryOf(var1, var2);
         this.entries[this.size++] = var3;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         return this.put((K)var1.getKey(), (V)var1.getValue());
      }

      @CanIgnoreReturnValue
      public ImmutableMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         return this.putAll(var1.entrySet());
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         if (var1 instanceof Collection) {
            this.ensureCapacity(this.size + ((Collection)var1).size());
         }

         for (Entry var3 : var1) {
            this.put(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         Preconditions.checkState(this.valueComparator == null, "valueComparator was already set");
         this.valueComparator = Preconditions.checkNotNull(var1, "valueComparator");
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         Preconditions.checkNotNull(var1);
         this.ensureCapacity(this.size + var1.size);
         System.arraycopy(var1.entries, 0, this.entries, this.size, var1.size);
         this.size = this.size + var1.size;
         return this;
      }

      public ImmutableMap<K, V> build() {
         return this.buildOrThrow();
      }

      public ImmutableMap<K, V> buildOrThrow() {
         if (this.valueComparator != null) {
            if (this.entriesUsed) {
               this.entries = Arrays.copyOf(this.entries, this.size);
            }

            Arrays.sort(this.entries, 0, this.size, Ordering.from(this.valueComparator).onResultOf(Maps.valueFunction()));
         }

         switch (this.size) {
            case 0:
               return ImmutableMap.of();
            case 1:
               Entry var1 = Objects.requireNonNull(this.entries[0]);
               return ImmutableMap.of((K)var1.getKey(), (V)var1.getValue());
            default:
               this.entriesUsed = true;
               return RegularImmutableMap.fromEntryArray(this.size, this.entries);
         }
      }

      @VisibleForTesting
      ImmutableMap<K, V> buildJdkBacked() {
         Preconditions.checkState(this.valueComparator == null, "buildJdkBacked is only for testing; can't use valueComparator");
         switch (this.size) {
            case 0:
               return ImmutableMap.of();
            case 1:
               Entry var1 = Objects.requireNonNull(this.entries[0]);
               return ImmutableMap.of((K)var1.getKey(), (V)var1.getValue());
            default:
               this.entriesUsed = true;
               return JdkBackedImmutableMap.create(this.size, this.entries);
         }
      }
   }

   abstract static class IteratorBasedImmutableMap<K, V> extends ImmutableMap<K, V> {
      abstract UnmodifiableIterator<Entry<K, V>> entryIterator();

      Spliterator<Entry<K, V>> entrySpliterator() {
         return Spliterators.spliterator(this.entryIterator(), (long)this.size(), 1297);
      }

      @Override
      ImmutableSet<K> createKeySet() {
         return new ImmutableMapKeySet<>(this);
      }

      @Override
      ImmutableSet<Entry<K, V>> createEntrySet() {
         class 1EntrySetImpl extends ImmutableMapEntrySet<K, V> {
            @Override
            ImmutableMap<K, V> map() {
               return IteratorBasedImmutableMap.this;
            }

            @Override
            public UnmodifiableIterator<Entry<K, V>> iterator() {
               return IteratorBasedImmutableMap.this.entryIterator();
            }
         }

         return new 1EntrySetImpl();
      }

      @Override
      ImmutableCollection<V> createValues() {
         return new ImmutableMapValues<>(this);
      }
   }

   private final class MapViewOfValuesAsSingletonSets extends ImmutableMap.IteratorBasedImmutableMap<K, ImmutableSet<V>> {
      private MapViewOfValuesAsSingletonSets() {
      }

      @Override
      public int size() {
         return ImmutableMap.this.size();
      }

      @Override
      ImmutableSet<K> createKeySet() {
         return ImmutableMap.this.keySet();
      }

      @Override
      public boolean containsKey(Object var1) {
         return ImmutableMap.this.containsKey(var1);
      }

      public ImmutableSet<V> get(Object var1) {
         Object var2 = ImmutableMap.this.get(var1);
         return var2 == null ? null : ImmutableSet.of((V)var2);
      }

      @Override
      boolean isPartialView() {
         return ImmutableMap.this.isPartialView();
      }

      @Override
      public int hashCode() {
         return ImmutableMap.this.hashCode();
      }

      @Override
      boolean isHashCodeFast() {
         return ImmutableMap.this.isHashCodeFast();
      }

      @Override
      UnmodifiableIterator<Entry<K, ImmutableSet<V>>> entryIterator() {
         final UnmodifiableIterator var1 = ImmutableMap.this.entrySet().iterator();
         return new UnmodifiableIterator<Entry<K, ImmutableSet<V>>>(this) {
            @Override
            public boolean hasNext() {
               return var1.hasNext();
            }

            public Entry<K, ImmutableSet<V>> next() {
               final Entry var1x = (Entry)var1.next();
               return new AbstractMapEntry<K, ImmutableSet<V>>(this) {
                  @Override
                  public K getKey() {
                     return (K)var1x.getKey();
                  }

                  public ImmutableSet<V> getValue() {
                     return ImmutableSet.of((V)var1x.getValue());
                  }
               };
            }
         };
      }
   }

   static class SerializedForm<K, V> implements Serializable {
      private static final boolean USE_LEGACY_SERIALIZATION = true;
      private final Object keys;
      private final Object values;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap<K, V> var1) {
         Object[] var2 = new Object[var1.size()];
         Object[] var3 = new Object[var1.size()];
         int var4 = 0;

         for (Entry var6 : var1.entrySet()) {
            var2[var4] = var6.getKey();
            var3[var4] = var6.getValue();
            var4++;
         }

         this.keys = var2;
         this.values = var3;
      }

      final Object readResolve() {
         if (!(this.keys instanceof ImmutableSet)) {
            return this.legacyReadResolve();
         } else {
            ImmutableSet var1 = (ImmutableSet)this.keys;
            ImmutableCollection var2 = (ImmutableCollection)this.values;
            ImmutableMap.Builder var3 = this.makeBuilder(var1.size());
            UnmodifiableIterator var4 = var1.iterator();
            UnmodifiableIterator var5 = var2.iterator();

            while (var4.hasNext()) {
               var3.put(var4.next(), var5.next());
            }

            return var3.build();
         }
      }

      final Object legacyReadResolve() {
         Object[] var1 = (Object[])this.keys;
         Object[] var2 = (Object[])this.values;
         ImmutableMap.Builder var3 = this.makeBuilder(var1.length);

         for (int var4 = 0; var4 < var1.length; var4++) {
            var3.put(var1[var4], var2[var4]);
         }

         return var3.build();
      }

      ImmutableMap.Builder<K, V> makeBuilder(int var1) {
         return new ImmutableMap.Builder<>(var1);
      }
   }
}
