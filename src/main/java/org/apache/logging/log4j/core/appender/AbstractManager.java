package org.apache.logging.log4j.core.appender;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractManager implements AutoCloseable {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final Map<String, AbstractManager> MAP = new HashMap<>();
   private static final Lock LOCK = new ReentrantLock();
   protected int count;
   private final String name;
   private final LoggerContext loggerContext;

   protected AbstractManager(LoggerContext var1, String var2) {
      this.loggerContext = var1;
      this.name = var2;
      LOGGER.debug("Starting {} {}", this.getClass().getSimpleName(), var2);
   }

   @Override
   public void close() {
      this.stop(0L, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
   }

   public boolean stop(long var1, TimeUnit var3) {
      boolean var4 = true;
      LOCK.lock();

      try {
         this.count--;
         if (this.count <= 0) {
            MAP.remove(this.name);
            LOGGER.debug("Shutting down {} {}", this.getClass().getSimpleName(), this.getName());
            var4 = this.releaseSub(var1, var3);
            LOGGER.debug("Shut down {} {}, all resources released: {}", this.getClass().getSimpleName(), this.getName(), var4);
         }
      } finally {
         LOCK.unlock();
      }

      return var4;
   }

   public static <M extends AbstractManager, T> M getManager(String var0, ManagerFactory<M, T> var1, T var2) {
      LOCK.lock();

      AbstractManager var4;
      try {
         AbstractManager var3 = MAP.get(var0);
         if (var3 == null) {
            var3 = (AbstractManager)Objects.requireNonNull(var1, "factory").createManager(var0, var2);
            if (var3 == null) {
               throw new IllegalStateException("ManagerFactory [" + var1 + "] unable to create manager for [" + var0 + "] with data [" + var2 + "]");
            }

            MAP.put(var0, var3);
         } else {
            var3.updateData(var2);
         }

         var3.count++;
         var4 = var3;
      } finally {
         LOCK.unlock();
      }

      return (M)var4;
   }

   public void updateData(Object var1) {
   }

   public static boolean hasManager(String var0) {
      LOCK.lock();

      boolean var1;
      try {
         var1 = MAP.containsKey(var0);
      } finally {
         LOCK.unlock();
      }

      return var1;
   }

   protected static <M extends AbstractManager> M narrow(Class<M> var0, AbstractManager var1) {
      if (var0.isAssignableFrom(var1.getClass())) {
         return (M)var1;
      } else {
         throw new ConfigurationException("Configuration has multiple incompatible Appenders pointing to the same resource '" + var1.getName() + "'");
      }
   }

   protected static StatusLogger logger() {
      return StatusLogger.getLogger();
   }

   protected boolean releaseSub(long var1, TimeUnit var3) {
      return true;
   }

   protected int getCount() {
      return this.count;
   }

   public LoggerContext getLoggerContext() {
      return this.loggerContext;
   }

   @Deprecated
   public void release() {
      this.close();
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getContentFormat() {
      return new HashMap<>();
   }

   protected void log(Level var1, String var2, Throwable var3) {
      Message var4 = LOGGER.<MessageFactory>getMessageFactory().newMessage("{} {} {}: {}", this.getClass().getSimpleName(), this.getName(), var2, var3);
      LOGGER.log(var1, var4, var3);
   }

   protected void logDebug(String var1, Throwable var2) {
      this.log(Level.DEBUG, var1, var2);
   }

   protected void logError(String var1, Throwable var2) {
      this.log(Level.ERROR, var1, var2);
   }

   protected void logWarn(String var1, Throwable var2) {
      this.log(Level.WARN, var1, var2);
   }
}
