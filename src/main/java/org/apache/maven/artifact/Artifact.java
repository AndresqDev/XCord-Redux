package org.apache.maven.artifact;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

public interface Artifact extends Comparable<Artifact> {
   String RELEASE_VERSION = "RELEASE";
   String LATEST_VERSION = "LATEST";
   String SNAPSHOT_VERSION = "SNAPSHOT";
   Pattern VERSION_FILE_PATTERN = Pattern.compile("^(.*)-([0-9]{8}\\.[0-9]{6})-([0-9]+)$");
   String SCOPE_COMPILE = "compile";
   String SCOPE_COMPILE_PLUS_RUNTIME = "compile+runtime";
   String SCOPE_TEST = "test";
   String SCOPE_RUNTIME = "runtime";
   String SCOPE_RUNTIME_PLUS_SYSTEM = "runtime+system";
   String SCOPE_PROVIDED = "provided";
   String SCOPE_SYSTEM = "system";
   String SCOPE_IMPORT = "import";

   String getGroupId();

   String getArtifactId();

   String getVersion();

   void setVersion(String var1);

   String getScope();

   String getType();

   String getClassifier();

   boolean hasClassifier();

   File getFile();

   void setFile(File var1);

   String getBaseVersion();

   void setBaseVersion(String var1);

   String getId();

   String getDependencyConflictId();

   void addMetadata(ArtifactMetadata var1);

   Collection<ArtifactMetadata> getMetadataList();

   void setRepository(ArtifactRepository var1);

   ArtifactRepository getRepository();

   void updateVersion(String var1, ArtifactRepository var2);

   String getDownloadUrl();

   void setDownloadUrl(String var1);

   ArtifactFilter getDependencyFilter();

   void setDependencyFilter(ArtifactFilter var1);

   ArtifactHandler getArtifactHandler();

   List<String> getDependencyTrail();

   void setDependencyTrail(List<String> var1);

   void setScope(String var1);

   VersionRange getVersionRange();

   void setVersionRange(VersionRange var1);

   void selectVersion(String var1);

   void setGroupId(String var1);

   void setArtifactId(String var1);

   boolean isSnapshot();

   void setResolved(boolean var1);

   boolean isResolved();

   void setResolvedVersion(String var1);

   void setArtifactHandler(ArtifactHandler var1);

   boolean isRelease();

   void setRelease(boolean var1);

   List<ArtifactVersion> getAvailableVersions();

   void setAvailableVersions(List<ArtifactVersion> var1);

   boolean isOptional();

   void setOptional(boolean var1);

   ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException;

   boolean isSelectedVersionKnown() throws OverConstrainedVersionException;
}
