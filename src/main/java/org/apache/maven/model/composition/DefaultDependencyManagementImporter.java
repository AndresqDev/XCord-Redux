package org.apache.maven.model.composition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultDependencyManagementImporter implements DependencyManagementImporter {
   @Override
   public void importManagement(Model var1, List<? extends DependencyManagement> var2, ModelBuildingRequest var3, ModelProblemCollector var4) {
      if (var2 != null && !var2.isEmpty()) {
         LinkedHashMap var5 = new LinkedHashMap();
         DependencyManagement var6 = var1.getDependencyManagement();
         if (var6 != null) {
            for (Dependency var8 : var6.getDependencies()) {
               var5.put(var8.getManagementKey(), var8);
            }
         } else {
            var6 = new DependencyManagement();
            var1.setDependencyManagement(var6);
         }

         for (DependencyManagement var13 : var2) {
            for (Dependency var10 : var13.getDependencies()) {
               String var11 = var10.getManagementKey();
               if (!var5.containsKey(var11)) {
                  var5.put(var11, var10);
               }
            }
         }

         var6.setDependencies(new ArrayList<>(var5.values()));
      }
   }
}
