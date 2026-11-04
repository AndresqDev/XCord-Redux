package org.eclipse.aether.impl;

import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

public interface RemoteRepositoryManager {
   List<RemoteRepository> aggregateRepositories(RepositorySystemSession var1, List<RemoteRepository> var2, List<RemoteRepository> var3, boolean var4);

   RepositoryPolicy getPolicy(RepositorySystemSession var1, RemoteRepository var2, boolean var3, boolean var4);
}
