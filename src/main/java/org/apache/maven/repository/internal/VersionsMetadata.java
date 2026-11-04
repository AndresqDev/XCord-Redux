package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;

final class VersionsMetadata extends MavenMetadata {
   private final Artifact artifact;

   VersionsMetadata(Artifact var1, Date var2) {
      super(createRepositoryMetadata(var1), null, var2);
      this.artifact = var1;
   }

   VersionsMetadata(Artifact var1, File var2, Date var3) {
      super(createRepositoryMetadata(var1), var2, var3);
      this.artifact = var1;
   }

   private static Metadata createRepositoryMetadata(Artifact var0) {
      Metadata var1 = new Metadata();
      var1.setGroupId(var0.getGroupId());
      var1.setArtifactId(var0.getArtifactId());
      Versioning var2 = new Versioning();
      var2.addVersion(var0.getBaseVersion());
      if (!var0.isSnapshot()) {
         var2.setRelease(var0.getBaseVersion());
      }

      if ("maven-plugin".equals(var0.getProperty("type", ""))) {
         var2.setLatest(var0.getBaseVersion());
      }

      var1.setVersioning(var2);
      return var1;
   }

   @Override
   protected void merge(Metadata var1) {
      Versioning var2 = this.metadata.getVersioning();
      var2.setLastUpdatedTimestamp(this.timestamp);
      if (var1.getVersioning() != null) {
         if (var2.getLatest() == null) {
            var2.setLatest(var1.getVersioning().getLatest());
         }

         if (var2.getRelease() == null) {
            var2.setRelease(var1.getVersioning().getRelease());
         }

         LinkedHashSet var3 = new LinkedHashSet<>(var1.getVersioning().getVersions());
         var3.addAll(var2.getVersions());
         var2.setVersions(new ArrayList<>(var3));
      }
   }

   public Object getKey() {
      return this.getGroupId() + ':' + this.getArtifactId();
   }

   public static Object getKey(Artifact var0) {
      return var0.getGroupId() + ':' + var0.getArtifactId();
   }

   public MavenMetadata setFile(File var1) {
      return new VersionsMetadata(this.artifact, var1, this.timestamp);
   }

   @Override
   public String getGroupId() {
      return this.artifact.getGroupId();
   }

   @Override
   public String getArtifactId() {
      return this.artifact.getArtifactId();
   }

   @Override
   public String getVersion() {
      return "";
   }

   @Override
   public org.eclipse.aether.metadata.Metadata.Nature getNature() {
      return this.artifact.isSnapshot() ? org.eclipse.aether.metadata.Metadata.Nature.RELEASE_OR_SNAPSHOT : org.eclipse.aether.metadata.Metadata.Nature.RELEASE;
   }
}
