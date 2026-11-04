package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

public interface LoggerContext {
   Object getExternalContext();

   default Object getObject(String var1) {
      return null;
   }

   default Object putObject(String var1, Object var2) {
      return null;
   }

   default Object putObjectIfAbsent(String var1, Object var2) {
      return null;
   }

   default Object removeObject(String var1) {
      return null;
   }

   default boolean removeObject(String var1, Object var2) {
      return false;
   }

   ExtendedLogger getLogger(String var1);

   default ExtendedLogger getLogger(Class<?> var1) {
      String var2 = var1.getCanonicalName();
      return this.getLogger(var2 != null ? var2 : var1.getName());
   }

   ExtendedLogger getLogger(String var1, MessageFactory var2);

   default ExtendedLogger getLogger(Class<?> var1, MessageFactory var2) {
      String var3 = var1.getCanonicalName();
      return this.getLogger(var3 != null ? var3 : var1.getName(), var2);
   }

   boolean hasLogger(String var1);

   boolean hasLogger(String var1, MessageFactory var2);

   boolean hasLogger(String var1, Class<? extends MessageFactory> var2);
}
