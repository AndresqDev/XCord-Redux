package org.apache.maven.model.interpolation;

import java.io.File;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface ModelInterpolator {
   Model interpolateModel(Model var1, File var2, ModelBuildingRequest var3, ModelProblemCollector var4);
}
