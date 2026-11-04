package com.lmax.disruptor;

public final class AlertException extends Exception {
   public static final AlertException INSTANCE = new AlertException();

   private AlertException() {
   }

   @Override
   public Throwable fillInStackTrace() {
      return this;
   }
}
