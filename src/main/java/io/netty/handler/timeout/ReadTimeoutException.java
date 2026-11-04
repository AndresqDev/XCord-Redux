package io.netty.handler.timeout;

import io.netty.util.internal.PlatformDependent;

public final class ReadTimeoutException extends TimeoutException {
   private static final long serialVersionUID = 169287984113283421L;
   public static final ReadTimeoutException INSTANCE = PlatformDependent.javaVersion() >= 7 ? new ReadTimeoutException(true) : new ReadTimeoutException();

   public ReadTimeoutException() {
   }

   public ReadTimeoutException(String var1) {
      super(var1, false);
   }

   private ReadTimeoutException(boolean var1) {
      super(null, var1);
   }
}
