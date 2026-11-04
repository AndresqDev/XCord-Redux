package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectShortMap<K> implements TObjectShortMap<K>, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TObjectShortMap<K> m;
   final Object mutex;
   private transient Set<K> keySet = null;
   private transient TShortCollection values = null;

   public TSynchronizedObjectShortMap(TObjectShortMap<K> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedObjectShortMap(TObjectShortMap<K> var1, Object var2) {
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
   public boolean containsKey(Object var1) {
      synchronized (this.mutex) {
         return this.m.containsKey(var1);
      }
   }

   @Override
   public boolean containsValue(short var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public short get(Object var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public short put(K var1, short var2) {
      synchronized (this.mutex) {
         return this.m.put((K)var1, var2);
      }
   }

   @Override
   public short remove(Object var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends Short> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TObjectShortMap<? extends K> var1) {
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
   public Set<K> keySet() {
      synchronized (this.mutex) {
         if (this.keySet == null) {
            this.keySet = new SynchronizedSet<>(this.m.keySet(), this.mutex);
         }

         return this.keySet;
      }
   }

   @Override
   public Object[] keys() {
      synchronized (this.mutex) {
         return this.m.keys();
      }
   }

   @Override
   public K[] keys(K[] var1) {
      synchronized (this.mutex) {
         return this.m.keys((K[])var1);
      }
   }

   @Override
   public TShortCollection valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public short[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public short[] values(short[] var1) {
      synchronized (this.mutex) {
         return this.m.values(var1);
      }
   }

   @Override
   public TObjectShortIterator<K> iterator() {
      return this.m.iterator();
   }

   @Override
   public short getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public short putIfAbsent(K var1, short var2) {
      synchronized (this.mutex) {
         return this.m.putIfAbsent((K)var1, var2);
      }
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      synchronized (this.mutex) {
         return this.m.forEachKey(var1);
      }
   }

   @Override
   public boolean forEachValue(TShortProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TObjectShortProcedure<? super K> var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TShortFunction var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TObjectShortProcedure<? super K> var1) {
      synchronized (this.mutex) {
         return this.m.retainEntries(var1);
      }
   }

   @Override
   public boolean increment(K var1) {
      synchronized (this.mutex) {
         return this.m.increment((K)var1);
      }
   }

   @Override
   public boolean adjustValue(K var1, short var2) {
      synchronized (this.mutex) {
         return this.m.adjustValue((K)var1, var2);
      }
   }

   @Override
   public short adjustOrPutValue(K var1, short var2, short var3) {
      synchronized (this.mutex) {
         return this.m.adjustOrPutValue((K)var1, var2, var3);
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
