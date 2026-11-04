package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface FutureCallback<V> {
   void onSuccess(@ParametricNullness V var1);

   void onFailure(Throwable var1);
}
