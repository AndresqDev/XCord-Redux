package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class ImmutableMapKeySet<K, V> extends IndexedImmutableSet<K> {
   private final ImmutableMap<K, V> map;

   ImmutableMapKeySet(ImmutableMap<K, V> var1) {
      this.map = var1;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public UnmodifiableIterator<K> iterator() {
      return this.map.keyIterator();
   }

   @Override
   public Spliterator<K> spliterator() {
      return this.map.keySpliterator();
   }

   @Override
   public boolean contains(Object var1) {
      return this.map.containsKey(var1);
   }

   @Override
   K get(int var1) {
      return this.map.entrySet().asList().get(var1).getKey();
   }

   @Override
   public void forEach(Consumer<? super K> var1) {
      Preconditions.checkNotNull(var1);
      this.map.forEach((var1x, var2) -> var1.accept(var1x));
   }

   @Override
   boolean isPartialView() {
      return true;
   }

   @GwtIncompatible
   private static class KeySetSerializedForm<K> implements Serializable {
      final ImmutableMap<K, ?> map;
      private static final long serialVersionUID = 0L;

      KeySetSerializedForm(ImmutableMap<K, ?> var1) {
         this.map = var1;
      }

      Object readResolve() {
         return this.map.keySet();
      }
   }
}
