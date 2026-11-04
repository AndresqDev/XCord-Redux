package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {
   static final int SPLITERATOR_CHARACTERISTICS = 1297;
   static final int MAX_TABLE_SIZE = 1073741824;
   private static final double DESIRED_LOAD_FACTOR = 0.7;
   private static final int CUTOFF = 751619276;

   public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
      return CollectCollectors.toImmutableSet();
   }

   public static <E> ImmutableSet<E> of() {
      return (ImmutableSet<E>)RegularImmutableSet.EMPTY;
   }

   public static <E> ImmutableSet<E> of(E var0) {
      return new SingletonImmutableSet<>((E)var0);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1) {
      return construct(2, 2, var0, var1);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2) {
      return construct(3, 3, var0, var1, var2);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3) {
      return construct(4, 4, var0, var1, var2, var3);
   }

   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3, E var4) {
      return construct(5, 5, var0, var1, var2, var3, var4);
   }

   @SafeVarargs
   public static <E> ImmutableSet<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      Preconditions.checkArgument(var6.length <= 2147483641, "the total number of elements must fit in an int");
      byte var7 = 6;
      Object[] var8 = new Object[6 + var6.length];
      var8[0] = var0;
      var8[1] = var1;
      var8[2] = var2;
      var8[3] = var3;
      var8[4] = var4;
      var8[5] = var5;
      System.arraycopy(var6, 0, var8, 6, var6.length);
      return construct(var8.length, var8.length, var8);
   }

   private static <E> ImmutableSet<E> constructUnknownDuplication(int var0, Object... var1) {
      return construct(var0, Math.max(4, IntMath.sqrt(var0, RoundingMode.CEILING)), var1);
   }

   private static <E> ImmutableSet<E> construct(int var0, int var1, Object... var2) {
      switch (var0) {
         case 0:
            return of();
         case 1:
            Object var3 = var2[0];
            return of((E)var3);
         default:
            Object var4 = new ImmutableSet.RegularSetBuilderImpl(var1);

            for (int var5 = 0; var5 < var0; var5++) {
               Object var6 = Preconditions.checkNotNull(var2[var5]);
               var4 = ((ImmutableSet.SetBuilderImpl<Object>)var4).add(var6);
            }

            return ((ImmutableSet.SetBuilderImpl)var4).review().build();
      }
   }

   public static <E> ImmutableSet<E> copyOf(Collection<? extends E> var0) {
      if (var0 instanceof ImmutableSet && !(var0 instanceof SortedSet)) {
         ImmutableSet var1 = (ImmutableSet)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      } else if (var0 instanceof EnumSet) {
         return copyOfEnumSet((EnumSet)var0);
      }

      Object[] var2 = var0.toArray();
      return var0 instanceof Set ? construct(var2.length, var2.length, var2) : constructUnknownDuplication(var2.length, var2);
   }

   public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> var0) {
      return var0 instanceof Collection ? copyOf((Collection<? extends E>)var0) : copyOf(var0.iterator());
   }

   public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> var0) {
      if (!var0.hasNext()) {
         return of();
      } else {
         Object var1 = var0.next();
         return !var0.hasNext() ? of((E)var1) : new ImmutableSet.Builder<E>().add((E)var1).addAll(var0).build();
      }
   }

   public static <E> ImmutableSet<E> copyOf(E[] var0) {
      switch (var0.length) {
         case 0:
            return of();
         case 1:
            return of((E)var0[0]);
         default:
            return constructUnknownDuplication(var0.length, (Object[])var0.clone());
      }
   }

   private static ImmutableSet copyOfEnumSet(EnumSet var0) {
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf(var0));
   }

   ImmutableSet() {
   }

   boolean isHashCodeFast() {
      return false;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 instanceof ImmutableSet && this.isHashCodeFast() && ((ImmutableSet)var1).isHashCodeFast() && this.hashCode() != var1.hashCode()
            ? false
            : Sets.equalsImpl(this, var1);
      }
   }

   @Override
   public int hashCode() {
      return Sets.hashCodeImpl(this);
   }

   @Override
   public abstract UnmodifiableIterator<E> iterator();

   @Override
   Object writeReplace() {
      return new ImmutableSet.SerializedForm(this.toArray());
   }

   public static <E> ImmutableSet.Builder<E> builder() {
      return new ImmutableSet.Builder<>();
   }

   @Beta
   public static <E> ImmutableSet.Builder<E> builderWithExpectedSize(int var0) {
      CollectPreconditions.checkNonnegative(var0, "expectedSize");
      return new ImmutableSet.Builder<>(var0);
   }

   static int chooseTableSize(int var0) {
      var0 = Math.max(var0, 2);
      if (var0 >= 751619276) {
         Preconditions.checkArgument(var0 < 1073741824, "collection too large");
         return 1073741824;
      } else {
         int var1 = Integer.highestOneBit(var0 - 1) << 1;

         while ((double)var1 * 0.7 < (double)var0) {
            var1 <<= 1;
         }

         return var1;
      }
   }

   public static class Builder<E> extends ImmutableCollection.Builder<E> {
      private ImmutableSet.SetBuilderImpl<E> impl;
      boolean forceCopy;

      public Builder() {
         this(0);
      }

      Builder(int var1) {
         if (var1 > 0) {
            this.impl = new ImmutableSet.RegularSetBuilderImpl<>(var1);
         } else {
            this.impl = ImmutableSet.EmptySetBuilderImpl.instance();
         }
      }

      Builder(boolean var1) {
         this.impl = null;
      }

      @VisibleForTesting
      void forceJdk() {
         this.impl = new ImmutableSet.JdkBackedSetBuilderImpl<>(this.impl);
      }

      final void copyIfNecessary() {
         if (this.forceCopy) {
            this.copy();
            this.forceCopy = false;
         }
      }

      void copy() {
         Objects.requireNonNull(this.impl);
         this.impl = this.impl.copy();
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> add(E var1) {
         Objects.requireNonNull(this.impl);
         Preconditions.checkNotNull(var1);
         this.copyIfNecessary();
         this.impl = this.impl.add((E)var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> add(E... var1) {
         super.add((E[])var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> addAll(Iterable<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSet.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      ImmutableSet.Builder<E> combine(ImmutableSet.Builder<E> var1) {
         Objects.requireNonNull(this.impl);
         Objects.requireNonNull(var1.impl);
         this.copyIfNecessary();
         this.impl = this.impl.combine(var1.impl);
         return this;
      }

      public ImmutableSet<E> build() {
         Objects.requireNonNull(this.impl);
         this.forceCopy = true;
         this.impl = this.impl.review();
         return this.impl.build();
      }
   }

   @GwtCompatible
   abstract static class CachingAsList<E> extends ImmutableSet<E> {
      @LazyInit
      @RetainedWith
      private transient ImmutableList<E> asList;

      @Override
      public ImmutableList<E> asList() {
         ImmutableList var1 = this.asList;
         return var1 == null ? (this.asList = this.createAsList()) : var1;
      }

      ImmutableList<E> createAsList() {
         return new RegularImmutableAsList<>(this, this.toArray());
      }
   }

   private static final class EmptySetBuilderImpl<E> extends ImmutableSet.SetBuilderImpl<E> {
      private static final ImmutableSet.EmptySetBuilderImpl<Object> INSTANCE = new ImmutableSet.EmptySetBuilderImpl<>();

      static <E> ImmutableSet.SetBuilderImpl<E> instance() {
         return (ImmutableSet.SetBuilderImpl<E>)INSTANCE;
      }

      private EmptySetBuilderImpl() {
         super(0);
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> add(E var1) {
         return new ImmutableSet.RegularSetBuilderImpl<E>(4).add((E)var1);
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> copy() {
         return this;
      }

      @Override
      ImmutableSet<E> build() {
         return ImmutableSet.of();
      }
   }

   abstract static class Indexed<E> extends ImmutableSet.CachingAsList<E> {
      abstract E get(int var1);

      @Override
      public UnmodifiableIterator<E> iterator() {
         return this.asList().iterator();
      }

      @Override
      public Spliterator<E> spliterator() {
         return CollectSpliterators.indexed(this.size(), 1297, this::get);
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
      int copyIntoArray(Object[] var1, int var2) {
         return this.asList().copyIntoArray(var1, var2);
      }

      @Override
      ImmutableList<E> createAsList() {
         return new ImmutableAsList<E>() {
            @Override
            public E get(int var1) {
               return (E)Indexed.this.get(var1);
            }

            ImmutableSet.Indexed<E> delegateCollection() {
               return Indexed.this;
            }
         };
      }
   }

   private static final class JdkBackedSetBuilderImpl<E> extends ImmutableSet.SetBuilderImpl<E> {
      private final Set<Object> delegate = Sets.newHashSetWithExpectedSize(this.distinct);

      JdkBackedSetBuilderImpl(ImmutableSet.SetBuilderImpl<E> var1) {
         super(var1);

         for (int var2 = 0; var2 < this.distinct; var2++) {
            this.delegate.add(Objects.requireNonNull(this.dedupedElements[var2]));
         }
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> add(E var1) {
         Preconditions.checkNotNull(var1);
         if (this.delegate.add(var1)) {
            this.addDedupedElement((E)var1);
         }

         return this;
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> copy() {
         return new ImmutableSet.JdkBackedSetBuilderImpl<>(this);
      }

      @Override
      ImmutableSet<E> build() {
         switch (this.distinct) {
            case 0:
               return ImmutableSet.of();
            case 1:
               return ImmutableSet.of(Objects.requireNonNull(this.dedupedElements[0]));
            default:
               return new JdkBackedImmutableSet<>(this.delegate, ImmutableList.asImmutableList(this.dedupedElements, this.distinct));
         }
      }
   }

   private static final class RegularSetBuilderImpl<E> extends ImmutableSet.SetBuilderImpl<E> {
      @Nullable
      private Object[] hashTable;
      private int maxRunBeforeFallback;
      private int expandTableThreshold;
      private int hashCode;
      static final int MAX_RUN_MULTIPLIER = 13;

      RegularSetBuilderImpl(int var1) {
         super(var1);
         this.hashTable = null;
         this.maxRunBeforeFallback = 0;
         this.expandTableThreshold = 0;
      }

      RegularSetBuilderImpl(ImmutableSet.RegularSetBuilderImpl<E> var1) {
         super(var1);
         this.hashTable = var1.hashTable == null ? null : (Object[])var1.hashTable.clone();
         this.maxRunBeforeFallback = var1.maxRunBeforeFallback;
         this.expandTableThreshold = var1.expandTableThreshold;
         this.hashCode = var1.hashCode;
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> add(E var1) {
         Preconditions.checkNotNull(var1);
         if (this.hashTable == null) {
            if (this.distinct == 0) {
               this.addDedupedElement((E)var1);
               return this;
            } else {
               this.ensureTableCapacity(this.dedupedElements.length);
               Object var2 = this.dedupedElements[0];
               this.distinct--;
               return this.insertInHashTable((E)var2).add((E)var1);
            }
         } else {
            return this.insertInHashTable((E)var1);
         }
      }

      private ImmutableSet.SetBuilderImpl<E> insertInHashTable(E var1) {
         Objects.requireNonNull(this.hashTable);
         int var2 = var1.hashCode();
         int var3 = Hashing.smear(var2);
         int var4 = this.hashTable.length - 1;

         for (int var5 = var3; var5 - var3 < this.maxRunBeforeFallback; var5++) {
            int var6 = var5 & var4;
            Object var7 = this.hashTable[var6];
            if (var7 == null) {
               this.addDedupedElement((E)var1);
               this.hashTable[var6] = var1;
               this.hashCode += var2;
               this.ensureTableCapacity(this.distinct);
               return this;
            }

            if (var7.equals(var1)) {
               return this;
            }
         }

         return new ImmutableSet.JdkBackedSetBuilderImpl<>(this).add((E)var1);
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> copy() {
         return new ImmutableSet.RegularSetBuilderImpl<>(this);
      }

      @Override
      ImmutableSet.SetBuilderImpl<E> review() {
         if (this.hashTable == null) {
            return this;
         } else {
            int var1 = ImmutableSet.chooseTableSize(this.distinct);
            if (var1 * 2 < this.hashTable.length) {
               this.hashTable = rebuildHashTable(var1, this.dedupedElements, this.distinct);
               this.maxRunBeforeFallback = maxRunBeforeFallback(var1);
               this.expandTableThreshold = (int)(0.7 * (double)var1);
            }

            return (ImmutableSet.SetBuilderImpl<E>)(hashFloodingDetected(this.hashTable) ? new ImmutableSet.JdkBackedSetBuilderImpl<>(this) : this);
         }
      }

      @Override
      ImmutableSet<E> build() {
         switch (this.distinct) {
            case 0:
               return ImmutableSet.of();
            case 1:
               return ImmutableSet.of(Objects.requireNonNull(this.dedupedElements[0]));
            default:
               Object[] var1 = (Object[])(this.distinct == this.dedupedElements.length
                  ? this.dedupedElements
                  : Arrays.copyOf(this.dedupedElements, this.distinct));
               return new RegularImmutableSet<>(var1, this.hashCode, Objects.requireNonNull(this.hashTable), this.hashTable.length - 1);
         }
      }

      static Object[] rebuildHashTable(int var0, Object[] var1, int var2) {
         Object[] var3 = new Object[var0];
         int var4 = var3.length - 1;

         for (int var5 = 0; var5 < var2; var5++) {
            Object var6 = Objects.requireNonNull(var1[var5]);
            int var7 = Hashing.smear(var6.hashCode());
            int var8 = var7;

            while (true) {
               int var9 = var8 & var4;
               if (var3[var9] == null) {
                  var3[var9] = var6;
                  break;
               }

               var8++;
            }
         }

         return var3;
      }

      void ensureTableCapacity(int var1) {
         int var2;
         if (this.hashTable == null) {
            var2 = ImmutableSet.chooseTableSize(var1);
            this.hashTable = new Object[var2];
         } else {
            if (var1 <= this.expandTableThreshold || this.hashTable.length >= 1073741824) {
               return;
            }

            var2 = this.hashTable.length * 2;
            this.hashTable = rebuildHashTable(var2, this.dedupedElements, this.distinct);
         }

         this.maxRunBeforeFallback = maxRunBeforeFallback(var2);
         this.expandTableThreshold = (int)(0.7 * (double)var2);
      }

      static boolean hashFloodingDetected(Object[] var0) {
         int var1 = maxRunBeforeFallback(var0.length);
         int var2 = var0.length - 1;
         int var3 = 0;
         int var4 = 0;

         label35:
         while (var3 < var0.length) {
            if (var3 == var4 && var0[var3] == null) {
               if (var0[var3 + var1 - 1 & var2] == null) {
                  var3 += var1;
               } else {
                  var3++;
               }

               var4 = var3;
            } else {
               for (int var5 = var3 + var1 - 1; var5 >= var4; var5--) {
                  if (var0[var5 & var2] == null) {
                     var4 = var3 + var1;
                     var3 = var5 + 1;
                     continue label35;
                  }
               }

               return true;
            }
         }

         return false;
      }

      static int maxRunBeforeFallback(int var0) {
         return 13 * IntMath.log2(var0, RoundingMode.UNNECESSARY);
      }
   }

   private static class SerializedForm implements Serializable {
      final Object[] elements;
      private static final long serialVersionUID = 0L;

      SerializedForm(Object[] var1) {
         this.elements = var1;
      }

      Object readResolve() {
         return ImmutableSet.copyOf(this.elements);
      }
   }

   private abstract static class SetBuilderImpl<E> {
      E[] dedupedElements;
      int distinct;

      SetBuilderImpl(int var1) {
         this.dedupedElements = (E[])(new Object[var1]);
         this.distinct = 0;
      }

      SetBuilderImpl(ImmutableSet.SetBuilderImpl<E> var1) {
         this.dedupedElements = Arrays.copyOf(var1.dedupedElements, var1.dedupedElements.length);
         this.distinct = var1.distinct;
      }

      private void ensureCapacity(int var1) {
         if (var1 > this.dedupedElements.length) {
            int var2 = ImmutableCollection.Builder.expandedCapacity(this.dedupedElements.length, var1);
            this.dedupedElements = Arrays.copyOf(this.dedupedElements, var2);
         }
      }

      final void addDedupedElement(E var1) {
         this.ensureCapacity(this.distinct + 1);
         this.dedupedElements[this.distinct++] = (E)var1;
      }

      abstract ImmutableSet.SetBuilderImpl<E> add(E var1);

      final ImmutableSet.SetBuilderImpl<E> combine(ImmutableSet.SetBuilderImpl<E> var1) {
         ImmutableSet.SetBuilderImpl var2 = this;

         for (int var3 = 0; var3 < var1.distinct; var3++) {
            var2 = var2.add(Objects.requireNonNull(var1.dedupedElements[var3]));
         }

         return var2;
      }

      abstract ImmutableSet.SetBuilderImpl<E> copy();

      ImmutableSet.SetBuilderImpl<E> review() {
         return this;
      }

      abstract ImmutableSet<E> build();
   }
}
