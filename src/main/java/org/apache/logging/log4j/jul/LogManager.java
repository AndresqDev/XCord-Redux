package org.apache.logging.log4j.jul;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class LogManager extends java.util.logging.LogManager {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final AbstractLoggerAdapter loggerAdapter;
   private final ThreadLocal<Set<String>> recursive = ThreadLocal.withInitial(HashSet::new);

   public LogManager() {
      AbstractLoggerAdapter var1 = null;
      String var2 = PropertiesUtil.getProperties().getStringProperty("log4j.jul.LoggerAdapter");
      if (var2 != null) {
         try {
            LOGGER.info("Trying to use LoggerAdaptor [{}] specified by Log4j property.", var2);
            var1 = LoaderUtil.newCheckedInstanceOf(var2, AbstractLoggerAdapter.class);
         } catch (Exception var7) {
            LOGGER.error("Specified LoggerAdapter [{}] is incompatible.", var2, var7);
         }
      }

      if (var1 == null) {
         String var3;
         try {
            LoaderUtil.loadClass("org.apache.logging.log4j.core.Logger");
            var3 = "org.apache.logging.log4j.jul.CoreLoggerAdapter";
         } catch (ClassNotFoundException var6) {
            var3 = "org.apache.logging.log4j.jul.ApiLoggerAdapter";
         }

         LOGGER.debug("Attempting to use {}", var3);

         try {
            var1 = LoaderUtil.newCheckedInstanceOf(var3, AbstractLoggerAdapter.class);
         } catch (Exception var5) {
            throw (LoggingException)LOGGER.throwing(new LoggingException(var5));
         }
      }

      this.loggerAdapter = var1;
      LOGGER.info("Registered Log4j as the java.util.logging.LogManager.");
   }

   @Override
   public boolean addLogger(java.util.logging.Logger var1) {
      return false;
   }

   @Override
   public java.util.logging.Logger getLogger(String var1) {
      LOGGER.trace("Call to LogManager.getLogger({})", var1);
      Set var2 = this.recursive.get();
      if (var2.add(var1)) {
         java.util.logging.Logger var3;
         try {
            var3 = this.loggerAdapter.getLogger(var1);
         } finally {
            var2.remove(var1);
         }

         return var3;
      } else {
         LOGGER.warn("Recursive call to getLogger for {} ignored.", var1);
         return new NoOpLogger(var1);
      }
   }

   @Override
   public Enumeration<String> getLoggerNames() {
      return Collections.enumeration(this.loggerAdapter.getLoggersInContext(this.loggerAdapter.getContext()).keySet());
   }
}
