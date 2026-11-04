package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.List;

public class CycleDetectedException extends Exception {
   private List<String> cycle;

   public CycleDetectedException(String var1, List<String> var2) {
      super(var1);
      this.cycle = var2;
   }

   public List<String> getCycle() {
      return this.cycle;
   }

   public String cycleToString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.cycle.iterator();

      while (var2.hasNext()) {
         var1.append((String)var2.next());
         if (var2.hasNext()) {
            var1.append(" --> ");
         }
      }

      return var1.toString();
   }

   @Override
   public String getMessage() {
      return super.getMessage() + " " + this.cycleToString();
   }
}
