package net.md_5.bungee.protocol;

public class BadPacketException extends RuntimeException {
   private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.bad-packet-traces");

   public BadPacketException(String var1) {
      super(var1);
   }

   public BadPacketException(String var1, Throwable var2) {
      super(var1, var2);
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
