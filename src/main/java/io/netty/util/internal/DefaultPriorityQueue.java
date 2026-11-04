package io.netty.util.internal;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DefaultPriorityQueue<T extends PriorityQueueNode> extends AbstractQueue<T> implements PriorityQueue<T> {
   private static final PriorityQueueNode[] EMPTY_ARRAY = new PriorityQueueNode[0];
   private final Comparator<T> comparator;
   private T[] queue;
   private int size;

   public DefaultPriorityQueue(Comparator<T> var1, int var2) {
      this.comparator = ObjectUtil.checkNotNull(var1, "comparator");
      this.queue = (T[])(var2 != 0 ? new PriorityQueueNode[var2] : EMPTY_ARRAY);
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof PriorityQueueNode)) {
         return false;
      } else {
         PriorityQueueNode var2 = (PriorityQueueNode)var1;
         return this.contains(var2, var2.priorityQueueIndex(this));
      }
   }

   public boolean containsTyped(T var1) {
      return this.contains(var1, var1.priorityQueueIndex(this));
   }

   @Override
   public void clear() {
      for (int var1 = 0; var1 < this.size; var1++) {
         PriorityQueueNode var2 = this.queue[var1];
         if (var2 != null) {
            var2.priorityQueueIndex(this, -1);
            this.queue[var1] = null;
         }
      }

      this.size = 0;
   }

   @Override
   public void clearIgnoringIndexes() {
      this.size = 0;
   }

   public boolean offer(T var1) {
      if (var1.priorityQueueIndex(this) != -1) {
         throw new IllegalArgumentException("e.priorityQueueIndex(): " + var1.priorityQueueIndex(this) + " (expected: " + -1 + ") + e: " + var1);
      } else {
         if (this.size >= this.queue.length) {
            this.queue = (T[])Arrays.copyOf(this.queue, this.queue.length + (this.queue.length < 64 ? this.queue.length + 2 : this.queue.length >>> 1));
         }

         this.bubbleUp(this.size++, (T)var1);
         return true;
      }
   }

   public T poll() {
      if (this.size == 0) {
         return null;
      } else {
         PriorityQueueNode var1 = this.queue[0];
         var1.priorityQueueIndex(this, -1);
         PriorityQueueNode var2 = this.queue[--this.size];
         this.queue[this.size] = null;
         if (this.size != 0) {
            this.bubbleDown(0, (T)var2);
         }

         return (T)var1;
      }
   }

   public T peek() {
      return this.size == 0 ? null : this.queue[0];
   }

   @Override
   public boolean remove(Object var1) {
      PriorityQueueNode var2;
      try {
         var2 = (PriorityQueueNode)var1;
      } catch (ClassCastException var4) {
         return false;
      }

      return this.removeTyped((T)var2);
   }

   public boolean removeTyped(T var1) {
      int var2 = var1.priorityQueueIndex(this);
      if (!this.contains(var1, var2)) {
         return false;
      } else {
         var1.priorityQueueIndex(this, -1);
         if (--this.size != 0 && this.size != var2) {
            PriorityQueueNode var3 = this.queue[var2] = this.queue[this.size];
            this.queue[this.size] = null;
            if (this.comparator.compare((T)var1, (T)var3) < 0) {
               this.bubbleDown(var2, (T)var3);
            } else {
               this.bubbleUp(var2, (T)var3);
            }

            return true;
         } else {
            this.queue[var2] = null;
            return true;
         }
      }
   }

   public void priorityChanged(T var1) {
      int var2 = var1.priorityQueueIndex(this);
      if (this.contains(var1, var2)) {
         if (var2 == 0) {
            this.bubbleDown(var2, (T)var1);
         } else {
            int var3 = var2 - 1 >>> 1;
            PriorityQueueNode var4 = this.queue[var3];
            if (this.comparator.compare((T)var1, (T)var4) < 0) {
               this.bubbleUp(var2, (T)var1);
            } else {
               this.bubbleDown(var2, (T)var1);
            }
         }
      }
   }

   @Override
   public Object[] toArray() {
      return Arrays.copyOf(this.queue, this.size);
   }

   @Override
   public <X> X[] toArray(X[] var1) {
      if (var1.length < this.size) {
         return (X[])Arrays.copyOf(this.queue, this.size, (Class<? extends T[]>)var1.getClass());
      } else {
         System.arraycopy(this.queue, 0, var1, 0, this.size);
         if (var1.length > this.size) {
            var1[this.size] = null;
         }

         return (X[])var1;
      }
   }

   @Override
   public Iterator<T> iterator() {
      return new DefaultPriorityQueue.PriorityQueueIterator();
   }

   private boolean contains(PriorityQueueNode var1, int var2) {
      return var2 >= 0 && var2 < this.size && var1.equals(this.queue[var2]);
   }

   private void bubbleDown(int var1, T var2) {
      int var3 = this.size >>> 1;

      while (var1 < var3) {
         int var4 = (var1 << 1) + 1;
         PriorityQueueNode var5 = this.queue[var4];
         int var6 = var4 + 1;
         if (var6 < this.size && this.comparator.compare((T)var5, this.queue[var6]) > 0) {
            var4 = var6;
            var5 = this.queue[var6];
         }

         if (this.comparator.compare((T)var2, (T)var5) <= 0) {
            break;
         }

         this.queue[var1] = (T)var5;
         var5.priorityQueueIndex(this, var1);
         var1 = var4;
      }

      this.queue[var1] = (T)var2;
      var2.priorityQueueIndex(this, var1);
   }

   private void bubbleUp(int var1, T var2) {
      while (var1 > 0) {
         int var3 = var1 - 1 >>> 1;
         PriorityQueueNode var4 = this.queue[var3];
         if (this.comparator.compare((T)var2, (T)var4) < 0) {
            this.queue[var1] = (T)var4;
            var4.priorityQueueIndex(this, var1);
            var1 = var3;
            continue;
         }
         break;
      }

      this.queue[var1] = (T)var2;
      var2.priorityQueueIndex(this, var1);
   }

   private final class PriorityQueueIterator implements Iterator<T> {
      private int index;

      private PriorityQueueIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.index < DefaultPriorityQueue.this.size;
      }

      public T next() {
         if (this.index >= DefaultPriorityQueue.this.size) {
            throw new NoSuchElementException();
         } else {
            return DefaultPriorityQueue.this.queue[this.index++];
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }
   }
}
