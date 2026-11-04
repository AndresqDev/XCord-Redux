package gnu.trove.impl.sync;

import gnu.trove.function.TIntFunction;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedIntList extends TSynchronizedIntCollection implements TIntList {
   static final long serialVersionUID = -7754090372962971524L;
   final TIntList list;

   public TSynchronizedIntList(TIntList var1) {
      super(var1);
      this.list = var1;
   }

   public TSynchronizedIntList(TIntList var1, Object var2) {
      super(var1, var2);
      this.list = var1;
   }

   @Override
   public boolean equals(Object var1) {
      synchronized (this.mutex) {
         return this.list.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.mutex) {
         return this.list.hashCode();
      }
   }

   @Override
   public int get(int var1) {
      synchronized (this.mutex) {
         return this.list.get(var1);
      }
   }

   @Override
   public int set(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, int[] var2) {
      synchronized (this.mutex) {
         this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, int[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.set(var1, var2, var3, var4);
      }
   }

   @Override
   public int replace(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.replace(var1, var2);
      }
   }

   @Override
   public void remove(int var1, int var2) {
      synchronized (this.mutex) {
         this.list.remove(var1, var2);
      }
   }

   @Override
   public int removeAt(int var1) {
      synchronized (this.mutex) {
         return this.list.removeAt(var1);
      }
   }

   @Override
   public void add(int[] var1) {
      synchronized (this.mutex) {
         this.list.add(var1);
      }
   }

   @Override
   public void add(int[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         this.list.add(var1, var2, var3);
      }
   }

   @Override
   public void insert(int var1, int var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, int[] var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, int[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(int var1) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1);
      }
   }

   @Override
   public int lastIndexOf(int var1) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1);
      }
   }

   @Override
   public TIntList subList(int var1, int var2) {
      synchronized (this.mutex) {
         return new TSynchronizedIntList(this.list.subList(var1, var2), this.mutex);
      }
   }

   @Override
   public int[] toArray(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2);
      }
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3);
      }
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3, int var4) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1, var2);
      }
   }

   @Override
   public int lastIndexOf(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1, var2);
      }
   }

   @Override
   public void fill(int var1) {
      synchronized (this.mutex) {
         this.list.fill(var1);
      }
   }

   @Override
   public void fill(int var1, int var2, int var3) {
      synchronized (this.mutex) {
         this.list.fill(var1, var2, var3);
      }
   }

   @Override
   public void reverse() {
      synchronized (this.mutex) {
         this.list.reverse();
      }
   }

   @Override
   public void reverse(int var1, int var2) {
      synchronized (this.mutex) {
         this.list.reverse(var1, var2);
      }
   }

   @Override
   public void shuffle(Random var1) {
      synchronized (this.mutex) {
         this.list.shuffle(var1);
      }
   }

   @Override
   public void sort() {
      synchronized (this.mutex) {
         this.list.sort();
      }
   }

   @Override
   public void sort(int var1, int var2) {
      synchronized (this.mutex) {
         this.list.sort(var1, var2);
      }
   }

   @Override
   public int binarySearch(int var1) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1);
      }
   }

   @Override
   public int binarySearch(int var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1, var2, var3);
      }
   }

   @Override
   public TIntList grep(TIntProcedure var1) {
      synchronized (this.mutex) {
         return this.list.grep(var1);
      }
   }

   @Override
   public TIntList inverseGrep(TIntProcedure var1) {
      synchronized (this.mutex) {
         return this.list.inverseGrep(var1);
      }
   }

   @Override
   public int max() {
      synchronized (this.mutex) {
         return this.list.max();
      }
   }

   @Override
   public int min() {
      synchronized (this.mutex) {
         return this.list.min();
      }
   }

   @Override
   public int sum() {
      synchronized (this.mutex) {
         return this.list.sum();
      }
   }

   @Override
   public boolean forEachDescending(TIntProcedure var1) {
      synchronized (this.mutex) {
         return this.list.forEachDescending(var1);
      }
   }

   @Override
   public void transformValues(TIntFunction var1) {
      synchronized (this.mutex) {
         this.list.transformValues(var1);
      }
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(this.list) : this;
   }
}
