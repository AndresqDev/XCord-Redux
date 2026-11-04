package org.eclipse.aether.transfer;

public abstract class AbstractTransferListener implements TransferListener {
   protected AbstractTransferListener() {
   }

   @Override
   public void transferInitiated(TransferEvent var1) throws TransferCancelledException {
   }

   @Override
   public void transferStarted(TransferEvent var1) throws TransferCancelledException {
   }

   @Override
   public void transferProgressed(TransferEvent var1) throws TransferCancelledException {
   }

   @Override
   public void transferCorrupted(TransferEvent var1) throws TransferCancelledException {
   }

   @Override
   public void transferSucceeded(TransferEvent var1) {
   }

   @Override
   public void transferFailed(TransferEvent var1) {
   }
}
