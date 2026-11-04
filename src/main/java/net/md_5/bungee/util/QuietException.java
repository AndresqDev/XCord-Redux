package net.md_5.bungee.util;

public class QuietException extends RuntimeException {
   public QuietException(String var1) {
      super(var1);
   }

   @Override
   public Throwable initCause(Throwable var1) {
      return this;
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
