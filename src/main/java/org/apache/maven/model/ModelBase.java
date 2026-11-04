package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ModelBase implements Serializable, Cloneable, InputLocationTracker {
   private List<String> modules;
   private DistributionManagement distributionManagement;
   private Properties properties;
   private DependencyManagement dependencyManagement;
   private List<Dependency> dependencies;
   private List<Repository> repositories;
   private List<Repository> pluginRepositories;
   private Object reports;
   private Reporting reporting;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation modulesLocation;
   private InputLocation distributionManagementLocation;
   private InputLocation propertiesLocation;
   private InputLocation dependencyManagementLocation;
   private InputLocation dependenciesLocation;
   private InputLocation repositoriesLocation;
   private InputLocation pluginRepositoriesLocation;
   private InputLocation reportsLocation;
   private InputLocation reportingLocation;

   public void addDependency(Dependency var1) {
      this.getDependencies().add(var1);
   }

   public void addModule(String var1) {
      this.getModules().add(var1);
   }

   public void addPluginRepository(Repository var1) {
      this.getPluginRepositories().add(var1);
   }

   public void addProperty(String var1, String var2) {
      this.getProperties().put(var1, var2);
   }

   public void addRepository(Repository var1) {
      this.getRepositories().add(var1);
   }

   public ModelBase clone() {
      try {
         ModelBase var1 = (ModelBase)super.clone();
         if (this.modules != null) {
            var1.modules = new ArrayList<>();
            var1.modules.addAll(this.modules);
         }

         if (this.distributionManagement != null) {
            var1.distributionManagement = this.distributionManagement.clone();
         }

         if (this.properties != null) {
            var1.properties = (Properties)this.properties.clone();
         }

         if (this.dependencyManagement != null) {
            var1.dependencyManagement = this.dependencyManagement.clone();
         }

         if (this.dependencies != null) {
            var1.dependencies = new ArrayList<>();

            for (Dependency var3 : this.dependencies) {
               var1.dependencies.add(var3.clone());
            }
         }

         if (this.repositories != null) {
            var1.repositories = new ArrayList<>();

            for (Repository var7 : this.repositories) {
               var1.repositories.add(var7.clone());
            }
         }

         if (this.pluginRepositories != null) {
            var1.pluginRepositories = new ArrayList<>();

            for (Repository var8 : this.pluginRepositories) {
               var1.pluginRepositories.add(var8.clone());
            }
         }

         if (this.reports != null) {
            var1.reports = new Xpp3Dom((Xpp3Dom)this.reports);
         }

         if (this.reporting != null) {
            var1.reporting = this.reporting.clone();
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public List<Dependency> getDependencies() {
      if (this.dependencies == null) {
         this.dependencies = new ArrayList<>();
      }

      return this.dependencies;
   }

   public DependencyManagement getDependencyManagement() {
      return this.dependencyManagement;
   }

   public DistributionManagement getDistributionManagement() {
      return this.distributionManagement;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "modules":
               return this.modulesLocation;
            case "distributionManagement":
               return this.distributionManagementLocation;
            case "properties":
               return this.propertiesLocation;
            case "dependencyManagement":
               return this.dependencyManagementLocation;
            case "dependencies":
               return this.dependenciesLocation;
            case "repositories":
               return this.repositoriesLocation;
            case "pluginRepositories":
               return this.pluginRepositoriesLocation;
            case "reports":
               return this.reportsLocation;
            case "reporting":
               return this.reportingLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public List<String> getModules() {
      if (this.modules == null) {
         this.modules = new ArrayList<>();
      }

      return this.modules;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "modules":
               this.modulesLocation = var2;
               return;
            case "distributionManagement":
               this.distributionManagementLocation = var2;
               return;
            case "properties":
               this.propertiesLocation = var2;
               return;
            case "dependencyManagement":
               this.dependencyManagementLocation = var2;
               return;
            case "dependencies":
               this.dependenciesLocation = var2;
               return;
            case "repositories":
               this.repositoriesLocation = var2;
               return;
            case "pluginRepositories":
               this.pluginRepositoriesLocation = var2;
               return;
            case "reports":
               this.reportsLocation = var2;
               return;
            case "reporting":
               this.reportingLocation = var2;
               return;
            default:
               this.setOtherLocation(var1, var2);
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public List<Repository> getPluginRepositories() {
      if (this.pluginRepositories == null) {
         this.pluginRepositories = new ArrayList<>();
      }

      return this.pluginRepositories;
   }

   public Properties getProperties() {
      if (this.properties == null) {
         this.properties = new Properties();
      }

      return this.properties;
   }

   public Reporting getReporting() {
      return this.reporting;
   }

   public Object getReports() {
      return this.reports;
   }

   public List<Repository> getRepositories() {
      if (this.repositories == null) {
         this.repositories = new ArrayList<>();
      }

      return this.repositories;
   }

   public void removeDependency(Dependency var1) {
      this.getDependencies().remove(var1);
   }

   public void removeModule(String var1) {
      this.getModules().remove(var1);
   }

   public void removePluginRepository(Repository var1) {
      this.getPluginRepositories().remove(var1);
   }

   public void removeRepository(Repository var1) {
      this.getRepositories().remove(var1);
   }

   public void setDependencies(List<Dependency> var1) {
      this.dependencies = var1;
   }

   public void setDependencyManagement(DependencyManagement var1) {
      this.dependencyManagement = var1;
   }

   public void setDistributionManagement(DistributionManagement var1) {
      this.distributionManagement = var1;
   }

   public void setModules(List<String> var1) {
      this.modules = var1;
   }

   public void setPluginRepositories(List<Repository> var1) {
      this.pluginRepositories = var1;
   }

   public void setProperties(Properties var1) {
      this.properties = var1;
   }

   public void setReporting(Reporting var1) {
      this.reporting = var1;
   }

   public void setReports(Object var1) {
      this.reports = var1;
   }

   public void setRepositories(List<Repository> var1) {
      this.repositories = var1;
   }
}
