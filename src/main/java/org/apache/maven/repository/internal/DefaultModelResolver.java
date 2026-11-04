package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

class DefaultModelResolver implements ModelResolver {
   private final RepositorySystemSession session;
   private final RequestTrace trace;
   private final String context;
   private List<RemoteRepository> repositories;
   private final List<RemoteRepository> externalRepositories;
   private final ArtifactResolver resolver;
   private final VersionRangeResolver versionRangeResolver;
   private final RemoteRepositoryManager remoteRepositoryManager;
   private final Set<String> repositoryIds;

   DefaultModelResolver(
      RepositorySystemSession var1,
      RequestTrace var2,
      String var3,
      ArtifactResolver var4,
      VersionRangeResolver var5,
      RemoteRepositoryManager var6,
      List<RemoteRepository> var7
   ) {
      this.session = var1;
      this.trace = var2;
      this.context = var3;
      this.resolver = var4;
      this.versionRangeResolver = var5;
      this.remoteRepositoryManager = var6;
      this.repositories = var7;
      this.externalRepositories = Collections.unmodifiableList(new ArrayList<>(var7));
      this.repositoryIds = new HashSet<>();
   }

   private DefaultModelResolver(DefaultModelResolver var1) {
      this.session = var1.session;
      this.trace = var1.trace;
      this.context = var1.context;
      this.resolver = var1.resolver;
      this.versionRangeResolver = var1.versionRangeResolver;
      this.remoteRepositoryManager = var1.remoteRepositoryManager;
      this.repositories = new ArrayList<>(var1.repositories);
      this.externalRepositories = var1.externalRepositories;
      this.repositoryIds = new HashSet<>(var1.repositoryIds);
   }

   @Override
   public void addRepository(Repository var1) throws InvalidRepositoryException {
      this.addRepository(var1, false);
   }

   @Override
   public void addRepository(Repository var1, boolean var2) throws InvalidRepositoryException {
      if (!this.session.isIgnoreArtifactDescriptorRepositories()) {
         if (!this.repositoryIds.add(var1.getId())) {
            if (!var2) {
               return;
            }

            removeMatchingRepository(this.repositories, var1.getId());
         }

         List var3 = Collections.singletonList(ArtifactDescriptorUtils.toRemoteRepository(var1));
         this.repositories = this.remoteRepositoryManager.aggregateRepositories(this.session, this.repositories, var3, true);
      }
   }

   private static void removeMatchingRepository(Iterable<RemoteRepository> var0, String var1) {
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         RemoteRepository var3 = (RemoteRepository)var2.next();
         if (var3.getId().equals(var1)) {
            var2.remove();
         }
      }
   }

   @Override
   public ModelResolver newCopy() {
      return new DefaultModelResolver(this);
   }

   @Override
   public ModelSource resolveModel(String var1, String var2, String var3) throws UnresolvableModelException {
      DefaultArtifact var4 = new DefaultArtifact(var1, var2, "", "pom", var3);

      try {
         ArtifactRequest var5 = new ArtifactRequest(var4, this.repositories, this.context);
         var5.setTrace(this.trace);
         var7 = this.resolver.resolveArtifact(this.session, var5).getArtifact();
      } catch (ArtifactResolutionException var6) {
         throw new UnresolvableModelException(var6.getMessage(), var1, var2, var3, var6);
      }

      File var8 = var7.getFile();
      return new FileModelSource(var8);
   }

   @Override
   public ModelSource resolveModel(Parent var1) throws UnresolvableModelException {
      try {
         DefaultArtifact var2 = new DefaultArtifact(var1.getGroupId(), var1.getArtifactId(), "", "pom", var1.getVersion());
         VersionRangeRequest var3 = new VersionRangeRequest(var2, this.repositories, this.context);
         var3.setTrace(this.trace);
         VersionRangeResult var4 = this.versionRangeResolver.resolveVersionRange(this.session, var3);
         if (var4.getHighestVersion() == null) {
            throw new UnresolvableModelException(
               String.format("No versions matched the requested parent version range '%s'", var1.getVersion()),
               var1.getGroupId(),
               var1.getArtifactId(),
               var1.getVersion()
            );
         } else if (var4.getVersionConstraint() != null
            && var4.getVersionConstraint().getRange() != null
            && var4.getVersionConstraint().getRange().getUpperBound() == null) {
            throw new UnresolvableModelException(
               String.format("The requested parent version range '%s' does not specify an upper bound", var1.getVersion()),
               var1.getGroupId(),
               var1.getArtifactId(),
               var1.getVersion()
            );
         } else {
            var1.setVersion(var4.getHighestVersion().toString());
            return this.resolveModel(var1.getGroupId(), var1.getArtifactId(), var1.getVersion());
         }
      } catch (VersionRangeResolutionException var5) {
         throw new UnresolvableModelException(var5.getMessage(), var1.getGroupId(), var1.getArtifactId(), var1.getVersion(), var5);
      }
   }

   @Override
   public ModelSource resolveModel(Dependency var1) throws UnresolvableModelException {
      try {
         DefaultArtifact var2 = new DefaultArtifact(var1.getGroupId(), var1.getArtifactId(), "", "pom", var1.getVersion());
         VersionRangeRequest var3 = new VersionRangeRequest(var2, this.repositories, this.context);
         var3.setTrace(this.trace);
         VersionRangeResult var4 = this.versionRangeResolver.resolveVersionRange(this.session, var3);
         if (var4.getHighestVersion() == null) {
            throw new UnresolvableModelException(
               String.format("No versions matched the requested dependency version range '%s'", var1.getVersion()),
               var1.getGroupId(),
               var1.getArtifactId(),
               var1.getVersion()
            );
         } else if (var4.getVersionConstraint() != null
            && var4.getVersionConstraint().getRange() != null
            && var4.getVersionConstraint().getRange().getUpperBound() == null) {
            throw new UnresolvableModelException(
               String.format("The requested dependency version range '%s' does not specify an upper bound", var1.getVersion()),
               var1.getGroupId(),
               var1.getArtifactId(),
               var1.getVersion()
            );
         } else {
            var1.setVersion(var4.getHighestVersion().toString());
            return this.resolveModel(var1.getGroupId(), var1.getArtifactId(), var1.getVersion());
         }
      } catch (VersionRangeResolutionException var5) {
         throw new UnresolvableModelException(var5.getMessage(), var1.getGroupId(), var1.getArtifactId(), var1.getVersion(), var5);
      }
   }
}
