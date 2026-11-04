package org.eclipse.aether.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;

public interface MetadataResolver {
   List<MetadataResult> resolveMetadata(RepositorySystemSession var1, Collection<? extends MetadataRequest> var2);
}
