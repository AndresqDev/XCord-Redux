package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.LoggingException;

public class AppenderLoggingException extends LoggingException {
   private static final long serialVersionUID = 6545990597472958303L;

   public AppenderLoggingException(String var1) {
      super(var1);
   }

   public AppenderLoggingException(String var1, Object... var2) {
      super(String.format(var1, var2));
   }

   public AppenderLoggingException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public AppenderLoggingException(Throwable var1) {
      super(var1);
   }

   public AppenderLoggingException(Throwable var1, String var2, Object... var3) {
      super(String.format(var2, var3), var1);
   }
}
