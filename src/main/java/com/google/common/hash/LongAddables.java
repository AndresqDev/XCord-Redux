package com.google.common.hash;

import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicLong;

@ElementTypesAreNonnullByDefault
final class LongAddables {
   private static final Supplier<LongAddable> SUPPLIER;

   public static LongAddable create() {
      return SUPPLIER.get();
   }

   static {
      Supplier var0;
      try {
         new LongAdder();
         var0 = new Supplier<LongAddable>() {
            public LongAddable get() {
               return new LongAdder();
            }
         };
      } catch (Throwable var2) {
         var0 = new Supplier<LongAddable>() {
            public LongAddable get() {
               return new LongAddables.PureJavaLongAddable();
            }
         };
      }

      SUPPLIER = var0;
   }

   private static final class PureJavaLongAddable extends AtomicLong implements LongAddable {
      private PureJavaLongAddable() {
      }

      @Override
      public void increment() {
         this.getAndIncrement();
      }

      @Override
      public void add(long var1) {
         this.getAndAdd(var1);
      }

      @Override
      public long sum() {
         return this.get();
      }
   }
}
