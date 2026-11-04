package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex implements Serializable, Cloneable {
   private String label = null;
   List<Vertex> children = new ArrayList<>();
   List<Vertex> parents = new ArrayList<>();

   public Vertex(String var1) {
      this.label = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public void addEdgeTo(Vertex var1) {
      this.children.add(var1);
   }

   public void removeEdgeTo(Vertex var1) {
      this.children.remove(var1);
   }

   public void addEdgeFrom(Vertex var1) {
      this.parents.add(var1);
   }

   public void removeEdgeFrom(Vertex var1) {
      this.parents.remove(var1);
   }

   public List<Vertex> getChildren() {
      return this.children;
   }

   public List<String> getChildLabels() {
      ArrayList var1 = new ArrayList(this.children.size());

      for (Vertex var3 : this.children) {
         var1.add(var3.getLabel());
      }

      return var1;
   }

   public List<Vertex> getParents() {
      return this.parents;
   }

   public List<String> getParentLabels() {
      ArrayList var1 = new ArrayList(this.parents.size());

      for (Vertex var3 : this.parents) {
         var1.add(var3.getLabel());
      }

      return var1;
   }

   public boolean isLeaf() {
      return this.children.size() == 0;
   }

   public boolean isRoot() {
      return this.parents.size() == 0;
   }

   public boolean isConnected() {
      return this.isRoot() || this.isLeaf();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   @Override
   public String toString() {
      return "Vertex{label='" + this.label + "'" + "}";
   }
}
