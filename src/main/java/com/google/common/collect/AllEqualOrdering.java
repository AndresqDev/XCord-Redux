package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class AllEqualOrdering extends Ordering<Object> implements Serializable {
   static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
   private static final long serialVersionUID = 0L;

   @Override
   public int compare(Object var1, Object var2) {
      return 0;
   }

   @Override
   public <E> List<E> sortedCopy(Iterable<E> var1) {
      return Lists.newArrayList(var1);
   }

   @Override
   public <E> ImmutableList<E> immutableSortedCopy(Iterable<E> var1) {
      return ImmutableList.copyOf(var1);
   }

   @Override
   public <S> Ordering<S> reverse() {
      return this;
   }

   private Object readResolve() {
      return INSTANCE;
   }

   @Override
   public String toString() {
      return "Ordering.allEqual()";
   }
}
