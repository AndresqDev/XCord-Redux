package org.apache.maven.model.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

class DefaultModelBuildingResult implements ModelBuildingResult {
   private Model effectiveModel;
   private List<String> modelIds = new ArrayList<>();
   private Map<String, Model> rawModels = new HashMap<>();
   private Map<String, List<Profile>> activePomProfiles = new HashMap<>();
   private List<Profile> activeExternalProfiles = new ArrayList<>();
   private List<ModelProblem> problems = new ArrayList<>();

   @Override
   public Model getEffectiveModel() {
      return this.effectiveModel;
   }

   public DefaultModelBuildingResult setEffectiveModel(Model var1) {
      this.effectiveModel = var1;
      return this;
   }

   @Override
   public List<String> getModelIds() {
      return this.modelIds;
   }

   public DefaultModelBuildingResult addModelId(String var1) {
      Objects.requireNonNull(var1, "modelId cannot null");
      this.modelIds.add(var1);
      return this;
   }

   @Override
   public Model getRawModel() {
      return this.rawModels.get(this.modelIds.get(0));
   }

   @Override
   public Model getRawModel(String var1) {
      return this.rawModels.get(var1);
   }

   public DefaultModelBuildingResult setRawModel(String var1, Model var2) {
      Objects.requireNonNull(var1, "modelId cannot null");
      this.rawModels.put(var1, var2);
      return this;
   }

   @Override
   public List<Profile> getActivePomProfiles(String var1) {
      return this.activePomProfiles.get(var1);
   }

   public DefaultModelBuildingResult setActivePomProfiles(String var1, List<Profile> var2) {
      Objects.requireNonNull(var1, "modelId cannot null");
      if (var2 != null) {
         this.activePomProfiles.put(var1, new ArrayList<>(var2));
      } else {
         this.activePomProfiles.remove(var1);
      }

      return this;
   }

   @Override
   public List<Profile> getActiveExternalProfiles() {
      return this.activeExternalProfiles;
   }

   public DefaultModelBuildingResult setActiveExternalProfiles(List<Profile> var1) {
      if (var1 != null) {
         this.activeExternalProfiles = new ArrayList<>(var1);
      } else {
         this.activeExternalProfiles.clear();
      }

      return this;
   }

   @Override
   public List<ModelProblem> getProblems() {
      return this.problems;
   }

   public DefaultModelBuildingResult setProblems(List<ModelProblem> var1) {
      if (var1 != null) {
         this.problems = new ArrayList<>(var1);
      } else {
         this.problems.clear();
      }

      return this;
   }
}
