package org.apache.logging.slf4j;

import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.spi.MDCAdapter;

public class Log4jMDCAdapter implements MDCAdapter {
   @Override
   public void put(String var1, String var2) {
      ThreadContext.put(var1, var2);
   }

   @Override
   public String get(String var1) {
      return ThreadContext.get(var1);
   }

   @Override
   public void remove(String var1) {
      ThreadContext.remove(var1);
   }

   @Override
   public void clear() {
      ThreadContext.clearMap();
   }

   @Override
   public Map<String, String> getCopyOfContextMap() {
      return ThreadContext.getContext();
   }

   @Override
   public void setContextMap(Map var1) {
      ThreadContext.clearMap();
      ThreadContext.putAll(var1);
   }
}
