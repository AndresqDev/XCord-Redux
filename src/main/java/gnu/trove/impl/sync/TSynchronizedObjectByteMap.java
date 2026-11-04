package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectByteMap<K> implements TObjectByteMap<K>, Serializable {
   private static final long serialVersionUID = 1978198479659022715L;
   private final TObjectByteMap<K> m;
   final Object mutex;
   private transient Set<K> keySet = null;
   private transient TByteCollection values = null;

   public TSynchronizedObjectByteMap(TObjectByteMap<K> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
         this.mutex = this;
      }
   }

   public TSynchronizedObjectByteMap(TObjectByteMap<K> var1, Object var2) {
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
   public boolean containsValue(byte var1) {
      synchronized (this.mutex) {
         return this.m.containsValue(var1);
      }
   }

   @Override
   public byte get(Object var1) {
      synchronized (this.mutex) {
         return this.m.get(var1);
      }
   }

   @Override
   public byte put(K var1, byte var2) {
      synchronized (this.mutex) {
         return this.m.put((K)var1, var2);
      }
   }

   @Override
   public byte remove(Object var1) {
      synchronized (this.mutex) {
         return this.m.remove(var1);
      }
   }

   @Override
   public void putAll(Map<? extends K, ? extends Byte> var1) {
      synchronized (this.mutex) {
         this.m.putAll(var1);
      }
   }

   @Override
   public void putAll(TObjectByteMap<? extends K> var1) {
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
   public TByteCollection valueCollection() {
      synchronized (this.mutex) {
         if (this.values == null) {
            this.values = new TSynchronizedByteCollection(this.m.valueCollection(), this.mutex);
         }

         return this.values;
      }
   }

   @Override
   public byte[] values() {
      synchronized (this.mutex) {
         return this.m.values();
      }
   }

   @Override
   public byte[] values(byte[] var1) {
      synchronized (this.mutex) {
         return this.m.values(var1);
      }
   }

   @Override
   public TObjectByteIterator<K> iterator() {
      return this.m.iterator();
   }

   @Override
   public byte getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public byte putIfAbsent(K var1, byte var2) {
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
   public boolean forEachValue(TByteProcedure var1) {
      synchronized (this.mutex) {
         return this.m.forEachValue(var1);
      }
   }

   @Override
   public boolean forEachEntry(TObjectByteProcedure<? super K> var1) {
      synchronized (this.mutex) {
         return this.m.forEachEntry(var1);
      }
   }

   @Override
   public void transformValues(TByteFunction var1) {
      synchronized (this.mutex) {
         this.m.transformValues(var1);
      }
   }

   @Override
   public boolean retainEntries(TObjectByteProcedure<? super K> var1) {
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
   public boolean adjustValue(K var1, byte var2) {
      synchronized (this.mutex) {
         return this.m.adjustValue((K)var1, var2);
      }
   }

   @Override
   public byte adjustOrPutValue(K var1, byte var2, byte var3) {
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
