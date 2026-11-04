package org.codehaus.plexus.util.dag;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CycleDetector {
   private static final Integer NOT_VISITED = 0;
   private static final Integer VISITING = 1;
   private static final Integer VISITED = 2;

   public static List<String> hasCycle(DAG var0) {
      List var1 = var0.getVertices();
      HashMap var2 = new HashMap();
      List var3 = null;

      for (Vertex var5 : var1) {
         if (isNotVisited(var5, var2)) {
            var3 = introducesCycle(var5, var2);
            if (var3 != null) {
               break;
            }
         }
      }

      return var3;
   }

   public static List<String> introducesCycle(Vertex var0, Map<Vertex, Integer> var1) {
      LinkedList var2 = new LinkedList();
      boolean var3 = dfsVisit(var0, var2, var1);
      if (var3) {
         String var4 = (String)var2.getFirst();
         int var5 = var2.lastIndexOf(var4);
         List var6 = var2.subList(0, var5 + 1);
         Collections.reverse(var6);
         return var6;
      } else {
         return null;
      }
   }

   public static List<String> introducesCycle(Vertex var0) {
      HashMap var1 = new HashMap();
      return introducesCycle(var0, var1);
   }

   private static boolean isNotVisited(Vertex var0, Map<Vertex, Integer> var1) {
      Integer var2 = (Integer)var1.get(var0);
      return var2 == null || NOT_VISITED.equals(var2);
   }

   private static boolean isVisiting(Vertex var0, Map<Vertex, Integer> var1) {
      Integer var2 = (Integer)var1.get(var0);
      return VISITING.equals(var2);
   }

   private static boolean dfsVisit(Vertex var0, LinkedList<String> var1, Map<Vertex, Integer> var2) {
      var1.addFirst(var0.getLabel());
      var2.put(var0, VISITING);

      for (Vertex var4 : var0.getChildren()) {
         if (isNotVisited(var4, var2)) {
            boolean var5 = dfsVisit(var4, var1, var2);
            if (var5) {
               return true;
            }
         } else if (isVisiting(var4, var2)) {
            var1.addFirst(var4.getLabel());
            return true;
         }
      }

      var2.put(var0, VISITED);
      var1.removeFirst();
      return false;
   }
}
