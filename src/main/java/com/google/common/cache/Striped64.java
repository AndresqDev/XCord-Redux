package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class Striped64 extends Number {
   static final ThreadLocal<int[]> threadHashCode = new ThreadLocal<>();
   static final Random rng = new Random();
   static final int NCPU = Runtime.getRuntime().availableProcessors();
   transient volatile Striped64.Cell[] cells;
   transient volatile long base;
   transient volatile int busy;
   private static final Unsafe UNSAFE;
   private static final long baseOffset;
   private static final long busyOffset;

   final boolean casBase(long var1, long var3) {
      return UNSAFE.compareAndSwapLong(this, baseOffset, var1, var3);
   }

   final boolean casBusy() {
      return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
   }

   abstract long fn(long var1, long var3);

   final void retryUpdate(long var1, int[] var3, boolean var4) {
      int var5;
      if (var3 == null) {
         threadHashCode.set(var3 = new int[1]);
         int var6 = rng.nextInt();
         var5 = var3[0] = var6 == 0 ? 1 : var6;
      } else {
         var5 = var3[0];
      }

      boolean var34 = false;

      while (true) {
         Striped64.Cell[] var7 = this.cells;
         int var9;
         if (this.cells != null && (var9 = var7.length) > 0) {
            Striped64.Cell var8;
            if ((var8 = var7[var9 - 1 & var5]) == null) {
               if (this.busy == 0) {
                  Striped64.Cell var36 = new Striped64.Cell(var1);
                  if (this.busy == 0 && this.casBusy()) {
                     boolean var38 = false;

                     try {
                        Striped64.Cell[] var14 = this.cells;
                        int var15;
                        int var16;
                        if (this.cells != null && (var15 = var14.length) > 0 && var14[var16 = var15 - 1 & var5] == null) {
                           var14[var16] = var36;
                           var38 = true;
                        }
                     } finally {
                        this.busy = 0;
                     }

                     if (var38) {
                        break;
                     }
                     continue;
                  }
               }

               var34 = false;
            } else if (!var4) {
               var4 = true;
            } else {
               long var35;
               if (var8.cas(var35 = var8.value, this.fn(var35, var1))) {
                  break;
               }

               if (var9 >= NCPU || this.cells != var7) {
                  var34 = false;
               } else if (!var34) {
                  var34 = true;
               } else if (this.busy == 0 && this.casBusy()) {
                  try {
                     if (this.cells == var7) {
                        Striped64.Cell[] var37 = new Striped64.Cell[var9 << 1];

                        for (int var39 = 0; var39 < var9; var39++) {
                           var37[var39] = var7[var39];
                        }

                        this.cells = var37;
                     }
                  } finally {
                     this.busy = 0;
                  }

                  var34 = false;
                  continue;
               }
            }

            int var32 = var5 ^ var5 << 13;
            int var33 = var32 ^ var32 >>> 17;
            var5 = var33 ^ var33 << 5;
            var3[0] = var5;
         } else if (this.busy == 0 && this.cells == var7 && this.casBusy()) {
            boolean var12 = false;

            try {
               if (this.cells == var7) {
                  Striped64.Cell[] var13 = new Striped64.Cell[2];
                  var13[var5 & 1] = new Striped64.Cell(var1);
                  this.cells = var13;
                  var12 = true;
               }
            } finally {
               this.busy = 0;
            }

            if (var12) {
               break;
            }
         } else {
            long var10;
            if (this.casBase(var10 = this.base, this.fn(var10, var1))) {
               break;
            }
         }
      }
   }

   final void internalReset(long var1) {
      Striped64.Cell[] var3 = this.cells;
      this.base = var1;
      if (var3 != null) {
         for (Striped64.Cell var6 : var3) {
            if (var6 != null) {
               var6.value = var1;
            }
         }
      }
   }

   private static Unsafe getUnsafe() {
      try {
         return Unsafe.getUnsafe();
      } catch (SecurityException var2) {
         try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
               public Unsafe run() throws Exception {
                  Class<Unsafe> var1 = Unsafe.class;

                  for (Field var5 : var1.getDeclaredFields()) {
                     var5.setAccessible(true);
                     Object var6 = var5.get(null);
                     if (var1.isInstance(var6)) {
                        return var1.cast(var6);
                     }
                  }

                  throw new NoSuchFieldError("the Unsafe");
               }
            });
         } catch (PrivilegedActionException var1) {
            throw new RuntimeException("Could not initialize intrinsics", var1.getCause());
         }
      }
   }

   static {
      try {
         UNSAFE = getUnsafe();
         Class<Striped64> var0 = Striped64.class;
         baseOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("base"));
         busyOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("busy"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class Cell {
      volatile long p0;
      volatile long p1;
      volatile long p2;
      volatile long p3;
      volatile long p4;
      volatile long p5;
      volatile long p6;
      volatile long value;
      volatile long q0;
      volatile long q1;
      volatile long q2;
      volatile long q3;
      volatile long q4;
      volatile long q5;
      volatile long q6;
      private static final Unsafe UNSAFE;
      private static final long valueOffset;

      Cell(long var1) {
         this.value = var1;
      }

      final boolean cas(long var1, long var3) {
         return UNSAFE.compareAndSwapLong(this, valueOffset, var1, var3);
      }

      static {
         try {
            UNSAFE = Striped64.getUnsafe();
            Class<Striped64.Cell> var0 = Striped64.Cell.class;
            valueOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("value"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
