package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodMap {
   private static final int MORE_SPECIFIC = 0;
   private static final int LESS_SPECIFIC = 1;
   private static final int INCOMPARABLE = 2;
   Map<String, List<Method>> methodByNameMap = new Hashtable<>();

   public void add(Method var1) {
      String var2 = var1.getName();
      Object var3 = this.get(var2);
      if (var3 == null) {
         var3 = new ArrayList();
         this.methodByNameMap.put(var2, (List<Method>)var3);
      }

      var3.add(var1);
   }

   public List<Method> get(String var1) {
      return this.methodByNameMap.get(var1);
   }

   public Method find(String var1, Object[] var2) throws MethodMap.AmbiguousException {
      List var3 = this.get(var1);
      if (var3 == null) {
         return null;
      } else {
         int var4 = var2.length;
         Class[] var5 = new Class[var4];

         for (int var6 = 0; var6 < var4; var6++) {
            Object var7 = var2[var6];
            var5[var6] = var7 == null ? null : var7.getClass();
         }

         return getMostSpecific(var3, var5);
      }
   }

   private static Method getMostSpecific(List<Method> var0, Class[] var1) throws MethodMap.AmbiguousException {
      LinkedList var2 = getApplicables(var0, var1);
      if (var2.isEmpty()) {
         return null;
      } else if (var2.size() == 1) {
         return (Method)var2.getFirst();
      } else {
         LinkedList var3 = new LinkedList();

         for (Method var5 : var2) {
            Class[] var6 = var5.getParameterTypes();
            boolean var7 = false;
            Iterator var8 = var3.iterator();

            while (!var7 && var8.hasNext()) {
               Method var9 = (Method)var8.next();
               switch (moreSpecific(var6, var9.getParameterTypes())) {
                  case 0:
                     var8.remove();
                     break;
                  case 1:
                     var7 = true;
               }
            }

            if (!var7) {
               var3.addLast(var5);
            }
         }

         if (var3.size() > 1) {
            throw new MethodMap.AmbiguousException();
         } else {
            return (Method)var3.getFirst();
         }
      }
   }

   private static int moreSpecific(Class[] var0, Class[] var1) {
      boolean var2 = false;
      boolean var3 = false;

      for (int var4 = 0; var4 < var0.length; var4++) {
         if (var0[var4] != var1[var4]) {
            var2 = var2 || isStrictMethodInvocationConvertible(var1[var4], var0[var4]);
            var3 = var3 || isStrictMethodInvocationConvertible(var0[var4], var1[var4]);
         }
      }

      if (var2) {
         return var3 ? 2 : 0;
      } else {
         return var3 ? 1 : 2;
      }
   }

   private static LinkedList<Method> getApplicables(List<Method> var0, Class[] var1) {
      LinkedList var2 = new LinkedList();

      for (Object var4 : var0) {
         Method var5 = (Method)var4;
         if (isApplicable(var5, var1)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private static boolean isApplicable(Method var0, Class[] var1) {
      Class[] var2 = var0.getParameterTypes();
      if (var2.length != var1.length) {
         return false;
      } else {
         for (int var3 = 0; var3 < var1.length; var3++) {
            if (!isMethodInvocationConvertible(var2[var3], var1[var3])) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isMethodInvocationConvertible(Class var0, Class var1) {
      if (var1 == null && !var0.isPrimitive()) {
         return true;
      } else if (var1 != null && var0.isAssignableFrom(var1)) {
         return true;
      } else {
         if (var0.isPrimitive()) {
            if (var0 == boolean.class && var1 == Boolean.class) {
               return true;
            }

            if (var0 == char.class && var1 == Character.class) {
               return true;
            }

            if (var0 == byte.class && var1 == Byte.class) {
               return true;
            }

            if (var0 == short.class && (var1 == Short.class || var1 == Byte.class)) {
               return true;
            }

            if (var0 == int.class && (var1 == Integer.class || var1 == Short.class || var1 == Byte.class)) {
               return true;
            }

            if (var0 == long.class && (var1 == Long.class || var1 == Integer.class || var1 == Short.class || var1 == Byte.class)) {
               return true;
            }

            if (var0 == float.class && (var1 == Float.class || var1 == Long.class || var1 == Integer.class || var1 == Short.class || var1 == Byte.class)) {
               return true;
            }

            if (var0 == double.class
               && (var1 == Double.class || var1 == Float.class || var1 == Long.class || var1 == Integer.class || var1 == Short.class || var1 == Byte.class)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isStrictMethodInvocationConvertible(Class var0, Class var1) {
      if (var1 == null && !var0.isPrimitive()) {
         return true;
      } else if (var0.isAssignableFrom(var1)) {
         return true;
      } else {
         if (var0.isPrimitive()) {
            if (var0 == short.class && var1 == byte.class) {
               return true;
            }

            if (var0 == int.class && (var1 == short.class || var1 == byte.class)) {
               return true;
            }

            if (var0 == long.class && (var1 == int.class || var1 == short.class || var1 == byte.class)) {
               return true;
            }

            if (var0 == float.class && (var1 == long.class || var1 == int.class || var1 == short.class || var1 == byte.class)) {
               return true;
            }

            if (var0 == double.class && (var1 == float.class || var1 == long.class || var1 == int.class || var1 == short.class || var1 == byte.class)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class AmbiguousException extends Exception {
   }
}
