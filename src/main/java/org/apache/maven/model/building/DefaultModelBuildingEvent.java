package org.apache.maven.model.building;

import org.apache.maven.model.Model;

class DefaultModelBuildingEvent implements ModelBuildingEvent {
   private final Model model;
   private final ModelBuildingRequest request;
   private final ModelProblemCollector problems;

   DefaultModelBuildingEvent(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      this.model = var1;
      this.request = var2;
      this.problems = var3;
   }

   @Override
   public Model getModel() {
      return this.model;
   }

   @Override
   public ModelBuildingRequest getRequest() {
      return this.request;
   }

   @Override
   public ModelProblemCollector getProblems() {
      return this.problems;
   }
}
