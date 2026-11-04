package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.aether.repository.ArtifactRepository;

public final class VersionResult {
   private final VersionRequest request;
   private List<Exception> exceptions;
   private String version;
   private ArtifactRepository repository;

   public VersionResult(VersionRequest var1) {
      this.request = Objects.requireNonNull(var1, "version request cannot be null");
      this.exceptions = Collections.emptyList();
   }

   public VersionRequest getRequest() {
      return this.request;
   }

   public List<Exception> getExceptions() {
      return this.exceptions;
   }

   public VersionResult addException(Exception var1) {
      if (var1 != null) {
         if (this.exceptions.isEmpty()) {
            this.exceptions = new ArrayList<>();
         }

         this.exceptions.add(var1);
      }

      return this;
   }

   public String getVersion() {
      return this.version;
   }

   public VersionResult setVersion(String var1) {
      this.version = var1;
      return this;
   }

   public ArtifactRepository getRepository() {
      return this.repository;
   }

   public VersionResult setRepository(ArtifactRepository var1) {
      this.repository = var1;
      return this;
   }

   @Override
   public String toString() {
      return this.getVersion() + " @ " + this.getRepository();
   }
}
