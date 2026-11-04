package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class LocalSnapshotMetadataGenerator implements MetadataGenerator {
   private Map<Object, LocalSnapshotMetadata> snapshots;
   private final boolean legacyFormat;
   private final Date timestamp;

   LocalSnapshotMetadataGenerator(RepositorySystemSession var1, InstallRequest var2) {
      this.legacyFormat = ConfigUtils.getBoolean(var1.getConfigProperties(), false, "maven.metadata.legacy");
      this.timestamp = (Date)ConfigUtils.getObject(var1, new Date(), "maven.startTime");
      this.snapshots = new LinkedHashMap<>();
   }

   @Override
   public Collection<? extends Metadata> prepare(Collection<? extends Artifact> var1) {
      for (Artifact var3 : var1) {
         if (var3.isSnapshot()) {
            Object var4 = LocalSnapshotMetadata.getKey(var3);
            LocalSnapshotMetadata var5 = this.snapshots.get(var4);
            if (var5 == null) {
               var5 = new LocalSnapshotMetadata(var3, this.legacyFormat, this.timestamp);
               this.snapshots.put(var4, var5);
            }

            var5.bind(var3);
         }
      }

      return Collections.emptyList();
   }

   @Override
   public Artifact transformArtifact(Artifact var1) {
      return var1;
   }

   @Override
   public Collection<? extends Metadata> finish(Collection<? extends Artifact> var1) {
      return this.snapshots.values();
   }
}
