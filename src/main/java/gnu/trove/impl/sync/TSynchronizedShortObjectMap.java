package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedShortObjectMap<V> implements TShortObjectMap<V>, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TShortObjectMap<V> m;
   final Object mutex;
   private transient TShortSet keySet = null;
   private transient Collection<V> values = null;

   public TSynchronizedShortObjectMap(TShortObjectMap<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedShortObjectMap(TShortObjectMap<V> var1, Object var2) {
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
   public boolean containsKey(short var1) {
      synchronized (this.mutex) {
         return this.m.containsKey(var1);
      }
   }

   @Override
   public boolean containsValue(Object var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public V get(short var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public V put(short var1, V var2) {
      synchronized (this.mutex) {
         return this.m.put(var1, (V)var2);
      }
   }

   @Override
   public V remove(short var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends Short, ? extends V> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TShortObjectMap<? extends V> var1) {
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
   public TShortSet keySet() {
      synchronized (this.mutex) {
         if (this.keySet == null) {
            this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex);
         }

         return this.keySet;
      }
   }

   @Override
   public short[] keys() {
      synchronized (this.mutex) {
         return this.m.keys();
      }
   }

   @Override
   public short[] keys(short[] var1) {
      synchronized (this.mutex) {
         return this.m.keys(var1);
      }
   }

   @Override
   public Collection<V> valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new SynchronizedCollection<>(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public Object[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public V[] values(V[] var1) {
      synchronized (this.mutex) {
         return this.m.values((V[])var1);
      }
   }

   @Override
   public TShortObjectIterator<V> iterator() {
      return this.m.iterator();
   }

   @Override
   public short getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public V putIfAbsent(short var1, V var2) {
      synchronized (this.mutex) {
         return this.m.putIfAbsent(var1, (V)var2);
      }
   }

   @Override
   public boolean forEachKey(TShortProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachKey(var1);
      }
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TShortObjectProcedure<? super V> var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TShortObjectProcedure<? super V> var1) {
      synchronized (this.mutex) {
         return this.m.retainEntries(var1);
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
