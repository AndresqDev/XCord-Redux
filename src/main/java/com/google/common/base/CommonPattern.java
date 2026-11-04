package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class CommonPattern {
   public abstract CommonMatcher matcher(CharSequence var1);

   public abstract String pattern();

   public abstract int flags();

   @Override
   public abstract String toString();

   public static CommonPattern compile(String var0) {
      return Platform.compilePattern(var0);
   }

   public static boolean isPcreLike() {
      return Platform.patternCompilerIsPcreLike();
   }
}
