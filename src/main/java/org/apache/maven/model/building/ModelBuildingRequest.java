package org.apache.maven.model.building;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.WorkspaceModelResolver;

public interface ModelBuildingRequest {
   int VALIDATION_LEVEL_MINIMAL = 0;
   int VALIDATION_LEVEL_MAVEN_2_0 = 20;
   int VALIDATION_LEVEL_MAVEN_3_0 = 30;
   int VALIDATION_LEVEL_MAVEN_3_1 = 31;
   int VALIDATION_LEVEL_STRICT = 30;

   Model getRawModel();

   ModelBuildingRequest setRawModel(Model var1);

   ModelSource getModelSource();

   ModelBuildingRequest setModelSource(ModelSource var1);

   File getPomFile();

   ModelBuildingRequest setPomFile(File var1);

   int getValidationLevel();

   ModelBuildingRequest setValidationLevel(int var1);

   boolean isProcessPlugins();

   ModelBuildingRequest setProcessPlugins(boolean var1);

   boolean isTwoPhaseBuilding();

   ModelBuildingRequest setTwoPhaseBuilding(boolean var1);

   boolean isLocationTracking();

   ModelBuildingRequest setLocationTracking(boolean var1);

   List<Profile> getProfiles();

   ModelBuildingRequest setProfiles(List<Profile> var1);

   List<String> getActiveProfileIds();

   ModelBuildingRequest setActiveProfileIds(List<String> var1);

   List<String> getInactiveProfileIds();

   ModelBuildingRequest setInactiveProfileIds(List<String> var1);

   Properties getSystemProperties();

   ModelBuildingRequest setSystemProperties(Properties var1);

   Properties getUserProperties();

   ModelBuildingRequest setUserProperties(Properties var1);

   Date getBuildStartTime();

   ModelBuildingRequest setBuildStartTime(Date var1);

   ModelResolver getModelResolver();

   ModelBuildingRequest setModelResolver(ModelResolver var1);

   ModelBuildingListener getModelBuildingListener();

   ModelBuildingRequest setModelBuildingListener(ModelBuildingListener var1);

   ModelCache getModelCache();

   ModelBuildingRequest setModelCache(ModelCache var1);

   WorkspaceModelResolver getWorkspaceModelResolver();

   ModelBuildingRequest setWorkspaceModelResolver(WorkspaceModelResolver var1);
}
