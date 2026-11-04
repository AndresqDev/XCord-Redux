package org.apache.maven.model.building;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.WorkspaceModelResolver;

class FilterModelBuildingRequest implements ModelBuildingRequest {
   protected ModelBuildingRequest request;

   FilterModelBuildingRequest(ModelBuildingRequest var1) {
      this.request = var1;
   }

   @Override
   public File getPomFile() {
      return this.request.getPomFile();
   }

   public FilterModelBuildingRequest setPomFile(File var1) {
      this.request.setPomFile(var1);
      return this;
   }

   @Override
   public ModelSource getModelSource() {
      return this.request.getModelSource();
   }

   public FilterModelBuildingRequest setModelSource(ModelSource var1) {
      this.request.setModelSource(var1);
      return this;
   }

   @Override
   public int getValidationLevel() {
      return this.request.getValidationLevel();
   }

   public FilterModelBuildingRequest setValidationLevel(int var1) {
      this.request.setValidationLevel(var1);
      return this;
   }

   @Override
   public boolean isProcessPlugins() {
      return this.request.isProcessPlugins();
   }

   public FilterModelBuildingRequest setProcessPlugins(boolean var1) {
      this.request.setProcessPlugins(var1);
      return this;
   }

   @Override
   public boolean isTwoPhaseBuilding() {
      return this.request.isTwoPhaseBuilding();
   }

   public FilterModelBuildingRequest setTwoPhaseBuilding(boolean var1) {
      this.request.setTwoPhaseBuilding(var1);
      return this;
   }

   @Override
   public boolean isLocationTracking() {
      return this.request.isLocationTracking();
   }

   public FilterModelBuildingRequest setLocationTracking(boolean var1) {
      this.request.setLocationTracking(var1);
      return this;
   }

   @Override
   public List<Profile> getProfiles() {
      return this.request.getProfiles();
   }

   public FilterModelBuildingRequest setProfiles(List<Profile> var1) {
      this.request.setProfiles(var1);
      return this;
   }

   @Override
   public List<String> getActiveProfileIds() {
      return this.request.getActiveProfileIds();
   }

   public FilterModelBuildingRequest setActiveProfileIds(List<String> var1) {
      this.request.setActiveProfileIds(var1);
      return this;
   }

   @Override
   public List<String> getInactiveProfileIds() {
      return this.request.getInactiveProfileIds();
   }

   public FilterModelBuildingRequest setInactiveProfileIds(List<String> var1) {
      this.request.setInactiveProfileIds(var1);
      return this;
   }

   @Override
   public Properties getSystemProperties() {
      return this.request.getSystemProperties();
   }

   public FilterModelBuildingRequest setSystemProperties(Properties var1) {
      this.request.setSystemProperties(var1);
      return this;
   }

   @Override
   public Properties getUserProperties() {
      return this.request.getUserProperties();
   }

   public FilterModelBuildingRequest setUserProperties(Properties var1) {
      this.request.setUserProperties(var1);
      return this;
   }

   @Override
   public Date getBuildStartTime() {
      return this.request.getBuildStartTime();
   }

   @Override
   public ModelBuildingRequest setBuildStartTime(Date var1) {
      this.request.setBuildStartTime(var1);
      return this;
   }

   @Override
   public ModelResolver getModelResolver() {
      return this.request.getModelResolver();
   }

   public FilterModelBuildingRequest setModelResolver(ModelResolver var1) {
      this.request.setModelResolver(var1);
      return this;
   }

   @Override
   public ModelBuildingListener getModelBuildingListener() {
      return this.request.getModelBuildingListener();
   }

   @Override
   public ModelBuildingRequest setModelBuildingListener(ModelBuildingListener var1) {
      this.request.setModelBuildingListener(var1);
      return this;
   }

   @Override
   public ModelCache getModelCache() {
      return this.request.getModelCache();
   }

   public FilterModelBuildingRequest setModelCache(ModelCache var1) {
      this.request.setModelCache(var1);
      return this;
   }

   @Override
   public Model getRawModel() {
      return this.request.getRawModel();
   }

   @Override
   public ModelBuildingRequest setRawModel(Model var1) {
      this.request.setRawModel(var1);
      return this;
   }

   @Override
   public WorkspaceModelResolver getWorkspaceModelResolver() {
      return this.request.getWorkspaceModelResolver();
   }

   @Override
   public ModelBuildingRequest setWorkspaceModelResolver(WorkspaceModelResolver var1) {
      this.request.setWorkspaceModelResolver(var1);
      return this;
   }
}
