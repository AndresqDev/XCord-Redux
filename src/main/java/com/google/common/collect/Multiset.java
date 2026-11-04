package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multiset<E> extends Collection<E> {
   @Override
   int size();

   int count(@CompatibleWith("E") Object var1);

   @CanIgnoreReturnValue
   int add(@ParametricNullness E var1, int var2);

   @CanIgnoreReturnValue
   @Override
   boolean add(@ParametricNullness E var1);

   @CanIgnoreReturnValue
   int remove(@CompatibleWith("E") Object var1, int var2);

   @CanIgnoreReturnValue
   @Override
   boolean remove(Object var1);

   @CanIgnoreReturnValue
   int setCount(@ParametricNullness E var1, int var2);

   @CanIgnoreReturnValue
   boolean setCount(@ParametricNullness E var1, int var2, int var3);

   Set<E> elementSet();

   Set<Multiset.Entry<E>> entrySet();

   @Beta
   default void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      this.entrySet().forEach(var1x -> var1.accept(var1x.getElement(), var1x.getCount()));
   }

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   @Override
   String toString();

   @Override
   Iterator<E> iterator();

   @Override
   boolean contains(Object var1);

   @Override
   boolean containsAll(Collection<?> var1);

   @CanIgnoreReturnValue
   @Override
   boolean removeAll(Collection<?> var1);

   @CanIgnoreReturnValue
   @Override
   boolean retainAll(Collection<?> var1);

   @Override
   default void forEach(Consumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      this.entrySet().forEach(var1x -> {
         Object var2 = var1x.getElement();
         int var3 = var1x.getCount();

         for (int var4 = 0; var4 < var3; var4++) {
            var1.accept(var2);
         }
      });
   }

   @Override
   default Spliterator<E> spliterator() {
      return Multisets.spliteratorImpl(this);
   }

   public interface Entry<E> {
      @ParametricNullness
      E getElement();

      int getCount();

      @Override
      boolean equals(Object var1);

      @Override
      int hashCode();

      @Override
      String toString();
   }
}
