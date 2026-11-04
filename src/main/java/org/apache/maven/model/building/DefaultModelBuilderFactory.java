package org.apache.maven.model.building;

import org.apache.maven.model.Model;
import org.apache.maven.model.composition.DefaultDependencyManagementImporter;
import org.apache.maven.model.composition.DependencyManagementImporter;
import org.apache.maven.model.inheritance.DefaultInheritanceAssembler;
import org.apache.maven.model.inheritance.InheritanceAssembler;
import org.apache.maven.model.interpolation.DefaultModelVersionProcessor;
import org.apache.maven.model.interpolation.ModelInterpolator;
import org.apache.maven.model.interpolation.ModelVersionProcessor;
import org.apache.maven.model.interpolation.StringVisitorModelInterpolator;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.locator.DefaultModelLocator;
import org.apache.maven.model.locator.ModelLocator;
import org.apache.maven.model.management.DefaultDependencyManagementInjector;
import org.apache.maven.model.management.DefaultPluginManagementInjector;
import org.apache.maven.model.management.DependencyManagementInjector;
import org.apache.maven.model.management.PluginManagementInjector;
import org.apache.maven.model.normalization.DefaultModelNormalizer;
import org.apache.maven.model.normalization.ModelNormalizer;
import org.apache.maven.model.path.DefaultModelPathTranslator;
import org.apache.maven.model.path.DefaultModelUrlNormalizer;
import org.apache.maven.model.path.DefaultPathTranslator;
import org.apache.maven.model.path.DefaultUrlNormalizer;
import org.apache.maven.model.path.ModelPathTranslator;
import org.apache.maven.model.path.ModelUrlNormalizer;
import org.apache.maven.model.path.PathTranslator;
import org.apache.maven.model.path.ProfileActivationFilePathInterpolator;
import org.apache.maven.model.path.UrlNormalizer;
import org.apache.maven.model.plugin.DefaultPluginConfigurationExpander;
import org.apache.maven.model.plugin.DefaultReportConfigurationExpander;
import org.apache.maven.model.plugin.DefaultReportingConverter;
import org.apache.maven.model.plugin.LifecycleBindingsInjector;
import org.apache.maven.model.plugin.PluginConfigurationExpander;
import org.apache.maven.model.plugin.ReportConfigurationExpander;
import org.apache.maven.model.plugin.ReportingConverter;
import org.apache.maven.model.profile.DefaultProfileInjector;
import org.apache.maven.model.profile.DefaultProfileSelector;
import org.apache.maven.model.profile.ProfileInjector;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.model.profile.activation.FileProfileActivator;
import org.apache.maven.model.profile.activation.JdkVersionProfileActivator;
import org.apache.maven.model.profile.activation.OperatingSystemProfileActivator;
import org.apache.maven.model.profile.activation.ProfileActivator;
import org.apache.maven.model.profile.activation.PropertyProfileActivator;
import org.apache.maven.model.superpom.DefaultSuperPomProvider;
import org.apache.maven.model.superpom.SuperPomProvider;
import org.apache.maven.model.validation.DefaultModelValidator;
import org.apache.maven.model.validation.ModelValidator;

public class DefaultModelBuilderFactory {
   protected ModelProcessor newModelProcessor() {
      DefaultModelProcessor var1 = new DefaultModelProcessor();
      var1.setModelLocator(this.newModelLocator());
      var1.setModelReader(this.newModelReader());
      return var1;
   }

   protected ModelLocator newModelLocator() {
      return new DefaultModelLocator();
   }

   protected ModelReader newModelReader() {
      return new DefaultModelReader();
   }

   protected ProfileSelector newProfileSelector() {
      DefaultProfileSelector var1 = new DefaultProfileSelector();

      for (ProfileActivator var5 : this.newProfileActivators()) {
         var1.addProfileActivator(var5);
      }

      return var1;
   }

   protected ProfileActivator[] newProfileActivators() {
      return new ProfileActivator[]{
         new JdkVersionProfileActivator(),
         new OperatingSystemProfileActivator(),
         new PropertyProfileActivator(),
         new FileProfileActivator().setProfileActivationFilePathInterpolator(this.newProfileActivationFilePathInterpolator())
      };
   }

