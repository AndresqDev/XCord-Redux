package org.apache.logging.log4j.core.net.ssl;

import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.status.StatusLogger;

public class StoreConfiguration<T> {
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   private String location;
   private PasswordProvider passwordProvider;

   public StoreConfiguration(String var1, PasswordProvider var2) {
      this.location = var1;
      this.passwordProvider = Objects.requireNonNull(var2, "passwordProvider");
   }

   @Deprecated
   public StoreConfiguration(String var1, char[] var2) {
      this(var1, new MemoryPasswordProvider(var2));
   }

   @Deprecated
   public StoreConfiguration(String var1, String var2) {
      this(var1, new MemoryPasswordProvider(var2 == null ? null : var2.toCharArray()));
   }

   public void clearSecrets() {
      this.location = null;
      this.passwordProvider = null;
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String var1) {
      this.location = var1;
   }

   @Deprecated
   public String getPassword() {
      return String.valueOf(this.passwordProvider.getPassword());
   }

   public char[] getPasswordAsCharArray() {
      return this.passwordProvider.getPassword();
   }

   public void setPassword(char[] var1) {
      this.passwordProvider = new MemoryPasswordProvider(var1);
   }

   @Deprecated
   public void setPassword(String var1) {
      this.passwordProvider = new MemoryPasswordProvider(var1 == null ? null : var1.toCharArray());
   }

   protected T load() throws StoreConfigurationException {
      return null;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = 1;
      var2 = 31 * var2 + (this.location == null ? 0 : this.location.hashCode());
      return 31 * var2 + Arrays.hashCode(this.passwordProvider.getPassword());
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         StoreConfiguration var2 = (StoreConfiguration)var1;
         return !Objects.equals(this.location, var2.location) ? false : Arrays.equals(this.passwordProvider.getPassword(), var2.passwordProvider.getPassword());
      }
   }
}
