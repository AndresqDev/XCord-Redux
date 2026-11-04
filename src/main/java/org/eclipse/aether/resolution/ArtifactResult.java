package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.transfer.ArtifactNotFoundException;

public final class ArtifactResult {
   private final ArtifactRequest request;
   private List<Exception> exceptions;
   private Artifact artifact;
   private ArtifactRepository repository;

   public ArtifactResult(ArtifactRequest var1) {
      this.request = Objects.requireNonNull(var1, "artifact request cannot be null");
      this.exceptions = Collections.emptyList();
   }

   public ArtifactRequest getRequest() {
      return this.request;
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public ArtifactResult setArtifact(Artifact var1) {
      this.artifact = var1;
      return this;
   }

   public List<Exception> getExceptions() {
      return this.exceptions;
   }

   public ArtifactResult addException(Exception var1) {
      if (var1 != null) {
         if (this.exceptions.isEmpty()) {
            this.exceptions = new ArrayList<>();
         }

         this.exceptions.add(var1);
      }

      return this;
   }

   public ArtifactRepository getRepository() {
      return this.repository;
   }

   public ArtifactResult setRepository(ArtifactRepository var1) {
      this.repository = var1;
      return this;
   }

   public boolean isResolved() {
      return this.getArtifact() != null && this.getArtifact().getFile() != null;
   }

   public boolean isMissing() {
      for (Exception var2 : this.getExceptions()) {
         if (!(var2 instanceof ArtifactNotFoundException)) {
            return false;
         }
      }

      return !this.isResolved();
   }

   @Override
   public String toString() {
      return this.getArtifact() + " < " + this.getRepository();
   }
}
