package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class KmpSearchProcessorFactory extends AbstractSearchProcessorFactory {
   private final int[] jumpTable;
   private final byte[] needle;

   KmpSearchProcessorFactory(byte[] var1) {
      this.needle = (byte[])var1.clone();
      this.jumpTable = new int[var1.length + 1];
      int var2 = 0;

      for (int var3 = 1; var3 < var1.length; var3++) {
         while (var2 > 0 && var1[var2] != var1[var3]) {
            var2 = this.jumpTable[var2];
         }

         if (var1[var2] == var1[var3]) {
            var2++;
         }

         this.jumpTable[var3 + 1] = var2;
      }
   }

   public KmpSearchProcessorFactory.Processor newSearchProcessor() {
      return new KmpSearchProcessorFactory.Processor(this.needle, this.jumpTable);
   }

   public static class Processor implements SearchProcessor {
      private final byte[] needle;
      private final int[] jumpTable;
      private long currentPosition;

      Processor(byte[] var1, int[] var2) {
         this.needle = var1;
         this.jumpTable = var2;
      }

      @Override
      public boolean process(byte var1) {
         while (this.currentPosition > 0L && PlatformDependent.getByte(this.needle, this.currentPosition) != var1) {
            this.currentPosition = (long)PlatformDependent.getInt(this.jumpTable, this.currentPosition);
         }

         if (PlatformDependent.getByte(this.needle, this.currentPosition) == var1) {
            this.currentPosition++;
         }

         if (this.currentPosition == (long)this.needle.length) {
            this.currentPosition = (long)PlatformDependent.getInt(this.jumpTable, this.currentPosition);
            return false;
         } else {
            return true;
         }
      }

      @Override
      public void reset() {
         this.currentPosition = 0L;
      }
   }
}
