package org.apache.maven.model.merge;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ModelMerger {
   public void merge(Model var1, Model var2, boolean var3, Map<?, ?> var4) {
      Objects.requireNonNull(var1, "target cannot be null");
      if (var2 != null) {
         HashMap var5 = new HashMap();
         if (var4 != null) {
            var5.putAll(var4);
         }

         this.mergeModel(var1, var2, var3, var5);
      }
   }

   protected void mergeModel(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      this.mergeModelBase(var1, var2, var3, var4);
      this.mergeModel_ChildProjectUrlInheritAppendPath(var1, var2, var3, var4);
      this.mergeModel_ModelVersion(var1, var2, var3, var4);
      this.mergeModel_Parent(var1, var2, var3, var4);
      this.mergeModel_GroupId(var1, var2, var3, var4);
      this.mergeModel_ArtifactId(var1, var2, var3, var4);
      this.mergeModel_Version(var1, var2, var3, var4);
      this.mergeModel_Packaging(var1, var2, var3, var4);
      this.mergeModel_Name(var1, var2, var3, var4);
      this.mergeModel_Description(var1, var2, var3, var4);
      this.mergeModel_Url(var1, var2, var3, var4);
      this.mergeModel_InceptionYear(var1, var2, var3, var4);
      this.mergeModel_Organization(var1, var2, var3, var4);
      this.mergeModel_Licenses(var1, var2, var3, var4);
      this.mergeModel_MailingLists(var1, var2, var3, var4);
      this.mergeModel_Developers(var1, var2, var3, var4);
      this.mergeModel_Contributors(var1, var2, var3, var4);
      this.mergeModel_IssueManagement(var1, var2, var3, var4);
      this.mergeModel_Scm(var1, var2, var3, var4);
      this.mergeModel_CiManagement(var1, var2, var3, var4);
      this.mergeModel_Prerequisites(var1, var2, var3, var4);
      this.mergeModel_Build(var1, var2, var3, var4);
      this.mergeModel_Profiles(var1, var2, var3, var4);
   }

   protected void mergeModel_ModelVersion(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getModelVersion();
      if (var5 != null && (var3 || var1.getModelVersion() == null)) {
         var1.setModelVersion(var5);
         var1.setLocation("modelVersion", var2.getLocation("modelVersion"));
      }
   }

   protected void mergeModel_Parent(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Parent var5 = var2.getParent();
      if (var5 != null) {
         Parent var6 = var1.getParent();
         if (var6 == null) {
            var6 = new Parent();
            var6.setRelativePath(null);
            var1.setParent(var6);
         }

         this.mergeParent(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_GroupId(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeModel_ArtifactId(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeModel_ChildProjectUrlInheritAppendPath(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChildProjectUrlInheritAppendPath();
      if (var5 != null && (var3 || var1.getChildProjectUrlInheritAppendPath() == null)) {
         var1.setChildProjectUrlInheritAppendPath(var5);
         var1.setLocation("child.project.url.inherit.append.path", var2.getLocation("child.project.url.inherit.append.path"));
      }
   }

   protected void mergeModel_Version(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeModel_Packaging(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getPackaging();
      if (var5 != null && (var3 || var1.getPackaging() == null)) {
         var1.setPackaging(var5);
         var1.setLocation("packaging", var2.getLocation("packaging"));
      }
   }

   protected void mergeModel_Name(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeModel_Description(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDescription();
      if (var5 != null && (var3 || var1.getDescription() == null)) {
         var1.setDescription(var5);
         var1.setLocation("description", var2.getLocation("description"));
      }
   }

   protected void mergeModel_Url(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeModel_InceptionYear(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getInceptionYear();
      if (var5 != null && (var3 || var1.getInceptionYear() == null)) {
         var1.setInceptionYear(var5);
         var1.setLocation("inceptionYear", var2.getLocation("inceptionYear"));
      }
   }

   protected void mergeModel_Organization(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Organization var5 = var2.getOrganization();
      if (var5 != null) {
         Organization var6 = var1.getOrganization();
         if (var6 == null) {
            var6 = new Organization();
            var1.setOrganization(var6);
         }

         this.mergeOrganization(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_Licenses(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var1.setLicenses(merge(var1.getLicenses(), var2.getLicenses(), var3, new ModelMerger.LicenseKeyComputer()));
   }

   protected void mergeModel_MailingLists(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var1.setMailingLists(merge(var1.getMailingLists(), var2.getMailingLists(), var3, new ModelMerger.MailingListKeyComputer()));
   }

   protected void mergeModel_Developers(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var1.setDevelopers(merge(var1.getDevelopers(), var2.getDevelopers(), var3, new ModelMerger.DeveloperKeyComputer()));
   }

   protected void mergeModel_Contributors(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var1.setContributors(merge(var1.getContributors(), var2.getContributors(), var3, new ModelMerger.ContributorKeyComputer()));
   }

   protected void mergeModel_IssueManagement(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      IssueManagement var5 = var2.getIssueManagement();
      if (var5 != null) {
         IssueManagement var6 = var1.getIssueManagement();
         if (var6 == null) {
            var6 = new IssueManagement();
            var1.setIssueManagement(var6);
         }

         this.mergeIssueManagement(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_Scm(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Scm var5 = var2.getScm();
      if (var5 != null) {
         Scm var6 = var1.getScm();
         if (var6 == null) {
            var6 = new Scm();
            var6.setTag(null);
            var1.setScm(var6);
         }

         this.mergeScm(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_CiManagement(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      CiManagement var5 = var2.getCiManagement();
      if (var5 != null) {
         CiManagement var6 = var1.getCiManagement();
         if (var6 == null) {
            var6 = new CiManagement();
            var1.setCiManagement(var6);
         }

         this.mergeCiManagement(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_Prerequisites(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Prerequisites var5 = var2.getPrerequisites();
      if (var5 != null) {
         Prerequisites var6 = var1.getPrerequisites();
         if (var6 == null) {
            var6 = new Prerequisites();
            var6.setMaven(null);
            var1.setPrerequisites(var6);
         }

         this.mergePrerequisites(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_Build(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Build var5 = var2.getBuild();
      if (var5 != null) {
         Build var6 = var1.getBuild();
         if (var6 == null) {
            var6 = new Build();
            var1.setBuild(var6);
         }

         this.mergeBuild(var6, var5, var3, var4);
      }
   }

   protected void mergeModel_Profiles(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var1.setProfiles(merge(var1.getProfiles(), var2.getProfiles(), var3, new ModelMerger.ProfileKeyComputer()));
   }

   protected void mergeModelBase(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      this.mergeModelBase_DistributionManagement(var1, var2, var3, var4);
      this.mergeModelBase_Modules(var1, var2, var3, var4);
      this.mergeModelBase_Repositories(var1, var2, var3, var4);
      this.mergeModelBase_PluginRepositories(var1, var2, var3, var4);
      this.mergeModelBase_Dependencies(var1, var2, var3, var4);
      this.mergeModelBase_Reporting(var1, var2, var3, var4);
      this.mergeModelBase_DependencyManagement(var1, var2, var3, var4);
      this.mergeModelBase_Properties(var1, var2, var3, var4);
   }

   protected void mergeModelBase_Modules(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getModules();
      if (!var5.isEmpty()) {
         List var6 = var1.getModules();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setModules(var7);
      }
   }

   protected void mergeModelBase_Dependencies(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      var1.setDependencies(merge(var1.getDependencies(), var2.getDependencies(), var3, new ModelMerger.DependencyKeyComputer()));
   }

   protected void mergeModelBase_Repositories(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      var1.setRepositories(merge(var1.getRepositories(), var2.getRepositories(), var3, new ModelMerger.RepositoryKeyComputer()));
   }

   protected void mergeModelBase_PluginRepositories(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      var1.setPluginRepositories(merge(var1.getPluginRepositories(), var2.getPluginRepositories(), var3, new ModelMerger.RepositoryKeyComputer()));
   }

   protected void mergeModelBase_DistributionManagement(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      DistributionManagement var5 = var2.getDistributionManagement();
      if (var5 != null) {
         DistributionManagement var6 = var1.getDistributionManagement();
         if (var6 == null) {
            var6 = new DistributionManagement();
            var1.setDistributionManagement(var6);
         }

         this.mergeDistributionManagement(var6, var5, var3, var4);
      }
   }

   protected void mergeModelBase_Reporting(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      Reporting var5 = var2.getReporting();
      if (var5 != null) {
         Reporting var6 = var1.getReporting();
         if (var6 == null) {
            var6 = new Reporting();
            var1.setReporting(var6);
         }

         this.mergeReporting(var6, var5, var3, var4);
      }
   }

   protected void mergeModelBase_DependencyManagement(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      DependencyManagement var5 = var2.getDependencyManagement();
      if (var5 != null) {
         DependencyManagement var6 = var1.getDependencyManagement();
         if (var6 == null) {
            var6 = new DependencyManagement();
            var1.setDependencyManagement(var6);
         }

         this.mergeDependencyManagement(var6, var5, var3, var4);
      }
   }

   protected void mergeModelBase_Properties(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      Properties var5 = new Properties();
      if (var3) {
         var5.putAll(var1.getProperties());
         var5.putAll(var2.getProperties());
      } else {
         var5.putAll(var2.getProperties());
         var5.putAll(var1.getProperties());
      }

      var1.setProperties(var5);
      var1.setLocation("properties", InputLocation.merge(var1.getLocation("properties"), var2.getLocation("properties"), var3));
   }

   protected void mergeDistributionManagement(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      this.mergeDistributionManagement_Repository(var1, var2, var3, var4);
      this.mergeDistributionManagement_SnapshotRepository(var1, var2, var3, var4);
      this.mergeDistributionManagement_Site(var1, var2, var3, var4);
      this.mergeDistributionManagement_Status(var1, var2, var3, var4);
      this.mergeDistributionManagement_DownloadUrl(var1, var2, var3, var4);
   }

   protected void mergeDistributionManagement_Repository(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      DeploymentRepository var5 = var2.getRepository();
      if (var5 != null) {
         DeploymentRepository var6 = var1.getRepository();
         if (var6 == null) {
            var6 = new DeploymentRepository();
            var1.setRepository(var6);
         }

         this.mergeDeploymentRepository(var6, var5, var3, var4);
      }
   }

   protected void mergeDistributionManagement_SnapshotRepository(
      DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4
   ) {
      DeploymentRepository var5 = var2.getSnapshotRepository();
      if (var5 != null) {
         DeploymentRepository var6 = var1.getSnapshotRepository();
         if (var6 == null) {
            var6 = new DeploymentRepository();
            var1.setSnapshotRepository(var6);
         }

         this.mergeDeploymentRepository(var6, var5, var3, var4);
      }
   }

   protected void mergeDistributionManagement_Site(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      Site var5 = var2.getSite();
      if (var5 != null) {
         Site var6 = var1.getSite();
         if (var6 == null) {
            var6 = new Site();
            var1.setSite(var6);
         }

         this.mergeSite(var6, var5, var3, var4);
      }
   }

   protected void mergeDistributionManagement_Status(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getStatus();
      if (var5 != null && (var3 || var1.getStatus() == null)) {
         var1.setStatus(var5);
         var1.setLocation("status", var2.getLocation("status"));
      }
   }

   protected void mergeDistributionManagement_DownloadUrl(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDownloadUrl();
      if (var5 != null && (var3 || var1.getDownloadUrl() == null)) {
         var1.setDownloadUrl(var5);
         var1.setLocation("downloadUrl", var2.getLocation("downloadUrl"));
      }
   }

   protected void mergeRelocation(Relocation var1, Relocation var2, boolean var3, Map<Object, Object> var4) {
      this.mergeRelocation_GroupId(var1, var2, var3, var4);
      this.mergeRelocation_ArtifactId(var1, var2, var3, var4);
      this.mergeRelocation_Version(var1, var2, var3, var4);
      this.mergeRelocation_Message(var1, var2, var3, var4);
   }

   protected void mergeRelocation_GroupId(Relocation var1, Relocation var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeRelocation_ArtifactId(Relocation var1, Relocation var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeRelocation_Version(Relocation var1, Relocation var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeRelocation_Message(Relocation var1, Relocation var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getMessage();
      if (var5 != null && (var3 || var1.getMessage() == null)) {
         var1.setMessage(var5);
         var1.setLocation("message", var2.getLocation("message"));
      }
   }

   protected void mergeDeploymentRepository(DeploymentRepository var1, DeploymentRepository var2, boolean var3, Map<Object, Object> var4) {
      this.mergeRepository(var1, var2, var3, var4);
      this.mergeDeploymentRepository_UniqueVersion(var1, var2, var3, var4);
   }

   protected void mergeDeploymentRepository_UniqueVersion(DeploymentRepository var1, DeploymentRepository var2, boolean var3, Map<Object, Object> var4) {
      if (var3) {
         var1.setUniqueVersion(var2.isUniqueVersion());
         var1.setLocation("uniqueVersion", var2.getLocation("uniqueVersion"));
      }
   }

   protected void mergeSite(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      this.mergeSite_ChildSiteUrlInheritAppendPath(var1, var2, var3, var4);
      this.mergeSite_Id(var1, var2, var3, var4);
      this.mergeSite_Name(var1, var2, var3, var4);
      this.mergeSite_Url(var1, var2, var3, var4);
   }

   protected void mergeSite_ChildSiteUrlInheritAppendPath(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChildSiteUrlInheritAppendPath();
      if (var5 != null && (var3 || var1.getChildSiteUrlInheritAppendPath() == null)) {
         var1.setChildSiteUrlInheritAppendPath(var5);
         var1.setLocation("child.site.url.inherit.append.path", var2.getLocation("child.site.url.inherit.append.path"));
      }
   }

   protected void mergeSite_Id(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getId();
      if (var5 != null && (var3 || var1.getId() == null)) {
         var1.setId(var5);
         var1.setLocation("id", var2.getLocation("id"));
      }
   }

   protected void mergeSite_Name(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeSite_Url(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeRepository(Repository var1, Repository var2, boolean var3, Map<Object, Object> var4) {
      this.mergeRepositoryBase(var1, var2, var3, var4);
      this.mergeRepository_Releases(var1, var2, var3, var4);
      this.mergeRepository_Snapshots(var1, var2, var3, var4);
   }

   protected void mergeRepository_Releases(Repository var1, Repository var2, boolean var3, Map<Object, Object> var4) {
      RepositoryPolicy var5 = var2.getReleases();
      if (var5 != null) {
         RepositoryPolicy var6 = var1.getReleases();
         if (var6 == null) {
            var6 = new RepositoryPolicy();
            var1.setReleases(var6);
         }

         this.mergeRepositoryPolicy(var6, var5, var3, var4);
      }
   }

   protected void mergeRepository_Snapshots(Repository var1, Repository var2, boolean var3, Map<Object, Object> var4) {
      RepositoryPolicy var5 = var2.getSnapshots();
      if (var5 != null) {
         RepositoryPolicy var6 = var1.getSnapshots();
         if (var6 == null) {
            var6 = new RepositoryPolicy();
            var1.setSnapshots(var6);
         }

         this.mergeRepositoryPolicy(var6, var5, var3, var4);
      }
   }

   protected void mergeRepositoryBase(RepositoryBase var1, RepositoryBase var2, boolean var3, Map<Object, Object> var4) {
      this.mergeRepositoryBase_Id(var1, var2, var3, var4);
      this.mergeRepositoryBase_Name(var1, var2, var3, var4);
      this.mergeRepositoryBase_Url(var1, var2, var3, var4);
      this.mergeRepositoryBase_Layout(var1, var2, var3, var4);
   }

   protected void mergeRepositoryBase_Id(RepositoryBase var1, RepositoryBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getId();
      if (var5 != null && (var3 || var1.getId() == null)) {
         var1.setId(var5);
         var1.setLocation("id", var2.getLocation("id"));
      }
   }

   protected void mergeRepositoryBase_Url(RepositoryBase var1, RepositoryBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeRepositoryBase_Name(RepositoryBase var1, RepositoryBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeRepositoryBase_Layout(RepositoryBase var1, RepositoryBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getLayout();
      if (var5 != null && (var3 || var1.getLayout() == null)) {
         var1.setLayout(var5);
         var1.setLocation("layout", var2.getLocation("layout"));
      }
   }

   protected void mergeRepositoryPolicy(RepositoryPolicy var1, RepositoryPolicy var2, boolean var3, Map<Object, Object> var4) {
      this.mergeRepositoryPolicy_Enabled(var1, var2, var3, var4);
      this.mergeRepositoryPolicy_UpdatePolicy(var1, var2, var3, var4);
      this.mergeRepositoryPolicy_ChecksumPolicy(var1, var2, var3, var4);
   }

   protected void mergeRepositoryPolicy_Enabled(RepositoryPolicy var1, RepositoryPolicy var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getEnabled();
      if (var5 != null && (var3 || var1.getEnabled() == null)) {
         var1.setEnabled(var5);
         var1.setLocation("enabled", var2.getLocation("enabled"));
      }
   }

   protected void mergeRepositoryPolicy_UpdatePolicy(RepositoryPolicy var1, RepositoryPolicy var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUpdatePolicy();
      if (var5 != null && (var3 || var1.getUpdatePolicy() == null)) {
         var1.setUpdatePolicy(var5);
         var1.setLocation("updatePolicy", var2.getLocation("updatePolicy"));
      }
   }

   protected void mergeRepositoryPolicy_ChecksumPolicy(RepositoryPolicy var1, RepositoryPolicy var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChecksumPolicy();
      if (var5 != null && (var3 || var1.getChecksumPolicy() == null)) {
         var1.setChecksumPolicy(var5);
         var1.setLocation("checksumPolicy", var2.getLocation("checksumPolicy"));
      }
   }

   protected void mergeDependency(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      this.mergeDependency_GroupId(var1, var2, var3, var4);
      this.mergeDependency_ArtifactId(var1, var2, var3, var4);
      this.mergeDependency_Version(var1, var2, var3, var4);
      this.mergeDependency_Type(var1, var2, var3, var4);
      this.mergeDependency_Classifier(var1, var2, var3, var4);
      this.mergeDependency_Scope(var1, var2, var3, var4);
      this.mergeDependency_SystemPath(var1, var2, var3, var4);
      this.mergeDependency_Optional(var1, var2, var3, var4);
      this.mergeDependency_Exclusions(var1, var2, var3, var4);
   }

   protected void mergeDependency_GroupId(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeDependency_ArtifactId(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeDependency_Version(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeDependency_Type(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getType();
      if (var5 != null && (var3 || var1.getType() == null)) {
         var1.setType(var5);
         var1.setLocation("type", var2.getLocation("type"));
      }
   }

   protected void mergeDependency_Classifier(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getClassifier();
      if (var5 != null && (var3 || var1.getClassifier() == null)) {
         var1.setClassifier(var5);
         var1.setLocation("classifier", var2.getLocation("classifier"));
      }
   }

   protected void mergeDependency_Scope(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getScope();
      if (var5 != null && (var3 || var1.getScope() == null)) {
         var1.setScope(var5);
         var1.setLocation("scope", var2.getLocation("scope"));
      }
   }

   protected void mergeDependency_SystemPath(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getSystemPath();
      if (var5 != null && (var3 || var1.getSystemPath() == null)) {
         var1.setSystemPath(var5);
         var1.setLocation("systemPath", var2.getLocation("systemPath"));
      }
   }

   protected void mergeDependency_Optional(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getOptional();
      if (var5 != null && (var3 || var1.getOptional() == null)) {
         var1.setOptional(var5);
         var1.setLocation("optional", var2.getLocation("optional"));
      }
   }

   protected void mergeDependency_Exclusions(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      var1.setExclusions(merge(var1.getExclusions(), var2.getExclusions(), var3, new ModelMerger.ExclusionKeyComputer()));
   }

   protected void mergeExclusion(Exclusion var1, Exclusion var2, boolean var3, Map<Object, Object> var4) {
      this.mergeExclusion_GroupId(var1, var2, var3, var4);
      this.mergeExclusion_ArtifactId(var1, var2, var3, var4);
   }

   protected void mergeExclusion_GroupId(Exclusion var1, Exclusion var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeExclusion_ArtifactId(Exclusion var1, Exclusion var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeReporting(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
      this.mergeReporting_OutputDirectory(var1, var2, var3, var4);
      this.mergeReporting_ExcludeDefaults(var1, var2, var3, var4);
      this.mergeReporting_Plugins(var1, var2, var3, var4);
   }

   protected void mergeReporting_OutputDirectory(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getOutputDirectory();
      if (var5 != null && (var3 || var1.getOutputDirectory() == null)) {
         var1.setOutputDirectory(var5);
         var1.setLocation("outputDirectory", var2.getLocation("outputDirectory"));
      }
   }

   protected void mergeReporting_ExcludeDefaults(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getExcludeDefaults();
      if (var5 != null && (var3 || var1.getExcludeDefaults() == null)) {
         var1.setExcludeDefaults(var5);
         var1.setLocation("excludeDefaults", var2.getLocation("excludeDefaults"));
      }
   }

   protected void mergeReporting_Plugins(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
      var1.setPlugins(merge(var1.getPlugins(), var2.getPlugins(), var3, new ModelMerger.ReportPluginKeyComputer()));
   }

   protected void mergeReportPlugin(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      this.mergeConfigurationContainer(var1, var2, var3, var4);
      this.mergeReportPlugin_GroupId(var1, var2, var3, var4);
      this.mergeReportPlugin_ArtifactId(var1, var2, var3, var4);
      this.mergeReportPlugin_Version(var1, var2, var3, var4);
      this.mergeReportPlugin_ReportSets(var1, var2, var3, var4);
   }

   protected void mergeReportPlugin_GroupId(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeReportPlugin_ArtifactId(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeReportPlugin_Version(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeReportPlugin_ReportSets(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      var1.setReportSets(merge(var1.getReportSets(), var2.getReportSets(), var3, new ModelMerger.ReportSetKeyComputer()));
   }

   protected void mergeReportSet(ReportSet var1, ReportSet var2, boolean var3, Map<Object, Object> var4) {
      this.mergeConfigurationContainer(var1, var2, var3, var4);
      this.mergeReportSet_Id(var1, var2, var3, var4);
      this.mergeReportSet_Reports(var1, var2, var3, var4);
   }

   protected void mergeReportSet_Id(ReportSet var1, ReportSet var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getId();
      if (var5 != null && (var3 || var1.getId() == null)) {
         var1.setId(var5);
         var1.setLocation("id", var2.getLocation("id"));
      }
   }

   protected void mergeReportSet_Reports(ReportSet var1, ReportSet var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getReports();
      if (!var5.isEmpty()) {
         List var6 = var1.getReports();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setReports(var7);
         InputLocation var8 = var2.getLocation("reports");
         if (var8 != null) {
            InputLocation var9 = var1.getLocation("reports");
            if (var9 == null) {
               var1.setLocation("reports", var8);
            } else {
               for (int var10 = 0; var10 < var5.size(); var10++) {
                  var9.setLocation(var6.size() + var10, var8.getLocation(var10));
               }
            }
         }
      }
   }

   protected void mergeDependencyManagement(DependencyManagement var1, DependencyManagement var2, boolean var3, Map<Object, Object> var4) {
      this.mergeDependencyManagement_Dependencies(var1, var2, var3, var4);
   }

   protected void mergeDependencyManagement_Dependencies(DependencyManagement var1, DependencyManagement var2, boolean var3, Map<Object, Object> var4) {
      var1.setDependencies(merge(var1.getDependencies(), var2.getDependencies(), var3, new ModelMerger.DependencyKeyComputer()));
   }

   protected void mergeParent(Parent var1, Parent var2, boolean var3, Map<Object, Object> var4) {
      this.mergeParent_GroupId(var1, var2, var3, var4);
      this.mergeParent_ArtifactId(var1, var2, var3, var4);
      this.mergeParent_Version(var1, var2, var3, var4);
      this.mergeParent_RelativePath(var1, var2, var3, var4);
   }

   protected void mergeParent_GroupId(Parent var1, Parent var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeParent_ArtifactId(Parent var1, Parent var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeParent_Version(Parent var1, Parent var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeParent_RelativePath(Parent var1, Parent var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getRelativePath();
      if (var5 != null && (var3 || var1.getRelativePath() == null)) {
         var1.setRelativePath(var5);
         var1.setLocation("relativePath", var2.getLocation("relativePath"));
      }
   }

   protected void mergeOrganization(Organization var1, Organization var2, boolean var3, Map<Object, Object> var4) {
      this.mergeOrganization_Name(var1, var2, var3, var4);
      this.mergeOrganization_Url(var1, var2, var3, var4);
   }

   protected void mergeOrganization_Name(Organization var1, Organization var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeOrganization_Url(Organization var1, Organization var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeLicense(License var1, License var2, boolean var3, Map<Object, Object> var4) {
      this.mergeLicense_Name(var1, var2, var3, var4);
      this.mergeLicense_Url(var1, var2, var3, var4);
      this.mergeLicense_Distribution(var1, var2, var3, var4);
      this.mergeLicense_Comments(var1, var2, var3, var4);
   }

   protected void mergeLicense_Name(License var1, License var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeLicense_Url(License var1, License var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeLicense_Distribution(License var1, License var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDistribution();
      if (var5 != null && (var3 || var1.getDistribution() == null)) {
         var1.setDistribution(var5);
         var1.setLocation("distribution", var2.getLocation("distribution"));
      }
   }

   protected void mergeLicense_Comments(License var1, License var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getComments();
      if (var5 != null && (var3 || var1.getComments() == null)) {
         var1.setComments(var5);
         var1.setLocation("comments", var2.getLocation("comments"));
      }
   }

   protected void mergeMailingList(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      this.mergeMailingList_Name(var1, var2, var3, var4);
      this.mergeMailingList_Subscribe(var1, var2, var3, var4);
      this.mergeMailingList_Unsubscribe(var1, var2, var3, var4);
      this.mergeMailingList_Post(var1, var2, var3, var4);
      this.mergeMailingList_OtherArchives(var1, var2, var3, var4);
   }

   protected void mergeMailingList_Name(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeMailingList_Subscribe(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getSubscribe();
      if (var5 != null && (var3 || var1.getSubscribe() == null)) {
         var1.setSubscribe(var5);
         var1.setLocation("subscribe", var2.getLocation("subscribe"));
      }
   }

   protected void mergeMailingList_Unsubscribe(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUnsubscribe();
      if (var5 != null && (var3 || var1.getUnsubscribe() == null)) {
         var1.setUnsubscribe(var5);
         var1.setLocation("unsubscribe", var2.getLocation("unsubscribe"));
      }
   }

   protected void mergeMailingList_Post(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getPost();
      if (var5 != null && (var3 || var1.getPost() == null)) {
         var1.setPost(var5);
         var1.setLocation("post", var2.getLocation("post"));
      }
   }

   protected void mergeMailingList_Archive(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArchive();
      if (var5 != null && (var3 || var1.getArchive() == null)) {
         var1.setArchive(var5);
         var1.setLocation("archive", var2.getLocation("archive"));
      }
   }

   protected void mergeMailingList_OtherArchives(MailingList var1, MailingList var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getOtherArchives();
      if (!var5.isEmpty()) {
         List var6 = var1.getOtherArchives();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setOtherArchives(var7);
      }
   }

   protected void mergeDeveloper(Developer var1, Developer var2, boolean var3, Map<Object, Object> var4) {
      this.mergeContributor(var1, var2, var3, var4);
      this.mergeDeveloper_Id(var1, var2, var3, var4);
   }

   protected void mergeDeveloper_Id(Developer var1, Developer var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getId();
      if (var5 != null && (var3 || var1.getId() == null)) {
         var1.setId(var5);
         var1.setLocation("id", var2.getLocation("id"));
      }
   }

   protected void mergeContributor(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      this.mergeContributor_Name(var1, var2, var3, var4);
      this.mergeContributor_Email(var1, var2, var3, var4);
      this.mergeContributor_Url(var1, var2, var3, var4);
      this.mergeContributor_Organization(var1, var2, var3, var4);
      this.mergeContributor_OrganizationUrl(var1, var2, var3, var4);
      this.mergeContributor_Timezone(var1, var2, var3, var4);
      this.mergeContributor_Roles(var1, var2, var3, var4);
      this.mergeContributor_Properties(var1, var2, var3, var4);
   }

   protected void mergeContributor_Name(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && (var3 || var1.getName() == null)) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   protected void mergeContributor_Email(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getEmail();
      if (var5 != null && (var3 || var1.getEmail() == null)) {
         var1.setEmail(var5);
         var1.setLocation("email", var2.getLocation("email"));
      }
   }

   protected void mergeContributor_Url(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeContributor_Organization(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getOrganization();
      if (var5 != null && (var3 || var1.getOrganization() == null)) {
         var1.setOrganization(var5);
         var1.setLocation("organization", var2.getLocation("organization"));
      }
   }

   protected void mergeContributor_OrganizationUrl(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getOrganizationUrl();
      if (var5 != null && (var3 || var1.getOrganizationUrl() == null)) {
         var1.setOrganizationUrl(var5);
         var1.setLocation("organizationUrl", var2.getLocation("organizationUrl"));
      }
   }

   protected void mergeContributor_Timezone(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getTimezone();
      if (var5 != null && (var3 || var1.getTimezone() == null)) {
         var1.setTimezone(var5);
         var1.setLocation("timezone", var2.getLocation("timezone"));
      }
   }

   protected void mergeContributor_Roles(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getRoles();
      if (!var5.isEmpty()) {
         List var6 = var1.getRoles();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setRoles(var7);
      }
   }

   protected void mergeContributor_Properties(Contributor var1, Contributor var2, boolean var3, Map<Object, Object> var4) {
      Properties var5 = new Properties();
      if (var3) {
         var5.putAll(var1.getProperties());
         var5.putAll(var2.getProperties());
      } else {
         var5.putAll(var2.getProperties());
         var5.putAll(var1.getProperties());
      }

      var1.setProperties(var5);
      var1.setLocation("properties", InputLocation.merge(var1.getLocation("properties"), var2.getLocation("properties"), var3));
   }

   protected void mergeIssueManagement(IssueManagement var1, IssueManagement var2, boolean var3, Map<Object, Object> var4) {
      this.mergeIssueManagement_Url(var1, var2, var3, var4);
      this.mergeIssueManagement_System(var1, var2, var3, var4);
   }

   protected void mergeIssueManagement_System(IssueManagement var1, IssueManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getSystem();
      if (var5 != null && (var3 || var1.getSystem() == null)) {
         var1.setSystem(var5);
         var1.setLocation("system", var2.getLocation("system"));
      }
   }

   protected void mergeIssueManagement_Url(IssueManagement var1, IssueManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeScm(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      this.mergeScm_ChildScmConnectionInheritAppendPath(var1, var2, var3, var4);
      this.mergeScm_ChildScmDeveloperConnectionInheritAppendPath(var1, var2, var3, var4);
      this.mergeScm_ChildScmUrlInheritAppendPath(var1, var2, var3, var4);
      this.mergeScm_Url(var1, var2, var3, var4);
      this.mergeScm_Connection(var1, var2, var3, var4);
      this.mergeScm_DeveloperConnection(var1, var2, var3, var4);
      this.mergeScm_Tag(var1, var2, var3, var4);
   }

   protected void mergeScm_ChildScmConnectionInheritAppendPath(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChildScmConnectionInheritAppendPath();
      if (var5 != null && (var3 || var1.getChildScmConnectionInheritAppendPath() == null)) {
         var1.setChildScmConnectionInheritAppendPath(var5);
         var1.setLocation("child.scm.connection.inherit.append.path", var2.getLocation("child.scm.connection.inherit.append.path"));
      }
   }

   protected void mergeScm_ChildScmDeveloperConnectionInheritAppendPath(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChildScmDeveloperConnectionInheritAppendPath();
      if (var5 != null && (var3 || var1.getChildScmDeveloperConnectionInheritAppendPath() == null)) {
         var1.setChildScmDeveloperConnectionInheritAppendPath(var5);
         var1.setLocation("child.scm.developerConnection.inherit.append.path", var2.getLocation("child.scm.developerConnection.inherit.append.path"));
      }
   }

   protected void mergeScm_ChildScmUrlInheritAppendPath(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getChildScmUrlInheritAppendPath();
      if (var5 != null && (var3 || var1.getChildScmUrlInheritAppendPath() == null)) {
         var1.setChildScmUrlInheritAppendPath(var5);
         var1.setLocation("child.scm.url.inherit.append.path", var2.getLocation("child.scm.url.inherit.append.path"));
      }
   }

   protected void mergeScm_Url(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeScm_Connection(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getConnection();
      if (var5 != null && (var3 || var1.getConnection() == null)) {
         var1.setConnection(var5);
         var1.setLocation("connection", var2.getLocation("connection"));
      }
   }

   protected void mergeScm_DeveloperConnection(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDeveloperConnection();
      if (var5 != null && (var3 || var1.getDeveloperConnection() == null)) {
         var1.setDeveloperConnection(var5);
         var1.setLocation("developerConnection", var2.getLocation("developerConnection"));
      }
   }

   protected void mergeScm_Tag(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getTag();
      if (var5 != null && (var3 || var1.getTag() == null)) {
         var1.setTag(var5);
         var1.setLocation("tag", var2.getLocation("tag"));
      }
   }

   protected void mergeCiManagement(CiManagement var1, CiManagement var2, boolean var3, Map<Object, Object> var4) {
      this.mergeCiManagement_System(var1, var2, var3, var4);
      this.mergeCiManagement_Url(var1, var2, var3, var4);
      this.mergeCiManagement_Notifiers(var1, var2, var3, var4);
   }

   protected void mergeCiManagement_System(CiManagement var1, CiManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getSystem();
      if (var5 != null && (var3 || var1.getSystem() == null)) {
         var1.setSystem(var5);
         var1.setLocation("system", var2.getLocation("system"));
      }
   }

   protected void mergeCiManagement_Url(CiManagement var1, CiManagement var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null && (var3 || var1.getUrl() == null)) {
         var1.setUrl(var5);
         var1.setLocation("url", var2.getLocation("url"));
      }
   }

   protected void mergeCiManagement_Notifiers(CiManagement var1, CiManagement var2, boolean var3, Map<Object, Object> var4) {
      var1.setNotifiers(merge(var1.getNotifiers(), var2.getNotifiers(), var3, new ModelMerger.NotifierKeyComputer()));
   }

   protected void mergeNotifier(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      this.mergeNotifier_Type(var1, var2, var3, var4);
      this.mergeNotifier_Address(var1, var2, var3, var4);
      this.mergeNotifier_Configuration(var1, var2, var3, var4);
      this.mergeNotifier_SendOnError(var1, var2, var3, var4);
      this.mergeNotifier_SendOnFailure(var1, var2, var3, var4);
      this.mergeNotifier_SendOnSuccess(var1, var2, var3, var4);
      this.mergeNotifier_SendOnWarning(var1, var2, var3, var4);
   }

   protected void mergeNotifier_Type(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getType();
      if (var5 != null && (var3 || var1.getType() == null)) {
         var1.setType(var5);
      }
   }

   protected void mergeNotifier_Address(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getAddress();
      if (var5 != null && (var3 || var1.getAddress() == null)) {
         var1.setAddress(var5);
      }
   }

   protected void mergeNotifier_Configuration(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      Properties var5 = new Properties();
      if (var3) {
         var5.putAll(var1.getConfiguration());
         var5.putAll(var2.getConfiguration());
      } else {
         var5.putAll(var2.getConfiguration());
         var5.putAll(var1.getConfiguration());
      }

      var1.setConfiguration(var5);
   }

   protected void mergeNotifier_SendOnError(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      if (var3) {
         var1.setSendOnError(var2.isSendOnError());
      }
   }

   protected void mergeNotifier_SendOnFailure(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      if (var3) {
         var1.setSendOnFailure(var2.isSendOnFailure());
      }
   }

   protected void mergeNotifier_SendOnSuccess(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      if (var3) {
         var1.setSendOnSuccess(var2.isSendOnSuccess());
      }
   }

   protected void mergeNotifier_SendOnWarning(Notifier var1, Notifier var2, boolean var3, Map<Object, Object> var4) {
      if (var3) {
         var1.setSendOnWarning(var2.isSendOnWarning());
      }
   }

   protected void mergePrerequisites(Prerequisites var1, Prerequisites var2, boolean var3, Map<Object, Object> var4) {
      this.mergePrerequisites_Maven(var1, var2, var3, var4);
   }

   protected void mergePrerequisites_Maven(Prerequisites var1, Prerequisites var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getMaven();
      if (var5 != null && (var3 || var1.getMaven() == null)) {
         var1.setMaven(var5);
         var1.setLocation("maven", var2.getLocation("maven"));
      }
   }

   protected void mergeBuild(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      this.mergeBuildBase(var1, var2, var3, var4);
      this.mergeBuild_SourceDirectory(var1, var2, var3, var4);
      this.mergeBuild_ScriptSourceDirectory(var1, var2, var3, var4);
      this.mergeBuild_TestSourceDirectory(var1, var2, var3, var4);
      this.mergeBuild_OutputDirectory(var1, var2, var3, var4);
      this.mergeBuild_TestOutputDirectory(var1, var2, var3, var4);
      this.mergeBuild_Extensions(var1, var2, var3, var4);
   }

   protected void mergeBuild_SourceDirectory(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getSourceDirectory();
      if (var5 != null && (var3 || var1.getSourceDirectory() == null)) {
         var1.setSourceDirectory(var5);
         var1.setLocation("sourceDirectory", var2.getLocation("sourceDirectory"));
      }
   }

   protected void mergeBuild_ScriptSourceDirectory(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getScriptSourceDirectory();
      if (var5 != null && (var3 || var1.getScriptSourceDirectory() == null)) {
         var1.setScriptSourceDirectory(var5);
         var1.setLocation("scriptSourceDirectory", var2.getLocation("scriptSourceDirectory"));
      }
   }

   protected void mergeBuild_TestSourceDirectory(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getTestSourceDirectory();
      if (var5 != null && (var3 || var1.getTestSourceDirectory() == null)) {
         var1.setTestSourceDirectory(var5);
         var1.setLocation("testSourceDirectory", var2.getLocation("testSourceDirectory"));
      }
   }

   protected void mergeBuild_OutputDirectory(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getOutputDirectory();
      if (var5 != null && (var3 || var1.getOutputDirectory() == null)) {
         var1.setOutputDirectory(var5);
         var1.setLocation("outputDirectory", var2.getLocation("outputDirectory"));
      }
   }

   protected void mergeBuild_TestOutputDirectory(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getTestOutputDirectory();
      if (var5 != null && (var3 || var1.getTestOutputDirectory() == null)) {
         var1.setTestOutputDirectory(var5);
         var1.setLocation("testOutputDirectory", var2.getLocation("testOutputDirectory"));
      }
   }

   protected void mergeBuild_Extensions(Build var1, Build var2, boolean var3, Map<Object, Object> var4) {
      var1.setExtensions(merge(var1.getExtensions(), var2.getExtensions(), var3, new ModelMerger.ExtensionKeyComputer()));
   }

   protected void mergeExtension(Extension var1, Extension var2, boolean var3, Map<Object, Object> var4) {
      this.mergeExtension_GroupId(var1, var2, var3, var4);
      this.mergeExtension_ArtifactId(var1, var2, var3, var4);
      this.mergeExtension_Version(var1, var2, var3, var4);
   }

   protected void mergeExtension_GroupId(Extension var1, Extension var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergeExtension_ArtifactId(Extension var1, Extension var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergeExtension_Version(Extension var1, Extension var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergeBuildBase(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      this.mergePluginConfiguration(var1, var2, var3, var4);
      this.mergeBuildBase_DefaultGoal(var1, var2, var3, var4);
      this.mergeBuildBase_FinalName(var1, var2, var3, var4);
      this.mergeBuildBase_Directory(var1, var2, var3, var4);
      this.mergeBuildBase_Resources(var1, var2, var3, var4);
      this.mergeBuildBase_TestResources(var1, var2, var3, var4);
      this.mergeBuildBase_Filters(var1, var2, var3, var4);
   }

   protected void mergeBuildBase_DefaultGoal(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDefaultGoal();
      if (var5 != null && (var3 || var1.getDefaultGoal() == null)) {
         var1.setDefaultGoal(var5);
         var1.setLocation("defaultGoal", var2.getLocation("defaultGoal"));
      }
   }

   protected void mergeBuildBase_Directory(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDirectory();
      if (var5 != null && (var3 || var1.getDirectory() == null)) {
         var1.setDirectory(var5);
         var1.setLocation("directory", var2.getLocation("directory"));
      }
   }

   protected void mergeBuildBase_FinalName(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getFinalName();
      if (var5 != null && (var3 || var1.getFinalName() == null)) {
         var1.setFinalName(var5);
         var1.setLocation("finalName", var2.getLocation("finalName"));
      }
   }

   protected void mergeBuildBase_Filters(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getFilters();
      if (!var5.isEmpty()) {
         List var6 = var1.getFilters();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setFilters(var7);
      }
   }

   protected void mergeBuildBase_Resources(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      var1.setResources(merge(var1.getResources(), var2.getResources(), var3, new ModelMerger.ResourceKeyComputer()));
   }

   protected void mergeBuildBase_TestResources(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      var1.setTestResources(merge(var1.getTestResources(), var2.getTestResources(), var3, new ModelMerger.ResourceKeyComputer()));
   }

   protected void mergePluginConfiguration(PluginConfiguration var1, PluginConfiguration var2, boolean var3, Map<Object, Object> var4) {
      this.mergePluginContainer(var1, var2, var3, var4);
      this.mergePluginConfiguration_PluginManagement(var1, var2, var3, var4);
   }

   protected void mergePluginConfiguration_PluginManagement(PluginConfiguration var1, PluginConfiguration var2, boolean var3, Map<Object, Object> var4) {
      PluginManagement var5 = var2.getPluginManagement();
      if (var5 != null) {
         PluginManagement var6 = var1.getPluginManagement();
         if (var6 == null) {
            var6 = new PluginManagement();
            var1.setPluginManagement(var6);
         }

         this.mergePluginManagement(var6, var5, var3, var4);
      }
   }

   protected void mergePluginContainer(PluginContainer var1, PluginContainer var2, boolean var3, Map<Object, Object> var4) {
      this.mergePluginContainer_Plugins(var1, var2, var3, var4);
   }

   protected void mergePluginContainer_Plugins(PluginContainer var1, PluginContainer var2, boolean var3, Map<Object, Object> var4) {
      var1.setPlugins(merge(var1.getPlugins(), var2.getPlugins(), var3, new ModelMerger.PluginKeyComputer()));
   }

   protected void mergePluginManagement(PluginManagement var1, PluginManagement var2, boolean var3, Map<Object, Object> var4) {
      this.mergePluginContainer(var1, var2, var3, var4);
   }

   protected void mergePlugin(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      this.mergeConfigurationContainer(var1, var2, var3, var4);
      this.mergePlugin_GroupId(var1, var2, var3, var4);
      this.mergePlugin_ArtifactId(var1, var2, var3, var4);
      this.mergePlugin_Version(var1, var2, var3, var4);
      this.mergePlugin_Extensions(var1, var2, var3, var4);
      this.mergePlugin_Dependencies(var1, var2, var3, var4);
      this.mergePlugin_Executions(var1, var2, var3, var4);
   }

   protected void mergePlugin_GroupId(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getGroupId();
      if (var5 != null && (var3 || var1.getGroupId() == null)) {
         var1.setGroupId(var5);
         var1.setLocation("groupId", var2.getLocation("groupId"));
      }
   }

   protected void mergePlugin_ArtifactId(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getArtifactId();
      if (var5 != null && (var3 || var1.getArtifactId() == null)) {
         var1.setArtifactId(var5);
         var1.setLocation("artifactId", var2.getLocation("artifactId"));
      }
   }

   protected void mergePlugin_Version(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getVersion();
      if (var5 != null && (var3 || var1.getVersion() == null)) {
         var1.setVersion(var5);
         var1.setLocation("version", var2.getLocation("version"));
      }
   }

   protected void mergePlugin_Extensions(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getExtensions();
      if (var5 != null && (var3 || var1.getExtensions() == null)) {
         var1.setExtensions(var5);
         var1.setLocation("extensions", var2.getLocation("extensions"));
      }
   }

   protected void mergePlugin_Dependencies(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      var1.setDependencies(merge(var1.getDependencies(), var2.getDependencies(), var3, new ModelMerger.DependencyKeyComputer()));
   }

   protected void mergePlugin_Executions(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      var1.setExecutions(merge(var1.getExecutions(), var2.getExecutions(), var3, new ModelMerger.ExecutionKeyComputer()));
   }

   protected void mergeConfigurationContainer(ConfigurationContainer var1, ConfigurationContainer var2, boolean var3, Map<Object, Object> var4) {
      this.mergeConfigurationContainer_Inherited(var1, var2, var3, var4);
      this.mergeConfigurationContainer_Configuration(var1, var2, var3, var4);
   }

   protected void mergeConfigurationContainer_Inherited(ConfigurationContainer var1, ConfigurationContainer var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getInherited();
      if (var5 != null && (var3 || var1.getInherited() == null)) {
         var1.setInherited(var5);
         var1.setLocation("inherited", var2.getLocation("inherited"));
      }
   }

   protected void mergeConfigurationContainer_Configuration(ConfigurationContainer var1, ConfigurationContainer var2, boolean var3, Map<Object, Object> var4) {
      Xpp3Dom var5 = (Xpp3Dom)var2.getConfiguration();
      if (var5 != null) {
         Xpp3Dom var6 = (Xpp3Dom)var1.getConfiguration();
         if (!var3 && var6 != null) {
            var6 = Xpp3Dom.mergeXpp3Dom(var6, var5);
         } else {
            var6 = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(var5), var6);
         }

         var1.setConfiguration(var6);
      }
   }

   protected void mergePluginExecution(PluginExecution var1, PluginExecution var2, boolean var3, Map<Object, Object> var4) {
      this.mergeConfigurationContainer(var1, var2, var3, var4);
      this.mergePluginExecution_Id(var1, var2, var3, var4);
      this.mergePluginExecution_Phase(var1, var2, var3, var4);
      this.mergePluginExecution_Goals(var1, var2, var3, var4);
   }

   protected void mergePluginExecution_Id(PluginExecution var1, PluginExecution var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getId();
      if (var5 != null && (var3 || var1.getId() == null)) {
         var1.setId(var5);
         var1.setLocation("id", var2.getLocation("id"));
      }
   }

   protected void mergePluginExecution_Phase(PluginExecution var1, PluginExecution var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getPhase();
      if (var5 != null && (var3 || var1.getPhase() == null)) {
         var1.setPhase(var5);
         var1.setLocation("phase", var2.getLocation("phase"));
      }
   }

   protected void mergePluginExecution_Goals(PluginExecution var1, PluginExecution var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getGoals();
      if (!var5.isEmpty()) {
         List var6 = var1.getGoals();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setGoals(var7);
      }
   }

   protected void mergeResource(Resource var1, Resource var2, boolean var3, Map<Object, Object> var4) {
      this.mergeFileSet(var1, var2, var3, var4);
      this.mergeResource_TargetPath(var1, var2, var3, var4);
      this.mergeResource_Filtering(var1, var2, var3, var4);
      this.mergeResource_MergeId(var1, var2, var3, var4);
   }

   protected void mergeResource_TargetPath(Resource var1, Resource var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getTargetPath();
      if (var5 != null && (var3 || var1.getTargetPath() == null)) {
         var1.setTargetPath(var5);
         var1.setLocation("targetPath", var2.getLocation("targetPath"));
      }
   }

   protected void mergeResource_Filtering(Resource var1, Resource var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getFiltering();
      if (var5 != null && (var3 || var1.getFiltering() == null)) {
         var1.setFiltering(var5);
         var1.setLocation("filtering", var2.getLocation("filtering"));
      }
   }

   protected void mergeResource_MergeId(Resource var1, Resource var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getMergeId();
      if (var5 != null && (var3 || var1.getMergeId() == null)) {
         var1.setMergeId(var5);
      }
   }

   protected void mergeFileSet(FileSet var1, FileSet var2, boolean var3, Map<Object, Object> var4) {
      this.mergePatternSet(var1, var2, var3, var4);
      this.mergeFileSet_Directory(var1, var2, var3, var4);
   }

   protected void mergeFileSet_Directory(FileSet var1, FileSet var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDirectory();
      if (var5 != null && (var3 || var1.getDirectory() == null)) {
         var1.setDirectory(var5);
         var1.setLocation("directory", var2.getLocation("directory"));
      }
   }

   protected void mergePatternSet(PatternSet var1, PatternSet var2, boolean var3, Map<Object, Object> var4) {
      this.mergePatternSet_Includes(var1, var2, var3, var4);
      this.mergePatternSet_Excludes(var1, var2, var3, var4);
   }

   protected void mergePatternSet_Includes(PatternSet var1, PatternSet var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getIncludes();
      if (!var5.isEmpty()) {
         List var6 = var1.getIncludes();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setIncludes(var7);
      }
   }

   protected void mergePatternSet_Excludes(PatternSet var1, PatternSet var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getExcludes();
      if (!var5.isEmpty()) {
         List var6 = var1.getExcludes();
         ArrayList var7 = new ArrayList(var6.size() + var5.size());
         var7.addAll(var6);
         var7.addAll(var5);
         var1.setExcludes(var7);
      }
   }

   protected void mergeProfile(Profile var1, Profile var2, boolean var3, Map<Object, Object> var4) {
      this.mergeModelBase(var1, var2, var3, var4);
   }

   protected void mergeActivation(Activation var1, Activation var2, boolean var3, Map<Object, Object> var4) {
   }

   protected Object getDependencyKey(Dependency var1) {
      return var1;
   }

   protected Object getPluginKey(Plugin var1) {
      return var1;
   }

   protected Object getPluginExecutionKey(PluginExecution var1) {
      return var1;
   }

   protected Object getReportPluginKey(ReportPlugin var1) {
      return var1;
   }

   protected Object getReportSetKey(ReportSet var1) {
      return var1;
   }

   protected Object getLicenseKey(License var1) {
      return var1;
   }

   protected Object getMailingListKey(MailingList var1) {
      return var1;
   }

   protected Object getDeveloperKey(Developer var1) {
      return var1;
   }

   protected Object getContributorKey(Contributor var1) {
      return var1;
   }

   protected Object getProfileKey(Profile var1) {
      return var1;
   }

   protected Object getRepositoryKey(Repository var1) {
      return this.getRepositoryBaseKey(var1);
   }

   protected Object getRepositoryBaseKey(RepositoryBase var1) {
      return var1;
   }

   protected Object getNotifierKey(Notifier var1) {
      return var1;
   }

   protected Object getResourceKey(Resource var1) {
      return var1;
   }

   protected Object getExtensionKey(Extension var1) {
      return var1;
   }

   protected Object getExclusionKey(Exclusion var1) {
      return var1;
   }

   private static <T> List<T> merge(List<T> var0, List<T> var1, boolean var2, ModelMerger.KeyComputer<T> var3) {
      return merge(var0, var1, var3, new ModelMerger.SourceDominant<>(var2));
   }

   private static <T> List<T> merge(List<T> var0, List<T> var1, ModelMerger.KeyComputer<T> var2, ModelMerger.Remapping<T> var3) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         ModelMerger.MergingList var4;
         if (var0 instanceof ModelMerger.MergingList) {
            var4 = (ModelMerger.MergingList)var0;
         } else {
            var4 = new ModelMerger.MergingList(var2, var1.size() + var0.size());
            var4.mergeAll(var0, new ModelMerger.SourceDominant<>(true));
         }

         var4.mergeAll(var1, var3);
         return var4;
      }
   }

   private class ContributorKeyComputer implements ModelMerger.KeyComputer<Contributor> {
      private ContributorKeyComputer() {
      }

      public Object key(Contributor var1) {
         return ModelMerger.this.getContributorKey(var1);
      }
   }

   private final class DependencyKeyComputer implements ModelMerger.KeyComputer<Dependency> {
      private DependencyKeyComputer() {
      }

      public Object key(Dependency var1) {
         return ModelMerger.this.getDependencyKey(var1);
      }
   }

   private class DeveloperKeyComputer implements ModelMerger.KeyComputer<Developer> {
      private DeveloperKeyComputer() {
      }

      public Object key(Developer var1) {
         return ModelMerger.this.getDeveloperKey(var1);
      }
   }

   private class ExclusionKeyComputer implements ModelMerger.KeyComputer<Exclusion> {
      private ExclusionKeyComputer() {
      }

      public Object key(Exclusion var1) {
         return ModelMerger.this.getExclusionKey(var1);
      }
   }

   private class ExecutionKeyComputer implements ModelMerger.KeyComputer<PluginExecution> {
      private ExecutionKeyComputer() {
      }

      public Object key(PluginExecution var1) {
         return ModelMerger.this.getPluginExecutionKey(var1);
      }
   }

   private class ExtensionKeyComputer implements ModelMerger.KeyComputer<Extension> {
      private ExtensionKeyComputer() {
      }

      public Object key(Extension var1) {
         return ModelMerger.this.getExtensionKey(var1);
      }
   }

   private interface KeyComputer<T> {
      Object key(T var1);
   }

   private class LicenseKeyComputer implements ModelMerger.KeyComputer<License> {
      private LicenseKeyComputer() {
      }

      public Object key(License var1) {
         return ModelMerger.this.getLicenseKey(var1);
      }
   }

   private class MailingListKeyComputer implements ModelMerger.KeyComputer<MailingList> {
      private MailingListKeyComputer() {
      }

      public Object key(MailingList var1) {
         return ModelMerger.this.getMailingListKey(var1);
      }
   }

   private static class MergingList<V> extends AbstractList<V> implements Serializable {
      private final ModelMerger.KeyComputer<V> keyComputer;
      private Map<Object, V> map;
      private List<V> list;

      MergingList(ModelMerger.KeyComputer<V> var1, int var2) {
         this.map = new LinkedHashMap<>(var2);
         this.keyComputer = var1;
      }

      Object writeReplace() throws ObjectStreamException {
         return new ArrayList<>(this);
      }

      @Override
      public Iterator<V> iterator() {
         return this.map != null ? this.map.values().iterator() : this.list.iterator();
      }

      void mergeAll(Collection<V> var1, ModelMerger.Remapping<V> var2) {
         if (this.map == null) {
            this.map = new LinkedHashMap<>(this.list.size() + var1.size());

            for (Object var4 : this.list) {
               this.map.put(this.keyComputer.key((V)var4), (V)var4);
            }

            this.list = null;
         }

         if (var1 instanceof ModelMerger.MergingList && ((ModelMerger.MergingList)var1).map != null) {
            for (Entry var11 : ((ModelMerger.MergingList)var1).map.entrySet()) {
               Object var12 = var11.getKey();
               Object var13 = this.map.get(var12);
               Object var14 = var13 == null ? var11.getValue() : var2.merge(var13, var11.getValue());
               if (var14 == null) {
                  this.remove(var12);
               } else if (var14 != var13) {
                  this.map.put(var12, (V)var14);
               }
            }
         } else {
            for (Object var10 : var1) {
               Object var5 = this.keyComputer.key((V)var10);
               Object var6 = this.map.get(var5);
               Object var7 = var6 == null ? var10 : var2.merge(var6, var10);
               if (var7 == null) {
                  this.remove(var5);
               } else {
                  this.map.put(var5, (V)var7);
               }
            }
         }
      }

      @Override
      public boolean contains(Object var1) {
         return this.map != null ? this.map.containsValue(var1) : this.list.contains(var1);
      }

      private List<V> asList() {
         if (this.list == null) {
            this.list = new ArrayList<>(this.map.values());
            this.map = null;
         }

         return this.list;
      }

      @Override
      public void add(int var1, V var2) {
         this.asList().add(var1, (V)var2);
      }

      @Override
      public V remove(int var1) {
         return this.asList().remove(var1);
      }

      @Override
      public V get(int var1) {
         return this.asList().get(var1);
      }

      @Override
      public int size() {
         return this.map != null ? this.map.size() : this.list.size();
      }
   }

   private class NotifierKeyComputer implements ModelMerger.KeyComputer<Notifier> {
      private NotifierKeyComputer() {
      }

      public Object key(Notifier var1) {
         return ModelMerger.this.getNotifierKey(var1);
      }
   }

   private class PluginKeyComputer implements ModelMerger.KeyComputer<Plugin> {
      private PluginKeyComputer() {
      }

      public Object key(Plugin var1) {
         return ModelMerger.this.getPluginKey(var1);
      }
   }

   private class ProfileKeyComputer implements ModelMerger.KeyComputer<Profile> {
      private ProfileKeyComputer() {
      }

      public Object key(Profile var1) {
         return ModelMerger.this.getProfileKey(var1);
      }
   }

   private interface Remapping<T> {
      T merge(T var1, T var2);
   }

   private class ReportPluginKeyComputer implements ModelMerger.KeyComputer<ReportPlugin> {
      private ReportPluginKeyComputer() {
      }

      public Object key(ReportPlugin var1) {
         return ModelMerger.this.getReportPluginKey(var1);
      }
   }

   private class ReportSetKeyComputer implements ModelMerger.KeyComputer<ReportSet> {
      private ReportSetKeyComputer() {
      }

      public Object key(ReportSet var1) {
         return ModelMerger.this.getReportSetKey(var1);
      }
   }

   private class RepositoryKeyComputer implements ModelMerger.KeyComputer<Repository> {
      private RepositoryKeyComputer() {
      }

      public Object key(Repository var1) {
         return ModelMerger.this.getRepositoryKey(var1);
      }
   }

   private class ResourceKeyComputer implements ModelMerger.KeyComputer<Resource> {
      private ResourceKeyComputer() {
      }

      public Object key(Resource var1) {
         return ModelMerger.this.getResourceKey(var1);
      }
   }

   private static class SourceDominant<T> implements ModelMerger.Remapping<T> {
      private final boolean sourceDominant;

      SourceDominant(boolean var1) {
         this.sourceDominant = var1;
      }

      @Override
      public T merge(T var1, T var2) {
         return (T)(this.sourceDominant ? var2 : var1);
      }
   }
}
