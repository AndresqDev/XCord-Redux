package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.MetadataTransferException;

public abstract class MetadataTransfer extends Transfer {
   private Metadata metadata;
   private File file;
   private MetadataTransferException exception;

   MetadataTransfer() {
   }

   public Metadata getMetadata() {
      return this.metadata;
   }

   public MetadataTransfer setMetadata(Metadata var1) {
      this.metadata = var1;
      return this;
   }

   public File getFile() {
      return this.file;
   }

   public MetadataTransfer setFile(File var1) {
      this.file = var1;
      return this;
   }

   public MetadataTransferException getException() {
      return this.exception;
   }

   public MetadataTransfer setException(MetadataTransferException var1) {
      this.exception = var1;
      return this;
   }
}
