package io.netty.buffer;

abstract class SizeClasses implements SizeClassesMetric {
   static final int LOG2_QUANTUM = 4;
   private static final int LOG2_SIZE_CLASS_GROUP = 2;
   private static final int LOG2_MAX_LOOKUP_SIZE = 12;
   private static final int INDEX_IDX = 0;
   private static final int LOG2GROUP_IDX = 1;
   private static final int LOG2DELTA_IDX = 2;
   private static final int NDELTA_IDX = 3;
   private static final int PAGESIZE_IDX = 4;
   private static final int SUBPAGE_IDX = 5;
   private static final int LOG2_DELTA_LOOKUP_IDX = 6;
   private static final byte no = 0;
   private static final byte yes = 1;
   protected final int pageSize;
   protected final int pageShifts;
   protected final int chunkSize;
   protected final int directMemoryCacheAlignment;
   final int nSizes;
   final int nSubpages;
   final int nPSizes;
   final int lookupMaxSize;
   final int smallMaxSizeIdx;
   private final int[] pageIdx2sizeTab;
   private final int[] sizeIdx2sizeTab;
   private final int[] size2idxTab;

   protected SizeClasses(int var1, int var2, int var3, int var4) {
      int var5 = PoolThreadCache.log2(var3) + 1 - 4;
      short[][] var6 = new short[var5 << 2][7];
      int var7 = -1;
      int var8 = 0;
      int var9 = 0;
      byte var10 = 4;
      int var11 = 4;
      byte var12 = 4;

      for (int var13 = 0; var13 < var12; var8++) {
         short[] var14 = newSizeClass(var8, var10, var11, var13, var2);
         var6[var8] = var14;
         var9 = sizeOf(var14, var4);
         var13++;
      }

      for (int var19 = var10 + 2; var9 < var3; var11++) {
         for (int var20 = 1; var20 <= var12 && var9 < var3; var8++) {
            short[] var22 = newSizeClass(var8, var19, var11, var20, var2);
            var6[var8] = var22;
            var9 = var7 = sizeOf(var22, var4);
            var20++;
         }

         var19++;
      }

      assert var3 == var7;

      int var21 = 0;
      int var23 = 0;
      int var15 = 0;
      int var16 = 0;

      for (int var17 = 0; var17 < var8; var17++) {
         short[] var18 = var6[var17];
         if (var18[4] == 1) {
            var15++;
         }

         if (var18[5] == 1) {
            var16++;
            var21 = var17;
         }

         if (var18[6] != 0) {
            var23 = sizeOf(var18, var4);
         }
      }

      this.smallMaxSizeIdx = var21;
      this.lookupMaxSize = var23;
      this.nPSizes = var15;
      this.nSubpages = var16;
      this.nSizes = var8;
      this.pageSize = var1;
      this.pageShifts = var2;
      this.chunkSize = var3;
      this.directMemoryCacheAlignment = var4;
      this.sizeIdx2sizeTab = newIdx2SizeTab(var6, var8, var4);
      this.pageIdx2sizeTab = newPageIdx2sizeTab(var6, var8, var15, var4);
      this.size2idxTab = newSize2idxTab(var23, var6);
   }

   private static short[] newSizeClass(int var0, int var1, int var2, int var3, int var4) {
      int var5;
      if (var2 >= var4) {
         var5 = 1;
      } else {
         int var6 = 1 << var4;
         int var7 = calculateSize(var1, var3, var2);
         var5 = var7 == var7 / var6 * var6 ? 1 : 0;
      }

      int var11 = var3 == 0 ? 0 : PoolThreadCache.log2(var3);
      boolean var12 = 1 << var11 < var3;
      int var8 = var2 + var11 == var1 ? var1 + 1 : var1;
      if (var8 == var1) {
         var12 = true;
      }

      int var9 = var8 < var4 + 2 ? 1 : 0;
      int var10 = var8 >= 12 && (var8 != 12 || var12) ? 0 : var2;
      return new short[]{(short)var0, (short)var1, (short)var2, (short)var3, (short)var5, (short)var9, (short)var10};
   }

   private static int[] newIdx2SizeTab(short[][] var0, int var1, int var2) {
      int[] var3 = new int[var1];

      for (int var4 = 0; var4 < var1; var4++) {
         short[] var5 = var0[var4];
         var3[var4] = sizeOf(var5, var2);
      }

      return var3;
   }

   private static int calculateSize(int var0, int var1, int var2) {
      return (1 << var0) + (var1 << var2);
   }

   private static int sizeOf(short[] var0, int var1) {
      short var2 = var0[1];
      short var3 = var0[2];
      short var4 = var0[3];
      int var5 = calculateSize(var2, var4, var3);
      return alignSizeIfNeeded(var5, var1);
   }

