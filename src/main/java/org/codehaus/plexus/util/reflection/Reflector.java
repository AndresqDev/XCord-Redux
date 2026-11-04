package org.codehaus.plexus.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class Reflector {
   private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
   private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
   private Map<String, Map<String, Map<String, Method>>> classMaps = new HashMap<>();

   public <T> T newInstance(Class<T> var1, Object[] var2) throws ReflectorException {
      if (var2 == null) {
         var2 = new Object[0];
      }

      Class[] var3 = new Class[var2.length];
      int var4 = 0;

      for (int var5 = var2.length; var4 < var5; var4++) {
         var3[var4] = var2[var4].getClass();
      }

      try {
         Constructor var13 = this.getConstructor(var1, var3);
         if (var13 != null) {
            return (T)var13.newInstance(var2);
         } else {
            StringBuilder var14 = new StringBuilder();
            var14.append("Constructor not found for class: ");
            var14.append(var1.getName());
            var14.append(" with specified or ancestor parameter classes: ");

            for (Class var9 : var3) {
               var14.append(var9.getName());
               var14.append(',');
            }

            var14.setLength(var14.length() - 1);
            throw new ReflectorException(var14.toString());
         }
      } catch (InstantiationException var10) {
         throw new ReflectorException(var10);
      } catch (InvocationTargetException var11) {
         throw new ReflectorException(var11);
      } catch (IllegalAccessException var12) {
         throw new ReflectorException(var12);
      }
   }

   public <T> T getSingleton(Class<T> var1, Object[] var2) throws ReflectorException {
      Class[] var3 = new Class[var2.length];
      int var4 = 0;

      for (int var5 = var2.length; var4 < var5; var4++) {
         var3[var4] = var2[var4].getClass();
      }

      try {
         Method var8 = this.getMethod(var1, "getInstance", var3);
         return (T)var8.invoke(null, var2);
      } catch (InvocationTargetException var6) {
         throw new ReflectorException(var6);
      } catch (IllegalAccessException var7) {
         throw new ReflectorException(var7);
      }
   }

   public Object invoke(Object var1, String var2, Object[] var3) throws ReflectorException {
      if (var3 == null) {
         var3 = new Object[0];
      }

      Class[] var4 = new Class[var3.length];
      int var5 = 0;

      for (int var6 = var3.length; var5 < var6; var5++) {
         var4[var5] = var3[var5].getClass();
      }

      try {
         Method var13 = this.getMethod(var1.getClass(), var2, var4);
         if (var13 != null) {
            return var13.invoke(var1, var3);
         } else {
            StringBuilder var14 = new StringBuilder();
            var14.append("Singleton-producing method named '").append(var2).append("' not found with specified parameter classes: ");

            for (Class var10 : var4) {
               var14.append(var10.getName());
               var14.append(',');
            }

            var14.setLength(var14.length() - 1);
            throw new ReflectorException(var14.toString());
         }
      } catch (InvocationTargetException var11) {
         throw new ReflectorException(var11);
      } catch (IllegalAccessException var12) {
         throw new ReflectorException(var12);
      }
   }

   public Object getStaticField(Class var1, String var2) throws ReflectorException {
      try {
         Field var3 = var1.getField(var2);
         return var3.get(null);
      } catch (SecurityException var4) {
         throw new ReflectorException(var4);
      } catch (NoSuchFieldException var5) {
         throw new ReflectorException(var5);
      } catch (IllegalArgumentException var6) {
         throw new ReflectorException(var6);
      } catch (IllegalAccessException var7) {
         throw new ReflectorException(var7);
      }
   }

   public Object getField(Object var1, String var2) throws ReflectorException {
      return this.getField(var1, var2, false);
   }

   public Object getField(Object var1, String var2, boolean var3) throws ReflectorException {
      Class var4 = var1.getClass();

      while (var4 != null) {
         try {
            Field var5 = var4.getDeclaredField(var2);
            boolean var6 = false;
            if (!var5.isAccessible() && var3) {
               var5.setAccessible(true);
               var6 = true;
            }

            Object var7 = var5.get(var1);
            if (var6) {
               var5.setAccessible(false);
            }

            return var7;
         } catch (SecurityException var8) {
            throw new ReflectorException(var8);
         } catch (NoSuchFieldException var9) {
            if (var4 == Object.class) {
               throw new ReflectorException(var9);
            }

            var4 = var4.getSuperclass();
         } catch (IllegalAccessException var10) {
            throw new ReflectorException(var10);
         }
      }

      return null;
   }

   public Object invokeStatic(Class var1, String var2, Object[] var3) throws ReflectorException {
      if (var3 == null) {
         var3 = new Object[0];
      }

      Class[] var4 = new Class[var3.length];
      int var5 = 0;

      for (int var6 = var3.length; var5 < var6; var5++) {
         var4[var5] = var3[var5].getClass();
      }

      try {
         Method var13 = this.getMethod(var1, var2, var4);
         if (var13 != null) {
            return var13.invoke(null, var3);
         } else {
            StringBuilder var14 = new StringBuilder();
            var14.append("Singleton-producing method named '").append(var2).append("' not found with specified parameter classes: ");

            for (Class var10 : var4) {
               var14.append(var10.getName());
               var14.append(',');
            }

            var14.setLength(var14.length() - 1);
            throw new ReflectorException(var14.toString());
         }
      } catch (InvocationTargetException var11) {
         throw new ReflectorException(var11);
      } catch (IllegalAccessException var12) {
         throw new ReflectorException(var12);
      }
   }

   public <T> Constructor<T> getConstructor(Class<T> var1, Class[] var2) throws ReflectorException {
      Map var3 = this.getConstructorMap(var1);
      StringBuilder var4 = new StringBuilder(200);
      var4.append("(");

      for (Class var8 : var2) {
         var4.append(var8.getName());
         var4.append(",");
      }

      if (var2.length > 0) {
         var4.setLength(var4.length() - 1);
      }

      var4.append(")");
      String var19 = var4.toString();
      Constructor var18;
      synchronized (var19.intern()) {
         var18 = (Constructor)var3.get(var19);
         if (var18 == null) {
            Constructor[] var20 = var1.getConstructors();

            for (Constructor var12 : var20) {
               Class[] var13 = var12.getParameterTypes();
               if (var2.length == var13.length) {
                  int var14 = 0;

                  for (int var15 = var2.length; var14 < var15; var14++) {
                     if (!var13[var14].isAssignableFrom(var2[var14])) {
                     }
                  }

                  var18 = var12;
                  var3.put(var19, var12);
               }
            }
         }
      }

      if (var18 == null) {
         throw new ReflectorException("Error retrieving constructor object for: " + var1.getName() + var19);
      } else {
         return var18;
      }
   }

   public Object getObjectProperty(Object var1, String var2) throws ReflectorException {
      if (var2 != null && var2.trim().length() >= 1) {
         String var4 = "get" + Character.toUpperCase(var2.charAt(0));
         if (var2.trim().length() > 1) {
            var4 = var4 + var2.substring(1).trim();
         }

         Class var5 = var1.getClass();
         Class[] var6 = new Class[0];
         Method var7 = this._getMethod(var5, var4, var6);
         if (var7 == null) {
            var7 = this._getMethod(var5, var2, var6);
         }

         if (var7 != null) {
            try {
               Object var3 = var7.invoke(var1);
            } catch (IllegalAccessException var11) {
               throw new ReflectorException("Error retrieving property '" + var2 + "' from '" + var5 + "'", var11);
            } catch (InvocationTargetException var12) {
               throw new ReflectorException("Error retrieving property '" + var2 + "' from '" + var5 + "'", var12);
            }
         }

         Object var13;
         if (var7 != null) {
            try {
               var13 = var7.invoke(var1);
            } catch (IllegalAccessException var9) {
               throw new ReflectorException("Error retrieving property '" + var2 + "' from '" + var5 + "'", var9);
            } catch (InvocationTargetException var10) {
               throw new ReflectorException("Error retrieving property '" + var2 + "' from '" + var5 + "'", var10);
            }
         } else {
            var13 = this.getField(var1, var2, true);
            if (var13 == null) {
               throw new ReflectorException(
                  "Neither method: '"
                     + var2
                     + "' nor bean accessor: '"
                     + var4
                     + "' can be found for class: '"
                     + var5
                     + "', and retrieval of field: '"
                     + var2
                     + "' returned null as value."
               );
            }
         }

         return var13;
      } else {
         throw new ReflectorException("Cannot retrieve value for empty property.");
      }
   }

   public Method getMethod(Class var1, String var2, Class[] var3) throws ReflectorException {
      Method var4 = this._getMethod(var1, var2, var3);
      if (var4 == null) {
         throw new ReflectorException("Method: '" + var2 + "' not found in class: '" + var1 + "'");
      } else {
         return var4;
      }
   }

   private Method _getMethod(Class var1, String var2, Class[] var3) throws ReflectorException {
      Map var4 = this.getMethodMap(var1, var2);
      StringBuilder var5 = new StringBuilder(200);
      var5.append("(");

      for (Class var9 : var3) {
         var5.append(var9.getName());
         var5.append(",");
      }

      var5.append(")");
      String var21 = var5.toString();
      synchronized (var21.intern()) {
         Method var20 = (Method)var4.get(var21);
         if (var20 == null) {
            Method[] var22 = var1.getMethods();

            for (Method var13 : var22) {
               String var14 = var13.getName();
               if (var2.equals(var14)) {
                  Class[] var15 = var13.getParameterTypes();
                  if (var3.length == var15.length) {
                     int var16 = 0;

                     for (int var17 = var3.length; var16 < var17; var16++) {
                        if (!var15[var16].isAssignableFrom(var3[var16])) {
                        }
                     }

                     var20 = var13;
                     var4.put(var21, var13);
                  }
               }
            }
         }

         return var20;
      }
   }

   private <T> Map<String, Constructor<T>> getConstructorMap(Class<T> var1) throws ReflectorException {
      return (Map<String, Constructor<T>>)this.getMethodMap(var1, "$$CONSTRUCTOR$$");
   }

   private Map<String, ?> getMethodMap(Class var1, String var2) throws ReflectorException {
      if (var1 == null) {
         return null;
      } else {
         String var4 = var1.getName();
         synchronized (var4.intern()) {
            Map var6 = this.classMaps.get(var4);
            Object var3;
            if (var6 == null) {
               HashMap var13 = new HashMap();
               var3 = new HashMap();
               var13.put(var2, var3);
               this.classMaps.put(var4, var13);
            } else {
               String var7 = var4 + "::" + var2;
               synchronized (var7.intern()) {
                  var3 = (Map)var6.get(var2);
                  if (var3 == null) {
                     var3 = new HashMap();
                     var6.put(var2, var3);
                  }
               }
            }

            return (Map<String, ?>)var3;
         }
      }
   }
}
