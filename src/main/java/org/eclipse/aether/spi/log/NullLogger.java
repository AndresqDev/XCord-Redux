package org.eclipse.aether.spi.log;

final class NullLogger implements Logger {
   @Override
   public boolean isDebugEnabled() {
      return false;
   }

   @Override
   public void debug(String var1) {
   }

   @Override
   public void debug(String var1, Throwable var2) {
   }

   @Override
   public boolean isWarnEnabled() {
      return false;
   }

   @Override
   public void warn(String var1) {
   }

   @Override
   public void warn(String var1, Throwable var2) {
   }
}
