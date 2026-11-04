package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class PoolChunkList<T> implements PoolChunkListMetric {
   private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.<PoolChunkMetric>emptyList().iterator();
   private final PoolArena<T> arena;
   private final PoolChunkList<T> nextList;
   private final int minUsage;
   private final int maxUsage;
   private final int maxCapacity;
   private PoolChunk<T> head;
   private final int freeMinThreshold;
   private final int freeMaxThreshold;
   private PoolChunkList<T> prevList;

   PoolChunkList(PoolArena<T> var1, PoolChunkList<T> var2, int var3, int var4, int var5) {
      assert var3 <= var4;

      this.arena = var1;
      this.nextList = var2;
      this.minUsage = var3;
      this.maxUsage = var4;
      this.maxCapacity = calculateMaxCapacity(var3, var5);
      this.freeMinThreshold = var4 == 100 ? 0 : (int)((double)var5 * (100.0 - (double)var4 + 0.99999999) / 100.0);
      this.freeMaxThreshold = var3 == 100 ? 0 : (int)((double)var5 * (100.0 - (double)var3 + 0.99999999) / 100.0);
   }

   private static int calculateMaxCapacity(int var0, int var1) {
      var0 = minUsage0(var0);
      return var0 == 100 ? 0 : (int)((long)var1 * (100L - (long)var0) / 100L);
   }

   void prevList(PoolChunkList<T> var1) {
      assert this.prevList == null;

      this.prevList = var1;
   }

   boolean allocate(PooledByteBuf<T> var1, int var2, int var3, PoolThreadCache var4) {
      int var5 = this.arena.sizeIdx2size(var3);
      if (var5 > this.maxCapacity) {
         return false;
      } else {
         for (PoolChunk var6 = this.head; var6 != null; var6 = var6.next) {
            if (var6.allocate(var1, var2, var3, var4)) {
               if (var6.freeBytes <= this.freeMinThreshold) {
                  this.remove(var6);
                  this.nextList.add(var6);
               }

               return true;
            }
         }

         return false;
      }
   }

   boolean free(PoolChunk<T> var1, long var2, int var4, ByteBuffer var5) {
      var1.free(var2, var4, var5);
      if (var1.freeBytes > this.freeMaxThreshold) {
         this.remove(var1);
         return this.move0(var1);
      } else {
         return true;
      }
   }

   private boolean move(PoolChunk<T> var1) {
      assert var1.usage() < this.maxUsage;

      if (var1.freeBytes > this.freeMaxThreshold) {
         return this.move0(var1);
      } else {
         this.add0(var1);
         return true;
      }
   }

   private boolean move0(PoolChunk<T> var1) {
      if (this.prevList == null) {
         assert var1.usage() == 0;

         return false;
      } else {
         return this.prevList.move(var1);
      }
   }

   void add(PoolChunk<T> var1) {
      if (var1.freeBytes <= this.freeMinThreshold) {
         this.nextList.add(var1);
      } else {
         this.add0(var1);
      }
   }

   void add0(PoolChunk<T> var1) {
      var1.parent = this;
      if (this.head == null) {
         this.head = var1;
         var1.prev = null;
         var1.next = null;
      } else {
         var1.prev = null;
         var1.next = this.head;
         this.head.prev = var1;
         this.head = var1;
      }
   }

   private void remove(PoolChunk<T> var1) {
      if (var1 == this.head) {
         this.head = var1.next;
         if (this.head != null) {
            this.head.prev = null;
         }
      } else {
         PoolChunk var2 = var1.next;
         var1.prev.next = var2;
         if (var2 != null) {
            var2.prev = var1.prev;
         }
      }
   }

   @Override
   public int minUsage() {
      return minUsage0(this.minUsage);
   }

   @Override
   public int maxUsage() {
      return Math.min(this.maxUsage, 100);
   }

   private static int minUsage0(int var0) {
      return Math.max(1, var0);
   }

   @Override
   public Iterator<PoolChunkMetric> iterator() {
      this.arena.lock();

      Iterator var1;
      try {
         if (this.head != null) {
            ArrayList var6 = new ArrayList();
            PoolChunk var2 = this.head;

            do {
               var6.add(var2);
               var2 = var2.next;
            } while (var2 != null);

            return var6.iterator();
         }

         var1 = EMPTY_METRICS;
      } finally {
         this.arena.unlock();
      }

      return var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.arena.lock();

      try {
         if (this.head == null) {
            return "none";
         } else {
            PoolChunk var2 = this.head;

            while (true) {
               var1.append(var2);
               var2 = var2.next;
               if (var2 == null) {
                  return var1.toString();
               }

               var1.append(StringUtil.NEWLINE);
            }
         }
      } finally {
         this.arena.unlock();
      }
   }

   void destroy(PoolArena<T> var1) {
      for (PoolChunk var2 = this.head; var2 != null; var2 = var2.next) {
         var1.destroyChunk(var2);
      }

      this.head = null;
   }
}
