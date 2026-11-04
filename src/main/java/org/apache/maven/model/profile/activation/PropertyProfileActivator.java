package org.apache.maven.model.profile.activation;

import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.util.StringUtils;
import xcord.Named;
import xcord.Singleton;

@Named("property")
@Singleton
public class PropertyProfileActivator implements ProfileActivator {
   @Override
   public boolean isActive(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         ActivationProperty var5 = var4.getProperty();
         if (var5 == null) {
            return false;
         } else {
            String var6 = var5.getName();
            boolean var7 = false;
            if (var6 != null && var6.startsWith("!")) {
               var7 = true;
               var6 = var6.substring(1);
            }

            if (var6 != null && var6.length() > 0) {
               String var8 = var2.getUserProperties().get(var6);
               if (var8 == null) {
                  var8 = var2.getSystemProperties().get(var6);
               }

               String var9 = var5.getValue();
               if (StringUtils.isNotEmpty(var9)) {
                  boolean var12 = false;
                  if (var9.startsWith("!")) {
                     var12 = true;
                     var9 = var9.substring(1);
                  }

                  boolean var11 = var9.equals(var8);
                  return var12 ? !var11 : var11;
               } else {
                  boolean var10 = StringUtils.isNotEmpty(var8);
                  return var7 ? !var10 : var10;
               }
            } else {
               var3.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                     .setMessage("The property name is required to activate the profile " + var1.getId())
                     .setLocation(var5.getLocation(""))
               );
               return false;
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
         ActivationProperty var5 = var4.getProperty();
         return var5 != null;
      }
   }
}
