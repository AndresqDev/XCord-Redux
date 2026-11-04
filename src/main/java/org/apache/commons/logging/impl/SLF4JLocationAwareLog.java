package org.apache.commons.logging.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLocationAwareLog implements Serializable, Log {
   private static final long serialVersionUID = -2379157579039314822L;
   protected String name;
   private transient LocationAwareLogger logger;
   private static final String FQCN = SLF4JLocationAwareLog.class.getName();

   SLF4JLocationAwareLog(LocationAwareLogger var1) {
      this.logger = var1;
      this.name = var1.getName();
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
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
   public void trace(Object var1) {
      if (this.isTraceEnabled()) {
         this.logger.log(null, FQCN, 0, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void trace(Object var1, Throwable var2) {
      if (this.isTraceEnabled()) {
         this.logger.log(null, FQCN, 0, String.valueOf(var1), null, var2);
      }
   }

   @Override
   public void debug(Object var1) {
      if (this.isDebugEnabled()) {
         this.logger.log(null, FQCN, 10, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void debug(Object var1, Throwable var2) {
      if (this.isDebugEnabled()) {
         this.logger.log(null, FQCN, 10, String.valueOf(var1), null, var2);
      }
   }

   @Override
   public void info(Object var1) {
      if (this.isInfoEnabled()) {
         this.logger.log(null, FQCN, 20, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void info(Object var1, Throwable var2) {
      if (this.isInfoEnabled()) {
         this.logger.log(null, FQCN, 20, String.valueOf(var1), null, var2);
      }
   }

   @Override
   public void warn(Object var1) {
      if (this.isWarnEnabled()) {
         this.logger.log(null, FQCN, 30, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void warn(Object var1, Throwable var2) {
      if (this.isWarnEnabled()) {
         this.logger.log(null, FQCN, 30, String.valueOf(var1), null, var2);
      }
   }

   @Override
   public void error(Object var1) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void error(Object var1, Throwable var2) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(var1), null, var2);
      }
   }

   @Override
   public void fatal(Object var1) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(var1), null, null);
      }
   }

   @Override
   public void fatal(Object var1, Throwable var2) {
      if (this.isErrorEnabled()) {
         this.logger.log(null, FQCN, 40, String.valueOf(var1), null, var2);
      }
   }

   protected Object readResolve() throws ObjectStreamException {
      Logger var1 = LoggerFactory.getLogger(this.name);
      return new SLF4JLocationAwareLog((LocationAwareLogger)var1);
   }
}
