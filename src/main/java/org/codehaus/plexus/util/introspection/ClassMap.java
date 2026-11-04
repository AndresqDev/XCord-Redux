package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

public class ClassMap {
   private static final ClassMap.CacheMiss CACHE_MISS = new ClassMap.CacheMiss();
   private static final Object OBJECT = new Object();
   private final Class clazz;
   private Map<String, Object> methodCache = new Hashtable<>();
   private final MethodMap methodMap = new MethodMap();

   public ClassMap(Class var1) {
      this.clazz = var1;
      this.populateMethodCache();
   }

   Class getCachedClass() {
      return this.clazz;
   }

   public Method findMethod(String var1, Object[] var2) throws MethodMap.AmbiguousException {
      String var3 = makeMethodKey(var1, var2);
      Object var4 = this.methodCache.get(var3);
      if (var4 == CACHE_MISS) {
         return null;
      } else {
         if (var4 == null) {
            try {
               var4 = this.methodMap.find(var1, var2);
            } catch (MethodMap.AmbiguousException var6) {
               this.methodCache.put(var3, CACHE_MISS);
               throw var6;
            }

            if (var4 == null) {
               this.methodCache.put(var3, CACHE_MISS);
            } else {
               this.methodCache.put(var3, var4);
            }
         }

         return (Method)var4;
      }
   }

   private void populateMethodCache() {
      Method[] var2 = getAccessibleMethods(this.clazz);

      for (Method var6 : var2) {
         Method var7 = getPublicMethod(var6);
         if (var7 != null) {
            this.methodMap.add(var7);
            this.methodCache.put(this.makeMethodKey(var7), var7);
         }
      }
   }

   private String makeMethodKey(Method var1) {
      Class[] var2 = var1.getParameterTypes();
      StringBuilder var3 = new StringBuilder(var1.getName());

      for (Class var7 : var2) {
         if (var7.isPrimitive()) {
            if (var7.equals(boolean.class)) {
               var3.append("java.lang.Boolean");
            } else if (var7.equals(byte.class)) {
               var3.append("java.lang.Byte");
            } else if (var7.equals(char.class)) {
               var3.append("java.lang.Character");
            } else if (var7.equals(double.class)) {
               var3.append("java.lang.Double");
            } else if (var7.equals(float.class)) {
               var3.append("java.lang.Float");
            } else if (var7.equals(int.class)) {
               var3.append("java.lang.Integer");
            } else if (var7.equals(long.class)) {
               var3.append("java.lang.Long");
            } else if (var7.equals(short.class)) {
               var3.append("java.lang.Short");
            }
         } else {
            var3.append(var7.getName());
         }
      }

      return var3.toString();
   }

   private static String makeMethodKey(String var0, Object[] var1) {
      StringBuilder var2 = new StringBuilder().append(var0);

      for (Object var6 : var1) {
         Object var7 = var6;
         if (var6 == null) {
            var7 = OBJECT;
         }

         var2.append(var7.getClass().getName());
      }

      return var2.toString();
   }

   private static Method[] getAccessibleMethods(Class var0) {
      Method[] var1 = var0.getMethods();
      if (Modifier.isPublic(var0.getModifiers())) {
         return var1;
      } else {
         ClassMap.MethodInfo[] var2 = new ClassMap.MethodInfo[var1.length];
         int var3 = var1.length;

         while (var3-- > 0) {
            var2[var3] = new ClassMap.MethodInfo(var1[var3]);
         }

         var3 = getAccessibleMethods(var0, var2, 0);
         if (var3 < var1.length) {
            var1 = new Method[var3];
         }

         int var4 = 0;

         for (ClassMap.MethodInfo var8 : var2) {
            if (var8.upcast) {
               var1[var4++] = var8.method;
            }
         }

         return var1;
      }
   }

   private static int getAccessibleMethods(Class var0, ClassMap.MethodInfo[] var1, int var2) {
      int var3 = var1.length;
      if (Modifier.isPublic(var0.getModifiers())) {
         for (int var4 = 0; var4 < var3 && var2 < var3; var4++) {
            try {
               ClassMap.MethodInfo var5 = var1[var4];
               if (!var5.upcast) {
                  var5.tryUpcasting(var0);
                  var2++;
               }
            } catch (NoSuchMethodException var7) {
            }
         }

         if (var2 == var3) {
            return var2;
         }
      }

      Class var8 = var0.getSuperclass();
      if (var8 != null) {
         var2 = getAccessibleMethods(var8, var1, var2);
         if (var2 == var3) {
            return var2;
         }
      }

      Class[] var9 = var0.getInterfaces();
      int var6 = var9.length;

      while (var6-- > 0) {
         var2 = getAccessibleMethods(var9[var6], var1, var2);
         if (var2 == var3) {
            return var2;
         }
      }

      return var2;
   }

   public static Method getPublicMethod(Method var0) {
      Class var1 = var0.getDeclaringClass();
      return (var1.getModifiers() & 1) != 0 ? var0 : getPublicMethod(var1, var0.getName(), var0.getParameterTypes());
   }

   private static Method getPublicMethod(Class var0, String var1, Class[] var2) {
      if ((var0.getModifiers() & 1) != 0) {
         try {
            return var0.getMethod(var1, var2);
         } catch (NoSuchMethodException var10) {
            return null;
         }
      } else {
         Class var3 = var0.getSuperclass();
         if (var3 != null) {
            Method var4 = getPublicMethod(var3, var1, var2);
            if (var4 != null) {
               return var4;
            }
         }

         Class[] var11 = var0.getInterfaces();

         for (Class var8 : var11) {
            Method var9 = getPublicMethod(var8, var1, var2);
            if (var9 != null) {
               return var9;
            }
         }

         return null;
      }
   }

   private static final class CacheMiss {
      private CacheMiss() {
      }
   }

   private static final class MethodInfo {
      Method method = null;
      String name;
      Class[] parameterTypes;
      boolean upcast;

      MethodInfo(Method var1) {
         this.name = var1.getName();
         this.parameterTypes = var1.getParameterTypes();
         this.upcast = false;
      }

      void tryUpcasting(Class var1) throws NoSuchMethodException {
         this.method = var1.getMethod(this.name, this.parameterTypes);
         this.name = null;
         this.parameterTypes = null;
         this.upcast = true;
      }
   }
}
