package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleIntMap implements TDoubleIntMap, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TDoubleIntMap m;
   final Object mutex;
   private transient TDoubleSet keySet = null;
   private transient TIntCollection values = null;

   public TSynchronizedDoubleIntMap(TDoubleIntMap var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedDoubleIntMap(TDoubleIntMap var1, Object var2) {
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
   public boolean containsKey(double var1) {
      synchronized (this.mutex) {
         return this.m.containsKey(var1);
      }
   }

   @Override
   public boolean containsValue(int var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public int get(double var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public int put(double var1, int var3) {
      synchronized (this.mutex) {
         return this.m.put(var1, var3);
      }
   }

   @Override
   public int remove(double var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Integer> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TDoubleIntMap var1) {
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
   public TDoubleSet keySet() {
      synchronized (this.mutex) {
         if (this.keySet == null) {
            this.keySet = new TSynchronizedDoubleSet(this.m.keySet(), this.mutex);
         }

         return this.keySet;
      }
   }

   @Override
   public double[] keys() {
      synchronized (this.mutex) {
         return this.m.keys();
      }
   }

   @Override
   public double[] keys(double[] var1) {
      synchronized (this.mutex) {
         return this.m.keys(var1);
      }
   }

   @Override
   public TIntCollection valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new TSynchronizedIntCollection(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public int[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public int[] values(int[] var1) {
      synchronized (this.mutex) {
         return this.m.values(var1);
      }
   }

   @Override
   public TDoubleIntIterator iterator() {
      return this.m.iterator();
   }

   @Override
   public double getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public int getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public int putIfAbsent(double var1, int var3) {
      synchronized (this.mutex) {
         return this.m.putIfAbsent(var1, var3);
      }
   }

   @Override
   public boolean forEachKey(TDoubleProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachKey(var1);
      }
   }

   @Override
   public boolean forEachValue(TIntProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TDoubleIntProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TIntFunction var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TDoubleIntProcedure var1) {
      synchronized (this.mutex) {
         return this.m.retainEntries(var1);
      }
   }

   @Override
   public boolean increment(double var1) {
      synchronized (this.mutex) {
         return this.m.increment(var1);
      }
   }

   @Override
   public boolean adjustValue(double var1, int var3) {
      synchronized (this.mutex) {
         return this.m.adjustValue(var1, var3);
      }
   }

   @Override
   public int adjustOrPutValue(double var1, int var3, int var4) {
      synchronized (this.mutex) {
         return this.m.adjustOrPutValue(var1, var3, var4);
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
