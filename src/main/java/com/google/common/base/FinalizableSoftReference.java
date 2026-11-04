package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.lang.ref.SoftReference;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference {
   protected FinalizableSoftReference(T var1, FinalizableReferenceQueue var2) {
      super((T)var1, var2.queue);
      var2.cleanUp();
   }
}
