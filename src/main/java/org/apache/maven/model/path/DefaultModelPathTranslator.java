package org.apache.maven.model.path;

import java.io.File;
import java.util.ArrayList;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Resource;
import org.apache.maven.model.building.ModelBuildingRequest;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelPathTranslator implements ModelPathTranslator {
   @Inject
   private PathTranslator pathTranslator;

   public DefaultModelPathTranslator setPathTranslator(PathTranslator var1) {
      this.pathTranslator = var1;
      return this;
   }

   @Override
   public void alignToBaseDirectory(Model var1, File var2, ModelBuildingRequest var3) {
      if (var1 != null && var2 != null) {
         Build var4 = var1.getBuild();
         if (var4 != null) {
            var4.setDirectory(this.alignToBaseDirectory(var4.getDirectory(), var2));
            var4.setSourceDirectory(this.alignToBaseDirectory(var4.getSourceDirectory(), var2));
            var4.setTestSourceDirectory(this.alignToBaseDirectory(var4.getTestSourceDirectory(), var2));
            var4.setScriptSourceDirectory(this.alignToBaseDirectory(var4.getScriptSourceDirectory(), var2));

            for (Resource var6 : var4.getResources()) {
               var6.setDirectory(this.alignToBaseDirectory(var6.getDirectory(), var2));
            }

            for (Resource var11 : var4.getTestResources()) {
               var11.setDirectory(this.alignToBaseDirectory(var11.getDirectory(), var2));
            }

            if (var4.getFilters() != null) {
               ArrayList var9 = new ArrayList(var4.getFilters().size());

               for (String var7 : var4.getFilters()) {
                  var9.add(this.alignToBaseDirectory(var7, var2));
               }

               var4.setFilters(var9);
            }

            var4.setOutputDirectory(this.alignToBaseDirectory(var4.getOutputDirectory(), var2));
            var4.setTestOutputDirectory(this.alignToBaseDirectory(var4.getTestOutputDirectory(), var2));
         }

         Reporting var10 = var1.getReporting();
         if (var10 != null) {
            var10.setOutputDirectory(this.alignToBaseDirectory(var10.getOutputDirectory(), var2));
         }
      }
   }

   private String alignToBaseDirectory(String var1, File var2) {
      return this.pathTranslator.alignToBaseDirectory(var1, var2);
   }
}
