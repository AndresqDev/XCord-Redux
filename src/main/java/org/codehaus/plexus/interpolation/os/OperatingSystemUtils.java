package org.codehaus.plexus.interpolation.os;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public final class OperatingSystemUtils {
   private static OperatingSystemUtils.EnvVarSource envVarSource = new OperatingSystemUtils.DefaultEnvVarSource();

   private OperatingSystemUtils() {
   }

   public static Properties getSystemEnvVars() throws IOException {
      return getSystemEnvVars(true);
   }

   public static Properties getSystemEnvVars(boolean var0) throws IOException {
      Properties var1 = new Properties();
      Map var2 = envVarSource.getEnvMap();

      for (String var4 : var2.keySet()) {
         String var5 = (String)var2.get(var4);
         if (!var0) {
            var4 = var4.toUpperCase(Locale.ENGLISH);
         }

         var1.put(var4, var5);
      }

      return var1;
   }

   public static void setEnvVarSource(OperatingSystemUtils.EnvVarSource var0) {
      envVarSource = var0;
   }

   public static class DefaultEnvVarSource implements OperatingSystemUtils.EnvVarSource {
      @Override
      public Map<String, String> getEnvMap() {
         return System.getenv();
      }
   }

   public interface EnvVarSource {
      Map<String, String> getEnvMap();
   }
}
