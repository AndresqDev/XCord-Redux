package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Ordering<T> implements Comparator<T> {
   static final int LEFT_IS_GREATER = 1;
   static final int RIGHT_IS_GREATER = -1;

   @GwtCompatible(
      serializable = true
   )
   public static <C extends Comparable> Ordering<C> natural() {
      return NaturalOrdering.INSTANCE;
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Ordering<T> from(Comparator<T> var0) {
      return (Ordering<T>)(var0 instanceof Ordering ? (Ordering)var0 : new ComparatorOrdering<>(var0));
   }

   @Deprecated
   @GwtCompatible(
      serializable = true
   )
   public static <T> Ordering<T> from(Ordering<T> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Ordering<T> explicit(List<T> var0) {
      return new ExplicitOrdering<>(var0);
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Ordering<T> explicit(T var0, T... var1) {
      return explicit(Lists.asList((T)var0, (T[])var1));
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering<Object> allEqual() {
      return AllEqualOrdering.INSTANCE;
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering<Object> usingToString() {
      return UsingToStringOrdering.INSTANCE;
   }

   public static Ordering<Object> arbitrary() {
      return Ordering.ArbitraryOrderingHolder.ARBITRARY_ORDERING;
   }

   protected Ordering() {
   }

   @GwtCompatible(
      serializable = true
   )
   public <S extends T> Ordering<S> reverse() {
      return new ReverseOrdering<>(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public <S extends T> Ordering<S> nullsFirst() {
      return new NullsFirstOrdering<>(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public <S extends T> Ordering<S> nullsLast() {
      return new NullsLastOrdering<>(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public <F> Ordering<F> onResultOf(Function<F, ? extends T> var1) {
      return new ByFunctionOrdering<>(var1, this);
   }

   <T2 extends T> Ordering<Entry<T2, ?>> onKeys() {
      return this.onResultOf(Maps.keyFunction());
   }

   @GwtCompatible(
      serializable = true
   )
   public <U extends T> Ordering<U> compound(Comparator<? super U> var1) {
      return new CompoundOrdering<>(this, Preconditions.checkNotNull(var1));
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> var0) {
      return new CompoundOrdering<>(var0);
   }

   @GwtCompatible(
      serializable = true
   )
   public <S extends T> Ordering<Iterable<S>> lexicographical() {
      return new LexicographicalOrdering<>(this);
   }

   @CanIgnoreReturnValue
   @Override
   public abstract int compare(@ParametricNullness T var1, @ParametricNullness T var2);

   @ParametricNullness
   public <E extends T> E min(Iterator<E> var1) {
      Object var2 = var1.next();

      while (var1.hasNext()) {
         var2 = this.min(var2, var1.next());
      }

      return (E)var2;
   }

   @ParametricNullness
   public <E extends T> E min(Iterable<E> var1) {
      return this.min(var1.iterator());
   }

   @ParametricNullness
   public <E extends T> E min(@ParametricNullness E var1, @ParametricNullness E var2) {
      return (E)(this.compare((T)var1, (T)var2) <= 0 ? var1 : var2);
   }

   @ParametricNullness
   public <E extends T> E min(@ParametricNullness E var1, @ParametricNullness E var2, @ParametricNullness E var3, E... var4) {
      Object var5 = this.min(this.min(var1, var2), var3);

      for (Object var9 : var4) {
         var5 = this.min(var5, var9);
      }

      return (E)var5;
   }

   @ParametricNullness
   public <E extends T> E max(Iterator<E> var1) {
      Object var2 = var1.next();

      while (var1.hasNext()) {
         var2 = this.max(var2, var1.next());
      }

      return (E)var2;
   }

   @ParametricNullness
   public <E extends T> E max(Iterable<E> var1) {
      return this.max(var1.iterator());
   }

   @ParametricNullness
   public <E extends T> E max(@ParametricNullness E var1, @ParametricNullness E var2) {
      return (E)(this.compare((T)var1, (T)var2) >= 0 ? var1 : var2);
   }

   @ParametricNullness
   public <E extends T> E max(@ParametricNullness E var1, @ParametricNullness E var2, @ParametricNullness E var3, E... var4) {
      Object var5 = this.max(this.max(var1, var2), var3);

      for (Object var9 : var4) {
         var5 = this.max(var5, var9);
      }

      return (E)var5;
   }

   public <E extends T> List<E> leastOf(Iterable<E> var1, int var2) {
      if (var1 instanceof Collection) {
         Collection var3 = (Collection)var1;
         if ((long)var3.size() <= 2L * (long)var2) {
            Object[] var4 = var3.toArray();
            Arrays.sort(var4, this);
            if (var4.length > var2) {
               var4 = Arrays.copyOf(var4, var2);
            }

            return Collections.unmodifiableList(Arrays.asList((E[])var4));
         }
      }

      return this.leastOf(var1.iterator(), var2);
   }

   public <E extends T> List<E> leastOf(Iterator<E> var1, int var2) {
      Preconditions.checkNotNull(var1);
      CollectPreconditions.checkNonnegative(var2, "k");
      if (var2 == 0 || !var1.hasNext()) {
         return Collections.emptyList();
      } else if (var2 >= 1073741823) {
         ArrayList var4 = Lists.newArrayList(var1);
         Collections.sort(var4, this);
         if (var4.size() > var2) {
            var4.subList(var2, var4.size()).clear();
         }

         var4.trimToSize();
         return Collections.unmodifiableList(var4);
      } else {
         TopKSelector var3 = TopKSelector.least(var2, this);
         var3.offerAll(var1);
         return var3.topK();
      }
   }

   public <E extends T> List<E> greatestOf(Iterable<E> var1, int var2) {
      return this.reverse().leastOf(var1, var2);
   }

   public <E extends T> List<E> greatestOf(Iterator<E> var1, int var2) {
      return this.reverse().leastOf(var1, var2);
   }

   public <E extends T> List<E> sortedCopy(Iterable<E> var1) {
      Object[] var2 = Iterables.toArray(var1);
      Arrays.sort(var2, this);
      return Lists.newArrayList(Arrays.asList((E[])var2));
   }

   public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> var1) {
      return ImmutableList.sortedCopyOf(this, var1);
   }

   public boolean isOrdered(Iterable<? extends T> var1) {
      Iterator var2 = var1.iterator();
      if (var2.hasNext()) {
         Object var3 = var2.next();

         while (var2.hasNext()) {
            Object var4 = var2.next();
            if (this.compare((T)var3, (T)var4) > 0) {
               return false;
            }

            var3 = var4;
         }
      }

      return true;
   }

   public boolean isStrictlyOrdered(Iterable<? extends T> var1) {
      Iterator var2 = var1.iterator();
      if (var2.hasNext()) {
         Object var3 = var2.next();

         while (var2.hasNext()) {
            Object var4 = var2.next();
            if (this.compare((T)var3, (T)var4) >= 0) {
               return false;
            }

            var3 = var4;
         }
      }

      return true;
   }

   @Deprecated
   public int binarySearch(List<? extends T> var1, @ParametricNullness T var2) {
      return Collections.binarySearch(var1, var2, this);
   }

   @VisibleForTesting
   static class ArbitraryOrdering extends Ordering<Object> {
      private final AtomicInteger counter = new AtomicInteger(0);
      private final ConcurrentMap<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeMap();

      private Integer getUid(Object var1) {
         Integer var2 = this.uids.get(var1);
         if (var2 == null) {
            var2 = this.counter.getAndIncrement();
            Integer var3 = this.uids.putIfAbsent(var1, var2);
            if (var3 != null) {
               var2 = var3;
            }
         }

         return var2;
      }

      @Override
      public int compare(Object var1, Object var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 == null) {
            return -1;
         } else if (var2 == null) {
            return 1;
         } else {
            int var3 = this.identityHashCode(var1);
            int var4 = this.identityHashCode(var2);
            if (var3 != var4) {
               return var3 < var4 ? -1 : 1;
            } else {
               int var5 = this.getUid(var1).compareTo(this.getUid(var2));
               if (var5 == 0) {
                  throw new AssertionError();
               } else {
                  return var5;
               }
            }
         }
      }

      @Override
      public String toString() {
         return "Ordering.arbitrary()";
      }

      int identityHashCode(Object var1) {
         return System.identityHashCode(var1);
      }
   }

   private static class ArbitraryOrderingHolder {
      static final Ordering<Object> ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
   }

   @VisibleForTesting
   static class IncomparableValueException extends ClassCastException {
      final Object value;
      private static final long serialVersionUID = 0L;

      IncomparableValueException(Object var1) {
         String var2 = String.valueOf(var1);
         super(new StringBuilder(22 + String.valueOf(var2).length()).append("Cannot compare value: ").append(var2).toString());
         this.value = var1;
      }
   }
}
