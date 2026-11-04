package org.apache.maven.model.profile.activation;

import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.util.Os;
import xcord.Named;
import xcord.Singleton;

@Named("os")
@Singleton
public class OperatingSystemProfileActivator implements ProfileActivator {
   @Override
   public boolean isActive(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         ActivationOS var5 = var4.getOs();
         if (var5 == null) {
            return false;
         } else {
            boolean var6 = this.ensureAtLeastOneNonNull(var5);
            if (var6 && var5.getFamily() != null) {
               var6 = this.determineFamilyMatch(var5.getFamily());
            }

            if (var6 && var5.getName() != null) {
               var6 = this.determineNameMatch(var5.getName());
            }

            if (var6 && var5.getArch() != null) {
               var6 = this.determineArchMatch(var5.getArch());
            }

            if (var6 && var5.getVersion() != null) {
               var6 = this.determineVersionMatch(var5.getVersion());
            }

            return var6;
         }
      }
   }

   @Override
   public boolean presentInConfig(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         ActivationOS var5 = var4.getOs();
         return var5 != null;
      }
   }

   private boolean ensureAtLeastOneNonNull(ActivationOS var1) {
      return var1.getArch() != null || var1.getFamily() != null || var1.getName() != null || var1.getVersion() != null;
   }

   private boolean determineVersionMatch(String var1) {
      String var2 = var1;
      boolean var3 = false;
      if (var1.startsWith("!")) {
         var3 = true;
         var2 = var1.substring(1);
      }

      boolean var4 = Os.isVersion(var2);
      return var3 ? !var4 : var4;
   }

   private boolean determineArchMatch(String var1) {
      String var2 = var1;
      boolean var3 = false;
      if (var1.startsWith("!")) {
         var3 = true;
         var2 = var1.substring(1);
      }

      boolean var4 = Os.isArch(var2);
      return var3 ? !var4 : var4;
   }

   private boolean determineNameMatch(String var1) {
      String var2 = var1;
      boolean var3 = false;
      if (var1.startsWith("!")) {
         var3 = true;
         var2 = var1.substring(1);
      }

      boolean var4 = Os.isName(var2);
      return var3 ? !var4 : var4;
   }

   private boolean determineFamilyMatch(String var1) {
      String var2 = var1;
      boolean var3 = false;
      if (var1.startsWith("!")) {
         var3 = true;
         var2 = var1.substring(1);
      }

      boolean var4 = Os.isFamily(var2);
      return var3 ? !var4 : var4;
   }
}
