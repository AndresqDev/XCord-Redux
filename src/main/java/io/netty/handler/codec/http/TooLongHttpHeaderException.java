package io.netty.handler.codec.http;

import io.netty.handler.codec.TooLongFrameException;

public final class TooLongHttpHeaderException extends TooLongFrameException {
   private static final long serialVersionUID = -8295159138628369730L;

   public TooLongHttpHeaderException() {
   }

   public TooLongHttpHeaderException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public TooLongHttpHeaderException(String var1) {
      super(var1);
   }

   public TooLongHttpHeaderException(Throwable var1) {
      super(var1);
   }
}
