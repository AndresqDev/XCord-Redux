package org.apache.maven.model.normalization;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelNormalizer {
   void mergeDuplicates(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3);

   void injectDefaultValues(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3);
}
