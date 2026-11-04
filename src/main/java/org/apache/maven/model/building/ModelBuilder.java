package org.apache.maven.model.building;

import java.io.File;
import org.apache.maven.model.Model;

public interface ModelBuilder {
   ModelBuildingResult build(ModelBuildingRequest var1) throws ModelBuildingException;

   ModelBuildingResult build(ModelBuildingRequest var1, ModelBuildingResult var2) throws ModelBuildingException;

   Result<? extends Model> buildRawModel(File var1, int var2, boolean var3);
}
