package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public final class DefaultRepositoryLayoutProvider implements RepositoryLayoutProvider, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryLayoutProvider.class);
   private Collection<RepositoryLayoutFactory> factories = new ArrayList<>();

   public DefaultRepositoryLayoutProvider() {
   }

   @Inject
   DefaultRepositoryLayoutProvider(Set<RepositoryLayoutFactory> var1) {
      this.setRepositoryLayoutFactories(var1);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setRepositoryLayoutFactories(var1.getServices(RepositoryLayoutFactory.class));
   }

   public DefaultRepositoryLayoutProvider addRepositoryLayoutFactory(RepositoryLayoutFactory var1) {
      this.factories.add(Objects.requireNonNull(var1, "layout factory cannot be null"));
      return this;
   }

   public DefaultRepositoryLayoutProvider setRepositoryLayoutFactories(Collection<RepositoryLayoutFactory> var1) {
      if (var1 == null) {
         this.factories = new ArrayList<>();
      } else {
         this.factories = var1;
      }

      return this;
   }

   @Override
   public RepositoryLayout newRepositoryLayout(RepositorySystemSession var1, RemoteRepository var2) throws NoRepositoryLayoutException {
      Objects.requireNonNull(var2, "remote repository cannot be null");
      PrioritizedComponents var3 = new PrioritizedComponents(var1);

      for (RepositoryLayoutFactory var5 : this.factories) {
         var3.add(var5, var5.getPriority());
      }

      ArrayList var9 = new ArrayList();

      for (PrioritizedComponent var6 : var3.getEnabled()) {
         try {
            return ((RepositoryLayoutFactory)var6.getComponent()).newInstance(var1, var2);
         } catch (NoRepositoryLayoutException var8) {
            var9.add(var8);
         }
      }

      if (LOGGER.isDebugEnabled() && var9.size() > 1) {
         for (Exception var13 : var9) {
            LOGGER.debug("Could not obtain layout factory for {}", var2, var13);
         }
      }

      StringBuilder var12 = new StringBuilder(256);
      if (var3.isEmpty()) {
         var12.append("No layout factories registered");
      } else {
         var12.append("Cannot access ").append(var2.getUrl());
         var12.append(" with type ").append(var2.getContentType());
         var12.append(" using the available layout factories: ");
         var3.list(var12);
      }

      throw new NoRepositoryLayoutException(var2, var12.toString(), var9.size() == 1 ? (NoRepositoryLayoutException)var9.get(0) : null);
   }
}
