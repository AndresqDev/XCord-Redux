package org.apache.maven.repository.internal;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.installation.InstallRequest;
import xcord.Named;
import xcord.Singleton;

@Named("snapshot")
@Singleton
public class SnapshotMetadataGeneratorFactory implements MetadataGeneratorFactory {
   @Override
   public MetadataGenerator newInstance(RepositorySystemSession var1, InstallRequest var2) {
      return new LocalSnapshotMetadataGenerator(var1, var2);
   }

   @Override
   public MetadataGenerator newInstance(RepositorySystemSession var1, DeployRequest var2) {
      return new RemoteSnapshotMetadataGenerator(var1, var2);
   }

   @Override
   public float getPriority() {
      return 10.0F;
   }
}
