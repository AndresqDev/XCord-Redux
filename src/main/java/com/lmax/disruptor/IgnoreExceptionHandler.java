package com.lmax.disruptor;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class IgnoreExceptionHandler implements ExceptionHandler<Object> {
   private static final Logger LOGGER = Logger.getLogger(IgnoreExceptionHandler.class.getName());
   private final Logger logger;

   public IgnoreExceptionHandler() {
      this.logger = LOGGER;
   }

   public IgnoreExceptionHandler(Logger var1) {
      this.logger = var1;
   }

   @Override
   public void handleEventException(Throwable var1, long var2, Object var4) {
      this.logger.log(Level.INFO, "Exception processing: " + var2 + " " + var4, var1);
   }

   @Override
   public void handleOnStartException(Throwable var1) {
      this.logger.log(Level.INFO, "Exception during onStart()", var1);
   }

   @Override
   public void handleOnShutdownException(Throwable var1) {
      this.logger.log(Level.INFO, "Exception during onShutdown()", var1);
   }
}
