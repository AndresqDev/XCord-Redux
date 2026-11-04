package org.eclipse.aether.collection;

import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.graph.Exclusion;

public final class DependencyManagement {
   private String version;
   private String scope;
   private Boolean optional;
   private Collection<Exclusion> exclusions;
   private Map<String, String> properties;

   public String getVersion() {
      return this.version;
   }

   public DependencyManagement setVersion(String var1) {
      this.version = var1;
      return this;
   }

   public String getScope() {
      return this.scope;
   }

   public DependencyManagement setScope(String var1) {
      this.scope = var1;
      return this;
   }

   public Boolean getOptional() {
      return this.optional;
   }

   public DependencyManagement setOptional(Boolean var1) {
      this.optional = var1;
      return this;
   }

   public Collection<Exclusion> getExclusions() {
      return this.exclusions;
   }

   public DependencyManagement setExclusions(Collection<Exclusion> var1) {
      this.exclusions = var1;
      return this;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public DependencyManagement setProperties(Map<String, String> var1) {
      this.properties = var1;
      return this;
   }
}
