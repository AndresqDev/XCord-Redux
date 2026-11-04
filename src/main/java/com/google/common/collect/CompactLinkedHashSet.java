package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class CompactLinkedHashSet<E> extends CompactHashSet<E> {
   private static final int ENDPOINT = -2;
   private transient int[] predecessor;
   private transient int[] successor;
   private transient int firstEntry;
   private transient int lastEntry;

   public static <E> CompactLinkedHashSet<E> create() {
      return new CompactLinkedHashSet<>();
   }

   public static <E> CompactLinkedHashSet<E> create(Collection<? extends E> var0) {
      CompactLinkedHashSet var1 = createWithExpectedSize(var0.size());
      var1.addAll(var0);
      return var1;
   }

   @SafeVarargs
   public static <E> CompactLinkedHashSet<E> create(E... var0) {
      CompactLinkedHashSet var1 = createWithExpectedSize(var0.length);
      Collections.addAll(var1, (E[])var0);
      return var1;
   }

   public static <E> CompactLinkedHashSet<E> createWithExpectedSize(int var0) {
      return new CompactLinkedHashSet<>(var0);
   }

   CompactLinkedHashSet() {
   }

   CompactLinkedHashSet(int var1) {
      super(var1);
   }

   @Override
   void init(int var1) {
      super.init(var1);
      this.firstEntry = -2;
      this.lastEntry = -2;
   }

   @Override
   int allocArrays() {
      int var1 = super.allocArrays();
      this.predecessor = new int[var1];
      this.successor = new int[var1];
      return var1;
   }

   @CanIgnoreReturnValue
   @Override
   Set<E> convertToHashFloodingResistantImplementation() {
      Set var1 = super.convertToHashFloodingResistantImplementation();
      this.predecessor = null;
      this.successor = null;
      return var1;
   }

   private int getPredecessor(int var1) {
      return this.requirePredecessors()[var1] - 1;
   }

   @Override
   int getSuccessor(int var1) {
      return this.requireSuccessors()[var1] - 1;
   }

   private void setSuccessor(int var1, int var2) {
      this.requireSuccessors()[var1] = var2 + 1;
   }

   private void setPredecessor(int var1, int var2) {
      this.requirePredecessors()[var1] = var2 + 1;
   }

   private void setSucceeds(int var1, int var2) {
      if (var1 == -2) {
         this.firstEntry = var2;
      } else {
         this.setSuccessor(var1, var2);
      }

      if (var2 == -2) {
         this.lastEntry = var1;
      } else {
         this.setPredecessor(var2, var1);
      }
   }

   @Override
   void insertEntry(int var1, @ParametricNullness E var2, int var3, int var4) {
      super.insertEntry(var1, (E)var2, var3, var4);
      this.setSucceeds(this.lastEntry, var1);
      this.setSucceeds(var1, -2);
   }

   @Override
   void moveLastEntry(int var1, int var2) {
      int var3 = this.size() - 1;
      super.moveLastEntry(var1, var2);
      this.setSucceeds(this.getPredecessor(var1), this.getSuccessor(var1));
      if (var1 < var3) {
         this.setSucceeds(this.getPredecessor(var3), var1);
         this.setSucceeds(var1, this.getSuccessor(var3));
      }

      this.requirePredecessors()[var3] = 0;
      this.requireSuccessors()[var3] = 0;
   }

   @Override
   void resizeEntries(int var1) {
      super.resizeEntries(var1);
      this.predecessor = Arrays.copyOf(this.requirePredecessors(), var1);
      this.successor = Arrays.copyOf(this.requireSuccessors(), var1);
   }

   @Override
   int firstEntryIndex() {
      return this.firstEntry;
   }

   @Override
   int adjustAfterRemove(int var1, int var2) {
      return var1 >= this.size() ? var2 : var1;
   }

   @Override
   public Object[] toArray() {
      return ObjectArrays.toArrayImpl(this);
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])ObjectArrays.toArrayImpl(this, var1);
   }

   @Override
   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this, 17);
   }

   @Override
   public void clear() {
      if (!this.needsAllocArrays()) {
         this.firstEntry = -2;
         this.lastEntry = -2;
         if (this.predecessor != null && this.successor != null) {
            Arrays.fill(this.predecessor, 0, this.size(), 0);
            Arrays.fill(this.successor, 0, this.size(), 0);
         }

         super.clear();
      }
   }

   private int[] requirePredecessors() {
      return Objects.requireNonNull(this.predecessor);
   }

   private int[] requireSuccessors() {
      return Objects.requireNonNull(this.successor);
   }
}
