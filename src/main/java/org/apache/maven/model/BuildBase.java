package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuildBase extends PluginConfiguration implements Serializable, Cloneable {
   private String defaultGoal;
   private List<Resource> resources;
   private List<Resource> testResources;
   private String directory;
   private String finalName;
   private List<String> filters;

   public void addFilter(String var1) {
      this.getFilters().add(var1);
   }

   public void addResource(Resource var1) {
      this.getResources().add(var1);
   }

   public void addTestResource(Resource var1) {
      this.getTestResources().add(var1);
   }

   public BuildBase clone() {
      try {
         BuildBase var1 = (BuildBase)super.clone();
         if (this.resources != null) {
            var1.resources = new ArrayList<>();

            for (Resource var3 : this.resources) {
               var1.resources.add(var3.clone());
            }
         }

         if (this.testResources != null) {
            var1.testResources = new ArrayList<>();

            for (Resource var6 : this.testResources) {
               var1.testResources.add(var6.clone());
            }
         }

         if (this.filters != null) {
            var1.filters = new ArrayList<>();
            var1.filters.addAll(this.filters);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getDefaultGoal() {
      return this.defaultGoal;
   }

   public String getDirectory() {
      return this.directory;
   }

   public List<String> getFilters() {
      if (this.filters == null) {
         this.filters = new ArrayList<>();
      }

      return this.filters;
   }

   public String getFinalName() {
      return this.finalName;
   }

   public List<Resource> getResources() {
      if (this.resources == null) {
         this.resources = new ArrayList<>();
      }

      return this.resources;
   }

   public List<Resource> getTestResources() {
      if (this.testResources == null) {
         this.testResources = new ArrayList<>();
      }

      return this.testResources;
   }

   public void removeFilter(String var1) {
      this.getFilters().remove(var1);
   }

   public void removeResource(Resource var1) {
      this.getResources().remove(var1);
   }

   public void removeTestResource(Resource var1) {
      this.getTestResources().remove(var1);
   }

   public void setDefaultGoal(String var1) {
      this.defaultGoal = var1;
   }

   public void setDirectory(String var1) {
      this.directory = var1;
   }

   public void setFilters(List<String> var1) {
      this.filters = var1;
   }

   public void setFinalName(String var1) {
      this.finalName = var1;
   }

   public void setResources(List<Resource> var1) {
      this.resources = var1;
   }

   public void setTestResources(List<Resource> var1) {
      this.testResources = var1;
   }
}
