package org.apache.maven.repository.internal;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.installation.InstallRequest;
import xcord.Named;
import xcord.Singleton;

@Named("versions")
@Singleton
public class VersionsMetadataGeneratorFactory implements MetadataGeneratorFactory {
   @Override
   public MetadataGenerator newInstance(RepositorySystemSession var1, InstallRequest var2) {
      return new VersionsMetadataGenerator(var1, var2);
   }

   @Override
   public MetadataGenerator newInstance(RepositorySystemSession var1, DeployRequest var2) {
      return new VersionsMetadataGenerator(var1, var2);
   }

   @Override
   public float getPriority() {
      return 5.0F;
   }
}
