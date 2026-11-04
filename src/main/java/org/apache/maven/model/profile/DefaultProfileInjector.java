package org.apache.maven.model.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultProfileInjector implements ProfileInjector {
   private DefaultProfileInjector.ProfileModelMerger merger = new DefaultProfileInjector.ProfileModelMerger();

   @Override
   public void injectProfile(Model var1, Profile var2, ModelBuildingRequest var3, ModelProblemCollector var4) {
      if (var2 != null) {
         this.merger.mergeModelBase(var1, var2);
         if (var2.getBuild() != null) {
            if (var1.getBuild() == null) {
               var1.setBuild(new Build());
            }

            this.merger.mergeBuildBase(var1.getBuild(), var2.getBuild());
         }
      }
   }

   protected static class ProfileModelMerger extends MavenModelMerger {
      public void mergeModelBase(ModelBase var1, ModelBase var2) {
         this.mergeModelBase(var1, var2, true, Collections.emptyMap());
      }

      public void mergeBuildBase(BuildBase var1, BuildBase var2) {
         this.mergeBuildBase(var1, var2, true, Collections.emptyMap());
      }

      @Override
      protected void mergePluginContainer_Plugins(PluginContainer var1, PluginContainer var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getPlugins();
         if (!var5.isEmpty()) {
            List var6 = var1.getPlugins();
            LinkedHashMap var7 = new LinkedHashMap(var6.size() * 2);

            for (Plugin var9 : var6) {
               Object var10 = this.getPluginKey(var9);
               var7.put(var10, var9);
            }

            LinkedHashMap var14 = new LinkedHashMap();
            ArrayList var15 = new ArrayList();

            for (Plugin var11 : var5) {
               Object var12 = this.getPluginKey(var11);
               Plugin var13 = (Plugin)var7.get(var12);
               if (var13 != null) {
                  this.mergePlugin(var13, var11, var3, var4);
                  if (!var15.isEmpty()) {
                     var14.put(var12, var15);
                     var15 = new ArrayList();
                  }
               } else {
                  var15.add(var11);
               }
            }

            ArrayList var17 = new ArrayList(var5.size() + var6.size());

            for (Entry var19 : var7.entrySet()) {
               List var20 = (List)var14.get(var19.getKey());
               if (var20 != null) {
                  var17.addAll(var20);
               }

               var17.add(var19.getValue());
            }

            var17.addAll(var15);
            var1.setPlugins(var17);
         }
      }

      @Override
      protected void mergePlugin_Executions(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getExecutions();
         if (!var5.isEmpty()) {
            List var6 = var1.getExecutions();
            LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

            for (PluginExecution var9 : var6) {
               Object var10 = this.getPluginExecutionKey(var9);
               var7.put(var10, var9);
            }

            for (PluginExecution var13 : var5) {
               Object var14 = this.getPluginExecutionKey(var13);
               PluginExecution var11 = (PluginExecution)var7.get(var14);
               if (var11 != null) {
                  this.mergePluginExecution(var11, var13, var3, var4);
               } else {
                  var7.put(var14, var13);
               }
            }

            var1.setExecutions(new ArrayList<>(var7.values()));
         }
      }

      @Override
      protected void mergeReporting_Plugins(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getPlugins();
         if (!var5.isEmpty()) {
            List var6 = var1.getPlugins();
            LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

            for (ReportPlugin var9 : var6) {
               Object var10 = this.getReportPluginKey(var9);
               var7.put(var10, var9);
            }

            for (ReportPlugin var13 : var5) {
               Object var14 = this.getReportPluginKey(var13);
               ReportPlugin var11 = (ReportPlugin)var7.get(var14);
               if (var11 == null) {
                  var7.put(var14, var13);
               } else {
                  this.mergeReportPlugin(var11, var13, var3, var4);
               }
            }

            var1.setPlugins(new ArrayList<>(var7.values()));
         }
      }

      @Override
      protected void mergeReportPlugin_ReportSets(ReportPlugin var1, ReportPlugin var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getReportSets();
         if (!var5.isEmpty()) {
            List var6 = var1.getReportSets();
            LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

            for (ReportSet var9 : var6) {
               Object var10 = this.getReportSetKey(var9);
               var7.put(var10, var9);
            }

            for (ReportSet var13 : var5) {
               Object var14 = this.getReportSetKey(var13);
               ReportSet var11 = (ReportSet)var7.get(var14);
               if (var11 != null) {
                  this.mergeReportSet(var11, var13, var3, var4);
               } else {
                  var7.put(var14, var13);
               }
            }

            var1.setReportSets(new ArrayList<>(var7.values()));
         }
      }
   }
}
