package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.DoNotCall;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableSortedSetFauxverideShim<E> extends ImmutableSet.CachingAsList<E> {
   @Deprecated
   @DoNotCall("Use toImmutableSortedSet")
   public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use naturalOrder")
   public static <E> ImmutableSortedSet.Builder<E> builder() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use naturalOrder (which does not accept an expected size)")
   public static <E> ImmutableSortedSet.Builder<E> builderWithExpectedSize(int var0) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass a parameter of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0, E var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0, E var1, E var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Pass parameters of type Comparable")
   public static <E> ImmutableSortedSet<E> copyOf(E[] var0) {
      throw new UnsupportedOperationException();
   }
}
