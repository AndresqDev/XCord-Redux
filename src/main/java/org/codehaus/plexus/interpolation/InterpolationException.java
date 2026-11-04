package org.codehaus.plexus.interpolation;

public class InterpolationException extends Exception {
   private static final long serialVersionUID = 1L;
   private final String expression;

   public InterpolationException(String var1, String var2, Throwable var3) {
      super(buildMessage(var1, var2), var3);
      this.expression = var2;
   }

   public InterpolationException(String var1, String var2) {
      super(buildMessage(var1, var2));
      this.expression = var2;
   }

   private static String buildMessage(String var0, String var1) {
      return "Resolving expression: '" + var1 + "': " + var0;
   }

   public String getExpression() {
      return this.expression;
   }
}
