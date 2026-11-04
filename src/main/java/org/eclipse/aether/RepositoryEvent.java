package org.eclipse.aether;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;

public final class RepositoryEvent {
   private final RepositoryEvent.EventType type;
   private final RepositorySystemSession session;
   private final Artifact artifact;
   private final Metadata metadata;
   private final ArtifactRepository repository;
   private final File file;
   private final List<Exception> exceptions;
   private final RequestTrace trace;

   RepositoryEvent(RepositoryEvent.Builder var1) {
      this.type = var1.type;
      this.session = var1.session;
      this.artifact = var1.artifact;
      this.metadata = var1.metadata;
      this.repository = var1.repository;
      this.file = var1.file;
      this.exceptions = var1.exceptions;
      this.trace = var1.trace;
   }

   public RepositoryEvent.EventType getType() {
      return this.type;
   }

   public RepositorySystemSession getSession() {
      return this.session;
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public Metadata getMetadata() {
      return this.metadata;
   }

   public File getFile() {
      return this.file;
   }

   public ArtifactRepository getRepository() {
      return this.repository;
   }

   public Exception getException() {
      return this.exceptions.isEmpty() ? null : this.exceptions.get(0);
   }

   public List<Exception> getExceptions() {
      return this.exceptions;
   }

   public RequestTrace getTrace() {
      return this.trace;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(256);
      var1.append(this.getType());
      if (this.getArtifact() != null) {
         var1.append(" ").append(this.getArtifact());
      }

      if (this.getMetadata() != null) {
         var1.append(" ").append(this.getMetadata());
      }

      if (this.getFile() != null) {
         var1.append(" (").append(this.getFile()).append(")");
      }

      if (this.getRepository() != null) {
         var1.append(" @ ").append(this.getRepository());
      }

      return var1.toString();
   }

   public static final class Builder {
      RepositoryEvent.EventType type;
      RepositorySystemSession session;
      Artifact artifact;
      Metadata metadata;
      ArtifactRepository repository;
      File file;
      List<Exception> exceptions = Collections.emptyList();
      RequestTrace trace;

      public Builder(RepositorySystemSession var1, RepositoryEvent.EventType var2) {
         this.session = Objects.requireNonNull(var1, "session cannot be null");
         this.type = Objects.requireNonNull(var2, "event type cannot be null");
      }

      public RepositoryEvent.Builder setArtifact(Artifact var1) {
         this.artifact = var1;
         return this;
      }

      public RepositoryEvent.Builder setMetadata(Metadata var1) {
         this.metadata = var1;
         return this;
      }

      public RepositoryEvent.Builder setRepository(ArtifactRepository var1) {
         this.repository = var1;
         return this;
      }

      public RepositoryEvent.Builder setFile(File var1) {
         this.file = var1;
         return this;
      }

      public RepositoryEvent.Builder setException(Exception var1) {
         if (var1 != null) {
            this.exceptions = Collections.singletonList(var1);
         } else {
            this.exceptions = Collections.emptyList();
         }

         return this;
      }

      public RepositoryEvent.Builder setExceptions(List<Exception> var1) {
         if (var1 != null) {
            this.exceptions = var1;
         } else {
            this.exceptions = Collections.emptyList();
         }

         return this;
      }

      public RepositoryEvent.Builder setTrace(RequestTrace var1) {
         this.trace = var1;
         return this;
      }

      public RepositoryEvent build() {
         return new RepositoryEvent(this);
      }
   }

   public static enum EventType {
      ARTIFACT_DESCRIPTOR_INVALID,
      ARTIFACT_DESCRIPTOR_MISSING,
      METADATA_INVALID,
      ARTIFACT_RESOLVING,
      ARTIFACT_RESOLVED,
      METADATA_RESOLVING,
      METADATA_RESOLVED,
      ARTIFACT_DOWNLOADING,
      ARTIFACT_DOWNLOADED,
      METADATA_DOWNLOADING,
      METADATA_DOWNLOADED,
      ARTIFACT_INSTALLING,
      ARTIFACT_INSTALLED,
      METADATA_INSTALLING,
      METADATA_INSTALLED,
      ARTIFACT_DEPLOYING,
      ARTIFACT_DEPLOYED,
      METADATA_DEPLOYING,
      METADATA_DEPLOYED;
   }
}
