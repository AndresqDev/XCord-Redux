package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class MethodUtils {
   public static Object invokeMethod(Object var0, String var1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, var1, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, var1, var2, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
   }

   public static Object invokeMethod(Object var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeMethod(var0, var1, var2, var3);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2, Object... var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var3 = ArrayUtils.nullToEmpty(var3);
      Class[] var4 = ClassUtils.toClass(var3);
      return invokeMethod(var0, var1, var2, var3, var4);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2, Object[] var3, Class<?>[] var4) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var4 = ArrayUtils.nullToEmpty(var4);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var6 = null;
      String var5;
      if (var1) {
         var5 = "No such method: ";
         var6 = getMatchingMethod(var0.getClass(), var2, var4);
         if (var6 != null && !var6.isAccessible()) {
            var6.setAccessible(true);
         }
      } else {
         var5 = "No such accessible method: ";
         var6 = getMatchingAccessibleMethod(var0.getClass(), var2, var4);
      }

      if (var6 == null) {
         throw new NoSuchMethodException(var5 + var2 + "() on object: " + var0.getClass().getName());
      } else {
         var3 = toVarArgs(var6, var3);
         return var6.invoke(var0, var3);
      }
   }

   public static Object invokeMethod(Object var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, false, var1, var2, var3);
   }

   public static Object invokeExactMethod(Object var0, String var1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeExactMethod(var0, var1, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
   }

   public static Object invokeExactMethod(Object var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeExactMethod(var0, var1, var2, var3);
   }

   public static Object invokeExactMethod(Object var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getAccessibleMethod(var0.getClass(), var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on object: " + var0.getClass().getName());
      } else {
         return var4.invoke(var0, var2);
      }
   }

   public static Object invokeExactStaticMethod(Class<?> var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getAccessibleMethod(var0, var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on class: " + var0.getName());
      } else {
         return var4.invoke(null, var2);
      }
   }

   public static Object invokeStaticMethod(Class<?> var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeStaticMethod(var0, var1, var2, var3);
   }

   public static Object invokeStaticMethod(Class<?> var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getMatchingAccessibleMethod(var0, var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on class: " + var0.getName());
      } else {
         var2 = toVarArgs(var4, var2);
         return var4.invoke(null, var2);
      }
   }

   private static Object[] toVarArgs(Method var0, Object[] var1) {
      if (var0.isVarArgs()) {
         Class[] var2 = var0.getParameterTypes();
         var1 = getVarArgs(var1, var2);
      }

      return var1;
   }

   static Object[] getVarArgs(Object[] var0, Class<?>[] var1) {
      if (var0.length == var1.length && var0[var0.length - 1].getClass().equals(var1[var1.length - 1])) {
         return var0;
      } else {
         Object[] var2 = new Object[var1.length];
         System.arraycopy(var0, 0, var2, 0, var1.length - 1);
         Class var3 = var1[var1.length - 1].getComponentType();
         int var4 = var0.length - var1.length + 1;
         Object var5 = Array.newInstance(ClassUtils.primitiveToWrapper(var3), var4);
         System.arraycopy(var0, var1.length - 1, var5, 0, var4);
         if (var3.isPrimitive()) {
            var5 = ArrayUtils.toPrimitive(var5);
         }

         var2[var1.length - 1] = var5;
         return var2;
      }
   }

   public static Object invokeExactStaticMethod(Class<?> var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeExactStaticMethod(var0, var1, var2, var3);
   }

   public static Method getAccessibleMethod(Class<?> var0, String var1, Class<?>... var2) {
      try {
         return getAccessibleMethod(var0.getMethod(var1, var2));
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static Method getAccessibleMethod(Method var0) {
      if (!MemberUtils.isAccessible(var0)) {
         return null;
      } else {
         Class var1 = var0.getDeclaringClass();
         if (Modifier.isPublic(var1.getModifiers())) {
            return var0;
         } else {
            String var2 = var0.getName();
            Class[] var3 = var0.getParameterTypes();
            var0 = getAccessibleMethodFromInterfaceNest(var1, var2, var3);
            if (var0 == null) {
               var0 = getAccessibleMethodFromSuperclass(var1, var2, var3);
            }

            return var0;
         }
      }
   }

   private static Method getAccessibleMethodFromSuperclass(Class<?> var0, String var1, Class<?>... var2) {
      for (Class var3 = var0.getSuperclass(); var3 != null; var3 = var3.getSuperclass()) {
         if (Modifier.isPublic(var3.getModifiers())) {
            try {
               return var3.getMethod(var1, var2);
            } catch (NoSuchMethodException var5) {
               return null;
            }
         }
      }

      return null;
   }

   private static Method getAccessibleMethodFromInterfaceNest(Class<?> var0, String var1, Class<?>... var2) {
      while (var0 != null) {
         Class[] var3 = var0.getInterfaces();

         for (Class var7 : var3) {
            if (Modifier.isPublic(var7.getModifiers())) {
               try {
                  return var7.getDeclaredMethod(var1, var2);
               } catch (NoSuchMethodException var9) {
                  Method var8 = getAccessibleMethodFromInterfaceNest(var7, var1, var2);
                  if (var8 != null) {
                     return var8;
                  }
               }
            }
         }

         var0 = var0.getSuperclass();
      }

      return null;
   }

   public static Method getMatchingAccessibleMethod(Class<?> var0, String var1, Class<?>... var2) {
      try {
         Method var11 = var0.getMethod(var1, var2);
         MemberUtils.setAccessibleWorkaround(var11);
         return var11;
      } catch (NoSuchMethodException var10) {
         Method var3 = null;
         Method[] var4 = var0.getMethods();

         for (Method var8 : var4) {
            if (var8.getName().equals(var1) && MemberUtils.isMatchingMethod(var8, var2)) {
               Method var9 = getAccessibleMethod(var8);
               if (var9 != null && (var3 == null || MemberUtils.compareMethodFit(var9, var3, var2) < 0)) {
                  var3 = var9;
               }
            }
         }

         if (var3 != null) {
            MemberUtils.setAccessibleWorkaround(var3);
         }

         if (var3 != null && var3.isVarArgs() && var3.getParameterTypes().length > 0 && var2.length > 0) {
            Class[] var12 = var3.getParameterTypes();
            Class var13 = var12[var12.length - 1].getComponentType();
            String var14 = ClassUtils.primitiveToWrapper(var13).getName();
            String var15 = var2[var2.length - 1].getName();
            String var16 = var2[var2.length - 1].getSuperclass().getName();
            if (!var14.equals(var15) && !var14.equals(var16)) {
               return null;
            }
         }

         return var3;
      }
   }

   public static Method getMatchingMethod(Class<?> var0, String var1, Class<?>... var2) {
      Validate.notNull(var0, "Null class not allowed.");
      Validate.notEmpty(var1, "Null or blank methodName not allowed.");
      Method[] var3 = var0.getDeclaredMethods();

      for (Class var6 : ClassUtils.getAllSuperclasses(var0)) {
         var3 = ArrayUtils.addAll(var3, var6.getDeclaredMethods());
      }

      Method var10 = null;

      for (Method var9 : var3) {
         if (var1.equals(var9.getName()) && Objects.deepEquals(var2, var9.getParameterTypes())) {
            return var9;
         }

         if (var1.equals(var9.getName()) && ClassUtils.isAssignable(var2, var9.getParameterTypes(), true)) {
            if (var10 == null) {
               var10 = var9;
            } else if (distance(var2, var9.getParameterTypes()) < distance(var2, var10.getParameterTypes())) {
               var10 = var9;
            }
         }
      }

      return var10;
   }

   private static int distance(Class<?>[] var0, Class<?>[] var1) {
      int var2 = 0;
      if (!ClassUtils.isAssignable(var0, var1, true)) {
         return -1;
      } else {
         for (int var3 = 0; var3 < var0.length; var3++) {
            if (!var0[var3].equals(var1[var3])) {
               if (ClassUtils.isAssignable(var0[var3], var1[var3], true) && !ClassUtils.isAssignable(var0[var3], var1[var3], false)) {
                  var2++;
               } else {
                  var2 += 2;
               }
            }
         }

         return var2;
      }
   }

   public static Set<Method> getOverrideHierarchy(Method var0, ClassUtils.Interfaces var1) {
      Validate.notNull(var0);
      LinkedHashSet var2 = new LinkedHashSet();
      var2.add(var0);
      Class[] var3 = var0.getParameterTypes();
      Class var4 = var0.getDeclaringClass();
      Iterator var5 = ClassUtils.hierarchy(var4, var1).iterator();
      var5.next();

      label34:
      while (var5.hasNext()) {
         Class var6 = (Class)var5.next();
         Method var7 = getMatchingAccessibleMethod(var6, var0.getName(), var3);
         if (var7 != null) {
            if (Arrays.equals((Object[])var7.getParameterTypes(), (Object[])var3)) {
               var2.add(var7);
            } else {
               Map var8 = TypeUtils.getTypeArguments(var4, var7.getDeclaringClass());

               for (int var9 = 0; var9 < var3.length; var9++) {
                  Type var10 = TypeUtils.unrollVariables(var8, var0.getGenericParameterTypes()[var9]);
                  Type var11 = TypeUtils.unrollVariables(var8, var7.getGenericParameterTypes()[var9]);
                  if (!TypeUtils.equals(var10, var11)) {
                     continue label34;
                  }
               }

               var2.add(var7);
            }
         }
      }

      return var2;
   }

   public static Method[] getMethodsWithAnnotation(Class<?> var0, Class<? extends Annotation> var1) {
      return getMethodsWithAnnotation(var0, var1, false, false);
   }

   public static List<Method> getMethodsListWithAnnotation(Class<?> var0, Class<? extends Annotation> var1) {
      return getMethodsListWithAnnotation(var0, var1, false, false);
   }

   public static Method[] getMethodsWithAnnotation(Class<?> var0, Class<? extends Annotation> var1, boolean var2, boolean var3) {
      List var4 = getMethodsListWithAnnotation(var0, var1, var2, var3);
      return var4.toArray(new Method[var4.size()]);
   }

   public static List<Method> getMethodsListWithAnnotation(Class<?> var0, Class<? extends Annotation> var1, boolean var2, boolean var3) {
      Validate.isTrue(var0 != null, "The class must not be null");
      Validate.isTrue(var1 != null, "The annotation class must not be null");
      Object var4 = var2 ? getAllSuperclassesAndInterfaces(var0) : new ArrayList();
      var4.add(0, var0);
      ArrayList var5 = new ArrayList();

      for (Class var7 : var4) {
         Method[] var8 = var3 ? var7.getDeclaredMethods() : var7.getMethods();

         for (Method var12 : var8) {
            if (var12.getAnnotation(var1) != null) {
               var5.add(var12);
            }
         }
      }

      return var5;
   }

   public static <A extends Annotation> A getAnnotation(Method var0, Class<A> var1, boolean var2, boolean var3) {
      Validate.isTrue(var0 != null, "The method must not be null");
      Validate.isTrue(var1 != null, "The annotation class must not be null");
      if (!var3 && !MemberUtils.isAccessible(var0)) {
         return null;
      } else {
         Annotation var4 = var0.getAnnotation(var1);
         if (var4 == null && var2) {
            Class var5 = var0.getDeclaringClass();

            for (Class var8 : getAllSuperclassesAndInterfaces(var5)) {
               Method var9;
               try {
                  var9 = var3 ? var8.getDeclaredMethod(var0.getName(), var0.getParameterTypes()) : var8.getMethod(var0.getName(), var0.getParameterTypes());
               } catch (NoSuchMethodException var11) {
                  continue;
               }

               var4 = var9.getAnnotation(var1);
               if (var4 != null) {
                  break;
               }
            }
         }

         return (A)var4;
      }
   }

   private static List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> var0) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList();
         List var2 = ClassUtils.getAllSuperclasses(var0);
         int var3 = 0;
         List var4 = ClassUtils.getAllInterfaces(var0);
         int var5 = 0;

         while (var5 < var4.size() || var3 < var2.size()) {
            Class var6;
            if (var5 >= var4.size()) {
               var6 = (Class)var2.get(var3++);
            } else if (var3 >= var2.size()) {
               var6 = (Class)var4.get(var5++);
            } else if (var5 < var3) {
               var6 = (Class)var4.get(var5++);
            } else if (var3 < var5) {
               var6 = (Class)var2.get(var3++);
            } else {
               var6 = (Class)var4.get(var5++);
            }

            var1.add(var6);
         }

         return var1;
      }
   }
}
