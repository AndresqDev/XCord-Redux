package org.apache.logging.log4j.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

public class ThreadContextDataInjector {
   private static final Logger LOGGER = StatusLogger.getLogger();
   public static Collection<ContextDataProvider> contextDataProviders = new ConcurrentLinkedDeque<>();
   private static volatile List<ContextDataProvider> serviceProviders = null;
   private static final Lock providerLock = new ReentrantLock();

   public static void initServiceProviders() {
      if (serviceProviders == null) {
         providerLock.lock();

         try {
            if (serviceProviders == null) {
               serviceProviders = getServiceProviders();
            }
         } finally {
            providerLock.unlock();
         }
      }
   }

   private static List<ContextDataProvider> getServiceProviders() {
      ArrayList var0 = new ArrayList();

      for (ClassLoader var4 : LoaderUtil.getClassLoaders()) {
         try {
            for (ContextDataProvider var6 : ServiceLoader.load(ContextDataProvider.class, var4)) {
               if (var0.stream().noneMatch(var1 -> var1.getClass().isAssignableFrom(var6.getClass()))) {
                  var0.add(var6);
               }
            }
         } catch (Throwable var7) {
            LOGGER.debug("Unable to access Context Data Providers {}", var7.getMessage());
         }
      }

      return var0;
   }

   public static void copyProperties(List<Property> var0, StringMap var1) {
      if (var0 != null) {
         for (int var2 = 0; var2 < var0.size(); var2++) {
            Property var3 = (Property)var0.get(var2);
            var1.putValue(var3.getName(), var3.getValue());
         }
      }
   }

   private static List<ContextDataProvider> getProviders() {
      initServiceProviders();
      ArrayList var0 = new ArrayList<>(contextDataProviders);
      if (serviceProviders != null) {
         var0.addAll(serviceProviders);
      }

      return var0;
   }

   public static class ForCopyOnWriteThreadContextMap implements ContextDataInjector {
      private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();

      @Override
      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         if (this.providers.size() != 1 || var1 != null && !var1.isEmpty()) {
            int var3 = var1 == null ? 0 : var1.size();
            StringMap[] var4 = new StringMap[this.providers.size()];

            for (int var5 = 0; var5 < this.providers.size(); var5++) {
               var4[var5] = this.providers.get(var5).supplyStringMap();
               var3 += var4[var5].size();
            }

            StringMap var10 = ContextDataFactory.createContextData(var3);
            ThreadContextDataInjector.copyProperties(var1, var10);

            for (StringMap var9 : var4) {
               var10.putAll(var9);
            }

            return var10;
         } else {
            return this.providers.get(0).supplyStringMap();
         }
      }

      @Override
      public ReadOnlyStringMap rawContextData() {
         return ThreadContext.getThreadContextMap().getReadOnlyContextData();
      }
   }

   public static class ForDefaultThreadContextMap implements ContextDataInjector {
      private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();

      @Override
      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         Object var3;
         if (this.providers.size() == 1) {
            var3 = this.providers.get(0).supplyContextData();
         } else {
            var3 = new HashMap();

            for (ContextDataProvider var5 : this.providers) {
               var3.putAll(var5.supplyContextData());
            }
         }

         if (var1 != null && !var1.isEmpty()) {
            JdkMapAdapterStringMap var7 = new JdkMapAdapterStringMap(new HashMap<>((Map<? extends String, ? extends String>)var3));

            for (int var8 = 0; var8 < var1.size(); var8++) {
               Property var6 = (Property)var1.get(var8);
               if (!var3.containsKey(var6.getName())) {
                  var7.putValue(var6.getName(), var6.getValue());
               }
            }

            var7.freeze();
            return var7;
         } else {
            return (StringMap)(var3.isEmpty() ? ContextDataFactory.emptyFrozenContextData() : frozenStringMap((Map<String, String>)var3));
         }
      }

      private static JdkMapAdapterStringMap frozenStringMap(Map<String, String> var0) {
         JdkMapAdapterStringMap var1 = new JdkMapAdapterStringMap(var0);
         var1.freeze();
         return var1;
      }

      @Override
      public ReadOnlyStringMap rawContextData() {
         ReadOnlyThreadContextMap var1 = ThreadContext.getThreadContextMap();
         if (var1 instanceof ReadOnlyStringMap) {
            return (ReadOnlyStringMap)var1;
         } else {
            Map var2 = ThreadContext.getImmutableContext();
            return (ReadOnlyStringMap)(var2.isEmpty() ? ContextDataFactory.emptyFrozenContextData() : new JdkMapAdapterStringMap(var2));
         }
      }
   }

   public static class ForGarbageFreeThreadContextMap implements ContextDataInjector {
      private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();

      @Override
      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         ThreadContextDataInjector.copyProperties(var1, var2);

         for (int var3 = 0; var3 < this.providers.size(); var3++) {
            var2.putAll(this.providers.get(var3).supplyStringMap());
         }

         return var2;
      }

      @Override
      public ReadOnlyStringMap rawContextData() {
         return ThreadContext.getThreadContextMap().getReadOnlyContextData();
      }
   }
}