   protected ProfileActivationFilePathInterpolator newProfileActivationFilePathInterpolator() {
      return new ProfileActivationFilePathInterpolator().setPathTranslator(this.newPathTranslator());
   }

   protected UrlNormalizer newUrlNormalizer() {
      return new DefaultUrlNormalizer();
   }

   protected PathTranslator newPathTranslator() {
      return new DefaultPathTranslator();
   }

   protected ModelInterpolator newModelInterpolator() {
      UrlNormalizer var1 = this.newUrlNormalizer();
      PathTranslator var2 = this.newPathTranslator();
      return new StringVisitorModelInterpolator()
         .setPathTranslator(var2)
         .setUrlNormalizer(var1)
         .setVersionPropertiesProcessor(this.newModelVersionPropertiesProcessor());
   }

   protected ModelVersionProcessor newModelVersionPropertiesProcessor() {
      return new DefaultModelVersionProcessor();
   }

   protected ModelValidator newModelValidator() {
      return new DefaultModelValidator(this.newModelVersionPropertiesProcessor());
   }

   protected ModelNormalizer newModelNormalizer() {
      return new DefaultModelNormalizer();
   }

   protected ModelPathTranslator newModelPathTranslator() {
      return new DefaultModelPathTranslator().setPathTranslator(this.newPathTranslator());
   }

   protected ModelUrlNormalizer newModelUrlNormalizer() {
      return new DefaultModelUrlNormalizer().setUrlNormalizer(this.newUrlNormalizer());
   }

   protected InheritanceAssembler newInheritanceAssembler() {
      return new DefaultInheritanceAssembler();
   }

   protected ProfileInjector newProfileInjector() {
      return new DefaultProfileInjector();
   }

   protected SuperPomProvider newSuperPomProvider() {
      return new DefaultSuperPomProvider().setModelProcessor(this.newModelProcessor());
   }

   protected DependencyManagementImporter newDependencyManagementImporter() {
      return new DefaultDependencyManagementImporter();
   }

   protected DependencyManagementInjector newDependencyManagementInjector() {
      return new DefaultDependencyManagementInjector();
   }

   protected LifecycleBindingsInjector newLifecycleBindingsInjector() {
      return new DefaultModelBuilderFactory.StubLifecycleBindingsInjector();
   }

   protected PluginManagementInjector newPluginManagementInjector() {
      return new DefaultPluginManagementInjector();
   }

   protected PluginConfigurationExpander newPluginConfigurationExpander() {
      return new DefaultPluginConfigurationExpander();
   }

   protected ReportConfigurationExpander newReportConfigurationExpander() {
      return new DefaultReportConfigurationExpander();
   }

   protected ReportingConverter newReportingConverter() {
      return new DefaultReportingConverter();
   }

   public DefaultModelBuilder newInstance() {
      DefaultModelBuilder var1 = new DefaultModelBuilder();
      var1.setModelProcessor(this.newModelProcessor());
      var1.setModelValidator(this.newModelValidator());
      var1.setModelNormalizer(this.newModelNormalizer());
      var1.setModelPathTranslator(this.newModelPathTranslator());
      var1.setModelUrlNormalizer(this.newModelUrlNormalizer());
      var1.setModelInterpolator(this.newModelInterpolator());
      var1.setInheritanceAssembler(this.newInheritanceAssembler());
      var1.setProfileInjector(this.newProfileInjector());
      var1.setProfileSelector(this.newProfileSelector());
      var1.setSuperPomProvider(this.newSuperPomProvider());
      var1.setDependencyManagementImporter(this.newDependencyManagementImporter());
      var1.setDependencyManagementInjector(this.newDependencyManagementInjector());
      var1.setLifecycleBindingsInjector(this.newLifecycleBindingsInjector());
      var1.setPluginManagementInjector(this.newPluginManagementInjector());
      var1.setPluginConfigurationExpander(this.newPluginConfigurationExpander());
      var1.setReportConfigurationExpander(this.newReportConfigurationExpander());
      var1.setReportingConverter(this.newReportingConverter());
      var1.setProfileActivationFilePathInterpolator(this.newProfileActivationFilePathInterpolator());
      return var1;
   }

   private static class StubLifecycleBindingsInjector implements LifecycleBindingsInjector {
      private StubLifecycleBindingsInjector() {
      }

      @Override
      public void injectLifecycleBindings(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      }
   }
}
