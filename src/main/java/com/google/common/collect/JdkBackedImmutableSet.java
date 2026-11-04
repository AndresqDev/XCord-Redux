package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class JdkBackedImmutableSet<E> extends IndexedImmutableSet<E> {
   private final Set<?> delegate;
   private final ImmutableList<E> delegateList;

   JdkBackedImmutableSet(Set<?> var1, ImmutableList<E> var2) {
      this.delegate = var1;
      this.delegateList = var2;
   }

   @Override
   E get(int var1) {
      return this.delegateList.get(var1);
   }

   @Override
   public boolean contains(Object var1) {
      return this.delegate.contains(var1);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public int size() {
      return this.delegateList.size();
   }
}
