package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableLongList extends TUnmodifiableLongCollection implements TLongList {
   static final long serialVersionUID = -283967356065247728L;
   final TLongList list;

   public TUnmodifiableLongList(TLongList var1) {
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
   public long get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(long var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(long var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public long[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public long[] toArray(long[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public long[] toArray(long[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TLongProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(long var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(long var1, int var3, int var4) {
      return this.list.binarySearch(var1, var3, var4);
   }

   @Override
   public int indexOf(int var1, long var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, long var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TLongList grep(TLongProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TLongList inverseGrep(TLongProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public long max() {
      return this.list.max();
   }

   @Override
   public long min() {
      return this.list.min();
   }

   @Override
   public long sum() {
      return this.list.sum();
   }

   @Override
   public TLongList subList(int var1, int var2) {
      return new TUnmodifiableLongList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessLongList(this.list) : this;
   }

   @Override
   public void add(long[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(long[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, long[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, long[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long set(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, long[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, long[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long replace(int var1, long var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TLongFunction var1) {
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
   public void fill(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, long var3) {
      throw new UnsupportedOperationException();
   }
}
