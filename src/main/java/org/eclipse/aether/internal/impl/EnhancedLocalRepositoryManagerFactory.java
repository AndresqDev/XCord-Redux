package org.eclipse.aether.internal.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import xcord.Named;

@Named("enhanced")
public class EnhancedLocalRepositoryManagerFactory implements LocalRepositoryManagerFactory {
   private float priority = 10.0F;

   @Override
   public LocalRepositoryManager newInstance(RepositorySystemSession var1, LocalRepository var2) throws NoLocalRepositoryManagerException {
      if (!"".equals(var2.getContentType()) && !"default".equals(var2.getContentType())) {
         throw new NoLocalRepositoryManagerException(var2);
      } else {
         return new EnhancedLocalRepositoryManager(var2.getBasedir(), var1);
      }
   }

   @Override
   public float getPriority() {
      return this.priority;
   }

   public EnhancedLocalRepositoryManagerFactory setPriority(float var1) {
      this.priority = var1;
      return this;
   }
}
