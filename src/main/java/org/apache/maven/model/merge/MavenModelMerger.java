package org.apache.maven.model.merge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.StringUtils;

public class MavenModelMerger extends ModelMerger {
   public static final String CHILD_PATH_ADJUSTMENT = "child-path-adjustment";
   public static final String ARTIFACT_ID = "artifact-id";

   @Override
   protected void mergeModel(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      var4.put("artifact-id", var1.getArtifactId());
      super.mergeModel(var1, var2, var3, var4);
   }

   @Override
   protected void mergeModel_Name(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getName();
      if (var5 != null && var3) {
         var1.setName(var5);
         var1.setLocation("name", var2.getLocation("name"));
      }
   }

   @Override
   protected void mergeModel_Url(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null) {
         if (var3) {
            var1.setUrl(var5);
            var1.setLocation("url", var2.getLocation("url"));
         } else if (var1.getUrl() == null) {
            var1.setUrl(this.extrapolateChildUrl(var5, var2.isChildProjectUrlInheritAppendPath(), var4));
            var1.setLocation("url", var2.getLocation("url"));
         }
      }
   }

   @Override
   protected void mergeModel_Organization(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      Organization var5 = var2.getOrganization();
      if (var5 != null) {
         Organization var6 = var1.getOrganization();
         if (var6 == null) {
            var6 = new Organization();
            var6.setLocation("", var5.getLocation(""));
            var1.setOrganization(var6);
            this.mergeOrganization(var6, var5, var3, var4);
         }
      }
   }

   @Override
   protected void mergeModel_IssueManagement(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      IssueManagement var5 = var2.getIssueManagement();
      if (var5 != null) {
         IssueManagement var6 = var1.getIssueManagement();
         if (var6 == null) {
            var6 = new IssueManagement();
            var6.setLocation("", var5.getLocation(""));
            var1.setIssueManagement(var6);
            this.mergeIssueManagement(var6, var5, var3, var4);
         }
      }
   }

   @Override
   protected void mergeModel_CiManagement(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      CiManagement var5 = var2.getCiManagement();
      if (var5 != null) {
         CiManagement var6 = var1.getCiManagement();
         if (var6 == null) {
            var6 = new CiManagement();
            var6.setLocation("", var5.getLocation(""));
            var1.setCiManagement(var6);
            this.mergeCiManagement(var6, var5, var3, var4);
         }
      }
   }

   @Override
   protected void mergeModel_ModelVersion(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
   }

   @Override
   protected void mergeModel_ArtifactId(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
   }

   @Override
   protected void mergeModel_Profiles(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
   }

   @Override
   protected void mergeModel_Prerequisites(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
   }

   @Override
   protected void mergeModel_Licenses(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      if (var1.getLicenses().isEmpty()) {
         var1.setLicenses(new ArrayList<>(var2.getLicenses()));
      }
   }

   @Override
   protected void mergeModel_Developers(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      if (var1.getDevelopers().isEmpty()) {
         var1.setDevelopers(new ArrayList<>(var2.getDevelopers()));
      }
   }

   @Override
   protected void mergeModel_Contributors(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      if (var1.getContributors().isEmpty()) {
         var1.setContributors(new ArrayList<>(var2.getContributors()));
      }
   }

   @Override
   protected void mergeModel_MailingLists(Model var1, Model var2, boolean var3, Map<Object, Object> var4) {
      if (var1.getMailingLists().isEmpty()) {
         var1.setMailingLists(new ArrayList<>(var2.getMailingLists()));
      }
   }

   @Override
   protected void mergeModelBase_Modules(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getModules();
      if (!var5.isEmpty() && var3) {
         ArrayList var6 = new ArrayList();
         List var7 = var1.getModules();
         LinkedHashSet var8 = new LinkedHashSet(var7);
         ArrayList var9 = new ArrayList(var7.size() + var5.size());
         var9.addAll(var7);
         int var10 = 0;

         for (int var11 = var7.size(); var10 < var11; var10++) {
            var6.add(var10);
         }

         var10 = 0;

         for (int var14 = var5.size(); var10 < var14; var10++) {
            String var12 = (String)var5.get(var10);
            if (!var8.contains(var12)) {
               var9.add(var12);
               var6.add(~var10);
            }
         }

         var1.setModules(var9);
         var1.setLocation("modules", InputLocation.merge(var1.getLocation("modules"), var2.getLocation("modules"), var6));
      }
   }

   @Override
   protected void mergeModelBase_Repositories(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getRepositories();
      if (!var5.isEmpty()) {
         List var6 = var1.getRepositories();
         LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);
         List var8;
         List var9;
         if (var3) {
            var8 = var5;
            var9 = var6;
         } else {
            var8 = var6;
            var9 = var5;
         }

         for (Repository var11 : var8) {
            Object var12 = this.getRepositoryKey(var11);
            var7.put(var12, var11);
         }

         for (Repository var14 : var9) {
            Object var15 = this.getRepositoryKey(var14);
            if (!var7.containsKey(var15)) {
               var7.put(var15, var14);
            }
         }

         var1.setRepositories(new ArrayList<>(var7.values()));
      }
   }

   @Override
   protected void mergeModelBase_PluginRepositories(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getPluginRepositories();
      if (!var5.isEmpty()) {
         List var6 = var1.getPluginRepositories();
         LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);
         List var8;
         List var9;
         if (var3) {
            var8 = var5;
            var9 = var6;
         } else {
            var8 = var6;
            var9 = var5;
         }

         for (Repository var11 : var8) {
            Object var12 = this.getRepositoryKey(var11);
            var7.put(var12, var11);
         }

         for (Repository var14 : var9) {
            Object var15 = this.getRepositoryKey(var14);
            if (!var7.containsKey(var15)) {
               var7.put(var15, var14);
            }
         }

         var1.setPluginRepositories(new ArrayList<>(var7.values()));
      }
   }

   @Override
   protected void mergeBuildBase_Filters(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getFilters();
      if (!var5.isEmpty()) {
         List var6 = var1.getFilters();
         LinkedHashSet var7 = new LinkedHashSet(var6);
         ArrayList var8 = new ArrayList(var6.size() + var5.size());
         var8.addAll(var6);

         for (String var10 : var5) {
            if (!var7.contains(var10)) {
               var8.add(var10);
            }
         }

         var1.setFilters(var8);
      }
   }

   @Override
   protected void mergeBuildBase_Resources(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      if (var3 || var1.getResources().isEmpty()) {
         super.mergeBuildBase_Resources(var1, var2, var3, var4);
      }
   }

   @Override
   protected void mergeBuildBase_TestResources(BuildBase var1, BuildBase var2, boolean var3, Map<Object, Object> var4) {
      if (var3 || var1.getTestResources().isEmpty()) {
         super.mergeBuildBase_TestResources(var1, var2, var3, var4);
      }
   }

   @Override
   protected void mergeDistributionManagement_Repository(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      DeploymentRepository var5 = var2.getRepository();
      if (var5 != null) {
         DeploymentRepository var6 = var1.getRepository();
         if (var3 || var6 == null) {
            var6 = new DeploymentRepository();
            var6.setLocation("", var5.getLocation(""));
            var1.setRepository(var6);
            this.mergeDeploymentRepository(var6, var5, var3, var4);
         }
      }
   }

   @Override
   protected void mergeDistributionManagement_SnapshotRepository(
      DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4
   ) {
      DeploymentRepository var5 = var2.getSnapshotRepository();
      if (var5 != null) {
         DeploymentRepository var6 = var1.getSnapshotRepository();
         if (var3 || var6 == null) {
            var6 = new DeploymentRepository();
            var6.setLocation("", var5.getLocation(""));
            var1.setSnapshotRepository(var6);
            this.mergeDeploymentRepository(var6, var5, var3, var4);
         }
      }
   }

   @Override
   protected void mergeDistributionManagement_Site(DistributionManagement var1, DistributionManagement var2, boolean var3, Map<Object, Object> var4) {
      Site var5 = var2.getSite();
      if (var5 != null) {
         Site var6 = var1.getSite();
         if (var3 || var6 == null || this.isSiteEmpty(var6)) {
            if (var6 == null) {
               var6 = new Site();
            }

            var6.setLocation("", var5.getLocation(""));
            var1.setSite(var6);
            this.mergeSite(var6, var5, var3, var4);
         }

         this.mergeSite_ChildSiteUrlInheritAppendPath(var6, var5, var3, var4);
      }
   }

   @Override
   protected void mergeSite(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      this.mergeSite_Id(var1, var2, var3, var4);
      this.mergeSite_Name(var1, var2, var3, var4);
      this.mergeSite_Url(var1, var2, var3, var4);
   }

   protected boolean isSiteEmpty(Site var1) {
      return StringUtils.isEmpty(var1.getId()) && StringUtils.isEmpty(var1.getName()) && StringUtils.isEmpty(var1.getUrl());
   }

   @Override
   protected void mergeSite_Url(Site var1, Site var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null) {
         if (var3) {
            var1.setUrl(var5);
            var1.setLocation("url", var2.getLocation("url"));
         } else if (var1.getUrl() == null) {
            var1.setUrl(this.extrapolateChildUrl(var5, var2.isChildSiteUrlInheritAppendPath(), var4));
            var1.setLocation("url", var2.getLocation("url"));
         }
      }
   }

   @Override
   protected void mergeScm_Url(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getUrl();
      if (var5 != null) {
         if (var3) {
            var1.setUrl(var5);
            var1.setLocation("url", var2.getLocation("url"));
         } else if (var1.getUrl() == null) {
            var1.setUrl(this.extrapolateChildUrl(var5, var2.isChildScmUrlInheritAppendPath(), var4));
            var1.setLocation("url", var2.getLocation("url"));
         }
      }
   }

   @Override
   protected void mergeScm_Connection(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getConnection();
      if (var5 != null) {
         if (var3) {
            var1.setConnection(var5);
            var1.setLocation("connection", var2.getLocation("connection"));
         } else if (var1.getConnection() == null) {
            var1.setConnection(this.extrapolateChildUrl(var5, var2.isChildScmConnectionInheritAppendPath(), var4));
            var1.setLocation("connection", var2.getLocation("connection"));
         }
      }
   }

   @Override
   protected void mergeScm_DeveloperConnection(Scm var1, Scm var2, boolean var3, Map<Object, Object> var4) {
      String var5 = var2.getDeveloperConnection();
      if (var5 != null) {
         if (var3) {
            var1.setDeveloperConnection(var5);
            var1.setLocation("developerConnection", var2.getLocation("developerConnection"));
         } else if (var1.getDeveloperConnection() == null) {
            String var6 = this.extrapolateChildUrl(var5, var2.isChildScmDeveloperConnectionInheritAppendPath(), var4);
            var1.setDeveloperConnection(var6);
            var1.setLocation("developerConnection", var2.getLocation("developerConnection"));
         }
      }
   }

   @Override
   protected void mergePlugin_Executions(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getExecutions();
      if (!var5.isEmpty()) {
         List var6 = var1.getExecutions();
         LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

         for (PluginExecution var9 : var5) {
            if (var3 || (var9.getInherited() != null ? var9.isInherited() : var2.isInherited())) {
               Object var10 = this.getPluginExecutionKey(var9);
               var7.put(var10, var9);
            }
         }

         for (PluginExecution var13 : var6) {
            Object var14 = this.getPluginExecutionKey(var13);
            PluginExecution var11 = (PluginExecution)var7.get(var14);
            if (var11 != null) {
               this.mergePluginExecution(var13, var11, var3, var4);
            }

            var7.put(var14, var13);
         }

         var1.setExecutions(new ArrayList<>(var7.values()));
      }
   }

   @Override
   protected void mergePluginExecution_Goals(PluginExecution var1, PluginExecution var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getGoals();
      if (!var5.isEmpty()) {
         List var6 = var1.getGoals();
         LinkedHashSet var7 = new LinkedHashSet(var6);
         ArrayList var8 = new ArrayList(var6.size() + var5.size());
         var8.addAll(var6);

         for (String var10 : var5) {
            if (!var7.contains(var10)) {
               var8.add(var10);
            }
         }

         var1.setGoals(var8);
      }
   }

   @Override
   protected void mergeReportPlugin_ReportSets(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
      List var5 = var2.getReportSets();
      if (!var5.isEmpty()) {
         List var6 = var1.getReportSets();
         LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

         for (ReportSet var9 : var5) {
            if (var3 || (var9.getInherited() != null ? var9.isInherited() : var2.isInherited())) {
               Object var10 = this.getReportSetKey(var9);
               var7.put(var10, var9);
            }
         }

         for (ReportSet var13 : var6) {
            Object var14 = this.getReportSetKey(var13);
            ReportSet var11 = (ReportSet)var7.get(var14);
            if (var11 != null) {
               this.mergeReportSet(var13, var11, var3, var4);
            }

            var7.put(var14, var13);
         }

         var1.setReportSets(new ArrayList<>(var7.values()));
      }
   }

   @Override
   protected Object getDependencyKey(Dependency var1) {
      return var1.getManagementKey();
   }

   @Override
   protected Object getPluginKey(Plugin var1) {
      return var1.getKey();
   }

   @Override
   protected Object getPluginExecutionKey(PluginExecution var1) {
      return var1.getId();
   }

   @Override
   protected Object getReportPluginKey(ReportPlugin var1) {
      return var1.getKey();
   }

   @Override
   protected Object getReportSetKey(ReportSet var1) {
      return var1.getId();
   }

   @Override
   protected Object getRepositoryBaseKey(RepositoryBase var1) {
      return var1.getId();
   }

   @Override
   protected Object getExtensionKey(Extension var1) {
      return var1.getGroupId() + ':' + var1.getArtifactId();
   }

   @Override
   protected Object getExclusionKey(Exclusion var1) {
      return var1.getGroupId() + ':' + var1.getArtifactId();
   }

   protected String extrapolateChildUrl(String var1, boolean var2, Map<Object, Object> var3) {
      return var1;
   }
}
