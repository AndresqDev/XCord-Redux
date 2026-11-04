package io.netty.handler.timeout;

import io.netty.util.internal.PlatformDependent;

public final class WriteTimeoutException extends TimeoutException {
   private static final long serialVersionUID = -144786655770296065L;
   public static final WriteTimeoutException INSTANCE = PlatformDependent.javaVersion() >= 7 ? new WriteTimeoutException(true) : new WriteTimeoutException();

   public WriteTimeoutException() {
   }

   public WriteTimeoutException(String var1) {
      super(var1, false);
   }

   private WriteTimeoutException(boolean var1) {
      super(null, var1);
   }
}
