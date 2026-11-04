package gnu.trove.impl.sync;

import gnu.trove.function.TCharFunction;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedCharList extends TSynchronizedCharCollection implements TCharList {
   static final long serialVersionUID = -7754090372962971524L;
   final TCharList list;

   public TSynchronizedCharList(TCharList var1) {
      super(var1);
      this.list = var1;
   }

   public TSynchronizedCharList(TCharList var1, Object var2) {
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
   public char get(int var1) {
      synchronized (this.mutex) {
         return this.list.get(var1);
      }
   }

   @Override
   public char set(int var1, char var2) {
      synchronized (this.mutex) {
         return this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, char[] var2) {
      synchronized (this.mutex) {
         this.list.set(var1, var2);
      }
   }

   @Override
   public void set(int var1, char[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.set(var1, var2, var3, var4);
      }
   }

   @Override
   public char replace(int var1, char var2) {
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
   public char removeAt(int var1) {
      synchronized (this.mutex) {
         return this.list.removeAt(var1);
      }
   }

   @Override
   public void add(char[] var1) {
      synchronized (this.mutex) {
         this.list.add(var1);
      }
   }

   @Override
   public void add(char[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         this.list.add(var1, var2, var3);
      }
   }

   @Override
   public void insert(int var1, char var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, char[] var2) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2);
      }
   }

   @Override
   public void insert(int var1, char[] var2, int var3, int var4) {
      synchronized (this.mutex) {
         this.list.insert(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(char var1) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1);
      }
   }

   @Override
   public int lastIndexOf(char var1) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1);
      }
   }

   @Override
   public TCharList subList(int var1, int var2) {
      synchronized (this.mutex) {
         return new TSynchronizedCharList(this.list.subList(var1, var2), this.mutex);
      }
   }

   @Override
   public char[] toArray(int var1, int var2) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2);
      }
   }

   @Override
   public char[] toArray(char[] var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3);
      }
   }

   @Override
   public char[] toArray(char[] var1, int var2, int var3, int var4) {
      synchronized (this.mutex) {
         return this.list.toArray(var1, var2, var3, var4);
      }
   }

   @Override
   public int indexOf(int var1, char var2) {
      synchronized (this.mutex) {
         return this.list.indexOf(var1, var2);
      }
   }

   @Override
   public int lastIndexOf(int var1, char var2) {
      synchronized (this.mutex) {
         return this.list.lastIndexOf(var1, var2);
      }
   }

   @Override
   public void fill(char var1) {
      synchronized (this.mutex) {
         this.list.fill(var1);
      }
   }

   @Override
   public void fill(int var1, int var2, char var3) {
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
   public int binarySearch(char var1) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1);
      }
   }

   @Override
   public int binarySearch(char var1, int var2, int var3) {
      synchronized (this.mutex) {
         return this.list.binarySearch(var1, var2, var3);
      }
   }

   @Override
   public TCharList grep(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.list.grep(var1);
      }
   }

   @Override
   public TCharList inverseGrep(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.list.inverseGrep(var1);
      }
   }

   @Override
   public char max() {
      synchronized (this.mutex) {
         return this.list.max();
      }
   }

   @Override
   public char min() {
      synchronized (this.mutex) {
         return this.list.min();
      }
   }

   @Override
   public char sum() {
      synchronized (this.mutex) {
         return this.list.sum();
      }
   }

   @Override
   public boolean forEachDescending(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.list.forEachDescending(var1);
      }
   }

   @Override
   public void transformValues(TCharFunction var1) {
      synchronized (this.mutex) {
         this.list.transformValues(var1);
      }
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(this.list) : this;
   }
}