   private static int[] newPageIdx2sizeTab(short[][] var0, int var1, int var2, int var3) {
      int[] var4 = new int[var2];
      int var5 = 0;

      for (int var6 = 0; var6 < var1; var6++) {
         short[] var7 = var0[var6];
         if (var7[4] == 1) {
            var4[var5++] = sizeOf(var7, var3);
         }
      }

      return var4;
   }

   private static int[] newSize2idxTab(int var0, short[][] var1) {
      int[] var2 = new int[var0 >> 4];
      int var3 = 0;
      int var4 = 0;

      for (int var5 = 0; var4 <= var0; var5++) {
         short var6 = var1[var5][2];

         for (int var7 = 1 << var6 - 4; var4 <= var0 && var7-- > 0; var4 = var3 + 1 << 4) {
            var2[var3++] = var5;
         }
      }

      return var2;
   }

   @Override
   public int sizeIdx2size(int var1) {
      return this.sizeIdx2sizeTab[var1];
   }

   @Override
   public int sizeIdx2sizeCompute(int var1) {
      int var2 = var1 >> 2;
      int var3 = var1 & 3;
      int var4 = var2 == 0 ? 0 : 32 << var2;
      int var5 = var2 == 0 ? 1 : var2;
      int var6 = var5 + 4 - 1;
      int var7 = var3 + 1 << var6;
      return var4 + var7;
   }

   @Override
   public long pageIdx2size(int var1) {
      return (long)this.pageIdx2sizeTab[var1];
   }

   @Override
   public long pageIdx2sizeCompute(int var1) {
      int var2 = var1 >> 2;
      int var3 = var1 & 3;
      long var4 = var2 == 0 ? 0L : 1L << this.pageShifts + 2 - 1 << var2;
      int var6 = var2 == 0 ? 1 : var2;
      int var7 = var6 + this.pageShifts - 1;
      int var8 = var3 + 1 << var7;
      return var4 + (long)var8;
   }

   @Override
   public int size2SizeIdx(int var1) {
      if (var1 == 0) {
         return 0;
      } else if (var1 > this.chunkSize) {
         return this.nSizes;
      } else {
         var1 = alignSizeIfNeeded(var1, this.directMemoryCacheAlignment);
         if (var1 <= this.lookupMaxSize) {
            return this.size2idxTab[var1 - 1 >> 4];
         } else {
            int var2 = PoolThreadCache.log2((var1 << 1) - 1);
            int var3 = var2 < 7 ? 0 : var2 - 6;
            int var4 = var3 << 2;
            int var5 = var2 < 7 ? 4 : var2 - 2 - 1;
            int var6 = -1 << var5;
            int var7 = (var1 - 1 & var6) >> var5 & 3;
            return var4 + var7;
         }
      }
   }

   @Override
   public int pages2pageIdx(int var1) {
      return this.pages2pageIdxCompute(var1, false);
   }

   @Override
   public int pages2pageIdxFloor(int var1) {
      return this.pages2pageIdxCompute(var1, true);
   }

   private int pages2pageIdxCompute(int var1, boolean var2) {
      int var3 = var1 << this.pageShifts;
      if (var3 > this.chunkSize) {
         return this.nPSizes;
      } else {
         int var4 = PoolThreadCache.log2((var3 << 1) - 1);
         int var5 = var4 < 2 + this.pageShifts ? 0 : var4 - (2 + this.pageShifts);
         int var6 = var5 << 2;
         int var7 = var4 < 2 + this.pageShifts + 1 ? this.pageShifts : var4 - 2 - 1;
         int var8 = -1 << var7;
         int var9 = (var3 - 1 & var8) >> var7 & 3;
         int var10 = var6 + var9;
         if (var2 && this.pageIdx2sizeTab[var10] > var1 << this.pageShifts) {
            var10--;
         }

         return var10;
      }
   }

   private static int alignSizeIfNeeded(int var0, int var1) {
      if (var1 <= 0) {
         return var0;
      } else {
         int var2 = var0 & var1 - 1;
         return var2 == 0 ? var0 : var0 + var1 - var2;
      }
   }

   @Override
   public int normalizeSize(int var1) {
      if (var1 == 0) {
         return this.sizeIdx2sizeTab[0];
      } else {
         var1 = alignSizeIfNeeded(var1, this.directMemoryCacheAlignment);
         if (var1 <= this.lookupMaxSize) {
            int var2 = this.sizeIdx2sizeTab[this.size2idxTab[var1 - 1 >> 4]];

            assert var2 == normalizeSizeCompute(var1);

            return var2;
         } else {
            return normalizeSizeCompute(var1);
         }
      }
   }

   private static int normalizeSizeCompute(int var0) {
      int var1 = PoolThreadCache.log2((var0 << 1) - 1);
      int var2 = var1 < 7 ? 4 : var1 - 2 - 1;
      int var3 = 1 << var2;
      int var4 = var3 - 1;
      return var0 + var4 & ~var4;
   }
}
