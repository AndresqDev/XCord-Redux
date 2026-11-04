package net.md_5.bungee.api.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

class LibraryLoader {
   private final Logger logger;
   private final RepositorySystem repository;
   private final DefaultRepositorySystemSession session;
   private final List<RemoteRepository> repositories;

   public LibraryLoader(final Logger var1) {
      this.logger = var1;
      DefaultServiceLocator var2 = MavenRepositorySystemUtils.newServiceLocator();
      var2.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
      var2.addService(TransporterFactory.class, HttpTransporterFactory.class);
      this.repository = (RepositorySystem)var2.getService(RepositorySystem.class);
      this.session = MavenRepositorySystemUtils.newSession();
      this.session.setChecksumPolicy("fail");
      this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository("libraries")));
      this.session.setTransferListener(new AbstractTransferListener() {
         public void transferStarted(TransferEvent var1x) throws TransferCancelledException {
            var1.log(Level.INFO, "Downloading {0}", var1x.getResource().getRepositoryUrl() + var1x.getResource().getResourceName());
         }
      });
      this.session.setReadOnly();
      this.repositories = this.repository
         .newResolutionRepositories(this.session, Arrays.asList(new Builder("central", "default", "https://repo.maven.apache.org/maven2").build()));
   }

   public ClassLoader createLoader(PluginDescription var1) {
      if (var1.getLibraries().isEmpty()) {
         return null;
      } else {
         this.logger.log(Level.INFO, "[{0}] Loading {1} libraries... please wait", new Object[]{var1.getName(), var1.getLibraries().size()});
         ArrayList var2 = new ArrayList();

         for (String var4 : var1.getLibraries()) {
            DefaultArtifact var5 = new DefaultArtifact(var4);
            Dependency var6 = new Dependency(var5, null);
            var2.add(var6);
         }

         DependencyResult var12;
         try {
            var12 = this.repository
               .resolveDependencies(this.session, new DependencyRequest(new CollectRequest((Dependency)null, var2, this.repositories), null));
         } catch (DependencyResolutionException var11) {
            throw new RuntimeException("Error resolving libraries", var11);
         }

         ArrayList var13 = new ArrayList();

         for (ArtifactResult var15 : var12.getArtifactResults()) {
            File var7 = var15.getArtifact().getFile();

            URL var8;
            try {
               var8 = var7.toURI().toURL();
            } catch (MalformedURLException var10) {
               throw new AssertionError(var10);
            }

            var13.add(var8);
            this.logger.log(Level.INFO, "[{0}] Loaded library {1}", new Object[]{var1.getName(), var7});
         }

         return new URLClassLoader(var13.toArray(new URL[0]));
      }
   }
}
