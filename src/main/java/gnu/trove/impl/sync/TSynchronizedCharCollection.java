package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedCharCollection implements TCharCollection, Serializable {
   private static final long serialVersionUID = 3053995032091335093L;
   final TCharCollection c;
   final Object mutex;

   public TSynchronizedCharCollection(TCharCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedCharCollection(TCharCollection var1, Object var2) {
      this.c = var1;
      this.mutex = var2;
   }

   @Override
   public int size() {
      synchronized (this.mutex) {
         return this.c.size();
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.mutex) {
         return this.c.isEmpty();
      }
   }

   @Override
   public boolean contains(char var1) {
      synchronized (this.mutex) {
         return this.c.contains(var1);
      }
   }

   @Override
   public char[] toArray() {
      synchronized (this.mutex) {
         return this.c.toArray();
      }
   }

   @Override
   public char[] toArray(char[] var1) {
      synchronized (this.mutex) {
         return this.c.toArray(var1);
      }
   }

   @Override
   public TCharIterator iterator() {
      return this.c.iterator();
   }

   @Override
   public boolean add(char var1) {
      synchronized (this.mutex) {
         return this.c.add(var1);
      }
   }

   @Override
   public boolean remove(char var1) {
      synchronized (this.mutex) {
         return this.c.remove(var1);
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(TCharCollection var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(char[] var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Character> var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(TCharCollection var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(char[] var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(TCharCollection var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(char[] var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(TCharCollection var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(char[] var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public char getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.c.forEach(var1);
      }
   }

   @Override
   public void clear() {
      synchronized (this.mutex) {
         this.c.clear();
      }
   }

   @Override
   public String toString() {
      synchronized (this.mutex) {
         return this.c.toString();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      synchronized (this.mutex) {
         var1.defaultWriteObject();
      }
   }
}
