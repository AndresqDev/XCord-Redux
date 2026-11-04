package org.apache.maven.building;

class DefaultProblem implements Problem {
   private final String source;
   private final int lineNumber;
   private final int columnNumber;
   private final String message;
   private final Exception exception;
   private final Problem.Severity severity;

   DefaultProblem(String var1, Problem.Severity var2, String var3, int var4, int var5, Exception var6) {
      this.message = var1;
      this.severity = var2 != null ? var2 : Problem.Severity.ERROR;
      this.source = var3 != null ? var3 : "";
      this.lineNumber = var4;
      this.columnNumber = var5;
      this.exception = var6;
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
   public String getLocation() {
      StringBuilder var1 = new StringBuilder(256);
      if (this.getSource().length() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append(this.getSource());
      }

      if (this.getLineNumber() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append("line ").append(this.getLineNumber());
      }

      if (this.getColumnNumber() > 0) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append("column ").append(this.getColumnNumber());
      }

      return var1.toString();
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
   public Problem.Severity getSeverity() {
      return this.severity;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append('[').append(this.getSeverity()).append("] ");
      var1.append(this.getMessage());
      var1.append(" @ ").append(this.getLocation());
      return var1.toString();
   }
}
