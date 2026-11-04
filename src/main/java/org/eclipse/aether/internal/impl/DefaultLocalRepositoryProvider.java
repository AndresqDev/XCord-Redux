package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.LocalRepositoryProvider;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultLocalRepositoryProvider implements LocalRepositoryProvider, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalRepositoryProvider.class);
   private Collection<LocalRepositoryManagerFactory> managerFactories = new ArrayList<>();

   public DefaultLocalRepositoryProvider() {
   }

   @Inject
   DefaultLocalRepositoryProvider(Set<LocalRepositoryManagerFactory> var1) {
      this.setLocalRepositoryManagerFactories(var1);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setLocalRepositoryManagerFactories(var1.getServices(LocalRepositoryManagerFactory.class));
   }

   public DefaultLocalRepositoryProvider addLocalRepositoryManagerFactory(LocalRepositoryManagerFactory var1) {
      this.managerFactories.add(Objects.requireNonNull(var1, "local repository manager factory cannot be null"));
      return this;
   }

   public DefaultLocalRepositoryProvider setLocalRepositoryManagerFactories(Collection<LocalRepositoryManagerFactory> var1) {
      if (var1 == null) {
         this.managerFactories = new ArrayList<>(2);
      } else {
         this.managerFactories = var1;
      }

      return this;
   }

   @Override
   public LocalRepositoryManager newLocalRepositoryManager(RepositorySystemSession var1, LocalRepository var2) throws NoLocalRepositoryManagerException {
      PrioritizedComponents var3 = new PrioritizedComponents(var1);

      for (LocalRepositoryManagerFactory var5 : this.managerFactories) {
         var3.add(var5, var5.getPriority());
      }

      ArrayList var10 = new ArrayList();

      for (PrioritizedComponent var6 : var3.getEnabled()) {
         try {
            LocalRepositoryManager var7 = ((LocalRepositoryManagerFactory)var6.getComponent()).newInstance(var1, var2);
            if (LOGGER.isDebugEnabled()) {
               StringBuilder var8 = new StringBuilder(256);
               var8.append("Using manager ").append(var7.getClass().getSimpleName());
               Utils.appendClassLoader(var8, var7);
               var8.append(" with priority ").append(var6.getPriority());
               var8.append(" for ").append(var2.getBasedir());
               LOGGER.debug(var8.toString());
            }

            return var7;
         } catch (NoLocalRepositoryManagerException var9) {
            var10.add(var9);
         }
      }

      if (LOGGER.isDebugEnabled() && var10.size() > 1) {
         for (Exception var14 : var10) {
            LOGGER.debug("Could not obtain local repository manager for {}", var2, var14);
         }
      }

      StringBuilder var13 = new StringBuilder(256);
      if (var3.isEmpty()) {
         var13.append("No local repository managers registered");
      } else {
         var13.append("Cannot access ").append(var2.getBasedir());
         var13.append(" with type ").append(var2.getContentType());
         var13.append(" using the available factories ");
         var3.list(var13);
      }

      throw new NoLocalRepositoryManagerException(var2, var13.toString(), var10.size() == 1 ? (NoLocalRepositoryManagerException)var10.get(0) : null);
   }
}
