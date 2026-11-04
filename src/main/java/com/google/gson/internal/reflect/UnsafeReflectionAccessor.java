package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class UnsafeReflectionAccessor extends ReflectionAccessor {
   private static Class unsafeClass;
   private final Object theUnsafe = getUnsafeInstance();
   private final Field overrideField = getOverrideField();

   @Override
   public void makeAccessible(AccessibleObject var1) {
      boolean var2 = this.makeAccessibleWithUnsafe(var1);
      if (!var2) {
         try {
            var1.setAccessible(true);
         } catch (SecurityException var4) {
            throw new JsonIOException(
               "Gson couldn't modify fields for "
                  + var1
                  + "\nand sun.misc.Unsafe not found.\nEither write a custom type adapter, or make fields accessible, or include sun.misc.Unsafe.",
               var4
            );
         }
      }
   }

   boolean makeAccessibleWithUnsafe(AccessibleObject var1) {
      if (this.theUnsafe != null && this.overrideField != null) {
         try {
            Method var2 = unsafeClass.getMethod("objectFieldOffset", Field.class);
            long var3 = (Long)var2.invoke(this.theUnsafe, this.overrideField);
            Method var5 = unsafeClass.getMethod("putBoolean", Object.class, long.class, boolean.class);
            var5.invoke(this.theUnsafe, var1, var3, true);
            return true;
         } catch (Exception var6) {
         }
      }

      return false;
   }

   private static Object getUnsafeInstance() {
      try {
         unsafeClass = Class.forName("sun.misc.Unsafe");
         Field var0 = unsafeClass.getDeclaredField("theUnsafe");
         var0.setAccessible(true);
         return var0.get(null);
      } catch (Exception var1) {
         return null;
      }
   }

   private static Field getOverrideField() {
      try {
         return AccessibleObject.class.getDeclaredField("override");
      } catch (Exception var1) {
         return null;
      }
   }
}
