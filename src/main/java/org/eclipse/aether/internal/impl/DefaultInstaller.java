package org.eclipse.aether.internal.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.Installer;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.metadata.MergeableMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalMetadataRegistration;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transform.FileTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultInstaller implements Installer, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInstaller.class);
   private FileProcessor fileProcessor;
   private RepositoryEventDispatcher repositoryEventDispatcher;
   private Collection<MetadataGeneratorFactory> metadataFactories = new ArrayList<>();
   private SyncContextFactory syncContextFactory;

   public DefaultInstaller() {
   }

   @Inject
   DefaultInstaller(FileProcessor var1, RepositoryEventDispatcher var2, Set<MetadataGeneratorFactory> var3, SyncContextFactory var4) {
      this.setFileProcessor(var1);
      this.setRepositoryEventDispatcher(var2);
      this.setMetadataGeneratorFactories(var3);
      this.setSyncContextFactory(var4);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setFileProcessor(var1.getService(FileProcessor.class));
      this.setRepositoryEventDispatcher(var1.getService(RepositoryEventDispatcher.class));
      this.setMetadataGeneratorFactories(var1.getServices(MetadataGeneratorFactory.class));
      this.setSyncContextFactory(var1.getService(SyncContextFactory.class));
   }

   public DefaultInstaller setFileProcessor(FileProcessor var1) {
      this.fileProcessor = Objects.requireNonNull(var1, "file processor cannot be null");
      return this;
   }

   public DefaultInstaller setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repository event dispatcher cannot be null");
      return this;
   }

   public DefaultInstaller addMetadataGeneratorFactory(MetadataGeneratorFactory var1) {
      this.metadataFactories.add(Objects.requireNonNull(var1, "metadata generator factory cannot be null"));
      return this;
   }

   public DefaultInstaller setMetadataGeneratorFactories(Collection<MetadataGeneratorFactory> var1) {
      if (var1 == null) {
         this.metadataFactories = new ArrayList<>();
      } else {
         this.metadataFactories = var1;
      }

      return this;
   }

   public DefaultInstaller setSyncContextFactory(SyncContextFactory var1) {
      this.syncContextFactory = Objects.requireNonNull(var1, "sync context factory cannot be null");
      return this;
   }

   @Override
   public InstallResult install(RepositorySystemSession var1, InstallRequest var2) throws InstallationException {
      InstallResult var5;
      try (SyncContext var3 = this.syncContextFactory.newInstance(var1, false)) {
         var5 = this.install(var3, var1, var2);
      }

      return var5;
   }

   private InstallResult install(SyncContext var1, RepositorySystemSession var2, InstallRequest var3) throws InstallationException {
      InstallResult var4 = new InstallResult(var3);
      RequestTrace var5 = RequestTrace.newChild(var3.getTrace(), var3);
      List var6 = this.getMetadataGenerators(var2, var3);
      ArrayList var7 = new ArrayList<>(var3.getArtifacts());
      IdentityHashMap var8 = new IdentityHashMap();
      List var9 = Utils.prepareMetadata(var6, var7);
      var1.acquire(var7, Utils.combine(var3.getMetadata(), var9));

      for (Metadata var11 : var9) {
         this.install(var2, var5, var11);
         var8.put(var11, null);
         var4.addMetadata(var11);
      }

      for (int var15 = 0; var15 < var7.size(); var15++) {
         Artifact var18 = (Artifact)var7.get(var15);

         for (MetadataGenerator var13 : var6) {
            var18 = var13.transformArtifact(var18);
         }

         var7.set(var15, var18);
         this.install(var2, var5, var18);
         var4.addArtifact(var18);
      }

      var9 = Utils.finishMetadata(var6, var7);
      var1.acquire(null, var9);

      for (Metadata var19 : var9) {
         this.install(var2, var5, var19);
         var8.put(var19, null);
         var4.addMetadata(var19);
      }

      for (Metadata var20 : var3.getMetadata()) {
         if (!var8.containsKey(var20)) {
            this.install(var2, var5, var20);
            var4.addMetadata(var20);
         }
      }

      return var4;
   }

   private List<? extends MetadataGenerator> getMetadataGenerators(RepositorySystemSession var1, InstallRequest var2) {
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

   private void install(RepositorySystemSession var1, RequestTrace var2, Artifact var3) throws InstallationException {
      LocalRepositoryManager var4 = var1.getLocalRepositoryManager();
      File var5 = var3.getFile();
      Collection var6 = var1.getFileTransformerManager().getTransformersForArtifact(var3);
      if (var6.isEmpty()) {
         this.install(var1, var2, var3, var4, var5, null);
      } else {
         for (FileTransformer var8 : var6) {
            this.install(var1, var2, var3, var4, var5, var8);
         }
      }
   }

   private void install(RepositorySystemSession var1, RequestTrace var2, Artifact var3, LocalRepositoryManager var4, File var5, FileTransformer var6) throws InstallationException {
      Artifact var7;
      if (var6 != null) {
         var7 = var6.transformArtifact(var3);
      } else {
         var7 = var3;
      }

      File var8 = new File(var4.getRepository().getBasedir(), var4.getPathForLocalArtifact(var7));
      this.artifactInstalling(var1, var2, var7, var8);
      Exception var9 = null;

      try {
         if (var8.equals(var5)) {
            throw new IllegalStateException("cannot install " + var8 + " to same path");
         }

         boolean var10 = "pom".equals(var7.getExtension()) || var5.lastModified() != var8.lastModified() || var5.length() != var8.length() || !var5.exists();
         if (!var10) {
            LOGGER.debug("Skipped re-installing {} to {}, seems unchanged", var5, var8);
         } else if (var6 != null) {
            try (InputStream var11 = var6.transformData(var5)) {
               this.fileProcessor.write(var8, var11);
               var8.setLastModified(var5.lastModified());
            }
         } else {
            this.fileProcessor.copy(var5, var8);
            var8.setLastModified(var5.lastModified());
         }

         var4.add(var1, new LocalArtifactRegistration(var7));
      } catch (Exception var32) {
         var9 = var32;
         throw new InstallationException("Failed to install artifact " + var7 + ": " + var32.getMessage(), var32);
      } finally {
         this.artifactInstalled(var1, var2, var7, var8, var9);
      }
   }

   private void install(RepositorySystemSession var1, RequestTrace var2, Metadata var3) throws InstallationException {
      LocalRepositoryManager var4 = var1.getLocalRepositoryManager();
      File var5 = new File(var4.getRepository().getBasedir(), var4.getPathForLocalMetadata(var3));
      this.metadataInstalling(var1, var2, var3, var5);
      Exception var6 = null;

      try {
         if (var3 instanceof MergeableMetadata) {
            ((MergeableMetadata)var3).merge(var5, var5);
         } else {
            if (var5.equals(var3.getFile())) {
               throw new IllegalStateException("cannot install " + var5 + " to same path");
            }

            this.fileProcessor.copy(var3.getFile(), var5);
         }

         var4.add(var1, new LocalMetadataRegistration(var3));
      } catch (Exception var11) {
         var6 = var11;
         throw new InstallationException("Failed to install metadata " + var3 + ": " + var11.getMessage(), var11);
      } finally {
         this.metadataInstalled(var1, var2, var3, var5, var6);
      }
   }

   private void artifactInstalling(RepositorySystemSession var1, RequestTrace var2, Artifact var3, File var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.ARTIFACT_INSTALLING);
      var5.setTrace(var2);
      var5.setArtifact(var3);
      var5.setRepository(var1.getLocalRepositoryManager().getRepository());
      var5.setFile(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private void artifactInstalled(RepositorySystemSession var1, RequestTrace var2, Artifact var3, File var4, Exception var5) {
      RepositoryEvent.Builder var6 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.ARTIFACT_INSTALLED);
      var6.setTrace(var2);
      var6.setArtifact(var3);
      var6.setRepository(var1.getLocalRepositoryManager().getRepository());
      var6.setFile(var4);
      var6.setException(var5);
      this.repositoryEventDispatcher.dispatch(var6.build());
   }

   private void metadataInstalling(RepositorySystemSession var1, RequestTrace var2, Metadata var3, File var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_INSTALLING);
      var5.setTrace(var2);
      var5.setMetadata(var3);
      var5.setRepository(var1.getLocalRepositoryManager().getRepository());
      var5.setFile(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private void metadataInstalled(RepositorySystemSession var1, RequestTrace var2, Metadata var3, File var4, Exception var5) {
      RepositoryEvent.Builder var6 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_INSTALLED);
      var6.setTrace(var2);
      var6.setMetadata(var3);
      var6.setRepository(var1.getLocalRepositoryManager().getRepository());
      var6.setFile(var4);
      var6.setException(var5);
      this.repositoryEventDispatcher.dispatch(var6.build());
   }
}
