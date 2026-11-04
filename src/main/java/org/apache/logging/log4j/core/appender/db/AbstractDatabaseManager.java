package org.apache.logging.log4j.core.appender.db;

import java.io.Flushable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;

public abstract class AbstractDatabaseManager extends AbstractManager implements Flushable {
   private final ArrayList<LogEvent> buffer;
   private final int bufferSize;
   private final Layout<? extends Serializable> layout;
   private boolean running;

   protected static <M extends AbstractDatabaseManager, T extends AbstractDatabaseManager.AbstractFactoryData> M getManager(
      String var0, T var1, ManagerFactory<M, T> var2
   ) {
      return AbstractManager.getManager(var0, var2, var1);
   }

   protected AbstractDatabaseManager(String var1, int var2) {
      this(var1, var2, null);
   }

   protected AbstractDatabaseManager(String var1, int var2, Layout<? extends Serializable> var3) {
      super(null, var1);
      this.bufferSize = var2;
      this.buffer = new ArrayList<>(var2 + 1);
      this.layout = var3;
   }

   protected void buffer(LogEvent var1) {
      this.buffer.add(var1.toImmutable());
      if (this.buffer.size() >= this.bufferSize || var1.isEndOfBatch()) {
         this.flush();
      }
   }

   protected abstract boolean commitAndClose();

   protected abstract void connectAndStart();

   @Override
   public final synchronized void flush() {
      if (this.isRunning() && this.isBuffered()) {
         this.connectAndStart();

         try {
            for (LogEvent var2 : this.buffer) {
               this.writeInternal(var2, this.layout != null ? this.layout.toSerializable(var2) : null);
            }
         } finally {
            this.commitAndClose();
            this.buffer.clear();
         }
      }
   }

   protected boolean isBuffered() {
      return this.bufferSize > 0;
   }

   public final boolean isRunning() {
      return this.running;
   }

   @Override
   public final boolean releaseSub(long var1, TimeUnit var3) {
      return this.shutdown();
   }

   public final synchronized boolean shutdown() {
      boolean var1 = true;
      this.flush();
      if (this.isRunning()) {
         try {
            var1 &= this.shutdownInternal();
         } catch (Exception var6) {
            this.logWarn("Caught exception while performing database shutdown operations", var6);
            var1 = false;
         } finally {
            this.running = false;
         }
      }

      return var1;
   }

   protected abstract boolean shutdownInternal() throws Exception;

   public final synchronized void startup() {
      if (!this.isRunning()) {
         try {
            this.startupInternal();
            this.running = true;
         } catch (Exception var2) {
            this.logError("Could not perform database startup operations", var2);
         }
      }
   }

   protected abstract void startupInternal() throws Exception;

   @Override
   public final String toString() {
      return this.getName();
   }

   @Deprecated
   public final synchronized void write(LogEvent var1) {
      this.write(var1, null);
   }

   public final synchronized void write(LogEvent var1, Serializable var2) {
      if (this.isBuffered()) {
         this.buffer(var1);
      } else {
         this.writeThrough(var1, var2);
      }
   }

   @Deprecated
   protected void writeInternal(LogEvent var1) {
      this.writeInternal(var1, null);
   }

   protected abstract void writeInternal(LogEvent var1, Serializable var2);

   protected void writeThrough(LogEvent var1, Serializable var2) {
      this.connectAndStart();

      try {
         this.writeInternal(var1, var2);
      } finally {
         this.commitAndClose();
      }
   }

   protected abstract static class AbstractFactoryData {
      private final int bufferSize;
      private final Layout<? extends Serializable> layout;

      protected AbstractFactoryData(int var1, Layout<? extends Serializable> var2) {
         this.bufferSize = var1;
         this.layout = var2;
      }

      public int getBufferSize() {
         return this.bufferSize;
      }

      public Layout<? extends Serializable> getLayout() {
         return this.layout;
      }
   }
}
