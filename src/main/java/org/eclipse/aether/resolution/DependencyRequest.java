package org.eclipse.aether.resolution;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class DependencyRequest {
   private DependencyNode root;
   private CollectRequest collectRequest;
   private DependencyFilter filter;
   private RequestTrace trace;

   public DependencyRequest() {
   }

   public DependencyRequest(DependencyNode var1, DependencyFilter var2) {
      this.setRoot(var1);
      this.setFilter(var2);
   }

   public DependencyRequest(CollectRequest var1, DependencyFilter var2) {
      this.setCollectRequest(var1);
      this.setFilter(var2);
   }

   public DependencyNode getRoot() {
      return this.root;
   }

   public DependencyRequest setRoot(DependencyNode var1) {
      this.root = var1;
      return this;
   }

   public CollectRequest getCollectRequest() {
      return this.collectRequest;
   }

   public DependencyRequest setCollectRequest(CollectRequest var1) {
      this.collectRequest = var1;
      return this;
   }

   public DependencyFilter getFilter() {
      return this.filter;
   }

   public DependencyRequest setFilter(DependencyFilter var1) {
      this.filter = var1;
      return this;
   }

   public RequestTrace getTrace() {
      return this.trace;
   }

   public DependencyRequest setTrace(RequestTrace var1) {
      this.trace = var1;
      return this;
   }

   @Override
   public String toString() {
      return this.root != null ? String.valueOf(this.root) : String.valueOf(this.collectRequest);
   }
}
