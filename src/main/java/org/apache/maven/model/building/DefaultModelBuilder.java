package org.apache.maven.model.building;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.composition.DependencyManagementImporter;
import org.apache.maven.model.inheritance.InheritanceAssembler;
import org.apache.maven.model.interpolation.ModelInterpolator;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.management.DependencyManagementInjector;
import org.apache.maven.model.management.PluginManagementInjector;
import org.apache.maven.model.normalization.ModelNormalizer;
import org.apache.maven.model.path.ModelPathTranslator;
import org.apache.maven.model.path.ModelUrlNormalizer;
import org.apache.maven.model.path.ProfileActivationFilePathInterpolator;
import org.apache.maven.model.plugin.LifecycleBindingsInjector;
import org.apache.maven.model.plugin.PluginConfigurationExpander;
import org.apache.maven.model.plugin.ReportConfigurationExpander;
import org.apache.maven.model.plugin.ReportingConverter;
import org.apache.maven.model.profile.DefaultProfileActivationContext;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.apache.maven.model.profile.ProfileInjector;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.apache.maven.model.resolution.WorkspaceModelResolver;
import org.apache.maven.model.superpom.SuperPomProvider;
import org.apache.maven.model.validation.ModelValidator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.sisu.Nullable;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelBuilder implements ModelBuilder {
   @Inject
   private ModelProcessor modelProcessor;
   @Inject
   private ModelValidator modelValidator;
   @Inject
   private ModelNormalizer modelNormalizer;
   @Inject
   private ModelInterpolator modelInterpolator;
   @Inject
   private ModelPathTranslator modelPathTranslator;
   @Inject
   private ModelUrlNormalizer modelUrlNormalizer;
   @Inject
   private SuperPomProvider superPomProvider;
   @Inject
   private InheritanceAssembler inheritanceAssembler;
   @Inject
   private ProfileSelector profileSelector;
   @Inject
   private ProfileInjector profileInjector;
   @Inject
   private PluginManagementInjector pluginManagementInjector;
   @Inject
   private DependencyManagementInjector dependencyManagementInjector;
   @Inject
   private DependencyManagementImporter dependencyManagementImporter;
   @Inject
   @Nullable
   private LifecycleBindingsInjector lifecycleBindingsInjector;
   @Inject
   private PluginConfigurationExpander pluginConfigurationExpander;
   @Inject
   private ReportConfigurationExpander reportConfigurationExpander;
   @Inject
   private ReportingConverter reportingConverter;
   @Inject
   private ProfileActivationFilePathInterpolator profileActivationFilePathInterpolator;

   public DefaultModelBuilder setModelProcessor(ModelProcessor var1) {
      this.modelProcessor = var1;
      return this;
   }

   public DefaultModelBuilder setModelValidator(ModelValidator var1) {
      this.modelValidator = var1;
      return this;
   }

   public DefaultModelBuilder setModelNormalizer(ModelNormalizer var1) {
      this.modelNormalizer = var1;
      return this;
   }

   public DefaultModelBuilder setModelInterpolator(ModelInterpolator var1) {
      this.modelInterpolator = var1;
      return this;
   }

   public DefaultModelBuilder setModelPathTranslator(ModelPathTranslator var1) {
      this.modelPathTranslator = var1;
      return this;
   }

   public DefaultModelBuilder setModelUrlNormalizer(ModelUrlNormalizer var1) {
      this.modelUrlNormalizer = var1;
      return this;
   }

   public DefaultModelBuilder setSuperPomProvider(SuperPomProvider var1) {
      this.superPomProvider = var1;
      return this;
   }

   public DefaultModelBuilder setProfileSelector(ProfileSelector var1) {
      this.profileSelector = var1;
      return this;
   }

   public DefaultModelBuilder setProfileInjector(ProfileInjector var1) {
      this.profileInjector = var1;
      return this;
   }

   public DefaultModelBuilder setInheritanceAssembler(InheritanceAssembler var1) {
      this.inheritanceAssembler = var1;
      return this;
   }

   public DefaultModelBuilder setDependencyManagementImporter(DependencyManagementImporter var1) {
      this.dependencyManagementImporter = var1;
      return this;
   }

   public DefaultModelBuilder setDependencyManagementInjector(DependencyManagementInjector var1) {
      this.dependencyManagementInjector = var1;
      return this;
   }

   public DefaultModelBuilder setLifecycleBindingsInjector(LifecycleBindingsInjector var1) {
      this.lifecycleBindingsInjector = var1;
      return this;
   }

   public DefaultModelBuilder setPluginConfigurationExpander(PluginConfigurationExpander var1) {
      this.pluginConfigurationExpander = var1;
      return this;
   }

   public DefaultModelBuilder setPluginManagementInjector(PluginManagementInjector var1) {
      this.pluginManagementInjector = var1;
      return this;
   }

   public DefaultModelBuilder setReportConfigurationExpander(ReportConfigurationExpander var1) {
      this.reportConfigurationExpander = var1;
      return this;
   }

   public DefaultModelBuilder setReportingConverter(ReportingConverter var1) {
      this.reportingConverter = var1;
      return this;
   }

   public DefaultModelBuilder setProfileActivationFilePathInterpolator(ProfileActivationFilePathInterpolator var1) {
      this.profileActivationFilePathInterpolator = var1;
      return this;
   }

   @Override
   public ModelBuildingResult build(ModelBuildingRequest var1) throws ModelBuildingException {
      return this.build(var1, new LinkedHashSet<>());
   }

   protected ModelBuildingResult build(ModelBuildingRequest var1, Collection<String> var2) throws ModelBuildingException {
      DefaultModelBuildingResult var3 = new DefaultModelBuildingResult();
      DefaultModelProblemCollector var4 = new DefaultModelProblemCollector(var3);
      DefaultProfileActivationContext var5 = this.getProfileActivationContext(var1);
      var4.setSource("(external profiles)");
      List var6 = this.profileSelector.getActiveProfiles(var1.getProfiles(), var5, var4);
      var3.setActiveExternalProfiles(var6);
      if (!var6.isEmpty()) {
         Properties var7 = new Properties();

         for (Profile var9 : var6) {
            var7.putAll(var9.getProperties());
         }

         var7.putAll(var5.getUserProperties());
         var5.setUserProperties(var7);
      }

      Model var21 = var1.getRawModel();
      if (var21 == null) {
         var21 = this.readModel(var1.getModelSource(), var1.getPomFile(), var1, var4);
      }

      var4.setRootModel(var21);
      ModelData var22 = new ModelData(var1.getModelSource(), var21);
      ModelData var23 = new ModelData(null, this.getSuperModel());
      LinkedHashSet var10 = new LinkedHashSet();
      ArrayList var11 = new ArrayList();
      ModelData var12 = var22;

      while (var12 != null) {
         var11.add(var12);
         Model var13 = var12.getModel();
         var12.setRawModel(var13);
         Model var14 = var13.clone();
         var12.setModel(var14);
         var4.setSource(var14);
         this.modelNormalizer.mergeDuplicates(var14, var1, var4);
         var5.setProjectProperties(var14.getProperties());
         List var15 = this.profileSelector.getActiveProfiles(var13.getProfiles(), var5, var4);
         var12.setActiveProfiles(var15);
         Map var16 = this.getInterpolatedActivations(var13, var5, var4);
         this.injectProfileActivations(var14, var16);

         for (Profile var18 : var15) {
            this.profileInjector.injectProfile(var14, var18, var1, var4);
         }

         if (var12 == var22) {
            for (Profile var33 : var6) {
               this.profileInjector.injectProfile(var14, var33, var1, var4);
            }
         }

         if (var12 == var23) {
            break;
         }

         this.configureResolver(var1.getModelResolver(), var14, var4);
         ModelData var32 = this.readParent(var14, var12.getSource(), var1, var4);
         if (var32 == null) {
            var12 = var23;
         } else if (var12 == var22) {
            var12.setGroupId(var12.getRawModel().getGroupId() == null ? var32.getGroupId() : var12.getRawModel().getGroupId());
            var12.setVersion(var12.getRawModel().getVersion() == null ? var32.getVersion() : var12.getRawModel().getVersion());
            var12.setArtifactId(var12.getRawModel().getArtifactId());
            var10.add(var12.getId());
            var12.setGroupId(null);
            var12.setArtifactId(null);
            var12.setVersion(null);
            var12 = var32;
         } else {
            if (!var10.add(var32.getId())) {
               String var34 = "The parents form a cycle: ";

               for (String var20 : var10) {
                  var34 = var34 + var20 + " -> ";
               }

               var34 = var34 + var32.getId();
               var4.add(new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE).setMessage(var34));
               throw var4.newModelBuildingException();
            }

            var12 = var32;
         }
      }

      var4.setSource(var21);
      this.checkPluginVersions(var11, var1, var4);
      this.assembleInheritance(var11, var1, var4);
      Model var24 = var22.getModel();
      var4.setSource(var24);
      var4.setRootModel(var24);
      Model var25 = this.interpolateModel(var24, var1, var4);
      var22.setModel(var25);
      if (var25.getParent() != null) {
         ModelData var26 = (ModelData)var11.get(1);
         if (var26.getVersion() == null || var26.getVersion().contains("${")) {
            Model var28 = this.interpolateModel(var26.getModel(), var1, var4);
            var26.setVersion(var28.getVersion());
         }
      }

      this.modelUrlNormalizer.normalize(var25, var1);
      this.configureResolver(var1.getModelResolver(), var25, var4, true);
      var22.setGroupId(var25.getGroupId());
      var22.setArtifactId(var25.getArtifactId());
      var22.setVersion(var25.getVersion());
      var3.setEffectiveModel(var25);

      for (ModelData var29 : var11) {
         String var30 = var29 != var23 ? var29.getId() : "";
         var3.addModelId(var30);
         var3.setActivePomProfiles(var30, var29.getActiveProfiles());
         var3.setRawModel(var30, var29.getRawModel());
      }

      if (!var1.isTwoPhaseBuilding()) {
         this.build(var1, var3, var2);
      }

      return var3;
   }

   private Map<String, Activation> getInterpolatedActivations(Model var1, DefaultProfileActivationContext var2, DefaultModelProblemCollector var3) {
      Map var4 = this.getProfileActivations(var1, true);

      for (Activation var6 : var4.values()) {
         if (var6.getFile() != null) {
            this.replaceWithInterpolatedValue(var6.getFile(), var2, var3);
         }
      }

      return var4;
   }

   private void replaceWithInterpolatedValue(ActivationFile var1, ProfileActivationContext var2, DefaultModelProblemCollector var3) {
      try {
         if (StringUtils.isNotEmpty(var1.getExists())) {
            String var4 = var1.getExists();
            String var8 = this.profileActivationFilePathInterpolator.interpolate(var4, var2);
            var1.setExists(var8);
         } else if (StringUtils.isNotEmpty(var1.getMissing())) {
            String var7 = var1.getMissing();
            String var9 = this.profileActivationFilePathInterpolator.interpolate(var7, var2);
            var1.setMissing(var9);
         }
      } catch (InterpolationException var6) {
         String var5 = StringUtils.isNotEmpty(var1.getExists()) ? var1.getExists() : var1.getMissing();
         var3.add(
            new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
               .setMessage("Failed to interpolate file location " + var5 + ": " + var6.getMessage())
               .setLocation(var1.getLocation(StringUtils.isNotEmpty(var1.getExists()) ? "exists" : "missing"))
               .setException(var6)
         );
      }
   }

   @Override
   public ModelBuildingResult build(ModelBuildingRequest var1, ModelBuildingResult var2) throws ModelBuildingException {
      return this.build(var1, var2, new LinkedHashSet<>());
   }

   private ModelBuildingResult build(ModelBuildingRequest var1, ModelBuildingResult var2, Collection<String> var3) throws ModelBuildingException {
      Model var4 = var2.getEffectiveModel();
      DefaultModelProblemCollector var5 = new DefaultModelProblemCollector(var2);
      var5.setSource(var4);
      var5.setRootModel(var4);
      this.modelPathTranslator.alignToBaseDirectory(var4, var4.getProjectDirectory(), var1);
      this.pluginManagementInjector.injectManagement(var4, var1, var5);
      this.fireEvent(var4, var1, var5, ModelBuildingEventCatapult.BUILD_EXTENSIONS_ASSEMBLED);
      if (var1.isProcessPlugins()) {
         if (this.lifecycleBindingsInjector == null) {
            throw new IllegalStateException("lifecycle bindings injector is missing");
         }

         this.lifecycleBindingsInjector.injectLifecycleBindings(var4, var1, var5);
      }

      this.importDependencyManagement(var4, var1, var5, var3);
      this.dependencyManagementInjector.injectManagement(var4, var1, var5);
      this.modelNormalizer.injectDefaultValues(var4, var1, var5);
      if (var1.isProcessPlugins()) {
         this.reportConfigurationExpander.expandPluginConfiguration(var4, var1, var5);
         this.reportingConverter.convertReporting(var4, var1, var5);
         this.pluginConfigurationExpander.expandPluginConfiguration(var4, var1, var5);
      }

      this.modelValidator.validateEffectiveModel(var4, var1, var5);
      if (this.hasModelErrors(var5)) {
         throw var5.newModelBuildingException();
      } else {
         return var2;
      }
   }

   @Override
   public Result<? extends Model> buildRawModel(File var1, int var2, boolean var3) {
      DefaultModelBuildingRequest var4 = new DefaultModelBuildingRequest().setValidationLevel(var2).setLocationTracking(var3);
      DefaultModelProblemCollector var5 = new DefaultModelProblemCollector(new DefaultModelBuildingResult());

      try {
         return Result.newResult(this.readModel(null, var1, var4, var5), var5.getProblems());
      } catch (ModelBuildingException var7) {
         return Result.error(var5.getProblems());
      }
   }

   private Model readModel(ModelSource var1, File var2, ModelBuildingRequest var3, DefaultModelProblemCollector var4) throws ModelBuildingException {
      if (var1 == null) {
         if (var2 == null) {
            throw new NullPointerException("neither pomFile nor modelSource can be null");
         }

         var1 = new FileModelSource(var2);
      }

      var4.setSource(((ModelSource)var1).getLocation());

      Model var5;
      try {
         boolean var6 = var3.getValidationLevel() >= 20;
         InputSource var15 = var3.isLocationTracking() ? new InputSource() : null;
         HashMap var8 = new HashMap();
         var8.put("org.apache.maven.model.io.isStrict", var6);
         var8.put("org.apache.maven.model.io.inputSource", var15);
         var8.put("org.apache.maven.model.building.source", var1);

         try {
            var5 = this.modelProcessor.read(((ModelSource)var1).getInputStream(), var8);
         } catch (ModelParseException var12) {
            if (!var6) {
               throw var12;
            }

            var8.put("org.apache.maven.model.io.isStrict", Boolean.FALSE);

            try {
               var5 = this.modelProcessor.read(((ModelSource)var1).getInputStream(), var8);
            } catch (ModelParseException var11) {
               throw var12;
            }

            if (var2 != null) {
               var4.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.V20)
                     .setMessage("Malformed POM " + ((ModelSource)var1).getLocation() + ": " + var12.getMessage())
                     .setException(var12)
               );
            } else {
               var4.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20)
                     .setMessage("Malformed POM " + ((ModelSource)var1).getLocation() + ": " + var12.getMessage())
                     .setException(var12)
               );
            }
         }

         if (var15 != null) {
            var15.setModelId(ModelProblemUtils.toId(var5));
            var15.setLocation(((ModelSource)var1).getLocation());
         }
      } catch (ModelParseException var13) {
         var4.add(
            new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE)
               .setMessage("Non-parseable POM " + ((ModelSource)var1).getLocation() + ": " + var13.getMessage())
               .setException(var13)
         );
         throw var4.newModelBuildingException();
      } catch (IOException var14) {
         String var7 = var14.getMessage();
         if (var7 == null || var7.length() <= 0) {
            if (var14.getClass().getName().endsWith("MalformedInputException")) {
               var7 = "Some input bytes do not match the file encoding.";
            } else {
               var7 = var14.getClass().getSimpleName();
            }
         }

         var4.add(
            new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE)
               .setMessage("Non-readable POM " + ((ModelSource)var1).getLocation() + ": " + var7)
               .setException(var14)
         );
         throw var4.newModelBuildingException();
      }

      var5.setPomFile(var2);
      var4.setSource(var5);
      this.modelValidator.validateRawModel(var5, var3, var4);
      if (this.hasFatalErrors(var4)) {
         throw var4.newModelBuildingException();
      } else {
         return var5;
      }
   }

   private DefaultProfileActivationContext getProfileActivationContext(ModelBuildingRequest var1) {
      DefaultProfileActivationContext var2 = new DefaultProfileActivationContext();
      var2.setActiveProfileIds(var1.getActiveProfileIds());
      var2.setInactiveProfileIds(var1.getInactiveProfileIds());
      var2.setSystemProperties(var1.getSystemProperties());
      var2.setUserProperties(var1.getUserProperties());
      var2.setProjectDirectory(var1.getPomFile() != null ? var1.getPomFile().getParentFile() : null);
      return var2;
   }

   private void configureResolver(ModelResolver var1, Model var2, DefaultModelProblemCollector var3) {
      this.configureResolver(var1, var2, var3, false);
   }

   private void configureResolver(ModelResolver var1, Model var2, DefaultModelProblemCollector var3, boolean var4) {
      if (var1 != null) {
         var3.setSource(var2);

         for (Repository var7 : var2.getRepositories()) {
            try {
               var1.addRepository(var7, var4);
            } catch (InvalidRepositoryException var9) {
               var3.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                     .setMessage("Invalid repository " + var7.getId() + ": " + var9.getMessage())
                     .setLocation(var7.getLocation(""))
                     .setException(var9)
               );
            }
         }
      }
   }

   private void checkPluginVersions(List<ModelData> var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      if (var2.getValidationLevel() >= 20) {
         HashMap var4 = new HashMap();
         HashMap var5 = new HashMap();
         HashMap var6 = new HashMap();

         for (int var7 = var1.size() - 1; var7 >= 0; var7--) {
            Model var8 = ((ModelData)var1.get(var7)).getModel();
            Build var9 = var8.getBuild();
            if (var9 != null) {
               for (Plugin var11 : var9.getPlugins()) {
                  String var12 = var11.getKey();
                  if (var5.get(var12) == null) {
                     var5.put(var12, var11.getVersion());
                     var4.put(var12, var11);
                  }
               }

               PluginManagement var17 = var9.getPluginManagement();
               if (var17 != null) {
                  for (Plugin var19 : var17.getPlugins()) {
                     String var13 = var19.getKey();
                     if (var6.get(var13) == null) {
                        var6.put(var13, var19.getVersion());
                     }
                  }
               }
            }
         }

         for (String var15 : var5.keySet()) {
            if (var5.get(var15) == null && var6.get(var15) == null) {
               InputLocation var16 = ((Plugin)var4.get(var15)).getLocation("");
               var3.add(
                  new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.V20)
                     .setMessage("'build.plugins.plugin.version' for " + var15 + " is missing.")
                     .setLocation(var16)
               );
            }
         }
      }
   }

   private void assembleInheritance(List<ModelData> var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      for (int var4 = var1.size() - 2; var4 >= 0; var4--) {
         Model var5 = ((ModelData)var1.get(var4 + 1)).getModel();
         Model var6 = ((ModelData)var1.get(var4)).getModel();
         this.inheritanceAssembler.assembleModelInheritance(var6, var5, var2, var3);
      }
   }

   private Map<String, Activation> getProfileActivations(Model var1, boolean var2) {
      HashMap var3 = new HashMap();

      for (Profile var5 : var1.getProfiles()) {
         Activation var6 = var5.getActivation();
         if (var6 != null) {
            if (var2) {
               var6 = var6.clone();
            }

            var3.put(var5.getId(), var6);
         }
      }

      return var3;
   }

   private void injectProfileActivations(Model var1, Map<String, Activation> var2) {
      for (Profile var4 : var1.getProfiles()) {
         Activation var5 = var4.getActivation();
         if (var5 != null) {
            var4.setActivation((Activation)var2.get(var4.getId()));
         }
      }
   }

   private Model interpolateModel(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Map var4 = this.getProfileActivations(var1, true);
      Model var5 = this.modelInterpolator.interpolateModel(var1, var1.getProjectDirectory(), var2, var3);
      if (var5.getParent() != null) {
         StringSearchInterpolator var6 = new StringSearchInterpolator();
         var6.addValueSource(new MapBasedValueSource(var2.getUserProperties()));
         var6.addValueSource(new MapBasedValueSource(var1.getProperties()));
         var6.addValueSource(new MapBasedValueSource(var2.getSystemProperties()));

         try {
            String var7 = var6.interpolate(var5.getParent().getVersion());
            var5.getParent().setVersion(var7);
         } catch (Exception var9) {
            ModelProblemCollectorRequest var8 = new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
               .setMessage("Failed to interpolate field: " + var5.getParent().getVersion() + " on class: ")
               .setException(var9);
            var3.add(var8);
         }
      }

      var5.setPomFile(var1.getPomFile());
      this.injectProfileActivations(var1, var4);
      return var5;
   }

   private ModelData readParent(Model var1, ModelSource var2, ModelBuildingRequest var3, DefaultModelProblemCollector var4) throws ModelBuildingException {
      Parent var6 = var1.getParent();
      ModelData var5;
      if (var6 != null) {
         String var7 = var6.getGroupId();
         String var8 = var6.getArtifactId();
         String var9 = var6.getVersion();
         var5 = this.getCache(var3.getModelCache(), var7, var8, var9, ModelCacheTag.RAW);
         if (var5 == null) {
            var5 = this.readParentLocally(var1, var2, var3, var4);
            if (var5 == null) {
               var5 = this.readParentExternally(var1, var3, var4);
            }

            this.putCache(var3.getModelCache(), var7, var8, var9, ModelCacheTag.RAW, var5);
         } else {
            File var10 = var5.getModel().getPomFile();
            if (var10 != null) {
               FileModelSource var11 = new FileModelSource(var10);
               ModelSource var12 = this.getParentPomFile(var1, var2);
               if (var12 == null || var12 instanceof ModelSource2 && !var11.equals(var12)) {
                  var5 = this.readParentExternally(var1, var3, var4);
               }
            }
         }

         Model var13 = var5.getModel();
         if (!"pom".equals(var13.getPackaging())) {
            var4.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                  .setMessage(
                     "Invalid packaging for parent POM " + ModelProblemUtils.toSourceHint(var13) + ", must be \"pom\" but is \"" + var13.getPackaging() + "\""
                  )
                  .setLocation(var13.getLocation("packaging"))
            );
         }
      } else {
         var5 = null;
      }

      return var5;
   }

   private ModelData readParentLocally(Model var1, ModelSource var2, ModelBuildingRequest var3, DefaultModelProblemCollector var4) throws ModelBuildingException {
      Parent var5 = var1.getParent();
      WorkspaceModelResolver var8 = var3.getWorkspaceModelResolver();
      Object var6;
      Model var7;
      if (var8 == null) {
         var6 = this.getParentPomFile(var1, var2);
         if (var6 == null) {
            return null;
         }

         File var9 = null;
         if (var6 instanceof FileModelSource) {
            var9 = ((FileModelSource)var6).getPomFile();
         }

         var7 = this.readModel((ModelSource)var6, var9, var3, var4);
      } else {
         try {
            var7 = var8.resolveRawModel(var5.getGroupId(), var5.getArtifactId(), var5.getVersion());
         } catch (UnresolvableModelException var15) {
            var4.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE)
                  .setMessage(var15.getMessage().toString())
                  .setLocation(var5.getLocation(""))
                  .setException(var15)
            );
            throw var4.newModelBuildingException();
         }

         if (var7 == null) {
            return null;
         }

         var6 = new FileModelSource(var7.getPomFile());
      }

      String var16 = var7.getGroupId();
      if (var16 == null && var7.getParent() != null) {
         var16 = var7.getParent().getGroupId();
      }

      String var10 = var7.getArtifactId();
      String var11 = var7.getVersion();
      if (var11 == null && var7.getParent() != null) {
         var11 = var7.getParent().getVersion();
      }

      if (var16 == null || !var16.equals(var5.getGroupId()) || var10 == null || !var10.equals(var5.getArtifactId())) {
         StringBuilder var17 = new StringBuilder(256);
         var17.append("'parent.relativePath'");
         if (var1 != var4.getRootModel()) {
            var17.append(" of POM ").append(ModelProblemUtils.toSourceHint(var1));
         }

         var17.append(" points at ").append(var16).append(':').append(var10);
         var17.append(" instead of ").append(var5.getGroupId()).append(':');
         var17.append(var5.getArtifactId()).append(", please verify your project structure");
         var4.setSource(var1);
         var4.add(
            new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.BASE)
               .setMessage(var17.toString())
               .setLocation(var5.getLocation(""))
         );
         return null;
      } else {
         if (var11 != null && var5.getVersion() != null && !var11.equals(var5.getVersion())) {
            try {
               VersionRange var12 = VersionRange.createFromVersionSpec(var5.getVersion());
               if (!var12.hasRestrictions()) {
                  return null;
               }

               if (!var12.containsVersion(new DefaultArtifactVersion(var11))) {
                  return null;
               }

               String var13 = var1.getVersion();
               if (var13 == null) {
                  var4.add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31)
                        .setMessage("Version must be a constant")
                        .setLocation(var1.getLocation(""))
                  );
               } else if (this.rawChildVersionReferencesParent(var13)) {
                  var4.add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31)
                        .setMessage("Version must be a constant")
                        .setLocation(var1.getLocation("version"))
                  );
               }
            } catch (InvalidVersionSpecificationException var14) {
               return null;
            }
         }

         return new ModelData((ModelSource)var6, var7, var16, var10, var11);
      }
   }

   private boolean rawChildVersionReferencesParent(String var1) {
      return var1.equals("${pom.version}")
         || var1.equals("${project.version}")
         || var1.equals("${pom.parent.version}")
         || var1.equals("${project.parent.version}");
   }

   private ModelSource getParentPomFile(Model var1, ModelSource var2) {
      if (!(var2 instanceof ModelSource2)) {
         return null;
      } else {
         String var3 = var1.getParent().getRelativePath();
         return var3 != null && var3.length() > 0 ? ((ModelSource2)var2).getRelatedSource(var3) : null;
      }
   }

   private ModelData readParentExternally(Model var1, ModelBuildingRequest var2, DefaultModelProblemCollector var3) throws ModelBuildingException {
      var3.setSource(var1);
      Parent var4 = var1.getParent().clone();
      String var5 = var4.getGroupId();
      String var6 = var4.getArtifactId();
      String var7 = var4.getVersion();
      ModelResolver var8 = var2.getModelResolver();
      Objects.requireNonNull(
         var8,
         String.format(
            "request.modelResolver cannot be null (parent POM %s and POM %s)", ModelProblemUtils.toId(var5, var6, var7), ModelProblemUtils.toSourceHint(var1)
         )
      );

      ModelSource var9;
      try {
         var9 = var8.resolveModel(var4);
      } catch (UnresolvableModelException var13) {
         StringBuilder var11 = new StringBuilder(256);
         var11.append("Non-resolvable parent POM");
         if (!this.containsCoordinates(var13.getMessage(), var5, var6, var7)) {
            var11.append(' ').append(ModelProblemUtils.toId(var5, var6, var7));
         }

         if (var1 != var3.getRootModel()) {
            var11.append(" for ").append(ModelProblemUtils.toId(var1));
         }

         var11.append(": ").append(var13.getMessage());
         if (var1.getProjectDirectory() != null) {
            if (var4.getRelativePath() != null && var4.getRelativePath().length() > 0) {
               var11.append(" and 'parent.relativePath' points at wrong local POM");
            } else {
               var11.append(" and 'parent.relativePath' points at no local POM");
            }
         }

         var3.add(
            new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE)
               .setMessage(var11.toString())
               .setLocation(var4.getLocation(""))
               .setException(var13)
         );
         throw var3.newModelBuildingException();
      }

      Object var10 = var2;
      if (var2.getValidationLevel() > 20) {
         var10 = new FilterModelBuildingRequest(var2) {
            @Override
            public int getValidationLevel() {
               return 20;
            }
         };
      }

      Model var14 = this.readModel(var9, null, (ModelBuildingRequest)var10, var3);
      if (!var4.getVersion().equals(var7)) {
         String var12 = var1.getVersion();
         if (var12 == null) {
            var3.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31)
                  .setMessage("Version must be a constant")
                  .setLocation(var1.getLocation(""))
            );
         } else if (this.rawChildVersionReferencesParent(var12)) {
            var3.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.V31)
                  .setMessage("Version must be a constant")
                  .setLocation(var1.getLocation("version"))
            );
         }
      }

      return new ModelData(var9, var14, var4.getGroupId(), var4.getArtifactId(), var4.getVersion());
   }

   private Model getSuperModel() {
      return this.superPomProvider.getSuperModel("4.0.0").clone();
   }

   private void importDependencyManagement(Model var1, ModelBuildingRequest var2, DefaultModelProblemCollector var3, Collection<String> var4) {
      DependencyManagement var5 = var1.getDependencyManagement();
      if (var5 != null) {
         String var6 = var1.getGroupId() + ':' + var1.getArtifactId() + ':' + var1.getVersion();
         var4.add(var6);
         WorkspaceModelResolver var7 = var2.getWorkspaceModelResolver();
         ModelResolver var8 = var2.getModelResolver();
         DefaultModelBuildingRequest var9 = null;
         ArrayList var10 = null;
         Iterator var11 = var5.getDependencies().iterator();

         while (var11.hasNext()) {
            Dependency var12 = (Dependency)var11.next();
            if ("pom".equals(var12.getType()) && "import".equals(var12.getScope())) {
               var11.remove();
               String var13 = var12.getGroupId();
               String var14 = var12.getArtifactId();
               String var15 = var12.getVersion();
               if (var13 == null || var13.length() <= 0) {
                  var3.add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                        .setMessage("'dependencyManagement.dependencies.dependency.groupId' for " + var12.getManagementKey() + " is missing.")
                        .setLocation(var12.getLocation(""))
                  );
               } else if (var14 == null || var14.length() <= 0) {
                  var3.add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                        .setMessage("'dependencyManagement.dependencies.dependency.artifactId' for " + var12.getManagementKey() + " is missing.")
                        .setLocation(var12.getLocation(""))
                  );
               } else if (var15 != null && var15.length() > 0) {
                  String var16 = var13 + ':' + var14 + ':' + var15;
                  if (var4.contains(var16)) {
                     String var25 = "The dependencies of type=pom and with scope=import form a cycle: ";

                     for (String var28 : var4) {
                        var25 = var25 + var28 + " -> ";
                     }

                     var25 = var25 + var16;
                     var3.add(new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE).setMessage(var25));
                  } else {
                     DependencyManagement var17 = this.getCache(var2.getModelCache(), var13, var14, var15, ModelCacheTag.IMPORT);
                     if (var17 == null) {
                        if (var7 == null && var8 == null) {
                           throw new NullPointerException(
                              String.format(
                                 "request.workspaceModelResolver and request.modelResolver cannot be null (parent POM %s and POM %s)",
                                 ModelProblemUtils.toId(var13, var14, var15),
                                 ModelProblemUtils.toSourceHint(var1)
                              )
                           );
                        }

                        Model var18 = null;
                        if (var7 != null) {
                           try {
                              var18 = var7.resolveEffectiveModel(var13, var14, var15);
                           } catch (UnresolvableModelException var23) {
                              var3.add(
                                 new ModelProblemCollectorRequest(ModelProblem.Severity.FATAL, ModelProblem.Version.BASE)
                                    .setMessage(var23.getMessage().toString())
                                    .setException(var23)
                              );
                              continue;
                           }
                        }

                        if (var18 == null) {
                           ModelSource var19;
                           try {
                              var19 = var8.resolveModel(var13, var14, var15);
                           } catch (UnresolvableModelException var24) {
                              StringBuilder var21 = new StringBuilder(256);
                              var21.append("Non-resolvable import POM");
                              if (!this.containsCoordinates(var24.getMessage(), var13, var14, var15)) {
                                 var21.append(' ').append(ModelProblemUtils.toId(var13, var14, var15));
                              }

                              var21.append(": ").append(var24.getMessage());
                              var3.add(
                                 new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                                    .setMessage(var21.toString())
                                    .setLocation(var12.getLocation(""))
                                    .setException(var24)
                              );
                              continue;
                           }

                           if (var9 == null) {
                              var9 = new DefaultModelBuildingRequest();
                              var9.setValidationLevel(0);
                              var9.setModelCache(var2.getModelCache());
                              var9.setSystemProperties(var2.getSystemProperties());
                              var9.setUserProperties(var2.getUserProperties());
                              var9.setLocationTracking(var2.isLocationTracking());
                           }

                           var9.setModelSource(var19);
                           var9.setModelResolver(var8.newCopy());

                           ModelBuildingResult var20;
                           try {
                              var20 = this.build(var9, var4);
                           } catch (ModelBuildingException var22) {
                              var3.addAll(var22.getProblems());
                              continue;
                           }

                           var3.addAll(var20.getProblems());
                           var18 = var20.getEffectiveModel();
                        }

                        var17 = var18.getDependencyManagement();
                        if (var17 == null) {
                           var17 = new DependencyManagement();
                        }

                        this.putCache(var2.getModelCache(), var13, var14, var15, ModelCacheTag.IMPORT, var17);
                     }

                     if (var10 == null) {
                        var10 = new ArrayList();
                     }

                     var10.add(var17);
                  }
               } else {
                  var3.add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                        .setMessage("'dependencyManagement.dependencies.dependency.version' for " + var12.getManagementKey() + " is missing.")
                        .setLocation(var12.getLocation(""))
                  );
               }
            }
         }

         var4.remove(var6);
         this.dependencyManagementImporter.importManagement(var1, var10, var2, var3);
      }
   }

   private <T> void putCache(ModelCache var1, String var2, String var3, String var4, ModelCacheTag<T> var5, T var6) {
      if (var1 != null) {
         var1.put(var2, var3, var4, var5.getName(), var5.intoCache(var6));
      }
   }

   private <T> T getCache(ModelCache var1, String var2, String var3, String var4, ModelCacheTag<T> var5) {
      if (var1 != null) {
         Object var6 = var1.get(var2, var3, var4, var5.getName());
         if (var6 != null) {
            return (T)var5.fromCache(var5.getType().cast(var6));
         }
      }

      return null;
   }

   private void fireEvent(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3, ModelBuildingEventCatapult var4) throws ModelBuildingException {
      ModelBuildingListener var5 = var2.getModelBuildingListener();
      if (var5 != null) {
         DefaultModelBuildingEvent var6 = new DefaultModelBuildingEvent(var1, var2, var3);
         var4.fire(var5, var6);
      }
   }

   private boolean containsCoordinates(String var1, String var2, String var3, String var4) {
      return var1 != null && (var2 == null || var1.contains(var2)) && (var3 == null || var1.contains(var3)) && (var4 == null || var1.contains(var4));
   }

   protected boolean hasModelErrors(ModelProblemCollectorExt var1) {
      if (var1 instanceof DefaultModelProblemCollector) {
         return ((DefaultModelProblemCollector)var1).hasErrors();
      } else {
         throw new IllegalStateException();
      }
   }

   protected boolean hasFatalErrors(ModelProblemCollectorExt var1) {
      if (var1 instanceof DefaultModelProblemCollector) {
         return ((DefaultModelProblemCollector)var1).hasFatalErrors();
      } else {
         throw new IllegalStateException();
      }
   }
}
