package org.slf4j.event;

import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

public class SubstituteLoggingEvent implements LoggingEvent {
   Level level;
   Marker marker;
   String loggerName;
   SubstituteLogger logger;
   String threadName;
   String message;
   Object[] argArray;
   long timeStamp;
   Throwable throwable;

   @Override
   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   @Override
   public Marker getMarker() {
      return this.marker;
   }

   public void setMarker(Marker var1) {
      this.marker = var1;
   }

   @Override
   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   public SubstituteLogger getLogger() {
      return this.logger;
   }

   public void setLogger(SubstituteLogger var1) {
      this.logger = var1;
   }

   @Override
   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   @Override
   public Object[] getArgumentArray() {
      return this.argArray;
   }

   public void setArgumentArray(Object[] var1) {
      this.argArray = var1;
   }

   @Override
   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   @Override
   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String var1) {
      this.threadName = var1;
   }

   @Override
   public Throwable getThrowable() {
      return this.throwable;
   }

   public void setThrowable(Throwable var1) {
      this.throwable = var1;
   }
}
