package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class LongAdder extends Striped64 implements LongAddable, Serializable {
   private static final long serialVersionUID = 7249069246863182397L;

   @Override
   final long fn(long var1, long var3) {
      return var1 + var3;
   }

   public LongAdder() {
   }

   @Override
   public void add(long var1) {
      Striped64.Cell[] var3 = this.cells;
      if (this.cells == null) {
         long var4 = this.base;
         if (this.casBase(this.base, var4 + var1)) {
            return;
         }
      }

      boolean var11 = true;
      int[] var8;
      Striped64.Cell var9;
      int var10;
      if ((var8 = threadHashCode.get()) != null && var3 != null && (var10 = var3.length) >= 1 && (var9 = var3[var10 - 1 & var8[0]]) != null) {
         long var6 = var9.value;
         if (var11 = var9.cas(var9.value, var6 + var1)) {
            return;
         }
      }

      this.retryUpdate(var1, var8, var11);
   }

   @Override
   public void increment() {
      this.add(1L);
   }

   public void decrement() {
      this.add(-1L);
   }

   @Override
   public long sum() {
      long var1 = this.base;
      Striped64.Cell[] var3 = this.cells;
      if (var3 != null) {
         for (Striped64.Cell var6 : var3) {
            if (var6 != null) {
               var1 += var6.value;
            }
         }
      }

      return var1;
   }

   public void reset() {
      this.internalReset(0L);
   }

   public long sumThenReset() {
      long var1 = this.base;
      Striped64.Cell[] var3 = this.cells;
      this.base = 0L;
      if (var3 != null) {
         for (Striped64.Cell var6 : var3) {
            if (var6 != null) {
               var1 += var6.value;
               var6.value = 0L;
            }
         }
      }

      return var1;
   }

   @Override
   public String toString() {
      return Long.toString(this.sum());
   }

   @Override
   public long longValue() {
      return this.sum();
   }

   @Override
   public int intValue() {
      return (int)this.sum();
   }

   @Override
   public float floatValue() {
      return (float)this.sum();
   }

   @Override
   public double doubleValue() {
      return (double)this.sum();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeLong(this.sum());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.busy = 0;
      this.cells = null;
      this.base = var1.readLong();
   }
}
