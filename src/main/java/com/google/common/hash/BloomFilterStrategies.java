package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

@ElementTypesAreNonnullByDefault
enum BloomFilterStrategies implements BloomFilter.Strategy {
   MURMUR128_MITZ_32 {
      @Override
      public <T> boolean put(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4) {
         long var5 = var4.bitSize();
         long var7 = Hashing.murmur3_128().hashObject(var1, var2).asLong();
         int var9 = (int)var7;
         int var10 = (int)(var7 >>> 32);
         boolean var11 = false;

         for (int var12 = 1; var12 <= var3; var12++) {
            int var13 = var9 + var12 * var10;
            if (var13 < 0) {
               var13 = ~var13;
            }

            var11 |= var4.set((long)var13 % var5);
         }

         return var11;
      }

      @Override
      public <T> boolean mightContain(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4) {
         long var5 = var4.bitSize();
         long var7 = Hashing.murmur3_128().hashObject(var1, var2).asLong();
         int var9 = (int)var7;
         int var10 = (int)(var7 >>> 32);

         for (int var11 = 1; var11 <= var3; var11++) {
            int var12 = var9 + var11 * var10;
            if (var12 < 0) {
               var12 = ~var12;
            }

            if (!var4.get((long)var12 % var5)) {
               return false;
            }
         }

         return true;
      }
   },
   MURMUR128_MITZ_64 {
      @Override
      public <T> boolean put(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4) {
         long var5 = var4.bitSize();
         byte[] var7 = Hashing.murmur3_128().hashObject(var1, var2).getBytesInternal();
         long var8 = this.lowerEight(var7);
         long var10 = this.upperEight(var7);
         boolean var12 = false;
         long var13 = var8;

         for (int var15 = 0; var15 < var3; var15++) {
            var12 |= var4.set((var13 & Long.MAX_VALUE) % var5);
            var13 += var10;
         }

         return var12;
      }

      @Override
      public <T> boolean mightContain(@ParametricNullness T var1, Funnel<? super T> var2, int var3, BloomFilterStrategies.LockFreeBitArray var4) {
         long var5 = var4.bitSize();
         byte[] var7 = Hashing.murmur3_128().hashObject(var1, var2).getBytesInternal();
         long var8 = this.lowerEight(var7);
         long var10 = this.upperEight(var7);
         long var12 = var8;

         for (int var14 = 0; var14 < var3; var14++) {
            if (!var4.get((var12 & Long.MAX_VALUE) % var5)) {
               return false;
            }

            var12 += var10;
         }

         return true;
      }

      private long lowerEight(byte[] var1) {
         return Longs.fromBytes(var1[7], var1[6], var1[5], var1[4], var1[3], var1[2], var1[1], var1[0]);
      }

      private long upperEight(byte[] var1) {
         return Longs.fromBytes(var1[15], var1[14], var1[13], var1[12], var1[11], var1[10], var1[9], var1[8]);
      }
   };

   private BloomFilterStrategies() {
   }

   static final class LockFreeBitArray {
      private static final int LONG_ADDRESSABLE_BITS = 6;
      final AtomicLongArray data;
      private final LongAddable bitCount;

      LockFreeBitArray(long var1) {
         Preconditions.checkArgument(var1 > 0L, "data length is zero!");
         this.data = new AtomicLongArray(Ints.checkedCast(LongMath.divide(var1, 64L, RoundingMode.CEILING)));
         this.bitCount = LongAddables.create();
      }

      LockFreeBitArray(long[] var1) {
         Preconditions.checkArgument(var1.length > 0, "data length is zero!");
         this.data = new AtomicLongArray(var1);
         this.bitCount = LongAddables.create();
         long var2 = 0L;

         for (long var7 : var1) {
            var2 += (long)Long.bitCount(var7);
         }

         this.bitCount.add(var2);
      }

      boolean set(long var1) {
         if (this.get(var1)) {
            return false;
         } else {
            int var3 = (int)(var1 >>> 6);
            long var4 = 1L << (int)var1;

            long var6;
            long var8;
            do {
               var6 = this.data.get(var3);
               var8 = var6 | var4;
               if (var6 == var8) {
                  return false;
               }
            } while (!this.data.compareAndSet(var3, var6, var8));

            this.bitCount.increment();
            return true;
         }
      }

      boolean get(long var1) {
         return (this.data.get((int)(var1 >>> 6)) & 1L << (int)var1) != 0L;
      }

      public static long[] toPlainArray(AtomicLongArray var0) {
         long[] var1 = new long[var0.length()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = var0.get(var2);
         }

         return var1;
      }

      long bitSize() {
         return (long)this.data.length() * 64L;
      }

      long bitCount() {
         return this.bitCount.sum();
      }

      BloomFilterStrategies.LockFreeBitArray copy() {
         return new BloomFilterStrategies.LockFreeBitArray(toPlainArray(this.data));
      }

      void putAll(BloomFilterStrategies.LockFreeBitArray var1) {
         Preconditions.checkArgument(
            this.data.length() == var1.data.length(), "BitArrays must be of equal length (%s != %s)", this.data.length(), var1.data.length()
         );

         for (int var2 = 0; var2 < this.data.length(); var2++) {
            long var3 = var1.data.get(var2);
            boolean var9 = true;

            long var5;
            long var7;
            do {
               var5 = this.data.get(var2);
               var7 = var5 | var3;
               if (var5 == var7) {
                  var9 = false;
                  break;
               }
            } while (!this.data.compareAndSet(var2, var5, var7));

            if (var9) {
               int var10 = Long.bitCount(var7) - Long.bitCount(var5);
               this.bitCount.add((long)var10);
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof BloomFilterStrategies.LockFreeBitArray) {
            BloomFilterStrategies.LockFreeBitArray var2 = (BloomFilterStrategies.LockFreeBitArray)var1;
            return Arrays.equals(toPlainArray(this.data), toPlainArray(var2.data));
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(toPlainArray(this.data));
      }
   }
}
