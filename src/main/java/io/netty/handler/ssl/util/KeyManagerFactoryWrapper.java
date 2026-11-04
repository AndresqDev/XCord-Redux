package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.ManagerFactoryParameters;

public final class KeyManagerFactoryWrapper extends SimpleKeyManagerFactory {
   private final KeyManager km;

   public KeyManagerFactoryWrapper(KeyManager var1) {
      this.km = ObjectUtil.checkNotNull(var1, "km");
   }

   @Override
   protected void engineInit(KeyStore var1, char[] var2) throws Exception {
   }

   @Override
   protected void engineInit(ManagerFactoryParameters var1) throws Exception {
   }

   @Override
   protected KeyManager[] engineGetKeyManagers() {
      return new KeyManager[]{this.km};
   }
}
