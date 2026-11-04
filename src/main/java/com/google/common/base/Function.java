package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Function<F, T> extends java.util.function.Function<F, T> {
   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   T apply(@ParametricNullness F var1);

   @Override
   boolean equals(Object var1);
}
