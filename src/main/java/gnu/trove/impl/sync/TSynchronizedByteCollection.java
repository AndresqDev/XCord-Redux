package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class TSynchronizedByteCollection implements TByteCollection, Serializable {
   private static final long serialVersionUID = 3053995032091335093L;
   final TByteCollection c;
   final Object mutex;

   public TSynchronizedByteCollection(TByteCollection var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedByteCollection(TByteCollection var1, Object var2) {
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
   public boolean contains(byte var1) {
      synchronized (this.mutex) {
         return this.c.contains(var1);
      }
   }

   @Override
   public byte[] toArray() {
      synchronized (this.mutex) {
         return this.c.toArray();
      }
   }

   @Override
   public byte[] toArray(byte[] var1) {
      synchronized (this.mutex) {
         return this.c.toArray(var1);
      }
   }

   @Override
   public TByteIterator iterator() {
      return this.c.iterator();
   }

   @Override
   public boolean add(byte var1) {
      synchronized (this.mutex) {
         return this.c.add(var1);
      }
   }

   @Override
   public boolean remove(byte var1) {
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
   public boolean containsAll(TByteCollection var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean containsAll(byte[] var1) {
      synchronized (this.mutex) {
         return this.c.containsAll(var1);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Byte> var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(TByteCollection var1) {
      synchronized (this.mutex) {
         return this.c.addAll(var1);
      }
   }

   @Override
   public boolean addAll(byte[] var1) {
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
   public boolean removeAll(TByteCollection var1) {
      synchronized (this.mutex) {
         return this.c.removeAll(var1);
      }
   }

   @Override
   public boolean removeAll(byte[] var1) {
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
   public boolean retainAll(TByteCollection var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public boolean retainAll(byte[] var1) {
      synchronized (this.mutex) {
         return this.c.retainAll(var1);
      }
   }

   @Override
   public byte getNoEntryValue() {
      return this.c.getNoEntryValue();
   }

   @Override
   public boolean forEach(TByteProcedure var1) {
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
