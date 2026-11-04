package net.md_5.bungee.protocol;

import io.netty.handler.codec.DecoderException;

public class FastDecoderException extends DecoderException {
   public FastDecoderException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public FastDecoderException(String var1) {
      super(var1);
   }

   public Throwable initCause(Throwable var1) {
      return this;
   }

   public Throwable fillInStackTrace() {
      return this;
   }
}
