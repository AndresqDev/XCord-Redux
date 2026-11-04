package org.apache.maven.model.building;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import org.apache.maven.model.Model;

public class ModelBuildingException extends Exception {
   private final ModelBuildingResult result;

   @Deprecated
   public ModelBuildingException(Model var1, String var2, List<ModelProblem> var3) {
      super(toMessage(var2, var3));
      if (var1 != null) {
         DefaultModelBuildingResult var4 = new DefaultModelBuildingResult();
         if (var2 == null) {
            var2 = "";
         }

         var4.addModelId(var2);
         var4.setRawModel(var2, var1);
         var4.setProblems(var3);
         this.result = var4;
      } else {
         this.result = null;
      }
   }

   public ModelBuildingException(ModelBuildingResult var1) {
      super(toMessage(var1));
      this.result = var1;
   }

   public ModelBuildingResult getResult() {
      return this.result;
   }

   public Model getModel() {
      if (this.result == null) {
         return null;
      } else {
         return this.result.getEffectiveModel() != null ? this.result.getEffectiveModel() : this.result.getRawModel();
      }
   }

   public String getModelId() {
      return this.result != null && !this.result.getModelIds().isEmpty() ? this.result.getModelIds().get(0) : "";
   }

   public List<ModelProblem> getProblems() {
      return this.result == null ? Collections.emptyList() : Collections.unmodifiableList(this.result.getProblems());
   }

   private static String toMessage(ModelBuildingResult var0) {
      return var0 != null && !var0.getModelIds().isEmpty() ? toMessage(var0.getModelIds().get(0), var0.getProblems()) : null;
   }

   private static String toMessage(String var0, List<ModelProblem> var1) {
      StringWriter var2 = new StringWriter(1024);
      PrintWriter var3 = new PrintWriter(var2);
      var3.print(var1.size());
      var3.print(var1.size() == 1 ? " problem was " : " problems were ");
      var3.print("encountered while building the effective model");
      if (var0 != null && var0.length() > 0) {
         var3.print(" for ");
         var3.print(var0);
      }

      var3.println();

      for (ModelProblem var5 : var1) {
         var3.print("[");
         var3.print(var5.getSeverity());
         var3.print("] ");
         var3.print(var5.getMessage());
         var3.print(" @ ");
         var3.println(ModelProblemUtils.formatLocation(var5, var0));
      }

      return var2.toString();
   }
}
