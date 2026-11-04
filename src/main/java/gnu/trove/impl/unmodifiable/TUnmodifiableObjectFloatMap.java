package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectFloatMap<K> implements TObjectFloatMap<K>, Serializable {
   private static final long serialVersionUID = -1034234728574286014L;
   private final TObjectFloatMap<K> m;
   private transient Set<K> keySet = null;
   private transient TFloatCollection values = null;

   public TUnmodifiableObjectFloatMap(TObjectFloatMap<K> var1) {
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
   public boolean containsKey(Object var1) {
      return this.m.containsKey(var1);
   }

   @Override
   public boolean containsValue(float var1) {
      return this.m.containsValue(var1);
   }

   @Override
   public float get(Object var1) {
      return this.m.get(var1);
   }

   @Override
   public float put(K var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(TObjectFloatMap<? extends K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends Float> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = Collections.unmodifiableSet(this.m.keySet());
      }

      return this.keySet;
   }

   @Override
   public Object[] keys() {
      return this.m.keys();
   }

   @Override
   public K[] keys(K[] var1) {
      return this.m.keys((K[])var1);
   }

   @Override
   public TFloatCollection valueCollection() {
      if (this.values == null) {
         this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
      }

      return this.values;
   }

   @Override
   public float[] values() {
      return this.m.values();
   }

   @Override
   public float[] values(float[] var1) {
      return this.m.values(var1);
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
   public float getNoEntryValue() {
      return this.m.getNoEntryValue();
   }

   @Override
   public boolean forEachKey(TObjectProcedure<? super K> var1) {
      return this.m.forEachKey(var1);
   }

   @Override
   public boolean forEachValue(TFloatProcedure var1) {
      return this.m.forEachValue(var1);
   }

   @Override
   public boolean forEachEntry(TObjectFloatProcedure<? super K> var1) {
      return this.m.forEachEntry(var1);
   }

   @Override
   public TObjectFloatIterator<K> iterator() {
      return new TObjectFloatIterator<K>() {
         TObjectFloatIterator<K> iter = TUnmodifiableObjectFloatMap.this.m.iterator();

         @Override
         public K key() {
            return this.iter.key();
         }

         @Override
         public float value() {
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
         public float setValue(float var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public float putIfAbsent(K var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void transformValues(TFloatFunction var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainEntries(TObjectFloatProcedure<? super K> var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean increment(K var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean adjustValue(K var1, float var2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float adjustOrPutValue(K var1, float var2, float var3) {
      throw new UnsupportedOperationException();
   }
}
