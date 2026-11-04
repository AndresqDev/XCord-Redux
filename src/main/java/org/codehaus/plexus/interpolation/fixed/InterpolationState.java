package org.codehaus.plexus.interpolation.fixed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class InterpolationState {
   private final List<String> messages = new ArrayList<>();
   private final List<Throwable> causes = new ArrayList<>();
   final Set<String> unresolvable = new HashSet<>();
   RecursionInterceptor recursionInterceptor = new SimpleRecursionInterceptor();
   FixedStringSearchInterpolator root = null;

   public void addFeedback(String var1, Throwable var2) {
      this.messages.add(var1);
      this.causes.add(var2);
   }

   public List asList() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 < this.messages.size(); var2++) {
         String var3 = this.messages.get(var2);
         if (var3 != null) {
            var1.add(var3);
         }

         Throwable var4 = this.causes.get(var2);
         if (var4 != null) {
            var1.add(var4);
         }
      }

      return var1.size() > 0 ? var1 : null;
   }

   public void clear() {
      this.messages.clear();
      this.causes.clear();
      this.unresolvable.clear();
      this.recursionInterceptor.clear();
      this.root = null;
   }

   public void setRecursionInterceptor(RecursionInterceptor var1) {
      this.recursionInterceptor = var1;
   }
}
