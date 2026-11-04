package org.yaml.snakeyaml.util;

public class PlatformFeatureDetector {
   private Boolean isRunningOnAndroid = null;

   public boolean isRunningOnAndroid() {
      if (this.isRunningOnAndroid == null) {
         String var1 = System.getProperty("java.runtime.name");
         this.isRunningOnAndroid = var1 != null && var1.startsWith("Android Runtime");
      }

      return this.isRunningOnAndroid;
   }
}
