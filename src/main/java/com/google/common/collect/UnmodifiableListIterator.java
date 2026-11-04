package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.DoNotCall;
import java.util.ListIterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E> {
   protected UnmodifiableListIterator() {
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void add(@ParametricNullness E var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void set(@ParametricNullness E var1) {
      throw new UnsupportedOperationException();
   }
}
