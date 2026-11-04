package org.codehaus.plexus.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {
   public static Field getFieldByNameIncludingSuperclasses(String var0, Class<?> var1) {
      Field var2 = null;

      try {
         var2 = var1.getDeclaredField(var0);
      } catch (NoSuchFieldException var5) {
         Class var4 = var1.getSuperclass();
         if (var4 != null) {
            var2 = getFieldByNameIncludingSuperclasses(var0, var4);
         }
      }

      return var2;
   }

   public static List<Field> getFieldsIncludingSuperclasses(Class<?> var0) {
      ArrayList var1 = new ArrayList<>(Arrays.asList(var0.getDeclaredFields()));
      Class var2 = var0.getSuperclass();
      if (var2 != null) {
         var1.addAll(getFieldsIncludingSuperclasses(var2));
      }

      return var1;
   }

   public static Method getSetter(String var0, Class<?> var1) {
      Method[] var2 = var1.getMethods();
      var0 = "set" + StringUtils.capitalizeFirstLetter(var0);

      for (Method var6 : var2) {
         if (var6.getName().equals(var0) && isSetter(var6)) {
            return var6;
         }
      }

      return null;
   }

   public static List<Method> getSetters(Class<?> var0) {
      Method[] var1 = var0.getMethods();
      ArrayList var2 = new ArrayList();

      for (Method var6 : var1) {
         if (isSetter(var6)) {
            var2.add(var6);
         }
      }

      return var2;
   }

   public static Class<?> getSetterType(Method var0) {
      if (!isSetter(var0)) {
         throw new RuntimeException("The method " + var0.getDeclaringClass().getName() + "." + var0.getName() + " is not a setter.");
      } else {
         return var0.getParameterTypes()[0];
      }
   }

   public static void setVariableValueInObject(Object var0, String var1, Object var2) throws IllegalAccessException {
      Field var3 = getFieldByNameIncludingSuperclasses(var1, var0.getClass());
      var3.setAccessible(true);
      var3.set(var0, var2);
   }

   public static Object getValueIncludingSuperclasses(String var0, Object var1) throws IllegalAccessException {
      Field var2 = getFieldByNameIncludingSuperclasses(var0, var1.getClass());
      var2.setAccessible(true);
      return var2.get(var1);
   }

   public static Map<String, Object> getVariablesAndValuesIncludingSuperclasses(Object var0) throws IllegalAccessException {
      HashMap var1 = new HashMap();
      gatherVariablesAndValuesIncludingSuperclasses(var0, var1);
      return var1;
   }

   public static boolean isSetter(Method var0) {
      return var0.getReturnType().equals(void.class) && !Modifier.isStatic(var0.getModifiers()) && var0.getParameterTypes().length == 1;
   }

   private static void gatherVariablesAndValuesIncludingSuperclasses(Object var0, Map<String, Object> var1) throws IllegalAccessException {
      Class var2 = var0.getClass();
      Field[] var3 = var2.getDeclaredFields();
      AccessibleObject.setAccessible(var3, true);

      for (Field var7 : var3) {
         var1.put(var7.getName(), var7.get(var0));
      }

      Class var8 = var2.getSuperclass();
      if (!Object.class.equals(var8)) {
         gatherVariablesAndValuesIncludingSuperclasses(var8, var1);
      }
   }
}
