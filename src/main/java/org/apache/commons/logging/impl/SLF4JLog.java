package org.apache.commons.logging.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JLog implements Serializable, Log {
   private static final long serialVersionUID = 680728617011167209L;
   protected String name;
   private transient Logger logger;

   SLF4JLog(Logger var1) {
      this.logger = var1;
      this.name = var1.getName();
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   @Override
   public boolean isFatalEnabled() {
      return this.logger.isErrorEnabled();
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
   }

   @Override
   public void trace(Object var1) {
      this.logger.trace(String.valueOf(var1));
   }

   @Override
   public void trace(Object var1, Throwable var2) {
      this.logger.trace(String.valueOf(var1), var2);
   }

   @Override
   public void debug(Object var1) {
      this.logger.debug(String.valueOf(var1));
   }

   @Override
   public void debug(Object var1, Throwable var2) {
      this.logger.debug(String.valueOf(var1), var2);
   }

   @Override
   public void info(Object var1) {
      this.logger.info(String.valueOf(var1));
   }

   @Override
   public void info(Object var1, Throwable var2) {
      this.logger.info(String.valueOf(var1), var2);
   }

   @Override
   public void warn(Object var1) {
      this.logger.warn(String.valueOf(var1));
   }

   @Override
   public void warn(Object var1, Throwable var2) {
      this.logger.warn(String.valueOf(var1), var2);
   }

   @Override
   public void error(Object var1) {
      this.logger.error(String.valueOf(var1));
   }

   @Override
   public void error(Object var1, Throwable var2) {
      this.logger.error(String.valueOf(var1), var2);
   }

   @Override
   public void fatal(Object var1) {
      this.logger.error(String.valueOf(var1));
   }

   @Override
   public void fatal(Object var1, Throwable var2) {
      this.logger.error(String.valueOf(var1), var2);
   }

   protected Object readResolve() throws ObjectStreamException {
      Logger var1 = LoggerFactory.getLogger(this.name);
      return new SLF4JLog(var1);
   }
}
