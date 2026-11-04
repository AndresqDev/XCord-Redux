package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class RemoteSnapshotMetadataGenerator implements MetadataGenerator {
   private final Map<Object, RemoteSnapshotMetadata> snapshots;
   private final boolean legacyFormat;
   private final Date timestamp;

   RemoteSnapshotMetadataGenerator(RepositorySystemSession var1, DeployRequest var2) {
      this.legacyFormat = ConfigUtils.getBoolean(var1, false, "maven.metadata.legacy");
      this.timestamp = (Date)ConfigUtils.getObject(var1, new Date(), "maven.startTime");
      this.snapshots = new LinkedHashMap<>();

      for (Metadata var4 : var2.getMetadata()) {
         if (var4 instanceof RemoteSnapshotMetadata) {
            RemoteSnapshotMetadata var5 = (RemoteSnapshotMetadata)var4;
            this.snapshots.put(var5.getKey(), var5);
         }
      }
   }

   @Override
   public Collection<? extends Metadata> prepare(Collection<? extends Artifact> var1) {
      for (Artifact var3 : var1) {
         if (var3.isSnapshot()) {
            Object var4 = RemoteSnapshotMetadata.getKey(var3);
            RemoteSnapshotMetadata var5 = this.snapshots.get(var4);
            if (var5 == null) {
               var5 = new RemoteSnapshotMetadata(var3, this.legacyFormat, this.timestamp);
               this.snapshots.put(var4, var5);
            }

            var5.bind(var3);
         }
      }

      return this.snapshots.values();
   }

   @Override
   public Artifact transformArtifact(Artifact var1) {
      if (var1.isSnapshot() && var1.getVersion().equals(var1.getBaseVersion())) {
         Object var2 = RemoteSnapshotMetadata.getKey(var1);
         RemoteSnapshotMetadata var3 = this.snapshots.get(var2);
         if (var3 != null) {
            var1 = var1.setVersion(var3.getExpandedVersion(var1));
         }
      }

      return var1;
   }

   @Override
   public Collection<? extends Metadata> finish(Collection<? extends Artifact> var1) {
      return Collections.emptyList();
   }
}
