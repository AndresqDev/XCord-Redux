package org.apache.maven.model.plugin;

import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultReportingConverter implements ReportingConverter {
   private final InputLocation location;

   public DefaultReportingConverter() {
      String var1 = "org.apache.maven:maven-model-builder:" + this.getClass().getPackage().getImplementationVersion() + ":reporting-converter";
      InputSource var2 = new InputSource();
      var2.setModelId(var1);
      this.location = new InputLocation(-1, -1, var2);
      this.location.setLocation(0, this.location);
   }

   @Override
   public void convertReporting(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Reporting var4 = var1.getReporting();
      if (var4 != null) {
         Build var5 = var1.getBuild();
         if (var5 == null) {
            var5 = new Build();
            var1.setBuild(var5);
            var1.setLocation("build", this.location);
         }

         Plugin var6 = this.findSitePlugin(var5);
         if (var6 == null) {
            var6 = new Plugin();
            var6.setArtifactId("maven-site-plugin");
            var6.setLocation("artifactId", this.location);
            PluginManagement var7 = var5.getPluginManagement();
            if (var7 == null) {
               var7 = new PluginManagement();
               var5.setPluginManagement(var7);
            }

            var7.addPlugin(var6);
         }

         Xpp3Dom var13 = (Xpp3Dom)var6.getConfiguration();
         if (var13 == null) {
            var13 = new Xpp3Dom("configuration", this.location);
            var6.setConfiguration(var13);
         }

         Xpp3Dom var8 = var13.getChild("reportPlugins");
         if (var8 != null) {
            var3.add(
               new ModelProblemCollectorRequest(ModelProblem.Severity.WARNING, ModelProblem.Version.BASE)
                  .setMessage(
                     "Reporting configuration should be done in <reporting> section, not in maven-site-plugin <configuration> as reportPlugins parameter."
                  )
                  .setLocation(var6.getLocation("configuration"))
            );
         } else {
            if (var13.getChild("outputDirectory") == null) {
               this.addDom(var13, "outputDirectory", var4.getOutputDirectory(), var4.getLocation("outputDirectory"));
            }

            var8 = new Xpp3Dom("reportPlugins", this.location);
            var13.addChild(var8);
            boolean var9 = false;

            for (ReportPlugin var11 : var4.getPlugins()) {
               Xpp3Dom var12 = this.convert(var11);
               var8.addChild(var12);
               if (!var4.isExcludeDefaults()
                  && !var9
                  && "org.apache.maven.plugins".equals(var11.getGroupId())
                  && "maven-project-info-reports-plugin".equals(var11.getArtifactId())) {
                  var9 = true;
               }
            }

            if (!var4.isExcludeDefaults() && !var9) {
               Xpp3Dom var15 = new Xpp3Dom("reportPlugin", this.location);
               this.addDom(var15, "groupId", "org.apache.maven.plugins");
               this.addDom(var15, "artifactId", "maven-project-info-reports-plugin");
               var8.addChild(var15);
            }
         }
      }
   }

   private Plugin findSitePlugin(Build var1) {
      for (Plugin var3 : var1.getPlugins()) {
         if (this.isSitePlugin(var3)) {
            return var3;
         }
      }

      PluginManagement var5 = var1.getPluginManagement();
      if (var5 != null) {
         for (Plugin var4 : var5.getPlugins()) {
            if (this.isSitePlugin(var4)) {
               return var4;
            }
         }
      }

      return null;
   }

   private boolean isSitePlugin(Plugin var1) {
      return "maven-site-plugin".equals(var1.getArtifactId()) && "org.apache.maven.plugins".equals(var1.getGroupId());
   }

   private Xpp3Dom convert(ReportPlugin var1) {
      Xpp3Dom var2 = new Xpp3Dom("reportPlugin", var1.getLocation(""));
      this.addDom(var2, "groupId", var1.getGroupId(), var1.getLocation("groupId"));
      this.addDom(var2, "artifactId", var1.getArtifactId(), var1.getLocation("artifactId"));
      this.addDom(var2, "version", var1.getVersion(), var1.getLocation("version"));
      Xpp3Dom var3 = (Xpp3Dom)var1.getConfiguration();
      if (var3 != null) {
         var3 = new Xpp3Dom(var3);
         var2.addChild(var3);
      }

      if (!var1.getReportSets().isEmpty()) {
         Xpp3Dom var4 = new Xpp3Dom("reportSets", var1.getLocation("reportSets"));

         for (ReportSet var6 : var1.getReportSets()) {
            Xpp3Dom var7 = this.convert(var6);
            var4.addChild(var7);
         }

         var2.addChild(var4);
      }

      return var2;
   }

   private Xpp3Dom convert(ReportSet var1) {
      Xpp3Dom var2 = new Xpp3Dom("reportSet", var1.getLocation(""));
      InputLocation var3 = var1.getLocation("id");
      this.addDom(var2, "id", var1.getId(), var3 == null ? this.location : var3);
      Xpp3Dom var4 = (Xpp3Dom)var1.getConfiguration();
      if (var4 != null) {
         var4 = new Xpp3Dom(var4);
         var2.addChild(var4);
      }

      if (!var1.getReports().isEmpty()) {
         InputLocation var5 = var1.getLocation("reports");
         Xpp3Dom var6 = new Xpp3Dom("reports", var5);
         int var7 = 0;

         for (String var9 : var1.getReports()) {
            this.addDom(var6, "report", var9, var5 == null ? null : var5.getLocation(var7++));
         }

         var2.addChild(var6);
      }

      return var2;
   }

   private void addDom(Xpp3Dom var1, String var2, String var3) {
      this.addDom(var1, var2, var3, this.location);
   }

   private void addDom(Xpp3Dom var1, String var2, String var3, InputLocation var4) {
      if (StringUtils.isNotEmpty(var3)) {
         var1.addChild(this.newDom(var2, var3, var4));
      }
   }

   private Xpp3Dom newDom(String var1, String var2, InputLocation var3) {
      Xpp3Dom var4 = new Xpp3Dom(var1, var3);
      var4.setValue(var2);
      return var4;
   }
}
