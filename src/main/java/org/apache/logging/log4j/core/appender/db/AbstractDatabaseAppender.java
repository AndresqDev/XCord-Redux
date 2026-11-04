package org.apache.logging.log4j.core.appender.db;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;

public abstract class AbstractDatabaseAppender<T extends AbstractDatabaseManager> extends AbstractAppender {
   public static final int DEFAULT_RECONNECT_INTERVAL_MILLIS = 5000;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock = this.lock.readLock();
   private final Lock writeLock = this.lock.writeLock();
   private T manager;

   @Deprecated
   protected AbstractDatabaseAppender(String var1, Filter var2, boolean var3, T var4) {
      super(var1, var2, null, var3, Property.EMPTY_ARRAY);
      this.manager = (T)var4;
   }

   protected AbstractDatabaseAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, Property[] var5, T var6) {
      super(var1, var2, var3, var4, var5);
      this.manager = (T)var6;
   }

   @Deprecated
   protected AbstractDatabaseAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, T var5) {
      super(var1, var2, var3, var4, Property.EMPTY_ARRAY);
      this.manager = (T)var5;
   }

   @Override
   public final void append(LogEvent var1) {
      this.readLock.lock();

      try {
         this.getManager().write(var1, this.toSerializable(var1));
      } catch (LoggingException var7) {
         LOGGER.error("Unable to write to database [{}] for appender [{}].", this.getManager().getName(), this.getName(), var7);
         throw var7;
      } catch (Exception var8) {
         LOGGER.error("Unable to write to database [{}] for appender [{}].", this.getManager().getName(), this.getName(), var8);
         throw new AppenderLoggingException("Unable to write to database in appender: " + var8.getMessage(), var8);
      } finally {
         this.readLock.unlock();
      }
   }

   @Override
   public final Layout<LogEvent> getLayout() {
      return null;
   }

   public final T getManager() {
      return this.manager;
   }

   protected final void replaceManager(T var1) {
      this.writeLock.lock();

      try {
         AbstractDatabaseManager var2 = this.getManager();
         if (!var1.isRunning()) {
            var1.startup();
         }

         this.manager = (T)var1;
         var2.close();
      } finally {
         this.writeLock.unlock();
      }
   }

   @Override
   public final void start() {
      if (this.getManager() == null) {
         LOGGER.error("No AbstractDatabaseManager set for the appender named [{}].", this.getName());
      }

      super.start();
      if (this.getManager() != null) {
         this.getManager().startup();
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      if (this.getManager() != null) {
         var4 &= this.getManager().stop(var1, var3);
      }

      this.setStopped();
      return var4;
   }

   public static class Builder<B extends AbstractDatabaseAppender.Builder<B>> extends AbstractAppender.Builder<B> {
   }
}
