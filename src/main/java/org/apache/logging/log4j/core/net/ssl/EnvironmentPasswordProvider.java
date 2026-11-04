package org.apache.logging.log4j.core.net.ssl;

import java.util.Objects;

class EnvironmentPasswordProvider implements PasswordProvider {
   private final String passwordEnvironmentVariable;

   public EnvironmentPasswordProvider(String var1) {
      this.passwordEnvironmentVariable = Objects.requireNonNull(var1, "passwordEnvironmentVariable");
   }

   @Override
   public char[] getPassword() {
      String var1 = System.getenv(this.passwordEnvironmentVariable);
      return var1 == null ? null : var1.toCharArray();
   }
}
