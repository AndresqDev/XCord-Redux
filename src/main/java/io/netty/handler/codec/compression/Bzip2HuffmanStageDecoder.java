package io.netty.handler.codec.compression;

final class Bzip2HuffmanStageDecoder {
   private final Bzip2BitReader reader;
   byte[] selectors;
   private final int[] minimumLengths;
   private final int[][] codeBases;
   private final int[][] codeLimits;
   private final int[][] codeSymbols;
   private int currentTable;
   private int groupIndex = -1;
   private int groupPosition = -1;
   final int totalTables;
   final int alphabetSize;
   final Bzip2MoveToFrontTable tableMTF = new Bzip2MoveToFrontTable();
   int currentSelector;
   final byte[][] tableCodeLengths;
   int currentGroup;
   int currentLength = -1;
   int currentAlpha;
   boolean modifyLength;

   Bzip2HuffmanStageDecoder(Bzip2BitReader var1, int var2, int var3) {
      this.reader = var1;
      this.totalTables = var2;
      this.alphabetSize = var3;
      this.minimumLengths = new int[var2];
      this.codeBases = new int[var2][25];
      this.codeLimits = new int[var2][24];
      this.codeSymbols = new int[var2][258];
      this.tableCodeLengths = new byte[var2][258];
   }

   void createHuffmanDecodingTables() {
      int var1 = this.alphabetSize;

      for (int var2 = 0; var2 < this.tableCodeLengths.length; var2++) {
         int[] var3 = this.codeBases[var2];
         int[] var4 = this.codeLimits[var2];
         int[] var5 = this.codeSymbols[var2];
         byte[] var6 = this.tableCodeLengths[var2];
         int var7 = 23;
         int var8 = 0;

         for (int var9 = 0; var9 < var1; var9++) {
            byte var10 = var6[var9];
            var8 = Math.max(var10, var8);
            var7 = Math.min(var10, var7);
         }

         this.minimumLengths[var2] = var7;

         for (int var12 = 0; var12 < var1; var12++) {
            var3[var6[var12] + 1]++;
         }

         int var13 = 1;

         for (int var16 = var3[0]; var13 < 25; var13++) {
            var16 += var3[var13];
            var3[var13] = var16;
         }

         var13 = var7;

         for (int var17 = 0; var13 <= var8; var13++) {
            int var18 = var17 + (var3[var13 + 1] - var3[var13]);
            var3[var13] = var17 - var3[var13];
            var4[var13] = var18 - 1;
            var17 = var18 << 1;
         }

         var13 = var7;

         for (int var19 = 0; var13 <= var8; var13++) {
            for (int var20 = 0; var20 < var1; var20++) {
               if (var6[var20] == var13) {
                  var5[var19++] = var20;
               }
            }
         }
      }

      this.currentTable = this.selectors[0];
   }

   int nextSymbol() {
      if (++this.groupPosition % 50 == 0) {
         this.groupIndex++;
         if (this.groupIndex == this.selectors.length) {
            throw new DecompressionException("error decoding block");
         }

         this.currentTable = this.selectors[this.groupIndex] & 255;
      }

      Bzip2BitReader var1 = this.reader;
      int var2 = this.currentTable;
      int[] var3 = this.codeLimits[var2];
      int[] var4 = this.codeBases[var2];
      int[] var5 = this.codeSymbols[var2];
      int var6 = this.minimumLengths[var2];

      for (int var7 = var1.readBits(var6); var6 <= 23; var6++) {
         if (var7 <= var3[var6]) {
            return var5[var7 - var4[var6]];
         }

         var7 = var7 << 1 | var1.readBits(1);
      }

      throw new DecompressionException("a valid code was not recognised");
   }
}
