package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "event",
   category = "Lookup"
)
public class EventLookup extends AbstractLookup {
   @Override
   public String lookup(LogEvent var1, String var2) {
      if (var1 == null) {
         return null;
      } else {
         switch (var2) {
            case "Marker":
               return var1.getMarker() != null ? var1.getMarker().getName() : null;
            case "ThreadName":
               return var1.getThreadName();
            case "Level":
               return var1.getLevel().toString();
            case "ThreadId":
               return Long.toString(var1.getThreadId());
            case "Timestamp":
               return Long.toString(var1.getTimeMillis());
            case "Exception":
               if (var1.getThrown() != null) {
                  return var1.getThrown().getClass().getSimpleName();
               } else {
                  if (var1.getThrownProxy() != null) {
                     return var1.getThrownProxy().getName();
                  }

                  return null;
               }
            case "Logger":
               return var1.getLoggerName();
            case "Message":
               return var1.getMessage().getFormattedMessage();
            default:
               return null;
         }
      }
   }
}
