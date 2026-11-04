package org.apache.maven.model.interpolation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.path.PathTranslator;
import org.apache.maven.model.path.UrlNormalizer;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;
import xcord.Inject;

public abstract class AbstractStringBasedModelInterpolator implements ModelInterpolator {
   private static final List<String> PROJECT_PREFIXES = Arrays.asList("pom.", "project.");
   private static final Collection<String> TRANSLATED_PATH_EXPRESSIONS;
   @Inject
   private PathTranslator pathTranslator;
   @Inject
   private UrlNormalizer urlNormalizer;
   @Inject
   private ModelVersionProcessor versionProcessor;

   public AbstractStringBasedModelInterpolator setPathTranslator(PathTranslator var1) {
      this.pathTranslator = var1;
      return this;
   }

   public AbstractStringBasedModelInterpolator setUrlNormalizer(UrlNormalizer var1) {
      this.urlNormalizer = var1;
      return this;
   }

   public AbstractStringBasedModelInterpolator setVersionPropertiesProcessor(ModelVersionProcessor var1) {
      this.versionProcessor = var1;
      return this;
   }

   protected List<ValueSource> createValueSources(Model var1, final File var2, final ModelBuildingRequest var3, ModelProblemCollector var4) {
      Properties var5 = var1.getProperties();
      Object var6 = new PrefixedObjectValueSource(PROJECT_PREFIXES, var1, false);
      if (var3.getValidationLevel() >= 20) {
         var6 = new ProblemDetectingValueSource((ValueSource)var6, "pom.", "project.", var4);
      }

      Object var7 = new ObjectBasedValueSource(var1);
      if (var3.getValidationLevel() >= 20) {
         var7 = new ProblemDetectingValueSource((ValueSource)var7, "", "project.", var4);
      }

      ArrayList var8 = new ArrayList(9);
      if (var2 != null) {
         PrefixedValueSourceWrapper var9 = new PrefixedValueSourceWrapper(new AbstractValueSource(false) {
            @Override
            public Object getValue(String var1) {
               return "basedir".equals(var1) ? var2.getAbsolutePath() : null;
            }
         }, PROJECT_PREFIXES, true);
         var8.add(var9);
         PrefixedValueSourceWrapper var10 = new PrefixedValueSourceWrapper(new AbstractValueSource(false) {
            @Override
            public Object getValue(String var1) {
               return "baseUri".equals(var1) ? var2.getAbsoluteFile().toPath().toUri().toASCIIString() : null;
            }
         }, PROJECT_PREFIXES, false);
         var8.add(var10);
         var8.add(new BuildTimestampValueSource(var3.getBuildStartTime(), var5));
      }

      var8.add(var6);
      var8.add(new MapBasedValueSource(var3.getUserProperties()));
      this.versionProcessor.overwriteModelProperties(var5, var3);
      var8.add(new MapBasedValueSource(var5));
      var8.add(new MapBasedValueSource(var3.getSystemProperties()));
      var8.add(new AbstractValueSource(false) {
         @Override
         public Object getValue(String var1) {
            return var3.getSystemProperties().getProperty("env." + var1);
         }
      });
      var8.add(var7);
      return var8;
   }

   protected List<? extends InterpolationPostProcessor> createPostProcessors(Model var1, File var2, ModelBuildingRequest var3) {
      ArrayList var4 = new ArrayList(2);
      if (var2 != null) {
         var4.add(new PathTranslatingPostProcessor(PROJECT_PREFIXES, TRANSLATED_PATH_EXPRESSIONS, var2, this.pathTranslator));
      }

      var4.add(new UrlNormalizingPostProcessor(this.urlNormalizer));
      return var4;
   }

   protected RecursionInterceptor createRecursionInterceptor() {
      return new PrefixAwareRecursionInterceptor(PROJECT_PREFIXES);
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("build.directory");
      var0.add("build.outputDirectory");
      var0.add("build.testOutputDirectory");
      var0.add("build.sourceDirectory");
      var0.add("build.testSourceDirectory");
      var0.add("build.scriptSourceDirectory");
      var0.add("reporting.outputDirectory");
      TRANSLATED_PATH_EXPRESSIONS = var0;
   }
}
