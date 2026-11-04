package org.eclipse.aether.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public final class VersionRangeResult {
   private final VersionRangeRequest request;
   private List<Exception> exceptions;
   private List<Version> versions;
   private Map<Version, ArtifactRepository> repositories;
   private VersionConstraint versionConstraint;

   public VersionRangeResult(VersionRangeRequest var1) {
      this.request = Objects.requireNonNull(var1, "version range request cannot be null");
      this.exceptions = Collections.emptyList();
      this.versions = Collections.emptyList();
      this.repositories = Collections.emptyMap();
   }

   public VersionRangeRequest getRequest() {
      return this.request;
   }

   public List<Exception> getExceptions() {
      return this.exceptions;
   }

   public VersionRangeResult addException(Exception var1) {
      if (var1 != null) {
         if (this.exceptions.isEmpty()) {
            this.exceptions = new ArrayList<>();
         }

         this.exceptions.add(var1);
      }

      return this;
   }

   public List<Version> getVersions() {
      return this.versions;
   }

   public VersionRangeResult addVersion(Version var1) {
      if (this.versions.isEmpty()) {
         this.versions = new ArrayList<>();
      }

      this.versions.add(var1);
      return this;
   }

   public VersionRangeResult setVersions(List<Version> var1) {
      if (var1 == null) {
         this.versions = Collections.emptyList();
      } else {
         this.versions = var1;
      }

      return this;
   }

   public Version getLowestVersion() {
      return this.versions.isEmpty() ? null : this.versions.get(0);
   }

   public Version getHighestVersion() {
      return this.versions.isEmpty() ? null : this.versions.get(this.versions.size() - 1);
   }

   public ArtifactRepository getRepository(Version var1) {
      return this.repositories.get(var1);
   }

   public VersionRangeResult setRepository(Version var1, ArtifactRepository var2) {
      if (var2 != null) {
         if (this.repositories.isEmpty()) {
            this.repositories = new HashMap<>();
         }

         this.repositories.put(var1, var2);
      }

      return this;
   }

   public VersionConstraint getVersionConstraint() {
      return this.versionConstraint;
   }

   public VersionRangeResult setVersionConstraint(VersionConstraint var1) {
      this.versionConstraint = var1;
      return this;
   }

   @Override
   public String toString() {
      return String.valueOf(this.repositories);
   }
}
