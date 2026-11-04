package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
   static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap<>(
      null, null, (Entry<Object, Object>[])ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0
   );
   static final double MAX_LOAD_FACTOR = 1.2;
   private final transient ImmutableMapEntry<K, V>[] keyTable;
   private final transient ImmutableMapEntry<K, V>[] valueTable;
   @VisibleForTesting
   final transient Entry<K, V>[] entries;
   private final transient int mask;
   private final transient int hashCode;
   @LazyInit
   @RetainedWith
   private transient ImmutableBiMap<V, K> inverse;

   static <K, V> ImmutableBiMap<K, V> fromEntries(Entry<K, V>... var0) {
      return fromEntryArray(var0.length, var0);
   }

   static <K, V> ImmutableBiMap<K, V> fromEntryArray(int var0, Entry<K, V>[] var1) {
      Preconditions.checkPositionIndex(var0, var1.length);
      int var2 = Hashing.closedTableSize(var0, 1.2);
      int var3 = var2 - 1;
      ImmutableMapEntry[] var4 = ImmutableMapEntry.createEntryArray(var2);
      ImmutableMapEntry[] var5 = ImmutableMapEntry.createEntryArray(var2);
      Object var6 = var0 == var1.length ? var1 : ImmutableMapEntry.createEntryArray(var0);
      int var7 = 0;

      for (int var8 = 0; var8 < var0; var8++) {
         Entry var9 = Objects.requireNonNull(var1[var8]);
         Object var10 = var9.getKey();
         Object var11 = var9.getValue();
         CollectPreconditions.checkEntryNotNull(var10, var11);
         int var12 = var10.hashCode();
         int var13 = var11.hashCode();
         int var14 = Hashing.smear(var12) & var3;
         int var15 = Hashing.smear(var13) & var3;
         ImmutableMapEntry var16 = var4[var14];
         int var17 = RegularImmutableMap.checkNoConflictInKeyBucket(var10, var9, var16);
         ImmutableMapEntry var18 = var5[var15];
         int var19 = checkNoConflictInValueBucket(var11, var9, var18);
         if (var17 > 8 || var19 > 8) {
            return JdkBackedImmutableBiMap.create(var0, var1);
         }

         Object var20 = var18 == null && var16 == null
            ? RegularImmutableMap.makeImmutable(var9, var10, var11)
            : new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<>(var10, var11, var16, var18);
         var4[var14] = (ImmutableMapEntry)var20;
         var5[var15] = (ImmutableMapEntry)var20;
         ((Object[])var6)[var8] = var20;
         var7 += var12 ^ var13;
      }

      return new RegularImmutableBiMap<>(var4, var5, (Entry<K, V>[])var6, var3, var7);
   }

   private RegularImmutableBiMap(ImmutableMapEntry<K, V>[] var1, ImmutableMapEntry<K, V>[] var2, Entry<K, V>[] var3, int var4, int var5) {
      this.keyTable = var1;
      this.valueTable = var2;
      this.entries = var3;
      this.mask = var4;
      this.hashCode = var5;
   }

   @CanIgnoreReturnValue
   private static int checkNoConflictInValueBucket(Object var0, Entry<?, ?> var1, ImmutableMapEntry<?, ?> var2) {
      int var3 = 0;

      while (var2 != null) {
         checkNoConflict(!var0.equals(var2.getValue()), "value", var1, var2);
         var3++;
         var2 = var2.getNextInValueBucket();
      }

      return var3;
   }

   @Override
   public V get(Object var1) {
      return RegularImmutableMap.get(var1, this.keyTable, this.mask);
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      return (ImmutableSet<Entry<K, V>>)(this.isEmpty() ? ImmutableSet.of() : new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries));
   }

   @Override
   ImmutableSet<K> createKeySet() {
      return new ImmutableMapKeySet<>(this);
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);

      for (Entry var5 : this.entries) {
         var1.accept(var5.getKey(), var5.getValue());
      }
   }

   @Override
   boolean isHashCodeFast() {
      return true;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public int size() {
      return this.entries.length;
   }

   @Override
   public ImmutableBiMap<V, K> inverse() {
      if (this.isEmpty()) {
         return ImmutableBiMap.of();
      } else {
         ImmutableBiMap var1 = this.inverse;
         return var1 == null ? (this.inverse = new RegularImmutableBiMap.Inverse()) : var1;
      }
   }

   private final class Inverse extends ImmutableBiMap<V, K> {
      private Inverse() {
      }

      @Override
      public int size() {
         return this.inverse().size();
      }

      @Override
      public ImmutableBiMap<K, V> inverse() {
         return RegularImmutableBiMap.this;
      }

      @Override
      public void forEach(BiConsumer<? super V, ? super K> var1) {
         Preconditions.checkNotNull(var1);
         RegularImmutableBiMap.this.forEach((var1x, var2) -> var1.accept(var2, var1x));
      }

      @Override
      public K get(Object var1) {
         if (var1 != null && RegularImmutableBiMap.this.valueTable != null) {
            int var2 = Hashing.smear(var1.hashCode()) & RegularImmutableBiMap.this.mask;

            for (ImmutableMapEntry var3 = RegularImmutableBiMap.this.valueTable[var2]; var3 != null; var3 = var3.getNextInValueBucket()) {
               if (var1.equals(var3.getValue())) {
                  return (K)var3.getKey();
               }
            }

            return null;
         } else {
            return null;
         }
      }

      @Override
      ImmutableSet<V> createKeySet() {
         return new ImmutableMapKeySet<>(this);
      }

      @Override
      ImmutableSet<Entry<V, K>> createEntrySet() {
         return new RegularImmutableBiMap.Inverse.InverseEntrySet();
      }

      @Override
      boolean isPartialView() {
         return false;
      }

      @Override
      Object writeReplace() {
         return new RegularImmutableBiMap.InverseSerializedForm<>(RegularImmutableBiMap.this);
      }

      final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
         @Override
         ImmutableMap<V, K> map() {
            return Inverse.this;
         }

         @Override
         boolean isHashCodeFast() {
            return true;
         }

         @Override
         public int hashCode() {
            return RegularImmutableBiMap.this.hashCode;
         }

         @Override
         public UnmodifiableIterator<Entry<V, K>> iterator() {
            return (UnmodifiableIterator<Entry<V, K>>)this.asList().iterator();
         }

         @Override
         public void forEach(Consumer<? super Entry<V, K>> var1) {
            this.asList().forEach(var1);
         }

         @Override
         ImmutableList<Entry<V, K>> createAsList() {
            return new ImmutableAsList<Entry<V, K>>() {
               public Entry<V, K> get(int var1) {
                  Entry var2 = RegularImmutableBiMap.this.entries[var1];
                  return Maps.immutableEntry((V)var2.getValue(), (K)var2.getKey());
               }

               @Override
               ImmutableCollection<Entry<V, K>> delegateCollection() {
                  return InverseEntrySet.this;
               }
            };
         }
      }
   }

   private static class InverseSerializedForm<K, V> implements Serializable {
      private final ImmutableBiMap<K, V> forward;
      private static final long serialVersionUID = 1L;

      InverseSerializedForm(ImmutableBiMap<K, V> var1) {
         this.forward = var1;
      }

      Object readResolve() {
         return this.forward.inverse();
      }
   }
}
