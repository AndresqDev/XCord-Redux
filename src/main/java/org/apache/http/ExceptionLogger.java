package org.apache.http;

public interface ExceptionLogger {
   ExceptionLogger NO_OP = new ExceptionLogger() {
      @Override
      public void log(Exception var1) {
      }
   };
   ExceptionLogger STD_ERR = new ExceptionLogger() {
      @Override
      public void log(Exception var1) {
         var1.printStackTrace();
      }
   };

   void log(Exception var1);
}
