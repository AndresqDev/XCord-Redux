package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.DoNotCall;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V> {
   @Deprecated
   @DoNotCall("Use toImmutableSortedMap")
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use toImmutableSortedMap")
   public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1, BinaryOperator<V> var2
   ) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use naturalOrder")
   public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use naturalOrder (which does not accept an expected size)")
   public static <K, V> ImmutableSortedMap.Builder<K, V> builderWithExpectedSize(int var0) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass a key of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13
   ) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15
   ) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(
      K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9, K var10, V var11, K var12, V var13, K var14, V var15, K var16, V var17
   ) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass keys of type Comparable")
   public static <K, V> ImmutableSortedMap<K, V> of(
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
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("ImmutableSortedMap.ofEntries not currently available; use ImmutableSortedMap.copyOf")
   public static <K, V> ImmutableSortedMap<K, V> ofEntries(Entry<? extends K, ? extends V>... var0) {
      throw new UnsupportedOperationException();
   }
}
