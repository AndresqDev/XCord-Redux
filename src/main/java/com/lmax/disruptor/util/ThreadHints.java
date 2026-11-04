package com.lmax.disruptor.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public final class ThreadHints {
   private static final MethodHandle ON_SPIN_WAIT_METHOD_HANDLE;

   private ThreadHints() {
   }

   public static void onSpinWait() {
      if (null != ON_SPIN_WAIT_METHOD_HANDLE) {
         try {
            ON_SPIN_WAIT_METHOD_HANDLE.invokeExact();
         } catch (Throwable var1) {
         }
      }
   }

   static {
      Lookup var0 = MethodHandles.lookup();
      MethodHandle var1 = null;

      try {
         var1 = var0.findStatic(Thread.class, "onSpinWait", MethodType.methodType(void.class));
      } catch (Exception var3) {
      }

      ON_SPIN_WAIT_METHOD_HANDLE = var1;
   }
}
