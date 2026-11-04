package org.codehaus.plexus.interpolation.fixed;

import org.codehaus.plexus.interpolation.RecursionInterceptor;

public class InterpolationCycleException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public InterpolationCycleException(RecursionInterceptor var1, String var2, String var3) {
      super("Detected the following recursive expression cycle in '" + var2 + "': " + var1.getExpressionCycle(var2) + var3);
   }
}
