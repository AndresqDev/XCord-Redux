package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableLongObjectMap<V> implements TLongObjectMap<V>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TLongObjectMap<V> m;
   private transient TLongSet keySet = null;
   private transient Collection<V> values = null;

   public TUnmodifiableLongObjectMap(TLongObjectMap<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m = var1;
      }
   }

   @Override
   public int size() {
      return this.m.size();
   }

   @Override
   public boolean isEmpty() {
      return this.m.isEmpty();
   }

   @Override
   public boolean containsKey(long var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public V get(long var1) {
      return this.m.get(var1);
   }

   @Override
   public V put(long var1, V var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TLongObjectMap<? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Long, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TLongSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public long[] keys() {
      return this.m.keys();
   }

   @Override
   public long[] keys(long[] var1) {
      return this.m.keys(var1);
   }

   @Override
   public Collection<V> valueCollection() {
      if (this.values == null) {
         this.values = Collections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public Object[] values() {
      return this.m.values();
   }

   @Override
   public V[] values(V[] var1) {
      return this.m.values((V[])var1);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.m.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.m.hashCode();
   }

   @Override
   public String toString() {
      return this.m.toString();
   }

   @Override
   public long getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public boolean forEachKey(TLongProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TLongObjectProcedure<? super V> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TLongObjectIterator<V> iterator() {
      return new TLongObjectIterator<V>() {
         TLongObjectIterator<V> iter = TUnmodifiableLongObjectMap.this.m.iterator();

         @Override
         public long key() {
            return this.iter.key();
         }

         @Override
         public V value() {
            return this.iter.value();
         }

         @Override
         public void advance() {
            this.iter.advance();
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         @Override
         public V setValue(V var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public V putIfAbsent(long var1, V var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TLongObjectProcedure<? super V> var1) {
      throw new UnsupportedOperationException();
   }
}
