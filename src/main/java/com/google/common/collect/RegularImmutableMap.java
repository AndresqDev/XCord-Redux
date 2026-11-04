package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {
   static final ImmutableMap<Object, Object> EMPTY = new RegularImmutableMap<>((Entry<Object, Object>[])ImmutableMap.EMPTY_ENTRY_ARRAY, null, 0);
   @VisibleForTesting
   static final double MAX_LOAD_FACTOR = 1.2;
   @VisibleForTesting
   static final double HASH_FLOODING_FPP = 0.001;
   @VisibleForTesting
   static final int MAX_HASH_BUCKET_LENGTH = 8;
   @VisibleForTesting
   final transient Entry<K, V>[] entries;
   private final transient ImmutableMapEntry<K, V>[] table;
   private final transient int mask;
   private static final long serialVersionUID = 0L;

   static <K, V> ImmutableMap<K, V> fromEntries(Entry<K, V>... var0) {
      return fromEntryArray(var0.length, var0);
   }

   static <K, V> ImmutableMap<K, V> fromEntryArray(int var0, Entry<K, V>[] var1) {
      Preconditions.checkPositionIndex(var0, var1.length);
      if (var0 == 0) {
         return (ImmutableMap<K, V>)EMPTY;
      } else {
         Object var2 = var0 == var1.length ? var1 : ImmutableMapEntry.createEntryArray(var0);
         int var3 = Hashing.closedTableSize(var0, 1.2);
         ImmutableMapEntry[] var4 = ImmutableMapEntry.createEntryArray(var3);
         int var5 = var3 - 1;

         for (int var6 = 0; var6 < var0; var6++) {
            Entry var7 = Objects.requireNonNull(var1[var6]);
            Object var8 = var7.getKey();
            Object var9 = var7.getValue();
            CollectPreconditions.checkEntryNotNull(var8, var9);
            int var10 = Hashing.smear(var8.hashCode()) & var5;
            ImmutableMapEntry var11 = var4[var10];
            Object var12 = var11 == null ? makeImmutable(var7, var8, var9) : new ImmutableMapEntry.NonTerminalImmutableMapEntry<>(var8, var9, var11);
            var4[var10] = (ImmutableMapEntry)var12;
            ((Object[])var2)[var6] = var12;
            int var13 = checkNoConflictInKeyBucket(var8, (Entry<?, ?>)var12, var11);
            if (var13 > 8) {
               return JdkBackedImmutableMap.create(var0, var1);
            }
         }

         return new RegularImmutableMap<>((Entry<K, V>[])var2, var4, var5);
      }
   }

   static <K, V> ImmutableMapEntry<K, V> makeImmutable(Entry<K, V> var0, K var1, V var2) {
      boolean var3 = var0 instanceof ImmutableMapEntry && ((ImmutableMapEntry)var0).isReusable();
      return var3 ? (ImmutableMapEntry)var0 : new ImmutableMapEntry<>((K)var1, (V)var2);
   }

   static <K, V> ImmutableMapEntry<K, V> makeImmutable(Entry<K, V> var0) {
      return makeImmutable(var0, (K)var0.getKey(), (V)var0.getValue());
   }

   private RegularImmutableMap(Entry<K, V>[] var1, ImmutableMapEntry<K, V>[] var2, int var3) {
      this.entries = var1;
      this.table = var2;
      this.mask = var3;
   }

   @CanIgnoreReturnValue
   static int checkNoConflictInKeyBucket(Object var0, Entry<?, ?> var1, ImmutableMapEntry<?, ?> var2) {
      int var3 = 0;

      while (var2 != null) {
         checkNoConflict(!var0.equals(var2.getKey()), "key", var1, var2);
         var3++;
         var2 = var2.getNextInKeyBucket();
      }

      return var3;
   }

   @Override
   public V get(Object var1) {
      return get(var1, this.table, this.mask);
   }

   static <V> V get(Object var0, ImmutableMapEntry<?, V>[] var1, int var2) {
      if (var0 != null && var1 != null) {
         int var3 = Hashing.smear(var0.hashCode()) & var2;

         for (ImmutableMapEntry var4 = var1[var3]; var4 != null; var4 = var4.getNextInKeyBucket()) {
            Object var5 = var4.getKey();
            if (var0.equals(var5)) {
               return (V)var4.getValue();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);

      for (Entry var5 : this.entries) {
         var1.accept(var5.getKey(), var5.getValue());
      }
   }

   @Override
   public int size() {
      return this.entries.length;
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
   }

   @Override
   ImmutableSet<K> createKeySet() {
      return new RegularImmutableMap.KeySet<>(this);
   }

   @Override
   ImmutableCollection<V> createValues() {
      return new RegularImmutableMap.Values<>(this);
   }

   @GwtCompatible(
      emulated = true
   )
   private static final class KeySet<K> extends IndexedImmutableSet<K> {
      private final RegularImmutableMap<K, ?> map;

      KeySet(RegularImmutableMap<K, ?> var1) {
         this.map = var1;
      }

      @Override
      K get(int var1) {
         return this.map.entries[var1].getKey();
      }

      @Override
      public boolean contains(Object var1) {
         return this.map.containsKey(var1);
      }

      @Override
      boolean isPartialView() {
         return true;
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @GwtIncompatible
      private static class SerializedForm<K> implements Serializable {
         final ImmutableMap<K, ?> map;
         private static final long serialVersionUID = 0L;

         SerializedForm(ImmutableMap<K, ?> var1) {
            this.map = var1;
         }

         Object readResolve() {
            return this.map.keySet();
         }
      }
   }

   @GwtCompatible(
      emulated = true
   )
   private static final class Values<K, V> extends ImmutableList<V> {
      final RegularImmutableMap<K, V> map;

      Values(RegularImmutableMap<K, V> var1) {
         this.map = var1;
      }

      @Override
      public V get(int var1) {
         return this.map.entries[var1].getValue();
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      boolean isPartialView() {
         return true;
      }

      @GwtIncompatible
      private static class SerializedForm<V> implements Serializable {
         final ImmutableMap<?, V> map;
         private static final long serialVersionUID = 0L;

         SerializedForm(ImmutableMap<?, V> var1) {
            this.map = var1;
         }

         Object readResolve() {
            return this.map.values();
         }
      }
   }
}
