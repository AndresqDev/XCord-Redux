package io.netty.handler.ssl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class GroupsConverter {
   private static final Map<String, String> mappings;

   static String toOpenSsl(String var0) {
      String var1 = mappings.get(var0);
      return var1 == null ? var0 : var1;
   }

   private GroupsConverter() {
   }

   static {
      HashMap var0 = new HashMap();
      var0.put("secp224r1", "P-224");
      var0.put("prime256v1", "P-256");
      var0.put("secp256r1", "P-256");
      var0.put("secp384r1", "P-384");
      var0.put("secp521r1", "P-521");
      var0.put("x25519", "X25519");
      mappings = Collections.unmodifiableMap(var0);
   }
}
