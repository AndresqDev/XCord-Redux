package org.apache.logging.log4j.spi;

import java.net.URI;

public interface LoggerContextFactory {
   default void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      if (this.hasContext(var1, var2, var3)) {
         LoggerContext var5 = this.getContext(var1, var2, null, var3);
         if (var5 instanceof Terminable) {
            ((Terminable)var5).terminate();
         }
      }
   }

   default boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      return false;
   }

   LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4);

   LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, URI var5, String var6);

   void removeContext(LoggerContext var1);

   default boolean isClassLoaderDependent() {
      return true;
   }
}
