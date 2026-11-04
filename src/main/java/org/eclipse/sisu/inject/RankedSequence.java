package org.eclipse.sisu.inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

final class RankedSequence<T> extends AtomicReference<RankedSequence.Content> implements Iterable<T> {
   private static final long serialVersionUID = 1L;

   RankedSequence() {
   }

   RankedSequence(RankedSequence<T> var1) {
      if (var1 != null) {
         this.set(var1.get());
      }
   }

   public void insert(T var1, int var2) {
      RankedSequence.Content var3;
      RankedSequence.Content var4;
      do {
         var4 = (var3 = this.get()) != null ? var3.insert(var1, var2) : new RankedSequence.Content(var1, var2);
      } while (!this.compareAndSet(var3, var4));
   }

   public T peek() {
      RankedSequence.Content var1 = this.get();
      return (T)(var1 != null ? var1.objs[0] : null);
   }

   public boolean contains(Object var1) {
      RankedSequence.Content var2 = this.get();
      return var2 != null && var2.indexOf(var1) >= 0;
   }

   public boolean containsThis(Object var1) {
      RankedSequence.Content var2 = this.get();
      return var2 != null && var2.indexOfThis(var1) >= 0;
   }

   public T remove(Object var1) {
      RankedSequence.Content var2;
      int var4;
      while ((var2 = this.get()) != null && (var4 = var2.indexOf(var1)) >= 0) {
         RankedSequence.Content var3 = var2.remove(var4);
         if (this.compareAndSet(var2, var3)) {
            return (T)var2.objs[var4];
         }
      }

      return null;
   }

   public boolean removeThis(T var1) {
      RankedSequence.Content var2;
      int var4;
      while ((var2 = this.get()) != null && (var4 = var2.indexOfThis(var1)) >= 0) {
         RankedSequence.Content var3 = var2.remove(var4);
         if (this.compareAndSet(var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public Iterable<T> snapshot() {
      RankedSequence.Content var1 = this.get();
      return (Iterable<T>)(var1 != null ? Arrays.asList((T[])var1.objs) : Collections.EMPTY_SET);
   }

   public void clear() {
      this.set(null);
   }

   public boolean isEmpty() {
      return this.get() == null;
   }

   public int size() {
      RankedSequence.Content var1 = this.get();
      return var1 != null ? var1.objs.length : 0;
   }

   public RankedSequence<T>.Itr iterator() {
      return new RankedSequence.Itr();
   }

   static long rank2uid(int var0, int var1) {
      return (long)(~var0) << 32 | 4294967295L & (long)var1;
   }

   static int uid2rank(long var0) {
      return (int)(~var0 >>> 32);
   }

   static int safeBinarySearch(long[] var0, long var1) {
      if (var1 < var0[0]) {
         return 0;
      } else {
         int var3 = 0;
         int var4 = var0.length - 1;

         while (var3 < var4) {
            int var5 = var3 + var4 >>> 1;
            if (var1 <= var0[var5]) {
               var4 = var5;
            } else {
               var3 = var5 + 1;
            }
         }

         if (var3 == var0.length - 1 && var0[var3] < var1) {
            var3++;
         }

         return var3;
      }
   }

   static final class Content {
      final Object[] objs;
      final long[] uids;
      final int uniq;

      Content(Object var1, int var2) {
         this.objs = new Object[]{var1};
         this.uids = new long[]{RankedSequence.rank2uid(var2, 0)};
         this.uniq = 1;
      }

      Content(Object[] var1, long[] var2, int var3) {
         this.objs = var1;
         this.uids = var2;
         this.uniq = var3;
      }

      public int indexOf(Object var1) {
         if (var1 == null) {
            return this.indexOfThis(null);
         } else {
            for (int var2 = 0; var2 < this.objs.length; var2++) {
               if (var1.equals(this.objs[var2])) {
                  return var2;
               }
            }

            return -1;
         }
      }

      public int indexOfThis(Object var1) {
         for (int var2 = 0; var2 < this.objs.length; var2++) {
            if (var1 == this.objs[var2]) {
               return var2;
            }
         }

         return -1;
      }

      public RankedSequence.Content insert(Object var1, int var2) {
         int var3 = this.objs.length + 1;
         Object[] var4 = new Object[var3];
         long[] var5 = new long[var3];
         long var6 = RankedSequence.rank2uid(var2, this.uniq);
         int var8 = RankedSequence.safeBinarySearch(this.uids, var6);
         if (var8 > 0) {
            System.arraycopy(this.objs, 0, var4, 0, var8);
            System.arraycopy(this.uids, 0, var5, 0, var8);
         }

         var4[var8] = var1;
         var5[var8] = var6;
         int var9 = var8 + 1;
         int var10 = var3 - var9;
         if (var10 > 0) {
            System.arraycopy(this.objs, var8, var4, var9, var10);
            System.arraycopy(this.uids, var8, var5, var9, var10);
         }

         return new RankedSequence.Content(var4, var5, this.uniq + 1);
      }

      public RankedSequence.Content remove(int var1) {
         if (this.objs.length == 1) {
            return null;
         } else {
            int var2 = this.objs.length - 1;
            Object[] var3 = new Object[var2];
            long[] var4 = new long[var2];
            if (var1 > 0) {
               System.arraycopy(this.objs, 0, var3, 0, var1);
               System.arraycopy(this.uids, 0, var4, 0, var1);
            }

            int var5 = var1 + 1;
            int var6 = var2 - var1;
            if (var6 > 0) {
               System.arraycopy(this.objs, var5, var3, var1, var6);
               System.arraycopy(this.uids, var5, var4, var1, var6);
            }

            return new RankedSequence.Content(var3, var4, this.uniq);
         }
      }
   }

   final class Itr implements Iterator<T> {
      private RankedSequence.Content content;
      private T nextObj;
      private long nextUID = Long.MIN_VALUE;
      private int index = -1;

      @Override
      public boolean hasNext() {
         if (this.nextObj != null) {
            return true;
         } else {
            RankedSequence.Content var1 = RankedSequence.this.get();
            if (this.content != var1) {
               this.index = var1 != null ? RankedSequence.safeBinarySearch(var1.uids, this.nextUID) : -1;
               this.content = var1;
            }

            if (this.index >= 0 && this.index < this.content.objs.length) {
               this.nextObj = (T)this.content.objs[this.index];
               this.nextUID = this.content.uids[this.index];
               return true;
            } else {
               return false;
            }
         }
      }

      public boolean hasNext(int var1) {
         if (this.nextObj != null) {
            return RankedSequence.uid2rank(this.nextUID) >= var1;
         } else {
            RankedSequence.Content var2 = RankedSequence.this.get();
            if (this.content != var2) {
               this.index = var2 != null ? RankedSequence.safeBinarySearch(var2.uids, this.nextUID) : -1;
               this.content = var2;
            }

            return this.index >= 0 && this.index < this.content.uids.length ? RankedSequence.uid2rank(this.content.uids[this.index]) >= var1 : false;
         }
      }

      @Override
      public T next() {
         if (this.hasNext()) {
            this.nextUID++;
            this.index++;
            Object var1 = this.nextObj;
            this.nextObj = null;
            return (T)var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      public int rank() {
         return RankedSequence.uid2rank(this.nextUID);
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
