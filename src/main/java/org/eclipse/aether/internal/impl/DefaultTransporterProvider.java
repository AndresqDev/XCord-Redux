package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoTransporterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public final class DefaultTransporterProvider implements TransporterProvider, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTransporterProvider.class);
   private Collection<TransporterFactory> factories = new ArrayList<>();

   public DefaultTransporterProvider() {
   }

   @Inject
   DefaultTransporterProvider(Set<TransporterFactory> var1) {
      this.setTransporterFactories(var1);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setTransporterFactories(var1.getServices(TransporterFactory.class));
   }

   public DefaultTransporterProvider addTransporterFactory(TransporterFactory var1) {
      this.factories.add(Objects.requireNonNull(var1, "transporter factory cannot be null"));
      return this;
   }

   public DefaultTransporterProvider setTransporterFactories(Collection<TransporterFactory> var1) {
      if (var1 == null) {
         this.factories = new ArrayList<>();
      } else {
         this.factories = var1;
      }

      return this;
   }

   @Override
   public Transporter newTransporter(RepositorySystemSession var1, RemoteRepository var2) throws NoTransporterException {
      Objects.requireNonNull(var2, "remote repository cannot be null");
      PrioritizedComponents var3 = new PrioritizedComponents(var1);

      for (TransporterFactory var5 : this.factories) {
         var3.add(var5, var5.getPriority());
      }

      ArrayList var10 = new ArrayList();

      for (PrioritizedComponent var6 : var3.getEnabled()) {
         try {
            Transporter var7 = ((TransporterFactory)var6.getComponent()).newInstance(var1, var2);
            if (LOGGER.isDebugEnabled()) {
               StringBuilder var8 = new StringBuilder(256);
               var8.append("Using transporter ").append(var7.getClass().getSimpleName());
               Utils.appendClassLoader(var8, var7);
               var8.append(" with priority ").append(var6.getPriority());
               var8.append(" for ").append(var2.getUrl());
               LOGGER.debug(var8.toString());
            }

            return var7;
         } catch (NoTransporterException var9) {
            var10.add(var9);
         }
      }

      if (LOGGER.isDebugEnabled() && var10.size() > 1) {
         for (Exception var14 : var10) {
            LOGGER.debug("Could not obtain transporter factory for {}", var2, var14);
         }
      }

      StringBuilder var13 = new StringBuilder(256);
      if (var3.isEmpty()) {
         var13.append("No transporter factories registered");
      } else {
         var13.append("Cannot access ").append(var2.getUrl());
         var13.append(" using the registered transporter factories: ");
         var3.list(var13);
      }

      throw new NoTransporterException(var2, var13.toString(), var10.size() == 1 ? (NoTransporterException)var10.get(0) : null);
   }
}
