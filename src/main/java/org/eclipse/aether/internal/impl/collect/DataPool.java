package org.eclipse.aether.internal.impl.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

final class DataPool {
   private static final String ARTIFACT_POOL = DataPool.class.getName() + "$Artifact";
   private static final String DEPENDENCY_POOL = DataPool.class.getName() + "$Dependency";
   private static final String DESCRIPTORS = DataPool.class.getName() + "$Descriptors";
   static final ArtifactDescriptorResult NO_DESCRIPTOR = new ArtifactDescriptorResult(new ArtifactDescriptorRequest());
   private ObjectPool<Artifact> artifacts;
   private ObjectPool<Dependency> dependencies;
   private Map<Object, DataPool.Descriptor> descriptors;
   private Map<Object, DataPool.Constraint> constraints = new HashMap<>();
   private Map<Object, List<DependencyNode>> nodes = new HashMap<>(256);

   DataPool(RepositorySystemSession var1) {
      RepositoryCache var2 = var1.getCache();
      if (var2 != null) {
         this.artifacts = (ObjectPool<Artifact>)var2.get(var1, ARTIFACT_POOL);
         this.dependencies = (ObjectPool<Dependency>)var2.get(var1, DEPENDENCY_POOL);
         this.descriptors = (Map<Object, DataPool.Descriptor>)var2.get(var1, DESCRIPTORS);
      }

      if (this.artifacts == null) {
         this.artifacts = new ObjectPool<>();
         if (var2 != null) {
            var2.put(var1, ARTIFACT_POOL, this.artifacts);
         }
      }

      if (this.dependencies == null) {
         this.dependencies = new ObjectPool<>();
         if (var2 != null) {
            var2.put(var1, DEPENDENCY_POOL, this.dependencies);
         }
      }

      if (this.descriptors == null) {
         this.descriptors = Collections.synchronizedMap(new WeakHashMap<>(256));
         if (var2 != null) {
            var2.put(var1, DESCRIPTORS, this.descriptors);
         }
      }
   }

   public Artifact intern(Artifact var1) {
      return this.artifacts.intern(var1);
   }

   public Dependency intern(Dependency var1) {
      return this.dependencies.intern(var1);
   }

   Object toKey(ArtifactDescriptorRequest var1) {
      return var1.getArtifact();
   }

   ArtifactDescriptorResult getDescriptor(Object var1, ArtifactDescriptorRequest var2) {
      DataPool.Descriptor var3 = this.descriptors.get(var1);
      return var3 != null ? var3.toResult(var2) : null;
   }

   void putDescriptor(Object var1, ArtifactDescriptorResult var2) {
      this.descriptors.put(var1, new DataPool.GoodDescriptor(var2));
   }

   void putDescriptor(Object var1, ArtifactDescriptorException var2) {
      this.descriptors.put(var1, DataPool.BadDescriptor.INSTANCE);
   }

   Object toKey(VersionRangeRequest var1) {
      return new DataPool.ConstraintKey(var1);
   }

   VersionRangeResult getConstraint(Object var1, VersionRangeRequest var2) {
      DataPool.Constraint var3 = this.constraints.get(var1);
      return var3 != null ? var3.toResult(var2) : null;
   }

   void putConstraint(Object var1, VersionRangeResult var2) {
      this.constraints.put(var1, new DataPool.Constraint(var2));
   }

   public Object toKey(
      Artifact var1, List<RemoteRepository> var2, DependencySelector var3, DependencyManager var4, DependencyTraverser var5, VersionFilter var6
   ) {
      return new DataPool.GraphKey(var1, var2, var3, var4, var5, var6);
   }

   public List<DependencyNode> getChildren(Object var1) {
      return this.nodes.get(var1);
   }

   public void putChildren(Object var1, List<DependencyNode> var2) {
      this.nodes.put(var1, var2);
   }

   static final class BadDescriptor extends DataPool.Descriptor {
      static final DataPool.BadDescriptor INSTANCE = new DataPool.BadDescriptor();

      @Override
      public ArtifactDescriptorResult toResult(ArtifactDescriptorRequest var1) {
         return DataPool.NO_DESCRIPTOR;
      }
   }

   private static final class Constraint {
      final DataPool.Constraint.VersionRepo[] repositories;
      final VersionConstraint versionConstraint;

      Constraint(VersionRangeResult var1) {
         this.versionConstraint = var1.getVersionConstraint();
         List var2 = var1.getVersions();
         this.repositories = new DataPool.Constraint.VersionRepo[var2.size()];
         int var3 = 0;

         for (Version var5 : var2) {
            this.repositories[var3++] = new DataPool.Constraint.VersionRepo(var5, var1.getRepository(var5));
         }
      }

