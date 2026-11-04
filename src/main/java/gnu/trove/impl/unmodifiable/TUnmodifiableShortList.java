package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableShortList extends TUnmodifiableShortCollection implements TShortList {
   static final long serialVersionUID = -283967356065247728L;
   final TShortList list;

   public TUnmodifiableShortList(TShortList var1) {
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
   public short get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(short var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(short var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public short[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public short[] toArray(short[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public short[] toArray(short[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TShortProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(short var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(short var1, int var2, int var3) {
      return this.list.binarySearch(var1, var2, var3);
   }

   @Override
   public int indexOf(int var1, short var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, short var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TShortList grep(TShortProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TShortList inverseGrep(TShortProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public short max() {
      return this.list.max();
   }

   @Override
   public short min() {
      return this.list.min();
   }

   @Override
   public short sum() {
      return this.list.sum();
   }

   @Override
   public TShortList subList(int var1, int var2) {
      return new TUnmodifiableShortList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessShortList(this.list) : this;
   }

   @Override
   public void add(short[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(short[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, short[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, short[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short set(int var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, short[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, short[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short replace(int var1, short var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TShortFunction var1) {
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
   public void fill(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, short var3) {
      throw new UnsupportedOperationException();
   }
}
