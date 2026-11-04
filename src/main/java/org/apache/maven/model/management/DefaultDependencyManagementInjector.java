package org.apache.maven.model.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultDependencyManagementInjector implements DependencyManagementInjector {
   private DefaultDependencyManagementInjector.ManagementModelMerger merger = new DefaultDependencyManagementInjector.ManagementModelMerger();

   @Override
   public void injectManagement(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      this.merger.mergeManagedDependencies(var1);
   }

   protected static class ManagementModelMerger extends MavenModelMerger {
      public void mergeManagedDependencies(Model var1) {
         DependencyManagement var2 = var1.getDependencyManagement();
         if (var2 != null) {
            HashMap var3 = new HashMap();
            Map var4 = Collections.emptyMap();

            for (Dependency var6 : var1.getDependencies()) {
               Object var7 = this.getDependencyKey(var6);
               var3.put(var7, var6);
            }

            for (Dependency var10 : var2.getDependencies()) {
               Object var11 = this.getDependencyKey(var10);
               Dependency var8 = (Dependency)var3.get(var11);
               if (var8 != null) {
                  this.mergeDependency(var8, var10, false, var4);
               }
            }
         }
      }

      @Override
      protected void mergeDependency_Optional(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
      }

      @Override
      protected void mergeDependency_Exclusions(Dependency var1, Dependency var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var1.getExclusions();
         if (var5.isEmpty()) {
            for (Exclusion var8 : var2.getExclusions()) {
               Exclusion var9 = var8.clone();
               var1.addExclusion(var9);
            }
         }
      }
   }
}
