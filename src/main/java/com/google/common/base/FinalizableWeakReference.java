package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.lang.ref.WeakReference;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class FinalizableWeakReference<T> extends WeakReference<T> implements FinalizableReference {
   protected FinalizableWeakReference(T var1, FinalizableReferenceQueue var2) {
      super((T)var1, var2.queue);
      var2.cleanUp();
   }
}
