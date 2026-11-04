package org.eclipse.aether.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public interface DependencyNode {
   int MANAGED_VERSION = 1;
   int MANAGED_SCOPE = 2;
   int MANAGED_OPTIONAL = 4;
   int MANAGED_PROPERTIES = 8;
   int MANAGED_EXCLUSIONS = 16;

   List<DependencyNode> getChildren();

   void setChildren(List<DependencyNode> var1);

   Dependency getDependency();

   Artifact getArtifact();

   void setArtifact(Artifact var1);

   List<? extends Artifact> getRelocations();

   Collection<? extends Artifact> getAliases();

   VersionConstraint getVersionConstraint();

   Version getVersion();

   void setScope(String var1);

   void setOptional(Boolean var1);

   int getManagedBits();

   List<RemoteRepository> getRepositories();

   String getRequestContext();

   void setRequestContext(String var1);

   Map<?, ?> getData();

   void setData(Map<Object, Object> var1);

   void setData(Object var1, Object var2);

   boolean accept(DependencyVisitor var1);
}
