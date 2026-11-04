package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {
   private final ImmutableMap<K, V> map;

   ImmutableMapValues(ImmutableMap<K, V> var1) {
      this.map = var1;
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public UnmodifiableIterator<V> iterator() {
      return new UnmodifiableIterator<V>() {
         final UnmodifiableIterator<Entry<K, V>> entryItr = ImmutableMapValues.this.map.entrySet().iterator();

         @Override
         public boolean hasNext() {
            return this.entryItr.hasNext();
         }

         @Override
         public V next() {
            return this.entryItr.next().getValue();
         }
      };
   }

   @Override
   public Spliterator<V> spliterator() {
      return CollectSpliterators.map(this.map.entrySet().spliterator(), Entry::getValue);
   }

   @Override
   public boolean contains(Object var1) {
      return var1 != null && Iterators.contains(this.iterator(), var1);
   }

   @Override
   boolean isPartialView() {
      return true;
   }

   @Override
   public ImmutableList<V> asList() {
      final ImmutableList var1 = this.map.entrySet().asList();
      return new ImmutableAsList<V>() {
         @Override
         public V get(int var1x) {
            return (V)((Entry)var1.get(var1x)).getValue();
         }

         @Override
         ImmutableCollection<V> delegateCollection() {
            return ImmutableMapValues.this;
         }
      };
   }

   @GwtIncompatible
   @Override
   public void forEach(Consumer<? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.map.forEach((var1x, var2) -> var1.accept(var2));
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
