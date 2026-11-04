package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class VersionsMetadataGenerator implements MetadataGenerator {
   private Map<Object, VersionsMetadata> versions = new LinkedHashMap<>();
   private Map<Object, VersionsMetadata> processedVersions = new LinkedHashMap<>();
   private final Date timestamp;

   VersionsMetadataGenerator(RepositorySystemSession var1, InstallRequest var2) {
      this(var1, var2.getMetadata());
   }

   VersionsMetadataGenerator(RepositorySystemSession var1, DeployRequest var2) {
      this(var1, var2.getMetadata());
   }

   private VersionsMetadataGenerator(RepositorySystemSession var1, Collection<? extends Metadata> var2) {
      this.timestamp = (Date)ConfigUtils.getObject(var1, new Date(), "maven.startTime");
      Iterator var3 = var2.iterator();

      while (var3.hasNext()) {
         Metadata var4 = (Metadata)var3.next();
         if (var4 instanceof VersionsMetadata) {
            var3.remove();
            VersionsMetadata var5 = (VersionsMetadata)var4;
            this.processedVersions.put(var5.getKey(), var5);
         }
      }
   }

   @Override
   public Collection<? extends Metadata> prepare(Collection<? extends Artifact> var1) {
      return Collections.emptyList();
   }

   @Override
   public Artifact transformArtifact(Artifact var1) {
      return var1;
   }

   @Override
   public Collection<? extends Metadata> finish(Collection<? extends Artifact> var1) {
      for (Artifact var3 : var1) {
         Object var4 = VersionsMetadata.getKey(var3);
         if (this.processedVersions.get(var4) == null) {
            VersionsMetadata var5 = this.versions.get(var4);
            if (var5 == null) {
               var5 = new VersionsMetadata(var3, this.timestamp);
               this.versions.put(var4, var5);
            }
         }
      }

      return this.versions.values();
   }
}
