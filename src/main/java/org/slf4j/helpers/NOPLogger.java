package org.slf4j.helpers;

public class NOPLogger extends MarkerIgnoringBase {
   private static final long serialVersionUID = -517220405410904473L;
   public static final NOPLogger NOP_LOGGER = new NOPLogger();

   protected NOPLogger() {
   }

   @Override
   public String getName() {
      return "NOP";
   }

   @Override
   public final boolean isTraceEnabled() {
      return false;
   }

   @Override
   public final void trace(String var1) {
   }

   @Override
   public final void trace(String var1, Object var2) {
   }

   @Override
   public final void trace(String var1, Object var2, Object var3) {
   }

   @Override
   public final void trace(String var1, Object... var2) {
   }

   @Override
   public final void trace(String var1, Throwable var2) {
   }

   @Override
   public final boolean isDebugEnabled() {
      return false;
   }

   @Override
   public final void debug(String var1) {
   }

   @Override
   public final void debug(String var1, Object var2) {
   }

   @Override
   public final void debug(String var1, Object var2, Object var3) {
   }

   @Override
   public final void debug(String var1, Object... var2) {
   }

   @Override
   public final void debug(String var1, Throwable var2) {
   }

   @Override
   public final boolean isInfoEnabled() {
      return false;
   }

   @Override
   public final void info(String var1) {
   }

   @Override
   public final void info(String var1, Object var2) {
   }

   @Override
   public final void info(String var1, Object var2, Object var3) {
   }

   @Override
   public final void info(String var1, Object... var2) {
   }

   @Override
   public final void info(String var1, Throwable var2) {
   }

   @Override
   public final boolean isWarnEnabled() {
      return false;
   }

   @Override
   public final void warn(String var1) {
   }

   @Override
   public final void warn(String var1, Object var2) {
   }

   @Override
   public final void warn(String var1, Object var2, Object var3) {
   }

   @Override
   public final void warn(String var1, Object... var2) {
   }

   @Override
   public final void warn(String var1, Throwable var2) {
   }

   @Override
   public final boolean isErrorEnabled() {
      return false;
   }

   @Override
   public final void error(String var1) {
   }

   @Override
   public final void error(String var1, Object var2) {
   }

   @Override
   public final void error(String var1, Object var2, Object var3) {
   }

   @Override
   public final void error(String var1, Object... var2) {
   }

   @Override
   public final void error(String var1, Throwable var2) {
   }
}
