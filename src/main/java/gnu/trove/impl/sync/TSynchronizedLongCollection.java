package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedLongCollection implements TLongCollection, Serializable {
   private static final long serialVersionUID = 3053995032091335093L;
   final TLongCollection c;
   final Object mutex;

   public TSynchronizedLongCollection(TLongCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedLongCollection(TLongCollection var1, Object var2) {
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
   public boolean contains(long var1) {
      synchronized (this.mutex) {
         return this.c.contains(var1);
      }
   }

   @Override
   public long[] toArray() {
      synchronized (this.mutex) {
         return this.c.toArray();
      }
   }

   @Override
   public long[] toArray(long[] var1) {
      synchronized (this.mutex) {
         return this.c.toArray(var1);
      }
   }

   @Override
   public TLongIterator iterator() {
      return this.c.iterator();
   }

   @Override
   public boolean add(long var1) {
      synchronized (this.mutex) {
         return this.c.add(var1);
      }
   }

   @Override
   public boolean remove(long var1) {
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
   public boolean containsAll(TLongCollection var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(long[] var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Long> var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(TLongCollection var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(long[] var1) {
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
   public boolean removeAll(TLongCollection var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(long[] var1) {
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
   public boolean retainAll(TLongCollection var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(long[] var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public long getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TLongProcedure var1) {
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
