package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Collections2 {
   private Collections2() {
   }

   public static <E> Collection<E> filter(Collection<E> var0, Predicate<? super E> var1) {
      return var0 instanceof Collections2.FilteredCollection
         ? ((Collections2.FilteredCollection)var0).createCombined(var1)
         : new Collections2.FilteredCollection<>(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1));
   }

   static boolean safeContains(Collection<?> var0, Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return var0.contains(var1);
      } catch (NullPointerException | ClassCastException var3) {
         return false;
      }
   }

   static boolean safeRemove(Collection<?> var0, Object var1) {
      Preconditions.checkNotNull(var0);

      try {
         return var0.remove(var1);
      } catch (NullPointerException | ClassCastException var3) {
         return false;
      }
   }

   public static <F, T> Collection<T> transform(Collection<F> var0, Function<? super F, T> var1) {
      return new Collections2.TransformedCollection<>(var0, var1);
   }

   static boolean containsAllImpl(Collection<?> var0, Collection<?> var1) {
      for (Object var3 : var1) {
         if (!var0.contains(var3)) {
            return false;
         }
      }

      return true;
   }

   static String toStringImpl(Collection<?> var0) {
      StringBuilder var1 = newStringBuilderForCollection(var0.size()).append('[');
      boolean var2 = true;

      for (Object var4 : var0) {
         if (!var2) {
            var1.append(", ");
         }

         var2 = false;
         if (var4 == var0) {
            var1.append("(this Collection)");
         } else {
            var1.append(var4);
         }
      }

      return var1.append(']').toString();
   }

   static StringBuilder newStringBuilderForCollection(int var0) {
      CollectPreconditions.checkNonnegative(var0, "size");
      return new StringBuilder((int)Math.min((long)var0 * 8L, 1073741824L));
   }

   @Beta
   public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> var0) {
      return orderedPermutations(var0, Ordering.natural());
   }

   @Beta
   public static <E> Collection<List<E>> orderedPermutations(Iterable<E> var0, Comparator<? super E> var1) {
      return new Collections2.OrderedPermutationCollection<>(var0, var1);
   }

   @Beta
   public static <E> Collection<List<E>> permutations(Collection<E> var0) {
      return new Collections2.PermutationCollection<>(ImmutableList.copyOf(var0));
   }

   private static boolean isPermutation(List<?> var0, List<?> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         HashMultiset var2 = HashMultiset.create(var0);
         HashMultiset var3 = HashMultiset.create(var1);
         return var2.equals(var3);
      }
   }

   static class FilteredCollection<E> extends AbstractCollection<E> {
      final Collection<E> unfiltered;
      final Predicate<? super E> predicate;

      FilteredCollection(Collection<E> var1, Predicate<? super E> var2) {
         this.unfiltered = var1;
         this.predicate = var2;
      }

      Collections2.FilteredCollection<E> createCombined(Predicate<? super E> var1) {
         return new Collections2.FilteredCollection<>(this.unfiltered, Predicates.and(this.predicate, var1));
      }

      @Override
      public boolean add(@ParametricNullness E var1) {
         Preconditions.checkArgument(this.predicate.apply((E)var1));
         return this.unfiltered.add((E)var1);
      }

      @Override
      public boolean addAll(Collection<? extends E> var1) {
         for (Object var3 : var1) {
            Preconditions.checkArgument(this.predicate.apply((E)var3));
         }

         return this.unfiltered.addAll(var1);
      }

      @Override
      public void clear() {
         Iterables.removeIf(this.unfiltered, this.predicate);
      }

      @Override
      public boolean contains(Object var1) {
         return Collections2.safeContains(this.unfiltered, var1) ? this.predicate.apply((E)var1) : false;
      }

      @Override
      public boolean containsAll(Collection<?> var1) {
         return Collections2.containsAllImpl(this, var1);
      }

      @Override
      public boolean isEmpty() {
         return !Iterables.any(this.unfiltered, this.predicate);
      }

      @Override
      public Iterator<E> iterator() {
         return Iterators.filter(this.unfiltered.iterator(), this.predicate);
      }

      @Override
      public Spliterator<E> spliterator() {
         return CollectSpliterators.filter(this.unfiltered.spliterator(), this.predicate);
      }

      @Override
      public void forEach(Consumer<? super E> var1) {
         Preconditions.checkNotNull(var1);
         this.unfiltered.forEach(var2 -> {
            if (this.predicate.test(var2)) {
               var1.accept(var2);
            }
         });
      }

      @Override
      public boolean remove(Object var1) {
         return this.contains(var1) && this.unfiltered.remove(var1);
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         return this.removeIf(var1::contains);
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return this.removeIf(var1x -> !var1.contains(var1x));
      }

      @Override
      public boolean removeIf(java.util.function.Predicate<? super E> var1) {
         Preconditions.checkNotNull(var1);
         return this.unfiltered.removeIf(var2 -> this.predicate.apply(var2) && var1.test(var2));
      }

      @Override
      public int size() {
         int var1 = 0;

         for (Object var3 : this.unfiltered) {
            if (this.predicate.apply((E)var3)) {
               var1++;
            }
         }

         return var1;
      }

      @Override
      public Object[] toArray() {
         return Lists.newArrayList(this.iterator()).toArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])Lists.newArrayList(this.iterator()).toArray(var1);
      }
   }

   private static final class OrderedPermutationCollection<E> extends AbstractCollection<List<E>> {
      final ImmutableList<E> inputList;
      final Comparator<? super E> comparator;
      final int size;

      OrderedPermutationCollection(Iterable<E> var1, Comparator<? super E> var2) {
         this.inputList = ImmutableList.sortedCopyOf(var2, var1);
         this.comparator = var2;
         this.size = calculateSize(this.inputList, var2);
      }

      private static <E> int calculateSize(List<E> var0, Comparator<? super E> var1) {
         int var2 = 1;
         int var3 = 1;

         int var4;
         for (var4 = 1; var3 < var0.size(); var4++) {
            int var5 = var1.compare(var0.get(var3 - 1), var0.get(var3));
            if (var5 < 0) {
               var2 = IntMath.saturatedMultiply(var2, IntMath.binomial(var3, var4));
               var4 = 0;
               if (var2 == Integer.MAX_VALUE) {
                  return Integer.MAX_VALUE;
               }
            }

            var3++;
         }

         return IntMath.saturatedMultiply(var2, IntMath.binomial(var3, var4));
      }

      @Override
      public int size() {
         return this.size;
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public Iterator<List<E>> iterator() {
         return new Collections2.OrderedPermutationIterator<>(this.inputList, this.comparator);
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof List) {
            List var2 = (List)var1;
            return Collections2.isPermutation(this.inputList, var2);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.inputList);
         return new StringBuilder(30 + String.valueOf(var1).length()).append("orderedPermutationCollection(").append(var1).append(")").toString();
      }
   }

   private static final class OrderedPermutationIterator<E> extends AbstractIterator<List<E>> {
      List<E> nextPermutation;
      final Comparator<? super E> comparator;

      OrderedPermutationIterator(List<E> var1, Comparator<? super E> var2) {
         this.nextPermutation = Lists.newArrayList(var1);
         this.comparator = var2;
      }

      protected List<E> computeNext() {
         if (this.nextPermutation == null) {
            return this.endOfData();
         } else {
            ImmutableList var1 = ImmutableList.copyOf(this.nextPermutation);
            this.calculateNextPermutation();
            return var1;
         }
      }

      void calculateNextPermutation() {
         int var1 = this.findNextJ();
         if (var1 == -1) {
            this.nextPermutation = null;
         } else {
            Objects.requireNonNull(this.nextPermutation);
            int var2 = this.findNextL(var1);
            Collections.swap(this.nextPermutation, var1, var2);
            int var3 = this.nextPermutation.size();
            Collections.reverse(this.nextPermutation.subList(var1 + 1, var3));
         }
      }

      int findNextJ() {
         Objects.requireNonNull(this.nextPermutation);

         for (int var1 = this.nextPermutation.size() - 2; var1 >= 0; var1--) {
            if (this.comparator.compare(this.nextPermutation.get(var1), this.nextPermutation.get(var1 + 1)) < 0) {
               return var1;
            }
         }

         return -1;
      }

      int findNextL(int var1) {
         Objects.requireNonNull(this.nextPermutation);
         Object var2 = this.nextPermutation.get(var1);

         for (int var3 = this.nextPermutation.size() - 1; var3 > var1; var3--) {
            if (this.comparator.compare((E)var2, this.nextPermutation.get(var3)) < 0) {
               return var3;
            }
         }

         throw new AssertionError("this statement should be unreachable");
      }
   }

   private static final class PermutationCollection<E> extends AbstractCollection<List<E>> {
      final ImmutableList<E> inputList;

      PermutationCollection(ImmutableList<E> var1) {
         this.inputList = var1;
      }

      @Override
      public int size() {
         return IntMath.factorial(this.inputList.size());
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public Iterator<List<E>> iterator() {
         return new Collections2.PermutationIterator<>(this.inputList);
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof List) {
            List var2 = (List)var1;
            return Collections2.isPermutation(this.inputList, var2);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.inputList);
         return new StringBuilder(14 + String.valueOf(var1).length()).append("permutations(").append(var1).append(")").toString();
      }
   }

   private static class PermutationIterator<E> extends AbstractIterator<List<E>> {
      final List<E> list;
      final int[] c;
      final int[] o;
      int j;

      PermutationIterator(List<E> var1) {
         this.list = new ArrayList<>(var1);
         int var2 = var1.size();
         this.c = new int[var2];
         this.o = new int[var2];
         Arrays.fill(this.c, 0);
         Arrays.fill(this.o, 1);
         this.j = Integer.MAX_VALUE;
      }

      protected List<E> computeNext() {
         if (this.j <= 0) {
            return this.endOfData();
         } else {
            ImmutableList var1 = ImmutableList.copyOf(this.list);
            this.calculateNextPermutation();
            return var1;
         }
      }

      void calculateNextPermutation() {
         this.j = this.list.size() - 1;
         int var1 = 0;
         if (this.j != -1) {
            while (true) {
               int var2 = this.c[this.j] + this.o[this.j];
               if (var2 >= 0) {
                  if (var2 != this.j + 1) {
                     Collections.swap(this.list, this.j - this.c[this.j] + var1, this.j - var2 + var1);
                     this.c[this.j] = var2;
                     break;
                  }

                  if (this.j == 0) {
                     break;
                  }

                  var1++;
                  this.switchDirection();
               } else {
                  this.switchDirection();
               }
            }
         }
      }

      void switchDirection() {
         this.o[this.j] = -this.o[this.j];
         this.j--;
      }
   }

   static class TransformedCollection<F, T> extends AbstractCollection<T> {
      final Collection<F> fromCollection;
      final Function<? super F, ? extends T> function;

      TransformedCollection(Collection<F> var1, Function<? super F, ? extends T> var2) {
         this.fromCollection = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public void clear() {
         this.fromCollection.clear();
      }

      @Override
      public boolean isEmpty() {
         return this.fromCollection.isEmpty();
      }

      @Override
      public Iterator<T> iterator() {
         return Iterators.transform(this.fromCollection.iterator(), this.function);
      }

      @Override
      public Spliterator<T> spliterator() {
         return CollectSpliterators.map(this.fromCollection.spliterator(), this.function);
      }

      @Override
      public void forEach(Consumer<? super T> var1) {
         Preconditions.checkNotNull(var1);
         this.fromCollection.forEach(var2 -> var1.accept((T)this.function.apply(var2)));
      }

      @Override
      public boolean removeIf(java.util.function.Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromCollection.removeIf(var2 -> var1.test((T)this.function.apply(var2)));
      }

      @Override
      public int size() {
         return this.fromCollection.size();
      }
   }
}
