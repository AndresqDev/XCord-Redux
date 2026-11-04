package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LoggerContext;

public interface ContextSelector {
   long DEFAULT_STOP_TIMEOUT = 50L;

   default void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      if (this.hasContext(var1, var2, var3)) {
         this.getContext(var1, var2, var3).stop(50L, TimeUnit.MILLISECONDS);
      }
   }

   default boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      return false;
   }

   LoggerContext getContext(String var1, ClassLoader var2, boolean var3);

   default LoggerContext getContext(String var1, ClassLoader var2, Entry<String, Object> var3, boolean var4) {
      LoggerContext var5 = this.getContext(var1, var2, var4);
      if (var5 != null) {
         var5.putObject((String)var3.getKey(), var3.getValue());
      }

      return var5;
   }

   LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4);

   default LoggerContext getContext(String var1, ClassLoader var2, Entry<String, Object> var3, boolean var4, URI var5) {
      LoggerContext var6 = this.getContext(var1, var2, var4, var5);
      if (var6 != null) {
         var6.putObject((String)var3.getKey(), var3.getValue());
      }

      return var6;
   }

   List<LoggerContext> getLoggerContexts();

   void removeContext(LoggerContext var1);

   default boolean isClassLoaderDependent() {
      return true;
   }
}
