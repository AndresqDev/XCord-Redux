package org.apache.maven.model.validation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputLocationTracker;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.apache.maven.model.interpolation.ModelVersionProcessor;
import org.codehaus.plexus.util.StringUtils;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelValidator implements ModelValidator {
   private static final Pattern CI_FRIENDLY_EXPRESSION = Pattern.compile("\\$\\{(.+?)\\}");
   private static final String ILLEGAL_FS_CHARS = "\\/:\"<>|?*";
   private static final String ILLEGAL_VERSION_CHARS = "\\/:\"<>|?*";
   private static final String ILLEGAL_REPO_ID_CHARS = "\\/:\"<>|?*";
   private static final String EMPTY = "";
   private final Set<String> validIds = new HashSet<>();
   private ModelVersionProcessor versionProcessor;

   @Inject
   public DefaultModelValidator(ModelVersionProcessor var1) {
      this.versionProcessor = var1;
   }

   @Override
   public void validateRawModel(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Parent var4 = var1.getParent();
      if (var4 != null) {
         this.validateStringNotEmpty("parent.groupId", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, var4.getGroupId(), var4);
         this.validateStringNotEmpty("parent.artifactId", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, var4.getArtifactId(), var4);
         this.validateStringNotEmpty("parent.version", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.BASE, var4.getVersion(), var4);
         if (equals(var4.getGroupId(), var1.getGroupId()) && equals(var4.getArtifactId(), var1.getArtifactId())) {
            addViolation(
               var3,
               ModelProblem.Severity.FATAL,
               ModelProblem.Version.BASE,
               "parent.artifactId",
               null,
               "must be changed, the parent element cannot have the same groupId:artifactId as the project.",
               var4
            );
         }

         if (equals("LATEST", var4.getVersion()) || equals("RELEASE", var4.getVersion())) {
            addViolation(
               var3,
               ModelProblem.Severity.WARNING,
               ModelProblem.Version.BASE,
               "parent.version",
               null,
               "is either LATEST or RELEASE (both of them are being deprecated)",
               var4
            );
         }
      }

      if (var2.getValidationLevel() >= 20) {
         ModelProblem.Severity var5 = getSeverity(var2, 30);
         this.validateStringNotEmpty("modelVersion", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var1.getModelVersion(), var1);
         this.validateModelVersion(var3, var1.getModelVersion(), var1, "4.0.0");
         this.validateStringNoExpression("groupId", var3, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, var1.getGroupId(), var1);
         if (var4 == null) {
            this.validateStringNotEmpty("groupId", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, var1.getGroupId(), var1);
         }

         this.validateStringNoExpression("artifactId", var3, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, var1.getArtifactId(), var1);
         this.validateStringNotEmpty("artifactId", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, var1.getArtifactId(), var1);
         this.validateVersionNoExpression("version", var3, ModelProblem.Severity.WARNING, ModelProblem.Version.V20, var1.getVersion(), var1);
         if (var4 == null) {
            this.validateStringNotEmpty("version", var3, ModelProblem.Severity.FATAL, ModelProblem.Version.V20, var1.getVersion(), var1);
         }

         this.validate20RawDependencies(var3, var1.getDependencies(), "dependencies.dependency.", "", var2);
         this.validate20RawDependenciesSelfReferencing(var3, var1, var1.getDependencies(), "dependencies.dependency", var2);
         if (var1.getDependencyManagement() != null) {
            this.validate20RawDependencies(var3, var1.getDependencyManagement().getDependencies(), "dependencyManagement.dependencies.dependency.", "", var2);
         }

         this.validateRawRepositories(var3, var1.getRepositories(), "repositories.repository.", "", var2);
         this.validateRawRepositories(var3, var1.getPluginRepositories(), "pluginRepositories.pluginRepository.", "", var2);
         Build var6 = var1.getBuild();
         if (var6 != null) {
            this.validate20RawPlugins(var3, var6.getPlugins(), "build.plugins.plugin.", "", var2);
            PluginManagement var7 = var6.getPluginManagement();
            if (var7 != null) {
               this.validate20RawPlugins(var3, var7.getPlugins(), "build.pluginManagement.plugins.plugin.", "", var2);
            }
         }

         HashSet var13 = new HashSet();

         for (Profile var9 : var1.getProfiles()) {
            String var10 = "profiles.profile[" + var9.getId() + "].";
            if (!var13.add(var9.getId())) {
               addViolation(
                  var3, var5, ModelProblem.Version.V20, "profiles.profile.id", null, "must be unique but found duplicate profile with id " + var9.getId(), var9
               );
            }

            this.validate30RawProfileActivation(var3, var9.getActivation(), var9.getId(), var10, "activation", var2);
            this.validate20RawDependencies(var3, var9.getDependencies(), var10, "dependencies.dependency.", var2);
            if (var9.getDependencyManagement() != null) {
               this.validate20RawDependencies(
                  var3, var9.getDependencyManagement().getDependencies(), var10, "dependencyManagement.dependencies.dependency.", var2
               );
            }

            this.validateRawRepositories(var3, var9.getRepositories(), var10, "repositories.repository.", var2);
            this.validateRawRepositories(var3, var9.getPluginRepositories(), var10, "pluginRepositories.pluginRepository.", var2);
            BuildBase var11 = var9.getBuild();
            if (var11 != null) {
               this.validate20RawPlugins(var3, var11.getPlugins(), var10, "plugins.plugin.", var2);
               PluginManagement var12 = var11.getPluginManagement();
               if (var12 != null) {
                  this.validate20RawPlugins(var3, var12.getPlugins(), var10, "pluginManagement.plugins.plugin.", var2);
               }
            }
         }
      }
   }

   private void validate30RawProfileActivation(ModelProblemCollector var1, Activation var2, String var3, String var4, String var5, ModelBuildingRequest var6) {
      if (var2 != null) {
         ActivationFile var7 = var2.getFile();
         if (var7 != null) {
            String var8;
            boolean var9;
            if (StringUtils.isNotEmpty(var7.getExists())) {
               var8 = var7.getExists();
               var9 = false;
            } else {
               if (!StringUtils.isNotEmpty(var7.getMissing())) {
                  return;
               }

               var8 = var7.getMissing();
               var9 = true;
            }

            if (var8.contains("${project.basedir}")) {
               addViolation(
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V30,
                  var4 + var5 + (var9 ? ".file.missing" : ".file.exists"),
                  null,
                  "Failed to interpolate file location "
                     + var8
                     + " for profile "
                     + var3
                     + ": ${project.basedir} expression not supported during profile activation, use ${basedir} instead",
                  var7.getLocation(var9 ? "missing" : "exists")
               );
            } else if (this.hasProjectExpression(var8)) {
               addViolation(
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V30,
                  var4 + var5 + (var9 ? ".file.missing" : ".file.exists"),
                  null,
                  "Failed to interpolate file location "
                     + var8
                     + " for profile "
                     + var3
                     + ": ${project.*} expressions are not supported during profile activation",
                  var7.getLocation(var9 ? "missing" : "exists")
               );
            }
         }
      }
   }

   private void validate20RawPlugins(ModelProblemCollector var1, List<Plugin> var2, String var3, String var4, ModelBuildingRequest var5) {
      ModelProblem.Severity var6 = getSeverity(var5, 31);
      HashMap var7 = new HashMap();

      for (Plugin var9 : var2) {
         if (var9.getGroupId() == null || var9.getGroupId() != null && var9.getGroupId().trim().isEmpty()) {
            addViolation(
               var1,
               ModelProblem.Severity.FATAL,
               ModelProblem.Version.V20,
               var3 + var4 + "(groupId:artifactId)",
               null,
               "groupId of a plugin must be defined. ",
               var9
            );
         }

         if (var9.getArtifactId() == null || var9.getArtifactId() != null && var9.getArtifactId().trim().isEmpty()) {
            addViolation(
               var1,
               ModelProblem.Severity.FATAL,
               ModelProblem.Version.V20,
               var3 + var4 + "(groupId:artifactId)",
               null,
               "artifactId of a plugin must be defined. ",
               var9
            );
         }

         if (var9.getVersion() != null && var9.getVersion().trim().isEmpty()) {
            addViolation(
               var1,
               ModelProblem.Severity.FATAL,
               ModelProblem.Version.V20,
               var3 + var4 + "(groupId:artifactId)",
               null,
               "version of a plugin must be defined. ",
               var9
            );
         }

         String var10 = var9.getKey();
         Plugin var11 = (Plugin)var7.get(var10);
         if (var11 != null) {
            addViolation(
               var1,
               var6,
               ModelProblem.Version.V20,
               var3 + var4 + "(groupId:artifactId)",
               null,
               "must be unique but found duplicate declaration of plugin " + var10,
               var9
            );
         } else {
            var7.put(var10, var9);
         }

         HashSet var12 = new HashSet();

         for (PluginExecution var14 : var9.getExecutions()) {
            if (!var12.add(var14.getId())) {
               addViolation(
                  var1,
                  ModelProblem.Severity.ERROR,
                  ModelProblem.Version.V20,
                  var3 + var4 + "[" + var9.getKey() + "].executions.execution.id",
                  null,
                  "must be unique but found duplicate execution with id " + var14.getId(),
                  var14
               );
            }
         }
      }
   }

   @Override
   public void validateEffectiveModel(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      this.validateStringNotEmpty("modelVersion", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var1.getModelVersion(), var1);
      this.validateId("groupId", var3, var1.getGroupId(), var1);
      this.validateId("artifactId", var3, var1.getArtifactId(), var1);
      this.validateStringNotEmpty("packaging", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var1.getPackaging(), var1);
      if (!var1.getModules().isEmpty()) {
         if (!"pom".equals(var1.getPackaging())) {
            addViolation(
               var3,
               ModelProblem.Severity.ERROR,
               ModelProblem.Version.BASE,
               "packaging",
               null,
               "with value '" + var1.getPackaging() + "' is invalid. Aggregator projects require 'pom' as packaging.",
               var1
            );
         }

         int var4 = 0;

         for (int var5 = var1.getModules().size(); var4 < var5; var4++) {
            String var6 = var1.getModules().get(var4);
            if (StringUtils.isBlank(var6)) {
               addViolation(
                  var3,
                  ModelProblem.Severity.ERROR,
                  ModelProblem.Version.BASE,
                  "modules.module[" + var4 + "]",
                  null,
                  "has been specified without a path to the project directory.",
                  var1.getLocation("modules")
               );
            }
         }
      }

      this.validateStringNotEmpty("version", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var1.getVersion(), var1);
      ModelProblem.Severity var12 = getSeverity(var2, 30);
      this.validateEffectiveDependencies(var3, var1, var1.getDependencies(), false, var2);
      DependencyManagement var13 = var1.getDependencyManagement();
      if (var13 != null) {
         this.validateEffectiveDependencies(var3, var1, var13.getDependencies(), true, var2);
      }

      if (var2.getValidationLevel() >= 20) {
         HashSet var14 = new HashSet();
         int var7 = 0;

         for (int var8 = var1.getModules().size(); var7 < var8; var7++) {
            String var9 = var1.getModules().get(var7);
            if (!var14.add(var9)) {
               addViolation(
                  var3,
                  ModelProblem.Severity.ERROR,
                  ModelProblem.Version.V20,
                  "modules.module[" + var7 + "]",
                  null,
                  "specifies duplicate child module " + var9,
                  var1.getLocation("modules")
               );
            }
         }

         ModelProblem.Severity var15 = getSeverity(var2, 31);
         this.validateBannedCharacters("", "version", var3, var15, ModelProblem.Version.V20, var1.getVersion(), null, var1, "\\/:\"<>|?*");
         this.validate20ProperSnapshotVersion("version", var3, var15, ModelProblem.Version.V20, var1.getVersion(), null, var1);
         Build var16 = var1.getBuild();
         if (var16 != null) {
            for (Plugin var10 : var16.getPlugins()) {
               this.validateStringNotEmpty(
                  "build.plugins.plugin.artifactId", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var10.getArtifactId(), var10
               );
               this.validateStringNotEmpty(
                  "build.plugins.plugin.groupId", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var10.getGroupId(), var10
               );
               this.validate20PluginVersion("build.plugins.plugin.version", var3, var10.getVersion(), var10.getKey(), var10, var2);
               this.validateBoolean("build.plugins.plugin.inherited", "", var3, var12, ModelProblem.Version.V20, var10.getInherited(), var10.getKey(), var10);
               this.validateBoolean("build.plugins.plugin.extensions", "", var3, var12, ModelProblem.Version.V20, var10.getExtensions(), var10.getKey(), var10);
               this.validate20EffectivePluginDependencies(var3, var10, var2);
            }

            this.validate20RawResources(var3, var16.getResources(), "build.resources.resource.", var2);
            this.validate20RawResources(var3, var16.getTestResources(), "build.testResources.testResource.", var2);
         }

         Reporting var18 = var1.getReporting();
         if (var18 != null) {
            for (ReportPlugin var11 : var18.getPlugins()) {
               this.validateStringNotEmpty(
                  "reporting.plugins.plugin.artifactId", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var11.getArtifactId(), var11
               );
               this.validateStringNotEmpty(
                  "reporting.plugins.plugin.groupId", var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var11.getGroupId(), var11
               );
            }
         }

         for (Repository var23 : var1.getRepositories()) {
            this.validate20EffectiveRepository(var3, var23, "repositories.repository.", var2);
         }

         for (Repository var24 : var1.getPluginRepositories()) {
            this.validate20EffectiveRepository(var3, var24, "pluginRepositories.pluginRepository.", var2);
         }

         DistributionManagement var22 = var1.getDistributionManagement();
         if (var22 != null) {
            if (var22.getStatus() != null) {
               addViolation(var3, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, "distributionManagement.status", null, "must not be specified.", var22);
            }

            this.validate20EffectiveRepository(var3, var22.getRepository(), "distributionManagement.repository.", var2);
            this.validate20EffectiveRepository(var3, var22.getSnapshotRepository(), "distributionManagement.snapshotRepository.", var2);
         }
      }
   }

   private void validate20RawDependencies(ModelProblemCollector var1, List<Dependency> var2, String var3, String var4, ModelBuildingRequest var5) {
      ModelProblem.Severity var6 = getSeverity(var5, 30);
      ModelProblem.Severity var7 = getSeverity(var5, 31);
      HashMap var8 = new HashMap();

      for (Dependency var10 : var2) {
         String var11 = var10.getManagementKey();
         if ("import".equals(var10.getScope())) {
            if (!"pom".equals(var10.getType())) {
               addViolation(
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V20,
                  var3 + var4 + "type",
                  var11,
                  "must be 'pom' to import the managed dependencies.",
                  var10
               );
            } else if (StringUtils.isNotEmpty(var10.getClassifier())) {
               addViolation(
                  var1, var6, ModelProblem.Version.V20, var3 + var4 + "classifier", var11, "must be empty, imported POM cannot have a classifier.", var10
               );
            }
         } else if ("system".equals(var10.getScope())) {
            if (var5.getValidationLevel() >= 31) {
               addViolation(
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V31,
                  var3 + var4 + "scope",
                  var11,
                  "declares usage of deprecated 'system' scope ",
                  var10
               );
            }

            String var12 = var10.getSystemPath();
            if (StringUtils.isNotEmpty(var12)) {
               if (!this.hasExpression(var12)) {
                  addViolation(
                     var1,
                     ModelProblem.Severity.WARNING,
                     ModelProblem.Version.V20,
                     var3 + var4 + "systemPath",
                     var11,
                     "should use a variable instead of a hard-coded path " + var12,
                     var10
                  );
               } else if (var12.contains("${basedir}") || var12.contains("${project.basedir}")) {
                  addViolation(
                     var1,
                     ModelProblem.Severity.WARNING,
                     ModelProblem.Version.V20,
                     var3 + var4 + "systemPath",
                     var11,
                     "should not point at files within the project directory, " + var12 + " will be unresolvable by dependent projects",
                     var10
                  );
               }
            }
         }

         if (equals("LATEST", var10.getVersion()) || equals("RELEASE", var10.getVersion())) {
            addViolation(
               var1,
               ModelProblem.Severity.WARNING,
               ModelProblem.Version.BASE,
               var3 + var4 + "version",
               var11,
               "is either LATEST or RELEASE (both of them are being deprecated)",
               var10
            );
         }

         Dependency var14 = (Dependency)var8.get(var11);
         if (var14 != null) {
            String var13;
            if (equals(var14.getVersion(), var10.getVersion())) {
               var13 = "duplicate declaration of version " + Objects.toString(var10.getVersion(), "(?)");
            } else {
               var13 = "version " + Objects.toString(var14.getVersion(), "(?)") + " vs " + Objects.toString(var10.getVersion(), "(?)");
            }

            addViolation(
               var1,
               var7,
               ModelProblem.Version.V20,
               var3 + var4 + "(groupId:artifactId:type:classifier)",
               null,
               "must be unique: " + var11 + " -> " + var13,
               var10
            );
         } else {
            var8.put(var11, var10);
         }
      }
   }

   private void validate20RawDependenciesSelfReferencing(ModelProblemCollector var1, Model var2, List<Dependency> var3, String var4, ModelBuildingRequest var5) {
      for (Dependency var7 : var3) {
         String var8 = var7.getGroupId()
            + ":"
            + var7.getArtifactId()
            + ":"
            + var7.getVersion()
            + (var7.getClassifier() != null ? ":" + var7.getClassifier() : "");
         String var9 = var2.getGroupId() + ":" + var2.getArtifactId() + ":" + var2.getVersion();
         if (var8.equals(var9)) {
            addViolation(var1, ModelProblem.Severity.FATAL, ModelProblem.Version.V31, var4 + "[" + var8 + "]", var8, "is referencing itself.", var7);
         }
      }
   }

   private void validateEffectiveDependencies(ModelProblemCollector var1, Model var2, List<Dependency> var3, boolean var4, ModelBuildingRequest var5) {
      ModelProblem.Severity var6 = getSeverity(var5, 30);
      String var7 = var4 ? "dependencyManagement.dependencies.dependency." : "dependencies.dependency.";

      for (Dependency var9 : var3) {
         this.validateEffectiveDependency(var1, var9, var4, var7, var5);
         if (var5.getValidationLevel() >= 20) {
            this.validateBoolean(var7, "optional", var1, var6, ModelProblem.Version.V20, var9.getOptional(), var9.getManagementKey(), var9);
            if (!var4) {
               this.validateVersion(var7, "version", var1, var6, ModelProblem.Version.V20, var9.getVersion(), var9.getManagementKey(), var9);
               this.validateEnum(
                  var7,
                  "scope",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V20,
                  var9.getScope(),
                  var9.getManagementKey(),
                  var9,
                  "provided",
                  "compile",
                  "runtime",
                  "test",
                  "system"
               );
               this.validateEffectiveModelAgainstDependency(var7, var1, var2, var9, var5);
            } else {
               this.validateEnum(
                  var7,
                  "scope",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V20,
                  var9.getScope(),
                  var9.getManagementKey(),
                  var9,
                  "provided",
                  "compile",
                  "runtime",
                  "test",
                  "system",
                  "import"
               );
            }
         }
      }
   }

   private void validateEffectiveModelAgainstDependency(String var1, ModelProblemCollector var2, Model var3, Dependency var4, ModelBuildingRequest var5) {
      String var6 = var4.getGroupId() + ":" + var4.getArtifactId() + ":" + var4.getVersion() + (var4.getClassifier() != null ? ":" + var4.getClassifier() : "");
      String var7 = var3.getGroupId() + ":" + var3.getArtifactId() + ":" + var3.getVersion();
      if (var6.equals(var7)) {
         addViolation(var2, ModelProblem.Severity.FATAL, ModelProblem.Version.V31, var1 + "[" + var6 + "]", var6, "is referencing itself.", var4);
      }
   }

   private void validate20EffectivePluginDependencies(ModelProblemCollector var1, Plugin var2, ModelBuildingRequest var3) {
      List var4 = var2.getDependencies();
      if (!var4.isEmpty()) {
         String var5 = "build.plugins.plugin[" + var2.getKey() + "].dependencies.dependency.";
         ModelProblem.Severity var6 = getSeverity(var3, 30);

         for (Dependency var8 : var4) {
            this.validateEffectiveDependency(var1, var8, false, var5, var3);
            this.validateVersion(var5, "version", var1, var6, ModelProblem.Version.BASE, var8.getVersion(), var8.getManagementKey(), var8);
            this.validateEnum(
               var5, "scope", var1, var6, ModelProblem.Version.BASE, var8.getScope(), var8.getManagementKey(), var8, "compile", "runtime", "system"
            );
         }
      }
   }

   private void validateEffectiveDependency(ModelProblemCollector var1, Dependency var2, boolean var3, String var4, ModelBuildingRequest var5) {
      this.validateId(var4, "artifactId", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var2.getArtifactId(), var2.getManagementKey(), var2);
      this.validateId(var4, "groupId", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var2.getGroupId(), var2.getManagementKey(), var2);
      if (!var3) {
         this.validateStringNotEmpty(var4, "type", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var2.getType(), var2.getManagementKey(), var2);
         this.validateDependencyVersion(var1, var2, var4);
      }

      if ("system".equals(var2.getScope())) {
         String var6 = var2.getSystemPath();
         if (StringUtils.isEmpty(var6)) {
            addViolation(var1, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var4 + "systemPath", var2.getManagementKey(), "is missing.", var2);
         } else {
            File var7 = new File(var6);
            if (!var7.isAbsolute()) {
               addViolation(
                  var1,
                  ModelProblem.Severity.ERROR,
                  ModelProblem.Version.BASE,
                  var4 + "systemPath",
                  var2.getManagementKey(),
                  "must specify an absolute path but is " + var6,
                  var2
               );
            } else if (!var7.isFile()) {
               String var8 = "refers to a non-existing file " + var7.getAbsolutePath();
               var6 = var6.replace('/', File.separatorChar).replace('\\', File.separatorChar);
               String var9 = var5.getSystemProperties().getProperty("java.home", "") + File.separator + "..";
               if (var6.startsWith(var9)) {
                  var8 = var8 + ". Please verify that you run Maven using a JDK and not just a JRE.";
               }

               addViolation(var1, ModelProblem.Severity.WARNING, ModelProblem.Version.BASE, var4 + "systemPath", var2.getManagementKey(), var8, var2);
            }
         }
      } else if (StringUtils.isNotEmpty(var2.getSystemPath())) {
         addViolation(
            var1,
            ModelProblem.Severity.ERROR,
            ModelProblem.Version.BASE,
            var4 + "systemPath",
            var2.getManagementKey(),
            "must be omitted. This field may only be specified for a dependency with system scope.",
            var2
         );
      }

      if (var5.getValidationLevel() >= 20) {
         for (Exclusion var12 : var2.getExclusions()) {
            if (var5.getValidationLevel() < 30) {
               this.validateId(
                  var4,
                  "exclusions.exclusion.groupId",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V20,
                  var12.getGroupId(),
                  var2.getManagementKey(),
                  var12
               );
               this.validateId(
                  var4,
                  "exclusions.exclusion.artifactId",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V20,
                  var12.getArtifactId(),
                  var2.getManagementKey(),
                  var12
               );
            } else {
               this.validateIdWithWildcards(
                  var4,
                  "exclusions.exclusion.groupId",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V30,
                  var12.getGroupId(),
                  var2.getManagementKey(),
                  var12
               );
               this.validateIdWithWildcards(
                  var4,
                  "exclusions.exclusion.artifactId",
                  var1,
                  ModelProblem.Severity.WARNING,
                  ModelProblem.Version.V30,
                  var12.getArtifactId(),
                  var2.getManagementKey(),
                  var12
               );
            }
         }
      }
   }

   protected void validateDependencyVersion(ModelProblemCollector var1, Dependency var2, String var3) {
      this.validateStringNotEmpty(
         var3, "version", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var2.getVersion(), var2.getManagementKey(), var2
      );
   }

   private void validateRawRepositories(ModelProblemCollector var1, List<Repository> var2, String var3, String var4, ModelBuildingRequest var5) {
      HashMap var6 = new HashMap();

      for (Repository var8 : var2) {
         this.validateStringNotEmpty(var3, var4, "id", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var8.getId(), null, var8);
         this.validateStringNotEmpty(
            var3, var4, "[" + var8.getId() + "].url", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var8.getUrl(), null, var8
         );
         String var9 = var8.getId();
         Repository var10 = (Repository)var6.get(var9);
         if (var10 != null) {
            ModelProblem.Severity var11 = getSeverity(var5, 30);
            addViolation(
               var1,
               var11,
               ModelProblem.Version.V20,
               var3 + var4 + "id",
               null,
               "must be unique: " + var8.getId() + " -> " + var10.getUrl() + " vs " + var8.getUrl(),
               var8
            );
         } else {
            var6.put(var9, var8);
         }
      }
   }

   private void validate20EffectiveRepository(ModelProblemCollector var1, Repository var2, String var3, ModelBuildingRequest var4) {
      if (var2 != null) {
         ModelProblem.Severity var5 = getSeverity(var4, 31);
         this.validateBannedCharacters(var3, "id", var1, var5, ModelProblem.Version.V20, var2.getId(), null, var2, "\\/:\"<>|?*");
         if ("local".equals(var2.getId())) {
            addViolation(
               var1,
               var5,
               ModelProblem.Version.V20,
               var3 + "id",
               null,
               "must not be 'local', this identifier is reserved for the local repository, using it for other repositories will corrupt your repository metadata.",
               var2
            );
         }

         if ("legacy".equals(var2.getLayout())) {
            addViolation(
               var1,
               ModelProblem.Severity.WARNING,
               ModelProblem.Version.V20,
               var3 + "layout",
               var2.getId(),
               "uses the unsupported value 'legacy', artifact resolution might fail.",
               var2
            );
         }
      }
   }

   private void validate20RawResources(ModelProblemCollector var1, List<Resource> var2, String var3, ModelBuildingRequest var4) {
      ModelProblem.Severity var5 = getSeverity(var4, 30);

      for (Resource var7 : var2) {
         this.validateStringNotEmpty(var3, "directory", var1, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, var7.getDirectory(), null, var7);
         this.validateBoolean(var3, "filtering", var1, var5, ModelProblem.Version.V20, var7.getFiltering(), var7.getDirectory(), var7);
      }
   }

   private boolean validateId(String var1, ModelProblemCollector var2, String var3, InputLocationTracker var4) {
      return this.validateId("", var1, var2, ModelProblem.Severity.ERROR, ModelProblem.Version.BASE, var3, null, var4);
   }

   private boolean validateId(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (this.validIds.contains(var6)) {
         return true;
      } else if (!this.validateStringNotEmpty(var1, var2, var3, var4, var5, var6, var7, var8)) {
         return false;
      } else if (!this.isValidId(var6)) {
         addViolation(var3, var4, var5, var1 + var2, var7, "with value '" + var6 + "' does not match a valid id pattern.", var8);
         return false;
      } else {
         this.validIds.add(var6);
         return true;
      }
   }

   private boolean isValidId(String var1) {
      for (int var2 = 0; var2 < var1.length(); var2++) {
         char var3 = var1.charAt(var2);
         if (!this.isValidIdCharacter(var3)) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidIdCharacter(char var1) {
      return var1 >= 'a' && var1 <= 'z' || var1 >= 'A' && var1 <= 'Z' || var1 >= '0' && var1 <= '9' || var1 == '-' || var1 == '_' || var1 == '.';
   }

   private boolean validateIdWithWildcards(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (!this.validateStringNotEmpty(var1, var2, var3, var4, var5, var6, var7, var8)) {
         return false;
      } else if (!this.isValidIdWithWildCards(var6)) {
         addViolation(var3, var4, var5, var1 + var2, var7, "with value '" + var6 + "' does not match a valid id pattern.", var8);
         return false;
      } else {
         return true;
      }
   }

   private boolean isValidIdWithWildCards(String var1) {
      for (int var2 = 0; var2 < var1.length(); var2++) {
         char var3 = var1.charAt(var2);
         if (!this.isValidIdWithWildCardCharacter(var3)) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidIdWithWildCardCharacter(char var1) {
      return this.isValidIdCharacter(var1) || var1 == '?' || var1 == '*';
   }

   private boolean validateStringNoExpression(
      String var1, ModelProblemCollector var2, ModelProblem.Severity var3, ModelProblem.Version var4, String var5, InputLocationTracker var6
   ) {
      if (!this.hasExpression(var5)) {
         return true;
      } else {
         addViolation(var2, var3, var4, var1, null, "contains an expression but should be a constant.", var6);
         return false;
      }
   }

   private boolean validateVersionNoExpression(
      String var1, ModelProblemCollector var2, ModelProblem.Severity var3, ModelProblem.Version var4, String var5, InputLocationTracker var6
   ) {
      if (!this.hasExpression(var5)) {
         return true;
      } else {
         Matcher var7 = CI_FRIENDLY_EXPRESSION.matcher(var5.trim());

         while (var7.find()) {
            String var8 = var7.group(1);
            if (!this.versionProcessor.isValidProperty(var8)) {
               addViolation(var2, var3, var4, var1, null, "contains an expression but should be a constant.", var6);
               return false;
            }
         }

         return true;
      }
   }

   private boolean hasExpression(String var1) {
      return var1 != null && var1.contains("${");
   }

   private boolean hasProjectExpression(String var1) {
      return var1 != null && var1.contains("${project.");
   }

   private boolean validateStringNotEmpty(
      String var1, ModelProblemCollector var2, ModelProblem.Severity var3, ModelProblem.Version var4, String var5, InputLocationTracker var6
   ) {
      return this.validateStringNotEmpty("", var1, var2, var3, var4, var5, null, var6);
   }

   private boolean validateStringNotEmpty(
      String var1,
      String var2,
      String var3,
      ModelProblemCollector var4,
      ModelProblem.Severity var5,
      ModelProblem.Version var6,
      String var7,
      String var8,
      InputLocationTracker var9
   ) {
      if (!this.validateNotNull(var1, var2, var3, var4, var5, var6, var7, var8, var9)) {
         return false;
      } else if (!var7.isEmpty()) {
         return true;
      } else {
         addViolation(var4, var5, var6, var1 + var2 + var3, var8, "is missing.", var9);
         return false;
      }
   }

   private boolean validateStringNotEmpty(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (!this.validateNotNull(var1, var2, var3, var4, var5, var6, var7, var8)) {
         return false;
      } else if (var6.length() > 0) {
         return true;
      } else {
         addViolation(var3, var4, var5, var1 + var2, var7, "is missing.", var8);
         return false;
      }
   }

   private boolean validateNotNull(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      Object var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (var6 != null) {
         return true;
      } else {
         addViolation(var3, var4, var5, var1 + var2, var7, "is missing.", var8);
         return false;
      }
   }

   private boolean validateNotNull(
      String var1,
      String var2,
      String var3,
      ModelProblemCollector var4,
      ModelProblem.Severity var5,
      ModelProblem.Version var6,
      Object var7,
      String var8,
      InputLocationTracker var9
   ) {
      if (var7 != null) {
         return true;
      } else {
         addViolation(var4, var5, var6, var1 + var2 + var3, var8, "is missing.", var9);
         return false;
      }
   }

   private boolean validateBoolean(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (var6 == null || var6.length() <= 0) {
         return true;
      } else if (!"true".equalsIgnoreCase(var6) && !"false".equalsIgnoreCase(var6)) {
         addViolation(var3, var4, var5, var1 + var2, var7, "must be 'true' or 'false' but is '" + var6 + "'.", var8);
         return false;
      } else {
         return true;
      }
   }

   private boolean validateEnum(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8,
      String... var9
   ) {
      if (var6 != null && var6.length() > 0) {
         List var10 = Arrays.asList(var9);
         if (var10.contains(var6)) {
            return true;
         } else {
            addViolation(var3, var4, var5, var1 + var2, var7, "must be one of " + var10 + " but is '" + var6 + "'.", var8);
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean validateModelVersion(ModelProblemCollector var1, String var2, InputLocationTracker var3, String... var4) {
      if (var2 != null && var2.length() > 0) {
         List var5 = Arrays.asList(var4);
         if (var5.contains(var2)) {
            return true;
         } else {
            boolean var6 = true;
            boolean var7 = true;

            for (String var11 : var4) {
               int var12 = compareModelVersions(var11, var2);
               var6 = var6 && var12 < 0;
               var7 = var7 && var12 > 0;
            }

            if (var6) {
               addViolation(
                  var1,
                  ModelProblem.Severity.FATAL,
                  ModelProblem.Version.V20,
                  "modelVersion",
                  null,
                  "of '"
                     + var2
                     + "' is newer than the versions supported by this version of Maven: "
                     + var5
                     + ". Building this project requires a newer version of Maven.",
                  var3
               );
            } else if (var7) {
               addViolation(
                  var1,
                  ModelProblem.Severity.FATAL,
                  ModelProblem.Version.V20,
                  "modelVersion",
                  null,
                  "of '"
                     + var2
                     + "' is older than the versions supported by this version of Maven: "
                     + var5
                     + ". Building this project requires an older version of Maven.",
                  var3
               );
            } else {
               addViolation(
                  var1, ModelProblem.Severity.ERROR, ModelProblem.Version.V20, "modelVersion", null, "must be one of " + var5 + " but is '" + var2 + "'.", var3
               );
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private static int compareModelVersions(String var0, String var1) {
      String[] var2 = StringUtils.split(var0, ".");
      String[] var3 = StringUtils.split(var1, ".");

      for (int var4 = 0; var4 < Math.min(var2.length, var3.length); var4++) {
         int var5 = Long.valueOf(var2[var4]).compareTo(Long.valueOf(var3[var4]));
         if (var5 != 0) {
            return var5;
         }
      }

      if (var2.length == var3.length) {
         return 0;
      } else {
         return var2.length > var3.length ? -1 : 1;
      }
   }

   private boolean validateBannedCharacters(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8,
      String var9
   ) {
      if (var6 != null) {
         for (int var10 = var6.length() - 1; var10 >= 0; var10--) {
            if (var9.indexOf(var6.charAt(var10)) >= 0) {
               addViolation(var3, var4, var5, var1 + var2, var7, "must not contain any of these characters " + var9 + " but found " + var6.charAt(var10), var8);
               return false;
            }
         }
      }

      return true;
   }

   private boolean validateVersion(
      String var1,
      String var2,
      ModelProblemCollector var3,
      ModelProblem.Severity var4,
      ModelProblem.Version var5,
      String var6,
      String var7,
      InputLocationTracker var8
   ) {
      if (var6 == null || var6.length() <= 0) {
         return true;
      } else if (this.hasExpression(var6)) {
         addViolation(var3, var4, var5, var1 + var2, var7, "must be a valid version but is '" + var6 + "'.", var8);
         return false;
      } else {
         return this.validateBannedCharacters(var1, var2, var3, var4, var5, var6, var7, var8, "\\/:\"<>|?*");
      }
   }

   private boolean validate20ProperSnapshotVersion(
      String var1, ModelProblemCollector var2, ModelProblem.Severity var3, ModelProblem.Version var4, String var5, String var6, InputLocationTracker var7
   ) {
      if (var5 == null || var5.length() <= 0) {
         return true;
      } else if (var5.endsWith("SNAPSHOT") && !var5.endsWith("-SNAPSHOT")) {
         addViolation(var2, var3, var4, var1, var6, "uses an unsupported snapshot version format, should be '*-SNAPSHOT' instead.", var7);
         return false;
      } else {
         return true;
      }
   }

   private boolean validate20PluginVersion(
      String var1, ModelProblemCollector var2, String var3, String var4, InputLocationTracker var5, ModelBuildingRequest var6
   ) {
      if (var3 == null) {
         return true;
      } else {
         ModelProblem.Severity var7 = getSeverity(var6, 30);
         if (!this.validateVersion("", var1, var2, var7, ModelProblem.Version.V20, var3, var4, var5)) {
            return false;
         } else if (var3.length() > 0 && !"RELEASE".equals(var3) && !"LATEST".equals(var3)) {
            return true;
         } else {
            addViolation(var2, var7, ModelProblem.Version.V20, var1, var4, "must be a valid version but is '" + var3 + "'.", var5);
            return false;
         }
      }
   }

   private static void addViolation(
      ModelProblemCollector var0, ModelProblem.Severity var1, ModelProblem.Version var2, String var3, String var4, String var5, InputLocationTracker var6
   ) {
      StringBuilder var7 = new StringBuilder(256);
      var7.append('\'').append(var3).append('\'');
      if (var4 != null) {
         var7.append(" for ").append(var4);
      }

      var7.append(' ').append(var5);
      var0.add(new ModelProblemCollectorRequest(var1, var2).setMessage(var7.toString()).setLocation(getLocation(var3, var6)));
   }

   private static InputLocation getLocation(String var0, InputLocationTracker var1) {
      InputLocation var2 = null;
      if (var1 != null) {
         if (var0 != null) {
            Object var3 = var0;
            int var4 = var0.lastIndexOf(46);
            if (var4 >= 0) {
               var0 = var0.substring(var4 + 1);
               var3 = var0;
            }

            if (var0.endsWith("]")) {
               var3 = var0.substring(var0.lastIndexOf(91) + 1, var0.length() - 1);

               try {
                  var3 = Integer.valueOf(var3.toString());
               } catch (NumberFormatException var6) {
               }
            }

            var2 = var1.getLocation(var3);
         }

         if (var2 == null) {
            var2 = var1.getLocation("");
         }
      }

      return var2;
   }

   private static boolean equals(String var0, String var1) {
      return StringUtils.clean(var0).equals(StringUtils.clean(var1));
   }

   private static ModelProblem.Severity getSeverity(ModelBuildingRequest var0, int var1) {
      return getSeverity(var0.getValidationLevel(), var1);
   }

   private static ModelProblem.Severity getSeverity(int var0, int var1) {
      return var0 < var1 ? ModelProblem.Severity.WARNING : ModelProblem.Severity.ERROR;
   }
}
