package org.apache.maven.model.interpolation;

import java.util.List;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.ValueSource;

class ProblemDetectingValueSource implements ValueSource {
   private final ValueSource valueSource;
   private final String bannedPrefix;
   private final String newPrefix;
   private final ModelProblemCollector problems;

   ProblemDetectingValueSource(ValueSource var1, String var2, String var3, ModelProblemCollector var4) {
      this.valueSource = var1;
      this.bannedPrefix = var2;
      this.newPrefix = var3;
      this.problems = var4;
   }

   @Override
   public Object getValue(String var1) {
      Object var2 = this.valueSource.getValue(var1);
      if (var2 != null && var1.startsWith(this.bannedPrefix)) {
         String var3 = "The expression ${" + var1 + "} is deprecated.";
         if (this.newPrefix != null && this.newPrefix.length() > 0) {
            var3 = var3 + " Please use ${" + this.newPrefix + var1.substring(this.bannedPrefix.length()) + "} instead.";
         }

         this.problems.add(new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20).setMessage(var3));
      }

      return var2;
   }

   @Override
   public List getFeedback() {
      return this.valueSource.getFeedback();
   }

   @Override
   public void clearFeedback() {
      this.valueSource.clearFeedback();
   }
}
