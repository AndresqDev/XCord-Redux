package org.apache.maven.model.interpolation;

import java.util.Properties;
import org.apache.maven.model.building.ModelBuildingRequest;

public interface ModelVersionProcessor {
   boolean isValidProperty(String var1);

   void overwriteModelProperties(Properties var1, ModelBuildingRequest var2);
}
