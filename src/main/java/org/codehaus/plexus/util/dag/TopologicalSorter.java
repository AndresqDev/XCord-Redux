package org.codehaus.plexus.util.dag;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TopologicalSorter {
   private static final Integer NOT_VISITED = 0;
   private static final Integer VISITING = 1;
   private static final Integer VISITED = 2;

   public static List<String> sort(DAG var0) {
      return dfs(var0);
   }

   public static List<String> sort(Vertex var0) {
      LinkedList var1 = new LinkedList();
      dfsVisit(var0, new HashMap<>(), var1);
      return var1;
   }

   private static List<String> dfs(DAG var0) {
      LinkedList var1 = new LinkedList();
      HashMap var2 = new HashMap();

      for (Vertex var4 : var0.getVertices()) {
         if (isNotVisited(var4, var2)) {
            dfsVisit(var4, var2, var1);
         }
      }

      return var1;
   }

   private static boolean isNotVisited(Vertex var0, Map<Vertex, Integer> var1) {
      Integer var2 = (Integer)var1.get(var0);
      return var2 == null || NOT_VISITED.equals(var2);
   }

   private static void dfsVisit(Vertex var0, Map<Vertex, Integer> var1, List<String> var2) {
      var1.put(var0, VISITING);

      for (Vertex var4 : var0.getChildren()) {
         if (isNotVisited(var4, var1)) {
            dfsVisit(var4, var1, var2);
         }
      }

      var1.put(var0, VISITED);
      var2.add(var0.getLabel());
   }
}
