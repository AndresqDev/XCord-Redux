package org.apache.maven.model.normalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import org.codehaus.plexus.util.StringUtils;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelNormalizer implements ModelNormalizer {
   private DefaultModelNormalizer.DuplicateMerger merger = new DefaultModelNormalizer.DuplicateMerger();

   @Override
   public void mergeDuplicates(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Build var4 = var1.getBuild();
      if (var4 != null) {
         List var5 = var4.getPlugins();
         LinkedHashMap var6 = new LinkedHashMap(var5.size() * 2);

         for (Plugin var8 : var5) {
            String var9 = var8.getKey();
            Plugin var10 = (Plugin)var6.get(var9);
            if (var10 != null) {
               this.merger.mergePlugin(var8, var10);
            }

            var6.put(var9, var8);
         }

         if (var5.size() != var6.size()) {
            var4.setPlugins(new ArrayList<>(var6.values()));
         }
      }

      List var11 = var1.getDependencies();
      LinkedHashMap var12 = new LinkedHashMap(var11.size() * 2);

      for (Dependency var14 : var11) {
         var12.put(var14.getManagementKey(), var14);
      }

      if (var11.size() != var12.size()) {
         var1.setDependencies(new ArrayList<>(var12.values()));
      }
   }

   @Override
   public void injectDefaultValues(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      this.injectDependencyDefaults(var1.getDependencies());
      Build var4 = var1.getBuild();
      if (var4 != null) {
         for (Plugin var6 : var4.getPlugins()) {
            this.injectDependencyDefaults(var6.getDependencies());
         }
      }
   }

   private void injectDependencyDefaults(List<Dependency> var1) {
      for (Dependency var3 : var1) {
         if (StringUtils.isEmpty(var3.getScope())) {
            var3.setScope("compile");
         }
      }
   }

   protected static class DuplicateMerger extends MavenModelMerger {
      public void mergePlugin(Plugin var1, Plugin var2) {
         super.mergePlugin(var1, var2, false, Collections.emptyMap());
      }
   }
}
