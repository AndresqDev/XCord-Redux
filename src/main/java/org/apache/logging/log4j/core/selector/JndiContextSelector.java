package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.naming.NamingException;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.status.StatusLogger;

public class JndiContextSelector implements NamedContextSelector {
   private static final LoggerContext CONTEXT = new LoggerContext("Default");
   private static final ConcurrentMap<String, LoggerContext> CONTEXT_MAP = new ConcurrentHashMap<>();
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   public JndiContextSelector() {
      if (!JndiManager.isJndiContextSelectorEnabled()) {
         throw new IllegalStateException("JNDI must be enabled by setting log4j2.enableJndiContextSelector=true");
      }
   }

   @Override
   public void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      LoggerContext var5 = ContextAnchor.THREAD_CONTEXT.get();
      if (var5 == null) {
         String var6 = this.getContextName();
         if (var6 != null) {
            var5 = CONTEXT_MAP.get(var6);
         }
      }

      if (var5 != null) {
         var5.stop(50L, TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      LoggerContext var4 = ContextAnchor.THREAD_CONTEXT.get();
      if (var4 == null) {
         String var5 = this.getContextName();
         if (var5 == null) {
            return false;
         }

         var4 = CONTEXT_MAP.get(var5);
      }

      return var4 != null && var4.isStarted();
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3) {
      return this.getContext(var1, var2, var3, null);
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      LoggerContext var5 = ContextAnchor.THREAD_CONTEXT.get();
      if (var5 != null) {
         return var5;
      } else {
         String var6 = null;

         try (JndiManager var7 = JndiManager.getDefaultManager()) {
            var6 = var7.lookup("java:comp/env/log4j/context-name");
         } catch (NamingException var20) {
            LOGGER.error("Unable to lookup {}", "java:comp/env/log4j/context-name", var20);
         }

         return var6 == null ? CONTEXT : this.locateContext(var6, null, var4);
      }
   }

   private String getContextName() {
      String var1 = null;

      try (JndiManager var2 = JndiManager.getDefaultManager()) {
         var1 = var2.lookup("java:comp/env/log4j/context-name");
      } catch (NamingException var15) {
         LOGGER.error("Unable to lookup {}", "java:comp/env/log4j/context-name", var15);
      }

      return var1;
   }

   @Override
   public LoggerContext locateContext(String var1, Object var2, URI var3) {
      if (var1 == null) {
         LOGGER.error("A context name is required to locate a LoggerContext");
         return null;
      } else {
         if (!CONTEXT_MAP.containsKey(var1)) {
            LoggerContext var4 = new LoggerContext(var1, var2, var3);
            CONTEXT_MAP.putIfAbsent(var1, var4);
         }

         return CONTEXT_MAP.get(var1);
      }
   }

   @Override
   public void removeContext(LoggerContext var1) {
      for (Entry var3 : CONTEXT_MAP.entrySet()) {
         if (((LoggerContext)var3.getValue()).equals(var1)) {
            CONTEXT_MAP.remove(var3.getKey());
         }
      }
   }

   @Override
   public boolean isClassLoaderDependent() {
      return false;
   }

   @Override
   public LoggerContext removeContext(String var1) {
      return CONTEXT_MAP.remove(var1);
   }

   @Override
   public List<LoggerContext> getLoggerContexts() {
      return Collections.unmodifiableList(new ArrayList<>(CONTEXT_MAP.values()));
   }
}
