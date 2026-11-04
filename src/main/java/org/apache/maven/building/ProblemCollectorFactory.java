package org.apache.maven.building;

import java.util.List;

public class ProblemCollectorFactory {
   public static ProblemCollector newInstance(List<Problem> var0) {
      return new DefaultProblemCollector(var0);
   }
}
