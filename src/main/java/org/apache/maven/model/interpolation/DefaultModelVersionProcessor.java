package org.apache.maven.model.interpolation;

import java.util.Properties;
import org.apache.maven.model.building.ModelBuildingRequest;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelVersionProcessor implements ModelVersionProcessor {
   private static final String SHA1_PROPERTY = "sha1";
   private static final String CHANGELIST_PROPERTY = "changelist";
   private static final String REVISION_PROPERTY = "revision";

   @Override
   public boolean isValidProperty(String var1) {
      return "revision".equals(var1) || "changelist".equals(var1) || "sha1".equals(var1);
   }

   @Override
   public void overwriteModelProperties(Properties var1, ModelBuildingRequest var2) {
      if (var2.getSystemProperties().containsKey("revision")) {
         var1.put("revision", var2.getSystemProperties().get("revision"));
      }

      if (var2.getSystemProperties().containsKey("changelist")) {
         var1.put("changelist", var2.getSystemProperties().get("changelist"));
      }

      if (var2.getSystemProperties().containsKey("sha1")) {
         var1.put("sha1", var2.getSystemProperties().get("sha1"));
      }
   }
}
