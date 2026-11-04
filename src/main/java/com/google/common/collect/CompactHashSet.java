package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactHashSet<E> extends AbstractSet<E> implements Serializable {
   @VisibleForTesting
   static final double HASH_FLOODING_FPP = 0.001;
   private static final int MAX_HASH_BUCKET_LENGTH = 9;
   private transient Object table;
   private transient int[] entries;
   @VisibleForTesting
   transient Object[] elements;
   private transient int metadata;
   private transient int size;

   public static <E> CompactHashSet<E> create() {
      return new CompactHashSet<>();
   }

   public static <E> CompactHashSet<E> create(Collection<? extends E> var0) {
      CompactHashSet var1 = createWithExpectedSize(var0.size());
      var1.addAll(var0);
      return var1;
   }

   @SafeVarargs
   public static <E> CompactHashSet<E> create(E... var0) {
      CompactHashSet var1 = createWithExpectedSize(var0.length);
      Collections.addAll(var1, (E[])var0);
      return var1;
   }

   public static <E> CompactHashSet<E> createWithExpectedSize(int var0) {
      return new CompactHashSet<>(var0);
   }

   CompactHashSet() {
      this.init(3);
   }

   CompactHashSet(int var1) {
      this.init(var1);
   }

   void init(int var1) {
      Preconditions.checkArgument(var1 >= 0, "Expected size must be >= 0");
      this.metadata = Ints.constrainToRange(var1, 1, 1073741823);
   }

   @VisibleForTesting
   boolean needsAllocArrays() {
      return this.table == null;
   }

   @CanIgnoreReturnValue
   int allocArrays() {
      Preconditions.checkState(this.needsAllocArrays(), "Arrays already allocated");
      int var1 = this.metadata;
      int var2 = CompactHashing.tableSize(var1);
      this.table = CompactHashing.createTable(var2);
      this.setHashTableMask(var2 - 1);
      this.entries = new int[var1];
      this.elements = new Object[var1];
      return var1;
   }

   @VisibleForTesting
   Set<E> delegateOrNull() {
      return this.table instanceof Set ? (Set)this.table : null;
   }

   private Set<E> createHashFloodingResistantDelegate(int var1) {
      return new LinkedHashSet<>(var1, 1.0F);
   }

   @VisibleForTesting
   @CanIgnoreReturnValue
   Set<E> convertToHashFloodingResistantImplementation() {
      Set var1 = this.createHashFloodingResistantDelegate(this.hashTableMask() + 1);

      for (int var2 = this.firstEntryIndex(); var2 >= 0; var2 = this.getSuccessor(var2)) {
         var1.add(this.element(var2));
      }

      this.table = var1;
      this.entries = null;
      this.elements = null;
      this.incrementModCount();
      return var1;
   }

   @VisibleForTesting
   boolean isUsingHashFloodingResistance() {
      return this.delegateOrNull() != null;
   }

   private void setHashTableMask(int var1) {
      int var2 = 32 - Integer.numberOfLeadingZeros(var1);
      this.metadata = CompactHashing.maskCombine(this.metadata, var2, 31);
   }

   private int hashTableMask() {
      return (1 << (this.metadata & 31)) - 1;
   }

   void incrementModCount() {
      this.metadata += 32;
   }

   @CanIgnoreReturnValue
   @Override
   public boolean add(@ParametricNullness E var1) {
      if (this.needsAllocArrays()) {
         this.allocArrays();
      }

      Set var2 = this.delegateOrNull();
      if (var2 != null) {
         return var2.add(var1);
      } else {
         int[] var3 = this.requireEntries();
         Object[] var4 = this.requireElements();
         int var5 = this.size;
         int var6 = var5 + 1;
         int var7 = Hashing.smearedHash(var1);
         int var8 = this.hashTableMask();
         int var9 = var7 & var8;
         int var10 = CompactHashing.tableGet(this.requireTable(), var9);
         if (var10 == 0) {
            if (var6 > var8) {
               var8 = this.resizeTable(var8, CompactHashing.newCapacity(var8), var7, var5);
            } else {
               CompactHashing.tableSet(this.requireTable(), var9, var5 + 1);
            }
         } else {
            int var13 = CompactHashing.getHashPrefix(var7, var8);
            int var14 = 0;

            int var11;
            int var12;
            do {
               var11 = var10 - 1;
               var12 = var3[var11];
               if (CompactHashing.getHashPrefix(var12, var8) == var13 && Objects.equal(var1, var4[var11])) {
                  return false;
               }

               var10 = CompactHashing.getNext(var12, var8);
               var14++;
            } while (var10 != 0);

            if (var14 >= 9) {
               return this.convertToHashFloodingResistantImplementation().add((E)var1);
            }

            if (var6 > var8) {
               var8 = this.resizeTable(var8, CompactHashing.newCapacity(var8), var7, var5);
            } else {
               var3[var11] = CompactHashing.maskCombine(var12, var5 + 1, var8);
            }
         }

         this.resizeMeMaybe(var6);
         this.insertEntry(var5, (E)var1, var7, var8);
         this.size = var6;
         this.incrementModCount();
         return true;
      }
   }

   void insertEntry(int var1, @ParametricNullness E var2, int var3, int var4) {
      this.setEntry(var1, CompactHashing.maskCombine(var3, 0, var4));
      this.setElement(var1, (E)var2);
   }

   private void resizeMeMaybe(int var1) {
      int var2 = this.requireEntries().length;
      if (var1 > var2) {
         int var3 = Math.min(1073741823, var2 + Math.max(1, var2 >>> 1) | 1);
         if (var3 != var2) {
            this.resizeEntries(var3);
         }
      }
   }

   void resizeEntries(int var1) {
      this.entries = Arrays.copyOf(this.requireEntries(), var1);
      this.elements = Arrays.copyOf(this.requireElements(), var1);
   }

   @CanIgnoreReturnValue
   private int resizeTable(int var1, int var2, int var3, int var4) {
      Object var5 = CompactHashing.createTable(var2);
      int var6 = var2 - 1;
      if (var4 != 0) {
         CompactHashing.tableSet(var5, var3 & var6, var4 + 1);
      }

      Object var7 = this.requireTable();
      int[] var8 = this.requireEntries();

      for (int var9 = 0; var9 <= var1; var9++) {
         int var10 = CompactHashing.tableGet(var7, var9);

         while (var10 != 0) {
            int var11 = var10 - 1;
            int var12 = var8[var11];
            int var13 = CompactHashing.getHashPrefix(var12, var1) | var9;
            int var14 = var13 & var6;
            int var15 = CompactHashing.tableGet(var5, var14);
            CompactHashing.tableSet(var5, var14, var10);
            var8[var11] = CompactHashing.maskCombine(var13, var15, var6);
            var10 = CompactHashing.getNext(var12, var1);
         }
      }

      this.table = var5;
      this.setHashTableMask(var6);
      return var6;
   }

   @Override
   public boolean contains(Object var1) {
      if (this.needsAllocArrays()) {
         return false;
      } else {
         Set var2 = this.delegateOrNull();
         if (var2 != null) {
            return var2.contains(var1);
         } else {
            int var3 = Hashing.smearedHash(var1);
            int var4 = this.hashTableMask();
            int var5 = CompactHashing.tableGet(this.requireTable(), var3 & var4);
            if (var5 == 0) {
               return false;
            } else {
               int var6 = CompactHashing.getHashPrefix(var3, var4);

               do {
                  int var7 = var5 - 1;
                  int var8 = this.entry(var7);
                  if (CompactHashing.getHashPrefix(var8, var4) == var6 && Objects.equal(var1, this.element(var7))) {
                     return true;
                  }

                  var5 = CompactHashing.getNext(var8, var4);
               } while (var5 != 0);

               return false;
            }
         }
      }
   }

   @CanIgnoreReturnValue
   @Override
   public boolean remove(Object var1) {
      if (this.needsAllocArrays()) {
         return false;
      } else {
         Set var2 = this.delegateOrNull();
         if (var2 != null) {
            return var2.remove(var1);
         } else {
            int var3 = this.hashTableMask();
            int var4 = CompactHashing.remove(var1, null, var3, this.requireTable(), this.requireEntries(), this.requireElements(), null);
            if (var4 == -1) {
               return false;
            } else {
               this.moveLastEntry(var4, var3);
               this.size--;
               this.incrementModCount();
               return true;
            }
         }
      }
   }

   void moveLastEntry(int var1, int var2) {
      Object var3 = this.requireTable();
      int[] var4 = this.requireEntries();
      Object[] var5 = this.requireElements();
      int var6 = this.size() - 1;
      if (var1 < var6) {
         Object var7 = var5[var6];
         var5[var1] = var7;
         var5[var6] = null;
         var4[var1] = var4[var6];
         var4[var6] = 0;
         int var8 = Hashing.smearedHash(var7) & var2;
         int var9 = CompactHashing.tableGet(var3, var8);
         int var10 = var6 + 1;
         if (var9 == var10) {
            CompactHashing.tableSet(var3, var8, var1 + 1);
         } else {
            int var11;
            int var12;
            do {
               var11 = var9 - 1;
               var12 = var4[var11];
               var9 = CompactHashing.getNext(var12, var2);
            } while (var9 != var10);

            var4[var11] = CompactHashing.maskCombine(var12, var1 + 1, var2);
         }
      } else {
         var5[var1] = null;
         var4[var1] = 0;
      }
   }

   int firstEntryIndex() {
      return this.isEmpty() ? -1 : 0;
   }

   int getSuccessor(int var1) {
      return var1 + 1 < this.size ? var1 + 1 : -1;
   }

   int adjustAfterRemove(int var1, int var2) {
      return var1 - 1;
   }

   @Override
   public Iterator<E> iterator() {
      Set var1 = this.delegateOrNull();
      return var1 != null ? var1.iterator() : new Iterator<E>() {
         int expectedMetadata = CompactHashSet.this.metadata;
         int currentIndex = CompactHashSet.this.firstEntryIndex();
         int indexToRemove = -1;

         @Override
         public boolean hasNext() {
            return this.currentIndex >= 0;
         }

         @ParametricNullness
         @Override
         public E next() {
            this.checkForConcurrentModification();
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.indexToRemove = this.currentIndex;
               Object var1 = CompactHashSet.this.element(this.currentIndex);
               this.currentIndex = CompactHashSet.this.getSuccessor(this.currentIndex);
               return (E)var1;
            }
         }

         @Override
         public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.indexToRemove >= 0);
            this.incrementExpectedModCount();
            CompactHashSet.this.remove(CompactHashSet.this.element(this.indexToRemove));
            this.currentIndex = CompactHashSet.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
            this.indexToRemove = -1;
         }

         void incrementExpectedModCount() {
            this.expectedMetadata += 32;
         }

         private void checkForConcurrentModification() {
            if (CompactHashSet.this.metadata != this.expectedMetadata) {
               throw new ConcurrentModificationException();
            }
         }
      };
   }

   @Override
   public Spliterator<E> spliterator() {
      if (this.needsAllocArrays()) {
         return Spliterators.spliterator(new Object[0], 17);
      } else {
         Set var1 = this.delegateOrNull();
         return var1 != null ? var1.spliterator() : Spliterators.spliterator(this.requireElements(), 0, this.size, 17);
      }
   }

   @Override
   public void forEach(Consumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      Set var2 = this.delegateOrNull();
      if (var2 != null) {
         var2.forEach(var1);
      } else {
         for (int var3 = this.firstEntryIndex(); var3 >= 0; var3 = this.getSuccessor(var3)) {
            var1.accept(this.element(var3));
         }
      }
   }

   @Override
   public int size() {
      Set var1 = this.delegateOrNull();
      return var1 != null ? var1.size() : this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public Object[] toArray() {
      if (this.needsAllocArrays()) {
         return new Object[0];
      } else {
         Set var1 = this.delegateOrNull();
         return var1 != null ? var1.toArray() : Arrays.copyOf(this.requireElements(), this.size);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public <T> T[] toArray(T[] var1) {
      if (this.needsAllocArrays()) {
         if (var1.length > 0) {
            var1[0] = null;
         }

         return (T[])var1;
      } else {
         Set var2 = this.delegateOrNull();
         return (T[])(var2 != null ? var2.toArray(var1) : ObjectArrays.toArrayImpl(this.requireElements(), 0, this.size, var1));
      }
   }

   public void trimToSize() {
      if (!this.needsAllocArrays()) {
         Set var1 = this.delegateOrNull();
         if (var1 != null) {
            Set var5 = this.createHashFloodingResistantDelegate(this.size());
            var5.addAll(var1);
            this.table = var5;
         } else {
            int var2 = this.size;
            if (var2 < this.requireEntries().length) {
               this.resizeEntries(var2);
            }

            int var3 = CompactHashing.tableSize(var2);
            int var4 = this.hashTableMask();
            if (var3 < var4) {
               this.resizeTable(var4, var3, 0, 0);
            }
         }
      }
   }

   @Override
   public void clear() {
      if (!this.needsAllocArrays()) {
         this.incrementModCount();
         Set var1 = this.delegateOrNull();
         if (var1 != null) {
            this.metadata = Ints.constrainToRange(this.size(), 3, 1073741823);
            var1.clear();
            this.table = null;
            this.size = 0;
         } else {
            Arrays.fill(this.requireElements(), 0, this.size, null);
            CompactHashing.tableClear(this.requireTable());
            Arrays.fill(this.requireEntries(), 0, this.size, 0);
            this.size = 0;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size());

      for (Object var3 : this) {
         var1.writeObject(var3);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new InvalidObjectException(new StringBuilder(25).append("Invalid size: ").append(var2).toString());
      } else {
         this.init(var2);

         for (int var3 = 0; var3 < var2; var3++) {
            Object var4 = var1.readObject();
            this.add((E)var4);
         }
      }
   }

   private Object requireTable() {
      return java.util.Objects.requireNonNull(this.table);
   }

   private int[] requireEntries() {
      return java.util.Objects.requireNonNull(this.entries);
   }

   private Object[] requireElements() {
      return java.util.Objects.requireNonNull(this.elements);
   }

   private E element(int var1) {
      return (E)this.requireElements()[var1];
   }

   private int entry(int var1) {
      return this.requireEntries()[var1];
   }

   private void setElement(int var1, E var2) {
      this.requireElements()[var1] = var2;
   }

   private void setEntry(int var1, int var2) {
      this.requireEntries()[var1] = var2;
   }
}
