package org.apache.maven.model.building;

import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

public interface ModelBuildingResult {
   List<String> getModelIds();

   Model getEffectiveModel();

   Model getRawModel();

   Model getRawModel(String var1);

   List<Profile> getActivePomProfiles(String var1);

   List<Profile> getActiveExternalProfiles();

   List<ModelProblem> getProblems();
}
