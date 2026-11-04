package org.apache.maven.repository.internal;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultVersionRangeResolver implements VersionRangeResolver, Service {
   private static final String MAVEN_METADATA_XML = "maven-metadata.xml";
   private MetadataResolver metadataResolver;
   private SyncContextFactory syncContextFactory;
   private RepositoryEventDispatcher repositoryEventDispatcher;

   public DefaultVersionRangeResolver() {
   }

   @Inject
   DefaultVersionRangeResolver(MetadataResolver var1, SyncContextFactory var2, RepositoryEventDispatcher var3) {
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

   public DefaultVersionRangeResolver setMetadataResolver(MetadataResolver var1) {
      this.metadataResolver = Objects.requireNonNull(var1, "metadataResolver cannot be null");
      return this;
   }

   public DefaultVersionRangeResolver setSyncContextFactory(SyncContextFactory var1) {
      this.syncContextFactory = Objects.requireNonNull(var1, "syncContextFactory cannot be null");
      return this;
   }

   public DefaultVersionRangeResolver setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repositoryEventDispatcher cannot be null");
      return this;
   }

   @Override
   public VersionRangeResult resolveVersionRange(RepositorySystemSession var1, VersionRangeRequest var2) throws VersionRangeResolutionException {
      VersionRangeResult var3 = new VersionRangeResult(var2);
      GenericVersionScheme var4 = new GenericVersionScheme();

      VersionConstraint var5;
      try {
         var5 = var4.parseVersionConstraint(var2.getArtifact().getVersion());
      } catch (InvalidVersionSpecificationException var12) {
         var3.addException(var12);
         throw new VersionRangeResolutionException(var3);
      }

      var3.setVersionConstraint(var5);
      if (var5.getRange() == null) {
         var3.addVersion(var5.getVersion());
      } else {
         Map var6 = this.getVersions(var1, var3, var2);
         ArrayList var7 = new ArrayList();

         for (Entry var9 : var6.entrySet()) {
            try {
               Version var10 = var4.parseVersion((String)var9.getKey());
               if (var5.containsVersion(var10)) {
                  var7.add(var10);
                  var3.setRepository(var10, (ArtifactRepository)var9.getValue());
               }
            } catch (InvalidVersionSpecificationException var11) {
               var3.addException(var11);
            }
         }

         Collections.sort(var7);
         var3.setVersions(var7);
      }

      return var3;
   }

   private Map<String, ArtifactRepository> getVersions(RepositorySystemSession var1, VersionRangeResult var2, VersionRangeRequest var3) {
      RequestTrace var4 = RequestTrace.newChild(var3.getTrace(), var3);
      HashMap var5 = new HashMap();
      DefaultMetadata var6 = new DefaultMetadata(
         var3.getArtifact().getGroupId(), var3.getArtifact().getArtifactId(), "maven-metadata.xml", Metadata.Nature.RELEASE_OR_SNAPSHOT
      );
      ArrayList var7 = new ArrayList(var3.getRepositories().size());
      var7.add(new MetadataRequest(var6, null, var3.getRequestContext()));

      for (RemoteRepository var9 : var3.getRepositories()) {
         MetadataRequest var10 = new MetadataRequest(var6, var9, var3.getRequestContext());
         var10.setDeleteLocalCopyIfMissing(true);
         var10.setTrace(var4);
         var7.add(var10);
      }

      List var16 = this.metadataResolver.resolveMetadata(var1, var7);
      WorkspaceReader var17 = var1.getWorkspaceReader();
      if (var17 != null) {
         for (String var12 : var17.findVersions(var3.getArtifact())) {
            var5.put(var12, var17.getRepository());
         }
      }

      for (MetadataResult var20 : var16) {
         var2.addException(var20.getException());
         Object var21 = var20.getRequest().getRepository();
         if (var21 == null) {
            var21 = var1.getLocalRepository();
         }

         Versioning var13 = this.readVersions(var1, var4, var20.getMetadata(), (ArtifactRepository)var21, var2);

         for (String var15 : var13.getVersions()) {
            if (!var5.containsKey(var15)) {
               var5.put(var15, var21);
            }
         }
      }

      return var5;
   }

   private Versioning readVersions(RepositorySystemSession var1, RequestTrace var2, Metadata var3, ArtifactRepository var4, VersionRangeResult var5) {
      Versioning var6 = null;

      try {
         if (var3 != null) {
            try (SyncContext var7 = this.syncContextFactory.newInstance(var1, true)) {
               var7.acquire(null, Collections.singleton(var3));
               if (var3.getFile() != null && var3.getFile().exists()) {
                  try (FileInputStream var9 = new FileInputStream(var3.getFile())) {
                     var6 = new MetadataXpp3Reader().read(var9, false).getVersioning();
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
}
