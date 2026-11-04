package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class JdkBackedImmutableMap<K, V> extends ImmutableMap<K, V> {
   private final transient Map<K, V> delegateMap;
   private final transient ImmutableList<Entry<K, V>> entries;

   static <K, V> ImmutableMap<K, V> create(int var0, Entry<K, V>[] var1) {
      HashMap var2 = Maps.newHashMapWithExpectedSize(var0);

      for (int var3 = 0; var3 < var0; var3++) {
         var1[var3] = RegularImmutableMap.makeImmutable(Objects.requireNonNull(var1[var3]));
         Object var4 = var2.putIfAbsent(var1[var3].getKey(), var1[var3].getValue());
         if (var4 != null) {
            Entry var10001 = var1[var3];
            String var5 = String.valueOf(var1[var3].getKey());
            String var6 = String.valueOf(var4);
            throw conflictException(
               "key",
               var10001,
               new StringBuilder(1 + String.valueOf(var5).length() + String.valueOf(var6).length()).append(var5).append("=").append(var6).toString()
            );
         }
      }

      return new JdkBackedImmutableMap<>(var2, ImmutableList.asImmutableList(var1, var0));
   }

   JdkBackedImmutableMap(Map<K, V> var1, ImmutableList<Entry<K, V>> var2) {
      this.delegateMap = var1;
      this.entries = var2;
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   @Override
   public V get(Object var1) {
      return this.delegateMap.get(var1);
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.entries.forEach(var1x -> var1.accept(var1x.getKey(), var1x.getValue()));
   }

   @Override
   ImmutableSet<K> createKeySet() {
      return new ImmutableMapKeySet<>(this);
   }

   @Override
   ImmutableCollection<V> createValues() {
      return new ImmutableMapValues<>(this);
   }

   @Override
   boolean isPartialView() {
      return false;
   }
}
