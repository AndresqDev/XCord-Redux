package org.apache.maven.model.building;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.WorkspaceModelResolver;

public class DefaultModelBuildingRequest implements ModelBuildingRequest {
   private Model rawModel;
   private File pomFile;
   private ModelSource modelSource;
   private int validationLevel = 30;
   private boolean processPlugins;
   private boolean twoPhaseBuilding;
   private boolean locationTracking;
   private List<Profile> profiles;
   private List<String> activeProfileIds;
   private List<String> inactiveProfileIds;
   private Properties systemProperties;
   private Properties userProperties;
   private Date buildStartTime;
   private ModelResolver modelResolver;
   private ModelBuildingListener modelBuildingListener;
   private ModelCache modelCache;
   private WorkspaceModelResolver workspaceResolver;

   public DefaultModelBuildingRequest() {
   }

   public DefaultModelBuildingRequest(ModelBuildingRequest var1) {
      this.setPomFile(var1.getPomFile());
      this.setModelSource(var1.getModelSource());
      this.setValidationLevel(var1.getValidationLevel());
      this.setProcessPlugins(var1.isProcessPlugins());
      this.setTwoPhaseBuilding(var1.isTwoPhaseBuilding());
      this.setProfiles(var1.getProfiles());
      this.setActiveProfileIds(var1.getActiveProfileIds());
      this.setInactiveProfileIds(var1.getInactiveProfileIds());
      this.setSystemProperties(var1.getSystemProperties());
      this.setUserProperties(var1.getUserProperties());
      this.setBuildStartTime(var1.getBuildStartTime());
      this.setModelResolver(var1.getModelResolver());
      this.setModelBuildingListener(var1.getModelBuildingListener());
      this.setModelCache(var1.getModelCache());
   }

   @Override
   public File getPomFile() {
      return this.pomFile;
   }

   public DefaultModelBuildingRequest setPomFile(File var1) {
      this.pomFile = var1 != null ? var1.getAbsoluteFile() : null;
      return this;
   }

   @Override
   public synchronized ModelSource getModelSource() {
      if (this.modelSource == null && this.pomFile != null) {
         this.modelSource = new FileModelSource(this.pomFile);
      }

      return this.modelSource;
   }

   public DefaultModelBuildingRequest setModelSource(ModelSource var1) {
      this.modelSource = var1;
      return this;
   }

   @Override
   public int getValidationLevel() {
      return this.validationLevel;
   }

   public DefaultModelBuildingRequest setValidationLevel(int var1) {
      this.validationLevel = var1;
      return this;
   }

   @Override
   public boolean isProcessPlugins() {
      return this.processPlugins;
   }

   public DefaultModelBuildingRequest setProcessPlugins(boolean var1) {
      this.processPlugins = var1;
      return this;
   }

   @Override
   public boolean isTwoPhaseBuilding() {
      return this.twoPhaseBuilding;
   }

   public DefaultModelBuildingRequest setTwoPhaseBuilding(boolean var1) {
      this.twoPhaseBuilding = var1;
      return this;
   }

   @Override
   public boolean isLocationTracking() {
      return this.locationTracking;
   }

   public DefaultModelBuildingRequest setLocationTracking(boolean var1) {
      this.locationTracking = var1;
      return this;
   }

   @Override
   public List<Profile> getProfiles() {
      if (this.profiles == null) {
         this.profiles = new ArrayList<>();
      }

      return this.profiles;
   }

   public DefaultModelBuildingRequest setProfiles(List<Profile> var1) {
      if (var1 != null) {
         this.profiles = new ArrayList<>(var1);
      } else {
         this.profiles = null;
      }

      return this;
   }

   @Override
   public List<String> getActiveProfileIds() {
      if (this.activeProfileIds == null) {
         this.activeProfileIds = new ArrayList<>();
      }

      return this.activeProfileIds;
   }

   public DefaultModelBuildingRequest setActiveProfileIds(List<String> var1) {
      if (var1 != null) {
         this.activeProfileIds = new ArrayList<>(var1);
      } else {
         this.activeProfileIds = null;
      }

      return this;
   }

   @Override
   public List<String> getInactiveProfileIds() {
      if (this.inactiveProfileIds == null) {
         this.inactiveProfileIds = new ArrayList<>();
      }

      return this.inactiveProfileIds;
   }

   public DefaultModelBuildingRequest setInactiveProfileIds(List<String> var1) {
      if (var1 != null) {
         this.inactiveProfileIds = new ArrayList<>(var1);
      } else {
         this.inactiveProfileIds = null;
      }

      return this;
   }

   @Override
   public Properties getSystemProperties() {
      if (this.systemProperties == null) {
         this.systemProperties = new Properties();
      }

      return this.systemProperties;
   }

   public DefaultModelBuildingRequest setSystemProperties(Properties var1) {
      if (var1 != null) {
         this.systemProperties = new Properties();
         synchronized (var1) {
            this.systemProperties.putAll(var1);
         }
      } else {
         this.systemProperties = null;
      }

      return this;
   }

   @Override
   public Properties getUserProperties() {
      if (this.userProperties == null) {
         this.userProperties = new Properties();
      }

      return this.userProperties;
   }

   public DefaultModelBuildingRequest setUserProperties(Properties var1) {
      if (var1 != null) {
         this.userProperties = new Properties();
         this.userProperties.putAll(var1);
      } else {
         this.userProperties = null;
      }

      return this;
   }

   @Override
   public Date getBuildStartTime() {
      return this.buildStartTime;
   }

   @Override
   public ModelBuildingRequest setBuildStartTime(Date var1) {
      this.buildStartTime = var1;
      return this;
   }

   @Override
   public ModelResolver getModelResolver() {
      return this.modelResolver;
   }

   public DefaultModelBuildingRequest setModelResolver(ModelResolver var1) {
      this.modelResolver = var1;
      return this;
   }

   @Override
   public ModelBuildingListener getModelBuildingListener() {
      return this.modelBuildingListener;
   }

   @Override
   public ModelBuildingRequest setModelBuildingListener(ModelBuildingListener var1) {
      this.modelBuildingListener = var1;
      return this;
   }

   @Override
   public ModelCache getModelCache() {
      return this.modelCache;
   }

   public DefaultModelBuildingRequest setModelCache(ModelCache var1) {
      this.modelCache = var1;
      return this;
   }

   @Override
   public Model getRawModel() {
      return this.rawModel;
   }

   @Override
   public ModelBuildingRequest setRawModel(Model var1) {
      this.rawModel = var1;
      return this;
   }

   @Override
   public WorkspaceModelResolver getWorkspaceModelResolver() {
      return this.workspaceResolver;
   }

   @Override
   public ModelBuildingRequest setWorkspaceModelResolver(WorkspaceModelResolver var1) {
      this.workspaceResolver = var1;
      return this;
   }
}
