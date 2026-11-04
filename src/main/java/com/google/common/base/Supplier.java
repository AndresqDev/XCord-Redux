package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Supplier<T> extends java.util.function.Supplier<T> {
   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   T get();
}
