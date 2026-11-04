package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;

public class BitapSearchProcessorFactory extends AbstractSearchProcessorFactory {
   private final long[] bitMasks = new long[256];
   private final long successBit;

   BitapSearchProcessorFactory(byte[] var1) {
      if (var1.length > 64) {
         throw new IllegalArgumentException("Maximum supported search pattern length is 64, got " + var1.length);
      } else {
         long var2 = 1L;

         for (byte var7 : var1) {
            this.bitMasks[var7 & 255] = this.bitMasks[var7 & 255] | var2;
            var2 <<= 1;
         }

         this.successBit = 1L << var1.length - 1;
      }
   }

   public BitapSearchProcessorFactory.Processor newSearchProcessor() {
      return new BitapSearchProcessorFactory.Processor(this.bitMasks, this.successBit);
   }

   public static class Processor implements SearchProcessor {
      private final long[] bitMasks;
      private final long successBit;
      private long currentMask;

      Processor(long[] var1, long var2) {
         this.bitMasks = var1;
         this.successBit = var2;
      }

      @Override
      public boolean process(byte var1) {
         this.currentMask = (this.currentMask << 1 | 1L) & PlatformDependent.getLong(this.bitMasks, (long)var1 & 255L);
         return (this.currentMask & this.successBit) == 0L;
      }

      @Override
      public void reset() {
         this.currentMask = 0L;
      }
   }
}
