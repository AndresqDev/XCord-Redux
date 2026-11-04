package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableByteCollection implements TByteCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TByteCollection c;

   public TUnmodifiableByteCollection(TByteCollection var1) {
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
   public boolean contains(byte var1) {
      return this.c.contains(var1);
   }

   @Override
   public byte[] toArray() {
      return this.c.toArray();
   }

   @Override
   public byte[] toArray(byte[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public byte getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TByteProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TByteIterator iterator() {
      return new TByteIterator() {
         TByteIterator i = TUnmodifiableByteCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public byte next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TByteCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(byte[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TByteCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Byte> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(byte[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TByteCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(byte[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TByteCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(byte[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
