package io.netty.buffer;

import java.util.Arrays;

final class LongPriorityQueue {
   public static final int NO_VALUE = -1;
   private long[] array = new long[9];
   private int size;

   public void offer(long var1) {
      if (var1 == -1L) {
         throw new IllegalArgumentException("The NO_VALUE (-1) cannot be added to the queue.");
      } else {
         this.size++;
         if (this.size == this.array.length) {
            this.array = Arrays.copyOf(this.array, 1 + (this.array.length - 1) * 2);
         }

         this.array[this.size] = var1;
         this.lift(this.size);
      }
   }

   public void remove(long var1) {
      for (int var3 = 1; var3 <= this.size; var3++) {
         if (this.array[var3] == var1) {
            this.array[var3] = this.array[this.size--];
            this.lift(var3);
            this.sink(var3);
            return;
         }
      }
   }

   public long peek() {
      return this.size == 0 ? -1L : this.array[1];
   }

   public long poll() {
      if (this.size == 0) {
         return -1L;
      } else {
         long var1 = this.array[1];
         this.array[1] = this.array[this.size];
         this.array[this.size] = 0L;
         this.size--;
         this.sink(1);
         return var1;
      }
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   private void lift(int var1) {
      int var2;
      while (var1 > 1 && this.subord(var2 = var1 >> 1, var1)) {
         this.swap(var1, var2);
         var1 = var2;
      }
   }

   private void sink(int var1) {
      int var2;
      while ((var2 = var1 << 1) <= this.size) {
         if (var2 < this.size && this.subord(var2, var2 + 1)) {
            var2++;
         }

         if (!this.subord(var1, var2)) {
            break;
         }

         this.swap(var1, var2);
         var1 = var2;
      }
   }

   private boolean subord(int var1, int var2) {
      return this.array[var1] > this.array[var2];
   }

   private void swap(int var1, int var2) {
      long var3 = this.array[var1];
      this.array[var1] = this.array[var2];
      this.array[var2] = var3;
   }
}
