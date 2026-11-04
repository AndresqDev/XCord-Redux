package net.md_5.bungee.protocol;

import io.netty.handler.codec.CorruptedFrameException;

public class MutedCorruptedFrameException extends CorruptedFrameException {
   public static final MutedCorruptedFrameException INSTANCE = new MutedCorruptedFrameException();
   private static final long serialVersionUID = 3918052232492988408L;

   public MutedCorruptedFrameException() {
   }

   public MutedCorruptedFrameException(String var1) {
      super(var1);
   }

   public Throwable initCause(Throwable var1) {
      return this;
   }

   public Throwable fillInStackTrace() {
      return this;
   }
}
