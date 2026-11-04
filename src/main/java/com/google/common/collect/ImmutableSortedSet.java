package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E> implements SortedIterable<E>, NavigableSet<E> {
   static final int SPLITERATOR_CHARACTERISTICS = 1301;
   final transient Comparator<? super E> comparator;
   @LazyInit
   @GwtIncompatible
   transient ImmutableSortedSet<E> descendingSet;

   public static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> var0) {
      return CollectCollectors.toImmutableSortedSet(var0);
   }

   static <E> RegularImmutableSortedSet<E> emptySet(Comparator<? super E> var0) {
      return (RegularImmutableSortedSet<E>)(Ordering.natural().equals(var0)
         ? RegularImmutableSortedSet.NATURAL_EMPTY_SET
         : new RegularImmutableSortedSet<>(ImmutableList.of(), var0));
   }

   public static <E> ImmutableSortedSet<E> of() {
      return (ImmutableSortedSet<E>)RegularImmutableSortedSet.NATURAL_EMPTY_SET;
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0) {
      return new RegularImmutableSortedSet<>(ImmutableList.of((E)var0), Ordering.natural());
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1) {
      return construct(Ordering.natural(), 2, (E[])(new Comparable[]{var0, var1}));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2) {
      return construct(Ordering.natural(), 3, (E[])(new Comparable[]{var0, var1, var2}));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3) {
      return construct(Ordering.natural(), 4, (E[])(new Comparable[]{var0, var1, var2, var3}));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      return construct(Ordering.natural(), 5, (E[])(new Comparable[]{var0, var1, var2, var3, var4}));
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      Comparable[] var7 = new Comparable[6 + var6.length];
      var7[0] = var0;
      var7[1] = var1;
      var7[2] = var2;
      var7[3] = var3;
      var7[4] = var4;
      var7[5] = var5;
      System.arraycopy(var6, 0, var7, 6, var6.length);
      return construct(Ordering.natural(), var7.length, (E[])var7);
   }

   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] var0) {
      return construct(Ordering.natural(), var0.length, (E[])((Comparable[])var0.clone()));
   }

   public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> var0) {
      Ordering var1 = Ordering.natural();
      return copyOf(var1, var0);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Iterator<? extends E> var1) {
      return new ImmutableSortedSet.Builder<E>(var0).addAll(var1).build();
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Iterable<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      boolean var2 = SortedIterables.hasSameComparator(var0, var1);
      if (var2 && var1 instanceof ImmutableSortedSet) {
         ImmutableSortedSet var3 = (ImmutableSortedSet)var1;
         if (!var3.isPartialView()) {
            return var3;
         }
      }

      Object[] var4 = Iterables.toArray(var1);
      return construct(var0, var4.length, (E[])var4);
   }

   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> var0, Collection<? extends E> var1) {
      return copyOf(var0, var1);
   }

   public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> var0) {
      Comparator var1 = SortedIterables.comparator(var0);
      ImmutableList var2 = ImmutableList.copyOf(var0);
      return var2.isEmpty() ? emptySet(var1) : new RegularImmutableSortedSet<>(var2, var1);
   }

   static <E> ImmutableSortedSet<E> construct(Comparator<? super E> var0, int var1, E... var2) {
      if (var1 == 0) {
         return emptySet(var0);
      } else {
         ObjectArrays.checkElementsNotNull(var2, var1);
         Arrays.sort(var2, 0, var1, var0);
         int var3 = 1;

         for (int var4 = 1; var4 < var1; var4++) {
            Object var5 = var2[var4];
            Object var6 = var2[var3 - 1];
            if (var0.compare(var5, var6) != 0) {
               var2[var3++] = var5;
            }
         }

         Arrays.fill(var2, var3, var1, null);
         return new RegularImmutableSortedSet<>(ImmutableList.asImmutableList(var2, var3), var0);
      }
   }

   public static <E> ImmutableSortedSet.Builder<E> orderedBy(Comparator<E> var0) {
      return new ImmutableSortedSet.Builder<>(var0);
   }

   public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> reverseOrder() {
      return new ImmutableSortedSet.Builder<>(Collections.reverseOrder());
   }

   public static <E extends Comparable<?>> ImmutableSortedSet.Builder<E> naturalOrder() {
      return new ImmutableSortedSet.Builder<>(Ordering.natural());
   }

   int unsafeCompare(Object var1, Object var2) {
      return unsafeCompare(this.comparator, var1, var2);
   }

   static int unsafeCompare(Comparator<?> var0, Object var1, Object var2) {
      return var0.compare(var1, var2);
   }

   ImmutableSortedSet(Comparator<? super E> var1) {
      this.comparator = var1;
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.comparator;
   }

   @Override
   public abstract UnmodifiableIterator<E> iterator();

   public ImmutableSortedSet<E> headSet(E var1) {
      return this.headSet((E)var1, false);
   }

   public ImmutableSortedSet<E> headSet(E var1, boolean var2) {
      return this.headSetImpl(Preconditions.checkNotNull((E)var1), var2);
   }

   public ImmutableSortedSet<E> subSet(E var1, E var2) {
      return this.subSet((E)var1, true, (E)var2, false);
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> subSet(E var1, boolean var2, E var3, boolean var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      Preconditions.checkArgument(this.comparator.compare((E)var1, (E)var3) <= 0);
      return this.subSetImpl((E)var1, var2, (E)var3, var4);
   }

   public ImmutableSortedSet<E> tailSet(E var1) {
      return this.tailSet((E)var1, true);
   }

   public ImmutableSortedSet<E> tailSet(E var1, boolean var2) {
      return this.tailSetImpl(Preconditions.checkNotNull((E)var1), var2);
   }

   abstract ImmutableSortedSet<E> headSetImpl(E var1, boolean var2);

   abstract ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4);

   abstract ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2);

   @GwtIncompatible
   @Override
   public E lower(E var1) {
      return Iterators.getNext(this.headSet((E)var1, false).descendingIterator(), null);
   }

   @Override
   public E floor(E var1) {
      return Iterators.getNext(this.headSet((E)var1, true).descendingIterator(), null);
   }

   @Override
   public E ceiling(E var1) {
      return Iterables.getFirst(this.tailSet((E)var1, true), null);
   }

   @GwtIncompatible
   @Override
   public E higher(E var1) {
      return Iterables.getFirst(this.tailSet((E)var1, false), null);
   }

   @Override
   public E first() {
      return this.iterator().next();
   }

   @Override
   public E last() {
      return this.descendingIterator().next();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final E pollFirst() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final E pollLast() {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible
   public ImmutableSortedSet<E> descendingSet() {
      ImmutableSortedSet var1 = this.descendingSet;
      if (var1 == null) {
         var1 = this.descendingSet = this.createDescendingSet();
         var1.descendingSet = this;
      }

      return var1;
   }

   @GwtIncompatible
   abstract ImmutableSortedSet<E> createDescendingSet();

   @Override
   public Spliterator<E> spliterator() {
      return new AbstractSpliterator<E>((long)this.size(), 1365) {
         final UnmodifiableIterator<E> iterator = ImmutableSortedSet.this.iterator();

         @Override
         public boolean tryAdvance(Consumer<? super E> var1) {
            if (this.iterator.hasNext()) {
               var1.accept(this.iterator.next());
               return true;
            } else {
               return false;
            }
         }

         @Override
         public Comparator<? super E> getComparator() {
            return ImmutableSortedSet.this.comparator;
         }
      };
   }

   @GwtIncompatible
   public abstract UnmodifiableIterator<E> descendingIterator();

   abstract int indexOf(Object var1);

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializedForm");
   }

   @Override
   Object writeReplace() {
      return new ImmutableSortedSet.SerializedForm<>(this.comparator, this.toArray());
   }

   public static final class Builder<E> extends ImmutableSet.Builder<E> {
      private final Comparator<? super E> comparator;
      private E[] elements;
      private int n;

      public Builder(Comparator<? super E> var1) {
         super(true);
         this.comparator = Preconditions.checkNotNull(var1);
         this.elements = (E[])(new Object[4]);
         this.n = 0;
      }

      @Override
      void copy() {
         this.elements = Arrays.copyOf(this.elements, this.elements.length);
      }

      private void sortAndDedup() {
         if (this.n != 0) {
            Arrays.sort(this.elements, 0, this.n, this.comparator);
            int var1 = 1;

            for (int var2 = 1; var2 < this.n; var2++) {
               int var3 = this.comparator.compare(this.elements[var1 - 1], this.elements[var2]);
               if (var3 < 0) {
                  this.elements[var1++] = this.elements[var2];
               } else if (var3 > 0) {
                  String var4 = String.valueOf(this.comparator);
                  throw new AssertionError(
                     new StringBuilder(48 + String.valueOf(var4).length())
                        .append("Comparator ")
                        .append(var4)
                        .append(" compare method violates its contract")
                        .toString()
                  );
               }
            }

            Arrays.fill(this.elements, var1, this.n, null);
            this.n = var1;
         }
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> add(E var1) {
         Preconditions.checkNotNull(var1);
         this.copyIfNecessary();
         if (this.n == this.elements.length) {
            this.sortAndDedup();
            int var2 = ImmutableCollection.Builder.expandedCapacity(this.n, this.n + 1);
            if (var2 > this.elements.length) {
               this.elements = Arrays.copyOf(this.elements, var2);
            }
         }

         this.elements[this.n++] = (E)var1;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> add(E... var1) {
         ObjectArrays.checkElementsNotNull(var1);

         for (Object var5 : var1) {
            this.add((E)var5);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSortedSet.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableSortedSet.Builder<E> combine(ImmutableSet.Builder<E> var1) {
         this.copyIfNecessary();
         ImmutableSortedSet.Builder var2 = (ImmutableSortedSet.Builder)var1;

         for (int var3 = 0; var3 < var2.n; var3++) {
            this.add(var2.elements[var3]);
         }

         return this;
      }

      public ImmutableSortedSet<E> build() {
         this.sortAndDedup();
         if (this.n == 0) {
            return ImmutableSortedSet.emptySet(this.comparator);
         } else {
            this.forceCopy = true;
            return new RegularImmutableSortedSet<>(ImmutableList.asImmutableList(this.elements, this.n), this.comparator);
         }
      }
   }

   private static class SerializedForm<E> implements Serializable {
      final Comparator<? super E> comparator;
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      public SerializedForm(Comparator<? super E> var1, Object[] var2) {
         this.comparator = var1;
         this.elements = var2;
      }

      Object readResolve() {
         return new ImmutableSortedSet.Builder<>(this.comparator).add((E[])this.elements).build();
      }
   }
}
