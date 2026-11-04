package org.eclipse.aether.spi.connector.transport;

import java.net.URI;
import java.util.Objects;

public abstract class TransportTask {
   static final TransportListener NOOP = new TransportListener() {
   };
   static final byte[] EMPTY = new byte[0];
   private URI location;
   private TransportListener listener = NOOP;

   TransportTask() {
   }

   public URI getLocation() {
      return this.location;
   }

   TransportTask setLocation(URI var1) {
      this.location = Objects.requireNonNull(var1, "location type cannot be null");
      return this;
   }

   public TransportListener getListener() {
      return this.listener;
   }

   TransportTask setListener(TransportListener var1) {
      this.listener = var1 != null ? var1 : NOOP;
      return this;
   }
}
