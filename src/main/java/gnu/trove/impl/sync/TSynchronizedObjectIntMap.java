package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectIntMap<K> implements TObjectIntMap<K>, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TObjectIntMap<K> m;
   final Object mutex;
   private transient Set<K> keySet = null;
   private transient TIntCollection values = null;

   public TSynchronizedObjectIntMap(TObjectIntMap<K> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedObjectIntMap(TObjectIntMap<K> var1, Object var2) {
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
   public boolean containsValue(int var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public int get(Object var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public int put(K var1, int var2) {
      synchronized (this.mutex) {
         return this.m.put((K)var1, var2);
      }
   }

   @Override
   public int remove(Object var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends Integer> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TObjectIntMap<? extends K> var1) {
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
   public TObjectIntIterator<K> iterator() {
      return this.m.iterator();
   }

   @Override
   public int getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public int putIfAbsent(K var1, int var2) {
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
   public boolean forEachValue(TIntProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TObjectIntProcedure<? super K> var1) {
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
   public boolean retainEntries(TObjectIntProcedure<? super K> var1) {
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
   public boolean adjustValue(K var1, int var2) {
      synchronized (this.mutex) {
         return this.m.adjustValue((K)var1, var2);
      }
   }

   @Override
   public int adjustOrPutValue(K var1, int var2, int var3) {
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
