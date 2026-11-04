package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;

public class NoOpLog implements Serializable, Log {
   private static final long serialVersionUID = 561423906191706148L;

   public NoOpLog() {
   }

   public NoOpLog(String var1) {
   }

   @Override
   public void trace(Object var1) {
   }

   @Override
   public void trace(Object var1, Throwable var2) {
   }

   @Override
   public void debug(Object var1) {
   }

   @Override
   public void debug(Object var1, Throwable var2) {
   }

   @Override
   public void info(Object var1) {
   }

   @Override
   public void info(Object var1, Throwable var2) {
   }

   @Override
   public void warn(Object var1) {
   }

   @Override
   public void warn(Object var1, Throwable var2) {
   }

   @Override
   public void error(Object var1) {
   }

   @Override
   public void error(Object var1, Throwable var2) {
   }

   @Override
   public void fatal(Object var1) {
   }

   @Override
   public void fatal(Object var1, Throwable var2) {
   }

   @Override
   public final boolean isDebugEnabled() {
      return false;
   }

   @Override
   public final boolean isErrorEnabled() {
      return false;
   }

   @Override
   public final boolean isFatalEnabled() {
      return false;
   }

   @Override
   public final boolean isInfoEnabled() {
      return false;
   }

   @Override
   public final boolean isTraceEnabled() {
      return false;
   }

   @Override
   public final boolean isWarnEnabled() {
      return false;
   }
}
