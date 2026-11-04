package org.eclipse.aether.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.UpdateCheck;
import org.eclipse.aether.impl.UpdateCheckManager;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.LocalMetadataRegistration;
import org.eclipse.aether.repository.LocalMetadataRequest;
import org.eclipse.aether.repository.LocalMetadataResult;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.RepositoryOfflineException;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.concurrency.RunnableErrorForwarder;
import org.eclipse.aether.util.concurrency.WorkerThreadFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultMetadataResolver implements MetadataResolver, Service {
   private static final String CONFIG_PROP_THREADS = "aether.metadataResolver.threads";
   private RepositoryEventDispatcher repositoryEventDispatcher;
   private UpdateCheckManager updateCheckManager;
   private RepositoryConnectorProvider repositoryConnectorProvider;
   private RemoteRepositoryManager remoteRepositoryManager;
   private SyncContextFactory syncContextFactory;
   private OfflineController offlineController;

   public DefaultMetadataResolver() {
   }

   @Inject
   DefaultMetadataResolver(
      RepositoryEventDispatcher var1,
      UpdateCheckManager var2,
      RepositoryConnectorProvider var3,
      RemoteRepositoryManager var4,
      SyncContextFactory var5,
      OfflineController var6
   ) {
      this.setRepositoryEventDispatcher(var1);
      this.setUpdateCheckManager(var2);
      this.setRepositoryConnectorProvider(var3);
      this.setRemoteRepositoryManager(var4);
      this.setSyncContextFactory(var5);
      this.setOfflineController(var6);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setRepositoryEventDispatcher(var1.getService(RepositoryEventDispatcher.class));
      this.setUpdateCheckManager(var1.getService(UpdateCheckManager.class));
      this.setRepositoryConnectorProvider(var1.getService(RepositoryConnectorProvider.class));
      this.setRemoteRepositoryManager(var1.getService(RemoteRepositoryManager.class));
      this.setSyncContextFactory(var1.getService(SyncContextFactory.class));
      this.setOfflineController(var1.getService(OfflineController.class));
   }

   public DefaultMetadataResolver setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repository event dispatcher cannot be null");
      return this;
   }

   public DefaultMetadataResolver setUpdateCheckManager(UpdateCheckManager var1) {
      this.updateCheckManager = Objects.requireNonNull(var1, "update check manager cannot be null");
      return this;
   }

   public DefaultMetadataResolver setRepositoryConnectorProvider(RepositoryConnectorProvider var1) {
      this.repositoryConnectorProvider = Objects.requireNonNull(var1, "repository connector provider cannot be null");
      return this;
   }

   public DefaultMetadataResolver setRemoteRepositoryManager(RemoteRepositoryManager var1) {
      this.remoteRepositoryManager = Objects.requireNonNull(var1, "remote repository provider cannot be null");
      return this;
   }

   public DefaultMetadataResolver setSyncContextFactory(SyncContextFactory var1) {
      this.syncContextFactory = Objects.requireNonNull(var1, "sync context factory cannot be null");
      return this;
   }

   public DefaultMetadataResolver setOfflineController(OfflineController var1) {
      this.offlineController = Objects.requireNonNull(var1, "offline controller cannot be null");
      return this;
   }

   @Override
   public List<MetadataResult> resolveMetadata(RepositorySystemSession var1, Collection<? extends MetadataRequest> var2) {
      List var17;
      try (SyncContext var3 = this.syncContextFactory.newInstance(var1, false)) {
         ArrayList var5 = new ArrayList(var2.size());

         for (MetadataRequest var7 : var2) {
            var5.add(var7.getMetadata());
         }

         var3.acquire(null, var5);
         var17 = this.resolve(var1, var2);
      }

      return var17;
   }

   private List<MetadataResult> resolve(RepositorySystemSession var1, Collection<? extends MetadataRequest> var2) {
      ArrayList var3 = new ArrayList(var2.size());
      ArrayList var4 = new ArrayList(var2.size());
      HashMap var5 = new HashMap();

      for (MetadataRequest var7 : var2) {
         RequestTrace var8 = RequestTrace.newChild(var7.getTrace(), var7);
         MetadataResult var9 = new MetadataResult(var7);
         var3.add(var9);
         Metadata var10 = var7.getMetadata();
         RemoteRepository var11 = var7.getRepository();
         if (var11 == null) {
            LocalRepository var41 = var1.getLocalRepositoryManager().getRepository();
            this.metadataResolving(var1, var8, var10, var41);
            File var44 = this.getLocalFile(var1, var10);
            if (var44 != null) {
               var10 = var10.setFile(var44);
               var9.setMetadata(var10);
            } else {
               var9.setException(new MetadataNotFoundException(var10, var41));
            }

            this.metadataResolved(var1, var8, var10, var41, var9.getException());
         } else {
            List var12 = this.getEnabledSourceRepositories(var11, var10.getNature());
            if (!var12.isEmpty()) {
               this.metadataResolving(var1, var8, var10, var11);
               LocalRepositoryManager var13 = var1.getLocalRepositoryManager();
               LocalMetadataRequest var14 = new LocalMetadataRequest(var10, var11, var7.getRequestContext());
               LocalMetadataResult var15 = var13.find(var1, var14);
               File var16 = var15.getFile();

               try {
                  Utils.checkOffline(var1, this.offlineController, var11);
               } catch (RepositoryOfflineException var28) {
                  if (var16 != null) {
                     var10 = var10.setFile(var16);
                     var9.setMetadata(var10);
                  } else {
                     String var18 = "Cannot access "
                        + var11.getId()
                        + " ("
                        + var11.getUrl()
                        + ") in offline mode and the metadata "
                        + var10
                        + " has not been downloaded from it before";
                     var9.setException(new MetadataNotFoundException(var10, var11, var18, var28));
                  }

                  this.metadataResolved(var1, var8, var10, var11, var9.getException());
                  continue;
               }

               Long var17 = null;
               if (var7.isFavorLocalRepository()) {
                  File var45 = this.getLocalFile(var1, var10);
                  var17 = (Long)var5.get(var45);
                  if (var17 == null) {
                     var17 = var45 != null ? var45.lastModified() : 0L;
                     var5.put(var45, var17);
                  }
               }

               ArrayList var46 = new ArrayList();
               RepositoryException var19 = null;

               for (RemoteRepository var21 : var12) {
                  UpdateCheck var22 = new UpdateCheck();
                  var22.setLocalLastUpdated(var17 != null ? var17 : 0L);
                  var22.setItem(var10);
                  File var23 = new File(
                     var1.getLocalRepository().getBasedir(), var1.getLocalRepositoryManager().getPathForRemoteMetadata(var10, var11, var7.getRequestContext())
                  );
                  var22.setFile(var23);
                  var22.setRepository(var11);
                  var22.setAuthoritativeRepository(var21);
                  var22.setPolicy(this.getPolicy(var1, var21, var10.getNature()).getUpdatePolicy());
                  if (var15.isStale()) {
                     var46.add(var22);
                  } else {
                     this.updateCheckManager.checkMetadata(var1, var22);
                     if (var22.isRequired()) {
                        var46.add(var22);
                     } else if (var19 == null) {
                        var19 = var22.getException();
                     }
                  }
               }

               if (!var46.isEmpty()) {
                  RepositoryPolicy var47 = this.getPolicy(var1, var11, var10.getNature());
                  File var48 = new File(
                     var1.getLocalRepository().getBasedir(),
                     var1.getLocalRepositoryManager().getPathForRemoteMetadata(var10, var7.getRepository(), var7.getRequestContext())
                  );
                  this.metadataDownloading(var1, var8, var9.getRequest().getMetadata(), var9.getRequest().getRepository());
                  DefaultMetadataResolver.ResolveTask var49 = new DefaultMetadataResolver.ResolveTask(var1, var8, var9, var48, var46, var47.getChecksumPolicy());
                  var4.add(var49);
               } else {
                  var9.setException(var19);
                  if (var16 != null) {
                     var10 = var10.setFile(var16);
                     var9.setMetadata(var10);
                  }

                  this.metadataResolved(var1, var8, var10, var11, var9.getException());
               }
            }
         }
      }

      if (!var4.isEmpty()) {
         int var29 = ConfigUtils.getInteger(var1, 4, "aether.metadataResolver.threads");
         Executor var30 = this.getExecutor(Math.min(var4.size(), var29));

         try {
            RunnableErrorForwarder var31 = new RunnableErrorForwarder();

            for (DefaultMetadataResolver.ResolveTask var36 : var4) {
               var30.execute(var31.wrap(var36));
            }

            var31.await();

            for (DefaultMetadataResolver.ResolveTask var37 : var4) {
               for (UpdateCheck var42 : var37.checks) {
                  this.updateCheckManager.touchMetadata(var37.session, var42.setException(var37.exception));
               }

               this.metadataDownloaded(var1, var37.trace, var37.request.getMetadata(), var37.request.getRepository(), var37.metadataFile, var37.exception);
               var37.result.setException(var37.exception);
            }
         } finally {
            this.shutdown(var30);
         }

         for (DefaultMetadataResolver.ResolveTask var35 : var4) {
            Metadata var38 = var35.request.getMetadata();
            LocalMetadataRequest var40 = new LocalMetadataRequest(var38, var35.request.getRepository(), var35.request.getRequestContext());
            File var43 = var1.getLocalRepositoryManager().find(var1, var40).getFile();
            if (var43 != null) {
               var38 = var38.setFile(var43);
               var35.result.setMetadata(var38);
            }

            if (var35.result.getException() == null) {
               var35.result.setUpdated(true);
            }

            this.metadataResolved(var1, var35.trace, var38, var35.request.getRepository(), var35.result.getException());
         }
      }

      return var3;
   }

   private File getLocalFile(RepositorySystemSession var1, Metadata var2) {
      LocalRepositoryManager var3 = var1.getLocalRepositoryManager();
      LocalMetadataResult var4 = var3.find(var1, new LocalMetadataRequest(var2, null, null));
      return var4.getFile();
   }

   private List<RemoteRepository> getEnabledSourceRepositories(RemoteRepository var1, Metadata.Nature var2) {
      ArrayList var3 = new ArrayList();
      if (var1.isRepositoryManager()) {
         for (RemoteRepository var5 : var1.getMirroredRepositories()) {
            if (this.isEnabled(var5, var2)) {
               var3.add(var5);
            }
         }
      } else if (this.isEnabled(var1, var2)) {
         var3.add(var1);
      }

      return var3;
   }

   private boolean isEnabled(RemoteRepository var1, Metadata.Nature var2) {
      return !Metadata.Nature.SNAPSHOT.equals(var2) && var1.getPolicy(false).isEnabled()
         ? true
         : !Metadata.Nature.RELEASE.equals(var2) && var1.getPolicy(true).isEnabled();
   }

   private RepositoryPolicy getPolicy(RepositorySystemSession var1, RemoteRepository var2, Metadata.Nature var3) {
      boolean var4 = !Metadata.Nature.SNAPSHOT.equals(var3);
      boolean var5 = !Metadata.Nature.RELEASE.equals(var3);
      return this.remoteRepositoryManager.getPolicy(var1, var2, var4, var5);
   }

   private void metadataResolving(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_RESOLVING);
      var5.setTrace(var2);
      var5.setMetadata(var3);
      var5.setRepository(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private void metadataResolved(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4, Exception var5) {
      RepositoryEvent.Builder var6 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_RESOLVED);
      var6.setTrace(var2);
      var6.setMetadata(var3);
      var6.setRepository(var4);
      var6.setException(var5);
      var6.setFile(var3.getFile());
      this.repositoryEventDispatcher.dispatch(var6.build());
   }

   private void metadataDownloading(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_DOWNLOADING);
      var5.setTrace(var2);
      var5.setMetadata(var3);
      var5.setRepository(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private void metadataDownloaded(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4, File var5, Exception var6) {
      RepositoryEvent.Builder var7 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_DOWNLOADED);
      var7.setTrace(var2);
      var7.setMetadata(var3);
      var7.setRepository(var4);
      var7.setException(var6);
      var7.setFile(var5);
      this.repositoryEventDispatcher.dispatch(var7.build());
   }

   private Executor getExecutor(int var1) {
      return (Executor)(var1 <= 1 ? new Executor() {
         @Override
         public void execute(Runnable var1) {
            var1.run();
         }
      } : new ThreadPoolExecutor(var1, var1, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new WorkerThreadFactory(null)));
   }

   private void shutdown(Executor var1) {
      if (var1 instanceof ExecutorService) {
         ((ExecutorService)var1).shutdown();
      }
   }

   class ResolveTask implements Runnable {
      final RepositorySystemSession session;
      final RequestTrace trace;
      final MetadataResult result;
      final MetadataRequest request;
      final File metadataFile;
      final String policy;
      final List<UpdateCheck<Metadata, MetadataTransferException>> checks;
      volatile MetadataTransferException exception;

      ResolveTask(
         RepositorySystemSession var2,
         RequestTrace var3,
         MetadataResult var4,
         File var5,
         List<UpdateCheck<Metadata, MetadataTransferException>> var6,
         String var7
      ) {
         this.session = var2;
         this.trace = var3;
         this.result = var4;
         this.request = var4.getRequest();
         this.metadataFile = var5;
         this.policy = var7;
         this.checks = var6;
      }

      @Override
      public void run() {
         Metadata var1 = this.request.getMetadata();
         RemoteRepository var2 = this.request.getRepository();

         try {
            ArrayList var3 = new ArrayList();

            for (UpdateCheck var5 : this.checks) {
               var3.add(var5.getAuthoritativeRepository());
            }

            MetadataDownload var19 = new MetadataDownload();
            var19.setMetadata(var1);
            var19.setRequestContext(this.request.getRequestContext());
            var19.setFile(this.metadataFile);
            var19.setChecksumPolicy(this.policy);
            var19.setRepositories(var3);
            var19.setListener(SafeTransferListener.wrap(this.session));
            var19.setTrace(this.trace);

            try (RepositoryConnector var20 = DefaultMetadataResolver.this.repositoryConnectorProvider.newRepositoryConnector(this.session, var2)) {
               var20.get(null, Arrays.asList(var19));
            }

            this.exception = var19.getException();
            if (this.exception == null) {
               List var21 = Collections.singletonList(this.request.getRequestContext());
               LocalMetadataRegistration var22 = new LocalMetadataRegistration(var1, var2, var21);
               this.session.getLocalRepositoryManager().add(this.session, var22);
            } else if (this.request.isDeleteLocalCopyIfMissing() && this.exception instanceof MetadataNotFoundException) {
               var19.getFile().delete();
            }
         } catch (NoRepositoryConnectorException var18) {
            this.exception = new MetadataTransferException(var1, var2, var18);
         }
      }
   }
}
