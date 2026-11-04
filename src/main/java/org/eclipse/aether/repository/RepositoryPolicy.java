package org.eclipse.aether.repository;

public final class RepositoryPolicy {
   public static final String UPDATE_POLICY_NEVER = "never";
   public static final String UPDATE_POLICY_ALWAYS = "always";
   public static final String UPDATE_POLICY_DAILY = "daily";
   public static final String UPDATE_POLICY_INTERVAL = "interval";
   public static final String CHECKSUM_POLICY_FAIL = "fail";
   public static final String CHECKSUM_POLICY_WARN = "warn";
   public static final String CHECKSUM_POLICY_IGNORE = "ignore";
   private final boolean enabled;
   private final String updatePolicy;
   private final String checksumPolicy;

   public RepositoryPolicy() {
      this(true, "daily", "warn");
   }

   public RepositoryPolicy(boolean var1, String var2, String var3) {
      this.enabled = var1;
      this.updatePolicy = var2 != null ? var2 : "";
      this.checksumPolicy = var3 != null ? var3 : "";
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getUpdatePolicy() {
      return this.updatePolicy;
   }

   public String getChecksumPolicy() {
      return this.checksumPolicy;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(256);
      var1.append("enabled=").append(this.isEnabled());
      var1.append(", checksums=").append(this.getChecksumPolicy());
      var1.append(", updates=").append(this.getUpdatePolicy());
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         RepositoryPolicy var2 = (RepositoryPolicy)var1;
         return this.enabled == var2.enabled && this.updatePolicy.equals(var2.updatePolicy) && this.checksumPolicy.equals(var2.checksumPolicy);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + (this.enabled ? 1 : 0);
      var1 = var1 * 31 + this.updatePolicy.hashCode();
      return var1 * 31 + this.checksumPolicy.hashCode();
   }
}
