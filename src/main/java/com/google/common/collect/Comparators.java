package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Comparators {
   private Comparators() {
   }

   @Beta
   public static <T, S extends T> Comparator<Iterable<S>> lexicographical(Comparator<T> var0) {
      return new LexicographicalOrdering<>(Preconditions.checkNotNull(var0));
   }

   @Beta
   public static <T> boolean isInOrder(Iterable<? extends T> var0, Comparator<T> var1) {
      Preconditions.checkNotNull(var1);
      Iterator var2 = var0.iterator();
      if (var2.hasNext()) {
         Object var3 = var2.next();

         while (var2.hasNext()) {
            Object var4 = var2.next();
            if (var1.compare(var3, var4) > 0) {
               return false;
            }

            var3 = var4;
         }
      }

      return true;
   }

   @Beta
   public static <T> boolean isInStrictOrder(Iterable<? extends T> var0, Comparator<T> var1) {
      Preconditions.checkNotNull(var1);
      Iterator var2 = var0.iterator();
      if (var2.hasNext()) {
         Object var3 = var2.next();

         while (var2.hasNext()) {
            Object var4 = var2.next();
            if (var1.compare(var3, var4) >= 0) {
               return false;
            }

            var3 = var4;
         }
      }

      return true;
   }

   public static <T> Collector<T, ?, List<T>> least(int var0, Comparator<? super T> var1) {
      CollectPreconditions.checkNonnegative(var0, "k");
      Preconditions.checkNotNull(var1);
      return Collector.of(() -> TopKSelector.least(var0, var1), TopKSelector::offer, TopKSelector::combine, TopKSelector::topK, Characteristics.UNORDERED);
   }

   public static <T> Collector<T, ?, List<T>> greatest(int var0, Comparator<? super T> var1) {
      return least(var0, var1.reversed());
   }

   @Beta
   public static <T> Comparator<Optional<T>> emptiesFirst(Comparator<? super T> var0) {
      Preconditions.checkNotNull(var0);
      return Comparator.comparing(var0x -> var0x.orElse(null), Comparator.nullsFirst(var0));
   }

   @Beta
   public static <T> Comparator<Optional<T>> emptiesLast(Comparator<? super T> var0) {
      Preconditions.checkNotNull(var0);
      return Comparator.comparing(var0x -> var0x.orElse(null), Comparator.nullsLast(var0));
   }

   @Beta
   public static <T extends Comparable<? super T>> T min(T var0, T var1) {
      return (T)(var0.compareTo(var1) <= 0 ? var0 : var1);
   }

   @ParametricNullness
   @Beta
   public static <T> T min(@ParametricNullness T var0, @ParametricNullness T var1, Comparator<T> var2) {
      return (T)(var2.compare(var0, var1) <= 0 ? var0 : var1);
   }

   @Beta
   public static <T extends Comparable<? super T>> T max(T var0, T var1) {
      return (T)(var0.compareTo(var1) >= 0 ? var0 : var1);
   }

   @ParametricNullness
   @Beta
   public static <T> T max(@ParametricNullness T var0, @ParametricNullness T var1, Comparator<T> var2) {
      return (T)(var2.compare(var0, var1) >= 0 ? var0 : var1);
   }
}
