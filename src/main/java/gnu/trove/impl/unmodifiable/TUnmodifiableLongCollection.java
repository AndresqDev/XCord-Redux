package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableLongCollection implements TLongCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TLongCollection c;

   public TUnmodifiableLongCollection(TLongCollection var1) {
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
   public boolean contains(long var1) {
      return this.c.contains(var1);
   }

   @Override
   public long[] toArray() {
      return this.c.toArray();
   }

   @Override
   public long[] toArray(long[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public long getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TLongProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TLongIterator iterator() {
      return new TLongIterator() {
         TLongIterator i = TUnmodifiableLongCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public long next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TLongCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(long[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TLongCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Long> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TLongCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(long[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TLongCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(long[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
