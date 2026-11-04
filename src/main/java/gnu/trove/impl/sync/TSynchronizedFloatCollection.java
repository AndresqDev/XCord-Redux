package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedFloatCollection implements TFloatCollection, Serializable {
   private static final long serialVersionUID = 3053995032091335093L;
   final TFloatCollection c;
   final Object mutex;

   public TSynchronizedFloatCollection(TFloatCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedFloatCollection(TFloatCollection var1, Object var2) {
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
   public boolean contains(float var1) {
      synchronized (this.mutex) {
         return this.c.contains(var1);
      }
   }

   @Override
   public float[] toArray() {
      synchronized (this.mutex) {
         return this.c.toArray();
      }
   }

   @Override
   public float[] toArray(float[] var1) {
      synchronized (this.mutex) {
         return this.c.toArray(var1);
      }
   }

   @Override
   public TFloatIterator iterator() {
      return this.c.iterator();
   }

   @Override
   public boolean add(float var1) {
      synchronized (this.mutex) {
         return this.c.add(var1);
      }
   }

   @Override
   public boolean remove(float var1) {
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
   public boolean containsAll(TFloatCollection var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(float[] var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Float> var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(TFloatCollection var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(float[] var1) {
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
   public boolean removeAll(TFloatCollection var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(float[] var1) {
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
   public boolean retainAll(TFloatCollection var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(float[] var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public float getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TFloatProcedure var1) {
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
