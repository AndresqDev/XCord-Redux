package io.github.waterfallmc.waterfall.utils;

public class FastException extends RuntimeException {
   public FastException(String var1) {
      super(var1);
   }

   @Override
   public synchronized Throwable initCause(Throwable var1) {
      return this;
   }

   @Override
   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
