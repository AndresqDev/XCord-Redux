package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeAccess {
   public static final boolean SUPPORTS_GET_AND_SET_REF = hasGetAndSetSupport();
   public static final boolean SUPPORTS_GET_AND_ADD_LONG = hasGetAndAddLongSupport();
   public static final Unsafe UNSAFE = getUnsafe();

   private static Unsafe getUnsafe() {
      Unsafe var0;
      try {
         Field var1 = Unsafe.class.getDeclaredField("theUnsafe");
         var1.setAccessible(true);
         var0 = (Unsafe)var1.get(null);
      } catch (Exception var4) {
         try {
            Constructor var2 = Unsafe.class.getDeclaredConstructor();
            var2.setAccessible(true);
            var0 = (Unsafe)var2.newInstance();
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      }

      return var0;
   }

   private static boolean hasGetAndSetSupport() {
      try {
         Unsafe.class.getMethod("getAndSetObject", Object.class, long.class, Object.class);
         return true;
      } catch (Exception var1) {
         return false;
      }
   }

   private static boolean hasGetAndAddLongSupport() {
      try {
         Unsafe.class.getMethod("getAndAddLong", Object.class, long.class, long.class);
         return true;
      } catch (Exception var1) {
         return false;
      }
   }

   public static long fieldOffset(Class var0, String var1) throws RuntimeException {
      try {
         return UNSAFE.objectFieldOffset(var0.getDeclaredField(var1));
      } catch (NoSuchFieldException var3) {
         throw new RuntimeException(var3);
      }
   }
}
