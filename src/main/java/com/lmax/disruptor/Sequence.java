package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import sun.misc.Unsafe;

public class Sequence extends RhsPadding {
   static final long INITIAL_VALUE = -1L;
   private static final Unsafe UNSAFE = Util.getUnsafe();
   private static final long VALUE_OFFSET;

   public Sequence() {
      this(-1L);
   }

   public Sequence(long var1) {
      UNSAFE.putOrderedLong(this, VALUE_OFFSET, var1);
   }

   public long get() {
      return this.value;
   }

   public void set(long var1) {
      UNSAFE.putOrderedLong(this, VALUE_OFFSET, var1);
   }

   public void setVolatile(long var1) {
      UNSAFE.putLongVolatile(this, VALUE_OFFSET, var1);
   }

   public boolean compareAndSet(long var1, long var3) {
      return UNSAFE.compareAndSwapLong(this, VALUE_OFFSET, var1, var3);
   }

   public long incrementAndGet() {
      return this.addAndGet(1L);
   }

   public long addAndGet(long var1) {
      long var3;
      long var5;
      do {
         var3 = this.get();
         var5 = var3 + var1;
      } while (!this.compareAndSet(var3, var5));

      return var5;
   }

   @Override
   public String toString() {
      return Long.toString(this.get());
   }

   static {
      try {
         VALUE_OFFSET = UNSAFE.objectFieldOffset(Value.class.getDeclaredField("value"));
      } catch (Exception var1) {
         throw new RuntimeException(var1);
      }
   }
}
