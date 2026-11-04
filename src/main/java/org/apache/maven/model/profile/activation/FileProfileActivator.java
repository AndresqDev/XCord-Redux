package org.apache.maven.model.profile.activation;

import java.io.File;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.path.ProfileActivationFilePathInterpolator;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.util.StringUtils;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named("file")
@Singleton
public class FileProfileActivator implements ProfileActivator {
   @Inject
   private ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator;

   public FileProfileActivator setProfileActivationFilePathInterpolator(ProfileActivationFilePathInterpolator var1) {
      this.profileActivationFilePathInterpolator = var1;
      return this;
   }

   @Override
   public boolean isActive(Profile var1, ProfileActivationContext var2, ModelProblemCollector var3) {
      Activation var4 = var1.getActivation();
      if (var4 == null) {
         return false;
      } else {
         ActivationFile var5 = var4.getFile();
         if (var5 == null) {
            return false;
         } else {
            String var6;
            boolean var7;
            if (StringUtils.isNotEmpty(var5.getExists())) {
               var6 = var5.getExists();
               var7 = false;
            } else {
               if (!StringUtils.isNotEmpty(var5.getMissing())) {
                  return false;
               }

               var6 = var5.getMissing();
               var7 = true;
            }

            try {
               var6 = this.profileActivationFilePathInterpolator.interpolate(var6, var2);
            } catch (InterpolationException var10) {
               var3.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                     .setMessage("Failed to interpolate file location " + var6 + " for profile " + var1.getId() + ": " + var10.getMessage())
                     .setLocation(var5.getLocation(var7 ? "missing" : "exists"))
                     .setException(var10)
               );
               return false;
            }

            if (var6 == null) {
               return false;
            } else {
               File var8 = new File(var6);
               if (!var8.isAbsolute()) {
                  return false;
               } else {
                  boolean var9 = var8.exists();
                  return var7 ? !var9 : var9;
               }
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
         ActivationFile var5 = var4.getFile();
         return var5 != null;
      }
   }
}
