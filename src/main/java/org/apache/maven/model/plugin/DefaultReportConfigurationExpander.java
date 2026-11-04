package org.apache.maven.model.plugin;

import org.apache.maven.model.Model;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultReportConfigurationExpander implements ReportConfigurationExpander {
   @Override
   public void expandPluginConfiguration(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Reporting var4 = var1.getReporting();
      if (var4 != null) {
         for (ReportPlugin var6 : var4.getPlugins()) {
            Xpp3Dom var7 = (Xpp3Dom)var6.getConfiguration();
            if (var7 != null) {
               for (ReportSet var9 : var6.getReportSets()) {
                  Xpp3Dom var10 = (Xpp3Dom)var9.getConfiguration();
                  var10 = Xpp3Dom.mergeXpp3Dom(var10, new Xpp3Dom(var7));
                  var9.setConfiguration(var10);
               }
            }
         }
      }
   }
}
