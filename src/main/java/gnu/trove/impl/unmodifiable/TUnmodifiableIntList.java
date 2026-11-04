package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TIntFunction;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableIntList extends TUnmodifiableIntCollection implements TIntList {
   static final long serialVersionUID = -283967356065247728L;
   final TIntList list;

   public TUnmodifiableIntList(TIntList var1) {
      super(var1);
      this.list = var1;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.list.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.list.hashCode();
   }

   @Override
   public int get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(int var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(int var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public int[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public int[] toArray(int[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TIntProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(int var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(int var1, int var2, int var3) {
      return this.list.binarySearch(var1, var2, var3);
   }

   @Override
   public int indexOf(int var1, int var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, int var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TIntList grep(TIntProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TIntList inverseGrep(TIntProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public int max() {
      return this.list.max();
   }

   @Override
   public int min() {
      return this.list.min();
   }

   @Override
   public int sum() {
      return this.list.sum();
   }

   @Override
   public TIntList subList(int var1, int var2) {
      return new TUnmodifiableIntList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessIntList(this.list) : this;
   }

   @Override
   public void add(int[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(int[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, int[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, int[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int set(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, int[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, int[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int replace(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TIntFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void reverse() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void reverse(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void shuffle(Random var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void sort() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void sort(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }
}
