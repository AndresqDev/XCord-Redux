package org.eclipse.aether.spi.connector;

import java.io.Closeable;
import java.util.Collection;

public interface RepositoryConnector extends Closeable {
   void get(Collection<? extends ArtifactDownload> var1, Collection<? extends MetadataDownload> var2);

   void put(Collection<? extends ArtifactUpload> var1, Collection<? extends MetadataUpload> var2);

   @Override
   void close();
}
