package org.apache.maven.model.building;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelParseException;

class DefaultModelProblemCollector implements ModelProblemCollectorExt {
   private final ModelBuildingResult result;
   private List<ModelProblem> problems;
   private String source;
   private Model sourceModel;
   private Model rootModel;
   private Set<ModelProblem.Severity> severities = EnumSet.noneOf(ModelProblem.Severity.class);

   DefaultModelProblemCollector(ModelBuildingResult var1) {
      this.result = var1;
      this.problems = var1.getProblems();

      for (ModelProblem var3 : this.problems) {
         this.severities.add(var3.getSeverity());
      }
   }

   public boolean hasFatalErrors() {
      return this.severities.contains(ModelProblem.Severity.FATAL);
   }

   public boolean hasErrors() {
      return this.severities.contains(ModelProblem.Severity.ERROR) || this.severities.contains(ModelProblem.Severity.FATAL);
   }

   @Override
   public List<ModelProblem> getProblems() {
      return this.problems;
   }

   public void setSource(String var1) {
      this.source = var1;
      this.sourceModel = null;
   }

   public void setSource(Model var1) {
      this.sourceModel = var1;
      this.source = null;
      if (this.rootModel == null) {
         this.rootModel = var1;
      }
   }

   private String getSource() {
      if (this.source == null && this.sourceModel != null) {
         this.source = ModelProblemUtils.toPath(this.sourceModel);
      }

      return this.source;
   }

   private String getModelId() {
      return ModelProblemUtils.toId(this.sourceModel);
   }

   public void setRootModel(Model var1) {
      this.rootModel = var1;
   }

   public Model getRootModel() {
      return this.rootModel;
   }

   public String getRootModelId() {
      return ModelProblemUtils.toId(this.rootModel);
   }

   public void add(ModelProblem var1) {
      this.problems.add(var1);
      this.severities.add(var1.getSeverity());
   }

   public void addAll(List<ModelProblem> var1) {
      this.problems.addAll(var1);

      for (ModelProblem var3 : var1) {
         this.severities.add(var3.getSeverity());
      }
   }

   @Override
   public void add(ModelProblemCollectorRequest var1) {
      int var2 = -1;
      int var3 = -1;
      String var4 = null;
      String var5 = null;
      if (var1.getLocation() != null) {
         var2 = var1.getLocation().getLineNumber();
         var3 = var1.getLocation().getColumnNumber();
         if (var1.getLocation().getSource() != null) {
            var5 = var1.getLocation().getSource().getModelId();
            var4 = var1.getLocation().getSource().getLocation();
         }
      }

      if (var5 == null) {
         var5 = this.getModelId();
         var4 = this.getSource();
      }

      if (var2 <= 0 && var3 <= 0 && var1.getException() instanceof ModelParseException) {
         ModelParseException var6 = (ModelParseException)var1.getException();
         var2 = var6.getLineNumber();
         var3 = var6.getColumnNumber();
      }

      DefaultModelProblem var7 = new DefaultModelProblem(var1.getMessage(), var1.getSeverity(), var1.getVersion(), var4, var2, var3, var5, var1.getException());
      this.add(var7);
   }

   public ModelBuildingException newModelBuildingException() {
      Object var1 = this.result;
      if (((ModelBuildingResult)var1).getModelIds().isEmpty()) {
         DefaultModelBuildingResult var2 = new DefaultModelBuildingResult();
         var2.setEffectiveModel(((ModelBuildingResult)var1).getEffectiveModel());
         var2.setProblems(this.getProblems());
         var2.setActiveExternalProfiles(((ModelBuildingResult)var1).getActiveExternalProfiles());
         String var3 = this.getRootModelId();
         var2.addModelId(var3);
         var2.setRawModel(var3, this.getRootModel());
         var1 = var2;
      }

      return new ModelBuildingException((ModelBuildingResult)var1);
   }
}
