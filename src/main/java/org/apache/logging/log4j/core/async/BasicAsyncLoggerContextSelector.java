package org.apache.logging.log4j.core.async;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;

public class BasicAsyncLoggerContextSelector implements ContextSelector {
   private static final AsyncLoggerContext CONTEXT = new AsyncLoggerContext("AsyncDefault");

   @Override
   public void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      LoggerContext var5 = this.getContext(var1, var2, var3);
      if (var5 != null && var5.isStarted()) {
         var5.stop(50L, TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      LoggerContext var4 = this.getContext(var1, var2, var3);
      return var4 != null && var4.isStarted();
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3) {
      LoggerContext var4 = ContextAnchor.THREAD_CONTEXT.get();
      return (LoggerContext)(var4 != null ? var4 : CONTEXT);
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      LoggerContext var5 = ContextAnchor.THREAD_CONTEXT.get();
      return (LoggerContext)(var5 != null ? var5 : CONTEXT);
   }

   @Override
   public void removeContext(LoggerContext var1) {
   }

   @Override
   public boolean isClassLoaderDependent() {
      return false;
   }

   @Override
   public List<LoggerContext> getLoggerContexts() {
      return Collections.singletonList(CONTEXT);
   }
}
