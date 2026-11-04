package io.netty.handler.timeout;

import io.netty.channel.ChannelException;

public class TimeoutException extends ChannelException {
   private static final long serialVersionUID = 4673641882869672533L;

   TimeoutException() {
   }

   TimeoutException(String var1, boolean var2) {
      super(var1, null, var2);
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
