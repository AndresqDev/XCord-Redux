package io.netty.handler.ssl.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class SimpleTrustManagerFactory extends TrustManagerFactory {
   private static final Provider PROVIDER = new Provider("", 0.0, "") {
      private static final long serialVersionUID = -2680540247105807895L;
   };
   private static final FastThreadLocal<SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi>() {
      protected SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi initialValue() {
         return new SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi();
      }
   };

   protected SimpleTrustManagerFactory() {
      this("");
   }

   protected SimpleTrustManagerFactory(String var1) {
      super(CURRENT_SPI.get(), PROVIDER, var1);
      CURRENT_SPI.get().init(this);
      CURRENT_SPI.remove();
      ObjectUtil.checkNotNull(var1, "name");
   }

   protected abstract void engineInit(KeyStore var1) throws Exception;

   protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

   protected abstract TrustManager[] engineGetTrustManagers();

   static final class SimpleTrustManagerFactorySpi extends TrustManagerFactorySpi {
      private SimpleTrustManagerFactory parent;
      private volatile TrustManager[] trustManagers;

      void init(SimpleTrustManagerFactory var1) {
         this.parent = var1;
      }

      @Override
      protected void engineInit(KeyStore var1) throws KeyStoreException {
         try {
            this.parent.engineInit(var1);
         } catch (KeyStoreException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new KeyStoreException(var4);
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
      protected TrustManager[] engineGetTrustManagers() {
         TrustManager[] var1 = this.trustManagers;
         if (var1 == null) {
            var1 = this.parent.engineGetTrustManagers();
            if (PlatformDependent.javaVersion() >= 7) {
               wrapIfNeeded(var1);
            }

            this.trustManagers = var1;
         }

         return (TrustManager[])var1.clone();
      }

      @SuppressJava6Requirement(
         reason = "Usage guarded by java version check"
      )
      private static void wrapIfNeeded(TrustManager[] var0) {
         for (int var1 = 0; var1 < var0.length; var1++) {
            TrustManager var2 = var0[var1];
            if (var2 instanceof X509TrustManager && !(var2 instanceof X509ExtendedTrustManager)) {
               var0[var1] = new X509TrustManagerWrapper((X509TrustManager)var2);
            }
         }
      }
   }
}
