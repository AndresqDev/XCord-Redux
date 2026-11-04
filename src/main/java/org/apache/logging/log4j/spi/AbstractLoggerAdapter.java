package org.apache.logging.log4j.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.LoaderUtil;

public abstract class AbstractLoggerAdapter<L> implements LoggerAdapter<L>, LoggerContextShutdownAware {
   protected final Map<LoggerContext, ConcurrentMap<String, L>> registry = new ConcurrentHashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

   @Override
   public L getLogger(String var1) {
      LoggerContext var2 = this.getContext();
      ConcurrentMap var3 = this.getLoggersInContext(var2);
      Object var4 = var3.get(var1);
      if (var4 != null) {
         return (L)var4;
      } else {
         var3.putIfAbsent(var1, this.newLogger(var1, var2));
         return (L)var3.get(var1);
      }
   }

   @Override
   public void contextShutdown(LoggerContext var1) {
      this.registry.remove(var1);
   }

   public ConcurrentMap<String, L> getLoggersInContext(LoggerContext var1) {
      this.lock.readLock().lock();

      ConcurrentMap var2;
      try {
         var2 = this.registry.get(var1);
      } finally {
         this.lock.readLock().unlock();
      }

      if (var2 != null) {
         return var2;
      } else {
         this.lock.writeLock().lock();

         Object var3;
         try {
            Object var11 = this.registry.get(var1);
            if (var11 == null) {
               var11 = new ConcurrentHashMap();
               this.registry.put(var1, (ConcurrentMap<String, L>)var11);
               if (var1 instanceof LoggerContextShutdownEnabled) {
                  ((LoggerContextShutdownEnabled)var1).addShutdownListener(this);
               }
            }

            var3 = var11;
         } finally {
            this.lock.writeLock().unlock();
         }

         return (ConcurrentMap<String, L>)var3;
      }
   }

   public Set<LoggerContext> getLoggerContexts() {
      return new HashSet<>(this.registry.keySet());
   }

   protected abstract L newLogger(String var1, LoggerContext var2);

   protected abstract LoggerContext getContext();

   protected LoggerContext getContext(Class<?> var1) {
      ClassLoader var2 = null;
      if (var1 != null) {
         var2 = var1.getClassLoader();
      }

      if (var2 == null) {
         var2 = LoaderUtil.getThreadContextClassLoader();
      }

      return LogManager.getContext(var2, false);
   }

   @Override
   public void close() {
      this.lock.writeLock().lock();

      try {
         this.registry.clear();
      } finally {
         this.lock.writeLock().unlock();
      }
   }
}
