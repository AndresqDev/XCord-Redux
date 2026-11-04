package com.lmax.disruptor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class SequenceGroups {
   static <T> void addSequences(T var0, AtomicReferenceFieldUpdater<T, Sequence[]> var1, Cursored var2, Sequence... var3) {
      Sequence[] var6;
      Sequence[] var7;
      do {
         var7 = (Sequence[])var1.get(var0);
         var6 = Arrays.copyOf(var7, var7.length + var3.length);
         long var4 = var2.getCursor();
         int var8 = var7.length;

         for (Sequence var12 : var3) {
            var12.set(var4);
            var6[var8++] = var12;
         }
      } while (!var1.compareAndSet(var0, var7, var6));

      long var13 = var2.getCursor();

      for (Sequence var17 : var3) {
         var17.set(var13);
      }
   }

   static <T> boolean removeSequence(T var0, AtomicReferenceFieldUpdater<T, Sequence[]> var1, Sequence var2) {
      while (true) {
         Sequence[] var4 = (Sequence[])var1.get(var0);
         int var3 = countMatching(var4, var2);
         if (0 != var3) {
            int var6 = var4.length;
            Sequence[] var5 = new Sequence[var6 - var3];
            int var7 = 0;

            for (int var8 = 0; var7 < var6; var7++) {
               Sequence var9 = var4[var7];
               if (var2 != var9) {
                  var5[var8++] = var9;
               }
            }

            if (!var1.compareAndSet(var0, var4, var5)) {
               continue;
            }
         }

         return var3 != 0;
      }
   }

   private static <T> int countMatching(T[] var0, T var1) {
      int var2 = 0;

      for (Object var6 : var0) {
         if (var6 == var1) {
            var2++;
         }
      }

      return var2;
   }
}
