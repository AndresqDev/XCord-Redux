package gnu.trove.list.array;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

public class TIntArrayList implements TIntList, Externalizable {
   static final long serialVersionUID = 1L;
   protected static final int DEFAULT_CAPACITY = 10;
   protected int[] _data;
   protected int _pos;
   protected int no_entry_value;

   public TIntArrayList() {
      this(10, 0);
   }

   public TIntArrayList(int var1) {
      this(var1, 0);
   }

   public TIntArrayList(int var1, int var2) {
      this._data = new int[var1];
      this._pos = 0;
      this.no_entry_value = var2;
   }

   public TIntArrayList(TIntCollection var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public TIntArrayList(int[] var1) {
      this(var1.length);
      this.add(var1);
   }

   protected TIntArrayList(int[] var1, int var2, boolean var3) {
      if (!var3) {
         throw new IllegalStateException("Wrong call");
      } else if (var1 == null) {
         throw new IllegalArgumentException("values can not be null");
      } else {
         this._data = var1;
         this._pos = var1.length;
         this.no_entry_value = var2;
      }
   }

   public static TIntArrayList wrap(int[] var0) {
      return wrap(var0, 0);
   }

   public static TIntArrayList wrap(int[] var0, int var1) {
      return new TIntArrayList(var0, var1, true) {
         @Override
         public void ensureCapacity(int var1) {
            if (var1 > this._data.length) {
               throw new IllegalStateException("Can not grow ArrayList wrapped external array");
            }
         }
      };
   }

   @Override
   public int getNoEntryValue() {
      return this.no_entry_value;
   }

   public void ensureCapacity(int var1) {
      if (var1 > this._data.length) {
         int var2 = Math.max(this._data.length << 1, var1);
         int[] var3 = new int[var2];
         System.arraycopy(this._data, 0, var3, 0, this._data.length);
         this._data = var3;
      }
   }

   @Override
   public int size() {
      return this._pos;
   }

   @Override
   public boolean isEmpty() {
      return this._pos == 0;
   }

   public void trimToSize() {
      if (this._data.length > this.size()) {
         int[] var1 = new int[this.size()];
         this.toArray(var1, 0, var1.length);
         this._data = var1;
      }
   }

   @Override
   public boolean add(int var1) {
      this.ensureCapacity(this._pos + 1);
      this._data[this._pos++] = var1;
      return true;
   }

   @Override
   public void add(int[] var1) {
      this.add(var1, 0, var1.length);
   }

   @Override
   public void add(int[] var1, int var2, int var3) {
      this.ensureCapacity(this._pos + var3);
      System.arraycopy(var1, var2, this._data, this._pos, var3);
      this._pos += var3;
   }

   @Override
   public void insert(int var1, int var2) {
      if (var1 == this._pos) {
         this.add(var2);
      } else {
         this.ensureCapacity(this._pos + 1);
         System.arraycopy(this._data, var1, this._data, var1 + 1, this._pos - var1);
         this._data[var1] = var2;
         this._pos++;
      }
   }

   @Override
   public void insert(int var1, int[] var2) {
      this.insert(var1, var2, 0, var2.length);
   }

   @Override
   public void insert(int var1, int[] var2, int var3, int var4) {
      if (var1 == this._pos) {
         this.add(var2, var3, var4);
      } else {
         this.ensureCapacity(this._pos + var4);
         System.arraycopy(this._data, var1, this._data, var1 + var4, this._pos - var1);
         System.arraycopy(var2, var3, this._data, var1, var4);
         this._pos += var4;
      }
   }

   @Override
   public int get(int var1) {
      if (var1 >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this._data[var1];
      }
   }

   public int getQuick(int var1) {
      return this._data[var1];
   }

   @Override
   public int set(int var1, int var2) {
      if (var1 >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         int var3 = this._data[var1];
         this._data[var1] = var2;
         return var3;
      }
   }

   @Override
   public int replace(int var1, int var2) {
      if (var1 >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         int var3 = this._data[var1];
         this._data[var1] = var2;
         return var3;
      }
   }

   @Override
   public void set(int var1, int[] var2) {
      this.set(var1, var2, 0, var2.length);
   }

   @Override
   public void set(int var1, int[] var2, int var3, int var4) {
      if (var1 >= 0 && var1 + var4 <= this._pos) {
         System.arraycopy(var2, var3, this._data, var1, var4);
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public void setQuick(int var1, int var2) {
      this._data[var1] = var2;
   }

   @Override
   public void clear() {
      this.clearQuick();
      Arrays.fill(this._data, this.no_entry_value);
   }

   public void clearQuick() {
      this._pos = 0;
   }

   @Override
   public boolean remove(int var1) {
      for (int var2 = 0; var2 < this._pos; var2++) {
         if (var1 == this._data[var2]) {
            this.remove(var2, 1);
            return true;
         }
      }

      return false;
   }

   @Override
   public int removeAt(int var1) {
      int var2 = this.get(var1);
      this.remove(var1, 1);
      return var2;
   }

   @Override
   public void remove(int var1, int var2) {
      if (var2 != 0) {
         if (var1 >= 0 && var1 < this._pos) {
            if (var1 == 0) {
               System.arraycopy(this._data, var2, this._data, 0, this._pos - var2);
            } else if (this._pos - var2 != var1) {
               System.arraycopy(this._data, var1 + var2, this._data, var1, this._pos - (var1 + var2));
            }

            this._pos -= var2;
         } else {
            throw new ArrayIndexOutOfBoundsException(var1);
         }
      }
   }

   @Override
   public TIntIterator iterator() {
      return new TIntArrayList.TIntArrayIterator(0);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      for (Object var3 : var1) {
         if (!(var3 instanceof Integer)) {
            return false;
         }

         int var4 = (Integer)var3;
         if (!this.contains(var4)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean containsAll(TIntCollection var1) {
      if (this == var1) {
         return true;
      } else {
         TIntIterator var2 = var1.iterator();

         while (var2.hasNext()) {
            int var3 = var2.next();
            if (!this.contains(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean containsAll(int[] var1) {
      int var2 = var1.length;

      while (var2-- > 0) {
         if (!this.contains(var1[var2])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean addAll(Collection<? extends Integer> var1) {
      boolean var2 = false;

      for (Integer var4 : var1) {
         int var5 = var4;
         if (this.add(var5)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean addAll(TIntCollection var1) {
      boolean var2 = false;
      TIntIterator var3 = var1.iterator();

      while (var3.hasNext()) {
         int var4 = var3.next();
         if (this.add(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean addAll(int[] var1) {
      boolean var2 = false;

      for (int var6 : var1) {
         if (this.add(var6)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      boolean var2 = false;
      TIntIterator var3 = this.iterator();

      while (var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(TIntCollection var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         TIntIterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   @Override
   public boolean retainAll(int[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      int[] var3 = this._data;
      int var4 = this._pos;

      while (var4-- > 0) {
         if (Arrays.binarySearch(var1, var3[var4]) < 0) {
            this.remove(var4, 1);
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      boolean var2 = false;

      for (Object var4 : var1) {
         if (var4 instanceof Integer) {
            int var5 = (Integer)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   @Override
   public boolean removeAll(TIntCollection var1) {
      if (var1 == this) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         TIntIterator var3 = var1.iterator();

         while (var3.hasNext()) {
            int var4 = var3.next();
            if (this.remove(var4)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   @Override
   public boolean removeAll(int[] var1) {
      boolean var2 = false;
      int var3 = var1.length;

      while (var3-- > 0) {
         if (this.remove(var1[var3])) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public void transformValues(TIntFunction var1) {
      for (int var2 = 0; var2 < this._pos; var2++) {
         this._data[var2] = var1.execute(this._data[var2]);
      }
   }

   @Override
   public void reverse() {
      this.reverse(0, this._pos);
   }

   @Override
   public void reverse(int var1, int var2) {
      if (var1 != var2) {
         if (var1 > var2) {
            throw new IllegalArgumentException("from cannot be greater than to");
         } else {
            int var3 = var1;

            for (int var4 = var2 - 1; var3 < var4; var4--) {
               this.swap(var3, var4);
               var3++;
            }
         }
      }
   }

   @Override
   public void shuffle(Random var1) {
      int var2 = this._pos;

      while (var2-- > 1) {
         this.swap(var2, var1.nextInt(var2));
      }
   }

   private void swap(int var1, int var2) {
      int var3 = this._data[var1];
      this._data[var1] = this._data[var2];
      this._data[var2] = var3;
   }

   @Override
   public TIntList subList(int var1, int var2) {
      if (var2 < var1) {
         throw new IllegalArgumentException("end index " + var2 + " greater than begin index " + var1);
      } else if (var1 < 0) {
         throw new IndexOutOfBoundsException("begin index can not be < 0");
      } else if (var2 > this._data.length) {
         throw new IndexOutOfBoundsException("end index < " + this._data.length);
      } else {
         TIntArrayList var3 = new TIntArrayList(var2 - var1);

         for (int var4 = var1; var4 < var2; var4++) {
            var3.add(this._data[var4]);
         }

         return var3;
      }
   }

   @Override
   public int[] toArray() {
      return this.toArray(0, this._pos);
   }

   @Override
   public int[] toArray(int var1, int var2) {
      int[] var3 = new int[var2];
      this.toArray(var3, var1, var2);
      return var3;
   }

   @Override
   public int[] toArray(int[] var1) {
      int var2 = var1.length;
      if (var1.length > this._pos) {
         var2 = this._pos;
         var1[var2] = this.no_entry_value;
      }

      this.toArray(var1, 0, var2);
      return var1;
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3) {
      if (var3 == 0) {
         return var1;
      } else if (var2 >= 0 && var2 < this._pos) {
         System.arraycopy(this._data, var2, var1, 0, var3);
         return var1;
      } else {
         throw new ArrayIndexOutOfBoundsException(var2);
      }
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return var1;
      } else if (var2 >= 0 && var2 < this._pos) {
         System.arraycopy(this._data, var2, var1, var3, var4);
         return var1;
      } else {
         throw new ArrayIndexOutOfBoundsException(var2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TIntList)) {
         return false;
      } else if (var1 instanceof TIntArrayList) {
         TIntArrayList var4 = (TIntArrayList)var1;
         if (var4.size() != this.size()) {
            return false;
         } else {
            int var5 = this._pos;

            while (var5-- > 0) {
               if (this._data[var5] != var4._data[var5]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         TIntList var2 = (TIntList)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            for (int var3 = 0; var3 < this._pos; var3++) {
               if (this._data[var3] != var2.get(var3)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;
      int var2 = this._pos;

      while (var2-- > 0) {
         var1 += HashFunctions.hash(this._data[var2]);
      }

      return var1;
   }

   @Override
   public boolean forEach(TIntProcedure var1) {
      for (int var2 = 0; var2 < this._pos; var2++) {
         if (!var1.execute(this._data[var2])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean forEachDescending(TIntProcedure var1) {
      int var2 = this._pos;

      while (var2-- > 0) {
         if (!var1.execute(this._data[var2])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void sort() {
      Arrays.sort(this._data, 0, this._pos);
   }

   @Override
   public void sort(int var1, int var2) {
      Arrays.sort(this._data, var1, var2);
   }

   @Override
   public void fill(int var1) {
      Arrays.fill(this._data, 0, this._pos, var1);
   }

   @Override
   public void fill(int var1, int var2, int var3) {
      if (var2 > this._pos) {
         this.ensureCapacity(var2);
         this._pos = var2;
      }

      Arrays.fill(this._data, var1, var2, var3);
   }

   @Override
   public int binarySearch(int var1) {
      return this.binarySearch(var1, 0, this._pos);
   }

   @Override
   public int binarySearch(int var1, int var2, int var3) {
      if (var2 < 0) {
         throw new ArrayIndexOutOfBoundsException(var2);
      } else if (var3 > this._pos) {
         throw new ArrayIndexOutOfBoundsException(var3);
      } else {
         int var4 = var2;
         int var5 = var3 - 1;

         while (var4 <= var5) {
            int var6 = var4 + var5 >>> 1;
            int var7 = this._data[var6];
            if (var7 < var1) {
               var4 = var6 + 1;
            } else {
               if (var7 <= var1) {
                  return var6;
               }

               var5 = var6 - 1;
            }
         }

         return -(var4 + 1);
      }
   }

   @Override
   public int indexOf(int var1) {
      return this.indexOf(0, var1);
   }

   @Override
   public int indexOf(int var1, int var2) {
      for (int var3 = var1; var3 < this._pos; var3++) {
         if (this._data[var3] == var2) {
            return var3;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(int var1) {
      return this.lastIndexOf(this._pos, var1);
   }

   @Override
   public int lastIndexOf(int var1, int var2) {
      int var3 = var1;

      while (var3-- > 0) {
         if (this._data[var3] == var2) {
            return var3;
         }
      }

      return -1;
   }

   @Override
   public boolean contains(int var1) {
      return this.lastIndexOf(var1) >= 0;
   }

   @Override
   public TIntList grep(TIntProcedure var1) {
      TIntArrayList var2 = new TIntArrayList();

      for (int var3 = 0; var3 < this._pos; var3++) {
         if (var1.execute(this._data[var3])) {
            var2.add(this._data[var3]);
         }
      }

      return var2;
   }

   @Override
   public TIntList inverseGrep(TIntProcedure var1) {
      TIntArrayList var2 = new TIntArrayList();

      for (int var3 = 0; var3 < this._pos; var3++) {
         if (!var1.execute(this._data[var3])) {
            var2.add(this._data[var3]);
         }
      }

      return var2;
   }

   @Override
   public int max() {
      if (this.size() == 0) {
         throw new IllegalStateException("cannot find maximum of an empty list");
      } else {
         int var1 = Integer.MIN_VALUE;

         for (int var2 = 0; var2 < this._pos; var2++) {
            if (this._data[var2] > var1) {
               var1 = this._data[var2];
            }
         }

         return var1;
      }
   }

   @Override
   public int min() {
      if (this.size() == 0) {
         throw new IllegalStateException("cannot find minimum of an empty list");
      } else {
         int var1 = Integer.MAX_VALUE;

         for (int var2 = 0; var2 < this._pos; var2++) {
            if (this._data[var2] < var1) {
               var1 = this._data[var2];
            }
         }

         return var1;
      }
   }

   @Override
   public int sum() {
      int var1 = 0;

      for (int var2 = 0; var2 < this._pos; var2++) {
         var1 += this._data[var2];
      }

      return var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      int var2 = 0;

      for (int var3 = this._pos - 1; var2 < var3; var2++) {
         var1.append(this._data[var2]);
         var1.append(", ");
      }

      if (this.size() > 0) {
         var1.append(this._data[this._pos - 1]);
      }

      var1.append("}");
      return var1.toString();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeInt(this._pos);
      var1.writeInt(this.no_entry_value);
      int var2 = this._data.length;
      var1.writeInt(var2);

      for (int var3 = 0; var3 < var2; var3++) {
         var1.writeInt(this._data[var3]);
      }
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._pos = var1.readInt();
      this.no_entry_value = var1.readInt();
      int var2 = var1.readInt();
      this._data = new int[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this._data[var3] = var1.readInt();
      }
   }

   class TIntArrayIterator implements TIntIterator {
      private int cursor = 0;
      int lastRet = -1;

      TIntArrayIterator(int var2) {
         this.cursor = var2;
      }

      @Override
      public boolean hasNext() {
         return this.cursor < TIntArrayList.this.size();
      }

      @Override
      public int next() {
         try {
            int var1 = TIntArrayList.this.get(this.cursor);
            this.lastRet = this.cursor++;
            return var1;
         } catch (IndexOutOfBoundsException var2) {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         } else {
            try {
               TIntArrayList.this.remove(this.lastRet, 1);
               if (this.lastRet < this.cursor) {
                  this.cursor--;
               }

               this.lastRet = -1;
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }
   }
}
