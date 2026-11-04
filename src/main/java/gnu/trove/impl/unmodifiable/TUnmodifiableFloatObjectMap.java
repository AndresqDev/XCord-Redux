package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableFloatObjectMap<V> implements TFloatObjectMap<V>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TFloatObjectMap<V> m;
   private transient TFloatSet keySet = null;
   private transient Collection<V> values = null;

   public TUnmodifiableFloatObjectMap(TFloatObjectMap<V> var1) {
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
   public boolean containsKey(float var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public V get(float var1) {
      return this.m.get(var1);
   }

   @Override
   public V put(float var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TFloatObjectMap<? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends Float, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TFloatSet keySet() {
      if (this.keySet == null) {
         this.keySet = TCollections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public float[] keys() {
      return this.m.keys();
   }

   @Override
   public float[] keys(float[] var1) {
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
   public float getNoEntryKey() {
      return this.m.getNoEntryKey();
   }

   @Override
   public boolean forEachKey(TFloatProcedure var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TObjectProcedure<? super V> var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TFloatObjectProcedure<? super V> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TFloatObjectIterator<V> iterator() {
      return new TFloatObjectIterator<V>() {
         TFloatObjectIterator<V> iter = TUnmodifiableFloatObjectMap.this.m.iterator();

         @Override
         public float key() {
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
   public V putIfAbsent(float var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TObjectFunction<V, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TFloatObjectProcedure<? super V> var1) {
      throw new UnsupportedOperationException();
   }
}
