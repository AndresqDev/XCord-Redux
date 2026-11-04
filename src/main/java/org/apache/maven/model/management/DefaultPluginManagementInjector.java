package org.apache.maven.model.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultPluginManagementInjector implements PluginManagementInjector {
   private DefaultPluginManagementInjector.ManagementModelMerger merger = new DefaultPluginManagementInjector.ManagementModelMerger();

   @Override
   public void injectManagement(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      this.merger.mergeManagedBuildPlugins(var1);
   }

   protected static class ManagementModelMerger extends MavenModelMerger {
      public void mergeManagedBuildPlugins(Model var1) {
         Build var2 = var1.getBuild();
         if (var2 != null) {
            PluginManagement var3 = var2.getPluginManagement();
            if (var3 != null) {
               this.mergePluginContainerPlugins(var2, var3);
            }
         }
      }

      private void mergePluginContainerPlugins(PluginContainer var1, PluginContainer var2) {
         List var3 = var2.getPlugins();
         if (!var3.isEmpty()) {
            List var4 = var1.getPlugins();
            LinkedHashMap var5 = new LinkedHashMap(var3.size() * 2);
            Map var6 = Collections.emptyMap();

            for (Plugin var8 : var3) {
               Object var9 = this.getPluginKey(var8);
               var5.put(var9, var8);
            }

            for (Plugin var12 : var4) {
               Object var13 = this.getPluginKey(var12);
               Plugin var10 = (Plugin)var5.get(var13);
               if (var10 != null) {
                  this.mergePlugin(var12, var10, false, var6);
               }
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
               Object var10 = this.getPluginExecutionKey(var9);
               var7.put(var10, var9.clone());
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
   }
}
