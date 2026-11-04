package org.eclipse.aether.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public final class DeployResult {
   private final DeployRequest request;
   private Collection<Artifact> artifacts;
   private Collection<Metadata> metadata;

   public DeployResult(DeployRequest var1) {
      this.request = Objects.requireNonNull(var1, "deploy request cannot be null");
      this.artifacts = Collections.emptyList();
      this.metadata = Collections.emptyList();
   }

   public DeployRequest getRequest() {
      return this.request;
   }

   public Collection<Artifact> getArtifacts() {
      return this.artifacts;
   }

   public DeployResult setArtifacts(Collection<Artifact> var1) {
      if (var1 == null) {
         this.artifacts = Collections.emptyList();
      } else {
         this.artifacts = var1;
      }

      return this;
   }

   public DeployResult addArtifact(Artifact var1) {
      if (var1 != null) {
         if (this.artifacts.isEmpty()) {
            this.artifacts = new ArrayList<>();
         }

         this.artifacts.add(var1);
      }

      return this;
   }

   public Collection<Metadata> getMetadata() {
      return this.metadata;
   }

   public DeployResult setMetadata(Collection<Metadata> var1) {
      if (var1 == null) {
         this.metadata = Collections.emptyList();
      } else {
         this.metadata = var1;
      }

      return this;
   }

   public DeployResult addMetadata(Metadata var1) {
      if (var1 != null) {
         if (this.metadata.isEmpty()) {
            this.metadata = new ArrayList<>();
         }

         this.metadata.add(var1);
      }

      return this;
   }

   @Override
   public String toString() {
      return this.getArtifacts() + ", " + this.getMetadata();
   }
}
