package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.Serializable;
import java.util.Collection;

public class TUnmodifiableCharCollection implements TCharCollection, Serializable {
   private static final long serialVersionUID = 1820017752578914078L;
   final TCharCollection c;

   public TUnmodifiableCharCollection(TCharCollection var1) {
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
   public boolean contains(char var1) {
      return this.c.contains(var1);
   }

   @Override
   public char[] toArray() {
      return this.c.toArray();
   }

   @Override
   public char[] toArray(char[] var1) {
      return this.c.toArray(var1);
   }

   @Override
   public String toString() {
      return this.c.toString();
   }

   @Override
   public char getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TCharProcedure var1) {
      return this.c.forEach(var1);
   }

   @Override
   public TCharIterator iterator() {
      return new TCharIterator() {
         TCharIterator i = TUnmodifiableCharCollection.this.c.iterator();

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         @Override
         public char next() {
            return this.i.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public boolean add(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(TCharCollection var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean containsAll(char[] var1) {
      return this.c.containsAll(var1);
   }

   @Override
   public boolean addAll(TCharCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection<? extends Character> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(char[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(TCharCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(char[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(TCharCollection var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(char[] var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
}
