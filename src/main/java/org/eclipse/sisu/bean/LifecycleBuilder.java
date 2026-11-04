package org.eclipse.sisu.bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

final class LifecycleBuilder {
   private final List<Method> startMethods = new ArrayList<>();
   private final List<Method> stopMethods = new ArrayList<>();
   private final List<Class<?>> hierarchy = new ArrayList<>();

   public synchronized BeanLifecycle build(Class<?> var1) {
      BeanLifecycle var4;
      try {
         for (Class var2 = var1; var2 != null && var2 != Object.class; var2 = var2.getSuperclass()) {
            this.addLifecycleMethods(var2);
         }

         if (!this.startMethods.isEmpty() || !this.stopMethods.isEmpty()) {
            return new BeanLifecycle(this.startMethods, this.stopMethods);
         }

         var4 = BeanLifecycle.NO_OP;
      } finally {
         this.startMethods.clear();
         this.stopMethods.clear();
         this.hierarchy.clear();
      }

      return var4;
   }

   private void addLifecycleMethods(Class<?> var1) {
      boolean var2 = false;
      boolean var3 = false;

      Method[] var7;
      for (Method var4 : var7 = var1.getDeclaredMethods()) {
         if (isCandidateMethod(var4)) {
            if (var4.isAnnotationPresent(PostConstruct.class)) {
               var2 = true;
               if (!this.isOverridden(var4)) {
                  this.startMethods.add(var4);
               }
            } else if (var4.isAnnotationPresent(PreDestroy.class)) {
               var3 = true;
               if (!this.isOverridden(var4)) {
                  this.stopMethods.add(var4);
               }
            }

            if (var2 && var3) {
               break;
            }
         }
      }

      this.hierarchy.add(var1);
   }

   private boolean isOverridden(Method var1) {
      String var2 = var1.getName();

      for (int var3 = this.hierarchy.size() - 1; var3 >= 0; var3--) {
         Method[] var7;
         for (Method var4 : var7 = this.hierarchy.get(var3).getDeclaredMethods()) {
            if (var2.equals(var4.getName()) && isCandidateMethod(var4)) {
               int var8 = var4.getModifiers();
               if (Modifier.isPublic(var8) || Modifier.isProtected(var8) || !Modifier.isPrivate(var8) && samePackage(var1, var4)) {
                  return true;
               }
               break;
            }
         }
      }

      return false;
   }

   private static boolean isCandidateMethod(Method var0) {
      if (var0.getReturnType() == void.class) {
         int var1 = var0.getModifiers();
         if (!Modifier.isStatic(var1) && !Modifier.isAbstract(var1) && !var0.isSynthetic()) {
            if (var0.getParameterTypes().length == 0) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   private static boolean samePackage(Method var0, Method var1) {
      return var0.getDeclaringClass().getPackage().equals(var1.getDeclaringClass().getPackage());
   }
}
