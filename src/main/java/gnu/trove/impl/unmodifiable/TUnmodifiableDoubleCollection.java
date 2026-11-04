package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableDoubleCollection implements TDoubleCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TDoubleCollection c;

   public TUnmodifiableDoubleCollection(TDoubleCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
      }
   }

   @Override
   public int size() {
      return this.c.size();
   }

   @Override
   public boolean isEmpty() {
      return this.c.isEmpty();
   }

   @Override
   public boolean contains(double var1) {
      return this.c.contains(var1);
   }

   @Override
   public double[] toArray() {
      return this.c.toArray();
   }

   @Override
   public double[] toArray(double[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public double getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TDoubleProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TDoubleIterator iterator() {
      return new TDoubleIterator() {
         TDoubleIterator i = TUnmodifiableDoubleCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public double next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TDoubleCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(double[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TDoubleCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Double> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(double[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TDoubleCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(double[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TDoubleCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(double[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
