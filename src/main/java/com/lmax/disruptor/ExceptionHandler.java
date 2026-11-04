package com.lmax.disruptor;

public interface ExceptionHandler<T> {
   void handleEventException(Throwable var1, long var2, T var4);

   void handleOnStartException(Throwable var1);

   void handleOnShutdownException(Throwable var1);
}
