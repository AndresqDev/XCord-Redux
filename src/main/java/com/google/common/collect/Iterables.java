package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Iterables {
   private Iterables() {
   }

   public static <T> Iterable<T> unmodifiableIterable(Iterable<? extends T> var0) {
      Preconditions.checkNotNull(var0);
      return (Iterable<T>)(!(var0 instanceof Iterables.UnmodifiableIterable) && !(var0 instanceof ImmutableCollection)
         ? new Iterables.UnmodifiableIterable<>(var0)
         : var0);
   }

   @Deprecated
   public static <E> Iterable<E> unmodifiableIterable(ImmutableCollection<E> var0) {
      return Preconditions.checkNotNull(var0);
   }

   public static int size(Iterable<?> var0) {
      return var0 instanceof Collection ? ((Collection)var0).size() : Iterators.size(var0.iterator());
   }

   public static boolean contains(Iterable<? extends Object> var0, Object var1) {
      if (var0 instanceof Collection) {
         Collection var2 = (Collection)var0;
         return Collections2.safeContains(var2, var1);
      } else {
         return Iterators.contains(var0.iterator(), var1);
      }
   }

   @CanIgnoreReturnValue
   public static boolean removeAll(Iterable<?> var0, Collection<?> var1) {
      return var0 instanceof Collection ? ((Collection)var0).removeAll(Preconditions.checkNotNull(var1)) : Iterators.removeAll(var0.iterator(), var1);
   }

   @CanIgnoreReturnValue
   public static boolean retainAll(Iterable<?> var0, Collection<?> var1) {
      return var0 instanceof Collection ? ((Collection)var0).retainAll(Preconditions.checkNotNull(var1)) : Iterators.retainAll(var0.iterator(), var1);
   }

   @CanIgnoreReturnValue
   public static <T> boolean removeIf(Iterable<T> var0, Predicate<? super T> var1) {
      return var0 instanceof Collection ? ((Collection)var0).removeIf(var1) : Iterators.removeIf(var0.iterator(), var1);
   }

   static <T> T removeFirstMatching(Iterable<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1);
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         Object var3 = var2.next();
         if (var1.apply(var3)) {
            var2.remove();
            return (T)var3;
         }
      }

      return null;
   }

   public static boolean elementsEqual(Iterable<?> var0, Iterable<?> var1) {
      if (var0 instanceof Collection && var1 instanceof Collection) {
         Collection var2 = (Collection)var0;
         Collection var3 = (Collection)var1;
         if (var2.size() != var3.size()) {
            return false;
         }
      }

      return Iterators.elementsEqual(var0.iterator(), var1.iterator());
   }

   public static String toString(Iterable<?> var0) {
      return Iterators.toString(var0.iterator());
   }

   @ParametricNullness
   public static <T> T getOnlyElement(Iterable<T> var0) {
      return Iterators.getOnlyElement(var0.iterator());
   }

   @ParametricNullness
   public static <T> T getOnlyElement(Iterable<? extends T> var0, @ParametricNullness T var1) {
      return Iterators.getOnlyElement(var0.iterator(), (T)var1);
   }

   @GwtIncompatible
   public static <T> T[] toArray(Iterable<? extends T> var0, Class<T> var1) {
      return (T[])toArray(var0, ObjectArrays.newArray(var1, 0));
   }

   static <T> T[] toArray(Iterable<? extends T> var0, T[] var1) {
      Collection var2 = castOrCopyToCollection(var0);
      return (T[])var2.toArray(var1);
   }

   static Object[] toArray(Iterable<?> var0) {
      return castOrCopyToCollection(var0).toArray();
   }

   private static <E> Collection<E> castOrCopyToCollection(Iterable<E> var0) {
      return (Collection<E>)(var0 instanceof Collection ? (Collection)var0 : Lists.newArrayList(var0.iterator()));
   }

   @CanIgnoreReturnValue
   public static <T> boolean addAll(Collection<T> var0, Iterable<? extends T> var1) {
      if (var1 instanceof Collection) {
         Collection var2 = (Collection)var1;
         return var0.addAll(var2);
      } else {
         return Iterators.addAll(var0, Preconditions.checkNotNull(var1).iterator());
      }
   }

   public static int frequency(Iterable<?> var0, Object var1) {
      if (var0 instanceof Multiset) {
         return ((Multiset)var0).count(var1);
      } else if (var0 instanceof Set) {
         return ((Set)var0).contains(var1) ? 1 : 0;
      } else {
         return Iterators.frequency(var0.iterator(), var1);
      }
   }

   public static <T> Iterable<T> cycle(final Iterable<T> var0) {
      Preconditions.checkNotNull(var0);
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.cycle(var0);
         }

         @Override
         public Spliterator<T> spliterator() {
            return Stream.<Iterable>generate(() -> var0).flatMap(Streams::stream).spliterator();
         }

         @Override
         public String toString() {
            return String.valueOf(var0.toString()).concat(" (cycled)");
         }
      };
   }

   @SafeVarargs
   public static <T> Iterable<T> cycle(T... var0) {
      return cycle(Lists.newArrayList((T[])var0));
   }

   public static <T> Iterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1) {
      return FluentIterable.concat(var0, var1);
   }

   public static <T> Iterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1, Iterable<? extends T> var2) {
      return FluentIterable.concat(var0, var1, var2);
   }

   public static <T> Iterable<T> concat(Iterable<? extends T> var0, Iterable<? extends T> var1, Iterable<? extends T> var2, Iterable<? extends T> var3) {
      return FluentIterable.concat(var0, var1, var2, var3);
   }

   @SafeVarargs
   public static <T> Iterable<T> concat(Iterable<? extends T>... var0) {
      return FluentIterable.concat(var0);
   }

   public static <T> Iterable<T> concat(Iterable<? extends Iterable<? extends T>> var0) {
      return FluentIterable.concat(var0);
   }

   public static <T> Iterable<List<T>> partition(final Iterable<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return new FluentIterable<List<T>>() {
         @Override
         public Iterator<List<T>> iterator() {
            return Iterators.partition(var0.iterator(), var1);
         }
      };
   }

   public static <T> Iterable<List<T>> paddedPartition(final Iterable<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return new FluentIterable<List<T>>() {
         @Override
         public Iterator<List<T>> iterator() {
            return Iterators.paddedPartition(var0.iterator(), var1);
         }
      };
   }

   public static <T> Iterable<T> filter(final Iterable<T> var0, final Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.filter(var0.iterator(), var1);
         }

         @Override
         public void forEach(Consumer<? super T> var1x) {
            Preconditions.checkNotNull(var1x);
            var0.forEach(var2 -> {
               if (var1.test(var2)) {
                  var1x.accept(var2);
               }
            });
         }

         @Override
         public Spliterator<T> spliterator() {
            return CollectSpliterators.filter(var0.spliterator(), var1);
         }
      };
   }

   @GwtIncompatible
   public static <T> Iterable<T> filter(Iterable<?> var0, Class<T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return filter(var0, Predicates.instanceOf(var1));
   }

   public static <T> boolean any(Iterable<T> var0, Predicate<? super T> var1) {
      return Iterators.any(var0.iterator(), var1);
   }

   public static <T> boolean all(Iterable<T> var0, Predicate<? super T> var1) {
      return Iterators.all(var0.iterator(), var1);
   }

   @ParametricNullness
   public static <T> T find(Iterable<T> var0, Predicate<? super T> var1) {
      return Iterators.find(var0.iterator(), var1);
   }

   public static <T> T find(Iterable<? extends T> var0, Predicate<? super T> var1, T var2) {
      return Iterators.find(var0.iterator(), var1, (T)var2);
   }

   public static <T> Optional<T> tryFind(Iterable<T> var0, Predicate<? super T> var1) {
      return Iterators.tryFind(var0.iterator(), var1);
   }

   public static <T> int indexOf(Iterable<T> var0, Predicate<? super T> var1) {
      return Iterators.indexOf(var0.iterator(), var1);
   }

   public static <F, T> Iterable<T> transform(final Iterable<F> var0, final Function<? super F, ? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.transform(var0.iterator(), var1);
         }

         @Override
         public void forEach(Consumer<? super T> var1x) {
            Preconditions.checkNotNull(var1x);
            var0.forEach(var2 -> var1x.accept(var1.apply(var2)));
         }

         @Override
         public Spliterator<T> spliterator() {
            return CollectSpliterators.map(var0.spliterator(), var1);
         }
      };
   }

   @ParametricNullness
   public static <T> T get(Iterable<T> var0, int var1) {
      Preconditions.checkNotNull(var0);
      return (T)(var0 instanceof List ? ((List)var0).get(var1) : Iterators.get(var0.iterator(), var1));
   }

   @ParametricNullness
   public static <T> T get(Iterable<? extends T> var0, int var1, @ParametricNullness T var2) {
      Preconditions.checkNotNull(var0);
      Iterators.checkNonnegative(var1);
      if (var0 instanceof List) {
         List var4 = Lists.cast(var0);
         return (T)(var1 < var4.size() ? var4.get(var1) : var2);
      } else {
         Iterator var3 = var0.iterator();
         Iterators.advance(var3, var1);
         return Iterators.getNext(var3, (T)var2);
      }
   }

   @ParametricNullness
   public static <T> T getFirst(Iterable<? extends T> var0, @ParametricNullness T var1) {
      return Iterators.getNext(var0.iterator(), (T)var1);
   }

   @ParametricNullness
   public static <T> T getLast(Iterable<T> var0) {
      if (var0 instanceof List) {
         List var1 = (List)var0;
         if (var1.isEmpty()) {
            throw new NoSuchElementException();
         } else {
            return getLastInNonemptyList(var1);
         }
      } else {
         return Iterators.getLast(var0.iterator());
      }
   }

   @ParametricNullness
   public static <T> T getLast(Iterable<? extends T> var0, @ParametricNullness T var1) {
      if (var0 instanceof Collection) {
         Collection var2 = (Collection)var0;
         if (var2.isEmpty()) {
            return (T)var1;
         }

         if (var0 instanceof List) {
            return getLastInNonemptyList(Lists.cast(var0));
         }
      }

      return Iterators.getLast(var0.iterator(), (T)var1);
   }

   @ParametricNullness
   private static <T> T getLastInNonemptyList(List<T> var0) {
      return (T)var0.get(var0.size() - 1);
   }

   public static <T> Iterable<T> skip(final Iterable<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "number to skip cannot be negative");
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            if (var0 instanceof List) {
               List var3 = (List)var0;
               int var2 = Math.min(var3.size(), var1);
               return var3.subList(var2, var3.size()).iterator();
            } else {
               final Iterator var1x = var0.iterator();
               Iterators.advance(var1x, var1);
               return new Iterator<T>(this) {
                  boolean atStart = true;

                  @Override
                  public boolean hasNext() {
                     return var1x.hasNext();
                  }

                  @ParametricNullness
                  @Override
                  public T next() {
                     Object var1xx = var1x.next();
                     this.atStart = false;
                     return (T)var1xx;
                  }

                  @Override
                  public void remove() {
                     CollectPreconditions.checkRemove(!this.atStart);
                     var1x.remove();
                  }
               };
            }
         }

         @Override
         public Spliterator<T> spliterator() {
            if (var0 instanceof List) {
               List var1x = (List)var0;
               int var2 = Math.min(var1x.size(), var1);
               return var1x.subList(var2, var1x.size()).spliterator();
            } else {
               return Streams.<T>stream(var0).skip((long)var1).spliterator();
            }
         }
      };
   }

   public static <T> Iterable<T> limit(final Iterable<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "limit is negative");
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.limit(var0.iterator(), var1);
         }

         @Override
         public Spliterator<T> spliterator() {
            return Streams.<T>stream(var0).limit((long)var1).spliterator();
         }
      };
   }

   public static <T> Iterable<T> consumingIterable(final Iterable<T> var0) {
      Preconditions.checkNotNull(var0);
      return new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return (Iterator<T>)(var0 instanceof Queue ? new ConsumingQueueIterator<>((Queue<T>)var0) : Iterators.consumingIterator(var0.iterator()));
         }

         @Override
         public String toString() {
            return "Iterables.consumingIterable(...)";
         }
      };
   }

   public static boolean isEmpty(Iterable<?> var0) {
      return var0 instanceof Collection ? ((Collection)var0).isEmpty() : !var0.iterator().hasNext();
   }

   @Beta
   public static <T> Iterable<T> mergeSorted(final Iterable<? extends Iterable<? extends T>> var0, final Comparator<? super T> var1) {
      Preconditions.checkNotNull(var0, "iterables");
      Preconditions.checkNotNull(var1, "comparator");
      FluentIterable var2 = new FluentIterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return Iterators.mergeSorted(Iterables.transform(var0, Iterables.toIterator()), var1);
         }
      };
      return new Iterables.UnmodifiableIterable<>(var2);
   }

   static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator() {
      return new Function<Iterable<? extends T>, Iterator<? extends T>>() {
         public Iterator<? extends T> apply(Iterable<? extends T> var1) {
            return var1.iterator();
         }
      };
   }

   private static final class UnmodifiableIterable<T> extends FluentIterable<T> {
      private final Iterable<? extends T> iterable;

      private UnmodifiableIterable(Iterable<? extends T> var1) {
         this.iterable = var1;
      }

      @Override
      public Iterator<T> iterator() {
         return Iterators.unmodifiableIterator(this.iterable.iterator());
      }

      @Override
      public void forEach(Consumer<? super T> var1) {
         this.iterable.forEach(var1);
      }

      @Override
      public Spliterator<T> spliterator() {
         return (Spliterator<T>)this.iterable.spliterator();
      }

      @Override
      public String toString() {
         return this.iterable.toString();
      }
   }
}
