package org.apache.logging.log4j.core.selector;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class ClassLoaderContextSelector implements ContextSelector, LoggerContextShutdownAware {
   private static final AtomicReference<LoggerContext> DEFAULT_CONTEXT = new AtomicReference<>();
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   protected static final ConcurrentMap<String, AtomicReference<WeakReference<LoggerContext>>> CONTEXT_MAP = new ConcurrentHashMap<>();

   @Override
   public void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      LoggerContext var5 = null;
      if (var3) {
         var5 = ContextAnchor.THREAD_CONTEXT.get();
      } else if (var2 != null) {
         var5 = this.findContext(var2);
      } else {
         Class var6 = StackLocatorUtil.getCallerClass(var1);
         if (var6 != null) {
            var5 = this.findContext(var6.getClassLoader());
         }

         if (var5 == null) {
            var5 = ContextAnchor.THREAD_CONTEXT.get();
         }
      }

      if (var5 != null) {
         var5.stop(50L, TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public void contextShutdown(org.apache.logging.log4j.spi.LoggerContext var1) {
      if (var1 instanceof LoggerContext) {
         this.removeContext((LoggerContext)var1);
      }
   }

   @Override
   public boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      LoggerContext var4;
      if (var3) {
         var4 = ContextAnchor.THREAD_CONTEXT.get();
      } else if (var2 != null) {
         var4 = this.findContext(var2);
      } else {
         Class var5 = StackLocatorUtil.getCallerClass(var1);
         if (var5 != null) {
            var4 = this.findContext(var5.getClassLoader());
         } else {
            var4 = ContextAnchor.THREAD_CONTEXT.get();
         }
      }

      return var4 != null && var4.isStarted();
   }

   private LoggerContext findContext(ClassLoader var1) {
      ClassLoader var2 = var1 != null ? var1 : ClassLoader.getSystemClassLoader();
      String var3 = this.toContextMapKey(var2);
      AtomicReference var4 = CONTEXT_MAP.get(var3);
      if (var4 != null) {
         WeakReference var5 = (WeakReference)var4.get();
         return (LoggerContext)var5.get();
      } else {
         return null;
      }
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3) {
      return this.getContext(var1, var2, var3, null);
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      return this.getContext(var1, var2, null, var3, var4);
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, Entry<String, Object> var3, boolean var4, URI var5) {
      if (var4) {
         LoggerContext var8 = ContextAnchor.THREAD_CONTEXT.get();
         return var8 != null ? var8 : this.getDefault();
      } else if (var2 != null) {
         return this.locateContext(var2, var3, var5);
      } else {
         Class var6 = StackLocatorUtil.getCallerClass(var1);
         if (var6 != null) {
            return this.locateContext(var6.getClassLoader(), var3, var5);
         } else {
            LoggerContext var7 = ContextAnchor.THREAD_CONTEXT.get();
            return var7 != null ? var7 : this.getDefault();
         }
      }
   }

   @Override
   public void removeContext(LoggerContext var1) {
      for (Entry var3 : CONTEXT_MAP.entrySet()) {
         LoggerContext var4 = (LoggerContext)((WeakReference)((AtomicReference)var3.getValue()).get()).get();
         if (var4 == var1) {
            CONTEXT_MAP.remove(var3.getKey());
         }
      }
   }

   @Override
   public boolean isClassLoaderDependent() {
      return true;
   }

   @Override
   public List<LoggerContext> getLoggerContexts() {
      ArrayList var1 = new ArrayList();

      for (AtomicReference var4 : CONTEXT_MAP.values()) {
         LoggerContext var5 = (LoggerContext)((WeakReference)var4.get()).get();
         if (var5 != null) {
            var1.add(var5);
         }
      }

      return Collections.unmodifiableList(var1);
   }

   private LoggerContext locateContext(ClassLoader var1, Entry<String, Object> var2, URI var3) {
      ClassLoader var4 = var1 != null ? var1 : ClassLoader.getSystemClassLoader();
      String var5 = this.toContextMapKey(var4);
      AtomicReference var6 = CONTEXT_MAP.get(var5);
      if (var6 == null) {
         if (var3 == null) {
            for (ClassLoader var11 = var4.getParent(); var11 != null; var11 = var11.getParent()) {
               var6 = CONTEXT_MAP.get(this.toContextMapKey(var11));
               if (var6 != null) {
                  WeakReference var14 = (WeakReference)var6.get();
                  LoggerContext var9 = (LoggerContext)var14.get();
                  if (var9 != null) {
                     return var9;
                  }
               }
            }
         }

         LoggerContext var12 = this.createContext(var5, var3);
         if (var2 != null) {
            var12.putObject((String)var2.getKey(), var2.getValue());
         }

         LoggerContext var15 = CONTEXT_MAP.computeIfAbsent(var5, var1x -> new AtomicReference<>(new WeakReference<>(var12))).get().get();
         if (var15 == var12) {
            var12.addShutdownListener(this);
         }

         return var15;
      } else {
         WeakReference var7 = (WeakReference)var6.get();
         LoggerContext var8 = (LoggerContext)var7.get();
         if (var8 != null) {
            if (var2 != null && var8.getObject((String)var2.getKey()) == null) {
               var8.putObject((String)var2.getKey(), var2.getValue());
            }

            if (var8.getConfigLocation() == null && var3 != null) {
               LOGGER.debug("Setting configuration to {}", var3);
               var8.setConfigLocation(var3);
            } else if (var8.getConfigLocation() != null && var3 != null && !var8.getConfigLocation().equals(var3)) {
               LOGGER.warn("locateContext called with URI {}. Existing LoggerContext has URI {}", var3, var8.getConfigLocation());
            }

            return var8;
         } else {
            var8 = this.createContext(var5, var3);
            if (var2 != null) {
               var8.putObject((String)var2.getKey(), var2.getValue());
            }

            var6.compareAndSet(var7, new WeakReference<>(var8));
            return var8;
         }
      }
   }

   protected LoggerContext createContext(String var1, URI var2) {
      return new LoggerContext(var1, null, var2);
   }

   protected String toContextMapKey(ClassLoader var1) {
      return Integer.toHexString(System.identityHashCode(var1));
   }

   protected LoggerContext getDefault() {
      LoggerContext var1 = DEFAULT_CONTEXT.get();
      if (var1 != null) {
         return var1;
      } else {
         DEFAULT_CONTEXT.compareAndSet(null, this.createContext(this.defaultContextName(), null));
         return DEFAULT_CONTEXT.get();
      }
   }

   protected String defaultContextName() {
      return "Default";
   }
}
