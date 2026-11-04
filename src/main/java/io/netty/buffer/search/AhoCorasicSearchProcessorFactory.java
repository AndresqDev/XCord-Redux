package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class AhoCorasicSearchProcessorFactory extends AbstractMultiSearchProcessorFactory {
   private final int[] jumpTable;
   private final int[] matchForNeedleId;
   static final int BITS_PER_SYMBOL = 8;
   static final int ALPHABET_SIZE = 256;

   AhoCorasicSearchProcessorFactory(byte[]... var1) {
      for (byte[] var5 : var1) {
         if (var5.length == 0) {
            throw new IllegalArgumentException("Needle must be non empty");
         }
      }

      AhoCorasicSearchProcessorFactory.Context var6 = buildTrie(var1);
      this.jumpTable = var6.jumpTable;
      this.matchForNeedleId = var6.matchForNeedleId;
      this.linkSuffixes();

      for (int var7 = 0; var7 < this.jumpTable.length; var7++) {
         if (this.matchForNeedleId[this.jumpTable[var7] >> 8] >= 0) {
            this.jumpTable[var7] = -this.jumpTable[var7];
         }
      }
   }

   private static AhoCorasicSearchProcessorFactory.Context buildTrie(byte[][] var0) {
      ArrayList var1 = new ArrayList(256);

      for (int var2 = 0; var2 < 256; var2++) {
         var1.add(-1);
      }

      ArrayList var13 = new ArrayList();
      var13.add(-1);

      for (int var3 = 0; var3 < var0.length; var3++) {
         byte[] var4 = var0[var3];
         int var5 = 0;

         for (byte var9 : var4) {
            int var10 = var9 & 255;
            int var11 = var5 + var10;
            if ((Integer)var1.get(var11) == -1) {
               var1.set(var11, var1.size());

               for (int var12 = 0; var12 < 256; var12++) {
                  var1.add(-1);
               }

               var13.add(-1);
            }

            var5 = (Integer)var1.get(var11);
         }

         var13.set(var5 >> 8, var3);
      }

      AhoCorasicSearchProcessorFactory.Context var14 = new AhoCorasicSearchProcessorFactory.Context();
      var14.jumpTable = new int[var1.size()];

      for (int var15 = 0; var15 < var1.size(); var15++) {
         var14.jumpTable[var15] = (Integer)var1.get(var15);
      }

      var14.matchForNeedleId = new int[var13.size()];

      for (int var16 = 0; var16 < var13.size(); var16++) {
         var14.matchForNeedleId[var16] = (Integer)var13.get(var16);
      }

      return var14;
   }

   private void linkSuffixes() {
      ArrayDeque var1 = new ArrayDeque();
      var1.add(0);
      int[] var2 = new int[this.matchForNeedleId.length];
      Arrays.fill(var2, -1);

      while (!var1.isEmpty()) {
         int var3 = (Integer)var1.remove();
         int var4 = var3 >> 8;
         int var5 = var2[var4] == -1 ? 0 : var2[var4];
         if (this.matchForNeedleId[var4] == -1) {
            this.matchForNeedleId[var4] = this.matchForNeedleId[var5 >> 8];
         }

         for (int var6 = 0; var6 < 256; var6++) {
            int var7 = var3 | var6;
            int var8 = var5 | var6;
            int var9 = this.jumpTable[var7];
            int var10 = this.jumpTable[var8];
            if (var9 != -1) {
               var2[var9 >> 8] = var3 > 0 && var10 != -1 ? var10 : 0;
               var1.add(var9);
            } else {
               this.jumpTable[var7] = var10 != -1 ? var10 : 0;
            }
         }
      }
   }

   public AhoCorasicSearchProcessorFactory.Processor newSearchProcessor() {
      return new AhoCorasicSearchProcessorFactory.Processor(this.jumpTable, this.matchForNeedleId);
   }

   private static class Context {
      int[] jumpTable;
      int[] matchForNeedleId;

      private Context() {
      }
   }

   public static class Processor implements MultiSearchProcessor {
      private final int[] jumpTable;
      private final int[] matchForNeedleId;
      private long currentPosition;

      Processor(int[] var1, int[] var2) {
         this.jumpTable = var1;
         this.matchForNeedleId = var2;
      }

      @Override
      public boolean process(byte var1) {
         this.currentPosition = (long)PlatformDependent.getInt(this.jumpTable, this.currentPosition | (long)var1 & 255L);
         if (this.currentPosition < 0L) {
            this.currentPosition = -this.currentPosition;
            return false;
         } else {
            return true;
         }
      }

      @Override
      public int getFoundNeedleId() {
         return this.matchForNeedleId[(int)this.currentPosition >> 8];
      }

      @Override
      public void reset() {
         this.currentPosition = 0L;
      }
   }
}
