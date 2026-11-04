package org.apache.maven.model.interpolation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
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
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class StringVisitorModelInterpolator extends AbstractStringBasedModelInterpolator {
   @Override
   public Model interpolateModel(Model var1, File var2, ModelBuildingRequest var3, ModelProblemCollector var4) {
      List var5 = this.createValueSources(var1, var2, var3, var4);
      List var6 = this.createPostProcessors(var1, var2, var3);
      StringVisitorModelInterpolator.InnerInterpolator var7 = this.createInterpolator(var5, var6, var4);
      new StringVisitorModelInterpolator.ModelVisitor(var7).visit(var1);
      return var1;
   }

   private StringVisitorModelInterpolator.InnerInterpolator createInterpolator(
      List<? extends ValueSource> var1, List<? extends InterpolationPostProcessor> var2, final ModelProblemCollector var3
   ) {
      final HashMap var4 = new HashMap();
      final StringSearchInterpolator var5 = new StringSearchInterpolator();
      var5.setCacheAnswers(true);

      for (ValueSource var7 : var1) {
         var5.addValueSource(var7);
      }

      for (InterpolationPostProcessor var10 : var2) {
         var5.addPostProcessor(var10);
      }

      final RecursionInterceptor var9 = this.createRecursionInterceptor();
      return new StringVisitorModelInterpolator.InnerInterpolator() {
         @Override
         public String interpolate(String var1) {
            if (var1 != null && var1.contains("${")) {
               String var2 = (String)var4.get(var1);
               if (var2 == null) {
                  try {
                     var2 = var5.interpolate(var1, var9);
                  } catch (InterpolationException var4x) {
                     var3.add(
                        new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                           .setMessage(var4x.getMessage())
                           .setException(var4x)
                     );
                  }

                  var4.put(var1, var2);
               }

               return var2;
            } else {
               return var1;
            }
         }
      };
   }

   interface InnerInterpolator {
      String interpolate(String var1);
   }

   private static final class ModelVisitor {
      private final StringVisitorModelInterpolator.InnerInterpolator interpolator;

      ModelVisitor(StringVisitorModelInterpolator.InnerInterpolator var1) {
         this.interpolator = var1;
      }

      void visit(Model var1) {
         if (var1 != null) {
            this.visit((ModelBase)var1);
            String var2 = var1.getModelVersion();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setModelVersion(var3);
            }

            this.visit(var1.getParent());
            String var4 = var1.getGroupId();
            String var5 = this.interpolate(var4);
            if (var4 != var5) {
               var1.setGroupId(var5);
            }

            String var6 = var1.getArtifactId();
            String var7 = this.interpolate(var6);
            if (var6 != var7) {
               var1.setArtifactId(var7);
            }

            String var8 = var1.getVersion();
            String var9 = this.interpolate(var8);
            if (var8 != var9) {
               var1.setVersion(var9);
            }

            String var10 = var1.getPackaging();
            String var11 = this.interpolate(var10);
            if (var10 != var11) {
               var1.setPackaging(var11);
            }

            String var12 = var1.getName();
            String var13 = this.interpolate(var12);
            if (var12 != var13) {
               var1.setName(var13);
            }

            String var14 = var1.getDescription();
            String var15 = this.interpolate(var14);
            if (var14 != var15) {
               var1.setDescription(var15);
            }

            String var16 = var1.getUrl();
            String var17 = this.interpolate(var16);
            if (var16 != var17) {
               var1.setUrl(var17);
            }

            String var18 = var1.getChildProjectUrlInheritAppendPath();
            String var19 = this.interpolate(var18);
            if (var18 != var19) {
               var1.setChildProjectUrlInheritAppendPath(var19);
            }

            String var20 = var1.getInceptionYear();
            String var21 = this.interpolate(var20);
            if (var20 != var21) {
               var1.setInceptionYear(var21);
            }

            this.visit(var1.getOrganization());

            for (License var23 : var1.getLicenses()) {
               this.visit(var23);
            }

            for (Developer var28 : var1.getDevelopers()) {
               this.visit(var28);
            }

            for (Contributor var29 : var1.getContributors()) {
               this.visit(var29);
            }

            for (MailingList var30 : var1.getMailingLists()) {
               this.visit(var30);
            }

            this.visit(var1.getPrerequisites());
            this.visit(var1.getScm());
            this.visit(var1.getIssueManagement());
            this.visit(var1.getCiManagement());
            this.visit(var1.getBuild());

            for (Profile var31 : var1.getProfiles()) {
               this.visit(var31);
            }
         }
      }

      private void visit(Parent var1) {
         if (var1 != null) {
            String var2 = var1.getGroupId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }

            var2 = var1.getRelativePath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setRelativePath(var3);
            }
         }
      }

      private void visit(Organization var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }
         }
      }

      private void visit(License var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }

            var2 = var1.getDistribution();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDistribution(var3);
            }

            var2 = var1.getComments();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setComments(var3);
            }
         }
      }

      private void visit(Developer var1) {
         if (var1 != null) {
            this.visit((Contributor)var1);
            String var2 = var1.getId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }
         }
      }

      private void visit(Contributor var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getEmail();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setEmail(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }

            var2 = var1.getOrganization();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setOrganization(var3);
            }

            var2 = var1.getOrganizationUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setOrganizationUrl(var3);
            }

            this.visit(var1.getRoles());
         }
      }

      private void visit(MailingList var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getSubscribe();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setSubscribe(var3);
            }

            var2 = var1.getUnsubscribe();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUnsubscribe(var3);
            }

            var2 = var1.getPost();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setPost(var3);
            }

            var2 = var1.getArchive();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArchive(var3);
            }
         }
      }

      private void visit(Prerequisites var1) {
         if (var1 != null) {
            String var2 = var1.getMaven();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setMaven(var3);
            }
         }
      }

      private void visit(Scm var1) {
         if (var1 != null) {
            String var2 = var1.getConnection();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setConnection(var3);
            }

            var2 = var1.getDeveloperConnection();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDeveloperConnection(var3);
            }

            var2 = var1.getTag();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setTag(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }

            var2 = var1.getChildScmConnectionInheritAppendPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setChildScmConnectionInheritAppendPath(var3);
            }

            var2 = var1.getChildScmDeveloperConnectionInheritAppendPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setChildScmDeveloperConnectionInheritAppendPath(var3);
            }

            var2 = var1.getChildScmUrlInheritAppendPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setChildScmUrlInheritAppendPath(var3);
            }
         }
      }

      private void visit(IssueManagement var1) {
         if (var1 != null) {
            String var2 = var1.getSystem();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setSystem(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }
         }
      }

      private void visit(CiManagement var1) {
         if (var1 != null) {
            String var2 = var1.getSystem();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setSystem(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }

            for (Notifier var5 : var1.getNotifiers()) {
               this.visit(var5);
            }
         }
      }

      private void visit(Notifier var1) {
         if (var1 != null) {
            String var2 = var1.getType();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setType(var3);
            }

            this.visit(var1.getConfiguration());
         }
      }

      private void visit(BuildBase var1) {
         if (var1 != null) {
            for (Plugin var5 : var1.getPlugins()) {
               this.visit(var5);
            }

            this.visit(var1.getPluginManagement());
            String var2 = var1.getDefaultGoal();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDefaultGoal(var3);
            }

            for (Resource var12 : var1.getResources()) {
               this.visit(var12);
            }

            for (Resource var13 : var1.getTestResources()) {
               this.visit(var13);
            }

            var2 = var1.getDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDirectory(var3);
            }

            var2 = var1.getFinalName();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setFinalName(var3);
            }

            this.visit(var1.getFilters());
         }
      }

      private void visit(PluginManagement var1) {
         if (var1 != null) {
            for (Plugin var3 : var1.getPlugins()) {
               this.visit(var3);
            }
         }
      }

      private void visit(Build var1) {
         if (var1 != null) {
            this.visit((BuildBase)var1);
            String var2 = var1.getSourceDirectory();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setSourceDirectory(var3);
            }

            var2 = var1.getScriptSourceDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setScriptSourceDirectory(var3);
            }

            var2 = var1.getTestSourceDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setTestSourceDirectory(var3);
            }

            var2 = var1.getOutputDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setOutputDirectory(var3);
            }

            var2 = var1.getTestOutputDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setTestOutputDirectory(var3);
            }

            for (Extension var5 : var1.getExtensions()) {
               this.visit(var5);
            }
         }
      }

      private void visit(Resource var1) {
         if (var1 != null) {
            this.visit(var1.getIncludes());
            this.visit(var1.getExcludes());
            String var2 = var1.getDirectory();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDirectory(var3);
            }

            var2 = var1.getTargetPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setTargetPath(var3);
            }

            var2 = var1.getFiltering();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setFiltering(var3);
            }
         }
      }

      private void visit(Plugin var1) {
         if (var1 != null) {
            String var2 = var1.getInherited();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setInherited(var3);
            }

            this.visit((Xpp3Dom)var1.getConfiguration());
            var2 = var1.getGroupId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }

            var2 = var1.getExtensions();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setExtensions(var3);
            }

            for (PluginExecution var5 : var1.getExecutions()) {
               this.visit(var5);
            }

            for (Dependency var15 : var1.getDependencies()) {
               this.visit(var15);
            }
         }
      }

      private void visit(PluginExecution var1) {
         if (var1 != null) {
            String var2 = var1.getInherited();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setInherited(var3);
            }

            this.visit((Xpp3Dom)var1.getConfiguration());
            var2 = var1.getId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }

            var2 = var1.getPhase();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setPhase(var3);
            }

            this.visit(var1.getGoals());
         }
      }

      private void visit(Xpp3Dom var1) {
         if (var1 != null) {
            String var2 = var1.getValue();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setValue(var3);
            }

            for (String var7 : var1.getAttributeNames()) {
               var2 = var1.getAttribute(var7);
               var3 = this.interpolate(var2);
               if (var2 != var3) {
                  var1.setAttribute(var7, var3);
               }
            }

            int var10 = 0;

            for (int var11 = var1.getChildCount(); var10 < var11; var10++) {
               this.visit(var1.getChild(var10));
            }
         }
      }

      private void visit(Extension var1) {
         if (var1 != null) {
            String var2 = var1.getGroupId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }
         }
      }

      private void visit(Profile var1) {
         if (var1 != null) {
            this.visit((ModelBase)var1);
            String var2 = var1.getId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }

            this.visit(var1.getActivation());
            this.visit(var1.getBuild());
         }
      }

      private void visit(Activation var1) {
         if (var1 != null) {
            String var2 = var1.getJdk();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setJdk(var3);
            }

            this.visit(var1.getOs());
            this.visit(var1.getProperty());
            this.visit(var1.getFile());
         }
      }

      private void visit(ActivationOS var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getFamily();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setFamily(var3);
            }

            var2 = var1.getArch();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArch(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }
         }
      }

      private void visit(ActivationProperty var1) {
         if (var1 != null) {
            String var2 = var1.getName();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getValue();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setValue(var3);
            }
         }
      }

      private void visit(ActivationFile var1) {
         if (var1 != null) {
            String var2 = var1.getMissing();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setMissing(var3);
            }

            var2 = var1.getExists();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setExists(var3);
            }
         }
      }

      private void visit(ModelBase var1) {
         if (var1 != null) {
            this.visit(var1.getModules());
            this.visit(var1.getDistributionManagement());
            this.visit(var1.getProperties());
            this.visit(var1.getDependencyManagement());

            for (Dependency var3 : var1.getDependencies()) {
               this.visit(var3);
            }

            for (Repository var6 : var1.getRepositories()) {
               this.visit(var6);
            }

            for (Repository var7 : var1.getPluginRepositories()) {
               this.visit(var7);
            }

            this.visit(var1.getReporting());
         }
      }

      private void visit(DistributionManagement var1) {
         if (var1 != null) {
            this.visit((Repository)var1.getRepository());
            this.visit((Repository)var1.getSnapshotRepository());
            this.visit(var1.getSite());
            String var2 = var1.getDownloadUrl();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setDownloadUrl(var3);
            }

            this.visit(var1.getRelocation());
         }
      }

      private void visit(Site var1) {
         if (var1 != null) {
            String var2 = var1.getId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }

            var2 = var1.getName();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setName(var3);
            }

            var2 = var1.getUrl();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setUrl(var3);
            }

            var2 = var1.getChildSiteUrlInheritAppendPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setChildSiteUrlInheritAppendPath(var3);
            }
         }
      }

      private void visit(Relocation var1) {
         if (var1 != null) {
            String var2 = var1.getGroupId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }

            var2 = var1.getMessage();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setMessage(var3);
            }
         }
      }

      private void visit(DependencyManagement var1) {
         if (var1 != null) {
            for (Dependency var3 : var1.getDependencies()) {
               this.visit(var3);
            }
         }
      }

      private void visit(Repository var1) {
         if (var1 != null) {
            this.visit((RepositoryBase)var1);
            this.visit(var1.getReleases());
            this.visit(var1.getSnapshots());
         }
      }

      private void visit(RepositoryBase var1) {
         if (var1 != null) {
            String var2 = var1.getId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }

            String var4 = var1.getName();
            String var5 = this.interpolate(var4);
            if (var4 != var5) {
               var1.setName(var5);
            }

            String var6 = var1.getUrl();
            String var7 = this.interpolate(var6);
            if (var6 != var7) {
               var1.setUrl(var7);
            }

            String var8 = var1.getLayout();
            String var9 = this.interpolate(var8);
            if (var8 != var9) {
               var1.setLayout(var9);
            }
         }
      }

      private void visit(RepositoryPolicy var1) {
         if (var1 != null) {
            String var2 = var1.getEnabled();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setEnabled(var3);
            }

            String var4 = var1.getUpdatePolicy();
            String var5 = this.interpolate(var4);
            if (var4 != var5) {
               var1.setUpdatePolicy(var5);
            }

            String var6 = var1.getChecksumPolicy();
            String var7 = this.interpolate(var6);
            if (var6 != var7) {
               var1.setChecksumPolicy(var7);
            }
         }
      }

      private void visit(Dependency var1) {
         if (var1 != null) {
            String var2 = var1.getGroupId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
               var1.clearManagementKey();
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
               var1.clearManagementKey();
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }

            var2 = var1.getType();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setType(var3);
               var1.clearManagementKey();
            }

            var2 = var1.getClassifier();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setClassifier(var3);
               var1.clearManagementKey();
            }

            var2 = var1.getScope();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setScope(var3);
            }

            var2 = var1.getSystemPath();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setSystemPath(var3);
            }

            for (Exclusion var5 : var1.getExclusions()) {
               this.visit(var5);
            }

            var2 = var1.getOptional();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setOptional(var3);
            }
         }
      }

      private void visit(Exclusion var1) {
         if (var1 != null) {
            String var2 = var1.getGroupId();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }
         }
      }

      private void visit(Reporting var1) {
         if (var1 != null) {
            String var2 = var1.getExcludeDefaults();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setExcludeDefaults(var3);
            }

            var2 = var1.getOutputDirectory();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setOutputDirectory(var3);
            }

            for (ReportPlugin var5 : var1.getPlugins()) {
               this.visit(var5);
            }
         }
      }

      private void visit(ReportPlugin var1) {
         if (var1 != null) {
            String var2 = var1.getInherited();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setInherited(var3);
            }

            this.visit((Xpp3Dom)var1.getConfiguration());
            var2 = var1.getGroupId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setGroupId(var3);
            }

            var2 = var1.getArtifactId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setArtifactId(var3);
            }

            var2 = var1.getVersion();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setVersion(var3);
            }

            for (ReportSet var5 : var1.getReportSets()) {
               this.visit(var5);
            }
         }
      }

      private void visit(ReportSet var1) {
         if (var1 != null) {
            String var2 = var1.getInherited();
            String var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setInherited(var3);
            }

            this.visit((Xpp3Dom)var1.getConfiguration());
            var2 = var1.getId();
            var3 = this.interpolate(var2);
            if (var2 != var3) {
               var1.setId(var3);
            }

            this.visit(var1.getReports());
         }
      }

      private void visit(Properties var1) {
         if (var1 != null) {
            for (Entry var3 : var1.entrySet()) {
               Object var4 = var3.getValue();
               if (var4 instanceof String) {
                  String var5 = (String)var4;
                  String var6 = this.interpolate(var5);
                  if (var5 != var6 && var6 != null) {
                     var3.setValue(var6);
                  }
               }
            }
         }
      }

      private void visit(List<String> var1) {
         if (var1 != null) {
            ListIterator var2 = var1.listIterator();

            while (var2.hasNext()) {
               String var3 = (String)var2.next();
               String var4 = this.interpolate(var3);
               if (var3 != var4) {
                  var2.set(var4);
               }
            }
         }
      }

      private String interpolate(String var1) {
         return this.interpolator.interpolate(var1);
      }
   }
}
