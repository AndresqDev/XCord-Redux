package org.eclipse.aether.repository;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.aether.artifact.Artifact;

public final class LocalArtifactRegistration {
   private Artifact artifact;
   private RemoteRepository repository;
   private Collection<String> contexts = Collections.emptyList();

   public LocalArtifactRegistration() {
   }

   public LocalArtifactRegistration(Artifact var1) {
      this.setArtifact(var1);
   }

   public LocalArtifactRegistration(Artifact var1, RemoteRepository var2, Collection<String> var3) {
      this.setArtifact(var1);
      this.setRepository(var2);
      this.setContexts(var3);
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public LocalArtifactRegistration setArtifact(Artifact var1) {
      this.artifact = var1;
      return this;
   }

   public RemoteRepository getRepository() {
      return this.repository;
   }

   public LocalArtifactRegistration setRepository(RemoteRepository var1) {
      this.repository = var1;
      return this;
   }

   public Collection<String> getContexts() {
      return this.contexts;
   }

   public LocalArtifactRegistration setContexts(Collection<String> var1) {
      if (var1 != null) {
         this.contexts = var1;
      } else {
         this.contexts = Collections.emptyList();
      }

      return this;
   }
}
