package org.codehaus.plexus.interpolation.object;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ObjectInterpolationWarning {
   private final String message;
   private Throwable cause;
   private final String path;

   public ObjectInterpolationWarning(String var1, String var2) {
      this.path = var1;
      this.message = var2;
   }

   public ObjectInterpolationWarning(String var1, String var2, Throwable var3) {
      this.path = var1;
      this.message = var2;
      this.cause = var3;
   }

   public String getPath() {
      return this.path;
   }

   public String getMessage() {
      return this.message;
   }

   public Throwable getCause() {
      return this.cause;
   }

   @Override
   public String toString() {
      if (this.cause == null) {
         return this.path + ": " + this.message;
      } else {
         StringWriter var1 = new StringWriter();
         PrintWriter var2 = new PrintWriter(var1);
         var2.print(this.path);
         var2.print(": ");
         var2.println(this.message);
         var2.println("Cause: ");
         this.cause.printStackTrace(var2);
         return var1.toString();
      }
   }
}
