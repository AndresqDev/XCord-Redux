package org.apache.logging.log4j.jul;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class WrappedLogger extends ExtendedLoggerWrapper {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = ApiLogger.class.getName();

   WrappedLogger(ExtendedLogger var1) {
      super(var1, var1.getName(), var1.getMessageFactory());
   }

   @Override
   public void log(Level var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, var1, null, var2, var3);
   }

   @Override
   public void log(Level var1, String var2) {
      this.logIfEnabled(FQCN, var1, null, var2);
   }

   @Override
   public void entry() {
      this.entry(FQCN, new Object[0]);
   }

   @Override
   public void entry(Object... var1) {
      this.entry(FQCN, var1);
   }

   @Override
   public void exit() {
      this.exit(FQCN, null);
   }

   @Override
   public <R> R exit(R var1) {
      return this.exit(FQCN, (R)var1);
   }

   @Override
   public <T extends Throwable> T throwing(T var1) {
      return this.throwing(FQCN, LevelTranslator.toLevel(java.util.logging.Level.FINER), (T)var1);
   }
}
