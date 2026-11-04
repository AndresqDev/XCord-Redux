package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import sun.misc.Unsafe;

abstract class RingBufferFields<E> extends RingBufferPad {
   private static final int BUFFER_PAD;
   private static final long REF_ARRAY_BASE;
   private static final int REF_ELEMENT_SHIFT;
   private static final Unsafe UNSAFE = Util.getUnsafe();
   private final long indexMask;
   private final Object[] entries;
   protected final int bufferSize;
   protected final Sequencer sequencer;

   RingBufferFields(EventFactory<E> var1, Sequencer var2) {
      this.sequencer = var2;
      this.bufferSize = var2.getBufferSize();
      if (this.bufferSize < 1) {
         throw new IllegalArgumentException("bufferSize must not be less than 1");
      } else if (Integer.bitCount(this.bufferSize) != 1) {
         throw new IllegalArgumentException("bufferSize must be a power of 2");
      } else {
         this.indexMask = (long)(this.bufferSize - 1);
         this.entries = new Object[var2.getBufferSize() + 2 * BUFFER_PAD];
         this.fill(var1);
      }
   }

   private void fill(EventFactory<E> var1) {
      for (int var2 = 0; var2 < this.bufferSize; var2++) {
         this.entries[BUFFER_PAD + var2] = var1.newInstance();
      }
   }

   protected final E elementAt(long var1) {
      return (E)UNSAFE.getObject(this.entries, REF_ARRAY_BASE + ((var1 & this.indexMask) << REF_ELEMENT_SHIFT));
   }

   static {
      int var0 = UNSAFE.arrayIndexScale(Object[].class);
      if (4 == var0) {
         REF_ELEMENT_SHIFT = 2;
      } else {
         if (8 != var0) {
            throw new IllegalStateException("Unknown pointer size");
         }

         REF_ELEMENT_SHIFT = 3;
      }

      BUFFER_PAD = 128 / var0;
      REF_ARRAY_BASE = (long)(UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT));
   }
}