      VersionRangeResult toResult(VersionRangeRequest var1) {
         VersionRangeResult var2 = new VersionRangeResult(var1);

         for (DataPool.Constraint.VersionRepo var6 : this.repositories) {
            var2.addVersion(var6.version);
            var2.setRepository(var6.version, var6.repo);
         }

         var2.setVersionConstraint(this.versionConstraint);
         return var2;
      }

      static final class VersionRepo {
         final Version version;
         final ArtifactRepository repo;

         VersionRepo(Version var1, ArtifactRepository var2) {
            this.version = var1;
            this.repo = var2;
         }
      }
   }

   static final class ConstraintKey {
      private final Artifact artifact;
      private final List<RemoteRepository> repositories;
      private final int hashCode;

      ConstraintKey(VersionRangeRequest var1) {
         this.artifact = var1.getArtifact();
         this.repositories = var1.getRepositories();
         this.hashCode = this.artifact.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof DataPool.ConstraintKey)) {
            return false;
         } else {
            DataPool.ConstraintKey var2 = (DataPool.ConstraintKey)var1;
            return this.artifact.equals(var2.artifact) && equals(this.repositories, var2.repositories);
         }
      }

      private static boolean equals(List<RemoteRepository> var0, List<RemoteRepository> var1) {
         if (var0.size() != var1.size()) {
            return false;
         } else {
            int var2 = 0;

            for (int var3 = var0.size(); var2 < var3; var2++) {
               RemoteRepository var4 = (RemoteRepository)var0.get(var2);
               RemoteRepository var5 = (RemoteRepository)var1.get(var2);
               if (var4.isRepositoryManager() != var5.isRepositoryManager()) {
                  return false;
               }

               if (var4.isRepositoryManager()) {
                  if (!equals(var4.getMirroredRepositories(), var5.getMirroredRepositories())) {
                     return false;
                  }
               } else {
                  if (!var4.getUrl().equals(var5.getUrl())) {
                     return false;
                  }

                  if (var4.getPolicy(true).isEnabled() != var5.getPolicy(true).isEnabled()) {
                     return false;
                  }

                  if (var4.getPolicy(false).isEnabled() != var5.getPolicy(false).isEnabled()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }
   }

   abstract static class Descriptor {
      public abstract ArtifactDescriptorResult toResult(ArtifactDescriptorRequest var1);
   }

   static final class GoodDescriptor extends DataPool.Descriptor {
      final Artifact artifact;
      final List<Artifact> relocations;
      final Collection<Artifact> aliases;
      final List<RemoteRepository> repositories;
      final List<Dependency> dependencies;
      final List<Dependency> managedDependencies;

      GoodDescriptor(ArtifactDescriptorResult var1) {
         this.artifact = var1.getArtifact();
         this.relocations = var1.getRelocations();
         this.aliases = var1.getAliases();
         this.dependencies = var1.getDependencies();
         this.managedDependencies = var1.getManagedDependencies();
         this.repositories = var1.getRepositories();
      }

      @Override
      public ArtifactDescriptorResult toResult(ArtifactDescriptorRequest var1) {
         ArtifactDescriptorResult var2 = new ArtifactDescriptorResult(var1);
         var2.setArtifact(this.artifact);
         var2.setRelocations(this.relocations);
         var2.setAliases(this.aliases);
         var2.setDependencies(this.dependencies);
         var2.setManagedDependencies(this.managedDependencies);
         var2.setRepositories(this.repositories);
         return var2;
      }
   }

   static final class GraphKey {
      private final Artifact artifact;
      private final List<RemoteRepository> repositories;
      private final DependencySelector selector;
      private final DependencyManager manager;
      private final DependencyTraverser traverser;
      private final VersionFilter filter;
      private final int hashCode;

      GraphKey(Artifact var1, List<RemoteRepository> var2, DependencySelector var3, DependencyManager var4, DependencyTraverser var5, VersionFilter var6) {
         this.artifact = var1;
         this.repositories = var2;
         this.selector = var3;
         this.manager = var4;
         this.traverser = var5;
         this.filter = var6;
         this.hashCode = Objects.hash(var1, var2, var3, var4, var5, var6);
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof DataPool.GraphKey)) {
            return false;
         } else {
            DataPool.GraphKey var2 = (DataPool.GraphKey)var1;
            return Objects.equals(this.artifact, var2.artifact)
               && Objects.equals(this.repositories, var2.repositories)
               && Objects.equals(this.selector, var2.selector)
               && Objects.equals(this.manager, var2.manager)
               && Objects.equals(this.traverser, var2.traverser)
               && Objects.equals(this.filter, var2.filter);
         }
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }
   }
}
