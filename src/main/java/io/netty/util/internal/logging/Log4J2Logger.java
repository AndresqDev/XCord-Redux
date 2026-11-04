package io.netty.util.internal.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class Log4J2Logger extends ExtendedLoggerWrapper implements InternalLogger {
   private static final long serialVersionUID = 5485418394879791397L;
   private static final boolean VARARGS_ONLY = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         try {
            Logger.class.getMethod("debug", String.class, Object.class);
            return false;
         } catch (NoSuchMethodException var2) {
            return true;
         } catch (SecurityException var3) {
            return false;
         }
      }
   });

   Log4J2Logger(Logger var1) {
      super((ExtendedLogger)var1, var1.getName(), var1.getMessageFactory());
      if (VARARGS_ONLY) {
         throw new UnsupportedOperationException("Log4J2 version mismatch");
      }
   }

   @Override
   public String name() {
      return this.getName();
   }

   @Override
   public void trace(Throwable var1) {
      this.log(Level.TRACE, "Unexpected exception:", var1);
   }

   @Override
   public void debug(Throwable var1) {
      this.log(Level.DEBUG, "Unexpected exception:", var1);
   }

   @Override
   public void info(Throwable var1) {
      this.log(Level.INFO, "Unexpected exception:", var1);
   }

   @Override
   public void warn(Throwable var1) {
      this.log(Level.WARN, "Unexpected exception:", var1);
   }

   @Override
   public void error(Throwable var1) {
      this.log(Level.ERROR, "Unexpected exception:", var1);
   }

   @Override
   public boolean isEnabled(InternalLogLevel var1) {
      return this.isEnabled(toLevel(var1));
   }

   @Override
   public void log(InternalLogLevel var1, String var2) {
      this.log(toLevel(var1), var2);
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object var3) {
      this.log(toLevel(var1), var2, var3);
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object var3, Object var4) {
      this.log(toLevel(var1), var2, var3, var4);
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Object... var3) {
      this.log(toLevel(var1), var2, var3);
   }

   @Override
   public void log(InternalLogLevel var1, String var2, Throwable var3) {
      this.log(toLevel(var1), var2, var3);
   }

   @Override
   public void log(InternalLogLevel var1, Throwable var2) {
      this.log(toLevel(var1), "Unexpected exception:", var2);
   }

   private static Level toLevel(InternalLogLevel var0) {
      switch (var0) {
         case INFO:
            return Level.INFO;
         case DEBUG:
            return Level.DEBUG;
         case WARN:
            return Level.WARN;
         case ERROR:
            return Level.ERROR;
         case TRACE:
            return Level.TRACE;
         default:
            throw new Error();
      }
   }
}
