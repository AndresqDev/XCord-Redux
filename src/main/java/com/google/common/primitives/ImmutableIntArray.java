package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterator.OfInt;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class ImmutableIntArray implements Serializable {
   private static final ImmutableIntArray EMPTY = new ImmutableIntArray(new int[0]);
   private final int[] array;
   private final transient int start;
   private final int end;

   public static ImmutableIntArray of() {
      return EMPTY;
   }

   public static ImmutableIntArray of(int var0) {
      return new ImmutableIntArray(new int[]{var0});
   }

   public static ImmutableIntArray of(int var0, int var1) {
      return new ImmutableIntArray(new int[]{var0, var1});
   }

   public static ImmutableIntArray of(int var0, int var1, int var2) {
      return new ImmutableIntArray(new int[]{var0, var1, var2});
   }

   public static ImmutableIntArray of(int var0, int var1, int var2, int var3) {
      return new ImmutableIntArray(new int[]{var0, var1, var2, var3});
   }

   public static ImmutableIntArray of(int var0, int var1, int var2, int var3, int var4) {
      return new ImmutableIntArray(new int[]{var0, var1, var2, var3, var4});
   }

   public static ImmutableIntArray of(int var0, int var1, int var2, int var3, int var4, int var5) {
      return new ImmutableIntArray(new int[]{var0, var1, var2, var3, var4, var5});
   }

   public static ImmutableIntArray of(int var0, int... var1) {
      Preconditions.checkArgument(var1.length <= 2147483646, "the total number of elements must fit in an int");
      int[] var2 = new int[var1.length + 1];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return new ImmutableIntArray(var2);
   }

   public static ImmutableIntArray copyOf(int[] var0) {
      return var0.length == 0 ? EMPTY : new ImmutableIntArray(Arrays.copyOf(var0, var0.length));
   }

   public static ImmutableIntArray copyOf(Collection<Integer> var0) {
      return var0.isEmpty() ? EMPTY : new ImmutableIntArray(Ints.toArray(var0));
   }

   public static ImmutableIntArray copyOf(Iterable<Integer> var0) {
      return var0 instanceof Collection ? copyOf((Collection<Integer>)var0) : builder().addAll(var0).build();
   }

   public static ImmutableIntArray copyOf(IntStream var0) {
      int[] var1 = var0.toArray();
      return var1.length == 0 ? EMPTY : new ImmutableIntArray(var1);
   }

   public static ImmutableIntArray.Builder builder(int var0) {
      Preconditions.checkArgument(var0 >= 0, "Invalid initialCapacity: %s", var0);
      return new ImmutableIntArray.Builder(var0);
   }

   public static ImmutableIntArray.Builder builder() {
      return new ImmutableIntArray.Builder(10);
   }

   private ImmutableIntArray(int[] var1) {
      this(var1, 0, var1.length);
   }

   private ImmutableIntArray(int[] var1, int var2, int var3) {
      this.array = var1;
      this.start = var2;
      this.end = var3;
   }

   public int length() {
      return this.end - this.start;
   }

   public boolean isEmpty() {
      return this.end == this.start;
   }

   public int get(int var1) {
      Preconditions.checkElementIndex(var1, this.length());
      return this.array[this.start + var1];
   }

   public int indexOf(int var1) {
      for (int var2 = this.start; var2 < this.end; var2++) {
         if (this.array[var2] == var1) {
            return var2 - this.start;
         }
      }

      return -1;
   }

   public int lastIndexOf(int var1) {
      for (int var2 = this.end - 1; var2 >= this.start; var2--) {
         if (this.array[var2] == var1) {
            return var2 - this.start;
         }
      }

      return -1;
   }

   public boolean contains(int var1) {
      return this.indexOf(var1) >= 0;
   }

   public void forEach(IntConsumer var1) {
      Preconditions.checkNotNull(var1);

      for (int var2 = this.start; var2 < this.end; var2++) {
         var1.accept(this.array[var2]);
      }
   }

   public IntStream stream() {
      return Arrays.stream(this.array, this.start, this.end);
   }

   public int[] toArray() {
      return Arrays.copyOfRange(this.array, this.start, this.end);
   }

   public ImmutableIntArray subArray(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, this.length());
      return var1 == var2 ? EMPTY : new ImmutableIntArray(this.array, this.start + var1, this.start + var2);
   }

   private OfInt spliterator() {
      return Spliterators.spliterator(this.array, this.start, this.end, 1040);
   }

   public List<Integer> asList() {
      return new ImmutableIntArray.AsList(this);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ImmutableIntArray)) {
         return false;
      } else {
         ImmutableIntArray var2 = (ImmutableIntArray)var1;
         if (this.length() != var2.length()) {
            return false;
         } else {
            for (int var3 = 0; var3 < this.length(); var3++) {
               if (this.get(var3) != var2.get(var3)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 1;

      for (int var2 = this.start; var2 < this.end; var2++) {
         var1 *= 31;
         var1 += Ints.hashCode(this.array[var2]);
      }

      return var1;
   }

   @Override
   public String toString() {
      if (this.isEmpty()) {
         return "[]";
      } else {
         StringBuilder var1 = new StringBuilder(this.length() * 5);
         var1.append('[').append(this.array[this.start]);

         for (int var2 = this.start + 1; var2 < this.end; var2++) {
            var1.append(", ").append(this.array[var2]);
         }

         var1.append(']');
         return var1.toString();
      }
   }

   public ImmutableIntArray trimmed() {
      return this.isPartialView() ? new ImmutableIntArray(this.toArray()) : this;
   }

   private boolean isPartialView() {
      return this.start > 0 || this.end < this.array.length;
   }

   Object writeReplace() {
      return this.trimmed();
   }

   Object readResolve() {
      return this.isEmpty() ? EMPTY : this;
   }

   static class AsList extends AbstractList<Integer> implements Serializable, RandomAccess {
      private final ImmutableIntArray parent;

      private AsList(ImmutableIntArray var1) {
         this.parent = var1;
      }

      @Override
      public int size() {
         return this.parent.length();
      }

      public Integer get(int var1) {
         return this.parent.get(var1);
      }

      @Override
      public boolean contains(Object var1) {
         return this.indexOf(var1) >= 0;
      }

      @Override
      public int indexOf(Object var1) {
         return var1 instanceof Integer ? this.parent.indexOf((Integer)var1) : -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         return var1 instanceof Integer ? this.parent.lastIndexOf((Integer)var1) : -1;
      }

      @Override
      public List<Integer> subList(int var1, int var2) {
         return this.parent.subArray(var1, var2).asList();
      }

      @Override
      public Spliterator<Integer> spliterator() {
         return this.parent.spliterator();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof ImmutableIntArray.AsList) {
            ImmutableIntArray.AsList var6 = (ImmutableIntArray.AsList)var1;
            return this.parent.equals(var6.parent);
         } else if (!(var1 instanceof List)) {
            return false;
         } else {
            List var2 = (List)var1;
            if (this.size() != var2.size()) {
               return false;
            } else {
               int var3 = this.parent.start;

               for (Object var5 : var2) {
                  if (!(var5 instanceof Integer) || this.parent.array[var3++] != (Integer)var5) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      @Override
      public int hashCode() {
         return this.parent.hashCode();
      }

      @Override
      public String toString() {
         return this.parent.toString();
      }
   }

   @CanIgnoreReturnValue
   public static final class Builder {
      private int[] array;
      private int count = 0;

      Builder(int var1) {
         this.array = new int[var1];
      }

      public ImmutableIntArray.Builder add(int var1) {
         this.ensureRoomFor(1);
         this.array[this.count] = var1;
         this.count++;
         return this;
      }

      public ImmutableIntArray.Builder addAll(int[] var1) {
         this.ensureRoomFor(var1.length);
         System.arraycopy(var1, 0, this.array, this.count, var1.length);
         this.count += var1.length;
         return this;
      }

      public ImmutableIntArray.Builder addAll(Iterable<Integer> var1) {
         if (var1 instanceof Collection) {
            return this.addAll((Collection<Integer>)var1);
         } else {
            for (Integer var3 : var1) {
               this.add(var3);
            }

            return this;
         }
      }

      public ImmutableIntArray.Builder addAll(Collection<Integer> var1) {
         this.ensureRoomFor(var1.size());

         for (Integer var3 : var1) {
            this.array[this.count++] = var3;
         }

         return this;
      }

      public ImmutableIntArray.Builder addAll(IntStream var1) {
         OfInt var2 = var1.spliterator();
         long var3 = var2.getExactSizeIfKnown();
         if (var3 > 0L) {
            this.ensureRoomFor(Ints.saturatedCast(var3));
         }

         var2.forEachRemaining(this::add);
         return this;
      }

      public ImmutableIntArray.Builder addAll(ImmutableIntArray var1) {
         this.ensureRoomFor(var1.length());
         System.arraycopy(var1.array, var1.start, this.array, this.count, var1.length());
         this.count = this.count + var1.length();
         return this;
      }

      private void ensureRoomFor(int var1) {
         int var2 = this.count + var1;
         if (var2 > this.array.length) {
            this.array = Arrays.copyOf(this.array, expandedCapacity(this.array.length, var2));
         }
      }

      private static int expandedCapacity(int var0, int var1) {
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

      @CheckReturnValue
      public ImmutableIntArray build() {
         return this.count == 0 ? ImmutableIntArray.EMPTY : new ImmutableIntArray(this.array, 0, this.count);
      }
   }
}
