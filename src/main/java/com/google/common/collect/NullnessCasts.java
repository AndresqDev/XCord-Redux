package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class NullnessCasts {
   @ParametricNullness
   static <T> T uncheckedCastNullableTToT(T var0) {
      return (T)var0;
   }

   @ParametricNullness
   static <T> T unsafeNull() {
      return null;
   }

   private NullnessCasts() {
   }
}
