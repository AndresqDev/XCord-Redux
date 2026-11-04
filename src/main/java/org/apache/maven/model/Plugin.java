package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class Plugin extends ConfigurationContainer implements Serializable, Cloneable {
   private String groupId = "org.apache.maven.plugins";
   private String artifactId;
   private String version;
   private String extensions;
   private List<PluginExecution> executions;
   private List<Dependency> dependencies;
   private Object goals;
   private Map<String, PluginExecution> executionMap = null;

   public void addDependency(Dependency var1) {
      this.getDependencies().add(var1);
   }

   public void addExecution(PluginExecution var1) {
      this.getExecutions().add(var1);
   }

   public Plugin clone() {
      try {
         Plugin var1 = (Plugin)super.clone();
         if (this.executions != null) {
            var1.executions = new ArrayList<>();

            for (PluginExecution var3 : this.executions) {
               var1.executions.add(var3.clone());
            }
         }

         if (this.dependencies != null) {
            var1.dependencies = new ArrayList<>();

            for (Dependency var6 : this.dependencies) {
               var1.dependencies.add(var6.clone());
            }
         }

         if (this.goals != null) {
            var1.goals = new Xpp3Dom((Xpp3Dom)this.goals);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public List<Dependency> getDependencies() {
      if (this.dependencies == null) {
         this.dependencies = new ArrayList<>();
      }

      return this.dependencies;
   }

   public List<PluginExecution> getExecutions() {
      if (this.executions == null) {
         this.executions = new ArrayList<>();
      }

      return this.executions;
   }

   public String getExtensions() {
      return this.extensions;
   }

   public Object getGoals() {
      return this.goals;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeDependency(Dependency var1) {
      this.getDependencies().remove(var1);
   }

   public void removeExecution(PluginExecution var1) {
      this.getExecutions().remove(var1);
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setDependencies(List<Dependency> var1) {
      this.dependencies = var1;
   }

   public void setExecutions(List<PluginExecution> var1) {
      this.executions = var1;
   }

   public void setExtensions(String var1) {
      this.extensions = var1;
   }

   public void setGoals(Object var1) {
      this.goals = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public boolean isExtensions() {
      return this.extensions != null ? Boolean.parseBoolean(this.extensions) : false;
   }

   public void setExtensions(boolean var1) {
      this.extensions = String.valueOf(var1);
   }

   public void flushExecutionMap() {
      this.executionMap = null;
   }

   public Map<String, PluginExecution> getExecutionsAsMap() {
      if (this.executionMap == null) {
         this.executionMap = new LinkedHashMap<>();
         if (this.getExecutions() != null) {
            for (PluginExecution var2 : this.getExecutions()) {
               if (this.executionMap.containsKey(var2.getId())) {
                  throw new IllegalStateException(
                     "You cannot have two plugin executions with the same (or missing) <id/> elements.\nOffending execution\n\nId: '"
                        + var2.getId()
                        + "'\nPlugin:'"
                        + this.getKey()
                        + "'\n\n"
                  );
               }

               this.executionMap.put(var2.getId(), var2);
            }
         }
      }

      return this.executionMap;
   }

   public String getId() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(this.getGroupId() == null ? "[unknown-group-id]" : this.getGroupId());
      var1.append(":");
      var1.append(this.getArtifactId() == null ? "[unknown-artifact-id]" : this.getArtifactId());
      var1.append(":");
      var1.append(this.getVersion() == null ? "[unknown-version]" : this.getVersion());
      return var1.toString();
   }

   public String getKey() {
      return constructKey(this.groupId, this.artifactId);
   }

   public static String constructKey(String var0, String var1) {
      return var0 + ":" + var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof Plugin) {
         Plugin var2 = (Plugin)var1;
         return this.getKey().equals(var2.getKey());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getKey().hashCode();
   }

   @Override
   public String toString() {
      return "Plugin [" + this.getKey() + "]";
   }
}
