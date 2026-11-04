package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableFloatCollection implements TFloatCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TFloatCollection c;

   public TUnmodifiableFloatCollection(TFloatCollection var1) {
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
   public boolean contains(float var1) {
      return this.c.contains(var1);
   }

   @Override
   public float[] toArray() {
      return this.c.toArray();
   }

   @Override
   public float[] toArray(float[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public float getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TFloatProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TFloatIterator iterator() {
      return new TFloatIterator() {
         TFloatIterator i = TUnmodifiableFloatCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public float next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TFloatCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(float[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TFloatCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Float> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(float[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TFloatCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(float[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TFloatCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(float[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
