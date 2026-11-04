package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TByteFunction;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableByteList extends TUnmodifiableByteCollection implements TByteList {
   static final long serialVersionUID = -283967356065247728L;
   final TByteList list;

   public TUnmodifiableByteList(TByteList var1) {
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
   public byte get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(byte var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(byte var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public byte[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public byte[] toArray(byte[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public byte[] toArray(byte[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TByteProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(byte var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(byte var1, int var2, int var3) {
      return this.list.binarySearch(var1, var2, var3);
   }

   @Override
   public int indexOf(int var1, byte var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, byte var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TByteList grep(TByteProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TByteList inverseGrep(TByteProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public byte max() {
      return this.list.max();
   }

   @Override
   public byte min() {
      return this.list.min();
   }

   @Override
   public byte sum() {
      return this.list.sum();
   }

   @Override
   public TByteList subList(int var1, int var2) {
      return new TUnmodifiableByteList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessByteList(this.list) : this;
   }

   @Override
   public void add(byte[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(byte[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, byte[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, byte[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte set(int var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, byte[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, byte[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte replace(int var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TByteFunction var1) {
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
   public void fill(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, byte var3) {
      throw new UnsupportedOperationException();
   }
}
