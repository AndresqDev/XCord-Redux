package org.codehaus.plexus.interpolation;

import java.util.List;

public interface RecursionInterceptor {
   void expressionResolutionStarted(String var1);

   void expressionResolutionFinished(String var1);

   boolean hasRecursiveExpression(String var1);

   List getExpressionCycle(String var1);

   void clear();
}
