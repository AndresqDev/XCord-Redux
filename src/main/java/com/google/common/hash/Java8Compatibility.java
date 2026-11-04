package com.google.common.hash;

import com.google.common.annotations.GwtIncompatible;
import java.nio.Buffer;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class Java8Compatibility {
   static void clear(Buffer var0) {
      var0.clear();
   }

   static void flip(Buffer var0) {
      var0.flip();
   }

   static void limit(Buffer var0, int var1) {
      var0.limit(var1);
   }

   static void position(Buffer var0, int var1) {
      var0.position(var1);
   }

   private Java8Compatibility() {
   }
}
