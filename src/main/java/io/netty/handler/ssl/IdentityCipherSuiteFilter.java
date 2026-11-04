package io.netty.handler.ssl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class IdentityCipherSuiteFilter implements CipherSuiteFilter {
   public static final IdentityCipherSuiteFilter INSTANCE = new IdentityCipherSuiteFilter(true);
   public static final IdentityCipherSuiteFilter INSTANCE_DEFAULTING_TO_SUPPORTED_CIPHERS = new IdentityCipherSuiteFilter(false);
   private final boolean defaultToDefaultCiphers;

   private IdentityCipherSuiteFilter(boolean var1) {
      this.defaultToDefaultCiphers = var1;
   }

   @Override
   public String[] filterCipherSuites(Iterable<String> var1, List<String> var2, Set<String> var3) {
      if (var1 == null) {
         return this.defaultToDefaultCiphers ? var2.toArray(new String[0]) : var3.toArray(new String[0]);
      } else {
         ArrayList var4 = new ArrayList(var3.size());

         for (String var6 : var1) {
            if (var6 == null) {
               break;
            }

            var4.add(var6);
         }

         return var4.toArray(new String[0]);
      }
   }
}
