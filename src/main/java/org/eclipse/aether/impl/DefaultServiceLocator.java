package org.eclipse.aether.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.DefaultArtifactResolver;
import org.eclipse.aether.internal.impl.DefaultChecksumPolicyProvider;
import org.eclipse.aether.internal.impl.DefaultDeployer;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.internal.impl.DefaultInstaller;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultMetadataResolver;
import org.eclipse.aether.internal.impl.DefaultOfflineController;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositoryConnectorProvider;
import org.eclipse.aether.internal.impl.DefaultRepositoryEventDispatcher;
import org.eclipse.aether.internal.impl.DefaultRepositoryLayoutProvider;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultSyncContextFactory;
import org.eclipse.aether.internal.impl.DefaultTransporterProvider;
import org.eclipse.aether.internal.impl.DefaultUpdateCheckManager;
import org.eclipse.aether.internal.impl.DefaultUpdatePolicyAnalyzer;
import org.eclipse.aether.internal.impl.EnhancedLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.Maven2RepositoryLayoutFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.collect.DefaultDependencyCollector;
import org.eclipse.aether.internal.impl.slf4j.Slf4jLoggerFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;

public final class DefaultServiceLocator implements ServiceLocator {
   private final Map<Class<?>, DefaultServiceLocator.Entry<?>> entries = new HashMap<>();
   private DefaultServiceLocator.ErrorHandler errorHandler;

   public DefaultServiceLocator() {
      this.addService(RepositorySystem.class, DefaultRepositorySystem.class);
      this.addService(ArtifactResolver.class, DefaultArtifactResolver.class);
      this.addService(DependencyCollector.class, DefaultDependencyCollector.class);
      this.addService(Deployer.class, DefaultDeployer.class);
      this.addService(Installer.class, DefaultInstaller.class);
      this.addService(MetadataResolver.class, DefaultMetadataResolver.class);
      this.addService(RepositoryLayoutProvider.class, DefaultRepositoryLayoutProvider.class);
      this.addService(RepositoryLayoutFactory.class, Maven2RepositoryLayoutFactory.class);
      this.addService(TransporterProvider.class, DefaultTransporterProvider.class);
      this.addService(ChecksumPolicyProvider.class, DefaultChecksumPolicyProvider.class);
      this.addService(RepositoryConnectorProvider.class, DefaultRepositoryConnectorProvider.class);
      this.addService(RemoteRepositoryManager.class, DefaultRemoteRepositoryManager.class);
      this.addService(UpdateCheckManager.class, DefaultUpdateCheckManager.class);
      this.addService(UpdatePolicyAnalyzer.class, DefaultUpdatePolicyAnalyzer.class);
      this.addService(FileProcessor.class, DefaultFileProcessor.class);
      this.addService(SyncContextFactory.class, DefaultSyncContextFactory.class);
      this.addService(RepositoryEventDispatcher.class, DefaultRepositoryEventDispatcher.class);
      this.addService(OfflineController.class, DefaultOfflineController.class);
      this.addService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
      this.addService(LocalRepositoryManagerFactory.class, SimpleLocalRepositoryManagerFactory.class);
      this.addService(LocalRepositoryManagerFactory.class, EnhancedLocalRepositoryManagerFactory.class);
      this.addService(LoggerFactory.class, Slf4jLoggerFactory.class);
   }

   private <T> DefaultServiceLocator.Entry<T> getEntry(Class<T> var1, boolean var2) {
      DefaultServiceLocator.Entry var3 = this.entries.get(Objects.requireNonNull(var1, "service type cannot be null"));
      if (var3 == null && var2) {
         var3 = new DefaultServiceLocator.Entry(var1);
         this.entries.put(var1, var3);
      }

      return var3;
   }

   public <T> DefaultServiceLocator setService(Class<T> var1, Class<? extends T> var2) {
      this.getEntry(var1, true).setService(var2);
      return this;
   }

   public <T> DefaultServiceLocator addService(Class<T> var1, Class<? extends T> var2) {
      this.getEntry(var1, true).addService(var2);
      return this;
   }

   public <T> DefaultServiceLocator setServices(Class<T> var1, T... var2) {
      this.getEntry(var1, true).setServices((T[])var2);
      return this;
   }

   @Override
   public <T> T getService(Class<T> var1) {
      DefaultServiceLocator.Entry var2 = this.getEntry(var1, false);
      return (T)(var2 != null ? var2.getInstance() : null);
   }

   @Override
   public <T> List<T> getServices(Class<T> var1) {
      DefaultServiceLocator.Entry var2 = this.getEntry(var1, false);
      return var2 != null ? var2.getInstances() : null;
   }

   private void serviceCreationFailed(Class<?> var1, Class<?> var2, Throwable var3) {
      if (this.errorHandler != null) {
         this.errorHandler.serviceCreationFailed(var1, var2, var3);
      }
   }

   public void setErrorHandler(DefaultServiceLocator.ErrorHandler var1) {
      this.errorHandler = var1;
   }

   private class Entry<T> {
      private final Class<T> type;
      private final Collection<Object> providers;
      private List<T> instances;

      Entry(Class<T> var2) {
         this.type = Objects.requireNonNull(var2, "service type cannot be null");
         this.providers = new LinkedHashSet<>(8);
      }

      public synchronized void setServices(T... var1) {
         this.providers.clear();
         if (var1 != null) {
            for (Object var5 : var1) {
               this.providers.add(Objects.requireNonNull(var5, "service instance cannot be null"));
            }
         }

         this.instances = null;
      }

      public synchronized void setService(Class<? extends T> var1) {
         this.providers.clear();
         this.addService(var1);
      }

      public synchronized void addService(Class<? extends T> var1) {
         this.providers.add(Objects.requireNonNull(var1, "implementation class cannot be null"));
         this.instances = null;
      }

      public T getInstance() {
         List var1 = this.getInstances();
         return (T)(var1.isEmpty() ? null : var1.get(0));
      }

      public synchronized List<T> getInstances() {
         if (this.instances == null) {
            this.instances = new ArrayList<>(this.providers.size());

            for (Object var2 : this.providers) {
               Object var3;
               if (var2 instanceof Class) {
                  var3 = this.newInstance((Class<?>)var2);
               } else {
                  var3 = this.type.cast(var2);
               }

               if (var3 != null) {
                  this.instances.add((T)var3);
               }
            }

            this.instances = Collections.unmodifiableList(this.instances);
         }

         return this.instances;
      }

      private T newInstance(Class<?> var1) {
         try {
            Constructor var2 = var1.getDeclaredConstructor();
            if (!Modifier.isPublic(var2.getModifiers())) {
               var2.setAccessible(true);
            }

            Object var3 = var2.newInstance();
            Object var4 = this.type.cast(var3);
            if (var4 instanceof Service) {
               ((Service)var4).initService(DefaultServiceLocator.this);
            }

            return (T)var4;
         } catch (LinkageError | Exception var5) {
            DefaultServiceLocator.this.serviceCreationFailed(this.type, var1, var5);
            return null;
         }
      }
   }

   public abstract static class ErrorHandler {
      public void serviceCreationFailed(Class<?> var1, Class<?> var2, Throwable var3) {
      }
   }
}
