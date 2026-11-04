package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.SubstituteLogger;

public class EventRecodingLogger implements Logger {
   String name;
   SubstituteLogger logger;
   Queue<SubstituteLoggingEvent> eventQueue;
   static final boolean RECORD_ALL_EVENTS = true;

   public EventRecodingLogger(SubstituteLogger var1, Queue<SubstituteLoggingEvent> var2) {
      this.logger = var1;
      this.name = var1.getName();
      this.eventQueue = var2;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean isTraceEnabled() {
      return true;
   }

   @Override
   public void trace(String var1) {
      this.recordEvent_0Args(Level.TRACE, null, var1, null);
   }

   @Override
   public void trace(String var1, Object var2) {
      this.recordEvent_1Args(Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Object var2, Object var3) {
      this.recordEvent2Args(Level.TRACE, null, var1, var2, var3);
   }

   @Override
   public void trace(String var1, Object... var2) {
      this.recordEventArgArray(Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Throwable var2) {
      this.recordEvent_0Args(Level.TRACE, null, var1, var2);
   }

   @Override
   public boolean isTraceEnabled(Marker var1) {
      return true;
   }

   @Override
   public void trace(Marker var1, String var2) {
      this.recordEvent_0Args(Level.TRACE, var1, var2, null);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3) {
      this.recordEvent_1Args(Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4) {
      this.recordEvent2Args(Level.TRACE, var1, var2, var3, var4);
   }

   @Override
   public void trace(Marker var1, String var2, Object... var3) {
      this.recordEventArgArray(Level.TRACE, var1, var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Throwable var3) {
      this.recordEvent_0Args(Level.TRACE, var1, var2, var3);
   }

   @Override
   public boolean isDebugEnabled() {
      return true;
   }

   @Override
   public void debug(String var1) {
      this.recordEvent_0Args(Level.DEBUG, null, var1, null);
   }

   @Override
   public void debug(String var1, Object var2) {
      this.recordEvent_1Args(Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Object var2, Object var3) {
      this.recordEvent2Args(Level.DEBUG, null, var1, var2, var3);
   }

   @Override
   public void debug(String var1, Object... var2) {
      this.recordEventArgArray(Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Throwable var2) {
      this.recordEvent_0Args(Level.DEBUG, null, var1, var2);
   }

   @Override
   public boolean isDebugEnabled(Marker var1) {
      return true;
   }

   @Override
   public void debug(Marker var1, String var2) {
      this.recordEvent_0Args(Level.DEBUG, var1, var2, null);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3) {
      this.recordEvent_1Args(Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4) {
      this.recordEvent2Args(Level.DEBUG, var1, var2, var3, var4);
   }

   @Override
   public void debug(Marker var1, String var2, Object... var3) {
      this.recordEventArgArray(Level.DEBUG, var1, var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Throwable var3) {
      this.recordEvent_0Args(Level.DEBUG, var1, var2, var3);
   }

   @Override
   public boolean isInfoEnabled() {
      return true;
   }

   @Override
   public void info(String var1) {
      this.recordEvent_0Args(Level.INFO, null, var1, null);
   }

   @Override
   public void info(String var1, Object var2) {
      this.recordEvent_1Args(Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Object var2, Object var3) {
      this.recordEvent2Args(Level.INFO, null, var1, var2, var3);
   }

   @Override
   public void info(String var1, Object... var2) {
      this.recordEventArgArray(Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Throwable var2) {
      this.recordEvent_0Args(Level.INFO, null, var1, var2);
   }

   @Override
   public boolean isInfoEnabled(Marker var1) {
      return true;
   }

   @Override
   public void info(Marker var1, String var2) {
      this.recordEvent_0Args(Level.INFO, var1, var2, null);
   }

   @Override
   public void info(Marker var1, String var2, Object var3) {
      this.recordEvent_1Args(Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4) {
      this.recordEvent2Args(Level.INFO, var1, var2, var3, var4);
   }

   @Override
   public void info(Marker var1, String var2, Object... var3) {
      this.recordEventArgArray(Level.INFO, var1, var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Throwable var3) {
      this.recordEvent_0Args(Level.INFO, var1, var2, var3);
   }

   @Override
   public boolean isWarnEnabled() {
      return true;
   }

   @Override
   public void warn(String var1) {
      this.recordEvent_0Args(Level.WARN, null, var1, null);
   }

   @Override
   public void warn(String var1, Object var2) {
      this.recordEvent_1Args(Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Object var2, Object var3) {
      this.recordEvent2Args(Level.WARN, null, var1, var2, var3);
   }

   @Override
   public void warn(String var1, Object... var2) {
      this.recordEventArgArray(Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Throwable var2) {
      this.recordEvent_0Args(Level.WARN, null, var1, var2);
   }

   @Override
   public boolean isWarnEnabled(Marker var1) {
      return true;
   }

   @Override
   public void warn(Marker var1, String var2) {
      this.recordEvent_0Args(Level.WARN, var1, var2, null);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3) {
      this.recordEvent_1Args(Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4) {
      this.recordEvent2Args(Level.WARN, var1, var2, var3, var4);
   }

   @Override
   public void warn(Marker var1, String var2, Object... var3) {
      this.recordEventArgArray(Level.WARN, var1, var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Throwable var3) {
      this.recordEvent_0Args(Level.WARN, var1, var2, var3);
   }

   @Override
   public boolean isErrorEnabled() {
      return true;
   }

   @Override
   public void error(String var1) {
      this.recordEvent_0Args(Level.ERROR, null, var1, null);
   }

   @Override
   public void error(String var1, Object var2) {
      this.recordEvent_1Args(Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Object var2, Object var3) {
      this.recordEvent2Args(Level.ERROR, null, var1, var2, var3);
   }

   @Override
   public void error(String var1, Object... var2) {
      this.recordEventArgArray(Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Throwable var2) {
      this.recordEvent_0Args(Level.ERROR, null, var1, var2);
   }

   @Override
   public boolean isErrorEnabled(Marker var1) {
      return true;
   }

   @Override
   public void error(Marker var1, String var2) {
      this.recordEvent_0Args(Level.ERROR, var1, var2, null);
   }

   @Override
   public void error(Marker var1, String var2, Object var3) {
      this.recordEvent_1Args(Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4) {
      this.recordEvent2Args(Level.ERROR, var1, var2, var3, var4);
   }

   @Override
   public void error(Marker var1, String var2, Object... var3) {
      this.recordEventArgArray(Level.ERROR, var1, var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Throwable var3) {
      this.recordEvent_0Args(Level.ERROR, var1, var2, var3);
   }

   private void recordEvent_0Args(Level var1, Marker var2, String var3, Throwable var4) {
      this.recordEvent(var1, var2, var3, null, var4);
   }

   private void recordEvent_1Args(Level var1, Marker var2, String var3, Object var4) {
      this.recordEvent(var1, var2, var3, new Object[]{var4}, null);
   }

   private void recordEvent2Args(Level var1, Marker var2, String var3, Object var4, Object var5) {
      if (var5 instanceof Throwable) {
         this.recordEvent(var1, var2, var3, new Object[]{var4}, (Throwable)var5);
      } else {
         this.recordEvent(var1, var2, var3, new Object[]{var4, var5}, null);
      }
   }

   private void recordEventArgArray(Level var1, Marker var2, String var3, Object[] var4) {
      Throwable var5 = MessageFormatter.getThrowableCandidate(var4);
      if (var5 != null) {
         Object[] var6 = MessageFormatter.trimmedCopy(var4);
         this.recordEvent(var1, var2, var3, var6, var5);
      } else {
         this.recordEvent(var1, var2, var3, var4, null);
      }
   }

   private void recordEvent(Level var1, Marker var2, String var3, Object[] var4, Throwable var5) {
      SubstituteLoggingEvent var6 = new SubstituteLoggingEvent();
      var6.setTimeStamp(System.currentTimeMillis());
      var6.setLevel(var1);
      var6.setLogger(this.logger);
      var6.setLoggerName(this.name);
      var6.setMarker(var2);
      var6.setMessage(var3);
      var6.setThreadName(Thread.currentThread().getName());
      var6.setArgumentArray(var4);
      var6.setThrowable(var5);
      this.eventQueue.add(var6);
   }
}
