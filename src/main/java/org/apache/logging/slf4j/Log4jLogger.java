package org.apache.logging.slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.impl.StaticMarkerBinder;
import org.slf4j.spi.LocationAwareLogger;

public class Log4jLogger implements Serializable, LocationAwareLogger {
   public static final String FQCN = Log4jLogger.class.getName();
   private static final long serialVersionUID = 7869000638091304316L;
   private static final Marker EVENT_MARKER = MarkerFactory.getMarker("EVENT");
   private static final EventDataConverter CONVERTER = createConverter();
   private final boolean eventLogger;
   private transient ExtendedLogger logger;
   private final String name;

   public Log4jLogger(ExtendedLogger var1, String var2) {
      this.logger = var1;
      this.eventLogger = "EventLogger".equals(var2);
      this.name = var2;
   }

   @Override
   public void trace(String var1) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, var1);
   }

   @Override
   public void trace(String var1, Object var2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Object var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, var1, var2, var3);
   }

   @Override
   public void trace(String var1, Object... var2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public void trace(String var1, Throwable var2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, null, var1, var2);
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isEnabled(Level.TRACE, null, null);
   }

   @Override
   public boolean isTraceEnabled(Marker var1) {
      return this.logger.isEnabled(Level.TRACE, getMarker(var1), null);
   }

   @Override
   public void trace(Marker var1, String var2) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, getMarker(var1), var2);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, getMarker(var1), var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Object var3, Object var4) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, getMarker(var1), var2, var3, var4);
   }

   @Override
   public void trace(Marker var1, String var2, Object... var3) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, getMarker(var1), var2, var3);
   }

   @Override
   public void trace(Marker var1, String var2, Throwable var3) {
      this.logger.logIfEnabled(FQCN, Level.TRACE, getMarker(var1), var2, var3);
   }

   @Override
   public void debug(String var1) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, var1);
   }

   @Override
   public void debug(String var1, Object var2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Object var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2, var3);
   }

   @Override
   public void debug(String var1, Object... var2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public void debug(String var1, Throwable var2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, null, var1, var2);
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isEnabled(Level.DEBUG, null, null);
   }

   @Override
   public boolean isDebugEnabled(Marker var1) {
      return this.logger.isEnabled(Level.DEBUG, getMarker(var1), null);
   }

   @Override
   public void debug(Marker var1, String var2) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(var1), var2);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(var1), var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Object var3, Object var4) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(var1), var2, var3, var4);
   }

   @Override
   public void debug(Marker var1, String var2, Object... var3) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(var1), var2, var3);
   }

   @Override
   public void debug(Marker var1, String var2, Throwable var3) {
      this.logger.logIfEnabled(FQCN, Level.DEBUG, getMarker(var1), var2, var3);
   }

   @Override
   public void info(String var1) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, var1);
   }

   @Override
   public void info(String var1, Object var2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Object var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, var1, var2, var3);
   }

   @Override
   public void info(String var1, Object... var2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public void info(String var1, Throwable var2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, null, var1, var2);
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isEnabled(Level.INFO, null, null);
   }

   @Override
   public boolean isInfoEnabled(Marker var1) {
      return this.logger.isEnabled(Level.INFO, getMarker(var1), null);
   }

   @Override
   public void info(Marker var1, String var2) {
      this.logger.logIfEnabled(FQCN, Level.INFO, getMarker(var1), var2);
   }

   @Override
   public void info(Marker var1, String var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.INFO, getMarker(var1), var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Object var3, Object var4) {
      this.logger.logIfEnabled(FQCN, Level.INFO, getMarker(var1), var2, var3, var4);
   }

   @Override
   public void info(Marker var1, String var2, Object... var3) {
      this.logger.logIfEnabled(FQCN, Level.INFO, getMarker(var1), var2, var3);
   }

   @Override
   public void info(Marker var1, String var2, Throwable var3) {
      this.logger.logIfEnabled(FQCN, Level.INFO, getMarker(var1), var2, var3);
   }

   @Override
   public void warn(String var1) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, var1);
   }

   @Override
   public void warn(String var1, Object var2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Object var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, var1, var2, var3);
   }

   @Override
   public void warn(String var1, Object... var2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public void warn(String var1, Throwable var2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, null, var1, var2);
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isEnabled(Level.WARN, null, null);
   }

   @Override
   public boolean isWarnEnabled(Marker var1) {
      return this.logger.isEnabled(Level.WARN, getMarker(var1), null);
   }

   @Override
   public void warn(Marker var1, String var2) {
      this.logger.logIfEnabled(FQCN, Level.WARN, getMarker(var1), var2);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.WARN, getMarker(var1), var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Object var3, Object var4) {
      this.logger.logIfEnabled(FQCN, Level.WARN, getMarker(var1), var2, var3, var4);
   }

   @Override
   public void warn(Marker var1, String var2, Object... var3) {
      this.logger.logIfEnabled(FQCN, Level.WARN, getMarker(var1), var2, var3);
   }

   @Override
   public void warn(Marker var1, String var2, Throwable var3) {
      this.logger.logIfEnabled(FQCN, Level.WARN, getMarker(var1), var2, var3);
   }

   @Override
   public void error(String var1) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, var1);
   }

   @Override
   public void error(String var1, Object var2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Object var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, var1, var2, var3);
   }

   @Override
   public void error(String var1, Object... var2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public void error(String var1, Throwable var2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, null, var1, var2);
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isEnabled(Level.ERROR, null, null);
   }

   @Override
   public boolean isErrorEnabled(Marker var1) {
      return this.logger.isEnabled(Level.ERROR, getMarker(var1), null);
   }

   @Override
   public void error(Marker var1, String var2) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, getMarker(var1), var2);
   }

   @Override
   public void error(Marker var1, String var2, Object var3) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, getMarker(var1), var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Object var3, Object var4) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, getMarker(var1), var2, var3, var4);
   }

   @Override
   public void error(Marker var1, String var2, Object... var3) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, getMarker(var1), var2, var3);
   }

   @Override
   public void error(Marker var1, String var2, Throwable var3) {
      this.logger.logIfEnabled(FQCN, Level.ERROR, getMarker(var1), var2, var3);
   }

   @Override
   public void log(Marker var1, String var2, int var3, String var4, Object[] var5, Throwable var6) {
      Level var7 = getLevel(var3);
      org.apache.logging.log4j.Marker var8 = getMarker(var1);
      if (this.logger.isEnabled(var7, var8, var4, var5)) {
         Object var9;
         if (CONVERTER != null && this.eventLogger && var1 != null && var1.contains(EVENT_MARKER)) {
            var9 = CONVERTER.convertEvent(var4, var5, var6);
         } else if (var5 == null) {
            var9 = new SimpleMessage(var4);
         } else {
            var9 = new ParameterizedMessage(var4, var5, var6);
            if (var6 != null) {
               var6 = ((Message)var9).getThrowable();
            }
         }

         this.logger.logMessage(var2, var7, var8, (Message)var9, var6);
      }
   }

   private static org.apache.logging.log4j.Marker getMarker(Marker var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof Log4jMarker) {
         return ((Log4jMarker)var0).getLog4jMarker();
      } else {
         Log4jMarkerFactory var1 = (Log4jMarkerFactory)StaticMarkerBinder.SINGLETON.getMarkerFactory();
         return ((Log4jMarker)var1.getMarker(var0)).getLog4jMarker();
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.logger = LogManager.getContext().getLogger(this.name);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private static EventDataConverter createConverter() {
      try {
         LoaderUtil.loadClass("org.slf4j.ext.EventData");
         return new EventDataConverter();
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }

   private static Level getLevel(int var0) {
      switch (var0) {
         case 0:
            return Level.TRACE;
         case 10:
            return Level.DEBUG;
         case 20:
            return Level.INFO;
         case 30:
            return Level.WARN;
         case 40:
            return Level.ERROR;
         default:
            return Level.ERROR;
      }
   }
}
