package org.codehaus.plexus.interpolation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixAwareRecursionInterceptor implements RecursionInterceptor {
   public static final String DEFAULT_START_TOKEN = "\\$\\{";
   public static final String DEFAULT_END_TOKEN = "\\}";
   private Stack<String> nakedExpressions = new Stack<>();
   private final String[] possiblePrefixes;
   private boolean watchUnprefixedExpressions = true;

   public PrefixAwareRecursionInterceptor(Collection<String> var1, boolean var2) {
      this.possiblePrefixes = var1.toArray(new String[var1.size()]);
      this.watchUnprefixedExpressions = var2;
   }

   public PrefixAwareRecursionInterceptor(Collection<String> var1) {
      this.possiblePrefixes = var1.toArray(new String[var1.size()]);
   }

   @Override
   public boolean hasRecursiveExpression(String var1) {
      String var2 = ValueSourceUtils.trimPrefix(var1, this.possiblePrefixes, this.watchUnprefixedExpressions);
      return var2 != null && this.nakedExpressions.contains(var2);
   }

   @Override
   public void expressionResolutionFinished(String var1) {
      this.nakedExpressions.pop();
   }

   @Override
   public void expressionResolutionStarted(String var1) {
      String var2 = ValueSourceUtils.trimPrefix(var1, this.possiblePrefixes, this.watchUnprefixedExpressions);
      this.nakedExpressions.push(var2);
   }

   @Override
   public List getExpressionCycle(String var1) {
      String var2 = ValueSourceUtils.trimPrefix(var1, this.possiblePrefixes, this.watchUnprefixedExpressions);
      if (var2 == null) {
         return Collections.EMPTY_LIST;
      } else {
         int var3 = this.nakedExpressions.indexOf(var2);
         return var3 < 0 ? Collections.EMPTY_LIST : this.nakedExpressions.subList(var3, this.nakedExpressions.size());
      }
   }

   @Override
   public void clear() {
      this.nakedExpressions.clear();
   }
}
