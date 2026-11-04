package io.netty.handler.ssl.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

public abstract class SimpleKeyManagerFactory extends KeyManagerFactory {
   private static final Provider PROVIDER = new Provider("", 0.0, "") {
      private static final long serialVersionUID = -2680540247105807895L;
   };
   private static final FastThreadLocal<SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi>() {
      protected SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi initialValue() {
         return new SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi();
      }
   };

   protected SimpleKeyManagerFactory() {
      this("");
   }

   protected SimpleKeyManagerFactory(String var1) {
      super(CURRENT_SPI.get(), PROVIDER, ObjectUtil.checkNotNull(var1, "name"));
      CURRENT_SPI.get().init(this);
      CURRENT_SPI.remove();
   }

   protected abstract void engineInit(KeyStore var1, char[] var2) throws Exception;

   protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

   protected abstract KeyManager[] engineGetKeyManagers();

   private static final class SimpleKeyManagerFactorySpi extends KeyManagerFactorySpi {
      private SimpleKeyManagerFactory parent;
      private volatile KeyManager[] keyManagers;

      private SimpleKeyManagerFactorySpi() {
      }

      void init(SimpleKeyManagerFactory var1) {
         this.parent = var1;
      }

      @Override
      protected void engineInit(KeyStore var1, char[] var2) throws KeyStoreException {
         try {
            this.parent.engineInit(var1, var2);
         } catch (KeyStoreException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new KeyStoreException(var5);
         }
      }

      @Override
      protected void engineInit(ManagerFactoryParameters var1) throws InvalidAlgorithmParameterException {
         try {
            this.parent.engineInit(var1);
         } catch (InvalidAlgorithmParameterException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new InvalidAlgorithmParameterException(var4);
         }
      }

      @Override
      protected KeyManager[] engineGetKeyManagers() {
         KeyManager[] var1 = this.keyManagers;
         if (var1 == null) {
            var1 = this.parent.engineGetKeyManagers();
            if (PlatformDependent.javaVersion() >= 7) {
               wrapIfNeeded(var1);
            }

            this.keyManagers = var1;
         }

         return (KeyManager[])var1.clone();
      }

      @SuppressJava6Requirement(
         reason = "Usage guarded by java version check"
      )
      private static void wrapIfNeeded(KeyManager[] var0) {
         for (int var1 = 0; var1 < var0.length; var1++) {
            KeyManager var2 = var0[var1];
            if (var2 instanceof X509KeyManager && !(var2 instanceof X509ExtendedKeyManager)) {
               var0[var1] = new X509KeyManagerWrapper((X509KeyManager)var2);
            }
         }
      }
   }
}
