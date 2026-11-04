package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.ExceptionHandler;

abstract class AbstractAsyncExceptionHandler<T> implements ExceptionHandler<T> {
   public void handleEventException(Throwable var1, long var2, T var4) {
      try {
         System.err.print("AsyncLogger error handling event seq=");
         System.err.print(var2);
         System.err.print(", value='");

         try {
            System.err.print(var4);
         } catch (Throwable var6) {
            System.err.print("ERROR calling toString() on ");
            System.err.print(var4.getClass().getName());
            System.err.print(": ");
            System.err.print(var6.getClass().getName());
            System.err.print(": ");
            System.err.print(var6.getMessage());
         }

         System.err.print("': ");
         System.err.print(var1.getClass().getName());
         System.err.print(": ");
         System.err.println(var1.getMessage());
         var1.printStackTrace();
      } catch (Throwable var7) {
      }
   }

   public void handleOnStartException(Throwable var1) {
      System.err.println("AsyncLogger error starting:");
      var1.printStackTrace();
   }

   public void handleOnShutdownException(Throwable var1) {
      System.err.println("AsyncLogger error shutting down:");
      var1.printStackTrace();
   }
}
