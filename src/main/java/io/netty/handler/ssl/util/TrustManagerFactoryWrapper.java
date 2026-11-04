package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;

public final class TrustManagerFactoryWrapper extends SimpleTrustManagerFactory {
   private final TrustManager tm;

   public TrustManagerFactoryWrapper(TrustManager var1) {
      this.tm = ObjectUtil.checkNotNull(var1, "tm");
   }

   @Override
   protected void engineInit(KeyStore var1) throws Exception {
   }

   @Override
   protected void engineInit(ManagerFactoryParameters var1) throws Exception {
   }

   @Override
   protected TrustManager[] engineGetTrustManagers() {
      return new TrustManager[]{this.tm};
   }
}
