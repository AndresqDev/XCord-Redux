package org.apache.maven.model.plugin;

import java.util.List;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultPluginConfigurationExpander implements PluginConfigurationExpander {
   @Override
   public void expandPluginConfiguration(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3) {
      Build var4 = var1.getBuild();
      if (var4 != null) {
         this.expand(var4.getPlugins());
         PluginManagement var5 = var4.getPluginManagement();
         if (var5 != null) {
            this.expand(var5.getPlugins());
         }
      }
   }

   private void expand(List<Plugin> var1) {
      for (Plugin var3 : var1) {
         Xpp3Dom var4 = (Xpp3Dom)var3.getConfiguration();
         if (var4 != null) {
            for (PluginExecution var6 : var3.getExecutions()) {
               Xpp3Dom var7 = (Xpp3Dom)var6.getConfiguration();
               var7 = Xpp3Dom.mergeXpp3Dom(var7, new Xpp3Dom(var4));
               var6.setConfiguration(var7);
            }
         }
      }
   }
}
