package net.md_5.bungee.protocol;

public class OverflowPacketException extends RuntimeException {
   public OverflowPacketException(String var1) {
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
