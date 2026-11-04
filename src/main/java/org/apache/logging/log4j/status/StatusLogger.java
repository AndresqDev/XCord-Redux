package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public final class StatusLogger extends AbstractLogger {
   public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";
   public static final String DEFAULT_STATUS_LISTENER_LEVEL = "log4j2.StatusLogger.level";
   public static final String STATUS_DATE_FORMAT = "log4j2.StatusLogger.DateFormat";
   private static final long serialVersionUID = 2L;
   private static final String NOT_AVAIL = "?";
   private static final PropertiesUtil PROPS = new PropertiesUtil("log4j2.StatusLogger.properties");
   private static final int MAX_ENTRIES = PROPS.getIntegerProperty("log4j2.status.entries", 200);
   private static final String DEFAULT_STATUS_LEVEL = PROPS.getStringProperty("log4j2.StatusLogger.level");
   private static final StatusLogger STATUS_LOGGER = new StatusLogger(StatusLogger.class.getName(), ParameterizedNoReferenceMessageFactory.INSTANCE);
   private final SimpleLogger logger;
   private final Collection<StatusListener> listeners = new CopyOnWriteArrayList<>();
   private final ReadWriteLock listenersLock = new ReentrantReadWriteLock();
   private final Queue<StatusData> messages = new StatusLogger.BoundedQueue<>(MAX_ENTRIES);
   private final Lock msgLock = new ReentrantLock();
   private int listenersLevel;

   private StatusLogger(String var1, MessageFactory var2) {
      super(var1, var2);
      String var3 = PROPS.getStringProperty("log4j2.StatusLogger.DateFormat", "");
      boolean var4 = !Strings.isEmpty(var3);
      this.logger = new SimpleLogger("StatusLogger", Level.ERROR, false, true, var4, false, var3, var2, PROPS, System.err);
      this.listenersLevel = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
      if (this.isDebugPropertyEnabled()) {
         this.logger.setLevel(Level.TRACE);
      }
   }

   private boolean isDebugPropertyEnabled() {
      return PropertiesUtil.getProperties().getBooleanProperty("log4j2.debug", false, true);
   }

   public static StatusLogger getLogger() {
      return STATUS_LOGGER;
   }

   public void setLevel(Level var1) {
      this.logger.setLevel(var1);
   }

   public void registerListener(StatusListener var1) {
      this.listenersLock.writeLock().lock();

      try {
         this.listeners.add(var1);
         Level var2 = var1.getStatusLevel();
         if (this.listenersLevel < var2.intLevel()) {
            this.listenersLevel = var2.intLevel();
         }
      } finally {
         this.listenersLock.writeLock().unlock();
      }
   }

   public void removeListener(StatusListener var1) {
      closeSilently(var1);
      this.listenersLock.writeLock().lock();

      try {
         this.listeners.remove(var1);
         int var2 = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();

         for (StatusListener var4 : this.listeners) {
            int var5 = var4.getStatusLevel().intLevel();
            if (var2 < var5) {
               var2 = var5;
            }
         }

         this.listenersLevel = var2;
      } finally {
         this.listenersLock.writeLock().unlock();
      }
   }

   public void updateListenerLevel(Level var1) {
      if (var1.intLevel() > this.listenersLevel) {
         this.listenersLevel = var1.intLevel();
      }
   }

   public Iterable<StatusListener> getListeners() {
      return this.listeners;
   }

   public void reset() {
      this.listenersLock.writeLock().lock();

      try {
         for (StatusListener var2 : this.listeners) {
            closeSilently(var2);
         }
      } finally {
         this.listeners.clear();
         this.listenersLock.writeLock().unlock();
         this.clear();
      }
   }

   private static void closeSilently(Closeable var0) {
      try {
         var0.close();
      } catch (IOException var2) {
      }
   }

   public List<StatusData> getStatusData() {
      this.msgLock.lock();

      ArrayList var1;
      try {
         var1 = new ArrayList<>(this.messages);
      } finally {
         this.msgLock.unlock();
      }

      return var1;
   }

   public void clear() {
      this.msgLock.lock();

      try {
         this.messages.clear();
      } finally {
         this.msgLock.unlock();
      }
   }

   @Override
   public Level getLevel() {
      return this.logger.getLevel();
   }

   @Override
   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      StackTraceElement var6 = null;
      if (var1 != null) {
         var6 = this.getStackTraceElement(var1, Thread.currentThread().getStackTrace());
      }

      StatusData var7 = new StatusData(var6, var2, var4, var5, null);
      this.msgLock.lock();

      try {
         this.messages.add(var7);
      } finally {
         this.msgLock.unlock();
      }

      if (!this.isDebugPropertyEnabled() && this.listeners.size() > 0) {
         for (StatusListener var9 : this.listeners) {
            if (var7.getLevel().isMoreSpecificThan(var9.getStatusLevel())) {
               var9.log(var7);
            }
         }
      } else {
         this.logger.logMessage(var1, var2, var3, var4, var5);
      }
   }

   private StackTraceElement getStackTraceElement(String var1, StackTraceElement[] var2) {
      if (var1 == null) {
         return null;
      } else {
         boolean var3 = false;

         for (StackTraceElement var7 : var2) {
            String var8 = var7.getClassName();
            if (var3 && !var1.equals(var8)) {
               return var7;
            }

            if (var1.equals(var8)) {
               var3 = true;
            } else if ("?".equals(var8)) {
               break;
            }
         }

         return null;
      }
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object... var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(
      Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(
      Level var1,
      Marker var2,
      String var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(
      Level var1,
      Marker var2,
      String var3,
      Object var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4) {
      return this.isEnabled(var1, var2);
   }

   @Override
   public boolean isEnabled(Level var1, Marker var2) {
      if (this.isDebugPropertyEnabled()) {
         return true;
      } else {
         return this.listeners.size() > 0 ? this.listenersLevel >= var1.intLevel() : this.logger.isEnabled(var1, var2);
      }
   }

   private class BoundedQueue<E> extends ConcurrentLinkedQueue<E> {
      private static final long serialVersionUID = -3945953719763255337L;
      private final int size;

      BoundedQueue(int var2) {
         this.size = var2;
      }

      @Override
      public boolean add(E var1) {
         super.add((E)var1);

         while (StatusLogger.this.messages.size() > this.size) {
            StatusLogger.this.messages.poll();
         }

         return this.size > 0;
      }
   }
}
