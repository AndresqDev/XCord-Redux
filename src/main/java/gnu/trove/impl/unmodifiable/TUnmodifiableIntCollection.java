package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableIntCollection implements TIntCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TIntCollection c;

   public TUnmodifiableIntCollection(TIntCollection var1) {
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
   public boolean contains(int var1) {
      return this.c.contains(var1);
   }

   @Override
   public int[] toArray() {
      return this.c.toArray();
   }

   @Override
   public int[] toArray(int[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public int getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TIntProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TIntIterator iterator() {
      return new TIntIterator() {
         TIntIterator i = TUnmodifiableIntCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public int next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TIntCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(int[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TIntCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Integer> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TIntCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(int[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TIntCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(int[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
