package org.apache.maven.model.path;

import java.io.File;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class ProfileActivationFilePathInterpolator {
   @Inject
   private PathTranslator pathTranslator;

   public ProfileActivationFilePathInterpolator setPathTranslator(PathTranslator var1) {
      this.pathTranslator = var1;
      return this;
   }

   public String interpolate(String var1, ProfileActivationContext var2) throws InterpolationException {
      if (var1 == null) {
         return null;
      } else {
         RegexBasedInterpolator var3 = new RegexBasedInterpolator();
         final File var4 = var2.getProjectDirectory();
         if (var4 != null) {
            var3.addValueSource(new AbstractValueSource(false) {
               @Override
               public Object getValue(String var1) {
                  return "basedir".equals(var1) ? var4.getAbsolutePath() : null;
               }
            });
         } else if (var1.contains("${basedir}")) {
            return null;
         }

         var3.addValueSource(new MapBasedValueSource(var2.getProjectProperties()));
         var3.addValueSource(new MapBasedValueSource(var2.getUserProperties()));
         var3.addValueSource(new MapBasedValueSource(var2.getSystemProperties()));
         String var5 = var3.interpolate(var1, "");
         return this.pathTranslator.alignToBaseDirectory(var5, var4);
      }
   }
}
