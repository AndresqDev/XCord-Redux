package com.lmax.disruptor;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class FatalExceptionHandler implements ExceptionHandler<Object> {
   private static final Logger LOGGER = Logger.getLogger(FatalExceptionHandler.class.getName());
   private final Logger logger;

   public FatalExceptionHandler() {
      this.logger = LOGGER;
   }

   public FatalExceptionHandler(Logger var1) {
      this.logger = var1;
   }

   @Override
   public void handleEventException(Throwable var1, long var2, Object var4) {
      this.logger.log(Level.SEVERE, "Exception processing: " + var2 + " " + var4, var1);
      throw new RuntimeException(var1);
   }

   @Override
   public void handleOnStartException(Throwable var1) {
      this.logger.log(Level.SEVERE, "Exception during onStart()", var1);
   }

   @Override
   public void handleOnShutdownException(Throwable var1) {
      this.logger.log(Level.SEVERE, "Exception during onShutdown()", var1);
   }
}
