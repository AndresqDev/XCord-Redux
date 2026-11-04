package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicyRequest;
import org.eclipse.aether.transfer.RepositoryOfflineException;

final class Utils {
   public static PrioritizedComponents<MetadataGeneratorFactory> sortMetadataGeneratorFactories(
      RepositorySystemSession var0, Collection<? extends MetadataGeneratorFactory> var1
   ) {
      PrioritizedComponents var2 = new PrioritizedComponents(var0);

      for (MetadataGeneratorFactory var4 : var1) {
         var2.add(var4, var4.getPriority());
      }

      return var2;
   }

   public static List<Metadata> prepareMetadata(List<? extends MetadataGenerator> var0, List<? extends Artifact> var1) {
      ArrayList var2 = new ArrayList();

      for (MetadataGenerator var4 : var0) {
         var2.addAll(var4.prepare(var1));
      }

      return var2;
   }

   public static List<Metadata> finishMetadata(List<? extends MetadataGenerator> var0, List<? extends Artifact> var1) {
      ArrayList var2 = new ArrayList();

      for (MetadataGenerator var4 : var0) {
         var2.addAll(var4.finish(var1));
      }

      return var2;
   }

   public static <T> List<T> combine(Collection<? extends T> var0, Collection<? extends T> var1) {
      ArrayList var2 = new ArrayList(var0.size() + var1.size());
      var2.addAll(var0);
      var2.addAll(var1);
      return var2;
   }

   public static int getPolicy(RepositorySystemSession var0, Artifact var1, RemoteRepository var2) {
      ResolutionErrorPolicy var3 = var0.getResolutionErrorPolicy();
      return var3 == null ? 0 : var3.getArtifactPolicy(var0, new ResolutionErrorPolicyRequest<>(var1, var2));
   }

   public static int getPolicy(RepositorySystemSession var0, Metadata var1, RemoteRepository var2) {
      ResolutionErrorPolicy var3 = var0.getResolutionErrorPolicy();
      return var3 == null ? 0 : var3.getMetadataPolicy(var0, new ResolutionErrorPolicyRequest<>(var1, var2));
   }

   public static void appendClassLoader(StringBuilder var0, Object var1) {
      ClassLoader var2 = var1.getClass().getClassLoader();
      if (var2 != null && !var2.equals(Utils.class.getClassLoader())) {
         var0.append(" from ").append(var2);
      }
   }

   public static void checkOffline(RepositorySystemSession var0, OfflineController var1, RemoteRepository var2) throws RepositoryOfflineException {
      if (var0.isOffline()) {
         var1.checkOffline(var0, var2);
      }
   }
}
