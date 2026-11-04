package org.eclipse.sisu.bean;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import org.eclipse.sisu.inject.Logs;

final class BeanLifecycle implements PrivilegedAction<Void> {
   private static final Method[] NO_METHODS = new Method[0];
   static final BeanLifecycle NO_OP = new BeanLifecycle(null, null);
   private final Method[] startMethods;
   private final Method[] stopMethods;

   BeanLifecycle(List<Method> var1, List<Method> var2) {
      this.startMethods = toArray(var1);
      this.stopMethods = toArray(var2);
      AccessController.doPrivileged(this);
   }

   public boolean isStartable() {
      return this.startMethods.length > 0;
   }

   public boolean isStoppable() {
      return this.stopMethods.length > 0;
   }

   public void start(Object var1) {
      Logs.trace("PostConstruct: <>", var1, null);
      int var2 = this.startMethods.length - 1;

      try {
         while (var2 >= 0) {
            this.startMethods[var2].invoke(var1);
            var2--;
         }
      } catch (Throwable var9) {
         Throwable var4 = var9 instanceof InvocationTargetException ? var9.getCause() : var9;
         Logs.catchThrowable(var4);

         try {
            Logs.warn("Error starting: {}", this.startMethods[var2], var4);
         } finally {
            Logs.throwUnchecked(var4);
         }
      }
   }

   public void stop(Object var1) {
      Logs.trace("PreDestroy: <>", var1, null);

      for (int var2 = 0; var2 < this.stopMethods.length; var2++) {
         try {
            this.stopMethods[var2].invoke(var1);
         } catch (Throwable var8) {
            Throwable var4 = var8 instanceof InvocationTargetException ? var8.getCause() : var8;
            Logs.catchThrowable(var4);

            try {
               Logs.warn("Problem stopping: {}", this.stopMethods[var2], var4);
            } finally {
               ;
            }
         }
      }
   }

   public Void run() {
      AccessibleObject.setAccessible(this.startMethods, true);
      AccessibleObject.setAccessible(this.stopMethods, true);
      return null;
   }

   private static Method[] toArray(List<Method> var0) {
      return var0 != null && !var0.isEmpty() ? var0.toArray(new Method[var0.size()]) : NO_METHODS;
   }
}
