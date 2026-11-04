package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Multisets {
   private Multisets() {
   }

   public static <T, E, M extends Multiset<E>> Collector<T, ?, M> toMultiset(Function<? super T, E> var0, ToIntFunction<? super T> var1, Supplier<M> var2) {
      return CollectCollectors.toMultiset(var0, var1, var2);
   }

   public static <E> Multiset<E> unmodifiableMultiset(Multiset<? extends E> var0) {
      return (Multiset<E>)(!(var0 instanceof Multisets.UnmodifiableMultiset) && !(var0 instanceof ImmutableMultiset)
         ? new Multisets.UnmodifiableMultiset<>(Preconditions.checkNotNull(var0))
         : var0);
   }

   @Deprecated
   public static <E> Multiset<E> unmodifiableMultiset(ImmutableMultiset<E> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Beta
   public static <E> SortedMultiset<E> unmodifiableSortedMultiset(SortedMultiset<E> var0) {
      return new UnmodifiableSortedMultiset<>(Preconditions.checkNotNull(var0));
   }

   public static <E> Multiset.Entry<E> immutableEntry(@ParametricNullness E var0, int var1) {
      return new Multisets.ImmutableEntry<>((E)var0, var1);
   }

   @Beta
   public static <E> Multiset<E> filter(Multiset<E> var0, Predicate<? super E> var1) {
      if (var0 instanceof Multisets.FilteredMultiset) {
         Multisets.FilteredMultiset var2 = (Multisets.FilteredMultiset)var0;
         Predicate var3 = Predicates.and(var2.predicate, var1);
         return new Multisets.FilteredMultiset<>(var2.unfiltered, var3);
      } else {
         return new Multisets.FilteredMultiset<>(var0, var1);
      }
   }

   static int inferDistinctElements(Iterable<?> var0) {
      return var0 instanceof Multiset ? ((Multiset)var0).elementSet().size() : 11;
   }

   @Beta
   public static <E> Multiset<E> union(final Multiset<? extends E> var0, final Multiset<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Multisets.ViewMultiset<E>() {
         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) || var1.contains(var1x);
         }

         @Override
         public boolean isEmpty() {
            return var0.isEmpty() && var1.isEmpty();
         }

         @Override
         public int count(Object var1x) {
            return Math.max(var0.count(var1x), var1.count(var1x));
         }

         @Override
         Set<E> createElementSet() {
            return Sets.union(var0.elementSet(), var1.elementSet());
         }

         @Override
         Iterator<E> elementIterator() {
            throw new AssertionError("should never be called");
         }

         @Override
         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            final Iterator var2 = var1.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  if (var1x.hasNext()) {
                     Multiset.Entry var4 = (Multiset.Entry)var1x.next();
                     Object var5 = var4.getElement();
                     int var3 = Math.max(var4.getCount(), var1.count(var5));
                     return Multisets.immutableEntry((E)var5, var3);
                  } else {
                     while (var2.hasNext()) {
                        Multiset.Entry var1xx = (Multiset.Entry)var2.next();
                        Object var2x = var1xx.getElement();
                        if (!var0.contains(var2x)) {
                           return Multisets.immutableEntry((E)var2x, var1xx.getCount());
                        }
                     }

                     return this.endOfData();
                  }
               }
            };
         }
      };
   }

   public static <E> Multiset<E> intersection(final Multiset<E> var0, final Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Multisets.ViewMultiset<E>() {
         @Override
         public int count(Object var1x) {
            int var2 = var0.count(var1x);
            return var2 == 0 ? 0 : Math.min(var2, var1.count(var1x));
         }

         @Override
         Set<E> createElementSet() {
            return Sets.intersection(var0.elementSet(), var1.elementSet());
         }

         @Override
         Iterator<E> elementIterator() {
            throw new AssertionError("should never be called");
         }

         @Override
         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  while (var1x.hasNext()) {
                     Multiset.Entry var1xx = (Multiset.Entry)var1x.next();
                     Object var2 = var1xx.getElement();
                     int var3 = Math.min(var1xx.getCount(), var1.count(var2));
                     if (var3 > 0) {
                        return Multisets.immutableEntry((E)var2, var3);
                     }
                  }

                  return this.endOfData();
               }
            };
         }
      };
   }

   @Beta
   public static <E> Multiset<E> sum(final Multiset<? extends E> var0, final Multiset<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Multisets.ViewMultiset<E>() {
         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) || var1.contains(var1x);
         }

         @Override
         public boolean isEmpty() {
            return var0.isEmpty() && var1.isEmpty();
         }

         @Override
         public int size() {
            return IntMath.saturatedAdd(var0.size(), var1.size());
         }

         @Override
         public int count(Object var1x) {
            return var0.count(var1x) + var1.count(var1x);
         }

         @Override
         Set<E> createElementSet() {
            return Sets.union(var0.elementSet(), var1.elementSet());
         }

         @Override
         Iterator<E> elementIterator() {
            throw new AssertionError("should never be called");
         }

         @Override
         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            final Iterator var2 = var1.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  if (var1x.hasNext()) {
                     Multiset.Entry var4 = (Multiset.Entry)var1x.next();
                     Object var5 = var4.getElement();
                     int var3 = var4.getCount() + var1.count(var5);
                     return Multisets.immutableEntry((E)var5, var3);
                  } else {
                     while (var2.hasNext()) {
                        Multiset.Entry var1xx = (Multiset.Entry)var2.next();
                        Object var2x = var1xx.getElement();
                        if (!var0.contains(var2x)) {
                           return Multisets.immutableEntry((E)var2x, var1xx.getCount());
                        }
                     }

                     return this.endOfData();
                  }
               }
            };
         }
      };
   }

   @Beta
   public static <E> Multiset<E> difference(final Multiset<E> var0, final Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Multisets.ViewMultiset<E>() {
         @Override
         public int count(Object var1x) {
            int var2 = var0.count(var1x);
            return var2 == 0 ? 0 : Math.max(0, var2 - var1.count(var1x));
         }

         @Override
         public void clear() {
            throw new UnsupportedOperationException();
         }

         @Override
         Iterator<E> elementIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            return new AbstractIterator<E>() {
               @Override
               protected E computeNext() {
                  while (var1x.hasNext()) {
                     Multiset.Entry var1xx = (Multiset.Entry)var1x.next();
                     Object var2 = var1xx.getElement();
                     if (var1xx.getCount() > var1.count(var2)) {
                        return (E)var2;
                     }
                  }

                  return (E)this.endOfData();
               }
            };
         }

         @Override
         Iterator<Multiset.Entry<E>> entryIterator() {
            final Iterator var1x = var0.entrySet().iterator();
            return new AbstractIterator<Multiset.Entry<E>>() {
               protected Multiset.Entry<E> computeNext() {
                  while (var1x.hasNext()) {
                     Multiset.Entry var1xx = (Multiset.Entry)var1x.next();
                     Object var2 = var1xx.getElement();
                     int var3 = var1xx.getCount() - var1.count(var2);
                     if (var3 > 0) {
                        return Multisets.immutableEntry((E)var2, var3);
                     }
                  }

                  return this.endOfData();
               }
            };
         }

         @Override
         int distinctElements() {
            return Iterators.size(this.entryIterator());
         }
      };
   }

   @CanIgnoreReturnValue
   public static boolean containsOccurrences(Multiset<?> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      for (Multiset.Entry var3 : var1.entrySet()) {
         int var4 = var0.count(var3.getElement());
         if (var4 < var3.getCount()) {
            return false;
         }
      }

      return true;
   }

   @CanIgnoreReturnValue
   public static boolean retainOccurrences(Multiset<?> var0, Multiset<?> var1) {
      return retainOccurrencesImpl(var0, var1);
   }

   private static <E> boolean retainOccurrencesImpl(Multiset<E> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Iterator var2 = var0.entrySet().iterator();
      boolean var3 = false;

      while (var2.hasNext()) {
         Multiset.Entry var4 = (Multiset.Entry)var2.next();
         int var5 = var1.count(var4.getElement());
         if (var5 == 0) {
            var2.remove();
            var3 = true;
         } else if (var5 < var4.getCount()) {
            var0.setCount(var4.getElement(), var5);
            var3 = true;
         }
      }

      return var3;
   }

   @CanIgnoreReturnValue
   public static boolean removeOccurrences(Multiset<?> var0, Iterable<?> var1) {
      if (var1 instanceof Multiset) {
         return removeOccurrences(var0, (Multiset<?>)var1);
      } else {
         Preconditions.checkNotNull(var0);
         Preconditions.checkNotNull(var1);
         boolean var2 = false;

         for (Object var4 : var1) {
            var2 |= var0.remove(var4);
         }

         return var2;
      }
   }

   @CanIgnoreReturnValue
   public static boolean removeOccurrences(Multiset<?> var0, Multiset<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = false;
      Iterator var3 = var0.entrySet().iterator();

      while (var3.hasNext()) {
         Multiset.Entry var4 = (Multiset.Entry)var3.next();
         int var5 = var1.count(var4.getElement());
         if (var5 >= var4.getCount()) {
            var3.remove();
            var2 = true;
         } else if (var5 > 0) {
            var0.remove(var4.getElement(), var5);
            var2 = true;
         }
      }

      return var2;
   }

   static boolean equalsImpl(Multiset<?> var0, Object var1) {
      if (var1 == var0) {
         return true;
      } else if (var1 instanceof Multiset) {
         Multiset var2 = (Multiset)var1;
         if (var0.size() == var2.size() && var0.entrySet().size() == var2.entrySet().size()) {
            for (Multiset.Entry var4 : var2.entrySet()) {
               if (var0.count(var4.getElement()) != var4.getCount()) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static <E> boolean addAllImpl(Multiset<E> var0, Collection<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (var1 instanceof Multiset) {
         return addAllImpl(var0, cast(var1));
      } else {
         return var1.isEmpty() ? false : Iterators.addAll(var0, var1.iterator());
      }
   }

   private static <E> boolean addAllImpl(Multiset<E> var0, Multiset<? extends E> var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         var1.forEachEntry(var0::add);
         return true;
      }
   }

   static boolean removeAllImpl(Multiset<?> var0, Collection<?> var1) {
      Object var2 = var1 instanceof Multiset ? ((Multiset)var1).elementSet() : var1;
      return var0.elementSet().removeAll((Collection<?>)var2);
   }

   static boolean retainAllImpl(Multiset<?> var0, Collection<?> var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = var1 instanceof Multiset ? ((Multiset)var1).elementSet() : var1;
      return var0.elementSet().retainAll((Collection<?>)var2);
   }

   static <E> int setCountImpl(Multiset<E> var0, @ParametricNullness E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "count");
      int var3 = var0.count(var1);
      int var4 = var2 - var3;
      if (var4 > 0) {
         var0.add(var1, var4);
      } else if (var4 < 0) {
         var0.remove(var1, -var4);
      }

      return var3;
   }

   static <E> boolean setCountImpl(Multiset<E> var0, @ParametricNullness E var1, int var2, int var3) {
      CollectPreconditions.checkNonnegative(var2, "oldCount");
      CollectPreconditions.checkNonnegative(var3, "newCount");
      if (var0.count(var1) == var2) {
         var0.setCount(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   static <E> Iterator<E> elementIterator(Iterator<Multiset.Entry<E>> var0) {
      return new TransformedIterator<Multiset.Entry<E>, E>(var0) {
         @ParametricNullness
         E transform(Multiset.Entry<E> var1) {
            return (E)var1.getElement();
         }
      };
   }

   static <E> Iterator<E> iteratorImpl(Multiset<E> var0) {
      return new Multisets.MultisetIteratorImpl<>(var0, var0.entrySet().iterator());
   }

   static <E> Spliterator<E> spliteratorImpl(Multiset<E> var0) {
      Spliterator var1 = var0.entrySet().spliterator();
      return CollectSpliterators.flatMap(
         var1,
         var0x -> (Spliterator<E>)Collections.nCopies(var0x.getCount(), var0x.getElement()).spliterator(),
         64 | var1.characteristics() & 1296,
         (long)var0.size()
      );
   }

   static int linearTimeSizeImpl(Multiset<?> var0) {
      long var1 = 0L;

      for (Multiset.Entry var4 : var0.entrySet()) {
         var1 += (long)var4.getCount();
      }

      return Ints.saturatedCast(var1);
   }

   static <T> Multiset<T> cast(Iterable<T> var0) {
      return (Multiset<T>)var0;
   }

   @Beta
   public static <E> ImmutableMultiset<E> copyHighestCountFirst(Multiset<E> var0) {
      Multiset.Entry[] var1 = var0.entrySet().toArray(new Multiset.Entry[0]);
      Arrays.sort(var1, Multisets.DecreasingCount.INSTANCE);
      return ImmutableMultiset.copyFromEntries(Arrays.asList(var1));
   }

   abstract static class AbstractEntry<E> implements Multiset.Entry<E> {
      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Multiset.Entry)) {
            return false;
         } else {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            return this.getCount() == var2.getCount() && Objects.equal(this.getElement(), var2.getElement());
         }
      }

      @Override
      public int hashCode() {
         Object var1 = this.getElement();
         return (var1 == null ? 0 : var1.hashCode()) ^ this.getCount();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.getElement());
         int var2 = this.getCount();
         return var2 == 1 ? var1 : new StringBuilder(14 + String.valueOf(var1).length()).append(var1).append(" x ").append(var2).toString();
      }
   }

   private static final class DecreasingCount implements Comparator<Multiset.Entry<?>> {
      static final Multisets.DecreasingCount INSTANCE = new Multisets.DecreasingCount();

      public int compare(Multiset.Entry<?> var1, Multiset.Entry<?> var2) {
         return var2.getCount() - var1.getCount();
      }
   }

   abstract static class ElementSet<E> extends Sets.ImprovedAbstractSet<E> {
      abstract Multiset<E> multiset();

      @Override
      public void clear() {
         this.multiset().clear();
      }

      @Override
      public boolean contains(Object var1) {
         return this.multiset().contains(var1);
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return this.multiset().containsAll(var1);
      }

      @Override
      public boolean isEmpty() {
         return this.multiset().isEmpty();
      }

      @Override
      public abstract Iterator<E> iterator();

      @Override
      public boolean remove(Object var1) {
         return this.multiset().remove(var1, Integer.MAX_VALUE) > 0;
      }

      @Override
      public int size() {
         return this.multiset().entrySet().size();
      }
   }

   abstract static class EntrySet<E> extends Sets.ImprovedAbstractSet<Multiset.Entry<E>> {
      abstract Multiset<E> multiset();

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Multiset.Entry) {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            if (var2.getCount() <= 0) {
               return false;
            } else {
               int var3 = this.multiset().count(var2.getElement());
               return var3 == var2.getCount();
            }
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object var1) {
         if (var1 instanceof Multiset.Entry) {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            Object var3 = var2.getElement();
            int var4 = var2.getCount();
            if (var4 != 0) {
               Multiset var5 = this.multiset();
               return var5.setCount(var3, var4, 0);
            }
         }

         return false;
      }

      @Override
      public void clear() {
         this.multiset().clear();
      }
   }

   private static final class FilteredMultiset<E> extends Multisets.ViewMultiset<E> {
      final Multiset<E> unfiltered;
      final Predicate<? super E> predicate;

      FilteredMultiset(Multiset<E> var1, Predicate<? super E> var2) {
         this.unfiltered = Preconditions.checkNotNull(var1);
         this.predicate = Preconditions.checkNotNull(var2);
      }

      public UnmodifiableIterator<E> iterator() {
         return Iterators.filter(this.unfiltered.iterator(), this.predicate);
      }

      @Override
      Set<E> createElementSet() {
         return Sets.filter(this.unfiltered.elementSet(), this.predicate);
      }

      @Override
      Iterator<E> elementIterator() {
         throw new AssertionError("should never be called");
      }

      @Override
      Set<Multiset.Entry<E>> createEntrySet() {
         return Sets.filter(this.unfiltered.entrySet(), new Predicate<Multiset.Entry<E>>() {
            public boolean apply(Multiset.Entry<E> var1) {
               return FilteredMultiset.this.predicate.apply((E)var1.getElement());
            }
         });
      }

      @Override
      Iterator<Multiset.Entry<E>> entryIterator() {
         throw new AssertionError("should never be called");
      }

      @Override
      public int count(Object var1) {
         int var2 = this.unfiltered.count(var1);
         if (var2 > 0) {
            return this.predicate.apply((E)var1) ? var2 : 0;
         } else {
            return 0;
         }
      }

      @Override
      public int add(@ParametricNullness E var1, int var2) {
         Preconditions.checkArgument(this.predicate.apply((E)var1), "Element %s does not match predicate %s", var1, this.predicate);
         return this.unfiltered.add((E)var1, var2);
      }

      @Override
      public int remove(Object var1, int var2) {
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            return this.contains(var1) ? this.unfiltered.remove(var1, var2) : 0;
         }
      }
   }

   static class ImmutableEntry<E> extends Multisets.AbstractEntry<E> implements Serializable {
      @ParametricNullness
      private final E element;
      private final int count;
      private static final long serialVersionUID = 0L;

      ImmutableEntry(@ParametricNullness E var1, int var2) {
         this.element = (E)var1;
         this.count = var2;
         CollectPreconditions.checkNonnegative(var2, "count");
      }

      @ParametricNullness
      @Override
      public final E getElement() {
         return this.element;
      }

      @Override
      public final int getCount() {
         return this.count;
      }

      public Multisets.ImmutableEntry<E> nextInBucket() {
         return null;
      }
   }

   static final class MultisetIteratorImpl<E> implements Iterator<E> {
      private final Multiset<E> multiset;
      private final Iterator<Multiset.Entry<E>> entryIterator;
      private Multiset.Entry<E> currentEntry;
      private int laterCount;
      private int totalCount;
      private boolean canRemove;

      MultisetIteratorImpl(Multiset<E> var1, Iterator<Multiset.Entry<E>> var2) {
         this.multiset = var1;
         this.entryIterator = var2;
      }

      @Override
      public boolean hasNext() {
         return this.laterCount > 0 || this.entryIterator.hasNext();
      }

      @ParametricNullness
      @Override
      public E next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if (this.laterCount == 0) {
               this.currentEntry = this.entryIterator.next();
               this.totalCount = this.laterCount = this.currentEntry.getCount();
            }

            this.laterCount--;
            this.canRemove = true;
            return java.util.Objects.requireNonNull(this.currentEntry).getElement();
         }
      }

      @Override
      public void remove() {
         CollectPreconditions.checkRemove(this.canRemove);
         if (this.totalCount == 1) {
            this.entryIterator.remove();
         } else {
            this.multiset.remove(java.util.Objects.requireNonNull(this.currentEntry).getElement());
         }

         this.totalCount--;
         this.canRemove = false;
      }
   }

   static class UnmodifiableMultiset<E> extends ForwardingMultiset<E> implements Serializable {
      final Multiset<? extends E> delegate;
      transient Set<E> elementSet;
      transient Set<Multiset.Entry<E>> entrySet;
      private static final long serialVersionUID = 0L;

      UnmodifiableMultiset(Multiset<? extends E> var1) {
         this.delegate = var1;
      }

      @Override
      protected Multiset<E> delegate() {
         return (Multiset<E>)this.delegate;
      }

      Set<E> createElementSet() {
         return Collections.unmodifiableSet(this.delegate.elementSet());
      }

      @Override
      public Set<E> elementSet() {
         Set var1 = this.elementSet;
         return var1 == null ? (this.elementSet = this.createElementSet()) : var1;
      }

      @Override
      public Set<Multiset.Entry<E>> entrySet() {
         Set var1 = this.entrySet;
         return var1 == null ? (this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())) : var1;
      }

      @Override
      public Iterator<E> iterator() {
         return Iterators.unmodifiableIterator(this.delegate.iterator());
      }

      @Override
      public boolean add(@ParametricNullness E var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int add(@ParametricNullness E var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int remove(Object var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public int setCount(@ParametricNullness E var1, int var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean setCount(@ParametricNullness E var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }
   }

   private abstract static class ViewMultiset<E> extends AbstractMultiset<E> {
      private ViewMultiset() {
      }

      @Override
      public int size() {
         return Multisets.linearTimeSizeImpl(this);
      }

      @Override
      public void clear() {
         this.elementSet().clear();
      }

      @Override
      public Iterator<E> iterator() {
         return Multisets.iteratorImpl(this);
      }

      @Override
      int distinctElements() {
         return this.elementSet().size();
      }
   }
}
