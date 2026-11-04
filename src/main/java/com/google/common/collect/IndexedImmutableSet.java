package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Spliterator;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class IndexedImmutableSet<E> extends ImmutableSet.CachingAsList<E> {
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

   @GwtIncompatible
   @Override
   int copyIntoArray(Object[] var1, int var2) {
      return this.asList().copyIntoArray(var1, var2);
   }

   @Override
   ImmutableList<E> createAsList() {
      return new ImmutableAsList<E>() {
         @Override
         public E get(int var1) {
            return (E)IndexedImmutableSet.this.get(var1);
         }

         @Override
         boolean isPartialView() {
            return IndexedImmutableSet.this.isPartialView();
         }

         @Override
         public int size() {
            return IndexedImmutableSet.this.size();
         }

         @Override
         ImmutableCollection<E> delegateCollection() {
            return IndexedImmutableSet.this;
         }
      };
   }
}
