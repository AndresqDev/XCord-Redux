package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;

final class LocalSnapshotMetadata extends MavenMetadata {
   private final Collection<Artifact> artifacts = new ArrayList<>();
   private final boolean legacyFormat;

   LocalSnapshotMetadata(Artifact var1, boolean var2, Date var3) {
      super(createMetadata(var1, var2), null, var3);
      this.legacyFormat = var2;
   }

   LocalSnapshotMetadata(Metadata var1, File var2, boolean var3, Date var4) {
      super(var1, var2, var4);
      this.legacyFormat = var3;
   }

   private static Metadata createMetadata(Artifact var0, boolean var1) {
      Snapshot var2 = new Snapshot();
      var2.setLocalCopy(true);
      Versioning var3 = new Versioning();
      var3.setSnapshot(var2);
      Metadata var4 = new Metadata();
      var4.setVersioning(var3);
      var4.setGroupId(var0.getGroupId());
      var4.setArtifactId(var0.getArtifactId());
      var4.setVersion(var0.getBaseVersion());
      if (!var1) {
         var4.setModelVersion("1.1.0");
      }

      return var4;
   }

   public void bind(Artifact var1) {
      this.artifacts.add(var1);
   }

   public MavenMetadata setFile(File var1) {
      return new LocalSnapshotMetadata(this.metadata, var1, this.legacyFormat, this.timestamp);
   }

   public Object getKey() {
      return this.getGroupId() + ':' + this.getArtifactId() + ':' + this.getVersion();
   }

   public static Object getKey(Artifact var0) {
      return var0.getGroupId() + ':' + var0.getArtifactId() + ':' + var0.getBaseVersion();
   }

   @Override
   protected void merge(Metadata var1) {
      this.metadata.getVersioning().setLastUpdatedTimestamp(this.timestamp);
      if (!this.legacyFormat) {
         String var2 = this.metadata.getVersioning().getLastUpdated();
         LinkedHashMap var3 = new LinkedHashMap();

         for (Artifact var5 : this.artifacts) {
            SnapshotVersion var6 = new SnapshotVersion();
            var6.setClassifier(var5.getClassifier());
            var6.setExtension(var5.getExtension());
            var6.setVersion(this.getVersion());
            var6.setUpdated(var2);
            var3.put(this.getKey(var6.getClassifier(), var6.getExtension()), var6);
         }

         Versioning var8 = var1.getVersioning();
         if (var8 != null) {
            for (SnapshotVersion var10 : var8.getSnapshotVersions()) {
               String var7 = this.getKey(var10.getClassifier(), var10.getExtension());
               if (!var3.containsKey(var7)) {
                  var3.put(var7, var10);
               }
            }
         }

         this.metadata.getVersioning().setSnapshotVersions(new ArrayList<>(var3.values()));
      }

      this.artifacts.clear();
   }

   private String getKey(String var1, String var2) {
      return var1 + ':' + var2;
   }

   @Override
   public String getGroupId() {
      return this.metadata.getGroupId();
   }

   @Override
   public String getArtifactId() {
      return this.metadata.getArtifactId();
   }

   @Override
   public String getVersion() {
      return this.metadata.getVersion();
   }

   @Override
   public org.eclipse.aether.metadata.Metadata.Nature getNature() {
      return org.eclipse.aether.metadata.Metadata.Nature.SNAPSHOT;
   }
}
