package org.apache.maven.model.building;

import java.util.Objects;
import org.apache.maven.model.InputLocation;

public final class ModelProblemCollectorRequest {
   private final ModelProblem.Severity severity;
   private final ModelProblem.Version version;
   private Exception exception;
   private String message;
   private InputLocation location;

   public ModelProblemCollectorRequest(ModelProblem.Severity var1, ModelProblem.Version var2) {
      this.severity = Objects.requireNonNull(var1, "severity cannot be null");
      this.version = Objects.requireNonNull(var2, "version cannot be null");
   }

   public ModelProblem.Severity getSeverity() {
      return this.severity;
   }

   public ModelProblem.Version getVersion() {
      return this.version;
   }

   public Exception getException() {
      return this.exception;
   }

   public ModelProblemCollectorRequest setException(Exception var1) {
      this.exception = var1;
      return this;
   }

   public String getMessage() {
      return this.message;
   }

   public ModelProblemCollectorRequest setMessage(String var1) {
      this.message = var1;
      return this;
   }

   public InputLocation getLocation() {
      return this.location;
   }

   public ModelProblemCollectorRequest setLocation(InputLocation var1) {
      this.location = var1;
      return this;
   }
}
