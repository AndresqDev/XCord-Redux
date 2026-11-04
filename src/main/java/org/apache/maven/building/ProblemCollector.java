package org.apache.maven.building;

import java.util.List;

public interface ProblemCollector {
   void add(Problem.Severity var1, String var2, int var3, int var4, Exception var5);

   void setSource(String var1);

   List<Problem> getProblems();
}
