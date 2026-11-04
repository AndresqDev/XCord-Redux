package org.apache.maven.repository.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Repository;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

public class ArtifactDescriptorReaderDelegate {
   public void populateResult(RepositorySystemSession var1, ArtifactDescriptorResult var2, Model var3) {
      ArtifactTypeRegistry var4 = var1.getArtifactTypeRegistry();

      for (Repository var6 : var3.getRepositories()) {
         var2.addRepository(ArtifactDescriptorUtils.toRemoteRepository(var6));
      }

      for (Dependency var13 : var3.getDependencies()) {
         var2.addDependency(this.convert(var13, var4));
      }

      DependencyManagement var12 = var3.getDependencyManagement();
      if (var12 != null) {
         for (Dependency var7 : var12.getDependencies()) {
            var2.addManagedDependency(this.convert(var7, var4));
         }
      }

      LinkedHashMap var15 = new LinkedHashMap();
      Prerequisites var16 = var3.getPrerequisites();
      if (var16 != null) {
         var15.put("prerequisites.maven", var16.getMaven());
      }

      List var8 = var3.getLicenses();
      var15.put("license.count", var8.size());

      for (int var9 = 0; var9 < var8.size(); var9++) {
         License var10 = (License)var8.get(var9);
         var15.put("license." + var9 + ".name", var10.getName());
         var15.put("license." + var9 + ".url", var10.getUrl());
         var15.put("license." + var9 + ".comments", var10.getComments());
         var15.put("license." + var9 + ".distribution", var10.getDistribution());
      }

      var2.setProperties(var15);
      this.setArtifactProperties(var2, var3);
   }

   private org.eclipse.aether.graph.Dependency convert(Dependency var1, ArtifactTypeRegistry var2) {
      Object var3 = var2.get(var1.getType());
      if (var3 == null) {
         var3 = new DefaultArtifactType(var1.getType());
      }

      boolean var4 = var1.getSystemPath() != null && var1.getSystemPath().length() > 0;
      Map var5 = null;
      if (var4) {
         var5 = Collections.singletonMap("localPath", var1.getSystemPath());
      }

      DefaultArtifact var6 = new DefaultArtifact(
         var1.getGroupId(), var1.getArtifactId(), var1.getClassifier(), null, var1.getVersion(), var5, (ArtifactType)var3
      );
      ArrayList var7 = new ArrayList(var1.getExclusions().size());

      for (Exclusion var9 : var1.getExclusions()) {
         var7.add(this.convert(var9));
      }

      return new org.eclipse.aether.graph.Dependency(var6, var1.getScope(), var1.getOptional() != null ? var1.isOptional() : null, var7);
   }

   private org.eclipse.aether.graph.Exclusion convert(Exclusion var1) {
      return new org.eclipse.aether.graph.Exclusion(var1.getGroupId(), var1.getArtifactId(), "*", "*");
   }

   private void setArtifactProperties(ArtifactDescriptorResult var1, Model var2) {
      String var3 = null;
      DistributionManagement var4 = var2.getDistributionManagement();
      if (var4 != null) {
         var3 = var4.getDownloadUrl();
      }

      if (var3 != null && var3.length() > 0) {
         Artifact var5 = var1.getArtifact();
         HashMap var6 = new HashMap<>(var5.getProperties());
         var6.put("downloadUrl", var3);
         var1.setArtifact(var5.setProperties(var6));
      }
   }
}
