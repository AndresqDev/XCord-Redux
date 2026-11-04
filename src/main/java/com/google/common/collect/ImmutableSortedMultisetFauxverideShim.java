package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.DoNotCall;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ImmutableSortedMultisetFauxverideShim<E> extends ImmutableMultiset<E> {
   @Deprecated
   @DoNotCall("Use toImmutableSortedMultiset.")
   public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use toImmutableSortedMultiset.")
   public static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> var0, ToIntFunction<? super T> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Use naturalOrder.")
   public static <E> ImmutableSortedMultiset.Builder<E> builder() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Elements must be Comparable. (Or, pass a Comparator to orderedBy or copyOf.)")
   public static <E> ImmutableSortedMultiset<E> copyOf(E[] var0) {
      throw new UnsupportedOperationException();
   }
}
