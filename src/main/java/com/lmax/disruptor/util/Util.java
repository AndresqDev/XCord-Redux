package com.lmax.disruptor.util;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

public final class Util {
   private static final Unsafe THE_UNSAFE;

   public static int ceilingNextPowerOfTwo(int var0) {
      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }

   public static long getMinimumSequence(Sequence[] var0) {
      return getMinimumSequence(var0, Long.MAX_VALUE);
   }

   public static long getMinimumSequence(Sequence[] var0, long var1) {
      int var3 = 0;

      for (int var4 = var0.length; var3 < var4; var3++) {
         long var5 = var0[var3].get();
         var1 = Math.min(var1, var5);
      }

      return var1;
   }

   public static Sequence[] getSequencesFor(EventProcessor... var0) {
      Sequence[] var1 = new Sequence[var0.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0[var2].getSequence();
      }

      return var1;
   }

   public static Unsafe getUnsafe() {
      return THE_UNSAFE;
   }

   public static int log2(int var0) {
      int var1 = 0;

      while ((var0 >>= 1) != 0) {
         var1++;
      }

      return var1;
   }

   static {
      try {
         PrivilegedExceptionAction var0 = new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
               Field var1 = Unsafe.class.getDeclaredField("theUnsafe");
               var1.setAccessible(true);
               return (Unsafe)var1.get(null);
            }
         };
         THE_UNSAFE = AccessController.doPrivileged(var0);
      } catch (Exception var1) {
         throw new RuntimeException("Unable to load unsafe", var1);
      }
   }
}
