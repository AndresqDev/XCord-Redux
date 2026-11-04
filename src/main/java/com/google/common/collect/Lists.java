package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Lists {
   private Lists() {
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList() {
      return new ArrayList<>();
   }

   @SafeVarargs
   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(E... var0) {
      Preconditions.checkNotNull(var0);
      int var1 = computeArrayListCapacity(var0.length);
      ArrayList var2 = new ArrayList(var1);
      Collections.addAll(var2, (E[])var0);
      return var2;
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(Iterable<? extends E> var0) {
      Preconditions.checkNotNull(var0);
      return var0 instanceof Collection ? new ArrayList<>((Collection<? extends E>)var0) : newArrayList(var0.iterator());
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> var0) {
      ArrayList var1 = newArrayList();
      Iterators.addAll(var1, var0);
      return var1;
   }

   @VisibleForTesting
   static int computeArrayListCapacity(int var0) {
      CollectPreconditions.checkNonnegative(var0, "arraySize");
      return Ints.saturatedCast(5L + (long)var0 + (long)(var0 / 10));
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayListWithCapacity(int var0) {
      CollectPreconditions.checkNonnegative(var0, "initialArraySize");
      return new ArrayList<>(var0);
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> ArrayList<E> newArrayListWithExpectedSize(int var0) {
      return new ArrayList<>(computeArrayListCapacity(var0));
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> LinkedList<E> newLinkedList() {
      return new LinkedList<>();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> var0) {
      LinkedList var1 = newLinkedList();
      Iterables.addAll(var1, var0);
      return var1;
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
      return new CopyOnWriteArrayList<>();
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> var0) {
      Object var1 = var0 instanceof Collection ? (Collection)var0 : newArrayList(var0);
      return new CopyOnWriteArrayList<>((Collection<? extends E>)var1);
   }

   public static <E> List<E> asList(@ParametricNullness E var0, E[] var1) {
      return new Lists.OnePlusArrayList<>((E)var0, (E[])var1);
   }

   public static <E> List<E> asList(@ParametricNullness E var0, @ParametricNullness E var1, E[] var2) {
      return new Lists.TwoPlusArrayList<>((E)var0, (E)var1, (E[])var2);
   }

   public static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> var0) {
      return CartesianList.create(var0);
   }

   @SafeVarargs
   public static <B> List<List<B>> cartesianProduct(List<? extends B>... var0) {
      return cartesianProduct(Arrays.asList(var0));
   }

   public static <F, T> List<T> transform(List<F> var0, Function<? super F, ? extends T> var1) {
      return (List<T>)(var0 instanceof RandomAccess ? new Lists.TransformingRandomAccessList<>(var0, var1) : new Lists.TransformingSequentialList<>(var0, var1));
   }

   public static <T> List<List<T>> partition(List<T> var0, int var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkArgument(var1 > 0);
      return (List<List<T>>)(var0 instanceof RandomAccess ? new Lists.RandomAccessPartition<>(var0, var1) : new Lists.Partition<>(var0, var1));
   }

   public static ImmutableList<Character> charactersOf(String var0) {
      return new Lists.StringAsImmutableList(Preconditions.checkNotNull(var0));
   }

   @Beta
   public static List<Character> charactersOf(CharSequence var0) {
      return new Lists.CharSequenceAsList(Preconditions.checkNotNull(var0));
   }

   public static <T> List<T> reverse(List<T> var0) {
      if (var0 instanceof ImmutableList) {
         return ((ImmutableList)var0).reverse();
      } else if (var0 instanceof Lists.ReverseList) {
         return ((Lists.ReverseList)var0).getForwardList();
      } else {
         return (List<T>)(var0 instanceof RandomAccess ? new Lists.RandomAccessReverseList<>(var0) : new Lists.ReverseList<>(var0));
      }
   }

   static int hashCodeImpl(List<?> var0) {
      int var1 = 1;

      for (Object var3 : var0) {
         var1 = 31 * var1 + (var3 == null ? 0 : var3.hashCode());
         var1 = ~(~var1);
      }

      return var1;
   }

   static boolean equalsImpl(List<?> var0, Object var1) {
      if (var1 == Preconditions.checkNotNull(var0)) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else {
         List var2 = (List)var1;
         int var3 = var0.size();
         if (var3 != var2.size()) {
            return false;
         } else if (var0 instanceof RandomAccess && var2 instanceof RandomAccess) {
            for (int var4 = 0; var4 < var3; var4++) {
               if (!Objects.equal(var0.get(var4), var2.get(var4))) {
                  return false;
               }
            }

            return true;
         } else {
            return Iterators.elementsEqual(var0.iterator(), var2.iterator());
         }
      }
   }

   static <E> boolean addAllImpl(List<E> var0, int var1, Iterable<? extends E> var2) {
      boolean var3 = false;
      ListIterator var4 = var0.listIterator(var1);

      for (Object var6 : var2) {
         var4.add(var6);
         var3 = true;
      }

      return var3;
   }

   static int indexOfImpl(List<?> var0, Object var1) {
      if (var0 instanceof RandomAccess) {
         return indexOfRandomAccess(var0, var1);
      } else {
         ListIterator var2 = var0.listIterator();

         while (var2.hasNext()) {
            if (Objects.equal(var1, var2.next())) {
               return var2.previousIndex();
            }
         }

         return -1;
      }
   }

   private static int indexOfRandomAccess(List<?> var0, Object var1) {
      int var2 = var0.size();
      if (var1 == null) {
         for (int var3 = 0; var3 < var2; var3++) {
            if (var0.get(var3) == null) {
               return var3;
            }
         }
      } else {
         for (int var4 = 0; var4 < var2; var4++) {
            if (var1.equals(var0.get(var4))) {
               return var4;
            }
         }
      }

      return -1;
   }

   static int lastIndexOfImpl(List<?> var0, Object var1) {
      if (var0 instanceof RandomAccess) {
         return lastIndexOfRandomAccess(var0, var1);
      } else {
         ListIterator var2 = var0.listIterator(var0.size());

         while (var2.hasPrevious()) {
            if (Objects.equal(var1, var2.previous())) {
               return var2.nextIndex();
            }
         }

         return -1;
      }
   }

   private static int lastIndexOfRandomAccess(List<?> var0, Object var1) {
      if (var1 == null) {
         for (int var2 = var0.size() - 1; var2 >= 0; var2--) {
            if (var0.get(var2) == null) {
               return var2;
            }
         }
      } else {
         for (int var3 = var0.size() - 1; var3 >= 0; var3--) {
            if (var1.equals(var0.get(var3))) {
               return var3;
            }
         }
      }

      return -1;
   }

   static <E> ListIterator<E> listIteratorImpl(List<E> var0, int var1) {
      return new Lists.AbstractListWrapper<E>(var0).listIterator(var1);
   }

   static <E> List<E> subListImpl(List<E> var0, int var1, int var2) {
      Object var3;
      if (var0 instanceof RandomAccess) {
         var3 = new Lists.RandomAccessListWrapper<E>(var0) {
            private static final long serialVersionUID = 0L;

            @Override
            public ListIterator<E> listIterator(int var1) {
               return this.backingList.listIterator(var1);
            }
         };
      } else {
         var3 = new Lists.AbstractListWrapper<E>(var0) {
            private static final long serialVersionUID = 0L;

            @Override
            public ListIterator<E> listIterator(int var1) {
               return this.backingList.listIterator(var1);
            }
         };
      }

      return var3.subList(var1, var2);
   }

   static <T> List<T> cast(Iterable<T> var0) {
      return (List<T>)var0;
   }

   private static class AbstractListWrapper<E> extends AbstractList<E> {
      final List<E> backingList;

      AbstractListWrapper(List<E> var1) {
         this.backingList = Preconditions.checkNotNull(var1);
      }

      @Override
      public void add(int var1, @ParametricNullness E var2) {
         this.backingList.add(var1, (E)var2);
      }

      @Override
      public boolean addAll(int var1, Collection<? extends E> var2) {
         return this.backingList.addAll(var1, var2);
      }

      @ParametricNullness
      @Override
      public E get(int var1) {
         return this.backingList.get(var1);
      }

      @ParametricNullness
      @Override
      public E remove(int var1) {
         return this.backingList.remove(var1);
      }

      @ParametricNullness
      @Override
      public E set(int var1, @ParametricNullness E var2) {
         return this.backingList.set(var1, (E)var2);
      }

      @Override
      public boolean contains(Object var1) {
         return this.backingList.contains(var1);
      }

      @Override
      public int size() {
         return this.backingList.size();
      }
   }

   private static final class CharSequenceAsList extends AbstractList<Character> {
      private final CharSequence sequence;

      CharSequenceAsList(CharSequence var1) {
         this.sequence = var1;
      }

      public Character get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.sequence.charAt(var1);
      }

      @Override
      public int size() {
         return this.sequence.length();
      }
   }

   private static class OnePlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
      @ParametricNullness
      final E first;
      final E[] rest;
      private static final long serialVersionUID = 0L;

      OnePlusArrayList(@ParametricNullness E var1, E[] var2) {
         this.first = (E)var1;
         this.rest = (E[])Preconditions.checkNotNull(var2);
      }

      @Override
      public int size() {
         return IntMath.saturatedAdd(this.rest.length, 1);
      }

      @ParametricNullness
      @Override
      public E get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return var1 == 0 ? this.first : this.rest[var1 - 1];
      }
   }

   private static class Partition<T> extends AbstractList<List<T>> {
      final List<T> list;
      final int size;

      Partition(List<T> var1, int var2) {
         this.list = var1;
         this.size = var2;
      }

      public List<T> get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         int var2 = var1 * this.size;
         int var3 = Math.min(var2 + this.size, this.list.size());
         return this.list.subList(var2, var3);
      }

      @Override
      public int size() {
         return IntMath.divide(this.list.size(), this.size, RoundingMode.CEILING);
      }

      @Override
      public boolean isEmpty() {
         return this.list.isEmpty();
      }
   }

   private static class RandomAccessListWrapper<E> extends Lists.AbstractListWrapper<E> implements RandomAccess {
      RandomAccessListWrapper(List<E> var1) {
         super(var1);
      }
   }

   private static class RandomAccessPartition<T> extends Lists.Partition<T> implements RandomAccess {
      RandomAccessPartition(List<T> var1, int var2) {
         super(var1, var2);
      }
   }

   private static class RandomAccessReverseList<T> extends Lists.ReverseList<T> implements RandomAccess {
      RandomAccessReverseList(List<T> var1) {
         super(var1);
      }
   }

   private static class ReverseList<T> extends AbstractList<T> {
      private final List<T> forwardList;

      ReverseList(List<T> var1) {
         this.forwardList = Preconditions.checkNotNull(var1);
      }

      List<T> getForwardList() {
         return this.forwardList;
      }

      private int reverseIndex(int var1) {
         int var2 = this.size();
         Preconditions.checkElementIndex(var1, var2);
         return var2 - 1 - var1;
      }

      private int reversePosition(int var1) {
         int var2 = this.size();
         Preconditions.checkPositionIndex(var1, var2);
         return var2 - var1;
      }

      @Override
      public void add(int var1, @ParametricNullness T var2) {
         this.forwardList.add(this.reversePosition(var1), (T)var2);
      }

      @Override
      public void clear() {
         this.forwardList.clear();
      }

      @ParametricNullness
      @Override
      public T remove(int var1) {
         return this.forwardList.remove(this.reverseIndex(var1));
      }

      @Override
      protected void removeRange(int var1, int var2) {
         this.subList(var1, var2).clear();
      }

      @ParametricNullness
      @Override
      public T set(int var1, @ParametricNullness T var2) {
         return this.forwardList.set(this.reverseIndex(var1), (T)var2);
      }

      @ParametricNullness
      @Override
      public T get(int var1) {
         return this.forwardList.get(this.reverseIndex(var1));
      }

      @Override
      public int size() {
         return this.forwardList.size();
      }

      @Override
      public List<T> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.size());
         return Lists.reverse(this.forwardList.subList(this.reversePosition(var2), this.reversePosition(var1)));
      }

      @Override
      public Iterator<T> iterator() {
         return this.listIterator();
      }

      @Override
      public ListIterator<T> listIterator(int var1) {
         int var2 = this.reversePosition(var1);
         final ListIterator var3 = this.forwardList.listIterator(var2);
         return new ListIterator<T>() {
            boolean canRemoveOrSet;

            @Override
            public void add(@ParametricNullness T var1) {
               var3.add(var1);
               var3.previous();
               this.canRemoveOrSet = false;
            }

            @Override
            public boolean hasNext() {
               return var3.hasPrevious();
            }

            @Override
            public boolean hasPrevious() {
               return var3.hasNext();
            }

            @ParametricNullness
            @Override
            public T next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return (T)var3.previous();
               }
            }

            @Override
            public int nextIndex() {
               return ReverseList.this.reversePosition(var3.nextIndex());
            }

            @ParametricNullness
            @Override
            public T previous() {
               if (!this.hasPrevious()) {
                  throw new NoSuchElementException();
               } else {
                  this.canRemoveOrSet = true;
                  return (T)var3.next();
               }
            }

            @Override
            public int previousIndex() {
               return this.nextIndex() - 1;
            }

            @Override
            public void remove() {
               CollectPreconditions.checkRemove(this.canRemoveOrSet);
               var3.remove();
               this.canRemoveOrSet = false;
            }

            @Override
            public void set(@ParametricNullness T var1) {
               Preconditions.checkState(this.canRemoveOrSet);
               var3.set(var1);
            }
         };
      }
   }

   private static final class StringAsImmutableList extends ImmutableList<Character> {
      private final String string;

      StringAsImmutableList(String var1) {
         this.string = var1;
      }

      @Override
      public int indexOf(Object var1) {
         return var1 instanceof Character ? this.string.indexOf((Character)var1) : -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         return var1 instanceof Character ? this.string.lastIndexOf((Character)var1) : -1;
      }

      @Override
      public ImmutableList<Character> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.size());
         return Lists.charactersOf(this.string.substring(var1, var2));
      }

      @Override
      boolean isPartialView() {
         return false;
      }

      public Character get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.string.charAt(var1);
      }

      @Override
      public int size() {
         return this.string.length();
      }
   }

   private static class TransformingRandomAccessList<F, T> extends AbstractList<T> implements Serializable, RandomAccess {
      final List<F> fromList;
      final Function<? super F, ? extends T> function;
      private static final long serialVersionUID = 0L;

      TransformingRandomAccessList(List<F> var1, Function<? super F, ? extends T> var2) {
         this.fromList = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public void clear() {
         this.fromList.clear();
      }

      @ParametricNullness
      @Override
      public T get(int var1) {
         return (T)this.function.apply(this.fromList.get(var1));
      }

      @Override
      public Iterator<T> iterator() {
         return this.listIterator();
      }

      @Override
      public ListIterator<T> listIterator(int var1) {
         return new TransformedListIterator<F, T>(this.fromList.listIterator(var1)) {
            @Override
            T transform(F var1) {
               return (T)TransformingRandomAccessList.this.function.apply((F)var1);
            }
         };
      }

      @Override
      public boolean isEmpty() {
         return this.fromList.isEmpty();
      }

      @Override
      public boolean removeIf(Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromList.removeIf(var2 -> var1.test((T)this.function.apply(var2)));
      }

      @ParametricNullness
      @Override
      public T remove(int var1) {
         return (T)this.function.apply(this.fromList.remove(var1));
      }

      @Override
      public int size() {
         return this.fromList.size();
      }
   }

   private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T> implements Serializable {
      final List<F> fromList;
      final Function<? super F, ? extends T> function;
      private static final long serialVersionUID = 0L;

      TransformingSequentialList(List<F> var1, Function<? super F, ? extends T> var2) {
         this.fromList = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public void clear() {
         this.fromList.clear();
      }

      @Override
      public int size() {
         return this.fromList.size();
      }

      @Override
      public ListIterator<T> listIterator(int var1) {
         return new TransformedListIterator<F, T>(this.fromList.listIterator(var1)) {
            @ParametricNullness
            @Override
            T transform(@ParametricNullness F var1) {
               return (T)TransformingSequentialList.this.function.apply((F)var1);
            }
         };
      }

      @Override
      public boolean removeIf(Predicate<? super T> var1) {
         Preconditions.checkNotNull(var1);
         return this.fromList.removeIf(var2 -> var1.test((T)this.function.apply(var2)));
      }
   }

   private static class TwoPlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
      @ParametricNullness
      final E first;
      @ParametricNullness
      final E second;
      final E[] rest;
      private static final long serialVersionUID = 0L;

      TwoPlusArrayList(@ParametricNullness E var1, @ParametricNullness E var2, E[] var3) {
         this.first = (E)var1;
         this.second = (E)var2;
         this.rest = (E[])Preconditions.checkNotNull(var3);
      }

      @Override
      public int size() {
         return IntMath.saturatedAdd(this.rest.length, 2);
      }

      @ParametricNullness
      @Override
      public E get(int var1) {
         switch (var1) {
            case 0:
               return this.first;
            case 1:
               return this.second;
            default:
               Preconditions.checkElementIndex(var1, this.size());
               return this.rest[var1 - 2];
         }
      }
   }
}
