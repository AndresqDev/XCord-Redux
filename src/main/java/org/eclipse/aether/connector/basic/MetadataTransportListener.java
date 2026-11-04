package org.eclipse.aether.connector.basic;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.MetadataTransfer;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.TransferEvent;

final class MetadataTransportListener extends TransferTransportListener<MetadataTransfer> {
   private final RemoteRepository repository;

   MetadataTransportListener(MetadataTransfer var1, RemoteRepository var2, TransferEvent.Builder var3) {
      super(var1, var3);
      this.repository = var2;
   }

   @Override
   public void transferFailed(Exception var1, int var2) {
      Object var3;
      if (var2 == 1) {
         var3 = new MetadataNotFoundException(this.getTransfer().getMetadata(), this.repository);
      } else {
         var3 = new MetadataTransferException(this.getTransfer().getMetadata(), this.repository, var1);
      }

      this.getTransfer().setException((MetadataTransferException)var3);
      super.transferFailed((Exception)var3, var2);
   }
}
