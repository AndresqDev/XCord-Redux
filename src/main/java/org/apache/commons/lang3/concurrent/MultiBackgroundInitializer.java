package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializer.MultiBackgroundInitializerResults> {
   private final Map<String, BackgroundInitializer<?>> childInitializers = new HashMap<>();

   public MultiBackgroundInitializer() {
   }

   public MultiBackgroundInitializer(ExecutorService var1) {
      super(var1);
   }

   public void addInitializer(String var1, BackgroundInitializer<?> var2) {
      Validate.isTrue(var1 != null, "Name of child initializer must not be null!");
      Validate.isTrue(var2 != null, "Child initializer must not be null!");
      synchronized (this) {
         if (this.isStarted()) {
            throw new IllegalStateException("addInitializer() must not be called after start()!");
         } else {
            this.childInitializers.put(var1, var2);
         }
      }
   }

   @Override
   protected int getTaskCount() {
      int var1 = 1;

      for (BackgroundInitializer var3 : this.childInitializers.values()) {
         var1 += var3.getTaskCount();
      }

      return var1;
   }

   protected MultiBackgroundInitializer.MultiBackgroundInitializerResults initialize() throws Exception {
      HashMap var1;
      synchronized (this) {
         var1 = new HashMap<>(this.childInitializers);
      }

      ExecutorService var2 = this.getActiveExecutor();

      for (BackgroundInitializer var4 : var1.values()) {
         if (var4.getExternalExecutor() == null) {
            var4.setExternalExecutor(var2);
         }

         var4.start();
      }

      HashMap var10 = new HashMap();
      HashMap var11 = new HashMap();

      for (Entry var6 : var1.entrySet()) {
         try {
            var10.put(var6.getKey(), ((BackgroundInitializer)var6.getValue()).get());
         } catch (ConcurrentException var8) {
            var11.put(var6.getKey(), var8);
         }
      }

      return new MultiBackgroundInitializer.MultiBackgroundInitializerResults(var1, var10, var11);
   }

   public static class MultiBackgroundInitializerResults {
      private final Map<String, BackgroundInitializer<?>> initializers;
      private final Map<String, Object> resultObjects;
      private final Map<String, ConcurrentException> exceptions;

      private MultiBackgroundInitializerResults(Map<String, BackgroundInitializer<?>> var1, Map<String, Object> var2, Map<String, ConcurrentException> var3) {
         this.initializers = var1;
         this.resultObjects = var2;
         this.exceptions = var3;
      }

      public BackgroundInitializer<?> getInitializer(String var1) {
         return this.checkName(var1);
      }

      public Object getResultObject(String var1) {
         this.checkName(var1);
         return this.resultObjects.get(var1);
      }

      public boolean isException(String var1) {
         this.checkName(var1);
         return this.exceptions.containsKey(var1);
      }

      public ConcurrentException getException(String var1) {
         this.checkName(var1);
         return this.exceptions.get(var1);
      }

      public Set<String> initializerNames() {
         return Collections.unmodifiableSet(this.initializers.keySet());
      }

      public boolean isSuccessful() {
         return this.exceptions.isEmpty();
      }

      private BackgroundInitializer<?> checkName(String var1) {
         BackgroundInitializer var2 = this.initializers.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException("No child initializer with name " + var1);
         } else {
            return var2;
         }
      }
   }
}
