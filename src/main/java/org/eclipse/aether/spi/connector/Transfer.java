package org.eclipse.aether.spi.connector;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferListener;

public abstract class Transfer {
   private TransferListener listener;
   private RequestTrace trace;

   Transfer() {
   }

   public abstract Exception getException();

   public TransferListener getListener() {
      return this.listener;
   }

   Transfer setListener(TransferListener var1) {
      this.listener = var1;
      return this;
   }

   public RequestTrace getTrace() {
      return this.trace;
   }

   Transfer setTrace(RequestTrace var1) {
      this.trace = var1;
      return this;
   }
}
