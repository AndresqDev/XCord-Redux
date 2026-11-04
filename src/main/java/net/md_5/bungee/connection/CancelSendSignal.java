package net.md_5.bungee.connection;

public class CancelSendSignal extends Error {
   public static final CancelSendSignal INSTANCE = new CancelSendSignal();

   @Override
   public Throwable initCause(Throwable var1) {
      return this;
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }

   private CancelSendSignal() {
   }
}
