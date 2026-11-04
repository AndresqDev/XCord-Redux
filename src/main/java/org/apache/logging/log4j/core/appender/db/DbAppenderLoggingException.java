package org.apache.logging.log4j.core.appender.db;

import org.apache.logging.log4j.core.appender.AppenderLoggingException;

public class DbAppenderLoggingException extends AppenderLoggingException {
   private static final long serialVersionUID = 1L;

   public DbAppenderLoggingException(String var1, Object... var2) {
      super(var1, var2);
   }

   public DbAppenderLoggingException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public DbAppenderLoggingException(Throwable var1, String var2, Object... var3) {
      super(var1, var2, var3);
   }
}
