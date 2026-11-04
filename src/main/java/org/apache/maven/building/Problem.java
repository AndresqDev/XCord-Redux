package org.apache.maven.building;

public interface Problem {
   String getSource();

   int getLineNumber();

   int getColumnNumber();

   String getLocation();

   Exception getException();

   String getMessage();

   Problem.Severity getSeverity();

   public static enum Severity {
      FATAL,
      ERROR,
      WARNING;
   }
}
