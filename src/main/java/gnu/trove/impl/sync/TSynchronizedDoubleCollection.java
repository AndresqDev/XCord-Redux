package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedDoubleCollection implements TDoubleCollection, Serializable {
   private static final long serialVersionUID = 3053995032091335093L;
   final TDoubleCollection c;
   final Object mutex;

   public TSynchronizedDoubleCollection(TDoubleCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedDoubleCollection(TDoubleCollection var1, Object var2) {
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
   public boolean contains(double var1) {
      synchronized (this.mutex) {
         return this.c.contains(var1);
      }
   }

   @Override
   public double[] toArray() {
      synchronized (this.mutex) {
         return this.c.toArray();
      }
   }

   @Override
   public double[] toArray(double[] var1) {
      synchronized (this.mutex) {
         return this.c.toArray(var1);
      }
   }

   @Override
   public TDoubleIterator iterator() {
      return this.c.iterator();
   }

   @Override
   public boolean add(double var1) {
      synchronized (this.mutex) {
         return this.c.add(var1);
      }
   }

   @Override
   public boolean remove(double var1) {
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
   public boolean containsAll(TDoubleCollection var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(double[] var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Double> var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(TDoubleCollection var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(double[] var1) {
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
   public boolean removeAll(TDoubleCollection var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(double[] var1) {
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
   public boolean retainAll(TDoubleCollection var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(double[] var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public double getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TDoubleProcedure var1) {
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
