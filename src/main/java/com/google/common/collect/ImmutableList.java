package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.InlineMe;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, RandomAccess {
   public static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
      return CollectCollectors.toImmutableList();
   }

   public static <E> ImmutableList<E> of() {
      return (ImmutableList<E>)RegularImmutableList.EMPTY;
   }

   public static <E> ImmutableList<E> of(E var0) {
      return new SingletonImmutableList<>((E)var0);
   }

   public static <E> ImmutableList<E> of(E var0, E var1) {
      return construct(var0, var1);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2) {
      return construct(var0, var1, var2);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3) {
      return construct(var0, var1, var2, var3);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4) {
      return construct(var0, var1, var2, var3, var4);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5) {
      return construct(var0, var1, var2, var3, var4, var5);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6) {
      return construct(var0, var1, var2, var3, var4, var5, var6);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6, E var7) {
      return construct(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6, E var7, E var8) {
      return construct(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6, E var7, E var8, E var9) {
      return construct(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6, E var7, E var8, E var9, E var10) {
      return construct(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @SafeVarargs
   public static <E> ImmutableList<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E var6, E var7, E var8, E var9, E var10, E var11, E... var12) {
      Preconditions.checkArgument(var12.length <= 2147483635, "the total number of elements must fit in an int");
      Object[] var13 = new Object[12 + var12.length];
      var13[0] = var0;
      var13[1] = var1;
      var13[2] = var2;
      var13[3] = var3;
      var13[4] = var4;
      var13[5] = var5;
      var13[6] = var6;
      var13[7] = var7;
      var13[8] = var8;
      var13[9] = var9;
      var13[10] = var10;
      var13[11] = var11;
      System.arraycopy(var12, 0, var13, 12, var12.length);
      return construct(var13);
   }

   public static <E> ImmutableList<E> copyOf(Iterable<? extends E> var0) {
      Preconditions.checkNotNull(var0);
      return var0 instanceof Collection ? copyOf((Collection<? extends E>)var0) : copyOf(var0.iterator());
   }

   public static <E> ImmutableList<E> copyOf(Collection<? extends E> var0) {
      if (var0 instanceof ImmutableCollection) {
         ImmutableList var1 = ((ImmutableCollection)var0).asList();
         return var1.isPartialView() ? asImmutableList(var1.toArray()) : var1;
      } else {
         return construct(var0.toArray());
      }
   }

   public static <E> ImmutableList<E> copyOf(Iterator<? extends E> var0) {
      if (!var0.hasNext()) {
         return of();
      } else {
         Object var1 = var0.next();
         return !var0.hasNext() ? of((E)var1) : new ImmutableList.Builder<E>().add((E)var1).addAll(var0).build();
      }
   }

   public static <E> ImmutableList<E> copyOf(E[] var0) {
      switch (var0.length) {
         case 0:
            return of();
         case 1:
            return of((E)var0[0]);
         default:
            return construct((Object[])var0.clone());
      }
   }

   public static <E extends Comparable<? super E>> ImmutableList<E> sortedCopyOf(Iterable<? extends E> var0) {
      Comparable[] var1 = Iterables.toArray(var0, new Comparable[0]);
      ObjectArrays.checkElementsNotNull(var1);
      Arrays.sort((Object[])var1);
      return asImmutableList(var1);
   }

   public static <E> ImmutableList<E> sortedCopyOf(Comparator<? super E> var0, Iterable<? extends E> var1) {
      Preconditions.checkNotNull(var0);
      Object[] var2 = Iterables.toArray(var1);
      ObjectArrays.checkElementsNotNull(var2);
      Arrays.sort(var2, var0);
      return asImmutableList(var2);
   }

   private static <E> ImmutableList<E> construct(Object... var0) {
      return asImmutableList(ObjectArrays.checkElementsNotNull(var0));
   }

   static <E> ImmutableList<E> asImmutableList(Object[] var0) {
      return asImmutableList(var0, var0.length);
   }

   static <E> ImmutableList<E> asImmutableList(Object[] var0, int var1) {
      switch (var1) {
         case 0:
            return of();
         case 1:
            Object var2 = Objects.requireNonNull(var0[0]);
            return of((E)var2);
         default:
            Object[] var3 = var1 < var0.length ? Arrays.copyOf(var0, var1) : var0;
            return new RegularImmutableList<>(var3);
      }
   }

   ImmutableList() {
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      return this.listIterator();
   }

   public UnmodifiableListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   public UnmodifiableListIterator<E> listIterator(int var1) {
      return new AbstractIndexedListIterator<E>(this.size(), var1) {
         @Override
         protected E get(int var1) {
            return (E)ImmutableList.this.get(var1);
         }
      };
   }

   @Override
   public void forEach(Consumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      int var2 = this.size();

      for (int var3 = 0; var3 < var2; var3++) {
         var1.accept(this.get(var3));
      }
   }

   @Override
   public int indexOf(Object var1) {
      return var1 == null ? -1 : Lists.indexOfImpl(this, var1);
   }

   @Override
   public int lastIndexOf(Object var1) {
      return var1 == null ? -1 : Lists.lastIndexOfImpl(this, var1);
   }

   @Override
   public boolean contains(Object var1) {
      return this.indexOf(var1) >= 0;
   }

   public ImmutableList<E> subList(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, this.size());
      int var3 = var2 - var1;
      if (var3 == this.size()) {
         return this;
      } else if (var3 == 0) {
         return of();
      } else {
         return var3 == 1 ? of(this.get(var1)) : this.subListUnchecked(var1, var2);
      }
   }

   ImmutableList<E> subListUnchecked(int var1, int var2) {
      return new ImmutableList.SubList(var1, var2 - var1);
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean addAll(int var1, Collection<? extends E> var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final E set(int var1, E var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void add(int var1, E var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final E remove(int var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void replaceAll(UnaryOperator<E> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void sort(Comparator<? super E> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @InlineMe(
      replacement = "this"
   )
   @Override
   public final ImmutableList<E> asList() {
      return this;
   }

   @Override
   public Spliterator<E> spliterator() {
      return CollectSpliterators.indexed(this.size(), 1296, this::get);
   }

   @Override
   int copyIntoArray(Object[] var1, int var2) {
      int var3 = this.size();

      for (int var4 = 0; var4 < var3; var4++) {
         var1[var2 + var4] = this.get(var4);
      }

      return var2 + var3;
   }

   public ImmutableList<E> reverse() {
      return (ImmutableList<E>)(this.size() <= 1 ? this : new ImmutableList.ReverseImmutableList<>(this));
   }

   @Override
   public boolean equals(Object var1) {
      return Lists.equalsImpl(this, var1);
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      int var2 = this.size();

      for (int var3 = 0; var3 < var2; var3++) {
         var1 = 31 * var1 + this.get(var3).hashCode();
         var1 = ~(~var1);
      }

      return var1;
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializedForm");
   }

   @Override
   Object writeReplace() {
      return new ImmutableList.SerializedForm(this.toArray());
   }

   public static <E> ImmutableList.Builder<E> builder() {
      return new ImmutableList.Builder<>();
   }

   @Beta
   public static <E> ImmutableList.Builder<E> builderWithExpectedSize(int var0) {
      CollectPreconditions.checkNonnegative(var0, "expectedSize");
      return new ImmutableList.Builder<>(var0);
   }

   public static final class Builder<E> extends ImmutableCollection.Builder<E> {
      @VisibleForTesting
      Object[] contents;
      private int size;
      private boolean forceCopy;

      public Builder() {
         this(4);
      }

      Builder(int var1) {
         this.contents = new Object[var1];
         this.size = 0;
      }

      private void getReadyToExpandTo(int var1) {
         if (this.contents.length < var1) {
            this.contents = Arrays.copyOf(this.contents, expandedCapacity(this.contents.length, var1));
            this.forceCopy = false;
         } else if (this.forceCopy) {
            this.contents = Arrays.copyOf(this.contents, this.contents.length);
            this.forceCopy = false;
         }
      }

      @CanIgnoreReturnValue
      public ImmutableList.Builder<E> add(E var1) {
         Preconditions.checkNotNull(var1);
         this.getReadyToExpandTo(this.size + 1);
         this.contents[this.size++] = var1;
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableList.Builder<E> add(E... var1) {
         ObjectArrays.checkElementsNotNull(var1);
         this.add(var1, var1.length);
         return this;
      }

      private void add(Object[] var1, int var2) {
         this.getReadyToExpandTo(this.size + var2);
         System.arraycopy(var1, 0, this.contents, this.size, var2);
         this.size += var2;
      }

      @CanIgnoreReturnValue
      public ImmutableList.Builder<E> addAll(Iterable<? extends E> var1) {
         Preconditions.checkNotNull(var1);
         if (var1 instanceof Collection) {
            Collection var2 = (Collection)var1;
            this.getReadyToExpandTo(this.size + var2.size());
            if (var2 instanceof ImmutableCollection) {
               ImmutableCollection var3 = (ImmutableCollection)var2;
               this.size = var3.copyIntoArray(this.contents, this.size);
               return this;
            }
         }

         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableList.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableList.Builder<E> combine(ImmutableList.Builder<E> var1) {
         Preconditions.checkNotNull(var1);
         this.add(var1.contents, var1.size);
         return this;
      }

      public ImmutableList<E> build() {
         this.forceCopy = true;
         return ImmutableList.asImmutableList(this.contents, this.size);
      }
   }

   private static class ReverseImmutableList<E> extends ImmutableList<E> {
      private final transient ImmutableList<E> forwardList;

      ReverseImmutableList(ImmutableList<E> var1) {
         this.forwardList = var1;
      }

      private int reverseIndex(int var1) {
         return this.size() - 1 - var1;
      }

      private int reversePosition(int var1) {
         return this.size() - var1;
      }

      @Override
      public ImmutableList<E> reverse() {
         return this.forwardList;
      }

      @Override
      public boolean contains(Object var1) {
         return this.forwardList.contains(var1);
      }

      @Override
      public int indexOf(Object var1) {
         int var2 = this.forwardList.lastIndexOf(var1);
         return var2 >= 0 ? this.reverseIndex(var2) : -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         int var2 = this.forwardList.indexOf(var1);
         return var2 >= 0 ? this.reverseIndex(var2) : -1;
      }

      @Override
      public ImmutableList<E> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.size());
         return this.forwardList.subList(this.reversePosition(var2), this.reversePosition(var1)).reverse();
      }

      @Override
      public E get(int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return this.forwardList.get(this.reverseIndex(var1));
      }

      @Override
      public int size() {
         return this.forwardList.size();
      }

      @Override
      boolean isPartialView() {
         return this.forwardList.isPartialView();
      }
   }

   static class SerializedForm implements Serializable {
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      SerializedForm(Object[] var1) {
         this.elements = var1;
      }

      Object readResolve() {
         return ImmutableList.copyOf(this.elements);
      }
   }

   class SubList extends ImmutableList<E> {
      final transient int offset;
      final transient int length;

      SubList(int var2, int var3) {
         this.offset = var2;
         this.length = var3;
      }

      @Override
      public int size() {
         return this.length;
      }

      @Override
      public E get(int var1) {
         Preconditions.checkElementIndex(var1, this.length);
         return ImmutableList.this.get(var1 + this.offset);
      }

      @Override
      public ImmutableList<E> subList(int var1, int var2) {
         Preconditions.checkPositionIndexes(var1, var2, this.length);
         return ImmutableList.this.subList(var1 + this.offset, var2 + this.offset);
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }
}
