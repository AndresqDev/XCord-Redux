package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectCharMap<K> implements TObjectCharMap<K>, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TObjectCharMap<K> m;
   final Object mutex;
   private transient Set<K> keySet = null;
   private transient TCharCollection values = null;

   public TSynchronizedObjectCharMap(TObjectCharMap<K> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedObjectCharMap(TObjectCharMap<K> var1, Object var2) {
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
   public boolean containsValue(char var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public char get(Object var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public char put(K var1, char var2) {
      synchronized (this.mutex) {
         return this.m.put((K)var1, var2);
      }
   }

   @Override
   public char remove(Object var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends Character> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TObjectCharMap<? extends K> var1) {
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
   public TObjectCharIterator<K> iterator() {
      return this.m.iterator();
   }

   @Override
   public char getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public char putIfAbsent(K var1, char var2) {
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
   public boolean forEachValue(TCharProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TObjectCharProcedure<? super K> var1) {
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
   public boolean retainEntries(TObjectCharProcedure<? super K> var1) {
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
   public boolean adjustValue(K var1, char var2) {
      synchronized (this.mutex) {
         return this.m.adjustValue((K)var1, var2);
      }
   }

   @Override
   public char adjustOrPutValue(K var1, char var2, char var3) {
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
