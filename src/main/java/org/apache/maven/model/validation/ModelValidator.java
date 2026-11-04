package org.apache.maven.model.validation;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelValidator {
   void validateRawModel(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3);

   void validateEffectiveModel(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3);
}
