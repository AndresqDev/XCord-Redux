package org.apache.maven.model.profile.activation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.profile.ProfileActivationContext;
import xcord.Named;
import xcord.Singleton;

@Named("jdk-version")
@Singleton
public class JdkVersionProfileActivator implements ProfileActivator {
   @Override
   public boolean isActive(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         String var5 = var4.getJdk();
         if (var5 == null) {
            return false;
         } else {
            String var6 = var2.getSystemProperties().get("java.version");
            if (var6 == null || var6.length() <= 0) {
               var3.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                     .setMessage("Failed to determine Java version for profile " + var1.getId())
                     .setLocation(var4.getLocation("jdk"))
               );
               return false;
            } else if (var5.startsWith("!")) {
               return !var6.startsWith(var5.substring(1));
            } else {
               return isRange(var5) ? isInRange(var6, getRange(var5)) : var6.startsWith(var5);
            }
         }
      }
   }

   @Override
   public boolean presentInConfig(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         String var5 = var4.getJdk();
         return var5 != null;
      }
   }

   private static boolean isInRange(String var0, List<JdkVersionProfileActivator.RangeValue> var1) {
      int var2 = getRelationOrder(var0, (JdkVersionProfileActivator.RangeValue)var1.get(0), true);
      if (var2 == 0) {
         return true;
      } else {
         return var2 < 0 ? false : getRelationOrder(var0, (JdkVersionProfileActivator.RangeValue)var1.get(1), false) <= 0;
      }
   }

   private static int getRelationOrder(String var0, JdkVersionProfileActivator.RangeValue var1, boolean var2) {
      if (var1.value.length() <= 0) {
         return var2 ? 1 : -1;
      } else {
         var0 = var0.replaceAll("[^0-9\\.\\-\\_]", "");
         ArrayList var3 = new ArrayList<>(Arrays.asList(var0.split("[\\.\\-\\_]")));
         ArrayList var4 = new ArrayList<>(Arrays.asList(var1.value.split("\\.")));
         addZeroTokens(var3, 3);
         addZeroTokens(var4, 3);

         for (int var5 = 0; var5 < 3; var5++) {
            int var6 = Integer.parseInt((String)var3.get(var5));
            int var7 = Integer.parseInt((String)var4.get(var5));
            if (var6 < var7) {
               return -1;
            }

            if (var6 > var7) {
               return 1;
            }
         }

         if (!var1.closed) {
            return var2 ? -1 : 1;
         } else {
            return 0;
         }
      }
   }

   private static void addZeroTokens(List<String> var0, int var1) {
      while (var0.size() < var1) {
         var0.add("0");
      }
   }

   private static boolean isRange(String var0) {
      return var0.startsWith("[") || var0.startsWith("(");
   }

   private static List<JdkVersionProfileActivator.RangeValue> getRange(String var0) {
      ArrayList var1 = new ArrayList();

      for (String var5 : var0.split(",")) {
         if (var5.startsWith("[")) {
            var1.add(new JdkVersionProfileActivator.RangeValue(var5.replace("[", ""), true));
         } else if (var5.startsWith("(")) {
            var1.add(new JdkVersionProfileActivator.RangeValue(var5.replace("(", ""), false));
         } else if (var5.endsWith("]")) {
            var1.add(new JdkVersionProfileActivator.RangeValue(var5.replace("]", ""), true));
         } else if (var5.endsWith(")")) {
            var1.add(new JdkVersionProfileActivator.RangeValue(var5.replace(")", ""), false));
         } else if (var5.length() <= 0) {
            var1.add(new JdkVersionProfileActivator.RangeValue("", false));
         }
      }

      if (var1.size() < 2) {
         var1.add(new JdkVersionProfileActivator.RangeValue("99999999", false));
      }

      return var1;
   }

   private static class RangeValue {
      private String value;
      private boolean closed;

      RangeValue(String var1, boolean var2) {
         this.value = var1.trim();
         this.closed = var2;
      }

      @Override
      public String toString() {
         return this.value;
      }
   }
}
