package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.TransferListener;

public final class MetadataUpload extends MetadataTransfer {
   public MetadataUpload() {
   }

   public MetadataUpload(Metadata var1, File var2) {
      this.setMetadata(var1);
      this.setFile(var2);
   }

   public MetadataUpload setMetadata(Metadata var1) {
      super.setMetadata(var1);
      return this;
   }

   public MetadataUpload setFile(File var1) {
      super.setFile(var1);
      return this;
   }

   public MetadataUpload setException(MetadataTransferException var1) {
      super.setException(var1);
      return this;
   }

   public MetadataUpload setListener(TransferListener var1) {
      super.setListener(var1);
      return this;
   }

   public MetadataUpload setTrace(RequestTrace var1) {
      super.setTrace(var1);
      return this;
   }

   @Override
   public String toString() {
      return this.getMetadata() + " - " + this.getFile();
   }
}
