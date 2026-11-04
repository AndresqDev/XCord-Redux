package org.eclipse.aether.connector.basic;

import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import xcord.Inject;
import xcord.Named;

@Named("basic")
public final class BasicRepositoryConnectorFactory implements RepositoryConnectorFactory, Service {
   private TransporterProvider transporterProvider;
   private RepositoryLayoutProvider layoutProvider;
   private ChecksumPolicyProvider checksumPolicyProvider;
   private FileProcessor fileProcessor;
   private float priority;

   public BasicRepositoryConnectorFactory() {
   }

   @Inject
   BasicRepositoryConnectorFactory(TransporterProvider var1, RepositoryLayoutProvider var2, ChecksumPolicyProvider var3, FileProcessor var4) {
      this.setTransporterProvider(var1);
      this.setRepositoryLayoutProvider(var2);
      this.setChecksumPolicyProvider(var3);
      this.setFileProcessor(var4);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setTransporterProvider(var1.getService(TransporterProvider.class));
      this.setRepositoryLayoutProvider(var1.getService(RepositoryLayoutProvider.class));
      this.setChecksumPolicyProvider(var1.getService(ChecksumPolicyProvider.class));
      this.setFileProcessor(var1.getService(FileProcessor.class));
   }

   public BasicRepositoryConnectorFactory setTransporterProvider(TransporterProvider var1) {
      this.transporterProvider = Objects.requireNonNull(var1, "transporter provider cannot be null");
      return this;
   }

   public BasicRepositoryConnectorFactory setRepositoryLayoutProvider(RepositoryLayoutProvider var1) {
      this.layoutProvider = Objects.requireNonNull(var1, "repository layout provider cannot be null");
      return this;
   }

   public BasicRepositoryConnectorFactory setChecksumPolicyProvider(ChecksumPolicyProvider var1) {
      this.checksumPolicyProvider = Objects.requireNonNull(var1, "checksum policy provider cannot be null");
      return this;
   }

   public BasicRepositoryConnectorFactory setFileProcessor(FileProcessor var1) {
      this.fileProcessor = Objects.requireNonNull(var1, "file processor cannot be null");
      return this;
   }

   @Override
   public float getPriority() {
      return this.priority;
   }

   public BasicRepositoryConnectorFactory setPriority(float var1) {
      this.priority = var1;
      return this;
   }

   @Override
   public RepositoryConnector newInstance(RepositorySystemSession var1, RemoteRepository var2) throws NoRepositoryConnectorException {
      Objects.requireNonNull("session", "session cannot be null");
      Objects.requireNonNull("repository", "repository cannot be null");
      return new BasicRepositoryConnector(var1, var2, this.transporterProvider, this.layoutProvider, this.checksumPolicyProvider, this.fileProcessor);
   }
}
