package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Iterators {
   private Iterators() {
   }

   static <T> UnmodifiableIterator<T> emptyIterator() {
      return emptyListIterator();
   }

   static <T> UnmodifiableListIterator<T> emptyListIterator() {
      return (UnmodifiableListIterator<T>)Iterators.ArrayItr.EMPTY;
   }

   static <T> Iterator<T> emptyModifiableIterator() {
      return Iterators.EmptyModifiableIterator.INSTANCE;
   }

   public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<? extends T> var0) {
      Preconditions.checkNotNull(var0);
      return var0 instanceof UnmodifiableIterator ? (UnmodifiableIterator)var0 : new UnmodifiableIterator<T>() {
         @Override
         public boolean hasNext() {
            return var0.hasNext();
         }

         @ParametricNullness
         @Override
         public T next() {
            return (T)var0.next();
         }
      };
   }

   @Deprecated
   public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> var0) {
      return Preconditions.checkNotNull(var0);
   }

   public static int size(Iterator<?> var0) {
      long var1;
      for (var1 = 0L; var0.hasNext(); var1++) {
         var0.next();
      }

      return Ints.saturatedCast(var1);
   }

   public static boolean contains(Iterator<?> var0, Object var1) {
      if (var1 == null) {
         while (var0.hasNext()) {
            if (var0.next() == null) {
               return true;
            }
         }
      } else {
         while (var0.hasNext()) {
            if (var1.equals(var0.next())) {
               return true;
            }
         }
      }

      return false;
   }

   @CanIgnoreReturnValue
   public static boolean removeAll(Iterator<?> var0, Collection<?> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      while (var0.hasNext()) {
         if (var1.contains(var0.next())) {
            var0.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public static <T> boolean removeIf(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      while (var0.hasNext()) {
         if (var1.apply(var0.next())) {
            var0.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public static boolean retainAll(Iterator<?> var0, Collection<?> var1) {
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      while (var0.hasNext()) {
         if (!var1.contains(var0.next())) {
            var0.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public static boolean elementsEqual(Iterator<?> var0, Iterator<?> var1) {
      while (var0.hasNext()) {
         if (!var1.hasNext()) {
            return false;
         }

         Object var2 = var0.next();
         Object var3 = var1.next();
         if (!Objects.equal(var2, var3)) {
            return false;
         }
      }

      return !var1.hasNext();
   }

   public static String toString(Iterator<?> var0) {
      StringBuilder var1 = new StringBuilder().append('[');
      boolean var2 = true;

      while (var0.hasNext()) {
         if (!var2) {
            var1.append(", ");
         }

         var2 = false;
         var1.append(var0.next());
      }

      return var1.append(']').toString();
   }

   @ParametricNullness
   public static <T> T getOnlyElement(Iterator<T> var0) {
      Object var1 = var0.next();
      if (!var0.hasNext()) {
         return (T)var1;
      } else {
         StringBuilder var2 = new StringBuilder().append("expected one element but was: <").append(var1);

         for (int var3 = 0; var3 < 4 && var0.hasNext(); var3++) {
            var2.append(", ").append(var0.next());
         }

         if (var0.hasNext()) {
            var2.append(", ...");
         }

         var2.append('>');
         throw new IllegalArgumentException(var2.toString());
      }
   }

   @ParametricNullness
   public static <T> T getOnlyElement(Iterator<? extends T> var0, @ParametricNullness T var1) {
      return (T)(var0.hasNext() ? getOnlyElement(var0) : var1);
   }

   @GwtIncompatible
   public static <T> T[] toArray(Iterator<? extends T> var0, Class<T> var1) {
      ArrayList var2 = Lists.newArrayList(var0);
      return (T[])Iterables.toArray(var2, var1);
   }

   @CanIgnoreReturnValue
   public static <T> boolean addAll(Collection<T> var0, Iterator<? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = false;

      while (var1.hasNext()) {
         var2 |= var0.add(var1.next());
      }

      return var2;
   }

   public static int frequency(Iterator<?> var0, Object var1) {
      int var2 = 0;

      while (contains(var0, var1)) {
         var2++;
      }

      return var2;
   }

   public static <T> Iterator<T> cycle(final Iterable<T> var0) {
      Preconditions.checkNotNull(var0);
      return new Iterator<T>() {
         Iterator<T> iterator = Iterators.emptyModifiableIterator();

         @Override
         public boolean hasNext() {
            return this.iterator.hasNext() || var0.iterator().hasNext();
         }

         @ParametricNullness
         @Override
         public T next() {
            if (!this.iterator.hasNext()) {
               this.iterator = var0.iterator();
               if (!this.iterator.hasNext()) {
                  throw new NoSuchElementException();
               }
            }

            return this.iterator.next();
         }

         @Override
         public void remove() {
            this.iterator.remove();
         }
      };
   }

   @SafeVarargs
   public static <T> Iterator<T> cycle(T... var0) {
      return cycle(Lists.newArrayList((T[])var0));
   }

   private static <I extends Iterator<?>> Iterator<I> consumingForArray(final I... var0) {
      return new UnmodifiableIterator<I>() {
         int index = 0;

         @Override
         public boolean hasNext() {
            return this.index < var0.length;
         }

         public I next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Iterator var1 = java.util.Objects.requireNonNull(var0[this.index]);
               var0[this.index] = null;
               this.index++;
               return (I)var1;
            }
         }
      };
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return concat(consumingForArray(var0, var1));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1, Iterator<? extends T> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return concat(consumingForArray(var0, var1, var2));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T> var0, Iterator<? extends T> var1, Iterator<? extends T> var2, Iterator<? extends T> var3) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return concat(consumingForArray(var0, var1, var2, var3));
   }

   public static <T> Iterator<T> concat(Iterator<? extends T>... var0) {
      return concatNoDefensiveCopy(Arrays.copyOf(var0, var0.length));
   }

   public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> var0) {
      return new Iterators.ConcatenatedIterator<>(var0);
   }

   static <T> Iterator<T> concatNoDefensiveCopy(Iterator<? extends T>... var0) {
      for (Iterator var4 : Preconditions.checkNotNull(var0)) {
         Preconditions.checkNotNull(var4);
      }

      return concat(consumingForArray(var0));
   }

   public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> var0, int var1) {
      return partitionImpl(var0, var1, false);
   }

   public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> var0, int var1) {
      return partitionImpl(var0, var1, true);
   }

   private static <T> UnmodifiableIterator<List<T>> partitionImpl(final Iterator<T> var0, final int var1, final boolean var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return new UnmodifiableIterator<List<T>>() {
         @Override
         public boolean hasNext() {
            return var0.hasNext();
         }

         public List<T> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Object[] var1x = new Object[var1];

               int var2x;
               for (var2x = 0; var2x < var1 && var0.hasNext(); var2x++) {
                  var1x[var2x] = var0.next();
               }

               for (int var3 = var2x; var3 < var1; var3++) {
                  var1x[var3] = null;
               }

               List var4 = Collections.unmodifiableList(Arrays.asList(var1x));
               return !var2 && var2x != var1 ? var4.subList(0, var2x) : var4;
            }
         }
      };
   }

   public static <T> UnmodifiableIterator<T> filter(final Iterator<T> var0, final Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AbstractIterator<T>() {
         @Override
         protected T computeNext() {
            while (var0.hasNext()) {
               Object var1x = var0.next();
               if (var1.apply(var1x)) {
                  return (T)var1x;
               }
            }

            return (T)this.endOfData();
         }
      };
   }

   @GwtIncompatible
   public static <T> UnmodifiableIterator<T> filter(Iterator<?> var0, Class<T> var1) {
      return filter(var0, Predicates.instanceOf(var1));
   }

   public static <T> boolean any(Iterator<T> var0, Predicate<? super T> var1) {
      return indexOf(var0, var1) != -1;
   }

   public static <T> boolean all(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1);

      while (var0.hasNext()) {
         Object var2 = var0.next();
         if (!var1.apply(var2)) {
            return false;
         }
      }

      return true;
   }

   @ParametricNullness
   public static <T> T find(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      while (var0.hasNext()) {
         Object var2 = var0.next();
         if (var1.apply(var2)) {
            return (T)var2;
         }
      }

      throw new NoSuchElementException();
   }

   public static <T> T find(Iterator<? extends T> var0, Predicate<? super T> var1, T var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      while (var0.hasNext()) {
         Object var3 = var0.next();
         if (var1.apply(var3)) {
            return (T)var3;
         }
      }

      return (T)var2;
   }

   public static <T> Optional<T> tryFind(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      while (var0.hasNext()) {
         Object var2 = var0.next();
         if (var1.apply(var2)) {
            return Optional.of((T)var2);
         }
      }

      return Optional.absent();
   }

   public static <T> int indexOf(Iterator<T> var0, Predicate<? super T> var1) {
      Preconditions.checkNotNull(var1, "predicate");

      for (int var2 = 0; var0.hasNext(); var2++) {
         Object var3 = var0.next();
         if (var1.apply(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public static <F, T> Iterator<T> transform(Iterator<F> var0, final Function<? super F, ? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return new TransformedIterator<F, T>(var0) {
         @ParametricNullness
         @Override
         T transform(@ParametricNullness F var1x) {
            return (T)var1.apply(var1x);
         }
      };
   }

   @ParametricNullness
   public static <T> T get(Iterator<T> var0, int var1) {
      checkNonnegative(var1);
      int var2 = advance(var0, var1);
      if (!var0.hasNext()) {
         throw new IndexOutOfBoundsException(
            new StringBuilder(91)
               .append("position (")
               .append(var1)
               .append(") must be less than the number of elements that remained (")
               .append(var2)
               .append(")")
               .toString()
         );
      } else {
         return (T)var0.next();
      }
   }

   @ParametricNullness
   public static <T> T get(Iterator<? extends T> var0, int var1, @ParametricNullness T var2) {
      checkNonnegative(var1);
      advance(var0, var1);
      return getNext(var0, (T)var2);
   }

   static void checkNonnegative(int var0) {
      if (var0 < 0) {
         throw new IndexOutOfBoundsException(new StringBuilder(43).append("position (").append(var0).append(") must not be negative").toString());
      }
   }

   @ParametricNullness
   public static <T> T getNext(Iterator<? extends T> var0, @ParametricNullness T var1) {
      return (T)(var0.hasNext() ? var0.next() : var1);
   }

   @ParametricNullness
   public static <T> T getLast(Iterator<T> var0) {
      Object var1;
      do {
         var1 = var0.next();
      } while (var0.hasNext());

      return (T)var1;
   }

   @ParametricNullness
   public static <T> T getLast(Iterator<? extends T> var0, @ParametricNullness T var1) {
      return (T)(var0.hasNext() ? getLast(var0) : var1);
   }

   @CanIgnoreReturnValue
   public static int advance(Iterator<?> var0, int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "numberToAdvance must be nonnegative");

      int var2;
      for (var2 = 0; var2 < var1 && var0.hasNext(); var2++) {
         var0.next();
      }

      return var2;
   }

   public static <T> Iterator<T> limit(final Iterator<T> var0, final int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 >= 0, "limit is negative");
      return new Iterator<T>() {
         private int count;

         @Override
         public boolean hasNext() {
            return this.count < var1 && var0.hasNext();
         }

         @ParametricNullness
         @Override
         public T next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.count++;
               return (T)var0.next();
            }
         }

         @Override
         public void remove() {
            var0.remove();
         }
      };
   }

   public static <T> Iterator<T> consumingIterator(final Iterator<T> var0) {
      Preconditions.checkNotNull(var0);
      return new UnmodifiableIterator<T>() {
         @Override
         public boolean hasNext() {
            return var0.hasNext();
         }

         @ParametricNullness
         @Override
         public T next() {
            Object var1 = var0.next();
            var0.remove();
            return (T)var1;
         }

         @Override
         public String toString() {
            return "Iterators.consumingIterator(...)";
         }
      };
   }

   static <T> T pollNext(Iterator<T> var0) {
      if (var0.hasNext()) {
         Object var1 = var0.next();
         var0.remove();
         return (T)var1;
      } else {
         return null;
      }
   }

   static void clear(Iterator<?> var0) {
      Preconditions.checkNotNull(var0);

      while (var0.hasNext()) {
         var0.next();
         var0.remove();
      }
   }

   @SafeVarargs
   public static <T> UnmodifiableIterator<T> forArray(T... var0) {
      return forArray((T[])var0, 0, var0.length, 0);
   }

   static <T> UnmodifiableListIterator<T> forArray(T[] var0, int var1, int var2, int var3) {
      Preconditions.checkArgument(var2 >= 0);
      int var4 = var1 + var2;
      Preconditions.checkPositionIndexes(var1, var4, var0.length);
      Preconditions.checkPositionIndex(var3, var2);
      return (UnmodifiableListIterator<T>)(var2 == 0 ? emptyListIterator() : new Iterators.ArrayItr<>((T[])var0, var1, var2, var3));
   }

   public static <T> UnmodifiableIterator<T> singletonIterator(@ParametricNullness final T var0) {
      return new UnmodifiableIterator<T>() {
         boolean done;

         @Override
         public boolean hasNext() {
            return !this.done;
         }

         @ParametricNullness
         @Override
         public T next() {
            if (this.done) {
               throw new NoSuchElementException();
            } else {
               this.done = true;
               return (T)var0;
            }
         }
      };
   }

   public static <T> UnmodifiableIterator<T> forEnumeration(final Enumeration<T> var0) {
      Preconditions.checkNotNull(var0);
      return new UnmodifiableIterator<T>() {
         @Override
         public boolean hasNext() {
            return var0.hasMoreElements();
         }

         @ParametricNullness
         @Override
         public T next() {
            return (T)var0.nextElement();
         }
      };
   }

   public static <T> Enumeration<T> asEnumeration(final Iterator<T> var0) {
      Preconditions.checkNotNull(var0);
      return new Enumeration<T>() {
         @Override
         public boolean hasMoreElements() {
            return var0.hasNext();
         }

         @ParametricNullness
         @Override
         public T nextElement() {
            return (T)var0.next();
         }
      };
   }

   public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> var0) {
      return var0 instanceof Iterators.PeekingImpl ? (Iterators.PeekingImpl)var0 : new Iterators.PeekingImpl<>(var0);
   }

   @Deprecated
   public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Beta
   public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> var0, Comparator<? super T> var1) {
      Preconditions.checkNotNull(var0, "iterators");
      Preconditions.checkNotNull(var1, "comparator");
      return new Iterators.MergingIterator<>(var0, var1);
   }

   static <T> ListIterator<T> cast(Iterator<T> var0) {
      return (ListIterator<T>)var0;
   }

   private static final class ArrayItr<T> extends AbstractIndexedListIterator<T> {
      static final UnmodifiableListIterator<Object> EMPTY = new Iterators.ArrayItr<>(new Object[0], 0, 0, 0);
      private final T[] array;
      private final int offset;

      ArrayItr(T[] var1, int var2, int var3, int var4) {
         super(var3, var4);
         this.array = (T[])var1;
         this.offset = var2;
      }

      @ParametricNullness
      @Override
      protected T get(int var1) {
         return this.array[this.offset + var1];
      }
   }

   private static class ConcatenatedIterator<T> implements Iterator<T> {
      private Iterator<? extends T> toRemove;
      private Iterator<? extends T> iterator = Iterators.emptyIterator();
      private Iterator<? extends Iterator<? extends T>> topMetaIterator;
      private Deque<Iterator<? extends Iterator<? extends T>>> metaIterators;

      ConcatenatedIterator(Iterator<? extends Iterator<? extends T>> var1) {
         this.topMetaIterator = Preconditions.checkNotNull(var1);
      }

      private Iterator<? extends Iterator<? extends T>> getTopMetaIterator() {
         while (this.topMetaIterator == null || !this.topMetaIterator.hasNext()) {
            if (this.metaIterators == null || this.metaIterators.isEmpty()) {
               return null;
            }

            this.topMetaIterator = this.metaIterators.removeFirst();
         }

         return this.topMetaIterator;
      }

      @Override
      public boolean hasNext() {
         while (!Preconditions.checkNotNull(this.iterator).hasNext()) {
            this.topMetaIterator = this.getTopMetaIterator();
            if (this.topMetaIterator == null) {
               return false;
            }

            this.iterator = (Iterator<? extends T>)this.topMetaIterator.next();
            if (this.iterator instanceof Iterators.ConcatenatedIterator) {
               Iterators.ConcatenatedIterator var1 = (Iterators.ConcatenatedIterator)this.iterator;
               this.iterator = var1.iterator;
               if (this.metaIterators == null) {
                  this.metaIterators = new ArrayDeque<>();
               }

               this.metaIterators.addFirst(this.topMetaIterator);
               if (var1.metaIterators != null) {
                  while (!var1.metaIterators.isEmpty()) {
                     this.metaIterators.addFirst(var1.metaIterators.removeLast());
                  }
               }

               this.topMetaIterator = var1.topMetaIterator;
            }
         }

         return true;
      }

      @ParametricNullness
      @Override
      public T next() {
         if (this.hasNext()) {
            this.toRemove = this.iterator;
            return (T)this.iterator.next();
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         if (this.toRemove == null) {
            throw new IllegalStateException("no calls to next() since the last call to remove()");
         } else {
            this.toRemove.remove();
            this.toRemove = null;
         }
      }
   }

   private static enum EmptyModifiableIterator implements Iterator<Object> {
      INSTANCE;

      @Override
      public boolean hasNext() {
         return false;
      }

      @Override
      public Object next() {
         throw new NoSuchElementException();
      }

      @Override
      public void remove() {
         CollectPreconditions.checkRemove(false);
      }
   }

   private static class MergingIterator<T> extends UnmodifiableIterator<T> {
      final Queue<PeekingIterator<T>> queue;

      public MergingIterator(Iterable<? extends Iterator<? extends T>> var1, final Comparator<? super T> var2) {
         Comparator var3 = new Comparator<PeekingIterator<T>>(this) {
            public int compare(PeekingIterator<T> var1, PeekingIterator<T> var2x) {
               return var2.compare(var1.peek(), var2x.peek());
            }
         };
         this.queue = new PriorityQueue<>(2, var3);

         for (Iterator var5 : var1) {
            if (var5.hasNext()) {
               this.queue.add(Iterators.peekingIterator(var5));
            }
         }
      }

      @Override
      public boolean hasNext() {
         return !this.queue.isEmpty();
      }

      @ParametricNullness
      @Override
      public T next() {
         PeekingIterator var1 = this.queue.remove();
         Object var2 = var1.next();
         if (var1.hasNext()) {
            this.queue.add(var1);
         }

         return (T)var2;
      }
   }

   private static class PeekingImpl<E> implements PeekingIterator<E> {
      private final Iterator<? extends E> iterator;
      private boolean hasPeeked;
      private E peekedElement;

      public PeekingImpl(Iterator<? extends E> var1) {
         this.iterator = Preconditions.checkNotNull(var1);
      }

      @Override
      public boolean hasNext() {
         return this.hasPeeked || this.iterator.hasNext();
      }

      @ParametricNullness
      @Override
      public E next() {
         if (!this.hasPeeked) {
            return (E)this.iterator.next();
         } else {
            Object var1 = NullnessCasts.uncheckedCastNullableTToT(this.peekedElement);
            this.hasPeeked = false;
            this.peekedElement = null;
            return (E)var1;
         }
      }

      @Override
      public void remove() {
         Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
         this.iterator.remove();
      }

      @ParametricNullness
      @Override
      public E peek() {
         if (!this.hasPeeked) {
            this.peekedElement = (E)this.iterator.next();
            this.hasPeeked = true;
         }

         return NullnessCasts.uncheckedCastNullableTToT(this.peekedElement);
      }
   }
}
