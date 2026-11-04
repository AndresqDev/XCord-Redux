package org.apache.maven.building;

import java.util.ArrayList;
import java.util.List;

class DefaultProblemCollector implements ProblemCollector {
   private List<Problem> problems;
   private String source;

   DefaultProblemCollector(List<Problem> var1) {
      this.problems = (List<Problem>)(var1 != null ? var1 : new ArrayList<>());
   }

   @Override
   public List<Problem> getProblems() {
      return this.problems;
   }

   @Override
   public void setSource(String var1) {
      this.source = var1;
   }

   @Override
   public void add(Problem.Severity var1, String var2, int var3, int var4, Exception var5) {
      DefaultProblem var6 = new DefaultProblem(var2, var1, this.source, var3, var4, var5);
      this.problems.add(var6);
   }
}
