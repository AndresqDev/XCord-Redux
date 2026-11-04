package org.eclipse.aether.internal.impl;

import java.util.Collection;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.metadata.Metadata;
import xcord.Named;

@Named
public class DefaultSyncContextFactory implements SyncContextFactory {
   @Override
   public SyncContext newInstance(RepositorySystemSession var1, boolean var2) {
      return new DefaultSyncContextFactory.DefaultSyncContext();
   }

   static class DefaultSyncContext implements SyncContext {
      @Override
      public void acquire(Collection<? extends Artifact> var1, Collection<? extends Metadata> var2) {
      }

      @Override
      public void close() {
      }
   }
}
