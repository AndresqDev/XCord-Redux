package org.eclipse.aether.connector.basic;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import org.eclipse.aether.spi.connector.Transfer;
import org.eclipse.aether.spi.connector.transport.TransportListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

class TransferTransportListener<T extends Transfer> extends TransportListener {
   private final T transfer;
   private final TransferListener listener;
   private final TransferEvent.Builder eventBuilder;
   private ChecksumCalculator checksumCalculator;

   protected TransferTransportListener(T var1, TransferEvent.Builder var2) {
      this.transfer = (T)var1;
      this.listener = var1.getListener();
      this.eventBuilder = var2;
   }

   protected T getTransfer() {
      return this.transfer;
   }

   public void transferInitiated() throws TransferCancelledException {
      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.INITIATED);
         this.listener.transferInitiated(this.eventBuilder.build());
      }
   }

   @Override
   public void transportStarted(long var1, long var3) throws TransferCancelledException {
      if (this.checksumCalculator != null) {
         this.checksumCalculator.init(var1);
      }

      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.STARTED).setTransferredBytes(var1);
         TransferEvent var5 = this.eventBuilder.build();
         var5.getResource().setContentLength(var3).setResumeOffset(var1);
         this.listener.transferStarted(var5);
      }
   }

   @Override
   public void transportProgressed(ByteBuffer var1) throws TransferCancelledException {
      if (this.checksumCalculator != null) {
         this.checksumCalculator.update(var1);
      }

      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.PROGRESSED).addTransferredBytes((long)var1.remaining()).setDataBuffer(var1);
         this.listener.transferProgressed(this.eventBuilder.build());
      }
   }

   public void transferCorrupted(Exception var1) throws TransferCancelledException {
      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.CORRUPTED).setException(var1);
         this.listener.transferCorrupted(this.eventBuilder.build());
      }
   }

   public void transferFailed(Exception var1, int var2) {
      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.FAILED).setException(var1);
         this.listener.transferFailed(this.eventBuilder.build());
      }
   }

   public void transferSucceeded() {
      if (this.listener != null) {
         this.eventBuilder.resetType(TransferEvent.EventType.SUCCEEDED);
         this.listener.transferSucceeded(this.eventBuilder.build());
      }
   }

   public Map<String, Object> getChecksums() {
      return this.checksumCalculator == null ? Collections.emptyMap() : this.checksumCalculator.get();
   }

   public void setChecksumCalculator(ChecksumCalculator var1) {
      this.checksumCalculator = var1;
   }
}
