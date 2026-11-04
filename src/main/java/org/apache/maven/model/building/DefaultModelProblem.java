package org.apache.maven.model.building;

import org.apache.maven.model.Model;

public class DefaultModelProblem implements ModelProblem {
   private final String source;
   private final int lineNumber;
   private final int columnNumber;
   private final String modelId;
   private final String message;
   private final Exception exception;
   private final ModelProblem.Severity severity;
   private final ModelProblem.Version version;

   public DefaultModelProblem(String var1, ModelProblem.Severity var2, ModelProblem.Version var3, Model var4, int var5, int var6, Exception var7) {
      this(var1, var2, var3, ModelProblemUtils.toPath(var4), var5, var6, ModelProblemUtils.toId(var4), var7);
   }

   public DefaultModelProblem(String var1, ModelProblem.Severity var2, ModelProblem.Version var3, String var4, int var5, int var6, String var7, Exception var8) {
      this.message = var1;
      this.severity = var2 != null ? var2 : ModelProblem.Severity.ERROR;
      this.source = var4 != null ? var4 : "";
      this.lineNumber = var5;
      this.columnNumber = var6;
      this.modelId = var7 != null ? var7 : "";
      this.exception = var8;
      this.version = var3;
   }

   @Override
   public String getSource() {
      return this.source;
   }

   @Override
   public int getLineNumber() {
      return this.lineNumber;
   }

   @Override
   public int getColumnNumber() {
      return this.columnNumber;
   }

   @Override
   public String getModelId() {
      return this.modelId;
   }

   @Override
   public Exception getException() {
      return this.exception;
   }

   @Override
   public String getMessage() {
      String var1;
      if (this.message != null && this.message.length() > 0) {
         var1 = this.message;
      } else {
         var1 = this.exception.getMessage();
         if (var1 == null) {
            var1 = "";
         }
      }

      return var1;
   }

   @Override
   public ModelProblem.Severity getSeverity() {
      return this.severity;
   }

   @Override
   public ModelProblem.Version getVersion() {
      return this.version;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append('[').append(this.getSeverity()).append("] ");
      var1.append(this.getMessage());
      var1.append(" @ ").append(ModelProblemUtils.formatLocation(this, null));
      return var1.toString();
   }
}
