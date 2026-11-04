package org.apache.maven.artifact;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.util.StringUtils;

public class DefaultArtifact implements Artifact {
   private String groupId;
   private String artifactId;
   private String baseVersion;
   private final String type;
   private final String classifier;
   private volatile String scope;
   private volatile File file;
   private ArtifactRepository repository;
   private String downloadUrl;
   private ArtifactFilter dependencyFilter;
   private ArtifactHandler artifactHandler;
   private List<String> dependencyTrail;
   private volatile String version;
   private VersionRange versionRange;
   private volatile boolean resolved;
   private boolean release;
   private List<ArtifactVersion> availableVersions;
   private Map<Object, ArtifactMetadata> metadataMap;
   private boolean optional;

   public DefaultArtifact(String var1, String var2, String var3, String var4, String var5, String var6, ArtifactHandler var7) {
      this(var1, var2, VersionRange.createFromVersion(var3), var4, var5, var6, var7, false);
   }

   public DefaultArtifact(String var1, String var2, VersionRange var3, String var4, String var5, String var6, ArtifactHandler var7) {
      this(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public DefaultArtifact(String var1, String var2, VersionRange var3, String var4, String var5, String var6, ArtifactHandler var7, boolean var8) {
      this.groupId = var1;
      this.artifactId = var2;
      this.versionRange = var3;
      this.selectVersionFromNewRangeIfAvailable();
      this.artifactHandler = var7;
      this.scope = var4;
      this.type = var5;
      if (var6 == null) {
         var6 = var7.getClassifier();
      }

      this.classifier = var6;
      this.optional = var8;
      this.validateIdentity();
   }

   private void validateIdentity() {
      if (this.empty(this.groupId)) {
         throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The groupId cannot be empty.");
      } else if (this.artifactId == null) {
         throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The artifactId cannot be empty.");
      } else if (this.type == null) {
         throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The type cannot be empty.");
      } else if (this.version == null && this.versionRange == null) {
         throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The version cannot be empty.");
      }
   }

   private boolean empty(String var1) {
      return var1 == null || var1.trim().length() < 1;
   }

   @Override
   public String getClassifier() {
      return this.classifier;
   }

   @Override
   public boolean hasClassifier() {
      return StringUtils.isNotEmpty(this.classifier);
   }

   @Override
   public String getScope() {
      return this.scope;
   }

   @Override
   public String getGroupId() {
      return this.groupId;
   }

   @Override
   public String getArtifactId() {
      return this.artifactId;
   }

   @Override
   public String getVersion() {
      return this.version;
   }

   @Override
   public void setVersion(String var1) {
      this.version = var1;
      this.setBaseVersionInternal(var1);
      this.versionRange = null;
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public void setFile(File var1) {
      this.file = var1;
   }

   @Override
   public File getFile() {
      return this.file;
   }

   @Override
   public ArtifactRepository getRepository() {
      return this.repository;
   }

   @Override
   public void setRepository(ArtifactRepository var1) {
      this.repository = var1;
   }

   @Override
   public String getId() {
      return this.getDependencyConflictId() + ":" + this.getBaseVersion();
   }

   @Override
   public String getDependencyConflictId() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(this.getGroupId());
      var1.append(':');
      this.appendArtifactTypeClassifierString(var1);
      return var1.toString();
   }

   private void appendArtifactTypeClassifierString(StringBuilder var1) {
      var1.append(this.getArtifactId());
      var1.append(':');
      var1.append(this.getType());
      if (this.hasClassifier()) {
         var1.append(':');
         var1.append(this.getClassifier());
      }
   }

   @Override
   public void addMetadata(ArtifactMetadata var1) {
      if (this.metadataMap == null) {
         this.metadataMap = new HashMap<>();
      }

      ArtifactMetadata var2 = this.metadataMap.get(var1.getKey());
      if (var2 != null) {
         var2.merge(var1);
      } else {
         this.metadataMap.put(var1.getKey(), var1);
      }
   }

   @Override
   public Collection<ArtifactMetadata> getMetadataList() {
      return (Collection<ArtifactMetadata>)(this.metadataMap == null ? Collections.emptyList() : Collections.unmodifiableCollection(this.metadataMap.values()));
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.getGroupId() != null) {
         var1.append(this.getGroupId());
         var1.append(':');
      }

      this.appendArtifactTypeClassifierString(var1);
      var1.append(':');
      if (this.getBaseVersionInternal() != null) {
         var1.append(this.getBaseVersionInternal());
      } else {
         var1.append(this.versionRange.toString());
      }

      if (this.scope != null) {
         var1.append(':');
         var1.append(this.scope);
      }

      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = 37 * var1 + this.groupId.hashCode();
      var1 = 37 * var1 + this.artifactId.hashCode();
      var1 = 37 * var1 + this.type.hashCode();
      if (this.version != null) {
         var1 = 37 * var1 + this.version.hashCode();
      }

      return 37 * var1 + (this.classifier != null ? this.classifier.hashCode() : 0);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Artifact)) {
         return false;
      } else {
         Artifact var2 = (Artifact)var1;
         if (!var2.getGroupId().equals(this.groupId)) {
            return false;
         } else if (!var2.getArtifactId().equals(this.artifactId)) {
            return false;
         } else if (!var2.getVersion().equals(this.version)) {
            return false;
         } else if (!var2.getType().equals(this.type)) {
            return false;
         } else {
            return var2.getClassifier() == null ? this.classifier == null : var2.getClassifier().equals(this.classifier);
         }
      }
   }

   @Override
   public String getBaseVersion() {
      if (this.baseVersion == null && this.version != null) {
         this.setBaseVersionInternal(this.version);
      }

      return this.baseVersion;
   }

   protected String getBaseVersionInternal() {
      if (this.baseVersion == null && this.version != null) {
         this.setBaseVersionInternal(this.version);
      }

      return this.baseVersion;
   }

   @Override
   public void setBaseVersion(String var1) {
      this.setBaseVersionInternal(var1);
   }

   protected void setBaseVersionInternal(String var1) {
      this.baseVersion = ArtifactUtils.toSnapshotVersion(var1);
   }

   public int compareTo(Artifact var1) {
      int var2 = this.groupId.compareTo(var1.getGroupId());
      if (var2 == 0) {
         var2 = this.artifactId.compareTo(var1.getArtifactId());
         if (var2 == 0) {
            var2 = this.type.compareTo(var1.getType());
            if (var2 == 0) {
               if (this.classifier == null) {
                  if (var1.getClassifier() != null) {
                     var2 = 1;
                  }
               } else if (var1.getClassifier() != null) {
                  var2 = this.classifier.compareTo(var1.getClassifier());
               } else {
                  var2 = -1;
               }

               if (var2 == 0) {
                  var2 = new DefaultArtifactVersion(this.version).compareTo((ArtifactVersion)(new DefaultArtifactVersion(var1.getVersion())));
               }
            }
         }
      }

      return var2;
   }

   @Override
   public void updateVersion(String var1, ArtifactRepository var2) {
      this.setResolvedVersion(var1);
      this.setFile(new File(var2.getBasedir(), var2.pathOf(this)));
   }

   @Override
   public String getDownloadUrl() {
      return this.downloadUrl;
   }

   @Override
   public void setDownloadUrl(String var1) {
      this.downloadUrl = var1;
   }

   @Override
   public ArtifactFilter getDependencyFilter() {
      return this.dependencyFilter;
   }

   @Override
   public void setDependencyFilter(ArtifactFilter var1) {
      this.dependencyFilter = var1;
   }

   @Override
   public ArtifactHandler getArtifactHandler() {
      return this.artifactHandler;
   }

   @Override
   public List<String> getDependencyTrail() {
      return this.dependencyTrail;
   }

   @Override
   public void setDependencyTrail(List<String> var1) {
      this.dependencyTrail = var1;
   }

   @Override
   public void setScope(String var1) {
      this.scope = var1;
   }

   @Override
   public VersionRange getVersionRange() {
      return this.versionRange;
   }

   @Override
   public void setVersionRange(VersionRange var1) {
      this.versionRange = var1;
      this.selectVersionFromNewRangeIfAvailable();
   }

   private void selectVersionFromNewRangeIfAvailable() {
      if (this.versionRange != null && this.versionRange.getRecommendedVersion() != null) {
         this.selectVersion(this.versionRange.getRecommendedVersion().toString());
      } else {
         this.version = null;
         this.baseVersion = null;
      }
   }

   @Override
   public void selectVersion(String var1) {
      this.version = var1;
      this.setBaseVersionInternal(var1);
   }

   @Override
   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   @Override
   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   @Override
   public boolean isSnapshot() {
      return this.getBaseVersion() != null && (this.getBaseVersion().endsWith("SNAPSHOT") || this.getBaseVersion().equals("LATEST"));
   }

   @Override
   public void setResolved(boolean var1) {
      this.resolved = var1;
   }

   @Override
   public boolean isResolved() {
      return this.resolved;
   }

   @Override
   public void setResolvedVersion(String var1) {
      this.version = var1;
   }

   @Override
   public void setArtifactHandler(ArtifactHandler var1) {
      this.artifactHandler = var1;
   }

   @Override
   public void setRelease(boolean var1) {
      this.release = var1;
   }

   @Override
   public boolean isRelease() {
      return this.release;
   }

   @Override
   public List<ArtifactVersion> getAvailableVersions() {
      return this.availableVersions;
   }

   @Override
   public void setAvailableVersions(List<ArtifactVersion> var1) {
      this.availableVersions = var1;
   }

   @Override
   public boolean isOptional() {
      return this.optional;
   }

   @Override
   public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
      return this.versionRange.getSelectedVersion(this);
   }

   @Override
   public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
      return this.versionRange.isSelectedVersionKnown(this);
   }

   @Override
   public void setOptional(boolean var1) {
      this.optional = var1;
   }
}
