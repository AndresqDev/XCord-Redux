package org.eclipse.aether.util;

@Deprecated
public final class StringUtils {
   private StringUtils() {
   }

      public static boolean isEmpty(String var0) {
      return var0 == null || var0.length() <= 0;
   }
}
