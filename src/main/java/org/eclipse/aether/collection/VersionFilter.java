package org.eclipse.aether.collection;

import java.util.Iterator;
import java.util.List;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public interface VersionFilter {
   void filterVersions(VersionFilter.VersionFilterContext var1) throws RepositoryException;

   VersionFilter deriveChildFilter(DependencyCollectionContext var1);

   public interface VersionFilterContext extends Iterable<Version> {
      RepositorySystemSession getSession();

      Dependency getDependency();

      int getCount();

      @Override
      Iterator<Version> iterator();

      VersionConstraint getVersionConstraint();

      ArtifactRepository getRepository(Version var1);

      List<RemoteRepository> getRepositories();
   }
}
