package org.apache.maven.model.building;

public interface ModelProblem {
   String getSource();

   int getLineNumber();

   int getColumnNumber();

   String getModelId();

   Exception getException();

   String getMessage();

   ModelProblem.Severity getSeverity();

   ModelProblem.Version getVersion();

   public static enum Severity {
      FATAL,
      ERROR,
      WARNING;
   }

   public static enum Version {
      BASE,
      V20,
      V30,
      V31;
   }
}
