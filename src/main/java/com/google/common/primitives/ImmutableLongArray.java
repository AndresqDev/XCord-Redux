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
import java.util.Spliterator.OfLong;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public final class ImmutableLongArray implements Serializable {
   private static final ImmutableLongArray EMPTY = new ImmutableLongArray(new long[0]);
   private final long[] array;
   private final transient int start;
   private final int end;

   public static ImmutableLongArray of() {
      return EMPTY;
   }

   public static ImmutableLongArray of(long var0) {
      return new ImmutableLongArray(new long[]{var0});
   }

   public static ImmutableLongArray of(long var0, long var2) {
      return new ImmutableLongArray(new long[]{var0, var2});
   }

   public static ImmutableLongArray of(long var0, long var2, long var4) {
      return new ImmutableLongArray(new long[]{var0, var2, var4});
   }

   public static ImmutableLongArray of(long var0, long var2, long var4, long var6) {
      return new ImmutableLongArray(new long[]{var0, var2, var4, var6});
   }

   public static ImmutableLongArray of(long var0, long var2, long var4, long var6, long var8) {
      return new ImmutableLongArray(new long[]{var0, var2, var4, var6, var8});
   }

   public static ImmutableLongArray of(long var0, long var2, long var4, long var6, long var8, long var10) {
      return new ImmutableLongArray(new long[]{var0, var2, var4, var6, var8, var10});
   }

   public static ImmutableLongArray of(long var0, long... var2) {
      Preconditions.checkArgument(var2.length <= 2147483646, "the total number of elements must fit in an int");
      long[] var3 = new long[var2.length + 1];
      var3[0] = var0;
      System.arraycopy(var2, 0, var3, 1, var2.length);
      return new ImmutableLongArray(var3);
   }

   public static ImmutableLongArray copyOf(long[] var0) {
      return var0.length == 0 ? EMPTY : new ImmutableLongArray(Arrays.copyOf(var0, var0.length));
   }

   public static ImmutableLongArray copyOf(Collection<Long> var0) {
      return var0.isEmpty() ? EMPTY : new ImmutableLongArray(Longs.toArray(var0));
   }

   public static ImmutableLongArray copyOf(Iterable<Long> var0) {
      return var0 instanceof Collection ? copyOf((Collection<Long>)var0) : builder().addAll(var0).build();
   }

   public static ImmutableLongArray copyOf(LongStream var0) {
      long[] var1 = var0.toArray();
      return var1.length == 0 ? EMPTY : new ImmutableLongArray(var1);
   }

   public static ImmutableLongArray.Builder builder(int var0) {
      Preconditions.checkArgument(var0 >= 0, "Invalid initialCapacity: %s", var0);
      return new ImmutableLongArray.Builder(var0);
   }

   public static ImmutableLongArray.Builder builder() {
      return new ImmutableLongArray.Builder(10);
   }

   private ImmutableLongArray(long[] var1) {
      this(var1, 0, var1.length);
   }

   private ImmutableLongArray(long[] var1, int var2, int var3) {
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

   public long get(int var1) {
      Preconditions.checkElementIndex(var1, this.length());
      return this.array[this.start + var1];
   }

   public int indexOf(long var1) {
      for (int var3 = this.start; var3 < this.end; var3++) {
         if (this.array[var3] == var1) {
            return var3 - this.start;
         }
      }

      return -1;
   }

   public int lastIndexOf(long var1) {
      for (int var3 = this.end - 1; var3 >= this.start; var3--) {
         if (this.array[var3] == var1) {
            return var3 - this.start;
         }
      }

      return -1;
   }

   public boolean contains(long var1) {
      return this.indexOf(var1) >= 0;
   }

   public void forEach(LongConsumer var1) {
      Preconditions.checkNotNull(var1);

      for (int var2 = this.start; var2 < this.end; var2++) {
         var1.accept(this.array[var2]);
      }
   }

   public LongStream stream() {
      return Arrays.stream(this.array, this.start, this.end);
   }

   public long[] toArray() {
      return Arrays.copyOfRange(this.array, this.start, this.end);
   }

   public ImmutableLongArray subArray(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, this.length());
      return var1 == var2 ? EMPTY : new ImmutableLongArray(this.array, this.start + var1, this.start + var2);
   }

   private OfLong spliterator() {
      return Spliterators.spliterator(this.array, this.start, this.end, 1040);
   }

   public List<Long> asList() {
      return new ImmutableLongArray.AsList(this);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ImmutableLongArray)) {
         return false;
      } else {
         ImmutableLongArray var2 = (ImmutableLongArray)var1;
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
         var1 += Longs.hashCode(this.array[var2]);
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

   public ImmutableLongArray trimmed() {
      return this.isPartialView() ? new ImmutableLongArray(this.toArray()) : this;
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

   static class AsList extends AbstractList<Long> implements Serializable, RandomAccess {
      private final ImmutableLongArray parent;

      private AsList(ImmutableLongArray var1) {
         this.parent = var1;
      }

      @Override
      public int size() {
         return this.parent.length();
      }

      public Long get(int var1) {
         return this.parent.get(var1);
      }

      @Override
      public boolean contains(Object var1) {
         return this.indexOf(var1) >= 0;
      }

      @Override
      public int indexOf(Object var1) {
         return var1 instanceof Long ? this.parent.indexOf((Long)var1) : -1;
      }

      @Override
      public int lastIndexOf(Object var1) {
         return var1 instanceof Long ? this.parent.lastIndexOf((Long)var1) : -1;
      }

      @Override
      public List<Long> subList(int var1, int var2) {
         return this.parent.subArray(var1, var2).asList();
      }

      @Override
      public Spliterator<Long> spliterator() {
         return this.parent.spliterator();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof ImmutableLongArray.AsList) {
            ImmutableLongArray.AsList var6 = (ImmutableLongArray.AsList)var1;
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
                  if (!(var5 instanceof Long) || this.parent.array[var3++] != (Long)var5) {
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
      private long[] array;
      private int count = 0;

      Builder(int var1) {
         this.array = new long[var1];
      }

      public ImmutableLongArray.Builder add(long var1) {
         this.ensureRoomFor(1);
         this.array[this.count] = var1;
         this.count++;
         return this;
      }

      public ImmutableLongArray.Builder addAll(long[] var1) {
         this.ensureRoomFor(var1.length);
         System.arraycopy(var1, 0, this.array, this.count, var1.length);
         this.count += var1.length;
         return this;
      }

      public ImmutableLongArray.Builder addAll(Iterable<Long> var1) {
         if (var1 instanceof Collection) {
            return this.addAll((Collection<Long>)var1);
         } else {
            for (Long var3 : var1) {
               this.add(var3);
            }

            return this;
         }
      }

      public ImmutableLongArray.Builder addAll(Collection<Long> var1) {
         this.ensureRoomFor(var1.size());

         for (Long var3 : var1) {
            this.array[this.count++] = var3;
         }

         return this;
      }

      public ImmutableLongArray.Builder addAll(LongStream var1) {
         OfLong var2 = var1.spliterator();
         long var3 = var2.getExactSizeIfKnown();
         if (var3 > 0L) {
            this.ensureRoomFor(Ints.saturatedCast(var3));
         }

         var2.forEachRemaining(this::add);
         return this;
      }

      public ImmutableLongArray.Builder addAll(ImmutableLongArray var1) {
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
      public ImmutableLongArray build() {
         return this.count == 0 ? ImmutableLongArray.EMPTY : new ImmutableLongArray(this.array, 0, this.count);
      }
   }
}
