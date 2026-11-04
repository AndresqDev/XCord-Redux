package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
   final transient K singleKey;
   final transient V singleValue;
   private final transient ImmutableBiMap<V, K> inverse;
   @LazyInit
   @RetainedWith
   private transient ImmutableBiMap<V, K> lazyInverse;

   SingletonImmutableBiMap(K var1, V var2) {
      CollectPreconditions.checkEntryNotNull(var1, var2);
      this.singleKey = (K)var1;
      this.singleValue = (V)var2;
      this.inverse = null;
   }

   private SingletonImmutableBiMap(K var1, V var2, ImmutableBiMap<V, K> var3) {
      this.singleKey = (K)var1;
      this.singleValue = (V)var2;
      this.inverse = var3;
   }

   @Override
   public V get(Object var1) {
      return this.singleKey.equals(var1) ? this.singleValue : null;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1).accept(this.singleKey, this.singleValue);
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.singleKey.equals(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.singleValue.equals(var1);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   ImmutableSet<Entry<K, V>> createEntrySet() {
      return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
   }

   @Override
   ImmutableSet<K> createKeySet() {
      return ImmutableSet.of(this.singleKey);
   }

   @Override
   public ImmutableBiMap<V, K> inverse() {
      if (this.inverse != null) {
         return this.inverse;
      } else {
         ImmutableBiMap var1 = this.lazyInverse;
         return var1 == null ? (this.lazyInverse = new SingletonImmutableBiMap<>(this.singleValue, this.singleKey, this)) : var1;
      }
   }
}
