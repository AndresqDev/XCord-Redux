package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class NullnessCasts {
   @ParametricNullness
   static <T> T uncheckedCastNullableTToT(T var0) {
      return (T)var0;
   }

   private NullnessCasts() {
   }
}
