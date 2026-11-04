package org.apache.maven.model.path;

import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.apache.maven.model.building.ModelBuildingRequest;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelUrlNormalizer implements ModelUrlNormalizer {
   @Inject
   private UrlNormalizer urlNormalizer;

   public DefaultModelUrlNormalizer setUrlNormalizer(UrlNormalizer var1) {
      this.urlNormalizer = var1;
      return this;
   }

   @Override
   public void normalize(Model var1, ModelBuildingRequest var2) {
      if (var1 != null) {
         var1.setUrl(this.normalize(var1.getUrl()));
         Scm var3 = var1.getScm();
         if (var3 != null) {
            var3.setUrl(this.normalize(var3.getUrl()));
            var3.setConnection(this.normalize(var3.getConnection()));
            var3.setDeveloperConnection(this.normalize(var3.getDeveloperConnection()));
         }

         DistributionManagement var4 = var1.getDistributionManagement();
         if (var4 != null) {
            Site var5 = var4.getSite();
            if (var5 != null) {
               var5.setUrl(this.normalize(var5.getUrl()));
            }
         }
      }
   }

   private String normalize(String var1) {
      return this.urlNormalizer.normalize(var1);
   }
}
