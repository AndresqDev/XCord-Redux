package org.apache.logging.slf4j;

import java.util.Map.Entry;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.slf4j.ext.EventData;

public class EventDataConverter {
   public Message convertEvent(String var1, Object[] var2, Throwable var3) {
      try {
         EventData var4 = var2 != null && var2[0] instanceof EventData ? (EventData)var2[0] : new EventData(var1);
         StructuredDataMessage var5 = new StructuredDataMessage(var4.getEventId(), var4.getMessage(), var4.getEventType());

         for (Entry var7 : var4.getEventMap().entrySet()) {
            String var8 = (String)var7.getKey();
            if (!"EventType".equals(var8) && !"EventId".equals(var8) && !"EventMessage".equals(var8)) {
               var5.put(var8, String.valueOf(var7.getValue()));
            }
         }

         return var5;
      } catch (Exception var9) {
         return new ParameterizedMessage(var1, var2, var3);
      }
   }
}
