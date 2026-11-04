package io.netty.handler.codec.http;

import io.netty.handler.codec.TooLongFrameException;

public final class TooLongHttpContentException extends TooLongFrameException {
   private static final long serialVersionUID = 3238341182129476117L;

   public TooLongHttpContentException() {
   }

   public TooLongHttpContentException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public TooLongHttpContentException(String var1) {
      super(var1);
   }

   public TooLongHttpContentException(Throwable var1) {
      super(var1);
   }
}
