package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Arrays;
import java.util.Collection;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true,
   serializable = true
)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
   private static final Multisets.ImmutableEntry<?>[] EMPTY_ARRAY = new Multisets.ImmutableEntry[0];
   static final ImmutableMultiset<Object> EMPTY = create(ImmutableList.of());
   @VisibleForTesting
   static final double MAX_LOAD_FACTOR = 1.0;
   @VisibleForTesting
   static final double HASH_FLOODING_FPP = 0.001;
   @VisibleForTesting
   static final int MAX_HASH_BUCKET_LENGTH = 9;
   private final transient Multisets.ImmutableEntry<E>[] entries;
   private final transient Multisets.ImmutableEntry<?>[] hashTable;
   private final transient int size;
   private final transient int hashCode;
   @LazyInit
   private transient ImmutableSet<E> elementSet;

   static <E> ImmutableMultiset<E> create(Collection<? extends Multiset.Entry<? extends E>> var0) {
      int var1 = var0.size();
      Multisets.ImmutableEntry[] var2 = new Multisets.ImmutableEntry[var1];
      if (var1 == 0) {
         return new RegularImmutableMultiset<>(var2, EMPTY_ARRAY, 0, 0, ImmutableSet.of());
      } else {
         int var3 = Hashing.closedTableSize(var1, 1.0);
         int var4 = var3 - 1;
         Multisets.ImmutableEntry[] var5 = new Multisets.ImmutableEntry[var3];
         int var6 = 0;
         int var7 = 0;
         long var8 = 0L;

         for (Multiset.Entry var11 : var0) {
            Object var13 = Preconditions.checkNotNull(var11.getElement());
            int var14 = var11.getCount();
            int var15 = var13.hashCode();
            int var16 = Hashing.smear(var15) & var4;
            Multisets.ImmutableEntry var17 = var5[var16];
            Object var18;
            if (var17 == null) {
               boolean var19 = var11 instanceof Multisets.ImmutableEntry && !(var11 instanceof RegularImmutableMultiset.NonTerminalEntry);
               var18 = var19 ? (Multisets.ImmutableEntry)var11 : new Multisets.ImmutableEntry<>(var13, var14);
            } else {
               var18 = new RegularImmutableMultiset.NonTerminalEntry<>(var13, var14, var17);
            }

            var7 += var15 ^ var14;
            var2[var6++] = (Multisets.ImmutableEntry)var18;
            var5[var16] = (Multisets.ImmutableEntry)var18;
            var8 += (long)var14;
         }

         return (ImmutableMultiset<E>)(hashFloodingDetected(var5)
            ? JdkBackedImmutableMultiset.create(ImmutableList.asImmutableList(var2))
            : new RegularImmutableMultiset<>(var2, var5, Ints.saturatedCast(var8), var7, null));
      }
   }

   private static boolean hashFloodingDetected(Multisets.ImmutableEntry<?>[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         int var2 = 0;

         for (Multisets.ImmutableEntry var3 = var0[var1]; var3 != null; var3 = var3.nextInBucket()) {
            if (++var2 > 9) {
               return true;
            }
         }
      }

      return false;
   }

   private RegularImmutableMultiset(Multisets.ImmutableEntry<E>[] var1, Multisets.ImmutableEntry<?>[] var2, int var3, int var4, ImmutableSet<E> var5) {
      this.entries = var1;
      this.hashTable = var2;
      this.size = var3;
      this.hashCode = var4;
      this.elementSet = var5;
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public int count(Object var1) {
      Multisets.ImmutableEntry[] var2 = this.hashTable;
      if (var1 != null && var2.length != 0) {
         int var3 = Hashing.smearedHash(var1);
         int var4 = var2.length - 1;

         for (Multisets.ImmutableEntry var5 = var2[var3 & var4]; var5 != null; var5 = var5.nextInBucket()) {
            if (Objects.equal(var1, var5.getElement())) {
               return var5.getCount();
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public ImmutableSet<E> elementSet() {
      ImmutableSet var1 = this.elementSet;
      return var1 == null ? (this.elementSet = new ImmutableMultiset.ElementSet<>(Arrays.asList(this.entries), this)) : var1;
   }

   @Override
   Multiset.Entry<E> getEntry(int var1) {
      return this.entries[var1];
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
      private final Multisets.ImmutableEntry<E> nextInBucket;

      NonTerminalEntry(E var1, int var2, Multisets.ImmutableEntry<E> var3) {
         super((E)var1, var2);
         this.nextInBucket = var3;
      }

      @Override
      public Multisets.ImmutableEntry<E> nextInBucket() {
         return this.nextInBucket;
      }
   }
}
