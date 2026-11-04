package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@FunctionalInterface
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Predicate<T> extends java.util.function.Predicate<T> {
   @CanIgnoreReturnValue
   boolean apply(@ParametricNullness T var1);

   @Override
   boolean equals(Object var1);

   @Override
   default boolean test(@ParametricNullness T var1) {
      return this.apply((T)var1);
   }
}
