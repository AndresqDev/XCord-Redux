package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.message.Message;

public class DefaultLogEventFactory implements LocationAwareLogEventFactory, LogEventFactory {
   private static final DefaultLogEventFactory instance = new DefaultLogEventFactory();

   public static DefaultLogEventFactory getInstance() {
      return instance;
   }

   @Override
   public LogEvent createEvent(String var1, Marker var2, String var3, Level var4, Message var5, List<Property> var6, Throwable var7) {
      return new Log4jLogEvent(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public LogEvent createEvent(String var1, Marker var2, String var3, StackTraceElement var4, Level var5, Message var6, List<Property> var7, Throwable var8) {
      return new Log4jLogEvent(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
