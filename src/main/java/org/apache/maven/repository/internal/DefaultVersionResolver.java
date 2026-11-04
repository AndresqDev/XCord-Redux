package org.apache.maven.repository.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.util.ConfigUtils;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultVersionResolver implements VersionResolver, Service {
   private static final String MAVEN_METADATA_XML = "maven-metadata.xml";
   private static final String RELEASE = "RELEASE";
   private static final String LATEST = "LATEST";
   private static final String SNAPSHOT = "SNAPSHOT";
   private MetadataResolver metadataResolver;
   private SyncContextFactory syncContextFactory;
   private RepositoryEventDispatcher repositoryEventDispatcher;

   public DefaultVersionResolver() {
   }

   @Inject
   DefaultVersionResolver(MetadataResolver var1, SyncContextFactory var2, RepositoryEventDispatcher var3) {
      this.setMetadataResolver(var1);
      this.setSyncContextFactory(var2);
      this.setRepositoryEventDispatcher(var3);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setMetadataResolver(var1.getService(MetadataResolver.class));
      this.setSyncContextFactory(var1.getService(SyncContextFactory.class));
      this.setRepositoryEventDispatcher(var1.getService(RepositoryEventDispatcher.class));
   }

   public DefaultVersionResolver setMetadataResolver(MetadataResolver var1) {
      this.metadataResolver = Objects.requireNonNull(var1, "metadataResolver cannot be null");
      return this;
   }

   public DefaultVersionResolver setSyncContextFactory(SyncContextFactory var1) {
      this.syncContextFactory = Objects.requireNonNull(var1, "syncContextFactory cannot be null");
      return this;
   }

   public DefaultVersionResolver setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repositoryEventDispatcher cannot be null");
      return this;
   }

   @Override
   public VersionResult resolveVersion(RepositorySystemSession var1, VersionRequest var2) throws VersionResolutionException {
      RequestTrace var3 = RequestTrace.newChild(var2.getTrace(), var2);
      Artifact var4 = var2.getArtifact();
      String var5 = var4.getVersion();
      VersionResult var6 = new VersionResult(var2);
      DefaultVersionResolver.Key var7 = null;
      RepositoryCache var8 = var1.getCache();
      if (var8 != null && !ConfigUtils.getBoolean(var1, false, "aether.versionResolver.noCache")) {
         var7 = new DefaultVersionResolver.Key(var1, var2);
         Object var9 = var8.get(var1, var7);
         if (var9 instanceof DefaultVersionResolver.Record) {
            DefaultVersionResolver.Record var19 = (DefaultVersionResolver.Record)var9;
            var6.setVersion(var19.version);
            var6.setRepository(this.getRepository(var1, var2.getRepositories(), var19.repoClass, var19.repoId));
            return var6;
         }
      }

      DefaultMetadata var17;
      if ("RELEASE".equals(var5)) {
         var17 = new DefaultMetadata(var4.getGroupId(), var4.getArtifactId(), "maven-metadata.xml", Metadata.Nature.RELEASE);
      } else if ("LATEST".equals(var5)) {
         var17 = new DefaultMetadata(var4.getGroupId(), var4.getArtifactId(), "maven-metadata.xml", Metadata.Nature.RELEASE_OR_SNAPSHOT);
      } else if (var5.endsWith("SNAPSHOT")) {
         WorkspaceReader var10 = var1.getWorkspaceReader();
         if (var10 != null && var10.findVersions(var4).contains(var5)) {
            var17 = null;
            var6.setRepository(var10.getRepository());
         } else {
            var17 = new DefaultMetadata(var4.getGroupId(), var4.getArtifactId(), var5, "maven-metadata.xml", Metadata.Nature.SNAPSHOT);
         }
      } else {
         var17 = null;
      }

      if (var17 == null) {
         var6.setVersion(var5);
      } else {
         ArrayList var18 = new ArrayList(var2.getRepositories().size());
         var18.add(new MetadataRequest(var17, null, var2.getRequestContext()));

         for (RemoteRepository var12 : var2.getRepositories()) {
            MetadataRequest var13 = new MetadataRequest(var17, var12, var2.getRequestContext());
            var13.setDeleteLocalCopyIfMissing(true);
            var13.setFavorLocalRepository(true);
            var13.setTrace(var3);
            var18.add(var13);
         }

         List var20 = this.metadataResolver.resolveMetadata(var1, var18);
         HashMap var21 = new HashMap();

         for (MetadataResult var14 : var20) {
            var6.addException(var14.getException());
            Object var15 = var14.getRequest().getRepository();
            if (var15 == null) {
               var15 = var1.getLocalRepository();
            }

            Versioning var16 = this.readVersions(var1, var3, var14.getMetadata(), (ArtifactRepository)var15, var6);
            this.merge(var4, var21, var16, (ArtifactRepository)var15);
         }

         if ("RELEASE".equals(var5)) {
            this.resolve(var6, var21, "RELEASE");
         } else if ("LATEST".equals(var5)) {
            if (!this.resolve(var6, var21, "LATEST")) {
               this.resolve(var6, var21, "RELEASE");
            }

            if (var6.getVersion() != null && var6.getVersion().endsWith("SNAPSHOT")) {
               VersionRequest var24 = new VersionRequest();
               var24.setArtifact(var4.setVersion(var6.getVersion()));
               if (var6.getRepository() instanceof RemoteRepository) {
                  RemoteRepository var25 = (RemoteRepository)var6.getRepository();
                  var24.setRepositories(Collections.singletonList(var25));
               } else {
                  var24.setRepositories(var2.getRepositories());
               }

               VersionResult var26 = this.resolveVersion(var1, var24);
               var6.setVersion(var26.getVersion());
               var6.setRepository(var26.getRepository());

               for (Exception var28 : var26.getExceptions()) {
                  var6.addException(var28);
               }
            }
         } else {
            String var23 = "SNAPSHOT" + this.getKey(var4.getClassifier(), var4.getExtension());
            this.merge(var21, "SNAPSHOT", var23);
            if (!this.resolve(var6, var21, var23)) {
               var6.setVersion(var5);
            }
         }

         if (StringUtils.isEmpty(var6.getVersion())) {
            throw new VersionResolutionException(var6);
         }
      }

      if (var7 != null && var17 != null && this.isSafelyCacheable(var1, var4)) {
         var8.put(var1, var7, new DefaultVersionResolver.Record(var6.getVersion(), var6.getRepository()));
      }

      return var6;
   }

   private boolean resolve(VersionResult var1, Map<String, DefaultVersionResolver.VersionInfo> var2, String var3) {
      DefaultVersionResolver.VersionInfo var4 = (DefaultVersionResolver.VersionInfo)var2.get(var3);
      if (var4 != null) {
         var1.setVersion(var4.version);
         var1.setRepository(var4.repository);
      }

      return var4 != null;
   }

   private Versioning readVersions(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4, VersionResult var5) {
      Versioning var6 = null;

      try {
         if (var3 != null) {
            try (SyncContext var7 = this.syncContextFactory.newInstance(var1, true)) {
               var7.acquire(null, Collections.singleton(var3));
               if (var3.getFile() != null && var3.getFile().exists()) {
                  try (FileInputStream var9 = new FileInputStream(var3.getFile())) {
                     var6 = new MetadataXpp3Reader().read(var9, false).getVersioning();
                     if (var6 != null && var4 instanceof LocalRepository && var6.getSnapshot() != null && var6.getSnapshot().getBuildNumber() > 0) {
                        Versioning var11 = new Versioning();
                        var11.setLastUpdated(var6.getLastUpdated());
                        var11.setSnapshot(new Snapshot());
                        var11.getSnapshot().setLocalCopy(true);
                        var6 = var11;
                        throw new IOException(
                           "Snapshot information corrupted with remote repository data, please verify that no remote repository uses the id '"
                              + var4.getId()
                              + "'"
                        );
                     }
                  }
               }
            }
         }
      } catch (Exception var39) {
         this.invalidMetadata(var1, var2, var3, var4, var39);
         var5.addException(var39);
      }

      return var6 != null ? var6 : new Versioning();
   }

   private void invalidMetadata(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4, Exception var5) {
      RepositoryEvent.Builder var6 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.METADATA_INVALID);
      var6.setTrace(var2);
      var6.setMetadata(var3);
      var6.setException(var5);
      var6.setRepository(var4);
      this.repositoryEventDispatcher.dispatch(var6.build());
   }

   private void merge(Artifact var1, Map<String, DefaultVersionResolver.VersionInfo> var2, Versioning var3, ArtifactRepository var4) {
      if (StringUtils.isNotEmpty(var3.getRelease())) {
         this.merge("RELEASE", var2, var3.getLastUpdated(), var3.getRelease(), var4);
      }

      if (StringUtils.isNotEmpty(var3.getLatest())) {
         this.merge("LATEST", var2, var3.getLastUpdated(), var3.getLatest(), var4);
      }

      for (SnapshotVersion var6 : var3.getSnapshotVersions()) {
         if (StringUtils.isNotEmpty(var6.getVersion())) {
            String var7 = this.getKey(var6.getClassifier(), var6.getExtension());
            this.merge("SNAPSHOT" + var7, var2, var6.getUpdated(), var6.getVersion(), var4);
         }
      }

      Snapshot var8 = var3.getSnapshot();
      if (var8 != null && var3.getSnapshotVersions().isEmpty()) {
         String var9 = var1.getVersion();
         if (var8.getTimestamp() != null && var8.getBuildNumber() > 0) {
            String var10 = var8.getTimestamp() + '-' + var8.getBuildNumber();
            var9 = var9.substring(0, var9.length() - "SNAPSHOT".length()) + var10;
         }

         this.merge("SNAPSHOT", var2, var3.getLastUpdated(), var9, var4);
      }
   }

   private void merge(String var1, Map<String, DefaultVersionResolver.VersionInfo> var2, String var3, String var4, ArtifactRepository var5) {
      DefaultVersionResolver.VersionInfo var6 = (DefaultVersionResolver.VersionInfo)var2.get(var1);
      if (var6 == null) {
         var6 = new DefaultVersionResolver.VersionInfo(var3, var4, var5);
         var2.put(var1, var6);
      } else if (var6.isOutdated(var3)) {
         var6.version = var4;
         var6.repository = var5;
         var6.timestamp = var3;
      }
   }

   private void merge(Map<String, DefaultVersionResolver.VersionInfo> var1, String var2, String var3) {
      DefaultVersionResolver.VersionInfo var4 = (DefaultVersionResolver.VersionInfo)var1.get(var2);
      DefaultVersionResolver.VersionInfo var5 = (DefaultVersionResolver.VersionInfo)var1.get(var3);
      if (var5 == null || var4 != null && var5.isOutdated(var4.timestamp) && var4.repository != var5.repository) {
         var1.put(var3, var4);
      }
   }

   private String getKey(String var1, String var2) {
      return StringUtils.clean(var1) + ':' + StringUtils.clean(var2);
   }

   private ArtifactRepository getRepository(RepositorySystemSession var1, List<RemoteRepository> var2, Class<?> var3, String var4) {
      if (var3 != null) {
         if (WorkspaceRepository.class.isAssignableFrom(var3)) {
            return var1.getWorkspaceReader().getRepository();
         }

         if (LocalRepository.class.isAssignableFrom(var3)) {
            return var1.getLocalRepository();
         }

         for (RemoteRepository var6 : var2) {
            if (var4.equals(var6.getId())) {
               return var6;
            }
         }
      }

      return null;
   }

   private boolean isSafelyCacheable(RepositorySystemSession var1, Artifact var2) {
      WorkspaceReader var3 = var1.getWorkspaceReader();
      if (var3 == null) {
         return true;
      } else {
         Artifact var4 = ArtifactDescriptorUtils.toPomArtifact(var2);
         return var3.findArtifact(var4) == null;
      }
   }

   private static class Key {
      private final String groupId;
      private final String artifactId;
      private final String classifier;
      private final String extension;
      private final String version;
      private final String context;
      private final File localRepo;
      private final WorkspaceRepository workspace;
      private final List<RemoteRepository> repositories;
      private final int hashCode;

      Key(RepositorySystemSession var1, VersionRequest var2) {
         Artifact var3 = var2.getArtifact();
         this.groupId = var3.getGroupId();
         this.artifactId = var3.getArtifactId();
         this.classifier = var3.getClassifier();
         this.extension = var3.getExtension();
         this.version = var3.getVersion();
         this.localRepo = var1.getLocalRepository().getBasedir();
         WorkspaceReader var4 = var1.getWorkspaceReader();
         this.workspace = var4 != null ? var4.getRepository() : null;
         this.repositories = new ArrayList<>(var2.getRepositories().size());
         boolean var5 = false;

         for (RemoteRepository var7 : var2.getRepositories()) {
            if (var7.isRepositoryManager()) {
               var5 = true;
               this.repositories.addAll(var7.getMirroredRepositories());
            } else {
               this.repositories.add(var7);
            }
         }

         this.context = var5 ? var2.getRequestContext() : "";
         int var8 = 17;
         var8 = var8 * 31 + this.groupId.hashCode();
         var8 = var8 * 31 + this.artifactId.hashCode();
         var8 = var8 * 31 + this.classifier.hashCode();
         var8 = var8 * 31 + this.extension.hashCode();
         var8 = var8 * 31 + this.version.hashCode();
         var8 = var8 * 31 + this.localRepo.hashCode();
         var8 = var8 * 31 + this.repositories.hashCode();
         this.hashCode = var8;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && this.getClass().equals(var1.getClass())) {
            DefaultVersionResolver.Key var2 = (DefaultVersionResolver.Key)var1;
            return this.artifactId.equals(var2.artifactId)
               && this.groupId.equals(var2.groupId)
               && this.classifier.equals(var2.classifier)
               && this.extension.equals(var2.extension)
               && this.version.equals(var2.version)
               && this.context.equals(var2.context)
               && this.localRepo.equals(var2.localRepo)
               && Objects.equals(this.workspace, var2.workspace)
               && this.repositories.equals(var2.repositories);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }
   }

   private static class Record {
      final String version;
      final String repoId;
      final Class<?> repoClass;

      Record(String var1, ArtifactRepository var2) {
         this.version = var1;
         if (var2 != null) {
            this.repoId = var2.getId();
            this.repoClass = var2.getClass();
         } else {
            this.repoId = null;
            this.repoClass = null;
         }
      }
   }

   private static class VersionInfo {
      String timestamp;
      String version;
      ArtifactRepository repository;

      VersionInfo(String var1, String var2, ArtifactRepository var3) {
         this.timestamp = var1 != null ? var1 : "";
         this.version = var2;
         this.repository = var3;
      }

      boolean isOutdated(String var1) {
         return var1 != null && var1.compareTo(this.timestamp) > 0;
      }
   }
}
