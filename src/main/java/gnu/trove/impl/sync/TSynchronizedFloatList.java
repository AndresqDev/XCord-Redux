package gnu.trove.impl.sync;

import gnu.trove.function.TFloatFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedFloatList extends TSynchronizedFloatCollection implements TFloatList {
   static final long serialVersionUID = -7754090372962971524L;
   final TFloatList list;

   public TSynchronizedFloatList(TFloatList var1) {
      super(var1);
      this.list = var1;
   }

   public TSynchronizedFloatList(TFloatList var1, Object var2) {
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
   public float get(int var1) {
      synchronized (this.mutex) {
         return this.list.get(var1);
      }
   }

   @Override
   public float set(int var1, float var2) {
      synchronized (this.mutex) {
         return this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, float[] var2) {
      synchronized (this.mutex) {
         this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, float[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.set(var1, var2, var3, var4);
      }
   }

   @Override
   public float replace(int var1, float var2) {
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
   public float removeAt(int var1) {
      synchronized (this.mutex) {
         return this.list.removeAt(var1);
      }
   }

   @Override
   public void add(float[] var1) {
      synchronized (this.mutex) {
         this.list.add(var1);
      }
   }

   @Override
   public void add(float[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         this.list.add(var1, var2, var3);
      }
   }

   @Override
   public void insert(int var1, float var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, float[] var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, float[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(float var1) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1);
      }
   }

   @Override
   public int lastIndexOf(float var1) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1);
      }
   }

   @Override
   public TFloatList subList(int var1, int var2) {
      synchronized (this.mutex) {
         return new TSynchronizedFloatList(this.list.subList(var1, var2), this.mutex);
      }
   }

   @Override
   public float[] toArray(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2);
      }
   }

   @Override
   public float[] toArray(float[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3);
      }
   }

   @Override
   public float[] toArray(float[] var1, int var2, int var3, int var4) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(int var1, float var2) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1, var2);
      }
   }

   @Override
   public int lastIndexOf(int var1, float var2) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1, var2);
      }
   }

   @Override
   public void fill(float var1) {
      synchronized (this.mutex) {
         this.list.fill(var1);
      }
   }

   @Override
   public void fill(int var1, int var2, float var3) {
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
   public int binarySearch(float var1) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1);
      }
   }

   @Override
   public int binarySearch(float var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1, var2, var3);
      }
   }

   @Override
   public TFloatList grep(TFloatProcedure var1) {
      synchronized (this.mutex) {
         return this.list.grep(var1);
      }
   }

   @Override
   public TFloatList inverseGrep(TFloatProcedure var1) {
      synchronized (this.mutex) {
         return this.list.inverseGrep(var1);
      }
   }

   @Override
   public float max() {
      synchronized (this.mutex) {
         return this.list.max();
      }
   }

   @Override
   public float min() {
      synchronized (this.mutex) {
         return this.list.min();
      }
   }

   @Override
   public float sum() {
      synchronized (this.mutex) {
         return this.list.sum();
      }
   }

   @Override
   public boolean forEachDescending(TFloatProcedure var1) {
      synchronized (this.mutex) {
         return this.list.forEachDescending(var1);
      }
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      synchronized (this.mutex) {
         this.list.transformValues(var1);
      }
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(this.list) : this;
   }
}
