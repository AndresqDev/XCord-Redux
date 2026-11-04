package org.apache.maven.repository.internal;

import java.util.Properties;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.util.artifact.DefaultArtifactTypeRegistry;
import org.eclipse.aether.util.graph.manager.ClassicDependencyManager;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.eclipse.aether.util.graph.transformer.JavaScopeDeriver;
import org.eclipse.aether.util.graph.transformer.JavaScopeSelector;
import org.eclipse.aether.util.graph.transformer.NearestVersionSelector;
import org.eclipse.aether.util.graph.transformer.SimpleOptionalitySelector;
import org.eclipse.aether.util.graph.traverser.FatArtifactTraverser;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;

public final class MavenRepositorySystemUtils {
   private MavenRepositorySystemUtils() {
   }

   public static DefaultServiceLocator newServiceLocator() {
      DefaultServiceLocator var0 = new DefaultServiceLocator();
      var0.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
      var0.addService(VersionResolver.class, DefaultVersionResolver.class);
      var0.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
      var0.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
      var0.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);
      return var0;
   }

   public static DefaultRepositorySystemSession newSession() {
      DefaultRepositorySystemSession var0 = new DefaultRepositorySystemSession();
      FatArtifactTraverser var1 = new FatArtifactTraverser();
      var0.setDependencyTraverser(var1);
      ClassicDependencyManager var2 = new ClassicDependencyManager();
      var0.setDependencyManager(var2);
      AndDependencySelector var3 = new AndDependencySelector(
         new ScopeDependencySelector("test", "provided"), new OptionalDependencySelector(), new ExclusionDependencySelector()
      );
      var0.setDependencySelector(var3);
      ConflictResolver var4 = new ConflictResolver(
         new NearestVersionSelector(), new JavaScopeSelector(), new SimpleOptionalitySelector(), new JavaScopeDeriver()
      );
      ChainedDependencyGraphTransformer var11 = new ChainedDependencyGraphTransformer(var4, new JavaDependencyContextRefiner());
      var0.setDependencyGraphTransformer(var11);
      DefaultArtifactTypeRegistry var5 = new DefaultArtifactTypeRegistry();
      var5.add(new DefaultArtifactType("pom"));
      var5.add(new DefaultArtifactType("maven-plugin", "jar", "", "java"));
      var5.add(new DefaultArtifactType("jar", "jar", "", "java"));
      var5.add(new DefaultArtifactType("ejb", "jar", "", "java"));
      var5.add(new DefaultArtifactType("ejb-client", "jar", "client", "java"));
      var5.add(new DefaultArtifactType("test-jar", "jar", "tests", "java"));
      var5.add(new DefaultArtifactType("javadoc", "jar", "javadoc", "java"));
      var5.add(new DefaultArtifactType("java-source", "jar", "sources", "java", false, false));
      var5.add(new DefaultArtifactType("war", "war", "", "java", false, true));
      var5.add(new DefaultArtifactType("ear", "ear", "", "java", false, true));
      var5.add(new DefaultArtifactType("rar", "rar", "", "java", false, true));
      var5.add(new DefaultArtifactType("par", "par", "", "java", false, true));
      var0.setArtifactTypeRegistry(var5);
      var0.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true));
      Properties var6 = new Properties();
      Properties var7 = System.getProperties();
      synchronized (var7) {
         var6.putAll(var7);
      }

      var0.setSystemProperties(var6);
      var0.setConfigProperties(var6);
      return var0;
   }
}
