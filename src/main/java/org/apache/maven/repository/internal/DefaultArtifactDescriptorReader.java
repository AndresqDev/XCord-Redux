package org.apache.maven.repository.internal;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicyRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultArtifactDescriptorReader implements ArtifactDescriptorReader, Service {
   private RemoteRepositoryManager remoteRepositoryManager;
   private VersionResolver versionResolver;
   private VersionRangeResolver versionRangeResolver;
   private ArtifactResolver artifactResolver;
   private RepositoryEventDispatcher repositoryEventDispatcher;
   private ModelBuilder modelBuilder;

   public DefaultArtifactDescriptorReader() {
   }

   @Inject
   DefaultArtifactDescriptorReader(
      RemoteRepositoryManager var1, VersionResolver var2, VersionRangeResolver var3, ArtifactResolver var4, ModelBuilder var5, RepositoryEventDispatcher var6
   ) {
      this.setRemoteRepositoryManager(var1);
      this.setVersionResolver(var2);
      this.setVersionRangeResolver(var3);
      this.setArtifactResolver(var4);
      this.setModelBuilder(var5);
      this.setRepositoryEventDispatcher(var6);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setRemoteRepositoryManager(var1.getService(RemoteRepositoryManager.class));
      this.setVersionResolver(var1.getService(VersionResolver.class));
      this.setVersionRangeResolver(var1.getService(VersionRangeResolver.class));
      this.setArtifactResolver(var1.getService(ArtifactResolver.class));
      this.modelBuilder = var1.getService(ModelBuilder.class);
      if (this.modelBuilder == null) {
         this.setModelBuilder(new DefaultModelBuilderFactory().newInstance());
      }

      this.setRepositoryEventDispatcher(var1.getService(RepositoryEventDispatcher.class));
   }

   public DefaultArtifactDescriptorReader setRemoteRepositoryManager(RemoteRepositoryManager var1) {
      this.remoteRepositoryManager = Objects.requireNonNull(var1, "remoteRepositoryManager cannot be null");
      return this;
   }

   public DefaultArtifactDescriptorReader setVersionResolver(VersionResolver var1) {
      this.versionResolver = Objects.requireNonNull(var1, "versionResolver cannot be null");
      return this;
   }

   public DefaultArtifactDescriptorReader setVersionRangeResolver(VersionRangeResolver var1) {
      this.versionRangeResolver = Objects.requireNonNull(var1, "versionRangeResolver cannot be null");
      return this;
   }

   public DefaultArtifactDescriptorReader setArtifactResolver(ArtifactResolver var1) {
      this.artifactResolver = Objects.requireNonNull(var1, "artifactResolver cannot be null");
      return this;
   }

   public DefaultArtifactDescriptorReader setRepositoryEventDispatcher(RepositoryEventDispatcher var1) {
      this.repositoryEventDispatcher = Objects.requireNonNull(var1, "repositoryEventDispatcher cannot be null");
      return this;
   }

   public DefaultArtifactDescriptorReader setModelBuilder(ModelBuilder var1) {
      this.modelBuilder = Objects.requireNonNull(var1, "modelBuilder cannot be null");
      return this;
   }

   @Override
   public ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession var1, ArtifactDescriptorRequest var2) throws ArtifactDescriptorException {
      ArtifactDescriptorResult var3 = new ArtifactDescriptorResult(var2);
      Model var4 = this.loadPom(var1, var2, var3);
      if (var4 != null) {
         Map var5 = var1.getConfigProperties();
         ArtifactDescriptorReaderDelegate var6 = (ArtifactDescriptorReaderDelegate)var5.get(ArtifactDescriptorReaderDelegate.class.getName());
         if (var6 == null) {
            var6 = new ArtifactDescriptorReaderDelegate();
         }

         var6.populateResult(var1, var3, var4);
      }

      return var3;
   }

   private Model loadPom(RepositorySystemSession var1, ArtifactDescriptorRequest var2, ArtifactDescriptorResult var3) throws ArtifactDescriptorException {
      RequestTrace var4 = RequestTrace.newChild(var2.getTrace(), var2);
      LinkedHashSet var5 = new LinkedHashSet();
      Artifact var6 = var2.getArtifact();

      while (true) {
         Artifact var7 = ArtifactDescriptorUtils.toPomArtifact(var6);

         try {
            VersionRequest var8 = new VersionRequest(var6, var2.getRepositories(), var2.getRequestContext());
            var8.setTrace(var4);
            VersionResult var9 = this.versionResolver.resolveVersion(var1, var8);
            var6 = var6.setVersion(var9.getVersion());
            var8 = new VersionRequest(var7, var2.getRepositories(), var2.getRequestContext());
            var8.setTrace(var4);
            var9 = this.versionResolver.resolveVersion(var1, var8);
            var7 = var7.setVersion(var9.getVersion());
         } catch (VersionResolutionException var14) {
            var3.addException(var14);
            throw new ArtifactDescriptorException(var3);
         }

         if (!var5.add(var6.getGroupId() + ':' + var6.getArtifactId() + ':' + var6.getBaseVersion())) {
            RepositoryException var22 = new RepositoryException("Artifact relocations form a cycle: " + var5);
            this.invalidDescriptor(var1, var4, var6, var22);
            if ((this.getPolicy(var1, var6, var2) & 2) != 0) {
               return null;
            }

            var3.addException(var22);
            throw new ArtifactDescriptorException(var3);
         }

         ArtifactResult var21;
         try {
            ArtifactRequest var24 = new ArtifactRequest(var7, var2.getRepositories(), var2.getRequestContext());
            var24.setTrace(var4);
            var21 = this.artifactResolver.resolveArtifact(var1, var24);
            var7 = var21.getArtifact();
            var3.setRepository(var21.getRepository());
         } catch (ArtifactResolutionException var16) {
            if (var16.getCause() instanceof ArtifactNotFoundException) {
               this.missingDescriptor(var1, var4, var6, (Exception)var16.getCause());
               if ((this.getPolicy(var1, var6, var2) & 1) != 0) {
                  return null;
               }
            }

            var3.addException(var16);
            throw new ArtifactDescriptorException(var3);
         }

         WorkspaceReader var10 = var1.getWorkspaceReader();
         if (var10 instanceof MavenWorkspaceReader) {
            Model var25 = ((MavenWorkspaceReader)var10).findModel(var7);
            if (var25 != null) {
               return var25;
            }
         }

         Model var26;
         try {
            DefaultModelBuildingRequest var11 = new DefaultModelBuildingRequest();
            var11.setValidationLevel(0);
            var11.setProcessPlugins(false);
            var11.setTwoPhaseBuilding(false);
            var11.setSystemProperties(this.toProperties(var1.getSystemProperties()));
            var11.setUserProperties(this.toProperties(var1.getUserProperties()));
            var11.setModelCache(DefaultModelCache.newInstance(var1));
            var11.setModelResolver(
               new DefaultModelResolver(
                  var1,
                  var4.newChild(var11),
                  var2.getRequestContext(),
                  this.artifactResolver,
                  this.versionRangeResolver,
                  this.remoteRepositoryManager,
                  var2.getRepositories()
               )
            );
            if (var21.getRepository() instanceof WorkspaceRepository) {
               var11.setPomFile(var7.getFile());
            } else {
               var11.setModelSource(new FileModelSource(var7.getFile()));
            }

            var26 = this.modelBuilder.build(var11).getEffectiveModel();
         } catch (ModelBuildingException var15) {
            for (ModelProblem var13 : var15.getProblems()) {
               if (var13.getException() instanceof UnresolvableModelException) {
                  var3.addException(var13.getException());
                  throw new ArtifactDescriptorException(var3);
               }
            }

            this.invalidDescriptor(var1, var4, var6, var15);
            if ((this.getPolicy(var1, var6, var2) & 2) != 0) {
               return null;
            }

            var3.addException(var15);
            throw new ArtifactDescriptorException(var3);
         }

         Relocation var27 = this.getRelocation(var26);
         if (var27 == null) {
            return var26;
         }

         var3.addRelocation(var6);
         var6 = new RelocatedArtifact(var6, var27.getGroupId(), var27.getArtifactId(), var27.getVersion(), var27.getMessage());
         var3.setArtifact(var6);
      }
   }

   private Properties toProperties(Map<String, String> var1) {
      Properties var2 = new Properties();
      var2.putAll(var1);
      return var2;
   }

   private Relocation getRelocation(Model var1) {
      Relocation var2 = null;
      DistributionManagement var3 = var1.getDistributionManagement();
      if (var3 != null) {
         var2 = var3.getRelocation();
      }

      return var2;
   }

   private void missingDescriptor(RepositorySystemSession var1, RequestTrace var2, Artifact var3, Exception var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.ARTIFACT_DESCRIPTOR_MISSING);
      var5.setTrace(var2);
      var5.setArtifact(var3);
      var5.setException(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private void invalidDescriptor(RepositorySystemSession var1, RequestTrace var2, Artifact var3, Exception var4) {
      RepositoryEvent.Builder var5 = new RepositoryEvent.Builder(var1, RepositoryEvent.EventType.ARTIFACT_DESCRIPTOR_INVALID);
      var5.setTrace(var2);
      var5.setArtifact(var3);
      var5.setException(var4);
      this.repositoryEventDispatcher.dispatch(var5.build());
   }

   private int getPolicy(RepositorySystemSession var1, Artifact var2, ArtifactDescriptorRequest var3) {
      ArtifactDescriptorPolicy var4 = var1.getArtifactDescriptorPolicy();
      return var4 == null ? 0 : var4.getPolicy(var1, new ArtifactDescriptorPolicyRequest(var2, var3.getRequestContext()));
   }
}
