package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
   private final transient EnumMap<K, V> delegate;

   static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> var0) {
      switch (var0.size()) {
         case 0:
            return ImmutableMap.of();
         case 1:
            Entry var1 = Iterables.getOnlyElement(var0.entrySet());
            return ImmutableMap.of((K)var1.getKey(), (V)var1.getValue());
         default:
            return new ImmutableEnumMap<>(var0);
      }
   }

   private ImmutableEnumMap(EnumMap<K, V> var1) {
      this.delegate = var1;
      Preconditions.checkArgument(!var1.isEmpty());
   }

   @Override
   UnmodifiableIterator<K> keyIterator() {
      return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
   }

   @Override
   Spliterator<K> keySpliterator() {
      return this.delegate.keySet().spliterator();
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.delegate.containsKey(var1);
   }

   @Override
   public V get(Object var1) {
      return this.delegate.get(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 instanceof ImmutableEnumMap) {
            var1 = ((ImmutableEnumMap)var1).delegate;
         }

         return this.delegate.equals(var1);
      }
   }

   @Override
   UnmodifiableIterator<Entry<K, V>> entryIterator() {
      return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
   }

   @Override
   Spliterator<Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.map(this.delegate.entrySet().spliterator(), Maps::unmodifiableEntry);
   }

   @Override
   public void forEach(BiConsumer<? super K, ? super V> var1) {
      this.delegate.forEach(var1);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   Object writeReplace() {
      return new ImmutableEnumMap.EnumSerializedForm<>(this.delegate);
   }

   private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable {
      final EnumMap<K, V> delegate;
      private static final long serialVersionUID = 0L;

      EnumSerializedForm(EnumMap<K, V> var1) {
         this.delegate = var1;
      }

      Object readResolve() {
         return new ImmutableEnumMap(this.delegate);
      }
   }
}
