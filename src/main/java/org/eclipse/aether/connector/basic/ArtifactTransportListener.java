package org.eclipse.aether.connector.basic;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.ArtifactTransfer;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferEvent;

final class ArtifactTransportListener extends TransferTransportListener<ArtifactTransfer> {
   private final RemoteRepository repository;

   ArtifactTransportListener(ArtifactTransfer var1, RemoteRepository var2, TransferEvent.Builder var3) {
      super(var1, var3);
      this.repository = var2;
   }

   @Override
   public void transferFailed(Exception var1, int var2) {
      Object var3;
      if (var2 == 1) {
         var3 = new ArtifactNotFoundException(this.getTransfer().getArtifact(), this.repository);
      } else {
         var3 = new ArtifactTransferException(this.getTransfer().getArtifact(), this.repository, var1);
      }

      this.getTransfer().setException((ArtifactTransferException)var3);
      super.transferFailed((Exception)var3, var2);
   }
}
