package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class JdkBackedImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
   private final transient ImmutableList<Entry<K, V>> entries;
   private final Map<K, V> forwardDelegate;
   private final Map<V, K> backwardDelegate;
   @LazyInit
   @RetainedWith
   private transient JdkBackedImmutableBiMap<V, K> inverse;

   @VisibleForTesting
   static <K, V> ImmutableBiMap<K, V> create(int var0, Entry<K, V>[] var1) {
      HashMap var2 = Maps.newHashMapWithExpectedSize(var0);
      HashMap var3 = Maps.newHashMapWithExpectedSize(var0);

      for (int var4 = 0; var4 < var0; var4++) {
         ImmutableMapEntry var5 = RegularImmutableMap.makeImmutable(Objects.requireNonNull(var1[var4]));
         var1[var4] = var5;
         Object var6 = var2.putIfAbsent(var5.getKey(), var5.getValue());
         if (var6 != null) {
            String var11 = String.valueOf(var5.getKey());
            String var12 = String.valueOf(var6);
            throw conflictException(
               "key",
               new StringBuilder(1 + String.valueOf(var11).length() + String.valueOf(var12).length()).append(var11).append("=").append(var12).toString(),
               var1[var4]
            );
         }

         Object var7 = var3.putIfAbsent(var5.getValue(), var5.getKey());
         if (var7 != null) {
            String var8 = String.valueOf(var7);
            String var9 = String.valueOf(var5.getValue());
            throw conflictException(
               "value",
               new StringBuilder(1 + String.valueOf(var8).length() + String.valueOf(var9).length()).append(var8).append("=").append(var9).toString(),
               var1[var4]
            );
         }
      }

      ImmutableList var10 = ImmutableList.asImmutableList(var1, var0);
      return new JdkBackedImmutableBiMap<>(var10, var2, var3);
   }

   private JdkBackedImmutableBiMap(ImmutableList<Entry<K, V>> var1, Map<K, V> var2, Map<V, K> var3) {
      this.entries = var1;
      this.forwardDelegate = var2;
      this.backwardDelegate = var3;
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   @Override
   public ImmutableBiMap<V, K> inverse() {
      JdkBackedImmutableBiMap var1 = this.inverse;
      if (var1 == null) {
         this.inverse = var1 = new JdkBackedImmutableBiMap<>(new JdkBackedImmutableBiMap.InverseEntries(), this.backwardDelegate, this.forwardDelegate);
         var1.inverse = (JdkBackedImmutableBiMap<V, K>)this;
      }

      return var1;
   }

   @Override
   public V get(Object var1) {
      return this.forwardDelegate.get(var1);
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
   }

   @Override
   ImmutableSet<K> createKeySet() {
      return new ImmutableMapKeySet<>(this);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   private final class InverseEntries extends ImmutableList<Entry<V, K>> {
      private InverseEntries() {
      }

      public Entry<V, K> get(int var1) {
         Entry var2 = JdkBackedImmutableBiMap.this.entries.get(var1);
         return Maps.immutableEntry((V)var2.getValue(), (K)var2.getKey());
      }

      @Override
      boolean isPartialView() {
         return false;
      }

      @Override
      public int size() {
         return JdkBackedImmutableBiMap.this.entries.size();
      }
   }
}
