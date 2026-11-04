package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509KeyManager;

public final class OpenSslX509KeyManagerFactory extends KeyManagerFactory {
   private final OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi spi;

   public OpenSslX509KeyManagerFactory() {
      this(newOpenSslKeyManagerFactorySpi(null));
   }

   public OpenSslX509KeyManagerFactory(Provider var1) {
      this(newOpenSslKeyManagerFactorySpi(var1));
   }

   public OpenSslX509KeyManagerFactory(String var1, Provider var2) throws NoSuchAlgorithmException {
      this(newOpenSslKeyManagerFactorySpi(var1, var2));
   }

   private OpenSslX509KeyManagerFactory(OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi var1) {
      super(var1, var1.kmf.getProvider(), var1.kmf.getAlgorithm());
      this.spi = var1;
   }

   private static OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(Provider var0) {
      try {
         return newOpenSslKeyManagerFactorySpi(null, var0);
      } catch (NoSuchAlgorithmException var2) {
         throw new IllegalStateException(var2);
      }
   }

   private static OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var0 == null) {
         var0 = KeyManagerFactory.getDefaultAlgorithm();
      }

      return new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi(
         var1 == null ? KeyManagerFactory.getInstance(var0) : KeyManagerFactory.getInstance(var0, var1)
      );
   }

   OpenSslKeyMaterialProvider newProvider() {
      return this.spi.newProvider();
   }

   public static OpenSslX509KeyManagerFactory newEngineBased(File var0, String var1) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newEngineBased(SslContext.toX509Certificates(var0), var1);
   }

   public static OpenSslX509KeyManagerFactory newEngineBased(X509Certificate[] var0, String var1) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectUtil.checkNotNull(var0, "certificateChain");
      OpenSslX509KeyManagerFactory.OpenSslKeyStore var2 = new OpenSslX509KeyManagerFactory.OpenSslKeyStore((X509Certificate[])var0.clone(), false);
      var2.load(null, null);
      OpenSslX509KeyManagerFactory var3 = new OpenSslX509KeyManagerFactory();
      var3.init(var2, var1 == null ? null : var1.toCharArray());
      return var3;
   }

   public static OpenSslX509KeyManagerFactory newKeyless(File var0) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newKeyless(SslContext.toX509Certificates(var0));
   }

   public static OpenSslX509KeyManagerFactory newKeyless(InputStream var0) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newKeyless(SslContext.toX509Certificates(var0));
   }

   public static OpenSslX509KeyManagerFactory newKeyless(X509Certificate... var0) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectUtil.checkNotNull(var0, "certificateChain");
      OpenSslX509KeyManagerFactory.OpenSslKeyStore var1 = new OpenSslX509KeyManagerFactory.OpenSslKeyStore((X509Certificate[])var0.clone(), true);
      var1.load(null, null);
      OpenSslX509KeyManagerFactory var2 = new OpenSslX509KeyManagerFactory();
      var2.init(var1, null);
      return var2;
   }

   private static final class OpenSslKeyManagerFactorySpi extends KeyManagerFactorySpi {
      final KeyManagerFactory kmf;
      private volatile OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory providerFactory;

      OpenSslKeyManagerFactorySpi(KeyManagerFactory var1) {
         this.kmf = ObjectUtil.checkNotNull(var1, "kmf");
      }

      @Override
      protected synchronized void engineInit(KeyStore var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
         if (this.providerFactory != null) {
            throw new KeyStoreException("Already initialized");
         } else if (!var1.aliases().hasMoreElements()) {
            throw new KeyStoreException("No aliases found");
         } else {
            this.kmf.init(var1, var2);
            this.providerFactory = new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory(
               ReferenceCountedOpenSslContext.chooseX509KeyManager(this.kmf.getKeyManagers()), password(var2), Collections.list(var1.aliases())
            );
         }
      }

      private static String password(char[] var0) {
         return var0 != null && var0.length != 0 ? new String(var0) : null;
      }

      @Override
      protected void engineInit(ManagerFactoryParameters var1) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("Not supported");
      }

      @Override
      protected KeyManager[] engineGetKeyManagers() {
         OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory var1 = this.providerFactory;
         if (var1 == null) {
            throw new IllegalStateException("engineInit(...) not called yet");
         } else {
            return new KeyManager[]{var1.keyManager};
         }
      }

      OpenSslKeyMaterialProvider newProvider() {
         OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory var1 = this.providerFactory;
         if (var1 == null) {
            throw new IllegalStateException("engineInit(...) not called yet");
         } else {
            return var1.newProvider();
         }
      }

      private static final class ProviderFactory {
         private final X509KeyManager keyManager;
         private final String password;
         private final Iterable<String> aliases;

         ProviderFactory(X509KeyManager var1, String var2, Iterable<String> var3) {
            this.keyManager = var1;
            this.password = var2;
            this.aliases = var3;
         }

         OpenSslKeyMaterialProvider newProvider() {
            return new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory.OpenSslPopulatedKeyMaterialProvider(
               this.keyManager, this.password, this.aliases
            );
         }

         private static final class OpenSslPopulatedKeyMaterialProvider extends OpenSslKeyMaterialProvider {
            private final Map<String, Object> materialMap = new HashMap<>();

            OpenSslPopulatedKeyMaterialProvider(X509KeyManager var1, String var2, Iterable<String> var3) {
               super(var1, var2);
               boolean var4 = false;

               try {
                  for (String var6 : var3) {
                     if (var6 != null && !this.materialMap.containsKey(var6)) {
                        try {
                           this.materialMap.put(var6, super.chooseKeyMaterial(UnpooledByteBufAllocator.DEFAULT, var6));
                        } catch (Exception var11) {
                           this.materialMap.put(var6, var11);
                        }
                     }
                  }

                  var4 = true;
               } finally {
                  if (!var4) {
                     this.destroy();
                  }
               }

               ObjectUtil.checkNonEmpty(this.materialMap, "materialMap");
            }

            @Override
            OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator var1, String var2) throws Exception {
               Object var3 = this.materialMap.get(var2);
               if (var3 == null) {
                  return null;
               } else if (var3 instanceof OpenSslKeyMaterial) {
                  return ((OpenSslKeyMaterial)var3).retain();
               } else {
                  throw (Exception)var3;
               }
            }

            @Override
            void destroy() {
               for (Object var2 : this.materialMap.values()) {
                  ReferenceCountUtil.release(var2);
               }

               this.materialMap.clear();
            }
         }
      }
   }

   private static final class OpenSslKeyStore extends KeyStore {
      private OpenSslKeyStore(final X509Certificate[] var1, final boolean var2) {
         super(new KeyStoreSpi() {
            private final Date creationDate = new Date();

            @Override
            public Key engineGetKey(String var1x, char[] var2x) throws UnrecoverableKeyException {
               if (this.engineContainsAlias(var1x)) {
                  long var3;
                  if (var2) {
                     var3 = 0L;
                  } else {
                     try {
                        var3 = SSL.loadPrivateKeyFromEngine(var1x, var2x == null ? null : new String(var2x));
                     } catch (Exception var7) {
                        UnrecoverableKeyException var6 = new UnrecoverableKeyException("Unable to load key from engine");
                        var6.initCause(var7);
                        throw var6;
                     }
                  }

                  return new OpenSslPrivateKey(var3);
               } else {
                  return null;
               }
            }

            @Override
            public Certificate[] engineGetCertificateChain(String var1x) {
               return this.engineContainsAlias(var1x) ? (X509Certificate[])var1.clone() : null;
            }

            @Override
            public Certificate engineGetCertificate(String var1x) {
               return this.engineContainsAlias(var1x) ? var1[0] : null;
            }

            @Override
            public Date engineGetCreationDate(String var1x) {
               return this.engineContainsAlias(var1x) ? this.creationDate : null;
            }

            @Override
            public void engineSetKeyEntry(String var1x, Key var2x, char[] var3, Certificate[] var4) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineSetKeyEntry(String var1x, byte[] var2x, Certificate[] var3) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineSetCertificateEntry(String var1x, Certificate var2x) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineDeleteEntry(String var1x) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public Enumeration<String> engineAliases() {
               return Collections.enumeration(Collections.singleton("key"));
            }

            @Override
            public boolean engineContainsAlias(String var1x) {
               return "key".equals(var1x);
            }

            @Override
            public int engineSize() {
               return 1;
            }

            @Override
            public boolean engineIsKeyEntry(String var1x) {
               return this.engineContainsAlias(var1x);
            }

            @Override
            public boolean engineIsCertificateEntry(String var1x) {
               return this.engineContainsAlias(var1x);
            }

            @Override
            public String engineGetCertificateAlias(Certificate var1x) {
               if (var1x instanceof X509Certificate) {
                  for (X509Certificate var5 : var1) {
                     if (var5.equals(var1x)) {
                        return "key";
                     }
                  }
               }

               return null;
            }

            @Override
            public void engineStore(OutputStream var1x, char[] var2x) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void engineLoad(InputStream var1x, char[] var2x) {
               if (var1x != null && var2x != null) {
                  throw new UnsupportedOperationException();
               }
            }
         }, null, "native");
         OpenSsl.ensureAvailability();
      }
   }
}
