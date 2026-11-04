package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteDoubleMap implements TByteDoubleMap, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TByteDoubleMap m;
   final Object mutex;
   private transient TByteSet keySet = null;
   private transient TDoubleCollection values = null;

   public TSynchronizedByteDoubleMap(TByteDoubleMap var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedByteDoubleMap(TByteDoubleMap var1, Object var2) {
      this.m = var1;
      this.mutex = var2;
   }

   @Override
   public int size() {
      synchronized (this.mutex) {
         return this.m.size();
      }
   }

   @Override
   public boolean isEmpty() {
      synchronized (this.mutex) {
         return this.m.isEmpty();
      }
   }

   @Override
   public boolean containsKey(byte var1) {
      synchronized (this.mutex) {
         return this.m.containsKey(var1);
      }
   }

   @Override
   public boolean containsValue(double var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public double get(byte var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public double put(byte var1, double var2) {
      synchronized (this.mutex) {
         return this.m.put(var1, var2);
      }
   }

   @Override
   public double remove(byte var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends Double> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TByteDoubleMap var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void clear() {
      synchronized (this.mutex) {
         this.m.clear();
      }
   }

   @Override
   public TByteSet keySet() {
      synchronized (this.mutex) {
         if (this.keySet == null) {
            this.keySet = new TSynchronizedByteSet(this.m.keySet(), this.mutex);
         }

         return this.keySet;
      }
   }

   @Override
   public byte[] keys() {
      synchronized (this.mutex) {
         return this.m.keys();
      }
   }

   @Override
   public byte[] keys(byte[] var1) {
      synchronized (this.mutex) {
         return this.m.keys(var1);
      }
   }

   @Override
   public TDoubleCollection valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new TSynchronizedDoubleCollection(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public double[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public double[] values(double[] var1) {
      synchronized (this.mutex) {
         return this.m.values(var1);
      }
   }

   @Override
   public TByteDoubleIterator iterator() {
      return this.m.iterator();
   }

   @Override
   public byte getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public double getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public double putIfAbsent(byte var1, double var2) {
      synchronized (this.mutex) {
         return this.m.putIfAbsent(var1, var2);
      }
   }

   @Override
   public boolean forEachKey(TByteProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachKey(var1);
      }
   }

   @Override
   public boolean forEachValue(TDoubleProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TByteDoubleProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TDoubleFunction var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TByteDoubleProcedure var1) {
      synchronized (this.mutex) {
         return this.m.retainEntries(var1);
      }
   }

   @Override
   public boolean increment(byte var1) {
      synchronized (this.mutex) {
         return this.m.increment(var1);
      }
   }

   @Override
   public boolean adjustValue(byte var1, double var2) {
      synchronized (this.mutex) {
         return this.m.adjustValue(var1, var2);
      }
   }

   @Override
   public double adjustOrPutValue(byte var1, double var2, double var4) {
      synchronized (this.mutex) {
         return this.m.adjustOrPutValue(var1, var2, var4);
      }
   }

   @Override
   public boolean equals(Object var1) {
      synchronized (this.mutex) {
         return this.m.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      synchronized (this.mutex) {
         return this.m.hashCode();
      }
   }

   @Override
   public String toString() {
      synchronized (this.mutex) {
         return this.m.toString();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      synchronized (this.mutex) {
         var1.defaultWriteObject();
      }
   }
}
