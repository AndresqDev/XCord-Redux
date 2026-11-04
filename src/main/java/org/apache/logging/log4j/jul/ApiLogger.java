package org.apache.logging.log4j.jul;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class ApiLogger extends Logger {
   private final WrappedLogger logger;
   private static final String FQCN = ApiLogger.class.getName();

   ApiLogger(ExtendedLogger var1) {
      super(var1.getName(), null);
      Level var2 = LevelTranslator.toJavaLevel(var1.getLevel());
      AccessController.doPrivileged((PrivilegedAction)(() -> {
         access$001(this, var2);
         return null;
      }));
      this.logger = new WrappedLogger(var1);
   }

   @Override
   public void log(LogRecord var1) {
      if (!this.isFiltered(var1)) {
         org.apache.logging.log4j.Level var2 = LevelTranslator.toLevel(var1.getLevel());
         Object[] var3 = var1.getParameters();
         MessageFactory var4 = this.logger.getMessageFactory();
         Message var5 = var3 == null ? var4.newMessage(var1.getMessage()) : var4.newMessage(var1.getMessage(), var3);
         Throwable var6 = var1.getThrown();
         this.logger.logIfEnabled(FQCN, var2, null, var5, var6);
      }
   }

   boolean isFiltered(LogRecord var1) {
      Filter var2 = this.getFilter();
      return var2 != null && !var2.isLoggable(var1);
   }

   @Override
   public boolean isLoggable(Level var1) {
      return this.logger.isEnabled(LevelTranslator.toLevel(var1));
   }

   @Override
   public String getName() {
      return this.logger.getName();
   }

   @Override
   public void setLevel(Level var1) throws SecurityException {
      throw new UnsupportedOperationException("Cannot set level through log4j-api");
   }

   protected void doSetLevel(Level var1) throws SecurityException {
      super.setLevel(var1);
   }

   @Override
   public void setParent(Logger var1) {
      throw new UnsupportedOperationException("Cannot set parent logger");
   }

   @Override
   public void log(Level var1, String var2) {
      if (this.getFilter() == null) {
         this.logger.log(LevelTranslator.toLevel(var1), var2);
      } else {
         super.log(var1, var2);
      }
   }

   @Override
   public void log(Level var1, String var2, Object var3) {
      if (this.getFilter() == null) {
         this.logger.log(LevelTranslator.toLevel(var1), var2, var3);
      } else {
         super.log(var1, var2, var3);
      }
   }

   @Override
   public void log(Level var1, String var2, Object[] var3) {
      if (this.getFilter() == null) {
         this.logger.log(LevelTranslator.toLevel(var1), var2, var3);
      } else {
         super.log(var1, var2, var3);
      }
   }

   @Override
   public void log(Level var1, String var2, Throwable var3) {
      if (this.getFilter() == null) {
         this.logger.log(LevelTranslator.toLevel(var1), var2, var3);
      } else {
         super.log(var1, var2, var3);
      }
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4) {
      this.log(var1, var4);
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Object var5) {
      this.log(var1, var4, var5);
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Object[] var5) {
      this.log(var1, var4, var5);
   }

   @Override
   public void logp(Level var1, String var2, String var3, String var4, Throwable var5) {
      this.log(var1, var4, var5);
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5) {
      this.log(var1, var5);
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object var6) {
      this.log(var1, var5, var6);
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Object[] var6) {
      this.log(var1, var5, var6);
   }

   @Override
   public void logrb(Level var1, String var2, String var3, String var4, String var5, Throwable var6) {
      this.log(var1, var5, var6);
   }

   @Override
   public void entering(String var1, String var2) {
      this.logger.entry();
   }

   @Override
   public void entering(String var1, String var2, Object var3) {
      this.logger.entry(var3);
   }

   @Override
   public void entering(String var1, String var2, Object[] var3) {
      this.logger.entry(var3);
   }

   @Override
   public void exiting(String var1, String var2) {
      this.logger.exit();
   }

   @Override
   public void exiting(String var1, String var2, Object var3) {
      this.logger.exit(var3);
   }

   @Override
   public void throwing(String var1, String var2, Throwable var3) {
      this.logger.throwing(var3);
   }

   @Override
   public void severe(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.ERROR, null, var1);
      } else {
         super.severe(var1);
      }
   }

   @Override
   public void warning(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.WARN, null, var1);
      } else {
         super.warning(var1);
      }
   }

   @Override
   public void info(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.INFO, null, var1);
      } else {
         super.info(var1);
      }
   }

   @Override
   public void config(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, LevelTranslator.CONFIG, null, var1);
      } else {
         super.config(var1);
      }
   }

   @Override
   public void fine(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.DEBUG, null, var1);
      } else {
         super.fine(var1);
      }
   }

   @Override
   public void finer(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, org.apache.logging.log4j.Level.TRACE, null, var1);
      } else {
         super.finer(var1);
      }
   }

   @Override
   public void finest(String var1) {
      if (this.getFilter() == null) {
         this.logger.logIfEnabled(FQCN, LevelTranslator.FINEST, null, var1);
      } else {
         super.finest(var1);
      }
   }
}
