package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableShortCollection implements TShortCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TShortCollection c;

   public TUnmodifiableShortCollection(TShortCollection var1) {
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
   public boolean contains(short var1) {
      return this.c.contains(var1);
   }

   @Override
   public short[] toArray() {
      return this.c.toArray();
   }

   @Override
   public short[] toArray(short[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public short getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TShortProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TShortIterator iterator() {
      return new TShortIterator() {
         TShortIterator i = TUnmodifiableShortCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public short next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TShortCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(short[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TShortCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Short> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(short[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TShortCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(short[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TShortCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(short[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
