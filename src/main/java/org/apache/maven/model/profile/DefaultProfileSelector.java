package org.apache.maven.model.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.profile.activation.ProfileActivator;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultProfileSelector implements ProfileSelector {
   @Inject
   private List<ProfileActivator> activators = new ArrayList<>();

   public DefaultProfileSelector addProfileActivator(ProfileActivator var1) {
      if (var1 != null) {
         this.activators.add(var1);
      }

      return this;
   }

   @Override
   public List<Profile> getActiveProfiles(Collection<Profile> var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      HashSet var4 = new HashSet<>(var2.getActiveProfileIds());
      HashSet var5 = new HashSet<>(var2.getInactiveProfileIds());
      ArrayList var6 = new ArrayList(var1.size());
      ArrayList var7 = new ArrayList();
      boolean var8 = false;

      for (Profile var10 : var1) {
         if (!var5.contains(var10.getId())) {
            if (var4.contains(var10.getId()) || this.isActive(var10, var2, var3)) {
               var6.add(var10);
               if ("pom".equals(var10.getSource())) {
                  var8 = true;
               }
            } else if (this.isActiveByDefault(var10)) {
               if ("pom".equals(var10.getSource())) {
                  var7.add(var10);
               } else {
                  var6.add(var10);
               }
            }
         }
      }

      if (!var8) {
         var6.addAll(var7);
      }

      return var6;
   }

   private boolean isActive(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      boolean var4 = false;

      for (ProfileActivator var6 : this.activators) {
         if (var6.presentInConfig(var1, var2, var3)) {
            var4 = true;
         }
      }

      for (ProfileActivator var10 : this.activators) {
         try {
            if (var10.presentInConfig(var1, var2, var3)) {
               var4 &= var10.isActive(var1, var2, var3);
            }
         } catch (RuntimeException var8) {
            var3.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                  .setMessage("Failed to determine activation for profile " + var1.getId())
                  .setLocation(var1.getLocation(""))
                  .setException(var8)
            );
            return false;
         }
      }

      return var4;
   }

   private boolean isActiveByDefault(Profile var1) {
      Activation var2 = var1.getActivation();
      return var2 != null && var2.isActiveByDefault();
   }
}
