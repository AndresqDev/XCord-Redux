package org.apache.maven.artifact.repository;

import java.util.Calendar;
import java.util.Date;

public class ArtifactRepositoryPolicy {
   public static final String UPDATE_POLICY_NEVER = "never";
   public static final String UPDATE_POLICY_ALWAYS = "always";
   public static final String UPDATE_POLICY_DAILY = "daily";
   public static final String UPDATE_POLICY_INTERVAL = "interval";
   public static final String CHECKSUM_POLICY_FAIL = "fail";
   public static final String CHECKSUM_POLICY_WARN = "warn";
   public static final String CHECKSUM_POLICY_IGNORE = "ignore";
   private boolean enabled;
   private String updatePolicy;
   private String checksumPolicy;

   public ArtifactRepositoryPolicy() {
      this(true, null, null);
   }

   public ArtifactRepositoryPolicy(ArtifactRepositoryPolicy var1) {
      this(var1.isEnabled(), var1.getUpdatePolicy(), var1.getChecksumPolicy());
   }

   public ArtifactRepositoryPolicy(boolean var1, String var2, String var3) {
      this.enabled = var1;
      if (var2 == null) {
         var2 = "daily";
      }

      this.updatePolicy = var2;
      if (var3 == null) {
         var3 = "warn";
      }

      this.checksumPolicy = var3;
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   public void setUpdatePolicy(String var1) {
      if (var1 != null) {
         this.updatePolicy = var1;
      }
   }

   public void setChecksumPolicy(String var1) {
      if (var1 != null) {
         this.checksumPolicy = var1;
      }
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

   public boolean checkOutOfDate(Date var1) {
      boolean var2 = false;
      if ("always".equals(this.updatePolicy)) {
         var2 = true;
      } else if ("daily".equals(this.updatePolicy)) {
         Calendar var3 = Calendar.getInstance();
         var3.set(11, 0);
         var3.set(12, 0);
         var3.set(13, 0);
         var3.set(14, 0);
         if (var3.getTime().after(var1)) {
            var2 = true;
         }
      } else if (this.updatePolicy.startsWith("interval")) {
         String var6 = this.updatePolicy.substring("interval".length() + 1);
         int var4 = Integer.parseInt(var6);
         Calendar var5 = Calendar.getInstance();
         var5.add(12, -var4);
         if (var5.getTime().after(var1)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append("{enabled=");
      var1.append(this.enabled);
      var1.append(", checksums=");
      var1.append(this.checksumPolicy);
      var1.append(", updates=");
      var1.append(this.updatePolicy);
      var1.append('}');
      return var1.toString();
   }

   public void merge(ArtifactRepositoryPolicy var1) {
      if (var1 != null && var1.isEnabled()) {
         this.setEnabled(true);
         if (this.ordinalOfCksumPolicy(var1.getChecksumPolicy()) < this.ordinalOfCksumPolicy(this.getChecksumPolicy())) {
            this.setChecksumPolicy(var1.getChecksumPolicy());
         }

         if (this.ordinalOfUpdatePolicy(var1.getUpdatePolicy()) < this.ordinalOfUpdatePolicy(this.getUpdatePolicy())) {
            this.setUpdatePolicy(var1.getUpdatePolicy());
         }
      }
   }

   private int ordinalOfCksumPolicy(String var1) {
      if ("fail".equals(var1)) {
         return 2;
      } else {
         return "ignore".equals(var1) ? 0 : 1;
      }
   }

   private int ordinalOfUpdatePolicy(String var1) {
      if ("daily".equals(var1)) {
         return 1440;
      } else if ("always".equals(var1)) {
         return 0;
      } else if (var1 != null && var1.startsWith("interval")) {
         String var2 = var1.substring("interval".length() + 1);
         return Integer.parseInt(var2);
      } else {
         return Integer.MAX_VALUE;
      }
   }
}
