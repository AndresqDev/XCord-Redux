package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleCharMap implements TDoubleCharMap, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TDoubleCharMap m;
   final Object mutex;
   private transient TDoubleSet keySet = null;
   private transient TCharCollection values = null;

   public TSynchronizedDoubleCharMap(TDoubleCharMap var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedDoubleCharMap(TDoubleCharMap var1, Object var2) {
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
   public boolean containsValue(char var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public char get(double var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public char put(double var1, char var3) {
      synchronized (this.mutex) {
         return this.m.put(var1, var3);
      }
   }

   @Override
   public char remove(double var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Character> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TDoubleCharMap var1) {
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
   public TCharCollection valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new TSynchronizedCharCollection(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public char[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public char[] values(char[] var1) {
      synchronized (this.mutex) {
         return this.m.values(var1);
      }
   }

   @Override
   public TDoubleCharIterator iterator() {
      return this.m.iterator();
   }

   @Override
   public double getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public char getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public char putIfAbsent(double var1, char var3) {
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
   public boolean forEachValue(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TDoubleCharProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TCharFunction var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TDoubleCharProcedure var1) {
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
   public boolean adjustValue(double var1, char var3) {
      synchronized (this.mutex) {
         return this.m.adjustValue(var1, var3);
      }
   }

   @Override
   public char adjustOrPutValue(double var1, char var3, char var4) {
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
