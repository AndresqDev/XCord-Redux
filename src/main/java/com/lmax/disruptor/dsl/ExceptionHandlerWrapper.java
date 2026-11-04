package com.lmax.disruptor.dsl;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.FatalExceptionHandler;

public class ExceptionHandlerWrapper<T> implements ExceptionHandler<T> {
   private ExceptionHandler<? super T> delegate = new FatalExceptionHandler();

   public void switchTo(ExceptionHandler<? super T> var1) {
      this.delegate = var1;
   }

   @Override
   public void handleEventException(Throwable var1, long var2, T var4) {
      this.delegate.handleEventException(var1, var2, (T)var4);
   }

   @Override
   public void handleOnStartException(Throwable var1) {
      this.delegate.handleOnStartException(var1);
   }

   @Override
   public void handleOnShutdownException(Throwable var1) {
      this.delegate.handleOnShutdownException(var1);
   }
}
