package org.eclipse.aether.util.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;

public final class ChainedRepositoryListener extends AbstractRepositoryListener {
   private final List<RepositoryListener> listeners = new CopyOnWriteArrayList<>();

   public static RepositoryListener newInstance(RepositoryListener var0, RepositoryListener var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (RepositoryListener)(var1 == null ? var0 : new ChainedRepositoryListener(var0, var1));
      }
   }

   public ChainedRepositoryListener(RepositoryListener... var1) {
      if (var1 != null) {
         this.add(Arrays.asList(var1));
      }
   }

   public ChainedRepositoryListener(Collection<? extends RepositoryListener> var1) {
      this.add(var1);
   }

   public void add(Collection<? extends RepositoryListener> var1) {
      if (var1 != null) {
         for (RepositoryListener var3 : var1) {
            this.add(var3);
         }
      }
   }

   public void add(RepositoryListener var1) {
      if (var1 != null) {
         this.listeners.add(var1);
      }
   }

   public void remove(RepositoryListener var1) {
      if (var1 != null) {
         this.listeners.remove(var1);
      }
   }

   protected void handleError(RepositoryEvent var1, RepositoryListener var2, RuntimeException var3) {
   }

   @Override
   public void artifactDeployed(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDeployed(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactDeploying(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDeploying(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactDescriptorInvalid(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDescriptorInvalid(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactDescriptorMissing(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDescriptorMissing(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactDownloaded(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDownloaded(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactDownloading(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactDownloading(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactInstalled(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactInstalled(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactInstalling(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactInstalling(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactResolved(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactResolved(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void artifactResolving(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.artifactResolving(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataDeployed(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataDeployed(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataDeploying(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataDeploying(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataDownloaded(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataDownloaded(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataDownloading(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataDownloading(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataInstalled(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataInstalled(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataInstalling(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataInstalling(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataInvalid(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataInvalid(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataResolved(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataResolved(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }

   @Override
   public void metadataResolving(RepositoryEvent var1) {
      for (RepositoryListener var3 : this.listeners) {
         try {
            var3.metadataResolving(var1);
         } catch (RuntimeException var5) {
            this.handleError(var1, var3, var5);
         }
      }
   }
}
