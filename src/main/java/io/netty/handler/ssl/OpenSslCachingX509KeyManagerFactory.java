package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509KeyManager;

public final class OpenSslCachingX509KeyManagerFactory extends KeyManagerFactory {
   private final int maxCachedEntries;

   public OpenSslCachingX509KeyManagerFactory(KeyManagerFactory var1) {
      this(var1, 1024);
   }

   public OpenSslCachingX509KeyManagerFactory(final KeyManagerFactory var1, int var2) {
      super(new KeyManagerFactorySpi() {
         @Override
         protected void engineInit(KeyStore var1x, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            var1.init(var1x, var2);
         }

         @Override
         protected void engineInit(ManagerFactoryParameters var1x) throws InvalidAlgorithmParameterException {
            var1.init(var1x);
         }

         @Override
         protected KeyManager[] engineGetKeyManagers() {
            return var1.getKeyManagers();
         }
      }, var1.getProvider(), var1.getAlgorithm());
      this.maxCachedEntries = ObjectUtil.checkPositive(var2, "maxCachedEntries");
   }

   OpenSslKeyMaterialProvider newProvider(String var1) {
      X509KeyManager var2 = ReferenceCountedOpenSslContext.chooseX509KeyManager(this.getKeyManagers());
      return (OpenSslKeyMaterialProvider)("sun.security.ssl.X509KeyManagerImpl".equals(var2.getClass().getName())
         ? new OpenSslKeyMaterialProvider(var2, var1)
         : new OpenSslCachingKeyMaterialProvider(ReferenceCountedOpenSslContext.chooseX509KeyManager(this.getKeyManagers()), var1, this.maxCachedEntries));
   }
}
