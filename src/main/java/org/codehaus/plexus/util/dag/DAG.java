package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DAG implements Serializable, Cloneable {
   private Map<String, Vertex> vertexMap = new HashMap<>();
   private List<Vertex> vertexList = new ArrayList<>();

   public List<Vertex> getVertices() {
      return this.vertexList;
   }

   @Deprecated
   public List<Vertex> getVerticies() {
      return this.getVertices();
   }

   public Set<String> getLabels() {
      return this.vertexMap.keySet();
   }

   public Vertex addVertex(String var1) {
      Vertex var2 = null;
      if (this.vertexMap.containsKey(var1)) {
         var2 = this.vertexMap.get(var1);
      } else {
         var2 = new Vertex(var1);
         this.vertexMap.put(var1, var2);
         this.vertexList.add(var2);
      }

      return var2;
   }

   public void addEdge(String var1, String var2) throws CycleDetectedException {
      Vertex var3 = this.addVertex(var1);
      Vertex var4 = this.addVertex(var2);
      this.addEdge(var3, var4);
   }

   public void addEdge(Vertex var1, Vertex var2) throws CycleDetectedException {
      var1.addEdgeTo(var2);
      var2.addEdgeFrom(var1);
      List var3 = CycleDetector.introducesCycle(var2);
      if (var3 != null) {
         this.removeEdge(var1, var2);
         String var4 = "Edge between '" + var1 + "' and '" + var2 + "' introduces to cycle in the graph";
         throw new CycleDetectedException(var4, var3);
      }
   }

   public void removeEdge(String var1, String var2) {
      Vertex var3 = this.addVertex(var1);
      Vertex var4 = this.addVertex(var2);
      this.removeEdge(var3, var4);
   }

   public void removeEdge(Vertex var1, Vertex var2) {
      var1.removeEdgeTo(var2);
      var2.removeEdgeFrom(var1);
   }

   public Vertex getVertex(String var1) {
      return this.vertexMap.get(var1);
   }

   public boolean hasEdge(String var1, String var2) {
      Vertex var3 = this.getVertex(var1);
      Vertex var4 = this.getVertex(var2);
      return var3.getChildren().contains(var4);
   }

   public List<String> getChildLabels(String var1) {
      Vertex var2 = this.getVertex(var1);
      return var2.getChildLabels();
   }

   public List<String> getParentLabels(String var1) {
      Vertex var2 = this.getVertex(var1);
      return var2.getParentLabels();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public boolean isConnected(String var1) {
      Vertex var2 = this.getVertex(var1);
      return var2.isConnected();
   }

   public List<String> getSuccessorLabels(String var1) {
      Vertex var2 = this.getVertex(var1);
      Object var3;
      if (var2.isLeaf()) {
         var3 = new ArrayList(1);
         var3.add(var1);
      } else {
         var3 = TopologicalSorter.sort(var2);
      }

      return (List<String>)var3;
   }
}
