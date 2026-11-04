package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultRepositoryConnectorProvider implements RepositoryConnectorProvider, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryConnectorProvider.class);
   private Collection<RepositoryConnectorFactory> connectorFactories = new ArrayList<>();

   public DefaultRepositoryConnectorProvider() {
   }

   @Inject
   DefaultRepositoryConnectorProvider(Set<RepositoryConnectorFactory> var1) {
      this.setRepositoryConnectorFactories(var1);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.connectorFactories = var1.getServices(RepositoryConnectorFactory.class);
   }

   public DefaultRepositoryConnectorProvider addRepositoryConnectorFactory(RepositoryConnectorFactory var1) {
      this.connectorFactories.add(Objects.requireNonNull(var1, "repository connector factory cannot be null"));
      return this;
   }

   public DefaultRepositoryConnectorProvider setRepositoryConnectorFactories(Collection<RepositoryConnectorFactory> var1) {
      if (var1 == null) {
         this.connectorFactories = new ArrayList<>();
      } else {
         this.connectorFactories = var1;
      }

      return this;
   }

   @Override
   public RepositoryConnector newRepositoryConnector(RepositorySystemSession var1, RemoteRepository var2) throws NoRepositoryConnectorException {
      Objects.requireNonNull(var2, "remote repository cannot be null");
      PrioritizedComponents var3 = new PrioritizedComponents(var1);

      for (RepositoryConnectorFactory var5 : this.connectorFactories) {
         var3.add(var5, var5.getPriority());
      }

      ArrayList var12 = new ArrayList();

      for (PrioritizedComponent var6 : var3.getEnabled()) {
         try {
            RepositoryConnector var7 = ((RepositoryConnectorFactory)var6.getComponent()).newInstance(var1, var2);
            if (LOGGER.isDebugEnabled()) {
               StringBuilder var8 = new StringBuilder(256);
               var8.append("Using connector ").append(var7.getClass().getSimpleName());
               Utils.appendClassLoader(var8, var7);
               var8.append(" with priority ").append(var6.getPriority());
               var8.append(" for ").append(var2.getUrl());
               Authentication var9 = var2.getAuthentication();
               if (var9 != null) {
                  var8.append(" with ").append(var9);
               }

               Proxy var10 = var2.getProxy();
               if (var10 != null) {
                  var8.append(" via ").append(var10.getHost()).append(':').append(var10.getPort());
                  var9 = var10.getAuthentication();
                  if (var9 != null) {
                     var8.append(" with ").append(var9);
                  }
               }

               LOGGER.debug(var8.toString());
            }

            return var7;
         } catch (NoRepositoryConnectorException var11) {
            var12.add(var11);
         }
      }

      if (LOGGER.isDebugEnabled() && var12.size() > 1) {
         for (Exception var16 : var12) {
            LOGGER.debug("Could not obtain connector factory for {}", var2, var16);
         }
      }

      StringBuilder var15 = new StringBuilder(256);
      if (var3.isEmpty()) {
         var15.append("No connector factories available");
      } else {
         var15.append("Cannot access ").append(var2.getUrl());
         var15.append(" with type ").append(var2.getContentType());
         var15.append(" using the available connector factories: ");
         var3.list(var15);
      }

      throw new NoRepositoryConnectorException(var2, var15.toString(), var12.size() == 1 ? (NoRepositoryConnectorException)var12.get(0) : null);
   }
}
