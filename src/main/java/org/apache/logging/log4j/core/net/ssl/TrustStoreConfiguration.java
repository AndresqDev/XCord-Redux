package org.apache.logging.log4j.core.net.ssl;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "TrustStore",
   category = "Core",
   printObject = true
)
public class TrustStoreConfiguration extends AbstractKeyStoreConfiguration {
   private final String trustManagerFactoryAlgorithm;

   public TrustStoreConfiguration(String var1, PasswordProvider var2, String var3, String var4) throws StoreConfigurationException {
      super(var1, var2, var3);
      this.trustManagerFactoryAlgorithm = var4 == null ? TrustManagerFactory.getDefaultAlgorithm() : var4;
   }

   @Deprecated
   public TrustStoreConfiguration(String var1, char[] var2, String var3, String var4) throws StoreConfigurationException {
      this(var1, new MemoryPasswordProvider(var2), var3, var4);
      if (var2 != null) {
         Arrays.fill(var2, '\u0000');
      }
   }

   @Deprecated
   public TrustStoreConfiguration(String var1, String var2, String var3, String var4) throws StoreConfigurationException {
      this(var1, new MemoryPasswordProvider(var2 == null ? null : var2.toCharArray()), var3, var4);
   }

   @PluginFactory
   public static TrustStoreConfiguration createKeyStoreConfiguration(
      @PluginAttribute("location") String var0,
      @PluginAttribute(value = "password",sensitive = true) char[] var1,
      @PluginAttribute("passwordEnvironmentVariable") String var2,
      @PluginAttribute("passwordFile") String var3,
      @PluginAttribute("type") String var4,
      @PluginAttribute("trustManagerFactoryAlgorithm") String var5
   ) throws StoreConfigurationException {
      if (var1 != null && var2 != null && var3 != null) {
         throw new IllegalStateException("You MUST set only one of 'password', 'passwordEnvironmentVariable' or 'passwordFile'.");
      } else {
         try {
            Object var6 = var3 != null
               ? new FilePasswordProvider(var3)
               : (var2 != null ? new EnvironmentPasswordProvider(var2) : new MemoryPasswordProvider(var1));
            if (var1 != null) {
               Arrays.fill(var1, '\u0000');
            }

            return new TrustStoreConfiguration(var0, (PasswordProvider)var6, var4, var5);
         } catch (Exception var7) {
            throw new StoreConfigurationException("Could not configure TrustStore", var7);
         }
      }
   }

   @Deprecated
   public static TrustStoreConfiguration createKeyStoreConfiguration(String var0, char[] var1, String var2, String var3) throws StoreConfigurationException {
      return createKeyStoreConfiguration(var0, var1, null, null, var2, var3);
   }

   @Deprecated
   public static TrustStoreConfiguration createKeyStoreConfiguration(String var0, String var1, String var2, String var3) throws StoreConfigurationException {
      return createKeyStoreConfiguration(var0, var1 == null ? null : var1.toCharArray(), null, null, var2, var3);
   }

   public TrustManagerFactory initTrustManagerFactory() throws NoSuchAlgorithmException, KeyStoreException {
      TrustManagerFactory var1 = TrustManagerFactory.getInstance(this.trustManagerFactoryAlgorithm);
      var1.init(this.getKeyStore());
      return var1;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = super.hashCode();
      return 31 * var2 + (this.trustManagerFactoryAlgorithm == null ? 0 : this.trustManagerFactoryAlgorithm.hashCode());
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
         TrustStoreConfiguration var2 = (TrustStoreConfiguration)var1;
         return Objects.equals(this.trustManagerFactoryAlgorithm, var2.trustManagerFactoryAlgorithm);
      }
   }

   public String getTrustManagerFactoryAlgorithm() {
      return this.trustManagerFactoryAlgorithm;
   }
}
