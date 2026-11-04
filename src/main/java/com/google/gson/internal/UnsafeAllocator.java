package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {
   public abstract <T> T newInstance(Class<T> var1) throws Exception;

   public static UnsafeAllocator create() {
      try {
         Class var8 = Class.forName("sun.misc.Unsafe");
         Field var9 = var8.getDeclaredField("theUnsafe");
         var9.setAccessible(true);
         final Object var10 = var9.get(null);
         final Method var3 = var8.getMethod("allocateInstance", Class.class);
         return new UnsafeAllocator() {
            @Override
            public <T> T newInstance(Class<T> var1) throws Exception {
               assertInstantiable(var1);
               return (T)var3.invoke(var10, var1);
            }
         };
      } catch (Exception var6) {
         try {
            Method var7 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            var7.setAccessible(true);
            final int var1 = (Integer)var7.invoke(null, Object.class);
            final Method var2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, int.class);
            var2.setAccessible(true);
            return new UnsafeAllocator() {
               @Override
               public <T> T newInstance(Class<T> var1x) throws Exception {
                  assertInstantiable(var1x);
                  return (T)var2.invoke(null, var1x, var1);
               }
            };
         } catch (Exception var5) {
            try {
               final Method var0 = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
               var0.setAccessible(true);
               return new UnsafeAllocator() {
                  @Override
                  public <T> T newInstance(Class<T> var1) throws Exception {
                     assertInstantiable(var1);
                     return (T)var0.invoke(null, var1, Object.class);
                  }
               };
            } catch (Exception var4) {
               return new UnsafeAllocator() {
                  @Override
                  public <T> T newInstance(Class<T> var1) {
                     throw new UnsupportedOperationException("Cannot allocate " + var1);
                  }
               };
            }
         }
      }
   }

   static void assertInstantiable(Class<?> var0) {
      int var1 = var0.getModifiers();
      if (Modifier.isInterface(var1)) {
         throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + var0.getName());
      } else if (Modifier.isAbstract(var1)) {
         throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + var0.getName());
      }
   }
}
