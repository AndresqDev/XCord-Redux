package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FingerprintTrustManagerFactoryBuilder {
   private final String algorithm;
   private final List<String> fingerprints = new ArrayList<>();

   FingerprintTrustManagerFactoryBuilder(String var1) {
      this.algorithm = ObjectUtil.checkNotNull(var1, "algorithm");
   }

   public FingerprintTrustManagerFactoryBuilder fingerprints(CharSequence... var1) {
      return this.fingerprints(Arrays.asList(ObjectUtil.checkNotNull(var1, "fingerprints")));
   }

   public FingerprintTrustManagerFactoryBuilder fingerprints(Iterable<? extends CharSequence> var1) {
      ObjectUtil.checkNotNull(var1, "fingerprints");

      for (CharSequence var3 : var1) {
         ObjectUtil.checkNotNullWithIAE(var3, "fingerprint");
         this.fingerprints.add(var3.toString());
      }

      return this;
   }

   public FingerprintTrustManagerFactory build() {
      if (this.fingerprints.isEmpty()) {
         throw new IllegalStateException("No fingerprints provided");
      } else {
         return new FingerprintTrustManagerFactory(this.algorithm, FingerprintTrustManagerFactory.toFingerprintArray(this.fingerprints));
      }
   }
}
