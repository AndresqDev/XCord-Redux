package org.eclipse.aether.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public final class DefaultDependencyNode implements DependencyNode {
   private List<DependencyNode> children;
   private Dependency dependency;
   private Artifact artifact;
   private List<? extends Artifact> relocations;
   private Collection<? extends Artifact> aliases;
   private VersionConstraint versionConstraint;
   private Version version;
   private byte managedBits;
   private List<RemoteRepository> repositories;
   private String context;
   private Map<Object, Object> data;

   public DefaultDependencyNode(Dependency var1) {
      this.dependency = var1;
      this.artifact = var1 != null ? var1.getArtifact() : null;
      this.children = new ArrayList<>(0);
      this.aliases = Collections.emptyList();
      this.relocations = Collections.emptyList();
      this.repositories = Collections.emptyList();
      this.context = "";
      this.data = Collections.emptyMap();
   }

   public DefaultDependencyNode(Artifact var1) {
      this.artifact = var1;
      this.children = new ArrayList<>(0);
      this.aliases = Collections.emptyList();
      this.relocations = Collections.emptyList();
      this.repositories = Collections.emptyList();
      this.context = "";
      this.data = Collections.emptyMap();
   }

   public DefaultDependencyNode(DependencyNode var1) {
      this.dependency = var1.getDependency();
      this.artifact = var1.getArtifact();
      this.children = new ArrayList<>(0);
      this.setAliases(var1.getAliases());
      this.setRequestContext(var1.getRequestContext());
      this.setManagedBits(var1.getManagedBits());
      this.setRelocations(var1.getRelocations());
      this.setRepositories(var1.getRepositories());
      this.setVersion(var1.getVersion());
      this.setVersionConstraint(var1.getVersionConstraint());
      Map var2 = var1.getData();
      this.setData(var2.isEmpty() ? null : new HashMap<>(var2));
   }

   @Override
   public List<DependencyNode> getChildren() {
      return this.children;
   }

   @Override
   public void setChildren(List<DependencyNode> var1) {
      if (var1 == null) {
         this.children = new ArrayList<>(0);
      } else {
         this.children = var1;
      }
   }

   @Override
   public Dependency getDependency() {
      return this.dependency;
   }

   @Override
   public Artifact getArtifact() {
      return this.artifact;
   }

   @Override
   public void setArtifact(Artifact var1) {
      if (this.dependency == null) {
         throw new IllegalStateException("node does not have a dependency");
      } else {
         this.dependency = this.dependency.setArtifact(var1);
         this.artifact = this.dependency.getArtifact();
      }
   }

   @Override
   public List<? extends Artifact> getRelocations() {
      return this.relocations;
   }

   public void setRelocations(List<? extends Artifact> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.relocations = var1;
      } else {
         this.relocations = Collections.emptyList();
      }
   }

   @Override
   public Collection<? extends Artifact> getAliases() {
      return this.aliases;
   }

   public void setAliases(Collection<? extends Artifact> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.aliases = var1;
      } else {
         this.aliases = Collections.emptyList();
      }
   }

   @Override
   public VersionConstraint getVersionConstraint() {
      return this.versionConstraint;
   }

   public void setVersionConstraint(VersionConstraint var1) {
      this.versionConstraint = var1;
   }

   @Override
   public Version getVersion() {
      return this.version;
   }

   public void setVersion(Version var1) {
      this.version = var1;
   }

   @Override
   public void setScope(String var1) {
      if (this.dependency == null) {
         throw new IllegalStateException("node does not have a dependency");
      } else {
         this.dependency = this.dependency.setScope(var1);
      }
   }

   @Override
   public void setOptional(Boolean var1) {
      if (this.dependency == null) {
         throw new IllegalStateException("node does not have a dependency");
      } else {
         this.dependency = this.dependency.setOptional(var1);
      }
   }

   @Override
   public int getManagedBits() {
      return this.managedBits;
   }

   public void setManagedBits(int var1) {
      this.managedBits = (byte)(var1 & 31);
   }

   @Override
   public List<RemoteRepository> getRepositories() {
      return this.repositories;
   }

   public void setRepositories(List<RemoteRepository> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.repositories = var1;
      } else {
         this.repositories = Collections.emptyList();
      }
   }

   @Override
   public String getRequestContext() {
      return this.context;
   }

   @Override
   public void setRequestContext(String var1) {
      this.context = var1 != null ? var1 : "";
   }

   @Override
   public Map<Object, Object> getData() {
      return this.data;
   }

   @Override
   public void setData(Map<Object, Object> var1) {
      if (var1 == null) {
         this.data = Collections.emptyMap();
      } else {
         this.data = var1;
      }
   }

   @Override
   public void setData(Object var1, Object var2) {
      Objects.requireNonNull(var1, "key cannot be null");
      if (var2 == null) {
         if (!this.data.isEmpty()) {
            this.data.remove(var1);
            if (this.data.isEmpty()) {
               this.data = Collections.emptyMap();
            }
         }
      } else {
         if (this.data.isEmpty()) {
            this.data = new HashMap<>(1, 2.0F);
         }

         this.data.put(var1, var2);
      }
   }

   @Override
   public boolean accept(DependencyVisitor var1) {
      if (var1.visitEnter(this)) {
         for (DependencyNode var3 : this.children) {
            if (!var3.accept(var1)) {
               break;
            }
         }
      }

      return var1.visitLeave(this);
   }

   @Override
   public String toString() {
      Dependency var1 = this.getDependency();
      return var1 == null ? String.valueOf(this.getArtifact()) : var1.toString();
   }
}
