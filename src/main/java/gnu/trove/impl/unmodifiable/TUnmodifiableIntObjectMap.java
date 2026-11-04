package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableIntObjectMap<V> implements TIntObjectMap<V>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TIntObjectMap<V> m;
   private transient TIntSet keySet = null;
   private transient Collection<V> values = null;

   public TUnmodifiableIntObjectMap(TIntObjectMap<V> var1) {
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
   public boolean containsKey(int var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public V get(int var1) {
      return this.m.get(var1);
   }

   @Override
   public V put(int var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TIntObjectMap<? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TIntSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public int[] keys() {
      return this.m.keys();
   }

   @Override
   public int[] keys(int[] var1) {
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
   public int getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public boolean forEachKey(TIntProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TIntObjectProcedure<? super V> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TIntObjectIterator<V> iterator() {
      return new TIntObjectIterator<V>() {
         TIntObjectIterator<V> iter = TUnmodifiableIntObjectMap.this.m.iterator();

         @Override
         public int key() {
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
   public V putIfAbsent(int var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TIntObjectProcedure<? super V> var1) {
      throw new UnsupportedOperationException();
   }
}
