package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.eclipse.aether.artifact.Artifact;

abstract class MavenSnapshotMetadata extends MavenMetadata {
   static final String SNAPSHOT = "SNAPSHOT";
   protected final Collection<Artifact> artifacts = new ArrayList<>();
   protected final boolean legacyFormat;

   protected MavenSnapshotMetadata(Metadata var1, File var2, boolean var3, Date var4) {
      super(var1, var2, var4);
      this.legacyFormat = var3;
   }

   protected static Metadata createRepositoryMetadata(Artifact var0, boolean var1) {
      Metadata var2 = new Metadata();
      if (!var1) {
         var2.setModelVersion("1.1.0");
      }

      var2.setGroupId(var0.getGroupId());
      var2.setArtifactId(var0.getArtifactId());
      var2.setVersion(var0.getBaseVersion());
      return var2;
   }

   public void bind(Artifact var1) {
      this.artifacts.add(var1);
   }

   public Object getKey() {
      return this.getGroupId() + ':' + this.getArtifactId() + ':' + this.getVersion();
   }

   public static Object getKey(Artifact var0) {
      return var0.getGroupId() + ':' + var0.getArtifactId() + ':' + var0.getBaseVersion();
   }

   protected String getKey(String var1, String var2) {
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
