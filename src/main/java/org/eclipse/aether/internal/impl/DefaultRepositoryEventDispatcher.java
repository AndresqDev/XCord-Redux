package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultRepositoryEventDispatcher implements RepositoryEventDispatcher, Service {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryEventDispatcher.class);
   private Collection<RepositoryListener> listeners = new ArrayList<>();

   public DefaultRepositoryEventDispatcher() {
   }

   @Inject
   DefaultRepositoryEventDispatcher(Set<RepositoryListener> var1) {
      this.setRepositoryListeners(var1);
   }

   public DefaultRepositoryEventDispatcher addRepositoryListener(RepositoryListener var1) {
      this.listeners.add(Objects.requireNonNull(var1, "repository listener cannot be null"));
      return this;
   }

   public DefaultRepositoryEventDispatcher setRepositoryListeners(Collection<RepositoryListener> var1) {
      if (var1 == null) {
         this.listeners = new ArrayList<>();
      } else {
         this.listeners = var1;
      }

      return this;
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setRepositoryListeners(var1.getServices(RepositoryListener.class));
   }

   @Override
   public void dispatch(RepositoryEvent var1) {
      if (!this.listeners.isEmpty()) {
         for (RepositoryListener var3 : this.listeners) {
            this.dispatch(var1, var3);
         }
      }

      RepositoryListener var4 = var1.getSession().getRepositoryListener();
      if (var4 != null) {
         this.dispatch(var1, var4);
      }
   }

   private void dispatch(RepositoryEvent var1, RepositoryListener var2) {
      try {
         switch (var1.getType()) {
            case ARTIFACT_DEPLOYED:
               var2.artifactDeployed(var1);
               break;
            case ARTIFACT_DEPLOYING:
               var2.artifactDeploying(var1);
               break;
            case ARTIFACT_DESCRIPTOR_INVALID:
               var2.artifactDescriptorInvalid(var1);
               break;
            case ARTIFACT_DESCRIPTOR_MISSING:
               var2.artifactDescriptorMissing(var1);
               break;
            case ARTIFACT_DOWNLOADED:
               var2.artifactDownloaded(var1);
               break;
            case ARTIFACT_DOWNLOADING:
               var2.artifactDownloading(var1);
               break;
            case ARTIFACT_INSTALLED:
               var2.artifactInstalled(var1);
               break;
            case ARTIFACT_INSTALLING:
               var2.artifactInstalling(var1);
               break;
            case ARTIFACT_RESOLVED:
               var2.artifactResolved(var1);
               break;
            case ARTIFACT_RESOLVING:
               var2.artifactResolving(var1);
               break;
            case METADATA_DEPLOYED:
               var2.metadataDeployed(var1);
               break;
            case METADATA_DEPLOYING:
               var2.metadataDeploying(var1);
               break;
            case METADATA_DOWNLOADED:
               var2.metadataDownloaded(var1);
               break;
            case METADATA_DOWNLOADING:
               var2.metadataDownloading(var1);
               break;
            case METADATA_INSTALLED:
               var2.metadataInstalled(var1);
               break;
            case METADATA_INSTALLING:
               var2.metadataInstalling(var1);
               break;
            case METADATA_INVALID:
               var2.metadataInvalid(var1);
               break;
            case METADATA_RESOLVED:
               var2.metadataResolved(var1);
               break;
            case METADATA_RESOLVING:
               var2.metadataResolving(var1);
               break;
            default:
               throw new IllegalStateException("unknown repository event type " + var1.getType());
         }
      } catch (LinkageError | Exception var4) {
         this.logError(var4, var2);
      }
   }

   private void logError(Throwable var1, Object var2) {
      LOGGER.warn("Failed to dispatch repository event to {}", var2.getClass().getCanonicalName(), var1);
   }
}
