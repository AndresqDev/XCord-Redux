package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.ObjIntConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class EnumMultiset<E extends Enum<E>> extends AbstractMultiset<E> implements Serializable {
   private transient Class<E> type;
   private transient E[] enumConstants;
   private transient int[] counts;
   private transient int distinctElements;
   private transient long size;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> var0) {
      return new EnumMultiset<>(var0);
   }

   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "EnumMultiset constructor passed empty Iterable");
      EnumMultiset var2 = new EnumMultiset(((Enum)var1.next()).getDeclaringClass());
      Iterables.addAll(var2, var0);
      return var2;
   }

   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> var0, Class<E> var1) {
      EnumMultiset var2 = create(var1);
      Iterables.addAll(var2, var0);
      return var2;
   }

   private EnumMultiset(Class<E> var1) {
      this.type = var1;
      Preconditions.checkArgument(var1.isEnum());
      this.enumConstants = (E[])var1.getEnumConstants();
      this.counts = new int[this.enumConstants.length];
   }

   private boolean isActuallyE(Object var1) {
      if (!(var1 instanceof Enum)) {
         return false;
      } else {
         Enum var2 = (Enum)var1;
         int var3 = var2.ordinal();
         return var3 < this.enumConstants.length && this.enumConstants[var3] == var2;
      }
   }

   private void checkIsE(Object var1) {
      Preconditions.checkNotNull(var1);
      if (!this.isActuallyE(var1)) {
         String var2 = String.valueOf(this.type);
         String var3 = String.valueOf(var1);
         throw new ClassCastException(
            new StringBuilder(21 + String.valueOf(var2).length() + String.valueOf(var3).length())
               .append("Expected an ")
               .append(var2)
               .append(" but got ")
               .append(var3)
               .toString()
         );
      }
   }

   @Override
   int distinctElements() {
      return this.distinctElements;
   }

   @Override
   public int size() {
      return Ints.saturatedCast(this.size);
   }

   @Override
   public int count(Object var1) {
      if (var1 != null && this.isActuallyE(var1)) {
         Enum var2 = (Enum)var1;
         return this.counts[var2.ordinal()];
      } else {
         return 0;
      }
   }

   @CanIgnoreReturnValue
   public int add(E var1, int var2) {
      this.checkIsE(var1);
      CollectPreconditions.checkNonnegative(var2, "occurrences");
      if (var2 == 0) {
         return this.count(var1);
      } else {
         int var3 = var1.ordinal();
         int var4 = this.counts[var3];
         long var5 = (long)var4 + (long)var2;
         Preconditions.checkArgument(var5 <= 2147483647L, "too many occurrences: %s", var5);
         this.counts[var3] = (int)var5;
         if (var4 == 0) {
            this.distinctElements++;
         }

         this.size += (long)var2;
         return var4;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int remove(Object var1, int var2) {
      if (var1 != null && this.isActuallyE(var1)) {
         Enum var3 = (Enum)var1;
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            int var4 = var3.ordinal();
            int var5 = this.counts[var4];
            if (var5 == 0) {
               return 0;
            } else {
               if (var5 <= var2) {
                  this.counts[var4] = 0;
                  this.distinctElements--;
                  this.size -= (long)var5;
               } else {
                  this.counts[var4] = var5 - var2;
                  this.size -= (long)var2;
               }

               return var5;
            }
         }
      } else {
         return 0;
      }
   }

   @CanIgnoreReturnValue
   public int setCount(E var1, int var2) {
      this.checkIsE(var1);
      CollectPreconditions.checkNonnegative(var2, "count");
      int var3 = var1.ordinal();
      int var4 = this.counts[var3];
      this.counts[var3] = var2;
      this.size += (long)(var2 - var4);
      if (var4 == 0 && var2 > 0) {
         this.distinctElements++;
      } else if (var4 > 0 && var2 == 0) {
         this.distinctElements--;
      }

      return var4;
   }

   @Override
   public void clear() {
      Arrays.fill(this.counts, 0);
      this.size = 0L;
      this.distinctElements = 0;
   }

   @Override
   Iterator<E> elementIterator() {
      return new EnumMultiset<E>.Itr<E>() {
         E output(int var1) {
            return EnumMultiset.this.enumConstants[var1];
         }
      };
   }

   @Override
   Iterator<Multiset.Entry<E>> entryIterator() {
      return new EnumMultiset<E>.Itr<Multiset.Entry<E>>() {
         Multiset.Entry<E> output(final int var1) {
            return new Multisets.AbstractEntry<E>() {
               public E getElement() {
                  return EnumMultiset.this.enumConstants[var1];
               }

               @Override
               public int getCount() {
                  return EnumMultiset.this.counts[var1];
               }
            };
         }
      };
   }

   @Override
   public void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);

      for (int var2 = 0; var2 < this.enumConstants.length; var2++) {
         if (this.counts[var2] > 0) {
            var1.accept(this.enumConstants[var2], this.counts[var2]);
         }
      }
   }

   @Override
   public Iterator<E> iterator() {
      return Multisets.iteratorImpl(this);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.type);
      Serialization.writeMultiset(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Class var2 = (Class)var1.readObject();
      this.type = var2;
      this.enumConstants = this.type.getEnumConstants();
      this.counts = new int[this.enumConstants.length];
      Serialization.populateMultiset(this, var1);
   }

   abstract class Itr<T> implements Iterator<T> {
      int index = 0;
      int toRemove = -1;

      abstract T output(int var1);

      @Override
      public boolean hasNext() {
         while (this.index < EnumMultiset.this.enumConstants.length) {
            if (EnumMultiset.this.counts[this.index] > 0) {
               return true;
            }

            this.index++;
         }

         return false;
      }

      @Override
      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            Object var1 = this.output(this.index);
            this.toRemove = this.index++;
            return (T)var1;
         }
      }

      @Override
      public void remove() {
         CollectPreconditions.checkRemove(this.toRemove >= 0);
         if (EnumMultiset.this.counts[this.toRemove] > 0) {
            EnumMultiset.this.distinctElements--;
            EnumMultiset.this.size -= (long)EnumMultiset.this.counts[this.toRemove];
            EnumMultiset.this.counts[this.toRemove] = 0;
         }

         this.toRemove = -1;
      }
   }
}
