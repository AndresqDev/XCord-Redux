package org.apache.logging.log4j.core.appender.nosql;

import java.io.Serializable;
import java.util.Date;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

public final class NoSqlDatabaseManager<W> extends AbstractDatabaseManager {
   private static final NoSqlDatabaseManager.NoSQLDatabaseManagerFactory FACTORY = new NoSqlDatabaseManager.NoSQLDatabaseManagerFactory();
   private final NoSqlProvider<NoSqlConnection<W, ? extends NoSqlObject<W>>> provider;
   private NoSqlConnection<W, ? extends NoSqlObject<W>> connection;

   private NoSqlDatabaseManager(String var1, int var2, NoSqlProvider<NoSqlConnection<W, ? extends NoSqlObject<W>>> var3) {
      super(var1, var2);
      this.provider = var3;
   }

   @Override
   protected void startupInternal() {
   }

   @Override
   protected boolean shutdownInternal() {
      return Closer.closeSilently(this.connection);
   }

   @Override
   protected void connectAndStart() {
      try {
         this.connection = this.provider.getConnection();
      } catch (Exception var2) {
         throw new AppenderLoggingException("Failed to get connection from NoSQL connection provider.", var2);
      }
   }

   @Override
   protected void writeInternal(LogEvent var1, Serializable var2) {
      if (this.isRunning() && this.connection != null && !this.connection.isClosed()) {
         NoSqlObject var3 = this.connection.createObject();
         if (var2 instanceof MapMessage) {
            this.setFields((MapMessage<?, ?>)var2, var3);
         } else {
            this.setFields(var1, var3);
         }

         this.connection.insertObject(var3);
      } else {
         throw new AppenderLoggingException("Cannot write logging event; NoSQL manager not connected to the database.");
      }
   }

   private void setFields(MapMessage<?, ?> var1, NoSqlObject<W> var2) {
      var1.forEach((var1x, var2x) -> var2.set(var1x, var2x));
   }

   private void setFields(LogEvent var1, NoSqlObject<W> var2) {
      var2.set("level", var1.getLevel());
      var2.set("loggerName", var1.getLoggerName());
      var2.set("message", var1.getMessage() == null ? null : var1.getMessage().getFormattedMessage());
      StackTraceElement var3 = var1.getSource();
      if (var3 == null) {
         var2.set("source", null);
      } else {
         var2.set("source", this.convertStackTraceElement(var3));
      }

      Marker var4 = var1.getMarker();
      if (var4 == null) {
         var2.set("marker", null);
      } else {
         var2.set("marker", this.buildMarkerEntity(var4));
      }

      var2.set("threadId", var1.getThreadId());
      var2.set("threadName", var1.getThreadName());
      var2.set("threadPriority", var1.getThreadPriority());
      var2.set("millis", var1.getTimeMillis());
      var2.set("date", new Date(var1.getTimeMillis()));
      Throwable var5 = var1.getThrown();
      if (var5 == null) {
         var2.set("thrown", null);
      } else {
         NoSqlObject var6 = this.connection.createObject();
         NoSqlObject var7 = var6;
         var6.set("type", var5.getClass().getName());
         var6.set("message", var5.getMessage());
         var6.set("stackTrace", this.convertStackTrace(var5.getStackTrace()));

         while (var5.getCause() != null) {
            var5 = var5.getCause();
            NoSqlObject var8 = this.connection.createObject();
            var8.set("type", var5.getClass().getName());
            var8.set("message", var5.getMessage());
            var8.set("stackTrace", this.convertStackTrace(var5.getStackTrace()));
            var7.set("cause", var8);
            var7 = var8;
         }

         var2.set("thrown", var6);
      }

      ReadOnlyStringMap var9 = var1.getContextData();
      if (var9 == null) {
         var2.set("contextMap", null);
      } else {
         NoSqlObject var10 = this.connection.createObject();
         var9.forEach((var1x, var2x) -> var10.set(var1x, var2x));
         var2.set("contextMap", var10);
      }

      ThreadContext.ContextStack var11 = var1.getContextStack();
      if (var11 == null) {
         var2.set("contextStack", null);
      } else {
         var2.set("contextStack", var11.asList().toArray());
      }
   }

   private NoSqlObject<W> buildMarkerEntity(Marker var1) {
      NoSqlObject var2 = this.connection.createObject();
      var2.set("name", var1.getName());
      Marker[] var3 = var1.getParents();
      if (var3 != null) {
         NoSqlObject[] var4 = new NoSqlObject[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = this.buildMarkerEntity(var3[var5]);
         }

         var2.set("parents", var4);
      }

      return var2;
   }

   @Override
   protected boolean commitAndClose() {
      return true;
   }

   private NoSqlObject<W>[] convertStackTrace(StackTraceElement[] var1) {
      NoSqlObject[] var2 = this.connection.createList(var1.length);

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = this.convertStackTraceElement(var1[var3]);
      }

      return var2;
   }

   private NoSqlObject<W> convertStackTraceElement(StackTraceElement var1) {
      NoSqlObject var2 = this.connection.createObject();
      var2.set("className", var1.getClassName());
      var2.set("methodName", var1.getMethodName());
      var2.set("fileName", var1.getFileName());
      var2.set("lineNumber", var1.getLineNumber());
      return var2;
   }

   public static NoSqlDatabaseManager<?> getNoSqlDatabaseManager(String var0, int var1, NoSqlProvider<?> var2) {
      return AbstractDatabaseManager.getManager(var0, new NoSqlDatabaseManager.FactoryData(var1, var2), FACTORY);
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final NoSqlProvider<?> provider;

      protected FactoryData(int var1, NoSqlProvider<?> var2) {
         super(var1, null);
         this.provider = var2;
      }
   }

   private static final class NoSQLDatabaseManagerFactory implements ManagerFactory<NoSqlDatabaseManager<?>, NoSqlDatabaseManager.FactoryData> {
      private NoSQLDatabaseManagerFactory() {
      }

      public NoSqlDatabaseManager<?> createManager(String var1, NoSqlDatabaseManager.FactoryData var2) {
         return new NoSqlDatabaseManager(var1, var2.getBufferSize(), var2.provider);
      }
   }
}
