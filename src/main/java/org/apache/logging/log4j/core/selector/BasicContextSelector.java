package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;

public class BasicContextSelector implements ContextSelector {
   private static final LoggerContext CONTEXT = new LoggerContext("Default");

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
      return var4 != null ? var4 : CONTEXT;
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      LoggerContext var5 = ContextAnchor.THREAD_CONTEXT.get();
      return var5 != null ? var5 : CONTEXT;
   }

   public LoggerContext locateContext(String var1, String var2) {
      return CONTEXT;
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
      ArrayList var1 = new ArrayList();
      var1.add(CONTEXT);
      return Collections.unmodifiableList(var1);
   }
}
