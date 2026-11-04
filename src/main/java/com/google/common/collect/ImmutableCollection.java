package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.DoNotMock;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

@DoNotMock("Use ImmutableList.of or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public abstract class ImmutableCollection<E> extends AbstractCollection<E> implements Serializable {
   static final int SPLITERATOR_CHARACTERISTICS = 1296;
   private static final Object[] EMPTY_ARRAY = new Object[0];

   ImmutableCollection() {
   }

   public abstract UnmodifiableIterator<E> iterator();

   @Override
   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this, 1296);
   }

   @Override
   public final Object[] toArray() {
      return this.toArray(EMPTY_ARRAY);
   }

   @CanIgnoreReturnValue
   @Override
   public final <T> T[] toArray(T[] var1) {
      Preconditions.checkNotNull(var1);
      int var2 = this.size();
      if (var1.length < var2) {
         Object[] var3 = this.internalArray();
         if (var3 != null) {
            return (T[])Platform.copy(var3, this.internalArrayStart(), this.internalArrayEnd(), var1);
         }

         var1 = ObjectArrays.newArray(var1, var2);
      } else if (var1.length > var2) {
         var1[var2] = null;
      }

      this.copyIntoArray(var1, 0);
      return (T[])var1;
   }

   Object[] internalArray() {
      return null;
   }

   int internalArrayStart() {
      throw new UnsupportedOperationException();
   }

   int internalArrayEnd() {
      throw new UnsupportedOperationException();
   }

   @Override
   public abstract boolean contains(Object var1);

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean add(E var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean addAll(Collection<? extends E> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean removeIf(Predicate<? super E> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public ImmutableList<E> asList() {
      switch (this.size()) {
         case 0:
            return ImmutableList.of();
         case 1:
            return ImmutableList.of(this.iterator().next());
         default:
            return new RegularImmutableAsList<>(this, this.toArray());
      }
   }

   abstract boolean isPartialView();

   @CanIgnoreReturnValue
   int copyIntoArray(Object[] var1, int var2) {
      for (Object var4 : this) {
         var1[var2++] = var4;
      }

      return var2;
   }

   Object writeReplace() {
      return new ImmutableList.SerializedForm(this.toArray());
   }

   @DoNotMock
   public abstract static class Builder<E> {
      static final int DEFAULT_INITIAL_CAPACITY = 4;

      static int expandedCapacity(int var0, int var1) {
         if (var1 < 0) {
            throw new AssertionError("cannot store more than MAX_VALUE elements");
         } else {
            int var2 = var0 + (var0 >> 1) + 1;
            if (var2 < var1) {
               var2 = Integer.highestOneBit(var1 - 1) << 1;
            }

            if (var2 < 0) {
               var2 = Integer.MAX_VALUE;
            }

            return var2;
         }
      }

      Builder() {
      }

      @CanIgnoreReturnValue
      public abstract ImmutableCollection.Builder<E> add(E var1);

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> add(E... var1) {
         for (Object var5 : var1) {
            this.add((E)var5);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> addAll(Iterable<? extends E> var1) {
         for (Object var3 : var1) {
            this.add((E)var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableCollection.Builder<E> addAll(Iterator<? extends E> var1) {
         while (var1.hasNext()) {
            this.add((E)var1.next());
         }

         return this;
      }

      public abstract ImmutableCollection<E> build();
   }
}
