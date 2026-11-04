package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class MarkerIgnoringBase extends NamedLoggerBase implements Logger {
   private static final long serialVersionUID = 9044267456635152283L;

   @Override
   public boolean isTraceEnabled(Marker var1) {
      return this.isTraceEnabled();
   }

   @Override
   public void trace(Marker var1, String var2) {
      this.trace(var2);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3) {
      this.trace(var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4) {
      this.trace(var2, var3, var4);
   }

   @Override
   public void trace(Marker var1, String var2, Object... var3) {
      this.trace(var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Throwable var3) {
      this.trace(var2, var3);
   }

   @Override
   public boolean isDebugEnabled(Marker var1) {
      return this.isDebugEnabled();
   }

   @Override
   public void debug(Marker var1, String var2) {
      this.debug(var2);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3) {
      this.debug(var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4) {
      this.debug(var2, var3, var4);
   }

   @Override
   public void debug(Marker var1, String var2, Object... var3) {
      this.debug(var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Throwable var3) {
      this.debug(var2, var3);
   }

   @Override
   public boolean isInfoEnabled(Marker var1) {
      return this.isInfoEnabled();
   }

   @Override
   public void info(Marker var1, String var2) {
      this.info(var2);
   }

   @Override
   public void info(Marker var1, String var2, Object var3) {
      this.info(var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4) {
      this.info(var2, var3, var4);
   }

   @Override
   public void info(Marker var1, String var2, Object... var3) {
      this.info(var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Throwable var3) {
      this.info(var2, var3);
   }

   @Override
   public boolean isWarnEnabled(Marker var1) {
      return this.isWarnEnabled();
   }

   @Override
   public void warn(Marker var1, String var2) {
      this.warn(var2);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3) {
      this.warn(var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4) {
      this.warn(var2, var3, var4);
   }

   @Override
   public void warn(Marker var1, String var2, Object... var3) {
      this.warn(var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Throwable var3) {
      this.warn(var2, var3);
   }

   @Override
   public boolean isErrorEnabled(Marker var1) {
      return this.isErrorEnabled();
   }

   @Override
   public void error(Marker var1, String var2) {
      this.error(var2);
   }

   @Override
   public void error(Marker var1, String var2, Object var3) {
      this.error(var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4) {
      this.error(var2, var3, var4);
   }

   @Override
   public void error(Marker var1, String var2, Object... var3) {
      this.error(var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Throwable var3) {
      this.error(var2, var3);
   }

   @Override
   public String toString() {
      return this.getClass().getName() + "(" + this.getName() + ")";
   }
}
