package org.jline.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class Signals {
   private Signals() {
   }

   public static Object register(String var0, Runnable var1) {
      Objects.requireNonNull(var1);
      return register(var0, var1, var1.getClass().getClassLoader());
   }

   public static Object register(String var0, Runnable var1, ClassLoader var2) {
      try {
         Class var3 = Class.forName("sun.misc.SignalHandler");
         Object var4 = Proxy.newProxyInstance(var2, new Class[]{var3}, (var3x, var4x, var5x) -> {
            if (var4x.getDeclaringClass() == Object.class) {
               if ("toString".equals(var4x.getName())) {
                  return var1.toString();
               }
            } else if (var4x.getDeclaringClass() == var3) {
               Log.trace(() -> "Calling handler " + toString(var1) + " for signal " + var0);
               var1.run();
            }

            return null;
         });
         return doRegister(var0, var4);
      } catch (Exception var5) {
         Log.debug("Error registering handler for signal ", var0, var5);
         return null;
      }
   }

   public static Object registerDefault(String var0) {
      try {
         Class var1 = Class.forName("sun.misc.SignalHandler");
         return doRegister(var0, var1.getField("SIG_DFL").get(null));
      } catch (Exception var2) {
         Log.debug("Error registering default handler for signal ", var0, var2);
         return null;
      }
   }

   public static void unregister(String var0, Object var1) {
      try {
         if (var1 != null) {
            doRegister(var0, var1);
         }
      } catch (Exception var3) {
         Log.debug("Error unregistering handler for signal ", var0, var3);
      }
   }

   private static Object doRegister(String var0, Object var1) throws Exception {
      Log.trace(() -> "Registering signal " + var0 + " with handler " + toString(var1));
      Class var2 = Class.forName("sun.misc.Signal");
      Constructor var3 = var2.getConstructor(String.class);

      Object var4;
      try {
         var4 = var3.newInstance(var0);
      } catch (IllegalArgumentException var6) {
         Log.trace(() -> "Ignoring unsupported signal " + var0);
         return null;
      }

      Class var5 = Class.forName("sun.misc.SignalHandler");
      return var2.getMethod("handle", var2, var5).invoke(null, var4, var1);
   }

   private static String toString(Object var0) {
      try {
         Class var1 = Class.forName("sun.misc.SignalHandler");
         if (var0 == var1.getField("SIG_DFL").get(null)) {
            return "SIG_DFL";
         }

         if (var0 == var1.getField("SIG_IGN").get(null)) {
            return "SIG_IGN";
         }
      } catch (Throwable var2) {
      }

      return var0 != null ? var0.toString() : "null";
   }
}
