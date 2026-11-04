package org.apache.maven.model.inheritance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.merge.MavenModelMerger;
import org.codehaus.plexus.util.StringUtils;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultInheritanceAssembler implements InheritanceAssembler {
   private DefaultInheritanceAssembler.InheritanceModelMerger merger = new DefaultInheritanceAssembler.InheritanceModelMerger();
   private static final String CHILD_DIRECTORY = "child-directory";
   private static final String CHILD_DIRECTORY_PROPERTY = "project.directory";

   @Override
   public void assembleModelInheritance(Model var1, Model var2, ModelBuildingRequest var3, ModelProblemCollector var4) {
      HashMap var5 = new HashMap();
      String var6 = var1.getProperties().getProperty("project.directory", var1.getArtifactId());
      var5.put("child-directory", var6);
      var5.put("child-path-adjustment", this.getChildPathAdjustment(var1, var2, var6));
      this.merger.merge(var1, var2, false, var5);
   }

   private String getChildPathAdjustment(Model var1, Model var2, String var3) {
      String var4 = "";
      if (var2 != null) {
         String var5 = var1.getArtifactId();
         if (var1.getProjectDirectory() != null) {
            var5 = var1.getProjectDirectory().getName();
         }

         for (String var7 : var2.getModules()) {
            var7 = var7.replace('\\', '/');
            if (var7.regionMatches(true, var7.length() - 4, ".xml", 0, 4)) {
               var7 = var7.substring(0, var7.lastIndexOf(47) + 1);
            }

            String var8 = var7;
            if (var7.endsWith("/")) {
               var8 = var7.substring(0, var7.length() - 1);
            }

            int var9 = var8.lastIndexOf(47);
            var8 = var8.substring(var9 + 1);
            if ((var8.equals(var5) || var8.equals(var3)) && var9 >= 0) {
               var4 = var7.substring(0, var9);
               break;
            }
         }
      }

      return var4;
   }

   protected static class InheritanceModelMerger extends MavenModelMerger {
      @Override
      protected String extrapolateChildUrl(String var1, boolean var2, Map<Object, Object> var3) {
         Object var4 = var3.get("child-directory");
         Object var5 = var3.get("child-path-adjustment");
         return !StringUtils.isBlank(var1) && var4 != null && var5 != null && var2 ? this.appendPath(var1, var4.toString(), var5.toString()) : var1;
      }

      private String appendPath(String var1, String var2, String var3) {
         StringBuilder var4 = new StringBuilder(var1.length() + var3.length() + var2.length() + (var3.length() == 0 ? 1 : 2));
         var4.append(var1);
         this.concatPath(var4, var3);
         this.concatPath(var4, var2);
         return var4.toString();
      }

      private void concatPath(StringBuilder var1, String var2) {
         if (var2.length() > 0) {
            boolean var3 = var1.charAt(var1.length() - 1) == '/';
            boolean var4 = var2.charAt(0) == '/';
            if (var4) {
               if (var3) {
                  var1.setLength(var1.length() - 1);
               }
            } else if (!var3) {
               var1.append('/');
            }

            var1.append(var2);
            if (var3 && !var2.endsWith("/")) {
               var1.append('/');
            }
         }
      }

      @Override
      protected void mergeModelBase_Properties(ModelBase var1, ModelBase var2, boolean var3, Map<Object, Object> var4) {
         Properties var5 = new Properties();
         if (var3) {
            var5.putAll(var1.getProperties());
            this.putAll(var5, var2.getProperties(), "project.directory");
         } else {
            this.putAll(var5, var2.getProperties(), "project.directory");
            var5.putAll(var1.getProperties());
         }

         var1.setProperties(var5);
         var1.setLocation("properties", InputLocation.merge(var1.getLocation("properties"), var2.getLocation("properties"), var3));
      }

      private void putAll(Map<Object, Object> var1, Map<Object, Object> var2, Object var3) {
         for (Entry var5 : var2.entrySet()) {
            if (!var5.getKey().equals(var3)) {
               var1.put(var5.getKey(), var5.getValue());
            }
         }
      }

      @Override
      protected void mergePluginContainer_Plugins(PluginContainer var1, PluginContainer var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getPlugins();
         if (!var5.isEmpty()) {
            List var6 = var1.getPlugins();
            LinkedHashMap var7 = new LinkedHashMap(var5.size() * 2);

            for (Plugin var9 : var5) {
               if (var9.isInherited() || !var9.getExecutions().isEmpty()) {
                  Plugin var10 = new Plugin();
                  var10.setLocation("", var9.getLocation(""));
                  var10.setGroupId(null);
                  this.mergePlugin(var10, var9, var3, var4);
                  Object var11 = this.getPluginKey(var9);
                  var7.put(var11, var10);
               }
            }

            LinkedHashMap var14 = new LinkedHashMap();
            ArrayList var15 = new ArrayList();

            for (Plugin var18 : var6) {
               Object var12 = this.getPluginKey(var18);
               Plugin var13 = (Plugin)var7.get(var12);
               if (var13 != null) {
                  this.mergePlugin(var18, var13, var3, var4);
                  var7.put(var12, var18);
                  if (!var15.isEmpty()) {
                     var14.put(var12, var15);
                     var15 = new ArrayList();
                  }
               } else {
                  var15.add(var18);
               }
            }

            ArrayList var17 = new ArrayList(var5.size() + var6.size());

            for (Entry var20 : var7.entrySet()) {
               List var21 = (List)var14.get(var20.getKey());
               if (var21 != null) {
                  var17.addAll(var21);
               }

               var17.add(var20.getValue());
            }

            var17.addAll(var15);
            var1.setPlugins(var17);
         }
      }

      @Override
      protected void mergePlugin(Plugin var1, Plugin var2, boolean var3, Map<Object, Object> var4) {
         if (var2.isInherited()) {
            this.mergeConfigurationContainer(var1, var2, var3, var4);
         }

         this.mergePlugin_GroupId(var1, var2, var3, var4);
         this.mergePlugin_ArtifactId(var1, var2, var3, var4);
         this.mergePlugin_Version(var1, var2, var3, var4);
         this.mergePlugin_Extensions(var1, var2, var3, var4);
         this.mergePlugin_Dependencies(var1, var2, var3, var4);
         this.mergePlugin_Executions(var1, var2, var3, var4);
      }

      @Override
      protected void mergeReporting_Plugins(Reporting var1, Reporting var2, boolean var3, Map<Object, Object> var4) {
         List var5 = var2.getPlugins();
         if (!var5.isEmpty()) {
            List var6 = var1.getPlugins();
            LinkedHashMap var7 = new LinkedHashMap((var5.size() + var6.size()) * 2);

            for (ReportPlugin var9 : var5) {
               Object var10 = this.getReportPluginKey(var9);
               if (var9.isInherited()) {
                  ReportPlugin var11 = new ReportPlugin();
                  var11.setLocation("", var9.getLocation(""));
                  var11.setGroupId(null);
                  this.mergeReportPlugin(var11, var9, var3, var4);
                  var7.put(var10, var11);
               }
            }

            for (ReportPlugin var13 : var6) {
               Object var14 = this.getReportPluginKey(var13);
               ReportPlugin var15 = (ReportPlugin)var7.get(var14);
               if (var15 != null) {
                  this.mergeReportPlugin(var13, var15, var3, var4);
               }

               var7.put(var14, var13);
            }

            var1.setPlugins(new ArrayList<>(var7.values()));
         }
      }
   }
}
