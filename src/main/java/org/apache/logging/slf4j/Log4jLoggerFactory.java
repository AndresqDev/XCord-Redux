package org.apache.logging.slf4j;

import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Log4jLoggerFactory extends AbstractLoggerAdapter<Logger> implements ILoggerFactory {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final String SLF4J_PACKAGE = "org.slf4j";
   private static final String TO_SLF4J_CONTEXT = "org.apache.logging.slf4j.SLF4JLoggerContext";
   private static final Predicate<Class<?>> CALLER_PREDICATE = var0 -> !AbstractLoggerAdapter.class.equals(var0) && !var0.getName().startsWith("org.slf4j");

   protected Logger newLogger(String var1, LoggerContext var2) {
      String var3 = "ROOT".equals(var1) ? "" : var1;
      return new Log4jLogger(this.validateContext(var2).getLogger(var3), var1);
   }

   @Override
   protected LoggerContext getContext() {
      Class var1 = LogManager.getFactory().isClassLoaderDependent() ? StackLocatorUtil.getCallerClass(Log4jLoggerFactory.class, CALLER_PREDICATE) : null;
      LOGGER.trace("Log4jLoggerFactory.getContext() found anchor {}", var1);
      return var1 == null ? LogManager.getContext(false) : this.getContext(var1);
   }

   private LoggerContext validateContext(LoggerContext var1) {
      if ("org.apache.logging.slf4j.SLF4JLoggerContext".equals(var1.getClass().getName())) {
         throw new LoggingException("log4j-slf4j-impl cannot be present with log4j-to-slf4j");
      } else {
         return var1;
      }
   }
}
