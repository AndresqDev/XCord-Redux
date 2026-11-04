package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter implements CipherSuiteFilter {
   public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

   private SupportedCipherSuiteFilter() {
   }

   @Override
   public String[] filterCipherSuites(Iterable<String> var1, List<String> var2, Set<String> var3) {
      ObjectUtil.checkNotNull(var2, "defaultCiphers");
      ObjectUtil.checkNotNull(var3, "supportedCiphers");
      ArrayList var4;
      if (var1 == null) {
         var4 = new ArrayList(var2.size());
         var1 = var2;
      } else {
         var4 = new ArrayList(var3.size());
      }

      for (String var6 : var1) {
         if (var6 == null) {
            break;
         }

         if (var3.contains(var6)) {
            var4.add(var6);
         }
      }

      return var4.toArray(new String[0]);
   }
}
