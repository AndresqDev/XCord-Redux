package org.apache.logging.log4j.core.osgi;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;

public class BundleContextSelector extends ClassLoaderContextSelector {
   @Override
   public void shutdown(String var1, ClassLoader var2, boolean var3, boolean var4) {
      LoggerContext var5 = null;
      Bundle var6 = null;
      if (var3) {
         var5 = ContextAnchor.THREAD_CONTEXT.get();
         ContextAnchor.THREAD_CONTEXT.remove();
      }

      if (var5 == null && var2 instanceof BundleReference) {
         var6 = ((BundleReference)var2).getBundle();
         var5 = this.getLoggerContext(var6);
         this.removeLoggerContext(var5);
      }

      if (var5 == null) {
         Class var7 = StackLocatorUtil.getCallerClass(var1);
         if (var7 != null) {
            var6 = FrameworkUtil.getBundle(var7);
            var5 = this.getLoggerContext(FrameworkUtil.getBundle(var7));
            this.removeLoggerContext(var5);
         }
      }

      if (var5 == null) {
         var5 = ContextAnchor.THREAD_CONTEXT.get();
         ContextAnchor.THREAD_CONTEXT.remove();
      }

      if (var5 != null) {
         var5.stop(50L, TimeUnit.MILLISECONDS);
      }

      if (var6 != null && var4) {
         Bundle[] var13 = var6.getBundleContext().getBundles();

         for (Bundle var11 : var13) {
            var5 = this.getLoggerContext(var11);
            if (var5 != null) {
               var5.stop(50L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   private LoggerContext getLoggerContext(Bundle var1) {
      String var2 = Objects.requireNonNull(var1, "No Bundle provided").getSymbolicName();
      AtomicReference var3 = CONTEXT_MAP.get(var2);
      return var3 != null && var3.get() != null ? (LoggerContext)((WeakReference)var3.get()).get() : null;
   }

   private void removeLoggerContext(LoggerContext var1) {
      CONTEXT_MAP.remove(var1.getName());
   }

   @Override
   public boolean hasContext(String var1, ClassLoader var2, boolean var3) {
      if (var3 && ContextAnchor.THREAD_CONTEXT.get() != null) {
         return ContextAnchor.THREAD_CONTEXT.get().isStarted();
      } else if (var2 instanceof BundleReference) {
         return hasContext(((BundleReference)var2).getBundle());
      } else {
         Class var4 = StackLocatorUtil.getCallerClass(var1);
         return var4 != null
            ? hasContext(FrameworkUtil.getBundle(var4))
            : ContextAnchor.THREAD_CONTEXT.get() != null && ContextAnchor.THREAD_CONTEXT.get().isStarted();
      }
   }

   @Override
   public LoggerContext getContext(String var1, ClassLoader var2, boolean var3, URI var4) {
      if (var3) {
         LoggerContext var7 = ContextAnchor.THREAD_CONTEXT.get();
         return var7 != null ? var7 : this.getDefault();
      } else if (var2 instanceof BundleReference) {
         return locateContext(((BundleReference)var2).getBundle(), var4);
      } else {
         Class var5 = StackLocatorUtil.getCallerClass(var1);
         if (var5 != null) {
            return locateContext(FrameworkUtil.getBundle(var5), var4);
         } else {
            LoggerContext var6 = ContextAnchor.THREAD_CONTEXT.get();
            return var6 == null ? this.getDefault() : var6;
         }
      }
   }

   private static boolean hasContext(Bundle var0) {
      String var1 = Objects.requireNonNull(var0, "No Bundle provided").getSymbolicName();
      AtomicReference var2 = CONTEXT_MAP.get(var1);
      return var2 != null && var2.get() != null && ((WeakReference)var2.get()).get() != null && ((LoggerContext)((WeakReference)var2.get()).get()).isStarted();
   }

   private static LoggerContext locateContext(Bundle var0, URI var1) {
      String var2 = Objects.requireNonNull(var0, "No Bundle provided").getSymbolicName();
      AtomicReference var3 = CONTEXT_MAP.get(var2);
      if (var3 == null) {
         LoggerContext var7 = new LoggerContext(var2, var0, var1);
         CONTEXT_MAP.putIfAbsent(var2, new AtomicReference<>(new WeakReference<>(var7)));
         return CONTEXT_MAP.get(var2).get().get();
      } else {
         WeakReference var4 = (WeakReference)var3.get();
         LoggerContext var5 = (LoggerContext)var4.get();
         if (var5 == null) {
            LoggerContext var8 = new LoggerContext(var2, var0, var1);
            var3.compareAndSet(var4, new WeakReference<>(var8));
            return (LoggerContext)((WeakReference)var3.get()).get();
         } else {
            URI var6 = var5.getConfigLocation();
            if (var6 == null && var1 != null) {
               LOGGER.debug("Setting bundle ({}) configuration to {}", var2, var1);
               var5.setConfigLocation(var1);
            } else if (var6 != null && var1 != null && !var1.equals(var6)) {
               LOGGER.warn("locateContext called with URI [{}], but existing LoggerContext has URI [{}]", var1, var6);
            }

            return var5;
         }
      }
   }
}
