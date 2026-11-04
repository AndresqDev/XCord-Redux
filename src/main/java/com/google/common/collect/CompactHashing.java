package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import java.util.Arrays;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class CompactHashing {
   static final byte UNSET = 0;
   private static final int HASH_TABLE_BITS_MAX_BITS = 5;
   static final int MODIFICATION_COUNT_INCREMENT = 32;
   static final int HASH_TABLE_BITS_MASK = 31;
   static final int MAX_SIZE = 1073741823;
   static final int DEFAULT_SIZE = 3;
   private static final int MIN_HASH_TABLE_SIZE = 4;
   private static final int BYTE_MAX_SIZE = 256;
   private static final int BYTE_MASK = 255;
   private static final int SHORT_MAX_SIZE = 65536;
   private static final int SHORT_MASK = 65535;

   private CompactHashing() {
   }

   static int tableSize(int var0) {
      return Math.max(4, Hashing.closedTableSize(var0 + 1, 1.0));
   }

   static Object createTable(int var0) {
      if (var0 < 2 || var0 > 1073741824 || Integer.highestOneBit(var0) != var0) {
         throw new IllegalArgumentException(new StringBuilder(52).append("must be power of 2 between 2^1 and 2^30: ").append(var0).toString());
      } else if (var0 <= 256) {
         return new byte[var0];
      } else {
         return var0 <= 65536 ? new short[var0] : new int[var0];
      }
   }

   static void tableClear(Object var0) {
      if (var0 instanceof byte[]) {
         Arrays.fill((byte[])var0, (byte)0);
      } else if (var0 instanceof short[]) {
         Arrays.fill((short[])var0, (short)0);
      } else {
         Arrays.fill((int[])var0, 0);
      }
   }

   static int tableGet(Object var0, int var1) {
      if (var0 instanceof byte[]) {
         return ((byte[])var0)[var1] & 0xFF;
      } else {
         return var0 instanceof short[] ? ((short[])var0)[var1] & 65535 : ((int[])var0)[var1];
      }
   }

   static void tableSet(Object var0, int var1, int var2) {
      if (var0 instanceof byte[]) {
         ((byte[])var0)[var1] = (byte)var2;
      } else if (var0 instanceof short[]) {
         ((short[])var0)[var1] = (short)var2;
      } else {
         ((int[])var0)[var1] = var2;
      }
   }

   static int newCapacity(int var0) {
      return (var0 < 32 ? 4 : 2) * (var0 + 1);
   }

   static int getHashPrefix(int var0, int var1) {
      return var0 & ~var1;
   }

   static int getNext(int var0, int var1) {
      return var0 & var1;
   }

   static int maskCombine(int var0, int var1, int var2) {
      return var0 & ~var2 | var1 & var2;
   }

   static int remove(Object var0, Object var1, int var2, Object var3, int[] var4, Object[] var5, Object[] var6) {
      int var7 = Hashing.smearedHash(var0);
      int var8 = var7 & var2;
      int var9 = tableGet(var3, var8);
      if (var9 == 0) {
         return -1;
      } else {
         int var10 = getHashPrefix(var7, var2);
         int var11 = -1;

         do {
            int var12 = var9 - 1;
            int var13 = var4[var12];
            if (getHashPrefix(var13, var2) == var10 && Objects.equal(var0, var5[var12]) && (var6 == null || Objects.equal(var1, var6[var12]))) {
               int var14 = getNext(var13, var2);
               if (var11 == -1) {
                  tableSet(var3, var8, var14);
               } else {
                  var4[var11] = maskCombine(var4[var11], var14, var2);
               }

               return var12;
            }

            var11 = var12;
            var9 = getNext(var13, var2);
         } while (var9 != 0);

         return -1;
      }
   }
}
