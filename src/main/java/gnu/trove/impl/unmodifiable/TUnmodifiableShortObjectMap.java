package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableShortObjectMap<V> implements TShortObjectMap<V>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TShortObjectMap<V> m;
   private transient TShortSet keySet = null;
   private transient Collection<V> values = null;

   public TUnmodifiableShortObjectMap(TShortObjectMap<V> var1) {
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
   public boolean containsKey(short var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public V get(short var1) {
      return this.m.get(var1);
   }

   @Override
   public V put(short var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TShortObjectMap<? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Short, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TShortSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public short[] keys() {
      return this.m.keys();
   }

   @Override
   public short[] keys(short[] var1) {
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
   public short getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public boolean forEachKey(TShortProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TShortObjectProcedure<? super V> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TShortObjectIterator<V> iterator() {
      return new TShortObjectIterator<V>() {
         TShortObjectIterator<V> iter = TUnmodifiableShortObjectMap.this.m.iterator();

         @Override
         public short key() {
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
   public V putIfAbsent(short var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TShortObjectProcedure<? super V> var1) {
      throw new UnsupportedOperationException();
   }
}
