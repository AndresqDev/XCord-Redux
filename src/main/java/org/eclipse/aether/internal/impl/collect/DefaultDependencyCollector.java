package org.eclipse.aether.internal.impl.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.DependencyCollector;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.graph.transformer.TransformationContextKeys;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Inject;
import xcord.Named;

@Named
public class DefaultDependencyCollector implements DependencyCollector, Service {
   private static final String CONFIG_PROP_MAX_EXCEPTIONS = "aether.dependencyCollector.maxExceptions";
   private static final int CONFIG_PROP_MAX_EXCEPTIONS_DEFAULT = 50;
   private static final String CONFIG_PROP_MAX_CYCLES = "aether.dependencyCollector.maxCycles";
   private static final int CONFIG_PROP_MAX_CYCLES_DEFAULT = 10;
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDependencyCollector.class);
   private RemoteRepositoryManager remoteRepositoryManager;
   private ArtifactDescriptorReader descriptorReader;
   private VersionRangeResolver versionRangeResolver;

   public DefaultDependencyCollector() {
   }

   @Inject
   DefaultDependencyCollector(RemoteRepositoryManager var1, ArtifactDescriptorReader var2, VersionRangeResolver var3) {
      this.setRemoteRepositoryManager(var1);
      this.setArtifactDescriptorReader(var2);
      this.setVersionRangeResolver(var3);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setRemoteRepositoryManager(var1.getService(RemoteRepositoryManager.class));
      this.setArtifactDescriptorReader(var1.getService(ArtifactDescriptorReader.class));
      this.setVersionRangeResolver(var1.getService(VersionRangeResolver.class));
   }

   public DefaultDependencyCollector setRemoteRepositoryManager(RemoteRepositoryManager var1) {
      this.remoteRepositoryManager = Objects.requireNonNull(var1, "remote repository provider cannot be null");
      return this;
   }

   public DefaultDependencyCollector setArtifactDescriptorReader(ArtifactDescriptorReader var1) {
      this.descriptorReader = Objects.requireNonNull(var1, "artifact descriptor reader cannot be null");
      return this;
   }

   public DefaultDependencyCollector setVersionRangeResolver(VersionRangeResolver var1) {
      this.versionRangeResolver = Objects.requireNonNull(var1, "version range resolver cannot be null");
      return this;
   }

   @Override
   public CollectResult collectDependencies(RepositorySystemSession var1, CollectRequest var2) throws DependencyCollectionException {
      var1 = optimizeSession(var1);
      RequestTrace var3 = RequestTrace.newChild(var2.getTrace(), var2);
      CollectResult var4 = new CollectResult(var2);
      DependencySelector var5 = var1.getDependencySelector();
      DependencyManager var6 = var1.getDependencyManager();
      DependencyTraverser var7 = var1.getDependencyTraverser();
      VersionFilter var8 = var1.getVersionFilter();
      Dependency var9 = var2.getRoot();
      List var10 = var2.getRepositories();
      List var11 = var2.getDependencies();
      List var12 = var2.getManagedDependencies();
      LinkedHashMap var13 = new LinkedHashMap();
      long var14 = System.nanoTime();
      DefaultDependencyNode var16;
      if (var9 != null) {
         List var17;
         VersionRangeResult var18;
         try {
            VersionRangeRequest var19 = new VersionRangeRequest(var9.getArtifact(), var2.getRepositories(), var2.getRequestContext());
            var19.setTrace(var3);
            var18 = this.versionRangeResolver.resolveVersionRange(var1, var19);
            var17 = filterVersions(var9, var18, var8, new DefaultVersionFilterContext(var1));
         } catch (VersionRangeResolutionException var27) {
            var4.addException(var27);
            throw new DependencyCollectionException(var4, var27.getMessage());
         }

         Version var32 = (Version)var17.get(var17.size() - 1);
         var9 = var9.setArtifact(var9.getArtifact().setVersion(var32.toString()));

         ArtifactDescriptorResult var20;
         try {
            ArtifactDescriptorRequest var21 = new ArtifactDescriptorRequest();
            var21.setArtifact(var9.getArtifact());
            var21.setRepositories(var2.getRepositories());
            var21.setRequestContext(var2.getRequestContext());
            var21.setTrace(var3);
            if (isLackingDescriptor(var9.getArtifact())) {
               var20 = new ArtifactDescriptorResult(var21);
            } else {
               var20 = this.descriptorReader.readArtifactDescriptor(var1, var21);
            }
         } catch (ArtifactDescriptorException var26) {
            var4.addException(var26);
            throw new DependencyCollectionException(var4, var26.getMessage());
         }

         var9 = var9.setArtifact(var20.getArtifact());
         if (!var1.isIgnoreArtifactDescriptorRepositories()) {
            var10 = this.remoteRepositoryManager.aggregateRepositories(var1, var10, var20.getRepositories(), true);
         }

         var11 = this.mergeDeps(var11, var20.getDependencies());
         var12 = this.mergeDeps(var12, var20.getManagedDependencies());
         var16 = new DefaultDependencyNode(var9);
         var16.setRequestContext(var2.getRequestContext());
         var16.setRelocations(var20.getRelocations());
         var16.setVersionConstraint(var18.getVersionConstraint());
         var16.setVersion(var32);
         var16.setAliases(var20.getAliases());
         var16.setRepositories(var2.getRepositories());
      } else {
         var16 = new DefaultDependencyNode(var2.getRootArtifact());
         var16.setRequestContext(var2.getRequestContext());
         var16.setRepositories(var2.getRepositories());
      }

      var4.setRoot(var16);
      boolean var30 = var9 == null || var7 == null || var7.traverseDependency(var9);
      String var31 = null;
      if (var30 && !var11.isEmpty()) {
         DataPool var33 = new DataPool(var1);
         NodeStack var35 = new NodeStack();
         var35.push(var16);
         DefaultDependencyCollectionContext var36 = new DefaultDependencyCollectionContext(var1, var2.getRootArtifact(), var9, var12);
         DefaultVersionFilterContext var22 = new DefaultVersionFilterContext(var1);
         DefaultDependencyCollector.Args var23 = new DefaultDependencyCollector.Args(var1, var3, var33, var35, var36, var22, var2);
         DefaultDependencyCollector.Results var24 = new DefaultDependencyCollector.Results(var4, var1);
         this.process(
            var23,
            var24,
            var11,
            var10,
            var5 != null ? var5.deriveChildSelector(var36) : null,
            var6 != null ? var6.deriveChildManager(var36) : null,
            var7 != null ? var7.deriveChildTraverser(var36) : null,
            var8 != null ? var8.deriveChildFilter(var36) : null
         );
         var31 = var24.errorPath;
      }

      long var34 = System.nanoTime();
      DependencyGraphTransformer var37 = var1.getDependencyGraphTransformer();
      if (var37 != null) {
         try {
            DefaultDependencyGraphTransformationContext var38 = new DefaultDependencyGraphTransformationContext(var1);
            var38.put(TransformationContextKeys.STATS, var13);
            var4.setRoot(var37.transformGraph(var16, var38));
         } catch (RepositoryException var25) {
            var4.addException(var25);
         }
      }

      long var39 = System.nanoTime();
      var13.put("DefaultDependencyCollector.collectTime", var34 - var14);
      var13.put("DefaultDependencyCollector.transformTime", var39 - var34);
      LOGGER.debug("Dependency collection stats {}", var13);
      if (var31 != null) {
         throw new DependencyCollectionException(var4, "Failed to collect dependencies at " + var31);
      } else if (!var4.getExceptions().isEmpty()) {
         throw new DependencyCollectionException(var4);
      } else {
         return var4;
      }
   }

   private static RepositorySystemSession optimizeSession(RepositorySystemSession var0) {
      DefaultRepositorySystemSession var1 = new DefaultRepositorySystemSession(var0);
      var1.setArtifactTypeRegistry(CachingArtifactTypeRegistry.newInstance(var0));
      return var1;
   }

   private List<Dependency> mergeDeps(List<Dependency> var1, List<Dependency> var2) {
      Object var3;
      if (var1 == null || var1.isEmpty()) {
         var3 = var2;
      } else if (var2 != null && !var2.isEmpty()) {
         int var4 = var1.size() + var2.size();
         var3 = new ArrayList(var4);
         HashSet var5 = new HashSet(var4, 1.0F);

         for (Dependency var7 : var1) {
            var5.add(getId(var7.getArtifact()));
            var3.add(var7);
         }

         for (Dependency var9 : var2) {
            if (!var5.contains(getId(var9.getArtifact()))) {
               var3.add(var9);
            }
         }
      } else {
         var3 = var1;
      }

      return (List<Dependency>)var3;
   }

   private static String getId(Artifact var0) {
      return var0.getGroupId() + ':' + var0.getArtifactId() + ':' + var0.getClassifier() + ':' + var0.getExtension();
   }

   private void process(
      DefaultDependencyCollector.Args var1,
      DefaultDependencyCollector.Results var2,
      List<Dependency> var3,
      List<RemoteRepository> var4,
      DependencySelector var5,
      DependencyManager var6,
      DependencyTraverser var7,
      VersionFilter var8
   ) {
      for (Dependency var10 : var3) {
         this.processDependency(var1, var2, var4, var5, var6, var7, var8, var10);
      }
   }

   private void processDependency(
      DefaultDependencyCollector.Args var1,
      DefaultDependencyCollector.Results var2,
      List<RemoteRepository> var3,
      DependencySelector var4,
      DependencyManager var5,
      DependencyTraverser var6,
      VersionFilter var7,
      Dependency var8
   ) {
      List var9 = Collections.emptyList();
      boolean var10 = false;
      this.processDependency(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private void processDependency(
      DefaultDependencyCollector.Args var1,
      DefaultDependencyCollector.Results var2,
      List<RemoteRepository> var3,
      DependencySelector var4,
      DependencyManager var5,
      DependencyTraverser var6,
      VersionFilter var7,
      Dependency var8,
      List<Artifact> var9,
      boolean var10
   ) {
      if (var4 == null || var4.selectDependency(var8)) {
         DefaultDependencyCollector.PremanagedDependency var11 = DefaultDependencyCollector.PremanagedDependency.create(var5, var8, var10, var1.premanagedState);
         var8 = var11.managedDependency;
         boolean var12 = isLackingDescriptor(var8.getArtifact());
         boolean var13 = !var12 && (var6 == null || var6.traverseDependency(var8));

         List var14;
         VersionRangeResult var15;
         try {
            VersionRangeRequest var16 = createVersionRangeRequest(var1, var3, var8);
            var15 = this.cachedResolveRangeResult(var16, var1.pool, var1.session);
            var14 = filterVersions(var8, var15, var7, var1.versionContext);
         } catch (VersionRangeResolutionException var27) {
            var2.addException(var8, var27, var1.nodes);
            return;
         }

         for (Version var17 : var14) {
            Artifact var18 = var8.getArtifact().setVersion(var17.toString());
            Dependency var19 = var8.setArtifact(var18);
            ArtifactDescriptorRequest var20 = createArtifactDescriptorRequest(var1, var3, var19);
            ArtifactDescriptorResult var21 = this.getArtifactDescriptorResult(var1, var2, var12, var19, var20);
            if (var21 != null) {
               var19 = var19.setArtifact(var21.getArtifact());
               DependencyNode var22 = var1.nodes.top();
               int var23 = var1.nodes.find(var19.getArtifact());
               if (var23 >= 0) {
                  var2.addCycle(var1.nodes, var23, var19);
                  DependencyNode var24 = var1.nodes.get(var23);
                  if (var24.getDependency() != null) {
                     DefaultDependencyNode var37 = createDependencyNode(var9, var11, var15, var17, var19, var21, var24);
                     var22.getChildren().add(var37);
                     continue;
                  }
               }

               if (!var21.getRelocations().isEmpty()) {
                  boolean var35 = var18.getGroupId().equals(var19.getArtifact().getGroupId())
                     && var18.getArtifactId().equals(var19.getArtifact().getArtifactId());
                  this.processDependency(var1, var2, var3, var4, var5, var6, var7, var19, var21.getRelocations(), var35);
                  return;
               }

               var19 = var1.pool.intern(var19.setArtifact(var1.pool.intern(var19.getArtifact())));
               List var34 = getRemoteRepositories(var15.getRepository(var17), var3);
               DefaultDependencyNode var25 = createDependencyNode(var9, var11, var15, var17, var19, var21.getAliases(), var34, var1.request.getRequestContext());
               var22.getChildren().add(var25);
               boolean var26 = var13 && !var21.getDependencies().isEmpty();
               if (var26) {
                  this.doRecurse(var1, var2, var3, var4, var5, var6, var7, var19, var21, var25);
               }
            } else {
               DependencyNode var32 = var1.nodes.top();
               List var33 = getRemoteRepositories(var15.getRepository(var17), var3);
               DefaultDependencyNode var36 = createDependencyNode(var9, var11, var15, var17, var19, null, var33, var1.request.getRequestContext());
               var32.getChildren().add(var36);
            }
         }
      }
   }

   private void doRecurse(
      DefaultDependencyCollector.Args var1,
      DefaultDependencyCollector.Results var2,
      List<RemoteRepository> var3,
      DependencySelector var4,
      DependencyManager var5,
      DependencyTraverser var6,
      VersionFilter var7,
      Dependency var8,
      ArtifactDescriptorResult var9,
      DefaultDependencyNode var10
   ) {
      DefaultDependencyCollectionContext var11 = var1.collectionContext;
      var11.set(var8, var9.getManagedDependencies());
      DependencySelector var12 = var4 != null ? var4.deriveChildSelector(var11) : null;
      DependencyManager var13 = var5 != null ? var5.deriveChildManager(var11) : null;
      DependencyTraverser var14 = var6 != null ? var6.deriveChildTraverser(var11) : null;
      VersionFilter var15 = var7 != null ? var7.deriveChildFilter(var11) : null;
      List var16 = var1.ignoreRepos ? var3 : this.remoteRepositoryManager.aggregateRepositories(var1.session, var3, var9.getRepositories(), true);
      Object var17 = var1.pool.toKey(var8.getArtifact(), var16, var12, var13, var14, var15);
      List var18 = var1.pool.getChildren(var17);
      if (var18 == null) {
         var1.pool.putChildren(var17, var10.getChildren());
         var1.nodes.push(var10);
         this.process(var1, var2, var9.getDependencies(), var16, var12, var13, var14, var15);
         var1.nodes.pop();
      } else {
         var10.setChildren(var18);
      }
   }

   private ArtifactDescriptorResult getArtifactDescriptorResult(
      DefaultDependencyCollector.Args var1, DefaultDependencyCollector.Results var2, boolean var3, Dependency var4, ArtifactDescriptorRequest var5
   ) {
      return var3 ? new ArtifactDescriptorResult(var5) : this.resolveCachedArtifactDescriptor(var1.pool, var5, var1.session, var4, var2, var1);
   }

   private ArtifactDescriptorResult resolveCachedArtifactDescriptor(
      DataPool var1,
      ArtifactDescriptorRequest var2,
      RepositorySystemSession var3,
      Dependency var4,
      DefaultDependencyCollector.Results var5,
      DefaultDependencyCollector.Args var6
   ) {
      Object var7 = var1.toKey(var2);
      ArtifactDescriptorResult var8 = var1.getDescriptor(var7, var2);
      if (var8 == null) {
         try {
            var8 = this.descriptorReader.readArtifactDescriptor(var3, var2);
            var1.putDescriptor(var7, var8);
         } catch (ArtifactDescriptorException var10) {
            var5.addException(var4, var10, var6.nodes);
            var1.putDescriptor(var7, var10);
            return null;
         }
      } else if (var8 == DataPool.NO_DESCRIPTOR) {
         return null;
      }

      return var8;
   }

   private static DefaultDependencyNode createDependencyNode(
      List<Artifact> var0,
      DefaultDependencyCollector.PremanagedDependency var1,
      VersionRangeResult var2,
      Version var3,
      Dependency var4,
      Collection<Artifact> var5,
      List<RemoteRepository> var6,
      String var7
   ) {
      DefaultDependencyNode var8 = new DefaultDependencyNode(var4);
      var1.applyTo(var8);
      var8.setRelocations(var0);
      var8.setVersionConstraint(var2.getVersionConstraint());
      var8.setVersion(var3);
      var8.setAliases(var5);
      var8.setRepositories(var6);
      var8.setRequestContext(var7);
      return var8;
   }

   private static DefaultDependencyNode createDependencyNode(
      List<Artifact> var0,
      DefaultDependencyCollector.PremanagedDependency var1,
      VersionRangeResult var2,
      Version var3,
      Dependency var4,
      ArtifactDescriptorResult var5,
      DependencyNode var6
   ) {
      DefaultDependencyNode var7 = createDependencyNode(var0, var1, var2, var3, var4, var5.getAliases(), var6.getRepositories(), var6.getRequestContext());
      var7.setChildren(var6.getChildren());
      return var7;
   }

   private static ArtifactDescriptorRequest createArtifactDescriptorRequest(DefaultDependencyCollector.Args var0, List<RemoteRepository> var1, Dependency var2) {
      ArtifactDescriptorRequest var3 = new ArtifactDescriptorRequest();
      var3.setArtifact(var2.getArtifact());
      var3.setRepositories(var1);
      var3.setRequestContext(var0.request.getRequestContext());
      var3.setTrace(var0.trace);
      return var3;
   }

   private static VersionRangeRequest createVersionRangeRequest(DefaultDependencyCollector.Args var0, List<RemoteRepository> var1, Dependency var2) {
      VersionRangeRequest var3 = new VersionRangeRequest();
      var3.setArtifact(var2.getArtifact());
      var3.setRepositories(var1);
      var3.setRequestContext(var0.request.getRequestContext());
      var3.setTrace(var0.trace);
      return var3;
   }

   private VersionRangeResult cachedResolveRangeResult(VersionRangeRequest var1, DataPool var2, RepositorySystemSession var3) throws VersionRangeResolutionException {
      Object var4 = var2.toKey(var1);
      VersionRangeResult var5 = var2.getConstraint(var4, var1);
      if (var5 == null) {
         var5 = this.versionRangeResolver.resolveVersionRange(var3, var1);
         var2.putConstraint(var4, var5);
      }

      return var5;
   }

   private static boolean isLackingDescriptor(Artifact var0) {
      return var0.getProperty("localPath", null) != null;
   }

   private static List<RemoteRepository> getRemoteRepositories(ArtifactRepository var0, List<RemoteRepository> var1) {
      if (var0 instanceof RemoteRepository) {
         return Collections.singletonList((RemoteRepository)var0);
      } else {
         return var0 != null ? Collections.emptyList() : var1;
      }
   }

   private static List<? extends Version> filterVersions(Dependency var0, VersionRangeResult var1, VersionFilter var2, DefaultVersionFilterContext var3) throws VersionRangeResolutionException {
      if (var1.getVersions().isEmpty()) {
         throw new VersionRangeResolutionException(var1, "No versions available for " + var0.getArtifact() + " within specified range");
      } else {
         List var4;
         if (var2 != null && var1.getVersionConstraint().getRange() != null) {
            var3.set(var0, var1);

            try {
               var2.filterVersions(var3);
            } catch (RepositoryException var6) {
               throw new VersionRangeResolutionException(var1, "Failed to filter versions for " + var0.getArtifact() + ": " + var6.getMessage(), var6);
            }

            var4 = var3.get();
            if (var4.isEmpty()) {
               throw new VersionRangeResolutionException(var1, "No acceptable versions for " + var0.getArtifact() + ": " + var1.getVersions());
            }
         } else {
            var4 = var1.getVersions();
         }

         return var4;
      }
   }

   static class Args {
      final RepositorySystemSession session;
      final boolean ignoreRepos;
      final boolean premanagedState;
      final RequestTrace trace;
      final DataPool pool;
      final NodeStack nodes;
      final DefaultDependencyCollectionContext collectionContext;
      final DefaultVersionFilterContext versionContext;
      final CollectRequest request;

      Args(
         RepositorySystemSession var1,
         RequestTrace var2,
         DataPool var3,
         NodeStack var4,
         DefaultDependencyCollectionContext var5,
         DefaultVersionFilterContext var6,
         CollectRequest var7
      ) {
         this.session = var1;
         this.request = var7;
         this.ignoreRepos = var1.isIgnoreArtifactDescriptorRepositories();
         this.premanagedState = ConfigUtils.getBoolean(var1, false, "aether.dependencyManager.verbose");
         this.trace = var2;
         this.pool = var3;
         this.nodes = var4;
         this.collectionContext = var5;
         this.versionContext = var6;
      }
   }

   static class PremanagedDependency {
      final String premanagedVersion;
      final String premanagedScope;
      final Boolean premanagedOptional;
      final Collection<Exclusion> premanagedExclusions;
      final Map<String, String> premanagedProperties;
      final int managedBits;
      final Dependency managedDependency;
      final boolean premanagedState;

      PremanagedDependency(
         String var1, String var2, Boolean var3, Collection<Exclusion> var4, Map<String, String> var5, int var6, Dependency var7, boolean var8
      ) {
         this.premanagedVersion = var1;
         this.premanagedScope = var2;
         this.premanagedOptional = var3;
         this.premanagedExclusions = var4 != null ? Collections.unmodifiableCollection(new ArrayList<>(var4)) : null;
         this.premanagedProperties = var5 != null ? Collections.unmodifiableMap(new HashMap<>(var5)) : null;
         this.managedBits = var6;
         this.managedDependency = var7;
         this.premanagedState = var8;
      }

      static DefaultDependencyCollector.PremanagedDependency create(DependencyManager var0, Dependency var1, boolean var2, boolean var3) {
         DependencyManagement var4 = var0 != null ? var0.manageDependency(var1) : null;
         byte var5 = 0;
         String var6 = null;
         String var7 = null;
         Boolean var8 = null;
         Collection var9 = null;
         Map var10 = null;
         if (var4 != null) {
            if (var4.getVersion() != null && !var2) {
               Artifact var11 = var1.getArtifact();
               var6 = var11.getVersion();
               var1 = var1.setArtifact(var11.setVersion(var4.getVersion()));
               var5 |= 1;
            }

            if (var4.getProperties() != null) {
               Artifact var12 = var1.getArtifact();
               var10 = var12.getProperties();
               var1 = var1.setArtifact(var12.setProperties(var4.getProperties()));
               var5 |= 8;
            }

            if (var4.getScope() != null) {
               var7 = var1.getScope();
               var1 = var1.setScope(var4.getScope());
               var5 |= 2;
            }

            if (var4.getOptional() != null) {
               var8 = var1.isOptional();
               var1 = var1.setOptional(var4.getOptional());
               var5 |= 4;
            }

            if (var4.getExclusions() != null) {
               var9 = var1.getExclusions();
               var1 = var1.setExclusions(var4.getExclusions());
               var5 |= 16;
            }
         }

         return new DefaultDependencyCollector.PremanagedDependency(var6, var7, var8, var9, var10, var5, var1, var3);
      }

      public void applyTo(DefaultDependencyNode var1) {
         var1.setManagedBits(this.managedBits);
         if (this.premanagedState) {
            var1.setData("premanaged.version", this.premanagedVersion);
            var1.setData("premanaged.scope", this.premanagedScope);
            var1.setData("premanaged.optional", this.premanagedOptional);
            var1.setData("premanaged.exclusions", this.premanagedExclusions);
            var1.setData("premanaged.properties", this.premanagedProperties);
         }
      }
   }

   static class Results {
      private final CollectResult result;
      final int maxExceptions;
      final int maxCycles;
      String errorPath;

      Results(CollectResult var1, RepositorySystemSession var2) {
         this.result = var1;
         this.maxExceptions = ConfigUtils.getInteger(var2, 50, "aether.dependencyCollector.maxExceptions");
         this.maxCycles = ConfigUtils.getInteger(var2, 10, "aether.dependencyCollector.maxCycles");
      }

      public void addException(Dependency var1, Exception var2, NodeStack var3) {
         if (this.maxExceptions < 0 || this.result.getExceptions().size() < this.maxExceptions) {
            this.result.addException(var2);
            if (this.errorPath == null) {
               StringBuilder var4 = new StringBuilder(256);

               for (int var5 = 0; var5 < var3.size(); var5++) {
                  if (var4.length() > 0) {
                     var4.append(" -> ");
                  }

                  Dependency var6 = var3.get(var5).getDependency();
                  if (var6 != null) {
                     var4.append(var6.getArtifact());
                  }
               }

               if (var4.length() > 0) {
                  var4.append(" -> ");
               }

               var4.append(var1.getArtifact());
               this.errorPath = var4.toString();
            }
         }
      }

      public void addCycle(NodeStack var1, int var2, Dependency var3) {
         if (this.maxCycles < 0 || this.result.getCycles().size() < this.maxCycles) {
            this.result.addCycle(new DefaultDependencyCycle(var1, var2, var3));
         }
      }
   }
}
