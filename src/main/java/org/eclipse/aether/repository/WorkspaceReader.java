package org.eclipse.aether.repository;

import java.io.File;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;

public interface WorkspaceReader {
   WorkspaceRepository getRepository();

   File findArtifact(Artifact var1);

   List<String> findVersions(Artifact var1);
}
