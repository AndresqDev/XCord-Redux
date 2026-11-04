package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public abstract class FluentIterable<E> implements Iterable<E> {
   private final Optional<Iterable<E>> iterableDelegate;

   protected FluentIterable() {
      this.iterableDelegate = Optional.absent();
   }

   FluentIterable(Iterable<E> var1) {
      this.iterableDelegate = Optional.of(var1);
   }

   private Iterable<E> getDelegate() {
      return this.iterableDelegate.or(this);
   }

   public static <E> FluentIterable<E> from(final Iterable<E> var0) {
      return var0 instanceof FluentIterable ? (FluentIterable)var0 : new FluentIterable<E>(var0) {
         @Override
         public Iterator<E> iterator() {
            return var0.iterator();
         }
      };
   }

   @Beta
   public static <E> FluentIterable<E> from(E[] var0) {
      return from(Arrays.asList((E[])var0));
   }

   @Deprecated
   @InlineMe(
      replacement = "checkNotNull(iterable)",
      staticImports = {"com.google.common.base.Preconditions.checkNotNull"}
   )
   public static <E> FluentIterable<E> from(FluentIterable<E> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Beta
   public static <T> FluentIterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1) {
      return concatNoDefensiveCopy(var0, var1);
   }

   @Beta
   public static <T> FluentIterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1, Iterable<? extends T> var2) {
      return concatNoDefensiveCopy(var0, var1, var2);
   }

   @Beta
   public static <T> FluentIterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1, Iterable<? extends T> var2, Iterable<? extends T> var3) {
      return concatNoDefensiveCopy(var0, var1, var2, var3);
   }

   @Beta
   public static <T> FluentIterable<T> concat(Iterable<? extends T>... var0) {
      return concatNoDefensiveCopy(Arrays.copyOf(var0, var0.length));
   }

   @Beta
   public static <T> FluentIterable<T> concat(final Iterable<? extends Iterable<? extends T>> var0) {
      Preconditions.checkNotNull(var0);
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.concat(Iterators.transform(var0.iterator(), Iterables.toIterator()));
         }
      };
   }

   private static <T> FluentIterable<T> concatNoDefensiveCopy(final Iterable<? extends T>... var0) {
      for (Iterable var4 : var0) {
         Preconditions.checkNotNull(var4);
      }

      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.concat(new AbstractIndexedListIterator<Iterator<? extends T>>(var0.length) {
               public Iterator<? extends T> get(int var1) {
                  return var0[var1].iterator();
               }
            });
         }
      };
   }

   @Beta
   public static <E> FluentIterable<E> of() {
      return from(Collections.emptyList());
   }

   @Beta
   public static <E> FluentIterable<E> of(@ParametricNullness E var0, E... var1) {
      return from(Lists.asList((E)var0, (E[])var1));
   }

   @Override
   public String toString() {
      return Iterables.toString(this.getDelegate());
   }

   public final int size() {
      return Iterables.size(this.getDelegate());
   }

   public final boolean contains(Object var1) {
      return Iterables.contains(this.getDelegate(), var1);
   }

   public final FluentIterable<E> cycle() {
      return from(Iterables.cycle(this.getDelegate()));
   }

   @Beta
   public final FluentIterable<E> append(Iterable<? extends E> var1) {
      return concat(this.getDelegate(), var1);
   }

   @Beta
   public final FluentIterable<E> append(E... var1) {
      return concat(this.getDelegate(), Arrays.asList((E[])var1));
   }

   public final FluentIterable<E> filter(Predicate<? super E> var1) {
      return from(Iterables.filter(this.getDelegate(), var1));
   }

   @GwtIncompatible
   public final <T> FluentIterable<T> filter(Class<T> var1) {
      return from(Iterables.filter(this.getDelegate(), var1));
   }

   public final boolean anyMatch(Predicate<? super E> var1) {
      return Iterables.any(this.getDelegate(), var1);
   }

   public final boolean allMatch(Predicate<? super E> var1) {
      return Iterables.all(this.getDelegate(), var1);
   }

   public final Optional<E> firstMatch(Predicate<? super E> var1) {
      return Iterables.tryFind(this.getDelegate(), var1);
   }

   public final <T> FluentIterable<T> transform(Function<? super E, T> var1) {
      return from(Iterables.transform(this.getDelegate(), var1));
   }

   public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> var1) {
      return concat(this.transform(var1));
   }

   public final Optional<E> first() {
      Iterator var1 = this.getDelegate().iterator();
      return var1.hasNext() ? Optional.of((E)var1.next()) : Optional.absent();
   }

   public final Optional<E> last() {
      Iterable var1 = this.getDelegate();
      if (var1 instanceof List) {
         List var4 = (List)var1;
         return var4.isEmpty() ? Optional.absent() : Optional.of((E)var4.get(var4.size() - 1));
      } else {
         Iterator var2 = var1.iterator();
         if (!var2.hasNext()) {
            return Optional.absent();
         } else if (var1 instanceof SortedSet) {
            SortedSet var5 = (SortedSet)var1;
            return Optional.of((E)var5.last());
         } else {
            Object var3;
            do {
               var3 = var2.next();
            } while (var2.hasNext());

            return Optional.of((E)var3);
         }
      }
   }

   public final FluentIterable<E> skip(int var1) {
      return from(Iterables.skip(this.getDelegate(), var1));
   }

   public final FluentIterable<E> limit(int var1) {
      return from(Iterables.limit(this.getDelegate(), var1));
   }

   public final boolean isEmpty() {
      return !this.getDelegate().iterator().hasNext();
   }

   public final ImmutableList<E> toList() {
      return ImmutableList.copyOf(this.getDelegate());
   }

   public final ImmutableList<E> toSortedList(Comparator<? super E> var1) {
      return Ordering.from(var1).immutableSortedCopy(this.getDelegate());
   }

   public final ImmutableSet<E> toSet() {
      return ImmutableSet.copyOf(this.getDelegate());
   }

   public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> var1) {
      return ImmutableSortedSet.copyOf(var1, this.getDelegate());
   }

   public final ImmutableMultiset<E> toMultiset() {
      return ImmutableMultiset.copyOf(this.getDelegate());
   }

   public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> var1) {
      return Maps.toMap(this.getDelegate(), var1);
   }

   public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> var1) {
      return Multimaps.index(this.getDelegate(), var1);
   }

   public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> var1) {
      return Maps.uniqueIndex(this.getDelegate(), var1);
   }

   @GwtIncompatible
   public final E[] toArray(Class<E> var1) {
      return Iterables.toArray(this.getDelegate(), var1);
   }

   @CanIgnoreReturnValue
   public final <C extends Collection<? super E>> C copyInto(C var1) {
      Preconditions.checkNotNull(var1);
      Iterable var2 = this.getDelegate();
      if (var2 instanceof Collection) {
         var1.addAll((Collection<? extends E>)var2);
      } else {
         for (Object var4 : var2) {
            var1.add(var4);
         }
      }

      return (C)var1;
   }

   @Beta
   public final String join(Joiner var1) {
      return var1.join(this);
   }

   @ParametricNullness
   public final E get(int var1) {
      return Iterables.get(this.getDelegate(), var1);
   }

   public final Stream<E> stream() {
      return Streams.stream(this.getDelegate());
   }

   private static class FromIterableFunction<E> implements Function<Iterable<E>, FluentIterable<E>> {
      public FluentIterable<E> apply(Iterable<E> var1) {
         return FluentIterable.from(var1);
      }
   }
}
