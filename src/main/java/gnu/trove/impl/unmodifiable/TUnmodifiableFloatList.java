package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TFloatFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableFloatList extends TUnmodifiableFloatCollection implements TFloatList {
   static final long serialVersionUID = -283967356065247728L;
   final TFloatList list;

   public TUnmodifiableFloatList(TFloatList var1) {
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
   public float get(int var1) {
      return this.list.get(var1);
   }

   @Override
   public int indexOf(float var1) {
      return this.list.indexOf(var1);
   }

   @Override
   public int lastIndexOf(float var1) {
      return this.list.lastIndexOf(var1);
   }

   @Override
   public float[] toArray(int var1, int var2) {
      return this.list.toArray(var1, var2);
   }

   @Override
   public float[] toArray(float[] var1, int var2, int var3) {
      return this.list.toArray(var1, var2, var3);
   }

   @Override
   public float[] toArray(float[] var1, int var2, int var3, int var4) {
      return this.list.toArray(var1, var2, var3, var4);
   }

   @Override
   public boolean forEachDescending(TFloatProcedure var1) {
      return this.list.forEachDescending(var1);
   }

   @Override
   public int binarySearch(float var1) {
      return this.list.binarySearch(var1);
   }

   @Override
   public int binarySearch(float var1, int var2, int var3) {
      return this.list.binarySearch(var1, var2, var3);
   }

   @Override
   public int indexOf(int var1, float var2) {
      return this.list.indexOf(var1, var2);
   }

   @Override
   public int lastIndexOf(int var1, float var2) {
      return this.list.lastIndexOf(var1, var2);
   }

   @Override
   public TFloatList grep(TFloatProcedure var1) {
      return this.list.grep(var1);
   }

   @Override
   public TFloatList inverseGrep(TFloatProcedure var1) {
      return this.list.inverseGrep(var1);
   }

   @Override
   public float max() {
      return this.list.max();
   }

   @Override
   public float min() {
      return this.list.min();
   }

   @Override
   public float sum() {
      return this.list.sum();
   }

   @Override
   public TFloatList subList(int var1, int var2) {
      return new TUnmodifiableFloatList(this.list.subList(var1, var2));
   }

   private Object readResolve() {
      return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessFloatList(this.list) : this;
   }

   @Override
   public void add(float[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(float[] var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float removeAt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void remove(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, float[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void insert(int var1, float[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float set(int var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, float[] var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void set(int var1, float[] var2, int var3, int var4) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float replace(int var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
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
   public void fill(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void fill(int var1, int var2, float var3) {
      throw new UnsupportedOperationException();
   }
}
