package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableDoubleList extends TUnmodifiableDoubleCollection implements TDoubleList {
   static final long serialVersionUID = -283967356065247728L;
   final TDoubleList list;

   public TUnmodifiableDoubleList(TDoubleList var1) {
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
   public double get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(double var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(double var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public double[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public double[] toArray(double[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public double[] toArray(double[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TDoubleProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(double var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(double var1, int var3, int var4) {
      return this.list.binarySearch(var1, var3, var4);
   }

   @Override
   public int indexOf(int var1, double var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, double var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TDoubleList grep(TDoubleProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TDoubleList inverseGrep(TDoubleProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public double max() {
      return this.list.max();
   }

   @Override
   public double min() {
      return this.list.min();
   }

   @Override
   public double sum() {
      return this.list.sum();
   }

   @Override
   public TDoubleList subList(int var1, int var2) {
      return new TUnmodifiableDoubleList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessDoubleList(this.list) : this;
   }

   @Override
   public void add(double[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(double[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, double[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, double[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double set(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, double[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, double[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double replace(int var1, double var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
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
   public void fill(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, double var3) {
      throw new UnsupportedOperationException();
   }
}
