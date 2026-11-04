package io.netty.handler.codec.http;

import io.netty.handler.codec.TooLongFrameException;

public final class TooLongHttpLineException extends TooLongFrameException {
   private static final long serialVersionUID = 1614751125592211890L;

   public TooLongHttpLineException() {
   }

   public TooLongHttpLineException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public TooLongHttpLineException(String var1) {
      super(var1);
   }

   public TooLongHttpLineException(Throwable var1) {
      super(var1);
   }
}
