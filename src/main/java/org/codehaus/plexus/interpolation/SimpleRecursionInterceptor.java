package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SimpleRecursionInterceptor implements RecursionInterceptor {
   private Stack expressions = new Stack();

   @Override
   public void expressionResolutionFinished(String var1) {
      this.expressions.pop();
   }

   @Override
   public void expressionResolutionStarted(String var1) {
      this.expressions.push(var1);
   }

   @Override
   public boolean hasRecursiveExpression(String var1) {
      return this.expressions.contains(var1);
   }

   @Override
   public List getExpressionCycle(String var1) {
      int var2 = this.expressions.indexOf(var1);
      return var2 < 0 ? Collections.EMPTY_LIST : this.expressions.subList(var2, this.expressions.size());
   }

   @Override
   public void clear() {
      this.expressions.clear();
   }
}
