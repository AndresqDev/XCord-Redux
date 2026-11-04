package org.eclipse.aether.internal.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeployResult;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.impl.Deployer;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.UpdateCheck;
import org.eclipse.aether.impl.UpdateCheckManager;
import org.eclipse.aether.metadata.MergeableMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.spi.connector.ArtifactUpload;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.MetadataUpload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.RepositoryOfflineException;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transform.FileTransformer;
import org.eclipse.aether.transform.FileTransformerManager;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultDeployer implements Deployer, Service {
   private FileProcessor fileProcessor;
   private RepositoryEventDispatcher repositoryEventDispatcher;
   private RepositoryConnectorProvider repositoryConnectorProvider;
   private RemoteRepositoryManager remoteRepositoryManager;
   private UpdateCheckManager updateCheckManager;
   private Collection<MetadataGeneratorFactory> metadataFactories = new ArrayList<>();
   private SyncContextFactory syncContextFactory;
   private OfflineController offlineController;

   public DefaultDeployer() {
   }

   @Inject
   DefaultDeployer(
      FileProcessor var1,
      RepositoryEventDispatcher var2,
      RepositoryConnectorProvider var3,
      RemoteRepositoryManager var4,
      UpdateCheckManager var5,
      Set<MetadataGeneratorFactory> var6,
      SyncContextFactory var7,
      OfflineController var8
   ) {
      this.setFileProcessor(var1);
      this.setRepositoryEventDispatcher(var2);
      this.setRepositoryConnectorProvider(var3);
      this.setRemoteRepositoryManager(var4);
      this.setUpdateCheckManager(var5);
      this.setMetadataGeneratorFactories(var6);
      this.setSyncContextFactory(var7);
      this.setOfflineController(var8);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setFileProcessor(var1.getService(FileProcessor.class));
      this.setRepositoryEventDispatcher(var1.getService(RepositoryEventDispatcher.class));
      this.setRepositoryConnectorProvider(var1.getService(RepositoryConnectorProvider.class));
      this.setRemoteRepositoryManager(var1.getService(RemoteRepositoryManager.class));
      this.setUpdateCheckManager(var1.getService(UpdateCheckManager.class));
      this.setMetadataGeneratorFactories(var1.getServices(MetadataGeneratorFactory.class));
      this.setSyncContextFactory(var1.getService(SyncContextFactory.class));
      this.setOfflineController(var1.getService(OfflineController.class));
   }

   public DefaultDeployer setFileProcessor(FileProcessor var1) {
      this.fileProcessor = Objects.requireNonNull(var1, "file processor cannot be null");
      return this;
   }

   public DefaultDeployer setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repository event dispatcher cannot be null");
      return this;
   }

   public DefaultDeployer setRepositoryConnectorProvider(RepositoryConnectorProvider var1) {
      this.repositoryConnectorProvider = Objects.requireNonNull(var1, "repository connector provider cannot be null");
      return this;
   }

   public DefaultDeployer setRemoteRepositoryManager(RemoteRepositoryManager var1) {
      this.remoteRepositoryManager = Objects.requireNonNull(var1, "remote repository provider cannot be null");
      return this;
   }

   public DefaultDeployer setUpdateCheckManager(UpdateCheckManager var1) {
      this.updateCheckManager = Objects.requireNonNull(var1, "update check manager cannot be null");
      return this;
   }

   public DefaultDeployer addMetadataGeneratorFactory(MetadataGeneratorFactory var1) {
      this.metadataFactories.add(Objects.requireNonNull(var1, "metadata generator factory cannot be null"));
      return this;
   }

   public DefaultDeployer setMetadataGeneratorFactories(Collection<MetadataGeneratorFactory> var1) {
      if (var1 == null) {
         this.metadataFactories = new ArrayList<>();
      } else {
         this.metadataFactories = var1;
      }

      return this;
   }

   public DefaultDeployer setSyncContextFactory(SyncContextFactory var1) {
      this.syncContextFactory = Objects.requireNonNull(var1, "sync context factory cannot be null");
      return this;
   }

   public DefaultDeployer setOfflineController(OfflineController var1) {
      this.offlineController = Objects.requireNonNull(var1, "offline controller cannot be null");
      return this;
   }

   @Override
   public DeployResult deploy(RepositorySystemSession var1, DeployRequest var2) throws DeploymentException {
      try {
         Utils.checkOffline(var1, this.offlineController, var2.getRepository());
      } catch (RepositoryOfflineException var16) {
         throw new DeploymentException(
            "Cannot deploy while " + var2.getRepository().getId() + " (" + var2.getRepository().getUrl() + ") is in offline mode", var16
         );
      }

      DeployResult var5;
      try (SyncContext var3 = this.syncContextFactory.newInstance(var1, false)) {
         var5 = this.deploy(var3, var1, var2);
      }

      return var5;
   }

   private DeployResult deploy(SyncContext var1, RepositorySystemSession var2, DeployRequest var3) throws DeploymentException {
      DeployResult var4 = new DeployResult(var3);
      RequestTrace var5 = RequestTrace.newChild(var3.getTrace(), var3);
      RemoteRepository var6 = var3.getRepository();

      RepositoryConnector var7;
      try {
         var7 = this.repositoryConnectorProvider.newRepositoryConnector(var2, var6);
      } catch (NoRepositoryConnectorException var26) {
         throw new DeploymentException("Failed to deploy artifacts/metadata: " + var26.getMessage(), var26);
      }

      try {
         List var8 = this.getMetadataGenerators(var2, var3);
         FileTransformerManager var9 = var2.getFileTransformerManager();
         ArrayList var10 = new ArrayList();
         ArrayList var11 = new ArrayList();
         IdentityHashMap var12 = new IdentityHashMap();
         DefaultDeployer.EventCatapult var13 = new DefaultDeployer.EventCatapult(var2, var5, var6, this.repositoryEventDispatcher);
         ArrayList var14 = new ArrayList<>(var3.getArtifacts());
         List var15 = Utils.prepareMetadata(var8, var14);
         var1.acquire(var14, Utils.combine(var3.getMetadata(), var15));

         for (Metadata var17 : var15) {
            this.upload(var11, var2, var17, var6, var7, var13);
            var12.put(var17, null);
         }

         for (int var29 = 0; var29 < var14.size(); var29++) {
            Artifact var34 = (Artifact)var14.get(var29);

            for (MetadataGenerator var19 : var8) {
               var34 = var19.transformArtifact(var34);
            }

            var14.set(var29, var34);
            Collection var39 = var9.getTransformersForArtifact(var34);
            if (!var39.isEmpty()) {
               for (FileTransformer var20 : var39) {
                  Artifact var21 = var20.transformArtifact(var34);
                  ArtifactUpload var22 = new ArtifactUpload(var21, var34.getFile(), var20);
                  var22.setTrace(var5);
                  var22.setListener(new DefaultDeployer.ArtifactUploadListener(var13, var22));
                  var10.add(var22);
               }
            } else {
               ArtifactUpload var41 = new ArtifactUpload(var34, var34.getFile());
               var41.setTrace(var5);
               var41.setListener(new DefaultDeployer.ArtifactUploadListener(var13, var41));
               var10.add(var41);
            }
         }

         var7.put(var10, null);

         for (ArtifactUpload var35 : var10) {
            if (var35.getException() != null) {
               throw new DeploymentException("Failed to deploy artifacts: " + var35.getException().getMessage(), var35.getException());
            }

            var4.addArtifact(var35.getArtifact());
         }

         var15 = Utils.finishMetadata(var8, var14);
         var1.acquire(null, var15);

         for (Metadata var36 : var15) {
            this.upload(var11, var2, var36, var6, var7, var13);
            var12.put(var36, null);
         }

         for (Metadata var37 : var3.getMetadata()) {
            if (!var12.containsKey(var37)) {
               this.upload(var11, var2, var37, var6, var7, var13);
               var12.put(var37, null);
            }
         }

         var7.put(null, var11);

         for (MetadataUpload var38 : var11) {
            if (var38.getException() != null) {
               throw new DeploymentException("Failed to deploy metadata: " + var38.getException().getMessage(), var38.getException());
            }

            var4.addMetadata(var38.getMetadata());
         }
      } finally {
         var7.close();
      }

      return var4;
   }

   private List<? extends MetadataGenerator> getMetadataGenerators(RepositorySystemSession var1, DeployRequest var2) {
      PrioritizedComponents var3 = Utils.sortMetadataGeneratorFactories(var1, this.metadataFactories);
      ArrayList var4 = new ArrayList();

      for (PrioritizedComponent var6 : var3.getEnabled()) {
         MetadataGenerator var7 = ((MetadataGeneratorFactory)var6.getComponent()).newInstance(var1, var2);
         if (var7 != null) {
            var4.add(var7);
         }
      }

      return var4;
   }

   private void upload(
      Collection<MetadataUpload> var1,
      RepositorySystemSession var2,
      Metadata var3,
      RemoteRepository var4,
      RepositoryConnector var5,
      DefaultDeployer.EventCatapult var6
   ) throws DeploymentException {
      LocalRepositoryManager var7 = var2.getLocalRepositoryManager();
      File var8 = var7.getRepository().getBasedir();
      File var9 = new File(var8, var7.getPathForRemoteMetadata(var3, var4, ""));
      if (var3 instanceof MergeableMetadata) {
         if (!((MergeableMetadata)var3).isMerged()) {
            RepositoryEvent.Builder var10 = new RepositoryEvent.Builder(var2, RepositoryEvent.EventType.METADATA_RESOLVING);
            var10.setTrace(var6.getTrace());
            var10.setMetadata(var3);
            var10.setRepository(var4);
            this.repositoryEventDispatcher.dispatch(var10.build());
            var10 = new RepositoryEvent.Builder(var2, RepositoryEvent.EventType.METADATA_DOWNLOADING);
            var10.setTrace(var6.getTrace());
            var10.setMetadata(var3);
            var10.setRepository(var4);
            this.repositoryEventDispatcher.dispatch(var10.build());
            RepositoryPolicy var11 = this.getPolicy(var2, var4, var3.getNature());
            MetadataDownload var12 = new MetadataDownload();
            var12.setMetadata(var3);
            var12.setFile(var9);
            var12.setChecksumPolicy(var11.getChecksumPolicy());
            var12.setListener(SafeTransferListener.wrap(var2));
            var12.setTrace(var6.getTrace());
            var5.get(null, Arrays.asList(var12));
            MetadataTransferException var13 = var12.getException();
            if (var13 instanceof MetadataNotFoundException) {
               var9.delete();
            }

            var10 = new RepositoryEvent.Builder(var2, RepositoryEvent.EventType.METADATA_DOWNLOADED);
            var10.setTrace(var6.getTrace());
            var10.setMetadata(var3);
            var10.setRepository(var4);
            var10.setException(var13);
            var10.setFile(var9);
            this.repositoryEventDispatcher.dispatch(var10.build());
            var10 = new RepositoryEvent.Builder(var2, RepositoryEvent.EventType.METADATA_RESOLVED);
            var10.setTrace(var6.getTrace());
            var10.setMetadata(var3);
            var10.setRepository(var4);
            var10.setException(var13);
            var10.setFile(var9);
            this.repositoryEventDispatcher.dispatch(var10.build());
            if (var13 != null && !(var13 instanceof MetadataNotFoundException)) {
               throw new DeploymentException("Failed to retrieve remote metadata " + var3 + ": " + var13.getMessage(), var13);
            }
         }

         try {
            ((MergeableMetadata)var3).merge(var9, var9);
         } catch (RepositoryException var15) {
            throw new DeploymentException("Failed to update metadata " + var3 + ": " + var15.getMessage(), var15);
         }
      } else {
         if (var3.getFile() == null) {
            throw new DeploymentException("Failed to update metadata " + var3 + ": No file attached.");
         }

         try {
            this.fileProcessor.copy(var3.getFile(), var9);
         } catch (IOException var14) {
            throw new DeploymentException("Failed to update metadata " + var3 + ": " + var14.getMessage(), var14);
         }
      }

      UpdateCheck var19 = new UpdateCheck();
      var19.setItem(var3);
      var19.setFile(var9);
      var19.setRepository(var4);
      var19.setAuthoritativeRepository(var4);
      this.updateCheckManager.touchMetadata(var2, var19);
      MetadataUpload var20 = new MetadataUpload(var3, var9);
      var20.setTrace(var6.getTrace());
      var20.setListener(new DefaultDeployer.MetadataUploadListener(var6, var20));
      var1.add(var20);
   }

   private RepositoryPolicy getPolicy(RepositorySystemSession var1, RemoteRepository var2, Metadata.Nature var3) {
      boolean var4 = !Metadata.Nature.SNAPSHOT.equals(var3);
      boolean var5 = !Metadata.Nature.RELEASE.equals(var3);
      return this.remoteRepositoryManager.getPolicy(var1, var2, var4, var5);
   }

   static final class ArtifactUploadListener extends SafeTransferListener {
      private final DefaultDeployer.EventCatapult catapult;
      private final ArtifactUpload transfer;

      ArtifactUploadListener(DefaultDeployer.EventCatapult var1, ArtifactUpload var2) {
         super(var1.getSession());
         this.catapult = var1;
         this.transfer = var2;
      }

      @Override
      public void transferInitiated(TransferEvent var1) throws TransferCancelledException {
         super.transferInitiated(var1);
         this.catapult.artifactDeploying(this.transfer.getArtifact(), this.transfer.getFile());
      }

      @Override
      public void transferFailed(TransferEvent var1) {
         super.transferFailed(var1);
         this.catapult.artifactDeployed(this.transfer.getArtifact(), this.transfer.getFile(), this.transfer.getException());
      }

      @Override
      public void transferSucceeded(TransferEvent var1) {
         super.transferSucceeded(var1);
         this.catapult.artifactDeployed(this.transfer.getArtifact(), this.transfer.getFile(), null);
      }
   }

   static final class EventCatapult {
      private final RepositorySystemSession session;
      private final RequestTrace trace;
      private final RemoteRepository repository;
      private final RepositoryEventDispatcher dispatcher;

      EventCatapult(RepositorySystemSession var1, RequestTrace var2, RemoteRepository var3, RepositoryEventDispatcher var4) {
         this.session = var1;
         this.trace = var2;
         this.repository = var3;
         this.dispatcher = var4;
      }

      public RepositorySystemSession getSession() {
         return this.session;
      }

      public RequestTrace getTrace() {
         return this.trace;
      }

      public void artifactDeploying(Artifact var1, File var2) {
         RepositoryEvent.Builder var3 = new RepositoryEvent.Builder(this.session, RepositoryEvent.EventType.ARTIFACT_DEPLOYING);
         var3.setTrace(this.trace);
         var3.setArtifact(var1);
         var3.setRepository(this.repository);
         var3.setFile(var2);
         this.dispatcher.dispatch(var3.build());
      }

      public void artifactDeployed(Artifact var1, File var2, ArtifactTransferException var3) {
         RepositoryEvent.Builder var4 = new RepositoryEvent.Builder(this.session, RepositoryEvent.EventType.ARTIFACT_DEPLOYED);
         var4.setTrace(this.trace);
         var4.setArtifact(var1);
         var4.setRepository(this.repository);
         var4.setFile(var2);
         var4.setException(var3);
         this.dispatcher.dispatch(var4.build());
      }

      public void metadataDeploying(Metadata var1, File var2) {
         RepositoryEvent.Builder var3 = new RepositoryEvent.Builder(this.session, RepositoryEvent.EventType.METADATA_DEPLOYING);
         var3.setTrace(this.trace);
         var3.setMetadata(var1);
         var3.setRepository(this.repository);
         var3.setFile(var2);
         this.dispatcher.dispatch(var3.build());
      }

      public void metadataDeployed(Metadata var1, File var2, Exception var3) {
         RepositoryEvent.Builder var4 = new RepositoryEvent.Builder(this.session, RepositoryEvent.EventType.METADATA_DEPLOYED);
         var4.setTrace(this.trace);
         var4.setMetadata(var1);
         var4.setRepository(this.repository);
         var4.setFile(var2);
         var4.setException(var3);
         this.dispatcher.dispatch(var4.build());
      }
   }

   static final class MetadataUploadListener extends SafeTransferListener {
      private final DefaultDeployer.EventCatapult catapult;
      private final MetadataUpload transfer;

      MetadataUploadListener(DefaultDeployer.EventCatapult var1, MetadataUpload var2) {
         super(var1.getSession());
         this.catapult = var1;
         this.transfer = var2;
      }

      @Override
      public void transferInitiated(TransferEvent var1) throws TransferCancelledException {
         super.transferInitiated(var1);
         this.catapult.metadataDeploying(this.transfer.getMetadata(), this.transfer.getFile());
      }

      @Override
      public void transferFailed(TransferEvent var1) {
         super.transferFailed(var1);
         this.catapult.metadataDeployed(this.transfer.getMetadata(), this.transfer.getFile(), this.transfer.getException());
      }

      @Override
      public void transferSucceeded(TransferEvent var1) {
         super.transferSucceeded(var1);
         this.catapult.metadataDeployed(this.transfer.getMetadata(), this.transfer.getFile(), null);
      }
   }
}
