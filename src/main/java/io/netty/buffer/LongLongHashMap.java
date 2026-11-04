package io.netty.buffer;

final class LongLongHashMap {
   private static final int MASK_TEMPLATE = -2;
   private int mask;
   private long[] array;
   private int maxProbe;
   private long zeroVal;
   private final long emptyVal;

   LongLongHashMap(long var1) {
      this.emptyVal = var1;
      this.zeroVal = var1;
      byte var3 = 32;
      this.array = new long[var3];
      this.mask = var3 - 1;
      this.computeMaskAndProbe();
   }

   public long put(long var1, long var3) {
      if (var1 == 0L) {
         long var11 = this.zeroVal;
         this.zeroVal = var3;
         return var11;
      } else {
         while (true) {
            int var5 = this.index(var1);

            for (int var6 = 0; var6 < this.maxProbe; var6++) {
               long var7 = this.array[var5];
               if (var7 == var1 || var7 == 0L) {
                  long var9 = var7 == 0L ? this.emptyVal : this.array[var5 + 1];
                  this.array[var5] = var1;

                  for (this.array[var5 + 1] = var3; var6 < this.maxProbe; var6++) {
                     var5 = var5 + 2 & this.mask;
                     if (this.array[var5] == var1) {
                        this.array[var5] = 0L;
                        var9 = this.array[var5 + 1];
                        break;
                     }
                  }

                  return var9;
               }

               var5 = var5 + 2 & this.mask;
            }

            this.expand();
         }
      }
   }

   public void remove(long var1) {
      if (var1 == 0L) {
         this.zeroVal = this.emptyVal;
      } else {
         int var3 = this.index(var1);

         for (int var4 = 0; var4 < this.maxProbe; var4++) {
            long var5 = this.array[var3];
            if (var5 == var1) {
               this.array[var3] = 0L;
               break;
            }

            var3 = var3 + 2 & this.mask;
         }
      }
   }

   public long get(long var1) {
      if (var1 == 0L) {
         return this.zeroVal;
      } else {
         int var3 = this.index(var1);

         for (int var4 = 0; var4 < this.maxProbe; var4++) {
            long var5 = this.array[var3];
            if (var5 == var1) {
               return this.array[var3 + 1];
            }

            var3 = var3 + 2 & this.mask;
         }

         return this.emptyVal;
      }
   }

   private int index(long var1) {
      var1 ^= var1 >>> 33;
      var1 *= -49064778989728563L;
      var1 ^= var1 >>> 33;
      var1 *= -4265267296055464877L;
      var1 ^= var1 >>> 33;
      return (int)var1 & this.mask;
   }

   private void expand() {
      long[] var1 = this.array;
      this.array = new long[var1.length * 2];
      this.computeMaskAndProbe();

      for (byte var2 = 0; var2 < var1.length; var2 += 2) {
         long var3 = var1[var2];
         if (var3 != 0L) {
            long var5 = var1[var2 + 1];
            this.put(var3, var5);
         }
      }
   }

   private void computeMaskAndProbe() {
      int var1 = this.array.length;
      this.mask = var1 - 1 & -2;
      this.maxProbe = (int)Math.log((double)var1);
   }
}
