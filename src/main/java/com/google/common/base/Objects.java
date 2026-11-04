package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Arrays;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Objects extends ExtraObjectsMethodsForWeb {
   private Objects() {
   }

   public static boolean equal(Object var0, Object var1) {
      return var0 == var1 || var0 != null && var0.equals(var1);
   }

   public static int hashCode(Object... var0) {
      return Arrays.hashCode(var0);
   }
}
