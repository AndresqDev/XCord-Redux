package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

final class ApplicationProtocolUtil {
   private static final int DEFAULT_LIST_SIZE = 2;

   private ApplicationProtocolUtil() {
   }

   static List<String> toList(Iterable<String> var0) {
      return toList(2, var0);
   }

   static List<String> toList(int var0, Iterable<String> var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var0);

         for (String var4 : var1) {
            var2.add(ObjectUtil.checkNonEmpty(var4, "p"));
         }

         return ObjectUtil.checkNonEmpty(var2, "result");
      }
   }

   static List<String> toList(String... var0) {
      return toList(2, var0);
   }

   static List<String> toList(int var0, String... var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var0);

         for (String var6 : var1) {
            var2.add(ObjectUtil.checkNonEmpty(var6, "p"));
         }

         return ObjectUtil.checkNonEmpty(var2, "result");
      }
   }
}
