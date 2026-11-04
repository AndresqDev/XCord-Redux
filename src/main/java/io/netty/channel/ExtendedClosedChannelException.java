package io.netty.channel;

import java.nio.channels.ClosedChannelException;

final class ExtendedClosedChannelException extends ClosedChannelException {
   ExtendedClosedChannelException(Throwable var1) {
      if (var1 != null) {
         this.initCause(var1);
      }
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
