package org.apache.logging.log4j.core.net.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.util.NetUtils;

public class AbstractKeyStoreConfiguration extends StoreConfiguration<KeyStore> {
   private final KeyStore keyStore;
   private final String keyStoreType;

   public AbstractKeyStoreConfiguration(String var1, PasswordProvider var2, String var3) throws StoreConfigurationException {
      super(var1, var2);
      this.keyStoreType = var3 == null ? "JKS" : var3;
      this.keyStore = this.load();
   }

   @Deprecated
   public AbstractKeyStoreConfiguration(String var1, char[] var2, String var3) throws StoreConfigurationException {
      this(var1, new MemoryPasswordProvider(var2), var3);
   }

   @Deprecated
   public AbstractKeyStoreConfiguration(String var1, String var2, String var3) throws StoreConfigurationException {
      this(var1, new MemoryPasswordProvider(var2 == null ? null : var2.toCharArray()), var3);
   }

   protected KeyStore load() throws StoreConfigurationException {
      String var1 = this.getLocation();
      LOGGER.debug("Loading keystore from location {}", var1);

      try {
         if (var1 == null) {
            throw new IOException("The location is null");
         } else {
            KeyStore var6;
            try (InputStream var2 = this.openInputStream(var1)) {
               KeyStore var4 = KeyStore.getInstance(this.keyStoreType);
               char[] var5 = this.getPasswordAsCharArray();

               try {
                  var4.load(var2, var5);
               } finally {
                  if (var5 != null) {
                     Arrays.fill(var5, '\u0000');
                  }
               }

               LOGGER.debug("KeyStore successfully loaded from location {}", var1);
               var6 = var4;
            }

            return var6;
         }
      } catch (CertificateException var33) {
         LOGGER.error("No Provider supports a KeyStoreSpi implementation for the specified type {} for location {}", this.keyStoreType, var1, var33);
         throw new StoreConfigurationException(var1, var33);
      } catch (NoSuchAlgorithmException var34) {
         LOGGER.error("The algorithm used to check the integrity of the keystore cannot be found for location {}", var1, var34);
         throw new StoreConfigurationException(var1, var34);
      } catch (KeyStoreException var35) {
         LOGGER.error("KeyStoreException for location {}", var1, var35);
         throw new StoreConfigurationException(var1, var35);
      } catch (FileNotFoundException var36) {
         LOGGER.error("The keystore file {} is not found", var1, var36);
         throw new StoreConfigurationException(var1, var36);
      } catch (IOException var37) {
         LOGGER.error("Something is wrong with the format of the keystore or the given password for location {}", var1, var37);
         throw new StoreConfigurationException(var1, var37);
      }
   }

   private InputStream openInputStream(String var1) {
      return ConfigurationSource.fromUri(NetUtils.toURI(var1)).getInputStream();
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = super.hashCode();
      var2 = 31 * var2 + (this.keyStore == null ? 0 : this.keyStore.hashCode());
      return 31 * var2 + (this.keyStoreType == null ? 0 : this.keyStoreType.hashCode());
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equals(var1)) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         AbstractKeyStoreConfiguration var2 = (AbstractKeyStoreConfiguration)var1;
         return !Objects.equals(this.keyStore, var2.keyStore) ? false : Objects.equals(this.keyStoreType, var2.keyStoreType);
      }
   }

   public String getKeyStoreType() {
      return this.keyStoreType;
   }
}
