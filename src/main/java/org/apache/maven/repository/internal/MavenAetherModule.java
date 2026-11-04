package org.apache.maven.repository.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.eclipse.aether.impl.AetherModule;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import xcord.Named;
import xcord.Singleton;

@Deprecated
public final class MavenAetherModule extends AbstractModule {
   protected void configure() {
      this.install(new AetherModule());
      this.bind(ArtifactDescriptorReader.class).to(DefaultArtifactDescriptorReader.class).in(Singleton.class);
      this.bind(VersionResolver.class).to(DefaultVersionResolver.class).in(Singleton.class);
      this.bind(VersionRangeResolver.class).to(DefaultVersionRangeResolver.class).in(Singleton.class);
      this.bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("snapshot")).to(SnapshotMetadataGeneratorFactory.class).in(Singleton.class);
      this.bind(MetadataGeneratorFactory.class).annotatedWith(Names.named("versions")).to(VersionsMetadataGeneratorFactory.class).in(Singleton.class);
      this.bind(ModelBuilder.class).toInstance(new DefaultModelBuilderFactory().newInstance());
   }

   @Provides
   @Singleton
   Set<MetadataGeneratorFactory> provideMetadataGeneratorFactories(
      @Named("snapshot") MetadataGeneratorFactory var1, @Named("versions") MetadataGeneratorFactory var2
   ) {
      HashSet var3 = new HashSet();
      var3.add(var1);
      var3.add(var2);
      return Collections.unmodifiableSet(var3);
   }
}
